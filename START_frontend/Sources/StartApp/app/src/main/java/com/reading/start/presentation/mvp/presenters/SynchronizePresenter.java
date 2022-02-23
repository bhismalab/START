package com.reading.start.presentation.mvp.presenters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.reading.start.presentation.mvp.holders.SynchronizeViewHolder;
import com.reading.start.presentation.mvp.models.SynchronizeModel;
import com.reading.start.presentation.mvp.views.SynchronizeView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SynchronizePresenter extends BasePresenter<SynchronizeView, SynchronizeModel, SynchronizeViewHolder> {

    public SynchronizePresenter() {
        super(true);
        setModel(new SynchronizeModel(getRealm()));
    }

    @Override
    public void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        processLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void processLoading() {
        getView().onLoading();
        final Observable<Boolean> observable = getModel().getLoadingObservable();

        int threadCount = Runtime.getRuntime().availableProcessors();
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(threadCount);
        Scheduler scheduler = Schedulers.from(threadPoolExecutor);

        getSubscriptions().add(observable
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onError("");
                    }

                    @Override
                    public void onComplete() {
                        getView().onLoadSuccess();
                    }
                }));

    }
}
