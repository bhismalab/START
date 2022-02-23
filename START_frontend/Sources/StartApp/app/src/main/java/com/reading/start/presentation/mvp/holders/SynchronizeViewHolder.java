package com.reading.start.presentation.mvp.holders;

import android.widget.ImageView;

public class SynchronizeViewHolder {

    private ImageView mSynchronizeTextImage;

    private ImageView mProgressImageView;

    private ImageView mLogo;

    public SynchronizeViewHolder(ImageView synchronizeTextImage, ImageView progress, ImageView logo) {
        mSynchronizeTextImage = synchronizeTextImage;
        mProgressImageView = progress;
        mLogo = logo;
    }

    public ImageView getSynchronizeTextImage() {
        return mSynchronizeTextImage;
    }

    public ImageView getProgressImageView() {
        return mProgressImageView;
    }

    public ImageView getLogo() {
        return mLogo;
    }
}
