#include <jni.h>
#include <jsi/jsi.h>
#include <android/log.h>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <cstdio>
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

using JavaObjectRef = std::shared_ptr<_jobject>;

static constexpr size_t kRuntimeDrainBatchSize = 64;
static constexpr size_t kRuntimeQueueWarnThreshold = 128;
static constexpr size_t kRuntimeQueueWarnInterval = 128;

struct RuntimeWorkQueue {
    std::deque<std::function<void(jsi::Runtime &)>> items;
    bool drainScheduled = false;
    // workId of the drain currently posted to gPendingWork (valid only while
    // drainScheduled == true). Tracked so a teardown path (nativeInvalidate
    // SharedRpc) can erase the orphaned gPendingWork entry if its posted drain
    // is dropped during reload. -1 means "none outstanding".
    int64_t scheduledDrainWorkId = -1;
};

static RuntimeWorkQueue gMainRuntimeWorkQueue;
static RuntimeWorkQueue gBgRuntimeWorkQueue;

static RuntimeWorkQueue &getRuntimeWorkQueue(bool isMain) {
    return isMain ? gMainRuntimeWorkQueue : gBgRuntimeWorkQueue;
}

// Caller MUST hold gWorkMutex. Intentionally leak (abandon) each queued functor
// — its ~jsi::Function must not run off the JS thread / on a dead runtime — then
// clear the queue and disarm the drain latch so a recovered runtime re-arms a
// fresh drain on the next enqueue instead of stranding work behind a stale
// drainScheduled==true.
static void leakAndClearRuntimeQueue(RuntimeWorkQueue &queue) {
    for (auto &work : queue.items) {
        new std::function<void(jsi::Runtime &)>(std::move(work));
    }
    queue.items.clear();
    queue.drainScheduled = false;
    queue.scheduledDrainWorkId = -1;
}

static bool callScheduleOnJSThread(const JavaObjectRef &ref, bool isMain, int64_t workId) {
    JNIEnv *env = getJNIEnv();
    if (!env || !ref) {
        LOGE("executor: env=%p, ref=%p — aborting", env, ref.get());
        return false;
    }

    jclass cls = env->GetObjectClass(ref.get());
    if (!cls) {
        LOGE("executor: GetObjectClass failed");
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
        return false;
    }

    jmethodID mid = env->GetMethodID(cls, "scheduleOnJSThread", "(ZJ)Z");
    if (!mid) {
        LOGE("executor: scheduleOnJSThread method not found!");
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
        env->DeleteLocalRef(cls);
        return false;
    }

    LOGI("executor: calling scheduleOnJSThread(isMain=%d, workId=%ld)", isMain, (long)workId);
    jboolean scheduled = env->CallBooleanMethod(
        ref.get(),
        mid,
        static_cast<jboolean>(isMain),
        static_cast<jlong>(workId));
    if (env->ExceptionCheck()) {
        LOGE("executor: JNI exception after scheduleOnJSThread");
        env->ExceptionDescribe();
        env->ExceptionClear();
        env->DeleteLocalRef(cls);
        return false;
    }
    env->DeleteLocalRef(cls);
    return scheduled == JNI_TRUE;
}

static void drainPendingBgEvals(const std::string &reason);

static void scheduleRuntimeDrain(const JavaObjectRef &ref, bool isMain);

static void drainRuntimeWorkQueue(jsi::Runtime &rt, JavaObjectRef ref, bool isMain) {
    size_t drained = 0;

    while (drained < kRuntimeDrainBatchSize) {
        std::function<void(jsi::Runtime &)> work;
        {
            std::lock_guard<std::mutex> lock(gWorkMutex);
            auto &queue = getRuntimeWorkQueue(isMain);
            if (queue.items.empty()) {
                break;
            }
            work = std::move(queue.items.front());
            queue.items.pop_front();
        }

        try {
            work(rt);
        } catch (const jsi::JSError &e) {
            LOGE("JSError in runtime drain work: %s", e.getMessage().c_str());
        } catch (const std::exception &e) {
            LOGE("Error in runtime drain work: %s", e.what());
        } catch (...) {
            LOGE("Unknown error in runtime drain work");
        }
        drained += 1;
    }

    bool shouldReschedule = false;
    size_t remaining = 0;
    {
        std::lock_guard<std::mutex> lock(gWorkMutex);
        auto &queue = getRuntimeWorkQueue(isMain);
        remaining = queue.items.size();
        if (remaining == 0) {
            queue.drainScheduled = false;
            // This drain's gPendingWork entry was already erased by
            // nativeExecuteWork before it ran; its workId is now stale.
            queue.scheduledDrainWorkId = -1;
        } else {
            shouldReschedule = true;
        }
    }

    if (drained > 1 || remaining > 0) {
        LOGI("executor: drained runtime queue isMain=%d, drained=%zu, remaining=%zu",
             isMain, drained, remaining);
    }

    if (shouldReschedule) {
        scheduleRuntimeDrain(ref, isMain);
    }
}

static void scheduleRuntimeDrain(const JavaObjectRef &ref, bool isMain) {
    int64_t workId;
    size_t queued = 0;
    {
        std::lock_guard<std::mutex> lock(gWorkMutex);
        auto &queue = getRuntimeWorkQueue(isMain);
        // Stale-id guard: drainRuntimeWorkQueue observes remaining>0, drops the
        // lock, then calls us — but a concurrent nativeInvalidateSharedRpc can
        // clear the queue + latch in between. If the queue is now empty there is
        // nothing to drain: do NOT post a gPendingWork entry / scheduleOnJSThread
        // for an already-drained/invalidated queue. Just disarm the latch and
        // return. The normal enqueue→schedule path always has items.size()>=1, so
        // this never short-circuits it.
        if (queue.items.empty()) {
            queue.drainScheduled = false;
            queue.scheduledDrainWorkId = -1;
            return;
        }
        workId = gNextWorkId++;
        queued = queue.items.size();
        // Track the outstanding drain's workId so a teardown path can erase its
        // orphaned gPendingWork entry if the post is dropped during reload.
        queue.scheduledDrainWorkId = workId;
        gPendingWork[workId] = [ref, isMain](jsi::Runtime &rt) {
            drainRuntimeWorkQueue(rt, ref, isMain);
        };
    }

    bool scheduled = callScheduleOnJSThread(ref, isMain, workId);
    if (!scheduled) {
        {
            std::lock_guard<std::mutex> lock(gWorkMutex);
            gPendingWork.erase(workId);
            leakAndClearRuntimeQueue(getRuntimeWorkQueue(isMain));
            LOGE("executor: failed to schedule runtime drain isMain=%d, workId=%ld, queued=%zu",
                 isMain, (long)workId, queued);
        }
        // The bg JS thread is unreachable, so the queued bg-eval lambdas will
        // never run. Settle any in-flight bg eval (retryable NO_RUNTIME) so its
        // JNI global ref is released and the JS promise resolves now instead of
        // hanging on the Kotlin 30s watchdog. Gated on !isMain — a main-thread
        // schedule hiccup must never falsely reject healthy bg evals. Called
        // outside gWorkMutex: drainPendingBgEvals takes gBgEvalMutex and does a
        // Java upcall, so it must not run under a native lock.
        if (!isMain) {
            drainPendingBgEvals("Background JS thread unreachable when scheduling segment eval");
        }
    }
}

static void enqueueRuntimeWork(JavaObjectRef ref, bool isMain, std::function<void(jsi::Runtime &)> work) {
    bool shouldSchedule = false;
    size_t queued = 0;
    {
        std::lock_guard<std::mutex> lock(gWorkMutex);
        auto &queue = getRuntimeWorkQueue(isMain);
        queue.items.push_back(std::move(work));
        queued = queue.items.size();
        if (!queue.drainScheduled) {
            queue.drainScheduled = true;
            shouldSchedule = true;
        }
    }

    if (queued >= kRuntimeQueueWarnThreshold && queued % kRuntimeQueueWarnInterval == 0) {
        LOGE("executor: runtime queue backlog isMain=%d, queued=%zu", isMain, queued);
    }

    if (shouldSchedule) {
        scheduleRuntimeDrain(ref, isMain);
    }
}

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
static std::thread gTimerWorkerThread;

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
        RPCRuntimeExecutor executor;
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
            executor = gBgTimerExecutor;
        }

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
        gTimerWorkerStop.store(false);
        gTimerWorkerThread = std::thread(timerWorkerLoop);
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

// ── Background segment eval (fix A: eval-then-resolve on the BG runtime) ──
//
// WHY THIS EXISTS — the "Requiring unknown module" race on the BACKGROUND
// runtime:
// The Kotlin BackgroundThreadManager.registerSegmentInBackground previously
// called `ReactContext.registerSegment(...)`, which (bridgeless) routes through
// ReactHostImpl.registerSegment → ReactInstance.registerSegment → C++
// ReactInstance::registerSegment, and that only `scheduleWork`s the
// evaluateJavaScript onto the RuntimeScheduler before returning. The Kotlin
// completion callback then fired immediately, so the JS promise resolved BEFORE
// the segment's `__d(...)` module definitions ran. Metro's
// `import().then(() => __r(moduleId))` could then run `__r` before the module
// table was populated → a fatal, uncatchable "Requiring unknown module". Locale
// segments load through THIS bg path, so a language switch could still crash.
//
// THE FIX (mirrors the MAIN-runtime SplitBundleLoaderJSI fix and the iOS
// callFunctionOnBufferedRuntimeExecutor: fix):
// We evaluate the segment OURSELVES on the BACKGROUND JS thread and signal
// completion in the SAME block, strictly AFTER eval. The accessor we use is the
// background runtime's own RuntimeExecutor (`gBgTimerExecutor`), captured in
// nativeInstallSharedBridge for the bg runtime (isMain=false). It dispatches via
// scheduleOnJSThread(isMain=false, ...) → nativeExecuteWork, which runs the work
// on the bg JS queue thread and then drainMicrotasks() — so this targets the
// BACKGROUND runtime, NOT the main one. This is the correct bg analogue of the
// main path's CallInvoker (the bg runtime is created by ReactHostImpl and the bg
// ReactContext does not surface a usable jsCallInvokerHolder the way the main
// one does, but its RuntimeExecutor already exists and routes to the bg JS
// thread).
//
// Off-thread read (fix F): the segment file is read on the CALLING (native
// module) thread before dispatch; only evaluateJavaScript + completion run on
// the bg JS thread.

// Java callback contract — implemented in Kotlin as
// BackgroundThreadManager.SegmentEvalCallback.onComplete(error). Empty/null
// error string → success. A message prefixed with "NO_RUNTIME:" → bg runtime
// not ready (retryable); "IO_ERROR:" → file read failure (fatal); otherwise →
// eval throw (fatal). Resolved exactly once on the Kotlin side via its watchdog
// guard.
static void invokeBgSegmentCallback(jobject globalCallback, const std::string &error) {
    JNIEnv *env = getJNIEnv();
    if (!env || !globalCallback) {
        return;
    }
    jclass cls = env->GetObjectClass(globalCallback);
    jmethodID mid =
        env->GetMethodID(cls, "onComplete", "(Ljava/lang/String;)V");
    if (mid) {
        jstring jerr = error.empty() ? nullptr : env->NewStringUTF(error.c_str());
        env->CallVoidMethod(globalCallback, mid, jerr);
        if (env->ExceptionCheck()) {
            LOGE("invokeBgSegmentCallback: JNI exception after onComplete");
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
        if (jerr) {
            env->DeleteLocalRef(jerr);
        }
    } else {
        LOGE("invokeBgSegmentCallback: onComplete method not found!");
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
        }
    }
    env->DeleteLocalRef(cls);
}

// ── Pending bg-eval callback registry (fix: bounded global-ref lifetime) ──
//
// The bg-eval work lambda captures a JNI global ref to the SegmentEvalCallback
// and is enqueued onto gPendingWork for the bg JS thread. If that work is
// enqueued but NEVER runs, the captured global ref would leak and the JS promise
// would settle only via the Kotlin 30s watchdog. The drop paths that release it:
//   - Java schedule fails (JNI exception / missing scheduleOnJSThread): the C++
//     executor erases gPendingWork[workId] and drains this eval.
//   - bg context==null / ptr==0 in scheduleOnJSThread: Kotlin calls
//     nativeDropScheduledWork(workId) — which erases the work AND drains —
//     BEFORE it would call nativeExecuteWork. (nativeExecuteWork's own
//     rt==nullptr guard merely returns and is NOT a drain path; it is never
//     reached for a dead ptr because Kotlin intercepts that case first.)
//   - nativeDestroy: intentionally leaks the work lambdas (their ~jsi::Function
//     can't run on a torn-down runtime) but drains this registry to settle them.
// To make this strictly bounded, every in-flight bg eval registers here. Whoever
// settles it first (the work lambda after eval, OR a drain on a drop path) claims
// it via `settled`, invokes the Java callback exactly once, and deletes the
// global ref. This guarantees no leak and no double-invoke.
struct PendingBgEval {
    jobject globalCallback;                  // owned: deleted by whoever settles
    std::shared_ptr<std::atomic<bool>> settled;  // exactly-once claim
};
static std::mutex gBgEvalMutex;
static std::unordered_map<int64_t, PendingBgEval> gPendingBgEvals;
static int64_t gNextBgEvalId = 0;

// Settle a pending bg eval exactly once: invoke the Java callback with `error`
// (empty => success) and release the global ref. The shared `settled` flag is
// the single source of truth for the one-shot — the registry entry may already
// be gone (claimed/erased by the other party), so this is self-contained.
static void settleBgEval(jobject globalCallback,
                         const std::shared_ptr<std::atomic<bool>> &settled,
                         const std::string &error) {
    if (!settled) {
        return;
    }
    bool expected = false;
    if (!settled->compare_exchange_strong(expected, true)) {
        // Already settled by the other party (lambda vs drain). Do nothing —
        // the winner already invoked + deleted the global ref.
        return;
    }
    invokeBgSegmentCallback(globalCallback, error);
    JNIEnv *env = getJNIEnv();
    if (env && globalCallback) {
        env->DeleteGlobalRef(globalCallback);
    }
}

// Settle ALL currently-registered bg evals with a retryable NO_RUNTIME-class
// failure and clear the registry. Used when the bg runtime is going away (or is
// unreachable) and any enqueued-but-unrun eval would otherwise leak its global
// ref and hang the JS promise until the Kotlin watchdog. Each settle is
// exactly-once (the work lambda may race us, but the shared flag arbitrates),
// so this never double-invokes.
static void drainPendingBgEvals(const std::string &reason) {
    // Move the entries OUT under the lock and clear the registry, then settle
    // OUTSIDE the lock. settleBgEval performs a Java upcall (onComplete via
    // CallVoidMethod), so holding gBgEvalMutex across it would hold a native
    // lock across arbitrary JS — a re-entrancy / deadlock hazard if a callback
    // ever synchronously re-enters a gBgEvalMutex-taking path. PendingBgEval is
    // copyable (jobject handle + shared_ptr); copies share the same global ref
    // and `settled` flag, and the shared flag still arbitrates exactly-once
    // against a racing work lambda, so the global ref is released exactly once.
    std::vector<PendingBgEval> drained;
    {
        std::lock_guard<std::mutex> lock(gBgEvalMutex);
        if (gPendingBgEvals.empty()) {
            return;
        }
        LOGE("[SplitBundle] draining %zu pending bg eval(s): %s",
             gPendingBgEvals.size(), reason.c_str());
        drained.reserve(gPendingBgEvals.size());
        for (auto &entry : gPendingBgEvals) {
            drained.push_back(entry.second);
        }
        gPendingBgEvals.clear();
    }
    for (auto &entry : drained) {
        settleBgEval(entry.globalCallback, entry.settled, "NO_RUNTIME:" + reason);
    }
}

// Reads the whole file at `path` into `out`. Returns false on failure.
static bool readBgFileToString(const std::string &path, std::string &out) {
    FILE *f = std::fopen(path.c_str(), "rb");
    if (f == nullptr) {
        return false;
    }
    if (std::fseek(f, 0, SEEK_END) != 0) {
        std::fclose(f);
        return false;
    }
    long size = std::ftell(f);
    if (size < 0) {
        std::fclose(f);
        return false;
    }
    if (std::fseek(f, 0, SEEK_SET) != 0) {
        std::fclose(f);
        return false;
    }
    out.resize(static_cast<size_t>(size));
    size_t readBytes =
        (size == 0) ? 0 : std::fread(&out[0], 1, static_cast<size_t>(size), f);
    std::fclose(f);
    return readBytes == static_cast<size_t>(size);
}

// nativeEvaluateSegmentInBackground: schedule eval of the segment at `path`
// onto the BACKGROUND JS thread via the bg RuntimeExecutor and invoke `callback`
// from INSIDE that same block, strictly AFTER eval. Returns immediately; the
// callback fires later on the bg JS thread (or synchronously here on a fail-fast
// path such as bg runtime not ready / file read failure).
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadManager_nativeEvaluateSegmentInBackground(
    JNIEnv *env, jobject /* thiz */, jstring segmentPath, jstring sourceURL,
    jobject callback) {

    // Global-ref the callback: it is invoked later on a different thread.
    jobject globalCallback = env->NewGlobalRef(callback);
    // Shared one-shot claim flag for this eval — used by BOTH the work lambda
    // (after eval) and any drop-path drain, so exactly one of them invokes the
    // callback + deletes the global ref.
    auto settled = std::make_shared<std::atomic<bool>>(false);

    const char *pathChars = segmentPath ? env->GetStringUTFChars(segmentPath, nullptr) : nullptr;
    std::string path = pathChars ? std::string(pathChars) : std::string();
    if (pathChars) env->ReleaseStringUTFChars(segmentPath, pathChars);

    const char *urlChars = sourceURL ? env->GetStringUTFChars(sourceURL, nullptr) : nullptr;
    std::string url = urlChars ? std::string(urlChars) : std::string("segment");
    if (urlChars) env->ReleaseStringUTFChars(sourceURL, urlChars);

    // Fail-fast on THIS thread: settle exactly once, no registry entry created.
    auto finishOnThisThread = [&](const std::string &err) {
        settleBgEval(globalCallback, settled, err);
    };

    if (path.empty()) {
        finishOnThisThread("IO_ERROR:Empty segment path");
        return;
    }

    // Snapshot the bg RuntimeExecutor under the timer mutex (the same lock that
    // guards its assignment/teardown). If it's null the bg runtime hasn't
    // installed its SharedBridge yet → retryable NO_RUNTIME.
    RPCRuntimeExecutor executor;
    {
        std::lock_guard<std::mutex> lock(gTimerMutex);
        executor = gBgTimerExecutor;
    }
    if (!executor) {
        finishOnThisThread("NO_RUNTIME:Background runtime executor not available");
        return;
    }

    // F: read the segment file HERE, on the calling (native module) thread,
    // BEFORE dispatch — so only evaluateJavaScript + completion run on the bg JS
    // thread and the read does not block it or race the watchdog.
    std::string source;
    if (!readBgFileToString(path, source)) {
        finishOnThisThread("IO_ERROR:Failed to read bg segment file: " + path);
        return;
    }
    if (source.empty()) {
        finishOnThisThread("IO_ERROR:Empty bg segment file: " + path);
        return;
    }

    // Register this in-flight eval BEFORE dispatch so that if the enqueued work
    // never runs (schedule fails, context==null, ptr==0, or nativeDestroy drops
    // pending work) the drain can settle it as a retryable NO_RUNTIME failure
    // and release the global ref — instead of leaking it and leaning on the
    // Kotlin 30s watchdog. The work lambda removes its own entry when it runs.
    int64_t bgEvalId;
    {
        std::lock_guard<std::mutex> lock(gBgEvalMutex);
        bgEvalId = gNextBgEvalId++;
        gPendingBgEvals[bgEvalId] = PendingBgEval{globalCallback, settled};
    }

    // Move the already-read buffer + the global callback ref into the work
    // lambda. The lambda runs on the bg JS thread (via nativeExecuteWork), which
    // also drainMicrotasks() after — preserving eval+resolve as one atomic turn.
    executor([globalCallback, settled, bgEvalId, source = std::move(source),
              url = std::move(url)](jsi::Runtime &rt) {
        // We are running now → claim ownership and remove our registry entry so
        // a concurrent nativeDestroy drain can't also touch this eval.
        {
            std::lock_guard<std::mutex> lock(gBgEvalMutex);
            gPendingBgEvals.erase(bgEvalId);
        }
        std::string error;
        try {
            LOGI("[SplitBundle] bg evaluating segment %s (%zu bytes)", url.c_str(),
                 source.size());
            auto buffer =
                std::make_shared<jsi::StringBuffer>(std::move(source));
            // Runs the segment's top-level __d(...) synchronously on the bg JS
            // thread before returning.
            rt.evaluateJavaScript(std::move(buffer), url);
            LOGI("[SplitBundle] bg segment %s evaluated", url.c_str());
        } catch (const jsi::JSError &e) {
            error = std::string("Bg segment eval JSError for ") + url + ": " +
                    e.getMessage();
            LOGE("[SplitBundle] %s", error.c_str());
        } catch (const std::exception &e) {
            error = std::string("Bg segment eval failed for ") + url + ": " +
                    e.what();
            LOGE("[SplitBundle] %s", error.c_str());
        } catch (...) {
            error = std::string("Bg segment eval failed for ") + url +
                    " (unknown C++ exception)";
            LOGE("[SplitBundle] %s", error.c_str());
        }
        // Resolve/reject from INSIDE this same bg-JS-thread block, strictly
        // AFTER eval above — the ordering guarantee that fixes the race. The
        // shared `settled` flag makes this a no-op if a drain already claimed
        // it (it would not have, since we erased our entry above, but the flag
        // keeps the invariant airtight against any reorder).
        settleBgEval(globalCallback, settled, error);
    });
}

// nativeDropScheduledWork: clean up after a scheduleOnJSThread drop path where
// CallVoidMethod itself SUCCEEDED but Kotlin then found the runtime unreachable
// (context==null / ptr==0) and returned WITHOUT calling nativeExecuteWork for
// this `workId`. Several things must be released for the given runtime:
//   1. gPendingWork[workId] — the stored work lambda (holding the segment
//      SOURCE BUFFER). nativeExecuteWork is the only other eraser and it will
//      never run for this id, so without this it leaks until nativeDestroy.
//   2. The coalesced RuntimeWorkQueue's drain latch for this runtime — under
//      the coalesced model a successful post (scheduled==true) that never
//      reaches the JS thread leaves the queue stranded with drainScheduled==
//      true, so a recovered runtime would never re-arm a drain. This is a
//      TRANSIENT condition: ptr==0 happens during reload, and the SAME runtime
//      recovers with a fresh ptr. We therefore must NOT leak+clear queue.items
//      — the main runtime has no JS-side retry net, so abandoning its queued
//      SharedRPC deliveries (notifyOtherRuntime) loses them forever. Instead we
//      only reset the drain latch (drainScheduled / scheduledDrainWorkId) so the
//      next enqueue re-arms a fresh drain, leaving queue.items intact for the
//      recovered runtime to drain. Applies to BOTH runtimes (isMain selects).
//   3. (bg only) The in-flight bg eval(s) — settle as retryable NO_RUNTIME so
//      the JNI global ref is released and the JS promise resolves now instead of
//      hanging on the 30s watchdog. drain-all is sound: an unreachable bg JS
//      thread dooms every enqueued bg eval equally.
// Exactly-once via the shared `settled` flag, so a recovered runtime that later
// DOES run stale work (it can't — we erased it) would be a harmless no-op.
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadManager_nativeDropScheduledWork(
    JNIEnv * /* env */, jobject /* thiz */, jboolean isMain, jlong workId) {
    {
        std::lock_guard<std::mutex> lock(gWorkMutex);
        gPendingWork.erase(static_cast<int64_t>(workId));
        // TRANSIENT ptr==0 (reload in flight): do NOT abandon queue.items — the
        // recovered runtime still needs them (main has no JS retry net). Only
        // disarm the drain latch so the next enqueue re-arms a fresh drain.
        auto &queue = getRuntimeWorkQueue(static_cast<bool>(isMain));
        queue.drainScheduled = false;
        queue.scheduledDrainWorkId = -1;
    }
    if (!isMain) {
        drainPendingBgEvals("Background runtime unreachable when scheduling segment eval");
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
        // Coalesce per-runtime work into a single batched drain (see
        // enqueueRuntimeWork) instead of one Kotlin scheduleOnJSThread hop per
        // item. The bg-eval failure-drain that previously lived inline here now
        // runs in scheduleRuntimeDrain's schedule-failure path — under the
        // coalesced model that is the single place a schedule can fail.
        enqueueRuntimeWork(ref, capturedIsMain, std::move(work));
    };

    std::string runtimeId = isMain ? "main" : "background";
    // Save the bg executor so our custom timer worker can dispatch callbacks
    // back to the bg JS queue. We must do this BEFORE moving `executor` into
    // SharedRPC::install (which will std::move it out).
    if (!capturedIsMain) {
        // gBgTimerExecutor is read/cleared under gTimerMutex (timer worker
        // snapshot ~L857, nativeDestroy ~L1133); this write must take the same
        // lock or it races those readers (UB on std::function). nativeInstall
        // SharedBridge holds no other lock here, so a narrow guard scoped to
        // just the assignment is correct and cannot double-lock.
        std::lock_guard<std::mutex> lock(gTimerMutex);
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

    // Recover items stranded by a ptr==0 drop during reload. nativeDropScheduled
    // Work deliberately KEEPS queue.items (main has no JS retry net) and only
    // disarms the drain latch, relying on a LATER enqueue to re-arm a drain. But
    // if no further enqueue arrives after this runtime recovers, those carried-
    // over items (e.g. main-runtime notifyOtherRuntime deliveries) would sit
    // until nativeDestroy and be lost. Make recovery structural: if the freshly
    // installed runtime's queue has leftover items and no drain is armed, re-arm
    // one now on this runtime's executor (`ref`). Mirror enqueueRuntimeWork's
    // lock discipline: set the latch under gWorkMutex, call scheduleRuntimeDrain
    // OUTSIDE the lock. Applies to BOTH runtimes (capturedIsMain selects).
    bool shouldRecoverDrain = false;
    {
        std::lock_guard<std::mutex> lock(gWorkMutex);
        auto &queue = getRuntimeWorkQueue(capturedIsMain);
        if (!queue.items.empty() && !queue.drainScheduled) {
            queue.drainScheduled = true;
            shouldRecoverDrain = true;
        }
    }
    if (shouldRecoverDrain) {
        scheduleRuntimeDrain(ref, capturedIsMain);
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

// ── nativeInvalidateSharedRpc ───────────────────────────────────────────
// Synchronously quiesce the SharedRPC listener for a given runtimeId before
// the underlying JS runtime is torn down. Called from
// BackgroundThreadManager.restart() ahead of process restart / soft reload
// so any cross-runtime executor lambda still in flight short-circuits
// instead of dispatching onto a dying runtime (parity with iOS).
extern "C" JNIEXPORT jboolean JNICALL
Java_com_backgroundthread_BackgroundThreadManager_nativeInvalidateSharedRpc(
    JNIEnv *env, jobject thiz, jstring runtimeId) {

    const char *idChars = env->GetStringUTFChars(runtimeId, nullptr);
    if (!idChars) {
        return JNI_FALSE;
    }
    std::string id(idChars);
    env->ReleaseStringUTFChars(runtimeId, idChars);

    bool found = SharedRPC::invalidate(id);

    // The runtime named by `id` is being torn down (restart → host.reload).
    // Its coalesced work queue must be fully quiesced: if a drain was posted
    // (drainScheduled==true) but is dropped during reload, the latch would
    // survive the reinstall and every future enqueue would see a stale
    // drainScheduled==true → shouldSchedule stays false and new work enqueues
    // but never schedules a drain again (work stranded forever). Leak+clear the
    // queue (it's being destroyed anyway — the queued ~jsi::Function must not
    // run on the dying runtime), reset the drain latch, and erase the orphaned
    // gPendingWork entry for the outstanding drain. runtimeId "main" → isMain.
    {
        std::lock_guard<std::mutex> lock(gWorkMutex);
        bool isMain = (id == "main");
        auto &queue = getRuntimeWorkQueue(isMain);
        if (queue.scheduledDrainWorkId >= 0) {
            gPendingWork.erase(queue.scheduledDrainWorkId);
        }
        leakAndClearRuntimeQueue(queue);
    }

    LOGI("nativeInvalidateSharedRpc: id=%s found=%d", id.c_str(), found ? 1 : 0);
    return found ? JNI_TRUE : JNI_FALSE;
}

// ── nativeDestroy ───────────────────────────────────────────────────────
// Clean up native resources.
// Called from BackgroundThreadManager.destroy().
extern "C" JNIEXPORT void JNICALL
Java_com_backgroundthread_BackgroundThreadManager_nativeDestroy(
    JNIEnv *env, jobject thiz) {

    SharedRPC::reset();
    SharedStore::reset();

    // Stop and join the timer worker before touching any shared state it
    // reads. Without this, the worker could finish one last dispatch after
    // we clear gTimers / gBgTimerExecutor and enqueue stale work against
    // the about-to-be-destroyed runtime.
    gTimerWorkerStop.store(true);
    gTimerCv.notify_all();
    if (gTimerWorkerThread.joinable()) {
        gTimerWorkerThread.join();
    }
    gTimerWorkerStarted.store(false);

    // Intentionally leak remaining jsi::Function callbacks (same rationale
    // as SharedRPC::reset): destroying them here would run ~jsi::Function
    // on the torn-down runtime and crash.
    {
        std::lock_guard<std::mutex> lock(gTimerMutex);
        for (auto &entry : gTimers) {
            if (entry.second.callback) {
                new std::shared_ptr<jsi::Function>(std::move(entry.second.callback));
            }
        }
        gTimers.clear();
        gBgTimerExecutor = nullptr;
    }

    // Drain pending cross-runtime work. Each std::function may capture a
    // shared_ptr<jsi::Function> tied to the destroyed runtime; leak them
    // for the same reason as above.
    //
    // NOTE: the bg-eval work lambdas captured here also hold a JNI global ref
    // to a SegmentEvalCallback. Leaking the std::function leaks that ref AND
    // leaves the JS promise pending — so we settle those explicitly below via
    // gPendingBgEvals (the entry survives independently of the leaked lambda).
    {
        std::lock_guard<std::mutex> lock(gWorkMutex);
        for (auto &entry : gPendingWork) {
            new std::function<void(jsi::Runtime &)>(std::move(entry.second));
        }
        gPendingWork.clear();
        for (auto *queue : {&gMainRuntimeWorkQueue, &gBgRuntimeWorkQueue}) {
            for (auto &work : queue->items) {
                new std::function<void(jsi::Runtime &)>(std::move(work));
            }
            queue->items.clear();
            queue->drainScheduled = false;
            queue->scheduledDrainWorkId = -1;
        }
    }

    // Drain pending bg-eval callbacks: the bg runtime is gone, so any eval that
    // was enqueued but never ran must be settled NOW (retryable NO_RUNTIME) so
    // the JS promise resolves immediately and the global ref is released —
    // rather than leaking and relying on the Kotlin 30s watchdog.
    drainPendingBgEvals("Background runtime destroyed before segment eval ran");

    LOGI("Native resources cleaned up");
}
