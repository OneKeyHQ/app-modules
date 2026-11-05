#import "ICMContentModalViewController.h"
#import "ICMRootViewControllerDependencies.h"
#import "ICMRootDestinationDependencies.h"
#import "ICMNavigationController.h"
#import "ICMColorHelperImports.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate21LoadingViewController")))
__attribute__((swift_name("LoadingViewController")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredLoadingViewController : UIViewController
- (instancetype)initWithImage:(UIImage *)image accessibilityText:(NSString *)text;
@end
@interface ICMRecoveredOpenCallService (OriginalContentState)
@property (nonatomic, readonly) NSInteger state;
- (void)retry;
@end
@implementation ICMContentModalViewController
@synthesize delegate = _delegate, closeButtonAccessibilityText = _closeButtonAccessibilityText;
@synthesize closeButton = _closeButton, closeButtonTintColor = _closeButtonTintColor;
@synthesize currentChildViewController = _currentChildViewController, contentViewController = _contentViewController;
@synthesize openCallService = _openCallService;
- (instancetype)initWithContentViewController:(id)controller {
    if ((self = [super init])) { _contentViewController = controller; _openCallService = (id)[ICMRecoveredOpenCallService shared]; }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    if ([self.contentViewController respondsToSelector:@selector(prefersTransparentNavigationBar)] && self.contentViewController.prefersTransparentNavigationBar) [[self intercomNavigationController] configureTransparentNavigationBar];
    self.navigationItem.backButtonDisplayMode = UINavigationItemBackButtonDisplayModeMinimal;
    if ([self.contentViewController respondsToSelector:@selector(setContentDelegate:)]) self.contentViewController.contentDelegate = self;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(openCallStateDidChange:) name:@"IntercomOpenCallStateDidChangeNotification" object:nil];
    [self handleOpenCallState];
    self.view.accessibilityIdentifier = @"intercom content holder";
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(viewDidDismissBySwipe) name:@"IntercomRootViewControllerDidDismissPresentedController" object:nil];
    [self configureAsDismissible:YES swipeDismissalRequiresConfirmation:NO didAttemptDismiss:nil];
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
- (void)setupContentViewController {
    (void)[(id)self.contentViewController isKindOfClass:[UIViewController class]];
    [(UIViewController *)self.contentViewController loadViewIfNeeded];
}
- (void)configureAsDismissible:(BOOL)enabled swipeDismissalRequiresConfirmation:(BOOL)confirmation didAttemptDismiss:(void (^)(void))attempt {
    ICMNavigationController *navigation = [self intercomNavigationController];
    id<ICMContentModalViewControllerDelegate> delegate = self.delegate;
    if (!enabled) {
        [delegate dismissibleBySwipe:NO didAttemptDismiss:nil];
        navigation.topViewController.navigationItem.rightBarButtonItem = nil;
        self.closeButton = nil;
        return;
    }
    [delegate dismissibleBySwipe:!confirmation didAttemptDismiss:attempt];
    if (@available(iOS 26.0, *)) self.closeButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:(UIBarButtonSystemItem)24 target:self action:@selector(closeButtonTapped)];
    else {
        UIImageSymbolConfiguration *configuration = [UIImageSymbolConfiguration configurationWithWeight:UIImageSymbolWeightSemibold];
        UIImage *image = [UIImage systemImageNamed:@"xmark" withConfiguration:configuration];
        self.closeButton = [[UIBarButtonItem alloc] initWithImage:image style:UIBarButtonItemStylePlain target:self action:@selector(closeButtonTapped)];
    }
    if (self.closeButtonAccessibilityText && ![self.closeButtonAccessibilityText isEqualToString:@""]) self.closeButton.accessibilityLabel = self.closeButtonAccessibilityText;
    self.closeButton.accessibilityIdentifier = @"intercom modal close button";
    if (self.closeButtonTintColor) self.closeButton.tintColor = self.closeButtonTintColor;
    navigation.topViewController.navigationItem.rightBarButtonItem = self.closeButton;
}
- (void)closeButtonTapped {
    if (self.currentChildViewController != (id)self.contentViewController) [self closeContentModal];
    else [self.contentViewController closeButtonTapped];
}
- (void)viewDidDismissBySwipe { [self.contentViewController viewDidDismissBySwipe]; }
- (void)addViewControllerAsChildViewController:(UIViewController *)controller {
    if (self.currentChildViewController) [self removeCurrentChildViewController];
    self.currentChildViewController = controller;
    [self addChildViewController:controller];
    [self.view addSubview:controller.view];
    CGRect bounds = self.view.bounds;
    controller.view.frame = bounds;
    controller.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    [controller didMoveToParentViewController:self];
}
- (void)removeCurrentChildViewController {
    [self.currentChildViewController willMoveToParentViewController:nil];
    [self.currentChildViewController.view removeFromSuperview];
    [self.currentChildViewController removeFromParentViewController];
}
- (ICMNavigationController *)intercomNavigationController {
    if ([self.navigationController isKindOfClass:[ICMNavigationController class]]) return (ICMNavigationController *)self.navigationController;
    return nil;
}
- (void)contentDidLoadWithDismissEnabled:(BOOL)enabled { [self contentDidLoadWithDismissEnabled:enabled swipeDismissalRequiresConfirmation:NO didAttemptDismiss:nil]; }
- (void)contentDidLoadWithDismissEnabled:(BOOL)enabled swipeDismissalRequiresConfirmation:(BOOL)confirmation didAttemptDismiss:(void (^)(void))attempt {
    [self addViewControllerAsChildViewController:(UIViewController *)self.contentViewController];
    [self configureAsDismissible:enabled swipeDismissalRequiresConfirmation:confirmation didAttemptDismiss:attempt];
}
- (void)configureDismissEnabled:(BOOL)enabled { [self configureDismissEnabled:enabled swipeDismissalRequiresConfirmation:NO didAttemptDismiss:nil]; }
- (void)configureDismissEnabled:(BOOL)enabled swipeDismissalRequiresConfirmation:(BOOL)confirmation didAttemptDismiss:(void (^)(void))attempt { [self configureAsDismissible:enabled swipeDismissalRequiresConfirmation:confirmation didAttemptDismiss:attempt]; }
- (void)showLoadingViewWithImage:(UIImage *)image {
    NSString *text = [[ICMTranslationManager messenger] translationForKey:@"loading"];
    ICMRecoveredLoadingViewController *loading = [[ICMRecoveredLoadingViewController alloc] initWithImage:image accessibilityText:text];
    [self addViewControllerAsChildViewController:loading];
    [self configureAsDismissible:YES swipeDismissalRequiresConfirmation:NO didAttemptDismiss:nil];
}
- (void)contentLoadingDidFail:(NSString *)title showActionButton:(BOOL)show errorMessageSubtitle:(NSString *)subtitle actionButtonTitle:(NSString *)buttonTitle {
    ICMRecoveredErrorViewController *error = [[ICMRecoveredErrorViewController alloc] initWithTitle:title subtitle:subtitle showActionButton:show actionButtonTitle:buttonTitle];
    error.errorDelegate = self;
    [self addViewControllerAsChildViewController:error];
    [self configureAsDismissible:YES swipeDismissalRequiresConfirmation:NO didAttemptDismiss:nil];
}
- (void)closeContentModal { [[self intercomNavigationController] closeIntercomModal]; }
- (void)configureContentTitle:(NSString *)title { [self setTitle:title]; }
- (void)configureContentTitleView:(UIView *)view {
    self.navigationItem.titleView = view;
    NSLayoutConstraint *top = [view.topAnchor constraintEqualToAnchor:self.navigationItem.titleView.topAnchor];
    NSLayoutConstraint *bottom = [view.bottomAnchor constraintEqualToAnchor:self.navigationItem.titleView.bottomAnchor];
    [NSLayoutConstraint activateConstraints:@[top, bottom]];
}
- (void)configureCloseButtonForBackgroundColor:(UIColor *)color {
    if (@available(iOS 26.0, *)) return;
    self.closeButtonTintColor = [ICMRecoveredColorHelper suitableForegroundColorFor:color theme:[ICMRecoveredDefaultThemeProvider shared].theme];
    self.closeButton.tintColor = self.closeButtonTintColor;
}
- (void)didSelectErrorAction {
    if ([(ICMRecoveredOpenCallService *)self.openCallService state] == 2) {
        [self showLoadingIndicator];
        [(ICMRecoveredOpenCallService *)self.openCallService retry];
    } else [self.contentViewController reload];
}
- (void)openCallStateDidChange:(NSNotification *)notification { (void)notification; [self handleOpenCallState]; }
- (void)handleOpenCallState {
    NSInteger state = [(ICMRecoveredOpenCallService *)self.openCallService state];
    if (state == 0) [self showLoadingIndicator];
    else if (state == 2) [self showOpenCallError];
    else if (state == 1) [self setupContentViewController];
}
- (void)showLoadingIndicator {
    if ([self.contentViewController respondsToSelector:@selector(showLoading)]) [self.contentViewController showLoading];
    else [self showLoadingViewWithImage:nil];
}
- (void)showOpenCallError {
    NSString *title = [[ICMTranslationManager messenger] translationForKey:@"spaces_error_title"];
    NSString *subtitle = [[ICMTranslationManager messenger] translationForKey:@"spaces_error_description"];
    NSString *buttonTitle = [[ICMTranslationManager messenger] translationForKey:@"reload"];
    ICMRecoveredErrorViewController *error = [[ICMRecoveredErrorViewController alloc] initWithTitle:title subtitle:subtitle showActionButton:YES actionButtonTitle:buttonTitle];
    error.errorDelegate = self;
    [self addViewControllerAsChildViewController:error];
    [self configureAsDismissible:YES swipeDismissalRequiresConfirmation:NO didAttemptDismiss:nil];
}
@end
