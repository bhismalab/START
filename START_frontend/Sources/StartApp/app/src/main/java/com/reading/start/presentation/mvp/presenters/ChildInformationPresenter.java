package com.reading.start.presentation.mvp.presenters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.reading.start.AppCore;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.CurrentScreen;
import com.reading.start.domain.entity.ISurvey;
import com.reading.start.domain.entity.Survey;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.holders.ChildInformationViewHolder;
import com.reading.start.presentation.mvp.models.ChildInformationModel;
import com.reading.start.presentation.mvp.views.ChildInformationView;
import com.reading.start.presentation.receivers.DownloadBroadcastReceiver;
import com.reading.start.presentation.receivers.UploadBroadcastReceiver;
import com.reading.start.presentation.services.DownloadService;
import com.reading.start.presentation.ui.adapters.ChildInformationSurveysAdapter;
import com.reading.start.utils.BitmapUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;

public class ChildInformationPresenter extends BasePresenter<ChildInformationView, ChildInformationModel, ChildInformationViewHolder> {

    public static final String TAG = ChildInformationPresenter.class.getSimpleName();

    private ChildInformationSurveysAdapter mAdapter;

    private SimpleDateFormat mDateFormatter;

    private String mWorker = null;

    private Context mContext;

    private boolean mIsRestore = false;

    private ChildInformationSurveysAdapter.OnSurveysItemClickListener mOnItemClickListener =
            new ChildInformationSurveysAdapter.OnSurveysItemClickListener() {
                @Override
                public void onFinishedSurveyItemClick(Survey survey, int position) {
                    Realm realm = null;

                    try {
                        realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                        if (realm != null && !realm.isClosed()) {
                            SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, survey.getId()).findFirst();

                            if (surveyStatus.isRemote()) {
                                if (!surveyStatus.isInProgress() && !surveyStatus.isNeedDownload()) {
                                    getView().onSurveyDownload(survey, position + 1);
                                }
                            } else {
                                getView().onSurveyOpen(false, survey, position + 1);
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

                @Override
                public void onAddSurveyItemClick() {
                    processAddSurveyPressed();
                }
            };

    private int mChildId = -1;

    private UploadBroadcastReceiver mUploadBroadcastReceiver = null;

    private DownloadBroadcastReceiver mDownloadBroadcastReceiver = null;

    public ChildInformationPresenter(Context context, int childId, boolean restore) {
        super(true);
        setModel(new ChildInformationModel(getRealm()));
        mContext = context;
        mChildId = childId;
        mDateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT_CHILD, Locale.getDefault());
        mWorker = AppCore.getInstance().getPreferences().getLoginWorker();
        mIsRestore = restore;
    }

    @Override
    public void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getViewHolder() != null) {
            RxView.clicks(getViewHolder().getEdit())
                    .subscribe(aVoid -> getView().onEditChild());
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.back_button))
                    .subscribe(aVoid -> getView().onBackPressed());
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.home_button))
                    .subscribe(aVoid -> getView().onHomePressed());
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.navigation_button))
                    .subscribe(aVoid -> getView().onNavigationMenuPressed());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateChildInfo();
        processLoadingItems();

        if (mIsRestore) {
            if (!openSurveyInfo()) {
                AppCore.getInstance().getPreferences().setCurrentScreen(CurrentScreen.ChildInfo);
                AppCore.getInstance().getPreferences().setCurrentChild(mChildId);
            }
        } else {
            AppCore.getInstance().getPreferences().setCurrentScreen(CurrentScreen.ChildInfo);
            AppCore.getInstance().getPreferences().setCurrentChild(mChildId);
        }

        mUploadBroadcastReceiver = new UploadBroadcastReceiver() {
            @Override
            public void onUploadStart(int surveyId) {
                updateSurvey(surveyId);
            }

            @Override
            public void onUploadProgress(int surveyId, int progress) {
                updateSurvey(surveyId);
            }

            @Override
            public void onUploadCompleted(int surveyId) {
                updateSurvey(surveyId);
            }

            @Override
            public void onUploadError(int surveyId, String message) {
                updateSurvey(surveyId);

                if (message != null) {
                    getView().onError(message);
                }
            }
        };

        mDownloadBroadcastReceiver = new DownloadBroadcastReceiver() {
            @Override
            public void onDownloadStart(int surveyId) {
                updateSurvey(surveyId);
            }

            @Override
            public void onDownloadProgress(int surveyId, int progress) {
                updateSurvey(surveyId);
            }

            @Override
            public void onDownloadCompleted(int surveyId) {
                updateSurvey(surveyId);
            }
        };

        if (mUploadBroadcastReceiver != null) {
            mUploadBroadcastReceiver.register(mContext);
        }

        if (mDownloadBroadcastReceiver != null) {
            mDownloadBroadcastReceiver.register(mContext);
        }
    }

    @Override
    public void onPause() {
        if (mUploadBroadcastReceiver != null) {
            mUploadBroadcastReceiver.unregister(mContext);
        }

        if (mDownloadBroadcastReceiver != null) {
            mDownloadBroadcastReceiver.unregister(mContext);
        }

        super.onPause();
    }

    public void downloadSurvey(int surveyId) {
        Realm realm = null;

        try {
            realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

            if (realm != null && !realm.isClosed()) {
                SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyId).findFirst();

                if (surveyStatus != null) {
                    realm.beginTransaction();
                    surveyStatus.setNeedDownload(true);
                    surveyStatus.setUploadProgress(0);
                    realm.commitTransaction();
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

        DownloadService.startDownload(AppCore.getInstance(), surveyId);
    }

    private void updateChildInfo() {
        final Observable<Children> observable = getModel().getChildObservable(mChildId);

        getSubscriptions().add(observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Children>() {
                    @Override
                    public void onNext(Children data) {
                        if (data != null) {
                            getViewHolder().getName().setText(data.getName() + " " + data.getSurname());
                            Bitmap bitmap = BitmapUtils.bitmapFromBase64(data.getPhoto());

                            if (bitmap != null) {
                                getViewHolder().getPhoto().setScaleType(ImageView.ScaleType.CENTER_CROP);
                                getViewHolder().getPhoto().setImageBitmap(bitmap);
                            }

                            String dateValue = mDateFormatter.format(new Date(data.getBirthDate()));
                            String childValue = String.format(AppCore.getInstance().getString(R.string.child_info_pattern),
                                    dateValue);
                            getViewHolder().getChildInfo().setText(childValue);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onError(null);
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    private void processLoadingItems() {
        final Observable<ArrayList<ISurvey>> observable = getModel().getSurveysListObservable(mChildId);

        getSubscriptions().add(observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<ISurvey>>() {
                    @Override
                    public void onNext(ArrayList<ISurvey> data) {
                        if (data != null) {
                            mAdapter = new ChildInformationSurveysAdapter(data, mOnItemClickListener);
                            getViewHolder().getRecyclerView().setAdapter(mAdapter);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onError(null);
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    private void processAddSurveyPressed() {
        final Observable<Survey> observable = getModel().getCreateSurveyObservable(mChildId, mWorker);

        getSubscriptions().add(observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Survey>() {
                    @Override
                    public void onNext(Survey survey) {
                        getView().onSurveyAdd(survey, mAdapter.getItemCount());
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    private void updateSurvey(int surveyId) {
        if (mAdapter != null && mAdapter.isExistSurvey(surveyId)) {
            final Observable<Survey> observable = getModel().getSurveyObservable(surveyId);

            getSubscriptions().add(observable
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Survey>() {
                        @Override
                        public void onNext(Survey survey) {
                            if (survey != null && mAdapter != null) {
                                mAdapter.updateSurvey(surveyId, survey);
                            }
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

    private boolean openSurveyInfo() {
        boolean result = false;
        CurrentScreen screen = AppCore.getInstance().getPreferences().getCurrentScreen();

        if (screen != null && screen == CurrentScreen.SurveyInfo) {
            Realm realm = null;

            try {
                realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                if (realm != null && !realm.isClosed()) {
                    List<Survey> sResult = realm.where(Survey.class).equalTo(Survey.FILED_CHILD_ID, AppCore.getInstance().getPreferences().getCurrentChild()).findAll();
                    Survey survey = null;
                    // TODO: need calculate position
                    int position = 0;

                    if (sResult != null && sResult.size() > 0) {
                        for (Survey sItem : sResult) {
                            position++;

                            if (sItem.getId() == AppCore.getInstance().getPreferences().getCurrentSurvey()) {
                                survey = sItem;
                                break;
                            }
                        }
                    }

                    if (survey != null) {
                        SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, AppCore.getInstance().getPreferences().getCurrentSurvey()).findFirst();

                        if (surveyStatus != null && !surveyStatus.isRemote()) {
                            getView().onSurveyOpen(true, survey, position);
                            result = true;
                        }
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
}
