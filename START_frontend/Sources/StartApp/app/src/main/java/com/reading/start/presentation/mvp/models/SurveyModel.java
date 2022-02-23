package com.reading.start.presentation.mvp.models;

import com.reading.start.AppCore;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.ChildrenInfo;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.general.TLog;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.TestsProvider;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.realm.Realm;

public class SurveyModel extends BaseModel {
    private static final String TAG = SurveyModel.class.getSimpleName();

    public SurveyModel(Realm realm) {
        super(realm);
    }

    /**
     * Gets all test modules
     */
    public Observable<ArrayList<ITestModule>> getModulesListObservable(int surveyId) {
        Observable<ArrayList<ITestModule>> result = Observable.create(subscriber -> {
            ArrayList<ITestModule> list = TestsProvider.getInstance(AppCore.getInstance()).getAllDisplayingTestModules();

            if (list != null && list.size() > 0) {
                for (ITestModule module : list) {
                    module.fetchTestResults(surveyId);
                }
            }

            if (list != null) {
                subscriber.onNext(list);
            }

            subscriber.onComplete();
        });

        return result;
    }

    /**
     * Gets child by id
     */
    public Observable<Children> getChildObservable(final int childId) {
        Observable<Children> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                if (realm != null && !realm.isClosed()) {
                    Children rResult = realm.where(Children.class).equalTo(Children.FILED_ID, childId).findFirst();

                    if (rResult != null) {
                        ChildrenInfo info = new ChildrenInfo();

                        Survey lastSurvey = realm.where(Survey.class)
                                .equalTo(Survey.FILED_CHILD_ID, rResult.getId()).findFirst();

                        if (lastSurvey != null) {
                            info.setSurveyDate(lastSurvey.getCreatedDateTime());
                            info.setIsFinished(lastSurvey.getIsCompleted());
                        }

                        int surveyCount = (int) realm.where(Survey.class)
                                .equalTo(Survey.FILED_CHILD_ID, rResult.getId()).count();

                        info.setSurveyNumber(surveyCount);
                        rResult.setChildrenInfo(info);

                        subscriber.onNext(rResult);
                        subscriber.onComplete();
                    } else {
                        subscriber.onError(new Exception("Unable to fetch child"));
                    }
                } else {
                    subscriber.onError(new Exception("Realm closed"));
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
     * Delete survey by id
     */
    public Observable<Boolean> getDeleteObservable(final int surveyId) {
        Observable<Boolean> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                Boolean isDeleted = false;
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                if (realm != null && !realm.isClosed()) {
                    Survey rResult = realm.where(Survey.class).equalTo(Survey.FILED_ID, surveyId).findFirst();
                    SurveyStatus sResult = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyId).findFirst();

                    if (rResult != null) {
                        try {
                            realm.beginTransaction();
                            rResult.deleteFromRealm();
                            isDeleted = true;
                            realm.commitTransaction();

                            if (sResult != null) {
                                realm.beginTransaction();
                                sResult.deleteFromRealm();
                                realm.commitTransaction();
                            }

                            ArrayList<ITestModule> list = TestsProvider.getInstance(AppCore.getInstance()).getAllDisplayingTestModules();

                            if (list != null && list.size() > 0) {
                                for (ITestModule module : list) {
                                    module.deleteResults(surveyId);
                                }
                            }
                        } catch (Exception e) {
                            realm.cancelTransaction();
                        }

                        subscriber.onNext(isDeleted);
                        subscriber.onComplete();
                    } else {
                        subscriber.onError(new Exception("Unable to delete survey"));
                    }
                } else {
                    subscriber.onError(new Exception("Realm closed"));
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
}
