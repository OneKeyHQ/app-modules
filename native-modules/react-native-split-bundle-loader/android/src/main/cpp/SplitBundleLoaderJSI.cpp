/*
 * SplitBundleLoaderJSI.cpp
 *
 * JNI bridge that evaluates a Metro split-bundle segment into the CURRENT
 * Hermes runtime and signals completion ONLY AFTER the segment's `__d(...)`
 * module definitions have actually run.
 *
 * WHY THIS EXISTS — the "Requiring unknown module" race:
 * The previous Kotlin implementation called `ReactContext.registerSegment`,
 * which routes (bridgeless) through `ReactHostImpl.registerSegment` →
 * `ReactInstance.registerSegment` → C++ `ReactInstance::registerSegment`, and
 * that only does `runtimeScheduler_->scheduleWork([]{ evaluateJavaScript() })`
 * — i.e. it ENQUEUES the eval and returns. `ReactHostImpl.registerSegment`
 * then invokes the completion callback immediately on `Task.IMMEDIATE_EXECUTOR`,
 * so the loadSegment promise resolves BEFORE the segment is evaluated. Metro's
 * `import().then(() => __r(moduleId))` microtask can therefore run `__r` before
 * the module table is populated → a fatal, uncatchable "Requiring unknown
 * module" inside metroRequire.
 *
 * THE FIX (mirrors iOS callFunctionOnBufferedRuntimeExecutor:):
 * We evaluate the segment OURSELVES on the JS thread via the bridgeless
 * CallInvoker. `CallInvoker::invokeAsync(CallFunc&&)` schedules the callback
 * onto the SAME RuntimeScheduler that registerSegment would have used, and the
 * callback receives `jsi::Runtime&`. We read the segment file, call
 * `runtime.evaluateJavaScript(...)`, and invoke the completion callback from
 * INSIDE that same block, strictly AFTER eval. Eval + completion are one atomic
 * unit of work on the JS thread, so any subsequent `__r(moduleId)` is
 * guaranteed to find the module.
 *
 * SEGMENT FORMAT: OneKey segments are standalone-evaluatable Metro bundles
 * (top-level `__d(moduleId, factory, deps)` calls; the `.seg.hbc` is just the
 * Hermes-compiled form of the same `.seg.js`). C++ ReactInstance::registerSegment
 * itself just calls `runtime.evaluateJavaScript(buffer, ...)` with no RAM/indexed
 * segment manifest wiring, confirming these are evaluatable as plain scripts.
 */

#include <fbjni/fbjni.h>
#include <jsi/jsi.h>

#include <ReactCommon/CallInvoker.h>
#include <ReactCommon/CallInvokerHolder.h>

#include <android/log.h>

#include <cstdio>
#include <memory>
#include <string>
#include <vector>

#define SBL_LOG_TAG "SplitBundleLoader"
#define SBL_LOGI(...) \
  __android_log_print(ANDROID_LOG_INFO, SBL_LOG_TAG, __VA_ARGS__)
#define SBL_LOGW(...) \
  __android_log_print(ANDROID_LOG_WARN, SBL_LOG_TAG, __VA_ARGS__)

namespace facebook::react::splitbundleloader {

namespace {

// Reads the whole file at `path` into a std::string. Returns false on failure.
bool readFileToString(const std::string& path, std::string& out) {
  FILE* f = std::fopen(path.c_str(), "rb");
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
  size_t read = (size == 0)
      ? 0
      : std::fread(&out[0], 1, static_cast<size_t>(size), f);
  std::fclose(f);
  return read == static_cast<size_t>(size);
}

} // namespace

// Java callback contract — implemented in Kotlin as
// SplitBundleLoaderModule.SegmentEvalCallback. `onComplete(null)` on success,
// `onComplete(errorMessage)` on failure. Invoked exactly once.
//
// Error-message prefix convention (read by the Kotlin side to pick the contract
// reject code): a message prefixed with "IO_ERROR:" is a segment file read/mmap
// failure → SPLIT_BUNDLE_IO_ERROR (non-retryable). Any other message is a
// segment JS/Hermes eval throw → SPLIT_BUNDLE_EVAL_ERROR (non-retryable).
struct JSegmentEvalCallback
    : jni::JavaClass<JSegmentEvalCallback> {
  static constexpr auto kJavaDescriptor =
      "Lcom/splitbundleloader/SplitBundleLoaderModule$SegmentEvalCallback;";

  // `error` empty → success (passes Java null); non-empty → failure (passes the
  // message). We always pass null on success and a non-empty message on failure,
  // so this mapping is unambiguous. `const` so it can be invoked through a
  // (const) global_ref / alias_ref.
  void onComplete(const std::string& error) const {
    static const auto method =
        javaClassStatic()->getMethod<void(jni::alias_ref<jni::JString>)>(
            "onComplete");
    jni::local_ref<jni::JString> arg =
        error.empty() ? jni::local_ref<jni::JString>(nullptr)
                      : jni::make_jstring(error);
    method(self(), arg);
  }
};

class SplitBundleLoaderJSI
    : public jni::JavaClass<SplitBundleLoaderJSI> {
 public:
  static constexpr auto kJavaDescriptor =
      "Lcom/splitbundleloader/SplitBundleLoaderModule;";

  // Schedules evaluation of the segment at `segmentPath` onto the JS thread via
  // the bridgeless CallInvoker, then invokes `callback` from INSIDE the same JS
  // thread block, strictly AFTER the segment has been evaluated. This is the
  // ordering guarantee that fixes the "Requiring unknown module" race.
  //
  // Does NOT block the calling (native modules) thread — returns immediately
  // after scheduling. Resolution happens later on the JS thread.
  static void nativeEvaluateSegment(
      jni::alias_ref<jclass> /* unused */,
      jni::alias_ref<CallInvokerHolder::javaobject> callInvokerHolder,
      jni::alias_ref<jstring> segmentPath,
      jni::alias_ref<jstring> sourceURL,
      jni::alias_ref<JSegmentEvalCallback> callback) {
    // Capture everything we need as values / global refs because the callback
    // runs later on a different thread.
    auto globalCallback = jni::make_global(callback);

    if (!callInvokerHolder) {
      globalCallback->onComplete("CallInvokerHolder is null");
      return;
    }

    std::shared_ptr<CallInvoker> callInvoker =
        callInvokerHolder->cthis()->getCallInvoker();
    if (!callInvoker) {
      globalCallback->onComplete("CallInvoker is null");
      return;
    }

    std::string path = segmentPath ? segmentPath->toStdString() : std::string();
    std::string url =
        sourceURL ? sourceURL->toStdString() : std::string("segment");

    if (path.empty()) {
      globalCallback->onComplete("Empty segment path");
      return;
    }

    // F: Read the segment file HERE, on the calling (native module) thread,
    // BEFORE invokeAsync. Doing the disk read inside the JS-thread callback
    // would block the JS thread on I/O and race the Kotlin watchdog. We move
    // the already-read buffer into the lambda so only evaluateJavaScript +
    // completion run on the JS thread (mirrors iOS, which mmaps off-thread and
    // only evaluates on the runtime thread). The read error is surfaced as a
    // dedicated IO error so JS can classify it as NON-retryable.
    std::string source;
    bool ioOk = readFileToString(path, source);
    if (!ioOk) {
      globalCallback->onComplete("IO_ERROR:Failed to read segment file: " + path);
      return;
    }
    if (source.empty()) {
      globalCallback->onComplete("IO_ERROR:Empty segment file: " + path);
      return;
    }

    // CallFunc = std::function<void(jsi::Runtime&)>; this runs on the JS thread
    // on the SAME RuntimeScheduler the segment registration would have used.
    callInvoker->invokeAsync([globalCallback,
                              source = std::move(source),
                              url = std::move(url)](jsi::Runtime& runtime) {
      std::string error;
      try {
        {
          SBL_LOGI(
              "[SplitBundle] evaluating segment %s (%zu bytes)",
              url.c_str(),
              source.size());
          auto buffer = std::make_shared<jsi::StringBuffer>(std::move(source));
          // Evaluate the segment into the CURRENT runtime. evaluateJavaScript
          // runs the segment's top-level __d(...) module definitions
          // synchronously on this JS thread before returning.
          runtime.evaluateJavaScript(std::move(buffer), url);
          SBL_LOGI("[SplitBundle] segment %s evaluated", url.c_str());
        }
      } catch (const jsi::JSError& e) {
        error = std::string("Segment evaluation JSError for ") + url + ": " +
            e.getMessage();
        SBL_LOGW("[SplitBundle] %s", error.c_str());
      } catch (const std::exception& e) {
        error = std::string("Segment evaluation failed for ") + url + ": " +
            e.what();
        SBL_LOGW("[SplitBundle] %s", error.c_str());
      } catch (...) {
        error = std::string("Segment evaluation failed for ") + url +
            " (unknown C++ exception)";
        SBL_LOGW("[SplitBundle] %s", error.c_str());
      }

      // Attach to the JVM for this (JS) thread before calling back into Java.
      // The JS thread is a native (pthread) thread not implicitly attached to
      // the JVM; ThreadScope ensures a valid JNIEnv for the callback.
      jni::ThreadScope ts;
      // Resolve/reject from INSIDE this same JS-thread block, strictly AFTER
      // eval above — the ordering guarantee that fixes the race.
      globalCallback->onComplete(error);
    });
  }

  static void registerNatives() {
    // Static JNI methods on a plain JavaClass (NOT a HybridClass): bind via
    // the class's registerNatives, not registerHybrid.
    javaClassStatic()->registerNatives({
        makeNativeMethod(
            "nativeEvaluateSegment",
            SplitBundleLoaderJSI::nativeEvaluateSegment),
    });
  }
};

} // namespace facebook::react::splitbundleloader

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* /* reserved */) {
  return facebook::jni::initialize(vm, [] {
    facebook::react::splitbundleloader::SplitBundleLoaderJSI::registerNatives();
  });
}
