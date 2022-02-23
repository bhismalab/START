# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.
set(pathToProject D:/Work/git/TherapyBox/StartApp/TherapyBoxPrivate/Sources/StartSdk)
set(pathToOpenCv D:/Work/git/test/OpenCV-android-sdk)
cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
include_directories(${pathToOpenCv}/sdk/native/jni/include)
add_library( eye-sdk-lib SHARED src/main/cpp/eye-sdk-lib.cpp )
add_library( lib_opencv SHARED IMPORTED )
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathToProject}/sdk/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)
find_library( log-lib log )
target_link_libraries( eye-sdk-lib $\{log-lib} lib_opencv)