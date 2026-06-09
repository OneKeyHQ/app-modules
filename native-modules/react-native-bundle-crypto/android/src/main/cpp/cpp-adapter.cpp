#include <jni.h>
#include "reactnativebundlecryptoOnLoad.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::reactnativebundlecrypto::initialize(vm);
}
