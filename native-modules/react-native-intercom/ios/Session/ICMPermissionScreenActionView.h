#pragma once
#import "ICMScreenActionView.h"
#import "ICMPermissionManager.h"
#import "ICMPermissionScreenAction.h"
@interface ICMPermissionScreenActionView : ICMScreenActionView
@property (nonatomic, strong) id<ICMPermissionManager> permissionManager;
@property (nonatomic, strong) ICMPermissionScreenAction *permissionScreenAction;
@property (nonatomic, strong) ICMScreenAction *skipScreenAction;
@property (nonatomic, strong) ICMScreenActionButton *permissionScreenActionButton;
@property (nonatomic, strong) ICMScreenActionButton *skipScreenActionButton;
@property (nonatomic) BOOL sentUserToSettings;
@property (nonatomic) NSInteger currentPermissionStatus;
- (void)setUpPermissionManager;
- (void)subscribeToPermissionManagerChanges;
- (void)configureScreenActionForPermissionStatus:(NSInteger)status;
- (void)configureForAuthorizedStatus;
- (void)configureForDeniedStatus;
- (void)configureForUnsupportedStatus;
- (void)configureForNotDeterminedStatus;
- (void)configureSkipButtonAsAuthorized;
- (void)configureSkipButtonNormally;
- (void)hidePermissionButton;
- (void)showPermissionButton;
- (void)hideSkipButton;
- (void)showSkipButton;
- (void)scaleViewDown:(UIView *)view;
- (void)rotateAndScaleViewToOriginalSize:(UIView *)view originalTransform:(CGAffineTransform)transform;
- (void)permissionButtonTapped;
- (void)goToNextScreenAfterDelay;
- (void)handleMetricsAfterPermissionRequestForStatus:(NSInteger)status requestType:(NSInteger)type;
- (void)handleMetricsAfterPermissionManagerChangesForStatus:(NSInteger)status;
- (void)reportStatsForPermissionStatus:(NSInteger)status;
@end
