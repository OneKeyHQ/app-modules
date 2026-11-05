#import <UIKit/UIKit.h>
@interface ICMHostAppState : NSObject {
    BOOL _shouldAutorotate;
    BOOL _prefersStatusBarHidden;
    UIInterfaceOrientationMask _supportedInterfaceOrientations;
    UIStatusBarStyle _preferredStatusBarStyle;
}
@property (nonatomic, readonly) UIInterfaceOrientationMask supportedInterfaceOrientations;
@property (nonatomic, readonly) BOOL shouldAutorotate;
@property (nonatomic, readonly) UIStatusBarStyle preferredStatusBarStyle;
@property (nonatomic, readonly) BOOL prefersStatusBarHidden;
- (instancetype)initWithViewController:(UIViewController *)controller;
+ (UIViewController *)topViewControllerWithRootViewController:(UIViewController *)root;
+ (UIStatusBarStyle)statusBarStyleFromViewController:(UIViewController *)controller;
+ (BOOL)statusBarHiddenFromViewController:(UIViewController *)controller;
+ (BOOL)isUsingViewControllerBasedStatusBarAppearance;
- (BOOL)isEqualToHostAppState:(ICMHostAppState *)other;
@end
