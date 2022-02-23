package com.reading.start.tests.test_motor_following.ui.fragments;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.gson.Gson;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.TestsProvider;
import com.reading.start.tests.test_motor_following.Constants;
import com.reading.start.tests.test_motor_following.MotorFollowingTest;
import com.reading.start.tests.test_motor_following.R;
import com.reading.start.tests.test_motor_following.data.DataProvider;
import com.reading.start.tests.test_motor_following.databinding.TestMotorFollowingFragmentTestBinding;
import com.reading.start.tests.test_motor_following.domain.entity.MotorFollowingTestSurveyAttachments;
import com.reading.start.tests.test_motor_following.domain.entity.MotorFollowingTestSurveyResult;
import com.reading.start.tests.test_motor_following.domain.entity.TestData;
import com.reading.start.tests.test_motor_following.domain.entity.TestDataAttempt;
import com.reading.start.tests.test_motor_following.domain.entity.TestDataItem;
import com.reading.start.tests.test_motor_following.ui.activities.MainActivity;
import com.reading.start.tests.test_motor_following.ui.dialogs.DialogCompleteTest;
import com.reading.start.tests.test_motor_following.ui.views.CompleteTriggerView;
import com.reading.start.tests.test_motor_following.ui.views.MotorFollowingView;
import com.reading.start.tests.test_motor_following.utils.BitmapUtils;
import com.reading.start.tests.test_motor_following.utils.PositionManager;
import com.reading.start.tests.test_motor_following.utils.SoundManager;
import com.reading.start.tests.test_motor_following.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;

public class MotorFollowingTestFragment extends BaseFragment {
    private static final String TAG = MotorFollowingTestFragment.class.getSimpleName();

    private TestMotorFollowingFragmentTestBinding mBinding;

    private boolean mInProgress = false;

    private boolean mSaving = false;

    private boolean mInit = false;

    private TestData mTestData = null;

    private PositionManager mPositionManager;

    private Handler mHandler = new Handler();

    private TestTimer mTestTimer = null;

    private int mAttempts = 0;

    private TestDataAttempt mAttemptData = null;

    private ArrayList<String> mAttemptsBitmap = new ArrayList<>();

    private boolean mInterrupted = false;

    private boolean mLeftToRight = true;

    private boolean mIsToastActive = false;

    private boolean mIsEnd = false;

    private SoundManager mSoundManager;

    public MotorFollowingTestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_motor_following_fragment_test, container, false);
        mSoundManager = SoundManager.getInstance(getMainActivity().getApplicationContext());

        mBinding.completeTriggerView.setListener(new CompleteTriggerView.CompleteTriggerViewListener() {
            @Override
            public void onCompleteTrigger() {
                if (!mInterrupted) {
                    mInterrupted = true;
                    mTestData.setInterrupted(true);
                    onTestCompleted();
                }
            }

            @Override
            public void onTouchChanged(float x, float y, float z) {
                // not need to implement
            }

            @Override
            public boolean onCountFingerChanged(int count) {
                if (count >= 2) {
                    if (!mIsToastActive) {
                        mIsToastActive = true;
                        Toast.makeText(getActivity(), getString(R.string.test_motor_following_fingers_error), Toast.LENGTH_SHORT).show();
                        mHandler.postDelayed(() -> mIsToastActive = false, 2000);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        mBinding.motorFollowingView.setListener(new MotorFollowingView.MotorFollowingViewListener() {
            @Override
            public void onStart(long time, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize) {
                addMoveTestDate(time, beeX, beeY, touchX, touchY, touchPressure, touchSize);
            }

            @Override
            public void onProgressChanged(long time, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize) {
                addMoveTestDate(time, beeX, beeY, touchX, touchY, touchPressure, touchSize);

                TestLog.e(TAG, "touchX = " + touchX + ", touchY = " + touchY);
            }

            @Override
            public void onEnd(long time, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize) {
                addEndTestDate(time, beeX, beeY, touchX, touchY, touchPressure, touchSize);

                mTestTimer = new TestTimer(() -> {
                    onTestCompleted();
                });

                mHandler.postDelayed(mTestTimer, Constants.TEST_DURATION);
            }

            @Override
            public void onBeeCaught(long time, boolean inProgress, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize) {
                addCaughtTestDate(time, beeX, beeY, touchX, touchY, touchPressure, touchSize);
            }

            @Override
            public void onAnimationStart() {
                if (mSoundManager != null) {
                    mSoundManager.playSound1();
                }
            }

            @Override
            public void onAnimationCompleted() {
                if (mTestTimer != null) {
                    mTestTimer.needProcess = false;
                    mTestTimer = null;
                    onTestCompleted();
                }
            }
        });

        ViewTreeObserver treeObserver = mBinding.motorFollowingView.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                ViewTreeObserver treeObserver = mBinding.motorFollowingView.getViewTreeObserver();

                if (treeObserver.isAlive()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        treeObserver.removeOnGlobalLayoutListener(this);
                    } else {
                        //noinspection deprecation
                        treeObserver.removeGlobalOnLayoutListener(this);
                    }
                }

                if (!mInit) {
                    mBinding.motorFollowingView.initView(mLeftToRight);
                    mInit = true;
                    startTest();
                }
            }
        });

        Size screenSize = Utility.getDisplaySize(getActivity());
        float dpi[] = Utility.getXYDpi(getActivity());
        mTestData = new TestData(screenSize.getWidth(), screenSize.getHeight(), dpi[0], dpi[1]);
        mAttemptData = new TestDataAttempt();
        mTestData.getAttempts().add(mAttemptData);
        mAttempts = 1;

        mPositionManager = new PositionManager(getActivity());
        mPositionManager.enable();

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mInit) {
            startTest();
        }
    }

    @Override
    public void onPause() {
        if (!mInterrupted) {
            mInterrupted = true;
            mTestData.setInterrupted(true);
            onTestCompleted();
        }

        stopTest();
        super.onPause();
    }

    private void startTest() {
        if (!mInProgress) {
            mInProgress = true;
            mInterrupted = false;

            Thread thread = new Thread(() -> {
                while (mInProgress) {
                    runOnUiThread(() -> {
                        mBinding.motorFollowingView.move();
                    });

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        TestLog.e(TAG, e);
                    }
                }
            });

            thread.start();

            if (mTestTimer != null) {
                mTestTimer.needProcess = false;
            }
        }
    }

    private void stopTest() {
        if (mInProgress) {
            mInProgress = false;
            mPositionManager.disable();

            if (mTestTimer != null) {
                mTestTimer.needProcess = false;
                mTestTimer = null;
            }
        }
    }

    private void addMoveTestDate(long time, float beeX, float beeY, float x, float y, float touchPressure, float touchSize) {
        String action = "";

        if (mAttemptData != null) {
            TestDataItem dataItem = new TestDataItem((int) beeX, (int) beeY, (int) x, (int) y, touchPressure, touchSize, time, action,
                    mPositionManager.getX(), mPositionManager.getY(), mPositionManager.getZ());
            mAttemptData.getItems().add(dataItem);
        }
    }

    private void addCaughtTestDate(long time, float beeX, float beeY, float x, float y, float touchPressure, float touchSize) {
        String action = Constants.ACTION_CAUGHT;

        if (mAttemptData != null) {
            TestDataItem dataItem = new TestDataItem((int) beeX, (int) beeY, (int) x, (int) y, touchPressure, touchSize, time, action,
                    mPositionManager.getX(), mPositionManager.getY(), mPositionManager.getZ());
            mAttemptData.getItems().add(dataItem);
        }
    }

    private void addEndTestDate(long time, float beeX, float beeY, float x, float y, float touchPressure, float touchSize) {
        String action = Constants.ACTION_END;

        if (mAttemptData != null) {
            TestDataItem dataItem = new TestDataItem((int) beeX, (int) beeY, (int) x, (int) y, touchPressure, touchSize, time, action,
                    mPositionManager.getX(), mPositionManager.getY(), mPositionManager.getZ());
            mAttemptData.getItems().add(dataItem);
        }
    }

    private synchronized void onTestCompleted() {
        if (!mIsEnd) {
            stopTest();
            String bitmapBase64 = resultImageToBase64();

            if (bitmapBase64 != null) {
                mAttemptsBitmap.add(bitmapBase64);
            } else {
                mAttemptsBitmap.add("");
            }

            if (!mInterrupted && mAttempts < Constants.ATTEMPT_COUNT) {
                mAttemptData = new TestDataAttempt();
                mTestData.getAttempts().add(mAttemptData);
                mAttempts++;
                mLeftToRight = !mLeftToRight;

                mHandler.postDelayed(() -> {
                    mBinding.motorFollowingView.initView(mLeftToRight);
                    mBinding.motorFollowingView.reset(mLeftToRight);
                    startTest();
                }, Constants.TEST_DELAY);
            } else {
                showEndTestDialog();
            }
        }
    }

    private void showEndTestDialog() {
        mIsEnd = true;
        MainActivity activity = getMainActivity();

        if (activity != null) {
            int attempt = saveTestResult();
            boolean showNewAttempt = attempt < com.reading.start.tests.Constants.ATTEMPT_COUNT;
            boolean showNextStep = !TestsProvider.getInstance(getActivity()).isLastTest(MotorFollowingTest.getInstance());

            String title = mTestData.isInterrupted() ? getResources().getString(R.string.test_motor_following_dialog_interrupted_title)
                    : getResources().getString(R.string.test_motor_following_dialog_success_title);
            String message = mTestData.isInterrupted() ? getResources().getString(R.string.test_motor_following_dialog_interrupted_message)
                    : getResources().getString(R.string.test_motor_following_dialog_success_message);

            DialogCompleteTest dialog = DialogCompleteTest.getInstance(title, message, showNewAttempt, showNextStep,
                    new DialogCompleteTest.DialogListener() {
                        @Override
                        public void onBack() {
                            activity.finishAsOpenChildTest();
                        }

                        @Override
                        public void onNext() {
                            activity.finishAsStartNext();
                        }

                        @Override
                        public void onAddNew() {
                            activity.finishAsTryAgain();
                        }
                    });

            dialog.setCancelable(false);
            dialog.show(getFragmentManager(), TAG);
        }
    }

    private int saveTestResult() {
        int attempt = 0;
        Realm realm = null;

        try {
            if (!mSaving) {
                mSaving = true;
                final MainActivity activity = getMainActivity();
                realm = DataProvider.getInstance(getActivity()).getRealm();

                if (activity != null) {
                    if (realm != null && !realm.isClosed()) {
                        mTestData.setEndTime(Calendar.getInstance().getTimeInMillis());
                        mTestData.setDeviceId(Utility.getDeviceId(activity));
                        MotorFollowingTestSurveyResult result = new MotorFollowingTestSurveyResult();
                        result.setSurveyId(activity.getSurveyId());
                        Gson gson = new Gson();
                        String testValue = gson.toJson(mTestData);
                        result.setResultFiles(testValue);
                        result.setTestRefId(MotorFollowingTest.TYPE);
                        result.setStartTime(mTestData.getStartTime());
                        result.setEndTime(mTestData.getEndTime());

                        try {
                            realm.beginTransaction();
                            Number currentId = realm.where(MotorFollowingTestSurveyResult.class).max(MotorFollowingTestSurveyResult.FILED_ID);
                            Number currentAttachmentsId = realm.where(MotorFollowingTestSurveyAttachments.class).max(MotorFollowingTestSurveyAttachments.FILED_ID);
                            int nextId;
                            int nextAttachmentsId;

                            if (currentId == null) {
                                nextId = 0;
                            } else {
                                nextId = currentId.intValue() + 1;
                            }

                            if (currentAttachmentsId == null) {
                                nextAttachmentsId = 0;
                            } else {
                                nextAttachmentsId = currentAttachmentsId.intValue() + 1;
                            }

                            result.setId(nextId);
                            realm.copyToRealmOrUpdate(result);

                            if (mAttemptsBitmap != null && mAttemptsBitmap.size() > 0) {
                                int attachmentId = 0;

                                for (String bitmap : mAttemptsBitmap) {
                                    MotorFollowingTestSurveyAttachments attachments = new MotorFollowingTestSurveyAttachments();
                                    attachments.setId(nextAttachmentsId + attachmentId);
                                    attachments.setSurveyId(activity.getSurveyId());
                                    attachments.setSurveyResultId(nextId);
                                    attachments.setAttachmentFile(bitmap);
                                    realm.copyToRealmOrUpdate(attachments);
                                    attachmentId++;
                                }
                            }

                            realm.commitTransaction();
                            attempt = (int) realm.where(MotorFollowingTestSurveyResult.class)
                                    .equalTo(MotorFollowingTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).count();
                        } catch (Exception e) {
                            TestLog.e(TAG, e);
                            realm.cancelTransaction();
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return attempt;
    }

    private String resultImageToBase64() {
        String result = null;

        try {
            if (mBinding.motorFollowingView.getBitmapResult() != null) {
                result = BitmapUtils.bitmapToBase64(mBinding.motorFollowingView.getBitmapResult());
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }

    private static class TestTimer implements Runnable {
        public boolean needProcess = true;

        private Runnable mLocalRun = null;

        public TestTimer(Runnable runnable) {
            mLocalRun = runnable;
        }

        @Override
        public void run() {
            if (needProcess && mLocalRun != null) {
                mLocalRun.run();
            }
        }
    }
}
