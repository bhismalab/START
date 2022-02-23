package com.reading.start.presentation.mvp.models;

import com.reading.start.AppCore;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.ChildrenInfo;
import com.reading.start.domain.entity.IChild;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.domain.rx.BooleanRxResult;
import com.reading.start.domain.rx.MySurveyInfo;
import com.reading.start.domain.rx.MySurveyRxResult;
import com.reading.start.domain.rx.RxStatus;
import com.reading.start.general.TLog;
import com.reading.start.presentation.services.UploadService;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.ITestModuleResult;
import com.reading.start.tests.TestsProvider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.realm.Realm;

public class MySurveysModel extends BaseModel {
    private static final String TAG = MySurveysModel.class.getSimpleName();

    public MySurveysModel(Realm realm) {
        super(realm);
    }

    /**
     * Gets all child
     */
    public Observable<MySurveyRxResult> getChildListObservable() {
        Observable<MySurveyRxResult> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
                MySurveyInfo rxInfo = new MySurveyInfo();

                if (realm != null && !realm.isClosed()) {
                    List<Children> rResult = realm.where(Children.class)
                            .equalTo(Children.FILED_ID_WORKER, AppCore.getInstance().getPreferences().getLoginWorkerId())
                            .findAll();

                    if (rResult != null) {
                        LinkedList<IChild> list = new LinkedList<>();
                        list.addAll(rResult);

                        for (IChild item : list) {
                            Children itemChild = (Children) item;
                            ChildrenInfo info = new ChildrenInfo();

                            Survey lastSurvey = realm.where(Survey.class)
                                    .equalTo(Survey.FILED_CHILD_ID, itemChild.getId()).findFirst();

                            if (lastSurvey != null) {
                                info.setSurveyDate(lastSurvey.getCreatedDateTime());
                                info.setIsFinished(lastSurvey.getIsCompleted());
                            }

                            int surveyCount = (int) realm.where(Survey.class)
                                    .equalTo(Survey.FILED_CHILD_ID, itemChild.getId()).count();

                            info.setSurveyNumber(surveyCount);
                            itemChild.setChildrenInfo(info);
                        }

                        rxInfo.mItems = list;
                        subscriber.onNext(new MySurveyRxResult(RxStatus.Success, rxInfo));
                        subscriber.onComplete();
                    } else {
                        subscriber.onError(new Exception("Unable to fetch children"));
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
     * Gets status for upload button
     */
    public Observable<BooleanRxResult> getUploadButtonStatusObservable() {
        Observable<BooleanRxResult> result = Observable.create(subscriber -> {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
                boolean isAvailable = false;

                if (realm != null && !realm.isClosed()) {
                    List<Children> rResult = realm.where(Children.class)
                            .equalTo(Children.FILED_ID_WORKER, AppCore.getInstance().getPreferences().getLoginWorkerId())
                            .findAll();

                    if (rResult != null) {
                        for (Children item : rResult) {
                            // check if need upload any surveys
                            List<Survey> surveys = realm.where(Survey.class).equalTo(Survey.FILED_CHILD_ID, item.getId()).findAll();
                            ArrayList<ITestModule> moduleList = TestsProvider.getInstance(AppCore.getInstance()).getAllDisplayingTestModules();

                            if (surveys != null && surveys.size() > 0) {
                                for (Survey sItem : surveys) {
                                    if (!isAvailable) {
                                        if (moduleList != null && moduleList.size() > 0) {
                                            for (ITestModule module : moduleList) {
                                                module.fetchTestResults(sItem.getId());
                                                ArrayList<ITestModuleResult> moduleResults = module.getTestResults(sItem.getId());

                                                if (!module.isUploaded(sItem.getId()) && moduleResults != null && moduleResults.size() > 0) {
                                                    if (UploadService.getUploadingSurveyId() != sItem.getId()) {
                                                        SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, sItem.getId()).findFirst();

                                                        if (surveyStatus != null) {
                                                            isAvailable = !(surveyStatus.isNeedUpload()
                                                                    || surveyStatus.isUploaded()
                                                                    || surveyStatus.isRemote()
                                                                    || surveyStatus.isInProgress());

                                                            if (isAvailable) {
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        subscriber.onNext(new BooleanRxResult(RxStatus.Success, isAvailable));
                        subscriber.onComplete();
                    } else {
                        subscriber.onError(new Exception("Unable to fetch children"));
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
