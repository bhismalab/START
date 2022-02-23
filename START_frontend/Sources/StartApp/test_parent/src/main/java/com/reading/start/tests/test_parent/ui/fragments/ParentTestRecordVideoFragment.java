package com.reading.start.tests.test_parent.ui.fragments;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.reading.start.tests.TestLog;
import com.reading.start.tests.test_parent.BuildConfig;
import com.reading.start.tests.test_parent.Constants;
import com.reading.start.tests.test_parent.R;
import com.reading.start.tests.test_parent.databinding.TestParentRecordVideoFragmentBinding;
import com.reading.start.tests.test_parent.domain.entity.TestData;
import com.reading.start.tests.test_parent.ui.activities.MainActivity;
import com.reading.start.tests.test_parent.ui.views.AutoFitTextureView;
import com.reading.start.tests.test_parent.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ParentTestRecordVideoFragment extends BaseFragment {
    private static final String TAG = ParentTestRecordVideoFragment.class.getSimpleName();

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

    private TestParentRecordVideoFragmentBinding mBinding;

    private boolean mInProgress = false;

    private Handler mHandler = new Handler();

    private TestData mTestData = null;

    private CameraDevice mCameraDevice;

    private CameraCaptureSession mPreviewSession;

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

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview();
            mCameraOpenCloseLock.release();

            if (null != mTextureView) {
                configureTransform(mTextureView.getWidth(), mTextureView.getHeight());
            }
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

    private TestTimer mTimeTimer = null;

    public ParentTestRecordVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_parent_record_video_fragment, container, false);
        mBinding.buttonStart.setVisibility(View.VISIBLE);
        mBinding.buttonStop.setVisibility(View.GONE);

        mBinding.buttonStart.setOnClickListener(v -> {
            if (!mIsRecordingVideo) {
                startRecordingVideo();
                mBinding.buttonStart.setVisibility(View.GONE);
                mBinding.buttonStop.setVisibility(View.VISIBLE);


                mTimeTimer = new TestTimer(() -> {
                    if (mTimeTimer.needProcess) {
                        mHandler.postDelayed(mTimeTimer, 1000);
                        mCurrentTime += 1000;
                        updateTime(mCurrentTime);
                    }
                });

                mCurrentTime = 0;
                mHandler.postDelayed(mTimeTimer, 1000);
                updateTime(mCurrentTime);
            } else {
            }
        });

        mBinding.buttonStop.setOnClickListener(v -> {
            if (mIsRecordingVideo) {
                stopRecordingVideo();
                mTestData.setFilePath(mNextVideoAbsolutePath);

                if (mTimeTimer != null) {
                    mTimeTimer.needProcess = false;
                }

                if (getMainActivity() != null) {
                    getMainActivity().onBackPressedSuper();
                }
            }
        });

        mBinding.buttonBack.setOnClickListener(v -> {
            final MainActivity activity = getMainActivity();

            if (activity != null) {
                stopRecordingVideo();
                activity.onBackPressedSuper();
            }
        });

        if (getMainActivity() != null) {
            mTestData = getMainActivity().getTestData();
        } else {
            Size screenSize = Utility.getDisplaySize(getActivity());
            float dpi[] = Utility.getXYDpi(getActivity());
            mTestData = new TestData(screenSize.getWidth(), screenSize.getHeight(), dpi[0], dpi[1]);
        }

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
    }

    @Override
    public void onPause() {
        if (mIsRecordingVideo) {
            stopRecordingVideo();
            mTestData.setFilePath(mNextVideoAbsolutePath);

            if (mTimeTimer != null) {
                mTimeTimer.needProcess = false;
            }

            if (getMainActivity() != null) {
                getMainActivity().onBackPressedSuper();
            }
        }

        closeCamera();
        stopBackgroundThread();

        super.onPause();
    }

    private void updateTime(long time) {
        int seconds = (int) (time / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        String timeValue = String.format("%d:%02d", minutes, seconds);
        mBinding.time.setText(timeValue);
    }

    private static class TestTimer implements Runnable {
        public boolean needProcess = true;

        private Runnable mLocalRun = null;

        public TestTimer(Runnable runnable) {
            mLocalRun = runnable;
        }

        @Override
        public void run() {
            if (needProcess && mLocalRun != null) {
                mLocalRun.run();
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

            String cameraId = manager.getCameraIdList()[BuildConfig.CAMERA_INDEX];

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

    private void startRecordingVideo() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }

        try {
            mNextVideoAbsolutePath = null;
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
                            // UI
//                        mBinding.buttonSave.setText(R.string.test_parent_button_save);
                            mIsRecordingVideo = true;

                            // Start recording
                            mMediaRecorder.start();
                        });
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
//                    Activity activity = getActivity();
//                    if (null != activity) {
//                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
//                    }
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
