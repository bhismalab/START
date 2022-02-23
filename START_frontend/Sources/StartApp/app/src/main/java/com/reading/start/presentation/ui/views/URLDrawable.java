package com.reading.start.presentation.ui.views;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Helper class uses for displaying images in WebView
 */
public class URLDrawable extends BitmapDrawable {
    // the drawable that you need to set, you could set the initial drawing
    // with the loading image if you need to
    protected Drawable drawable;

    @Override
    public void draw(Canvas canvas) {
        // override the draw to facilitate refresh function later
        if(drawable != null) {
            drawable.draw(canvas);
        }
    }
}