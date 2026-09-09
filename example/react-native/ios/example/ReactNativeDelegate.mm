#import "ReactNativeDelegate.h"
#import <React/RCTBundleURLProvider.h>
#import <ReactCommon/RCTHost.h>
#import <BackgroundThread/BackgroundThreadManager.h>
#import <CoreText/CoreText.h>

@implementation ReactNativeDelegate

- (instancetype)init
{
  self = [super init];
  if (self) {
    // Market's RN header must resolve the same fonts as its native rows on first layout.
    NSURL *resourceURL = [[NSBundle mainBundle] URLForResource:@"NativeListResources" withExtension:@"bundle"];
    NSBundle *resources = resourceURL ? [NSBundle bundleWithURL:resourceURL] : nil;
    for (NSString *weight in @[@"Regular", @"Medium", @"SemiBold", @"Bold"]) {
      NSURL *fontURL = [resources URLForResource:[@"Roobert-" stringByAppendingString:weight] withExtension:@"ttf"];
      if (fontURL) CTFontManagerRegisterFontsForURL((__bridge CFURLRef)fontURL, kCTFontManagerScopeProcess, nil);
    }
  }
  return self;
}

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
  NSString *bgURL = [[[RCTBundleURLProvider sharedSettings] jsBundleURLForBundleRoot:@"background"] absoluteString];
#else
  NSString *bgURL = @"background.bundle";
#endif
  [[BackgroundThreadManager sharedInstance] startBackgroundRunnerWithEntryURL:bgURL];
}

@end
