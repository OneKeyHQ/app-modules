#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHNotiInMessageManager.h"
#import "JPUSHNotiInMessageConfig.h"
#import "JCOREThread.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "RecoveredCache.h"
#import "JPUSHHelper.h"
#import "JPUSHMessageReport.h"
#import <UIKit/UIKit.h>
#import <math.h>
#define NOTI_LOG(lineValue, message, ...)                                      \
  [JCORELog log:YES level:0x3ff flag:0x100 context:0                            \
      file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"              \
           "ios-jpush/JPush/JPush/Controller/NotiInApp/JPUSHNotiInMessageManager.m" \
      function:__PRETTY_FUNCTION__ line:lineValue tag:nil                       \
      format:message, __PRETTY_FUNCTION__, ##__VA_ARGS__]
static long long NotiSignedInteger(double value) {
  return isnan(value) ? 0 : value >= 0x1p63 ? INT64_MAX
      : value <= -0x1p63 ? INT64_MIN : (long long)value;
}
@implementation JPUSHNotiInMessageManager
@synthesize delegate = _delegate, priority = _priority, foregroundDate = _foregroundDate;
@synthesize delayTime = _delayTime, datasArr = _datasArr;
- (void)dealloc {
  [NSNotificationCenter.defaultCenter removeObserver:self];
}
- (instancetype)init {
  if ((self = [super init])) {
    self.foregroundDate = NSDate.date;
    self.priority = 2;
    self.delayTime = 10;
    [self getCacheDatas];
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
- (void)getCacheDatas {
  if ([JCOREThread isMainThread]) {
    id cached = [JCORECacheUtilities cacheObjectForKey:@"JPushNotiInMessagesKey"];
    if ([cached isKindOfClass:NSArray.class])
      self.datasArr = cached;
  } else {
    [JCOREThread performSel:@selector(getCacheDatas) target:self withObject:nil];
  }
}
+ (void)inMessageManager:(JPUSHNotiManagerCompletion)completion {
  [JCOREThread performSel:@selector(inMessageManagerInJCoreThread:) target:self withObject:completion];
}
+ (void)inMessageManagerInJCoreThread:(JPUSHNotiManagerCompletion)completion {
  if (![JCOREThread isMainThread]) {
    NOTI_LOG(0x5f, @"[%s] thread is not JCOREThread");
    return;
  }
  JPUSHNotiInMessageManager *manager = [[JPUSHNotiInMessageManager alloc] init];
  if (completion)
    dispatch_async(dispatch_get_main_queue(), ^{ completion(manager); });
}
- (void)handleInMessageDic:(id)message {
  if (![message isKindOfClass:NSDictionary.class] || !message) {
    NOTI_LOG(0x6c, @"[%s] messageDic is bad");
    return;
  }
  if (![JCOREThread isMainThread]) {
    [JCOREThread performSel:@selector(handleInMessageDic:) target:self withObject:message];
    return;
  }
  @try {
    if (![self checkMessageDic:message])
      return;
    if (![self cacheMessageDic:message])
      NOTI_LOG(0x7c, @"[%s] cache inapp message failure");
    id config = [message objectForKey:@"inapp"];
    id priority = [config objectForKey:@"inapp_priority"];
    if ([priority isKindOfClass:NSNumber.class] && priority)
      self.priority = [priority intValue];
    id direct = [config objectForKey:@"inapp_display_direct"];
    if ([direct isKindOfClass:NSNumber.class] && direct && [direct integerValue] == 1)
      [self messageNeedShowNow];
    [self triggerMessageShow];
  } @catch (NSException *exception) {
    NOTI_LOG(0x91, @"[%s] is exception");
  }
}
- (BOOL)checkMessageDic:(id)message {
  if (![message isKindOfClass:NSDictionary.class] || !message) {
    NOTI_LOG(0x99, @"[%s] messageDic is bad");
    return NO;
  }
  if (![JCOREThread isMainThread]) {
    NOTI_LOG(0x9d, @"[%s] thread no is JCOREThread");
    return NO;
  }
  @try {
    id messageID = [[message objectForKey:@"_j_msgid"] stringValue];
    if (![messageID isKindOfClass:NSString.class] || ![messageID length]) {
      NOTI_LOG(0xa5, @"[%s] messageId is nil");
      return NO;
    }
    if ([message objectForKey:@"_j_geofence"]) {
      NOTI_LOG(0xab, @"[%s] notiInMessage should not have content about geofence");
      return NO;
    }
    id data = [message objectForKey:@"_j_data_"];
    [self reportMessageStatus:0x834 withMessageID:messageID msgData:data];
    id aps = [message objectForKey:@"aps"];
    if (![aps isKindOfClass:NSDictionary.class] || !aps) {
      NOTI_LOG(0xb5, @"[%s] aps is nil");
      return NO;
    }
    id config = [message objectForKey:@"inapp"];
    if (![config isKindOfClass:NSDictionary.class] || !config) {
      NOTI_LOG(0xbb, @"[%s] inapp config is nil");
      return NO;
    }
    if ([self checkMsgRevoke:messageID]) {
      NOTI_LOG(0xc1, @"[%s] msg is revoke %@", messageID);
      [self reportMessageStatus:0x837 withMessageID:messageID msgData:data];
      return NO;
    }
    id delay = [config objectForKey:@"inapp_delay_display_time"];
    if ([delay isKindOfClass:NSNumber.class] && delay)
      [JCORECacheUtilities setCacheObject:delay forKey:@"inapp_delay_display_time"];
    return YES;
  } @catch (NSException *exception) {
    NOTI_LOG(0xcd, @"[%s] is exception");
  }
  return NO;
}
- (BOOL)cacheMessageDic:(id)message {
  if (![message isKindOfClass:NSDictionary.class] || !message) {
    NOTI_LOG(0xd6, @"[%s] messageDic is bad");
    return NO;
  }
  if (![JCOREThread isMainThread]) {
    NOTI_LOG(0xda, @"[%s] thread no is JCOREThread");
    return NO;
  }
  @try {
    NSUInteger maximum = 10;
    id config = [message objectForKey:@"inapp"];
    if ([config isKindOfClass:NSDictionary.class]) {
      id count = [config objectForKey:@"inapp_max_cache_numb"];
      if ([count isKindOfClass:NSNumber.class] && [count intValue] > 0)
        maximum = (NSUInteger)[count intValue];
    }
    NSMutableArray *messages = [NSMutableArray array];
    id cached = [JCORECacheUtilities cacheObjectForKey:@"JPushNotiInMessagesKey"];
    if ([cached isKindOfClass:NSArray.class] && [cached count])
      [messages addObjectsFromArray:cached];
    while (messages.count && messages.count >= maximum)
      [messages removeObjectAtIndex:0];
    [messages addObject:message];
    self.datasArr = [NSMutableArray arrayWithArray:messages];
    [JCORECacheUtilities setCacheObject:messages forKey:@"JPushNotiInMessagesKey"];
    return YES;
  } @catch (NSException *exception) {
    NOTI_LOG(0xfb, @"[%s] is exception");
  }
  return NO;
}
- (id)messageDicForCache {
  if (![JCOREThread isMainThread]) {
    NOTI_LOG(0x104, @"[%s] thread no is JCOREThread");
    return nil;
  }
  id message = nil;
  @try {
    id cached = self.datasArr;
    if (![cached isKindOfClass:NSArray.class] || ![cached count])
      return nil;
    NSMutableArray *messages = [NSMutableArray arrayWithArray:cached];
    while (messages.count && !message) {
      message = messages.lastObject;
      [messages removeLastObject];
      if ([self checkTimeOutWithMessageDic:message]) {
        message = nil;
        NOTI_LOG(0x11b, @"[%s] tempMessagedic is expired");
      }
    }
    self.datasArr = [NSMutableArray arrayWithArray:messages];
    [JCORECacheUtilities setCacheObject:messages forKey:@"JPushNotiInMessagesKey"];
    return message;
  } @catch (NSException *exception) {
    NOTI_LOG(0x122, @"[%s] is exception");
  }
  return message;
}
- (void)getInMessageContent:(JPUSHNotiConfigCompletion)completion {
  [JCOREThread performSel:@selector(getInMessageContentWithJCoreThread:) target:self withObject:completion];
}
- (void)getInMessageContentWithJCoreThread:(JPUSHNotiConfigCompletion)completion {
  if (![self checkForegroundDateWithMessagDic]) {
    NOTI_LOG(0x130, @"[%s] foregroundResult is no pass");
    [self callbackMessageConfig:nil withCompletion:completion];
    return;
  }
  if (![self checkBannerStatus]) {
    NOTI_LOG(0x138, @"[%s] jad is show ,return");
    [self callbackMessageConfig:nil withCompletion:completion];
    return;
  }
  @try {
    id message = [self messageDicForCache];
    if (![message isKindOfClass:NSDictionary.class] || !message) {
      [self callbackMessageConfig:nil withCompletion:completion];
      return;
    }
    id messageID = [[message objectForKey:@"_j_msgid"] stringValue];
    id data = [message objectForKey:@"_j_data_"];
    id config = [message objectForKey:@"inapp"];
    if (![config isKindOfClass:NSDictionary.class] || !config)
      return;
    JPUSHNotiInMessageConfig *result = [[JPUSHNotiInMessageConfig alloc] init];
    id path = [config objectForKey:@"inapp_h5_tpl_url"];
    id mouldID = [JCOREUtilities stringByMD5:path];
    result.messageDic = message;
    result.msg_id = messageID;
    result.msgData = data;
    if ([path length])
      result.networkPath = path;
    if ([mouldID length])
      result.mouldID = mouldID;
    result.h5Dic = [message objectForKey:@"aps"];
    result.callbackContent = [self getCallBackContent:message];
    id timer = [config objectForKey:@"inapp_show_threshold_time"];
    if ([timer isKindOfClass:NSNumber.class] && timer)
      result.showTimer = (double)[timer floatValue];
    [self callbackMessageConfig:result withCompletion:completion];
  } @catch (NSException *exception) {
    NOTI_LOG(0x16e, @"[%s] is exception");
  }
}
- (void)callbackMessageConfig:(JPUSHNotiInMessageConfig *)config withCompletion:(JPUSHNotiConfigCompletion)completion {
  if (completion)
    dispatch_async(dispatch_get_main_queue(), ^{ completion(config); });
}
- (void)delayTriggerMessageShow {
  if (![JCOREThread isMainThread]) {
    NOTI_LOG(0x17f, @"[%s] thread no is JCOREThread");
    return;
  }
  self.foregroundDate = NSDate.date;
  id cached = [JCORECacheUtilities cacheObjectForKey:@"inapp_delay_display_time"];
  if ([cached isKindOfClass:NSNumber.class]) {
    double value = [cached doubleValue];
    if (value > 0)
      self.delayTime = NotiSignedInteger(value);
  }
  dispatch_after(dispatch_time(DISPATCH_TIME_NOW,
      (int64_t)((uint64_t)self.delayTime * UINT64_C(1000000000))), dispatch_get_main_queue(), ^{
    [self triggerMessageShow];
  });
}
- (void)triggerMessageShow {
  dispatch_async(dispatch_get_main_queue(), ^{
    if ([self.delegate respondsToSelector:@selector(jPushInMessageNewDataComing)])
      [self.delegate jPushInMessageNewDataComing];
  });
}
- (void)messageNeedShowNow {
  dispatch_async(dispatch_get_main_queue(), ^{
    if ([self.delegate respondsToSelector:@selector(jPushInMessageNeedShowNow)])
      [self.delegate jPushInMessageNeedShowNow];
  });
}
- (BOOL)checkForegroundDateWithMessagDic {
  BOOL passed = NO;
  @try {
    if (!self.foregroundDate)
      return YES;
    double delay = (double)self.delayTime;
    id cached = [JCORECacheUtilities cacheObjectForKey:@"inapp_delay_display_time"];
    if ([cached isKindOfClass:NSNumber.class]) {
      double value = [cached doubleValue];
      if (value > 0)
        delay = value;
    }
    double elapsed = (double)NotiSignedInteger([NSDate.date timeIntervalSinceDate:self.foregroundDate]);
    passed = delay <= elapsed;
    if (delay != (double)self.delayTime && elapsed < delay && NotiSignedInteger(delay - elapsed) > 0)
      dispatch_after(dispatch_time(DISPATCH_TIME_NOW, NotiSignedInteger(delay * 1e9)), dispatch_get_main_queue(), ^{
        [self triggerMessageShow];
      });
    return passed;
  } @catch (NSException *exception) {
    NOTI_LOG(0x1c1, @"[bomb][%s] is exception");
  }
  return passed;
}
- (BOOL)checkBannerStatus {
  @try {
    id status = [NSUserDefaults.standardUserDefaults objectForKey:@"JAd_TopBanner_Status"];
    if (status && [status isKindOfClass:NSDictionary.class]) {
      id start = [status valueForKey:@"exposure_start_time"];
      id exposure = [status valueForKey:@"exposure_time"];
      if (start && [start isKindOfClass:NSNumber.class] && exposure && [exposure isKindOfClass:NSNumber.class]) {
        double current = [JCOREUtilities currentTimestamp];
        double remaining = ([start doubleValue] + [exposure doubleValue]) - current;
        if (remaining > 0)
          dispatch_after(dispatch_time(DISPATCH_TIME_NOW, NotiSignedInteger(remaining * 1e9)), dispatch_get_main_queue(), ^{
            [self triggerMessageShow];
          });
        return remaining <= 0;
      }
    }
    return YES;
  } @catch (NSException *exception) {
    NOTI_LOG(0x1df, @"[bomb][%s] is exception");
  }
  return YES;
}
- (id)getCallBackContent:(id)message {
  if (message && [message isKindOfClass:NSDictionary.class]) {
    NSMutableDictionary *content = [NSMutableDictionary dictionaryWithDictionary:message];
    [content removeObjectForKey:@"_j_data_"];
    [content removeObjectForKey:@"inapp"];
    return [content copy];
  }
  return nil;
}
- (void)reportMessageStatus:(int)status withMessageID:(id)messageID msgData:(id)data {
  if (![JCOREThread mainThread]) {
    NOTI_LOG(0x1f2, @"[%s] thread is not JCOREThread");
    return;
  }
  if (![messageID isKindOfClass:NSString.class] || ![messageID length]) {
    NOTI_LOG(0x1f6, @"[%s] messageid is bad");
    return;
  }
  JPUSHMessageReportRequest *request = [[JPUSHMessageReportRequest alloc] init];
  request.msgData = data;
  [request setNotiInMessageType:status messageID:messageID];
  [request send];
}
- (BOOL)checkTimeOutWithMessageDic:(id)message {
  if (![JCOREThread isMainThread]) {
    NOTI_LOG(0x202, @"[%s] thread no is JCOREThread");
    return NO;
  }
  @try {
    if (![message isKindOfClass:NSDictionary.class]) {
      NOTI_LOG(0x209, @"[%s] messageDic is bad");
      return NO;
    }
    id config = [message objectForKey:@"inapp"];
    if (![config isKindOfClass:NSDictionary.class]) {
      NOTI_LOG(0x20f, @"[%s] config is bad");
      return NO;
    }
    id end = [config objectForKey:@"inapp_end_time"];
    if (![end isKindOfClass:NSString.class] || ![end length]) {
      NOTI_LOG(0x214, @"[%s] inapp_end_time is bad");
      return NO;
    }
    NSDate *date = [JPUSHHelper transformStrToDate:end];
    return [NSDate.date compare:date] == NSOrderedDescending;
  } @catch (NSException *exception) {
    NOTI_LOG(0x220, @"[%s] is exception");
  }
  return NO;
}
- (void)handleRevokeMessageIdList:(id)messageIDs {
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
    NOTI_LOG(0x239, @"[%s] msg is bad");
    return;
  }
  [messageID length];
  if (![JCOREThread isMainThread]) {
    NOTI_LOG(0x23d, @"[%s] thread no is JCOREThread");
    return;
  }
  @try {
    id messages = self.datasArr;
    if (![messages isKindOfClass:NSArray.class] || ![messages count]) {
      [JCORELog log:YES level:0x3ff flag:0x100 context:0
          file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Controller/NotiInApp/JPUSHNotiInMessageManager.m"
          function:__PRETTY_FUNCTION__ line:0x245 tag:nil format:@"messageArr is nil"];
      [self cacheRevokeMsgId:messageID];
      return;
    }
    __block NSDictionary *found = nil;
    [messages enumerateObjectsUsingBlock:^(NSDictionary *message, NSUInteger index, BOOL *stop) {
      if (![message isKindOfClass:NSDictionary.class]) {
        NOTI_LOG(0x254, @"[%s] messageDic is bad");
      } else if ([[[message objectForKey:@"_j_msgid"] stringValue] isEqualToString:messageID]) {
        found = message;
        *stop = YES;
      }
    }];
    if (!found) {
      [self cacheRevokeMsgId:messageID];
      return;
    }
    NOTI_LOG(0x258, @"[%s] msg is revoke");
    [self reportMessageStatus:0x837 withMessageID:[[found objectForKey:@"_j_msgid"] stringValue]
        msgData:[found objectForKey:@"_j_data_"]];
    NSMutableArray *remaining = [NSMutableArray arrayWithArray:messages];
    [remaining removeObject:found];
    self.datasArr = [NSMutableArray arrayWithArray:remaining];
    [JCORECacheUtilities setCacheObject:remaining forKey:@"JPushNotiInMessagesKey"];
  } @catch (NSException *exception) {
    NOTI_LOG(0x269, @"[%s] is exception");
  }
}
- (BOOL)checkMsgRevoke:(id)messageID {
  if (![JCOREThread isMainThread]) {
    NOTI_LOG(0x26f, @"[%s] thread no is JCOREThread");
    return NO;
  }
  __block BOOL revoked = NO;
  @try {
    id cached = [JCORECacheUtilities cacheObjectForKey:@"JPushInMessageRevokeKey"];
    NSMutableArray *removed = [NSMutableArray array];
    [cached enumerateObjectsUsingBlock:^(NSDictionary *message, NSUInteger index, BOOL *stop) {
      if ([message isKindOfClass:NSDictionary.class]) {
        double timestamp = [[message objectForKey:@"time"] doubleValue];
        if ([JCOREUtilities currentTimestamp] - timestamp >= 86400.0) {
          [removed addObject:message];
        } else if ([[message objectForKey:@"_j_msgid"] isEqualToString:messageID]) {
          revoked = YES;
          [removed addObject:message];
        }
      }
    }];
    NSMutableArray *remaining = [NSMutableArray arrayWithArray:cached];
    [removed enumerateObjectsUsingBlock:^(id message, NSUInteger index, BOOL *stop) {
      [remaining removeObject:message];
    }];
    [JCORECacheUtilities setCacheObject:remaining forKey:@"JPushInMessageRevokeKey"];
    return revoked;
  } @catch (NSException *exception) {
    NOTI_LOG(0x293, @"[%s] is exception");
  }
  return revoked;
}
- (void)cacheRevokeMsgId:(id)messageID {
  if (![JCOREThread isMainThread]) {
    NOTI_LOG(0x29c, @"[%s] thread no is JCOREThread");
    return;
  }
  @try {
    id cached = [JCORECacheUtilities cacheObjectForKey:@"JPushInMessageRevokeKey"];
    NSMutableArray *messages = [NSMutableArray arrayWithArray:cached];
    [messages addObject:@{@"_j_msgid" : messageID, @"time" : @([JCOREUtilities currentTimestamp])}];
    [JCORECacheUtilities setCacheObject:messages forKey:@"JPushInMessageRevokeKey"];
  } @catch (NSException *exception) {
    NOTI_LOG(0x2ab, @"[%s] is exception");
  }
}
@end
#endif
