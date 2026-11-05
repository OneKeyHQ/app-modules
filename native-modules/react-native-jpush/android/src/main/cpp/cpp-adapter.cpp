#include <jni.h>
#include <fbjni/fbjni.h>
#include "reactnativejpushOnLoad.hpp"
#include "JHybridReactNativeJpushSpec.hpp"

namespace margelo::nitro::reactnativejpush {
class OwnedReactNativeJpush final : public JHybridReactNativeJpushSpec {
 public:
  explicit OwnedReactNativeJpush(const jni::local_ref<JavaPart>& javaPart)
      : HybridObject(HybridReactNativeJpushSpec::TAG), JHybridObject(javaPart), JHybridReactNativeJpushSpec(javaPart) {}
  ~OwnedReactNativeJpush() override {
    try {
      jni::ThreadScope::WithClassLoader([&] {
        const auto& javaPart = getJavaPart();
        const auto release = javaPart->getClass()->getMethod<void()>("releaseOwnedListeners");
        release(javaPart);
      });
    } catch (...) {
      // Runtime teardown cannot propagate Java cleanup failures.
    }
  }
};
class OwnedCxxPart final : public jni::HybridClass<OwnedCxxPart, JHybridReactNativeJpushSpec::CxxPart> {
 public:
  static constexpr auto kJavaDescriptor = "Lcom/margelo/nitro/reactnativejpush/ReactNativeJpush$OwnedCxxPart;";
  using HybridBase::HybridBase;
  static jni::local_ref<jhybriddata> initHybrid(jni::alias_ref<jhybridobject> self) {
    return makeCxxInstance(self);
  }
  static void registerNatives() {
    registerHybrid({makeNativeMethod("initHybrid", OwnedCxxPart::initHybrid)});
  }
 protected:
  std::shared_ptr<JHybridObject> createHybridObject(const jni::local_ref<JHybridObject::JavaPart>& javaPart) override {
    auto typed = jni::dynamic_ref_cast<JHybridReactNativeJpushSpec::JavaPart>(javaPart);
    return std::make_shared<OwnedReactNativeJpush>(typed);
  }
};
}

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return facebook::jni::initialize(vm, [] {
    margelo::nitro::reactnativejpush::registerAllNatives();
    margelo::nitro::reactnativejpush::OwnedCxxPart::registerNatives();
  });
}
