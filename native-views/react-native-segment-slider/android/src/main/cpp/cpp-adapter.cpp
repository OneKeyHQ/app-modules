#include <jni.h>
#include "segmentsliderOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::segmentslider::initialize(vm);
}
