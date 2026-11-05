#import "ICMRootViewController+ModalLifecycle.h"
#import "ICMRootViewControllerDependencies.h"
@implementation ICMRootViewController (ModalLifecycle)
- (UIViewController *)topPresentedViewController:(UIViewController *)controller {
    if (!controller) return self;
    if (controller.presentedViewController) return [self topPresentedViewController:controller.presentedViewController];
    return controller;
}
- (void)dismissAllIntercomViews {
    [self closeIntercomModalWithCompletion:^{
        if ([self isPresentingVC]) dispatch_async(dispatch_get_main_queue(), ^{
            if ([self isPresentingVC]) [self dismissAllIntercomViews];
        });
    }];
}
- (void)dismissAllButBottomViewController {
    if (self.presentedViewController.presentedViewController) {
        [self closeIntercomModalWithCompletion:^{
            if (self.presentedViewController) {
                NSArray *behind = [self presentedViewControllersBehind:[self topPresentedViewController:self.presentedViewController]];
                for (UIViewController *controller in behind) if ([controller.presentingViewController isKindOfClass:[ICMRootViewController class]]) [controller dismissViewControllerAnimated:YES completion:nil];
            }
        }];
    }
}
- (UINavigationController *)getBottomNavigationController {
    if ([self.presentedViewController isKindOfClass:[ICMNavigationController class]]) return (UINavigationController *)self.presentedViewController;
    return nil;
}
- (NSArray *)presentedViewControllersBehind:(UIViewController *)controller {
    NSMutableArray *behind = [NSMutableArray array];
    UIViewController *presenter = controller.presentingViewController;
    while (presenter) { [behind addObject:presenter]; presenter = presenter.presentingViewController; }
    return [behind copy];
}
- (void)closeIntercomModalWithCompletion:(void (^)(void))completion {
    [self postIntercomModalViewControllerWillHideNotification];
    if (!self.presentedViewController) { [self intercomWindowDidClose]; return; }
    UIViewController *top = [self topPresentedViewController:self.presentedViewController];
    UINavigationController *navigation = nil;
    if ([top isKindOfClass:[ICMNavigationController class]]) navigation = (UINavigationController *)top;
    else if ([top.presentingViewController isKindOfClass:[ICMNavigationController class]]) navigation = (UINavigationController *)top.presentingViewController;
    if (navigation) {
        if ([navigation.presentingViewController isKindOfClass:[ICMRootViewController class]]) [self postIntercomWindowWillHideNotification];
        [navigation dismissViewControllerAnimated:YES completion:^{
            [self postIntercomModalViewControllerDidHideNotification];
            [self handleIntercomWindowDismissal:navigation];
            if (completion) completion();
        }];
    }
}
- (void)handleIntercomWindowDismissal:(UINavigationController *)navigation {
    if (self.contentModalPresented && navigation.viewControllers.count == 1 && [navigation.viewControllers.firstObject isKindOfClass:[ICMContentModalViewController class]]) self.contentModalPresented = NO;
    if (![self isIntercomPresented]) {
        [self removeSnapshotOfHostAppWindow];
        [self intercomWindowDidClose];
        [self postIntercomWindowDidHideNotification];
        [self animateStatusBarChange];
    }
}
- (void)postIntercomWindowWillHideNotification {
    [[NSNotificationCenter defaultCenter] postNotificationName:@"IntercomWindowWillHideNotification" object:nil];
}
- (void)postIntercomWindowDidHideNotification {
    [self restoreAppLevelStatusBarStyleIfNeeded];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"IntercomWindowDidHideNotification" object:nil];
}
- (void)postIntercomWindowWillShowNotification {
    [self saveAppLevelStatusBarStyleIfNeeded];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"IntercomWindowWillShowNotification" object:nil];
}
- (void)postIntercomWindowDidShowNotification {
    [[NSNotificationCenter defaultCenter] postNotificationName:@"IntercomWindowDidShowNotification" object:nil];
}
- (void)saveAppLevelStatusBarStyleIfNeeded {
    if (![ICMHostAppState isUsingViewControllerBasedStatusBarAppearance] && self.windowScene) {
        self.savedAppLevelStatusBarStyle = self.windowScene.statusBarManager.statusBarStyle;
        if (@available(iOS 26.0, *)) [self setAppLevelStatusBarStyle:self.savedAppLevelStatusBarStyle];
        else [self setAppLevelStatusBarStyle:UIStatusBarStyleLightContent];
    }
}
- (void)restoreAppLevelStatusBarStyleIfNeeded {
    if (![ICMHostAppState isUsingViewControllerBasedStatusBarAppearance] && self.windowScene) [self setAppLevelStatusBarStyle:self.savedAppLevelStatusBarStyle];
}
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
- (void)setAppLevelStatusBarStyle:(UIStatusBarStyle)style { [[UIApplication sharedApplication] setStatusBarStyle:style animated:YES]; }
#pragma clang diagnostic pop
- (void)postIntercomModalViewControllerWillShowNotification {
    [[NSNotificationCenter defaultCenter] postNotificationName:@"IntercomModalViewControllerWillShowNotification" object:nil];
}
- (void)postIntercomModalViewControllerWillHideNotification {
    [[NSNotificationCenter defaultCenter] postNotificationName:@"IntercomModalViewControllerWillHideNotification" object:nil];
}
- (void)postIntercomModalViewControllerDidHideNotification {
    [[NSNotificationCenter defaultCenter] postNotificationName:@"IntercomModalViewControllerDidHideNotification" object:nil];
}
- (void)intercomWindowDidClose {
    if (!self.presentedViewController) [self.delegate setWindowIsFullScreen:NO];
    [self.inAppsVC updateLauncherVisibility];
    self.tearingDown = NO;
    if (!self.inAppsVC.conversations.count) [self hideIntercomWindowIfNeeded];
    self.isPresentedViewControllerDismissibleBySwipe = YES;
}
@end
