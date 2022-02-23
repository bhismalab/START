package com.reading.start.tests.test_wheel.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
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
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.reading.start.sdk.general.SdkLog;
import com.reading.start.sdk.ui.view.AutoFitTextureView;
import com.reading.start.tests.test_wheel.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public abstract class BaseCameraFragment extends BaseFragment {
    private static final String TAG = BaseCameraFragment.class.getSimpleName();

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

    private String mNextVideoAbsolutePath;

    private CaptureRequest.Builder mPreviewBuilder;

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        mTextureView = view.findViewById(R.id.texture);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    protected void startCameraPreview() {
        startBackgroundThread();

        if (mTextureView != null) {
            if (mTextureView.isAvailable()) {
                openCamera(mTextureView.getWidth(), mTextureView.getHeight());
            } else {
                mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
            }
        }
    }

    protected void stopCameraPreview() {
        closeCamera();
        stopBackgroundThread();
    }

    protected abstract int getCameraIndex();

    protected abstract Size getVideoSize();

    protected abstract void onVideoStartRecoding();

    protected abstract void onVideoStopRecoding();

    protected void startVideoRecoding(String file) {
        mNextVideoAbsolutePath = file;
        startRecordingVideo();
    }

    protected void stopVideoRecoding() {
        stopRecordingVideo();
        mNextVideoAbsolutePath = null;
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
        if (mBackgroundThread == null) {
            mBackgroundThread = new HandlerThread("CameraBackground");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    private void stopBackgroundThread() {
        if (mBackgroundThread != null) {
            mBackgroundThread.quitSafely();

            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                SdkLog.e(TAG, e);
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void openCamera(int width, int height) {
        final Activity activity = getActivity();

        if (null == activity || activity.isFinishing() || mCameraDevice != null) {
            startPreview();
            return;
        }

        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        try {
            Log.d(TAG, "tryAcquire");

            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }

            String cameraId = String.valueOf(getCameraIndex());

            // Choose the sizes for camera preview and video recording
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

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
            manager.openCamera(cameraId, mStateCallback, null);
        } catch (CameraAccessException e) {
            //Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            //activity.finish();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            SdkLog.e(TAG, e);
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
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            SdkLog.e(TAG, e);
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
            SdkLog.e(TAG, e);
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
            float scale = Math.min(
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

            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            mMediaRecorder.setOutputFile(mNextVideoAbsolutePath);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setVideoSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.prepare();
        } catch (Exception e) {
            SdkLog.e(TAG, e);
        }
    }

    private void startRecordingVideo() {
        if (null == mCameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            return;
        }

        try {
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
                            mIsRecordingVideo = true;

                            // Start recording
                            mMediaRecorder.start();
                            onVideoStartRecoding();
                        });
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    }
                }, mBackgroundHandler);
            }
        } catch (Exception e) {
            SdkLog.e(TAG, e);
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
            } catch (CameraAccessException e) {
                SdkLog.e(TAG, e);
            }

            try {
                // Stop recording
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            } catch (Exception e) {
                SdkLog.e(TAG, e);
            }

            onVideoStopRecoding();
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
