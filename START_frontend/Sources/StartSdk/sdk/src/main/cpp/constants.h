#ifndef EYEDETECTPOC_CONSTANTS_H
#define EYEDETECTPOC_CONSTANTS_H

// Algorithm Parameters
// old value if 50
const int kFastEyeWidth = 35;
const int kWeightBlurSize = 5;
const bool kEnableWeight = true;
const float kWeightDivisor = 1.0;
const double kGradientThreshold = 50.0;

// Postprocessing
const bool kEnablePostProcess = false;
const float kPostProcessThreshold = 0.97;

#endif //EYEDETECTPOC_CONSTANTS_H
