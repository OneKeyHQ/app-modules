#include <jni.h>
#include "chartwebviewOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::chartwebview::initialize(vm);
}
