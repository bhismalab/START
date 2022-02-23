package com.reading.start.presentation.mvp.models;

import com.reading.start.AppCore;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.ChildrenInfo;
import com.reading.start.domain.entity.ISurvey;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.general.TLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.realm.Realm;

public class ChildInformationModel extends BaseModel {
    private static final String TAG = ChildInformationModel.class.getSimpleName();

    public ChildInformationModel(Realm realm) {
        super(realm);
    }

    /**
     * Gets all survey for child
     */
    public Observable<ArrayList<ISurvey>> getSurveysListObservable(final int childId) {
        Observable<ArrayList<ISurvey>> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                if (realm != null && !realm.isClosed()) {
                    List<Survey> rResult = realm.where(Survey.class).equalTo(Survey.FILED_CHILD_ID, childId).findAll();

                    if (rResult != null) {
                        ArrayList<ISurvey> list = new ArrayList<>();
                        list.addAll(rResult);

                        Collections.sort(list, (o1, o2) -> {
                            if (o1 != null && o2 != null) {
                                Long time1 = ((Survey) (o1)).getCreatedDateTime();
                                Long time2 = ((Survey) (o2)).getCreatedDateTime();
                                return time1.compareTo(time2);
                            } else {
                                return 0;
                            }
                        });

                        if (list != null) {
                            subscriber.onNext(list);
                        }

                        subscriber.onComplete();
                    } else {
                        subscriber.onError(new Exception("Unable to fetch surveys"));
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
     * Gets survey by id
     */
    public Observable<Survey> getSurveyObservable(final int surveyId) {
        Observable<Survey> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                if (realm != null && !realm.isClosed()) {
                    Survey rResult = realm.where(Survey.class).equalTo(Survey.FILED_ID, surveyId).findFirst();

                    if (rResult != null) {
                        subscriber.onNext(rResult);
                        subscriber.onComplete();
                    } else {
                        subscriber.onError(new Exception("Unable to fetch surveys"));
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
     * Get child by id
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
                        status.setRemote(false);
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
}
