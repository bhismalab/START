package com.reading.start.presentation.mvp.presenters;

import com.jakewharton.rxbinding2.view.RxView;
import com.reading.start.presentation.mvp.holders.ActionBarViewHolder;
import com.reading.start.presentation.mvp.views.ActionBarView;

public class ActionBarPresenter {

    private ActionBarView mView;

    private ActionBarViewHolder mViewHolder;

    public ActionBarPresenter() {
    }

    public void init(ActionBarView view, ActionBarViewHolder viewHolder) {
        mView = view;
        mViewHolder = viewHolder;
    }

    public void onCreate() {
        if (getViewHolder() != null) {
            RxView.clicks(getViewHolder().getBackButton()).subscribe(aVoid -> getView().onBackPressed());
            RxView.clicks(getViewHolder().getHomeButton()).subscribe(aVoid -> getView().onHomePressed());
            RxView.clicks(getViewHolder().getNavigationMenuButton()).subscribe(aVoid -> getView().onNavigationMenuPressed());
        }
    }

    public void onDestroy() {
        mView = null;
    }

    public ActionBarView getView() {
        return mView;
    }

    public void setView(ActionBarView view) {
        mView = view;
    }

    public ActionBarViewHolder getViewHolder() {
        return mViewHolder;
    }

    public void setViewHolder(ActionBarViewHolder viewHolder) {
        mViewHolder = viewHolder;
    }
}
