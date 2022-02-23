package com.reading.start.sdk.ui.fragments;

import android.databinding.DataBindingUtil;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;

import com.reading.start.sdk.AppCore;
import com.reading.start.sdk.R;
import com.reading.start.sdk.core.CalibrateMode;
import com.reading.start.sdk.core.DetectMode;
import com.reading.start.sdk.core.DetectType;
import com.reading.start.sdk.core.Settings;
import com.reading.start.sdk.databinding.SdkFragmentSettingsBinding;
import com.reading.start.sdk.general.SdkLog;
import com.reading.start.sdk.ui.adapters.CameraSizeArrayAdapter;
import com.reading.start.sdk.utils.CameraUtils;
import com.reading.start.sdk.utils.SettingsHelper;

import org.opencv.android.JavaCameraView;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends BaseFragment {
    private static final String TAG = SettingsFragment.class.getSimpleName();

    private SdkFragmentSettingsBinding mBinding;

    private Settings mSettings;

    private List<Camera.Size> mResolutionList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.sdk_fragment_settings, container, false);
        setHasOptionsMenu(false);
        mSettings = getMainActivity().getCameraSettings();

        // init camera index
        Integer[] cameraIndex = CameraUtils.getCameraIndexes();
        final ArrayAdapter<Integer> cameraIndexAdapter = new ArrayAdapter<>(AppCore.getInstance(),
                R.layout.sdk_item_spinner, cameraIndex);
        cameraIndexAdapter.setDropDownViewResource(R.layout.sdk_item_spinner_dropdown);
        mBinding.cameraIndex.setAdapter(cameraIndexAdapter);
        mBinding.cameraIndex.setSelection(CameraUtils.getIndexCameraIndex(mSettings.getGeneral().getCameraIndex()));
        mBinding.cameraIndex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Integer value = cameraIndexAdapter.getItem(position);
                    mSettings.getGeneral().setCameraIndex(value);

                    mResolutionList = JavaCameraView.getSupportedCameraResolutions(CameraUtils.getIndexCameraIndex(mSettings.getGeneral().getCameraIndex()));
                    final CameraSizeArrayAdapter cameraResolutionAdapter = new CameraSizeArrayAdapter(AppCore.getInstance(),
                            R.layout.sdk_item_spinner, mResolutionList);
                    mBinding.cameraResolution.setAdapter(cameraResolutionAdapter);
                    mBinding.cameraResolution.setSelection(getSelectedResolution());
                } catch (Exception e) {
                    SdkLog.e(TAG, e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // init camera resolution
        mResolutionList = JavaCameraView.getSupportedCameraResolutions(CameraUtils.getIndexCameraIndex(mSettings.getGeneral().getCameraIndex()));
        final CameraSizeArrayAdapter cameraResolutionAdapter = new CameraSizeArrayAdapter(AppCore.getInstance(),
                R.layout.sdk_item_spinner, mResolutionList);
        mBinding.cameraResolution.setAdapter(cameraResolutionAdapter);
        mBinding.cameraResolution.setSelection(getSelectedResolution());
        mBinding.cameraResolution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Camera.Size value = mResolutionList.get(position);
                    mSettings.getGeneral().setCameraSize(new Size(value.width, value.height));
                } catch (Exception e) {
                    SdkLog.e(TAG, e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // init detect type
        final ArrayList<DetectType> detectTypes = new ArrayList<>();
        detectTypes.add(DetectType.RealTime);
        detectTypes.add(DetectType.PostProcessing);
        final ArrayAdapter<DetectType> detectTypesAdapter = new ArrayAdapter<>(AppCore.getInstance(),
                R.layout.sdk_item_spinner, detectTypes);
        mBinding.detectTypeIndex.setAdapter(detectTypesAdapter);

        if (mSettings.getGeneral().getDetectType() == null) {
            mSettings.getGeneral().setDetectType(DetectType.RealTime);
        }

        mBinding.detectTypeIndex.setSelection(mSettings.getGeneral().getDetectType().ordinal());
        mBinding.detectTypeIndex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mSettings.getGeneral().setDetectType(DetectType.values()[position]);
                } catch (Exception e) {
                    SdkLog.e(TAG, e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // init calibration mode
        final ArrayList<CalibrateMode> calibrationModes = new ArrayList<>();
        calibrationModes.add(CalibrateMode.Point1);
        calibrationModes.add(CalibrateMode.Point5);
        final ArrayAdapter<CalibrateMode> calibrationModesAdapter = new ArrayAdapter<>(AppCore.getInstance(),
                R.layout.sdk_item_spinner, calibrationModes);
        mBinding.calibrationModeIndex.setAdapter(calibrationModesAdapter);
        mBinding.calibrationModeIndex.setSelection(mSettings.getGeneral().getCalibrateMode().ordinal());
        mBinding.calibrationModeIndex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mSettings.getGeneral().setCalibrateMode(CalibrateMode.values()[position]);
                } catch (Exception e) {
                    SdkLog.e(TAG, e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // init multi thread
        mBinding.multiThread.setChecked(mSettings.getGeneral().isMultiThread());
        mBinding.multiThread.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.getGeneral().setIsMultiThread(isChecked));

        final ArrayList<Integer> multiThreadList = new ArrayList<>();
        multiThreadList.add(2);
        multiThreadList.add(3);
        multiThreadList.add(4);
        final ArrayAdapter<Integer> multiThreadAdapter = new ArrayAdapter<>(AppCore.getInstance(),
                R.layout.sdk_item_spinner, multiThreadList);
        mBinding.multiThreadCount.setAdapter(multiThreadAdapter);
        mBinding.multiThreadCount.setSelection(mSettings.getGeneral().getCountOfThread() - 2);
        mBinding.multiThreadCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mSettings.getGeneral().setCountOfThread(position + 2);
                } catch (Exception e) {
                    SdkLog.e(TAG, e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // init detect mode
        final ArrayList<DetectMode> detectModes = new ArrayList<>();
        detectModes.add(DetectMode.Mode2x1);
        detectModes.add(DetectMode.Mode3x1);
        detectModes.add(DetectMode.Mode2x2);
        detectModes.add(DetectMode.Mode3x2);
        detectModes.add(DetectMode.Mode4x2);
        final ArrayAdapter<DetectMode> detectModeAdapter = new ArrayAdapter<>(AppCore.getInstance(),
                R.layout.sdk_item_spinner, detectModes);
        mBinding.detectMode.setAdapter(detectModeAdapter);
        mBinding.detectMode.setSelection(mSettings.getGeneral().getDetectMode().ordinal());
        mBinding.detectMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    DetectMode mode = detectModes.get(position);
                    mSettings.getGeneral().setDetectMode(mode);

                    switch (mode) {
                        case Mode4x2: {
                            mBinding.calibrateCenterAreaLayout.setVisibility(View.GONE);
                            mBinding.calibrateLeftRightAreaLayout.setVisibility(View.VISIBLE);
                            break;
                        }
                        case Mode3x1:
                        case Mode3x2: {
                            mBinding.calibrateCenterAreaLayout.setVisibility(View.VISIBLE);
                            mBinding.calibrateLeftRightAreaLayout.setVisibility(View.GONE);
                            break;
                        }
                        default: {
                            mBinding.calibrateCenterAreaLayout.setVisibility(View.INVISIBLE);
                            mBinding.calibrateLeftRightAreaLayout.setVisibility(View.GONE);
                            break;
                        }
                    }
                } catch (Exception e) {
                    SdkLog.e(TAG, e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mBinding.detectSettings.setEnabled(mSettings.getProcessSettings().isTrackFace());
        mBinding.detect.setChecked(mSettings.getProcessSettings().isTrackFace());
        mBinding.detect.setText(mSettings.getProcessSettings().isTrackFace() ? "Detect ON" : "Detect OFF");
        mBinding.detect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mBinding.detect.setText(isChecked ? "Detect ON" : "Detect OFF");
            mSettings.getProcessSettings().setTrackFace(isChecked);
            mBinding.detectSettings.setEnabled(isChecked);
        });

        mBinding.detectRightEye.setChecked(mSettings.getProcessSettings().isTrackRightEye());
        mBinding.detectRightEye.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.getProcessSettings().setTrackRightEye(isChecked));

        mBinding.detectLeftEye.setChecked(mSettings.getProcessSettings().isTrackLeftEye());
        mBinding.detectLeftEye.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.getProcessSettings().setTrackLeftEye(isChecked));

        mBinding.detectRightPupil.setChecked(mSettings.getProcessSettings().isTrackRightPupil());
        mBinding.detectRightPupil.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.getProcessSettings().setTrackRightPupil(isChecked));

        mBinding.detectLeftPupil.setChecked(mSettings.getProcessSettings().isTrackLeftPupil());
        mBinding.detectLeftPupil.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.getProcessSettings().setTrackLeftPupil(isChecked));

        mBinding.writeVideo.setChecked(mSettings.getGeneral().isWriteVideo());
        mBinding.writeVideo.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.getGeneral().setWriteVideo(isChecked));

        mBinding.calibrateCenterArea.setText("Center area size(in %): " + String.valueOf(mSettings.getProcessSettings().getCenterAreaSize()));
        mBinding.calibrateCenterAreaSize.setProgress(mSettings.getProcessSettings().getCenterAreaSize());
        mBinding.calibrateCenterAreaSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSettings.getProcessSettings().setCenterAreaSize(progress);
                mBinding.calibrateCenterArea.setText("Center area size(in %): " + String.valueOf(mSettings.getProcessSettings().getCenterAreaSize()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mBinding.calibrateLeftArea.setText("Left area size(in %): " + String.valueOf(mSettings.getProcessSettings().getLeftAreaSize()));
        mBinding.calibrateLeftAreaSize.setProgress(mSettings.getProcessSettings().getLeftAreaSize());
        mBinding.calibrateLeftAreaSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSettings.getProcessSettings().setLeftAreaSize(progress);
                mBinding.calibrateLeftArea.setText("Left area size(in %): " + String.valueOf(mSettings.getProcessSettings().getLeftAreaSize()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSettings.getProcessSettings().setLeftAreaSize(seekBar.getProgress());
                mBinding.calibrateLeftArea.setText("Left area size(in %): " + String.valueOf(mSettings.getProcessSettings().getLeftAreaSize()));
            }
        });

        mBinding.calibrateRightArea.setText("Right area size(in %): " + String.valueOf(mSettings.getProcessSettings().getRightAreaSize()));
        mBinding.calibrateRightAreaSize.setProgress(mSettings.getProcessSettings().getRightAreaSize());
        mBinding.calibrateRightAreaSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSettings.getProcessSettings().setRightAreaSize(progress);
                mBinding.calibrateRightArea.setText("Right area size(in %): " + String.valueOf(mSettings.getProcessSettings().getRightAreaSize()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSettings.getProcessSettings().setRightAreaSize(seekBar.getProgress());
                mBinding.calibrateRightArea.setText("Right area size(in %): " + String.valueOf(mSettings.getProcessSettings().getRightAreaSize()));
            }
        });

        mBinding.pupilAreaSizeValue.setText("Area size: " + String.valueOf(mSettings.getProcessSettings().getPupilAreaSize()));
        mBinding.pupilAreaSize.setProgress(mSettings.getProcessSettings().getPupilAreaSize() - 20);
        mBinding.pupilAreaSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = progress + 20;
                mSettings.getProcessSettings().setPupilAreaSize(value);
                mBinding.pupilAreaSizeValue.setText("Area size: " + String.valueOf(mSettings.getProcessSettings().getPupilAreaSize()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int value = seekBar.getProgress() + 20;
                mSettings.getProcessSettings().setPupilAreaSize(value);
                mBinding.pupilAreaSizeValue.setText("Area size: " + String.valueOf(mSettings.getProcessSettings().getPupilAreaSize()));
            }
        });

        // calibrate threshold
        mBinding.calibrateXValue.setText("X threshold: " + String.valueOf(mSettings.getProcessSettings().getCalibrationX()));
        mBinding.calibrateX.setProgress((int) Math.round(mSettings.getProcessSettings().getCalibrationX() * 100));
        mBinding.calibrateX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double calibrationX = (double) progress / 100;
                mSettings.getProcessSettings().setCalibrationX(calibrationX);
                mBinding.calibrateXValue.setText("X threshold: " + String.valueOf(mSettings.getProcessSettings().getCalibrationX()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double calibrationX = (double) seekBar.getProgress() / 100;
                mSettings.getProcessSettings().setCalibrationX(calibrationX);
                mBinding.calibrateXValue.setText("X threshold: " + String.valueOf(mSettings.getProcessSettings().getCalibrationX()));
            }
        });

        mBinding.calibrateYValue.setText("Y threshold: " + String.valueOf(mSettings.getProcessSettings().getCalibrationY()));
        mBinding.calibrateY.setProgress((int) Math.round(mSettings.getProcessSettings().getCalibrationY() * 100));
        mBinding.calibrateY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double calibrationY = (double) progress / 100;
                mSettings.getProcessSettings().setCalibrationY(calibrationY);
                mBinding.calibrateYValue.setText("Y threshold: " + String.valueOf(calibrationY));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double calibrationY = (double) seekBar.getProgress() / 100;
                mSettings.getProcessSettings().setCalibrationY(calibrationY);
                mBinding.calibrateYValue.setText("Y threshold: " + String.valueOf(calibrationY));
            }
        });

        // calibrate vertical
        mBinding.calibrateVerticalValue.setText("Vertical calibration: " + String.valueOf(mSettings.getProcessSettings().getVerticalCalibrationOffset()));
        int progress = (int) ((mSettings.getProcessSettings().getVerticalCalibrationOffset() + 1) * 50);
        mBinding.calibrateVertical.setProgress(progress);
        mBinding.calibrateVertical.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double calibration = (double) progress / 50 - 1;
                mSettings.getProcessSettings().setVerticalCalibrationOffset(calibration);
                mBinding.calibrateVerticalValue.setText("Vertical calibration: " + String.valueOf(mSettings.getProcessSettings().getVerticalCalibrationOffset()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double calibration = (double) seekBar.getProgress() / 50 - 1;
                mSettings.getProcessSettings().setVerticalCalibrationOffset(calibration);
                mBinding.calibrateVerticalValue.setText("Vertical calibration: " + String.valueOf(mSettings.getProcessSettings().getVerticalCalibrationOffset()));
            }
        });

        // outside
        mBinding.outsideXValue.setText("X outside: " + String.valueOf(mSettings.getProcessSettings().getGazeOutsizeValueX()));
        mBinding.outsideX.setProgress((int) Math.round((mSettings.getProcessSettings().getGazeOutsizeValueX() - 1) * 100));
        mBinding.outsideX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double calibrationX = 1 + (double) progress / 100;
                mSettings.getProcessSettings().setGazeOutsizeValueX(calibrationX);
                mBinding.outsideXValue.setText("X outside: " + String.valueOf(mSettings.getProcessSettings().getGazeOutsizeValueX()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double calibrationX = 1 + (double) seekBar.getProgress() / 100;
                mSettings.getProcessSettings().setGazeOutsizeValueX(calibrationX);
                mBinding.outsideXValue.setText("X outside: " + String.valueOf(mSettings.getProcessSettings().getGazeOutsizeValueX()));
            }
        });

        mBinding.outsideYValue.setText("Y outside: " + String.valueOf(mSettings.getProcessSettings().getGazeOutsizeValueY()));
        mBinding.outsideY.setProgress((int) Math.round((mSettings.getProcessSettings().getGazeOutsizeValueY() - 1) * 100));
        mBinding.outsideY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double calibrationY = 1 + (double) progress / 100;
                mSettings.getProcessSettings().setGazeOutsizeValueY(calibrationY);
                mBinding.outsideYValue.setText("Y outside: " + String.valueOf(calibrationY));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double calibrationX = 1 + (double) seekBar.getProgress() / 100;
                mSettings.getProcessSettings().setGazeOutsizeValueY(calibrationX);
                mBinding.outsideYValue.setText("Y outside: " + String.valueOf(mSettings.getProcessSettings().getGazeOutsizeValueY()));
            }
        });

        // contrast
        mBinding.contrastValue.setText("Contrast: " + String.valueOf(mSettings.getProcessSettings().getContrast()));
        mBinding.contrast.setProgress((int) Math.round((mSettings.getProcessSettings().getContrast() - 1) * 10));
        mBinding.contrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double contrast = 1 + (double) progress / 10;
                mSettings.getProcessSettings().setContrast(contrast);
                mBinding.contrastValue.setText("Contrast: " + String.valueOf(mSettings.getProcessSettings().getContrast()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double contrast = 1 + (double) seekBar.getProgress() / 10;
                mSettings.getProcessSettings().setContrast(contrast);
                mBinding.contrastValue.setText("Contrast: " + String.valueOf(mSettings.getProcessSettings().getContrast()));
            }
        });

        // brightness
        mBinding.brightnessValue.setText("Brightness: " + String.valueOf(mSettings.getProcessSettings().getBrightness()));
        mBinding.brightness.setProgress((int) Math.round(mSettings.getProcessSettings().getBrightness()));
        mBinding.brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double brightness = (double) progress;
                mSettings.getProcessSettings().setBrightness(brightness);
                mBinding.brightnessValue.setText("Brightness: " + String.valueOf(brightness));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double brightness = (double) seekBar.getProgress();
                mSettings.getProcessSettings().setBrightness(brightness);
                mBinding.brightnessValue.setText("Brightness: " + String.valueOf(brightness));
            }
        });

        mBinding.equalizeHist.setChecked(mSettings.getProcessSettings().isEqualizeHist());
        mBinding.equalizeHist.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.getProcessSettings().setEqualizeHist(isChecked));

        mBinding.normalize.setChecked(mSettings.getProcessSettings().isNormalize());
        mBinding.normalize.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.getProcessSettings().setNormalize(isChecked));

        mBinding.showProcessVideo.setChecked(mSettings.getGeneral().isShowProcessVideo());
        mBinding.showProcessVideo.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.getGeneral().setShowProcessVideo(isChecked));

        mBinding.autoSettings.setChecked(mSettings.getProcessSettings().isAutoSettings());
        mBinding.autoSettings.setOnCheckedChangeListener((buttonView, isChecked) -> mSettings.getProcessSettings().setIsAutoSettings(isChecked));

        mBinding.buttonCancel.setOnClickListener(v -> {
            mSettings = SettingsHelper.loadSetting();
            getMainActivity().setCameraSettings(mSettings);
            getMainActivity().onBackPressed();
        });

        mBinding.buttonSave.setOnClickListener(v -> {
            SettingsHelper.saveSetting(mSettings);
            getMainActivity().onBackPressed();
        });

        return mBinding.getRoot();
    }

    private int getSelectedResolution() {
        int result = -1;

        if (mResolutionList != null && mResolutionList.size() > 0) {
            Size currentSize = calculateCameraFrameSize(mResolutionList, mSettings.getGeneral().getCameraSize().getWidth(), mSettings.getGeneral().getCameraSize().getHeight());

            for (Camera.Size item : mResolutionList) {
                if (item.width == currentSize.getWidth() && item.height == currentSize.getHeight()) {
                    result = mResolutionList.indexOf(item);
                    break;
                }
            }
        }

        return result;
    }

    private Size calculateCameraFrameSize(List<Camera.Size> supportedSizes, int surfaceWidth, int surfaceHeight) {
        int calcWidth = 0;
        int calcHeight = 0;

        for (Camera.Size size : supportedSizes) {
            int width = size.width;
            int height = size.height;

            if (width <= surfaceWidth && height <= surfaceHeight) {
                if (width >= calcWidth && height >= calcHeight) {
                    calcWidth = width;
                    calcHeight = height;
                }
            }
        }

        return new Size(calcWidth, calcHeight);
    }
}
