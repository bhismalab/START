package com.reading.start.sdk.core;

import android.graphics.Bitmap;

import org.opencv.core.Point;

/**
 * Sdk listener.
 */
public interface StartSdkListener {
    /**
     * Raises when calibration started
     */
    void onCalibrateStart(long timeStamp);

    /**
     * Raises when calibration changed type
     */
    void onCalibrateChanged(CalibrateType type, long timeStamp);

    /**
     * Raises when calibration completed
     */
    void onCalibrateCompleted(boolean success, long timeStamp);

    /**
     * Raises when calibration stoped
     */
    void onCalibrateStopped(long timeStamp);

    /**
     * Raises when changed position of the gaze
     */
    void onPositionUpdated(Point right, Point left, long timeStamp);

    /**
     * Raises when changes detected area
     */
    void onDetectedSelectedArea(DetectMode mode, int col, int row, long timeStamp);

    /**
     * Raises when face detected
     */
    void onFaceDetected(long timeStamp);

    /**
     * Raises when face lost and not detecting
     */
    void onFaceLost(long timeStamp);

    /**
     * Raises when right eye detecting
     */
    void onRightEyeDetected(long timeStamp);

    /**
     * Raises when right eye lost and not detecting
     */
    void onRightEyeLost(long timeStamp);

    /**
     * Raises when left eye detected
     */
    void onLeftEyeDetected(long timeStamp);

    /**
     * Raises when left eye lost and not detecting
     */
    void onLeftEyeLost(long timeStamp);

    /**
     * Raises when gaze out of screen
     */
    void onGazeOutside(long timeStamp);

    /**
     * Raises when gaze detecting on the screen
     */
    void onGazeDetected(long timeStamp);

    /**
     * Raises when sdk state changed
     */
    void onStartSdkStateChanged(StartSdkState state, long timeStamp);

    /**
     * Raises when frame processed
     */
    void onFrameProcessed(DetectMode mode, int col, int row, boolean isEyeDetected, boolean gazeOutside, long timeStamp);

    /**
     * Raises when frame processed for post processing mode
     */
    void onFramePostProcessed(Bitmap bitmap, long timeStamp);

    /**
     * Raises when video start recoding
     */
    void onVideoRecordingStart(StartSdk.VideoType type);

    /**
     * Raises when video stop recoding
     */
    void onVideoRecordingStop(StartSdk.VideoType type);

    /**
     * Raises when calibration started fot post processing mode.
     */
    void onPostProcessingPostCalibrationStart();

    /**
     * Raises when calibration end fot post processing mode.
     */
    void onPostProcessingPostCalibrationEnd();

    /**
     * Raises when start test fot post processing mode.
     */
    void onPostProcessingTestStart();

    /**
     * Raises when stop test fot post processing mode.
     */
    void onPostProcessingTestEnd();
}
