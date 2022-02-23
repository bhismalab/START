package com.reading.start.presentation.ui.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.reading.start.R;

/**
 * Custom view for displaying circle progress.
 */
public class CircularProgressBar extends View {
    private float mProgress = 0;
    private float mStrokeWidth = getResources().getDimension(R.dimen.default_stroke_width);
    private float mBackgroundStrokeWidth = getResources().getDimension(R.dimen.default_background_stroke_width);
    private int mColor = Color.BLACK;
    private int mBackgroundColor = Color.GRAY;

    private int mStartAngle = -90;
    private RectF mRectF;
    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mRectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, 0, 0);

        try {
            mProgress = typedArray.getFloat(R.styleable.CircularProgressBar_cpb_progress, mProgress);
            mStrokeWidth = typedArray.getDimension(R.styleable.CircularProgressBar_cpb_progressbar_width, mStrokeWidth);
            mBackgroundStrokeWidth = typedArray.getDimension(R.styleable.CircularProgressBar_cpb_background_progressbar_width, mBackgroundStrokeWidth);
            mColor = typedArray.getInt(R.styleable.CircularProgressBar_cpb_progressbar_color, mColor);
            mBackgroundColor = typedArray.getInt(R.styleable.CircularProgressBar_cpb_background_progressbar_color, mBackgroundColor);
        } finally {
            typedArray.recycle();
        }

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackgroundPaint.setStrokeWidth(mBackgroundStrokeWidth);

        mForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mForegroundPaint.setColor(mColor);
        mForegroundPaint.setStrokeCap(Paint.Cap.ROUND);
        mForegroundPaint.setStyle(Paint.Style.STROKE);
        mForegroundPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(mRectF, mBackgroundPaint);
        float angle = 360 * mProgress / 100;
        canvas.drawArc(mRectF, mStartAngle, angle, false, mForegroundPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        float highStroke = (mStrokeWidth > mBackgroundStrokeWidth) ? mStrokeWidth : mBackgroundStrokeWidth;
        mRectF.set(0 + highStroke / 2, 0 + highStroke / 2, min - highStroke / 2, min - highStroke / 2);
    }

    public float getProgress() {
        return mProgress;
    }

    public void setProgress(float progress) {
        mProgress = (progress <= 100) ? progress : 100;
        invalidate();
    }

    public float getProgressBarWidth() {
        return mStrokeWidth;
    }

    public void setProgressBarWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
        mForegroundPaint.setStrokeWidth(strokeWidth);
        requestLayout();
        invalidate();
    }

    public float getBackgroundProgressBarWidth() {
        return mBackgroundStrokeWidth;
    }

    public void setBackgroundProgressBarWidth(float backgroundStrokeWidth) {
        mBackgroundStrokeWidth = backgroundStrokeWidth;
        mBackgroundPaint.setStrokeWidth(backgroundStrokeWidth);
        requestLayout();
        invalidate();
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
        mForegroundPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
        mBackgroundPaint.setColor(backgroundColor);
        invalidate();
        requestLayout();
    }

    public void setProgressWithAnimation(float progress) {
        setProgressWithAnimation(progress, 1500);
    }

    public void setProgressWithAnimation(float progress, int duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }
}