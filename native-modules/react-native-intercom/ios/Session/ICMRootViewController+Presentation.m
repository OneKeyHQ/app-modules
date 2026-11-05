#import "ICMRootViewController+Presentation.h"
#import "ICMRootViewController+ModalLifecycle.h"
#import "ICMRootViewControllerDependencies.h"
#import "ICMWindow.h"
@implementation ICMRootViewController (Presentation)
- (BOOL)shouldSnapshotHostAppWindow {
    if (@available(iOS 26.0, *)) return NO;
    return [UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPhone;
}
- (void)insertSnapshotOfHostAppWindow {
    if (![self shouldSnapshotHostAppWindow]) return;
    if (self.hostAppSnapshotView) [self.hostAppSnapshotView removeFromSuperview];
    self.hostAppSnapshotView = [self.hostAppWindow snapshotViewAfterScreenUpdates:NO];
    CGRect bounds = self.view.bounds;
    self.hostAppSnapshotView.frame = bounds;
    self.intercomWindow.backgroundColor = [UIColor blackColor];
    [self.view insertSubview:self.hostAppSnapshotView atIndex:0];
}
- (void)removeSnapshotOfHostAppWindow {
    if (self.presentedViewController) return;
    if (self.hostAppSnapshotView) {
        [self.hostAppSnapshotView removeFromSuperview];
        self.hostAppSnapshotView = nil;
        self.intercomWindow.backgroundColor = [UIColor clearColor];
    }
}
- (UINavigationController *)newNavigationControllerInMessengerNavigationController:(BOOL)messenger {
    Class type = messenger ? [ICMMessengerNavigationController class] : [ICMNavigationController class];
    return [[type alloc] initWithHostAppState:self.hostAppState closeIntercomModalDelegate:self];
}
- (void)presentIntercomModal:(NSArray *)controllers useMessengerNavigationController:(BOOL)messenger {
    UINavigationController *navigation = [self newNavigationControllerInMessengerNavigationController:messenger];
    navigation.viewControllers = controllers;
    [self insertSnapshotOfHostAppWindow];
    navigation.modalPresentationStyle = UIModalPresentationPageSheet;
    navigation.presentationController.delegate = self;
    self.isPresentedViewControllerDismissibleBySwipe = YES;
    [self.delegate setWindowIsFullScreen:YES];
    [self dismissHostAppKeyboard];
    BOOL wasPresented = [self isIntercomPresented];
    if (!wasPresented) {
        [self postIntercomWindowWillShowNotification];
        [(ICMRecoveredOpenCallService *)self.openCallService performOpenCallIfNeeded];
    }
    UIViewController *top = [self topPresentedViewController:self.presentedViewController];
    [top presentViewController:navigation animated:YES completion:^{ if (!wasPresented) [self postIntercomWindowDidShowNotification]; }];
    [self animateStatusBarChange];
    [self.inAppsVC hideLauncherWithAnimation];
    [self postIntercomModalViewControllerWillShowNotification];
}
- (void)presentSheet:(id)sheet { [self presentIntercomModal:@[sheet] useMessengerNavigationController:NO]; }
- (void)presentIntercomWithViewControllers:(NSArray *)controllers {
    if (!self.tearingDown) [self presentIntercomModal:controllers useMessengerNavigationController:YES];
}
@end
