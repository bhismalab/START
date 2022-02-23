package com.reading.start.tests.test_coloring.ui.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.reading.start.tests.Constants;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_coloring.R;
import com.reading.start.tests.test_coloring.data.DataProvider;
import com.reading.start.tests.test_coloring.databinding.TestColoringFragmentResultTestBinding;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestSurveyAttachments;
import com.reading.start.tests.test_coloring.domain.entity.ColoringTestSurveyResult;
import com.reading.start.tests.test_coloring.domain.entity.TestData;
import com.reading.start.tests.test_coloring.domain.entity.TestDataAttempt;
import com.reading.start.tests.test_coloring.domain.entity.TestDataItem;
import com.reading.start.tests.test_coloring.ui.activities.MainActivity;
import com.reading.start.tests.test_coloring.ui.adapters.ScreenSlidePagerAdapter;
import com.reading.start.tests.test_coloring.ui.adapters.ScreenSlidePagerImageAdapter;
import com.reading.start.tests.test_coloring.ui.views.CirclePageIndicator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ColoringTestResultFragment extends BaseFragmentV4 {
    private static final String TAG = ColoringTestResultFragment.class.getSimpleName();

    private TestColoringFragmentResultTestBinding mBinding;

    private int mAttempt = 0;

    private ViewPager mPager;

    public ColoringTestResultFragment() {
        // Required empty public constructor
    }

    public void setAttempt(int value) {
        mAttempt = value;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_coloring_fragment_result_test, container, false);
        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);

        if (mAttempt == 0) {
            title.setText(R.string.test_coloring_action_bar_test_result_1);
        } else if (mAttempt == 1) {
            title.setText(R.string.test_coloring_action_bar_test_result_2);
        } else if (mAttempt == 2) {
            title.setText(R.string.test_coloring_action_bar_test_result_3);
        }

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.GONE);

        mBinding.actionBar.findViewById(R.id.back_button).setOnClickListener(v -> {
            final Activity activity = getActivity();

            if (activity != null) {
                activity.setResult(Constants.ACTIVITY_RESULT_BACK);
                activity.finish();
            }
        });

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTestResult(mAttempt);
    }

    private void loadTestResult(int attempt) {
        Realm realm = null;

        try {
            final MainActivity activity = getMainActivity();
            realm = DataProvider.getInstance(getActivity()).getRealm();

            if (activity != null) {
                if (realm != null && !realm.isClosed()) {
                    if (com.reading.start.tests.test_coloring.Constants.RENDERING_RESULT) {
                        List<ColoringTestSurveyResult> results = realm.where(ColoringTestSurveyResult.class)
                                .equalTo(ColoringTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).findAll();

                        if (results != null && results.size() > 0 && attempt < results.size()) {
                            ColoringTestSurveyResult result = results.get(attempt);

                            if (result != null) {
                                mPager = getView().findViewById(R.id.pager);
                                ScreenSlidePagerImageAdapter pagerAdapter = new ScreenSlidePagerImageAdapter(getFragmentManager());
                                ArrayList<Bitmap> bitmapList = buildBitmap(result);

                                if (bitmapList != null && bitmapList.size() > 0) {
                                    pagerAdapter.addAll(bitmapList);
                                }

                                mPager.setAdapter(pagerAdapter);

                                CirclePageIndicator indicator = getView().findViewById(R.id.indicator);
                                indicator.setViewPager(mPager);
                            }
                        }
                    } else {
                        List<ColoringTestSurveyResult> results = realm.where(ColoringTestSurveyResult.class)
                                .equalTo(ColoringTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).findAll();

                        if (results != null && results.size() > 0 && attempt < results.size()) {
                            ColoringTestSurveyResult result = results.get(attempt);

                            List<ColoringTestSurveyAttachments> attachments = realm.where(ColoringTestSurveyAttachments.class)
                                    .equalTo(ColoringTestSurveyAttachments.FILED_SURVEY_RESULT_ID, result.getId()).findAll();

                            if (attachments != null && attachments.size() > 0) {
                                mPager = getView().findViewById(R.id.pager);
                                ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getFragmentManager());

                                for (ColoringTestSurveyAttachments item : attachments) {
                                    pagerAdapter.add(item.getAttachmentFile());
                                }

                                mPager.setAdapter(pagerAdapter);
                                CirclePageIndicator indicator = getView().findViewById(R.id.indicator);
                                indicator.setViewPager(mPager);
                            }
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
    }

    private ArrayList<Bitmap> buildBitmap(ColoringTestSurveyResult surveyResult) {
        ArrayList<Bitmap> result = null;

        try {
            if (surveyResult != null) {
                result = new ArrayList<>();
                String value = surveyResult.getResultFiles();
                Gson gson = new Gson();
                TestData data = gson.fromJson(value, TestData.class);
                Paint dumpPaint = new Paint();
                dumpPaint.setColor(Color.BLACK);
                dumpPaint.setStrokeWidth(2);
                dumpPaint.setAntiAlias(true);
                dumpPaint.setStrokeCap(Paint.Cap.ROUND);
                dumpPaint.setStrokeJoin(Paint.Join.MITER);

                Paint touchPaint = new Paint();
                touchPaint.setColor(Color.RED);
                touchPaint.setStrokeWidth(com.reading.start.tests.test_coloring.Constants.MAX_STROKE_WIDTH);
                touchPaint.setAntiAlias(true);
                touchPaint.setStrokeCap(Paint.Cap.ROUND);
                touchPaint.setStrokeJoin(Paint.Join.MITER);

                if (data.getAttempts() != null && data.getAttempts().size() > 0) {
                    for (TestDataAttempt item : data.getAttempts()) {
                        // create bitmap the same size as screen
                        Bitmap bitmap = Bitmap.createBitmap(data.getScreenWidth(), data.getScreenHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);

                        // draw touch
                        if (item.getItems() != null && item.getItems().size() > 0) {
                            for (TestDataItem dataItem : item.getItems()) {
                                canvas.drawPoint(dataItem.getX(), dataItem.getY(), touchPaint);
                            }
                        }

                        // draw image dump
                        Type listType = new TypeToken<ArrayList<Point>>() {
                        }.getType();
                        ArrayList<Point> dumpPoints = gson.fromJson(item.getScaledImageDump(), listType);

                        if (dumpPoints != null && dumpPoints.size() > 0) {
                            for (Point point : dumpPoints) {
                                canvas.drawPoint(point.x, point.y, dumpPaint);
                            }
                        }

                        result.add(bitmap);
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }
}
