#include <jni.h>
#include "onekeyfe_reactnativecheckbiometricauthchangedOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::onekeyfe_reactnativecheckbiometricauthchanged::initialize(vm);
}
