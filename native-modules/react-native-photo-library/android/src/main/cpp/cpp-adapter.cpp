#include <jni.h>
#include "reactnativephotolibraryOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativephotolibrary::initialize(vm);
}
