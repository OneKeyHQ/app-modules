#import "ICMBridge.h"
#import <UserNotifications/UserNotifications.h>
@interface ICMBridge (OriginalPush)
+ (void)registerDeviceToken:(NSData *)token failure:(void (^)(NSError *))failure;
+ (void)registerDeviceToken:(NSData *)token success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (void)submitDeviceTokenWithFreshPushStatus:(NSData *)token success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (BOOL)isIntercomPushNotification:(NSDictionary *)notification;
+ (void)handleIntercomPushNotification:(NSDictionary *)notification;
+ (void)handleIntercomRichPushNotificationContent:(UNMutableNotificationContent *)content withContentHandler:(void (^)(UNNotificationContent *))handler;
+ (void)deleteDeviceTokenIfPushNotificationsDisabled;
@end
