#import "BackgroundThreadManager.h"
#import <React/RCTBridge.h>
#import <React/RCTBundleURLProvider.h>
#if __has_include(<ReactAppDependencyProvider/RCTAppDependencyProvider.h>)
#import <ReactAppDependencyProvider/RCTAppDependencyProvider.h>
#endif
#if __has_include(<ReactAppDependencyProvider/RCTReactNativeFactory.h>)
#import <ReactAppDependencyProvider/RCTReactNativeFactory.h>
#endif
#if __has_include(<ReactAppDependencyProvider/RCTAppDependencyProvider.h>)
#import <ReactAppDependencyProvider/RCTAppDependencyProvider.h>
#elif __has_include("RCTAppDependencyProvider.h")
#import "RCTAppDependencyProvider.h"
#endif
#import "BackgroundRunnerReactNativeDelegate.h"
#import "BTLogger.h"

#include "SharedStore.h"
#include "SharedRPC.h"
#import <React/RCTReloadCommand.h>
#import <ReactCommon/RCTHost.h>
#import <objc/runtime.h>
#import <os/lock.h>
#include <jsi/jsi.h>
#include <cstdint>

namespace {

// Zero-copy jsi::Buffer that retains its NSData for the async (buffered)
// executor block's lifetime — same rationale as SplitBundleLoader's
// NSDataJSIBuffer (M4/M5): no second full copy of the segment bytes, and the
// mmap'd/heap bytes stay alive because the buffer owns the NSData.
class BgMgrNSDataJSIBuffer : public facebook::jsi::Buffer {
 public:
  explicit BgMgrNSDataJSIBuffer(NSData *data) : data_(data) {}
  size_t size() const override { return data_.length; }
  const uint8_t *data() const override {
    return static_cast<const uint8_t *>(data_.bytes);
  }

 private:
  NSData *data_;  // strong retain (ARC).
};

}  // namespace

// Exactly-once settle guard for the bg watchdog — same design as
// SplitBundleLoader's SBLSettleGuard. An ARC object captured by both the
// executor block and the watchdog dispatch_after; ARC keeps its lock alive
// until both release, so neither block needs (or may do) a manual free —
// avoiding the use-after-free that would occur if either freed the lock while
// the other still runs.
@interface BgMgrSettleGuard : NSObject
- (BOOL)tryClaim;
@end

@implementation BgMgrSettleGuard {
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

// Watchdog window for the bg buffered runtime executor (H2 = bg-runtime port of
// SplitBundleLoader's C1). The bg runtime's buffered executor stays buffered
// until the bg entry bundle finishes evaluating in
// BackgroundReactNativeDelegate.hostDidStart:; if that never completes (host
// teardown, OTA-resolve abort), the block never runs — without this watchdog
// the JS promise would hang inflightSegments forever.
//
// Fix G: 30s (matching Android and the main-runtime watchdog). The buffered
// executor stays buffered until the bg ENTRY bundle finishes evaluating; on a
// slow/throttled cold start that entry eval can itself exceed 10s, which would
// falsely trip the watchdog on a load that was about to succeed. 30s keeps the
// genuine-wedge safety net while leaving generous headroom for slow cold starts.
static const NSTimeInterval kBgSegmentEvalWatchdogSeconds = 30.0;

// EBgMgrSegmentEvalError NSError `code` values are declared in
// BackgroundThreadManager.h so the TurboModule boundary
// (BackgroundThread.loadSegmentInBackground) can map each distinct code to its
// own JS reject code by NAME. They stay numerically aligned with
// SplitBundleLoader's ESegmentEvalError; see installProdBundleLoader.ts for the
// retryable-vs-fatal classification (H3 / fix E).

@interface BackgroundThreadManager ()
@property (nonatomic, strong) BackgroundReactNativeDelegate *reactNativeFactoryDelegate;
@property (nonatomic, strong) RCTReactNativeFactory *reactNativeFactory;
@property (nonatomic, assign) BOOL hasListeners;
// Atomic because the post-reload health-check reads it on the main thread
// while startBackgroundRunnerWithEntryURL: writes it from whichever thread
// the caller is on (the module's public surface does not constrain caller
// thread). Atomic gives us the memory-barrier needed for cross-thread
// reads to see the latest store. The set+dispatch pattern in start... is
// still TOCTOU-racy for concurrent first-time starts, but that hazard
// pre-exists this PR and is out of scope here.
@property (atomic, assign, readwrite) BOOL isStarted;
// Flipped to YES inside the installSharedBridgeInMainRuntime: lambda and to
// NO at the start of restartWithMode:. Read by the post-reload health-check
// to detect when the host app's hostDidStart: failed to re-invoke the
// install on the new RCTHost — which would silently break main→bg RPC.
@property (atomic, assign) BOOL mainSharedBridgeInstalled;
// Monotonic counter bumped on every restartWithMode: call. The post-reload
// health-check captures its own generation and bails if a newer restart()
// supersedes it, preventing the second restart's flag reset from making
// the first restart's check misreport. Touched only on the main thread
// (the dispatch_block_t `work` runs there), so non-atomic is correct.
@property (nonatomic, assign) NSUInteger restartGeneration;
// Cached from the most recent startBackgroundRunnerWithEntryURL: call. Used
// by the mode='all' self-respawn fallback to preserve the host's last
// chosen entry URL (typically an OTA-resolved bundle path) across restart;
// `reactNativeFactoryDelegate` is released for mode='all', so without this
// cache we'd fall back to the bundled "background.bundle" — which on OTA
// devices is a moduleId-mismatch crash waiting to happen. Atomic for the
// same reason as isStarted: written from the caller's thread (public API
// doesn't pin start... to main) and read on main by the health-check.
@property (atomic, copy) NSString *lastEntryURL;

// Forward declaration so restartWithMode: can call it; definition lives
// after restartWithMode: for readability (the call site is the natural
// place to start reading the restart flow).
- (void)scheduleHealthCheckForRestart:(NSString *)mode
                                isAll:(BOOL)isAll
                           generation:(NSUInteger)myGen
                              retried:(BOOL)retried;
@end

// First-stage delay for the post-reload health-check. Picked so the host's
// hostDidStart: chain has time to run on a typical device. On slower
// devices the chain can exceed this; that's why the check is structured as
// two stages — see scheduleHealthCheckForRestart:isAll:generation:retried:.
static const NSTimeInterval kRestartHealthCheckDelaySeconds = 3.0;

@implementation BackgroundThreadManager

static BackgroundThreadManager *_sharedInstance = nil;
static dispatch_once_t onceToken;
static NSString *const MODULE_NAME = @"background";
static NSString *const MODULE_DEBUG_URL = @"http://localhost:8082/apps/mobile/background.bundle?platform=ios&dev=true&lazy=false&minify=false&inlineSourceMap=false&modulesOnly=false&runModule=true&excludeSource=true&sourcePaths=url-server&app=so.onekey.wallet&transform.routerRoot=app&transform.engine=hermes&transform.bytecode=1&unstable_transformProfile=hermes-stable";

#pragma mark - Singleton

+ (instancetype)sharedInstance {
    dispatch_once(&onceToken, ^{
        _sharedInstance = [[self alloc] init];
    });
    return _sharedInstance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _isStarted = NO;
        _hasListeners = NO;
    }
    return self;
}

#pragma mark - SharedBridge

+ (void)installSharedBridgeInMainRuntime:(RCTHost *)host {
    if (!host) {
        [BTLogger error:@"Cannot install SharedBridge: RCTHost is nil"];
        return;
    }

    Ivar ivar = class_getInstanceVariable([host class], "_instance");
    id instance = object_getIvar(host, ivar);

    if (!instance) {
        [BTLogger error:@"Cannot install SharedBridge: RCTInstance is nil"];
        return;
    }

    [instance callFunctionOnBufferedRuntimeExecutor:^(facebook::jsi::Runtime &runtime) {
        SharedStore::install(runtime);

        // Install SharedRPC with executor for cross-runtime notifications.
        // Capture the RCTInstance __weak: when the main host is torn down on
        // reload (RNRestart / RCTReloadCommand / BackgroundThread.restart) the
        // instance is dealloc'd, and any executor lambda still in flight will
        // see a nil strong reference and bail out — instead of dispatching
        // callFunctionOnBufferedRuntimeExecutor on a freed instance and
        // crashing (EXC_BAD_ACCESS / use-after-free).
        __weak id weakInstance = instance;
        RPCRuntimeExecutor mainExecutor = [weakInstance](std::function<void(jsi::Runtime &)> work) {
            id strongInstance = weakInstance;
            if (!strongInstance) {
                return;
            }
            [strongInstance callFunctionOnBufferedRuntimeExecutor:[work](jsi::Runtime &rt) {
                work(rt);
            }];
        };
        SharedRPC::install(runtime, std::move(mainExecutor), "main");
        // Set the integration-health flag from inside the JS-thread lambda so
        // it only flips true AFTER the listener is actually live; the post-
        // reload observer reads this to detect host hostDidStart: omissions.
        [BackgroundThreadManager sharedInstance].mainSharedBridgeInstalled = YES;
        [BTLogger info:@"SharedStore and SharedRPC installed in main runtime"];
    }];
}

#pragma mark - Public Methods

- (void)startBackgroundRunner {
#if DEBUG
    [self startBackgroundRunnerWithEntryURL:MODULE_DEBUG_URL];
#else
    // In production, use the bundled background.bundle (not the debug HTTP URL)
    [self startBackgroundRunnerWithEntryURL:@"background.bundle"];
#endif
}

- (void)startBackgroundRunnerWithEntryURL:(NSString *)entryURL {
    if (self.isStarted) {
        // Surface the URL mismatch when a second start... call would
        // otherwise silently drop the host's intent — most common in the
        // self-respawn-vs-host-start race after restart('all'): self-
        // respawn boots with cached URL, then the host's async
        // hostDidStart chain (gated on feature flag / login / network)
        // finally calls start with a different URL and gets short-
        // circuited. The log lets that race be diagnosed from production
        // traces instead of "why is bg on the old URL".
        if (entryURL.length > 0 && ![entryURL isEqualToString:self.lastEntryURL]) {
            [BTLogger warn:[NSString stringWithFormat:
                @"startBackgroundRunnerWithEntryURL: ignored (already started); requested=%@ active=%@",
                entryURL, self.lastEntryURL ?: @"<nil>"]];
        }
        return;
    }
    self.isStarted = YES;
    // Cache the URL before we even start so the mode='all' self-respawn
    // path can preserve it across a restart that releases the delegate.
    // Updated unconditionally — first-time and re-start with a new URL
    // both reflect into lastEntryURL.
    self.lastEntryURL = entryURL;
    [BTLogger info:[NSString stringWithFormat:@"Starting background runner with entryURL: %@", entryURL]];

    dispatch_async(dispatch_get_main_queue(), ^{
        NSDictionary *initialProperties = @{};
        NSDictionary *launchOptions = @{};
        self.reactNativeFactoryDelegate = [[BackgroundReactNativeDelegate alloc] init];
        self.reactNativeFactory = [[RCTReactNativeFactory alloc] initWithDelegate:self.reactNativeFactoryDelegate];

        // Only set jsBundleSource for debug HTTP URLs or explicit OTA overrides.
        // Leaving the default release name ("background.bundle") unset lets the
        // delegate fall back to split-bundle mode (common.bundle + entry).
        if (![entryURL isEqualToString:@"background.bundle"]) {
            [self.reactNativeFactoryDelegate setJsBundleSource:std::string([entryURL UTF8String])];
        }

        [self.reactNativeFactory.rootViewFactory viewWithModuleName:MODULE_NAME
                                                     initialProperties:initialProperties
                                                         launchOptions:launchOptions];
    });
}

#pragma mark - Segment Registration (Phase 2.5 spike)

- (void)registerSegmentInBackground:(NSNumber *)segmentId
                               path:(NSString *)path
                         completion:(void (^)(NSError * _Nullable error))completion
{
    if (!self.isStarted || !self.reactNativeFactoryDelegate) {
        // Transient: the bg runtime just isn't up yet. NotStarted → NO_RUNTIME
        // (retryable). Previously a raw `code:1` which fell through the
        // boundary's `default` — same destination, but now NAMED so the mapping
        // is explicit and can never drift (fix 1 / E).
        NSError *error = [NSError errorWithDomain:@"BackgroundThread"
                                             code:EBgMgrSegmentEvalErrorNotStarted
                                         userInfo:@{NSLocalizedDescriptionKey: @"Background runtime not started"}];
        if (completion) completion(error);
        return;
    }

    // Verify the file exists. FATAL: a missing segment file is real packaging /
    // OTA corruption — retrying just re-misses. Previously a raw `code:2` that
    // the boundary's `default` MISCLASSIFIED as retryable NO_RUNTIME, masking
    // the corruption; now FileNotFound → NOT_FOUND (fatal) (fix 1 / E — the
    // NO-SHIP blocker).
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) {
        NSError *error = [NSError errorWithDomain:@"BackgroundThread"
                                             code:EBgMgrSegmentEvalErrorFileNotFound
                                         userInfo:@{NSLocalizedDescriptionKey:
                                                        [NSString stringWithFormat:@"Segment file not found: %@", path]}];
        if (completion) completion(error);
        return;
    }

    [self evaluateSegmentInBackground:segmentId path:path completion:completion];
}

// Evaluate-then-resolve segment load for the BACKGROUND runtime (H2).
//
// WHY THIS REPLACES registerSegmentWithId: + immediate completion(nil):
// The old path called `[delegate registerSegmentWithId:path:]` (which routes to
// RCTInstance/ReactInstance::registerSegment — that only ENQUEUES
// `runtime.evaluateJavaScript(segment)` on the runtime scheduler and returns)
// then resolved the JS promise immediately. That is the exact
// "Requiring unknown module" race the MAIN runtime already fixed in
// SplitBundleLoader: Metro's `import().then(() => __r(moduleId))` microtask can
// run `__r` BEFORE the scheduled eval populated the module table → a FATAL,
// uncatchable crash. Locale segments load through THIS path in the bg runtime
// (ServiceSetting.refreshLocaleMessages → import('./json/*.json') runs in
// kit-bg), so a language switch could still crash.
//
// Fix: evaluate the segment OURSELVES inside one
// `callFunctionOnBufferedRuntimeExecutor:` block on the bg RCTInstance and
// signal completion in that SAME block, strictly AFTER eval — making eval +
// resolve one atomic unit so any subsequent `__r(moduleId)` finds the module.
// The bg RCTInstance is the delegate's private `_rctInstance` ivar; we read it
// reflectively (the same pattern installSharedBridgeInMainRuntime: uses on the
// main host) to keep this fix self-contained in this file rather than widening
// the delegate's surface. Includes the same exactly-once + watchdog guard as
// the main-runtime fix (C1) because the bg buffered executor is likewise
// buffered until the bg entry bundle finishes evaluating in hostDidStart:.
- (void)evaluateSegmentInBackground:(NSNumber *)segmentId
                               path:(NSString *)path
                         completion:(void (^)(NSError * _Nullable error))completion
{
    // Reach the bg RCTInstance via the delegate's `_rctInstance` ivar. `id`
    // (not a typed RCTInstance*) mirrors installSharedBridgeInMainRuntime:'s
    // untyped handling and avoids needing the RCTInstance header here.
    BackgroundReactNativeDelegate *delegate = self.reactNativeFactoryDelegate;
    Ivar ivar = class_getInstanceVariable([delegate class], "_rctInstance");
    if (!ivar) {
        // Loud failure (L7 parity): a future RN/delegate refactor that renames
        // this ivar silently disables ALL bg segment loading — surface it.
        // STRUCTURAL/PERMANENT (not transient): retrying can never recreate a
        // renamed ivar, so this maps to fatal NATIVE_UNAVAILABLE — distinct from
        // the nil-instance case below, which IS transient. Misclassifying this
        // as NO_RUNTIME would make JS retry a permanently-broken reflection.
        [BTLogger error:[NSString stringWithFormat:@"[SplitBundle] FATAL: _rctInstance ivar not found on %@ — bg segment loading is DISABLED.", [delegate class]]];
        NSError *error = [NSError errorWithDomain:@"BackgroundThread"
                                             code:EBgMgrSegmentEvalErrorIvarMissing
                                         userInfo:@{NSLocalizedDescriptionKey: @"_rctInstance ivar not found on bg delegate"}];
        if (completion) completion(error);
        return;
    }
    id instance = object_getIvar(delegate, ivar);
    if (!instance) {
        [BTLogger error:@"[SplitBundle] bg loadSegment: background RCTInstance not available"];
        NSError *error = [NSError errorWithDomain:@"BackgroundThread"
                                             code:EBgMgrSegmentEvalErrorNilInstance
                                         userInfo:@{NSLocalizedDescriptionKey: @"Background RCTInstance not available"}];
        if (completion) completion(error);
        return;
    }

    // M4/M5: mmap + zero-copy buffer (retains the NSData for the async block).
    NSError *readError = nil;
    NSData *data = [NSData dataWithContentsOfFile:path
                                         options:NSDataReadingMappedIfSafe
                                           error:&readError];
    if (!data || data.length == 0) {
        NSError *error = [NSError errorWithDomain:@"BackgroundThread"
                                             code:EBgMgrSegmentEvalErrorIORead
                                         userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Failed to read bg segment at %@%@", path, readError ? [NSString stringWithFormat:@": %@", readError.localizedDescription] : @""]}];
        if (completion) completion(error);
        return;
    }

    // M6: synthetic `seg-<id>.js` source URL for in-segment crash symbolication.
    int segIdInt = segmentId.intValue;
    NSString *sourceURL = [NSString stringWithFormat:@"seg-%d.js", segIdInt];

    // C1: exactly-once guard shared (and retained) by the executor block and the
    // watchdog. ARC-owned so the lock outlives both with no manual free.
    BgMgrSettleGuard *settleGuard = [[BgMgrSettleGuard alloc] init];

    CFAbsoluteTime dispatchStart = CFAbsoluteTimeGetCurrent();
    [BTLogger info:[NSString stringWithFormat:@"[SplitBundle] bg loadSegment: evaluating %@ (id=%d, %lu bytes)", sourceURL, segIdInt, (unsigned long)data.length]];

    // No __weak dance needed here (unlike the SharedRPC executor): this block
    // does not re-dispatch onto the instance — it runs synchronously inside the
    // instance's own runtime executor with a live `runtime &`, so by the time it
    // executes the instance is necessarily still alive.
    [instance callFunctionOnBufferedRuntimeExecutor:^(facebook::jsi::Runtime &runtime) {
        @autoreleasepool {
            BOOL won = [settleGuard tryClaim];
            NSError *evalError = nil;
            CFAbsoluteTime evalStart = CFAbsoluteTimeGetCurrent();
            try {
                auto buffer = std::make_shared<BgMgrNSDataJSIBuffer>(data);
                runtime.evaluateJavaScript(buffer, [sourceURL UTF8String]);
                double evalMs = (CFAbsoluteTimeGetCurrent() - evalStart) * 1000.0;
                [BTLogger info:[NSString stringWithFormat:@"[SplitBundle] bg loadSegment: %@ evaluated in %.1fms", sourceURL, evalMs]];
            } catch (const std::exception &e) {
                evalError = [NSError errorWithDomain:@"BackgroundThread"
                                                code:EBgMgrSegmentEvalErrorEvalThrow
                                            userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Bg segment evaluation failed for %@: %s", sourceURL, e.what()]}];
                [BTLogger error:[NSString stringWithFormat:@"[SplitBundle] bg loadSegment: %@ evaluation threw: %s", sourceURL, e.what()]];
            } catch (...) {
                evalError = [NSError errorWithDomain:@"BackgroundThread"
                                                code:EBgMgrSegmentEvalErrorEvalThrow
                                            userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Bg segment evaluation failed for %@ (unknown C++ exception)", sourceURL]}];
                [BTLogger error:[NSString stringWithFormat:@"[SplitBundle] bg loadSegment: %@ evaluation threw an unknown exception", sourceURL]];
            }
            if (won) {
                // Resolve AFTER eval — the ordering guarantee that fixes the race.
                if (completion) completion(evalError);
            } else {
                [BTLogger warn:[NSString stringWithFormat:@"[SplitBundle] bg loadSegment: %@ evaluated AFTER watchdog already settled (bg entry was wedged >%.0fs)", sourceURL, kBgSegmentEvalWatchdogSeconds]];
            }
        }
    }];

    // C1 watchdog — fires only on a genuine wedge (bg entry bundle never
    // finished evaluating). Rejects with a retryable timeout so the JS loader
    // re-attempts. settleGuard is retained by this block, so the lock stays
    // valid even if the executor block later runs.
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(kBgSegmentEvalWatchdogSeconds * NSEC_PER_SEC)),
                   dispatch_get_global_queue(QOS_CLASS_UTILITY, 0), ^{
        if ([settleGuard tryClaim]) {
            [BTLogger error:[NSString stringWithFormat:@"[SplitBundle] bg loadSegment: %@ WATCHDOG fired after %.0fs — bg runtime executor never ran (bg entry bundle likely never finished evaluating). Rejecting as retryable timeout.", sourceURL, kBgSegmentEvalWatchdogSeconds]];
            NSError *timeoutError = [NSError errorWithDomain:@"BackgroundThread"
                                                        code:EBgMgrSegmentEvalErrorTimeout
                                                    userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Bg segment %@ eval timed out after %.0fs (buffered runtime executor never ran)", sourceURL, kBgSegmentEvalWatchdogSeconds]}];
            if (completion) completion(timeoutError);
        }
    });

    double dispatchMs = (CFAbsoluteTimeGetCurrent() - dispatchStart) * 1000.0;
    [BTLogger info:[NSString stringWithFormat:@"[SplitBundle] bg loadSegment: %@ dispatched in %.1fms (resolve fires after eval; watchdog %.0fs)", sourceURL, dispatchMs, kBgSegmentEvalWatchdogSeconds]];
}

#pragma mark - Restart

- (void)restartWithMode:(NSString *)mode
                 reason:(NSString *)reason
             completion:(void (^)(NSError * _Nullable error))completion
{
    BOOL isUI = [mode isEqualToString:@"ui"];
    BOOL isAll = [mode isEqualToString:@"all"];

    if (!isUI && !isAll) {
        NSError *error = [NSError errorWithDomain:@"BackgroundThread"
                                             code:10
                                         userInfo:@{NSLocalizedDescriptionKey:
                                                        [NSString stringWithFormat:@"BackgroundThread.restart: unsupported mode '%@', expected 'ui' or 'all'", mode]}];
        if (completion) completion(error);
        return;
    }

    NSString *attributedReason = reason.length > 0
        ? [NSString stringWithFormat:@"BackgroundThread.restart(%@): %@", mode, reason]
        : [NSString stringWithFormat:@"BackgroundThread.restart(%@)", mode];

    [BTLogger info:[NSString stringWithFormat:@"restart: mode=%@ reason=%@", mode, reason ?: @"<none>"]];

    // Bridge teardown must happen on the main thread; everything before
    // RCTTriggerReloadCommandListeners runs synchronously on that thread so
    // the quiesce is provably done before any reload-driven instance dealloc.
    dispatch_block_t work = ^{
        // (1) Quiesce SharedRPC listener(s). Synchronous, holds the SharedRPC
        // mutex internally — any concurrent notifyOtherRuntime that gets past
        // the lock will see alive=false.
        SharedRPC::invalidate("main");
        if (isAll) {
            SharedRPC::invalidate("background");
        }

        // (2) For mode=all, drop our strong refs to the bg host so ARC can
        // dealloc it. We also reset isStarted so the post-reload hostDidStart
        // callback (which is invoked from AppDelegate after the main reload
        // completes) re-enters startBackgroundRunner instead of short-circuiting.
        if (isAll) {
            [BTLogger info:@"restart(all): releasing bg factory + resetting isStarted"];
            self.reactNativeFactory = nil;
            self.reactNativeFactoryDelegate = nil;
            self.isStarted = NO;
        }

        // (3) Schedule a defensive post-reload health-check BEFORE triggering
        // the reload. The check uses pure dispatch_after on our own state
        // (mainSharedBridgeInstalled, isStarted) instead of an
        // RCTJavaScriptDidLoadNotification observer — that notification's
        // post timing is unreliable in bridgeless / NewArch, and an
        // observer-based design introduces observer-leak + cross-generation
        // misreporting hazards that dispatch_after + restartGeneration
        // sidesteps entirely. See scheduleHealthCheckForRestart:... for the
        // two-stage retry that tolerates slow devices.
        self.mainSharedBridgeInstalled = NO;
        NSUInteger myGen = ++self.restartGeneration;
        [self scheduleHealthCheckForRestart:mode isAll:isAll generation:myGen retried:NO];

        // (4) Trigger the main bridge reload. Equivalent to what
        // react-native-restart did, but now sequenced AFTER quiesce so the
        // SharedRPC race window is closed.
        RCTTriggerReloadCommandListeners(attributedReason);

        // (5) For mode=all, the new main host's hostDidStart will call
        // BackgroundThreadBridge.startBackgroundRunner again (isStarted==NO
        // now), recreating the bg host and re-installing the "background"
        // listener via BackgroundRunnerReactNativeDelegate.hostDidStart.
        // If the host fails to do so, the health-check above self-heals
        // (with the default-entry-URL caveat noted there).
        // For mode=ui, the bg host stays as-is; only the "main" listener
        // gets re-installed in installSharedBridgeInMainRuntime.

        if (completion) completion(nil);
    };

    if ([NSThread isMainThread]) {
        work();
    } else {
        dispatch_async(dispatch_get_main_queue(), work);
    }
}

#pragma mark - Restart Health Check

// Two-stage post-reload health-check.
//
// Stage 1 fires at +kRestartHealthCheckDelaySeconds (~3s) from restart()
// dispatch. Reads `mainSharedBridgeInstalled` and (for mode='all')
// `isStarted` — both signals the module owns. Decides:
//   - Both healthy → log success, done.
//   - Anything missing → reschedule stage 2 (+~3s more, total ~6s) and
//     defer the final verdict. This is intentional: even the "main ready,
//     bg not ready" case is rescheduled rather than self-respawned at
//     stage 1, because hosts that gate startBackgroundRunner on async
//     work (feature-flag fetch, login, network callback) can be
//     mainReady=YES while their async start is still in flight — see the
//     in-line comment in the stage-1 branch below.
//
// Stage 2 fires at total ~6s. Whatever state we see is the final verdict;
// any remaining gap is logged as an integration failure (with self-heal
// where possible). 6s comfortably covers a low-end iPhone OTA reload chain
// (observed worst case ~2.5s); past that, integration is more likely
// broken than slow.
//
// Generation check at the top of each stage block bails out if a newer
// restart() has bumped restartGeneration in the meantime — its own check
// will run, and reading flags now would mix cycles.
- (void)scheduleHealthCheckForRestart:(NSString *)mode
                                isAll:(BOOL)isAll
                           generation:(NSUInteger)myGen
                              retried:(BOOL)retried
{
    __weak BackgroundThreadManager *weakSelf = self;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW,
                                 (int64_t)(kRestartHealthCheckDelaySeconds * NSEC_PER_SEC)),
                   dispatch_get_main_queue(), ^{
        BackgroundThreadManager *strongSelf = weakSelf;
        if (!strongSelf) return;
        if (strongSelf.restartGeneration != myGen) {
            [BTLogger info:[NSString stringWithFormat:
                @"restart(%@) health-check stage%@ superseded by newer restart (gen %lu → %lu)",
                mode, retried ? @"2" : @"1",
                (unsigned long)myGen, (unsigned long)strongSelf.restartGeneration]];
            return;
        }

        BOOL mainReady = strongSelf.mainSharedBridgeInstalled;
        BOOL bgReady = !isAll || strongSelf.isStarted;
        NSString *stage = retried ? @"2" : @"1";

        if (mainReady && bgReady) {
            [BTLogger info:[NSString stringWithFormat:
                @"restart(%@): post-reload health-check stage%@ OK (mainReady=YES, bgReady=YES)",
                mode, stage]];
            return;
        }

        // Stage 1: anything missing → reschedule stage 2.
        //
        // Earlier rounds short-circuited the "mainReady=YES, bgReady=NO"
        // case as a stable signal that the host wasn't going to call
        // startBackgroundRunner. That assumption holds only for hosts
        // whose hostDidStart: synchronously calls both install AND start.
        // Hosts that gate startBackgroundRunner on async work (feature
        // flag fetch, login state, network callback) can have
        // mainReady=YES while their async start call is still in flight;
        // self-respawning at stage 1 would race them and the host's
        // later (real) start would be silently dropped by the early
        // return in startBackgroundRunnerWithEntryURL: (now logged as a
        // warn, see #2). Stage 2 (+~3s) is sized to give that async path
        // time to land; if it still hasn't by then, the host is broken
        // and self-respawn is correct.
        if (!retried) {
            [BTLogger info:[NSString stringWithFormat:
                @"restart(%@): stage1 incomplete (mainReady=%@, bgReady=%@) — rescheduling stage2 to cover slow hostDidStart chains and async host start calls",
                mode, mainReady ? @"YES" : @"NO", bgReady ? @"YES" : @"NO"]];
            [strongSelf scheduleHealthCheckForRestart:mode isAll:isAll generation:myGen retried:YES];
            return;
        }

        // Stage 2 final verdict.

        if (isAll && !strongSelf.isStarted) {
            NSString *cachedURL = strongSelf.lastEntryURL;
            // Treat the bundled default name as "no real cache" so the
            // OTA warn still fires for hosts that initially bootstrapped
            // with the default URL and haven't yet swapped to an OTA-
            // resolved path. Such hosts have OTA risk but no signal in
            // lastEntryURL that distinguishes them.
            BOOL hasCustomCachedURL = cachedURL.length > 0
                && ![cachedURL isEqualToString:@"background.bundle"];
            if (hasCustomCachedURL) {
                // Preferred path: replay the host's last entry URL. On OTA
                // devices this is the OTA-resolved bundle, which keeps the
                // bg moduleId table aligned with the new main bundle and
                // avoids the silent → crash regression of falling back to
                // the bundled `background.bundle`.
                [BTLogger info:[NSString stringWithFormat:
                    @"restart(%@): bg not respawned by host AppDelegate; self-respawning with cached entryURL=%@",
                    mode, cachedURL]];
                [strongSelf startBackgroundRunnerWithEntryURL:cachedURL];
            } else {
                // Cache is empty or holds the bundled default. Fall back
                // and warn — on an OTA-equipped host this is the path
                // that historically produces a moduleId-mismatch crash on
                // the next cross-runtime RPC.
                [BTLogger warn:[NSString stringWithFormat:
                    @"restart(%@): bg not respawned and no custom cached entryURL (cache=%@); falling back to default. If host uses OTA bundles, wire AppDelegate.hostDidStart: to call startBackgroundRunner explicitly to avoid moduleId-mismatch.",
                    mode, cachedURL ?: @"<nil>"]];
                [strongSelf startBackgroundRunner];
            }
        }

        if (!strongSelf.mainSharedBridgeInstalled) {
            [BTLogger error:[NSString stringWithFormat:
                @"restart(%@): SharedBridge not re-installed in main runtime within ~%.1fs after reload (stage%@). "
                @"Host AppDelegate's hostDidStart: must invoke "
                @"+[BackgroundThreadManager installSharedBridgeInMainRuntime:newHost] "
                @"on the new RCTHost or main→bg RPC will silently fail.",
                mode, kRestartHealthCheckDelaySeconds * 2, stage]];
        }
    });
}

@end
