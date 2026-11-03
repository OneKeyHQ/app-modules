#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHInAppManager.h"
#import "JPUSHInAppConfig.h"
#import "JPUSHPropertyModels.h"
#import "JPUSHMessageReport.h"
#import "JCOREThread.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "RecoveredCache.h"
#import <UIKit/UIKit.h>
#import <math.h>
#define INAPP_LOG(lineValue, message, ...)                                     \
  [JCORELog log:YES level:0x3ff flag:0x100 context:0                            \
      file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"              \
           "ios-jpush/JPush/JPush/Controller/InApp/JPUSHInAppManager.m"         \
      function:__PRETTY_FUNCTION__ line:lineValue tag:nil                       \
      format:message, ##__VA_ARGS__]
static long long InAppSignedInteger(double value) {
  return isnan(value) ? 0 : value >= 0x1p63 ? INT64_MAX
      : value <= -0x1p63 ? INT64_MIN : (long long)value;
}
@implementation JPUSHInAppManager
@synthesize showInterval = _showInterval, pageDelayTime = _pageDelayTime;
@synthesize delegate = _delegate, inappDatasArr = _inappDatasArr, foregroundDate = _foregroundDate;
+ (instancetype)shareManager {
  static JPUSHInAppManager *instance;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{ instance = [[super allocWithZone:NULL] init]; });
  return instance;
}
+ (instancetype)allocWithZone:(NSZone *)zone {
  return [JPUSHInAppManager shareManager];
}
- (id)copyWithZone:(NSZone *)zone {
  return [JPUSHInAppManager shareManager];
}
- (instancetype)init {
  if ((self = [super init])) {
    self.inappDatasArr = [NSMutableArray array];
    id cached = [self getInappCacheDatas];
    if ([cached count])
      [self.inappDatasArr addObjectsFromArray:cached];
    [self getConfig];
    [NSNotificationCenter.defaultCenter addObserver:self
        selector:@selector(applicationWillEnterForeground)
        name:UIApplicationWillEnterForegroundNotification object:nil];
    [NSNotificationCenter.defaultCenter addObserver:self
        selector:@selector(applicationDidBecomeActive)
        name:UIApplicationDidBecomeActiveNotification object:nil];
  }
  return self;
}
- (void)applicationWillEnterForeground {
  [JCOREThread performSel:@selector(delayTriggerMessageShow) target:self withObject:nil];
}
- (void)applicationDidBecomeActive {
  [JCOREThread performSel:@selector(delayTriggerMessageShow) target:self withObject:nil];
}
- (void)delayTriggerMessageShow {
  if (![JCOREThread isMainThread]) {
    INAPP_LOG(0x5c, @"[%s] thread no is JCOREThread", __PRETTY_FUNCTION__);
    return;
  }
  self.foregroundDate = NSDate.date;
  id cached = [JCORECacheUtilities cacheObjectForKey:@"JPushInAppShowPageDelayKey"];
  if ([cached isKindOfClass:NSNumber.class]) {
    double value = [cached doubleValue];
    if (value > 0)
      self.pageDelayTime = (float)value;
  }
  float nanos = self.pageDelayTime * 1e9f;
  dispatch_after(dispatch_time(DISPATCH_TIME_NOW, InAppSignedInteger(nanos)), dispatch_get_main_queue(), ^{
    [self triggerMessageShow];
  });
}
- (BOOL)checkForegroundDateWithMessagDic {
  BOOL passed = NO;
  @try {
    if (!self.foregroundDate)
      return YES;
    double delay = self.pageDelayTime;
    id cached = [JCORECacheUtilities cacheObjectForKey:@"JPushInAppShowPageDelayKey"];
    if ([cached isKindOfClass:NSNumber.class]) {
      double value = [cached doubleValue];
      if (value > 0)
        delay = value;
    }
    double elapsed = (double)InAppSignedInteger([NSDate.date timeIntervalSinceDate:self.foregroundDate]);
    passed = delay <= elapsed;
    if (delay != (double)self.pageDelayTime && elapsed < delay && InAppSignedInteger(delay - elapsed) > 0)
      dispatch_after(dispatch_time(DISPATCH_TIME_NOW, InAppSignedInteger(delay * 1e9)), dispatch_get_main_queue(), ^{
        [self triggerMessageShow];
      });
  } @catch (NSException *exception) {
    INAPP_LOG(0x8d, @"[bomb][%s] is exception", __PRETTY_FUNCTION__);
  }
  return passed;
}
- (void)getConfig {
  if (![JCOREThread isMainThread]) {
    [JCOREThread performSel:@selector(getConfig) target:self withObject:nil];
    return;
  }
  self.showInterval = 3;
  self.pageDelayTime = 3;
  id cached = [JCORECacheUtilities cacheObjectForKey:@"JPushInAppShowIntervalKey"];
  if (cached && [cached isKindOfClass:NSNumber.class] && [cached floatValue] > 0)
    self.showInterval = [cached floatValue];
  cached = [JCORECacheUtilities cacheObjectForKey:@"JPushInAppShowPageDelayKey"];
  if (cached && [cached isKindOfClass:NSNumber.class] && [cached floatValue] > 0)
    self.pageDelayTime = [cached floatValue];
}
+ (void)handleInMessageDic:(id)message {
  [[self shareManager] handleInAppDic:message];
}
- (void)handleInAppDic:(id)message {
  if (![JCOREThread isMainThread]) {
    [JCOREThread performSel:@selector(handleInAppDic:) target:self withObject:message];
    return;
  }
  if (![message isKindOfClass:NSDictionary.class] || !message) {
    INAPP_LOG(0xaf, @"[%s] messageDic is bad", __PRETTY_FUNCTION__);
    return;
  }
  id messageID = [message objectForKey:@"messageId"];
  if (![messageID isKindOfClass:NSString.class] || ![messageID length]) {
    INAPP_LOG(0xb5, @"[%s] messageId is bad", __PRETTY_FUNCTION__);
    return;
  }
  [self reportInappStatus:0x9a9638 withMessageID:messageID];
  if ([self checkCachesContainsMsg:messageID]) {
    INAPP_LOG(0xbd, @"[%s] inapp cache has messageId : %@", __PRETTY_FUNCTION__, messageID);
    return;
  }
  if (![self checkMessageDic:message]) {
    INAPP_LOG(0xc3, @"in message data check fail");
    [self reportInappStatus:0x9a9a24 withMessageID:messageID];
    return;
  }
  INAPP_LOG(0xc8, @"in message data check success");
  [self reportInappStatus:0x9a9a25 withMessageID:messageID];
  if ([self checkMsgRevoke:messageID]) {
    INAPP_LOG(0xd0, @"[%s] msg is revoke %@", __PRETTY_FUNCTION__, messageID);
    [self reportInappStatus:0x9aa1f5 withMessageID:messageID];
    return;
  }
  id interval = [message valueForKey:@"interval"];
  if (interval && [interval isKindOfClass:NSNumber.class] && [interval floatValue] > 0) {
    self.showInterval = [interval floatValue] / 1000.0f;
    [JCORECacheUtilities setCacheObject:@(self.showInterval) forKey:@"JPushInAppShowIntervalKey"];
  }
  id delay = [message valueForKey:@"delay"];
  if (delay && [delay isKindOfClass:NSNumber.class] && [delay floatValue] > 0) {
    self.pageDelayTime = [delay floatValue] / 1000.0f;
    [JCORECacheUtilities setCacheObject:@(self.pageDelayTime) forKey:@"JPushInAppShowPageDelayKey"];
  }
  [self addInAppMesDic:message];
  [self triggerMessageShow];
}
- (id)handleInAppDatas:(id)messages {
  if (!messages || ![messages isKindOfClass:NSArray.class])
    return nil;
  NSMutableArray *valid = [NSMutableArray array];
  [messages enumerateObjectsUsingBlock:^(id message, NSUInteger index, BOOL *stop) {
    if (![self checkTimeOutWithMessageDic:message]) {
      [valid addObject:message];
    } else if ([message isKindOfClass:NSDictionary.class]) {
      id messageID = [message valueForKey:@"messageId"];
      INAPP_LOG(0xf6, @"inapp - msg:%@ delete, expire", messageID);
      [self reportInappStatus:0x9a9a22 withMessageID:messageID];
    }
  }];
  while (valid.count && valid.count > 10) {
    id messageID = [valid[0] valueForKey:@"messageId"];
    [self reportInappStatus:0x9a9a27 withMessageID:messageID];
    INAPP_LOG(0x102, @"inapp - msg:%@ delete, exceed maxCacheCount", messageID);
    [valid removeObjectAtIndex:0];
  }
  return [valid mutableCopy];
}
- (void)addInAppMesDic:(id)message {
  if (![JCOREThread isMainThread]) {
    INAPP_LOG(0x10d, @"[%s] thread no is JCOREThread", __PRETTY_FUNCTION__);
    return;
  }
  if (![message isKindOfClass:NSDictionary.class] || !message) {
    INAPP_LOG(0x112, @"[%s] messageDic is bad", __PRETTY_FUNCTION__);
    return;
  }
  NSMutableArray *messages = [NSMutableArray arrayWithArray:self.inappDatasArr];
  [messages addObject:message];
  id valid = [self handleInAppDatas:messages];
  if (valid && [valid isKindOfClass:NSArray.class] && [valid count]) {
    self.inappDatasArr = [NSMutableArray arrayWithArray:valid];
    [self cacheInappDatas:self.inappDatasArr];
  }
}
- (BOOL)checkCachesContainsMsg:(id)messageID {
  if (messageID && [messageID isKindOfClass:NSString.class]) {
    for (id message in [self.inappDatasArr copy]) {
      id cachedID = [message objectForKey:@"messageId"];
      if (cachedID && [cachedID isKindOfClass:NSString.class] && [cachedID isEqualToString:messageID])
        return YES;
    }
  }
  return NO;
}
- (void)cacheMessageDic:(id)message {
  if ([JCOREThread isMainThread])
    [self addInAppMesDic:message];
  else
    [JCOREThread performSel:@selector(addInAppMesDic:) target:self withObject:message];
}
- (BOOL)checkInAppTarget:(id)target currentPageName:(id)page {
  if (target && [target isKindOfClass:NSArray.class] && [target count] &&
      page && [page isKindOfClass:NSString.class] && [page length])
    return [target containsObject:page];
  return YES;
}
- (void)getInAppContentWithType:(long long)type currentPageName:(id)page completion:(JPUSHInAppConfigCompletion)completion {
  JPUSHInAppConfigCompletion wrapper = ^(JPUSHInAppConfig *config) {
    if (completion && ![(id)completion isKindOfClass:NSNull.class])
      completion(config);
  };
  NSDictionary *param = @{@"type" : @((NSInteger)type), @"currentPageName" : page ?: @"", @"completion" : wrapper};
  if ([JCOREThread isMainThread])
    [self getInAppContentWithTypeOnJCoreThread:param];
  else
    [JCOREThread performSel:@selector(getInAppContentWithTypeOnJCoreThread:) target:self withObject:param];
}
- (void)getInAppContentWithTypeOnJCoreThread:(id)param {
  if (![JCOREThread isMainThread]) {
    INAPP_LOG(0x15e, @"[%s] thread no is JCOREThread", __PRETTY_FUNCTION__);
    return;
  }
  long long type = [[param valueForKey:@"type"] integerValue];
  id page = [param valueForKey:@"currentPageName"];
  JPUSHInAppConfigCompletion completion = [param valueForKey:@"completion"];
  if (![self checkForegroundDateWithMessagDic]) {
    INAPP_LOG(0x167, @"inapp - foregroundResult is no pass");
    dispatch_async(dispatch_get_main_queue(), ^{ completion(nil); });
  } else {
    JPUSHInAppConfig *config = [self transformToInAppModel:[self getInAppDicWithType:type currentPageName:page]];
    if (completion)
      dispatch_async(dispatch_get_main_queue(), ^{ completion(config); });
  }
}
- (id)getInAppDicWithType:(long long)type currentPageName:(id)page {
  if (![JCOREThread isMainThread]) {
    INAPP_LOG(0x17b, @"[%s] thread no is JCOREThread", __PRETTY_FUNCTION__);
    return nil;
  }
  if ((unsigned long long)type >= 3) {
    INAPP_LOG(0x17f, @"not support inapp type");
    return nil;
  }
  NSMutableArray *messages = [NSMutableArray arrayWithArray:self.inappDatasArr];
  for (id message in self.inappDatasArr) {
    if (![message isKindOfClass:NSDictionary.class])
      continue;
    id messageType = [message valueForKey:@"type"];
    if (!messageType || ![messageType isKindOfClass:NSNumber.class])
      continue;
    id messageID = [message objectForKey:@"messageId"];
    if (![messageID isKindOfClass:NSString.class] || ![messageID length])
      continue;
    if ([self checkMsgRevoke:messageID]) {
      INAPP_LOG(0x193, @"[%s] msg is revoke %@", __PRETTY_FUNCTION__, messageID);
      [messages removeObject:message];
      [self reportInappStatus:0x9aa1f5 withMessageID:messageID];
      continue;
    }
    if ([self checkTimeOutWithMessageDic:message]) {
      INAPP_LOG(0x19c, @"[%s] msg is expire %@", __PRETTY_FUNCTION__, messageID);
      [messages removeObject:message];
      [self reportInappStatus:0x9a9a22 withMessageID:messageID];
      continue;
    }
    long long group = [messageType integerValue] & ~1LL;
    if (!((type == 0 && group == 10) || (type == 1 && group == 20) || (type == 2 && group == 30)))
      continue;
    id target = [message valueForKey:@"target"];
    if (page && [page isKindOfClass:NSString.class] && [page length]) {
      if (target && [target isKindOfClass:NSArray.class] && [target count] && ![target containsObject:page])
        continue;
      if (message) {
        [messages removeObject:message];
        self.inappDatasArr = [messages mutableCopy];
        [self cacheInappDatas:self.inappDatasArr];
      }
      return message;
    }
  }
  return nil;
}
- (JPUSHInAppConfig *)transformToInAppModel:(id)message {
  if (![JCOREThread isMainThread]) {
    INAPP_LOG(0x1c1, @"[%s] thread no is JCOREThread", __PRETTY_FUNCTION__);
    return nil;
  }
  if (!message || ![message isKindOfClass:NSDictionary.class])
    return nil;
  JPUSHInAppConfig *result = [[JPUSHInAppConfig alloc] init];
  result.messageDic = message;
  id value;
  value = [message valueForKey:@"messageId"];
  if (value && [value isKindOfClass:NSString.class])
    result.msg_id = value;
  value = [message valueForKey:@"target"];
  if (value && [value isKindOfClass:NSArray.class])
    result.target = value;
  value = [message valueForKey:@"expired"];
  if (value && [value isKindOfClass:NSString.class])
    result.expired = value;
  value = [message valueForKey:@"delay"];
  if (value && [value isKindOfClass:NSNumber.class])
    result.delay = [value integerValue];
  value = [message valueForKey:@"interval"];
  if (value && [value isKindOfClass:NSNumber.class])
    result.interval = [value integerValue];
  value = [message valueForKey:@"image"];
  if (value && [value isKindOfClass:NSString.class])
    result.image = value;
  id click = [message valueForKey:@"click"];
  if (click && [click isKindOfClass:NSDictionary.class]) {
    value = [click valueForKey:@"type"];
    if (value && [value isKindOfClass:NSNumber.class])
      result.clickType = [value integerValue];
    value = [click valueForKey:@"action"];
    if (value && [value isKindOfClass:NSString.class])
      result.clickAction = value;
  }
  value = [message valueForKey:@"type"];
  if (value && [value isKindOfClass:NSNumber.class])
    result.type = [value integerValue];
  value = [message valueForKey:@"extras"];
  if (value && [value isKindOfClass:NSDictionary.class])
    result.extras = value;
  id interstitial = [message valueForKey:@"interstitial"];
  if (interstitial && [interstitial isKindOfClass:NSDictionary.class]) {
    JPUSHInterstitialConfig *config = [[JPUSHInterstitialConfig alloc] init];
    result.interstitialConfig = config;
    id title = [interstitial valueForKey:@"title"];
    if (title && [title isKindOfClass:NSDictionary.class]) {
      value = [title valueForKey:@"text"];
      if (value && [value isKindOfClass:NSString.class])
        config.title = value;
      config.titleStyle = [self configStyle:[title valueForKey:@"style"]];
    }
    id content = [interstitial valueForKey:@"content"];
    if (content && [content isKindOfClass:NSDictionary.class]) {
      value = [content valueForKey:@"text"];
      if (value && [value isKindOfClass:NSString.class])
        config.content = value;
      config.contentStyle = [self configStyle:[content valueForKey:@"style"]];
    }
    NSMutableArray *buttons = [NSMutableArray array];
    id rawButtons = [interstitial valueForKey:@"buttons"];
    if (rawButtons && [rawButtons isKindOfClass:NSArray.class]) {
      for (id raw in rawButtons) {
        JPUSHInAppBtnConfig *button = [[JPUSHInAppBtnConfig alloc] init];
        [buttons addObject:button];
        value = [raw valueForKey:@"text"];
        if (value && [value isKindOfClass:NSString.class])
          button.text = value;
        button.style = [self configStyle:[raw valueForKey:@"style"]];
        id buttonClick = [raw valueForKey:@"click"];
        if (buttonClick && [buttonClick isKindOfClass:NSDictionary.class]) {
          value = [buttonClick valueForKey:@"type"];
          if (value && [value isKindOfClass:NSNumber.class])
            button.clickType = [value integerValue];
          value = [buttonClick valueForKey:@"action"];
          if (value && [value isKindOfClass:NSString.class])
            button.clickAction = value;
        }
        value = [raw valueForKey:@"bg_color"];
        if (value && [value isKindOfClass:NSString.class])
          button.bg_color = value;
      }
    }
    config.buttons = [buttons copy];
    value = [interstitial valueForKey:@"close_location"];
    if (value && [value isKindOfClass:NSNumber.class])
      config.close_location = [value integerValue];
  }
  id full = [message valueForKey:@"full"];
  if (full && [full isKindOfClass:NSDictionary.class]) {
    JPUSHFullConfig *config = [[JPUSHFullConfig alloc] init];
    result.fullConfig = config;
    value = [full valueForKey:@"text"];
    if (value && [value isKindOfClass:NSString.class])
      config.text = value;
    config.style = [self configStyle:[full valueForKey:@"style"]];
    value = [full valueForKey:@"duration"];
    if (value && [value isKindOfClass:NSNumber.class])
      config.duration = [value integerValue];
    value = [full valueForKey:@"bg_color"];
    if (value && [value isKindOfClass:NSString.class])
      config.bg_color = value;
  }
  id banner = [message valueForKey:@"banner"];
  if (banner && [banner isKindOfClass:NSDictionary.class]) {
    JPUSHBannerConfig *config = [[JPUSHBannerConfig alloc] init];
    result.bannerConfig = config;
    value = [banner valueForKey:@"small_image"];
    if (value && [value isKindOfClass:NSString.class])
      config.small_image = value;
    id title = [banner valueForKey:@"title"];
    if (title && [title isKindOfClass:NSDictionary.class]) {
      value = [title valueForKey:@"text"];
      if (value && [value isKindOfClass:NSString.class])
        config.title = value;
      config.titleStyle = [self configStyle:[title valueForKey:@"style"]];
    }
    id content = [banner valueForKey:@"content"];
    if (content && [content isKindOfClass:NSDictionary.class]) {
      value = [content valueForKey:@"text"];
      if (value && [value isKindOfClass:NSString.class])
        config.content = value;
      config.contentStyle = [self configStyle:[content valueForKey:@"style"]];
    }
    value = [banner valueForKey:@"duration"];
    if (value && [value isKindOfClass:NSNumber.class])
      config.duration = [value integerValue];
    value = [banner valueForKey:@"location"];
    if (value && [value isKindOfClass:NSNumber.class])
      config.location = [value integerValue];
  }
  return result;
}
- (JPUSHInAppStyleConfig *)configStyle:(id)style {
  if (![JCOREThread isMainThread]) {
    INAPP_LOG(0x27c, @"[%s] thread no is JCOREThread", __PRETTY_FUNCTION__);
    return nil;
  }
  if (!style || ![style isKindOfClass:NSArray.class] || [style count] < 4)
    return nil;
  JPUSHInAppStyleConfig *config = [[JPUSHInAppStyleConfig alloc] init];
  id value = [style objectAtIndex:0];
  config.bold = value && [value isKindOfClass:NSNumber.class] ? [value boolValue] : NO;
  value = [style objectAtIndex:1];
  if (value && [value isKindOfClass:NSNumber.class])
    config.size = (double)[value floatValue];
  value = [style objectAtIndex:2];
  if (value && [value isKindOfClass:NSNumber.class]) {
    NSInteger alignment = [value integerValue];
    config.align = alignment == 3 ? 2 : alignment != 2;
  }
  value = [style objectAtIndex:3];
  if (value && [value isKindOfClass:NSString.class])
    config.color = value;
  return config;
}
- (BOOL)checkMessageDic:(id)message {
  if (![JCOREThread isMainThread]) {
    INAPP_LOG(0x2a5, @"[%s] thread no is JCOREThread", __PRETTY_FUNCTION__);
    return NO;
  }
  if (![message isKindOfClass:NSDictionary.class] || !message) {
    INAPP_LOG(0x2a9, @"[%s] inappmsg check: messageDic is bad", __PRETTY_FUNCTION__);
    return NO;
  }
  id messageID = [message objectForKey:@"messageId"];
  if (![messageID isKindOfClass:NSString.class] || ![messageID length]) {
    INAPP_LOG(0x2af, @"[%s] inappmsg check: messageId is nil", __PRETTY_FUNCTION__);
    return NO;
  }
  id rawType = [message objectForKey:@"type"];
  if (![rawType isKindOfClass:NSNumber.class]) {
    INAPP_LOG(0x2b5, @"[%s] inappmsg check: type is nil", __PRETTY_FUNCTION__);
    return NO;
  }
  NSInteger type = [rawType integerValue];
  if (!((type == 10 || type == 11) || (type == 20 || type == 21) || (type == 30 || type == 31))) {
    INAPP_LOG(0x2bf, @"[%s] inappmsg check: not support inapp type", __PRETTY_FUNCTION__);
    return NO;
  }
  id value = [message valueForKey:@"target"];
  if (!value || ![value isKindOfClass:NSArray.class]) {
    INAPP_LOG(0x2c5, @"[%s] inappmsg check: target is bad", __PRETTY_FUNCTION__);
    return NO;
  }
  value = [message valueForKey:@"expired"];
  if (!value || ![value isKindOfClass:NSString.class]) {
    INAPP_LOG(0x2cb, @"[%s] inappmsg check: expired is bad", __PRETTY_FUNCTION__);
    return NO;
  }
  value = [message valueForKey:@"delay"];
  if (!value || ![value isKindOfClass:NSNumber.class]) {
    INAPP_LOG(0x2d1, @"[%s] inappmsg check: delay is bad", __PRETTY_FUNCTION__);
    return NO;
  }
  value = [message valueForKey:@"interval"];
  if (!value || ![value isKindOfClass:NSNumber.class]) {
    INAPP_LOG(0x2d7, @"[%s] inappmsg check: interval is bad", __PRETTY_FUNCTION__, value);
    return NO;
  }
  if (type == 11 || type == 21 || type == 30 || type == 31) {
    value = [message objectForKey:@"image"];
    if (![value isKindOfClass:NSString.class] || ![value length]) {
      INAPP_LOG(0x2df, @"[%s] inappmsg check: image is nil", __PRETTY_FUNCTION__);
      return NO;
    }
  }
  return YES;
}
- (void)handleRevokeMessageIdList:(id)messageIDs {
  if (![JCOREThread isMainThread]) {
    [JCOREThread performSel:@selector(handleRevokeMessageIdList:) target:self withObject:messageIDs];
    return;
  }
  if ([messageIDs isKindOfClass:NSString.class] && [messageIDs length]) {
    NSArray *ids = [messageIDs componentsSeparatedByString:@","];
    for (NSUInteger index = 0; index < ids.count; index++) {
      id messageID = ids[index];
      if ([messageID isKindOfClass:NSString.class] && [messageID length])
        [self handleRevokeMessageId:messageID];
    }
  }
}
- (void)handleRevokeMessageId:(id)messageID {
  if (![messageID isKindOfClass:NSString.class]) {
    INAPP_LOG(0x2fe, @"[%s] msg is bad", __PRETTY_FUNCTION__);
    return;
  }
  [messageID length];
  if (![JCOREThread isMainThread]) {
    INAPP_LOG(0x302, @"[%s] thread no is JCOREThread", __PRETTY_FUNCTION__);
    return;
  }
  @try {
    if (![self.inappDatasArr isKindOfClass:NSArray.class] || !self.inappDatasArr.count) {
      INAPP_LOG(0x30a, @"messageArr is nil");
      [self cacheRevokeMsgId:messageID];
      return;
    }
    __block NSDictionary *found = nil;
    [self.inappDatasArr enumerateObjectsUsingBlock:^(NSDictionary *message, NSUInteger index, BOOL *stop) {
      if (![message isKindOfClass:NSDictionary.class]) {
        INAPP_LOG(0x31a, @"[%s] messageDic is bad", __PRETTY_FUNCTION__);
      } else if ([[message objectForKey:@"messageId"] isEqualToString:messageID]) {
        found = message;
        *stop = YES;
      }
    }];
    if (!found) {
      [self cacheRevokeMsgId:messageID];
      return;
    }
    INAPP_LOG(0x31f, @"[%s] msg is revoke", __PRETTY_FUNCTION__);
    [self reportInappStatus:0x9aa1f5 withMessageID:[found objectForKey:@"messageId"]];
    NSMutableArray *remaining = [NSMutableArray arrayWithArray:self.inappDatasArr];
    [remaining removeObject:found];
    self.inappDatasArr = [remaining mutableCopy];
    [self cacheInappDatas:self.inappDatasArr];
  } @catch (NSException *exception) {
    INAPP_LOG(0x32e, @"[%s] is exception", __PRETTY_FUNCTION__);
  }
}
- (BOOL)checkMsgRevoke:(id)messageID {
  if (![JCOREThread isMainThread]) {
    INAPP_LOG(0x335, @"[%s] thread no is JCOREThread", __PRETTY_FUNCTION__);
    return NO;
  }
  __block BOOL revoked = NO;
  @try {
    id cached = [JCORECacheUtilities cacheObjectForKey:@"JPushInAppMsgRevokeKey"];
    NSMutableArray *removed = [NSMutableArray array];
    [cached enumerateObjectsUsingBlock:^(NSDictionary *message, NSUInteger index, BOOL *stop) {
      if ([message isKindOfClass:NSDictionary.class]) {
        double timestamp = [[message objectForKey:@"time"] doubleValue];
        if ([JCOREUtilities currentTimestamp] - timestamp >= 86400.0) {
          [removed addObject:message];
        } else if ([[message objectForKey:@"messageId"] isEqualToString:messageID]) {
          revoked = YES;
          [removed addObject:message];
        }
      }
    }];
    NSMutableArray *remaining = [NSMutableArray arrayWithArray:cached];
    [removed enumerateObjectsUsingBlock:^(id message, NSUInteger index, BOOL *stop) {
      [remaining removeObject:message];
    }];
    [JCORECacheUtilities setCacheObject:remaining forKey:@"JPushInAppMsgRevokeKey"];
  } @catch (NSException *exception) {
    INAPP_LOG(0x359, @"[%s] is exception", __PRETTY_FUNCTION__);
  }
  return revoked;
}
- (void)cacheRevokeMsgId:(id)messageID {
  if (![JCOREThread isMainThread]) {
    INAPP_LOG(0x362, @"[%s] thread no is JCOREThread", __PRETTY_FUNCTION__);
    return;
  }
  if (!messageID || ![messageID isKindOfClass:NSString.class] || ![messageID length])
    return;
  @try {
    id cached = [JCORECacheUtilities cacheObjectForKey:@"JPushInAppMsgRevokeKey"];
    NSMutableArray *messages = [NSMutableArray arrayWithArray:cached];
    [messages addObject:@{@"messageId" : messageID, @"time" : @([JCOREUtilities currentTimestamp])}];
    [JCORECacheUtilities setCacheObject:messages forKey:@"JPushInAppMsgRevokeKey"];
  } @catch (NSException *exception) {
    INAPP_LOG(0x374, @"[%s] is exception", __PRETTY_FUNCTION__);
  }
}
- (BOOL)checkTimeOutWithMessageDic:(id)message {
  if (![JCOREThread isMainThread]) {
    INAPP_LOG(0x37d, @"[%s] thread no is JCOREThread", __PRETTY_FUNCTION__);
    return NO;
  }
  @try {
    if (![message isKindOfClass:NSDictionary.class]) {
      INAPP_LOG(0x384, @"[%s] messageDic is bad", __PRETTY_FUNCTION__);
      return NO;
    }
    id expired = [message objectForKey:@"expired"];
    if ([expired isKindOfClass:NSString.class] && [expired length]) {
      double milliseconds = [expired doubleValue];
      return milliseconds <= [JCOREUtilities currentTimestamp] * 1000.0;
    }
    INAPP_LOG(0x389, @"[%s] expiredTime is bad", __PRETTY_FUNCTION__);
  } @catch (NSException *exception) {
    INAPP_LOG(0x393, @"[%s] is exception", __PRETTY_FUNCTION__);
  }
  return NO;
}
- (void)triggerMessageShow {
  dispatch_async(dispatch_get_main_queue(), ^{
    if ([self.delegate respondsToSelector:@selector(jPushInAppNewDataComing)])
      [self.delegate jPushInAppNewDataComing];
  });
}
- (void)reportInappStatus:(int)status withMessageID:(id)messageID {
  if (![JCOREThread mainThread]) {
    INAPP_LOG(0x3a4, @"[%s] thread is not JCOREThread", __PRETTY_FUNCTION__);
    return;
  }
  if (![messageID isKindOfClass:NSString.class] || ![messageID length]) {
    INAPP_LOG(0x3a8, @"[%s] messageid is bad", __PRETTY_FUNCTION__);
    return;
  }
  JPUSHMessageReportRequest *request = [[JPUSHMessageReportRequest alloc] init];
  [request setInAppType:status messageID:messageID];
  [request send];
}
- (id)getInappCacheDatas {
  id cached = [JCORECacheUtilities cacheObjectForKey:@"JPushInAppKey"];
  return [cached isKindOfClass:NSArray.class] && [cached count] ? cached : nil;
}
- (void)cacheInappDatas:(id)messages {
  INAPP_LOG(0x3bf, @"inapp - cacheInappDatas: %@", messages);
  [JCORECacheUtilities setCacheObject:messages forKey:@"JPushInAppKey"];
}
@end
#endif
