#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHInMessageManager.h"
#import "JPUSHNotiInMessageH5Manager.h"
#import "JPUSHPropertyModels.h"
#import "JPUSHMessageReport.h"
#import "JPUSHService.h"
#import "JPUSHHelper.h"
#import "JCOREThread.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import <math.h>
#define MESSAGE_LOG(lineValue, message, ...)                                   \
  [JCORELog log:YES level:0x3ff flag:0x100 context:0                            \
      file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"              \
           "ios-jpush/JPush/JPush/Controller/NotiInApp/JPUSHInMessageManager.m" \
      function:__PRETTY_FUNCTION__ line:lineValue tag:nil                       \
      format:message, ##__VA_ARGS__]
static long long MessageSignedInteger(double value) {
  return isnan(value) ? 0 : value >= 0x1p63 ? INT64_MAX
      : value <= -0x1p63 ? INT64_MIN : (long long)value;
}
@implementation JPUSHInMessageManager
@synthesize hasShowFullView = _hasShowFullView, hasShowModelView = _hasShowModelView;
@synthesize isShowProcess = _isShowProcess, notiInMessageDelegate = _notiInMessageDelegate;
@synthesize inAppMessageDelegate = _inAppMessageDelegate, notiManager = _notiManager;
@synthesize inAppManager = _inAppManager, h5Manager = _h5Manager, tempMessageViews = _tempMessageViews;
@synthesize bannerView = _bannerView, nextMessageViewTimer = _nextMessageViewTimer;
@synthesize currentPage = _currentPage, delayShowTimer = _delayShowTimer, inappShowCursor = _inappShowCursor;
@dynamic hash, superclass, description, debugDescription;
- (JPUSHNotiInMessageH5Manager *)h5Manager {
  if (!_h5Manager)
    _h5Manager = [[JPUSHNotiInMessageH5Manager alloc] initWithMaxCount:20];
  return _h5Manager;
}
- (NSMutableSet *)tempMessageViews {
  if (!_tempMessageViews)
    _tempMessageViews = [[NSMutableSet alloc] init];
  return _tempMessageViews;
}
+ (instancetype)shareManager {
  static JPUSHInMessageManager *instance;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{ instance = [[super allocWithZone:NULL] init]; });
  return instance;
}
+ (instancetype)allocWithZone:(NSZone *)zone {
  return [JPUSHInMessageManager shareManager];
}
- (id)copyWithZone:(NSZone *)zone {
  return [JPUSHInMessageManager shareManager];
}
- (instancetype)init {
  if ((self = [super init])) {
    self.notiManager = [[JPUSHNotiInMessageManager alloc] init];
    self.notiManager.delegate = self;
    self.inAppManager = [[JPUSHInAppManager alloc] init];
    self.inAppManager.delegate = self;
    self.currentPage = nil;
    self.hasShowFullView = NO;
    self.hasShowModelView = NO;
    self.inappShowCursor = 0;
    self.isShowProcess = NO;
    [self clearTopBannerStatus];
  }
  return self;
}
- (void)dealloc {
  [NSNotificationCenter.defaultCenter removeObserver:self];
}
+ (void)handleNotiInMessageDic:(id)message {
  [[JPUSHInMessageManager shareManager] handleInMessageDic:message];
}
- (void)handleInMessageDic:(id)message {
  [self.notiManager handleInMessageDic:message];
}
- (void)handleRevokeMessageId:(id)messageID {
  if (messageID && [messageID isKindOfClass:NSString.class]) {
    [self.notiManager handleRevokeMessageIdList:messageID];
    [self.inAppManager handleRevokeMessageIdList:messageID];
  }
}
+ (void)handleInMessageDic:(id)message {
  [[JPUSHInMessageManager shareManager] handleInAppDic:message];
}
- (void)handleInAppDic:(id)message {
  [self.inAppManager handleInAppDic:message];
}
+ (void)pageEnterTo:(id)page {
  [[self shareManager] pageEnterTo:page];
}
+ (void)pageLeave:(id)page {
  [[self shareManager] pageLeave:page];
}
- (void)pageEnterTo:(id)page {
  dispatch_async(dispatch_get_main_queue(), ^{
    MESSAGE_LOG(0x97, @"inapp - pageEnterTo:%@", page);
    [self dismissLastPageInappView];
    self.currentPage = page;
    self.hasShowFullView = NO;
    self.hasShowModelView = NO;
    [self invalidateNextMessageViewTimer];
    [self startDelayShowTimer];
    self.inappShowCursor = 0;
    self.isShowProcess = NO;
  });
}
- (void)pageLeave:(id)page {
  dispatch_async(dispatch_get_main_queue(), ^{
    if (page && [page isKindOfClass:NSString.class] && [page isEqualToString:self.currentPage]) {
      MESSAGE_LOG(0xab, @"inapp - pageLeave:%@", page);
      [self dismissLastPageInappView];
      [self invalidateDelayShowTimer];
      [self invalidateNextMessageViewTimer];
      self.currentPage = nil;
      self.hasShowFullView = NO;
      self.hasShowModelView = NO;
      self.inappShowCursor = 0;
      self.isShowProcess = NO;
    }
  });
}
- (void)dismissLastPageInappView {
  if (NSThread.isMainThread) {
    MESSAGE_LOG(0xc1, @"inapp - dismissLastPageInappView");
    if (self.bannerView) {
      [self.bannerView removeFromSuperview];
      self.bannerView = nil;
    }
  }
}
- (void)invalidateDelayShowTimer {
  MESSAGE_LOG(0xcb, @"inapp - validate delay show timer");
  if (self.delayShowTimer && self.delayShowTimer.isValid)
    [self.delayShowTimer invalidate];
  self.delayShowTimer = nil;
}
- (void)startDelayShowTimer {
  [self invalidateDelayShowTimer];
  float delay = self.inAppManager.pageDelayTime;
  MESSAGE_LOG(0xd5, @"inapp - start delay show timer - %f", (double)delay);
  self.delayShowTimer = [NSTimer scheduledTimerWithTimeInterval:(double)delay target:self
      selector:@selector(pageDelayShowInapp) userInfo:nil repeats:NO];
}
- (void)pageDelayShowInapp {
  dispatch_async(dispatch_get_main_queue(), ^{ [self beginShowInApp]; });
}
- (void)startNextMessageViewTimer:(double)delay {
  [self invalidateNextMessageViewTimer];
  MESSAGE_LOG(0xe3, @"inapp - start NextMessage delay show timer - %f", delay);
  self.nextMessageViewTimer = [NSTimer scheduledTimerWithTimeInterval:delay target:self
      selector:@selector(nextInappViewShow) userInfo:nil repeats:NO];
}
- (void)invalidateNextMessageViewTimer {
  MESSAGE_LOG(0xe9, @"inapp - invalidate NextMessage delay show timer");
  if (self.nextMessageViewTimer && self.nextMessageViewTimer.isValid) {
    [self.nextMessageViewTimer invalidate];
    self.nextMessageViewTimer = nil;
  }
}
- (void)nextInappViewShow {
  dispatch_async(dispatch_get_main_queue(), ^{ [self showInappProcess]; });
}
- (void)beginShowInApp {
  if (self.isShowProcess) {
    MESSAGE_LOG(0xfb, @"inapp - in show process, return");
    return;
  }
  self.isShowProcess = YES;
  [self showInappProcess];
}
- (void)showInappProcess {
  switch (self.inappShowCursor) {
    case 0:
      MESSAGE_LOG(0x108, @"inapp - ========= start show inapp process ========");
      if (self.hasShowFullView) {
        MESSAGE_LOG(0x10a, @"inapp - has show full view");
        self.inappShowCursor = (long long)((unsigned long long)self.inappShowCursor + 1);
        [self showInappProcess];
      } else {
        [[JPUSHInAppManager shareManager] getInAppContentWithType:2 currentPageName:self.currentPage
            completion:^(JPUSHInAppConfig *config) {
          if (config) {
            MESSAGE_LOG(0x115, @"inapp - show full view");
            [self configInAppView:config];
          } else {
            MESSAGE_LOG(0x110, @"inapp - no full view");
            self.inappShowCursor = (long long)((unsigned long long)self.inappShowCursor + 1);
            [self showInappProcess];
          }
        }];
      }
      break;
    case 1:
      if (self.hasShowModelView) {
        MESSAGE_LOG(0x120, @"inapp - has show model view");
        self.inappShowCursor = (long long)((unsigned long long)self.inappShowCursor + 1);
        [self showInappProcess];
      } else {
        [[JPUSHInAppManager shareManager] getInAppContentWithType:1 currentPageName:self.currentPage
            completion:^(JPUSHInAppConfig *config) {
          if (config) {
            MESSAGE_LOG(0x12b, @"inapp - show model view");
            [self configInAppView:config];
          } else {
            MESSAGE_LOG(0x126, @"inapp - no model view");
            self.inappShowCursor = (long long)((unsigned long long)self.inappShowCursor + 1);
            [self showInappProcess];
          }
        }];
      }
      break;
    case 2: {
      [[JPUSHInAppManager shareManager] getInAppContentWithType:0 currentPageName:self.currentPage
          completion:^(JPUSHInAppConfig *config) {
        if (config) {
          MESSAGE_LOG(0x13c, @"inapp - show banner view");
          [self configInAppView:config];
        } else {
          MESSAGE_LOG(0x137, @"inapp - no banner view");
          self.inappShowCursor = (long long)((unsigned long long)self.inappShowCursor + 1);
          [self showInappProcess];
        }
      }];
      break;
    }
    case 3: {
      __weak JPUSHInMessageManager *weakSelf = self;
      [self.notiManager getInMessageContent:^(JPUSHNotiInMessageConfig *config) {
        if (config) {
          MESSAGE_LOG(0x14f, @"inapp - show notiinapp view");
          [weakSelf showMessageWithConfig:config];
        } else {
          self.inappShowCursor = 0;
          self.isShowProcess = NO;
          MESSAGE_LOG(0x14b, @"inapp - no notiinapp view");
          MESSAGE_LOG(0x14c, @"inapp - ========= stop show inapp process =========");
        }
      }];
      break;
    }
    default:
      break;
  }
}
- (void)configInAppView:(JPUSHInAppConfig *)config {
  id path = config.type == 10 ? config.bannerConfig.small_image : config.image;
  if (!path || ![path isKindOfClass:NSString.class] || ![path length])
    [self showInWindowWithConfig:config];
  __weak JPUSHInMessageManager *weakSelf = self;
  [self.h5Manager downloadSingleImage:path successBlock:^(NSString *local) {
    dispatch_async(dispatch_get_main_queue(), ^{
      if (weakSelf && local && [local isKindOfClass:NSString.class] && local.length) {
        if (config.type == 10)
          config.bannerConfig.smallImageLocalPath = local;
        else
          config.localImagePath = local;
        [weakSelf showInWindowWithConfig:config];
      } else {
        MESSAGE_LOG(0x173, @"inapp - image fail");
        [weakSelf reportInappStatus:0x9a9e10 withConfig:config];
        [weakSelf showInappProcess];
      }
    });
  } failBlock:^(NSError *error) {
    MESSAGE_LOG(0x17b, @"inapp - image fail");
    if (error.code == -1009 || error.code == -1001)
      [JCOREThread performSel:@selector(cacheMessageDic:) target:self.inAppManager withObject:config.messageDic];
    else
      [weakSelf reportInappStatus:0x9a9e10 withConfig:config];
    [weakSelf showInappProcess];
  }];
}
- (void)showInWindowWithConfig:(JPUSHInAppConfig *)config {
  if (![self.inAppManager checkInAppTarget:config.target currentPageName:self.currentPage]) {
    [self.inAppManager cacheMessageDic:config.messageDic];
    MESSAGE_LOG(0x18c, @"inapp - not in target view, return and re cache");
    return;
  }
  dispatch_async(dispatch_get_main_queue(), ^{
    UIWindow *window = [self getCurrentWindow];
    if (!window) {
      MESSAGE_LOG(0x192, @"inapp - no window");
      [self reportInappStatus:0x9a7704 withConfig:config];
      [JCOREThread performSel:@selector(cacheMessageDic:) target:self.inAppManager withObject:config.messageDic];
    } else {
      JPUSHInAppView *view = [JPUSHInAppView createViewWithConfig:config delegate:self];
      if (config.bigType == 0)
        self.bannerView = view;
      [view showInView:window];
    }
  });
}
- (void)asyncDownloadMouldWithConfig:(JPUSHNotiInMessageConfig *)config {
  if (!NSThread.isMainThread) {
    MESSAGE_LOG(0x1a6, @"[%s] thread is not mainthread", __PRETTY_FUNCTION__);
    return;
  }
  __weak JPUSHInMessageManager *weakSelf = self;
  [self.h5Manager filepath:config.mouldID networkPath:config.networkPath block:^(NSString *path) {
    dispatch_async(dispatch_get_main_queue(), ^{
      if (path.length)
        config.filepath = path;
      else
        MESSAGE_LOG(0x1af, @"[%s] filepath is nil", __PRETTY_FUNCTION__);
      if (weakSelf)
        [weakSelf messageWithConfig:config];
    });
  }];
}
- (BOOL)messageWithConfig:(JPUSHNotiInMessageConfig *)config {
  if (!config)
    return NO;
  if (!NSThread.currentThread.isMainThread) {
    MESSAGE_LOG(0x1bf, @"[%s] thread is not maindThred", __PRETTY_FUNCTION__);
    return NO;
  }
  JPUSHNotiInMessageView *view = [[JPUSHNotiInMessageView alloc] initWithFrame:CGRectZero
      WithConfig:config delegate:[JPUSHInMessageManager shareManager]];
  [self.tempMessageViews addObject:view];
  UIWindow *window = [JPUSHHelper getCurrentWinow];
  if (window)
    [window addSubview:view];
  return YES;
}
- (BOOL)replaceImagepathWithConfig:(JPUSHNotiInMessageConfig *)config resultBlock:(JPUSHInMessageResult)result {
  if (!config || !config.h5Dic || ![config.h5Dic isKindOfClass:NSDictionary.class]) {
    MESSAGE_LOG(0x1d3, @"[%s] messageConfig is bad", __PRETTY_FUNCTION__);
    return NO;
  }
  if (!NSThread.isMainThread) {
    MESSAGE_LOG(0x1d7, @"[%s] thread is not mainThread", __PRETTY_FUNCTION__);
    return NO;
  }
  @try {
    NSMutableDictionary *dictionary = [NSMutableDictionary dictionaryWithDictionary:config.h5Dic];
    id number = [dictionary objectForKey:@"in_pic_count"];
    if (!number || ![number isKindOfClass:NSNumber.class]) {
      MESSAGE_LOG(0x1de, @"[%s] picnum is nil", __PRETTY_FUNCTION__);
      result(YES);
      return YES;
    }
    NSInteger count = [number integerValue];
    if (count < 1) {
      result(YES);
      return YES;
    }
    NSMutableArray *paths = [NSMutableArray arrayWithCapacity:(NSUInteger)count];
    for (NSInteger index = 1; index <= count; index++) {
      id path = [dictionary objectForKey:[NSString stringWithFormat:@"in_pic_path%d", (int)index]];
      if (![path isKindOfClass:NSString.class] || ![path length])
        return NO;
      [paths addObject:path];
    }
    __weak JPUSHInMessageManager *weakSelf = self;
    [self.h5Manager dowloadImage:paths block:^(BOOL success, NSArray *local) {
      dispatch_async(dispatch_get_main_queue(), ^{
        if (weakSelf)
          [weakSelf replacefilepathWithDownloadResult:success withFilepathArr:local messageConfig:config resultBlock:result];
      });
    }];
    return YES;
  } @catch (NSException *exception) {
    MESSAGE_LOG(0x203, @"[bomb] [%s] is exception", __PRETTY_FUNCTION__);
  }
  return YES;
}
- (void)replacefilepathWithDownloadResult:(BOOL)success withFilepathArr:(id)paths
    messageConfig:(JPUSHNotiInMessageConfig *)config resultBlock:(JPUSHInMessageResult)result {
  if (!NSThread.isMainThread) {
    MESSAGE_LOG(0x20b, @"[%s] thread is not mainthread", __PRETTY_FUNCTION__);
    return;
  }
  if (!paths || ![paths count] || !success) {
    if (result)
      result(NO);
    return;
  }
  config.localimagepaths = paths;
  NSMutableDictionary *dictionary = [NSMutableDictionary dictionaryWithDictionary:[config.h5Dic copy]];
  @try {
    NSInteger count = (NSInteger)[paths count];
    if (count < 1) {
      if (result)
        result(NO);
      MESSAGE_LOG(0x239, @"[%s] picCount is nil", __PRETTY_FUNCTION__);
      return;
    }
    for (NSInteger index = 0; index < count; index++) {
      NSString *key = [NSString stringWithFormat:@"in_pic_path%d", (int)(index + 1)];
      id old = [dictionary objectForKey:key];
      if (![old isKindOfClass:NSString.class] || ![old length]) {
        MESSAGE_LOG(0x22d, @"[%s] pic is not exist", __PRETTY_FUNCTION__);
      } else {
        id path = [paths objectAtIndex:(NSUInteger)index];
        if (![path isKindOfClass:NSString.class] || ![path length])
          MESSAGE_LOG(0x22a, @"[%s] netwotkpath is nil", __PRETTY_FUNCTION__);
        else
          [dictionary setObject:path forKey:key];
      }
    }
    config.h5Dic = dictionary;
    if (result)
      result(YES);
  } @catch (NSException *exception) {
    MESSAGE_LOG(0x23c, @"[bomb] [%s] is exception", __PRETTY_FUNCTION__);
    if (result)
      result(NO);
  }
}
- (BOOL)fadeInBannerMessage:(JPUSHNotiInMessageView *)view {
  if (!NSThread.isMainThread) {
    MESSAGE_LOG(0x246, @"[%s] thread is not maindthread", __PRETTY_FUNCTION__);
    return NO;
  }
  if (self.bannerView)
    return NO;
  self.bannerView = view;
  return [self fadeInCurrentNotification:view];
}
- (BOOL)fadeInCurrentNotification:(JPUSHNotiInMessageView *)view {
  if (!NSThread.currentThread.isMainThread)
    return NO;
  @try {
    if ([self.notiInMessageDelegate respondsToSelector:@selector(jPushNotiInMessageDidShowWithContent:)])
      [self.notiInMessageDelegate jPushNotiInMessageDidShowWithContent:view.config.callbackContent];
    BOOL result = [self initMessageViewLayout:view];
    if (!result) {
      MESSAGE_LOG(0x260, @"[%s] layoutResult is faliure", __PRETTY_FUNCTION__);
    } else {
      view.isShow = YES;
      if (view.config.showTimer != 0 && !view.config.isfixed)
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, MessageSignedInteger(view.config.showTimer * 1e9)),
            dispatch_get_main_queue(), ^{ [self dissmissMessageView:view]; });
      double timestamp = [JCOREUtilities currentTimestamp];
      view.config.exposureStartTime = timestamp;
      [self saveTopBannerStatus:view];
      [self reportNotiMessageStatus:0x835 withMessageConfig:view.config];
    }
    return result;
  } @catch (NSException *exception) {
    MESSAGE_LOG(0x272, @"[bomb][%s] is exception", __PRETTY_FUNCTION__);
  }
  return NO;
}
- (BOOL)jPushInMessageNewDataComing {
  BOOL main = NSThread.isMainThread;
  if (!main)
    MESSAGE_LOG(0x27b, @"[%s] thread is not mainthread", __PRETTY_FUNCTION__);
  else
    [self beginShowInApp];
  return main;
}
- (void)showMessageWithConfig:(JPUSHNotiInMessageConfig *)config {
  [self replaceImagepathWithConfig:config resultBlock:^(BOOL success) {
    if (!success)
      MESSAGE_LOG(0x287, @"[%s] replaceimage is failure", __PRETTY_FUNCTION__);
    [self asyncDownloadMouldWithConfig:config];
  }];
}
- (void)jPushInMessageNeedShowNow {
  [self dismissLastPageInappView];
}
- (void)jPushInAppNewDataComing {
  [self beginShowInApp];
}
- (void)jPushMessageViewDelegateFadeOut:(JPUSHNotiInMessageView *)view {
  if (view && NSThread.isMainThread)
    [self dissmissMessageView:view];
}
- (void)jPushMessageViewDidTap:(JPUSHNotiInMessageView *)view {
}
- (void)jPushInMessageClik:(JPUSHNotiInMessageView *)view withParam:(NSDictionary *)param {
  if (!param || ![param isKindOfClass:NSDictionary.class]) {
    MESSAGE_LOG(0x2b7, @"[%s] param is bad", __PRETTY_FUNCTION__);
    return;
  }
  MESSAGE_LOG(0x2ba, @"[%s] inMessageClik ", __PRETTY_FUNCTION__);
  [self dissmissMessageView:view];
  [self reportNotiMessageStatus:0x836 withMessageConfig:view.config];
  if ([self.notiInMessageDelegate respondsToSelector:@selector(jPushNotiInMessageDidClickWithContent:)])
    [self.notiInMessageDelegate jPushNotiInMessageDidClickWithContent:view.config.callbackContent];
}
- (void)jPushInMessageCallback:(JPUSHNotiInMessageView *)view withParam:(NSDictionary *)param {
  if (!param || ![param isKindOfClass:NSDictionary.class]) {
    MESSAGE_LOG(0x2c8, @"[%s] param is bad", __PRETTY_FUNCTION__);
    return;
  }
  id number = [param objectForKey:@"statusCode"];
  if (!number || ![number isKindOfClass:NSNumber.class])
    MESSAGE_LOG(0x2cd, @"[%s] number is bad", __PRETTY_FUNCTION__);
  MESSAGE_LOG(0x2cf, @"[%s] inMessage Callback", __PRETTY_FUNCTION__);
  if ([number integerValue] == 0)
    [self prepareNotificationToBeShown:view resultBlock:^(BOOL success) {}];
  if ([self.tempMessageViews containsObject:view])
    [self.tempMessageViews removeObject:view];
}
- (void)prepareNotificationToBeShown:(JPUSHNotiInMessageView *)view resultBlock:(JPUSHInMessageResult)result {
  BOOL shown = NO;
  if (!NSThread.isMainThread) {
    MESSAGE_LOG(0x2de, @"[%s] thread is not maindthread", __PRETTY_FUNCTION__);
  } else if (!view) {
    MESSAGE_LOG(0x2e5, @"[%s] messageView is nil", __PRETTY_FUNCTION__);
  } else {
    shown = [self fadeInBannerMessage:view];
    if (!shown)
      [JCOREThread performSel:@selector(cacheMessageDic:) target:self.notiManager withObject:view.config.messageDic];
  }
  if (result)
    result(shown);
}
- (void)jPushInMessageReportdata:(JPUSHNotiInMessageView *)view withParam:(NSDictionary *)param {
  JPUSHMessageReportRequest *request = [[JPUSHMessageReportRequest alloc] init];
  request.requestInfo = param;
  [request send];
}
- (void)clearTopBannerStatus {
  [NSUserDefaults.standardUserDefaults setObject:nil forKey:@"JPUSHNotiInMessage_TopBanner_Status"];
  [NSUserDefaults.standardUserDefaults synchronize];
}
- (void)saveTopBannerStatus:(JPUSHNotiInMessageView *)view {
  if (view && view.config) {
    double duration = view.config.duration;
    double dismiss = view.config.dissmissDuration;
    double timer = view.config.showTimer;
    double timestamp = view.config.exposureStartTime;
    NSDictionary *status = @{@"exposure_start_time" : @(timestamp), @"exposure_time" : @(duration + dismiss + timer)};
    [NSUserDefaults.standardUserDefaults setObject:status forKey:@"JPUSHNotiInMessage_TopBanner_Status"];
    [NSUserDefaults.standardUserDefaults synchronize];
  }
}
- (void)reportNotiMessageStatus:(int)status withMessageConfig:(JPUSHNotiInMessageConfig *)config {
  if (!NSThread.isMainThread) {
    MESSAGE_LOG(0x316, @"[%s] thread is not mainthread", __PRETTY_FUNCTION__);
    return;
  }
  if (!config.messageDic) {
    MESSAGE_LOG(0x31a, @"[%s] messageConfig.messageDic is nil", __PRETTY_FUNCTION__);
    return;
  }
  id messageID = config.msg_id;
  if (![messageID isKindOfClass:NSString.class]) {
    MESSAGE_LOG(0x321, @"[%s] msg_id is bad", __PRETTY_FUNCTION__);
    return;
  }
  [messageID length];
  [self reportMessageStatus:status withMessageID:messageID withMsgData:config.msgData];
}
- (void)reportMessageStatus:(int)status withMessageID:(id)messageID withMsgData:(id)data {
  if (!NSThread.currentThread.isMainThread) {
    MESSAGE_LOG(0x327, @"[%s] thread is not maindthread", __PRETTY_FUNCTION__);
    return;
  }
  if (![messageID isKindOfClass:NSString.class] || ![messageID length]) {
    MESSAGE_LOG(0x32b, @"[%s] messageid is bad", __PRETTY_FUNCTION__);
    return;
  }
  JPUSHMessageReportRequest *request = [[JPUSHMessageReportRequest alloc] init];
  request.msgData = data;
  [request setNotiInMessageType:status messageID:messageID];
  [request send];
}
- (void)reportInappStatus:(int)status withConfig:(id)config {
  if (config && [config isKindOfClass:JPUSHInAppConfig.class]) {
    NSDictionary *param = @{@"status" : @((unsigned int)status), @"config" : config};
    if ([JCOREThread isMainThread])
      [self reportInappStatusOnJcoreThread:param];
    else
      [JCOREThread performSel:@selector(reportInappStatusOnJcoreThread:) target:self withObject:param];
  }
}
- (void)reportInappStatusOnJcoreThread:(id)param {
  if (![JCOREThread mainThread]) {
    MESSAGE_LOG(0x344, @"[%s] thread is not JCOREThread", __PRETTY_FUNCTION__);
    return;
  }
  if (!param || ![param isKindOfClass:NSDictionary.class])
    return;
  id status = [param valueForKey:@"status"];
  id config = [param valueForKey:@"config"];
  if (!status || ![status isKindOfClass:NSNumber.class] || !config || ![config isKindOfClass:JPUSHInAppConfig.class])
    return;
  id messageID = [config msg_id];
  if (![messageID isKindOfClass:NSString.class] || ![messageID length]) {
    MESSAGE_LOG(0x352, @"[%s] messageid is bad", __PRETTY_FUNCTION__);
    return;
  }
  NSInteger value = [status integerValue];
  JPUSHMessageReportRequest *request = [[JPUSHMessageReportRequest alloc] init];
  [request setInAppType:(int)value messageID:messageID];
  [request send];
}
- (UIWindow *)getCurrentWindow {
  return [JPUSHHelper getCurrentWinow];
}
- (JPushInAppMessage *)convertToInAppMsg:(id)config clickAction:(id)action {
  if (!config || ![config isKindOfClass:JPUSHInAppConfig.class])
    return nil;
  JPUSHInAppConfig *input = config;
  JPushInAppMessage *message = [[JPushInAppMessage alloc] init];
  message.mesageId = input.msg_id;
  message.target = input.target;
  message.extras = input.extras;
  message.clickAction = action;
  if (input.bigType == 0) {
    message.title = input.bannerConfig.title;
    message.content = input.bannerConfig.content;
  } else if (input.bigType == 1) {
    message.title = input.interstitialConfig.title;
    message.content = input.interstitialConfig.content;
  }
  return message;
}
- (BOOL)initMessageViewLayout:(JPUSHNotiInMessageView *)view {
  @try {
    UIWindow *current = [self getCurrentWindow];
    view.config.window = current;
    if (!view.config.window) {
      MESSAGE_LOG(0x380, @"[%s] window is nil", __PRETTY_FUNCTION__);
      return NO;
    }
    [view.config.window addSubview:view];
    UIView *window = view.config.window;
    view.translatesAutoresizingMaskIntoConstraints = NO;
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:view attribute:3 relatedBy:0
        toItem:window attribute:3 multiplier:1 constant:-view.config.height];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:view attribute:1 relatedBy:0
        toItem:window attribute:1 multiplier:1 constant:[self messageLeftMargin:view]];
    NSLayoutConstraint *right = [NSLayoutConstraint constraintWithItem:view attribute:2 relatedBy:0
        toItem:window attribute:2 multiplier:1 constant:[self messageRightMargin:view]];
    NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:view attribute:8 relatedBy:0
        toItem:nil attribute:8 multiplier:1 constant:view.config.height];
    view.leftConstraint = left;
    view.rightConstraint = right;
    view.topConstraint = top;
    view.heightConstraint = height;
    [window addConstraints:@[top, left, right, height]];
    [window layoutIfNeeded];
    [window setNeedsUpdateConstraints];
    [window updateConstraintsIfNeeded];
    if (view.config.duration != 0)
      [UIView animateWithDuration:view.config.duration delay:0 usingSpringWithDamping:0.7
          initialSpringVelocity:1 options:0x20000 animations:^{
        if (view.config.top == -1)
          top.constant = [self messageTopMargin];
        else {
          double margin = view.config.top;
          top.constant = margin + [self messageTopMargin];
        }
        [window layoutSubviews];
      } completion:^(BOOL finished) {}];
    return YES;
  } @catch (NSException *exception) {
    MESSAGE_LOG(0x3b5, @"[bomb] [%s] is exception", __PRETTY_FUNCTION__);
  }
  return NO;
}
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
- (double)messageLeftMargin:(JPUSHNotiInMessageView *)view {
  UIInterfaceOrientation orientation = UIApplication.sharedApplication.statusBarOrientation;
  return orientation == 3 || orientation == 4 ? view.config.landscapeLeft : view.config.left;
}
- (double)messageTopMargin {
  UIInterfaceOrientation orientation = UIApplication.sharedApplication.statusBarOrientation;
  if (orientation == 3 || orientation == 4)
    return 20;
  if (!UIApplication.sharedApplication.isStatusBarHidden)
    return UIApplication.sharedApplication.statusBarFrame.size.height;
  UIWindow *window = [self getCurrentWindow];
  double firstWidth = window.frame.size.width;
  double secondWidth = window.frame.size.width;
  return firstWidth == 812 || secondWidth == 896 ? 44 : 20;
}
- (double)messageRightMargin:(JPUSHNotiInMessageView *)view {
  UIInterfaceOrientation orientation = UIApplication.sharedApplication.statusBarOrientation;
  return orientation == 3 || orientation == 4 ? view.config.landscapeRight : view.config.right;
}
#pragma clang diagnostic pop
- (void)dissmissMessageView:(JPUSHNotiInMessageView *)view {
  if (view && NSThread.isMainThread && view.isShow) {
    [self fadeOutCurrentNotification:view];
    if (view.config.localimagepaths.count)
      [self.h5Manager removeTempImagepaths:view.config.localimagepaths];
  }
}
- (void)fadeOutCurrentNotification:(JPUSHNotiInMessageView *)view {
  if (!view || !NSThread.isMainThread)
    return;
  if (!view.isShow) {
    [view removeFromSuperview];
    return;
  }
  view.isShow = NO;
  @try {
    if (view.superview) {
      [view.superview setNeedsUpdateConstraints];
      [view.superview updateConstraintsIfNeeded];
      double offset = -view.heightConstraint.constant - 10;
      [UIView animateWithDuration:view.config.dissmissDuration delay:0 usingSpringWithDamping:1
          initialSpringVelocity:0.5 options:0x10000 animations:^{
        view.topConstraint.constant = offset;
        [view.superview layoutSubviews];
      } completion:^(BOOL finished) { [view removeFromSuperview]; }];
      self.bannerView = nil;
      [self clearTopBannerStatus];
    }
  } @catch (NSException *exception) {
    MESSAGE_LOG(0x419, @"[bomb] [%s] is exception", __PRETTY_FUNCTION__);
  }
  [self startNextMessageViewTimer:(double)self.inAppManager.showInterval + 0.5];
}
- (void)jpushInAppViewShow:(JPUSHInAppView *)view {
  if (view.inappType == 2)
    self.hasShowFullView = YES;
  else if (view.inappType == 1)
    self.hasShowModelView = YES;
  [self reportInappStatus:0x9a9e08 withConfig:view.config];
  if (self.inAppMessageDelegate && [self.inAppMessageDelegate respondsToSelector:@selector(jPushInAppMessageDidShow:)]) {
    JPushInAppMessage *message = [self convertToInAppMsg:view.config clickAction:nil];
    [self.inAppMessageDelegate jPushInAppMessageDidShow:message];
  }
}
- (void)jpushInAppViewDismiss:(JPUSHInAppView *)view {
  if (view.inappType == 0)
    self.bannerView = nil;
  [self startNextMessageViewTimer:(double)self.inAppManager.showInterval + 0.5];
}
- (void)jpushInappViewClick:(JPUSHInAppView *)view clickViewPart:(long long)part
    clickActionType:(long long)type clickAction:(NSString *)action {
  switch (part) {
    case 0: [self reportInappStatus:0x9aa1f0 withConfig:view.config]; break;
    case 1: [self reportInappStatus:0x9aa1fb withConfig:view.config]; break;
    case 2: [self reportInappStatus:0x9aa1fc withConfig:view.config]; break;
    case 3: [self reportInappStatus:0x9aa1fd withConfig:view.config]; break;
    case 4: [self reportInappStatus:0x9aa1fe withConfig:view.config]; break;
    default: break;
  }
  if ((type == 1 || type == 2) && action && [action isKindOfClass:NSString.class] && action.length) {
    if (view.config.messageDic && [view.config.messageDic isKindOfClass:NSDictionary.class]) {
      NSDictionary *info = @{@"inappmessage" : view.config.messageDic};
      [NSNotificationCenter.defaultCenter postNotificationName:@"kJiguangPushClick" object:nil userInfo:info];
    }
    if (self.inAppMessageDelegate && [self.inAppMessageDelegate respondsToSelector:@selector(jPushInAppMessageDidClick:)]) {
      JPushInAppMessage *message = [self convertToInAppMsg:view.config clickAction:action];
      [self.inAppMessageDelegate jPushInAppMessageDidClick:message];
    }
    [self urlOrDeeplinkJump:action completion:^(BOOL success) {
      int status = type == 1 ? (success ? 0x9aa1f4 : 0x9aa1f3) : (success ? 0x9aa1f2 : 0x9aa1f1);
      [self reportInappStatus:status withConfig:view.config];
    }];
  }
  dispatch_async(dispatch_get_main_queue(), ^{ [view dismiss]; });
}
- (void)urlOrDeeplinkJump:(id)action completion:(JPUSHInMessageResult)completion {
  if (!action || ![action isKindOfClass:NSString.class] || ![action length]) {
    MESSAGE_LOG(0x469, @"%s,inappmsg url nil", __PRETTY_FUNCTION__);
    return;
  }
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
  if ([JPUSHHelper containsChinese:action])
    action = [action stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
  NSURL *url = [NSURL URLWithString:action];
  if (!url) {
    MESSAGE_LOG(0x474, @"%s,inappmsg url bad", __PRETTY_FUNCTION__);
    return;
  }
  if (@available(iOS 10.0, *)) {
    [UIApplication.sharedApplication openURL:url options:@{} completionHandler:^(BOOL success) {
      if (completion)
        completion(success);
    }];
  } else {
    [UIApplication.sharedApplication openURL:url];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 500000000), dispatch_get_main_queue(), ^{
      UIApplicationState state = UIApplication.sharedApplication.applicationState;
      if (completion)
        completion(state == 0);
    });
  }
#pragma clang diagnostic pop
}
@end
#endif
