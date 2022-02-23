package com.reading.start.presentation.mvp.presenters;

import com.reading.start.AppCore;
import com.reading.start.data.DataBaseProvider;
import com.reading.start.presentation.mvp.models.BaseModel;

import io.reactivex.disposables.CompositeDisposable;
import io.realm.Realm;

public abstract class BasePresenter<V, M extends BaseModel, H> implements IBasePresenter {
    private CompositeDisposable mSubscriptions;

    private V mView;

    private M mModel;

    private H mViewHolder;

    protected V getView() {
        return mView;
    }

    public void setView(V view) {
        mView = view;
    }

    protected M getModel() {
        return mModel;
    }

    public void setModel(M model) {
        mModel = model;
    }

    public H getViewHolder() {
        return mViewHolder;
    }

    public void setViewHolder(H viewHolder) {
        mViewHolder = viewHolder;
    }

    private Realm mRealm = null;

    public BasePresenter(boolean createRealm) {
        if (createRealm) {
            mRealm = DataBaseProvider.getInstance(AppCore.getInstance()).getRealm();
        }
    }

    public void init(V view, H viewHolder) {
        mView = view;
        mViewHolder = viewHolder;
    }

    protected Realm getRealm() {
        return mRealm;
    }

    protected CompositeDisposable getSubscriptions() {
        if (mSubscriptions == null) {
            mSubscriptions = new CompositeDisposable();
        }

        return mSubscriptions;
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
            mRealm = null;
        }

        mView = null;
        unsubscribe();
    }

    private void unsubscribe() {
        if (mSubscriptions != null && !mSubscriptions.isDisposed()) {
            mSubscriptions.dispose();
            mSubscriptions = null;
        }
    }
}
