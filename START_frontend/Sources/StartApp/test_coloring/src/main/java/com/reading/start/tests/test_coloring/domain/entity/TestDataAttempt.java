package com.reading.start.tests.test_coloring.domain.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TestDataAttempt {
    public static final String FILED_ITEMS = "items";

    public static final String FILED_IMAGE_DUMP = "image_dump";

    public static final String FILED_SCALED_IMAGE_DUMP = "scaled_image_dump";

    public static final String FILED_IMAGE_WIDTH = "image_width";

    public static final String FILED_IMAGE_HEIGHT = "image_height";

    public static final String FILED_IMAGE_NAME = "image_name";

    @SerializedName(FILED_IMAGE_WIDTH)
    private int mImageWidth = 0;

    @SerializedName(FILED_IMAGE_HEIGHT)
    private int mImageHeight = 0;

    @SerializedName(FILED_IMAGE_DUMP)
    private String mImageDump = null;

    @SerializedName(FILED_SCALED_IMAGE_DUMP)
    private String mScaledImageDump = null;

    @SerializedName(FILED_ITEMS)
    private ArrayList<TestDataItem> mItems = new ArrayList<>();

    @SerializedName(FILED_IMAGE_NAME)
    private String mImageName = null;

    public ArrayList<TestDataItem> getItems() {
        return mItems;
    }

    public String getImageDump() {
        return mImageDump;
    }

    public void setImageDump(String value) {
        mImageDump = value;
    }

    public int getImageWidth() {
        return mImageWidth;
    }

    public void setImageWidth(int imageWidth) {
        mImageWidth = imageWidth;
    }

    public int getImageHeight() {
        return mImageHeight;
    }

    public void setImageHeight(int imageHeight) {
        mImageHeight = imageHeight;
    }

    public String getScaledImageDump() {
        return mScaledImageDump;
    }

    public void setScaledImageDump(String scaledImageDump) {
        mScaledImageDump = scaledImageDump;
    }

    public String getImageName() {
        return mImageName;
    }

    public void setImageName(String imageName) {
        mImageName = imageName;
    }
}
