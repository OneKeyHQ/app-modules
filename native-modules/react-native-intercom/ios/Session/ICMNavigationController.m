#import "ICMNavigationController.h"
#import "ICMBlockAppearanceDependencies.h"
#import <QuartzCore/QuartzCore.h>
@interface ICMRecoveredTheme (OriginalNavigationColors)
@property (nonatomic, readonly) UIColor *neutralBorderEmphasis;
@end
@implementation ICMNavigationController
- (instancetype)initWithHostAppState:(ICMHostAppState *)state closeIntercomModalDelegate:(id<ICMCloseIntercomModalDelegate>)delegate {
    if ((self = [super init])) { _hostAppState = state; _closeIntercomModalDelegate = delegate; }
    return self;
}
- (void)loadView { [super loadView]; }
- (void)viewDidLoad {
    [super viewDidLoad];
    UIColor *background = [ICMRecoveredDefaultThemeProvider shared].theme.base;
    self.view.backgroundColor = background;
    [self setupNavigationBar];
}
- (void)setupNavigationBar {
    if (@available(iOS 26.0, *)) return;
    UINavigationBarAppearance *appearance = [UINavigationBarAppearance new];
    [appearance configureWithOpaqueBackground];
    appearance.shadowColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralBorderEmphasis;
    appearance.backgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.base;
    self.navigationBar.tintColor = [ICMRecoveredDefaultThemeProvider shared].theme.iconDefault;
    self.navigationBar.standardAppearance = appearance;
    self.navigationBar.scrollEdgeAppearance = appearance;
    self.navigationBar.compactAppearance = appearance;
}
- (void)configureTransparentNavigationBar {
    if (@available(iOS 26.0, *)) return;
    UINavigationBarAppearance *appearance = [UINavigationBarAppearance new];
    [appearance configureWithTransparentBackground];
    appearance.shadowColor = nil;
    appearance.shadowImage = nil;
    self.navigationBar.tintColor = nil;
    self.navigationBar.standardAppearance = appearance;
    self.navigationBar.scrollEdgeAppearance = appearance;
    self.navigationBar.compactAppearance = appearance;
}
- (UIStatusBarStyle)preferredStatusBarStyle { return UIStatusBarStyleLightContent; }
- (BOOL)prefersStatusBarHidden {
    if (self.hostAppState) return self.hostAppState.prefersStatusBarHidden;
    return [super prefersStatusBarHidden];
}
- (void)closeIntercomModal {
    if ([self.closeIntercomModalDelegate respondsToSelector:@selector(closeIntercomModal)]) [self.closeIntercomModalDelegate closeIntercomModal];
}
- (void)displayCloseButton {
    UIImageSymbolWeight weight;
    if (@available(iOS 26.0, *)) weight = UIImageSymbolWeightRegular;
    else weight = UIImageSymbolWeightSemibold;
    UIImageSymbolConfiguration *configuration = [UIImageSymbolConfiguration configurationWithWeight:weight];
    UIImage *image = [UIImage systemImageNamed:@"xmark" withConfiguration:configuration];
    UIBarButtonItem *button = [[UIBarButtonItem alloc] initWithImage:image style:UIBarButtonItemStylePlain target:self action:@selector(closeIntercomModal)];
    button.accessibilityIdentifier = @"intercom close button";
    button.accessibilityLabel = [[ICMTranslationManager messenger] translationForKey:@"close"];
    self.topViewController.navigationItem.rightBarButtonItem = button;
}
- (void)removeCloseButton { self.topViewController.navigationItem.rightBarButtonItem = nil; }
- (UIBarButtonItem *)closeButton { return self.topViewController.navigationItem.rightBarButtonItem; }
- (void)pushViewController:(UIViewController *)controller animated:(BOOL)animated { [super pushViewController:controller animated:animated]; }
- (void)fadeToViewController:(UIViewController *)controller {
    CATransition *transition = [CATransition animation];
    transition.duration = 0.33;
    transition.type = kCATransitionFade;
    [self.view.layer addAnimation:transition forKey:nil];
    NSMutableArray *controllers = [self.viewControllers mutableCopy];
    NSUInteger count = controllers.count;
    [controllers replaceObjectAtIndex:count - 1 withObject:controller];
    [self setViewControllers:controllers animated:NO];
}
- (UIViewController *)popViewControllerAnimated:(BOOL)animated {
    (void)self.viewControllers.count;
    return [super popViewControllerAnimated:animated];
}
- (void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator {
    if (self.navigationController.modalPresentationStyle != 0) {
        [coordinator animateAlongsideTransition:nil completion:^(id<UIViewControllerTransitionCoordinatorContext> context) {
            (void)context;
            CGRect frame = self.navigationBar.frame;
            frame.origin.y = 0;
            [UIView animateWithDuration:0.1 animations:^{ self.navigationBar.frame = frame; }];
        }];
    }
    [super viewWillTransitionToSize:size withTransitionCoordinator:coordinator];
}
@end
