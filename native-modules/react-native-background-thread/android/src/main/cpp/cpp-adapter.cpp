#include <jni.h>
#include <jsi/jsi.h>
#include <android/log.h>
#include <memory>
#include <mutex>
#include <string>
#include <unordered_map>

#include "SharedStore.h"
#include "SharedRPC.h"

#define LOG_TAG "BackgroundThread"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

namespace jsi = facebook::jsi;

static JavaVM *gJavaVM = nullptr;

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

static void invokeOptionalGlobalFunction(jsi::Runtime &runtime, const char *name) {
    try {
        auto fnValue = runtime.global().getProperty(runtime, name);
        if (!fnValue.isObject() || !fnValue.asObject(runtime).isFunction(runtime)) {
            return;
        }

        auto fn = fnValue.asObject(runtime).asFunction(runtime);
        fn.call(runtime);
    } catch (const jsi::JSError &e) {
        LOGE("JSError calling global function %s: %s", name, e.getMessage().c_str());
    } catch (const std::exception &e) {
        LOGE("Error calling global function %s: %s", name, e.what());
    }
}

// ── Pending work map for cross-runtime executor ───────────────────────
static std::mutex gWorkMutex;
static std::unordered_map<int64_t, std::function<void(jsi::Runtime &)>> gPendingWork;
static int64_t gNextWorkId = 0;

// Called from Kotlin after runOnJSQueueThread dispatches to the correct thread.
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadManager_nativeExecuteWork(
    JNIEnv *env, jobject thiz, jlong runtimePtr, jlong workId) {
    LOGI("nativeExecuteWork: runtimePtr=%ld, workId=%ld", (long)runtimePtr, (long)workId);
    auto *rt = reinterpret_cast<jsi::Runtime *>(runtimePtr);
    if (!rt) return;

    std::function<void(jsi::Runtime &)> work;
    {
        std::lock_guard<std::mutex> lock(gWorkMutex);
        auto it = gPendingWork.find(workId);
        if (it == gPendingWork.end()) return;
        work = std::move(it->second);
        gPendingWork.erase(it);
    }
    try {
        work(*rt);
    } catch (const jsi::JSError &e) {
        LOGE("JSError in nativeExecuteWork: %s", e.getMessage().c_str());
    } catch (const std::exception &e) {
        LOGE("Error in nativeExecuteWork: %s", e.what());
    }

    // CRITICAL: Drain the Hermes microtask queue. React Native 0.74+ configures
    // Hermes with an explicit microtask queue, which must be manually drained
    // after each JS execution. Without this, Promise.then() / async-await
    // continuations (including already-resolved promises) are never executed,
    // causing all awaits to hang forever in the background runtime.
    try {
        rt->drainMicrotasks();
    } catch (const jsi::JSError &e) {
        LOGE("JSError draining microtasks: %s", e.getMessage().c_str());
    } catch (const std::exception &e) {
        LOGE("Error draining microtasks: %s", e.what());
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

    // Create executor that schedules work on this runtime's JS thread via Kotlin.
    // Wrap GlobalRef in shared_ptr so it is automatically released when all
    // copies of the executor lambda are destroyed (e.g. on runtime reload).
    auto ref = std::shared_ptr<_jobject>(env->NewGlobalRef(thiz), [](jobject r) {
        if (r) {
            JNIEnv *e = getJNIEnv();
            if (e) e->DeleteGlobalRef(r);
        }
    });
    bool capturedIsMain = static_cast<bool>(isMain);

    RPCRuntimeExecutor executor = [ref, capturedIsMain](std::function<void(jsi::Runtime &)> work) {
        JNIEnv *env = getJNIEnv();
        if (!env || !ref) {
            LOGE("executor: env=%p, ref=%p — aborting", env, ref.get());
            return;
        }

        int64_t workId;
        {
            std::lock_guard<std::mutex> lock(gWorkMutex);
            workId = gNextWorkId++;
            gPendingWork[workId] = std::move(work);
        }

        jclass cls = env->GetObjectClass(ref.get());
        jmethodID mid = env->GetMethodID(cls, "scheduleOnJSThread", "(ZJ)V");
        if (mid) {
            LOGI("executor: calling scheduleOnJSThread(isMain=%d, workId=%ld)", capturedIsMain, (long)workId);
            env->CallVoidMethod(ref.get(), mid, static_cast<jboolean>(capturedIsMain), static_cast<jlong>(workId));
            if (env->ExceptionCheck()) {
                LOGE("executor: JNI exception after scheduleOnJSThread");
                env->ExceptionDescribe();
                env->ExceptionClear();
            }
        } else {
            LOGE("executor: scheduleOnJSThread method not found!");
            if (env->ExceptionCheck()) {
                env->ExceptionDescribe();
                env->ExceptionClear();
            }
        }
        env->DeleteLocalRef(cls);
    };

    std::string runtimeId = isMain ? "main" : "background";
    SharedRPC::install(*rt, std::move(executor), runtimeId);
    LOGI("SharedStore and SharedRPC installed (isMain=%d)", static_cast<int>(isMain));
    if (!capturedIsMain) {
        invokeOptionalGlobalFunction(*rt, "__setupBackgroundRPCHandler");
    }
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
// Clean up native resources.
// Called from BackgroundThreadManager.destroy().
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadManager_nativeDestroy(
    JNIEnv *env, jobject thiz) {

    SharedRPC::reset();
    SharedStore::reset();

    LOGI("Native resources cleaned up");
}
