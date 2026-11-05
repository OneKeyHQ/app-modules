#pragma once
#import "ICMPermissionManager.h"
#import <UserNotifications/UserNotifications.h>
@interface ICMPushPermissionManager : NSObject <ICMPermissionManager>
@property (nonatomic, copy) ICMPermissionStatusCompletion block;
- (void)appDidBecomeActive;
+ (NSInteger)convertUNAuthorizationStatusToICMPermissionStatus:(UNAuthorizationStatus)status;
@end
