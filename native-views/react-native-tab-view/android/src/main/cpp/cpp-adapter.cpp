#include <jni.h>
#include "tabviewOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::tabview::initialize(vm);
}
