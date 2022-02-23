package com.reading.start.presentation.mvp.models;

import com.reading.start.AppCore;
import com.reading.start.BuildConfig;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.data.DataServerManager;
import com.reading.start.data.entity.DataChildrenItem;
import com.reading.start.data.entity.DataParentItem;
import com.reading.start.data.entity.DataSurveyItem;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.Parent;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.domain.rx.ProgressRxResult;
import com.reading.start.domain.rx.RxStatus;
import com.reading.start.general.TLog;
import com.reading.start.tests.ISO8601;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.TestsProvider;
import com.reading.start.tests.UploadResult;
import com.reading.start.utils.NetworkHelper;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.realm.Realm;

public class UploadSurveyModel extends BaseModel {
    private static final String TAG = UploadSurveyModel.class.getSimpleName();

    public UploadSurveyModel(Realm realm) {
        super(realm);
    }

    /**
     * Process upload of survey
     */
    public Observable<ProgressRxResult> getUploadObservable(final int childId, final int surveyId) {
        Observable<ProgressRxResult> result = Observable.create(subscriber -> {
            Realm realm = null;
            Boolean success = true;
            int progress = 0;
            Survey surveyResult = null;
            SurveyStatus surveyStatus = null;
            String errorMessage = AppCore.getInstance().getString(R.string.error_message_upload_general);

            try {
                if (BuildConfig.FAKE_UPLOAD || NetworkHelper.hasActiveInternetConnection()) {
                    realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                    if (realm != null && !realm.isClosed()) {
                        String token = AppCore.getInstance().getPreferences().getServerToken();

                        // update child info
                        Children childResult = realm.where(Children.class).equalTo(Children.FILED_ID, childId).findFirst();
                        DataChildrenItem childData = getDataChildrenItem(childResult);

                        if (childResult != null && childData != null) {
                            if (childResult.getIdServer() >= 0) {
                                // update on the server
                                //success = DataServerManager.getInstance().updateChildren(token, childData);
                                DataServerManager.getInstance().updateChildren(token, childData); // skip check to success update child/parent info
                            } else {
                                // add on the server
                                int id = DataServerManager.getInstance().addChildren(token, childData);

                                if (id >= 0) {
                                    success = true;
                                    realm.beginTransaction();
                                    childResult.setIdServer(id);
                                    realm.commitTransaction();
                                } else {
                                    success = false;
                                    errorMessage = AppCore.getInstance().getString(R.string.error_message_upload_add_child);
                                }
                            }
                        }

                        if (BuildConfig.FAKE_UPLOAD || success) {
                            // upload surveys
                            surveyResult = realm.where(Survey.class).equalTo(Survey.FILED_ID, surveyId).findFirst();
                            surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyId).findFirst();

                            if (childResult.getIdServer() != -1 && surveyResult != null) {
                                realm.beginTransaction();
                                surveyStatus.setUploadProgress(0);
                                realm.commitTransaction();

                                if (!subscriber.isDisposed()) {
                                    subscriber.onNext(new ProgressRxResult(RxStatus.Success, 0));
                                }

                                DataSurveyItem surveyData = getDataSurveyItem(surveyResult, childResult.getIdServer());

                                if (surveyData != null) {
                                    if (surveyResult.getIdServer() == -1) {
                                        int serverSurveyId = DataServerManager.getInstance().addSurvey(token, surveyData);

                                        if (serverSurveyId >= 0) {
                                            success = true;
                                            realm.beginTransaction();
                                            surveyResult.setIdServer(serverSurveyId);
                                            realm.commitTransaction();
                                        } else {
                                            success = false;
                                            errorMessage = AppCore.getInstance().getString(R.string.error_message_upload_add_survey);
                                        }
                                    } else {
                                        success = true;

                                        if (surveyResult.getIsCompleted()) {
                                            DataServerManager.getInstance().updateSurveyCompleted(token, surveyData);
                                        }
                                    }

                                    if (BuildConfig.FAKE_UPLOAD || success) {
                                        // upload survey results
                                        ArrayList<ITestModule> list = TestsProvider.getInstance(AppCore.getInstance()).getAllProcessingTestModules();

                                        if (list != null && list.size() > 0) {
                                            int increment = 100 / list.size();

                                            for (ITestModule module : list) {
                                                UploadResult uploadResult = module.uploadResult(token, surveyId, surveyResult.getIdServer());
                                                success = uploadResult.isSuccess();

                                                if (success) {
                                                    progress += increment;

                                                    realm.beginTransaction();
                                                    surveyStatus.setUploadProgress(progress);
                                                    realm.commitTransaction();

                                                    if (!subscriber.isDisposed()) {
                                                        subscriber.onNext(new ProgressRxResult(RxStatus.Success, progress));
                                                    }
                                                } else {
                                                    if (uploadResult.getMessage() != null && !uploadResult.getMessage().isEmpty()) {
                                                        errorMessage = uploadResult.getMessage();
                                                    } else {
                                                        errorMessage = String.format(AppCore.getInstance().getString(R.string.error_message_upload_test),
                                                                AppCore.getInstance().getString(module.getNameResource()));
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    success = false;
                                }
                            }
                        }
                    } else {
                        success = false;
                    }
                } else {
                    success = false;
                    errorMessage = AppCore.getInstance().getString(R.string.error_message_upload_internet_connection);
                }
            } catch (Exception e) {
                TLog.e(TAG, e);
                success = false;
            } finally {
                if (realm == null) {
                    realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
                }

                if (realm != null) {
                    surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyId).findFirst();

                    if (surveyStatus != null) {
                        if (success) {
                            realm.beginTransaction();
                            surveyStatus.setUploadProgress(100);
                            surveyStatus.setNeedUpload(false);
                            surveyStatus.setNeedDownload(false);
                            surveyStatus.setUploaded(true);
                            surveyStatus.setRemote(false);
                            surveyStatus.setInProgress(false);
                            surveyStatus.setTries(0);
                            realm.commitTransaction();

                            if (!subscriber.isDisposed()) {
                                subscriber.onNext(new ProgressRxResult(RxStatus.Success, 100));
                            }
                        } else {
                            realm.beginTransaction();
                            surveyStatus.setUploadProgress(0);
                            surveyStatus.setNeedDownload(false);
                            surveyStatus.setUploaded(false);
                            surveyStatus.setRemote(false);
                            surveyStatus.setInProgress(false);
                            surveyStatus.setTries(surveyStatus.getTries() + 1);

                            if (surveyStatus.getTries() > Constants.UPLOADING_COUNT_RETRY) {
                                surveyStatus.setTries(0);
                            }

                            surveyStatus.setNeedUpload(true);
                            realm.commitTransaction();
                        }
                    }
                }

                if (realm != null) {
                    realm.close();
                }
            }
            if (success) {
                if (!subscriber.isDisposed()) {
                    subscriber.onComplete();
                }
            } else {
                if (!subscriber.isDisposed()) {
                    subscriber.onError(new Exception(errorMessage));
                }
            }
        });

        return result;
    }

    private DataChildrenItem getDataChildrenItem(Children data) {
        DataChildrenItem result = null;

        if (data != null) {
            result = new DataChildrenItem();
            result.setId(data.getIdServer());
            result.setName(data.getName());
            result.setSurname(data.getSurname());
            result.setPatronymic(data.getPatronymic());
            result.setState(data.getState());
            result.setAddress(data.getAddress());
            result.setGender(data.getGender() != null && data.getGender().endsWith(Constants.FEMALE) ? "F" : "M");
            result.setBirthDate(data.getBirthDate() == 0 ? "" : ISO8601.fromDate(data.getBirthDate()));
            result.setLatitude(data.getLatitude());
            result.setLongitude(data.getLongitude());
            result.setDiagnosis(data.getDiagnosis());
            result.setDiagnosisClinic(data.getDiagnosisClinic());
            result.setDiagnosisDate(data.getDiagnosisDate() == 0 ? "" : ISO8601.fromTime(data.getDiagnosisDate()));
            result.setPhoto(addEncodedString(data.getPhoto()));
            result.setHand(data.getHand() == null ? "" : data.getHand().toLowerCase());

            ArrayList<DataParentItem> parents = new ArrayList<>();
            result.setParents(parents);

            if (data.getParentFirst() != null) {
                DataParentItem parentItem1 = getDataParentItem(data.getParentFirst());

                if (parentItem1 != null) {
                    parents.add(parentItem1);
                }
            }

            if (data.getParentSecond() != null) {
                DataParentItem parentItem2 = getDataParentItem(data.getParentSecond());

                if (parentItem2 != null) {
                    parents.add(parentItem2);
                }
            }
        }

        return result;
    }

    private DataParentItem getDataParentItem(Parent data) {
        DataParentItem result = null;

        if (data != null && data.getName() != null && !data.getName().isEmpty()) {
            result = new DataParentItem();
            result.setName(data.getName());
            result.setSurname(data.getSurname());
            result.setPatronymic(data.getPatronymic());
            result.setState(data.getState());
            result.setAddress(data.getAddress());
            result.setGender(data.getGender() != null && data.getGender().endsWith(Constants.FEMALE) ? "F" : "M");
            result.setChildRelationship(data.getChildRelationship() != null && data.getChildRelationship().endsWith(Constants.PARENT) ? "parent" : "guardian");
            result.setBirthDate(data.getBirthDate() == 0 ? "" : ISO8601.fromDate(data.getBirthDate()));
            result.setSpokenLanguage(data.getSpokenLanguage());
            result.setPhone(data.getPhone());
            result.setEmail(data.getEmail());
            result.setSignatureScan(addEncodedString(data.getSignatureScan()));
            result.setPreferableContact(data.getPreferableContact() == null ? "" : data.getPreferableContact().toLowerCase());
        }

        return result;
    }

    private DataSurveyItem getDataSurveyItem(Survey data, int childServerId) {
        DataSurveyItem result = null;

        if (data != null) {
            result = new DataSurveyItem();
            result.setId(data.getIdServer());
            result.setChildId(String.valueOf(childServerId));
            result.setCompletedDateTime(data.getCompletedDateTime() == 0 ? "" : ISO8601.fromTime(data.getCompletedDateTime()));
            result.setCreatedDateTime(data.getCreatedDateTime() == 0 ? "" : ISO8601.fromTime(data.getCreatedDateTime()));
        }

        return result;
    }

    private String addEncodedString(String value) {
        String result = "";

        if (value != null && !value.isEmpty()) {
            result = Constants.PHOTO_BASE64_ENCODED + value;
        }

        return result;
    }
}
