#import "ICMPushPermissionManager.h"
#import <UIKit/UIKit.h>
@implementation ICMPushPermissionManager
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
- (void)permissionStatusWithCompletionHandler:(ICMPermissionStatusCompletion)completion {
    [[UNUserNotificationCenter currentNotificationCenter] getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings *settings) {
        completion([ICMPushPermissionManager convertUNAuthorizationStatusToICMPermissionStatus:settings.authorizationStatus]);
    }];
}
- (void)requestPermissionWithCompletionHandler:(ICMPermissionRequestCompletion)completion {
    [self permissionStatusWithCompletionHandler:^(NSInteger status) {
        if (status == 3) {
            [[UNUserNotificationCenter currentNotificationCenter] requestAuthorizationWithOptions:7 completionHandler:^(BOOL granted, NSError *error) {
                (void)error;
                if (granted) {
                    dispatch_async(dispatch_get_main_queue(), ^{ [[UIApplication sharedApplication] registerForRemoteNotifications]; });
                    completion(1, 1);
                } else completion(2, 1);
            }];
        } else if (status == 2) {
            dispatch_async(dispatch_get_main_queue(), ^{
                UIApplication *application = [UIApplication sharedApplication];
                NSURL *url = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
                [application openURL:url options:@{} completionHandler:nil];
                completion(2, 0);
            });
        } else completion(status, 2);
    }];
}
- (void)subscribeToChangesWithBlock:(ICMPermissionStatusCompletion)block {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appDidBecomeActive) name:UIApplicationDidBecomeActiveNotification object:nil];
    self.block = block;
}
- (void)appDidBecomeActive { [self permissionStatusWithCompletionHandler:^(NSInteger status) { self.block(status); }]; }
- (BOOL)isPermissionInPList { return YES; }
+ (NSInteger)convertUNAuthorizationStatusToICMPermissionStatus:(UNAuthorizationStatus)status {
    static const NSInteger values[] = {2,1,1,1};
    NSUInteger index = (NSUInteger)status - 1;
    return index <= 3 ? values[index] : 3;
}
@end
