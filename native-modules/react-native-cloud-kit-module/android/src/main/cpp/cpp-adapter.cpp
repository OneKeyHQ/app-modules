#include <jni.h>
#include "cloudkitmoduleOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::cloudkitmodule::initialize(vm);
}
