#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHNotificationController.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JPUSHCache.h"
#import "JPUSHMessageReport.h"
#import "JPUSHService.h"
extern NSString *const kJCORENetworkCmdCtrlNotification;
extern NSString *const kJCORENetworkDidLoginNotification;
extern NSString *const kJPUSHCheckNotiAuthNotification;
#import "JPUSHInMessageManager.h"
#define NOTIFICATION_LOG(flagValue, lineValue, formatValue, ...)                 \
  [JCORELog log:YES level:0x3ff flag:flagValue context:0                          \
          file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"            \
               "ios-jpush/JPush/JPush/Controller/JPUSHNotificationController.m" \
      function:__PRETTY_FUNCTION__ line:lineValue tag:nil                        \
        format:formatValue, ##__VA_ARGS__]
@implementation JPUSHNotificationController
- (void)dealloc {
  [NSNotificationCenter.defaultCenter removeObserver:self];
}
- (instancetype)init {
  if ((self = [super init])) {
    [NSNotificationCenter.defaultCenter addObserver:self
        selector:@selector(handleCmdControl:)
        name:kJCORENetworkCmdCtrlNotification object:nil];
    [NSNotificationCenter.defaultCenter addObserver:self
        selector:@selector(didLogin:)
        name:kJCORENetworkDidLoginNotification object:nil];
  }
  return self;
}
- (void)start {
  NOTIFICATION_LOG(0x100, 0x2c, @"start notification control support");
  [self checkCachedUndoNotification];
}
- (void)didLogin:(id)notification {
  static BOOL checked;
  if (@available(iOS 10.0, *)) {
  } else if (!checked) {
    checked = YES;
    [NSNotificationCenter.defaultCenter
        postNotificationName:kJPUSHCheckNotiAuthNotification object:nil
        userInfo:@{@"type" : @0}];
  }
}
- (void)handleCmdControl:(id)notification {
  if (!notification || ![notification userInfo])
    return;
  id info = [notification userInfo];
  id content = [info isKindOfClass:NSDictionary.class]
                   ? [info objectForKey:@"cmd_content"] : nil;
  if (![content isKindOfClass:NSDictionary.class]) {
    NOTIFICATION_LOG(0x100, 0x4a, @"handle cmd control cancel, cmd content nil");
    return;
  }
  id command = content[@"cmd"];
  if (!command) {
    NOTIFICATION_LOG(0x100, 0x4f, @"handle cmd control cancel, cmd nil");
    return;
  }
  signed char value = [command charValue];
  if (value == 60) {
    [self handleCheckNotiAuth:nil];
    return;
  }
  id body = content[@"content"];
  if (![body isKindOfClass:NSDictionary.class]) {
    NOTIFICATION_LOG(0x40, 0x57, @"handle cmd control cancel, content error");
    return;
  }
  if (![body count]) {
    NOTIFICATION_LOG(0x100, 0x5c, @"handle cmd control cancel, content nil");
    return;
  }
  if (value == 59)
    [self handleUndoContent:body];
}
- (void)handleCheckNotiAuth:(id)content {
  [NSNotificationCenter.defaultCenter
      postNotificationName:kJPUSHCheckNotiAuthNotification object:nil
      userInfo:@{@"type" : @1}];
}
- (void)checkCachedUndoNotification {
  [self cacheUndoNotification:nil];
}
- (void)cacheUndoNotification:(id)entry {
  id cached = [JPUSHCache objectForKey:@"__JPUSH_UNDO_NOTIFICATION_LIST"];
  NSMutableArray *valid = [NSMutableArray new];
  if ([cached isKindOfClass:NSArray.class] && [cached count]) {
    for (NSUInteger index = 0; index < [cached count]; index++) {
      id item = cached[index];
      if (![item isKindOfClass:NSDictionary.class] ||
          [item isEqualToDictionary:entry])
        continue;
      id time = [item valueForKey:@"time"];
      if (![time isKindOfClass:NSNumber.class])
        continue;
      double timestamp = [time doubleValue];
      if ([JCOREUtilities currentTimestamp] - timestamp > 86400.0)
        continue;
      id messageID = [item valueForKey:@"msg_id"];
      if ([messageID isKindOfClass:NSString.class] && [messageID length]) {
        [valid addObject:item];
        [self handleUndoNotification:messageID];
      }
    }
  }
  if ([entry isKindOfClass:NSDictionary.class] && [entry count])
    [valid addObject:entry];
  if (cached || entry)
    [JPUSHCache setObject:[valid copy] forKey:@"__JPUSH_UNDO_NOTIFICATION_LIST"];
}
- (void)handleUndoContent:(id)content {
  NOTIFICATION_LOG(0x100, 0xa5, @"handle cmd control:%@", content);
  id ids = content[@"ids"];
  id messageID = ([ids isKindOfClass:NSString.class] && [ids length]) ? ids : nil;
  if ([ids isKindOfClass:NSNumber.class])
    messageID = [ids stringValue];
  if (![messageID isKindOfClass:NSString.class] || ![messageID length]) {
    NOTIFICATION_LOG(0x40, 0xaf, @"handle undo notification cancel, msg_id error");
    return;
  }
  [self handleUndoNotification:messageID];
  [self cacheUndoNotification:@{
    @"time" : @([JCOREUtilities currentTimestamp]), @"msg_id" : messageID
  }];
  [[JPUSHInMessageManager shareManager] handleRevokeMessageId:messageID];
}
- (void)handleUndoNotification:(id)messageID {
  if (![messageID isKindOfClass:NSString.class] || ![messageID length])
    return;
  id identifiers = [messageID componentsSeparatedByString:@","];
  for (NSUInteger index = 0; index < [identifiers count]; index++) {
    id identifier = identifiers[index];
    if ([identifier isKindOfClass:NSString.class] && [identifier length]) {
      [self removeNotification:identifier withNeedFind:YES];
      [self performSelector:@selector(handleOtherNotification:)
                 withObject:identifier afterDelay:0.0];
    }
  }
}
- (void)removeNotification:(id)identifier withNeedFind:(BOOL)find {
  if (![identifier isKindOfClass:NSString.class] || ![identifier length])
    return;
  dispatch_async(dispatch_get_main_queue(), ^{
    JPushNotificationIdentifier *item = [JPushNotificationIdentifier new];
    item.delivered = YES;
    item.identifiers = @[identifier];
    if (find) {
      item.findCompletionHandler = ^(NSArray *notifications) {
        if ([notifications isKindOfClass:NSArray.class] && notifications.count)
          [self reportMsgStatusWithMessageId:identifier];
      };
      [JPUSHService findNotification:item];
    }
    [JPUSHService removeNotification:item];
  });
}
- (void)handleOtherNotification:(id)messageID {
  JPushNotificationIdentifier *item = [JPushNotificationIdentifier new];
  item.delivered = YES;
  item.identifiers = @[];
  item.findCompletionHandler = ^(NSArray *notifications) {
    if (![notifications isKindOfClass:NSArray.class] || !notifications.count)
      return;
    for (NSUInteger index = 0; index < notifications.count; index++) {
      id notification = notifications[index];
      id info = [self userInfoFromNotification:notification];
      if (![info isKindOfClass:NSDictionary.class] || ![info count])
        continue;
      NSString *found = [NSString stringWithFormat:@"%@", info[@"_j_msgid"]];
      if (![messageID isEqualToString:found])
        continue;
      id identifier = [self identifierFromNotification:notification];
      if ([identifier isKindOfClass:NSString.class] && [identifier length]) {
        [self reportMsgStatusWithMessageId:messageID];
        [self removeNotification:identifier withNeedFind:NO];
        return;
      }
    }
  };
  [JPUSHService findNotification:item];
}
- (void)reportMsgStatusWithMessageId:(id)messageID {
  JPUSHMessageReportRequest *request = [JPUSHMessageReportRequest new];
  [request setActiveType:2007 messageID:messageID flag:-10000];
  [request send];
}
- (id)identifierFromNotification:(id)notification {
  if (@available(iOS 10.0, *)) {
    if ([notification isKindOfClass:NSClassFromString(@"UNNotification")]) {
      __unsafe_unretained id request = nil;
      [self invokeMethod:notification name:@"request" param:nil result:&request];
      if (![request isKindOfClass:NSClassFromString(@"UNNotificationRequest")])
        return nil;
      __unsafe_unretained id identifier = nil;
      [self invokeMethod:request name:@"identifier" param:nil result:&identifier];
      return identifier;
    }
  }
  return nil;
}
- (id)userInfoFromNotification:(id)notification {
  if (@available(iOS 10.0, *)) {
    if ([notification isKindOfClass:NSClassFromString(@"UNNotification")]) {
      __unsafe_unretained id request = nil;
      [self invokeMethod:notification name:@"request" param:nil result:&request];
      if (![request isKindOfClass:NSClassFromString(@"UNNotificationRequest")])
        return nil;
      __unsafe_unretained id content = nil;
      [self invokeMethod:request name:@"content" param:nil result:&content];
      if (![content isKindOfClass:NSClassFromString(@"UNNotificationContent")])
        return nil;
      __unsafe_unretained id info = nil;
      [self invokeMethod:content name:@"userInfo" param:nil result:&info];
      return info;
    }
  }
  return nil;
}
- (void)invokeMethod:(id)target name:(id)name param:(id)param result:(void *)result {
  SEL selector = NSSelectorFromString(name);
  NSMethodSignature *signature = [target methodSignatureForSelector:selector];
  if (!signature)
    return;
  NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:signature];
  invocation.target = target;
  invocation.selector = selector;
  if (param)
    [invocation setArgument:&param atIndex:2];
  [invocation retainArguments];
  [invocation invoke];
  if (result)
    [invocation getReturnValue:result];
}
@end
#endif
