package com.reading.start.presentation.mvp.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ActionBarViewHolder {

    ImageView mHomeButton;

    ImageView mBackButton;

    ImageView mInfoButton;

    ImageView mNavigationMenuButton;

    TextView mTextTitle;

    View mView;

    public ActionBarViewHolder(ImageView homeButton, ImageView backButton,ImageView navigationMenuButton,
                               TextView textTitle, View view) {
        mHomeButton = homeButton;
        mBackButton = backButton;
        mNavigationMenuButton = navigationMenuButton;
        mTextTitle = textTitle;
        mView = view;
    }

    public ImageView getHomeButton() {
        return mHomeButton;
    }

    public ImageView getBackButton() {
        return mBackButton;
    }

    public ImageView getInfoButton() {
        return mInfoButton;
    }

    public ImageView getNavigationMenuButton() {
        return mNavigationMenuButton;
    }

    public TextView getTextTitle() {
        return mTextTitle;
    }

    public View getView() {
        return mView;
    }
}
