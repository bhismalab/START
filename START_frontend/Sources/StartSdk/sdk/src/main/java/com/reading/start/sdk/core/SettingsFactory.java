package com.reading.start.sdk.core;

import android.util.Size;

import com.reading.start.sdk.Constants;

public class SettingsFactory {
    public static Settings getDebugDetect() {
        Settings.General generalSettings = new Settings.General(
                DetectType.RealTime,
                new Size(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT),
                Constants.CAMERA_INDEX,
                true,
                DetectMode.Mode3x2,
                CalibrateMode.Point1,
                false,
                false,
                2,
                true);


        Settings.Ui uiSettings = new Settings.Ui(
                true, // face
                true, // right eye
                true, // left eye
                true, // right pupil
                true // left pupil
        );

        Settings.Process processSettings = new Settings.Process(
                true, // face
                true, // right eye
                true, // left eye
                true, // right pupil
                true, // left pupil
                Constants.DEFAULT_PUPIL_AREA_SIZE,
                Constants.THRESHOLD_HORIZONTAL,
                Constants.THRESHOLD_VERTICAL,
                Constants.GAZE_OUTSIZE_VALUE_X,
                Constants.GAZE_OUTSIZE_VALUE_Y,
                Constants.CENTER_AREA_SIZE,
                Constants.RIGHT_AREA_SIZE,
                Constants.LEFT_AREA_SIZE,
                Constants.DEFAULT_CONTRAST,
                Constants.DEFAULT_BRIGHTNESS,
                false,
                true,
                Constants.VERTICAL_CALIBRATION_OFFSET,
                false
        );

        Settings settings = new Settings(generalSettings, uiSettings, processSettings);
        return settings;
    }

    public static Settings getDebugDetectPostprocessing() {
        Settings.General generalSettings = new Settings.General(
                DetectType.PostProcessing,
                new Size(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT),
                Constants.CAMERA_INDEX,
                true,
                DetectMode.Mode2x1,
                CalibrateMode.Point1,
                true,
                false,
                2,
                true);


        Settings.Ui uiSettings = new Settings.Ui(
                true, // face
                true, // right eye
                true, // left eye
                true, // right pupil
                true // left pupil
        );

        Settings.Process processSettings = new Settings.Process(
                true, // face
                true, // right eye
                true, // left eye
                true, // right pupil
                true, // left pupil
                Constants.DEFAULT_PUPIL_AREA_SIZE,
                Constants.THRESHOLD_HORIZONTAL,
                Constants.THRESHOLD_VERTICAL,
                Constants.GAZE_OUTSIZE_VALUE_X,
                Constants.GAZE_OUTSIZE_VALUE_Y,
                Constants.CENTER_AREA_SIZE,
                Constants.RIGHT_AREA_SIZE,
                Constants.LEFT_AREA_SIZE,
                Constants.DEFAULT_CONTRAST,
                Constants.DEFAULT_BRIGHTNESS,
                false,
                true,
                Constants.VERTICAL_CALIBRATION_OFFSET,
                false
        );

        Settings settings = new Settings(generalSettings, uiSettings, processSettings);
        return settings;
    }

    public static Settings getReleaseDetect2x1() {
        Settings.General generalSettings = new Settings.General(
                DetectType.RealTime,
                new Size(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT),
                Constants.CAMERA_INDEX,
                false,
                DetectMode.Mode2x1,
                CalibrateMode.Point1,
                true,
                true,
                2,
                false);


        Settings.Ui uiSettings = new Settings.Ui(
                false, // face
                false, // right eye
                false, // left eye
                false, // right pupil
                false // left pupil
        );

        Settings.Process processSettings = new Settings.Process(
                true, // face
                true, // right eye
                true, // left eye
                true, // right pupil
                true, // left pupil
                Constants.DEFAULT_PUPIL_AREA_SIZE,
                Constants.THRESHOLD_HORIZONTAL,
                Constants.THRESHOLD_VERTICAL,
                Constants.GAZE_OUTSIZE_VALUE_X,
                Constants.GAZE_OUTSIZE_VALUE_Y,
                Constants.CENTER_AREA_SIZE,
                Constants.RIGHT_AREA_SIZE,
                Constants.LEFT_AREA_SIZE,
                Constants.DEFAULT_CONTRAST,
                Constants.DEFAULT_BRIGHTNESS,
                false,
                true,
                Constants.VERTICAL_CALIBRATION_OFFSET,
                true
        );

        Settings settings = new Settings(generalSettings, uiSettings, processSettings);
        return settings;
    }

    public static Settings getReleaseDetect2x2() {
        Settings.General generalSettings = new Settings.General(
                DetectType.RealTime,
                new Size(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT),
                Constants.CAMERA_INDEX,
                false,
                DetectMode.Mode2x2,
                CalibrateMode.Point1,
                false,
                false,
                2,
                false);


        Settings.Ui uiSettings = new Settings.Ui(
                false, // face
                false, // right eye
                false, // left eye
                false, // right pupil
                false // left pupil
        );

        Settings.Process processSettings = new Settings.Process(
                true, // face
                true, // right eye
                true, // left eye
                true, // right pupil
                true, // left pupil
                Constants.DEFAULT_PUPIL_AREA_SIZE,
                Constants.THRESHOLD_HORIZONTAL,
                Constants.THRESHOLD_VERTICAL,
                Constants.GAZE_OUTSIZE_VALUE_X,
                Constants.GAZE_OUTSIZE_VALUE_Y,
                Constants.CENTER_AREA_SIZE,
                Constants.RIGHT_AREA_SIZE,
                Constants.LEFT_AREA_SIZE,
                Constants.DEFAULT_CONTRAST,
                Constants.DEFAULT_BRIGHTNESS,
                false,
                true,
                Constants.VERTICAL_CALIBRATION_OFFSET,
                true
        );

        Settings settings = new Settings(generalSettings, uiSettings, processSettings);
        return settings;
    }

    public static Settings getReleaseDetect3x1() {
        Settings.General generalSettings = new Settings.General(
                DetectType.RealTime,
                new Size(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT),
                Constants.CAMERA_INDEX,
                false,
                DetectMode.Mode3x1,
                CalibrateMode.Point1,
                true,
                true,
                2,
                false);


        Settings.Ui uiSettings = new Settings.Ui(
                false, // face
                false, // right eye
                false, // left eye
                false, // right pupil
                false // left pupil
        );

        Settings.Process processSettings = new Settings.Process(
                true, // face
                true, // right eye
                true, // left eye
                true, // right pupil
                true, // left pupil
                Constants.DEFAULT_PUPIL_AREA_SIZE,
                Constants.THRESHOLD_HORIZONTAL,
                Constants.THRESHOLD_VERTICAL,
                Constants.GAZE_OUTSIZE_VALUE_X,
                Constants.GAZE_OUTSIZE_VALUE_Y,
                Constants.CENTER_AREA_SIZE,
                Constants.RIGHT_AREA_SIZE,
                Constants.LEFT_AREA_SIZE,
                Constants.DEFAULT_CONTRAST,
                Constants.DEFAULT_BRIGHTNESS,
                false,
                true,
                Constants.VERTICAL_CALIBRATION_OFFSET,
                true
        );

        Settings settings = new Settings(generalSettings, uiSettings, processSettings);
        return settings;
    }

    public static Settings getReleaseDetect3x2() {
        Settings.General generalSettings = new Settings.General(
                DetectType.RealTime,
                new Size(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT),
                Constants.CAMERA_INDEX,
                false,
                DetectMode.Mode3x2,
                CalibrateMode.Point1,
                false,
                false,
                2,
                false);


        Settings.Ui uiSettings = new Settings.Ui(
                false, // face
                false, // right eye
                false, // left eye
                false, // right pupil
                false // left pupil
        );

        Settings.Process processSettings = new Settings.Process(
                true, // face
                true, // right eye
                true, // left eye
                true, // right pupil
                true, // left pupil
                Constants.DEFAULT_PUPIL_AREA_SIZE,
                Constants.THRESHOLD_HORIZONTAL,
                Constants.THRESHOLD_VERTICAL,
                Constants.GAZE_OUTSIZE_VALUE_X,
                Constants.GAZE_OUTSIZE_VALUE_Y,
                Constants.CENTER_AREA_SIZE,
                Constants.RIGHT_AREA_SIZE,
                Constants.LEFT_AREA_SIZE,
                Constants.DEFAULT_CONTRAST,
                Constants.DEFAULT_BRIGHTNESS,
                false,
                true,
                Constants.VERTICAL_CALIBRATION_OFFSET,
                true
        );

        Settings settings = new Settings(generalSettings, uiSettings, processSettings);
        return settings;
    }

    public static Settings getReleaseDetect2x1Postprocessing() {
        Settings.General generalSettings = new Settings.General(
                DetectType.PostProcessing,
                new Size(Constants.CAMERA_WIDTH, Constants.CAMERA_HEIGHT),
                Constants.CAMERA_INDEX,
                false,
                DetectMode.Mode2x1,
                CalibrateMode.Point1,
                true,
                true,
                2,
                false);


        Settings.Ui uiSettings = new Settings.Ui(
                false, // face
                false, // right eye
                false, // left eye
                false, // right pupil
                false // left pupil
        );

        Settings.Process processSettings = new Settings.Process(
                true, // face
                true, // right eye
                true, // left eye
                true, // right pupil
                true, // left pupil
                Constants.DEFAULT_PUPIL_AREA_SIZE,
                Constants.THRESHOLD_HORIZONTAL,
                Constants.THRESHOLD_VERTICAL,
                Constants.GAZE_OUTSIZE_VALUE_X,
                Constants.GAZE_OUTSIZE_VALUE_Y,
                Constants.CENTER_AREA_SIZE,
                Constants.RIGHT_AREA_SIZE,
                Constants.LEFT_AREA_SIZE,
                Constants.DEFAULT_CONTRAST,
                Constants.DEFAULT_BRIGHTNESS,
                false,
                true,
                Constants.VERTICAL_CALIBRATION_OFFSET,
                true
        );

        Settings settings = new Settings(generalSettings, uiSettings, processSettings);
        return settings;
    }
}
