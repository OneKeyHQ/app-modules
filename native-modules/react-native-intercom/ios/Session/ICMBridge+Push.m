#import "ICMBridge+Push.h"
#import "ICMBridgeDependencies.h"
#import "ICMPushNotificationStatus.h"
#import "ICMIdentityStore.h"
#import "../Identity/ICMDeviceTokenData.h"
@interface ICMRecoveredErrorProcessor (OriginalPushErrors)
+ (NSError *)generatePushNotificationsNotEnabledError;
@end
@interface ICMHTTPBridge (OriginalPushRequests)
+ (void)registerDeviceToken:(NSData *)token success:(void (^)(void))success failure:(void (^)(NSError *))failure;
@end
@interface ICMPushNotificationManager (OriginalPushHandling)
+ (BOOL)isIntercomPushNotification:(NSDictionary *)notification;
+ (void)handleIntercomPushNotification:(NSDictionary *)notification;
+ (void)handleIntercomRichPushNotificationContent:(UNMutableNotificationContent *)content withContentHandler:(void (^)(UNNotificationContent *))handler;
@end
@implementation ICMBridge (OriginalPush)
+ (void)registerDeviceToken:(NSData *)token failure:(void (^)(NSError *))failure {
    if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Registering device token for push notifications");
    [self submitDeviceTokenWithFreshPushStatus:token success:nil failure:failure];
}
+ (void)registerDeviceToken:(NSData *)token success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Registering device token for push notifications with completion callbacks");
    [self submitDeviceTokenWithFreshPushStatus:token success:success failure:failure];
}
+ (void)submitDeviceTokenWithFreshPushStatus:(NSData *)token success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    [[ICMRecoveredPushNotificationStatus shared] refreshWithCompletion:^{
        void (^submit)(void) = ^{
            if ([ICMPushNotificationManager isPushNotificationsEnabled]) {
                [ICMHTTPBridge registerDeviceToken:token success:success failure:failure];
                if (![ICMUtils swizzlingDisabled]) [ICMPushNotificationManager swizzleReceiveRemoteNotifications];
            } else if (failure) {
                failure([ICMRecoveredErrorProcessor generatePushNotificationsNotEnabledError]);
            }
        };
        if ([NSThread isMainThread]) submit();
        else dispatch_async(dispatch_get_main_queue(), submit);
    }];
}
+ (BOOL)isIntercomPushNotification:(NSDictionary *)notification { return [ICMPushNotificationManager isIntercomPushNotification:notification]; }
+ (void)handleIntercomPushNotification:(NSDictionary *)notification { [ICMPushNotificationManager handleIntercomPushNotification:notification]; }
+ (void)handleIntercomRichPushNotificationContent:(UNMutableNotificationContent *)content withContentHandler:(void (^)(UNNotificationContent *))handler {
    [ICMPushNotificationManager handleIntercomRichPushNotificationContent:content withContentHandler:handler];
}
+ (void)deleteDeviceTokenIfPushNotificationsDisabled {
    [[ICMRecoveredPushNotificationStatus shared] refreshWithCompletion:^{
        ICMIdentityStore *store = [ICMIdentityStore currentIdentity];
        if (![ICMPushNotificationManager isPushNotificationsEnabled] && store.deviceTokenData.deviceTokenSubmitted) {
            if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Deleting device token for push notifications");
            [ICMHTTPBridge deleteDeviceToken:store.deviceTokenData.deviceToken forUserIdentity:store.userIdentity completion:nil];
        }
    }];
}
@end
