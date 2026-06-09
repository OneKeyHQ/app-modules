#include <jni.h>
#include "perpdepthbarOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::perpdepthbar::initialize(vm);
}
