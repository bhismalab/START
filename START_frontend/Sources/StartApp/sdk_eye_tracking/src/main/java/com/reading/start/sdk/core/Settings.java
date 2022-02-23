package com.reading.start.sdk.core;

import android.util.Size;

import com.reading.start.sdk.Constants;

/**
 * Represent all settings for eye-tracking sdk.
 */
public class Settings {
    private General mGeneral;

    private Ui mUiSettings;

    private Process mProcessSettings;

    public Settings(General general, Ui uiSettings, Process processSettings) {
        mGeneral = general;
        mUiSettings = uiSettings;
        mProcessSettings = processSettings;
    }

    public General getGeneral() {
        return mGeneral;
    }

    public Ui getUiSettings() {
        return mUiSettings;
    }

    public Process getProcessSettings() {
        return mProcessSettings;
    }

    public static class General {
        private DetectType mDetectType = DetectType.RealTime;

        private int mCameraIndex = -1;

        private boolean mEnableFpsMeter = true;

        private Size mCameraSize = null;

        private DetectMode mDetectMode = DetectMode.Mode2x2;

        private CalibrateMode mCalibrateMode = CalibrateMode.Point1;

        private boolean mWriteVideo = true;

        private String mViewFile = null;

        private boolean mIsMultiThread = false;

        private int mCountOfThread = 2;

        private boolean mShowProcessVideo = false;

        public General(DetectType detectType, Size cameraSize, int cameraIndex,
                       boolean enableFpsMeter, DetectMode mode, CalibrateMode calibrateMode,
                       boolean writeVideo, boolean multiThread, int countOfThread, boolean showProcessVideo) {
            mDetectType = detectType;
            mCameraSize = cameraSize;
            mCameraIndex = cameraIndex;
            mEnableFpsMeter = enableFpsMeter;
            mDetectMode = mode;
            mCalibrateMode = calibrateMode;
            mWriteVideo = writeVideo;
            mIsMultiThread = multiThread;
            mCountOfThread = countOfThread;
            mShowProcessVideo = showProcessVideo;
        }

        public DetectType getDetectType() {
            return mDetectType;
        }

        public void setDetectType(DetectType detectType) {
            mDetectType = detectType;
        }

        public boolean isEnableFpsMeter() {
            return mEnableFpsMeter;
        }

        public int getCameraIndex() {
            return mCameraIndex;
        }

        public Size getCameraSize() {
            return mCameraSize;
        }

        public void setCameraIndex(int cameraIndex) {
            mCameraIndex = cameraIndex;
        }

        public void setEnableFpsMeter(boolean enableFpsMeter) {
            mEnableFpsMeter = enableFpsMeter;
        }

        public void setCameraSize(Size cameraSize) {
            mCameraSize = cameraSize;
        }

        public DetectMode getDetectMode() {
            if (mDetectMode == null) {
                mDetectMode = DetectMode.Mode2x2;
            }

            return mDetectMode;
        }

        public void setDetectMode(DetectMode detectMode) {
            mDetectMode = detectMode;
        }

        public CalibrateMode getCalibrateMode() {
            return mCalibrateMode;
        }

        public void setCalibrateMode(CalibrateMode calibrateMode) {
            mCalibrateMode = calibrateMode;
        }

        public boolean isWriteVideo() {
            return mWriteVideo;
        }

        public void setWriteVideo(boolean writeVideo) {
            mWriteVideo = writeVideo;
        }

        public String getViewFile() {
            return mViewFile;
        }

        public void setViewFile(String viewFile) {
            mViewFile = viewFile;
        }

        public boolean isMultiThread() {
            return mIsMultiThread;
        }

        public void setIsMultiThread(boolean isMultiThread) {
            mIsMultiThread = isMultiThread;
        }

        public int getCountOfThread() {
            return mCountOfThread;
        }

        public void setCountOfThread(int countOfThread) {
            mCountOfThread = countOfThread;
        }

        public boolean isShowProcessVideo() {
            return mShowProcessVideo;
        }

        public void setShowProcessVideo(boolean showProcessVideo) {
            mShowProcessVideo = showProcessVideo;
        }
    }

    public static class Ui {
        private boolean mDrawFaceArea = true;

        private boolean mDrawRightEyeArea = true;

        private boolean mDrawLeftEyeArea = true;

        private boolean mDrawRightPupilArea = true;

        private boolean mDrawLeftPupilArea = true;

        public Ui(boolean drawFaceArea, boolean drawRightEyeArea, boolean drawLeftEyeArea,
                  boolean drawRightPupilArea, boolean drawLeftPupilArea) {
            mDrawFaceArea = drawFaceArea;
            mDrawRightEyeArea = drawRightEyeArea;
            mDrawLeftEyeArea = drawLeftEyeArea;
            mDrawRightPupilArea = drawRightPupilArea;
            mDrawLeftPupilArea = drawLeftPupilArea;
        }

        public boolean isDrawFaceArea() {
            return mDrawFaceArea;
        }

        public boolean isDrawRightEyeArea() {
            return mDrawRightEyeArea;
        }

        public boolean isDrawLeftEyeArea() {
            return mDrawLeftEyeArea;
        }

        public boolean isDrawRightPupilArea() {
            return mDrawRightPupilArea;
        }

        public boolean isDrawLeftPupilArea() {
            return mDrawLeftPupilArea;
        }
    }

    public static class Process {
        private boolean mTrackFace = true;

        private boolean mTrackRightEye = true;

        private boolean mTrackLeftEye = true;

        private boolean mTrackRightPupil = true;

        private boolean mTrackLeftPupil = true;

        private int mPupilAreaSize = Constants.DEFAULT_PUPIL_AREA_SIZE;

        private double mCalibrationX = Constants.THRESHOLD_HORIZONTAL;

        private double mCalibrationY = Constants.THRESHOLD_VERTICAL;

        private double mGazeOutsizeValueX = Constants.GAZE_OUTSIZE_VALUE_X;

        private double mGazeOutsizeValueY = Constants.GAZE_OUTSIZE_VALUE_Y;

        private int mCenterAreaSize = Constants.CENTER_AREA_SIZE;

        private int mLeftAreaSize = Constants.LEFT_AREA_SIZE;

        private int mRightAreaSize = Constants.RIGHT_AREA_SIZE;

        private double mContrast = Constants.DEFAULT_CONTRAST;

        private double mBrightness = Constants.DEFAULT_BRIGHTNESS;

        private boolean mEqualizeHist = true;

        private boolean mNormalize = true;

        private double mVerticalCalibrationOffset = Constants.VERTICAL_CALIBRATION_OFFSET;

        private boolean mIsAutoSettings = false;

        public Process(boolean trackFace, boolean trackRightEye, boolean trackLeftEye,
                       boolean trackRightPupil, boolean trackLeftPupil, int pupilAreSize,
                       double calibrationX, double calibrationY, double gazeOutsizeValueX, double gazeOutsizeValueY,
                       int centerAreaSize, int rightAreaSize, int leftAreaSize,
                       double contrast, double brightness, boolean equalizeHist, boolean normalize,
                       double verticalCalibrationOffset, boolean isAutoSettings) {
            mTrackFace = trackFace;
            mTrackRightEye = trackRightEye;
            mTrackLeftEye = trackLeftEye;
            mTrackRightPupil = trackRightPupil;
            mTrackLeftPupil = trackLeftPupil;
            mPupilAreaSize = pupilAreSize;
            mCalibrationX = calibrationX;
            mGazeOutsizeValueX = gazeOutsizeValueX;
            mGazeOutsizeValueY = gazeOutsizeValueY;
            mCalibrationY = calibrationY;
            mCenterAreaSize = centerAreaSize;
            mRightAreaSize = rightAreaSize;
            mLeftAreaSize = leftAreaSize;
            mContrast = contrast;
            mBrightness = brightness;
            mEqualizeHist = equalizeHist;
            mNormalize = normalize;
            mVerticalCalibrationOffset = verticalCalibrationOffset;
            mIsAutoSettings = isAutoSettings;
        }

        public boolean isTrackFace() {
            return mTrackFace;
        }

        public boolean isTrackRightEye() {
            return mTrackRightEye;
        }

        public boolean isTrackLeftEye() {
            return mTrackLeftEye;
        }

        public boolean isTrackRightPupil() {
            return mTrackRightPupil;
        }

        public boolean isTrackLeftPupil() {
            return mTrackLeftPupil;
        }

        public int getPupilAreaSize() {
            return mPupilAreaSize;
        }

        public double getCalibrationX() {
            return mCalibrationX;
        }

        public double getCalibrationY() {
            return mCalibrationY;
        }

        public void setTrackFace(boolean trackFace) {
            mTrackFace = trackFace;
        }

        public void setTrackRightEye(boolean trackRightEye) {
            mTrackRightEye = trackRightEye;
        }

        public void setTrackLeftEye(boolean trackLeftEye) {
            mTrackLeftEye = trackLeftEye;
        }

        public void setTrackRightPupil(boolean trackRightPupil) {
            mTrackRightPupil = trackRightPupil;
        }

        public void setTrackLeftPupil(boolean trackLeftPupil) {
            mTrackLeftPupil = trackLeftPupil;
        }

        public void setPupilAreaSize(int mPupilAreaSize) {
            this.mPupilAreaSize = mPupilAreaSize;
        }

        public void setCalibrationX(double calibrationX) {
            mCalibrationX = calibrationX;
        }

        public void setCalibrationY(double calibrationY) {
            mCalibrationY = calibrationY;
        }

        public int getCenterAreaSize() {
            return mCenterAreaSize;
        }

        public void setCenterAreaSize(int centerAreaSize) {
            mCenterAreaSize = centerAreaSize;
        }

        public double getGazeOutsizeValueX() {
            return mGazeOutsizeValueX;
        }

        public void setGazeOutsizeValueX(double gazeOutsizeValue) {
            mGazeOutsizeValueX = gazeOutsizeValue;
        }

        public double getGazeOutsizeValueY() {
            return mGazeOutsizeValueY;
        }

        public void setGazeOutsizeValueY(double gazeOutsizeValue) {
            mGazeOutsizeValueY = gazeOutsizeValue;
        }

        public int getLeftAreaSize() {
            return mLeftAreaSize;
        }

        public void setLeftAreaSize(int leftAreaSize) {
            mLeftAreaSize = leftAreaSize;
        }

        public int getRightAreaSize() {
            return mRightAreaSize;
        }

        public void setRightAreaSize(int rightAreaSize) {
            mRightAreaSize = rightAreaSize;
        }

        public double getContrast() {
            return mContrast;
        }

        public void setContrast(double contrast) {
            mContrast = contrast;
        }

        public double getBrightness() {
            return mBrightness;
        }

        public void setBrightness(double brightness) {
            mBrightness = brightness;
        }

        public boolean isEqualizeHist() {
            return mEqualizeHist;
        }

        public void setEqualizeHist(boolean equalizeHist) {
            mEqualizeHist = equalizeHist;
        }

        public boolean isNormalize() {
            return mNormalize;
        }

        public void setNormalize(boolean normalize) {
            mNormalize = normalize;
        }

        public double getVerticalCalibrationOffset() {
            return mVerticalCalibrationOffset;
        }

        public void setVerticalCalibrationOffset(double verticalCalibrationOffset) {
            mVerticalCalibrationOffset = verticalCalibrationOffset;
        }

        public boolean isAutoSettings() {
            return mIsAutoSettings;
        }

        public void setIsAutoSettings(boolean isAutoSettings) {
            mIsAutoSettings = isAutoSettings;
        }
    }
}
