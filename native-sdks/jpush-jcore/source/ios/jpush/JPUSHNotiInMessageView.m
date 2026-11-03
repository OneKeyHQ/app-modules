#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHNotiInMessageView.h"
#import "JPUSHHelper.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"
#include <math.h>
@interface JPUSHNotiInMessageView () {
  BOOL _isShow;
  BOOL _isCallback;
  BOOL _isNetwork;
  JPUSHNotiInMessageConfig *_config;
  __weak id<JPushNotiInMessageViewDelegate> _delegate;
  NSLayoutConstraint *_leftConstraint;
  NSLayoutConstraint *_rightConstraint;
  NSLayoutConstraint *_topConstraint;
  NSLayoutConstraint *_heightConstraint;
  NSLayoutConstraint *_bottomConstraint;
  WKWebView *_webView;
  JPushNotiInAppWeakScriptMessageDelegate *_messageDelegate;
}
@end
#define NOTI_VIEW_LOG(flagValue, lineValue, message, ...)                        \
  [JCORELog log:YES level:0x3ff flag:flagValue context:0                          \
      file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"                \
           "ios-jpush/JPush/JPush/Controller/NotiInApp/JPUSHNotiInMessageView.m" \
      function:__PRETTY_FUNCTION__ line:lineValue tag:nil                         \
      format:message, ##__VA_ARGS__]
@implementation JPUSHNotiInMessageView
@synthesize webView = _webView, messageDelegate = _messageDelegate;
@synthesize isCallback = _isCallback, isNetwork = _isNetwork, config = _config;
@synthesize isShow = _isShow, delegate = _delegate;
@synthesize leftConstraint = _leftConstraint, rightConstraint = _rightConstraint;
@synthesize topConstraint = _topConstraint, heightConstraint = _heightConstraint;
@synthesize bottomConstraint = _bottomConstraint;
@dynamic hash, superclass, description, debugDescription;
- (void)dealloc {
  [self removeScriptMessageHandler];
  self.messageDelegate = nil;
  NOTI_VIEW_LOG(0x100, 0x38, @"JPushMessageView is dealloc");
}
- (JPushNotiInAppWeakScriptMessageDelegate *)messageDelegate {
  if (!_messageDelegate)
    _messageDelegate = [[JPushNotiInAppWeakScriptMessageDelegate alloc] initWithDelegate:self];
  return _messageDelegate;
}
- (instancetype)initWithConfig:(JPUSHNotiInMessageConfig *)config
                     delegate:(id<JPushNotiInMessageViewDelegate>)delegate {
  if ((self = [super init])) {
    _config = config;
    _delegate = delegate;
    _isCallback = NO;
    _isNetwork = NO;
    [self setView];
    [self initGesture];
  }
  return self;
}
- (instancetype)initWithFrame:(CGRect)frame WithConfig:(JPUSHNotiInMessageConfig *)config
                    delegate:(id<JPushNotiInMessageViewDelegate>)delegate {
  if ((self = [super initWithFrame:frame])) {
    _config = config;
    _delegate = delegate;
    [self setView];
    [self initGesture];
  }
  return self;
}
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
- (void)setView {
  if (!NSThread.isMainThread) {
    NOTI_VIEW_LOG(0x100, 0x5c, @"[%s] thread is not mainThread", __PRETTY_FUNCTION__);
    return;
  }
  if (!_config) {
    NOTI_VIEW_LOG(8, 0x60, @"[%s] config is nil", __PRETTY_FUNCTION__);
    return;
  }
  self.backgroundColor = self.config.backgroudColor ?: UIColor.clearColor;
  WKWebViewConfiguration *configuration = [WKWebViewConfiguration new];
  WKPreferences *preferences = [WKPreferences new];
  preferences.javaScriptEnabled = YES;
  preferences.javaScriptCanOpenWindowsAutomatically = NO;
  configuration.preferences = preferences;
  configuration.userContentController = [WKUserContentController new];
  configuration.processPool = [WKProcessPool new];
  _webView = [[WKWebView alloc] initWithFrame:self.frame configuration:configuration];
  _webView.backgroundColor = UIColor.clearColor;
  _webView.scrollView.backgroundColor = UIColor.clearColor;
  _webView.opaque = NO;
  _webView.UIDelegate = self;
  _webView.navigationDelegate = self;
  _webView.allowsBackForwardNavigationGestures = YES;
  _webView.scrollView.showsVerticalScrollIndicator = NO;
  _webView.scrollView.showsHorizontalScrollIndicator = NO;
  _webView.scrollView.bounces = NO;
  if (@available(iOS 11.0, *))
    _webView.scrollView.contentInsetAdjustmentBehavior = 2;
  [self addSubview:_webView];
  _webView.translatesAutoresizingMaskIntoConstraints = NO;
  [self addConstraints:@[
    [NSLayoutConstraint constraintWithItem:_webView attribute:1 relatedBy:0
        toItem:self attribute:1 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:_webView attribute:2 relatedBy:0
        toItem:self attribute:2 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:_webView attribute:3 relatedBy:0
        toItem:self attribute:3 multiplier:1 constant:0],
    [NSLayoutConstraint constraintWithItem:_webView attribute:4 relatedBy:0
        toItem:self attribute:4 multiplier:1 constant:0]
  ]];
  if (self.config.filepath.length) {
    NSURL *url = [NSURL fileURLWithPath:self.config.filepath];
    if (!url) {
      NOTI_VIEW_LOG(0x100, 0x97, @"[%s] fileurl is nil", __PRETTY_FUNCTION__);
    } else if (@available(iOS 9.0, *)) {
      [_webView loadFileURL:url allowingReadAccessToURL:url];
    } else {
      NSURL *temporary = [self fileURLForBuggyWKWebView8WithFileURL:url];
      if (temporary)
        [_webView loadRequest:[NSURLRequest requestWithURL:temporary]];
      else
        NOTI_VIEW_LOG(0x100, 0x93, @"[%s] tempurl is nil", __PRETTY_FUNCTION__);
    }
  } else if (self.config.networkPath.length) {
    self.isNetwork = YES;
    NSURL *url = [NSURL URLWithString:self.config.networkPath];
    if (url)
      [_webView loadRequest:[[NSURLRequest alloc] initWithURL:url]];
    else
      NOTI_VIEW_LOG(0x100, 0xa2, @"[%s] netowrkUrl is nil", __PRETTY_FUNCTION__);
  } else {
    NOTI_VIEW_LOG(0x100, 0xa5, @"[%s] networkPath is nil", __PRETTY_FUNCTION__);
  }
  [self addScriptMessageHandler];
}
#pragma clang diagnostic pop
- (void)initGesture {
  if (!NSThread.isMainThread) {
    NOTI_VIEW_LOG(0x100, 0xaf, @"[%s] thread is not mainThread", __PRETTY_FUNCTION__);
    return;
  }
  if (!_config) {
    NOTI_VIEW_LOG(0x100, 0xb3, @"[%s] config is nil", __PRETTY_FUNCTION__);
    return;
  }
  UISwipeGestureRecognizer *up = [[UISwipeGestureRecognizer alloc]
      initWithTarget:self action:@selector(fadeMeout)];
  up.direction = 4;
  up.delegate = self;
  [self addGestureRecognizer:up];
  UISwipeGestureRecognizer *down = [[UISwipeGestureRecognizer alloc]
      initWithTarget:self action:@selector(handleTap:)];
  down.direction = 8;
  down.delegate = self;
  [self.webView addGestureRecognizer:down];
}
- (void)fadeMeout {
  if ([self.delegate respondsToSelector:@selector(jPushMessageViewDelegateFadeOut:)])
    [self.delegate jPushMessageViewDelegateFadeOut:self];
}
- (void)handleTap:(UIGestureRecognizer *)gesture {
  if (gesture.state == 3 &&
      [self.delegate respondsToSelector:@selector(jPushMessageViewDidTap:)])
    [self.delegate jPushMessageViewDidTap:self];
}
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gesture
    shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)other {
  return YES;
}
- (void)addScriptMessageHandler {
  self.messageDelegate = [[JPushNotiInAppWeakScriptMessageDelegate alloc] initWithDelegate:self];
  [_webView.configuration.userContentController addScriptMessageHandler:self.messageDelegate
      name:@"inAppClick"];
  [_webView.configuration.userContentController addScriptMessageHandler:self.messageDelegate
      name:@"getTplData"];
  [_webView.configuration.userContentController addScriptMessageHandler:self.messageDelegate
      name:@"onLoadCallback"];
  [_webView.configuration.userContentController addScriptMessageHandler:self.messageDelegate
      name:@"reportData"];
  [_webView.configuration.userContentController addScriptMessageHandler:self.messageDelegate
      name:@"getTplExtraData"];
}
- (void)removeScriptMessageHandler {
  [_webView.configuration.userContentController removeScriptMessageHandlerForName:@"inAppClick"];
  [_webView.configuration.userContentController removeScriptMessageHandlerForName:@"getTplData"];
  [_webView.configuration.userContentController removeScriptMessageHandlerForName:@"onLoadCallback"];
  [_webView.configuration.userContentController removeScriptMessageHandlerForName:@"reportData"];
  [_webView.configuration.userContentController removeScriptMessageHandlerForName:@"getTplExtraData"];
}
- (void)userContentController:(WKUserContentController *)controller
     didReceiveScriptMessage:(WKScriptMessage *)message {
  NOTI_VIEW_LOG(0x100, 0xec, @"WKScriptMessage body is %@,WKScriptMessage name is %@",
      message.body, message.name);
  if ([message.name isEqualToString:@"inAppClick"])
    [self inAppClick:message.body];
  else if ([message.name isEqualToString:@"getTplData"])
    [self getTplData:message.body];
  else if ([message.name isEqualToString:@"onLoadCallback"])
    [self onLoadCallback:message.body];
  else if ([message.name isEqualToString:@"reportData"])
    [self reportData:message.body];
  else if ([message.name isEqualToString:@"getTplExtraData"])
    [self getTplExtraData:message.body];
  else
    NOTI_VIEW_LOG(0x100, 0xf8, @"[%s] %@ message.name is not support",
        __PRETTY_FUNCTION__, message.name);
}
- (void)inAppClick:(id)param {
  if (!NSThread.isMainThread) {
    NOTI_VIEW_LOG(0x100, 0xff, @"[%s] thread is not mainThread", __PRETTY_FUNCTION__);
    return;
  }
  if (![param isKindOfClass:NSDictionary.class] || !param) {
    NOTI_VIEW_LOG(0x100, 0x103, @"[%s] param is bad", __PRETTY_FUNCTION__);
    return;
  }
  if ([self.delegate respondsToSelector:@selector(jPushInMessageClik:withParam:)])
    [self.delegate jPushInMessageClik:self withParam:param];
}
- (void)getTplData:(id)param {
  if (!NSThread.isMainThread) {
    NOTI_VIEW_LOG(0x100, 0x10e, @"[%s] thread is not mainThread", __PRETTY_FUNCTION__);
    return;
  }
  if (!self.config.h5Dic) {
    NOTI_VIEW_LOG(0x100, 0x112, @"[%s] h5dic is nil", __PRETTY_FUNCTION__);
    return;
  }
  NSString *json = [self dicToString:self.isNetwork ? self.config.networkH5Dic : self.config.h5Dic];
  if (!json.length) {
    NOTI_VIEW_LOG(0x100, 0x11d, @"[%s] h5 param is nil", __PRETTY_FUNCTION__);
    return;
  }
  [self.webView evaluateJavaScript:[NSString stringWithFormat:@"parseInfo('%@')", json]
      completionHandler:^(id result, NSError *error) {
        if (error) {
          NOTI_VIEW_LOG(0x100, 0x123, @"[%s] evaluateJavaScript is failure", __PRETTY_FUNCTION__);
          [self onLoadCallback:@{@"statusCode" : @11}];
        } else {
          NOTI_VIEW_LOG(0x100, 0x127, @"[%s] evaluateJavaScript is success", __PRETTY_FUNCTION__);
        }
      }];
}
- (void)onLoadCallback:(id)param {
  if (!NSThread.isMainThread) {
    NOTI_VIEW_LOG(0x100, 0x12f, @"[%s] thread is not mainThread", __PRETTY_FUNCTION__);
    return;
  }
  if (![param isKindOfClass:NSDictionary.class] || !param) {
    NOTI_VIEW_LOG(0x100, 0x133, @"[%s] param is bad", __PRETTY_FUNCTION__);
    return;
  }
  if (self.isCallback) {
    NOTI_VIEW_LOG(0x100, 0x138, @"[%s] is callbak", __PRETTY_FUNCTION__);
    return;
  }
  self.isCallback = YES;
  if ([self.delegate respondsToSelector:@selector(jPushInMessageCallback:withParam:)])
    [self.delegate jPushInMessageCallback:self withParam:param];
}
- (void)reportData:(id)param {
  if (!NSThread.isMainThread) {
    NOTI_VIEW_LOG(0x100, 0x144, @"[%s] thread is not mainThread", __PRETTY_FUNCTION__);
    return;
  }
  if (![param isKindOfClass:NSDictionary.class] || !param) {
    NOTI_VIEW_LOG(0x100, 0x148, @"[%s] param is bad", __PRETTY_FUNCTION__);
    return;
  }
  if ([self.delegate respondsToSelector:@selector(jPushInMessageReportdata:withParam:)])
    [self.delegate jPushInMessageReportdata:self withParam:param];
}
- (void)getTplExtraData:(id)param {
  if (!NSThread.isMainThread) {
    NOTI_VIEW_LOG(0x100, 0x153, @"[%s] thread is not mainThread", __PRETTY_FUNCTION__);
    return;
  }
  NSString *model = [JCOREUtilities modelName];
  NSString *system = [JCOREUtilities systemVersion];
  NSString *language = [JCOREUtilities systemLanguage];
  double screenWidth = UIScreen.mainScreen.bounds.size.width;
  screenWidth *= UIScreen.mainScreen.scale;
  double screenHeight = UIScreen.mainScreen.bounds.size.height;
  screenHeight *= UIScreen.mainScreen.scale;
  double scale = UIScreen.mainScreen.scale;
  NSString *orientation = [JPUSHHelper isLandscape] ? @"landscape" : @"portrait";
  double timestamp = [JCOREUtilities currentTimestamp];
  long long seconds = isnan(timestamp) ? 0 : timestamp >= 0x1p63 ? INT64_MAX
      : timestamp <= -0x1p63 ? INT64_MIN : (long long)timestamp;
  BOOL dark = [JPUSHHelper isDarkAppearance];
  BOOL is24Hour = [JPUSHHelper is24Hour];
  NSDictionary *device = @{
    @"manufacturer" : @"Apple", @"model" : model ?: @"", @"romUIVersion" : @"",
    @"osVersion" : system ?: @"", @"language" : language ?: @"",
    @"screenW" : @(screenWidth), @"screenH" : @(screenHeight),
    @"scaledDensity" : @(scale),
    @"orientation" : orientation,
    @"currentTime" : @((long long)((unsigned long long)seconds * 1000)),
    @"nightMode" : @(dark), @"is24Hour" : @(is24Hour)
  };
  NSDictionary *landscape = @{
    @"w" : @(self.config.width * UIScreen.mainScreen.scale),
    @"h" : @(self.config.height * UIScreen.mainScreen.scale),
    @"r" : @(self.config.toH5WhRatio)
  };
  NSDictionary *portrait = @{
    @"w" : @(self.config.width * UIScreen.mainScreen.scale),
    @"h" : @(self.config.height * UIScreen.mainScreen.scale),
    @"r" : @(self.config.toH5WhRatio)
  };
  NSString *json = [self dicToString:@{
    @"device" : device, @"container" : @{@"landscape" : landscape, @"portrait" : portrait}
  }];
  if (!json.length) {
    NOTI_VIEW_LOG(0x100, 0x175, @"[%s] h5ExtraDataStr is nil", __PRETTY_FUNCTION__);
    return;
  }
  [self.webView evaluateJavaScript:[NSString stringWithFormat:@"setRem('%@')", json]
      completionHandler:^(id result, NSError *error) {
        if (error) {
          NOTI_VIEW_LOG(0x100, 0x17c, @"[%s] evaluateJavaScript is failure", __PRETTY_FUNCTION__);
          [self onLoadCallback:@{@"statusCode" : @11}];
        } else {
          NOTI_VIEW_LOG(0x100, 0x180, @"[%s] evaluateJavaScript is success", __PRETTY_FUNCTION__);
        }
      }];
}
- (void)webView:(WKWebView *)webView didFinishNavigation:(WKNavigation *)navigation {
  dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 10000000000),
      dispatch_get_main_queue(), ^{ [self onLoadCallback:@{@"statusCode" : @10}]; });
}
- (void)webView:(WKWebView *)webView
    didFailProvisionalNavigation:(WKNavigation *)navigation withError:(NSError *)error {
  [self onLoadCallback:@{@"statusCode" : @10}];
}
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
- (void)webView:(WKWebView *)webView
    decidePolicyForNavigationAction:(WKNavigationAction *)action
    decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler {
  BOOL allowed = ![webView.URL.absoluteString hasPrefix:@"https://itunes.apple.com"];
  if (!allowed)
    [UIApplication.sharedApplication openURL:action.request.URL];
  decisionHandler(allowed);
}
#pragma clang diagnostic pop
- (NSString *)dicToString:(id)dictionary {
  if (dictionary && [dictionary isKindOfClass:NSDictionary.class]) {
    NSData *data = [self dicToData:dictionary];
    if (!data.length)
      return nil;
    return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
  }
  NOTI_VIEW_LOG(0x100, 0x1a9, @"[%s] dic is bad", __PRETTY_FUNCTION__);
  return nil;
}
- (NSData *)dicToData:(id)dictionary {
  if (![NSJSONSerialization isValidJSONObject:dictionary]) {
    NOTI_VIEW_LOG(0x100, 0x1b6, @"[%s] serialization unknown type ", __PRETTY_FUNCTION__);
    return nil;
  }
  NSData *data = nil;
  @try {
    NSError *error = nil;
    data = [NSJSONSerialization dataWithJSONObject:dictionary options:0 error:&error];
    if (error)
      NOTI_VIEW_LOG(0x100, 0x1bd, @"json serialization failed with error:%@", error);
  } @catch (NSException *exception) {
    NOTI_VIEW_LOG(0x100, 0x1c0, @"[bomb] [%s] is exception", __PRETTY_FUNCTION__);
  }
  return data;
}
- (NSURL *)fileURLForBuggyWKWebView8WithFileURL:(NSURL *)url {
  if (!url.isFileURL)
    return nil;
  NSError *error = nil;
  if (![url checkResourceIsReachableAndReturnError:&error])
    return nil;
  NSURL *result = nil;
  @try {
    NSFileManager *manager = NSFileManager.defaultManager;
    NSURL *directory = [[NSURL fileURLWithPath:NSTemporaryDirectory()]
        URLByAppendingPathComponent:@"JPUSHInMessageImage"];
    [manager createDirectoryAtURL:directory withIntermediateDirectories:YES
        attributes:nil error:&error];
    result = [directory URLByAppendingPathComponent:url.lastPathComponent];
    [manager removeItemAtURL:result error:&error];
    [manager copyItemAtURL:url toURL:result error:&error];
  } @catch (NSException *exception) {
    NOTI_VIEW_LOG(0x100, 0x1d4, @"[bomb][%s] is exception", __PRETTY_FUNCTION__);
  }
  return result;
}
@end
#endif
