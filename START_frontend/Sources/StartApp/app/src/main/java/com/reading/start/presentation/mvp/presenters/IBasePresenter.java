package com.reading.start.presentation.mvp.presenters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public interface IBasePresenter {
    void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onDestroy();
}
