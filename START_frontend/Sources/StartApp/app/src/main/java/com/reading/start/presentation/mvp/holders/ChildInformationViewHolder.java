package com.reading.start.presentation.mvp.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ChildInformationViewHolder {

    private View mActionBar;

    private RecyclerView mRecyclerView;

    private ImageView mPhoto;

    private TextView mName;

    private TextView mChildInfo;

    private View mEdit;

    public ChildInformationViewHolder(View actionBar, RecyclerView recyclerView, ImageView photo,
                                      TextView name, TextView childInfo, View edit) {
        mActionBar = actionBar;
        mRecyclerView = recyclerView;
        mPhoto = photo;
        mName = name;
        mChildInfo = childInfo;
        mEdit = edit;
    }

    public View getActionBar() {
        return mActionBar;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    public ImageView getPhoto() {
        return mPhoto;
    }

    public TextView getName() {
        return mName;
    }

    public TextView getChildInfo() {
        return mChildInfo;
    }

    public View getEdit() {
        return mEdit;
    }
}
