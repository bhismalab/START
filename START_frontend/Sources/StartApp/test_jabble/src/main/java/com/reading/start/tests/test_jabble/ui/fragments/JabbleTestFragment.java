package com.reading.start.tests.test_jabble.ui.fragments;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.TestsProvider;
import com.reading.start.tests.test_jabble.Constants;
import com.reading.start.tests.test_jabble.JabbleTest;
import com.reading.start.tests.test_jabble.R;
import com.reading.start.tests.test_jabble.data.DataProvider;
import com.reading.start.tests.test_jabble.databinding.TestJabbleFragmentTestBinding;
import com.reading.start.tests.test_jabble.domain.entity.Bubble;
import com.reading.start.tests.test_jabble.domain.entity.JabbleTestSurveyResult;
import com.reading.start.tests.test_jabble.domain.entity.TestData;
import com.reading.start.tests.test_jabble.domain.entity.TestDataItem;
import com.reading.start.tests.test_jabble.ui.activities.MainActivity;
import com.reading.start.tests.test_jabble.ui.dialogs.DialogCompleteTest;
import com.reading.start.tests.test_jabble.ui.views.CompleteTriggerView;
import com.reading.start.tests.test_jabble.utils.PositionManager;
import com.reading.start.tests.test_jabble.utils.SoundManager;
import com.reading.start.tests.test_jabble.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;

public class JabbleTestFragment extends BaseFragment {
    private static final String TAG = JabbleTestFragment.class.getSimpleName();

    private TestJabbleFragmentTestBinding mBinding;

    private boolean mInProgress = false;

    private boolean mSaving = false;

    private TestData mTestData = null;

    private ArrayList<Bubble> mBubbleList = new ArrayList<>();

    private int mLevel = -1;

    private boolean mBubbleInit = false;

    private SoundManager mSoundManager;

    private Handler mHandler = null;

    private boolean mIsBubbleTouch = false;

    private boolean mIsToastActive = false;

    private Bubble.BubbleListener mBubbleListener = new Bubble.BubbleListener() {
        @Override
        public void onClick(Bubble bubble) {
            if (!bubble.isPopped()) {
                mIsBubbleTouch = true;
                bubble.setIsPopped(true);
                float x = mBinding.completeTriggerView.getCurrentX();
                float y = mBinding.completeTriggerView.getCurrentY();
                float touchPressure = mBinding.completeTriggerView.getCurrentTouchPressure();
                float touchSize = mBinding.completeTriggerView.getCurrentTouchSize();

                addTouchTestDate(bubble.getName(), x, y, touchPressure, touchSize);
                mTestData.setBubblesPopped(mTestData.getBubblesPopped() + 1);
                mSoundManager.playSound1();
                bubble.hideAnimation();
                checkLevel();
                mIsBubbleTouch = false;
            }
        }
    };

    private PositionManager mPositionManager;

    public JabbleTestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_jabble_fragment_test, container, false);
        mHandler = new Handler();
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

        mBinding.completeTriggerView.setListener(new CompleteTriggerView.CompleteTriggerViewListener() {
            @Override
            public void onCompleteTrigger() {
                mTestData.setInterrupted(true);
                onTestCompleted();
            }

            @Override
            public void onTouchChanged(float x, float y, float touchPressure, float touchSize) {
                if (!mIsBubbleTouch) {
                    addTouchTestDate(null, x, y, touchPressure, touchSize);
                }
            }

            @Override
            public boolean onCountFingerChanged(int count) {
                if (count >= 2) {
                    if (!mIsToastActive) {
                        mIsToastActive = true;
                        Toast.makeText(getActivity(), getString(R.string.test_jabble_instruction_fingers_error), Toast.LENGTH_SHORT).show();
                        mHandler.postDelayed(() -> mIsToastActive = false, 2000);
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });

        Size screenSize = Utility.getDisplaySize(getActivity());
        float dpi[] = Utility.getXYDpi(getActivity());
        mTestData = new TestData(screenSize.getWidth(), screenSize.getHeight(), dpi[0], dpi[1]);
        mTestData.setBubblesTotal(Constants.TOTAL_BUBBLES);
        mPositionManager = new PositionManager(getActivity());
        mPositionManager.enable();

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
        if (mInProgress) {
            mTestData.setInterrupted(true);
            onTestCompleted();
        }

        stopTest();
        super.onPause();
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

    private void addTouchTestDate(String bubbleName, float x, float y, float touchPressure, float touchSize) {
        float bubble1X = mBubbleList.get(0).isVisible() ? mBubbleList.get(0).getCenterX() : 0;
        float bubble1Y = mBubbleList.get(0).isVisible() ? mBubbleList.get(0).getCenterY() : 0;
        float bubble2X = mBubbleList.get(1).isVisible() ? mBubbleList.get(1).getCenterX() : 0;
        float bubble2Y = mBubbleList.get(1).isVisible() ? mBubbleList.get(1).getCenterY() : 0;
        float bubble3X = mBubbleList.get(2).isVisible() ? mBubbleList.get(2).getCenterX() : 0;
        float bubble3Y = mBubbleList.get(2).isVisible() ? mBubbleList.get(2).getCenterY() : 0;
        float bubble4X = mBubbleList.get(3).isVisible() ? mBubbleList.get(3).getCenterX() : 0;
        float bubble4Y = mBubbleList.get(3).isVisible() ? mBubbleList.get(3).getCenterY() : 0;
        float bubble5X = mBubbleList.get(4).isVisible() ? mBubbleList.get(4).getCenterX() : 0;
        float bubble5Y = mBubbleList.get(4).isVisible() ? mBubbleList.get(4).getCenterY() : 0;
        float bubble6X = mBubbleList.get(5).isVisible() ? mBubbleList.get(5).getCenterX() : 0;
        float bubble6Y = mBubbleList.get(5).isVisible() ? mBubbleList.get(5).getCenterY() : 0;

        long time = Calendar.getInstance().getTimeInMillis();

        TestDataItem dataItem = new TestDataItem(bubble1X, bubble1Y, bubble2X, bubble2Y,
                bubble3X, bubble3Y, bubble4X, bubble4Y, bubble5X, bubble5Y, bubble6X, bubble6Y,
                x, y, touchPressure, touchSize, time, bubbleName, mPositionManager.getX(), mPositionManager.getY(), mPositionManager.getZ());
        mTestData.getItems().add(dataItem);
    }

    private void stopTest() {
        if (mInProgress) {
            mInProgress = false;
            mPositionManager.disable();
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

            if (allHide) {
                mHandler.postDelayed(() -> updateLevel(), Constants.ATTEMPT_DELAY);
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
            onTestCompleted();
        }
    }

    private synchronized void onTestCompleted() {
        if (mInProgress) {
            stopTest();
            showEndTestDialog();
        }
    }

    private void showEndTestDialog() {
        MainActivity activity = getMainActivity();

        if (activity != null) {
            int attempt = saveTestResult();
            boolean showNewAttempt = attempt < com.reading.start.tests.Constants.ATTEMPT_COUNT;
            boolean showNextStep = !TestsProvider.getInstance(getActivity()).isLastTest(JabbleTest.getInstance());

            String title = mTestData.isInterrupted() ? getResources().getString(R.string.test_jabble_dialog_interrupted_title)
                    : getResources().getString(R.string.test_jabble_dialog_success_title);
            String message = mTestData.isInterrupted() ? getResources().getString(R.string.test_jabble_dialog_interrupted_message)
                    : getResources().getString(R.string.test_jabble_dialog_success_message);

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
                        JabbleTestSurveyResult result = new JabbleTestSurveyResult();
                        result.setSurveyId(activity.getSurveyId());
                        Gson gson = new Gson();
                        String testValue = gson.toJson(mTestData);
                        result.setResultFiles(testValue);
                        result.setTestRefId(JabbleTest.TYPE);
                        result.setStartTime(mTestData.getStartTime());
                        result.setEndTime(mTestData.getEndTime());

                        try {
                            realm.beginTransaction();
                            Number currentId = realm.where(JabbleTestSurveyResult.class).max(JabbleTestSurveyResult.FILED_ID);
                            int nextId;

                            if (currentId == null) {
                                nextId = 0;
                            } else {
                                nextId = currentId.intValue() + 1;
                            }

                            result.setId(nextId);
                            realm.copyToRealmOrUpdate(result);
                            realm.commitTransaction();
                            attempt = (int) realm.where(JabbleTestSurveyResult.class)
                                    .equalTo(JabbleTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).count();
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
}
