#include <jni.h>
#include "reactnativeappupdateOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativeappupdate::initialize(vm);
}
