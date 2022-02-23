package com.reading.start.tests.test_choice_touching.data.response;

import com.google.gson.annotations.SerializedName;

public abstract class BaseResponseData<T> {
    @SerializedName("success")
    boolean mSuccess;

    @SerializedName("message")
    String mMessage;

    @SerializedName("data")
    T mData;

    private int mCode = 0;

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getMessage() {
        return mMessage;
    }

    public T getData() {
        return mData;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int value) {
        mCode = value;
    }
}
