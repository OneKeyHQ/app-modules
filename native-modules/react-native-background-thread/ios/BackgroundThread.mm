#import "BackgroundThread.h"
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

@interface BackgroundThread ()
@property (nonatomic, strong) BackgroundReactNativeDelegate *reactNativeFactoryDelegate;
@property (nonatomic, strong) RCTReactNativeFactory *reactNativeFactory;
@property (nonatomic, assign) BOOL hasListeners;
@property (nonatomic, assign) BOOL isStarted;
@end

@implementation BackgroundThread

static BackgroundThread *sharedInstance = nil;
static BOOL isStarted = NO;
static NSString *const MODULE_NAME = @"background";
static NSString *const MODULE_DEBUG_URL = @"http://localhost:8082/apps/mobile/background.bundle?platform=ios&dev=true&lazy=false&minify=false&inlineSourceMap=false&modulesOnly=false&runModule=true&excludeSource=true&sourcePaths=url-server&app=so.onekey.wallet&transform.routerRoot=app&transform.engine=hermes&transform.bytecode=1&unstable_transformProfile=hermes-stable";

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeBackgroundThreadSpecJSI>(params);
}

- (void)startBackgroundRunner {
  [self startBackgroundRunnerWithEntryURL:MODULE_DEBUG_URL];
}

- (void)startBackgroundRunnerWithEntryURL:(NSString *)entryURL {
  if (isStarted) {
      return;
    }
    isStarted = YES;
    
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
      __weak typeof(self) weakSelf = self;
      [self.reactNativeFactoryDelegate setOnMessageCallback:^(NSString *message) {
          [weakSelf emitOnBackgroundMessage:message];
      }];
    });
}

- (void)postBackgroundMessage:(nonnull NSString *)message {
  [self.reactNativeFactoryDelegate postMessage:std::string([message UTF8String])];
}

+ (NSString *)moduleName
{
  return @"BackgroundThread";
}

@end
