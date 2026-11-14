#import "ICMRootViewController.h"
@interface ICMRootViewController (Destinations)
- (BOOL)errorViewIsDisplayed;
- (void)logIdentifyError;
- (void)presentErrorViewController:(UIViewController *)controller;
- (void)presentConversationSpace;
- (void)presentHelpCenterViewController:(UIViewController *)controller;
@end
