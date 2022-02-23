package com.reading.start.tests.test_wheel.ui.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_wheel.Constants;
import com.reading.start.tests.test_wheel.R;
import com.reading.start.tests.test_wheel.data.DataProvider;
import com.reading.start.tests.test_wheel.databinding.TestWheelFragmentTrainingTestBinding;
import com.reading.start.tests.test_wheel.domain.entity.WheelTestContent;
import com.reading.start.tests.test_wheel.ui.activities.MainActivity;

import java.io.File;

import io.realm.Realm;

public class WheelTestTrainingFragment extends BaseFragment {

    private static final String TAG = WheelTestTrainingFragment.class.getSimpleName();

    private TestWheelFragmentTrainingTestBinding mBinding;

    private int mCurrentAttempt = 0;

    private boolean mIsButtonTouch = false;

    private boolean mIsToastActive = false;

    private Handler mHandler = null;

    private Uri mVideoFile = null;

    private CancelRunnable mCancelRunnable = null;

    private Object mSync = new Object();

    public WheelTestTrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_wheel_fragment_training_test, container, false);
        mHandler = new Handler();

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

        mBinding.actionButton.setOnClickListener(v -> {
            synchronized (mSync) {
                if (mCancelRunnable != null) {
                    mCancelRunnable.isCancel = true;
                    mCancelRunnable = null;
                }

                showBlackScreen();
            }
        });

        mHandler.postDelayed(autoStart, Constants.AUTO_START_TEST);
        return mBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        fillVideoFiles();
        moveToNextPosition();
    }

    private void onPlayVideo(Uri video) {
        if (video != null) {
            final VideoView videoView = mBinding.videoView;

            if (!videoView.isPlaying()) {
                videoView.setVideoURI(video);
                videoView.setOnPreparedListener(mp -> {
                    if (mp != null) {
                        mp.setLooping(true);
                    }
                });
                videoView.setOnCompletionListener(mp -> {
                });
                videoView.start();
            }
        }
    }

    private void moveToNextPosition() {
        mBinding.videoView.setVisibility(View.VISIBLE);
        mBinding.actionButton.setVisibility(View.VISIBLE);
        onPlayVideo(mVideoFile);

        mCancelRunnable = new CancelRunnable(() -> {
            synchronized (mSync) {
                if (mCancelRunnable != null) {
                    mCancelRunnable.isCancel = true;
                    mCancelRunnable = null;
                }

                showBlackScreen();
            }
        });

        mHandler.postDelayed(mCancelRunnable, Constants.VIDEO_TIMEOUT);
    }

    private void fillVideoFiles() {
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(getActivity()).getRealm();
            WheelTestContent content = realm.where(WheelTestContent.class).findFirst();
            mVideoFile = Uri.fromFile(new File(content.getVideo()));
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void showBlackScreen() {
        mCurrentAttempt++;

        mBinding.videoView.stopPlayback();
        mBinding.videoView.setVisibility(View.INVISIBLE);
        mBinding.actionButton.setVisibility(View.INVISIBLE);
        mHandler.postDelayed(() -> {
            moveToNextPosition();
        }, Constants.BLACK_SCREEN_DELAY);
    }


    private class CancelRunnable implements Runnable {
        boolean isCancel = false;

        private Runnable mRunnable;

        CancelRunnable(Runnable runnable) {
            mRunnable = runnable;
        }

        @Override
        public void run() {
            if (!isCancel) {
                mRunnable.run();
            }
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
