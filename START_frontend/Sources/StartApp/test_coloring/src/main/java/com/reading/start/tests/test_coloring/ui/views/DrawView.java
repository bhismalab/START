package com.reading.start.tests.test_coloring.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_coloring.Constants;
import com.reading.start.tests.test_coloring.domain.entity.TestDataAttempt;
import com.reading.start.tests.test_coloring.domain.entity.TestDataItem;
import com.reading.start.tests.test_coloring.utils.BitmapUtils;
import com.reading.start.tests.test_coloring.utils.PositionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DrawView extends View implements View.OnTouchListener, View.OnClickListener {
    private static final String TAG = DrawView.class.getSimpleName();

    public interface DrawViewListener {
        void onCompleteTrigger();

        void onClick();
    }

    private List<PointF> mPoints = new ArrayList<>();

    private Paint mDrawPaint;

    private Paint mElasticPaint;

    private Bitmap mDrawBitmap;

    private Bitmap mBackgroundBitmap;

    private Bitmap mResultBitmap;

    private Bitmap mScaledBackgroundBitmap;

    private Canvas mDrawCanvas;

    private Canvas mResultCanvas;

    private Canvas mScaledBackgroundCanvas;

    private Rect mScreenBounds;

    private Rect mBackgroundBounds;

    private int mScaledBitmapWidth;

    private int mScaledBitmapHeight;

    private double mScaleCoefficient = 1;

    private final int mLineWidth = Constants.MAX_STROKE_WIDTH;

    private final int mColor = Color.RED;

    private boolean mIsElasticMode = false;

    private DrawViewListener mListener = null;

    private TestDataAttempt mTestData;

    private PositionManager mPositionManager;

    private boolean mIsLocked = false;

    private int mTouchCount = 0;

    private boolean mIsDumpProcessed = false;

    private boolean mTopLeftTap = false;
    private boolean mTopRightTap = false;
    private boolean mBottomLeftTap = false;
    private boolean mBottomRightTap = false;
    private long mLastTapTime = 0;

    public Bitmap getScaledBackgroundBitmap() {
        return mScaledBackgroundBitmap;
    }

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setTestData(TestDataAttempt testData) {
        mTestData = testData;
    }

    public TestDataAttempt getTestData() {
        return mTestData;
    }

    public void setPositionManager(PositionManager positionManager) {
        mPositionManager = positionManager;
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        setDrawingCacheEnabled(true);

        setOnTouchListener(this);
        setOnClickListener(this);

        mDrawPaint = new Paint();
        mDrawPaint.setColor(mColor);
        mDrawPaint.setStrokeWidth(mLineWidth);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaint.setStrokeJoin(Paint.Join.MITER);

        mElasticPaint = new Paint();
        mElasticPaint.setColor(Color.TRANSPARENT);
        mElasticPaint.setStrokeWidth(mLineWidth);
        mElasticPaint.setAntiAlias(true);
        mElasticPaint.setStrokeCap(Paint.Cap.ROUND);
        mElasticPaint.setStrokeJoin(Paint.Join.MITER);
        mElasticPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void setBackground(Bitmap bitmap) {
        mScreenBounds = null;
        loadImages(bitmap);
        mIsDumpProcessed = false;
        invalidate();
    }

    public void setListener(DrawViewListener listener) {
        mListener = listener;
    }

    public void lock() {
        mIsLocked = true;
    }

    public void unlock() {
        mIsLocked = false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mScreenBounds = null;
        mScaledBackgroundBitmap = null;
        mScaledBackgroundCanvas = null;
        mIsDumpProcessed = false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mScreenBounds == null && mBackgroundBitmap != null) {
            mScaleCoefficient = getScaleCoefficient(mBackgroundBitmap);
            mScaledBitmapWidth = (int) ((double) mBackgroundBitmap.getWidth() * mScaleCoefficient);
            mScaledBitmapHeight = (int) ((double) mBackgroundBitmap.getHeight() * mScaleCoefficient);

            mBackgroundBounds = new Rect(0, 0, mBackgroundBitmap.getWidth(), mBackgroundBitmap.getHeight());
            mScreenBounds = new Rect(getBitmapWidthMargin(mScaledBitmapWidth), getBitmapHeightMargin(mScaledBitmapHeight),
                    mScaledBitmapWidth + getBitmapWidthMargin(mScaledBitmapWidth), mScaledBitmapHeight + getBitmapHeightMargin(mScaledBitmapHeight));
        }

        if (mScaledBackgroundBitmap == null && mBackgroundBitmap != null) {
            mScaledBackgroundBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), mBackgroundBitmap.getConfig());
            mScaledBackgroundBitmap.eraseColor(Color.WHITE);
            mScaledBackgroundCanvas = new Canvas(mScaledBackgroundBitmap);
        }

        for (PointF point : mPoints) {
            if (mPoints.indexOf(point) == 0) {
                mDrawCanvas.drawCircle(point.x, point.y, mLineWidth / 2, mIsElasticMode ? mElasticPaint : mDrawPaint);
            } else {
                PointF last = mPoints.get(mPoints.indexOf(point) - 1);
                mDrawCanvas.drawLine(last.x, last.y, point.x, point.y, mIsElasticMode ? mElasticPaint : mDrawPaint);
            }
        }

        if (mResultCanvas != null) {
            mResultCanvas.drawColor(Color.WHITE);
        }

        if (mBackgroundBitmap != null) {
            // draw  points
            if (mDrawBitmap != null && !mDrawBitmap.isRecycled()) {
                canvas.drawBitmap(mDrawBitmap, mBackgroundBounds, mScreenBounds, mDrawPaint);

                if (mResultCanvas != null) {
                    mResultCanvas.drawBitmap(mDrawBitmap, 0, 0, mDrawPaint);
                }
            }

            // draw background
            if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled()) {
                canvas.drawBitmap(mBackgroundBitmap, mBackgroundBounds, mScreenBounds, mDrawPaint);

                if (mResultCanvas != null) {
                    mResultCanvas.drawBitmap(mBackgroundBitmap, 0, 0, mDrawPaint);
                }

                if (mScaledBackgroundCanvas != null && !mIsDumpProcessed) {
                    mScaledBackgroundCanvas.drawBitmap(mBackgroundBitmap, mBackgroundBounds, mScreenBounds, mDrawPaint);

                    if (mTestData != null && getMeasuredHeight() > 0 && getMeasuredWidth() > 0) {
                        mIsDumpProcessed = true;
                        makeDumpInBackground(mScaledBackgroundBitmap);
                    }
                }
            }
        }
    }

    private void makeDumpInBackground(final Bitmap bitmap) {
        Thread thread = new Thread(() -> {
            String value = BitmapUtils.makeBitmapDump(bitmap);

            if (value != null) {
                mTestData.setScaledImageDump(value);
            }
        });
        thread.start();
    }

    private void loadImages(Bitmap bitmap) {
        try {
            mIsDumpProcessed = false;
            mScaledBackgroundBitmap = null;
            mScaledBackgroundCanvas = null;
            mBackgroundBitmap = bitmap;
            mDrawBitmap = Bitmap.createBitmap(mBackgroundBitmap.getWidth(), mBackgroundBitmap.getHeight(), mBackgroundBitmap.getConfig());
            mDrawBitmap.eraseColor(Color.TRANSPARENT);
            mResultBitmap = Bitmap.createBitmap(mBackgroundBitmap.getWidth(), mBackgroundBitmap.getHeight(), mBackgroundBitmap.getConfig());
            mDrawCanvas = new Canvas(mDrawBitmap);
            mResultCanvas = new Canvas(mResultBitmap);

            if (mTestData != null) {
                mTestData.setImageWidth(mBackgroundBitmap.getWidth());
                mTestData.setImageHeight(mBackgroundBitmap.getHeight());
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }
    }

    private double getScaleCoefficient(Bitmap scaleBitmap) {
        double result = 1;

        if (scaleBitmap != null) {
            if (scaleBitmap.getWidth() >= scaleBitmap.getHeight()) {
                result = (scaleBitmap.getWidth() > getMeasuredWidth()) ?
                        (double) scaleBitmap.getWidth() / (double) getMeasuredWidth()
                        : (double) getMeasuredWidth() / (double) scaleBitmap.getWidth();

                if ((double) getMeasuredHeight() < (double) scaleBitmap.getHeight() * result) {
                    result = (double) getMeasuredHeight() / (double) scaleBitmap.getHeight();
                }
            } else {
                result = (double) getMeasuredHeight() / (double) scaleBitmap.getHeight();
            }
        }

        return result;
    }

    private int getBitmapWidthMargin(int scaledBitmapWidth) {
        return (getMeasuredWidth() - scaledBitmapWidth) / 2;
    }

    private int getBitmapHeightMargin(int scaledBitmapHeight) {
        return (getMeasuredHeight() - scaledBitmapHeight) / 2;
    }

    public Bitmap getBitmapResult() {
        return mResultBitmap;
    }

    public void clear() {
        mDrawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClick();
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        if (mIsLocked) {
            return super.onTouchEvent(event);
        } else {
            super.onTouchEvent(event);

            if (event.getAction() == MotionEvent.ACTION_DOWN
                    || event.getAction() == MotionEvent.ACTION_POINTER_DOWN
                    || event.getAction() == MotionEvent.ACTION_POINTER_1_DOWN
                    || event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN
                    || event.getAction() == MotionEvent.ACTION_POINTER_3_DOWN) {
                mTouchCount++;
            } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_POINTER_1_UP
                    || event.getAction() == MotionEvent.ACTION_POINTER_2_UP
                    || event.getAction() == MotionEvent.ACTION_POINTER_3_UP) {
                mTouchCount--;

                if (mTouchCount < 0) {
                    mTouchCount = 0;
                }
            }

            if (mTouchCount == 1 && mTestData != null && mTestData.getItems() != null) {
                float x = event.getX();
                float y = event.getY();
                float touchPressure = event.getPressure();
                float touchSize = event.getSize();

                long time = Calendar.getInstance().getTimeInMillis();
                int color = mDrawPaint.getColor();
                float deviceX = 0;
                float deviceY = 0;
                float deviceZ = 0;

                if (mPositionManager != null) {
                    deviceX = mPositionManager.getX();
                    deviceY = mPositionManager.getY();
                    deviceZ = mPositionManager.getZ();
                }

                TestDataItem item = new TestDataItem(x, y, touchPressure, touchSize, time, color, deviceX, deviceY, deviceZ);
                mTestData.getItems().add(item);
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                mPoints.clear();
            } else {
                if (mTouchCount == 1 && mScreenBounds != null && mScreenBounds.contains((int) event.getX(), (int) event.getY())) {
                    PointF point = new PointF();
                    point.x = (event.getX() - getBitmapWidthMargin(mScaledBitmapWidth)) / (float) mScaleCoefficient;
                    point.y = (event.getY() - getBitmapHeightMargin(mScaledBitmapHeight)) / (float) mScaleCoefficient;
                    mPoints.add(point);
                }
            }

            if (!detectCompleteTouch(event)) {
                mTopLeftTap = false;
                mTopRightTap = false;
                mBottomLeftTap = false;
                mBottomRightTap = false;
            }

            invalidate();
            return true;
        }
    }

    public void setElasticMode(boolean value) {
        mIsElasticMode = value;
        invalidate();
    }

    public void setPaintColor(int colorId) {
        if (mDrawPaint != null) {
            mIsElasticMode = false;
            mDrawPaint.setColor(colorId);
        }
    }

    public void setStroke(int strokeWidth) {
        if (mDrawPaint != null) {
            mDrawPaint.setStrokeWidth(strokeWidth);
            mElasticPaint.setStrokeWidth(strokeWidth);
        }
    }

    private synchronized boolean detectCompleteTouch(MotionEvent event) {
        boolean result = false;

        if (event != null) {
            if (event.getY() > 0 && event.getY() < getHeight() * 0.2 && event.getX() > 0 && event.getX() < getWidth() * 0.2) {
                // top left corner
                result = onTopLeftTap();
            } else if (event.getY() > 0 && event.getY() < getHeight() * 0.2 && event.getX() > getWidth() * 0.8 && event.getX() < getWidth()) {
                // top right corner
                result = onTopRightTap();
            } else if (event.getY() > getHeight() * 0.8 && event.getY() < getHeight() && event.getX() > 0 && event.getX() < getWidth() * 0.2) {
                // bottom left corner
                result = onBottomLeftTap();
            } else if (event.getY() > getHeight() * 0.8 && event.getY() < getHeight() && event.getX() > getWidth() * 0.8 && event.getX() < getWidth()) {
                // bottom right corner
                result = onBottomRightTap();
            }
        }

        return result;
    }

    private boolean onTopLeftTap() {
        boolean result = false;

        if (!mTopLeftTap) {
            mTopLeftTap = true;
            mTopRightTap = false;
            mBottomLeftTap = false;
            mBottomRightTap = false;
            mLastTapTime = Calendar.getInstance().getTimeInMillis();
            result = true;
        } else {
            result = true;
        }

        return result;
    }

    private boolean onTopRightTap() {
        boolean result = false;

        if (!mTopRightTap && mTopLeftTap && Math.abs(Calendar.getInstance().getTimeInMillis() - mLastTapTime) < 2000) {
            mTopRightTap = true;
            mBottomLeftTap = false;
            mBottomRightTap = false;
            mLastTapTime = Calendar.getInstance().getTimeInMillis();
            result = true;
        } else if (mTopRightTap && mTopLeftTap && Math.abs(Calendar.getInstance().getTimeInMillis() - mLastTapTime) < 2000) {
            result = true;
        }

        return result;
    }

    private boolean onBottomRightTap() {
        boolean result = false;

        if (!mBottomRightTap && mTopLeftTap && mTopRightTap && Math.abs(Calendar.getInstance().getTimeInMillis() - mLastTapTime) < 2000) {
            mBottomRightTap = true;
            mBottomLeftTap = false;
            mLastTapTime = Calendar.getInstance().getTimeInMillis();
            result = true;

            if (mListener != null) {
                mListener.onCompleteTrigger();
            }
        } else if (mBottomRightTap && mTopLeftTap && mTopRightTap && Math.abs(Calendar.getInstance().getTimeInMillis() - mLastTapTime) < 2000) {
            result = true;
        }

        return result;
    }

    private boolean onBottomLeftTap() {
        boolean result = false;

        if (!mBottomLeftTap && mBottomRightTap && mTopRightTap && mTopLeftTap && Math.abs(Calendar.getInstance().getTimeInMillis() - mLastTapTime) < 2000) {
            mBottomLeftTap = true;
            mLastTapTime = Calendar.getInstance().getTimeInMillis();
            result = true;

            if (mListener != null) {
                mListener.onCompleteTrigger();
            }
        } else if (mBottomLeftTap && mBottomRightTap && mTopRightTap && mTopLeftTap && Math.abs(Calendar.getInstance().getTimeInMillis() - mLastTapTime) < 2000) {
            result = true;
        }

        return result;
    }
}