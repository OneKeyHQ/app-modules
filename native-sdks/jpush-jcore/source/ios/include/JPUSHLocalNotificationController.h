#pragma once
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
@class JPushNotificationRequest, JPushNotificationIdentifier;
#import <CoreLocation/CoreLocation.h>
#import <UIKit/UIKit.h>
@interface JPUSHLocalNotificationController : NSObject
+ (UILocalNotification *)setLocalNotification:(NSDate *)fireDate
                                    alertBody:(NSString *)body
                                        badge:(int)badge
                                  alertAction:(NSString *)action
                                identifierKey:(NSString *)key
                                     userInfo:(NSDictionary *)info
                                    soundName:(NSString *)sound
                                       region:(CLRegion *)region
                           regionTriggersOnce:(BOOL)once
                                     category:(NSString *)category;
+ (void)addNotificationToCacheWithNotification:(UILocalNotification *)notification andKey:(NSString *)key;
+ (void)showLocalNotificationAtFront:(UILocalNotification *)notification identifierKey:(NSString *)key;
+ (NSArray *)findLocalNotificationWithIdentifier:(NSString *)key;
+ (NSArray *)findAllLocalNotifications;
+ (void)deleteLocalNotificationWithIdentifierKey:(NSString *)key;
+ (void)deleteLocalNotification:(UILocalNotification *)notification;
+ (void)clearAllLocalNotifications;
+ (void)p_deleteCacheObjectWithKey:(NSString *)key UsingBlock:(void (^)(UILocalNotification *, BOOL *))block;
+ (void)addNotification:(JPushNotificationRequest *)request;
+ (void)removeNotification:(JPushNotificationIdentifier *)identifier;
+ (void)findNotification:(JPushNotificationIdentifier *)identifier;
+ (void)addNotification_ios8:(JPushNotificationRequest *)request;
+ (void)addNotification_ios10:(JPushNotificationRequest *)request;
+ (void)removeNotification_ios10:(JPushNotificationIdentifier *)identifier;
+ (void)findNotification_ios10:(JPushNotificationIdentifier *)identifier;
+ (void)invokeMethod:(id)target name:(NSString *)name param:(id)param result:(void *)result;
+ (void)invokeMethod:(id)target name:(NSString *)name param:(id)param param2:(id)param2 result:(void *)result;
+ (void)invokeMethod:(id)target
                name:(NSString *)name
               param:(id)param
           boolParam:(BOOL)value
              result:(void *)result;
+ (void)invokeMethod:(id)target
                name:(NSString *)name
         doubleParam:(double)value
           boolParam:(BOOL)repeat
              result:(void *)result;
+ (void)invokeMethod:(id)target name:(NSString *)name uIntegerParam:(NSUInteger)value result:(void *)result;
+ (void)invokeMethod:(id)target
                name:(NSString *)name
               param:(id)param
          floatParam:(float)value
              result:(void *)result;
+ (void)invokeMethod:(id)target name:(NSString *)name floatParam:(float)value result:(void *)result;
+ (void)invokeMethod:(id)target
                name:(NSString *)name
               param:(id)param
              param2:(id)param2
              param3:(id)param3
              result:(void *)result;
@end

#pragma clang diagnostic pop
