package com.reading.start.presentation.mvp.models;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.data.DataServerManager;
import com.reading.start.data.entity.DataChildren;
import com.reading.start.data.entity.DataChildrenItem;
import com.reading.start.data.entity.DataChildrenSurveyItem;
import com.reading.start.data.entity.DataLanguageItem;
import com.reading.start.data.entity.DataParentItem;
import com.reading.start.data.entity.DataStateItem;
import com.reading.start.data.response.ResponseChildrenData;
import com.reading.start.data.response.ResponseChildrenSurveysData;
import com.reading.start.data.response.ResponseConsent;
import com.reading.start.data.response.ResponseLanguages;
import com.reading.start.data.response.ResponseStates;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.Consent;
import com.reading.start.domain.entity.Languages;
import com.reading.start.domain.entity.Parent;
import com.reading.start.domain.entity.SocialWorker;
import com.reading.start.domain.entity.States;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.general.TLog;
import com.reading.start.tests.ISO8601;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.TestsProvider;
import com.reading.start.utils.NetworkHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.realm.Realm;
import io.realm.RealmResults;

public class SynchronizeModel extends BaseModel {
    private static final String TAG = SynchronizeModel.class.getSimpleName();

    public static class LoadingDataException extends Exception {
        public LoadingDataException() {
            super("LoadingDataException");
        }

        @Override
        public String toString() {
            return LoadingDataException.class.getSimpleName();
        }
    }

    public SynchronizeModel(Realm realm) {
        super(realm);
    }

    /**
     * Loadign data from serve
     */
    public Observable<Boolean> getLoadingObservable() {
        Observable<Boolean> result = Observable.create(subscriber -> {
            TLog.d(TAG, "START SYNC");
            Boolean success = true;
            long syncTime = Calendar.getInstance().getTimeInMillis();

            try {
                if (NetworkHelper.hasActiveInternetConnection()) {
                    String token = AppCore.getInstance().getPreferences().getServerToken();
                    String date = null;

                    if (AppCore.getInstance().getPreferences().getSyncTime() > 0) {
                        date = ISO8601.fromTime(AppCore.getInstance().getPreferences().getSyncTime());
                    }

                    // upload children info
                    ResponseChildrenData responseChildren = DataServerManager.getInstance().getChildren(token, date);

                    if (responseChildren != null && responseChildren.isSuccess()) {
                        success = updateChildren(responseChildren.getData());
                    } else {
                        success = false;
                    }

                    TLog.d(TAG, "upload children info: " + (success ? "success" : "fail"));

                    // no need track success for children
                    success = true;

                    // download app content
                    success = downloadAppContent(token);

                    if (success) {
                        // update surveys for all child
                        success = updateSurveys(token);

                        if (success) {
                            // download modules content info
                            success = updateModuleContent(token, date);

                            if (success) {
                                AppCore.getInstance().getPreferences().setSyncTime(syncTime);
                                Realm realm = null;

                                try {
                                    SocialWorker worker = null;
                                    realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                                    if (realm != null && !realm.isClosed()) {
                                        try {
                                            worker = realm.where(SocialWorker.class)
                                                    .equalTo(SocialWorker.FILED_ID, AppCore.getInstance().getPreferences().getLoginWorkerId()).findFirst();
                                        } catch (Exception e) {
                                            TLog.e(TAG, e);
                                        }
                                    }

                                    if (worker != null) {
                                        realm.beginTransaction();
                                        worker.setSyncTime(syncTime);
                                        realm.commitTransaction();
                                    }
                                } catch (Exception e) {
                                    if (realm != null) {
                                        realm.cancelTransaction();
                                    }
                                    TLog.e(TAG, e);
                                } finally {
                                    if (realm != null) {
                                        realm.close();
                                    }
                                }
                            }
                        }
                    }
                } else if (AppCore.getInstance().getPreferences().getSyncTime() == 0) {
                    success = false;
                }
            } catch (Exception e) {
                TLog.e(TAG, e);
                success = false;
            }

            TLog.d(TAG, "END SYNC: " + (success ? "success" : "fail"));

            if (success) {
                if (!subscriber.isDisposed()) {
                    subscriber.onNext(success);
                    subscriber.onComplete();
                }
            } else {
                if (!subscriber.isDisposed()) {
                    subscriber.onError(new LoadingDataException());
                }
            }
        });

        return result;
    }

    private boolean updateChildren(DataChildren data) {
        boolean result = true;
        Realm realm = null;

        try {
            if (data != null && data.getChildren().size() > 0) {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                for (DataChildrenItem item : data.getChildren()) {
                    if (item.isDeleted().equals("1")) {
                        Children childResult = realm.where(Children.class).equalTo(Children.FILED_ID_SERVER, item.getId()).findFirst();

                        if (childResult != null) {
                            int childId = childResult.getId();

                            //delete child from database
                            try {
                                realm.beginTransaction();
                                childResult.deleteFromRealm();
                                realm.commitTransaction();
                            } catch (Exception e) {
                                realm.cancelTransaction();
                                TLog.e(TAG, e);
                            }

                            // delete all child's surveys
                            RealmResults<Survey> surveys = realm.where(Survey.class).equalTo(Survey.FILED_CHILD_ID, childId).findAll();

                            if (surveys != null && surveys.size() > 0) {
                                for (Survey surveyItem : surveys) {
                                    try {
                                        int surveyId = surveyItem.getId();
                                        realm.beginTransaction();
                                        surveyItem.deleteFromRealm();
                                        realm.commitTransaction();
                                        deleteResultFromModules(surveyId);
                                    } catch (Exception e) {
                                        realm.cancelTransaction();
                                        TLog.e(TAG, e);
                                    }
                                }
                            }
                        }
                    } else {
                        try {
                            realm.beginTransaction();
                            Children childResult = realm.where(Children.class).equalTo(Children.FILED_ID_SERVER, item.getId()).findFirst();
                            Number currentChildIdNum = realm.where(Children.class).max(Children.FILED_ID);
                            Number currentParentIdNum = realm.where(Parent.class).max(Parent.FILED_ID);
                            int nextChildId;
                            int nextParentId;

                            if (currentChildIdNum == null) {
                                nextChildId = 0;
                            } else {
                                nextChildId = currentChildIdNum.intValue() + 1;
                            }

                            if (currentParentIdNum == null) {
                                nextParentId = 1;
                            } else {
                                nextParentId = currentParentIdNum.intValue() + 1;
                            }

                            if (childResult != null) {
                                fillChild(childResult, item);
                            } else {
                                childResult = new Children();
                                childResult.setId(nextChildId);
                                childResult = realm.copyToRealmOrUpdate(childResult);
                                fillChild(childResult, item);
                            }

                            childResult.setIdWorker(AppCore.getInstance().getPreferences().getLoginWorkerId());

                            Parent parentResult1 = null;
                            Parent parentResult2 = null;

                            if (item.getParents() != null && item.getParents().size() > 0) {
                                for (DataParentItem parentItem : item.getParents()) {
                                    Parent parentResult = realm.where(Parent.class).equalTo(Parent.FILED_ID_SERVER, parentItem.getId()).findFirst();

                                    if (parentResult != null) {
                                        fillParent(parentResult, parentItem);
                                    } else {
                                        parentResult = new Parent();
                                        parentResult.setId(nextParentId + item.getParents().indexOf(parentItem));
                                        parentResult = realm.copyToRealmOrUpdate(parentResult);
                                        fillParent(parentResult, parentItem);
                                    }

                                    if (item.getParents().indexOf(parentItem) == 0) {
                                        parentResult1 = parentResult;
                                    } else {
                                        parentResult2 = parentResult;
                                    }
                                }
                            }

                            if (parentResult1 == null) {
                                parentResult1 = new Parent();
                                parentResult1.setId(nextParentId);
                                parentResult1 = realm.copyToRealmOrUpdate(parentResult1);
                            }

                            if (parentResult2 == null) {
                                parentResult2 = new Parent();
                                parentResult2.setId(nextParentId + 1);
                                parentResult2 = realm.copyToRealmOrUpdate(parentResult2);
                            }

                            if (parentResult1 != null) {
                                parentResult1.setChildId(childResult.getId());
                                childResult.setParentFirst(parentResult1);
                            }

                            if (parentResult2 != null) {
                                parentResult2.setChildId(childResult.getId());
                                childResult.setParentSecond(parentResult2);
                            }

                            realm.commitTransaction();
                        } catch (Exception e) {
                            TLog.e(TAG, e);
                            realm.cancelTransaction();
                        }
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            TLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    private void fillChild(Children child, DataChildrenItem data) {
        if (child != null && data != null) {
            child.setIdServer(data.getId());
            child.setName(data.getName());
            child.setSurname(data.getSurname());
            child.setPatronymic(data.getPatronymic());
            child.setState(data.getState());
            child.setAddress(data.getAddress());
            child.setGender(data.getGender().endsWith("F") ? Constants.FEMALE : Constants.MALE);
            child.setBirthDate(ISO8601.toDate(data.getBirthDate()));
            child.setLatitude(data.getLatitude());
            child.setLongitude(data.getLongitude());
            child.setDiagnosis(data.getDiagnosis());
            child.setDiagnosisClinic(data.getDiagnosisClinic());
            child.setDiagnosisDate(ISO8601.toDate(data.getDiagnosisDate()));
            child.setAddDateTime(ISO8601.toTime(data.getAddDateTime()));
            child.setAddBy(data.getAddBy());
            child.setModDateTime(ISO8601.toTime(data.getModDateTime()));
            child.setModBy(data.getModBy());
            child.setPhoto(excludeEncodedString(data.getPhoto()));
            child.setHand(data.getHand());
        }
    }

    private void fillParent(Parent parent, DataParentItem data) {
        if (parent != null && data != null) {
            parent.setIdServer(data.getId());
            parent.setName(data.getName());
            parent.setSurname(data.getSurname());
            parent.setPatronymic(data.getPatronymic());
            parent.setState(data.getState());
            parent.setAddress(data.getAddress());
            parent.setGender(data.getGender().endsWith("F") ? Constants.FEMALE : Constants.MALE);
            parent.setChildRelationship(data.getGender().endsWith("parent") ? Constants.PARENT : Constants.GUARDIAN);
            parent.setBirthDate(ISO8601.toDate(data.getBirthDate()));
            parent.setSpokenLanguage(data.getSpokenLanguage());
            parent.setPhone(data.getPhone());
            parent.setEmail(data.getEmail());
            parent.setSignatureScan(excludeEncodedString(data.getSignatureScan()));
            parent.setAddDateTime(ISO8601.toTime(data.getAddDateTime()));
            parent.setAddBy(data.getAddBy());
            parent.setModDateTime(ISO8601.toTime(data.getModDateTime()));
            parent.setModBy(data.getModBy());
            parent.setPreferableContact(data.getPreferableContact());
        }
    }

    private boolean downloadAppContent(String token) {
        TLog.d(TAG, "downloadAppContent: start");
        boolean result = true;
        Realm realm = null;

        try {
            if (token != null) {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
                ResponseStates responseStates = DataServerManager.getInstance().getStates(token);

                if (responseStates != null && responseStates.getData() != null && responseStates.getData().getStates() != null) {
                    RealmResults<States> resultStates = realm.where(States.class).findAll();
                    realm.beginTransaction();
                    resultStates.deleteAllFromRealm();
                    int index = 0;

                    for (DataStateItem item : responseStates.getData().getStates()) {
                        States stateItem = new States();
                        stateItem.setId(index);
                        stateItem.setIdServer(item.getId());
                        stateItem.setNameEnglish(item.getNameEnglish());
                        stateItem.setNameHindi(item.getNameHindi());
                        realm.copyToRealmOrUpdate(stateItem);
                        index++;
                    }

                    realm.commitTransaction();
                }

                ResponseLanguages responseLanguages = DataServerManager.getInstance().getLanguages(token);

                if (responseLanguages != null && responseLanguages.getData() != null && responseLanguages.getData().getLanguages() != null) {
                    RealmResults<Languages> resultLanguages = realm.where(Languages.class).findAll();
                    realm.beginTransaction();
                    resultLanguages.deleteAllFromRealm();
                    int index = 0;

                    for (DataLanguageItem item : responseLanguages.getData().getLanguages()) {
                        Languages languageItem = new Languages();
                        languageItem.setId(index);
                        languageItem.setIdServer(item.getId());
                        languageItem.setNameEnglish(item.getNameEnglish());
                        languageItem.setNameHindi(item.getNameHindi());
                        realm.copyToRealmOrUpdate(languageItem);
                        index++;
                    }

                    realm.commitTransaction();
                }

                ResponseConsent responseConsent = DataServerManager.getInstance().getConsent(token);

                if (responseConsent != null && responseConsent.getData() != null && responseConsent.getData().getConsentForm() != null) {
                    RealmResults<Consent> resultConsent = realm.where(Consent.class).findAll();
                    realm.beginTransaction();
                    resultConsent.deleteAllFromRealm();

                    Consent consentItem = new Consent();
                    consentItem.setId(0);
                    consentItem.setEnglish(responseConsent.getData().getConsentForm().getEnglish());
                    consentItem.setHindi(responseConsent.getData().getConsentForm().getHindi());
                    realm.copyToRealmOrUpdate(consentItem);
                    realm.commitTransaction();
                }
            }
        } catch (Exception e) {
            result = false;

            if (realm != null) {
                realm.cancelTransaction();
            }
            TLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        TLog.d(TAG, "downloadAppContent: " + (result ? "success" : "fail"));
        TLog.d(TAG, "downloadAppContent: end");
        return result;
    }

    private boolean updateSurveys(String token) {
        TLog.d(TAG, "updateSurveys: start");
        boolean result = true;
        Realm realm = null;

        try {
            if (token != null) {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
                List<Children> items = realm.where(Children.class).findAll();

                for (Children item : items) {
                    updateChildSurveys(token, item, realm);
                }
            }
        } catch (Exception e) {
            result = false;
            TLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        TLog.d(TAG, "updateSurveys: " + (result ? "success" : "fail"));
        TLog.d(TAG, "updateSurveys: end");
        return result;
    }

    private boolean updateChildSurveys(String token, Children child, Realm realm) {
        boolean result = true;

        try {
            if (token != null && child != null && realm != null && child.getIdServer() != -1) {
                // upload children info
                ResponseChildrenSurveysData responseChildren = DataServerManager.getInstance().getChildrenSurveys(token, String.valueOf(child.getIdServer()));

                if (responseChildren != null && responseChildren.isSuccess() && responseChildren.getData() != null) {
                    if (responseChildren.getData().getCount() > 0) {
                        ArrayList<DataChildrenSurveyItem> items = responseChildren.getData().getSurveys();

                        if (items != null && items.size() > 0) {
                            for (DataChildrenSurveyItem item : items) {
                                boolean isDeletedItem = item.isDeleted().equals("1");
                                // check is exist in local db.
                                long count = realm.where(Survey.class).equalTo(Survey.FILED_ID_SERVER, item.getId()).count();

                                if (count == 0) {
                                    try {
                                        if (!isDeletedItem) {
                                            realm.beginTransaction();
                                            Number currentIdNum = realm.where(Survey.class).max(Survey.FILED_ID);
                                            Number currentIdNumStatus = realm.where(SurveyStatus.class).max(SurveyStatus.FILED_ID);
                                            int nextId;
                                            int nextIdStatus;

                                            if (currentIdNum == null) {
                                                nextId = 0;
                                            } else {
                                                nextId = currentIdNum.intValue() + 1;
                                            }

                                            if (currentIdNumStatus == null) {
                                                nextIdStatus = 0;
                                            } else {
                                                nextIdStatus = currentIdNumStatus.intValue() + 1;
                                            }

                                            Survey survey = new Survey();
                                            survey.setId(nextId);
                                            survey.setIdServer(item.getId());
                                            survey.setChildId(child.getId());
                                            survey.setCreatedDateTime(ISO8601.toTime(item.getCreatedDateTime()));
                                            survey.setCreatedBySocialWorker(item.getCreatedBySocialWorker());
                                            survey.setCompletedDateTime(ISO8601.toTime(item.getCompletedDateTime()));
                                            realm.copyToRealmOrUpdate(survey);

                                            SurveyStatus status = new SurveyStatus();
                                            status.setId(nextIdStatus);
                                            status.setIdSurvey(nextId);
                                            status.setRemote(true);
                                            status.setUploadProgress(0);
                                            status.setUploaded(false);
                                            status.setNeedUpload(false);
                                            status.setNeedDownload(false);
                                            status.setTries(0);
                                            realm.copyToRealmOrUpdate(status);

                                            realm.commitTransaction();
                                        }
                                    } catch (Exception e) {
                                        realm.cancelTransaction();
                                        TLog.e(TAG, e);
                                    }
                                } else {
                                    if (isDeletedItem) {
                                        try {
                                            realm.beginTransaction();
                                            RealmResults<Survey> deletedItems = realm.where(Survey.class).equalTo(Survey.FILED_ID_SERVER, item.getId()).findAll();

                                            if (deletedItems != null && deletedItems.size() > 0) {
                                                for (Survey surveyItem : deletedItems) {
                                                    int surveyItemId = surveyItem.getId();
                                                    surveyItem.deleteFromRealm();
                                                    deleteResultFromModules(surveyItemId);

                                                    SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyItemId).findFirst();
                                                    surveyStatus.deleteFromRealm();
                                                }
                                            }
                                            realm.commitTransaction();
                                        } catch (Exception e) {
                                            realm.cancelTransaction();
                                            TLog.e(TAG, e);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            TLog.e(TAG, e);
        }

        return result;
    }

    private boolean updateModuleContent(String token, String date) {
        TLog.d(TAG, "updateModuleContent: start");
        boolean result = true;

        try {
            ArrayList<ITestModule> list = TestsProvider.getInstance(AppCore.getInstance()).getAllDisplayingTestModules();

            if (list != null && list.size() > 0) {
                boolean resultTest;

                for (ITestModule module : list) {
                    TLog.d(TAG, "updateModuleContent, module - " + module.getSurveyType() + ": start");
                    resultTest = module.downloadTestData(token, date);
                    TLog.d(TAG, "updateModuleContent, module - " + module.getSurveyType() + ": " + (resultTest ? "success" : "fail"));

                    if (!resultTest) {
                        result = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            TLog.e(TAG, e);
        }

        TLog.d(TAG, "updateModuleContent: " + (result ? "success" : "fail"));
        TLog.d(TAG, "updateModuleContent: end");
        return result;
    }

    private boolean deleteResultFromModules(int surveyId) {
        boolean result = true;

        try {
            ArrayList<ITestModule> list = TestsProvider.getInstance(AppCore.getInstance()).getAllDisplayingTestModules();

            if (list != null && list.size() > 0) {
                for (ITestModule module : list) {
                    module.deleteResults(surveyId);
                }
            }

        } catch (Exception e) {
            result = false;
            TLog.e(TAG, e);
        }

        return result;
    }

    private String excludeEncodedString(String value) {
        String result = "";
        int index = value.indexOf(',');

        if (index > 0) {
            result = value.substring(index);
        } else {
            result = value;
        }

        return result;
    }
}
