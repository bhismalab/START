package com.reading.start.tests.test_coloring.ui.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.TestsProvider;
import com.reading.start.tests.test_coloring.ColoringTest;
import com.reading.start.tests.test_coloring.Constants;
import com.reading.start.tests.test_coloring.R;
import com.reading.start.tests.test_coloring.data.DataProvider;
import com.reading.start.tests.test_coloring.databinding.TestColoringFragmentTestBinding;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestContent;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestSurveyAttachments;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestSurveyResult;
import com.reading.start.tests.test_coloring.domain.entity.TestData;
import com.reading.start.tests.test_coloring.domain.entity.TestDataAttempt;
import com.reading.start.tests.test_coloring.ui.activities.MainActivity;
import com.reading.start.tests.test_coloring.ui.dialogs.DialogCompleteTest;
import com.reading.start.tests.test_coloring.ui.views.DrawView;
import com.reading.start.tests.test_coloring.utils.BitmapUtils;
import com.reading.start.tests.test_coloring.utils.PositionManager;
import com.reading.start.tests.test_coloring.utils.Utility;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;

public class ColoringTestFragment extends BaseFragment {
    private static final String TAG = ColoringTestFragment.class.getSimpleName();

    private TestColoringFragmentTestBinding mBinding;

    private boolean mInProgress = false;

    private boolean mSaving = false;

    private TestData mTestData = null;

    private PositionManager mPositionManager;

    private boolean mIsPanelOpened = false;

    private int mAttempts = 0;

    private ArrayList<String> mAttemptsBitmap = new ArrayList<>();

    private boolean mIsFinished = false;

    public ColoringTestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_coloring_fragment_test, container, false);

        mBinding.imageColoring.setListener(new DrawView.DrawViewListener() {
            @Override
            public void onCompleteTrigger() {
                closePanel();
                mBinding.layoutBrush.setVisibility(View.GONE);

                if (mInProgress) {
                    mTestData.setInterrupted(true);
                    complete();
                }
            }

            @Override
            public void onClick() {
                closePanel();
                mBinding.layoutBrush.setVisibility(View.GONE);
            }
        });

        mBinding.elasticImage.setOnClickListener(v -> {
            mBinding.layoutBrush.setVisibility(View.GONE);
            mBinding.imageColoring.setElasticMode(true);
        });

        mBinding.elasticImage.setOnLongClickListener(v -> {
            mBinding.imageColoring.clear();
            return true;
        });

        mBinding.brushImage.setOnClickListener(v -> openBrush());

        mBinding.colorPickerImage.setOnClickListener(v -> {
            mBinding.layoutBrush.setVisibility(View.GONE);
        });

        mBinding.saveImage.setOnClickListener(v -> {
            closePanel();
            mBinding.layoutBrush.setVisibility(View.GONE);
            checkComplete();
        });

        mBinding.panelButton.setOnClickListener(v -> {
            mBinding.layoutBrush.setVisibility(View.GONE);

            if (mIsPanelOpened) {
                closePanel();
            } else {
                openPanel();
            }
        });

        mIsPanelOpened = false;
        collapse(mBinding.layoutNavigationButtons);

        Animation pulse = AnimationUtils.loadAnimation(getActivity(), R.anim.pulse);
        mBinding.panelButton.startAnimation(pulse);

        fillPalette();
        Size screenSize = Utility.getDisplaySize(getActivity());
        float dpi[] = Utility.getXYDpi(getActivity());
        mTestData = new TestData(screenSize.getWidth(), screenSize.getHeight(), dpi[0], dpi[1]);
        TestDataAttempt attempt = new TestDataAttempt();
        mTestData.getAttempts().add(attempt);
        mAttempts = 1;
        mBinding.imageColoring.setTestData(attempt);
        mBinding.imageColoring.setStroke(Constants.MAX_STROKE_WIDTH);

        mPositionManager = new PositionManager(getActivity());

        return mBinding.getRoot();
    }

    private void openPanel() {
        if (!mIsPanelOpened) {
            mIsPanelOpened = true;
            mBinding.imageColoring.lock();
            expand(mBinding.layoutNavigationButtons);
        }
    }

    private void closePanel() {
        if (mIsPanelOpened) {
            mBinding.imageColoring.unlock();
            mIsPanelOpened = false;
            collapse(mBinding.layoutNavigationButtons);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startTest();
    }

    @Override
    public void onPause() {
        if (mInProgress) {
            mTestData.setInterrupted(true);
            complete();
        } else {
            stopTest();
        }

        super.onPause();
    }

    private void fillPalette() {
        mBinding.color1.setOnClickListener(v -> {
            mBinding.imageColoring.setPaintColor(getResources().getColor(R.color.test_coloring_palette_color_1));

            resetSelected();
            mBinding.color1.setBackgroundResource(R.drawable.rectangle_rounded_selected_red);
        });
        mBinding.color2.setOnClickListener(v -> {
            mBinding.imageColoring.setPaintColor(getResources().getColor(R.color.test_coloring_palette_color_2));

            resetSelected();
            mBinding.color2.setBackgroundResource(R.drawable.rectangle_rounded_selected_yellow);
        });
        mBinding.color3.setOnClickListener(v -> {
            mBinding.imageColoring.setPaintColor(getResources().getColor(R.color.test_coloring_palette_color_3));

            resetSelected();
            mBinding.color3.setBackgroundResource(R.drawable.rectangle_rounded_selected_green);
        });
        mBinding.color4.setOnClickListener(v -> {
            mBinding.imageColoring.setPaintColor(getResources().getColor(R.color.test_coloring_palette_color_4));

            resetSelected();
            mBinding.color4.setBackgroundResource(R.drawable.rectangle_rounded_selected_blue);
        });

        mBinding.imageColoring.setPaintColor(getResources().getColor(R.color.test_coloring_palette_color_1));
        resetSelected();
        mBinding.color1.setBackgroundResource(R.drawable.rectangle_rounded_selected_red);
    }

    private void resetSelected() {
        mBinding.color1.setBackgroundResource(R.drawable.rectangle_rounded_red);
        mBinding.color2.setBackgroundResource(R.drawable.rectangle_rounded_yellow);
        mBinding.color3.setBackgroundResource(R.drawable.rectangle_rounded_green);
        mBinding.color4.setBackgroundResource(R.drawable.rectangle_rounded_blue);
    }

    private void openBrush() {
        mBinding.layoutBrush.setVisibility(View.VISIBLE);
        mBinding.seekBar.setMax(Constants.MAX_STROKE_WIDTH - Constants.MIN_STROKE_WIDTH);
        mBinding.seekBar.setProgress(Constants.MAX_STROKE_WIDTH - Constants.MIN_STROKE_WIDTH);

        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int seekBarProgress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = progress + Constants.MIN_STROKE_WIDTH;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mBinding.imageColoring.setStroke(seekBarProgress);

                mBinding.layoutPalette.setVisibility(View.GONE);
                mBinding.layoutBrush.setVisibility(View.GONE);
            }
        });
    }

    private void startTest() {
        if (!mInProgress && !mIsFinished) {
            BackgroundRecord record = getBackground();

            if (record != null) {
                if (mBinding.imageColoring.getTestData() != null) {
                    mBinding.imageColoring.getTestData().setImageDump(record.dump);
                    mBinding.imageColoring.getTestData().setImageName(record.name);
                }

                mBinding.imageColoring.setBackground(record.bitmap);
                mInProgress = true;
                mPositionManager.enable();
                mBinding.imageColoring.setPositionManager(mPositionManager);
            }
        }
    }

    private void checkComplete() {
        String bitmapBase64 = resultImageToBase64();

        if (bitmapBase64 != null) {
            mAttemptsBitmap.add(bitmapBase64);
        } else {
            mAttemptsBitmap.add("");
        }

        if (mAttempts < Constants.ATTEMPT_COUNT) {
            TestDataAttempt attempt = new TestDataAttempt();
            mTestData.getAttempts().add(attempt);
            mAttempts++;
            mBinding.imageColoring.setTestData(attempt);
            mInProgress = false;
            startTest();
        } else {
            stopTest();
            showEndTestDialog();
        }
    }

    private void complete() {
        String bitmapBase64 = resultImageToBase64();

        if (bitmapBase64 != null) {
            mAttemptsBitmap.add(bitmapBase64);
        } else {
            mAttemptsBitmap.add("");
        }

        stopTest();
        showEndTestDialog();
    }

    private void stopTest() {
        if (mInProgress) {
            mIsFinished = true;
            mInProgress = false;
            mPositionManager.disable();
            mBinding.imageColoring.setPositionManager(null);
        }
    }

    private void showEndTestDialog() {
        MainActivity activity = getMainActivity();

        if (activity != null) {
            int attempt = saveTestResult();
            boolean showNewAttempt = attempt < com.reading.start.tests.Constants.ATTEMPT_COUNT;
            boolean showNextStep = !TestsProvider.getInstance(getActivity()).isLastTest(ColoringTest.getInstance());

            String title = mTestData.isInterrupted() ? getResources().getString(R.string.test_coloring_dialog_interrupted_title)
                    : getResources().getString(R.string.test_coloring_dialog_success_title);
            String message = mTestData.isInterrupted() ? getResources().getString(R.string.test_coloring_dialog_interrupted_message)
                    : getResources().getString(R.string.test_coloring_dialog_success_message);

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
                        ColoringTestSurveyResult result = new ColoringTestSurveyResult();
                        result.setSurveyId(activity.getSurveyId());
                        Gson gson = new Gson();
                        String testValue = gson.toJson(mTestData);
                        result.setResultFiles(testValue);
                        result.setTestRefId(ColoringTest.TYPE);
                        result.setStartTime(mTestData.getStartTime());
                        result.setEndTime(mTestData.getEndTime());

                        try {
                            realm.beginTransaction();
                            Number currentId = realm.where(ColoringTestSurveyResult.class).max(ColoringTestSurveyResult.FILED_ID);
                            Number currentAttachmentsId = realm.where(ColoringTestSurveyAttachments.class).max(ColoringTestSurveyAttachments.FILED_ID);
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
                                    ColoringTestSurveyAttachments attachments = new ColoringTestSurveyAttachments();
                                    attachments.setId(nextAttachmentsId + attachmentId);
                                    attachments.setSurveyId(activity.getSurveyId());
                                    attachments.setSurveyResultId(nextId);
                                    attachments.setAttachmentFile(bitmap);
                                    realm.copyToRealmOrUpdate(attachments);
                                    attachmentId++;
                                }
                            }

                            realm.commitTransaction();
                            attempt = (int) realm.where(ColoringTestSurveyResult.class)
                                    .equalTo(ColoringTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).count();
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
            if (mBinding.imageColoring.getBitmapResult() != null) {
                result = BitmapUtils.bitmapToBase64(mBinding.imageColoring.getBitmapResult());
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

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private BackgroundRecord getBackground() {
        BackgroundRecord record = null;
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(getActivity()).getRealm();
            ColoringTestContent results = realm.where(ColoringTestContent.class).findFirst();

            if (results != null) {
                int surveyAttempt = getMainActivity().getSurveyAttempt();

                if (surveyAttempt % 2 == 0) {
                    // images 5 and 5
                    if (mAttempts == 1) {
                        record = new BackgroundRecord();
                        record.bitmap = BitmapFactory.decodeFile(results.getImg5());
                        record.dump = results.getImg5Dump();
                        record.name = results.getImgName5();
                    } else {
                        record = new BackgroundRecord();
                        record.bitmap = BitmapFactory.decodeFile(results.getImg6());
                        record.dump = results.getImg6Dump();
                        record.name = results.getImgName6();
                    }
                } else {
                    // images 3 and 4
                    if (mAttempts == 1) {
                        record = new BackgroundRecord();
                        record.bitmap = BitmapFactory.decodeFile(results.getImg3());
                        record.dump = results.getImg3Dump();
                        record.name = results.getImgName3();
                    } else {
                        record = new BackgroundRecord();
                        record.bitmap = BitmapFactory.decodeFile(results.getImg4());
                        record.dump = results.getImg4Dump();
                        record.name = results.getImgName4();
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

        return record;
    }

    private static class BackgroundRecord {
        Bitmap bitmap;
        String dump;
        String name;
    }
}
