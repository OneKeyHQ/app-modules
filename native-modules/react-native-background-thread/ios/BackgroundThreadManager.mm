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
@end

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

        // (3) Trigger the main bridge reload. Equivalent to what
        // react-native-restart did, but now sequenced AFTER quiesce so the
        // SharedRPC race window is closed.
        RCTTriggerReloadCommandListeners(attributedReason);

        // (4) For mode=all, the new main host's hostDidStart will call
        // BackgroundThreadBridge.startBackgroundRunner again (isStarted==NO
        // now), recreating the bg host and re-installing the "background"
        // listener via BackgroundRunnerReactNativeDelegate.hostDidStart.
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
