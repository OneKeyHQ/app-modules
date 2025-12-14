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

@interface BackgroundThreadManager ()
@property (nonatomic, strong) BackgroundReactNativeDelegate *reactNativeFactoryDelegate;
@property (nonatomic, strong) RCTReactNativeFactory *reactNativeFactory;
@property (nonatomic, assign) BOOL hasListeners;
@property (nonatomic, assign, readwrite) BOOL isStarted;
@property (nonatomic, copy) void (^onMessageCallback)(NSString *message);
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

#pragma mark - Public Methods

- (void)startBackgroundRunner {
    [self startBackgroundRunnerWithEntryURL:MODULE_DEBUG_URL];
}

- (void)startBackgroundRunnerWithEntryURL:(NSString *)entryURL {
    if (self.isStarted) {
        return;
    }
    self.isStarted = YES;
    
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
        
        __weak __typeof__(self) weakSelf = self;
        [self.reactNativeFactoryDelegate setOnMessageCallback:^(NSString *message) {
            if (weakSelf.onMessageCallback) {
                weakSelf.onMessageCallback(message);
            }
        }];
    });
}

- (void)postBackgroundMessage:(NSString *)message {
    if (self.reactNativeFactoryDelegate) {
        [self.reactNativeFactoryDelegate postMessage:std::string([message UTF8String])];
    }
}

- (void)setOnMessageCallback:(void (^)(NSString *message))callback {
    self.onMessageCallback = callback;
}

@end
