#import "ICMRootViewController.h"
@interface ICMRootViewController (ModalLifecycle)
- (UIViewController *)topPresentedViewController:(UIViewController *)controller;
- (NSArray *)presentedViewControllersBehind:(UIViewController *)controller;
- (void)closeIntercomModalWithCompletion:(void (^)(void))completion;
- (void)handleIntercomWindowDismissal:(UINavigationController *)navigation;
- (void)postIntercomWindowWillHideNotification;
- (void)postIntercomWindowDidHideNotification;
- (void)postIntercomWindowWillShowNotification;
- (void)postIntercomWindowDidShowNotification;
- (void)saveAppLevelStatusBarStyleIfNeeded;
- (void)restoreAppLevelStatusBarStyleIfNeeded;
- (void)setAppLevelStatusBarStyle:(UIStatusBarStyle)style;
- (void)postIntercomModalViewControllerWillShowNotification;
- (void)postIntercomModalViewControllerWillHideNotification;
- (void)postIntercomModalViewControllerDidHideNotification;
- (void)intercomWindowDidClose;
@end
@interface ICMRootViewController (UnrestoredPresentationEffects)
- (void)removeSnapshotOfHostAppWindow;
- (void)animateStatusBarChange;
@end
