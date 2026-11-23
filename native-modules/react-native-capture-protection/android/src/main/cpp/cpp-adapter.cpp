#include <jni.h>
#include "reactnativecaptureprotectionOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativecaptureprotection::initialize(vm);
}
