#import "ICMPermissionScreenActionView.h"
#import "ICMPermissionHelper.h"
#import "ICMCarouselMetricService.h"
#import "ICMAssets.h"
@implementation ICMPermissionScreenActionView
- (instancetype)initWithScreenActions:(NSArray<ICMScreenAction *> *)actions screenDelegate:(id<ICMScreenDelegate>)delegate {
    self = [super init];
    if (self) {
        _currentPermissionStatus = 0;
        self.screenDelegate = delegate;
        self.screenActions = actions;
        [self setUpPermissionManager];
        [self subscribeToPermissionManagerChanges];
        [self configureView];
    }
    return self;
}
- (void)setUpPermissionManager {
    for (ICMScreenAction *action in self.screenActions) {
        if ([action isKindOfClass:ICMPermissionScreenAction.class]) self.permissionManager = [ICMPermissionHelper permissionManagerForPermissionType:((ICMPermissionScreenAction *)action).permissionType];
    }
}
- (void)setUpScreenActionButtons {
    for (ICMScreenAction *action in self.screenActions) {
        if (action.type == 3) {
            self.skipScreenActionButton = [self addButtonForScreenAction:action];
            self.skipScreenAction = action;
        } else if ([action isMemberOfClass:ICMPermissionScreenAction.class]) {
            self.permissionScreenActionButton = [self addButtonForScreenAction:action];
            self.permissionScreenAction = (ICMPermissionScreenAction *)action;
        }
    }
    [self.permissionManager permissionStatusWithCompletionHandler:^(NSInteger status) {
        dispatch_async(dispatch_get_main_queue(), ^{ [self configureScreenActionForPermissionStatus:status]; });
    }];
}
- (void)subscribeToPermissionManagerChanges {
    __weak ICMPermissionScreenActionView *weakSelf = self;
    [self.permissionManager subscribeToChangesWithBlock:^(NSInteger status) {
        dispatch_async(dispatch_get_main_queue(), ^{
            if (weakSelf.sentUserToSettings) {
                [weakSelf handleMetricsAfterPermissionManagerChangesForStatus:status];
                [weakSelf reportStatsForPermissionStatus:status];
                [weakSelf goToNextScreenAfterDelay];
            }
            [weakSelf configureScreenActionForPermissionStatus:status];
        });
    }];
}
- (void)configureScreenActionForPermissionStatus:(NSInteger)status {
    if (self.currentPermissionStatus == status) return;
    switch (status) {
        case 1: [self configureForAuthorizedStatus]; break;
        case 2: [self configureForDeniedStatus]; break;
        case 3: [self configureForNotDeterminedStatus]; break;
        case 4: [self configureForUnsupportedStatus]; break;
        default: break;
    }
    self.currentPermissionStatus = status;
}
- (void)configureForAuthorizedStatus {
    [self hidePermissionButton]; [self showSkipButton]; [self configureSkipButtonAsAuthorized];
    if ([self.screenDelegate shouldAutoProgressScreen]) [self goToNextScreenAfterDelay];
}
- (void)configureForDeniedStatus { [self showPermissionButton]; [self showSkipButton]; [self configureSkipButtonNormally]; }
- (void)configureForUnsupportedStatus { [self hidePermissionButton]; [self showSkipButton]; [self configureSkipButtonNormally]; }
- (void)configureForNotDeterminedStatus { [self showPermissionButton]; [self hideSkipButton]; [self configureSkipButtonNormally]; }
- (void)hidePermissionButton { [self.stackView removeArrangedSubview:self.permissionScreenActionButton]; [self.permissionScreenActionButton removeFromSuperview]; }
- (void)showPermissionButton { [self.stackView insertArrangedSubview:self.permissionScreenActionButton atIndex:1]; }
- (void)hideSkipButton { [self.stackView removeArrangedSubview:self.skipScreenActionButton]; [self.skipScreenActionButton removeFromSuperview]; }
- (void)showSkipButton { [self.stackView insertArrangedSubview:self.skipScreenActionButton atIndex:0]; }
- (void)configureSkipButtonAsAuthorized {
    [self.skipScreenActionButton setTitle:@"" forState:UIControlStateNormal];
    UIImageView *imageView = self.skipScreenActionButton.imageView;
    imageView.contentMode = UIViewContentModeCenter;
    CGAffineTransform transform = imageView.transform;
    [self scaleViewDown:imageView];
    [self.skipScreenActionButton setImage:[ICMAssets screenActionCheckmark] forState:UIControlStateNormal];
    [self rotateAndScaleViewToOriginalSize:imageView originalTransform:transform];
    self.skipScreenActionButton.accessibilityLabel = @"Permission Authorised";
}
- (void)configureSkipButtonNormally {
    [self.skipScreenActionButton setTitle:self.skipScreenAction.title forState:UIControlStateNormal];
    [self.skipScreenActionButton setImage:nil forState:UIControlStateNormal];
}
- (void)scaleViewDown:(UIView *)view { view.transform = CGAffineTransformScale(view.transform, 0.0001, 0.0001); }
- (void)rotateAndScaleViewToOriginalSize:(UIView *)view originalTransform:(CGAffineTransform)transform {
    [UIView animateWithDuration:0.4 animations:^{
        view.transform = CGAffineTransformScale(transform, 1, 1);
        view.transform = CGAffineTransformRotate(view.transform, M_PI);
        view.transform = CGAffineTransformRotate(view.transform, M_PI);
    }];
}
- (void)carouselButtonTapped:(ICMScreenActionButton *)sender {
    if ([sender.screenAction isKindOfClass:ICMPermissionScreenAction.class]) [self permissionButtonTapped];
    else if (sender.screenAction.type == 3) {
        [super carouselButtonTapped:sender];
        [[ICMCarouselMetricService sharedInstance] addEventForPermissionRequestSkipped:self.permissionScreenAction.permissionType];
    } else [super carouselButtonTapped:sender];
}
- (void)permissionButtonTapped {
    [self.permissionManager requestPermissionWithCompletionHandler:^(NSInteger status, NSInteger requestType) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self configureScreenActionForPermissionStatus:status];
            if (requestType != 0) [self goToNextScreenAfterDelay];
            else self.sentUserToSettings = YES;
            [self handleMetricsAfterPermissionRequestForStatus:status requestType:requestType];
            [self reportStatsForPermissionStatus:status];
        });
    }];
}
- (void)goToNextScreenAfterDelay {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 600000000), dispatch_get_main_queue(), ^{ [self.screenDelegate goToNextScreenFromContext:1]; });
}
- (void)handleMetricsAfterPermissionRequestForStatus:(NSInteger)status requestType:(NSInteger)type {
    if (type == 1) [[ICMCarouselMetricService sharedInstance] addEventForPermissionRequestViaSystemDialog:self.permissionScreenAction.permissionType approved:status == 1];
}
- (void)handleMetricsAfterPermissionManagerChangesForStatus:(NSInteger)status {
    [[ICMCarouselMetricService sharedInstance] addEventForPermissionRequestViaSettings:self.permissionScreenAction.permissionType approved:status == 1];
    self.sentUserToSettings = NO;
}
- (void)reportStatsForPermissionStatus:(NSInteger)status {
    if (status == 1) [self.screenDelegate permissionWasGrantedForScreen:self.permissionScreenAction.screenId];
}
@end
