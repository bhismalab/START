package com.reading.start.tests.test_jabble.domain.entity;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

public class Bubble {
    public enum BubbleDirection {
        Down,
        Up
    }

    public interface BubbleListener {
        void onClick(Bubble bubble);
    }

    private String mName;

    private View mView;

    private int mSpeed;

    private BubbleDirection mDirection = BubbleDirection.Down;

    private float mMinY = 0;
    private float mMaxY = 0;

    private float mX;
    private float mY;

    private boolean mIsVisible = false;

    private boolean mIsPopped = false;

    private BubbleListener mListener = null;

    public Bubble(String name, ImageView imageView, int speed, BubbleDirection direction,
                  float x, float y, float minYContainer, float maxYContainer, BubbleListener listener) {
        mName = name;
        mView = imageView;
        mSpeed = speed;
        mDirection = direction;
        mX = x;
        mY = y;
        mMinY = minYContainer;
        mMaxY = maxYContainer;
        mListener = listener;

        mView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClick(Bubble.this);
            }
        });

        hide();
    }

    public boolean isVisible() {
        return mIsVisible;
    }

    public boolean isPopped() {
        return mIsPopped;
    }

    public void setIsPopped(boolean isPopped) {
        mIsPopped = isPopped;
    }

    public void hideAnimation() {
        if (mIsVisible) {
            mIsVisible = false;
            mView.animate().alpha(0).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(() -> {
                if (mView != null) {
                    mView.setAlpha(1f);
                    mView.setVisibility(View.GONE);
                }
            }).start();
        }
    }

    public void hide() {
        mIsVisible = false;

        if (mView != null) {
            mView.setVisibility(View.GONE);
        }
    }

    public void show() {
        mIsPopped = false;
        mIsVisible = true;

        if (mView != null) {
            mView.setAlpha(1f);
            mView.setVisibility(View.VISIBLE);
        }
    }

    public void move() {
        float viewHeight = mView.getMeasuredHeight();

        if (mDirection == BubbleDirection.Down) {
            if (mY + viewHeight >= mMaxY) {
                mDirection = BubbleDirection.Up;
            } else {
                mY += mSpeed;
            }
        } else {
            if (mY <= mMinY) {
                mDirection = BubbleDirection.Down;
            } else {
                mY -= mSpeed;
            }
        }

        mView.setX(mX);
        mView.setY(mY);

        mView.invalidate();
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    public float getCenterX() {
        if (mView != null) {
            return mX + mView.getWidth() / 2;
        } else {
            return mX;
        }
    }

    public float getCenterY() {
        if (mView != null) {
            return mY + mView.getHeight() / 2;
        } else {
            return mY;
        }
    }

    public String getName() {
        return mName;
    }
}
