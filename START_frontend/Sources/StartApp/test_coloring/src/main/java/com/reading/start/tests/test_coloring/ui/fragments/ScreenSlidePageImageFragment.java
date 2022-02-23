package com.reading.start.tests.test_coloring.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.reading.start.tests.test_coloring.R;

import java.io.ByteArrayOutputStream;

public class ScreenSlidePageImageFragment extends Fragment {
    private static final String PIC_URL = "screenslidepagefragment.picurl";

    public static ScreenSlidePageImageFragment newInstance(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        Bundle arguments = new Bundle();
        arguments.putByteArray(PIC_URL, byteArray);
        ScreenSlidePageImageFragment fragment = new ScreenSlidePageImageFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.test_coloring_fragment_slide_page, container, false);

        ImageView imageView = rootView.findViewById(R.id.image);
        Bundle arguments = getArguments();

        byte[] byteArray = arguments.getByteArray(PIC_URL);
        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(image);

        return rootView;
    }
}