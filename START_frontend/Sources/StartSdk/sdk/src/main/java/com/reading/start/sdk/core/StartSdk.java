package com.reading.start.sdk.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.reading.start.sdk.Constants;
import com.reading.start.sdk.general.SdkLog;
import com.reading.start.sdk.ui.view.CameraDetectView;
import com.reading.start.sdk.utils.DevicePositionSensorManager;
import com.reading.start.sdk.utils.LightSensorManager;
import com.reading.start.sdk.utils.ProcessExecutor;
import com.reading.start.sdk.utils.PupilDetectHelper;
import com.reading.start.sdk.utils.Utils;
import com.reading.start.sdk.utils.VideoRecorder;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class StartSdk {
    public enum VideoType {
        None,
        Calibration,
        PostCalibration,
        Test
    }

    private enum CalibrateStateType {
        None,
        Center,
        TopLeft,
        TopRight,
        BottomLeft,
        BottomRight,
        Done
    }

    private static final String TAG = StartSdk.class.getSimpleName();

    private static final Scalar FRAME_COLOR = new Scalar(255, 0, 0, 0);

    private static StartSdk sInstance = null;

    public static StartSdk getInstance() {
        if (sInstance == null) {
            sInstance = new StartSdk();
        }

        return sInstance;
    }

    private File mCascadeFile;
    private File mCascadeFileEyeLeft;
    private File mCascadeFileEyeRight;

    private CascadeClassifier mJavaDetector;
    private CascadeClassifier mJavaDetectorEyeLeft;
    private CascadeClassifier mJavaDetectorEyeRight;

    private Context mContext = null;

    private CameraDetectView mCameraView = null;

    private Settings mSettings = null;

    private boolean mIsStart = false;

    private CalibrateStateType mCalibrateType = CalibrateStateType.None;

    private boolean mIsCalibrated = false;

    private boolean mIsCalibrating = false;

    private boolean mIsStopCalibrating = false;

    private Mat mRgba;
    private Mat mGray;

    private Object mCalibrateSync = new Objdetect();

    private Handler mHandler = new Handler();

    private LinkedList<Rect> mCalibrateFaceRectList = new LinkedList<>();
    private LinkedList<Rect> mFaceRectList = new LinkedList<>();

    private LinkedList<Rect> mCalibrateRightEyePointsCenter = new LinkedList<>();
    private LinkedList<Rect> mCalibrateRightEyePointsTopLeft = new LinkedList<>();
    private LinkedList<Rect> mCalibrateRightEyePointsTopRight = new LinkedList<>();
    private LinkedList<Rect> mCalibrateRightEyePointsBottomLeft = new LinkedList<>();
    private LinkedList<Rect> mCalibrateRightEyePointsBottomRight = new LinkedList<>();
    private LinkedList<Rect> mRightEyePoints = new LinkedList<>();

    private LinkedList<Rect> mCalibrateLeftEyePointsCenter = new LinkedList<>();
    private LinkedList<Rect> mCalibrateLeftEyePointsTopLeft = new LinkedList<>();
    private LinkedList<Rect> mCalibrateLeftEyePointsTopRight = new LinkedList<>();
    private LinkedList<Rect> mCalibrateLeftEyePointsBottomLeft = new LinkedList<>();
    private LinkedList<Rect> mCalibrateLeftEyePointsBottomRight = new LinkedList<>();
    private LinkedList<Rect> mLeftEyePoints = new LinkedList<>();

    private LinkedList<Point> mCalibrateRightPupilPointsCenter = new LinkedList<>();
    private LinkedList<Point> mCalibrateRightPupilPointsTopLeft = new LinkedList<>();
    private LinkedList<Point> mCalibrateRightPupilPointsTopRight = new LinkedList<>();
    private LinkedList<Point> mCalibrateRightPupilPointsBottomLeft = new LinkedList<>();
    private LinkedList<Point> mCalibrateRightPupilPointsBottomRight = new LinkedList<>();
    private LinkedList<Point> mRightPupilPoints = new LinkedList<>();

    private LinkedList<Point> mCalibrateLeftPupilPointsCenter = new LinkedList<>();
    private LinkedList<Point> mCalibrateLeftPupilPointsTopLeft = new LinkedList<>();
    private LinkedList<Point> mCalibrateLeftPupilPointsTopRight = new LinkedList<>();
    private LinkedList<Point> mCalibrateLeftPupilPointsBottomLeft = new LinkedList<>();
    private LinkedList<Point> mCalibrateLeftPupilPointsBottomRight = new LinkedList<>();
    private LinkedList<Point> mLeftPupilPoints = new LinkedList<>();

    private StartSdkListener mStartSdkListener = null;

    private int mCurrentSelectedCol = -1;

    private int mCurrentSelectedRow = -1;

    private boolean mIsGazeOutside = false;

    private boolean mIsFaceDetected = false;

    private boolean mIsEyeDetected = false;

    private Object mProcessSync = new Object();

    private Rect mFaceRect = null;
    private Rect mRightEye = null;
    private Point mRightEyeCenter = null;
    private Point mLeftEyeCenter = null;
    private Rect mLeftEye = null;

    private int mCameraWidth = 0;
    private int mCameraHeight = 0;

    private Object mCountInProgressSync = new Object();

    private int mCountInProgress = 0;

    private CompositeDisposable mSubscriptions;

    private StartSdkState mStartSdkState = new StartSdkState();

    private StartSdkState mNewStartSdkState = new StartSdkState();

    private LightSensorManager mLightSensorManager = null;

    private DevicePositionSensorManager mDevicePositionSensorManager = null;

    private long mTimeStamp = -1;

    private long mOldTimeStamp = -1;

    private VideoType mVideoType = VideoType.None;

    private boolean mIsPostProcessingInProgress = false;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(mContext) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                    try {
                        File cascadeDir = mContext.getDir("cascade", Context.MODE_PRIVATE);

                        // load cascade file for face
                        InputStream is = mContext.getResources().openRawResource(Constants.FACE_DETECT_RAW);
                        mCascadeFile = new File(cascadeDir, Constants.FACE_DETECT_FILE);
                        FileOutputStream os = new FileOutputStream(mCascadeFile);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        // load cascade file from eyes
                        InputStream ise = mContext.getResources().openRawResource(Constants.EYE_LEFT_DETECT_RAW);
                        mCascadeFileEyeLeft = new File(cascadeDir, Constants.EYE_LEFT_DETECT_FILE);
                        FileOutputStream ose = new FileOutputStream(mCascadeFileEyeLeft);
                        while ((bytesRead = ise.read(buffer)) != -1) {
                            ose.write(buffer, 0, bytesRead);
                        }
                        ise.close();
                        ose.close();
                        ise = mContext.getResources().openRawResource(Constants.EYE_RIGHT_DETECT_RAW);
                        mCascadeFileEyeRight = new File(cascadeDir, Constants.EYE_RIGHT_DETECT_FILE);
                        ose = new FileOutputStream(mCascadeFileEyeRight);
                        while ((bytesRead = ise.read(buffer)) != -1) {
                            ose.write(buffer, 0, bytesRead);
                        }
                        ise.close();
                        ose.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());

                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier for face");
                            mJavaDetector = null;
                        } else {
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                        }

                        mJavaDetectorEyeLeft = new CascadeClassifier(mCascadeFileEyeLeft.getAbsolutePath());

                        if (mJavaDetectorEyeLeft.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier for eye");
                            mJavaDetectorEyeLeft = null;
                        } else {
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileEyeLeft.getAbsolutePath());
                        }

                        mJavaDetectorEyeRight = new CascadeClassifier(mCascadeFileEyeRight.getAbsolutePath());

                        if (mJavaDetectorEyeRight.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier for eye");
                            mJavaDetectorEyeRight = null;
                        } else {
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFileEyeRight.getAbsolutePath());
                        }

                        cascadeDir.delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    if (mSettings.getGeneral().isEnableFpsMeter()) {
                        mCameraView.enableFpsMeter();
                    } else {
                        mCameraView.disableFpsMeter();
                    }

                    mCameraView.setCameraIndex(mSettings.getGeneral().getCameraIndex());
                    mCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private CameraBridgeViewBase.CvCameraViewListener2 mCvCameraViewListener2 = new CameraBridgeViewBase.CvCameraViewListener2() {
        @Override
        public void onCameraViewStarted(int width, int height) {
            mGray = new Mat();
            mRgba = new Mat();
            mCameraWidth = width;
            mCameraHeight = height;
        }

        @Override
        public void onCameraViewStopped() {
            mGray.release();
            mRgba.release();
            mCountInProgress = 0;
        }

        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            synchronized (mNewStartSdkState) {
                mStartSdkState.copy(mNewStartSdkState);
            }

            mRgba = inputFrame.rgba();
            mGray = inputFrame.gray();

            if (mSettings.getGeneral().getDetectType() == DetectType.PostProcessing) {
                return onCameraFramePostProcess();
            } else {
                return onCameraFrameRealTime();
            }
        }
    };

    public boolean start(Context context, CameraDetectView view, Settings settings, StartSdkListener callback) {
        boolean result = false;

        if (!mIsStart) {
            mIsStart = true;

            if (context != null && view != null && settings != null) {
                mContext = context;
                mCameraView = view;
                mSettings = settings;
                mStartSdkListener = callback;
                mStartSdkState.reset();
                mNewStartSdkState.reset();

                if (mStartSdkListener != null) {
                    mStartSdkListener.onStartSdkStateChanged(mStartSdkState, mTimeStamp);
                }

                ProcessExecutor.reset();

                view.setCvCameraViewListener(mCvCameraViewListener2);
                view.setMaxFrameSize(settings.getGeneral().getCameraSize().getWidth(),
                        settings.getGeneral().getCameraSize().getHeight());

                try {
                    if (!OpenCVLoader.initDebug()) {
                        Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, mContext, mLoaderCallback);
                    } else {
                        Log.d(TAG, "OpenCV library found inside package. Using it!");
                        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
                    }
                } catch (Exception e) {
                    SdkLog.e(TAG, e);
                }

                /*
                if (Constants.ENABLE_VIDEO_RECORDING && mSettings.getGeneral().isWriteVideo()) {
                    File file = settings.getGeneral().getViewFile() != null
                            ? new File(settings.getGeneral().getViewFile())
                            : new File(Environment.getExternalStorageDirectory(), Constants.VIDEO_FILE_NAME);
                    VideoRecorder.getInstance().start(settings.getGeneral().getCameraSize().getWidth(),
                            settings.getGeneral().getCameraSize().getHeight(), file);
                }
                */

                startLightSensor();
                startDeviceSensor();
                result = true;
            }
        }

        return result;
    }

    public boolean stop() {
        boolean result = false;

        if (mIsStart) {
            mCameraView.disableView();
            mCameraView = null;

            if (Constants.ENABLE_VIDEO_RECORDING && mSettings.getGeneral().isWriteVideo()) {
                VideoRecorder.getInstance().stop();
            }

            result = true;
            mIsStart = false;
        }

        if (mSubscriptions != null && !mSubscriptions.isDisposed()) {
            mSubscriptions.dispose();
            mSubscriptions = null;
        }

        ProcessExecutor.reset();
        stopLightSensor();
        stopDeviceSensor();
        mCountInProgress = 0;
        return result;
    }

    public void startVideoRecord() {
        startVideoRecord(VideoType.None);
    }

    public void startVideoRecord(VideoType type) {
        if (Constants.ENABLE_VIDEO_RECORDING && mSettings.getGeneral().isWriteVideo()) {
            File file = null;

            if (type == VideoType.None) {
                mVideoType = VideoType.None;
                file = mSettings.getGeneral().getViewFile() != null
                        ? new File(mSettings.getGeneral().getViewFile())
                        : new File(Environment.getExternalStorageDirectory(), Constants.VIDEO_FILE_NAME);
                VideoRecorder.getInstance().start(mSettings.getGeneral().getCameraSize().getWidth(),
                        mSettings.getGeneral().getCameraSize().getHeight(), file);
            } else {
                if (type == VideoType.Calibration) {
                    file = getCalibrationVideoPath();
                } else if (type == VideoType.PostCalibration) {
                    file = getPostCalibrationVideoPath();
                } else {
                    file = getTestVideoPath();
                }

                if (file.exists()) {
                    file.delete();
                }

                mVideoType = type;
                VideoRecorder.getInstance().start(mSettings.getGeneral().getCameraSize().getWidth(),
                        mSettings.getGeneral().getCameraSize().getHeight(), file);
            }

            if (mStartSdkListener != null) {
                mStartSdkListener.onVideoRecordingStart(mVideoType);
            }
        }
    }

    public void stopVideoRecord() {
        if (Constants.ENABLE_VIDEO_RECORDING && mSettings.getGeneral().isWriteVideo()) {
            VideoRecorder.getInstance().stop();
        }

        if (mStartSdkListener != null) {
            mStartSdkListener.onVideoRecordingStop(mVideoType);
        }
    }

    public boolean calibrate() {
        return calibrate(true);
    }

    public boolean calibrate(boolean startTimer) {
        boolean result = false;
        mIsCalibrated = false;
        mIsCalibrating = true;
        mIsStopCalibrating = false;

        if (mStartSdkListener != null && !mIsStopCalibrating) {
            mStartSdkListener.onCalibrateStart(mTimeStamp);
        }

        doCalibrate(startTimer);
        result = true;

        return result;
    }

    public boolean stopCalibrate() {
        boolean result = false;

        if (mIsCalibrating) {
            mIsStopCalibrating = true;

            if (mStartSdkListener != null) {
                mStartSdkListener.onCalibrateStopped(mTimeStamp);
            }

            result = true;
        }

        return result;
    }

    public void processVideo(Settings settings, AtomicBoolean stop, StartSdkListener callback) {
        mIsPostProcessingInProgress = true;
        processVideoCalibration(settings, stop, callback);
        processVideoPostCalibration(settings, stop, callback);
        processVideoTest(settings, stop, callback);
        mIsPostProcessingInProgress = false;
    }

    public File getCalibrationVideoPath() {
        File baseFile = mSettings.getGeneral().getViewFile() != null
                ? new File(mSettings.getGeneral().getViewFile())
                : new File(Environment.getExternalStorageDirectory(), Constants.VIDEO_FILE_NAME);

        File file = new File(baseFile.getParent(), Constants.VIDEO_FILE_PREFIX_TEMP_CALIBRATION + baseFile.getName());
        return file;
    }

    public File getPostCalibrationVideoPath() {
        File baseFile = mSettings.getGeneral().getViewFile() != null
                ? new File(mSettings.getGeneral().getViewFile())
                : new File(Environment.getExternalStorageDirectory(), Constants.VIDEO_FILE_NAME);

        File file = new File(baseFile.getParent(), Constants.VIDEO_FILE_PREFIX_TEMP_POST_CALIBRATION + baseFile.getName());
        return file;
    }

    public File getTestVideoPath() {
        File baseFile = mSettings.getGeneral().getViewFile() != null
                ? new File(mSettings.getGeneral().getViewFile())
                : new File(Environment.getExternalStorageDirectory(), Constants.VIDEO_FILE_NAME);

        File file = new File(baseFile.getParent(), Constants.VIDEO_FILE_PREFIX_TEMP_TEST + baseFile.getName());
        return file;
    }

    private void processVideoCalibration(Settings settings, AtomicBoolean stop, StartSdkListener callback) {
        String path = getCalibrationVideoPath().getPath();
        FFmpegFrameGrabber grabber = null;

        try {
            mSettings = settings;
            grabber = new FFmpegFrameGrabber(path);
            grabber.setFormat("mp4");
            grabber.start();
            int length = grabber.getLengthInFrames();

            if (length > 0) {
                Frame frame;
                Mat rgb = new Mat();
                Mat gray = new Mat();
                int countFrames = 0;
                AndroidFrameConverter androidFrameConverter = new AndroidFrameConverter();
                Bitmap convertedBitmap;
                Bitmap processedBitmap = null;
                mTimeStamp = 0;
                calibrate(false);
                boolean isDoneRaised = false;

                while (!stop.get() && (frame = grabber.grab()) != null) {
                    try {
                        countFrames++;

                        if (frame.image != null) {
                            mTimeStamp = grabber.getTimestamp() / 1000;
                            convertedBitmap = androidFrameConverter.convert(frame);
                            org.opencv.android.Utils.bitmapToMat(convertedBitmap, rgb);

                            if (processedBitmap == null) {
                                processedBitmap = Bitmap.createBitmap(frame.imageWidth, frame.imageHeight, Bitmap.Config.ARGB_8888);
                            }

                            // prepare gray mat
                            Imgproc.cvtColor(rgb, gray, Imgproc.COLOR_RGB2GRAY);

                            // detect pupils
                            processFrame(gray);

                            // draw result
                            drawFrameInfo(rgb);

                            if (callback != null && rgb != null && processedBitmap != null) {
                                org.opencv.android.Utils.matToBitmap(rgb, processedBitmap);
                                callback.onFramePostProcessed(processedBitmap, mTimeStamp);
                            }

                            if (mCalibrateType != CalibrateStateType.Done && mTimeStamp > Constants.CALIBRATE_DELAY) {
                                doCalibrate(false);
                                isDoneRaised = true;
                            }
                        }
                    } catch (Exception e) {
                        SdkLog.e(TAG, e);
                    }
                }

                if (!isDoneRaised) {
                    if (mCalibrateType != CalibrateStateType.Done) {
                        doCalibrate(false);
                    }
                }

                SdkLog.d(TAG, "countFrames: " + countFrames);
            }
        } catch (Exception e) {
            SdkLog.e(TAG, e);
        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                }
            } catch (Exception e) {
                SdkLog.e(TAG, e);
            }
        }
    }

    private void processVideoPostCalibration(Settings settings, AtomicBoolean stop, StartSdkListener callback) {
        String path = getPostCalibrationVideoPath().getPath();
        FFmpegFrameGrabber grabber = null;

        try {
            mSettings = settings;
            grabber = new FFmpegFrameGrabber(path);
            grabber.setFormat("mp4");
            grabber.start();
            int length = grabber.getLengthInFrames();

            if (length > 0) {
                Frame frame;
                Mat rgb = new Mat();
                Mat gray = new Mat();
                int countFrames = 0;
                AndroidFrameConverter androidFrameConverter = new AndroidFrameConverter();
                Bitmap convertedBitmap;
                Bitmap processedBitmap = null;
                mTimeStamp = 0;

                if (callback != null) {
                    callback.onPostProcessingPostCalibrationStart();
                }

                while (!stop.get() && (frame = grabber.grab()) != null) {
                    try {
                        countFrames++;

                        if (frame.image != null) {
                            mTimeStamp = grabber.getTimestamp() / 1000;
                            convertedBitmap = androidFrameConverter.convert(frame);
                            org.opencv.android.Utils.bitmapToMat(convertedBitmap, rgb);

                            if (processedBitmap == null) {
                                processedBitmap = Bitmap.createBitmap(frame.imageWidth, frame.imageHeight, Bitmap.Config.ARGB_8888);
                            }

                            // prepare gray mat
                            Imgproc.cvtColor(rgb, gray, Imgproc.COLOR_RGB2GRAY);

                            // detect pupils
                            processFrame(gray);

                            // draw result
                            drawFrameInfo(rgb);

                            if (callback != null && rgb != null && processedBitmap != null) {
                                org.opencv.android.Utils.matToBitmap(rgb, processedBitmap);
                                callback.onFramePostProcessed(processedBitmap, mTimeStamp);
                            }
                        }
                    } catch (Exception e) {
                        SdkLog.e(TAG, e);
                    }
                }

                if (callback != null) {
                    callback.onPostProcessingPostCalibrationEnd();
                }

                SdkLog.d(TAG, "countFrames: " + countFrames);
            }
        } catch (Exception e) {
            SdkLog.e(TAG, e);
        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                }
            } catch (Exception e) {
                SdkLog.e(TAG, e);
            }
        }
    }

    private void processVideoTest(Settings settings, AtomicBoolean stop, StartSdkListener callback) {
        String path = getTestVideoPath().getPath();
        FFmpegFrameGrabber grabber = null;

        try {
            mSettings = settings;
            grabber = new FFmpegFrameGrabber(path);
            grabber.setFormat("mp4");
            grabber.start();
            int length = grabber.getLengthInFrames();

            if (length > 0) {
                Frame frame;
                Mat rgb = new Mat();
                Mat gray = new Mat();
                int countFrames = 0;
                AndroidFrameConverter androidFrameConverter = new AndroidFrameConverter();
                Bitmap convertedBitmap;
                Bitmap processedBitmap = null;
                mTimeStamp = 0;

                if (callback != null) {
                    callback.onPostProcessingTestStart();
                }

                File file = mSettings.getGeneral().getViewFile() != null
                        ? new File(mSettings.getGeneral().getViewFile())
                        : new File(Environment.getExternalStorageDirectory(), Constants.VIDEO_FILE_NAME);

                if (file.exists()) {
                    file.delete();
                }

                VideoRecorder.getInstance().start(mSettings.getGeneral().getCameraSize().getWidth(),
                        mSettings.getGeneral().getCameraSize().getHeight(), file);

                while (!stop.get() && (frame = grabber.grab()) != null) {
                    try {
                        countFrames++;

                        if (frame.image != null) {
                            mOldTimeStamp = mTimeStamp;
                            mTimeStamp = grabber.getTimestamp() / 1000;

                            //SdkLog.d(TAG, "grabber timestamp: " + grabber.getTimestamp());
                            //SdkLog.d(TAG, "timestamp: " + (mTimeStamp - mOldTimeStamp));
                            //SdkLog.d(TAG, "time: " + mTimeStamp);

                            convertedBitmap = androidFrameConverter.convert(frame);
                            org.opencv.android.Utils.bitmapToMat(convertedBitmap, rgb);

                            VideoRecorder.getInstance().writeFrame(rgb, mTimeStamp);

                            if (processedBitmap == null) {
                                processedBitmap = Bitmap.createBitmap(frame.imageWidth, frame.imageHeight, Bitmap.Config.ARGB_8888);
                            }

                            // prepare gray mat
                            Imgproc.cvtColor(rgb, gray, Imgproc.COLOR_RGB2GRAY);

                            // detect pupils
                            processFrame(gray);

                            // draw result
                            drawFrameInfo(rgb);

                            if (callback != null && rgb != null && processedBitmap != null) {
                                org.opencv.android.Utils.matToBitmap(rgb, processedBitmap);
                                callback.onFramePostProcessed(processedBitmap, mTimeStamp);
                            }
                        }
                    } catch (Exception e) {
                        SdkLog.e(TAG, e);
                    }
                }

                VideoRecorder.getInstance().stop();

                if (callback != null) {
                    callback.onPostProcessingTestEnd();
                }

                SdkLog.d(TAG, "countFrames: " + countFrames);
            }
        } catch (Exception e) {
            SdkLog.e(TAG, e);
        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                }
            } catch (Exception e) {
                SdkLog.e(TAG, e);
            }
        }
    }

    private Mat onCameraFrameRealTime() {
        Mat processMat = mGray;
        Mat displayMat = mSettings.getGeneral().isShowProcessVideo() ? mGray : mRgba;

        if (mSettings.getProcessSettings().isEqualizeHist()) {
            Imgproc.equalizeHist(processMat, processMat);
        }

        if (mSettings.getProcessSettings().isNormalize()) {
            Core.normalize(processMat, processMat, 0, 255, Core.NORM_MINMAX, -1, new Mat());
        }

        int realBrightness = PupilDetectHelper.calculateBrightness(processMat);
        //SdkLog.d(TAG, "realBrightness: " + realBrightness);

        synchronized (mNewStartSdkState) {
            if (realBrightness < Constants.MIN_BRIGHTNESS_LEVEL) {
                mNewStartSdkState.setLightCameraOk(false);
            } else {
                mNewStartSdkState.setLightCameraOk(true);
            }
        }

        if (mSettings.getProcessSettings().getContrast() > 1 || mSettings.getProcessSettings().getBrightness() > 0) {
            processMat.convertTo(processMat, -1, mSettings.getProcessSettings().getContrast(),
                    mSettings.getProcessSettings().getBrightness());
        }

        int brightness = PupilDetectHelper.calculateBrightness(processMat);
        //SdkLog.d(TAG, "correctedBrightness: " + brightness);

        if (mSettings.getProcessSettings().isAutoSettings()) {
            if (brightness < Constants.PREFERRED_BRIGHTNESS_LEVEL - 10 || brightness > Constants.PREFERRED_BRIGHTNESS_LEVEL + 10) {
                // auto correction for contrast

                if (mSettings.getProcessSettings().getBrightness() == 0) {
                    if (brightness < Constants.PREFERRED_BRIGHTNESS_LEVEL) {
                        if (mSettings.getProcessSettings().getContrast() < 2) {
                            mSettings.getProcessSettings().setContrast(mSettings.getProcessSettings().getContrast() + 0.1d);
                        }
                    } else {
                        if (mSettings.getProcessSettings().getContrast() >= 1.1) {
                            mSettings.getProcessSettings().setContrast(mSettings.getProcessSettings().getContrast() - 0.1d);
                        }
                    }
                }

                if (mSettings.getProcessSettings().getContrast() >= 1.8) {
                    if (brightness < Constants.PREFERRED_BRIGHTNESS_LEVEL) {
                        if (mSettings.getProcessSettings().getBrightness() < 31) {
                            mSettings.getProcessSettings().setBrightness(mSettings.getProcessSettings().getBrightness() + 1d);
                        }
                    } else {
                        if (mSettings.getProcessSettings().getBrightness() >= 1d) {
                            mSettings.getProcessSettings().setBrightness(mSettings.getProcessSettings().getBrightness() - 1d);
                        }
                    }
                }

                //SdkLog.d(TAG, "auto correct, Brightness: " + mSettings.getProcessSettings().getBrightness() + ", contrast: " + mSettings.getProcessSettings().getContrast());
            }
        }

        if (mSettings.getGeneral().isMultiThread() && mSettings.getGeneral().getCountOfThread() > 1) {
            int poolSize = mSettings.getGeneral().getCountOfThread();

            synchronized (mCountInProgressSync) {
                if (mCountInProgress < poolSize) {
                    mCountInProgress++;
                    final Observable<Boolean> observable = getProcessObservable(processMat);

                    getSubscriptions().add(observable
                            .subscribeOn(Schedulers.from(ProcessExecutor.getInstance(poolSize)))
                            .retry(1)
                            .subscribe(data -> {
                                synchronized (mCountInProgressSync) {
                                    mCountInProgress--;
                                }

                                if (Constants.ENABLE_VIDEO_RECORDING && mSettings.getGeneral().isWriteVideo()
                                        && VideoRecorder.getInstance().isRecording()) {
                                    VideoRecorder.getInstance().writeFrame(mRgba);
                                }
                            }));
                }
            }
        } else {
            processFrame(processMat);

            if (Constants.ENABLE_VIDEO_RECORDING && mSettings.getGeneral().isWriteVideo()
                    && VideoRecorder.getInstance().isRecording()) {
                VideoRecorder.getInstance().writeFrame(mRgba);
            }
        }

        synchronized (mNewStartSdkState) {
            if (!mStartSdkState.equals(mNewStartSdkState)) {
                mNewStartSdkState.copy(mStartSdkState);

                if (mStartSdkListener != null) {
                    mStartSdkListener.onStartSdkStateChanged(mStartSdkState, mTimeStamp);
                }
            }
        }

        drawFrameInfo(displayMat);
        return displayMat;
    }

    private Mat onCameraFramePostProcess() {
        if (!mIsPostProcessingInProgress && Constants.ENABLE_VIDEO_RECORDING && mSettings.getGeneral().isWriteVideo()
                && VideoRecorder.getInstance().isRecording()) {
            VideoRecorder.getInstance().writeFrame(mRgba);
        }

        return mRgba;
    }

    private void processFrame(Mat processMat) {
        // detect face
        if (mSettings.getProcessSettings().isTrackFace()) {
            Rect faceRect = detectFaceRect(processMat, mJavaDetector);

            if (faceRect != null) {
                synchronized (mNewStartSdkState) {
                    mNewStartSdkState.setHeadDetectedOk(true);
                }

                if (!mIsFaceDetected) {
                    mIsFaceDetected = true;

                    if (mStartSdkListener != null) {
                        mStartSdkListener.onFaceDetected(mTimeStamp);
                    }
                }

                int faceAreaSize = Utils.getFillFaceAreaSize(mCameraWidth, mCameraHeight, faceRect.width, faceRect.height);
                //SdkLog.d(TAG, "faceAreaSize: " + faceAreaSize);

                synchronized (mNewStartSdkState) {
                    if (faceAreaSize < Constants.MIN_FACE_AREA) {
                        mNewStartSdkState.setHeadCloseOk(true);
                        mNewStartSdkState.setHeadFarOk(false);
                    } else if (faceAreaSize > Constants.MAX_FACE_AREA) {
                        mNewStartSdkState.setHeadCloseOk(false);
                        mNewStartSdkState.setHeadFarOk(true);
                    } else {
                        mNewStartSdkState.setHeadCloseOk(true);
                        mNewStartSdkState.setHeadFarOk(true);
                    }
                }

                // aprox rectangle for eyes
                Rect eyeAreaRight = new Rect(faceRect.x + faceRect.width / 16,
                        (int) (faceRect.y + (faceRect.height / 4.5)),
                        (faceRect.width - 2 * faceRect.width / 16) / 2, (int) (faceRect.height / 3.0));
                Rect eyeAreaLeft = new Rect(faceRect.x + faceRect.width / 16
                        + (faceRect.width - 2 * faceRect.width / 16) / 2,
                        (int) (faceRect.y + (faceRect.height / 4.5)),
                        (faceRect.width - 2 * faceRect.width / 16) / 2, (int) (faceRect.height / 3.0));

                Rect rightEye = null;
                Point rightEyeCenter = null;

                Point leftEyeCenter = null;
                Rect leftEye = null;

                boolean eyeState = true;

                if (mSettings.getProcessSettings().isTrackRightEye()) {
                    rightEye = detectEyeRect(processMat, eyeAreaRight, mJavaDetectorEyeRight,
                            mRightEyePoints,
                            mCalibrateRightEyePointsCenter,
                            mCalibrateRightEyePointsTopLeft,
                            mCalibrateRightEyePointsTopRight,
                            mCalibrateRightEyePointsBottomLeft,
                            mCalibrateRightEyePointsBottomRight);

                    if (rightEye != null) {
                        if (!mIsEyeDetected) {
                            mIsEyeDetected = true;

                            if (mStartSdkListener != null) {
                                mStartSdkListener.onRightEyeDetected(mTimeStamp);
                            }
                        }
                    } else {
                        eyeState = false;

                        if (mIsEyeDetected) {
                            mIsEyeDetected = false;

                            if (mStartSdkListener != null) {
                                mStartSdkListener.onRightEyeLost(mTimeStamp);
                            }
                        }
                    }

                    if (mSettings.getProcessSettings().isTrackRightPupil()) {
                        rightEyeCenter = detectPupilCenter(processMat, rightEye, mRightPupilPoints,
                                mCalibrateRightPupilPointsCenter,
                                mCalibrateRightPupilPointsTopLeft,
                                mCalibrateRightPupilPointsTopRight,
                                mCalibrateRightPupilPointsBottomLeft,
                                mCalibrateRightPupilPointsBottomRight);
                    }
                }

                if (mSettings.getProcessSettings().isTrackLeftEye()) {
                    leftEye = detectEyeRect(processMat, eyeAreaLeft, mJavaDetectorEyeLeft, mLeftEyePoints,
                            mCalibrateLeftEyePointsCenter,
                            mCalibrateLeftEyePointsTopLeft,
                            mCalibrateLeftEyePointsTopRight,
                            mCalibrateLeftEyePointsBottomLeft,
                            mCalibrateLeftEyePointsBottomRight);

                    if (leftEye != null) {
                        if (!mIsEyeDetected) {
                            mIsEyeDetected = true;

                            if (mStartSdkListener != null) {
                                mStartSdkListener.onLeftEyeDetected(mTimeStamp);
                            }
                        }
                    } else {
                        eyeState = false;

                        if (mIsEyeDetected) {
                            mIsEyeDetected = false;

                            if (mStartSdkListener != null) {
                                mStartSdkListener.onLeftEyeLost(mTimeStamp);
                            }
                        }
                    }

                    if (mSettings.getProcessSettings().isTrackLeftPupil()) {
                        leftEyeCenter = detectPupilCenter(processMat, leftEye, mLeftPupilPoints,
                                mCalibrateLeftPupilPointsCenter,
                                mCalibrateLeftPupilPointsTopLeft,
                                mCalibrateLeftPupilPointsTopRight,
                                mCalibrateLeftPupilPointsBottomLeft,
                                mCalibrateLeftPupilPointsBottomRight);
                    }
                }

                synchronized (mNewStartSdkState) {
                    if (eyeState) {
                        mNewStartSdkState.setEyeDetectOk(true);
                    } else {
                        mNewStartSdkState.setEyeDetectOk(false);
                    }
                }

                synchronized (mProcessSync) {
                    mFaceRect = faceRect;
                    mRightEye = rightEye;
                    mRightEyeCenter = rightEyeCenter;
                    mLeftEyeCenter = leftEyeCenter;
                    mLeftEye = leftEye;

                    switch (mSettings.getGeneral().getCalibrateMode()) {
                        case Point5: {
                            detectPositionPoint5();
                            break;
                        }
                        case Point1:
                        default: {
                            detectPositionPoint1();
                            break;
                        }
                    }
                }
            } else {
                synchronized (mProcessSync) {
                    mFaceRect = null;
                    mRightEye = null;
                    mRightEyeCenter = null;
                    mLeftEyeCenter = null;
                    mLeftEye = null;
                }

                if (mIsFaceDetected) {
                    mIsFaceDetected = false;

                    if (mStartSdkListener != null) {
                        mStartSdkListener.onFaceLost(mTimeStamp);
                    }
                }

                synchronized (mNewStartSdkState) {
                    mNewStartSdkState.setHeadDetectedOk(false);
                    mNewStartSdkState.setHeadCloseOk(false);
                    mNewStartSdkState.setHeadFarOk(false);
                    mNewStartSdkState.setEyeDetectOk(false);
                }
            }

            if (mStartSdkListener != null) {
                DetectMode mode = mSettings.getGeneral().getDetectMode();
                int col = mCurrentSelectedCol;
                int row = mCurrentSelectedRow;
                mStartSdkListener.onFrameProcessed(mode, col, row, mIsEyeDetected, mIsGazeOutside, mTimeStamp);
            }
        }
    }

    private void drawFrameInfo(Mat displayMat) {
        try {
            // draw face rectangle
            if (mSettings.getUiSettings().isDrawFaceArea() && mFaceRect != null) {
                Imgproc.rectangle(displayMat, mFaceRect.tl(), mFaceRect.br(), FRAME_COLOR, 3);
            }

            if (mSettings.getUiSettings().isDrawRightEyeArea()) {
                if (mRightEye != null) {
                    Imgproc.rectangle(displayMat, mRightEye.tl(), mRightEye.br(), FRAME_COLOR, 2);
                }
            }

            if (mSettings.getUiSettings().isDrawLeftEyeArea()) {
                if (mLeftEye != null) {
                    Imgproc.rectangle(displayMat, mLeftEye.tl(), mLeftEye.br(), FRAME_COLOR, 2);
                }
            }

            if (mSettings.getUiSettings().isDrawRightPupilArea()) {
                if (mRightEyeCenter != null) {
                    Imgproc.circle(displayMat, mRightEyeCenter, 4, FRAME_COLOR);
                }
            }

            if (mSettings.getUiSettings().isDrawLeftPupilArea()) {
                if (mLeftEyeCenter != null) {
                    Imgproc.circle(displayMat, mLeftEyeCenter, 4, FRAME_COLOR);
                }
            }
        } catch (Exception e) {
            SdkLog.e(TAG, e);
        }
    }

    private Observable<Boolean> getProcessObservable(Mat mat) {
        final Mat processMat = mat.clone();

        Observable<Boolean> result = Observable.create(subscriber -> {
            try {
                processFrame(processMat);
                processMat.release();
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (Exception e) {
                SdkLog.e(TAG, e);
                subscriber.onError(e);
            }
        });

        return result;
    }

    private synchronized void doCalibrate(boolean startTimer) {
        if (mCalibrateType == CalibrateStateType.Done) {
            mCalibrateType = CalibrateStateType.None;
        }

        if (mCalibrateType == CalibrateStateType.None) {
            synchronized (mCalibrateSync) {
                // reset all calibration data
                mCalibrateFaceRectList.clear();
                mFaceRectList.clear();

                mCalibrateRightEyePointsCenter.clear();
                mCalibrateRightEyePointsTopLeft.clear();
                mCalibrateRightEyePointsTopRight.clear();
                mCalibrateRightEyePointsBottomLeft.clear();
                mCalibrateRightEyePointsBottomRight.clear();

                mCalibrateLeftEyePointsCenter.clear();
                mCalibrateLeftEyePointsTopLeft.clear();
                mCalibrateLeftEyePointsTopRight.clear();
                mCalibrateLeftEyePointsBottomLeft.clear();
                mCalibrateLeftEyePointsBottomRight.clear();

                mRightEyePoints.clear();
                mRightPupilPoints.clear();

                mLeftEyePoints.clear();
                mLeftPupilPoints.clear();

                mCalibrateRightPupilPointsCenter.clear();
                mCalibrateRightPupilPointsTopLeft.clear();
                mCalibrateRightPupilPointsTopRight.clear();
                mCalibrateRightPupilPointsBottomLeft.clear();
                mCalibrateRightPupilPointsBottomRight.clear();

                mCalibrateLeftPupilPointsCenter.clear();
                mCalibrateLeftPupilPointsTopLeft.clear();
                mCalibrateLeftPupilPointsTopRight.clear();
                mCalibrateLeftPupilPointsBottomLeft.clear();
                mCalibrateLeftPupilPointsBottomRight.clear();
            }

            mIsCalibrated = false;
            mIsCalibrating = true;
        }

        switch (mSettings.getGeneral().getCalibrateMode()) {
            case Point5: {
                switch (mCalibrateType) {
                    case None: {
                        mCalibrateType = CalibrateStateType.Center;

                        if (mStartSdkListener != null) {
                            mStartSdkListener.onCalibrateChanged(CalibrateType.Center, mTimeStamp);
                        }
                        break;
                    }
                    case Center: {
                        mCalibrateType = CalibrateStateType.TopLeft;

                        if (mStartSdkListener != null) {
                            mStartSdkListener.onCalibrateChanged(CalibrateType.TopLeft, mTimeStamp);
                        }
                        break;
                    }
                    case TopLeft: {
                        mCalibrateType = CalibrateStateType.TopRight;

                        if (mStartSdkListener != null) {
                            mStartSdkListener.onCalibrateChanged(CalibrateType.TopRight, mTimeStamp);
                        }
                        break;
                    }
                    case TopRight: {
                        mCalibrateType = CalibrateStateType.BottomRight;

                        if (mStartSdkListener != null) {
                            mStartSdkListener.onCalibrateChanged(CalibrateType.BottomRight, mTimeStamp);
                        }
                        break;
                    }
                    case BottomRight: {
                        mCalibrateType = CalibrateStateType.BottomLeft;

                        if (mStartSdkListener != null) {
                            mStartSdkListener.onCalibrateChanged(CalibrateType.BottomLeft, mTimeStamp);
                        }
                        break;
                    }
                    case BottomLeft: {
                        mCalibrateType = CalibrateStateType.Done;
                        mIsCalibrating = false;
                        mIsCalibrated = true;

                        if (mStartSdkListener != null && !mIsStopCalibrating) {
                            mStartSdkListener.onCalibrateCompleted(isCalibrateSuccess(), mTimeStamp);
                        }
                        break;
                    }
                }
                break;
            }
            case Point1:
            default: {
                switch (mCalibrateType) {
                    case None: {
                        mCalibrateType = CalibrateStateType.Center;

                        if (mStartSdkListener != null) {
                            mStartSdkListener.onCalibrateChanged(CalibrateType.Center, mTimeStamp);
                        }
                        break;
                    }
                    case Center: {
                        mCalibrateType = CalibrateStateType.Done;
                        mIsCalibrating = false;
                        mIsCalibrated = true;

                        if (mStartSdkListener != null && !mIsStopCalibrating) {
                            mStartSdkListener.onCalibrateCompleted(isCalibrateSuccess(), mTimeStamp);
                        }
                        break;
                    }
                }
                break;
            }

        }

        if (startTimer) {
            mTimeStamp = -1;
            mHandler.postDelayed(() -> {
                synchronized (mCalibrateSync) {
                    if (mCalibrateType != CalibrateStateType.Done) {
                        doCalibrate(startTimer);
                    }
                }
            }, Constants.CALIBRATE_DELAY);
        }
    }

    private Point detectPupilCenter(Mat mat, Rect eye, LinkedList<Point> point,
                                    LinkedList<Point> calibratePointsCenter,
                                    LinkedList<Point> calibratePointsTopLeft,
                                    LinkedList<Point> calibratePointsTopRight,
                                    LinkedList<Point> calibratePointsBottomLeft,
                                    LinkedList<Point> calibratePointsBottomRight) {
        if (eye != null) {
            Point pupil = PupilDetectHelper.detectEyeCenter(mat, eye, mSettings.getProcessSettings().getPupilAreaSize());

            if (mIsCalibrating) {
                synchronized (mCalibrateSync) {
                    switch (mSettings.getGeneral().getCalibrateMode()) {
                        case Point5: {
                            switch (mCalibrateType) {
                                case Center: {
                                    calibratePointsCenter.addLast(pupil.clone());

                                    if (calibratePointsCenter.size() > Constants.CALIBRATE_PUPIL_LIST_SIZE) {
                                        calibratePointsCenter.removeFirst();
                                    }
                                    break;
                                }
                                case TopLeft: {
                                    calibratePointsTopLeft.addLast(pupil.clone());

                                    if (calibratePointsTopLeft.size() > Constants.CALIBRATE_PUPIL_LIST_SIZE) {
                                        calibratePointsTopLeft.removeFirst();
                                    }
                                    break;
                                }
                                case TopRight: {
                                    calibratePointsTopRight.addLast(pupil.clone());

                                    if (calibratePointsTopRight.size() > Constants.CALIBRATE_PUPIL_LIST_SIZE) {
                                        calibratePointsTopRight.removeFirst();
                                    }
                                    break;
                                }
                                case BottomLeft: {
                                    calibratePointsBottomLeft.addLast(pupil.clone());

                                    if (calibratePointsBottomLeft.size() > Constants.CALIBRATE_PUPIL_LIST_SIZE) {
                                        calibratePointsBottomLeft.removeFirst();
                                    }
                                    break;
                                }
                                case BottomRight: {
                                    calibratePointsBottomRight.addLast(pupil.clone());

                                    if (calibratePointsBottomRight.size() > Constants.CALIBRATE_PUPIL_LIST_SIZE) {
                                        calibratePointsBottomRight.removeFirst();
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                        case Point1:
                        default: {
                            calibratePointsCenter.addLast(pupil.clone());

                            if (calibratePointsCenter.size() > Constants.CALIBRATE_PUPIL_LIST_SIZE) {
                                calibratePointsCenter.removeFirst();
                            }
                            break;
                        }
                    }
                }
            }

            if (point != null) {
                point.addLast(pupil.clone());

                if (point.size() > Constants.POINTS_LIST_SIZE) {
                    point.removeFirst();
                }
            }
        }

        Point result = Utils.getAveragePoint(point);
        return result;
    }

    private Rect detectEyeRect(Mat mat, Rect face, CascadeClassifier clasificator,
                               LinkedList<Rect> point,
                               LinkedList<Rect> calibratePointsCenter,
                               LinkedList<Rect> calibratePointsTopLeft,
                               LinkedList<Rect> calibratePointsTopRight,
                               LinkedList<Rect> calibratePointsBottomLeft,
                               LinkedList<Rect> calibratePointsBottomRight) {
        Rect result = null;

        try {
            Mat roi = mat.submat(face);
            MatOfRect eyes = new MatOfRect();
            clasificator.detectMultiScale(roi, eyes, 1.2, 2, Objdetect.CASCADE_FIND_BIGGEST_OBJECT | Objdetect.CASCADE_SCALE_IMAGE,
                    new Size(40, 40), new Size(0, 0));
            Rect[] eyesArray = eyes.toArray();

            if (eyesArray.length > 0) {
                Rect r = eyesArray[0];
                result = new Rect(r.x + face.x, r.y = r.y + face.y, r.width, r.height);

                if (mIsCalibrating) {
                    synchronized (calibratePointsCenter) {
                        switch (mSettings.getGeneral().getCalibrateMode()) {
                            case Point5: {
                                switch (mCalibrateType) {
                                    case Center: {
                                        calibratePointsCenter.addLast(result.clone());

                                        if (calibratePointsCenter.size() > Constants.CALIBRATE_EYE_LIST_SIZE) {
                                            calibratePointsCenter.removeFirst();
                                        }
                                        break;
                                    }
                                    case TopLeft: {
                                        calibratePointsTopLeft.addLast(result.clone());

                                        if (calibratePointsTopLeft.size() > Constants.CALIBRATE_EYE_LIST_SIZE) {
                                            calibratePointsTopLeft.removeFirst();
                                        }
                                        break;
                                    }
                                    case TopRight: {
                                        calibratePointsTopRight.addLast(result.clone());

                                        if (calibratePointsTopRight.size() > Constants.CALIBRATE_EYE_LIST_SIZE) {
                                            calibratePointsTopRight.removeFirst();
                                        }
                                        break;
                                    }
                                    case BottomLeft: {
                                        calibratePointsBottomLeft.addLast(result.clone());

                                        if (calibratePointsBottomLeft.size() > Constants.CALIBRATE_EYE_LIST_SIZE) {
                                            calibratePointsBottomLeft.removeFirst();
                                        }
                                        break;
                                    }
                                    case BottomRight: {
                                        calibratePointsBottomRight.addLast(result.clone());

                                        if (calibratePointsBottomRight.size() > Constants.CALIBRATE_EYE_LIST_SIZE) {
                                            calibratePointsBottomRight.removeFirst();
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                            case Point1:
                            default: {
                                calibratePointsCenter.addLast(result.clone());

                                if (calibratePointsCenter.size() > Constants.CALIBRATE_EYE_LIST_SIZE) {
                                    calibratePointsCenter.removeFirst();
                                }
                                break;
                            }
                        }
                    }
                }

                point.addLast(result.clone());

                if (point.size() > Constants.FACE_LIST_SIZE) {
                    point.removeFirst();
                }
            } else {
                if (point.size() > 0) {
                    result = point.removeLast();
                }
            }

            //result = Utils.getAverageRect(point);
        } catch (Exception e) {
            SdkLog.e(TAG, e);
        }

        return result;
    }

    private Rect detectFaceRect(Mat scanMat, CascadeClassifier clasificator) {
        Rect result = null;
        MatOfRect faces = new MatOfRect();
        Mat faceMat = new Mat();

        Imgproc.GaussianBlur(scanMat, faceMat, new org.opencv.core.Size(9, 9), 0);

        if (clasificator != null) {
            // can change value 1.2 to 1.1, but in this case have bad performance
            clasificator.detectMultiScale(scanMat, faces, 1.2, 2, Objdetect.CASCADE_FIND_BIGGEST_OBJECT | Objdetect.CASCADE_SCALE_IMAGE,
                    new Size(100, 100), new Size(0, 0));
        }

        faceMat.release();
        Rect[] facesArray = faces.toArray();

        if (facesArray.length > 0) {
            result = facesArray[0];

            if (mIsCalibrating) {
                synchronized (mCalibrateSync) {
                    if (mCalibrateFaceRectList != null) {
                        mCalibrateFaceRectList.addLast(result.clone());

                        if (mCalibrateFaceRectList.size() > Constants.CALIBRATE_FACE_LIST_SIZE) {
                            mCalibrateFaceRectList.removeFirst();
                        }
                    }
                }
            }

            mFaceRectList.addLast(result);

            if (mFaceRectList.size() > Constants.FACE_LIST_SIZE) {
                mFaceRectList.removeFirst();
            }

            result = Utils.getAverageRect(mFaceRectList);
        } else {
            if (mFaceRectList.size() > 0) {
                result = mFaceRectList.removeLast();
            }
        }

        return result;
    }

    private void detectPositionPoint1() {
        if (mIsCalibrated) {
            Point offsetRightPoint = null;
            Point offsetLeftPoint = null;

            if (mSettings.getProcessSettings().isTrackRightPupil()) {
                offsetRightPoint = calculateFullOffsetPoint1(mRightEyePoints, mCalibrateRightEyePointsCenter, mRightPupilPoints, mCalibrateRightPupilPointsCenter);
                //Log.d(TAG, "Right, offsetX: " + offsetRightPoint.x + ", offsetY: " + offsetRightPoint.y);
            }

            if (mSettings.getProcessSettings().isTrackLeftPupil()) {
                offsetLeftPoint = calculateFullOffsetPoint1(mLeftEyePoints, mCalibrateLeftEyePointsCenter, mLeftPupilPoints, mCalibrateLeftPupilPointsCenter);
                //Log.d(TAG, "Left, offsetX: " + offsetRightPoint.x + ", offsetY: " + offsetRightPoint.y);
            }

            if (mStartSdkListener != null) {
                mStartSdkListener.onPositionUpdated(offsetRightPoint, offsetLeftPoint, mTimeStamp);
            }

            switch (mSettings.getGeneral().getDetectMode()) {
                case Mode2x1: {
                    detectPositionMode2x1Point1(offsetRightPoint, offsetLeftPoint);
                    break;
                }
                case Mode3x1: {
                    detectPositionMode3x1Point1(offsetRightPoint, offsetLeftPoint);
                    break;
                }
                case Mode2x2: {
                    detectPositionMode2x2Point1(offsetRightPoint, offsetLeftPoint);
                    break;
                }
                case Mode3x2: {
                    detectPositionMode3x2Point1(offsetRightPoint, offsetLeftPoint);
                    break;
                }
                case Mode4x2: {
                    detectPositionMode4x2Point1(offsetRightPoint, offsetLeftPoint);
                    break;
                }
            }
        }
    }

    private void detectPositionPoint5() {
        if (mIsCalibrated) {
            Point offsetRightPoint = null;
            Point offsetLeftPoint = null;

            if (mSettings.getProcessSettings().isTrackRightPupil()) {
                offsetRightPoint = calculateFullOffsetPoint5(mRightEyePoints,
                        mCalibrateRightEyePointsCenter,
                        mCalibrateRightEyePointsTopLeft,
                        mCalibrateRightEyePointsTopRight,
                        mCalibrateRightEyePointsBottomLeft,
                        mCalibrateRightEyePointsBottomRight,
                        mRightPupilPoints,
                        mCalibrateRightPupilPointsCenter,
                        mCalibrateRightPupilPointsTopLeft,
                        mCalibrateRightPupilPointsTopRight,
                        mCalibrateRightPupilPointsBottomLeft,
                        mCalibrateRightPupilPointsBottomRight);
            }

            if (mSettings.getProcessSettings().isTrackLeftPupil()) {
                offsetLeftPoint = calculateFullOffsetPoint5(mLeftEyePoints,
                        mCalibrateLeftEyePointsCenter,
                        mCalibrateLeftEyePointsTopLeft,
                        mCalibrateLeftEyePointsTopRight,
                        mCalibrateLeftEyePointsBottomLeft,
                        mCalibrateLeftEyePointsBottomRight,
                        mLeftPupilPoints,
                        mCalibrateLeftPupilPointsCenter,
                        mCalibrateLeftPupilPointsTopLeft,
                        mCalibrateLeftPupilPointsTopRight,
                        mCalibrateLeftPupilPointsBottomLeft,
                        mCalibrateLeftPupilPointsBottomRight);
            }

            if (mStartSdkListener != null) {
                mStartSdkListener.onPositionUpdated(offsetRightPoint, offsetLeftPoint, mTimeStamp);
            }

            switch (mSettings.getGeneral().getDetectMode()) {
                case Mode2x1: {
                    detectPositionMode2x1Point5(offsetRightPoint, offsetLeftPoint);
                    break;
                }
                case Mode3x1: {
                    detectPositionMode3x1Point5(offsetRightPoint, offsetLeftPoint);
                    break;
                }
                case Mode2x2: {
                    detectPositionMode2x2Point5(offsetRightPoint, offsetLeftPoint);
                    break;
                }
                case Mode3x2: {
                    detectPositionMode3x2Point5(offsetRightPoint, offsetLeftPoint);
                    break;
                }
                case Mode4x2: {
                    detectPositionMode4x2Point5(offsetRightPoint, offsetLeftPoint);
                    break;
                }
            }
        }
    }

    private void detectPositionMode2x1Point1(Point offsetRightPoint, Point offsetLeftPoint) {
        double xOffset = 0;
        int selectedCol = 0;
        int selectedRow = 0;
        boolean isGazeOutsideX = false;

        if (offsetRightPoint != null) {
            xOffset += offsetRightPoint.x;
        }

        if (offsetLeftPoint != null) {
            xOffset += offsetLeftPoint.x;
        }

        if (Math.abs(xOffset) > mSettings.getProcessSettings().getGazeOutsizeValueX()) {
            isGazeOutsideX = true;
            selectedCol = -1;
        } else {
            isGazeOutsideX = false;
            selectedCol = mCurrentSelectedCol;

            if (Math.abs(xOffset) > mSettings.getProcessSettings().getCalibrationX()) {
                if (xOffset > 0) {
                    selectedCol = 0;
                } else {
                    selectedCol = 1;
                }
            }

            if (isGazeOutsideX) {
                mCurrentSelectedCol = -1;
            }
        }

        if (mCurrentSelectedCol != selectedCol || mCurrentSelectedRow != selectedRow) {
            mCurrentSelectedCol = selectedCol;
            mCurrentSelectedRow = selectedRow;

            if (mStartSdkListener != null) {
                mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                        mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
            }
        }

        boolean gazeOutside = isGazeOutsideX;

        if (mIsGazeOutside != gazeOutside) {
            mIsGazeOutside = gazeOutside;

            if (mStartSdkListener != null) {
                if (mIsGazeOutside) {
                    mStartSdkListener.onGazeOutside(mTimeStamp);
                } else {
                    mStartSdkListener.onGazeDetected(mTimeStamp);
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }
    }

    private void detectPositionMode2x2Point1(Point offsetRightPoint, Point offsetLeftPoint) {
        double yOffset = 0;
        double xOffset = 0;
        int selectedCol = -1;
        int selectedRow = -1;
        boolean isGazeOutsideX = false;
        boolean isGazeOutsideY = false;

        if (offsetRightPoint != null) {
            xOffset += offsetRightPoint.x;
            yOffset += offsetRightPoint.y;
        }

        if (offsetLeftPoint != null) {
            xOffset += offsetLeftPoint.x;
            yOffset += offsetLeftPoint.y;
        }

        if (Math.abs(yOffset) > mSettings.getProcessSettings().getGazeOutsizeValueY()) {
            isGazeOutsideY = true;
        } else {
            isGazeOutsideY = false;
            if (Math.abs(yOffset) > mSettings.getProcessSettings().getCalibrationY()) {
                if (yOffset > 0) {
                    selectedRow = 1;
                } else {
                    selectedRow = 0;
                }
            }
        }

        if (Math.abs(xOffset) > mSettings.getProcessSettings().getGazeOutsizeValueX()) {
            isGazeOutsideX = true;
        } else {
            isGazeOutsideX = false;

            if (Math.abs(xOffset) > mSettings.getProcessSettings().getCalibrationX()) {
                if (xOffset > 0) {
                    selectedCol = 0;
                } else {
                    selectedCol = 1;
                }
            }

            if (mCurrentSelectedCol != selectedCol || mCurrentSelectedRow != selectedRow) {
                mCurrentSelectedCol = selectedCol;
                mCurrentSelectedRow = selectedRow;

                if (mStartSdkListener != null) {
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }

        boolean gazeOutside = isGazeOutsideX || isGazeOutsideY;

        if (mIsGazeOutside != gazeOutside) {
            mIsGazeOutside = gazeOutside;

            if (mStartSdkListener != null) {
                if (mIsGazeOutside) {
                    mStartSdkListener.onGazeOutside(mTimeStamp);
                } else {
                    mStartSdkListener.onGazeDetected(mTimeStamp);
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }
    }

    private void detectPositionMode2x1Point5(Point offsetRightPoint, Point offsetLeftPoint) {
        double xOffset = 0;
        int selectedCol = -1;
        int selectedRow = 0;
        boolean isGazeOutsideX = false;

        if (offsetRightPoint != null) {
            xOffset += offsetRightPoint.x;
        }

        if (offsetLeftPoint != null) {
            xOffset += offsetLeftPoint.x;
        }

        if (Math.abs(xOffset) > mSettings.getProcessSettings().getGazeOutsizeValueX()) {
            isGazeOutsideX = true;
        } else {
            isGazeOutsideX = false;

            if (Math.abs(xOffset) > mSettings.getProcessSettings().getCalibrationX()) {
                if (xOffset > 0) {
                    selectedCol = 0;
                } else {
                    selectedCol = 1;
                }
            }

            if (mCurrentSelectedCol != selectedCol || mCurrentSelectedRow != selectedRow) {
                mCurrentSelectedCol = selectedCol;
                mCurrentSelectedRow = selectedRow;

                if (mStartSdkListener != null) {
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }

        boolean gazeOutside = isGazeOutsideX;

        if (mIsGazeOutside != gazeOutside) {
            mIsGazeOutside = gazeOutside;

            if (mStartSdkListener != null) {
                if (mIsGazeOutside) {
                    mStartSdkListener.onGazeOutside(mTimeStamp);
                } else {
                    mStartSdkListener.onGazeDetected(mTimeStamp);
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }
    }

    private void detectPositionMode2x2Point5(Point offsetRightPoint, Point offsetLeftPoint) {
        double yOffset = 0;
        double xOffset = 0;
        int selectedCol = -1;
        int selectedRow = -1;
        boolean isGazeOutsideX = false;
        boolean isGazeOutsideY = false;

        if (offsetRightPoint != null) {
            xOffset += offsetRightPoint.x;
            yOffset += offsetRightPoint.y;
        }

        if (offsetLeftPoint != null) {
            xOffset += offsetLeftPoint.x;
            yOffset += offsetLeftPoint.y;
        }

        if (Math.abs(yOffset) > mSettings.getProcessSettings().getGazeOutsizeValueY()) {
            isGazeOutsideY = true;
        } else {
            isGazeOutsideY = false;
            if (Math.abs(yOffset) > mSettings.getProcessSettings().getCalibrationY()) {
                if (yOffset > mSettings.getProcessSettings().getVerticalCalibrationOffset()) {
                    selectedRow = 1;
                } else {
                    selectedRow = 0;
                }
            }
        }

        if (Math.abs(xOffset) > mSettings.getProcessSettings().getGazeOutsizeValueX()) {
            isGazeOutsideX = true;
        } else {
            isGazeOutsideX = false;

            if (Math.abs(xOffset) > mSettings.getProcessSettings().getCalibrationX()) {
                if (xOffset > 0) {
                    selectedCol = 0;
                } else {
                    selectedCol = 1;
                }
            }

            if (mCurrentSelectedCol != selectedCol || mCurrentSelectedRow != selectedRow) {
                mCurrentSelectedCol = selectedCol;
                mCurrentSelectedRow = selectedRow;

                if (mStartSdkListener != null) {
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }

        boolean gazeOutside = isGazeOutsideX || isGazeOutsideY;

        if (mIsGazeOutside != gazeOutside) {
            mIsGazeOutside = gazeOutside;

            if (mStartSdkListener != null) {
                if (mIsGazeOutside) {
                    mStartSdkListener.onGazeOutside(mTimeStamp);
                } else {
                    mStartSdkListener.onGazeDetected(mTimeStamp);
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }
    }

    private void detectPositionMode3x2Point1(Point offsetRightPoint, Point offsetLeftPoint) {
        double yOffset = 0;
        double xOffset = 0;
        int selectedCol = -1;
        int selectedRow = -1;
        boolean isGazeOutsideX = false;
        boolean isGazeOutsideY = false;

        if (offsetRightPoint != null) {
            xOffset += offsetRightPoint.x;
            yOffset += offsetRightPoint.y;
        }

        if (offsetLeftPoint != null) {
            xOffset += offsetLeftPoint.x;
            yOffset += offsetLeftPoint.y;
        }

        if (Math.abs(yOffset) > mSettings.getProcessSettings().getGazeOutsizeValueY()) {
            isGazeOutsideY = true;
        } else {
            isGazeOutsideY = false;
            if (Math.abs(yOffset) > mSettings.getProcessSettings().getCalibrationY()) {
                if (yOffset > mSettings.getProcessSettings().getVerticalCalibrationOffset()) {
                    selectedRow = 1;
                } else {
                    selectedRow = 0;
                }
            }
        }

        if (Math.abs(xOffset) > mSettings.getProcessSettings().getGazeOutsizeValueX()) {
            isGazeOutsideX = true;
        } else {
            isGazeOutsideX = false;

            if (Math.abs(xOffset) > mSettings.getProcessSettings().getCalibrationX()) {
                double xSubOffset = (double) mSettings.getProcessSettings().getCenterAreaSize() / 100 / 2;

                if (xOffset > 0) {
                    if (xOffset > xSubOffset) {
                        selectedCol = 0;
                    } else {
                        selectedCol = 1;
                    }
                } else {
                    if (xOffset < -xSubOffset) {
                        selectedCol = 2;
                    } else {
                        selectedCol = 1;
                    }
                }
            }

            if (mCurrentSelectedCol != selectedCol || mCurrentSelectedRow != selectedRow) {
                mCurrentSelectedRow = selectedRow;
                mCurrentSelectedCol = selectedCol;

                if (mStartSdkListener != null) {
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }

        boolean gazeOutside = isGazeOutsideX || isGazeOutsideY;

        if (mIsGazeOutside != gazeOutside) {
            mIsGazeOutside = gazeOutside;

            if (mStartSdkListener != null) {
                if (mIsGazeOutside) {
                    mStartSdkListener.onGazeOutside(mTimeStamp);
                } else {
                    mStartSdkListener.onGazeDetected(mTimeStamp);
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }
    }

    private void detectPositionMode3x1Point1(Point offsetRightPoint, Point offsetLeftPoint) {
        double yOffset = 0;
        double xOffset = 0;
        int selectedCol = 0;
        int selectedRow = 0;
        boolean isGazeOutsideX = false;
        boolean isGazeOutsideY = false;

        if (offsetRightPoint != null) {
            xOffset += offsetRightPoint.x;
            yOffset += offsetRightPoint.y;
        }

        if (offsetLeftPoint != null) {
            xOffset += offsetLeftPoint.x;
            yOffset += offsetLeftPoint.y;
        }

        if (Math.abs(yOffset) > mSettings.getProcessSettings().getGazeOutsizeValueY()) {
            isGazeOutsideY = true;
        } else {
            isGazeOutsideY = false;
        }

        if (Math.abs(xOffset) > mSettings.getProcessSettings().getGazeOutsizeValueX()) {
            isGazeOutsideX = true;
            selectedCol = -1;
        } else {
            isGazeOutsideX = false;
            selectedCol = mCurrentSelectedCol;

            if (Math.abs(xOffset) > mSettings.getProcessSettings().getCalibrationX()) {
                double xSubOffset = (double) mSettings.getProcessSettings().getCenterAreaSize() / 100 / 2;

                if (xOffset > 0) {
                    if (xOffset > xSubOffset) {
                        selectedCol = 0;
                    } else {
                        selectedCol = 1;
                    }
                } else {
                    if (xOffset < -xSubOffset) {
                        selectedCol = 2;
                    } else {
                        selectedCol = 1;
                    }
                }
            }

            if (isGazeOutsideX) {
                mCurrentSelectedCol = -1;
            }
        }

        if (mCurrentSelectedCol != selectedCol || mCurrentSelectedRow != selectedRow) {
            mCurrentSelectedRow = selectedRow;
            mCurrentSelectedCol = selectedCol;

            if (mStartSdkListener != null) {
                mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                        mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
            }
        }

        boolean gazeOutside = isGazeOutsideX || isGazeOutsideY;

        if (mIsGazeOutside != gazeOutside) {
            mIsGazeOutside = gazeOutside;

            if (mStartSdkListener != null) {
                if (mIsGazeOutside) {
                    mStartSdkListener.onGazeOutside(mTimeStamp);
                } else {
                    mStartSdkListener.onGazeDetected(mTimeStamp);
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }
    }

    private void detectPositionMode3x2Point5(Point offsetRightPoint, Point offsetLeftPoint) {
        double yOffset = 0;
        double xOffset = 0;
        int selectedCol = -1;
        int selectedRow = -1;
        boolean isGazeOutsideX = false;
        boolean isGazeOutsideY = false;

        if (offsetRightPoint != null) {
            xOffset += offsetRightPoint.x;
            yOffset += offsetRightPoint.y;
        }

        if (offsetLeftPoint != null) {
            xOffset += offsetLeftPoint.x;
            yOffset += offsetLeftPoint.y;
        }

//        if (Math.abs(yOffset) > mSettings.getProcessSettings().getGazeOutsizeValueY()) {
//            isGazeOutsideY = true;
//        } else
        {
            isGazeOutsideY = false;
            if (Math.abs(yOffset) > mSettings.getProcessSettings().getCalibrationY()) {
                if (yOffset > 0) {
                    selectedRow = 1;
                } else {
                    selectedRow = 0;
                }
            }
        }

//        if (Math.abs(xOffset) > mSettings.getProcessSettings().getGazeOutsizeValueX()) {
//            isGazeOutsideX = true;
//        } else
        {
            isGazeOutsideX = false;

            if (Math.abs(xOffset) > mSettings.getProcessSettings().getCalibrationX()) {
                double xSubOffset = (double) mSettings.getProcessSettings().getCenterAreaSize() / 100 / 2;

                if (offsetLeftPoint != null && offsetRightPoint != null) {
                    xSubOffset = xSubOffset * 2;
                }

                if (xOffset > 0) {
                    if (xOffset > xSubOffset) {
                        selectedCol = 0;
                    } else {
                        selectedCol = 1;
                    }
                } else {
                    if (xOffset < -xSubOffset) {
                        selectedCol = 2;
                    } else {
                        selectedCol = 1;
                    }
                }
            }

            if (mCurrentSelectedCol != selectedCol || mCurrentSelectedRow != selectedRow) {
                mCurrentSelectedRow = selectedRow;
                mCurrentSelectedCol = selectedCol;

                if (mStartSdkListener != null) {
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }

        boolean gazeOutside = isGazeOutsideX || isGazeOutsideY;

        if (mIsGazeOutside != gazeOutside) {
            mIsGazeOutside = gazeOutside;

            if (mStartSdkListener != null) {
                if (mIsGazeOutside) {
                    mStartSdkListener.onGazeOutside(mTimeStamp);
                } else {
                    mStartSdkListener.onGazeDetected(mTimeStamp);
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }
    }

    private void detectPositionMode3x1Point5(Point offsetRightPoint, Point offsetLeftPoint) {
        double yOffset = 0;
        double xOffset = 0;
        int selectedCol = -1;
        int selectedRow = 0;
        boolean isGazeOutsideX = false;
        boolean isGazeOutsideY = false;

        if (offsetRightPoint != null) {
            xOffset += offsetRightPoint.x;
            yOffset += offsetRightPoint.y;
        }

        if (offsetLeftPoint != null) {
            xOffset += offsetLeftPoint.x;
            yOffset += offsetLeftPoint.y;
        }

//        if (Math.abs(yOffset) > mSettings.getProcessSettings().getGazeOutsizeValueY()) {
//            isGazeOutsideY = true;
//        } else
        {
            isGazeOutsideY = false;
        }

//        if (Math.abs(xOffset) > mSettings.getProcessSettings().getGazeOutsizeValueX()) {
//            isGazeOutsideX = true;
//        } else
        {
            isGazeOutsideX = false;

            if (Math.abs(xOffset) > mSettings.getProcessSettings().getCalibrationX()) {
                double xSubOffset = (double) mSettings.getProcessSettings().getCenterAreaSize() / 100 / 2;

                if (offsetLeftPoint != null && offsetRightPoint != null) {
                    xSubOffset = xSubOffset * 2;
                }

                if (xOffset > 0) {
                    if (xOffset > xSubOffset) {
                        selectedCol = 0;
                    } else {
                        selectedCol = 1;
                    }
                } else {
                    if (xOffset < -xSubOffset) {
                        selectedCol = 2;
                    } else {
                        selectedCol = 1;
                    }
                }
            }

            if (mCurrentSelectedCol != selectedCol || mCurrentSelectedRow != selectedRow) {
                mCurrentSelectedRow = selectedRow;
                mCurrentSelectedCol = selectedCol;

                if (mStartSdkListener != null) {
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }

        boolean gazeOutside = isGazeOutsideX || isGazeOutsideY;

        if (mIsGazeOutside != gazeOutside) {
            mIsGazeOutside = gazeOutside;

            if (mStartSdkListener != null) {
                if (mIsGazeOutside) {
                    mStartSdkListener.onGazeOutside(mTimeStamp);
                } else {
                    mStartSdkListener.onGazeDetected(mTimeStamp);
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }
    }

    private void detectPositionMode4x2Point1(Point offsetRightPoint, Point offsetLeftPoint) {
        double yOffset = 0;
        double xOffset = 0;
        int selectedCol = -1;
        int selectedRow = -1;
        boolean isGazeOutsideX = false;
        boolean isGazeOutsideY = false;

        if (offsetRightPoint != null) {
            xOffset += offsetRightPoint.x;
            yOffset += offsetRightPoint.y;
        }

        if (offsetLeftPoint != null) {
            xOffset += offsetLeftPoint.x;
            yOffset += offsetLeftPoint.y;
        }

        if (Math.abs(yOffset) > mSettings.getProcessSettings().getGazeOutsizeValueY()) {
            isGazeOutsideY = true;
        } else {
            isGazeOutsideY = false;

            if (Math.abs(yOffset) > mSettings.getProcessSettings().getCalibrationY()) {
                if (yOffset > mSettings.getProcessSettings().getVerticalCalibrationOffset()) {
                    selectedRow = 1;
                } else {
                    selectedRow = 0;
                }
            }
        }

        if (Math.abs(xOffset) > mSettings.getProcessSettings().getGazeOutsizeValueX()) {
            isGazeOutsideX = true;
        } else {
            isGazeOutsideX = false;

            if (Math.abs(xOffset) > mSettings.getProcessSettings().getCalibrationX()) {
                double leftSubOffset = (double) mSettings.getProcessSettings().getLeftAreaSize() / 100;
                double rightSubOffset = -(double) mSettings.getProcessSettings().getRightAreaSize() / 100;

                if (xOffset > 0) {
                    if (xOffset > leftSubOffset) {
                        selectedCol = 0;
                    } else {
                        selectedCol = 1;
                    }
                } else {
                    if (xOffset < rightSubOffset) {
                        selectedCol = 3;
                    } else {
                        selectedCol = 2;
                    }
                }
            }

            if (mCurrentSelectedCol != selectedCol || mCurrentSelectedRow != selectedRow) {
                mCurrentSelectedRow = selectedRow;
                mCurrentSelectedCol = selectedCol;

                if (mStartSdkListener != null) {
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }

        boolean gazeOutside = isGazeOutsideX || isGazeOutsideY;

        if (mIsGazeOutside != gazeOutside) {
            mIsGazeOutside = gazeOutside;

            if (mStartSdkListener != null) {
                if (mIsGazeOutside) {
                    mStartSdkListener.onGazeOutside(mTimeStamp);
                } else {
                    mStartSdkListener.onGazeDetected(mTimeStamp);
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }
    }

    private void detectPositionMode4x2Point5(Point offsetRightPoint, Point offsetLeftPoint) {
        double yOffset = 0;
        double xOffset = 0;
        int selectedCol = -1;
        int selectedRow = -1;
        boolean isGazeOutsideX = false;
        boolean isGazeOutsideY = false;

        if (offsetRightPoint != null) {
            xOffset += offsetRightPoint.x;
            yOffset += offsetRightPoint.y;
        }

        if (offsetLeftPoint != null) {
            xOffset += offsetLeftPoint.x;
            yOffset += offsetLeftPoint.y;
        }

//        if (Math.abs(yOffset) > mSettings.getProcessSettings().getGazeOutsizeValueY()) {
//            isGazeOutsideY = true;
//        } else
        {
            isGazeOutsideY = false;

            if (Math.abs(yOffset) > mSettings.getProcessSettings().getCalibrationY()) {
                if (yOffset > 0) {
                    selectedRow = 1;
                } else {
                    selectedRow = 0;
                }
            }
        }

//        if (Math.abs(xOffset) > mSettings.getProcessSettings().getGazeOutsizeValueX()) {
//            isGazeOutsideX = true;
//        } else
        {
            isGazeOutsideX = false;

            if (Math.abs(xOffset) > mSettings.getProcessSettings().getCalibrationX()) {
                double leftSubOffset = (double) mSettings.getProcessSettings().getLeftAreaSize() / 100;
                double rightSubOffset = -(double) mSettings.getProcessSettings().getRightAreaSize() / 100;

                if (xOffset > 0) {
                    if (xOffset > leftSubOffset) {
                        selectedCol = 0;
                    } else {
                        selectedCol = 1;
                    }
                } else {
                    if (xOffset < rightSubOffset) {
                        selectedCol = 3;
                    } else {
                        selectedCol = 2;
                    }
                }
            }

            if (mCurrentSelectedCol != selectedCol || mCurrentSelectedRow != selectedRow) {
                mCurrentSelectedRow = selectedRow;
                mCurrentSelectedCol = selectedCol;

                if (mStartSdkListener != null) {
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }

        boolean gazeOutside = isGazeOutsideX || isGazeOutsideY;

        if (mIsGazeOutside != gazeOutside) {
            mIsGazeOutside = gazeOutside;

            if (mStartSdkListener != null) {
                if (mIsGazeOutside) {
                    mStartSdkListener.onGazeOutside(mTimeStamp);
                } else {
                    mStartSdkListener.onGazeDetected(mTimeStamp);
                    mStartSdkListener.onDetectedSelectedArea(mSettings.getGeneral().getDetectMode(),
                            mCurrentSelectedCol, mCurrentSelectedRow, mTimeStamp);
                }
            }
        }
    }

    private Point calculateFullOffsetPoint1(LinkedList<Rect> eyeList, LinkedList<Rect> calibrateEyeList,
                                            LinkedList<Point> pupilList, LinkedList<Point> calibratePupilList) {
        Point calibratePupil = Utils.getAveragePoint(calibratePupilList);
        Point pupil = Utils.getAveragePoint(pupilList);

        Rect calibrateEye = Utils.getAverageRect(calibrateEyeList);
        Rect eye = Utils.getAverageRect(eyeList);

        double cdx1 = Math.abs(calibrateEye.x - calibratePupil.x) / calibrateEye.width;
        double cdx2 = Math.abs(calibrateEye.x + calibrateEye.width - calibratePupil.x) / calibrateEye.width;

        double cdy1 = Math.abs(calibrateEye.y - calibratePupil.y) / calibrateEye.height;
        double cdy2 = Math.abs(calibrateEye.y + calibrateEye.height - calibratePupil.y) / calibrateEye.height;

        double dx1 = Math.abs(eye.x - pupil.x) / eye.width;
        double dx2 = Math.abs(eye.x + eye.width - pupil.x) / eye.width;

        double dy1 = Math.abs(eye.y - pupil.y) / eye.height;
        double dy2 = Math.abs(eye.y + eye.height - pupil.y) / eye.height;

        double ox = 0;
        double oy = 0;

        if (cdx1 < dx1) {
            ox = Math.abs(dx1 - cdx1) * 4;
        } else {
            ox = -Math.abs(dx2 - cdx2) * 4;
        }

        if (cdy1 < dy1) {
            oy = Math.abs(dy1 - cdy1) * 4;
        } else {
            oy = -Math.abs(dy2 - cdy2) * 4;
        }

        Point result = new Point(ox, oy);
        return result;
    }


    private Point calculateFullOffsetPoint5(LinkedList<Rect> eyeList,
                                            LinkedList<Rect> calibrateEyeListCenter,
                                            LinkedList<Rect> calibrateEyeListTopLeft,
                                            LinkedList<Rect> calibrateEyeListTopRight,
                                            LinkedList<Rect> calibrateEyeListBottomLeft,
                                            LinkedList<Rect> calibrateEyeListBottomRight,
                                            LinkedList<Point> pupilList,
                                            LinkedList<Point> calibratePupilListCenter,
                                            LinkedList<Point> calibratePupilListTopLeft,
                                            LinkedList<Point> calibratePupilListTopRight,
                                            LinkedList<Point> calibratePupilListBottomLeft,
                                            LinkedList<Point> calibratePupilListBottomRight) {

        Point calibratePupilCenter = Utils.getAveragePoint(calibratePupilListCenter);
        Point calibratePupilTopLeft = Utils.getAveragePoint(calibratePupilListTopLeft);
        Point calibratePupilTopRight = Utils.getAveragePoint(calibratePupilListTopRight);
        Point calibratePupilBottomLeft = Utils.getAveragePoint(calibratePupilListBottomLeft);
        Point calibratePupilBottomRight = Utils.getAveragePoint(calibratePupilListBottomRight);
        Point pupil = Utils.getAveragePoint(pupilList);

        Rect calibrateEyeCenter = Utils.getAverageRect(calibrateEyeListCenter);
        Rect calibrateEyeTopLeft = Utils.getAverageRect(calibrateEyeListTopLeft);
        Rect calibrateEyeTopRight = Utils.getAverageRect(calibrateEyeListTopRight);
        Rect calibrateEyeBottomLeft = Utils.getAverageRect(calibrateEyeListBottomLeft);
        Rect calibrateEyeBottomRight = Utils.getAverageRect(calibrateEyeListBottomRight);
        Rect eye = Utils.getAverageRect(eyeList);

        double cdx = 0;
        double cdy = 0;

        double dxCurrent1 = Math.abs(eye.x - pupil.x);
        double dxCurrent2 = Math.abs(eye.x + eye.width - pupil.x);
        double dxCurrent = dxCurrent1 / dxCurrent2;

        double cdxCenter1 = Math.abs(calibrateEyeCenter.x - calibratePupilCenter.x);
        double cdxCenter2 = Math.abs(calibrateEyeCenter.x + calibrateEyeCenter.width - calibratePupilCenter.x);
        double cdxCenter = cdxCenter1 / cdxCenter2;

        double cdx2 = Math.abs(cdxCenter - dxCurrent);

        double cdxRight1 = Math.abs(calibrateEyeTopRight.x - calibratePupilTopRight.x);
        double cdxRight2 = Math.abs(calibrateEyeTopRight.x + calibrateEyeTopRight.width - calibratePupilTopRight.x);
        double cdxRight = cdxRight1 / cdxRight2;

        double cdxLeft1 = Math.abs(calibrateEyeTopLeft.x - calibratePupilTopLeft.x);
        double cdxLeft2 = Math.abs(calibrateEyeTopLeft.x + calibrateEyeTopLeft.width - calibratePupilTopLeft.x);
        double cdxLeft = cdxLeft1 / cdxLeft2;

        if (dxCurrent > cdxCenter) {
            // left side
            double cdx1 = Math.abs(cdxLeft - dxCurrent);
            cdx = cdx1 / cdx2;
        } else {
            // right side
            double cdx1 = Math.abs(cdxRight - cdxCenter);
            cdx = cdx1 / cdx2 - 1;
        }

        double dyCurrent1 = Math.abs(eye.y - pupil.y);
        double dyCurrent2 = Math.abs(eye.y + eye.height - pupil.y);
        double dyCurrent = dyCurrent1 / dyCurrent2;

        double cdyCenter1 = Math.abs(calibrateEyeCenter.y - calibratePupilCenter.y);
        double cdyCenter2 = Math.abs(calibrateEyeCenter.y + calibrateEyeCenter.height - calibratePupilCenter.y);
        double cdyCenter = cdyCenter1 / cdyCenter2;

        double cdy2 = Math.abs(cdyCenter - dyCurrent);

        double cdyRight1 = Math.abs(calibrateEyeTopRight.y - calibratePupilTopRight.y);
        double cdyRight2 = Math.abs(calibrateEyeTopRight.y + calibrateEyeTopRight.height - calibratePupilTopRight.y);
        double cdyRight = cdyRight1 / cdyRight2;

        double cdyLeft1 = Math.abs(calibrateEyeTopLeft.y - calibratePupilTopLeft.y);
        double cdyLeft2 = Math.abs(calibrateEyeTopLeft.y + calibrateEyeTopLeft.height - calibratePupilTopLeft.y);
        double cdyLeft = cdyLeft1 / cdyLeft2;

        if (dyCurrent > cdyCenter) {
            // top side
            double cdy1 = Math.abs(cdyLeft - dyCurrent);
            cdy = cdy1 / cdy2;
        } else {
            // bottom side
            double cdy1 = Math.abs(cdyRight - cdyCenter);
            cdy = cdy1 / cdy2 - 1;
        }

        double ox = cdx;
        double oy = cdy;

        //SdkLog.d(TAG, "offsetX: " + ox + ", offsetY: " + oy);

        Point result = new Point(ox, oy);
        return result;
    }

    private CompositeDisposable getSubscriptions() {
        if (mSubscriptions == null) {
            mSubscriptions = new CompositeDisposable();
        }

        return mSubscriptions;
    }

    private boolean isCalibrateSuccess() {
        boolean result = true;

        if (mCalibrateFaceRectList != null && mCalibrateFaceRectList.size() > 1) {
            int maxOffsetX = 0;
            int maxOffsetY = 0;


            for (Rect item : mCalibrateFaceRectList) {
                for (Rect itemSub : mCalibrateFaceRectList) {
                    if (Math.abs(item.x - itemSub.x) > maxOffsetX) {
                        maxOffsetX = Math.abs(item.x - itemSub.x);
                    }

                    if (Math.abs(item.y - itemSub.y) > maxOffsetY) {
                        maxOffsetY = Math.abs(item.y - itemSub.y);
                    }
                }
            }

            if (maxOffsetX > Constants.CALIBRATE_FAIL_OFFSET_X || maxOffsetY > Constants.CALIBRATE_FAIL_OFFSET_Y) {
                result = false;
            }
        }

        return result;
    }

    private void startLightSensor() {
        if (mLightSensorManager == null) {
            mLightSensorManager = new LightSensorManager(mContext);
            mLightSensorManager.setEnvironmentChangedListener(new LightSensorManager.EnvironmentChangedListener() {
                @Override
                public void onLightOk() {
                    synchronized (mNewStartSdkState) {
                        mNewStartSdkState.setLightEnvironmentOk(true);
                    }
                }

                @Override
                public void onLightBad() {
                    synchronized (mNewStartSdkState) {
                        mNewStartSdkState.setLightEnvironmentOk(false);
                    }
                }
            });
        }

        mLightSensorManager.enable();
    }

    private void stopLightSensor() {
        if (mLightSensorManager != null) {
            mLightSensorManager.disable();
            mLightSensorManager = null;
        }
    }

    private void startDeviceSensor() {
        if (mDevicePositionSensorManager == null) {
            mDevicePositionSensorManager = new DevicePositionSensorManager(mContext);
            mDevicePositionSensorManager.setEnvironmentChangedListener(new DevicePositionSensorManager.EnvironmentChangedListener() {
                @Override
                public void onPositionChanged(boolean ok) {
                    synchronized (mNewStartSdkState) {
                        mNewStartSdkState.setDevicePositionOk(ok);
                    }
                }

                @Override
                public void onShakeChanged(boolean ok) {
                    synchronized (mNewStartSdkState) {
                        mNewStartSdkState.setDeviceShakeOk(ok);
                    }
                }
            });
        }

        mDevicePositionSensorManager.enable();
    }

    private void stopDeviceSensor() {
        if (mDevicePositionSensorManager != null) {
            mDevicePositionSensorManager.disable();
            mDevicePositionSensorManager = null;
        }
    }
}
