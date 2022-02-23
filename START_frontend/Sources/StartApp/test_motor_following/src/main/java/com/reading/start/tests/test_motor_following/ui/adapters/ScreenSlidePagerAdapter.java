package com.reading.start.tests.test_motor_following.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.reading.start.tests.test_motor_following.ui.fragments.ScreenSlidePageFragment;

import java.util.ArrayList;
import java.util.List;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private List<String> mList = new ArrayList<>();

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return ScreenSlidePageFragment.newInstance(mList.get(i));
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public void addAll(List<String> picList) {
        mList = picList;
    }

    public void add(String value) {
        mList.add(value);
    }
}
