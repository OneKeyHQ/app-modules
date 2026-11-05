// Original host status/orientation snapshot, including legacy UIKit behavior.
#import "ICMHostAppState.h"
@implementation ICMHostAppState
@synthesize shouldAutorotate = _shouldAutorotate, prefersStatusBarHidden = _prefersStatusBarHidden;
@synthesize supportedInterfaceOrientations = _supportedInterfaceOrientations, preferredStatusBarStyle = _preferredStatusBarStyle;
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
- (instancetype)initWithViewController:(UIViewController *)controller {
    if ((self = [super init])) {
        UIViewController *top = [[self class] topViewControllerWithRootViewController:controller];
        if (!top) top = [UIViewController new];
        _supportedInterfaceOrientations = top.supportedInterfaceOrientations;
        _shouldAutorotate = top.shouldAutorotate;
        if ([ICMHostAppState isUsingViewControllerBasedStatusBarAppearance]) {
            _preferredStatusBarStyle = [ICMHostAppState statusBarStyleFromViewController:top];
            _prefersStatusBarHidden = [ICMHostAppState statusBarHiddenFromViewController:top];
        } else {
            _preferredStatusBarStyle = controller.view.window.windowScene.statusBarManager.statusBarStyle;
            _prefersStatusBarHidden = controller.view.window.windowScene.statusBarManager.statusBarHidden;
        }
    }
    return self;
}
#pragma clang diagnostic pop
+ (UIViewController *)topViewControllerWithRootViewController:(UIViewController *)root {
    UIViewController *presented = root.presentedViewController;
    if (presented && presented != root) return [self topViewControllerWithRootViewController:presented];
    return root;
}
+ (UIStatusBarStyle)statusBarStyleFromViewController:(UIViewController *)controller {
    if (!controller) return 0;
    UIViewController *child = controller.childViewControllerForStatusBarStyle;
    if (child && child != controller) return [self statusBarStyleFromViewController:child];
    return controller.preferredStatusBarStyle;
}
+ (BOOL)statusBarHiddenFromViewController:(UIViewController *)controller {
    if (!controller) return NO;
    UIViewController *child = controller.childViewControllerForStatusBarHidden;
    if (child && child != controller) return [self statusBarHiddenFromViewController:child];
    return controller.prefersStatusBarHidden;
}
+ (BOOL)isUsingViewControllerBasedStatusBarAppearance {
    id value = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"UIViewControllerBasedStatusBarAppearance"];
    return !value || [@YES isEqual:value] || [[NSNull null] isEqual:value];
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToHostAppState:object];
}
- (BOOL)isEqualToHostAppState:(ICMHostAppState *)other {
    return self.supportedInterfaceOrientations == other.supportedInterfaceOrientations && self.shouldAutorotate == other.shouldAutorotate && self.preferredStatusBarStyle == other.preferredStatusBarStyle && self.prefersStatusBarHidden == other.prefersStatusBarHidden;
}
- (NSUInteger)hash { return self.supportedInterfaceOrientations ^ self.shouldAutorotate ^ self.preferredStatusBarStyle ^ self.prefersStatusBarHidden; }
@end
