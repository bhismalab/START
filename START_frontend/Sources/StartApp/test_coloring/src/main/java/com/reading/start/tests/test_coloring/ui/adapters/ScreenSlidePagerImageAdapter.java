package com.reading.start.tests.test_coloring.ui.adapters;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.reading.start.tests.test_coloring.ui.fragments.ScreenSlidePageImageFragment;

import java.util.ArrayList;
import java.util.List;

public class ScreenSlidePagerImageAdapter extends FragmentStatePagerAdapter {
    private List<Bitmap> mList = new ArrayList<>();

    public ScreenSlidePagerImageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return ScreenSlidePageImageFragment.newInstance(mList.get(i));
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public void addAll(List<Bitmap> picList) {
        mList = picList;
    }

    public void add(Bitmap value) {
        mList.add(value);
    }
}
