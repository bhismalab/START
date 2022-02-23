package com.reading.start.sdk.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import com.reading.start.sdk.Constants;
import com.reading.start.tests.ServerLog;
import com.reading.start.tests.TestLog;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.opencv.core.Mat;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Helper class for recording video from images.
 */
public class VideoRecorder {
    private static final String TAG = VideoRecorder.class.getSimpleName();

    private static VideoRecorder sInstance = null;

    public static VideoRecorder getInstance() {
        if (sInstance == null) {
            sInstance = new VideoRecorder();
        }

        return sInstance;
    }

    private static final int AUDIO_RATE_IN_HZ = 44100;

    private static final int FRAME_RATE = 30;

    private static final String VIDEO_FORMAT = "mp4";

    private FFmpegFrameRecorder mRecorder;

    private Frame mFrameImage = null;

    private long mStartTime = 0;

    private boolean mRecording = false;

    private File mFile;

    private int mWidth;

    private int mHeight;

    private byte[] mByteFrame;

    private Object mLock = new Object();

    private Mat mTimeStampMat = null;

    private Bitmap mTimeStampBitmap = null;

    private Canvas mTimeStampCanvas = null;

    private Paint mTimePaint = null;

    private int mAudioBufferSize = 0;

    private ShortBuffer mFakeAudioData = null;

    /**
     * Starts video recording
     */
    public void start(int width, int height, File videoFile) {
        ServerLog.log("VIDEO RECORDER", "START");

        mWidth = width;
        mHeight = height;
        mFile = videoFile;

        try {
            initRecorder();
            startRecording();
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("VIDEO RECORDER", "ERROR: " + e.getMessage());
        }

        mStartTime = 0;
        mRecorder.setTimestamp(0);

        // init fake audio data
        mAudioBufferSize = AudioRecord.getMinBufferSize(AUDIO_RATE_IN_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mFakeAudioData = ShortBuffer.allocate(mAudioBufferSize);
    }

    /**
     * Write frame to video
     */
    public void writeFrame(Mat frame) {
        writeFrame(frame, -1);
    }

    /**
     * Write frame to video
     */
    public void writeFrame(Mat frame, long timeStamp) {
        ServerLog.log("VIDEO RECORDER", "WRITE FRAME START");
        try {
            synchronized (mLock) {
                if (mFrameImage != null && mRecording) {
                    if (Constants.ENABLE_TIME_STAMP_ON_VIDEO) {
                        addTimeStamp(frame, timeStamp);
                    }

                    if (mByteFrame == null) {
                        mByteFrame = new byte[(int) (frame.total() * frame.channels())];
                    }

                    frame.get(0, 0, mByteFrame);
                    ((ByteBuffer) mFrameImage.image[0].position(0)).put(mByteFrame);
                    long t = (timeStamp != -1) ? timeStamp * 1000 : (1000 * (System.currentTimeMillis() - mStartTime));

                    if (t > mRecorder.getTimestamp()) {
                        mRecorder.setTimestamp(t);
                    }

                    mRecorder.record(mFrameImage);
                    //mRecorder.recordSamples(mFakeAudioData);
                    TestLog.d(TAG, "timestamp: " + t);
                }
            }
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("VIDEO RECORDER", "ERROR: " + e.getMessage());
        }
        ServerLog.log("VIDEO RECORDER", "WRITE FRAME END");
    }

    /**
     * Stop video recording
     */
    public void stop() {
        try {
            stopRecording();
        } catch (Exception e) {
            TestLog.e(TAG, e);
        }
    }

    /**
     * Return value whether indicate is video recoding
     */
    public boolean isRecording() {
        return mRecording;
    }

    /**
     * Initialize video recorder
     */
    private boolean initRecorder() {
        ServerLog.log("VIDEO RECORDER", "INIT RECORDER");
        boolean result = false;

        try {
            if (mFrameImage == null) {
                mFrameImage = new Frame(mWidth, mHeight, Frame.DEPTH_UBYTE, 4);
            }

            if (mRecorder == null) {
                FFmpegLogCallback.set();
                mRecorder = new FFmpegFrameRecorder(mFile, mWidth, mHeight, 1);
                mRecorder.setFormat(VIDEO_FORMAT);
                mRecorder.setSampleRate(AUDIO_RATE_IN_HZ);
                mRecorder.setFrameRate(FRAME_RATE);
                mRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            }

            result = true;
            Log.i(TAG, "VideoRecorder initialize success");
            ServerLog.log("VIDEO RECORDER", "INIT SUCCESS");
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("VIDEO RECORDER", "ERROR: " + e.getMessage());
        }

        return result;
    }

    private void startRecording() {
        try {
            mByteFrame = null;
            mRecorder.start();
            mStartTime = System.currentTimeMillis();
            mRecording = true;
        } catch (Exception e) {
            TestLog.e(TAG, e);
            ServerLog.log("VIDEO RECORDER", "ERROR: " + e.getMessage());
        }
    }

    private void stopRecording() {
        synchronized (mLock) {
            if (mRecorder != null && mRecording) {
                mRecording = false;

                try {
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                } catch (Exception e) {
                    TestLog.e(TAG, e);
                }

                Log.v(TAG, "Finished VideoRecorder");
            }

            mFrameImage = null;
        }
    }

    /**
     * Adds time stamp to video
     */
    private void addTimeStamp(Mat mat, long timeStamp) {
        if (mat != null) {
            if (mTimeStampMat == null) {
                mTimeStampBitmap = Bitmap.createBitmap(110, 40, Bitmap.Config.ARGB_4444);
                mTimeStampCanvas = new Canvas(mTimeStampBitmap);
                mTimePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mTimePaint.setTextSize(30);
                mTimePaint.setColor(Color.WHITE);
                mTimePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                mTimePaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));

                mTimeStampMat = new Mat();
                org.opencv.android.Utils.bitmapToMat(mTimeStampBitmap, mTimeStampMat);
            }

            String text = String.valueOf((timeStamp == -1) ? (System.currentTimeMillis() - mStartTime) : timeStamp);
            int timeCol = mTimeStampMat.cols();
            int timeRow = mTimeStampMat.rows();

            mTimeStampCanvas.drawColor(Color.BLACK);
            mTimeStampCanvas.drawText(text, 5, 30, mTimePaint);
            org.opencv.android.Utils.bitmapToMat(mTimeStampBitmap, mTimeStampMat);

            Mat timeStampAre = mat.submat(mat.rows() - timeRow, mat.rows(), 0, timeCol);
            mTimeStampMat.copyTo(timeStampAre);
        }
    }
}
