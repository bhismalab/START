package com.reading.start.tests.test_choice_touching.ui.fragments;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.TestsProvider;
import com.reading.start.tests.test_choice_touching.ChoiceTouchingTest;
import com.reading.start.tests.test_choice_touching.Constants;
import com.reading.start.tests.test_choice_touching.Preferences;
import com.reading.start.tests.test_choice_touching.R;
import com.reading.start.tests.test_choice_touching.data.DataProvider;
import com.reading.start.tests.test_choice_touching.databinding.TestChoiceTouchingFragmentTestBinding;
import com.reading.start.tests.test_choice_touching.domain.entity.ChoiceTouchingTestContent;
import com.reading.start.tests.test_choice_touching.domain.entity.ChoiceTouchingTestSurveyResult;
import com.reading.start.tests.test_choice_touching.domain.entity.ColorButton;
import com.reading.start.tests.test_choice_touching.domain.entity.TestData;
import com.reading.start.tests.test_choice_touching.domain.entity.TestDataItem;
import com.reading.start.tests.test_choice_touching.ui.activities.MainActivity;
import com.reading.start.tests.test_choice_touching.ui.dialogs.DialogCompleteTest;
import com.reading.start.tests.test_choice_touching.ui.views.CompleteTriggerView;
import com.reading.start.tests.test_choice_touching.utils.PositionManager;
import com.reading.start.tests.test_choice_touching.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import io.realm.Realm;

public class ChoiceTouchingTestFragment extends BaseFragment {
    private static final String TAG = ChoiceTouchingTestFragment.class.getSimpleName();

    private static Random sRandom = new Random();

    private TestChoiceTouchingFragmentTestBinding mBinding;

    private boolean mInProgress = false;

    private boolean mAllAddData = true;

    private boolean mSaving = false;

    private boolean mInPlaying = false;

    private TestData mTestData = null;

    private int mGreenClickCount = 0;

    private int mRedClickCount = 0;

    private PositionManager mPositionManager;

    private boolean mButtonsInit = false;

    private ArrayList<ColorButton> mButtonList = new ArrayList<>();

    private int mCurrentIteration = 0;

    private int mCountIteration = Constants.COUNT_ANIMATION_BUTTONS;

    private int mAttempt = 0;

    private boolean mIsButtonTouch = false;

    private String mPlayedVideoName = null;

    private File mSelectedVideo = null;

    private ArrayList<File> mVideoFilesRed = new ArrayList<>();

    private ArrayList<File> mVideoFilesGreen = new ArrayList<>();

    private ArrayList<File> mPlayedVideoFiles = new ArrayList<>();

    private boolean mIsToastActive = false;

    private Handler mHandler = null;

    private ColorButton.ColorButtonListener mGreenBubbleListener = bubble -> {
        if (mInProgress && !mInPlaying && mAllAddData) {
            if (!checkIfFinish()) {
                mAttempt++;

                if (bubble.getView().getId() == R.id.button_green) {
                    mGreenClickCount++;
                } else if (bubble.getView().getId() == R.id.button_red) {
                    mRedClickCount++;
                }

                Uri uri = null;

                if (mSelectedVideo != null && mSelectedVideo.exists()) {
                    uri = Uri.fromFile(mSelectedVideo);
                }

                if (uri != null) {
                    onPlayVideo(uri);
                }
            } else {
                onTestCompleted();
            }
        }
    };

    private ColorButton.ColorButtonListener mRedBubbleListener = bubble -> {
        if (mInProgress && !mInPlaying && mAllAddData) {
            if (!checkIfFinish()) {
                mAttempt++;

                if (bubble.getView().getId() == R.id.button_green) {
                    mGreenClickCount++;
                } else if (bubble.getView().getId() == R.id.button_red) {
                    mRedClickCount++;
                }

                Uri uri = null;

                if (mSelectedVideo != null && mSelectedVideo.exists()) {
                    mPlayedVideoName = mSelectedVideo.getName();
                    uri = Uri.fromFile(mSelectedVideo);
                }

                if (uri != null) {
                    onPlayVideo(uri);
                }
            } else {
                onTestCompleted();
            }
        }
    };

    public ChoiceTouchingTestFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_choice_touching_fragment_test, container, false);
        mHandler = new Handler();
        Size screenSize = Utility.getDisplaySize(getActivity());
        float dpi[] = Utility.getXYDpi(getActivity());
        mTestData = new TestData(screenSize.getWidth(), screenSize.getHeight(), dpi[0], dpi[1]);

        mBinding.buttonGreen.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mIsButtonTouch = true;
                mSelectedVideo = getVideoGreen();

                if (mSelectedVideo != null) {
                    mPlayedVideoName = mSelectedVideo.getName();
                } else {
                    mPlayedVideoName = "";
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsButtonTouch = false;
            }

            if (mAllAddData) {
                float x = mBinding.completeTriggerView.getCurrentX();
                float y = mBinding.completeTriggerView.getCurrentY();
                float touchPressure = mBinding.completeTriggerView.getCurrentTouchPressure();
                float touchSize = mBinding.completeTriggerView.getCurrentTouchSize();
                long time = Calendar.getInstance().getTimeInMillis();
                TestDataItem item = new TestDataItem(x, y, touchPressure, touchSize, time, Constants.CHOICE_TOUCHING_GREEN_BUTTON_NAME,
                        mPlayedVideoName, mPositionManager.getX(), mPositionManager.getY(), mPositionManager.getZ());
                mTestData.getItems().add(item);
            }

            return false;
        });

        mBinding.buttonRed.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mIsButtonTouch = true;
                mSelectedVideo = getVideoRed();

                if (mSelectedVideo != null) {
                    mPlayedVideoName = mSelectedVideo.getName();
                } else {
                    mPlayedVideoName = "";
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsButtonTouch = false;
            }

            if (mAllAddData) {
                float x = mBinding.completeTriggerView.getCurrentX();
                float y = mBinding.completeTriggerView.getCurrentY();
                float touchPressure = mBinding.completeTriggerView.getCurrentTouchPressure();
                float touchSize = mBinding.completeTriggerView.getCurrentTouchSize();
                long time = Calendar.getInstance().getTimeInMillis();
                TestDataItem item = new TestDataItem(x, y, touchPressure, touchSize, time, Constants.CHOICE_TOUCHING_RED_BUTTON_NAME,
                        mPlayedVideoName, mPositionManager.getX(), mPositionManager.getY(), mPositionManager.getZ());
                mTestData.getItems().add(item);
            }

            return false;
        });

        mBinding.completeTriggerView.setListener(new CompleteTriggerView.CompleteTriggerViewListener() {
            @Override
            public void onCompleteTrigger() {
                mTestData.setInterrupted(true);
                onTestCompleted();
            }

            @Override
            public void onTouchChanged(float x, float y, float touchPressure, float touchSize, int touchCount) {
                if (mAllAddData && !mIsButtonTouch && touchCount == 1) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    TestDataItem item = new TestDataItem(x, y, touchPressure, touchSize, time, "", "", mPositionManager.getX(), mPositionManager.getY(), mPositionManager.getZ());
                    mTestData.getItems().add(item);
                }
            }

            @Override
            public boolean onCountFingerChanged(int count) {
                if (count >= 2) {
                    if (!mIsToastActive) {
                        mIsToastActive = true;
                        Toast.makeText(getActivity(), getString(R.string.test_choice_touching_fingers_error), Toast.LENGTH_SHORT).show();
                        mHandler.postDelayed(() -> mIsToastActive = false, 2000);
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });

        mPositionManager = new PositionManager(getActivity());

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

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mButtonsInit) {
            startTest();
        }
    }

    @Override
    public void onPause() {
        if (mInProgress) {
            final VideoView videoView = mBinding.videoView;

            if (videoView.isPlaying()) {
                videoView.stopPlayback();
                videoView.setVisibility(View.GONE);
                mBinding.videoViewHolder.setVisibility(View.GONE);
                mInPlaying = false;
            }

            mTestData.setInterrupted(true);
            onTestCompleted();
        }

        stopTest();
        super.onPause();
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
        startTest();
    }

    private void moveToNewPosition() {
        mCurrentIteration = 0;

        Thread thread = new Thread(() -> {
            mAllAddData = false;

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

            mAllAddData = true;
        });

        thread.start();
    }

    private void showButtons() {
        mAllAddData = true;

        if (mButtonList != null && mButtonList.size() > 0) {
            for (ColorButton item : mButtonList) {
                item.show();
            }
        }
    }

    private boolean checkIfFinish() {
        boolean result = false;

        if ((mGreenClickCount + mRedClickCount) >= Constants.CHOICE_TOUCHING_COUNT) {
            result = true;
        }

        return result;
    }

    private synchronized void onTestCompleted() {
        if (!mInPlaying && mInProgress) {
            stopTest();
            showEndTestDialog();
        }
    }

    private void onPlayVideo(Uri video) {
        if (video != null) {
            final VideoView videoView = mBinding.videoView;

            if (!videoView.isPlaying()) {
                mAllAddData = false;
                mInPlaying = true;
                videoView.setVisibility(View.VISIBLE);
                mBinding.videoViewHolder.setVisibility(View.VISIBLE);
                videoView.setVideoURI(video);
                videoView.setOnCompletionListener(mp -> {
                    mSelectedVideo = null;
                    mPlayedVideoName = null;
                    mAllAddData = true;
                    mInPlaying = false;
                    videoView.setVisibility(View.GONE);
                    mBinding.videoViewHolder.setVisibility(View.GONE);

                    if (checkIfFinish()) {
                        onTestCompleted();
                    } else {
                        moveToNewPosition();
                    }
                });
                videoView.start();
            }
        }
    }

    private File getVideoRed() {
        File result = null;

        if (mVideoFilesRed.size() > 0) {
            int index = sRandom.nextInt(mVideoFilesRed.size());

            if (index < mVideoFilesRed.size()) {
                result = mVideoFilesRed.get(index);
                mVideoFilesRed.remove(index);
                mPlayedVideoFiles.add(result);
            }
        }
        return result;
    }

    private File getVideoGreen() {
        File result = null;

        if (mVideoFilesGreen.size() > 0) {
            int index = sRandom.nextInt(mVideoFilesGreen.size());

            if (index < mVideoFilesGreen.size()) {
                result = mVideoFilesGreen.get(index);
                mVideoFilesGreen.remove(index);
                mPlayedVideoFiles.add(result);
            }
        }

        return result;
    }

    private void fillVideoFiles() {
        mVideoFilesRed.clear();
        mVideoFilesGreen.clear();
        mPlayedVideoFiles.clear();
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(getActivity()).getRealm();
            ChoiceTouchingTestContent content = realm.where(ChoiceTouchingTestContent.class).findFirst();
            Preferences pref = new Preferences(getActivity());

            if (pref.isGreenVideo()) {
                mVideoFilesGreen.add(new File(content.getVideo_1_social()));
                mVideoFilesGreen.add(new File(content.getVideo_2_social()));
                mVideoFilesGreen.add(new File(content.getVideo_3_social()));
                mVideoFilesGreen.add(new File(content.getVideo_4_social()));
                mVideoFilesGreen.add(new File(content.getVideo_1_social()));
                mVideoFilesGreen.add(new File(content.getVideo_2_social()));
                mVideoFilesGreen.add(new File(content.getVideo_3_social()));
                mVideoFilesGreen.add(new File(content.getVideo_4_social()));
                mVideoFilesRed.add(new File(content.getVideo_1_no_social()));
                mVideoFilesRed.add(new File(content.getVideo_2_no_social()));
                mVideoFilesRed.add(new File(content.getVideo_3_no_social()));
                mVideoFilesRed.add(new File(content.getVideo_4_no_social()));
                mVideoFilesRed.add(new File(content.getVideo_1_no_social()));
                mVideoFilesRed.add(new File(content.getVideo_2_no_social()));
                mVideoFilesRed.add(new File(content.getVideo_3_no_social()));
                mVideoFilesRed.add(new File(content.getVideo_4_no_social()));
            } else {
                mVideoFilesGreen.add(new File(content.getVideo_1_no_social()));
                mVideoFilesGreen.add(new File(content.getVideo_2_no_social()));
                mVideoFilesGreen.add(new File(content.getVideo_3_no_social()));
                mVideoFilesGreen.add(new File(content.getVideo_4_no_social()));
                mVideoFilesGreen.add(new File(content.getVideo_1_no_social()));
                mVideoFilesGreen.add(new File(content.getVideo_2_no_social()));
                mVideoFilesGreen.add(new File(content.getVideo_3_no_social()));
                mVideoFilesGreen.add(new File(content.getVideo_4_no_social()));
                mVideoFilesRed.add(new File(content.getVideo_1_social()));
                mVideoFilesRed.add(new File(content.getVideo_2_social()));
                mVideoFilesRed.add(new File(content.getVideo_3_social()));
                mVideoFilesRed.add(new File(content.getVideo_4_social()));
                mVideoFilesRed.add(new File(content.getVideo_1_social()));
                mVideoFilesRed.add(new File(content.getVideo_2_social()));
                mVideoFilesRed.add(new File(content.getVideo_3_social()));
                mVideoFilesRed.add(new File(content.getVideo_4_social()));
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void startTest() {
        if (!mInProgress) {
            mInProgress = true;
            mPositionManager.enable();

            fillVideoFiles();
        }
    }

    private void stopTest() {
        if (mInProgress) {
            mInProgress = false;
            mPositionManager.disable();
        }
    }

    private void showEndTestDialog() {
        MainActivity activity = getMainActivity();

        if (activity != null) {
            int attempt = saveTestResult();
            boolean showNewAttempt = attempt < com.reading.start.tests.Constants.ATTEMPT_COUNT;
            boolean showNextStep = !TestsProvider.getInstance(getActivity()).isLastTest(ChoiceTouchingTest.getInstance());

            String title = mTestData.isInterrupted() ? getResources().getString(R.string.test_choice_touching_dialog_interrupted_title)
                    : getResources().getString(R.string.test_choice_touching_dialog_success_title);
            String message = mTestData.isInterrupted() ? getResources().getString(R.string.test_choice_touching_dialog_interrupted_message)
                    : getResources().getString(R.string.test_choice_touching_dialog_success_message);

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
                        mTestData.setGreenClickCount(mGreenClickCount);
                        mTestData.setRedClickCount(mRedClickCount);
                        mTestData.setDeviceId(Utility.getDeviceId(activity));

                        ChoiceTouchingTestSurveyResult result = new ChoiceTouchingTestSurveyResult();
                        result.setSurveyId(activity.getSurveyId());
                        Gson gson = new Gson();
                        String testValue = gson.toJson(mTestData);
                        result.setResultFiles(testValue);
                        result.setTestRefId(ChoiceTouchingTest.TYPE);
                        result.setStartTime(mTestData.getStartTime());
                        result.setEndTime(mTestData.getEndTime());

                        try {
                            realm.beginTransaction();
                            Number currentId = realm.where(ChoiceTouchingTestSurveyResult.class).max(ChoiceTouchingTestSurveyResult.FILED_ID);
                            int nextId;

                            if (currentId == null) {
                                nextId = 0;
                            } else {
                                nextId = currentId.intValue() + 1;
                            }

                            result.setId(nextId);
                            realm.copyToRealmOrUpdate(result);
                            realm.commitTransaction();
                            attempt = (int) realm.where(ChoiceTouchingTestSurveyResult.class)
                                    .equalTo(ChoiceTouchingTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).count();
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
