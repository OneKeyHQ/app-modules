#include <jni.h>
#include "reactnativebundleupdateOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativebundleupdate::initialize(vm);
}
