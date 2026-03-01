#include <jni.h>
#include "reactnativesplashscreenOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativesplashscreen::initialize(vm);
}
