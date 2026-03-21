#include <jni.h>
#include <jsi/jsi.h>
#include <android/log.h>
#include <memory>
#include <string>

#include "SharedBridge.h"

#define LOG_TAG "BackgroundThread"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace jsi = facebook::jsi;

static JavaVM *gJavaVM = nullptr;
static jobject gModuleRef = nullptr;
static std::shared_ptr<jsi::Function> gBgOnMessageCallback;

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {
    gJavaVM = vm;
    return JNI_VERSION_1_6;
}

static JNIEnv *getJNIEnv() {
    JNIEnv *env = nullptr;
    if (gJavaVM) {
        gJavaVM->AttachCurrentThread(&env, nullptr);
    }
    return env;
}

// Install postHostMessage / onHostMessage JSI globals into the background runtime.
// Also stores a global ref to the Java module for callbacks.
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadModule_nativeInstallBgBindings(
    JNIEnv *env, jobject thiz, jlong runtimePtr) {

    auto *runtime = reinterpret_cast<jsi::Runtime *>(runtimePtr);
    if (!runtime) return;

    // Store global ref to module for JNI callbacks
    if (gModuleRef) {
        env->DeleteGlobalRef(gModuleRef);
    }
    gModuleRef = env->NewGlobalRef(thiz);

    // Reset previous callback
    gBgOnMessageCallback.reset();

    // postHostMessage(message: string) — background JS calls this to send to main
    auto postHostMessage = jsi::Function::createFromHostFunction(
        *runtime,
        jsi::PropNameID::forAscii(*runtime, "postHostMessage"),
        1,
        [](jsi::Runtime &rt, const jsi::Value &,
           const jsi::Value *args, size_t count) -> jsi::Value {
            if (count < 1 || !args[0].isString()) {
                throw jsi::JSError(rt, "postHostMessage expects a string argument");
            }
            std::string msg = args[0].getString(rt).utf8(rt);

            JNIEnv *env = getJNIEnv();
            if (env && gModuleRef) {
                jclass cls = env->GetObjectClass(gModuleRef);
                jmethodID mid = env->GetMethodID(
                    cls, "onBgMessage", "(Ljava/lang/String;)V");
                if (mid) {
                    jstring jmsg = env->NewStringUTF(msg.c_str());
                    env->CallVoidMethod(gModuleRef, mid, jmsg);
                    env->DeleteLocalRef(jmsg);
                }
                env->DeleteLocalRef(cls);
            }
            return jsi::Value::undefined();
        });

    runtime->global().setProperty(
        *runtime, "postHostMessage", std::move(postHostMessage));

    // onHostMessage(callback: function) — background JS registers a message handler
    auto onHostMessage = jsi::Function::createFromHostFunction(
        *runtime,
        jsi::PropNameID::forAscii(*runtime, "onHostMessage"),
        1,
        [](jsi::Runtime &rt, const jsi::Value &,
           const jsi::Value *args, size_t count) -> jsi::Value {
            if (count < 1 || !args[0].isObject() ||
                !args[0].asObject(rt).isFunction(rt)) {
                throw jsi::JSError(rt, "onHostMessage expects a function");
            }
            gBgOnMessageCallback = std::make_shared<jsi::Function>(
                args[0].asObject(rt).asFunction(rt));
            return jsi::Value::undefined();
        });

    runtime->global().setProperty(
        *runtime, "onHostMessage", std::move(onHostMessage));

    LOGI("JSI bindings (postHostMessage/onHostMessage) installed in background runtime");
}

// Post a message from main to background JS.
// Must be called on the background JS thread.
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadModule_nativePostToBackground(
    JNIEnv *env, jobject thiz, jlong runtimePtr, jstring message) {

    if (!gBgOnMessageCallback || runtimePtr == 0) return;

    auto *runtime = reinterpret_cast<jsi::Runtime *>(runtimePtr);
    const char *msgChars = env->GetStringUTFChars(message, nullptr);
    std::string msg(msgChars);
    env->ReleaseStringUTFChars(message, msgChars);

    try {
        auto parsedValue = runtime->global()
            .getPropertyAsObject(*runtime, "JSON")
            .getPropertyAsFunction(*runtime, "parse")
            .call(*runtime, jsi::String::createFromUtf8(*runtime, msg));

        gBgOnMessageCallback->call(*runtime, {std::move(parsedValue)});
    } catch (const jsi::JSError &e) {
        LOGE("JSError in postToBackground: %s", e.getMessage().c_str());
    } catch (const std::exception &e) {
        LOGE("Error in postToBackground: %s", e.what());
    }
}

// Install SharedBridge HostObject into a runtime.
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadModule_nativeInstallSharedBridge(
    JNIEnv *env, jobject thiz, jlong runtimePtr, jboolean isMain) {

    auto *rt = reinterpret_cast<jsi::Runtime *>(runtimePtr);
    if (!rt) return;

    SharedBridge::install(*rt, static_cast<bool>(isMain));
    LOGI("SharedBridge installed (isMain=%d)", static_cast<int>(isMain));
}
