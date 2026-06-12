#import "SplitBundleLoader.h"
#import "SBLLogger.h"
#import <ReactCommon/RCTHost.h>
#import <ReactCommon/RCTHost+Internal.h>
#import <ReactCommon/RCTInstance.h>
#import <objc/runtime.h>
#import <CommonCrypto/CommonDigest.h>
#import <os/lock.h>
#include <jsi/jsi.h>
#include <cstdint>

namespace {

// Zero-copy jsi::Buffer over an NSData (M4/M5).
//
// WHY: the previous code did `std::string(data.bytes, data.length)` inside a
// jsi::StringBuffer — a FULL second copy of the (potentially multi-MB) segment
// bytes on top of whatever the NSData read already cost. For the hot segment
// path that doubles peak memory and adds a memcpy on the JS thread.
//
// This buffer instead RETAINS the NSData and hands jsi the NSData's own bytes
// directly. Combined with NSDataReadingMappedIfSafe at the read site, the
// segment is mmap'd and Hermes parses straight from the mapped pages — no heap
// copy at all on the happy path. The retained NSData also makes the buffer's
// lifetime explicit and self-owned: the executor block runs ASYNCHRONOUSLY
// (it's buffered until the entry bundle finishes), so the NSData MUST outlive
// the originating scope. Holding it inside the Buffer (which jsi keeps alive
// via the shared_ptr for the duration of evaluateJavaScript) guarantees that.
class NSDataJSIBuffer : public facebook::jsi::Buffer {
 public:
  explicit NSDataJSIBuffer(NSData *data) : data_(data) {}
  size_t size() const override { return data_.length; }
  const uint8_t *data() const override {
    return static_cast<const uint8_t *>(data_.bytes);
  }

 private:
  NSData *data_;  // strong retain (ARC) — keeps mmap/heap bytes alive for jsi.
};

}  // namespace

// Exactly-once settle guard for the C1 watchdog. Wraps a BOOL behind an
// os_unfair_lock. WHY AN OBJECT (not a __block BOOL + manual free): the executor
// block and the watchdog dispatch_after BOTH capture it and BOTH may run
// (dispatch_after is not cancellable, and on a wedge the executor can run AFTER
// the watchdog). Tying the lock's lifetime to ARC — both blocks retain this
// object, it deallocs only after both release — eliminates the use-after-free a
// manual free() in either block would cause. `tryClaim` returns YES to exactly
// one caller; the loser does nothing.
@interface SBLSettleGuard : NSObject
- (BOOL)tryClaim;
@end

@implementation SBLSettleGuard {
  os_unfair_lock _lock;
  BOOL _settled;
}
- (instancetype)init {
  if (self = [super init]) {
    _lock = OS_UNFAIR_LOCK_INIT;
    _settled = NO;
  }
  return self;
}
- (BOOL)tryClaim {
  os_unfair_lock_lock(&_lock);
  BOOL won = !_settled;
  if (won) {
    _settled = YES;
  }
  os_unfair_lock_unlock(&_lock);
  return won;
}
@end

// Watchdog window for the buffered runtime executor (C1). Post-entry eval is
// sub-millisecond, so this only ever elapses on a genuine wedge (entry bundle
// never finished evaluating) — never on a healthy slow device.
//
// Fix G: 30s (matching Android and the bg-runtime watchdog). The executor stays
// buffered until the main ENTRY bundle finishes evaluating; on a slow/throttled
// cold start that entry eval can itself exceed 10s, which would falsely trip the
// watchdog on a segment load that was about to succeed. 30s keeps the
// genuine-wedge safety net while leaving headroom for slow cold starts.
static const NSTimeInterval kSegmentEvalWatchdogSeconds = 30.0;

// NSError `code` values produced by +evaluateSegmentAtPath:... . These are
// DISTINCT (L8) so loadSegment: can map each to its own JS reject code and JS
// can classify retryable-vs-fatal:
//   - HostMissing / NilInstance → SPLIT_BUNDLE_NO_RUNTIME (retryable):
//       the runtime/host simply wasn't ready yet; a later attempt may succeed.
//   - IvarMissing → SPLIT_BUNDLE_NATIVE_UNAVAILABLE (NOT retryable): a renamed
//       ivar is a structural/build defect that no retry can fix.
//   - Timeout → SPLIT_BUNDLE_TIMEOUT (retryable): buffered executor never ran.
//   - IORead → SPLIT_BUNDLE_IO_ERROR: file read/mmap failed.
//   - EvalThrow → SPLIT_BUNDLE_EVAL_ERROR (NOT retryable): a real bug in the
//       segment's own JS/Hermes code; retrying just re-throws.
typedef NS_ENUM(NSInteger, ESegmentEvalError) {
    ESegmentEvalErrorHostMissing = 1,
    ESegmentEvalErrorIvarMissing = 2,
    ESegmentEvalErrorNilInstance = 3,
    ESegmentEvalErrorIORead = 4,
    ESegmentEvalErrorEvalThrow = 5,
    ESegmentEvalErrorTimeout = 6,
};

@implementation SplitBundleLoader

// Reflectively asks bundle-update whether the current bundle's manifest is
// required to ship per-segment SHA-256s (three-bundle / split-thread format).
// When this returns YES, an empty `expected` in verifySha256OfFile:expected:
// is treated as a hard fail instead of the back-compat skip. Returns NO when
// the symbol is absent so older bundle-update versions don't wedge the loader.
+ (BOOL)currentBundleRequiresPerSegmentHash
{
    Class cls = NSClassFromString(@"ReactNativeBundleUpdate.BundleUpdateStore");
    if (!cls) return NO;
    SEL sel = NSSelectorFromString(@"currentBundleRequiresPerSegmentHash");
    if (![cls respondsToSelector:sel]) return NO;
    NSMethodSignature *sig = [cls methodSignatureForSelector:sel];
    if (!sig) return NO;
    // BOOL on Apple platforms is signed char (or bool on arm64); accept both.
    const char *retType = sig.methodReturnType;
    if (strcmp(retType, @encode(BOOL)) != 0 &&
        strcmp(retType, @encode(char)) != 0 &&
        strcmp(retType, @encode(bool)) != 0) {
        return NO;
    }
    NSInvocation *inv = [NSInvocation invocationWithMethodSignature:sig];
    inv.target = cls;
    inv.selector = sel;
    [inv invoke];
    BOOL result = NO;
    [inv getReturnValue:&result];
    return result;
}

// Segment-time SHA-256 verification helper. Returns YES on match, NO on
// mismatch or read failure. Streams the file with a fixed-size buffer so a
// large segment doesn't pull the whole bundle into memory just to hash it.
+ (BOOL)verifySha256OfFile:(NSString *)path expected:(NSString *)expected
{
    if (expected.length == 0) {
        // Defer to bundle-update: three-bundle format manifests must ship
        // per-segment hashes (fail closed); older formats are allowed to omit
        // them (back-compat).
        return ![SplitBundleLoader currentBundleRequiresPerSegmentHash];
    }
    NSFileHandle *handle = [NSFileHandle fileHandleForReadingAtPath:path];
    if (!handle) {
        return NO;
    }
    CC_SHA256_CTX ctx;
    CC_SHA256_Init(&ctx);
    @try {
        const NSUInteger chunk = 64 * 1024;
        while (1) {
            NSData *data = [handle readDataOfLength:chunk];
            if (data.length == 0) break;
            CC_SHA256_Update(&ctx, data.bytes, (CC_LONG)data.length);
        }
    } @finally {
        [handle closeFile];
    }
    unsigned char digest[CC_SHA256_DIGEST_LENGTH];
    CC_SHA256_Final(digest, &ctx);
    NSMutableString *hex = [NSMutableString stringWithCapacity:CC_SHA256_DIGEST_LENGTH * 2];
    for (int i = 0; i < CC_SHA256_DIGEST_LENGTH; i++) {
        [hex appendFormat:@"%02x", digest[i]];
    }
    return [hex caseInsensitiveCompare:expected] == NSOrderedSame;
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    [SBLLogger info:@"SplitBundleLoader module initialized"];
    return std::make_shared<facebook::react::NativeSplitBundleLoaderSpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"SplitBundleLoader";
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

// MARK: - OTA bundle path helper

/// Safely retrieves the OTA bundle path via typed NSInvocation to avoid
/// performSelector ARC/signature issues (#15).
+ (nullable NSString *)otaBundlePath
{
    Class cls = NSClassFromString(@"ReactNativeBundleUpdate.BundleUpdateStore");
    if (!cls) return nil;

    SEL sel = NSSelectorFromString(@"currentBundleMainJSBundle");
    if (![cls respondsToSelector:sel]) return nil;

    NSMethodSignature *sig = [cls methodSignatureForSelector:sel];
    if (!sig || strcmp(sig.methodReturnType, @encode(id)) != 0) {
        [SBLLogger warn:@"OTA method signature mismatch — skipping"];
        return nil;
    }

    NSInvocation *inv = [NSInvocation invocationWithMethodSignature:sig];
    inv.target = cls;
    inv.selector = sel;
    [inv invoke];

    __unsafe_unretained id rawResult = nil;
    [inv getReturnValue:&rawResult];
    if (![rawResult isKindOfClass:[NSString class]]) return nil;

    NSString *result = (NSString *)rawResult;
    if (result.length == 0) return nil;

    if ([result hasPrefix:@"file://"]) {
        result = [[NSURL URLWithString:result] path];
    }
    return result;
}

// MARK: - RCTHost resolution helper

/// Resolves the bridgeless RCTHost via the AppDelegate's `reactHost` accessor
/// (New Architecture). Returns nil when the host is unavailable so callers can
/// reject gracefully. Extracted so both segment registration and the
/// evaluate-then-resolve path (#race) share one lookup.
+ (nullable RCTHost *)currentReactHost
{
    id<UIApplicationDelegate> appDelegate = [UIApplication sharedApplication].delegate;
    if ([appDelegate respondsToSelector:NSSelectorFromString(@"reactHost")]) {
        RCTHost *host = [appDelegate performSelector:NSSelectorFromString(@"reactHost")];
        if (host) {
            return host;
        }
    }
    return nil;
}

// MARK: - Segment evaluation helper (resolve-after-eval, fixes lazy-segment race)

/// Evaluates a segment bundle into the CURRENT runtime and invokes `onEvaluated`
/// from INSIDE the same runtime-executor block, immediately after the segment's
/// `__d(...)` module definitions have run.
///
/// WHY THIS EXISTS — the "Requiring unknown module" race (#race):
/// The previous implementation called `RCTHost registerSegmentWithId:path:`,
/// which routes to `ReactInstance::registerSegment` →
/// `runtimeScheduler_->scheduleWork([]{ runtime.evaluateJavaScript(segment) })`.
/// That only ENQUEUES the eval onto the runtime scheduler and returns; the
/// loadSegment promise was resolved IMMEDIATELY afterwards. Metro's
/// `import().then(() => __r(moduleId))` microtask could therefore run `__r`
/// BEFORE the scheduled eval populated the module table → a FATAL, uncatchable
/// "Requiring unknown module" inside metroRequire.
///
/// We cannot fix this by merely scheduling `resolve` after `registerSegment` on
/// the same executor: `RuntimeScheduler_Modern::scheduleWork` pushes
/// ImmediatePriority tasks into a `std::priority_queue` keyed only on
/// `expirationTime` (RuntimeScheduler_Modern.cpp / Task.h `TaskPriorityComparer`).
/// `std::priority_queue` is NOT stable, so two same-tick tasks have UNDEFINED
/// relative order — FIFO is not guaranteed under the Modern scheduler.
///
/// Instead we evaluate the segment OURSELVES inside a single
/// `callFunctionOnBufferedRuntimeExecutor:` block (exactly like
/// `loadEntryBundle:`) and signal completion in that SAME block. Eval and the
/// resolve are now one atomic unit of work — there is no cross-task ordering to
/// lose, so any subsequent `__r(moduleId)` is guaranteed to find the module.
///
/// This is safe because OneKey segments are STANDALONE-EVALUATABLE Metro
/// bundles (the serializer emits `baseJSBundle`/`bundleToString` output — plain
/// top-level `__d(moduleId, factory, deps)` calls, NOT Hermes RAM/indexed
/// segments that would require the registerSegment manifest wiring). The paired
/// `.seg.hbc` is just the Hermes-compiled form of that same source, which
/// `evaluateJavaScript` runs identically to the entry bundle's `.hbc`.
///
/// `onEvaluated` is invoked EXACTLY ONCE with nil on success or a populated
/// NSError on failure. The NSError `code` is meaningful and mapped by the caller
/// to a distinct JS reject code (see ESegmentEvalError below + loadSegment:):
/// callers use it to classify retryable (no-runtime / timeout) vs fatal
/// (eval-throw / IO) failures.
+ (void)evaluateSegmentAtPath:(NSString *)bundlePath
                    segmentId:(int)segmentId
                   segmentKey:(NSString *)segmentKey
                  onEvaluated:(void (^)(NSError *_Nullable error))onEvaluated
{
    RCTHost *host = [SplitBundleLoader currentReactHost];
    if (!host) {
        onEvaluated([NSError errorWithDomain:@"SplitBundleLoader"
                                        code:ESegmentEvalErrorHostMissing
                                    userInfo:@{NSLocalizedDescriptionKey: @"RCTHost not available for segment evaluation"}]);
        return;
    }

    // Reach the RCTInstance the same way loadEntryBundle: does, so we can use
    // the buffered runtime executor primitive (callFunctionOnBufferedRuntimeExecutor:).
    Ivar ivar = class_getInstanceVariable([host class], "_instance");
    if (!ivar) {
        // L7: a missing `_instance` ivar means a future RN bump renamed/removed
        // the field our reflection depends on. That silently disables ALL
        // segment loading, so log loudly (error, with the class name) instead of
        // failing quietly — the next RN upgrade then surfaces visibly in logs.
        [SBLLogger error:[NSString stringWithFormat:@"[SplitBundle] FATAL: _instance ivar not found on %@ — segment loading is DISABLED. An RN upgrade likely renamed this private field; SplitBundleLoader reflection must be updated.", [host class]]];
        onEvaluated([NSError errorWithDomain:@"SplitBundleLoader"
                                        code:ESegmentEvalErrorIvarMissing
                                    userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"_instance ivar not found on %@", [host class]]}]);
        return;
    }

    RCTInstance *instance = object_getIvar(host, ivar);
    if (!instance) {
        onEvaluated([NSError errorWithDomain:@"SplitBundleLoader"
                                        code:ESegmentEvalErrorNilInstance
                                    userInfo:@{NSLocalizedDescriptionKey: @"RCTInstance is nil"}]);
        return;
    }

    // M4/M5: mmap the segment (NSDataReadingMappedIfSafe) instead of a full
    // heap read, then wrap it zero-copy in NSDataJSIBuffer (which retains the
    // NSData). Net effect: no second copy, and the bytes stay alive for the
    // async executor block because the buffer owns the NSData.
    NSError *readError = nil;
    NSData *data = [NSData dataWithContentsOfFile:bundlePath
                                         options:NSDataReadingMappedIfSafe
                                           error:&readError];
    if (!data || data.length == 0) {
        onEvaluated([NSError errorWithDomain:@"SplitBundleLoader"
                                        code:ESegmentEvalErrorIORead
                                    userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Failed to read segment at %@%@", bundlePath, readError ? [NSString stringWithFormat:@": %@", readError.localizedDescription] : @""]}]);
        return;
    }

    // M6: preserve a meaningful eval source URL so in-segment crash frames are
    // symbolicated. RN's registerSegment used
    // `JSExecutor::getSyntheticBundlePath(segmentId, segmentPath)`, which for a
    // non-main segment yields `seg-<id>.js` (see cxxreact/JSExecutor.cpp). We
    // replicate that exact form so Hermes/Metro attribute frames to the segment
    // the same way the native path did — using `lastPathComponent` here would
    // degrade symbolication.
    NSString *sourceURL = [NSString stringWithFormat:@"seg-%d.js", segmentId];
    CFAbsoluteTime dispatchStart = CFAbsoluteTimeGetCurrent();
    [SBLLogger info:[NSString stringWithFormat:@"[SplitBundle] loadSegment: evaluating %@ (key=%@, %lu bytes)", sourceURL, segmentKey, (unsigned long)data.length]];

    // C1: the executor block below is BUFFERED — callFunctionOnBufferedRuntimeExecutor
    // does not run it until the main entry bundle finishes evaluating
    // (RCTInstance/ReactInstance.cpp). If a segment load is requested before the
    // entry completes (early startup, reload teardown, host swap) and the entry
    // never completes, the block NEVER runs → onEvaluated would never fire →
    // the JS promise hangs forever and inflightSegments wedges with no timeout.
    //
    // Guard: invoke `onEvaluated` EXACTLY ONCE. SBLSettleGuard wraps a BOOL
    // behind an os_unfair_lock; whichever racer wins — the executor block (happy
    // path) or the watchdog timer (genuine wedge) — gets YES from tryClaim and
    // settles the promise; the loser does nothing. The guard is an ARC object
    // captured (retained) by BOTH blocks, so its lock outlives both with no
    // manual free() and therefore no use-after-free (both blocks can run, in
    // either order, on a wedge).
    SBLSettleGuard *settleGuard = [[SBLSettleGuard alloc] init];

    [instance callFunctionOnBufferedRuntimeExecutor:^(facebook::jsi::Runtime &runtime) {
        @autoreleasepool {
            // If the watchdog already fired (entry took >30s then unwedged),
            // the JS promise is already rejected — still evaluate the segment
            // (the module table benefits) but don't double-settle.
            BOOL won = [settleGuard tryClaim];
            NSError *evalError = nil;
            CFAbsoluteTime evalStart = CFAbsoluteTimeGetCurrent();
            try {
                auto buffer = std::make_shared<NSDataJSIBuffer>(data);
                runtime.evaluateJavaScript(buffer, [sourceURL UTF8String]);
                double evalMs = (CFAbsoluteTimeGetCurrent() - evalStart) * 1000.0;
                [SBLLogger info:[NSString stringWithFormat:@"[SplitBundle] loadSegment: %@ evaluated in %.1fms", sourceURL, evalMs]];
            } catch (const std::exception &e) {
                // L8: a JS/Hermes eval throw is a REAL BUG in the segment's own
                // code, not a transient runtime-readiness problem. Mapped to a
                // NON-retryable code by the caller so JS caches it as failed.
                evalError = [NSError errorWithDomain:@"SplitBundleLoader"
                                                code:ESegmentEvalErrorEvalThrow
                                            userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Segment evaluation failed for %@: %s", sourceURL, e.what()]}];
                [SBLLogger warn:[NSString stringWithFormat:@"[SplitBundle] loadSegment: %@ evaluation threw: %s", sourceURL, e.what()]];
            } catch (...) {
                evalError = [NSError errorWithDomain:@"SplitBundleLoader"
                                                code:ESegmentEvalErrorEvalThrow
                                            userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Segment evaluation failed for %@ (unknown C++ exception)", sourceURL]}];
                [SBLLogger warn:[NSString stringWithFormat:@"[SplitBundle] loadSegment: %@ evaluation threw an unknown exception", sourceURL]];
            }
            if (won) {
                // Resolve/reject the JS promise from INSIDE this same block,
                // strictly AFTER the segment eval above — the ordering guarantee
                // that fixes the "Requiring unknown module" race (see method doc).
                onEvaluated(evalError);
            } else {
                [SBLLogger warn:[NSString stringWithFormat:@"[SplitBundle] loadSegment: %@ evaluated AFTER watchdog already settled (entry was wedged >%.0fs)", sourceURL, kSegmentEvalWatchdogSeconds]];
            }
        }
    }];

    // C1 watchdog. Fires only on a genuine wedge: in steady state the entry
    // bundle is long done and the buffered block runs sub-millisecond, so the
    // guard is already claimed (tryClaim returns NO) long before this elapses.
    // On a real wedge it settles the promise with a distinct, RETRYABLE timeout
    // error so the JS loader can re-attempt instead of hanging inflightSegments
    // forever. The block retains settleGuard, so the lock stays valid even if
    // the executor block later runs (entry finally evaluates).
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(kSegmentEvalWatchdogSeconds * NSEC_PER_SEC)),
                   dispatch_get_global_queue(QOS_CLASS_UTILITY, 0), ^{
        if ([settleGuard tryClaim]) {
            [SBLLogger error:[NSString stringWithFormat:@"[SplitBundle] loadSegment: %@ (key=%@) WATCHDOG fired after %.0fs — runtime executor never ran (entry bundle likely never finished evaluating). Rejecting as retryable timeout.", sourceURL, segmentKey, kSegmentEvalWatchdogSeconds]];
            onEvaluated([NSError errorWithDomain:@"SplitBundleLoader"
                                            code:ESegmentEvalErrorTimeout
                                        userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Segment %@ eval timed out after %.0fs (buffered runtime executor never ran)", segmentKey, kSegmentEvalWatchdogSeconds]}]);
        }
    });

    double dispatchMs = (CFAbsoluteTimeGetCurrent() - dispatchStart) * 1000.0;
    [SBLLogger info:[NSString stringWithFormat:@"[SplitBundle] loadSegment: %@ dispatched in %.1fms (resolve fires after eval; watchdog %.0fs)", sourceURL, dispatchMs, kSegmentEvalWatchdogSeconds]];
}

// MARK: - getRuntimeBundleContext

- (void)getRuntimeBundleContext:(RCTPromiseResolveBlock)resolve
                         reject:(RCTPromiseRejectBlock)reject
{
    @try {
        NSString *runtimeKind = @"main";
        NSString *sourceKind = @"builtin";
        NSString *bundleRoot = @"";
        NSString *nativeVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"] ?: @"";
        NSString *bundleVersion = @"";

        NSString *otaPath = [SplitBundleLoader otaBundlePath];
        if (otaPath && [[NSFileManager defaultManager] fileExistsAtPath:otaPath]) {
            sourceKind = @"ota";
            bundleRoot = [otaPath stringByDeletingLastPathComponent];
        }

        if ([sourceKind isEqualToString:@"builtin"]) {
            bundleRoot = [[NSBundle mainBundle] resourcePath] ?: @"";
        }

        resolve(@{
            @"runtimeKind": runtimeKind,
            @"sourceKind": sourceKind,
            @"bundleRoot": bundleRoot,
            @"nativeVersion": nativeVersion,
            @"bundleVersion": bundleVersion,
        });
    } @catch (NSException *exception) {
        reject(@"SPLIT_BUNDLE_CONTEXT_ERROR", exception.reason, nil);
    }
}

// MARK: - Path resolution helper

/// Resolves a relative segment path to an absolute path, checking OTA then builtin.
/// Returns nil if the segment file is not found.
+ (nullable NSString *)resolveAbsolutePath:(NSString *)relativePath
{
    // 1. Try OTA bundle root first
    NSString *otaPath = [SplitBundleLoader otaBundlePath];
    if (otaPath) {
        // Standardize root so hasPrefix matches candidate (iOS resolves /private/var → /var).
        NSString *otaRoot = [[otaPath stringByDeletingLastPathComponent] stringByStandardizingPath];
        NSString *candidate = [[otaRoot stringByAppendingPathComponent:relativePath] stringByStandardizingPath];
        BOOL otaPrefixOk = [candidate hasPrefix:otaRoot];
        BOOL otaExists = [[NSFileManager defaultManager] fileExistsAtPath:candidate];
        [SBLLogger info:[NSString stringWithFormat:@"[resolveAbs] rel=%@ ota root=%@ cand=%@ prefixOk=%d exists=%d",
                         relativePath, otaRoot, candidate, otaPrefixOk, otaExists]];
        if (otaPrefixOk && otaExists) {
            return candidate;
        }
    } else {
        [SBLLogger info:[NSString stringWithFormat:@"[resolveAbs] rel=%@ otaPath=(nil) — skipping OTA", relativePath]];
    }

    // 2. Fallback to builtin resource path
    NSString *builtinRoot = [[[NSBundle mainBundle] resourcePath] stringByStandardizingPath];
    NSString *candidate = [[builtinRoot stringByAppendingPathComponent:relativePath] stringByStandardizingPath];
    BOOL builtinPrefixOk = [candidate hasPrefix:builtinRoot];
    BOOL builtinExists = [[NSFileManager defaultManager] fileExistsAtPath:candidate];
    [SBLLogger info:[NSString stringWithFormat:@"[resolveAbs] rel=%@ builtin root=%@ cand=%@ prefixOk=%d exists=%d",
                     relativePath, builtinRoot, candidate, builtinPrefixOk, builtinExists]];
    if (builtinPrefixOk && builtinExists) {
        return candidate;
    }

    [SBLLogger warn:[NSString stringWithFormat:@"[resolveAbs] rel=%@ → nil (not found in OTA nor builtin)", relativePath]];
    return nil;
}

// MARK: - resolveSegmentPath (Phase 3)

- (void)resolveSegmentPath:(NSString *)relativePath
                    sha256:(NSString *)sha256
                   resolve:(RCTPromiseResolveBlock)resolve
                    reject:(RCTPromiseRejectBlock)reject
{
    @try {
        if ([relativePath containsString:@".."]) {
            reject(@"SPLIT_BUNDLE_INVALID_PATH",
                   [NSString stringWithFormat:@"Path traversal rejected: %@", relativePath],
                   nil);
            return;
        }

        NSString *absolutePath = [SplitBundleLoader resolveAbsolutePath:relativePath];
        if (!absolutePath) {
            reject(@"SPLIT_BUNDLE_NOT_FOUND",
                   [NSString stringWithFormat:@"Segment file not found: %@", relativePath],
                   nil);
            return;
        }
        if (![SplitBundleLoader verifySha256OfFile:absolutePath expected:sha256]) {
            reject(@"SPLIT_BUNDLE_SHA256_MISMATCH",
                   [NSString stringWithFormat:@"Segment SHA-256 mismatch: %@", relativePath],
                   nil);
            return;
        }
        resolve(absolutePath);
    } @catch (NSException *exception) {
        reject(@"SPLIT_BUNDLE_RESOLVE_ERROR", exception.reason, nil);
    }
}

// MARK: - loadEntryBundle (common + entry split loading)

+ (void)loadEntryBundle:(NSString *)bundlePath inHost:(id)host
{
  if (!host || bundlePath.length == 0) {
    [SBLLogger warn:[NSString stringWithFormat:@"loadEntryBundle: invalid arguments (host=%@, path=%@)", host, bundlePath]];
    return;
  }

  Ivar ivar = class_getInstanceVariable([host class], "_instance");
  if (!ivar) {
    [SBLLogger warn:[NSString stringWithFormat:@"loadEntryBundle: _instance ivar not found on %@", [host class]]];
    return;
  }

  RCTInstance *instance = object_getIvar(host, ivar);
  if (!instance) {
    [SBLLogger warn:@"loadEntryBundle: _instance is nil"];
    return;
  }

  // M5: mmap + zero-copy (NSDataJSIBuffer retains the NSData for the async block),
  // mirroring the segment path. Entry bundle is the largest single read, so this
  // saves the biggest single copy.
  NSData *data = [NSData dataWithContentsOfFile:bundlePath
                                       options:NSDataReadingMappedIfSafe
                                         error:nil];
  if (!data || data.length == 0) {
    [SBLLogger warn:[NSString stringWithFormat:@"loadEntryBundle: failed to read bundle at %@", bundlePath]];
    return;
  }

  NSString *sourceURL = bundlePath.lastPathComponent ?: bundlePath;
  CFAbsoluteTime startTime = CFAbsoluteTimeGetCurrent();
  [SBLLogger info:[NSString stringWithFormat:@"[SplitBundle] loadEntryBundle: evaluating %@ (%lu bytes)", sourceURL, (unsigned long)data.length]];

  [instance callFunctionOnBufferedRuntimeExecutor:^(facebook::jsi::Runtime &runtime) {
    @autoreleasepool {
      CFAbsoluteTime evalStart = CFAbsoluteTimeGetCurrent();
      auto buffer = std::make_shared<NSDataJSIBuffer>(data);
      runtime.evaluateJavaScript(buffer, [sourceURL UTF8String]);
      double evalMs = (CFAbsoluteTimeGetCurrent() - evalStart) * 1000.0;
      [SBLLogger info:[NSString stringWithFormat:@"[SplitBundle] loadEntryBundle: %@ evaluated in %.1fms", sourceURL, evalMs]];
    }
  }];

  double totalMs = (CFAbsoluteTimeGetCurrent() - startTime) * 1000.0;
  [SBLLogger info:[NSString stringWithFormat:@"[SplitBundle] loadEntryBundle: %@ dispatched in %.1fms (eval is async)", sourceURL, totalMs]];
}

// MARK: - loadSegment

- (void)loadSegment:(double)segmentId
         segmentKey:(NSString *)segmentKey
       relativePath:(NSString *)relativePath
             sha256:(NSString *)sha256
            resolve:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject
{
    @try {
        int segId = (int)segmentId;
        CFAbsoluteTime segStart = CFAbsoluteTimeGetCurrent();

        // Path traversal guard (#45)
        if ([relativePath containsString:@".."]) {
            reject(@"SPLIT_BUNDLE_INVALID_PATH",
                   [NSString stringWithFormat:@"Path traversal rejected: %@", relativePath],
                   nil);
            return;
        }

        NSString *absolutePath = [SplitBundleLoader resolveAbsolutePath:relativePath];

        if (!absolutePath) {
            reject(@"SPLIT_BUNDLE_NOT_FOUND",
                   [NSString stringWithFormat:@"Segment file not found: %@ (key=%@)", relativePath, segmentKey],
                   nil);
            return;
        }

        if (![SplitBundleLoader verifySha256OfFile:absolutePath expected:sha256]) {
            reject(@"SPLIT_BUNDLE_SHA256_MISMATCH",
                   [NSString stringWithFormat:@"Segment SHA-256 mismatch: %@ (key=%@)", relativePath, segmentKey],
                   nil);
            return;
        }

        // Evaluate the segment into the current runtime and resolve ONLY after
        // its module definitions have actually run (#race). We intentionally do
        // NOT use registerSegmentWithId: + immediate resolve here: that resolves
        // before the scheduler-enqueued eval completes, so Metro's
        // `import().then(() => __r(moduleId))` can hit "Requiring unknown module"
        // (a fatal, uncatchable crash). See +evaluateSegmentAtPath:... doc for
        // the full ordering rationale (Modern scheduler priority_queue is not
        // FIFO, so we collapse eval+resolve into one runtime-executor block).
        // segId is threaded through for the synthetic eval source URL (M6) and
        // for log parity with the previous register flow.
        [SplitBundleLoader evaluateSegmentAtPath:absolutePath
                                       segmentId:segId
                                      segmentKey:segmentKey
                                     onEvaluated:^(NSError *_Nullable evalError) {
            if (evalError) {
                // L8: map the helper's distinct NSError code to a distinct JS
                // reject code so JS can classify retryable vs fatal (see
                // ESegmentEvalError + installProdBundleLoader.ts H3).
                NSString *rejectCode;
                switch ((ESegmentEvalError)evalError.code) {
                    case ESegmentEvalErrorTimeout:
                        rejectCode = @"SPLIT_BUNDLE_TIMEOUT";        // retryable
                        break;
                    case ESegmentEvalErrorIORead:
                        rejectCode = @"SPLIT_BUNDLE_IO_ERROR";       // fatal
                        break;
                    case ESegmentEvalErrorEvalThrow:
                        rejectCode = @"SPLIT_BUNDLE_EVAL_ERROR";     // fatal (segment bug)
                        break;
                    case ESegmentEvalErrorIvarMissing:
                        // Fix 2: `_instance` ivar reflection failed — STRUCTURAL/
                        // PERMANENT. An RN version bump renamed/removed the
                        // private field our reflection depends on, so segment
                        // loading is disabled until the native code is updated.
                        // Retrying can NEVER recreate a renamed ivar, so this is
                        // fatal NATIVE_UNAVAILABLE — NOT retryable NO_RUNTIME. By
                        // contrast HostMissing / NilInstance below are genuinely
                        // transient (host/instance not up yet → a later attempt
                        // may succeed).
                        rejectCode = @"SPLIT_BUNDLE_NATIVE_UNAVAILABLE"; // fatal
                        break;
                    case ESegmentEvalErrorHostMissing:
                    case ESegmentEvalErrorNilInstance:
                    default:
                        rejectCode = @"SPLIT_BUNDLE_NO_RUNTIME";     // retryable
                        break;
                }
                reject(rejectCode,
                       evalError.localizedDescription ?: @"Segment evaluation failed",
                       evalError);
                return;
            }
            double segMs = (CFAbsoluteTimeGetCurrent() - segStart) * 1000.0;
            [SBLLogger info:[NSString stringWithFormat:@"[SplitBundle] Loaded segment %@ (id=%d) in %.1fms (eval-complete)", segmentKey, segId, segMs]];
            resolve(nil);
        }];
    } @catch (NSException *exception) {
        reject(@"SPLIT_BUNDLE_LOAD_ERROR",
               [NSString stringWithFormat:@"Failed to load segment %@: %@", segmentKey, exception.reason],
               nil);
    }
}

@end
