package com.reading.start.tests.test_choice_touching.ui.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.VideoView;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_choice_touching.Constants;
import com.reading.start.tests.test_choice_touching.Preferences;
import com.reading.start.tests.test_choice_touching.R;
import com.reading.start.tests.test_choice_touching.data.DataProvider;
import com.reading.start.tests.test_choice_touching.databinding.TestChoiceTouchingFragmentTrainingTestBinding;
import com.reading.start.tests.test_choice_touching.domain.entity.ChoiceTouchingTestContent;
import com.reading.start.tests.test_choice_touching.domain.entity.ColorButton;
import com.reading.start.tests.test_choice_touching.ui.activities.MainActivity;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;

public class ChoiceTouchingTestTrainingFragment extends BaseFragment {

    private static final String TAG = ChoiceTouchingTestTrainingFragment.class.getSimpleName();

    private TestChoiceTouchingFragmentTrainingTestBinding mBinding;

    private boolean mButtonsInit = false;

    private ArrayList<ColorButton> mButtonList = new ArrayList<>();

    private int mCurrentIteration = 0;

    private int mCountIteration = Constants.COUNT_ANIMATION_BUTTONS;

    private int mAttempt = 0;

    private Handler mHandler = new Handler();

    private ColorButton.ColorButtonListener mGreenBubbleListener = bubble -> {
        mAttempt++;
        onPlayVideo(getGreenVideo());
    };

    private ColorButton.ColorButtonListener mRedBubbleListener = bubble -> {
        mAttempt++;
        onPlayVideo(getRedVideo());
    };

    public ChoiceTouchingTestTrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_choice_touching_fragment_training_test, container, false);

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

        ViewTreeObserver treeObserver = mBinding.buttonsContainer.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                ViewTreeObserver treeObserver = mBinding.buttonsContainer.getViewTreeObserver();

                if (treeObserver.isAlive()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        treeObserver.removeOnGlobalLayoutListener(this);
                    } else {
                        //noinspection deprecation
                        treeObserver.removeGlobalOnLayoutListener(this);
                    }
                }

                if (!mButtonsInit) {
                    initBubbles();
                }
            }
        });

        mHandler.postDelayed(autoStart, Constants.AUTO_START_TEST);
        return mBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mButtonsInit) {
            // start test
        }
    }

    private void initBubbles() {
        mButtonList.clear();
        RectF bound = new RectF(0, 0, mBinding.buttonsContainer.getMeasuredWidth(), mBinding.buttonsContainer.getMeasuredHeight());
        float bubbleSize = mBinding.buttonGreen.getMeasuredWidth();

        float x = bound.width() / 4 - bubbleSize / 2;
        float y = bound.height() / 2 - bubbleSize / 2;

        mButtonList.add(new ColorButton(mBinding.buttonGreen, x, y, bound, mGreenBubbleListener));
        x = bound.width() / 2 + bound.width() / 4 - bubbleSize / 2;
        y = bound.height() / 2 - bubbleSize / 2;
        mButtonList.add(new ColorButton(mBinding.buttonRed, x, y, bound, mRedBubbleListener));
        mButtonsInit = true;
        showButtons();
    }

    private void moveToNewPosition() {
        mCurrentIteration = 0;

        Thread thread = new Thread(() -> {
            while (mCurrentIteration < mCountIteration) {
                mCurrentIteration++;

                runOnUiThread(() -> {
                    for (ColorButton item : mButtonList) {
                        item.move();
                    }
                });

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    TestLog.e(TAG, e);
                }
            }

            runOnUiThread(() -> {
                if (mButtonList != null && mButtonList.size() > 0) {
                    boolean intersect = true;

                    while (intersect) {
                        for (ColorButton item : mButtonList) {
                            item.moveToNextRandomPosition();
                        }

                        intersect = mButtonList.get(0).isIntersect(mButtonList.get(1));
                    }
                }
            });
        });

        thread.start();
    }

    private void showButtons() {
        if (mButtonList != null && mButtonList.size() > 0) {
            for (ColorButton item : mButtonList) {
                item.show();
            }
        }
    }

    private void onPlayVideo(Uri video) {
        if (video != null) {
            final VideoView videoView = mBinding.videoView;

            if (!videoView.isPlaying()) {
                videoView.setVisibility(View.VISIBLE);
                mBinding.videoViewHolder.setVisibility(View.VISIBLE);
                videoView.setVideoURI(video);
                videoView.setOnCompletionListener(mp -> {
                    videoView.setVisibility(View.GONE);
                    mBinding.videoViewHolder.setVisibility(View.GONE);
                    moveToNewPosition();
                });
                videoView.start();
            }
        }
    }

    public Uri getGreenVideo() {
        Uri result = null;
        Realm realm = null;

        try {
            Preferences pref = new Preferences(getActivity());
            realm = DataProvider.getInstance(getActivity()).getRealm();
            ChoiceTouchingTestContent content = realm.where(ChoiceTouchingTestContent.class).findFirst();

            if (content != null && content.getVideoDemo_social() != null) {
                File file = pref.isGreenVideo() ? new File(content.getVideoDemo_social()) : new File(content.getVideoDemo_no_social());

                if (file != null && file.exists()) {
                    result = Uri.fromFile(file);
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return result;
    }

    public Uri getRedVideo() {
        Uri result = null;
        Realm realm = null;

        try {
            Preferences pref = new Preferences(getActivity());
            realm = DataProvider.getInstance(getActivity()).getRealm();
            ChoiceTouchingTestContent content = realm.where(ChoiceTouchingTestContent.class).findFirst();

            if (content != null && content.getVideoDemo_no_social() != null) {
                File file = pref.isGreenVideo() ? new File(content.getVideoDemo_no_social()) : new File(content.getVideoDemo_social());

                if (file != null && file.exists()) {
                    result = Uri.fromFile(file);
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
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
