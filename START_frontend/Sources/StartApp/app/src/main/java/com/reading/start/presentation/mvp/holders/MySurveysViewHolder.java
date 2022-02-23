package com.reading.start.presentation.mvp.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

public class MySurveysViewHolder {

    private View mActionBar;

    private ImageView mMenu;

    private RecyclerView mRecyclerView;

    public MySurveysViewHolder(View actionBar, ImageView menu, RecyclerView recyclerView) {
        mActionBar = actionBar;
        mMenu = menu;
        mRecyclerView = recyclerView;
    }

    public View getActionBar() {
        return mActionBar;
    }

    public ImageView getMenu() {
        return mMenu;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }
}
