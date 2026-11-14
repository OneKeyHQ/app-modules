#pragma once
#import "ICMPermissionManager.h"
#import <Photos/Photos.h>
@interface ICMPhotoLibraryPermissionManager : NSObject <ICMPermissionManager>
@property (nonatomic, copy) ICMPermissionStatusCompletion block;
- (void)handleRequestWhenNotDeterminedWithCompletionHandler:(ICMPermissionRequestCompletion)completion;
- (void)handleRequestWhenDeniedWithCompletionHandler:(ICMPermissionRequestCompletion)completion;
- (void)handleRequestWhenAuthorizedWithCompletionHandler:(ICMPermissionRequestCompletion)completion;
- (void)appDidBecomeActive;
+ (NSInteger)convertPHAuthorizationStatusToICMPermissionStatus:(PHAuthorizationStatus)status;
@end
