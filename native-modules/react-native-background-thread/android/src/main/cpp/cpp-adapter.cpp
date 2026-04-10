#include <jni.h>
#include <jsi/jsi.h>
#include <android/log.h>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <deque>
#include <functional>
#include <memory>
#include <mutex>
#include <string>
#include <thread>
#include <unordered_map>
#include <unordered_set>

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

// ── Timer support for background runtime ──────────────────────────────
// The background Hermes runtime does NOT have working setTimeout/setInterval
// out of the box (RN's timer module only wires into the main runtime). We
// install our own JSI-level setTimeout/setInterval/clearTimeout/clearInterval
// backed by a single C++ worker thread that dispatches callbacks back to the
// background JS queue via the same executor used by SharedRPC.

struct TimerEntry {
    std::shared_ptr<jsi::Function> callback;
    long long fireAtMs;      // Absolute time in ms when the timer should fire.
    long long intervalMs;    // 0 if one-shot, >0 if setInterval period.
    bool cancelled;
};

static std::mutex gTimerMutex;
static std::condition_variable gTimerCv;
static std::unordered_map<int64_t, TimerEntry> gTimers;
static std::atomic<int64_t> gNextTimerId{1};
static std::atomic<bool> gTimerWorkerStarted{false};
static std::atomic<bool> gTimerWorkerStop{false};
static RPCRuntimeExecutor gBgTimerExecutor;

static long long nowMs() {
    using namespace std::chrono;
    return duration_cast<milliseconds>(
        steady_clock::now().time_since_epoch())
        .count();
}

// Called on the bg JS thread. Executes the callback only; the worker has
// already erased (one-shot) or rescheduled (interval) the timer under lock.
static void fireTimerOnJsThread(
    int64_t timerId,
    std::shared_ptr<jsi::Function> cb,
    jsi::Runtime &rt) {
    if (!cb) return;
    try {
        cb->call(rt);
    } catch (const jsi::JSError &e) {
        LOGE("Timer %lld callback JSError: %s", (long long)timerId,
             e.getMessage().c_str());
    } catch (const std::exception &e) {
        LOGE("Timer %lld callback error: %s", (long long)timerId, e.what());
    }
}

static void timerWorkerLoop() {
    while (!gTimerWorkerStop.load()) {
        // Snapshot of timers that should be dispatched this iteration and
        // their callbacks. Captured under the lock; callbacks are invoked on
        // the JS thread (not here).
        std::vector<std::pair<int64_t, std::shared_ptr<jsi::Function>>> toFire;
        {
            std::unique_lock<std::mutex> lock(gTimerMutex);
            if (gTimers.empty()) {
                gTimerCv.wait(lock, [] {
                    return gTimerWorkerStop.load() || !gTimers.empty();
                });
                if (gTimerWorkerStop.load()) return;
                continue;
            }

            // Find the earliest fireAt among non-cancelled timers.
            long long earliest = LLONG_MAX;
            for (auto &kv : gTimers) {
                if (!kv.second.cancelled && kv.second.fireAtMs < earliest) {
                    earliest = kv.second.fireAtMs;
                }
            }
            long long now = nowMs();
            if (earliest == LLONG_MAX) {
                // Only cancelled timers remain; clean them up.
                for (auto it = gTimers.begin(); it != gTimers.end();) {
                    if (it->second.cancelled) it = gTimers.erase(it);
                    else ++it;
                }
                continue;
            }
            if (earliest > now) {
                gTimerCv.wait_for(
                    lock, std::chrono::milliseconds(earliest - now));
                continue;
            }

            // Collect ready timers AND either erase (one-shot) or reschedule
            // (interval) them RIGHT HERE under the lock. This is critical:
            // if we wait to erase in fireTimerOnJsThread, the next worker
            // iteration would immediately find the same timers still
            // in-queue and re-dispatch them, causing an infinite flood of
            // `scheduleOnJSThread` calls.
            for (auto it = gTimers.begin(); it != gTimers.end();) {
                if (it->second.cancelled) {
                    it = gTimers.erase(it);
                    continue;
                }
                if (it->second.fireAtMs <= now) {
                    toFire.emplace_back(it->first, it->second.callback);
                    if (it->second.intervalMs > 0) {
                        // Reschedule interval. Use `now + intervalMs` rather
                        // than `fireAtMs + intervalMs` so a slow fire path
                        // cannot produce an infinite backlog.
                        it->second.fireAtMs = now + it->second.intervalMs;
                        ++it;
                    } else {
                        it = gTimers.erase(it);
                    }
                } else {
                    ++it;
                }
            }
        }

        RPCRuntimeExecutor executor = gBgTimerExecutor;
        if (!executor) {
            std::this_thread::sleep_for(std::chrono::milliseconds(10));
            continue;
        }
        for (auto &pair : toFire) {
            int64_t id = pair.first;
            std::shared_ptr<jsi::Function> cb = pair.second;
            executor([id, cb](jsi::Runtime &rt) {
                fireTimerOnJsThread(id, cb, rt);
            });
        }
    }
}

static void ensureTimerWorkerStarted() {
    bool expected = false;
    if (gTimerWorkerStarted.compare_exchange_strong(expected, true)) {
        std::thread(timerWorkerLoop).detach();
        LOGI("Timer worker thread started");
    }
}

static int64_t scheduleTimer(
    std::shared_ptr<jsi::Function> cb,
    double ms,
    bool isInterval) {
    int64_t id = gNextTimerId.fetch_add(1);
    long long intervalMs = isInterval ? static_cast<long long>(ms) : 0;
    long long delay = static_cast<long long>(ms);
    if (delay < 0) delay = 0;
    {
        std::lock_guard<std::mutex> lock(gTimerMutex);
        gTimers[id] = TimerEntry{
            std::move(cb),
            nowMs() + delay,
            intervalMs,
            false,
        };
    }
    gTimerCv.notify_all();
    ensureTimerWorkerStarted();
    return id;
}

static void cancelTimer(int64_t id) {
    {
        std::lock_guard<std::mutex> lock(gTimerMutex);
        auto it = gTimers.find(id);
        if (it != gTimers.end()) {
            it->second.cancelled = true;
        }
    }
    gTimerCv.notify_all();
}

static void installTimersOnRuntime(jsi::Runtime &rt) {
    auto makeSetter = [](bool isInterval) {
        return [isInterval](
                   jsi::Runtime &rt,
                   const jsi::Value &,
                   const jsi::Value *args,
                   size_t count) -> jsi::Value {
            if (count < 1 || !args[0].isObject() ||
                !args[0].getObject(rt).isFunction(rt)) {
                return jsi::Value::undefined();
            }
            auto cb = std::make_shared<jsi::Function>(
                args[0].getObject(rt).getFunction(rt));
            double ms = 0;
            if (count >= 2 && args[1].isNumber()) {
                ms = args[1].asNumber();
            }
            int64_t id = scheduleTimer(std::move(cb), ms, isInterval);
            return jsi::Value(static_cast<double>(id));
        };
    };
    auto makeCanceller = []() {
        return [](jsi::Runtime &rt,
                  const jsi::Value &,
                  const jsi::Value *args,
                  size_t count) -> jsi::Value {
            if (count < 1 || !args[0].isNumber()) {
                return jsi::Value::undefined();
            }
            int64_t id = static_cast<int64_t>(args[0].asNumber());
            cancelTimer(id);
            return jsi::Value::undefined();
        };
    };

    // requestAnimationFrame(cb): fires after ~16ms (60fps) with high-resolution
    // timestamp arg, matching the DOM contract. Background runtime has no
    // rendering concept, so we just approximate via setTimeout(16ms).
    auto rafFn = [](jsi::Runtime &rt,
                     const jsi::Value &,
                     const jsi::Value *args,
                     size_t count) -> jsi::Value {
        if (count < 1 || !args[0].isObject() ||
            !args[0].getObject(rt).isFunction(rt)) {
            return jsi::Value::undefined();
        }
        // Wrap callback so it receives a DOMHighResTimeStamp-like arg.
        auto userCb = std::make_shared<jsi::Function>(
            args[0].getObject(rt).getFunction(rt));
        auto wrapper = jsi::Function::createFromHostFunction(
            rt,
            jsi::PropNameID::forAscii(rt, "rafWrapper"),
            0,
            [userCb](jsi::Runtime &rt2,
                     const jsi::Value &,
                     const jsi::Value *,
                     size_t) -> jsi::Value {
                try {
                    userCb->call(rt2, jsi::Value(static_cast<double>(nowMs())));
                } catch (const jsi::JSError &e) {
                    LOGE("rAF callback JSError: %s", e.getMessage().c_str());
                } catch (const std::exception &e) {
                    LOGE("rAF callback error: %s", e.what());
                }
                return jsi::Value::undefined();
            });
        auto wrappedCb = std::make_shared<jsi::Function>(std::move(wrapper));
        int64_t id = scheduleTimer(std::move(wrappedCb), 16.0, false);
        return jsi::Value(static_cast<double>(id));
    };

    // requestIdleCallback(cb, {timeout?}): fires "soon" with an IdleDeadline-ish
    // object. Background runtime has no render frames to be idle between, so
    // we approximate via setTimeout(1ms) and provide a deadline stub whose
    // timeRemaining() always returns 50 (reasonable budget).
    auto ricFn = [](jsi::Runtime &rt,
                     const jsi::Value &,
                     const jsi::Value *args,
                     size_t count) -> jsi::Value {
        if (count < 1 || !args[0].isObject() ||
            !args[0].getObject(rt).isFunction(rt)) {
            return jsi::Value::undefined();
        }
        auto userCb = std::make_shared<jsi::Function>(
            args[0].getObject(rt).getFunction(rt));
        auto wrapper = jsi::Function::createFromHostFunction(
            rt,
            jsi::PropNameID::forAscii(rt, "ricWrapper"),
            0,
            [userCb](jsi::Runtime &rt2,
                     const jsi::Value &,
                     const jsi::Value *,
                     size_t) -> jsi::Value {
                try {
                    // Build a minimal IdleDeadline: { didTimeout: false,
                    // timeRemaining: () => 50 }.
                    jsi::Object deadline(rt2);
                    deadline.setProperty(rt2, "didTimeout", jsi::Value(false));
                    deadline.setProperty(
                        rt2,
                        "timeRemaining",
                        jsi::Function::createFromHostFunction(
                            rt2,
                            jsi::PropNameID::forAscii(rt2, "timeRemaining"),
                            0,
                            [](jsi::Runtime &,
                               const jsi::Value &,
                               const jsi::Value *,
                               size_t) -> jsi::Value {
                                return jsi::Value(50.0);
                            }));
                    userCb->call(rt2, jsi::Value(rt2, std::move(deadline)));
                } catch (const jsi::JSError &e) {
                    LOGE("rIC callback JSError: %s", e.getMessage().c_str());
                } catch (const std::exception &e) {
                    LOGE("rIC callback error: %s", e.what());
                }
                return jsi::Value::undefined();
            });
        auto wrappedCb = std::make_shared<jsi::Function>(std::move(wrapper));
        int64_t id = scheduleTimer(std::move(wrappedCb), 1.0, false);
        return jsi::Value(static_cast<double>(id));
    };

    auto global = rt.global();
    global.setProperty(
        rt, "setTimeout",
        jsi::Function::createFromHostFunction(
            rt, jsi::PropNameID::forAscii(rt, "setTimeout"), 2,
            makeSetter(false)));
    global.setProperty(
        rt, "setInterval",
        jsi::Function::createFromHostFunction(
            rt, jsi::PropNameID::forAscii(rt, "setInterval"), 2,
            makeSetter(true)));
    global.setProperty(
        rt, "clearTimeout",
        jsi::Function::createFromHostFunction(
            rt, jsi::PropNameID::forAscii(rt, "clearTimeout"), 1,
            makeCanceller()));
    global.setProperty(
        rt, "clearInterval",
        jsi::Function::createFromHostFunction(
            rt, jsi::PropNameID::forAscii(rt, "clearInterval"), 1,
            makeCanceller()));
    global.setProperty(
        rt, "requestAnimationFrame",
        jsi::Function::createFromHostFunction(
            rt, jsi::PropNameID::forAscii(rt, "requestAnimationFrame"), 1,
            rafFn));
    global.setProperty(
        rt, "cancelAnimationFrame",
        jsi::Function::createFromHostFunction(
            rt, jsi::PropNameID::forAscii(rt, "cancelAnimationFrame"), 1,
            makeCanceller()));
    global.setProperty(
        rt, "requestIdleCallback",
        jsi::Function::createFromHostFunction(
            rt, jsi::PropNameID::forAscii(rt, "requestIdleCallback"), 1,
            ricFn));
    global.setProperty(
        rt, "cancelIdleCallback",
        jsi::Function::createFromHostFunction(
            rt, jsi::PropNameID::forAscii(rt, "cancelIdleCallback"), 1,
            makeCanceller()));
    LOGI("Timer + rAF + rIC polyfills installed on bg runtime");
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
    // Save the bg executor so our custom timer worker can dispatch callbacks
    // back to the bg JS queue. We must do this BEFORE moving `executor` into
    // SharedRPC::install (which will std::move it out).
    if (!capturedIsMain) {
        gBgTimerExecutor = executor;
    }
    SharedRPC::install(*rt, std::move(executor), runtimeId);
    LOGI("SharedStore and SharedRPC installed (isMain=%d)", static_cast<int>(isMain));
    if (!capturedIsMain) {
        // Install setTimeout/setInterval/clearTimeout/clearInterval on the
        // background runtime. React Native's built-in timer module only wires
        // into the main runtime, so without this, any `await wait(ms)` or
        // setTimeout callback in the background thread would never fire.
        installTimersOnRuntime(*rt);
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
