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
@property (nonatomic, assign, readwrite) BOOL isStarted;
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
@end

// Empirical upper bound for the host's post-reload hostDidStart chain on
// low-end devices. iPhone-baseline measurements show ~500ms; 6x margin
// gives 3s. Beyond this, integration failure is more likely than a slow
// host — the health-check is meant to catch wiring bugs, not racing them.
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
        // the reload. Both signals we need (mainSharedBridgeInstalled and
        // isStarted) are our own state — there is no external notification
        // to wait on. We use a plain dispatch_after instead of an
        // RCTJavaScriptDidLoadNotification observer because that notification
        // is unreliable in bridgeless / NewArch (post timing is not
        // guaranteed across RN versions) and the observer pattern adds two
        // failure modes that this code is otherwise immune to:
        //   - If the notification never fires, the observer leaks and any
        //     later reload that finally fires it will trigger stale checks.
        //   - Concurrent restart() calls each register an observer, all of
        //     which fire on the next notification using the latest flag
        //     state (cross-generation misreporting).
        // dispatch_after, paired with a per-restart `myGen` generation
        // capture, has neither hazard.
        self.mainSharedBridgeInstalled = NO;
        NSUInteger myGen = ++self.restartGeneration;
        __weak BackgroundThreadManager *weakSelf = self;
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW,
                                     (int64_t)(kRestartHealthCheckDelaySeconds * NSEC_PER_SEC)),
                       dispatch_get_main_queue(), ^{
            BackgroundThreadManager *strongSelf = weakSelf;
            if (!strongSelf) return;
            // A newer restart() has bumped the generation while we were
            // waiting — its own check will run; bail to avoid reading flags
            // that now belong to the newer cycle.
            if (strongSelf.restartGeneration != myGen) {
                [BTLogger info:[NSString stringWithFormat:
                    @"restart(%@) health-check superseded by newer restart (gen %lu → %lu)",
                    mode, (unsigned long)myGen, (unsigned long)strongSelf.restartGeneration]];
                return;
            }
            if (isAll && !strongSelf.isStarted) {
                // Self-respawn falls through startBackgroundRunner →
                // startBackgroundRunnerWithEntryURL:@"background.bundle"
                // (release) / debug URL (debug). Any custom entryURL the
                // host previously passed (e.g. OTA-resolved bundle path) is
                // gone because reactNativeFactoryDelegate was released for
                // mode='all'. Warn explicitly so a host with broken
                // AppDelegate wiring + OTA paths doesn't get silently
                // pinned to the default bundle.
                [BTLogger info:@"restart(all): bg not respawned by host AppDelegate; self-respawning with default entry URL"];
                [BTLogger warn:@"restart(all): self-respawn uses the default entry URL; any custom entryURL set via startBackgroundRunnerWithEntryURL: is lost. Wire host AppDelegate.hostDidStart: to call startBackgroundRunner explicitly if you need to preserve a custom OTA bundle path."];
                [strongSelf startBackgroundRunner];
            }
            if (!strongSelf.mainSharedBridgeInstalled) {
                [BTLogger error:[NSString stringWithFormat:
                    @"restart(%@): SharedBridge not re-installed in main runtime within %.1fs after reload. "
                    @"Host AppDelegate's hostDidStart: must invoke "
                    @"+[BackgroundThreadManager installSharedBridgeInMainRuntime:newHost] "
                    @"on the new RCTHost or main→bg RPC will silently fail.",
                    mode, kRestartHealthCheckDelaySeconds]];
            }
        });

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

@end
