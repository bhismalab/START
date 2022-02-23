package com.reading.start.sdk.utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.video.KalmanFilter;

public class Kalman {
    // This implementation based on next info from next link
    // 1. https://github.com/Franciscodesign/Moving-Target-Tracking-with-OpenCV
    // 2. https://www.youtube.com/watch?v=1tVaisYEqpU

    private static final double KALMAN_DT = 0.2;
    private static final double KALMAN_ACCEL_NOISE_MAG = 0.1;

    private KalmanFilter mKalmanFilter;
    private Point mLastResult;
    private double mDeltaTime;

    public Kalman(Point startPoint) {
        Point pt = new Point(startPoint.x, startPoint.y);

        mKalmanFilter = new KalmanFilter(4, 2, 0, CvType.CV_32F);
        mDeltaTime = KALMAN_DT;

        Mat transitionMatrix = new Mat(4, 4, CvType.CV_32F, new Scalar(0));
        float[] tM = {
                1, 0, 1, 0,
                0, 1, 0, 1,
                0, 0, 1, 0,
                0, 0, 0, 1};
        transitionMatrix.put(0, 0, tM);

        mKalmanFilter.set_transitionMatrix(transitionMatrix);

        // init
        mLastResult = pt;
        Mat statePre = new Mat(4, 1, CvType.CV_32F, new Scalar(0)); // Toa do (x,y), van toc (0,0)
        statePre.put(0, 0, pt.x);
        statePre.put(1, 0, pt.y);
        statePre.put(2, 0, 0);
        statePre.put(3, 0, 0);
        mKalmanFilter.set_statePre(statePre);

        Mat statePost = new Mat(4, 1, CvType.CV_32F, new Scalar(0));
        statePost.put(0, 0, pt.x);
        statePost.put(1, 0, pt.y);
        statePost.put(2, 0, 0);
        statePost.put(3, 0, 0);
        mKalmanFilter.set_statePost(statePost);

        mKalmanFilter.set_measurementMatrix(Mat.eye(2, 4, CvType.CV_32F));

        //Mat processNoiseCov = Mat.eye(4, 4, CvType.CV_32F);
        Mat processNoiseCov = new Mat(4, 4, CvType.CV_32F, new Scalar(0));
        float[] dTime = {(float) (Math.pow(mDeltaTime, 4.0) / 4.0), 0,
                (float) (Math.pow(mDeltaTime, 3.0) / 2.0), 0, 0,
                (float) (Math.pow(mDeltaTime, 4.0) / 4.0), 0,
                (float) (Math.pow(mDeltaTime, 3.0) / 2.0),
                (float) (Math.pow(mDeltaTime, 3.0) / 2.0), 0,
                (float) Math.pow(mDeltaTime, 2.0), 0, 0,
                (float) (Math.pow(mDeltaTime, 3.0) / 2.0), 0,
                (float) Math.pow(mDeltaTime, 2.0)};
        processNoiseCov.put(0, 0, dTime);

        processNoiseCov = processNoiseCov.mul(processNoiseCov, KALMAN_ACCEL_NOISE_MAG);
        mKalmanFilter.set_processNoiseCov(processNoiseCov);

        Mat id1 = Mat.eye(2, 2, CvType.CV_32F);
        id1 = id1.mul(id1, 1e-1);
        mKalmanFilter.set_measurementNoiseCov(id1);

        Mat id2 = Mat.eye(4, 4, CvType.CV_32F);
        id2 = id2.mul(id2, .1);
        mKalmanFilter.set_errorCovPost(id2);
    }

    public Kalman(Point pt, double dt, double Accel_noise_mag) {
        mKalmanFilter = new KalmanFilter(4, 2, 0, CvType.CV_32F);
        mDeltaTime = dt;

        Mat transitionMatrix = new Mat(4, 4, CvType.CV_32F, new Scalar(0));
        float[] tM = {
                1, 0, 1, 0,
                0, 1, 0, 1,
                0, 0, 1, 0,
                0, 0, 0, 1};
        transitionMatrix.put(0, 0, tM);

        mKalmanFilter.set_transitionMatrix(transitionMatrix);

        // init
        mLastResult = pt;
        Mat statePre = new Mat(4, 1, CvType.CV_32F, new Scalar(0)); // Toa do (x,y), van toc (0,0)
        statePre.put(0, 0, pt.x);
        statePre.put(1, 0, pt.y);
        statePre.put(2, 0, 0);
        statePre.put(3, 0, 0);
        mKalmanFilter.set_statePre(statePre);

        Mat statePost = new Mat(4, 1, CvType.CV_32F, new Scalar(0));
        statePost.put(0, 0, pt.x);
        statePost.put(1, 0, pt.y);
        statePost.put(2, 0, 0);
        statePost.put(3, 0, 0);
        mKalmanFilter.set_statePost(statePost);

        mKalmanFilter.set_measurementMatrix(Mat.eye(2, 4, CvType.CV_32F));

        //Mat processNoiseCov = Mat.eye(4, 4, CvType.CV_32F);
        Mat processNoiseCov = new Mat(4, 4, CvType.CV_32F, new Scalar(0));
        float[] dTime = {(float) (Math.pow(mDeltaTime, 4.0) / 4.0), 0,
                (float) (Math.pow(mDeltaTime, 3.0) / 2.0), 0, 0,
                (float) (Math.pow(mDeltaTime, 4.0) / 4.0), 0,
                (float) (Math.pow(mDeltaTime, 3.0) / 2.0),
                (float) (Math.pow(mDeltaTime, 3.0) / 2.0), 0,
                (float) Math.pow(mDeltaTime, 2.0), 0, 0,
                (float) (Math.pow(mDeltaTime, 3.0) / 2.0), 0,
                (float) Math.pow(mDeltaTime, 2.0)};
        processNoiseCov.put(0, 0, dTime);

        processNoiseCov = processNoiseCov.mul(processNoiseCov, Accel_noise_mag); // Accel_noise_mag = 0.5
        mKalmanFilter.set_processNoiseCov(processNoiseCov);

        Mat id1 = Mat.eye(2, 2, CvType.CV_32F);
        id1 = id1.mul(id1, 1e-1);
        mKalmanFilter.set_measurementNoiseCov(id1);

        Mat id2 = Mat.eye(4, 4, CvType.CV_32F);
        id2 = id2.mul(id2, .1);
        mKalmanFilter.set_errorCovPost(id2);
    }

    public Point getPrediction() {
        Mat prediction = mKalmanFilter.predict();
        mLastResult = new Point(prediction.get(0, 0)[0], prediction.get(1, 0)[0]);
        return mLastResult;
    }

    private Point update(Point p, boolean dataCorrect) {
        Mat measurement = new Mat(2, 1, CvType.CV_32F, new Scalar(0));
        if (!dataCorrect) {
            measurement.put(0, 0, mLastResult.x);
            measurement.put(1, 0, mLastResult.y);
        } else {
            measurement.put(0, 0, p.x);
            measurement.put(1, 0, p.y);
        }
        // Correction
        Mat estimated = mKalmanFilter.correct(measurement);
        mLastResult.x = estimated.get(0, 0)[0];
        mLastResult.y = estimated.get(1, 0)[0];
        return mLastResult;
    }

    private Point correction(Point p) {
        Mat measurement = new Mat(2, 1, CvType.CV_32F, new Scalar(0));
        measurement.put(0, 0, p.x);
        measurement.put(1, 0, p.y);

        Mat estimated = mKalmanFilter.correct(measurement);
        mLastResult.x = estimated.get(0, 0)[0];
        mLastResult.y = estimated.get(1, 0)[0];
        return mLastResult;
    }


    private void setLastResult(Point lastResult) {
        mLastResult = lastResult;
    }

    public void pushPoint(Point point) {
        if (point != null) {
            Point kPoint = new Point(point.x, point.y);
            update(kPoint, true);
        }
    }

    public Point getPredictionPoint(Point point) {
        pushPoint(point);
        Point result = getPrediction();
        Double x = result.x;
        Double y = result.y;
        Point resultF = new Point(x.floatValue(), y.floatValue());
        return resultF;
    }
}