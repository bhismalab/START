package com.reading.start.tests.test_wheel.domain.entity;

import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

public class ColorButton {
    private static final String TAG = ColorButton.class.getSimpleName();

    private static final float OFFSET_X = 200f;
    private static final float OFFSET_Y = 200f;

    public interface ColorButtonListener {
        void onClick(ColorButton bubble);
    }

    private View mView;

    private float mX;
    private float mY;

    private RectF mBound = null;

    private boolean mIsVisible = false;

    private ColorButtonListener mListener = null;

    private static Random mRandom = new Random();

    public ColorButton(ImageView imageView, float x, float y, RectF bounds, ColorButtonListener listener) {
        mView = imageView;
        mX = x;
        mY = y;
        mBound = bounds;
        mListener = listener;
        mView.setX(mX);
        mView.setY(mY);

        mView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClick(ColorButton.this);
            }
        });

        hide();
    }

    public boolean isVisible() {
        return mIsVisible;
    }

    public void hide() {
        mIsVisible = false;

        if (mView != null) {
            mView.setVisibility(View.GONE);
        }
    }

    public void show() {
        mIsVisible = true;

        if (mView != null) {
            mView.setX(mX);
            mView.setY(mY);
            mView.setVisibility(View.VISIBLE);
            mView.invalidate();
        }
    }

    public void moveToNextRandomPosition() {
        float viewWeight = mView.getMeasuredWidth();
        float viewHeight = mView.getMeasuredHeight();

        mX = mRandom.nextFloat() * mBound.width();

        if (mX + viewWeight > mBound.width()) {
            mX -= Math.abs(mX + viewWeight - mBound.width());
        }

        mY = mRandom.nextFloat() * mBound.height();

        if (mY + viewHeight > mBound.height()) {
            mY -= Math.abs(mY + viewHeight - mBound.height());
        }

        mView.setX(mX);
        mView.setY(mY);
        mView.invalidate();
    }

    public void move() {
        float viewWeight = mView.getMeasuredWidth();
        float viewHeight = mView.getMeasuredHeight();

        mX = mRandom.nextFloat() * mBound.width();
        //mX += mRandom.nextFloat() * OFFSET_X * (mRandom.nextBoolean() ? 1 : -1);

        if (mX < 0) {
            mX = 0;
        }

        if (mX + viewWeight > mBound.width()) {
            mX -= Math.abs(mX + viewWeight - mBound.width());
        }

        mY = mRandom.nextFloat() * mBound.height();
        //mY += mRandom.nextFloat() * OFFSET_X * (mRandom.nextBoolean() ? 1 : -1);

        if (mY < 0) {
            mY = 0;
        }

        if (mY + viewHeight > mBound.height()) {
            mY -= Math.abs(mY + viewHeight - mBound.height());
        }

        mView.setX(mX);
        mView.setY(mY);
        mView.invalidate();
    }

    public boolean isIntersect(ColorButton button) {
        boolean result = false;

        if (button != null) {
            RectF bound1 = new RectF(mView.getX(), mView.getY(), mView.getX() + mView.getMeasuredWidth(), mView.getY() + mView.getMeasuredHeight());
            RectF bound2 = new RectF(button.mView.getX(), button.mView.getY(), button.mView.getX() + button.mView.getMeasuredWidth(), button.mView.getY() + button.mView.getMeasuredHeight());
            result = bound1.intersect(bound2);
        }

        return result;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public View getView() {
        return mView;
    }
}
