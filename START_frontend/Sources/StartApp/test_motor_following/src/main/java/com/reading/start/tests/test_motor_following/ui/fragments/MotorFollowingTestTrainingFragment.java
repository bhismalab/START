package com.reading.start.tests.test_motor_following.ui.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_motor_following.Constants;
import com.reading.start.tests.test_motor_following.R;
import com.reading.start.tests.test_motor_following.databinding.TestMotorFollowingFragmentTrainingTestBinding;
import com.reading.start.tests.test_motor_following.ui.activities.MainActivity;
import com.reading.start.tests.test_motor_following.ui.views.MotorFollowingView;
import com.reading.start.tests.test_motor_following.utils.SoundManager;

public class MotorFollowingTestTrainingFragment extends BaseFragment {
    private static final String TAG = MotorFollowingTestTrainingFragment.class.getSimpleName();

    private TestMotorFollowingFragmentTrainingTestBinding mBinding;

    private boolean mInit = false;

    private boolean mInProgress = false;

    private Handler mHandler = new Handler();

    private TestTimer mTestTimer = null;

    private boolean mLeftToRight = true;

    private SoundManager mSoundManager;

    public MotorFollowingTestTrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_motor_following_fragment_training_test, container, false);
        mSoundManager = SoundManager.getInstance(getMainActivity().getApplicationContext());

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);

        mBinding.actionBar.findViewById(R.id.back_button).setOnClickListener(v -> {
            final Activity activity = getActivity();

            if (activity != null) {
                activity.onBackPressed();
            }
        });

        mBinding.buttonStart.setEnabled(false);
        TestTimer autoStart = new TestTimer(() -> mBinding.buttonStart.setEnabled(true));
        mBinding.buttonStart.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openTestFragment();
        });

        mBinding.motorFollowingView.setListener(new MotorFollowingView.MotorFollowingViewListener() {
            @Override
            public void onStart(long time, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize) {
            }

            @Override
            public void onProgressChanged(long time, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize) {
            }

            @Override
            public void onEnd(long time, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize) {
                mTestTimer = new TestTimer(() -> {
                    mLeftToRight = !mLeftToRight;
                    mBinding.motorFollowingView.initView(mLeftToRight);
                    mBinding.motorFollowingView.reset(mLeftToRight);
                });

                mHandler.postDelayed(mTestTimer, Constants.TEST_DURATION);
            }

            @Override
            public void onBeeCaught(long time, boolean inProgress, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize) {
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
                    mLeftToRight = !mLeftToRight;
                    mBinding.motorFollowingView.initView(mLeftToRight);
                    mBinding.motorFollowingView.reset(mLeftToRight);
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

        mHandler.postDelayed(autoStart, Constants.AUTO_START_TEST);
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
        super.onPause();
        stopTest();
    }

    private void startTest() {
        if (!mInProgress) {
            mInProgress = true;

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
        }
    }

    private void stopTest() {
        if (mInProgress) {
            mInProgress = false;
        }
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
