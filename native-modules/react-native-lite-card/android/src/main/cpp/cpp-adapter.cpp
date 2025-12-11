#include <jni.h>
#include "litecardOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::litecard::initialize(vm);
}
