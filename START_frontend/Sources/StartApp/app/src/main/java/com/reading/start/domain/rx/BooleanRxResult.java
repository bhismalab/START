package com.reading.start.domain.rx;

/**
 * Represent boolean result fro RxJava
 */
public class BooleanRxResult extends RxResult<Boolean> {
    public BooleanRxResult(RxStatus status, Boolean result, String message) {
        super(status, result, message);
    }

    public BooleanRxResult(RxStatus status, Boolean result) {
        super(status, result);
    }

    public BooleanRxResult(RxStatus status) {
        super(status);
    }
}
