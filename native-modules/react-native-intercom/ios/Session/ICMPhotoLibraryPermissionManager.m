#import "ICMPhotoLibraryPermissionManager.h"
#import <UIKit/UIKit.h>
@implementation ICMPhotoLibraryPermissionManager
- (void)requestPermissionWithCompletionHandler:(ICMPermissionRequestCompletion)completion {
    PHAuthorizationStatus status = [PHPhotoLibrary authorizationStatusForAccessLevel:PHAccessLevelReadWrite];
    switch (status) {
        case 0: [self handleRequestWhenNotDeterminedWithCompletionHandler:completion]; break;
        case 2: [self handleRequestWhenDeniedWithCompletionHandler:completion]; break;
        case 3: case 4: [self handleRequestWhenAuthorizedWithCompletionHandler:completion]; break;
        default: completion([ICMPhotoLibraryPermissionManager convertPHAuthorizationStatusToICMPermissionStatus:status], 2); break;
    }
}
- (void)handleRequestWhenNotDeterminedWithCompletionHandler:(ICMPermissionRequestCompletion)completion {
    if ([self isPermissionInPList]) [PHPhotoLibrary requestAuthorizationForAccessLevel:PHAccessLevelReadWrite handler:^(PHAuthorizationStatus status) {
        NSInteger converted = [ICMPhotoLibraryPermissionManager convertPHAuthorizationStatusToICMPermissionStatus:status];
        completion(converted == 1 ? 1 : 2, 1);
    }];
    else completion([ICMPhotoLibraryPermissionManager convertPHAuthorizationStatusToICMPermissionStatus:0], 2);
}
- (void)handleRequestWhenDeniedWithCompletionHandler:(ICMPermissionRequestCompletion)completion {
    dispatch_async(dispatch_get_main_queue(), ^{
        [[NSNotificationCenter defaultCenter] postNotificationName:@"kICMPermissionSettingsWillOpen" object:nil];
        UIApplication *application = [UIApplication sharedApplication];
        NSURL *url = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
        [application openURL:url options:@{} completionHandler:nil];
        completion(2, 0);
    });
}
- (void)handleRequestWhenAuthorizedWithCompletionHandler:(ICMPermissionRequestCompletion)completion { completion(1, 2); }
- (void)permissionStatusWithCompletionHandler:(ICMPermissionStatusCompletion)completion { completion([ICMPhotoLibraryPermissionManager convertPHAuthorizationStatusToICMPermissionStatus:[PHPhotoLibrary authorizationStatusForAccessLevel:PHAccessLevelReadWrite]]); }
- (void)subscribeToChangesWithBlock:(ICMPermissionStatusCompletion)block {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appDidBecomeActive) name:UIApplicationDidBecomeActiveNotification object:nil];
    self.block = block;
}
- (void)appDidBecomeActive { [self permissionStatusWithCompletionHandler:^(NSInteger status) { self.block(status); }]; }
- (BOOL)isPermissionInPList { return [[NSBundle mainBundle] objectForInfoDictionaryKey:@"NSPhotoLibraryUsageDescription"] != nil; }
+ (NSInteger)convertPHAuthorizationStatusToICMPermissionStatus:(PHAuthorizationStatus)status {
    static const NSInteger values[] = {3,2,2};
    return (NSUInteger)status <= 2 ? values[(NSUInteger)status] : 1;
}
@end
