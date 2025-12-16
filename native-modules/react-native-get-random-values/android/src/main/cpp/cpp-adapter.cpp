#include <jni.h>
#include "HybridReactNativeGetRandomValuesSpecSwift.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativegetrandomvalues::initialize(vm);
}
