#include <jni.h>
#include "reactnativedeviceutilsOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativedeviceutils::initialize(vm);
}
