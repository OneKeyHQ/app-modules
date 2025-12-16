#include <jni.h>
#include "Hybrid{{modulePascalCase}}SpecSwift.hpp"

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::{{cxxNamespace}}::initialize(vm);
}
