package com.reading.start.tests.test_coloring.data.entity;

import com.google.gson.annotations.SerializedName;

public class DataAttachmentId {
    @SerializedName("attachment_id")
    private int mAttachmentId = -1;

    public int getAttachmentId() {
        return mAttachmentId;
    }
}
