// Partial original class: only the authorization status dependency is restored.
#import <Foundation/Foundation.h>
#import <UserNotifications/UserNotifications.h>
@interface ICMPushNotificationManager : NSObject
+ (BOOL)isPushNotificationsEnabled;
+ (void)safeGetAuthorizationStatusWithCompletion:(void (^)(UNAuthorizationStatus))completion;
@end
