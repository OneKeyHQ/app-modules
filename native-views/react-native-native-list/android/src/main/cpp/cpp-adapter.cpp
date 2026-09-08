#include <jni.h>
#include "nativelistOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::nativelist::initialize(vm);
}
