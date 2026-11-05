#include <jni.h>
#include "reactnativejpushOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativejpush::initialize(vm);
}
