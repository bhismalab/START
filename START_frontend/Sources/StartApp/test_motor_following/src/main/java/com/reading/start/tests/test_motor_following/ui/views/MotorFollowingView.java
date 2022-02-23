package com.reading.start.tests.test_motor_following.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_motor_following.Constants;
import com.reading.start.tests.test_motor_following.R;
import com.reading.start.tests.test_motor_following.utils.SplineInterpolator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class MotorFollowingView extends View {
    private static final String TAG = MotorFollowingView.class.getSimpleName();

    public interface MotorFollowingViewListener {
        void onStart(long time, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize);

        void onProgressChanged(long time, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize);

        void onEnd(long time, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize);

        void onBeeCaught(long time, boolean inProgress, float beeX, float beeY, float touchX, float touchY, float touchPressure, float touchSize);

        void onAnimationStart();

        void onAnimationCompleted();
    }

    public enum State {
        Start,
        InProgress,
        End
    }

    private final int mLineWidth = Constants.BEE_PATH_SIZE;

    private final int mTouchLineWidth = Constants.TOUCH_PATH_SIZE;

    private int mBeePathColor = Color.BLACK;

    private int mTouchPathColor = Color.BLUE;

    private int mBackgroundColor = Color.WHITE;

    private PointF mStartPoint = null;

    private PointF mEndPoint = null;

    private PointF mBeePosition = null;

    private RectF mBeeRect = new RectF();

    private PointF mPrevBeePosition = null;

    private ArrayList<PointF> mTouchPoints = new ArrayList<>();

    private Paint mTouchPaint;

    private Paint mBeePathPaint;

    private float mTouchX = 0;

    private float mTouchY = 0;

    private float mTouchPressure = 0;

    private float mTouchSize = 0;

    private Bitmap mBeeBitmap;

    private State mState = State.Start;

    private SplineInterpolator mSplineInterpolator = null;

    private MotorFollowingViewListener mListener;

    private Bitmap mResultBitmap;

    private Bitmap mPathBitmap;

    private Bitmap mTouchBitmap;

    private Canvas mResultCanvas;

    private Canvas mPathCanvas;

    private Canvas mTouchCanvas;

    private boolean mIsBeeCaught = false;

    private boolean mIsBeeCaughtEnd = false;

    private boolean mIsBeeAnimating = false;

    private int mTouchCount = 0;

    private boolean mLeftToRight = true;

    public MotorFollowingView(Context context) {
        super(context);
        init();
    }

    public MotorFollowingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MotorFollowingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setListener(MotorFollowingViewListener listener) {
        mListener = listener;
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        setDrawingCacheEnabled(true);

        mBackgroundColor = Color.TRANSPARENT;
        mBeePathColor = getResources().getColor(R.color.test_motor_following_color_test_bee_path);
        mTouchPathColor = getResources().getColor(R.color.test_motor_following_color_test_touch_path);

        mBeePathPaint = new Paint();
        mBeePathPaint.setColor(mBeePathColor);
        mBeePathPaint.setStrokeWidth(mLineWidth);
        mBeePathPaint.setAntiAlias(true);
        mBeePathPaint.setStrokeCap(Paint.Cap.ROUND);
        mBeePathPaint.setStrokeJoin(Paint.Join.MITER);

        mTouchPaint = new Paint();
        mTouchPaint.setColor(mTouchPathColor);
        mTouchPaint.setStrokeWidth(mTouchLineWidth);
        mTouchPaint.setAntiAlias(true);
        mTouchPaint.setStrokeCap(Paint.Cap.ROUND);
        mTouchPaint.setStrokeJoin(Paint.Join.MITER);

        mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_1);
    }

    public void initView(boolean leftToRight) {
        mLeftToRight = leftToRight;
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();

        try {
            mResultBitmap = Bitmap.createBitmap((int) width, (int) height, mBeeBitmap.getConfig());
            mResultCanvas = new Canvas(mResultBitmap);
            mResultCanvas.drawColor(mBackgroundColor, PorterDuff.Mode.CLEAR);
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        try {
            mPathBitmap = Bitmap.createBitmap((int) width, (int) height, mBeeBitmap.getConfig());
            mPathCanvas = new Canvas(mPathBitmap);
            mPathCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        try {
            mTouchBitmap = Bitmap.createBitmap((int) width, (int) height, mBeeBitmap.getConfig());
            mTouchCanvas = new Canvas(mTouchBitmap);
            mTouchCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        mStartPoint = mLeftToRight ? new PointF(0, height / 2) : new PointF(width, height / 2);
        mEndPoint = mLeftToRight ? new PointF(width, height / 2) : new PointF(0, height / 2);

        mBeePosition = mLeftToRight ? new PointF(mBeeBitmap.getWidth() / 2, height / 2)
                : new PointF(width - mBeeBitmap.getWidth() / 2, height / 2);
        mBeeRect.set(mBeePosition.x - mBeeBitmap.getWidth() / 2, mBeePosition.y - mBeeBitmap.getHeight() / 2,
                mBeePosition.x + mBeeBitmap.getWidth() / 2, mBeePosition.y + mBeeBitmap.getHeight() / 2);
        mPrevBeePosition = mLeftToRight ? new PointF(mBeeBitmap.getWidth() / 2, height / 2)
                : new PointF(width - mBeeBitmap.getWidth() / 2, height / 2);

        ArrayList<Float> x = new ArrayList<>();
        ArrayList<Float> y = new ArrayList<>();
        int countPoints = Constants.COUNT_POINTS;
        Random random = new Random();
        boolean flag = true;

        float beeSize = Math.max(mBeeBitmap.getWidth(), mBeeBitmap.getHeight());

        if (mLeftToRight) {
            x.add(mStartPoint.x);
            y.add(mStartPoint.y);
        } else {
            x.add(mEndPoint.x);
            y.add(mEndPoint.y);
        }

        for (int i = 1; i < countPoints - 2; i++) {
            x.add(i * (width / (countPoints - 1)));
            float rHeight = 0;

            if (flag) {
                rHeight = (float) random.nextInt((int) height / 2);
            } else {
                rHeight = (float) random.nextInt((int) height / 2) + (height / 2);
            }

            if (rHeight < beeSize) {
                rHeight = beeSize;
            } else if (rHeight > height - beeSize) {
                rHeight = height - beeSize;
            }

            y.add(rHeight);
            flag = !flag;
        }

        if (mLeftToRight) {
            x.add(mEndPoint.x);
            y.add(mEndPoint.y);
        } else {
            x.add(mStartPoint.x);
            y.add(mStartPoint.y);
        }

        mSplineInterpolator = SplineInterpolator.createMonotoneCubicSpline(x, y);
        mState = State.Start;

        if (mListener != null) {
            long time = Calendar.getInstance().getTimeInMillis();
            mListener.onStart(time, mBeeRect.centerX(), mBeeRect.centerY(), mTouchX, mTouchY, mTouchPressure, mTouchSize);
        }
    }

    public Bitmap getBitmapResult() {
        Bitmap result = null;

        try {

            float width = getMeasuredWidth();
            float height = getMeasuredHeight();

            result = Bitmap.createBitmap((int) width, (int) height, mBeeBitmap.getConfig());
            Canvas resultCanvas = new Canvas(result);
            resultCanvas.drawColor(mBackgroundColor, PorterDuff.Mode.CLEAR);

            if (mSplineInterpolator != null) {
                if (mLeftToRight) {
                    int count = (int) mBeePosition.x;
                    int partSize = Constants.PATH_PART_SIZE;

                    for (int i = 0; i < count; i++) {
                        boolean value = (i / partSize) % 2 == 0;

                        if (value) {
                            float x = i;
                            float y = mSplineInterpolator.interpolate(x);
                            resultCanvas.drawPoint(x, y, mBeePathPaint);
                        }
                    }
                } else {
                    int count = (int) width - (int) mBeePosition.x;
                    int partSize = Constants.PATH_PART_SIZE;

                    for (int i = (int) mBeePosition.x; i < count; i++) {
                        boolean value = (i / partSize) % 2 == 0;

                        if (value) {
                            float x = i;
                            float y = mSplineInterpolator.interpolate(x);
                            resultCanvas.drawPoint(x, y, mBeePathPaint);
                        }
                    }
                }
            }

            resultCanvas.drawBitmap(mTouchBitmap, 0, 0, mTouchPaint);
            mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_1);

            if (mBeeBitmap != null) {
                double deltaX = mPrevBeePosition.x - mBeePosition.x;
                double deltaY = mPrevBeePosition.y - mBeePosition.y;
                double angle = Math.atan2(deltaY, deltaX) / Math.PI * 180 + 180;

                Matrix rotator = new Matrix();
                int xTranslate = (int) (mBeePosition.x - mBeeBitmap.getWidth() / 2);
                int yTranslate = (int) (mBeePosition.y - mBeeBitmap.getHeight() / 2);
                rotator.postTranslate(xTranslate, yTranslate);
                rotator.postRotate((int) angle, (int) mBeePosition.x, (int) mBeePosition.y);

                resultCanvas.drawBitmap(mBeeBitmap, rotator, mBeePathPaint);
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }

        return result;
    }

    public void reset(boolean leftToRight) {
        mLeftToRight = leftToRight;
        mIsBeeCaught = false;
        mIsBeeCaughtEnd = false;
        mIsBeeAnimating = false;
        mTouchPoints.clear();
        mResultCanvas.drawColor(mBackgroundColor, PorterDuff.Mode.CLEAR);
        mPathCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mPathCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        float width = getMeasuredWidth();
        float height = getMeasuredHeight();

        mBeePosition = mLeftToRight ? new PointF(mBeeBitmap.getWidth() / 2, height / 2)
                : new PointF(width - mBeeBitmap.getWidth() / 2, height / 2);
        mBeeRect.set(mBeePosition.x - mBeeBitmap.getWidth() / 2, mBeePosition.y - mBeeBitmap.getHeight() / 2,
                mBeePosition.x + mBeeBitmap.getWidth() / 2, mBeePosition.y + mBeeBitmap.getHeight() / 2);
        mPrevBeePosition = mLeftToRight ? new PointF(mBeeBitmap.getWidth() / 2, height / 2)
                : new PointF(width - mBeeBitmap.getWidth() / 2, height / 2);

        if (mState != State.Start) {
            mState = State.Start;

            if (mListener != null) {
                long time = Calendar.getInstance().getTimeInMillis();
                mListener.onStart(time, mBeeRect.centerX(), mBeeRect.centerY(), mTouchX, mTouchY, mTouchPressure, mTouchSize);
            }
        }

        invalidate();
    }

    public void move() {
        if (mSplineInterpolator != null) {
            float width = getMeasuredWidth();
            boolean isEnd = mLeftToRight ? mBeePosition.x < width - mBeeBitmap.getWidth() / 2
                    : mBeePosition.x > mBeeBitmap.getWidth() / 2;

            if (isEnd) {
                float newX = mLeftToRight ? mBeePosition.x + Constants.BEE_SPEED : mBeePosition.x - Constants.BEE_SPEED;
                float newY = mSplineInterpolator.interpolate(newX);

                mPrevBeePosition.set(mBeePosition);
                mBeePosition.set(newX, newY);
                mBeeRect.set(mBeePosition.x - mBeeBitmap.getWidth() / 2, mBeePosition.y - mBeeBitmap.getHeight() / 2,
                        mBeePosition.x + mBeeBitmap.getWidth() / 2, mBeePosition.y + mBeeBitmap.getHeight() / 2);

                if (mState != State.InProgress) {
                    mState = State.InProgress;
                }

                if (mListener != null) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    mListener.onProgressChanged(time, mBeeRect.centerX(), mBeeRect.centerY(), mTouchX, mTouchY, mTouchPressure, mTouchSize);
                }

                invalidate();
            } else {
                if (mState != State.End) {
                    mState = State.End;

                    if (mListener != null) {
                        long time = Calendar.getInstance().getTimeInMillis();
                        mListener.onEnd(time, mBeeRect.centerX(), mBeeRect.centerY(), mTouchX, mTouchY, mTouchPressure, mTouchSize);
                    }

                    mPathCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    invalidate();
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);

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

        if (mTouchCount <= 1) {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                mTouchX = event.getX();
                mTouchY = event.getY();
                mTouchPressure = event.getPressure();
                mTouchSize = event.getSize();

                if (mListener != null && mState == State.End) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    mListener.onProgressChanged(time, mBeeRect.centerX(), mBeeRect.centerY(), mTouchX, mTouchY, mTouchPressure, mTouchSize);
                }

                boolean eventSent = false;

                if (!mIsBeeCaught && mListener != null && mBeeRect.contains(mTouchX, mTouchY)) {
                    mIsBeeCaught = true;
                    long time = Calendar.getInstance().getTimeInMillis();
                    mListener.onBeeCaught(time, mState == State.InProgress, mBeeRect.centerX(), mBeeRect.centerY(), mTouchX, mTouchY, mTouchPressure, mTouchSize);
                    eventSent = true;
                }

                if (!mIsBeeCaughtEnd && mBeeRect.contains(mTouchX, mTouchY) && mState == State.End) {
                    mIsBeeCaughtEnd = true;

                    if (!eventSent) {
                        long time = Calendar.getInstance().getTimeInMillis();
                        mListener.onBeeCaught(time, mState == State.End, mBeeRect.centerX(), mBeeRect.centerY(), mTouchX, mTouchY, mTouchPressure, mTouchSize);
                    }

                    startBeeAnimation();
                }
            } else {
                mTouchX = 0;
                mTouchY = 0;
                mTouchPressure = 0;
                mTouchSize = 0;
                mIsBeeCaught = false;
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                mTouchPoints.clear();
            } else {
                PointF point = new PointF();
                point.x = event.getX();
                point.y = event.getY();
                mTouchPoints.add(point);
            }

            invalidate();
        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mBackgroundColor);

        // draw bee path
        if (mSplineInterpolator != null) {
            int count = Constants.BEE_PATH_LONG;
            int partSize = Constants.PATH_PART_SIZE;
            mPathCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

            if (mState != State.End) {
                if (mLeftToRight) {
                    for (int i = (int) mBeePosition.x - count; i < (int) mBeePosition.x; i++) {
                        boolean value = (i / partSize) % 2 == 0;

                        if (value) {
                            float x = i;
                            float y = mSplineInterpolator.interpolate(x);
                            canvas.drawPoint(x, y, mBeePathPaint);
                            mPathCanvas.drawPoint(x, y, mBeePathPaint);
                        }
                    }
                } else {
                    for (int i = (int) mBeePosition.x; i < (int) mBeePosition.x + count; i++) {
                        boolean value = (i / partSize) % 2 == 0;

                        if (value) {
                            float x = i;
                            float y = mSplineInterpolator.interpolate(x);
                            canvas.drawPoint(x, y, mBeePathPaint);
                            mPathCanvas.drawPoint(x, y, mBeePathPaint);
                        }
                    }
                }
            }
        }

        for (PointF point : mTouchPoints) {
            if (mTouchPoints.indexOf(point) == 0) {
                mTouchCanvas.drawCircle(point.x, point.y, mLineWidth / 2, mTouchPaint);
            } else {
                PointF last = mTouchPoints.get(mTouchPoints.indexOf(point) - 1);
                mTouchCanvas.drawLine(last.x, last.y, point.x, point.y, mTouchPaint);
            }
        }

        canvas.drawBitmap(mResultBitmap, 0, 0, mTouchPaint);
        canvas.drawBitmap(mPathBitmap, 0, 0, mTouchPaint);
        canvas.drawBitmap(mTouchBitmap, 0, 0, mTouchPaint);

        if (mBeeBitmap != null) {
            double deltaX = mPrevBeePosition.x - mBeePosition.x;
            double deltaY = mPrevBeePosition.y - mBeePosition.y;
            double result = Math.atan2(deltaY, deltaX) / Math.PI * 180 + 180;
            double angle = result;

            Matrix rotator = new Matrix();
            int xTranslate = (int) (mBeePosition.x - mBeeBitmap.getWidth() / 2);
            int yTranslate = (int) (mBeePosition.y - mBeeBitmap.getHeight() / 2);
            rotator.postTranslate(xTranslate, yTranslate);
            rotator.postRotate((int) angle, (int) mBeePosition.x, (int) mBeePosition.y);
            canvas.drawBitmap(mBeeBitmap, rotator, mBeePathPaint);
        }
    }

    private void startBeeAnimation() {
        if (!mIsBeeAnimating) {
            if (mListener != null) {
                mListener.onAnimationStart();
            }

            postDelayed(() -> {
                mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_2);
                invalidate();
            }, 0);

            postDelayed(() -> {
                mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_3);
                invalidate();
            }, 300);

            postDelayed(() -> {
                mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_4);
                invalidate();
            }, 600);

            postDelayed(() -> {
                mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_5);
                invalidate();
            }, 900);

            postDelayed(() -> {
                mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_6);
                invalidate();
            }, 1200);

            postDelayed(() -> {
                mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_1);
                invalidate();
            }, 1500);

            postDelayed(() -> {
                mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_2);
                invalidate();
            }, 1800);

            postDelayed(() -> {
                mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_3);
                invalidate();
            }, 2100);

            postDelayed(() -> {
                mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_4);
                invalidate();
            }, 2400);

            postDelayed(() -> {
                mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_5);
                invalidate();
            }, 2700);

            postDelayed(() -> {
                mBeeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bee_color_1);
                invalidate();

                mIsBeeAnimating = false;

                if (mListener != null) {
                    mListener.onAnimationCompleted();
                }
            }, 3000);
        }
    }
}
