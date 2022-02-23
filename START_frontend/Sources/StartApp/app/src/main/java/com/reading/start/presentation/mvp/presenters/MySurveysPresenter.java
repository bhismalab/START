package com.reading.start.presentation.mvp.presenters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;
import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.CurrentScreen;
import com.reading.start.domain.entity.IChild;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.domain.rx.BooleanRxResult;
import com.reading.start.domain.rx.MySurveyRxResult;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.holders.MySurveysViewHolder;
import com.reading.start.presentation.mvp.models.MySurveysModel;
import com.reading.start.presentation.mvp.views.MySurveysView;
import com.reading.start.presentation.services.UploadService;
import com.reading.start.presentation.ui.adapters.MySurveysAdapter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class MySurveysPresenter extends BasePresenter<MySurveysView, MySurveysModel, MySurveysViewHolder> {

    public static final String TAG = MySurveysPresenter.class.getSimpleName();

    private MySurveysAdapter mAdapter;

    private boolean mIsRestore = false;

    public MySurveysPresenter(boolean restore) {
        super(true);
        setModel(new MySurveysModel(getRealm()));
        mIsRestore = restore;
    }

    MySurveysAdapter.OnSurveysItemClickListener mOnItemClickListener =
            new MySurveysAdapter.OnSurveysItemClickListener() {
                @Override
                public void onChildItemClick(Children child) {
                    getView().onChildInformationOpen(false, child);
                }

                @Override
                public void onAddItemClick() {
                    getView().onAddChild();
                }
            };

    @Override
    public void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getViewHolder() != null) {
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.navigation_button))
                    .subscribe(aVoid -> getView().onNavigationMenuPressed());

            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.upload_button_all))
                    .subscribe(aVoid -> uploadSurvey());
        }
    }

    @Override
    public void onResume() {
        getViewHolder().getActionBar().findViewById(R.id.upload_button_all).setEnabled(false);
        processLoadingItems();
        processUploadUploadButton();

        if (mIsRestore) {
            if (!openChildInfo()) {
                AppCore.getInstance().getPreferences().setCurrentScreen(CurrentScreen.ChildList);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private boolean openChildInfo() {
        boolean result = false;
        CurrentScreen screen = AppCore.getInstance().getPreferences().getCurrentScreen();

        if (screen != null && (screen == CurrentScreen.ChildInfo || screen == CurrentScreen.SurveyInfo)) {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                if (realm != null && !realm.isClosed()) {
                    Children rResult = realm.where(Children.class)
                            .equalTo(Children.FILED_ID, AppCore.getInstance().getPreferences().getCurrentChild())
                            .findFirst();

                    if (rResult != null) {
                        getView().onChildInformationOpen(true, rResult);
                        result = true;
                    }
                }
            } catch (Exception e) {
                TLog.e(TAG, e);
            } finally {
                if (realm != null) {
                    realm.close();
                }
            }
        }

        return result;
    }

    private void processLoadingItems() {
        final Observable<MySurveyRxResult> observable = getModel().getChildListObservable();

        getSubscriptions().add(observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<MySurveyRxResult>() {
                    @Override
                    public void onNext(MySurveyRxResult data) {
                        if (data != null) {
                            getViewHolder().getActionBar().findViewById(R.id.upload_button_all).setEnabled(data.getResult().mIsUploadAvailable);
                            mAdapter = new MySurveysAdapter(data.getResult().mItems, mOnItemClickListener);
                            getViewHolder().getRecyclerView().setAdapter(mAdapter);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onError(AppCore.getInstance().getResources().getString(R.string.error_message_error));
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    private void processUploadUploadButton() {
        final Observable<BooleanRxResult> observable = getModel().getUploadButtonStatusObservable();

        getSubscriptions().add(observable
                .subscribeOn(Schedulers.io())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<BooleanRxResult>() {
                    @Override
                    public void onNext(BooleanRxResult data) {
                        if (data != null) {
                            getViewHolder().getActionBar().findViewById(R.id.upload_button_all).setEnabled(data.getResult());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onError(AppCore.getInstance().getResources().getString(R.string.error_message_error));
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    public MySurveysAdapter getAdapter() {
        return mAdapter;
    }

    private void uploadSurvey() {
        Realm realm = null;

        try {
            realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

            if (realm != null && !realm.isClosed()) {
                if (mAdapter != null && mAdapter.getChildList() != null && mAdapter.getChildList().size() > 0) {
                    for (IChild cItem : mAdapter.getChildList()) {
                        if (cItem instanceof Children) {
                            List<Survey> surveys = realm.where(Survey.class).equalTo(Survey.FILED_CHILD_ID, ((Children) cItem).getId()).findAll();

                            if (surveys != null && surveys.size() > 0) {
                                for (Survey sItem : surveys) {
                                    SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, sItem.getId()).findFirst();

                                    if (surveyStatus != null && !(surveyStatus.isNeedUpload()
                                            || surveyStatus.isUploaded()
                                            || surveyStatus.isRemote()
                                            || surveyStatus.isInProgress())) {
                                        realm.beginTransaction();
                                        surveyStatus.setRemote(false);
                                        surveyStatus.setNeedUpload(true);
                                        surveyStatus.setNeedDownload(false);
                                        surveyStatus.setUploaded(false);
                                        surveyStatus.setUploadProgress(0);
                                        realm.commitTransaction();
                                    }
                                }
                            }
                        }
                    }

                    getViewHolder().getActionBar().findViewById(R.id.upload_button_all).setEnabled(false);
                }
            }
        } catch (Exception e) {
            TLog.e(TAG, e);
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        UploadService.checkUpload(AppCore.getInstance());
    }
}
