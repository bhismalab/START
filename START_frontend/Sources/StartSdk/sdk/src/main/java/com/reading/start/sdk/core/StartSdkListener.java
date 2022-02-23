package com.reading.start.sdk.core;

import android.graphics.Bitmap;

import org.opencv.core.Point;

public interface StartSdkListener {
    void onCalibrateStart(long timeStamp);

    void onCalibrateChanged(CalibrateType type, long timeStamp);

    void onCalibrateCompleted(boolean success, long timeStamp);

    void onCalibrateStopped(long timeStamp);

    void onPositionUpdated(Point right, Point left, long timeStamp);

    void onDetectedSelectedArea(DetectMode mode, int col, int row, long timeStamp);

    void onFaceDetected(long timeStamp);

    void onFaceLost(long timeStamp);

    void onRightEyeDetected(long timeStamp);

    void onRightEyeLost(long timeStamp);

    void onLeftEyeDetected(long timeStamp);

    void onLeftEyeLost(long timeStamp);

    void onGazeOutside(long timeStamp);

    void onGazeDetected(long timeStamp);

    void onStartSdkStateChanged(StartSdkState state, long timeStamp);

    void onFrameProcessed(DetectMode mode, int col, int row, boolean isEyeDetected, boolean gazeOutside, long timeStamp);

    void onFramePostProcessed(Bitmap bitmap, long timeStamp);

    void onVideoRecordingStart(StartSdk.VideoType type);

    void onVideoRecordingStop(StartSdk.VideoType type);

    void onPostProcessingPostCalibrationStart();

    void onPostProcessingPostCalibrationEnd();

    void onPostProcessingTestStart();

    void onPostProcessingTestEnd();
}
