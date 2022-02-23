package com.reading.start.sdk.ui.view;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;

import org.opencv.android.JavaCameraView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * View uses for displaying video stream from camera.
 */
public class CameraDetectView extends JavaCameraView implements PictureCallback {
    private static final String TAG = CameraDetectView.class.getSimpleName();

    public static final int CV_CAP_ANDROID = 1000;

    private String mPictureFileName;

    public CameraDetectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Parameters getCameraParameters() {
        return mCamera.getParameters();
    }

    public void setCameraParameters(Parameters CamParameters) {
        mCamera.setParameters(CamParameters);
    }

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public List<Size> getSelectedResolutionList() {
        List<Size> selectedSupportedResolutions = mCamera.getParameters().getSupportedPreviewSizes();
        int i = 0;

        while (i < selectedSupportedResolutions.size()) {
            if (((double) selectedSupportedResolutions.get(i).width) / 10.0d != ((double) (selectedSupportedResolutions.get(i).width / 10)) || ((double) selectedSupportedResolutions.get(i).height) / 10.0d != ((double) (selectedSupportedResolutions.get(i).height / 10))) {
                selectedSupportedResolutions.remove(i);
            }
            i++;
        }

        return selectedSupportedResolutions;
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        this.mMaxHeight = resolution.height;
        this.mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }

    public void setInitialResolution() {
        List<Size> selectedSupportedResolutions = mCamera.getParameters().getSupportedPreviewSizes();
        int i = 0;

        while (i < selectedSupportedResolutions.size()) {
            if (((double) selectedSupportedResolutions.get(i).width) / 10.0d != ((double) (selectedSupportedResolutions.get(i).width / 10)) || ((double) selectedSupportedResolutions.get(i).height) / 10.0d != ((double) (selectedSupportedResolutions.get(i).height / 10))) {
                selectedSupportedResolutions.remove(i);
            }
            i++;
        }

        int selectedResolutionIndex = 0;
        int selectedResolutionDist = CV_CAP_ANDROID;

        for (i = 0; i < selectedSupportedResolutions.size(); i++) {
            if (selectedResolutionDist > Math.abs(selectedSupportedResolutions.get(i).width - 640)) {
                selectedResolutionDist = Math.abs(selectedSupportedResolutions.get(i).width - 640);
                selectedResolutionIndex = i;
            }
        }

        Size selectedResolution = selectedSupportedResolutions.get(selectedResolutionIndex);
        Log.i("Set Resolution", "1");
        disconnectCamera();
        Log.i("Set Resolution", "2");
        mMaxHeight = selectedResolution.height;
        mMaxWidth = selectedResolution.width;
        Log.i("Set Resolution", "3");
        connectCamera(getWidth(), getHeight());
        Log.i("Set Resolution", "4");
    }

    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(String fileName) {
        Log.i(TAG, "Taking picture");
        mPictureFileName = fileName;
        mCamera.setPreviewCallback(null);
        mCamera.takePicture(null, null, this);
    }

    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        try {
            FileOutputStream fos = new FileOutputStream(this.mPictureFileName);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
    }
}