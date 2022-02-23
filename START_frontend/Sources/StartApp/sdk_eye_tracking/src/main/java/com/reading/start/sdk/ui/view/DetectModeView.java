package com.reading.start.sdk.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.reading.start.sdk.core.DetectMode;
import com.reading.start.sdk_eye_tracking.R;

/**
 * View displaying position of gaze on the screen
 */
public class DetectModeView extends View {

    private DetectMode mDetectMode = null;

    private int mCol = -1;

    private int mRow = -1;

    private Paint mRectPaint = null;

    public DetectModeView(Context context) {
        super(context);
    }

    public DetectModeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DetectModeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DetectModeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DetectMode getDetectMode() {
        return mDetectMode;
    }

    public void setDetectMode(DetectMode detectMode) {
        mDetectMode = detectMode;
    }

    public void setPosition(int col, int row) {
        mCol = col;
        mRow = row;
        invalidate();
    }

    public void reset() {
        mCol = -1;
        mRow = -1;
        invalidate();
    }

    public int getCol() {
        return mCol;
    }

    public int getRow() {
        return mRow;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mCol != -1 && mRow != -1) {
            if (mRectPaint == null) {
                mRectPaint = new Paint();
                mRectPaint.setColor(getResources().getColor(R.color.colorSelectedColor));
                mRectPaint.setAlpha(127);
            }

            int x = mCol * getWidthPart();
            int y = mRow * getHeightPart();

            Rect r = new Rect(x, y, x + getWidthPart(), y + getHeightPart());
            canvas.drawRect(r, mRectPaint);
        }
    }

    private int getWidthPart() {
        int result = 0;

        if (mDetectMode != null) {
            switch (mDetectMode) {
                case Mode2x1: {
                    result = getMeasuredWidth() / 2;
                    break;
                }
                case Mode3x1: {
                    result = getMeasuredWidth() / 3;
                    break;
                }
                case Mode2x2: {
                    result = getMeasuredWidth() / 2;
                    break;
                }
                case Mode3x2: {
                    result = getMeasuredWidth() / 3;
                    break;
                }
                case Mode4x2: {
                    result = getMeasuredWidth() / 4;
                    break;
                }
            }
        }

        return result;
    }

    private int getHeightPart() {
        int result = 0;

        if (mDetectMode != null) {
            switch (mDetectMode) {
                case Mode2x1: {
                    result = getMeasuredHeight();
                    break;
                }
                case Mode3x1: {
                    result = getMeasuredHeight();
                    break;
                }
                case Mode2x2: {
                    result = getMeasuredHeight() / 2;
                    break;
                }
                case Mode3x2: {
                    result = getMeasuredHeight() / 2;
                    break;
                }
                case Mode4x2: {
                    result = getMeasuredHeight() / 2;
                    break;
                }
            }
        }

        return result;
    }
}
