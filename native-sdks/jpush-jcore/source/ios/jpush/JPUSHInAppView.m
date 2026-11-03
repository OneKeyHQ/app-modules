#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHInAppView.h"
#import "JCORELog.h"
#import "JPUSHInAppBannerView.h"
#import "JPUSHInAppModalView.h"
#import "JPUSHInAppFullView.h"
@interface JPUSHInAppView () {
  long long _inappType;
  JPUSHInAppConfig *_config;
  __weak id<JPUSHInAppViewDelegate> _delegate;
}
@end
#define INAPP_VIEW_LOG(lineValue, message, ...)                                      \
  [JCORELog log:YES level:0x3ff flag:0x100 context:0                             \
      file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"               \
           "ios-jpush/JPush/JPush/Controller/InApp/JPUSHInAppView.m"           \
      function:__PRETTY_FUNCTION__ line:lineValue tag:nil format:message, ##__VA_ARGS__]
@implementation JPUSHInAppView
@synthesize inappType = _inappType, config = _config, delegate = _delegate;
+ (instancetype)createViewWithConfig:(JPUSHInAppConfig *)config
                          delegate:(id<JPUSHInAppViewDelegate>)delegate {
  if (![NSThread isMainThread]) {
    INAPP_VIEW_LOG(0x1b, @"[%s] thread is not mainThread", __PRETTY_FUNCTION__);
    return nil;
  }
  JPUSHInAppView *view;
  long long type;
  if (config.type == 10 || config.type == 11) {
    view = [[JPUSHInAppBannerView alloc] initWithConfig:config delegate:delegate];
    type = 0;
  } else if (config.type == 20 || config.type == 21) {
    view = [[JPUSHInAppModalView alloc] initWithConfig:config delegate:delegate];
    type = 1;
  } else if (config.type == 30 || config.type == 31) {
    view = [[JPUSHInAppFullView alloc] initWithConfig:config delegate:delegate];
    type = 2;
  } else {
    INAPP_VIEW_LOG(0x2b, @"unsupport inapp style");
    return nil;
  }
  view.inappType = type;
  return view;
}
- (instancetype)initWithConfig:(JPUSHInAppConfig *)config
                     delegate:(id<JPUSHInAppViewDelegate>)delegate {
  if ((self = [super init])) {
    _config = config;
    _delegate = delegate;
  }
  return self;
}
- (void)showInView:(UIView *)view {
  if (self.delegate &&
      [self.delegate respondsToSelector:@selector(jpushInAppViewShow:)])
    [self.delegate jpushInAppViewShow:self];
}
- (void)dismiss {
  if (self.delegate &&
      [self.delegate respondsToSelector:@selector(jpushInAppViewDismiss:)])
    [self.delegate jpushInAppViewDismiss:self];
}
@end
#endif
