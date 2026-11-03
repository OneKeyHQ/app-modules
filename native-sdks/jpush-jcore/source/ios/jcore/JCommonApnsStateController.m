#import "JCommonApnsStateController.h"
#import "JCORECacheChannel.h"
#import "JCOREFCCache.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCommonReportManager.h"
#import <UIKit/UIKit.h>
#import <objc/message.h>
#define ApnsStateFile                                                                                        \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/"                        \
    "JCommonApnsStateController.m"
// The original retains these legacy notification-setting probes.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
@implementation JCommonAPNSStateEntity
+ (void)currentState:(void (^)(JCommonAPNSStateEntity *))completion {
    JCommonAPNSStateEntity *state = [[JCommonAPNSStateEntity alloc] init];
    if (UIDevice.currentDevice.systemVersion.floatValue >= 8) {
        if (UIDevice.currentDevice.systemVersion.floatValue >= 10) {
            void (^handle)(id) = ^(id settings) {
#define SETTING(selectorName, propertyName, valueType)                                                       \
    do {                                                                                                     \
        SEL selector = NSSelectorFromString(@selectorName);                                                  \
        if ([settings respondsToSelector:selector])                                                          \
            state.propertyName = (valueType)((NSUInteger (*)(id, SEL, SEL))objc_msgSend)(                    \
                settings, @selector(performSelector:), selector);                                            \
    } while (0)
              SETTING("notificationCenterSetting", notificationCenter, NSUInteger);
              SETTING("badgeSetting", badge, NSUInteger);
              SETTING("soundSetting", sound, NSUInteger);
              SETTING("alertSetting", alert, NSUInteger);
              SETTING("authorizationStatus", authStatus, NSInteger);
              SETTING("lockScreenSetting", lockScreen, NSUInteger);
              SETTING("carPlaySetting", carPlay, NSUInteger);
              SETTING("criticalAlertSetting", criticalAlert, NSUInteger);
              SETTING("alertStyle", alertStyle, NSInteger);
              SEL provides = NSSelectorFromString(@"providesAppNotificationSettings");
              if ([settings respondsToSelector:provides])
                  state.providesAppNotification = ((NSUInteger (*)(id, SEL, SEL))objc_msgSend)(
                                                      settings, @selector(performSelector:), provides) != 0;
              SETTING("announcementSetting", announcement, NSUInteger);
#undef SETTING
              completion(state);
            };
            Class centerClass = NSClassFromString(@"UNUserNotificationCenter");
            if (!centerClass)
                return;
            SEL current = NSSelectorFromString(@"currentNotificationCenter");
            if (![centerClass respondsToSelector:current])
                return;
            NSMethodSignature *signature = [centerClass methodSignatureForSelector:current];
            if (!signature)
                return;
            NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:signature];
            invocation.target = centerClass;
            invocation.selector = current;
            [invocation invoke];
            __unsafe_unretained id center = nil;
            [invocation getReturnValue:&center];
            SEL get = NSSelectorFromString(@"getNotificationSettingsWithCompletionHandler:");
            signature = [center methodSignatureForSelector:get];
            if (!signature)
                return;
            invocation = [NSInvocation invocationWithMethodSignature:signature];
            invocation.target = center;
            invocation.selector = get;
            [invocation setArgument:&handle atIndex:2];
            [invocation invoke];
            return;
        }
        NSUInteger types = UIApplication.sharedApplication.currentUserNotificationSettings.types;
        state.notificationCenter = types ? 2 : 1;
        state.authStatus = types ? 2 : 1;
        state.badge = (types & 1) ? 2 : 1;
        state.sound = (types & 2) ? 2 : 1;
        state.alert = (types & 4) ? 2 : 1;
    } else {
        NSUInteger types = UIApplication.sharedApplication.enabledRemoteNotificationTypes;
        state.notificationCenter = types ? 2 : 1;
        state.authStatus = types ? 2 : 1;
        state.badge = (types & 1) ? 2 : 1;
        state.sound = (types & 2) ? 2 : 1;
        state.alert = (types & 4) ? 2 : 1;
    }
    completion(state);
}
@end
#pragma clang diagnostic pop
@implementation JCommonApnsStateController
+ (BOOL)isChanged:(JCommonAPNSStateEntity *)state {
    id old = [JCORECacheChannel lastApnsState];
    if (![old isKindOfClass:NSString.class] || ![old length])
        old = [JCOREFCCache objectForKey:@"_JCorePersistenceLastAPNS"];
    else
        [JCORECacheChannel setLastApnsState:nil];
    NSString *current = [NSString
        stringWithFormat:@"%lu %lu %lu %lu %lu %lu %lu %lu %lu %d %lu", (unsigned long)state.notificationCenter,
                         (unsigned long)state.badge, (unsigned long)state.sound, (unsigned long)state.alert,
                         (unsigned long)state.authStatus, (unsigned long)state.lockScreen,
                         (unsigned long)state.carPlay, (unsigned long)state.criticalAlert,
                         (unsigned long)state.alertStyle, state.providesAppNotification,
                         (unsigned long)state.announcement];
    if (![old isKindOfClass:NSString.class] || ![current isEqualToString:old]) {
        [JCOREFCCache setObject:current forKey:@"_JCorePersistenceLastAPNS"];
        return YES;
    }
    return NO;
}
+ (void)asyncHandleReport:(id)information {
    if ([information isKindOfClass:NSDictionary.class]) {
        id state = information[@"state"];
        id handle = information[@"handle"];
        if (handle && ![handle isKindOfClass:NSNull.class])
            ((void (^)(id))handle)(state);
    }
}
+ (void)reportAPNSState:(NSUInteger)trigger {
    NSThread *caller = NSThread.currentThread;
    void (^handle)(JCommonAPNSStateEntity *) = ^(JCommonAPNSStateEntity *state) {
      if (![self isChanged:state]) {
          [JCORELog log:YES
                  level:0x3ff
                   flag:0x100
                context:0
                   file:ApnsStateFile
               function:"+[JCommonApnsStateController reportAPNSState:]_block_invoke"
                   line:149
                    tag:nil
                 format:@"apns state is not changed"];
          return;
      }
      [JCommonReportManager
          sendApnsStateReport:[self infoWithEntity:state trigger:trigger]
            completionHandler:^(id response, NSError *error) {
              [JCORELog log:YES
                      level:0x3ff
                       flag:0x100
                    context:0
                       file:ApnsStateFile
                   function:"+[JCommonApnsStateController reportAPNSState:]_block_invoke_2"
                       line:error ? 140 : 143
                        tag:nil
                     format:error ? @"apnsStateReport result error: %@" : @"apnsStateReport result success",
                            error];
            }];
    };
    [JCommonAPNSStateEntity currentState:^(JCommonAPNSStateEntity *state) {
      if ([NSThread.currentThread isEqual:caller]) {
          handle(state);
          return;
      }
      NSMutableDictionary *information = [[NSMutableDictionary alloc] init];
      if (handle)
          information[@"handle"] = handle;
      if (state)
          information[@"state"] = state;
      [self performSelector:@selector(asyncHandleReport:)
                   onThread:caller
                 withObject:[information copy]
              waitUntilDone:NO];
    }];
}
+ (NSMutableDictionary *)infoWithEntity:(JCommonAPNSStateEntity *)state trigger:(NSUInteger)trigger {
    NSMutableDictionary *content = [@{
        @"type" : @"ios_notification_state",
        @"sys_ver" : [JCOREUtilities systemVersion] ?: @"",
        @"notification" : @(state.notificationCenter),
        @"badge" : @(state.badge),
        @"sound" : @(state.sound),
        @"alert" : @(state.alert),
        @"auth_status" : @(state.authStatus),
        @"lock_screen" : @(state.lockScreen),
        @"car_play" : @(state.carPlay),
        @"critical_alert" : @(state.criticalAlert),
        @"alert_style" : @(state.alertStyle),
        @"provides_app_notification" : @(state.providesAppNotification),
        @"announcement" : @(state.announcement),
        @"itime" : @((long)[JCOREUtilities currentTimestamp]),
        @"trigger_scene" : @(trigger)
    } mutableCopy];
    id device = [JCOREUtilities deviceID:nil];
    if (device && [device length])
        [content setObject:device forKey:@"device_id"];
    return content;
}
@end
