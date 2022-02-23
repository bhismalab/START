package com.reading.start.presentation.mvp.presenters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.reading.start.AppCore;
import com.reading.start.BuildConfig;
import com.reading.start.Constants;
import com.reading.start.R;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.domain.entity.Children;
import com.reading.start.domain.entity.SurveyStatus;
import com.reading.start.general.TLog;
import com.reading.start.presentation.mvp.holders.SurveyViewHolder;
import com.reading.start.presentation.mvp.models.SurveyModel;
import com.reading.start.presentation.mvp.views.SurveyView;
import com.reading.start.presentation.receivers.UploadBroadcastReceiver;
import com.reading.start.presentation.services.UploadService;
import com.reading.start.presentation.ui.adapters.SurveyTestAdapter;
import com.reading.start.tests.ITestModule;
import com.reading.start.tests.ITestModuleResult;
import com.reading.start.utils.BitmapUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class SurveyPresenter extends BasePresenter<SurveyView, SurveyModel, SurveyViewHolder> {
    public static final String TAG = SurveyPresenter.class.getSimpleName();

    private SurveyTestAdapter mAdapter;

    private int mChildId = -1;

    private int mSurveyId = -1;

    private int mSurveyNumber = 1;

    private SimpleDateFormat mDateFormatter;

    private Context mContext;

    private UploadBroadcastReceiver mUploadBroadcastReceiver = null;

    private int mScrollY = 0;

    private SurveyTestAdapter.SurveyTestAdapterListener mAdapterListener = new SurveyTestAdapter.SurveyTestAdapterListener() {
        @Override
        public void onAttempt(ITestModule module, int attempt) {
            mScrollY = getViewHolder().getRecyclerView().computeVerticalScrollOffset();

            if (getView() != null) {
                getView().onOpenSurvey(module, attempt);
            }
        }

        @Override
        public void onAddAttempt(ITestModule module) {
            mScrollY = getViewHolder().getRecyclerView().computeVerticalScrollOffset();

            if (getView() != null) {
                getView().onStartSurvey(module);
            }
        }
    };

    public SurveyPresenter(Context context, int childId, int surveyId, int surveyNumber) {
        super(true);
        setModel(new SurveyModel(getRealm()));
        mContext = context;
        mChildId = childId;
        mSurveyId = surveyId;
        mSurveyNumber = surveyNumber;
        mDateFormatter = new SimpleDateFormat(Constants.DATE_FORMAT_CHILD, Locale.getDefault());
    }

    @Override
    public void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getViewHolder() != null) {
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.back_button))
                    .subscribe(aVoid -> getView().onBackPressed());
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.home_button))
                    .subscribe(aVoid -> getView().onHomePressed());
            RxView.clicks(getViewHolder().getActionBar().findViewById(R.id.navigation_button))
                    .subscribe(aVoid -> getView().onNavigationMenuPressed());

            RxView.clicks(getViewHolder().getUploadSurvey())
                    .subscribe(aVoid -> uploadSurvey());

            RxView.clicks(getViewHolder().getDeleteSurvey())
                    .subscribe(aVoid -> getView().onDeleteSurvey());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateChildInfo();
        processLoadingItems();

        mUploadBroadcastReceiver = new UploadBroadcastReceiver() {
            @Override
            public void onUploadStart(int surveyId) {
            }

            @Override
            public void onUploadProgress(int surveyId, int progress) {
            }

            @Override
            public void onUploadCompleted(int surveyId) {
                Realm realm = null;

                try {
                    realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

                    if (realm != null && !realm.isClosed()) {
                        SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, surveyId).findFirst();

                        if (surveyStatus != null) {
                            boolean enable = !surveyStatus.isNeedUpload() && !surveyStatus.isUploaded();
                            getViewHolder().getUploadSurvey().setEnabled(enable);
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
            }

            @Override
            public void onUploadError(int surveyId, String message) {
                if (mAdapter != null) {
                    updateUploadSurveyButton(mAdapter.getItems());
                } else {
                    updateUploadSurveyButton(null);
                }

                if (message != null) {
                    getView().onError(message);
                }
            }
        };

        if (mUploadBroadcastReceiver != null) {
            mUploadBroadcastReceiver.register(mContext);
        }
    }

    @Override
    public void onPause() {
        if (mUploadBroadcastReceiver != null) {
            mUploadBroadcastReceiver.unregister(mContext);
        }

        super.onPause();
    }

    public void deleteSurvey() {
        final Observable<Boolean> observable = getModel().getDeleteObservable(mSurveyId);

        getSubscriptions().add(observable
                .subscribeOn(Schedulers.io())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onDeletedSurvey();
                    }

                    @Override
                    public void onComplete() {
                        getView().onDeletedSurvey();
                    }
                }));
    }

    public void uploadSurvey() {
        Realm realm = null;

        try {
            realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

            if (realm != null && !realm.isClosed()) {
                SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, mSurveyId).findFirst();

                if (surveyStatus != null) {
                    realm.beginTransaction();
                    surveyStatus.setRemote(false);
                    surveyStatus.setNeedUpload(true);
                    surveyStatus.setNeedDownload(false);
                    surveyStatus.setUploaded(false);
                    surveyStatus.setUploadProgress(0);
                    realm.commitTransaction();

                    getViewHolder().getUploadSurvey().setEnabled(false);
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

        UploadService.startUpload(AppCore.getInstance(), mSurveyId);
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
                            TextView title = getViewHolder().getActionBar().findViewById(R.id.text_actionbar);
                            String value = String.format(AppCore.getInstance().getString(R.string.action_bar_survey),
                                    String.valueOf(mSurveyNumber));
                            title.setText(value);

                            getViewHolder().getName().setText(data.getName() + " " + data.getSurname());
                            Bitmap bitmap = BitmapUtils.bitmapFromBase64(data.getPhoto());

                            if (bitmap != null) {
                                getViewHolder().getPhoto().setScaleType(ImageView.ScaleType.CENTER_CROP);
                                getViewHolder().getPhoto().setImageBitmap(bitmap);
                            }

                            String dateValue = mDateFormatter.format(new Date(data.getBirthDate()));
                            String childValue = String.format(AppCore.getInstance().getString(R.string.child_info_pattern), dateValue);
                            getViewHolder().getChildInfo().setText(childValue);
                            getView().onChildInfoUpdated(data);
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
        if (BuildConfig.FAKE_UPLOAD) {
            getViewHolder().getUploadSurvey().setEnabled(true);
        } else {
            getViewHolder().getUploadSurvey().setEnabled(false);
        }

        final Observable<ArrayList<ITestModule>> observable = getModel().getModulesListObservable(mSurveyId);

        getSubscriptions().add(observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<ITestModule>>() {
                    @Override
                    public void onNext(ArrayList<ITestModule> data) {
                        if (data != null && data.size() > 0) {
                            mAdapter = new SurveyTestAdapter(mSurveyId, data, mAdapterListener);
                            mScrollY = getViewHolder().getRecyclerView().computeVerticalScrollOffset();
                            getViewHolder().getRecyclerView().setAdapter(mAdapter);
                            getViewHolder().getRecyclerView().scrollBy(0, mScrollY);
                            processUpdateUploadSurveyButton(data);
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

    private void processUpdateUploadSurveyButton(ArrayList<ITestModule> items) {
        final Observable<ArrayList<ITestModule>> observable = Observable.fromCallable(() -> {
            updateUploadSurveyButton(items);
            return items;
        });

        getSubscriptions().add(observable
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(Constants.DEFAULT_EXECUTE_TIME, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ArrayList<ITestModule>>() {
                    @Override
                    public void onNext(ArrayList<ITestModule> data) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    private void updateUploadSurveyButton(ArrayList<ITestModule> items) {
        boolean enable = false;
        boolean surveyUploading = false;

        Realm realm = null;

        try {
            realm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();

            if (realm != null && !realm.isClosed()) {
                SurveyStatus surveyStatus = realm.where(SurveyStatus.class).equalTo(SurveyStatus.FILED_ID_SURVEY, mSurveyId).findFirst();

                if (surveyStatus != null) {
                    // in this case need reset in progress status
                    if (surveyStatus.isInProgress() && UploadService.getUploadingSurveyId() != mSurveyId) {
                        realm.beginTransaction();
                        surveyStatus.setNeedUpload(false);
                        surveyStatus.setUploaded(false);
                        surveyStatus.setInProgress(false);
                        realm.commitTransaction();
                    }

                    surveyUploading = surveyStatus.isNeedUpload() || surveyStatus.isUploaded();
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

        if (!surveyUploading && items != null && items.size() > 0) {
            for (ITestModule item : items) {
                ArrayList<ITestModuleResult> results = item.getTestResults(mSurveyId);

                if (!item.isUploaded(mSurveyId) && results != null && results.size() > 0) {
                    enable = true;
                    break;
                }
            }

            if (BuildConfig.FAKE_UPLOAD) {
                getViewHolder().getUploadSurvey().setEnabled(true);
            } else {
                getViewHolder().getUploadSurvey().setEnabled(enable);
            }
        }
    }
}
