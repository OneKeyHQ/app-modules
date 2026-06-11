cmake_minimum_required(VERSION 3.13)

# Native (JNI) side of the SplitBundleLoader module.
#
# WHY THIS EXISTS:
# The Kotlin `loadSegment` previously called `ReactContext.registerSegment`,
# whose completion callback fires BEFORE the segment bytecode is evaluated
# into the runtime (the C++ ReactInstance::registerSegment only ENQUEUES the
# eval onto the RuntimeScheduler and returns). That races Metro's
# `import().then(() => __r(moduleId))` and can produce a fatal, uncatchable
# "Requiring unknown module".
#
# This library evaluates the segment OURSELVES on the JS thread via the
# bridgeless CallInvoker (which receives `jsi::Runtime&`) and signals
# completion in that SAME callback, so eval + resolve are one atomic unit —
# mirroring the iOS `callFunctionOnBufferedRuntimeExecutor:` fix.
project(splitbundleloader)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_VERBOSE_MAKEFILE ON)

add_library(
  splitbundleloader
  SHARED
  src/main/cpp/SplitBundleLoaderJSI.cpp
)

target_include_directories(
  splitbundleloader
  PRIVATE
  src/main/cpp
)

# jsi, reactnative (which carries CallInvoker / CallInvokerHolder headers and
# the merged libreactnative.so), and fbjni are all prefab targets exposed by
# the react-android AAR. See ReactAndroid build.gradle.kts prefab entries:
#   - jsi               (../ReactCommon/jsi/)
#   - reactnative       (turbomodule/ReactCommon/CallInvokerHolder.h,
#                        callinvoker/ReactCommon/CallInvoker.h, ...)
find_package(ReactAndroid REQUIRED CONFIG)
find_package(fbjni REQUIRED CONFIG)

target_link_libraries(
  splitbundleloader
  android
  log
  fbjni::fbjni
  ReactAndroid::jsi
  ReactAndroid::reactnative
)
