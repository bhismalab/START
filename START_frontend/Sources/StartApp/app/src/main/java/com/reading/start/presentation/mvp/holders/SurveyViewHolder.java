package com.reading.start.presentation.mvp.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SurveyViewHolder {

    private View mActionBar;

    private RecyclerView mRecyclerView;

    private ImageView mPhoto;

    private TextView mName;

    private TextView mChildInfo;

    private View mUploadSurvey;

    private View mDeleteSurvey;

    public SurveyViewHolder(View actionBar, RecyclerView recyclerView, ImageView photo, TextView name,
                            TextView childInfo, View uploadSurvey, View deleteSurvey) {
        mActionBar = actionBar;
        mRecyclerView = recyclerView;
        mPhoto = photo;
        mName = name;
        mChildInfo = childInfo;
        mUploadSurvey = uploadSurvey;
        mDeleteSurvey = deleteSurvey;
    }

    public View getActionBar() {
        return mActionBar;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
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

    public View getUploadSurvey() {
        return mUploadSurvey;
    }

    public View getDeleteSurvey() {
        return mDeleteSurvey;
    }
}
