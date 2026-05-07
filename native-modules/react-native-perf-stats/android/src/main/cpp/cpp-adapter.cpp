#include <jni.h>
#include "reactnativeperfstatsOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativeperfstats::initialize(vm);
}
