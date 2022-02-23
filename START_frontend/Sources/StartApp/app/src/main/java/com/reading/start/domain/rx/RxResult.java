package com.reading.start.domain.rx;

/**
 * Represent base class for RxJava result
 */
public class RxResult<V> {
    private RxStatus mStatus = RxStatus.None;

    private V mResult = null;

    private String mMessage = null;

    public RxResult(RxStatus status, V result, String message) {
        mStatus = status;
        mResult = result;
        mMessage = message;
    }

    public RxResult(RxStatus status, V result) {
        mStatus = status;
        mResult = result;
    }

    public RxResult(RxStatus status) {
        mStatus = status;
    }

    public RxStatus getStatus() {
        return mStatus;
    }

    public V getResult() {
        return mResult;
    }

    public String getMessage() {
        return mMessage;
    }
}
