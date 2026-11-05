#pragma once
#import "ICMPermissionManager.h"
#import <CoreLocation/CoreLocation.h>
@interface ICMLocationPermissionManager : NSObject <ICMPermissionManager, CLLocationManagerDelegate>
@property (nonatomic) BOOL requestingPermission;
@property (nonatomic, copy) ICMPermissionStatusCompletion block;
@property (nonatomic, strong) CLLocationManager *locationManager;
@property (nonatomic, copy) ICMPermissionRequestCompletion permissionRequestBlock;
- (void)handleRequestWhenNotDeterminedWithCompletionHandler:(ICMPermissionRequestCompletion)completion;
- (void)handleRequestWhenDeniedWithCompletionHandler:(ICMPermissionRequestCompletion)completion;
- (void)handleRequestWhenAuthorizedForInUseWithCompletionHandler:(ICMPermissionRequestCompletion)completion;
- (void)handleRequestWhenAuthorizedForAlwaysWithCompletionHandler:(ICMPermissionRequestCompletion)completion;
- (void)didChangeAuthorizationStatus:(CLAuthorizationStatus)status;
- (CLAuthorizationStatus)authorizationStatus;
- (void)requestWhenInUseLocationAuthorization;
- (void)requestAlwaysLocationAuthorization;
+ (BOOL)hasRequestedAlwaysPermission;
+ (void)setHasRequestedAlwaysPermission:(BOOL)requested;
+ (NSInteger)convertCLLocationAuthorizationStatusToICMPermissionStatus:(CLAuthorizationStatus)status;
+ (BOOL)pListEntryForAlwaysLocationUsage;
+ (BOOL)pListEntryForWhenInUseLocationUsage;
@end
