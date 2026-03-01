#include <jni.h>
#include "reactnativewebviewcheckerOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativewebviewchecker::initialize(vm);
}
