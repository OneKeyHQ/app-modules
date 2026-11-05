#import "ICMPermissionHelper.h"
#import "ICMPushPermissionManager.h"
#import "ICMLocationPermissionManager.h"
#import "ICMAVCapturePermissionManager.h"
#import "ICMPhotoLibraryPermissionManager.h"
@implementation ICMPermissionHelper
+ (id<ICMPermissionManager>)permissionManagerForPermissionType:(NSInteger)type {
    switch (type) {
        case 1: return [ICMPushPermissionManager new];
        case 2: return [ICMLocationPermissionManager new];
        case 3: return [[ICMAVCapturePermissionManager alloc] initWithCaptureType:0];
        case 4: return [[ICMAVCapturePermissionManager alloc] initWithCaptureType:1];
        case 5: return [ICMPhotoLibraryPermissionManager new];
        default: return nil;
    }
}
@end
