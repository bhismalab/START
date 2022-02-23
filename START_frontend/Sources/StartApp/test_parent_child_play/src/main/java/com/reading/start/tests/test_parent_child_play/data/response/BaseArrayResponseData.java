package com.reading.start.tests.test_parent_child_play.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public abstract class BaseArrayResponseData<T> {
    @SerializedName("success")
    boolean mSuccess;

    @SerializedName("message")
    String mMessage;

    @SerializedName("data")
    ArrayList<T> mData;

    private int mCode = 0;

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getMessage() {
        return mMessage;
    }

    public ArrayList<T> getData() {
        return mData;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int value) {
        mCode = value;
    }
}
