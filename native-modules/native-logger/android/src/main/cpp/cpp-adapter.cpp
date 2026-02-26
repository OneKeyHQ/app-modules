#include <jni.h>
#include "nativeloggerOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::nativelogger::initialize(vm);
}
