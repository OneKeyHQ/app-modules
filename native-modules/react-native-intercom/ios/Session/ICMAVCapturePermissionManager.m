#import "ICMAVCapturePermissionManager.h"
#import <UIKit/UIKit.h>
@implementation ICMAVCapturePermissionManager
- (instancetype)initWithCaptureType:(NSInteger)type {
    self = [super init];
    if (self) { self.captureType = type; self.avMediaType = [self avMediaTypeFromICMAVCaptureType:type]; }
    return self;
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
- (NSString *)avMediaTypeFromICMAVCaptureType:(NSInteger)type { return type == 0 ? AVMediaTypeAudio : AVMediaTypeVideo; }
- (void)requestPermissionWithCompletionHandler:(ICMPermissionRequestCompletion)completion {
    AVAuthorizationStatus status = [AVCaptureDevice authorizationStatusForMediaType:self.avMediaType];
    switch (status) {
        case 0: [self handleRequestWhenNotDeterminedWithCompletionHandler:completion]; break;
        case 2: [self handleRequestWhenDeniedWithCompletionHandler:completion]; break;
        case 3: [self handleRequestWhenAuthorizedWithCompletionHandler:completion]; break;
        default: completion([ICMAVCapturePermissionManager convertAVAuthorizationStatusToICMPermissionStatus:status], 2); break;
    }
}
- (void)handleRequestWhenNotDeterminedWithCompletionHandler:(ICMPermissionRequestCompletion)completion {
    if ([self isPermissionInPList]) [AVCaptureDevice requestAccessForMediaType:self.avMediaType completionHandler:^(BOOL granted) { completion(granted ? 1 : 2, 1); }];
    else completion([ICMAVCapturePermissionManager convertAVAuthorizationStatusToICMPermissionStatus:0], 2);
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
- (void)permissionStatusWithCompletionHandler:(ICMPermissionStatusCompletion)completion { completion([ICMAVCapturePermissionManager convertAVAuthorizationStatusToICMPermissionStatus:[AVCaptureDevice authorizationStatusForMediaType:self.avMediaType]]); }
- (void)subscribeToChangesWithBlock:(ICMPermissionStatusCompletion)block {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(appDidBecomeActive) name:UIApplicationDidBecomeActiveNotification object:nil];
    self.block = block;
}
- (void)appDidBecomeActive { [self permissionStatusWithCompletionHandler:^(NSInteger status) { self.block(status); }]; }
- (BOOL)isPermissionInPList { return [[NSBundle mainBundle] objectForInfoDictionaryKey:self.captureType == 0 ? @"NSMicrophoneUsageDescription" : @"NSCameraUsageDescription"] != nil; }
+ (NSInteger)convertAVAuthorizationStatusToICMPermissionStatus:(AVAuthorizationStatus)status {
    static const NSInteger values[] = {3,2,2};
    return (NSUInteger)status <= 2 ? values[(NSUInteger)status] : 1;
}
@end
