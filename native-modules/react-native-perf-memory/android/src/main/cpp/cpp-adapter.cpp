#include <jni.h>
#include "reactnativeperfmemoryOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativeperfmemory::initialize(vm);
}
