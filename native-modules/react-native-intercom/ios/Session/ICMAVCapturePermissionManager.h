#pragma once
#import "ICMPermissionManager.h"
#import <AVFoundation/AVFoundation.h>
@interface ICMAVCapturePermissionManager : NSObject <ICMPermissionManager>
@property (nonatomic) NSInteger captureType;
@property (nonatomic, strong) NSString *avMediaType;
@property (nonatomic, copy) ICMPermissionStatusCompletion block;
- (instancetype)initWithCaptureType:(NSInteger)type;
- (void)handleRequestWhenNotDeterminedWithCompletionHandler:(ICMPermissionRequestCompletion)completion;
- (void)handleRequestWhenDeniedWithCompletionHandler:(ICMPermissionRequestCompletion)completion;
- (void)handleRequestWhenAuthorizedWithCompletionHandler:(ICMPermissionRequestCompletion)completion;
- (NSString *)avMediaTypeFromICMAVCaptureType:(NSInteger)type;
- (void)appDidBecomeActive;
+ (NSInteger)convertAVAuthorizationStatusToICMPermissionStatus:(AVAuthorizationStatus)status;
@end
