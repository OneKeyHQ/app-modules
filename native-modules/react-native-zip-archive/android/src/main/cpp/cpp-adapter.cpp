#include <jni.h>
#include "reactnativeziparchiveOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativeziparchive::initialize(vm);
}
