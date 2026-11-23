#include <jni.h>
#include "NitroModules.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void * reserved) {
  return margelo::nitro::initialize(vm);
}
