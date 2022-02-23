package com.reading.start.presentation.mvp.presenters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.domain.entity.Survey;
import com.reading.start.presentation.mvp.holders.MainActivityViewHolder;
import com.reading.start.presentation.mvp.models.MainActivityModel;
import com.reading.start.presentation.mvp.views.MainActivityView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

public class MainActivityPresenter extends BasePresenter<MainActivityView, MainActivityModel, MainActivityViewHolder> {
    public static final String TAG = MainActivityPresenter.class.getSimpleName();

    public MainActivityPresenter() {
        super(true);
        setModel(new MainActivityModel(getRealm()));
    }

    @Override
    public void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addSurvey(int childId) {
        String worker = AppCore.getInstance().getPreferences().getLoginWorker();
        final Observable<Survey> observable = getModel().getCreateSurveyObservable(childId, worker);

        getSubscriptions().add(observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Survey>() {
                    @Override
                    public void onNext(Survey survey) {
                        getView().onAddSurvey(childId, survey, 1);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    public void checkIsSurveyCompleted(int surveyId) {
        final Observable<Boolean> observable = getModel().getCheckIsSurveyCompletedObservable(surveyId);

        getSubscriptions().add(observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    public void checkIsSurveyUploaded(int surveyId) {
        final Observable<Boolean> observable = getModel().getCheckIsSurveyUploadedObservable(surveyId);

        getSubscriptions().add(observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }
}
