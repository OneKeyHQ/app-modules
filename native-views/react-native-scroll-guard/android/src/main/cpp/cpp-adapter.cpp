#include <jni.h>
#include "scrollguardOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::scrollguard::initialize(vm);
}
