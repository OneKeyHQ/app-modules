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

        // Install SharedRPC with executor for cross-runtime notifications
        __block id capturedInstance = instance;
        RuntimeExecutor mainExecutor = [capturedInstance](std::function<void(jsi::Runtime &)> work) {
            [capturedInstance callFunctionOnBufferedRuntimeExecutor:[work](jsi::Runtime &rt) {
                work(rt);
            }];
        };
        SharedRPC::install(runtime, std::move(mainExecutor));
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

        #if DEBUG
            [self.reactNativeFactoryDelegate setJsBundleSource:std::string([entryURL UTF8String])];
        #endif

        [self.reactNativeFactory.rootViewFactory viewWithModuleName:MODULE_NAME
                                                     initialProperties:initialProperties
                                                         launchOptions:launchOptions];
    });
}

@end
