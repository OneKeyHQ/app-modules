#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
#import "JCORELog.h"
#import "JPUSHAutomatedTester.h"
#import "JPUSHLocalNotificationController.h"
#import "RecoveredNotifications.h"
#define LOCAL_LOG(lineNo, formatValue, ...)                                                                  \
    [JCORELog log:NO                                                                                         \
            level:0x3ff                                                                                      \
             flag:1                                                                                          \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/ios-jpush/JPush/JPush/Controller/"    \
                  "JPUSHLocalNotificationController.m"                                                       \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineNo                                                                                     \
              tag:nil                                                                                        \
           format:formatValue, ##__VA_ARGS__]
static NSString *const LocalNotificationKey = @"__JPUSHNotificationKey";
@implementation JPUSHLocalNotificationController
+ (UILocalNotification *)setLocalNotification:(NSDate *)fireDate
                                    alertBody:(NSString *)body
                                        badge:(int)badge
                                  alertAction:(NSString *)action
                                identifierKey:(NSString *)key
                                     userInfo:(NSDictionary *)info
                                    soundName:(NSString *)sound
                                       region:(CLRegion *)region
                           regionTriggersOnce:(BOOL)once
                                     category:(NSString *)category {
    if (!fireDate) {
        if (UIDevice.currentDevice.systemVersion.floatValue >= 8) {
            if (!region) {
                LOCAL_LOG(0x25, @"通知时间和region不能同时为空");
                [JPUSHAutomatedTester PostAutomatedTestStatusNotification:19];
                return nil;
            }
        } else {
            LOCAL_LOG(0x2a, @"通知时间不能为空");
            [JPUSHAutomatedTester PostAutomatedTestStatusNotification:18];
            return nil;
        }
    } else if ([fireDate compare:NSDate.date] == NSOrderedAscending) {
        LOCAL_LOG(0x2f, @"设置LocalNotification通知时间小于当前时间");
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:20];
        return nil;
    }
    UILocalNotification *notification = [[UILocalNotification alloc] init];
    if (!notification) {
        LOCAL_LOG(0x75, @"设置LocalNotification失败");
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:21];
        return nil;
    }
    if (fireDate)
        notification.fireDate = fireDate;
    notification.timeZone = NSTimeZone.defaultTimeZone;
    notification.soundName = UILocalNotificationDefaultSoundName;
    notification.alertBody = body;
    notification.soundName = sound ?: UILocalNotificationDefaultSoundName;
    notification.alertAction = action.length ? action : nil;
    notification.applicationIconBadgeNumber = badge < 0 ? 0 : (badge == 0 ? -1 : badge);
    NSMutableDictionary *userInfo =
        info.count ? [NSMutableDictionary dictionaryWithDictionary:info] : [[NSMutableDictionary alloc] init];
    if (key.length)
        userInfo[LocalNotificationKey] = key;
    notification.userInfo = userInfo.count ? userInfo : nil;
    if (UIDevice.currentDevice.systemVersion.floatValue >= 8) {
        if (region)
            notification.region = region;
        if (once)
            notification.regionTriggersOnce = YES;
        if (category)
            notification.category = category;
    }
    [self addNotificationToCacheWithNotification:notification andKey:key];
    return [notification copy];
}
+ (void)addNotificationToCacheWithNotification:(UILocalNotification *)notification andKey:(NSString *)key {
    if ((NSInteger)UIApplication.sharedApplication.scheduledLocalNotifications.count < 64) {
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:16];
        [UIApplication.sharedApplication scheduleLocalNotification:notification];
    } else {
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:17];
        LOCAL_LOG(0x82, @"设置本地通知不能大于64个");
    }
}
+ (void)showLocalNotificationAtFront:(UILocalNotification *)notification identifierKey:(NSString *)key {
    if (!notification) {
        LOCAL_LOG(0xa2, @"notification参数错误，不能为空");
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:24];
        return;
    }
    NSString *stored = notification.userInfo[LocalNotificationKey];
    if (!key || (stored && [key isEqualToString:stored])) {
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:22];
        UIAlertView *alert;
        if (notification.alertAction.length)
            alert = [[UIAlertView alloc] initWithTitle:@""
                                               message:notification.alertBody
                                              delegate:self
                                     cancelButtonTitle:@"关闭"
                                     otherButtonTitles:notification.alertAction, nil];
        else
            alert = [[UIAlertView alloc] initWithTitle:@""
                                               message:notification.alertBody
                                              delegate:self
                                     cancelButtonTitle:@"关闭"
                                     otherButtonTitles:nil];
        [alert show];
    } else
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:23];
}
+ (NSArray *)findLocalNotificationWithIdentifier:(NSString *)key {
    if (!key) {
        LOCAL_LOG(0xa9, @"notificationKey参数错误，不能为空");
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:26];
        return nil;
    }
    NSMutableArray *found = [NSMutableArray array];
    for (UILocalNotification *notification in UIApplication.sharedApplication.scheduledLocalNotifications) {
        NSString *stored = notification.userInfo[LocalNotificationKey];
        if (stored && [stored isEqualToString:key])
            [found addObject:notification];
    }
    [JPUSHAutomatedTester PostAutomatedTestStatusNotification:28];
    return found;
}
+ (NSArray *)findAllLocalNotifications {
    return UIApplication.sharedApplication.scheduledLocalNotifications;
}
+ (void)deleteLocalNotificationWithIdentifierKey:(NSString *)key {
    if (!key) {
        LOCAL_LOG(0xc2, @"notificationKey参数错误,不能为空");
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:26];
    } else {
        [self p_deleteCacheObjectWithKey:key
                              UsingBlock:^(UILocalNotification *notification, BOOL *stop) {
                                  [UIApplication.sharedApplication cancelLocalNotification:notification];
                              }];
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:25];
    }
}
+ (void)deleteLocalNotification:(UILocalNotification *)notification {
    if (!notification) {
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:27];
        LOCAL_LOG(0xd3, @"localNotification参数错误,不能为空");
    } else {
        [UIApplication.sharedApplication cancelLocalNotification:notification];
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:25];
    }
}
+ (void)clearAllLocalNotifications {
    [UIApplication.sharedApplication cancelAllLocalNotifications];
    [JPUSHAutomatedTester PostAutomatedTestStatusNotification:29];
}
+ (void)p_deleteCacheObjectWithKey:(NSString *)key UsingBlock:(void (^)(UILocalNotification *, BOOL *))block {
    NSArray *found = [self findLocalNotificationWithIdentifier:key];
    BOOL stop = NO;
    for (UILocalNotification *notification in found) {
        if (stop)
            break;
        block(notification, &stop);
    }
}
+ (void)addNotification:(JPushNotificationRequest *)request {
    if (!request || !request.content || !request.trigger)
        LOCAL_LOG(0xf2, @"传入参数有误，无法注册或更新推送");
    if (UIDevice.currentDevice.systemVersion.floatValue >= 10)
        [self addNotification_ios10:request];
    else if (UIDevice.currentDevice.systemVersion.floatValue >= 8)
        [self addNotification_ios8:request];
    else {
        JPushNotificationContent *c = request.content;
        id notification = [self setLocalNotification:request.trigger.fireDate
                                           alertBody:c.body
                                               badge:c.badge.intValue
                                         alertAction:c.action
                                       identifierKey:request.requestIdentifier
                                            userInfo:c.userInfo
                                           soundName:c.sound
                                              region:nil
                                  regionTriggersOnce:NO
                                            category:nil];
        if (request.completionHandler)
            request.completionHandler([notification copy]);
    }
}
+ (void)removeNotification:(JPushNotificationIdentifier *)identifier {
    if (UIDevice.currentDevice.systemVersion.floatValue >= 10) {
        [self removeNotification_ios10:identifier];
        return;
    }
    if (!identifier) {
        [self clearAllLocalNotifications];
        return;
    }
    if (identifier.notificationObj)
        [self deleteLocalNotification:identifier.notificationObj];
    if (identifier.identifiers.count) {
        [identifier.identifiers enumerateObjectsUsingBlock:^(NSString *key, NSUInteger index, BOOL *stop) {
            [self deleteLocalNotificationWithIdentifierKey:key];
        }];
    } else if (!identifier.notificationObj)
        [self clearAllLocalNotifications];
}
+ (void)findNotification:(JPushNotificationIdentifier *)identifier {
    if (!identifier) {
        LOCAL_LOG(0x126, @"传入参数为nil，无法返回查询结果");
        return;
    }
    if (!identifier.findCompletionHandler) {
        LOCAL_LOG(0x12a, @"未传入findCompletionHandler回调参数，无法返回查询结果");
        return;
    }
    if (UIDevice.currentDevice.systemVersion.floatValue >= 10) {
        [self findNotification_ios10:identifier];
        return;
    }
    NSMutableArray *found = [NSMutableArray array];
    if (!identifier.identifiers.count)
        [found addObjectsFromArray:[self findAllLocalNotifications]];
    else
        [identifier.identifiers enumerateObjectsUsingBlock:^(NSString *key, NSUInteger index, BOOL *stop) {
            [found addObjectsFromArray:[self findLocalNotificationWithIdentifier:key]];
        }];
    identifier.findCompletionHandler([found copy]);
}
+ (void)addNotification_ios8:(JPushNotificationRequest *)request {
    JPushNotificationContent *c = request.content;
    JPushNotificationTrigger *t = request.trigger;
    id notification = [JPUSHLocalNotificationController setLocalNotification:t.fireDate
                                                                   alertBody:c.body
                                                                       badge:c.badge.intValue
                                                                 alertAction:c.action
                                                               identifierKey:request.requestIdentifier
                                                                    userInfo:c.userInfo
                                                                   soundName:c.sound
                                                                      region:t.region
                                                          regionTriggersOnce:t.repeat
                                                                    category:c.categoryIdentifier];
    if (request.completionHandler)
        request.completionHandler([notification copy]);
}
+ (void)addNotification_ios10:(JPushNotificationRequest *)request {
    id content = [[NSClassFromString(@"UNMutableNotificationContent") alloc] init];
    JPushNotificationContent *c = request.content;
    [self invokeMethod:content name:@"setTitle:" param:c.title result:NULL];
    [self invokeMethod:content name:@"setSubtitle:" param:c.subtitle result:NULL];
    [self invokeMethod:content name:@"setBody:" param:c.body result:NULL];
    id soundClass = NSClassFromString(@"UNNotificationSound");
    __unsafe_unretained id sound = nil;
    NSString *soundName = c.soundSetting.soundName ?: c.sound;
    [self invokeMethod:soundClass
                  name:soundName ? @"soundNamed:" : @"defaultSound"
                 param:soundName
                result:&sound];
    if (@available(iOS 12.0, *)) {
        if (c.soundSetting.criticalSoundName)
            [self invokeMethod:soundClass
                          name:@"criticalSoundNamed:withAudioVolume:"
                         param:c.soundSetting.criticalSoundName
                    floatParam:c.soundSetting.criticalSoundVolume
                        result:&sound];
        else if (soundName)
            [self invokeMethod:soundClass name:@"soundNamed:" param:soundName result:&sound];
        else
            [self invokeMethod:soundClass
                          name:@"defaultCriticalSoundWithAudioVolume:"
                    floatParam:c.soundSetting.criticalSoundVolume
                        result:&sound];
    }
    [self invokeMethod:content name:@"setSound:" param:sound result:NULL];
    [self invokeMethod:content name:@"setBadge:" param:c.badge result:NULL];
    [self invokeMethod:content name:@"setCategoryIdentifier:" param:c.categoryIdentifier result:NULL];
    [self invokeMethod:content name:@"setAttachments:" param:c.attachments result:NULL];
    [self invokeMethod:content name:@"setUserInfo:" param:c.userInfo result:NULL];
    [self invokeMethod:content name:@"setThreadIdentifier:" param:c.threadIdentifier result:NULL];
    [self invokeMethod:content name:@"setLaunchImageName:" param:c.launchImageName result:NULL];
    if (UIDevice.currentDevice.systemVersion.floatValue >= 12) {
        if (@available(iOS 12.0, *)) {
            if ([content respondsToSelector:NSSelectorFromString(@"setSummaryArgument:")])
                [self invokeMethod:content name:@"setSummaryArgument:" param:c.summaryArgument result:NULL];
            if ([content respondsToSelector:NSSelectorFromString(@"setSummaryArgumentCount:")])
                [self invokeMethod:content
                              name:@"setSummaryArgumentCount:"
                     uIntegerParam:c.summaryArgumentCount
                            result:NULL];
        }
    }
    if (@available(iOS 13.0, *))
        if ([content respondsToSelector:NSSelectorFromString(@"setTargetContentIdentifier:")])
            [self invokeMethod:content
                          name:@"setTargetContentIdentifier:"
                         param:c.targetContentIdentifier
                        result:NULL];
    if (@available(iOS 15.0, *)) {
        // The original passes this double through its NSUInteger invocation helper.
        if ([content respondsToSelector:NSSelectorFromString(@"setRelevanceScore:")])
            [self invokeMethod:content
                          name:@"setRelevanceScore:"
                 uIntegerParam:(NSUInteger)c.relevanceScore
                        result:NULL];
        if ([content respondsToSelector:NSSelectorFromString(@"setInterruptionLevel:")])
            [self invokeMethod:content
                          name:@"setInterruptionLevel:"
                 uIntegerParam:c.interruptionLevel
                        result:NULL];
    }
    if (@available(iOS 16.0, *))
        if ([content respondsToSelector:NSSelectorFromString(@"setFilterCriteria:")])
            [self invokeMethod:content name:@"setFilterCriteria:" param:c.filterCriteria result:NULL];
    JPushNotificationTrigger *t = request.trigger;
    __unsafe_unretained id trigger = nil;
    if (t.region)
        [self invokeMethod:NSClassFromString(@"UNLocationNotificationTrigger")
                      name:@"triggerWithRegion:repeats:"
                     param:t.region
                 boolParam:t.repeat
                    result:&trigger];
    else if (t.dateComponents)
        [self invokeMethod:NSClassFromString(@"UNCalendarNotificationTrigger")
                      name:@"triggerWithDateMatchingComponents:repeats:"
                     param:t.dateComponents
                 boolParam:t.repeat
                    result:&trigger];
    else if (t.timeInterval > 0)
        [self invokeMethod:NSClassFromString(@"UNTimeIntervalNotificationTrigger")
                      name:@"triggerWithTimeInterval:repeats:"
               doubleParam:t.timeInterval
                 boolParam:t.repeat
                    result:&trigger];
    else {
        LOCAL_LOG(
            0x1aa,
            @"请设置有效的触发推送方式，region、dateComponents、timeInterval对其中一个属性设置有效值即可");
        return;
    }
    if (!request.requestIdentifier.length) {
        LOCAL_LOG(0x1b2, @"identifier不能传入空值");
        return;
    }
    __unsafe_unretained id notification = nil;
    [self invokeMethod:NSClassFromString(@"UNNotificationRequest")
                  name:@"requestWithIdentifier:content:trigger:"
                 param:request.requestIdentifier
                param2:content
                param3:trigger
                result:&notification];
    __unsafe_unretained id center = nil;
    [self invokeMethod:NSClassFromString(@"UNUserNotificationCenter")
                  name:@"currentNotificationCenter"
                 param:nil
                result:&center];
    id copied = [notification copy];
    void (^completion)(NSError *) = ^(NSError *error) {
        if (error)
            LOCAL_LOG(0x1bf, @"注册推送失败：%@", error.localizedDescription);
        if (request.completionHandler)
            request.completionHandler(error ? nil : copied);
    };
    [self invokeMethod:center
                  name:@"addNotificationRequest:withCompletionHandler:"
                 param:notification
                param2:completion
                result:NULL];
}
+ (void)removeNotification_ios10:(JPushNotificationIdentifier *)identifier {
    __unsafe_unretained id center = nil;
    [self invokeMethod:NSClassFromString(@"UNUserNotificationCenter")
                  name:@"currentNotificationCenter"
                 param:nil
                result:&center];
    if (!identifier) {
        [self invokeMethod:center name:@"removeAllDeliveredNotifications" param:nil result:NULL];
        [self invokeMethod:center name:@"removeAllPendingNotificationRequests" param:nil result:NULL];
        return;
    }
    NSString *name =
        identifier.delivered
            ? (identifier.identifiers.count ? @"removeDeliveredNotificationsWithIdentifiers:"
                                            : @"removeAllDeliveredNotifications")
            : (identifier.identifiers.count ? @"removePendingNotificationRequestsWithIdentifiers:"
                                            : @"removeAllPendingNotificationRequests");
    [self invokeMethod:center
                  name:name
                 param:identifier.identifiers.count ? identifier.identifiers : nil
                result:NULL];
}
+ (void)findNotification_ios10:(JPushNotificationIdentifier *)identifier {
    NSArray *identifiers = identifier.identifiers;
    __unsafe_unretained id center = nil;
    [self invokeMethod:NSClassFromString(@"UNUserNotificationCenter")
                  name:@"currentNotificationCenter"
                 param:nil
                result:&center];
    if (identifier.delivered) {
        void (^completion)(NSArray *) = ^(NSArray *notifications) {
            if (!identifiers.count) {
                identifier.findCompletionHandler([notifications copy]);
                return;
            }
            NSMutableArray *found = [NSMutableArray array];
            [notifications enumerateObjectsUsingBlock:^(id notification, NSUInteger index, BOOL *stop) {
                __unsafe_unretained id request = nil, value = nil;
                [self invokeMethod:notification name:@"request" param:nil result:&request];
                [self invokeMethod:request name:@"identifier" param:nil result:&value];
                if ([identifiers containsObject:value])
                    [found addObject:notification];
            }];
            identifier.findCompletionHandler([found copy]);
        };
        [self invokeMethod:center
                      name:@"getDeliveredNotificationsWithCompletionHandler:"
                     param:completion
                    result:NULL];
    } else {
        void (^completion)(NSArray *) = ^(NSArray *requests) {
            if (!identifiers.count) {
                identifier.findCompletionHandler([requests copy]);
                return;
            }
            NSMutableArray *found = [NSMutableArray array];
            [requests enumerateObjectsUsingBlock:^(id request, NSUInteger index, BOOL *stop) {
                __unsafe_unretained id value = nil;
                [self invokeMethod:request name:@"identifier" param:nil result:&value];
                if ([identifiers containsObject:value])
                    [found addObject:request];
            }];
            identifier.findCompletionHandler([found copy]);
        };
        [self invokeMethod:center
                      name:@"getPendingNotificationRequestsWithCompletionHandler:"
                     param:completion
                    result:NULL];
    }
}
+ (void)invokeMethod:(id)target name:(NSString *)name param:(id)param result:(void *)result {
    [self invokeMethod:target name:name param:param param2:nil param3:nil result:result];
}
+ (void)invokeMethod:(id)target
                name:(NSString *)name
               param:(id)param
              param2:(id)param2
              result:(void *)result {
    [self invokeMethod:target name:name param:param param2:param2 param3:nil result:result];
}
+ (void)invokeMethod:(id)target
                name:(NSString *)name
               param:(id)param
           boolParam:(BOOL)value
              result:(void *)result {
    SEL selector = NSSelectorFromString(name);
    NSInvocation *invocation =
        [NSInvocation invocationWithMethodSignature:[target methodSignatureForSelector:selector]];
    invocation.target = target;
    invocation.selector = selector;
    if (param)
        [invocation setArgument:&param atIndex:2];
    if (value)
        [invocation setArgument:&value atIndex:3];
    [invocation retainArguments];
    [invocation invoke];
    if (result)
        [invocation getReturnValue:result];
}
+ (void)invokeMethod:(id)target
                name:(NSString *)name
         doubleParam:(double)value
           boolParam:(BOOL)repeat
              result:(void *)result {
    SEL selector = NSSelectorFromString(name);
    NSInvocation *invocation =
        [NSInvocation invocationWithMethodSignature:[target methodSignatureForSelector:selector]];
    invocation.target = target;
    invocation.selector = selector;
    if (value)
        [invocation setArgument:&value atIndex:2];
    if (repeat)
        [invocation setArgument:&repeat atIndex:3];
    [invocation retainArguments];
    [invocation invoke];
    if (result)
        [invocation getReturnValue:result];
}
+ (void)invokeMethod:(id)target name:(NSString *)name uIntegerParam:(NSUInteger)value result:(void *)result {
    SEL selector = NSSelectorFromString(name);
    NSInvocation *invocation =
        [NSInvocation invocationWithMethodSignature:[target methodSignatureForSelector:selector]];
    invocation.target = target;
    invocation.selector = selector;
    if (value)
        [invocation setArgument:&value atIndex:2];
    [invocation retainArguments];
    [invocation invoke];
    if (result)
        [invocation getReturnValue:result];
}
+ (void)invokeMethod:(id)target
                name:(NSString *)name
               param:(id)param
          floatParam:(float)value
              result:(void *)result {
    SEL selector = NSSelectorFromString(name);
    NSInvocation *invocation =
        [NSInvocation invocationWithMethodSignature:[target methodSignatureForSelector:selector]];
    invocation.target = target;
    invocation.selector = selector;
    if (param)
        [invocation setArgument:&param atIndex:2];
    if (value)
        [invocation setArgument:&value atIndex:3];
    [invocation retainArguments];
    [invocation invoke];
    if (result)
        [invocation getReturnValue:result];
}
+ (void)invokeMethod:(id)target name:(NSString *)name floatParam:(float)value result:(void *)result {
    SEL selector = NSSelectorFromString(name);
    NSInvocation *invocation =
        [NSInvocation invocationWithMethodSignature:[target methodSignatureForSelector:selector]];
    invocation.target = target;
    invocation.selector = selector;
    if (value)
        [invocation setArgument:&value atIndex:2];
    [invocation retainArguments];
    [invocation invoke];
    if (result)
        [invocation getReturnValue:result];
}
+ (void)invokeMethod:(id)target
                name:(NSString *)name
               param:(id)param
              param2:(id)param2
              param3:(id)param3
              result:(void *)result {
    SEL selector = NSSelectorFromString(name);
    NSInvocation *invocation =
        [NSInvocation invocationWithMethodSignature:[target methodSignatureForSelector:selector]];
    invocation.target = target;
    invocation.selector = selector;
    if (param)
        [invocation setArgument:&param atIndex:2];
    if (param2)
        [invocation setArgument:&param2 atIndex:3];
    if (param3)
        [invocation setArgument:&param3 atIndex:4];
    [invocation retainArguments];
    [invocation invoke];
    if (result)
        [invocation getReturnValue:result];
}
@end
#pragma clang diagnostic pop
#endif
