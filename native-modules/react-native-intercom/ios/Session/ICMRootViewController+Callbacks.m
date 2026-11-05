#import "ICMRootViewController+Callbacks.h"
#import "ICMRootViewController+Presentation.h"
#import "ICMRootViewController+ModalLifecycle.h"
#import "ICMRootViewControllerDependencies.h"
#import "ICMRootDestinationDependencies.h"
#import "ICMDataManagerDependencies.h"
#import "ICMHTTPBridge.h"
@implementation ICMRootViewController (Callbacks)
- (BOOL)presentationControllerShouldDismiss:(__unused UIPresentationController *)controller { return self.isPresentedViewControllerDismissibleBySwipe; }
- (void)presentationControllerDidAttemptToDismiss:(__unused UIPresentationController *)controller {
    if (self.didAttemptDismissBlock) self.didAttemptDismissBlock();
}
- (void)presentationControllerWillDismiss:(UIPresentationController *)controller {
    if ([controller.presentedViewController isKindOfClass:[ICMNavigationController class]]) {
        [self insertSnapshotOfHostAppWindow];
        if ([controller.presentingViewController isKindOfClass:[ICMRootViewController class]]) [self postIntercomWindowWillHideNotification];
    }
}
- (void)presentationControllerDidDismiss:(UIPresentationController *)controller {
    [self postIntercomModalViewControllerWillHideNotification];
    if ([controller.presentedViewController isKindOfClass:[ICMNavigationController class]]) {
        UINavigationController *navigation = (UINavigationController *)controller.presentedViewController;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"IntercomRootViewControllerDidDismissPresentedController" object:nil];
        [self handleIntercomWindowDismissal:navigation];
    }
}
- (void)launcherTapped { [ICMRecoveredHomeMetricService addLauncherClickedMetric]; [self presentHome]; }
- (void)didShowFullScreenInApp { [self.delegate setWindowIsFullScreen:YES]; }
- (void)didHideFullScreenInApp { if (![self isPresentingVC]) [self.delegate setWindowIsFullScreen:NO]; }
- (void)dismissibleBySwipe:(BOOL)dismissible didAttemptDismiss:(void (^)(void))attempt {
    self.isPresentedViewControllerDismissibleBySwipe = dismissible;
    self.didAttemptDismissBlock = attempt;
}
- (void)didSelectErrorAction { [self closeIntercomModal]; }
@end
