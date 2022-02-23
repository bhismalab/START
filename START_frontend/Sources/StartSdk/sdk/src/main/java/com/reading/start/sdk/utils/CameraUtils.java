package com.reading.start.sdk.utils;

import android.content.Context;
import android.hardware.camera2.CameraManager;

import com.reading.start.sdk.AppCore;
import com.reading.start.sdk.general.SdkLog;

public class CameraUtils {
    private static final String TAG = CameraUtils.class.getSimpleName();

    private static Integer[] mIndexes = null;

    public static Integer[] getCameraIndexes() {
        if (mIndexes == null) {
            Integer[] arr = new Integer[]{0};

            try {
                CameraManager manager = (CameraManager) AppCore.getInstance().getSystemService(Context.CAMERA_SERVICE);

                if (manager.getCameraIdList().length > 0) {
                    arr = new Integer[manager.getCameraIdList().length];

                    if (manager != null) {
                        int i = 0;

                        for (String cameraId : manager.getCameraIdList()) {
                            try {
                                arr[i] = Integer.parseInt(cameraId);
                                i++;
                            } catch (Exception e) {
                                arr[i] = 0;
                                SdkLog.e(TAG, e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                SdkLog.e(TAG, e);
            }

            mIndexes = arr;
            return mIndexes;
        } else {
            return mIndexes;
        }
    }

    public static int getCameraIndex() {
        return getCameraIndexes()[0];
    }

    public static int getIndexCameraIndex(int value) {
        int index = 0;

        for (int i = 0; i < getCameraIndexes().length; i++) {
            if (value == getCameraIndexes()[i]) {
                index = i;
                break;
            }
        }

        return index;
    }
}
