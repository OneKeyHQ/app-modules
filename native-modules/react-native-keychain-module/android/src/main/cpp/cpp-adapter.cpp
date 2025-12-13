#include <jni.h>
#include "keychainmoduleOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::keychainmodule::initialize(vm);
}
