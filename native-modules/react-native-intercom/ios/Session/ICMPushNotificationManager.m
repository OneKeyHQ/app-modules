#import "ICMPushNotificationManager.h"
#import "ICMPushNotificationStatus.h"
@implementation ICMPushNotificationManager
+ (BOOL)isPushNotificationsEnabled { return ICMRecoveredPushNotificationStatus.shared.isEnabled; }
+ (void)safeGetAuthorizationStatusWithCompletion:(void (^)(UNAuthorizationStatus))completion {
    if (NSClassFromString(@"XCTestCase")) {
        completion(UNAuthorizationStatusNotDetermined);
        return;
    }
    [[UNUserNotificationCenter currentNotificationCenter] getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings *settings) {
        completion(settings.authorizationStatus);
    }];
}
@end
