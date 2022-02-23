package com.reading.start.tests.test_jabble.ui.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_jabble.Constants;
import com.reading.start.tests.test_jabble.R;
import com.reading.start.tests.test_jabble.databinding.TestJabbleFragmentTrainingTestBinding;
import com.reading.start.tests.test_jabble.domain.entity.Bubble;
import com.reading.start.tests.test_jabble.ui.activities.MainActivity;
import com.reading.start.tests.test_jabble.utils.SoundManager;

import java.util.ArrayList;

public class JabbleTestTrainingFragment extends BaseFragment {

    private static final String TAG = JabbleTestTrainingFragment.class.getSimpleName();

    private TestJabbleFragmentTrainingTestBinding mBinding;

    private boolean mInProgress = false;

    private ArrayList<Bubble> mBubbleList = new ArrayList<>();

    private int mLevel = -1;

    private boolean mBubbleInit = false;

    private SoundManager mSoundManager;

    private Handler mHandler = null;

    private boolean mUpdatingLevel = false;

    public JabbleTestTrainingFragment() {
        // Required empty public constructor
    }

    private Bubble.BubbleListener mBubbleListener = new Bubble.BubbleListener() {
        @Override
        public void onClick(Bubble bubble) {
            if (!bubble.isPopped()) {
                bubble.setIsPopped(true);
                mSoundManager.playSound1();
                bubble.hideAnimation();
                checkLevel();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_jabble_fragment_training_test, container, false);
        mHandler = new Handler();

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.edit_button).setVisibility(View.GONE);

        mBinding.actionBar.findViewById(R.id.back_button).setOnClickListener(v -> {
            final Activity activity = getActivity();

            if (activity != null) {
                activity.onBackPressed();
            }
        });

        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
        title.setText(R.string.test_jabble_action_bar_text_default);

        mSoundManager = SoundManager.getInstance(getMainActivity().getApplicationContext());

        ViewTreeObserver treeObserver = mBinding.bubblesContainer.getViewTreeObserver();
        treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                ViewTreeObserver treeObserver = mBinding.bubblesContainer.getViewTreeObserver();

                if (treeObserver.isAlive()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        treeObserver.removeOnGlobalLayoutListener(this);
                    } else {
                        //noinspection deprecation
                        treeObserver.removeGlobalOnLayoutListener(this);
                    }
                }

                if (!mBubbleInit) {
                    initBubbles();
                }
            }
        });

        mBinding.buttonStart.setEnabled(false);
        TestTimer autoStart = new TestTimer(() -> mBinding.buttonStart.setEnabled(true));
        mBinding.buttonStart.setOnClickListener(v -> {
            ((MainActivity) getActivity()).openTestFragment();
        });

        mHandler.postDelayed(autoStart, Constants.AUTO_START_TEST);
        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBubbleInit) {
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
            mLevel = -1;
            mInProgress = true;
            updateLevel();

            Thread thread = new Thread(() -> {
                while (mInProgress) {
                    runOnUiThread(() -> {
                        for (Bubble item : mBubbleList) {
                            item.move();
                        }
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

    private void initBubbles() {
        mBubbleList.clear();

        float x = 0;
        float y = 0;
        float minY = 0;
        float maxY = mBinding.bubblesContainer.getMeasuredHeight();
        float maxX = mBinding.bubblesContainer.getMeasuredWidth();
        float space = 0;
        float heightSpace = mBinding.bubblesContainer.getMeasuredHeight() / 6;
        float bubbleSize = maxX / 6;

        RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) mBinding.imageBubble1.getLayoutParams();
        param.width = (int) bubbleSize;
        param.height = (int) bubbleSize;
        mBinding.imageBubble1.setLayoutParams(param);

        param = (RelativeLayout.LayoutParams) mBinding.imageBubble2.getLayoutParams();
        param.width = (int) bubbleSize;
        param.height = (int) bubbleSize;
        mBinding.imageBubble2.setLayoutParams(param);

        param = (RelativeLayout.LayoutParams) mBinding.imageBubble3.getLayoutParams();
        param.width = (int) bubbleSize;
        param.height = (int) bubbleSize;
        mBinding.imageBubble3.setLayoutParams(param);

        param = (RelativeLayout.LayoutParams) mBinding.imageBubble4.getLayoutParams();
        param.width = (int) bubbleSize;
        param.height = (int) bubbleSize;
        mBinding.imageBubble4.setLayoutParams(param);

        param = (RelativeLayout.LayoutParams) mBinding.imageBubble5.getLayoutParams();
        param.width = (int) bubbleSize;
        param.height = (int) bubbleSize;
        mBinding.imageBubble5.setLayoutParams(param);

        param = (RelativeLayout.LayoutParams) mBinding.imageBubble6.getLayoutParams();
        param.width = (int) bubbleSize;
        param.height = (int) bubbleSize;
        mBinding.imageBubble6.setLayoutParams(param);

        mBubbleList.add(new Bubble(Constants.BUBBLE_1, mBinding.imageBubble1, Constants.BUBBLES_SPEED, Bubble.BubbleDirection.Down,
                x, y, minY, maxY, mBubbleListener));
        x = 2 * space + bubbleSize;
        y = 5 * heightSpace;
        mBubbleList.add(new Bubble(Constants.BUBBLE_2, mBinding.imageBubble2, Constants.BUBBLES_SPEED, Bubble.BubbleDirection.Up,
                x, y, minY, maxY, mBubbleListener));
        x = 3 * space + 2 * bubbleSize;
        y = 3 * heightSpace;
        mBubbleList.add(new Bubble(Constants.BUBBLE_3, mBinding.imageBubble3, Constants.BUBBLES_SPEED, Bubble.BubbleDirection.Down,
                x, y, minY, maxY, mBubbleListener));
        x = 4 * space + 3 * bubbleSize;
        y = 4 * heightSpace;
        mBubbleList.add(new Bubble(Constants.BUBBLE_4, mBinding.imageBubble4, Constants.BUBBLES_SPEED, Bubble.BubbleDirection.Up,
                x, y, minY, maxY, mBubbleListener));
        x = 5 * space + 4 * bubbleSize;
        y = heightSpace;
        mBubbleList.add(new Bubble(Constants.BUBBLE_5, mBinding.imageBubble5, Constants.BUBBLES_SPEED, Bubble.BubbleDirection.Down,
                x, y, minY, maxY, mBubbleListener));
        x = 6 * space + 5 * bubbleSize;
        y = 4 * heightSpace;
        mBubbleList.add(new Bubble(Constants.BUBBLE_6, mBinding.imageBubble6, Constants.BUBBLES_SPEED, Bubble.BubbleDirection.Up,
                x, y, minY, maxY, mBubbleListener));

        mBubbleInit = true;
        startTest();
    }

    private void checkLevel() {
        if (mBubbleList != null) {
            boolean allHide = true;

            for (Bubble item : mBubbleList) {
                if (item.isVisible()) {
                    allHide = false;
                    break;
                }
            }

            if (allHide && !mUpdatingLevel) {
                mUpdatingLevel = true;
                mHandler.postDelayed(() -> {
                    updateLevel();
                    mUpdatingLevel = false;
                }, Constants.ATTEMPT_DELAY);
            }
        }
    }

    private void updateLevel() {
        if (mLevel == -1) {
            mLevel++;
            mBubbleList.get(0).hide();
            mBubbleList.get(1).hide();
            mBubbleList.get(2).show();
            mBubbleList.get(3).hide();
            mBubbleList.get(4).hide();
            mBubbleList.get(5).hide();
        } else if (mLevel == 0) {
            mLevel++;
            mBubbleList.get(0).hide();
            mBubbleList.get(1).show();
            mBubbleList.get(2).hide();
            mBubbleList.get(3).show();
            mBubbleList.get(4).hide();
            mBubbleList.get(5).hide();
        } else if (mLevel == 1) {
            mLevel++;
            mBubbleList.get(0).show();
            mBubbleList.get(1).hide();
            mBubbleList.get(2).show();
            mBubbleList.get(3).hide();
            mBubbleList.get(4).show();
            mBubbleList.get(5).hide();
        } else if (mLevel == 2) {
            mLevel++;
            mBubbleList.get(0).show();
            mBubbleList.get(1).hide();
            mBubbleList.get(2).show();
            mBubbleList.get(3).hide();
            mBubbleList.get(4).show();
            mBubbleList.get(5).show();
        } else if (mLevel == 3) {
            mLevel++;
            mBubbleList.get(0).show();
            mBubbleList.get(1).show();
            mBubbleList.get(2).show();
            mBubbleList.get(3).hide();
            mBubbleList.get(4).show();
            mBubbleList.get(5).show();
        } else if (mLevel == 4) {
            mLevel++;
            mBubbleList.get(0).show();
            mBubbleList.get(1).show();
            mBubbleList.get(2).show();
            mBubbleList.get(3).show();
            mBubbleList.get(4).show();
            mBubbleList.get(5).show();
        } else {
            mLevel = -1;
            mInProgress = true;
            updateLevel();
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
