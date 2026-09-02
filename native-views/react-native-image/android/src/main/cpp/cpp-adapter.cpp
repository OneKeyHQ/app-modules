#include <jni.h>
#include "onekeyimageOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::onekeyimage::initialize(vm);
}
