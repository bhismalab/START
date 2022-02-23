package com.reading.start.presentation.mvp.models;

import com.reading.start.AppCore;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.general.TLog;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.TestsProvider;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.realm.Realm;

public class MainActivityModel extends BaseModel {
    private static final String TAG = MainActivityModel.class.getSimpleName();

    public MainActivityModel(Realm realm) {
        super(realm);
    }

    /**
     * Creates new survey
     */
    public Observable<Survey> getCreateSurveyObservable(final int childId, final String worker) {
        Observable<Survey> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
                Survey instance = null;

                if (realm != null && !realm.isClosed()) {
                    try {
                        realm.beginTransaction();
                        Number currentSurveyIdNum = realm.where(Survey.class).max(Survey.FILED_ID);
                        Number currentIdNumStatus = realm.where(SurveyStatus.class).max(SurveyStatus.FILED_ID);
                        int nextSurveyId;
                        int nextIdStatus;

                        if (currentSurveyIdNum == null) {
                            nextSurveyId = 0;
                        } else {
                            nextSurveyId = currentSurveyIdNum.intValue() + 1;
                        }

                        if (currentIdNumStatus == null) {
                            nextIdStatus = 0;
                        } else {
                            nextIdStatus = currentIdNumStatus.intValue() + 1;
                        }

                        Survey survey = new Survey(nextSurveyId, childId, worker);
                        instance = realm.copyToRealmOrUpdate(survey);

                        SurveyStatus status = new SurveyStatus();
                        status.setId(nextIdStatus);
                        status.setIdSurvey(nextSurveyId);
                        status.setRemote(true);
                        status.setUploadProgress(0);
                        status.setUploaded(false);
                        status.setNeedUpload(false);
                        status.setNeedDownload(false);
                        status.setTries(0);
                        realm.copyToRealmOrUpdate(status);

                        realm.commitTransaction();
                    } catch (Exception e) {
                        realm.cancelTransaction();
                    }
                }

                if (instance != null) {
                    subscriber.onNext(instance);
                    subscriber.onComplete();
                } else {
                    subscriber.onError(new Exception("Unable to save object to Realm"));
                }
            } catch (Exception e) {
                TLog.e(TAG, e);
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });

        return result;
    }

    /**
     * Check is srvey completed
     */
    public Observable<Boolean> getCheckIsSurveyCompletedObservable(final int surveyId) {
        Observable<Boolean> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
                Boolean resultValue = true;
                float progress = 0;
                float countCompleted = 0;
                ArrayList<ITestModule> items = TestsProvider.getInstance(AppCore.getInstance()).getAllDisplayingTestModules();

                if (items != null && items.size() > 0) {
                    for (ITestModule item : items) {
                        if (item.isCompleted(surveyId)) {
                            countCompleted++;
                        }
                    }

                    progress = countCompleted / items.size() * 100;
                }

                boolean isCompleted = progress == 100;

                if (realm != null && !realm.isClosed()) {
                    try {
                        Survey survey = realm.where(Survey.class).equalTo(Survey.FILED_ID, surveyId).findFirst();
                        SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyId).findFirst();
                        realm.beginTransaction();
                        survey.setIsCompleted(isCompleted);
                        surveyStatus.setCompleteProgress((int) progress);
                        realm.commitTransaction();
                    } catch (Exception e) {
                        realm.cancelTransaction();
                        resultValue = false;
                    }
                }

                if (resultValue != null) {
                    subscriber.onNext(resultValue);
                }

                subscriber.onComplete();
            } catch (Exception e) {
                TLog.e(TAG, e);
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });

        return result;
    }

    /**
     * Check is survey uploaded
     */
    public Observable<Boolean> getCheckIsSurveyUploadedObservable(final int surveyId) {
        Observable<Boolean> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
                Boolean resultValue = true;
                ArrayList<ITestModule> items = TestsProvider.getInstance(AppCore.getInstance()).getAllDisplayingTestModules();

                if (items != null && items.size() > 0) {
                    for (ITestModule item : items) {
                        if (!item.isUploaded(surveyId)) {
                            resultValue = false;
                        }
                    }
                }

                if (realm != null && !realm.isClosed()) {
                    try {
                        SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyId).findFirst();
                        realm.beginTransaction();
                        surveyStatus.setUploadProgress(0);
                        surveyStatus.setNeedUpload(false);
                        surveyStatus.setNeedDownload(false);
                        surveyStatus.setUploaded(resultValue);
                        surveyStatus.setRemote(false);
                        realm.commitTransaction();
                    } catch (Exception e) {
                        realm.cancelTransaction();
                        resultValue = false;
                    }
                }

                subscriber.onNext(resultValue);
                subscriber.onComplete();

            } catch (Exception e) {
                TLog.e(TAG, e);
                subscriber.onError(e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        });

        return result;
    }
}
