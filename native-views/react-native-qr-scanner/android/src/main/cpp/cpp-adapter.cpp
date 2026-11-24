#include <jni.h>
#include "qrscannerOnLoad.hpp"

// Registers this package's HybridObjects (the template only initialized the
// Nitro core, so QrScanner/QrImageDecoder would not be found at runtime).
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::qrscanner::initialize(vm);
}
