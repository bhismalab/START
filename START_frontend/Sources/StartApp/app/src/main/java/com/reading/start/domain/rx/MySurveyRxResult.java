package com.reading.start.domain.rx;

/**
 * Represent MySurveyInfo result fro RxJava
 */
public class MySurveyRxResult extends RxResult<MySurveyInfo> {
    public MySurveyRxResult(RxStatus status, MySurveyInfo result, String message) {
        super(status, result, message);
    }

    public MySurveyRxResult(RxStatus status, MySurveyInfo result) {
        super(status, result);
    }

    public MySurveyRxResult(RxStatus status) {
        super(status);
    }
}
