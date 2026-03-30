#import "ReactNativeDelegate.h"
#import <React/RCTBundleURLProvider.h>
#import <ReactCommon/RCTHost.h>
#import <BackgroundThread/BackgroundThreadManager.h>

@implementation ReactNativeDelegate

- (NSURL *)sourceURLForBridge:(RCTBridge *)bridge
{
  return [self bundleURL];
}

- (NSURL *)bundleURL
{
#if DEBUG
  return [[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"index"];
#else
  return [[NSBundle mainBundle] URLForResource:@"main" withExtension:@"jsbundle"];
#endif
}

- (void)hostDidStart:(RCTHost *)host
{
  [super hostDidStart:host];
  [BackgroundThreadManager installSharedBridgeInMainRuntime:host];

#if DEBUG
  NSString *bgURL = @"http://localhost:8082/background.bundle?platform=ios&dev=true&lazy=false&minify=false&inlineSourceMap=false&modulesOnly=false&runModule=true";
#else
  NSString *bgURL = @"background.bundle";
#endif
  [[BackgroundThreadManager sharedInstance] startBackgroundRunnerWithEntryURL:bgURL];
}

@end
