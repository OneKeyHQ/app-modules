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
// devices is a moduleId-mismatch crash waiting to happen.
@property (nonatomic, copy) NSString *lastEntryURL;

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
        NSError *error = [NSError errorWithDomain:@"BackgroundThread"
                                             code:1
                                         userInfo:@{NSLocalizedDescriptionKey: @"Background runtime not started"}];
        if (completion) completion(error);
        return;
    }

    // Verify the file exists
    if (![[NSFileManager defaultManager] fileExistsAtPath:path]) {
        NSError *error = [NSError errorWithDomain:@"BackgroundThread"
                                             code:2
                                         userInfo:@{NSLocalizedDescriptionKey:
                                                        [NSString stringWithFormat:@"Segment file not found: %@", path]}];
        if (completion) completion(error);
        return;
    }

    BOOL success = [self.reactNativeFactoryDelegate registerSegmentWithId:segmentId path:path];
    if (success) {
        if (completion) completion(nil);
    } else {
        NSError *error = [NSError errorWithDomain:@"BackgroundThread"
                                             code:3
                                         userInfo:@{NSLocalizedDescriptionKey:
                                                        @"Failed to register segment in background runtime"}];
        if (completion) completion(error);
    }
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
//   - Main healthy, bg not healthy (mode='all' only) → STABLE signal that
//     the host AppDelegate didn't re-spawn bg; self-respawn now without
//     waiting another cycle.
//   - Main NOT healthy → could be a slow device whose hostDidStart chain
//     hasn't finished yet. Reschedule stage 2 (+~3s more, total ~6s) and
//     defer the final verdict.
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

        // Stage 1 and main is NOT ready: could be a slow device; give it
        // one more stage before declaring integration broken. We DON'T
        // reschedule on the bg-only-failure path even when not retried —
        // mainReady=YES is a stable signal that the new host has run far
        // enough into hostDidStart that any startBackgroundRunner call
        // from the host would have landed already.
        if (!retried && !mainReady) {
            [BTLogger info:[NSString stringWithFormat:
                @"restart(%@): stage1 incomplete (mainReady=NO, bgReady=%@) — rescheduling stage2 to allow slow hostDidStart chains",
                mode, bgReady ? @"YES" : @"NO"]];
            [strongSelf scheduleHealthCheckForRestart:mode isAll:isAll generation:myGen retried:YES];
            return;
        }

        // Verdict: either mainReady is true but bg failed (stable, stage 1
        // is enough), or we're in stage 2 and something still failed.

        if (isAll && !strongSelf.isStarted) {
            NSString *cachedURL = strongSelf.lastEntryURL;
            if (cachedURL.length > 0) {
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
                // No cached URL means host never called start. Fall back to
                // the default-URL path. Same OTA-mismatch caveat applies
                // but is implausible in practice: an OTA-equipped host
                // would have called startBackgroundRunnerWithEntryURL: at
                // least once before triggering restart('all').
                [BTLogger warn:[NSString stringWithFormat:
                    @"restart(%@): bg not respawned and no cached entryURL; falling back to default. If host uses OTA bundles, wire AppDelegate.hostDidStart: to call startBackgroundRunner explicitly to avoid moduleId-mismatch.",
                    mode]];
                [strongSelf startBackgroundRunner];
            }
        }

        if (!strongSelf.mainSharedBridgeInstalled) {
            [BTLogger error:[NSString stringWithFormat:
                @"restart(%@): SharedBridge not re-installed in main runtime within ~%.1fs after reload (stage%@). "
                @"Host AppDelegate's hostDidStart: must invoke "
                @"+[BackgroundThreadManager installSharedBridgeInMainRuntime:newHost] "
                @"on the new RCTHost or main→bg RPC will silently fail.",
                mode, retried ? kRestartHealthCheckDelaySeconds * 2 : kRestartHealthCheckDelaySeconds,
                stage]];
        }
    });
}

@end
