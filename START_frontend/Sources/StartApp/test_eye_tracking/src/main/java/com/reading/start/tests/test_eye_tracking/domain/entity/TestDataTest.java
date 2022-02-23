package com.reading.start.tests.test_eye_tracking.domain.entity;

import com.google.gson.annotations.SerializedName;
import com.reading.start.tests.test_eye_tracking.Constants;

import java.util.ArrayList;
import java.util.Calendar;

public class TestDataTest {
    public static final String FILED_NAME = "name";
    public static final String FILED_START_TIME = "startTime";
    public static final String FILED_END_TIME = "endTime";
    public static final String FILED_GAZE_DETECTION = "gazeDetection";
    public static final String FILED_GAZE_ON_SCREEN = "gazeOnTheScreen";
    public static final String FILED_FILE_PATH = "filePath";
    public static final String FILED_ITEMS_STIMULUS_QUALITY = "itemsStimulusQuality";
    public static final String FILED_ITEMS_EYE_TRACKING_QUALITY = "itemsEyeTrackingQuality";
    public static final String FILED_ITEMS_STIMULUS = "itemsStimulus";
    public static final String FILED_ITEMS_EYE_TRACKING = "itemsEyeTracking";
    public static final String FILED_ITEMS_MERGED = "itemsMerged";
    public static final String FILED_ITEMS_MERGED_QUALITY = "itemsMergedQality";
    public static final String FILED_INTERRUPTED = "interrupted";
    public static final String FILED_CALIBRATION_QUALITY = "calibrationQuality";
    public static final String FILED_VIDEO_PROCESSED = "videoProcessed";

    @SerializedName(FILED_NAME)
    private String mName = "";

    @SerializedName(FILED_START_TIME)
    private long mStartTime = 0;

    @SerializedName(FILED_END_TIME)
    private long mEndTime = 0;

    @SerializedName(FILED_GAZE_DETECTION)
    private int mGazeDetection = -1;

    @SerializedName(FILED_GAZE_ON_SCREEN)
    private int mGazeOnTheScreen = -1;

    @SerializedName(FILED_FILE_PATH)
    private String mFilePath = "";

    @SerializedName(FILED_ITEMS_STIMULUS_QUALITY)
    private ArrayList<TestDataStep> mItemsStimulusQuality = new ArrayList<>();

    @SerializedName(FILED_ITEMS_EYE_TRACKING_QUALITY)
    private ArrayList<TestDataEyeTracking> mItemsEyeTrackingQuality = new ArrayList<>();

    @SerializedName(FILED_ITEMS_STIMULUS)
    private ArrayList<TestDataStep> mItemsStimulus = new ArrayList<>();

    @SerializedName(FILED_ITEMS_EYE_TRACKING)
    private ArrayList<TestDataEyeTracking> mItemsEyeTracking = new ArrayList<>();

    @SerializedName(FILED_ITEMS_MERGED)
    private ArrayList<TestDataMerged> mItemsMerged = new ArrayList<>();

    @SerializedName(FILED_ITEMS_MERGED_QUALITY)
    private ArrayList<TestDataMerged> mItemsMergedQuality = new ArrayList<>();

    @SerializedName(FILED_INTERRUPTED)
    private boolean mInterrupted = false;

    @SerializedName(FILED_CALIBRATION_QUALITY)
    private int mCalibrationQuality = -1;

    @SerializedName(FILED_VIDEO_PROCESSED)
    private boolean mVideoProcessed = false;

    public TestDataTest(String name) {
        mName = name;
        mStartTime = Calendar.getInstance().getTimeInMillis();
        mItemsStimulus = new ArrayList<>();
        mItemsEyeTracking = new ArrayList<>();
        mItemsEyeTrackingQuality = new ArrayList<>();
        mItemsStimulusQuality = new ArrayList<>();
        mItemsMerged = new ArrayList<>();
        mItemsMergedQuality = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long startDate) {
        mStartTime = startDate;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endDate) {
        mEndTime = endDate;
    }

    public ArrayList<TestDataStep> getItemsStimulus() {
        return mItemsStimulus;
    }

    public void setItemsStimulus(ArrayList<TestDataStep> items) {
        mItemsStimulus = items;
    }

    public ArrayList<TestDataEyeTracking> getItemsEyeTracking() {
        return mItemsEyeTracking;
    }

    public void setItemsEyeTracking(ArrayList<TestDataEyeTracking> items) {
        mItemsEyeTracking = items;
    }

    public ArrayList<TestDataMerged> getItemsMerged() {
        return mItemsMerged;
    }

    public void setItemsMerged(ArrayList<TestDataMerged> itemsMerged) {
        mItemsMerged = itemsMerged;
    }

    public ArrayList<TestDataStep> getItemsStimulusQuality() {
        return mItemsStimulusQuality;
    }

    public void setItemsStimulusQuality(ArrayList<TestDataStep> itemsStimulusQuality) {
        mItemsStimulusQuality = itemsStimulusQuality;
    }

    public ArrayList<TestDataEyeTracking> getItemsEyeTrackingQuality() {
        return mItemsEyeTrackingQuality;
    }

    public void setItemsEyeTrackingQuality(ArrayList<TestDataEyeTracking> itemsEyeTrackingQuality) {
        mItemsEyeTrackingQuality = itemsEyeTrackingQuality;
    }

    public ArrayList<TestDataMerged> getItemsMergedQuality() {
        return mItemsMergedQuality;
    }

    public void setItemsMergedQuality(ArrayList<TestDataMerged> itemsMergedQuality) {
        mItemsMergedQuality = itemsMergedQuality;
    }

    public int getGazeDetection() {
        return mGazeDetection;
    }

    public void setGazeDetection(int gazeDetection) {
        mGazeDetection = gazeDetection;
    }

    public int getGazeOnTheScreen() {
        return mGazeOnTheScreen;
    }

    public void setGazeOnTheScreen(int gazeOnTheScreen) {
        mGazeOnTheScreen = gazeOnTheScreen;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String path) {
        mFilePath = path;
    }

    public boolean isInterrupted() {
        return mInterrupted;
    }

    public void setInterrupted(boolean interrupted) {
        mInterrupted = interrupted;
    }

    public boolean isVideoProcessed() {
        return mVideoProcessed;
    }

    public void setVideoProcessed(boolean videoProcessed) {
        mVideoProcessed = videoProcessed;
    }

    public int calculateGazeDetection() {
        int result = 0;

        if (mItemsEyeTracking != null) {
            int totalCount = 0;
            int validCount = 0;

            for (TestDataEyeTracking stepItem : mItemsEyeTracking) {
                totalCount++;

                if (stepItem.isValid() == 1) {
                    validCount++;
                }
            }

            result = (int) ((float) validCount / (float) totalCount * 100f);
        }

        return result;
    }

    public int calculateGazeOnScreen() {
        int result = 0;

        if (mItemsEyeTracking != null) {
            int totalCount = 0;
            int validCount = 0;

            for (TestDataEyeTracking stepItem : mItemsEyeTracking) {
                totalCount++;

                if (stepItem.isValid() == 1 && stepItem.getGazeOut() == 0) {
                    validCount++;
                }
            }

            result = (int) ((float) validCount / (float) totalCount * 100f);
        }

        return result;
    }


    private int findImagePosition(TestDataEyeTracking item, ArrayList<TestDataStep> stimulus) {
        int result = -1;

        if (item != null && stimulus != null && stimulus.size() > 0) {
            boolean found = false;

            for (int i = stimulus.size(); i > 0; i--) {
                if (stimulus.get(i - 1).getItems() != null && stimulus.get(i - 1).getItems().size() > 0) {
                    for (int j = stimulus.get(i - 1).getItems().size(); j > 0; j--) {
                        if (stimulus.get(i - 1).getItems().get(j - 1).getTime() <= item.getTime()) {
                            result = stimulus.get(i - 1).getItems().get(j - 1).getStimulusSide().equals(Constants.STIMULUS_SIDE_LEFT) ? 0 : 1;
                            found = true;
                            break;
                        }
                    }
                }

                if (found) {
                    break;
                }
            }
        }

        return result;
    }

    public int calculateGazeQuality() {
        int result = 0;
        ArrayList<CalibrationCheckItem> calibrationCheckItems = new ArrayList<>();

        for (TestDataEyeTracking tItem : mItemsEyeTrackingQuality) {
            int imagePosition = findImagePosition(tItem, mItemsStimulusQuality);
            calibrationCheckItems.add(new CalibrationCheckItem((int) tItem.getX(), imagePosition));
        }

        if (calibrationCheckItems.size() > 0) {
            int countValid = 0;

            for (CalibrationCheckItem item : calibrationCheckItems) {
                if (item.isValid()) {
                    countValid++;
                }
            }

            result = (100 * countValid) / calibrationCheckItems.size();
        }

        return result;
    }

    public int getCalibrationQuality() {
        return mCalibrationQuality;
    }

    public void setCalibrationQuality(int calibrationQuality) {
        mCalibrationQuality = calibrationQuality;
    }
}
