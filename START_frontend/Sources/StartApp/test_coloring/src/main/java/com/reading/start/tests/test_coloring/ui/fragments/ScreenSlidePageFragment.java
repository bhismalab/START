package com.reading.start.tests.test_coloring.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.reading.start.tests.test_coloring.R;
import com.reading.start.tests.test_coloring.utils.BitmapUtils;

public class ScreenSlidePageFragment extends Fragment {
    private static final String PIC_URL = "screenslidepagefragment.picurl";

    public static ScreenSlidePageFragment newInstance(String picUrl) {
        Bundle arguments = new Bundle();
        arguments.putString(PIC_URL, picUrl);
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.test_coloring_fragment_slide_page, container, false);

        ImageView imageView = rootView.findViewById(R.id.image);
        Bundle arguments = getArguments();
        String value = arguments.getString(PIC_URL);

        imageView.setImageBitmap(BitmapUtils.bitmapFromBase64(value));

        return rootView;
    }
}