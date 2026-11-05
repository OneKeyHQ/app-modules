#import "ICMLocationPermissionManager.h"
#import <UIKit/UIKit.h>
static BOOL ICMRequestedAlwaysPermission;
@implementation ICMLocationPermissionManager
- (instancetype)init {
    self = [super init];
    if (self) { self.locationManager = [CLLocationManager new]; self.locationManager.delegate = self; }
    return self;
}
- (void)requestPermissionWithCompletionHandler:(ICMPermissionRequestCompletion)completion {
    CLAuthorizationStatus status = [self authorizationStatus];
    switch (status) {
        case 0: [self handleRequestWhenNotDeterminedWithCompletionHandler:completion]; break;
        case 2: [self handleRequestWhenDeniedWithCompletionHandler:completion]; break;
        case 3: [self handleRequestWhenAuthorizedForAlwaysWithCompletionHandler:completion]; break;
        case 4: [self handleRequestWhenAuthorizedForInUseWithCompletionHandler:completion]; break;
        default: completion([ICMLocationPermissionManager convertCLLocationAuthorizationStatusToICMPermissionStatus:status], 2); break;
    }
}
- (void)handleRequestWhenNotDeterminedWithCompletionHandler:(ICMPermissionRequestCompletion)completion {
    if (![ICMLocationPermissionManager pListEntryForWhenInUseLocationUsage]) { completion([ICMLocationPermissionManager convertCLLocationAuthorizationStatusToICMPermissionStatus:0], 2); return; }
    self.requestingPermission = YES;
    self.permissionRequestBlock = completion;
    if ([ICMLocationPermissionManager pListEntryForAlwaysLocationUsage]) [self requestAlwaysLocationAuthorization];
    else [self requestWhenInUseLocationAuthorization];
}
- (void)handleRequestWhenDeniedWithCompletionHandler:(ICMPermissionRequestCompletion)completion {
    dispatch_async(dispatch_get_main_queue(), ^{
        UIApplication *application = [UIApplication sharedApplication];
        NSURL *url = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
        [application openURL:url options:@{} completionHandler:nil];
        completion(2, 0);
    });
}
- (void)handleRequestWhenAuthorizedForInUseWithCompletionHandler:(ICMPermissionRequestCompletion)completion {
    if (![ICMLocationPermissionManager pListEntryForAlwaysLocationUsage]) { completion(1, 2); return; }
    if ([ICMLocationPermissionManager hasRequestedAlwaysPermission]) { completion(1, 2); return; }
    self.requestingPermission = YES;
    self.permissionRequestBlock = completion;
    [self requestAlwaysLocationAuthorization];
}
- (void)handleRequestWhenAuthorizedForAlwaysWithCompletionHandler:(ICMPermissionRequestCompletion)completion { completion(1, 2); }
- (void)permissionStatusWithCompletionHandler:(ICMPermissionStatusCompletion)completion { completion([ICMLocationPermissionManager convertCLLocationAuthorizationStatusToICMPermissionStatus:[self authorizationStatus]]); }
- (void)subscribeToChangesWithBlock:(ICMPermissionStatusCompletion)block { self.block = block; }
- (void)locationManagerDidChangeAuthorization:(CLLocationManager *)manager { [self didChangeAuthorizationStatus:manager.authorizationStatus]; }
- (void)didChangeAuthorizationStatus:(CLAuthorizationStatus)status {
    NSInteger converted = [ICMLocationPermissionManager convertCLLocationAuthorizationStatusToICMPermissionStatus:status];
    if (self.requestingPermission) {
        self.requestingPermission = NO;
        if (!self.permissionRequestBlock) return;
        self.permissionRequestBlock(converted, 1);
        self.permissionRequestBlock = nil;
    } else {
        if (!self.block) return;
        self.block(converted);
    }
}
// These three selectors and the scalar bridge are fixed by the original implementation.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
- (CLAuthorizationStatus)authorizationStatus {
    SEL selector = NSSelectorFromString(@"authorizationStatus");
    if ([self.locationManager respondsToSelector:selector]) return (CLAuthorizationStatus)(int32_t)(uintptr_t)(__bridge void *)[self.locationManager performSelector:selector];
    return 1;
}
- (void)requestWhenInUseLocationAuthorization {
    SEL selector = NSSelectorFromString(@"requestWhenInUseAuthorization");
    if ([self.locationManager respondsToSelector:selector]) [self.locationManager performSelector:selector];
}
- (void)requestAlwaysLocationAuthorization {
    SEL selector = NSSelectorFromString(@"requestAlwaysAuthorization");
    if ([self.locationManager respondsToSelector:selector]) [self.locationManager performSelector:selector];
    [ICMLocationPermissionManager setHasRequestedAlwaysPermission:YES];
}
#pragma clang diagnostic pop
+ (BOOL)hasRequestedAlwaysPermission { return ICMRequestedAlwaysPermission; }
+ (void)setHasRequestedAlwaysPermission:(BOOL)requested { ICMRequestedAlwaysPermission = requested; }
+ (NSInteger)convertCLLocationAuthorizationStatusToICMPermissionStatus:(CLAuthorizationStatus)status {
    switch (status) {
        case 1: case 2: return 2;
        case 3: return 1;
        case 4:
            if (![ICMLocationPermissionManager pListEntryForAlwaysLocationUsage]) return 1;
            return [ICMLocationPermissionManager hasRequestedAlwaysPermission] ? 1 : 3;
        default: return 3;
    }
}
+ (BOOL)pListEntryForAlwaysLocationUsage { return [[NSBundle mainBundle] objectForInfoDictionaryKey:@"NSLocationAlwaysAndWhenInUseUsageDescription"] != nil; }
+ (BOOL)pListEntryForWhenInUseLocationUsage { return [[NSBundle mainBundle] objectForInfoDictionaryKey:@"NSLocationWhenInUseUsageDescription"] != nil; }
- (BOOL)isPermissionInPList {
    if ([ICMLocationPermissionManager pListEntryForAlwaysLocationUsage]) return YES;
    return [ICMLocationPermissionManager pListEntryForWhenInUseLocationUsage];
}
@end
