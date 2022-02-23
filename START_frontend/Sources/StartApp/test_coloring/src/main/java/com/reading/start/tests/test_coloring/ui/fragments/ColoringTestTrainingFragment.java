package com.reading.start.tests.test_coloring.ui.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.SeekBar;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_coloring.Constants;
import com.reading.start.tests.test_coloring.R;
import com.reading.start.tests.test_coloring.data.DataProvider;
import com.reading.start.tests.test_coloring.databinding.TestColoringFragmentTrainingTestBinding;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestContent;
import com.reading.start.tests.test_coloring.ui.activities.MainActivity;
import com.reading.start.tests.test_coloring.ui.views.DrawView;

import io.realm.Realm;

public class ColoringTestTrainingFragment extends BaseFragment {

    private static final String TAG = ColoringTestTrainingFragment.class.getSimpleName();

    private TestColoringFragmentTrainingTestBinding mBinding;

    private boolean mIsPanelOpened = false;

    private Handler mHandler = new Handler();

    public ColoringTestTrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_coloring_fragment_training_test, container, false);

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

        mBinding.imageColoring.setBackground(getBackground());
        mBinding.imageColoring.setListener(new DrawView.DrawViewListener() {
            @Override
            public void onCompleteTrigger() {
                closePanel();
                //mBinding.layoutPalette.setVisibility(View.GONE);
                mBinding.layoutBrush.setVisibility(View.GONE);
            }

            @Override
            public void onClick() {
                closePanel();
//                mBinding.layoutPalette.setVisibility(View.GONE);
                mBinding.layoutBrush.setVisibility(View.GONE);
                mBinding.imageColoring.unlock();
            }
        });

        mBinding.elasticImage.setOnClickListener(v -> {
//            mBinding.layoutPalette.setVisibility(View.GONE);
            mBinding.layoutBrush.setVisibility(View.GONE);
            mBinding.imageColoring.setElasticMode(true);
        });

        mBinding.elasticImage.setOnLongClickListener(v -> {
            mBinding.imageColoring.clear();
            return true;
        });

        mBinding.brushImage.setOnClickListener(v -> openBrush());

        mBinding.colorPickerImage.setOnClickListener(v -> {
//            mBinding.layoutPalette.setVisibility(View.VISIBLE);
            mBinding.layoutBrush.setVisibility(View.GONE);
        });

        mBinding.saveImage.setOnClickListener(v -> {
            closePanel();
            mBinding.layoutPalette.setVisibility(View.GONE);
            mBinding.layoutBrush.setVisibility(View.GONE);
        });

        mBinding.panelButton.setOnClickListener(v -> {
//            mBinding.layoutPalette.setVisibility(View.GONE);
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

        mHandler.postDelayed(autoStart, Constants.AUTO_START_TEST);
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

    private void fillPalette() {
        mBinding.color1.setOnClickListener(v -> {
            mBinding.imageColoring.setPaintColor(getResources().getColor(R.color.test_coloring_palette_color_1));
            //mBinding.layoutPalette.setVisibility(View.GONE);

            resetSelected();
            mBinding.color1.setBackgroundResource(R.drawable.rectangle_rounded_selected_red);
        });
        mBinding.color2.setOnClickListener(v -> {
            mBinding.imageColoring.setPaintColor(getResources().getColor(R.color.test_coloring_palette_color_2));
            //mBinding.layoutPalette.setVisibility(View.GONE);

            resetSelected();
            mBinding.color2.setBackgroundResource(R.drawable.rectangle_rounded_selected_yellow);
        });
        mBinding.color3.setOnClickListener(v -> {
            mBinding.imageColoring.setPaintColor(getResources().getColor(R.color.test_coloring_palette_color_3));
            //mBinding.layoutPalette.setVisibility(View.GONE);

            resetSelected();
            mBinding.color3.setBackgroundResource(R.drawable.rectangle_rounded_selected_green);
        });
        mBinding.color4.setOnClickListener(v -> {
            mBinding.imageColoring.setPaintColor(getResources().getColor(R.color.test_coloring_palette_color_4));
            //mBinding.layoutPalette.setVisibility(View.GONE);

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
        //mBinding.layoutPalette.setVisibility(View.GONE);
        mBinding.layoutBrush.setVisibility(View.VISIBLE);
        mBinding.seekBar.setMax(Constants.MAX_STROKE_WIDTH - Constants.MIN_STROKE_WIDTH);
        mBinding.seekBar.setProgress(0);

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

                //mBinding.layoutPalette.setVisibility(View.GONE);
                mBinding.layoutBrush.setVisibility(View.GONE);
            }
        });
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

    private Bitmap getBackground() {
        Bitmap bitmap = null;
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(getActivity()).getRealm();
            ColoringTestContent results = realm.where(ColoringTestContent.class).findFirst();

            if (results != null) {
                int surveyAttempt = getMainActivity().getSurveyAttempt();

                if (surveyAttempt % 2 == 0) {
                    bitmap = BitmapFactory.decodeFile(results.getImg1());
                } else {
                    bitmap = BitmapFactory.decodeFile(results.getImg2());
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return bitmap;
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
