#include <jni.h>
#include "{{cxxNamespace}}OnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::{{cxxNamespace}}::initialize(vm);
}
