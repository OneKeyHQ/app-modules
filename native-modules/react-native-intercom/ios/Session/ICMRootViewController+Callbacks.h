#import "ICMRootViewController.h"
@interface ICMRootViewController (Callbacks)
- (void)launcherTapped;
- (void)didShowFullScreenInApp;
- (void)didHideFullScreenInApp;
- (void)dismissibleBySwipe:(BOOL)dismissible didAttemptDismiss:(void (^)(void))attempt;
- (void)didSelectErrorAction;
@end
