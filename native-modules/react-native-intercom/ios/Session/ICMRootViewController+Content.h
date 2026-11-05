#import "ICMRootViewController.h"
@interface ICMRootViewController (Content)
- (void)presentContentViewController:(UIViewController *)controller closeButtonAccessibilityText:(NSString *)text;
- (BOOL)isPresentingHomeView:(NSArray *)controllers;
@end
