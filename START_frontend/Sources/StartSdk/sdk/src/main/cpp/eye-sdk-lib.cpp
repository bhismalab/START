#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/opencv.hpp>
#include "constants.h"
#include "eye-sdk-lib.h"
#include <android/bitmap.h>

// ------------------------ DETECT PUPIL ------------------------

int mFastEyeWidth = kFastEyeWidth;

extern "C" JNIEXPORT jdoubleArray JNICALL
Java_com_reading_start_sdk_utils_PupilDetectHelper_detectEyeCenter__JIIIII(
        JNIEnv *env, jobject /* this */, jlong matObj, jint x, jint y, jint w, jint h, jint f) {

    // prepare input data
    cv::Mat face = *(cv::Mat *) matObj;
    cv::Rect eyeRect(x, y, w, h);

    mFastEyeWidth = f;

    // detect eye
    cv::Point resultPoint = findEyeCenter(face, eyeRect);

    // result point
    double resultX = resultPoint.x;
    double resultY = resultPoint.y;

    // return result to java
    jboolean isCopy2;
    jdoubleArray result = env->NewDoubleArray(2);
    jdouble *destArrayElems = env->GetDoubleArrayElements(result, &isCopy2);
    destArrayElems[0] = resultX;
    destArrayElems[1] = resultY;
    env->SetDoubleArrayRegion(result, 0, 2, destArrayElems);
    return result;
}

extern "C" JNIEXPORT jint JNICALL
Java_com_reading_start_sdk_utils_PupilDetectHelper_calculateBrightness__J(
        JNIEnv *env, jobject /* this */, jlong matObj) {

    // prepare input data
    cv::Mat mat = *(cv::Mat *) matObj;
    int count = 0;
    double value = 0;

    for (int i = 0; i < mat.cols; i++) {
        for (int j = 0; j < mat.rows; j++) {
            value += mat.at<uchar>(cv::Point(i, j));
            count++;
        }
    }

    jint result = value / count;
    return result;
}

cv::Point findEyeCenter(cv::Mat face, cv::Rect eye) {
    // main algorithm
    cv::Mat eyeROIUnscaled = face(eye);

    cv::Mat eyeROI;
    scaleToFastSize(eyeROIUnscaled, eyeROI);

    //-- Find the gradient
    cv::Mat gradientX = computeMatXGradient(eyeROI);
    cv::Mat gradientY = computeMatXGradient(eyeROI.t()).t();

    //-- Normalize and threshold the gradient
    // compute all the magnitudes
    cv::Mat mags = matrixMagnitude(gradientX, gradientY);
    //compute the threshold
    double gradientThresh = computeDynamicThreshold(mags, kGradientThreshold);
    //double gradientThresh = kGradientThreshold;
    //double gradientThresh = 0;
    //normalize
    for (int y = 0; y < eyeROI.rows; ++y) {
        double *Xr = gradientX.ptr<double>(y), *Yr = gradientY.ptr<double>(y);
        const double *Mr = mags.ptr<double>(y);
        for (int x = 0; x < eyeROI.cols; ++x) {
            double gX = Xr[x], gY = Yr[x];
            double magnitude = Mr[x];
            if (magnitude > gradientThresh) {
                Xr[x] = gX / magnitude;
                Yr[x] = gY / magnitude;
            } else {
                Xr[x] = 0.0;
                Yr[x] = 0.0;
            }
        }
    }

    //-- Create a blurred and inverted image for weighting
    cv::Mat weight;
    GaussianBlur(eyeROI, weight, cv::Size(kWeightBlurSize, kWeightBlurSize), 0, 0);
    for (int y = 0; y < weight.rows; ++y) {
        char *row = weight.ptr<char>(y);
        for (int x = 0; x < weight.cols; ++x) {
            row[x] = (255 - row[x]);
        }
    }

    //-- Run the algorithm!
    cv::Mat outSum = cv::Mat::zeros(eyeROI.rows, eyeROI.cols, CV_64F);
    // for each possible gradient location
    // Note: these loops are reversed from the way the paper does them
    // it evaluates every possible center for each gradient location instead of
    // every possible gradient location for every center.
    //printf("Eye Size: %ix%i\n",outSum.cols,outSum.rows);

    for (int y = 0; y < weight.rows; ++y) {
        const double *Xr = gradientX.ptr<double>(y), *Yr = gradientY.ptr<double>(y);

        for (int x = 0; x < weight.cols; ++x) {
            double gX = Xr[x], gY = Yr[x];

            if (gX == 0.0 && gY == 0.0) {
                continue;
            }

            testPossibleCentersFormula(x, y, weight, gX, gY, outSum);
        }
    }

    // scale all the values down, basically averaging them
    double numGradients = (weight.rows * weight.cols);
    cv::Mat out;
    outSum.convertTo(out, CV_32F, 1.0 / numGradients);

    //-- Find the maximum point
    cv::Point maxP;
    double maxVal;
    cv::minMaxLoc(out, NULL, &maxVal, NULL, &maxP);

    //-- Flood fill the edges
    if (kEnablePostProcess) {
        cv::Mat floodClone;
        //double floodThresh = computeDynamicThreshold(out, 1.5);
        double floodThresh = maxVal * kPostProcessThreshold;
        cv::threshold(out, floodClone, floodThresh, 0.0f, cv::THRESH_TOZERO);

        cv::Mat mask = floodKillEdges(floodClone);
        // redo max
        cv::minMaxLoc(out, NULL, &maxVal, NULL, &maxP, mask);
    }

    return unscalePoint(maxP, eye);
}

cv::Mat floodKillEdges(cv::Mat &mat) {
    rectangle(mat, cv::Rect(0, 0, mat.cols, mat.rows), 255);

    cv::Mat mask(mat.rows, mat.cols, CV_8U, 255);
    std::queue<cv::Point> toDo;
    toDo.push(cv::Point(0, 0));
    while (!toDo.empty()) {
        cv::Point p = toDo.front();
        toDo.pop();
        if (mat.at<float>(p) == 0.0f) {
            continue;
        }
        // add in every direction
        cv::Point np(p.x + 1, p.y); // right
        if (floodShouldPushPoint(np, mat)) toDo.push(np);
        np.x = p.x - 1;
        np.y = p.y; // left
        if (floodShouldPushPoint(np, mat)) toDo.push(np);
        np.x = p.x;
        np.y = p.y + 1; // down
        if (floodShouldPushPoint(np, mat)) toDo.push(np);
        np.x = p.x;
        np.y = p.y - 1; // up
        if (floodShouldPushPoint(np, mat)) toDo.push(np);
        // kill it
        mat.at<float>(p) = 0.0f;
        mask.at<uchar>(p) = 0;
    }
    return mask;
}

void testPossibleCentersFormula(int x, int y, const cv::Mat &weight, double gx, double gy,
                                cv::Mat &out) {
    // for all possible centers
    for (int cy = 0; cy < out.rows; ++cy) {
        double *Or = out.ptr<double>(cy);
        const char *Wr = weight.ptr<char>(cy);
        for (int cx = 0; cx < out.cols; ++cx) {
            if (x == cx && y == cy) {
                continue;
            }
            // create a vector from the possible center to the gradient origin
            double dx = x - cx;
            double dy = y - cy;
            // normalize d
            double magnitude = sqrt((dx * dx) + (dy * dy));
            dx = dx / magnitude;
            dy = dy / magnitude;
            double dotProduct = dx * gx + dy * gy;
            dotProduct = std::max(0.0, dotProduct);
            // square and multiply by the weight
            if (kEnableWeight) {
                Or[cx] += dotProduct * dotProduct * (Wr[cx] / kWeightDivisor);
            } else {
                Or[cx] += dotProduct * dotProduct;
            }
        }
    }
}

cv::Point unscalePoint(cv::Point p, cv::Rect origSize) {
    float ratio = (((float) mFastEyeWidth) / origSize.width);
    int x = round(p.x / ratio);
    int y = round(p.y / ratio);
    return cv::Point(x, y);
}

void scaleToFastSize(const cv::Mat &src, cv::Mat &dst) {
    cv::resize(src, dst, cv::Size(mFastEyeWidth, (((float) mFastEyeWidth) / src.cols) * src.rows));
}

cv::Mat computeMatXGradient(const cv::Mat &mat) {
    cv::Mat out(mat.rows, mat.cols, CV_64F);

    for (int y = 0; y < mat.rows; ++y) {
        const uchar *Mr = mat.ptr<uchar>(y);
        double *Or = out.ptr<double>(y);

        Or[0] = Mr[1] - Mr[0];
        for (int x = 1; x < mat.cols - 1; ++x) {
            Or[x] = (Mr[x + 1] - Mr[x - 1]) / 2.0;
        }
        Or[mat.cols - 1] = Mr[mat.cols - 1] - Mr[mat.cols - 2];
    }

    return out;
}

bool rectInImage(cv::Rect rect, cv::Mat image) {
    return rect.x > 0 && rect.y > 0 && rect.x + rect.width < image.cols &&
           rect.y + rect.height < image.rows;
}

bool inMat(cv::Point p, int rows, int cols) {
    return p.x >= 0 && p.x < cols && p.y >= 0 && p.y < rows;
}

bool floodShouldPushPoint(const cv::Point &np, const cv::Mat &mat) {
    return inMat(np, mat.rows, mat.cols);
}

cv::Mat matrixMagnitude(const cv::Mat &matX, const cv::Mat &matY) {
    cv::Mat mags(matX.rows, matX.cols, CV_64F);
    for (int y = 0; y < matX.rows; ++y) {
        const double *Xr = matX.ptr<double>(y), *Yr = matY.ptr<double>(y);
        double *Mr = mags.ptr<double>(y);
        for (int x = 0; x < matX.cols; ++x) {
            double gX = Xr[x], gY = Yr[x];
            double magnitude = sqrt((gX * gX) + (gY * gY));
            Mr[x] = magnitude;
        }
    }
    return mags;
}

double computeDynamicThreshold(const cv::Mat &mat, double stdDevFactor) {
    cv::Scalar stdMagnGrad, meanMagnGrad;
    cv::meanStdDev(mat, meanMagnGrad, stdMagnGrad);
    double stdDev = stdMagnGrad[0] / sqrt(mat.rows * mat.cols);
    return stdDevFactor * stdDev + meanMagnGrad[0];
}

template<class T, class TCpp>
class JavaArrayAccessor {
public:
    JavaArrayAccessor(JNIEnv *env, T array) :
            env(env),
            array(array),
            data(reinterpret_cast< TCpp * >(env->GetPrimitiveArrayCritical(array,
                                                                           NULL))) // never returns NULL
    {}

    ~JavaArrayAccessor() {
        env->ReleasePrimitiveArrayCritical(array, data, 0);
    }

    TCpp *getData() {
        return data;
    }

private:
    JNIEnv *env;
    T array;
    TCpp *data;
};

class AndroidBitmapAccessorException : public std::exception {
public:
    AndroidBitmapAccessorException(int code) :
            code(code) {}

    virtual ~AndroidBitmapAccessorException() {}

    const int code;
};

class AndroidBitmapAccessor {
public:
    AndroidBitmapAccessor(JNIEnv *env, jobject bitmap) throw(AndroidBitmapAccessorException):
            env(env),
            bitmap(bitmap),
            data(NULL) {
        int rv = AndroidBitmap_lockPixels(env, bitmap, reinterpret_cast< void ** >(&data));
        if (rv != ANDROID_BITMAP_RESULT_SUCCESS) {
            throw AndroidBitmapAccessorException(rv);
        }
    }

    ~AndroidBitmapAccessor() {
        if (data) {
            AndroidBitmap_unlockPixels(env, bitmap);
        }
    }

    uchar *getData() {
        return data;
    }

private:
    JNIEnv *env;
    jobject bitmap;
    uchar *data;
};

extern "C" JNIEXPORT void JNICALL
Java_com_reading_start_sdk_utils_PupilDetectHelper_handleFrame(JNIEnv *env,
                                                               jobject thiz,
                                                               jint width,
                                                               jint height,
                                                               jbyteArray nv21Data,
                                                               jobject bitmap) {
    try {
        // create output rgba-formatted output Mat object using the raw Java data
        // allocated by the Bitmap object to prevent an extra memcpy. note that
        // the bitmap must be created in ARGB_8888 format
        AndroidBitmapAccessor bitmapAccessor(env, bitmap);
        cv::Mat rgba(height, width, CV_8UC4, bitmapAccessor.getData());

        // create input nv21-formatted input Mat object using the raw Java data to
        // prevent extraneous allocations. note the use of height*1.5 to account
        // for the nv21 (YUV420) formatting
        JavaArrayAccessor<jbyteArray, uchar> nv21Accessor(env, nv21Data);
        cv::Mat nv21(height * 1.5, width, CV_8UC1, nv21Accessor.getData());

        // initialize the rgba output using the nv21 data
        cv::cvtColor(nv21, rgba, CV_YUV2RGBA_NV21);

        // convert the nv21 image to grayscale by lopping off the extra 0.5*height bits. note
        // this this ctor is smart enough to not actually copy the data
        cv::Mat gray(nv21, cv::Rect(0, 0, width, height));

        // do your processing on the nv21 and/or grayscale image here, making sure to update the
        // rgba mat with the appropriate output
    }
    catch (const AndroidBitmapAccessorException &e) {
        //LOGE("error locking bitmap: %d", e.code);
    }
}
