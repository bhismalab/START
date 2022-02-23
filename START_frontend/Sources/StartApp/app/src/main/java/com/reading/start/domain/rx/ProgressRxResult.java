package com.reading.start.domain.rx;

/**
 * Represent Integer result fro RxJava
 */
public class ProgressRxResult extends RxResult<Integer> {
    public ProgressRxResult(RxStatus status, Integer result, String message) {
        super(status, result, message);
    }

    public ProgressRxResult(RxStatus status, Integer result) {
        super(status, result);
    }

    public ProgressRxResult(RxStatus status) {
        super(status);
    }
}
