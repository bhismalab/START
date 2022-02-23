#ifndef EYEDETECTPOC_NATIVE_LIB_H
#define EYEDETECTPOC_NATIVE_LIB_H

#include <opencv2/core/mat.hpp>

// Pre-declarations
cv::Mat floodKillEdges(cv::Mat &mat);

void scaleToFastSize(const cv::Mat &src, cv::Mat &dst);

void
testPossibleCentersFormula(int x, int y, const cv::Mat &weight, double gx, double gy, cv::Mat &out);

cv::Point unscalePoint(cv::Point p, cv::Rect origSize);

cv::Mat computeMatXGradient(const cv::Mat &mat);

bool rectInImage(cv::Rect rect, cv::Mat image);

bool inMat(cv::Point p, int rows, int cols);

bool floodShouldPushPoint(const cv::Point &np, const cv::Mat &mat);

cv::Mat matrixMagnitude(const cv::Mat &matX, const cv::Mat &matY);

double computeDynamicThreshold(const cv::Mat &mat, double stdDevFactor);

cv::Point findEyeCenter(cv::Mat face, cv::Rect eye);

#endif //EYEDETECTPOC_NATIVE_LIB_H
