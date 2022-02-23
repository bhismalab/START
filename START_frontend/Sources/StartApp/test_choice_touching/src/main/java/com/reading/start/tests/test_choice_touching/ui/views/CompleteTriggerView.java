package com.reading.start.tests.test_choice_touching.ui.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

public class CompleteTriggerView extends View {
    private static final String TAG = CompleteTriggerView.class.getSimpleName();

    public interface CompleteTriggerViewListener {
        void onCompleteTrigger();

        void onTouchChanged(float x, float y, float touchPressure, float touchSize, int touchCount);

        boolean onCountFingerChanged(int count);
    }

    private CompleteTriggerViewListener mListener = null;

    private int mTouchCount = 0;

    private float mCurrentX = 0;

    private float mCurrentY = 0;

    private float mCurrentTouchPressure = 0;

    private float mCurrentTouchSize = 0;

    private boolean mTopLeftTap = false;
    private boolean mTopRightTap = false;
    private boolean mBottomLeftTap = false;
    private boolean mBottomRightTap = false;
    private long mLastTapTime = 0;

    public CompleteTriggerView(Context context) {
        super(context);
        init();
    }

    public CompleteTriggerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CompleteTriggerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setListener(CompleteTriggerViewListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int oldTouchCount = mTouchCount;

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
        }

        mCurrentX = event.getX();
        mCurrentY = event.getY();
        mCurrentTouchPressure = event.getPressure();
        mCurrentTouchSize = event.getSize();

        if (mListener != null) {
            mListener.onTouchChanged(mCurrentX, mCurrentY, mCurrentTouchPressure, mCurrentTouchSize, mTouchCount);

            if (!detectCompleteTouch(event)) {
                mTopLeftTap = false;
                mTopRightTap = false;
                mBottomLeftTap = false;
                mBottomRightTap = false;
            }

            if (oldTouchCount != mTouchCount && mListener.onCountFingerChanged(mTouchCount)) {
                return true;
            }
        }

        if (getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) getParent();

            if (parent.getChildCount() > 1) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    if (parent.getChildAt(i) != this) {
                        parent.getChildAt(i).dispatchTouchEvent(event);
                    }
                }
            }
        }

        return true;
    }

    public float getCurrentX() {
        return mCurrentX;
    }

    public float getCurrentY() {
        return mCurrentY;
    }

    public float getCurrentTouchPressure() {
        return mCurrentTouchPressure;
    }

    public float getCurrentTouchSize() {
        return mCurrentTouchSize;
    }

    private void init() {
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
