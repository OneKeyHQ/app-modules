#include <jni.h>
#include "autosizeinputOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::autosizeinput::initialize(vm);
}
