package com.reading.start.presentation.mvp.models;

import com.reading.start.AppCore;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.general.TLog;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.TestsProvider;
import com.reading.start.utils.NetworkHelper;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.realm.Realm;

public class DownloadSurveyModel extends BaseModel {
    private static final String TAG = DownloadSurveyModel.class.getSimpleName();

    public DownloadSurveyModel(Realm realm) {
        super(realm);
    }

    /**
     * Process download survey
     */
    public Observable<Integer> getDownloadObservable(final int childId, final int surveyId) {
        Observable<Integer> result = Observable.create(subscriber -> {
            Realm realm = null;
            Boolean success = true;
            Integer progress = 0;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                if (NetworkHelper.hasActiveInternetConnection() && realm != null && !realm.isClosed()) {
                    String token = AppCore.getInstance().getPreferences().getServerToken();

                    // upload surveys
                    Survey surveyResult = realm.where(Survey.class).equalTo(Survey.FILED_ID, surveyId).findFirst();
                    SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyId).findFirst();

                    if (surveyResult != null) {
                        realm.beginTransaction();
                        surveyStatus.setUploadProgress(0);
                        surveyStatus.setInProgress(true);
                        realm.commitTransaction();
                        subscriber.onNext(0);

                        // upload survey results
                        ArrayList<ITestModule> list = TestsProvider.getInstance(AppCore.getInstance()).getAllDisplayingTestModules();

                        if (list != null && list.size() > 0) {
                            int increment = 100 / list.size();

                            for (ITestModule module : list) {
                                module.downloadResult(token, surveyId, surveyResult.getIdServer());

                                progress += increment;
                                realm.beginTransaction();
                                surveyStatus.setUploadProgress(progress);
                                realm.commitTransaction();
                                subscriber.onNext(progress);
                            }
                        }

                        realm.beginTransaction();
                        surveyStatus.setUploadProgress(100);
                        surveyStatus.setRemote(false);
                        surveyStatus.setNeedUpload(false);
                        surveyStatus.setNeedDownload(false);
                        surveyStatus.setUploaded(true);
                        surveyStatus.setInProgress(false);
                        surveyStatus.setTries(0);
                        subscriber.onNext(100);
                        realm.commitTransaction();
                    }
                } else {
                    success = false;
                }
            } catch (Exception e) {
                TLog.e(TAG, e);
                success = false;
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }

            if (success) {
                subscriber.onComplete();
            } else {
                subscriber.onError(new Exception("Unable to upload survey"));
            }
        });

        return result;
    }

    /**
     * Check is survey completed
     */
    public Observable<Boolean> getCheckIsSurveyCompletedObservable(final int surveyId) {
        Observable<Boolean> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
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
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

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
