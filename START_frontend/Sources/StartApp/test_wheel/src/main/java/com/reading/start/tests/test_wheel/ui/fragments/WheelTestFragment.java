package com.reading.start.tests.test_wheel.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.reading.start.tests.TestLog;
import com.reading.start.tests.TestsProvider;
import com.reading.start.tests.test_wheel.BuildConfig;
import com.reading.start.tests.test_wheel.Constants;
import com.reading.start.tests.test_wheel.R;
import com.reading.start.tests.test_wheel.WheelTest;
import com.reading.start.tests.test_wheel.data.DataProvider;
import com.reading.start.tests.test_wheel.databinding.TestWheelFragmentTestBinding;
import com.reading.start.tests.test_wheel.domain.entity.TestData;
import com.reading.start.tests.test_wheel.domain.entity.TestDataItem;
import com.reading.start.tests.test_wheel.domain.entity.TestDataStimulus;
import com.reading.start.tests.test_wheel.domain.entity.WheelTestContent;
import com.reading.start.tests.test_wheel.domain.entity.WheelTestSurveyResult;
import com.reading.start.tests.test_wheel.ui.activities.MainActivity;
import com.reading.start.tests.test_wheel.ui.dialogs.DialogCompleteTest;
import com.reading.start.tests.test_wheel.ui.views.AutoFitTextureView;
import com.reading.start.tests.test_wheel.ui.views.CompleteTriggerView;
import com.reading.start.tests.test_wheel.utils.PositionManager;
import com.reading.start.tests.test_wheel.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

public class WheelTestFragment extends BaseFragment {
    private static final String TAG = WheelTestFragment.class.getSimpleName();

    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }

    private AutoFitTextureView mTextureView;

    private TestWheelFragmentTestBinding mBinding;

    private boolean mInProgress = false;

    private boolean mSaving = false;

    private boolean mInPlaying = false;

    private TestData mTestData = null;

    private PositionManager mPositionManager;

    private int mCurrentAttempt = 0;

    private boolean mIsButtonTouch = false;

    private boolean mIsToastActive = false;

    private Handler mHandler = null;

    private Uri mVideoFile = null;

    private CancelRunnable mCancelRunnable = null;

    private Object mSync = new Object();

    private boolean mIsBlackScreen = false;

    private CameraDevice mCameraDevice;

    private CameraCaptureSession mPreviewSession;

    private boolean mIsFinished = false;

    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };

    private Size mPreviewSize;

    private Size mVideoSize;

    private MediaRecorder mMediaRecorder;

    private boolean mIsRecordingVideo;

    private HandlerThread mBackgroundThread;

    private Handler mBackgroundHandler;

    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    private float mCurrentX = 0;

    private float mCurrentY = 0;

    private float mCurrentTouchPressure = 0;

    private float mCurrentTouchSize = 0;

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview();
            mCameraOpenCloseLock.release();

            if (null != mTextureView) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
            }

            mHandler.postDelayed(() -> startRecordingVideo(), 2000);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }
    };

    private Integer mSensorOrientation;

    private String mNextVideoAbsolutePath;

    private CaptureRequest.Builder mPreviewBuilder;

    private long mCurrentTime = 0;

    public WheelTestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_wheel_fragment_test, container, false);
        mHandler = new Handler();
        Size screenSize = Utility.getDisplaySize(getActivity());
        float dpi[] = Utility.getXYDpi(getActivity());
        mTestData = new TestData(screenSize.getWidth(), screenSize.getHeight(), dpi[0], dpi[1]);

        mBinding.actionButton.setOnClickListener(v -> {
            if (mBinding.videoView.getVisibility() == View.VISIBLE) {
                synchronized (mSync) {
                    long time = Calendar.getInstance().getTimeInMillis();
                    TestDataItem item = new TestDataItem(mCurrentX, mCurrentY, mCurrentTouchPressure, mCurrentTouchSize, time,
                            Constants.ACTION_NAME_PRESS_BUTTON, mPositionManager.getX(), mPositionManager.getY(), mPositionManager.getZ());
                    mTestData.getItems().add(item);

                    if (mCancelRunnable != null) {
                        mCancelRunnable.isCancel = true;
                        mCancelRunnable = null;
                    }

                    showBlackScreen();
                }
            }
        });

        mBinding.actionButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mIsButtonTouch = true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mIsButtonTouch = false;
            }

            mCurrentX = v.getX() + event.getX();
            mCurrentY = v.getY() + event.getY();
            mCurrentTouchPressure = event.getPressure();
            mCurrentTouchSize = event.getSize();

            return false;
        });

        mBinding.completeTriggerView.setListener(new CompleteTriggerView.CompleteTriggerViewListener() {
            @Override
            public void onCompleteTrigger() {
                mTestData.setInterrupted(true);
                onTestCompleted();
            }

            @Override
            public void onTouchChanged(float x, float y, float touchPressure, float touchSize, int touchCount) {
                if (!mIsButtonTouch && touchCount == 1) {
                    long time = Calendar.getInstance().getTimeInMillis();

                    if (mBinding.videoView.getVisibility() == View.VISIBLE) {
                        TestDataItem item = new TestDataItem(x, y, touchPressure, touchSize, time, Constants.ACTION_NAME_PRESS_VIDEO, mPositionManager.getX(), mPositionManager.getY(), mPositionManager.getZ());
                        mTestData.getItems().add(item);
                    } else {
                        TestDataItem item = new TestDataItem(x, y, touchPressure, touchSize, time, Constants.ACTION_NAME_PRESS_BLACK_SCREEN, mPositionManager.getX(), mPositionManager.getY(), mPositionManager.getZ());
                        mTestData.getItems().add(item);
                    }
                }
            }

            @Override
            public boolean onCountFingerChanged(int count) {
                if (count >= 2) {
                    if (!mIsToastActive) {
                        mIsToastActive = true;
                        Toast.makeText(getActivity(), getString(R.string.test_wheel_fingers_error), Toast.LENGTH_SHORT).show();
                        mHandler.postDelayed(() -> mIsToastActive = false, 2000);
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });

        mPositionManager = new PositionManager(getActivity());
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mTextureView = view.findViewById(R.id.texture);
    }

    @Override
    public void onResume() {
        super.onResume();

        startBackgroundThread();

        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }

        startTest();
    }

    @Override
    public void onPause() {
        if (mInProgress) {
            mTestData.setInterrupted(true);
            onTestCompleted();
        }

        closeCamera();
        stopBackgroundThread();

        stopTest();
        super.onPause();
    }

    private boolean checkIfFinish() {
        boolean result = false;

        if (mCurrentAttempt >= Constants.COUNT_ATTEMPT) {
            result = true;
        }

        return result;
    }

    private synchronized void onTestCompleted() {
        if (mInProgress) {
            mIsFinished = true;
            mBinding.videoView.stopPlayback();
            stopTest();
            showEndTestDialog();
        }
    }

    private void onPlayVideo(Uri video) {
        if (video != null) {
            final VideoView videoView = mBinding.videoView;

            if (!videoView.isPlaying()) {
                mInPlaying = true;
                videoView.setVideoURI(video);
                videoView.setOnPreparedListener(mp -> {
                    if (mp != null) {
                        mp.setLooping(true);
                    }
                });
                videoView.setOnCompletionListener(mp -> {
                });
                videoView.start();
            }
        }
    }

    private void moveToNextPosition() {
        mBinding.videoView.setVisibility(View.VISIBLE);
        mBinding.actionButton.setVisibility(View.VISIBLE);
        onPlayVideo(mVideoFile);
        TestDataStimulus data = new TestDataStimulus(Constants.ACTION_NAME_VIDEO_START, Calendar.getInstance().getTimeInMillis());
        mTestData.getStimulusItems().add(data);

        mCancelRunnable = new CancelRunnable(() -> {
            synchronized (mSync) {
                if (mCancelRunnable != null) {
                    mCancelRunnable.isCancel = true;
                    mCancelRunnable = null;
                    showBlackScreen();
                }
            }
        });

        mHandler.postDelayed(mCancelRunnable, Constants.VIDEO_TIMEOUT);
    }

    private void showBlackScreen() {
        mIsBlackScreen = true;
        mCurrentAttempt++;
        mBinding.videoView.stopPlayback();
        mInPlaying = false;
        mBinding.videoView.setVisibility(View.INVISIBLE);
        mBinding.actionButton.setVisibility(View.INVISIBLE);
        TestDataStimulus data = new TestDataStimulus(Constants.ACTION_NAME_VIDEO_STOP, Calendar.getInstance().getTimeInMillis());
        mTestData.getStimulusItems().add(data);
        mHandler.postDelayed(() -> {
            mIsBlackScreen = false;

            if (checkIfFinish()) {
                onTestCompleted();
            } else {
                moveToNextPosition();
            }
        }, Constants.BLACK_SCREEN_DELAY);
    }

    private void fillVideoFiles() {
        Realm realm = null;

        try {
            realm = DataProvider.getInstance(getActivity()).getRealm();
            WheelTestContent content = realm.where(WheelTestContent.class).findFirst();
            mVideoFile = Uri.fromFile(new File(content.getVideo()));
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private void startTest() {
        if (!mInProgress) {
            mCurrentAttempt = 0;
            mInProgress = true;
            mPositionManager.enable();
            fillVideoFiles();

            if (!mIsFinished) {
                moveToNextPosition();
            }
        }
    }

    private void stopTest() {
        if (mInProgress) {
            stopRecordingVideo();
            mInProgress = false;
            mPositionManager.disable();
        }
    }

    private void showEndTestDialog() {
        MainActivity activity = getMainActivity();

        if (activity != null) {
            int attempt = saveTestResult();
            boolean showNewAttempt = attempt < com.reading.start.tests.Constants.ATTEMPT_COUNT;
            boolean showNextStep = !TestsProvider.getInstance(getActivity()).isLastTest(WheelTest.getInstance());

            String title = mTestData.isInterrupted() ? getResources().getString(R.string.test_wheel_dialog_interrupted_title)
                    : getResources().getString(R.string.test_wheel_dialog_success_title);
            String message = mTestData.isInterrupted() ? getResources().getString(R.string.test_wheel_dialog_interrupted_message)
                    : getResources().getString(R.string.test_wheel_dialog_success_message);

            DialogCompleteTest dialog = DialogCompleteTest.getInstance(title, message, showNewAttempt, showNextStep,
                    new DialogCompleteTest.DialogListener() {
                        @Override
                        public void onBack() {
                            activity.finishAsOpenChildTest();
                        }

                        @Override
                        public void onNext() {
                            activity.finishAsStartNext();
                        }

                        @Override
                        public void onAddNew() {
                            activity.finishAsTryAgain();
                        }
                    });

            dialog.setCancelable(false);
            dialog.show(getFragmentManager(), TAG);
        }
    }

    private int saveTestResult() {
        int attempt = 0;
        Realm realm = null;

        try {
            if (!mSaving) {
                mSaving = true;
                final MainActivity activity = getMainActivity();
                realm = DataProvider.getInstance(getActivity()).getRealm();

                if (activity != null) {
                    if (realm != null && !realm.isClosed()) {
                        mTestData.setEndTime(Calendar.getInstance().getTimeInMillis());
                        mTestData.setFilePath(mNextVideoAbsolutePath);
                        mTestData.setDeviceId(Utility.getDeviceId(activity));

                        WheelTestSurveyResult result = new WheelTestSurveyResult();
                        result.setSurveyId(activity.getSurveyId());
                        Gson gson = new Gson();
                        String testValue = gson.toJson(mTestData);
                        result.setResultFiles(testValue);
                        result.setTestRefId(WheelTest.TYPE);
                        result.setStartTime(mTestData.getStartTime());
                        result.setEndTime(mTestData.getEndTime());

                        try {
                            realm.beginTransaction();
                            Number currentId = realm.where(WheelTestSurveyResult.class).max(WheelTestSurveyResult.FILED_ID);
                            int nextId;

                            if (currentId == null) {
                                nextId = 0;
                            } else {
                                nextId = currentId.intValue() + 1;
                            }

                            result.setId(nextId);
                            realm.copyToRealmOrUpdate(result);
                            realm.commitTransaction();
                            attempt = (int) realm.where(WheelTestSurveyResult.class)
                                    .equalTo(WheelTestSurveyResult.FILED_SURVEY_ID, activity.getSurveyId()).count();
                        } catch (Exception e) {
                            TestLog.e(TAG, e);
                            realm.cancelTransaction();
                        }
                    }
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

        return attempt;
    }

    private class CancelRunnable implements Runnable {
        boolean isCancel = false;

        private Runnable mRunnable;

        CancelRunnable(Runnable runnable) {
            mRunnable = runnable;
        }

        @Override
        public void run() {
            if (!isCancel) {
                mRunnable.run();
            }
        }
    }

    protected Size getVideoSize() {
        return new Size(BuildConfig.CAMERA_WIDTH, BuildConfig.CAMERA_HEIGHT);
    }

    // -------------------------------------------------

    /**
     * In this sample, we choose a video size with 3x4 aspect ratio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        Log.e(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private static Size chooseOptimalSize(Size[] choices, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        Arrays.sort(choices, new CompareSizesByAreaHS());
        int height = aspectRatio.getHeight();

        Size selectedSize = choices[0];
        int offset = Integer.MAX_VALUE;

        for (Size option : choices) {
            if (Math.abs(option.getHeight() - height) < offset) {
                selectedSize = option;
                offset = Math.abs(option.getHeight() - height);
            }
        }

        return selectedSize;
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();

        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            TestLog.e(TAG, e);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void openCamera(int width, int height) {
        final Activity activity = getActivity();

        if (null == activity || activity.isFinishing()) {
            return;
        }

        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        try {
            Log.d(TAG, "tryAcquire");

            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            String cameraId = "0";

            try {
                if (BuildConfig.CAMERA_INDEX >= 0) {
                    cameraId = manager.getCameraIdList()[BuildConfig.CAMERA_INDEX];
                }
            } catch (Exception e) {
                TestLog.e(TAG, e);
            }

            // Choose the sizes for camera preview and video recording
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

            if (map == null) {
                throw new RuntimeException("Cannot get available preview/video sizes");
            }

            mVideoSize = getVideoSize();
            mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), mVideoSize);
            int orientation = getResources().getConfiguration().orientation;

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }

            configureTransform(width, height);
            mMediaRecorder = new MediaRecorder();
            manager.openCamera(cameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            //Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            //activity.finish();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            TestLog.e(TAG, e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.");
        }
    }

    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mMediaRecorder) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.");
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Start the camera preview.
     */
    private void startPreview() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }

        try {
            closePreviewSession();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            Surface previewSurface = new Surface(texture);
            mPreviewBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Collections.singletonList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mPreviewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
//                    Activity activity = getActivity();
//                    if (null != activity) {
//                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
//                    }
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            TestLog.e(TAG, e);
        }
    }

    private void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }

        try {
            setUpCaptureRequestBuilder(mPreviewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            TestLog.e(TAG, e);
        }
    }

    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();

        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }

        mTextureView.setTransform(matrix);
    }

    private void setUpMediaRecorder() {
        try {
            final Activity activity = getActivity();

            if (null == activity) {
                return;
            }

            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            if (mNextVideoAbsolutePath == null || mNextVideoAbsolutePath.isEmpty()) {
                mNextVideoAbsolutePath = getVideoFilePath(getActivity());
            }

            mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
            mMediaRecorder.setVideoEncodingBitRate(10000000);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setVideoSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.prepare();
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }
    }

    private String getVideoFilePath(Context context) {
        String result = null;
        final MainActivity activity = getMainActivity();

        if (activity != null && context != null) {
            String fileName = String.valueOf(activity.getSurveyId());
            fileName += "_" + String.valueOf(Calendar.getInstance().getTimeInMillis());
            fileName += "." + Constants.VIDEO_FILE_EXT;
            result = new File(context.getFilesDir(), fileName).toString();
        }

        return result;
    }

    private synchronized void startRecordingVideo() {
        if (mIsRecordingVideo && null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }

        try {
            mIsRecordingVideo = true;
            closePreviewSession();
            setUpMediaRecorder();
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            Surface previewSurface = new Surface(texture);
            surfaces.add(previewSurface);
            mPreviewBuilder.addTarget(previewSurface);

            // Set up Surface for the MediaRecorder
            Surface recorderSurface = mMediaRecorder.getSurface();

            if (recorderSurface != null) {
                surfaces.add(recorderSurface);
                mPreviewBuilder.addTarget(recorderSurface);

                // Start a capture session
                // Once the session starts, we can update the UI and start recording
                mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        mPreviewSession = cameraCaptureSession;
                        updatePreview();
                        getActivity().runOnUiThread(() -> {
                            mMediaRecorder.start();
                        });
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                        mIsRecordingVideo = false;
                    }
                }, mBackgroundHandler);
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }
    }

    private void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
    }

    private void stopRecordingVideo() {
        if (mMediaRecorder != null && mIsRecordingVideo) {
            mIsRecordingVideo = false;

            try {
                mPreviewSession.stopRepeating();
                mPreviewSession.abortCaptures();
                mPreviewSession.close();
            } catch (CameraAccessException e) {
                TestLog.e(TAG, e);
            } finally {
                mPreviewSession = null;
            }

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                TestLog.e(TAG, e);
            }

            try {
                // Stop recording
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            } catch (Exception e) {
                TestLog.e(TAG, e);
            }
        }
    }

    private static class CompareSizesByAreaSH implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
        }
    }

    private static class CompareSizesByAreaHS implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) rhs.getWidth() * rhs.getHeight() - (long) lhs.getWidth() * lhs.getHeight());
        }
    }
}
