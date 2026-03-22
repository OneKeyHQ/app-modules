#include <jni.h>
#include <jsi/jsi.h>
#include <android/log.h>
#include <memory>
#include <mutex>
#include <string>

#include "SharedStore.h"
#include "SharedRPC.h"

#define LOG_TAG "BackgroundThread"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace jsi = facebook::jsi;

static JavaVM *gJavaVM = nullptr;
static jobject gManagerRef = nullptr;
static std::mutex gCallbackMutex;
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

// Stub a JSI function on an object (replaces it with a no-op).
static void stubJsiFunction(jsi::Runtime &runtime, jsi::Object &object, const char *name) {
    object.setProperty(
        runtime,
        name,
        jsi::Function::createFromHostFunction(
            runtime, jsi::PropNameID::forUtf8(runtime, name), 1,
            [](auto &, const auto &, const auto *, size_t) {
                return jsi::Value::undefined();
            }));
}

// ── nativeInstallBgBindings ─────────────────────────────────────────────
// Install postHostMessage / onHostMessage JSI globals into the background runtime.
// Also stores a global ref to the Java manager for callbacks.
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadManager_nativeInstallBgBindings(
    JNIEnv *env, jobject thiz, jlong runtimePtr) {

    auto *runtime = reinterpret_cast<jsi::Runtime *>(runtimePtr);
    if (!runtime) return;

    // Store global ref to manager for JNI callbacks
    if (gManagerRef) {
        env->DeleteGlobalRef(gManagerRef);
    }
    gManagerRef = env->NewGlobalRef(thiz);

    // Reset previous callback
    {
        std::lock_guard<std::mutex> lock(gCallbackMutex);
        gBgOnMessageCallback.reset();
    }

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
            if (env && gManagerRef) {
                jclass cls = env->GetObjectClass(gManagerRef);
                jmethodID mid = env->GetMethodID(
                    cls, "onBgMessage", "(Ljava/lang/String;)V");
                if (mid) {
                    jstring jmsg = env->NewStringUTF(msg.c_str());
                    env->CallVoidMethod(gManagerRef, mid, jmsg);
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
            {
                std::lock_guard<std::mutex> lock(gCallbackMutex);
                gBgOnMessageCallback = std::make_shared<jsi::Function>(
                    args[0].asObject(rt).asFunction(rt));
            }
            return jsi::Value::undefined();
        });

    runtime->global().setProperty(
        *runtime, "onHostMessage", std::move(onHostMessage));

    LOGI("JSI bindings (postHostMessage/onHostMessage) installed in background runtime");
}

// ── nativePostToBackground ──────────────────────────────────────────────
// Post a message from main to background JS.
// Must be called on the background JS thread.
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadManager_nativePostToBackground(
    JNIEnv *env, jobject thiz, jlong runtimePtr, jstring message) {

    std::shared_ptr<jsi::Function> callback;
    {
        std::lock_guard<std::mutex> lock(gCallbackMutex);
        callback = gBgOnMessageCallback;
    }
    if (!callback || runtimePtr == 0) return;

    auto *runtime = reinterpret_cast<jsi::Runtime *>(runtimePtr);
    const char *msgChars = env->GetStringUTFChars(message, nullptr);
    std::string msg(msgChars);
    env->ReleaseStringUTFChars(message, msgChars);

    try {
        auto parsedValue = runtime->global()
            .getPropertyAsObject(*runtime, "JSON")
            .getPropertyAsFunction(*runtime, "parse")
            .call(*runtime, jsi::String::createFromUtf8(*runtime, msg));

        callback->call(*runtime, {std::move(parsedValue)});
    } catch (const jsi::JSError &e) {
        LOGE("JSError in postToBackground: %s", e.getMessage().c_str());
    } catch (const std::exception &e) {
        LOGE("Error in postToBackground: %s", e.what());
    }
}

// ── nativeInstallSharedBridge ───────────────────────────────────────────
// Install SharedStore and SharedRPC into a runtime.
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadManager_nativeInstallSharedBridge(
    JNIEnv *env, jobject thiz, jlong runtimePtr, jboolean isMain) {

    auto *rt = reinterpret_cast<jsi::Runtime *>(runtimePtr);
    if (!rt) return;

    SharedStore::install(*rt);
    SharedRPC::install(*rt);
    LOGI("SharedStore and SharedRPC installed (isMain=%d)", static_cast<int>(isMain));
}

// ── nativeSetupErrorHandler ─────────────────────────────────────────────
// Wrap the global error handler in the background runtime.
// Mirrors iOS BackgroundRunnerReactNativeDelegate.setupErrorHandler:.
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadManager_nativeSetupErrorHandler(
    JNIEnv *env, jobject thiz, jlong runtimePtr) {

    auto *runtime = reinterpret_cast<jsi::Runtime *>(runtimePtr);
    if (!runtime) return;

    try {
        jsi::Object global = runtime->global();
        jsi::Value errorUtilsVal = global.getProperty(*runtime, "ErrorUtils");
        if (!errorUtilsVal.isObject()) {
            LOGE("ErrorUtils is not available on global object");
            return;
        }

        jsi::Object errorUtils = errorUtilsVal.asObject(*runtime);

        // Capture the current global error handler
        auto originalHandler = std::make_shared<jsi::Value>(
            errorUtils.getProperty(*runtime, "getGlobalHandler")
                .asObject(*runtime).asFunction(*runtime).call(*runtime));

        // Create a custom handler that delegates to the original
        auto handlerFunc = jsi::Function::createFromHostFunction(
            *runtime,
            jsi::PropNameID::forAscii(*runtime, "customGlobalErrorHandler"),
            2,
            [originalHandler](
                jsi::Runtime &rt, const jsi::Value &,
                const jsi::Value *args, size_t count) -> jsi::Value {
                if (count < 2) {
                    return jsi::Value::undefined();
                }

                if (originalHandler->isObject() &&
                    originalHandler->asObject(rt).isFunction(rt)) {
                    jsi::Function original =
                        originalHandler->asObject(rt).asFunction(rt);
                    original.call(rt, args, count);
                }

                return jsi::Value::undefined();
            });

        // Set the new global error handler
        jsi::Function setHandler =
            errorUtils.getProperty(*runtime, "setGlobalHandler")
                .asObject(*runtime).asFunction(*runtime);
        setHandler.call(*runtime, {std::move(handlerFunc)});

        // Disable further setGlobalHandler from background JS
        stubJsiFunction(*runtime, errorUtils, "setGlobalHandler");

        LOGI("Error handler installed in background runtime");
    } catch (const jsi::JSError &e) {
        LOGE("JSError setting up error handler: %s", e.getMessage().c_str());
    } catch (const std::exception &e) {
        LOGE("Error setting up error handler: %s", e.what());
    }
}

// ── nativeDestroy ───────────────────────────────────────────────────────
// Clean up JNI global references and callbacks.
// Called from BackgroundThreadManager.destroy().
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadManager_nativeDestroy(
    JNIEnv *env, jobject thiz) {

    {
        std::lock_guard<std::mutex> lock(gCallbackMutex);
        gBgOnMessageCallback.reset();
    }

    if (gManagerRef) {
        env->DeleteGlobalRef(gManagerRef);
        gManagerRef = nullptr;
    }

    LOGI("Native resources cleaned up");
}
