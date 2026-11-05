// Partial original lifecycle/host/unread implementation, not a complete UI.
#import "ICMRootViewController.h"
#import "ICMRootViewControllerDependencies.h"
#import "ICMRootViewController+Presentation.h"
#import "ICMRootViewController+ModalLifecycle.h"
#import "ICMDataManagerDependencies.h"
#import "ICMHTTPBridge.h"
#import "ICMWindow.h"
#import "ICMWindowDependencies.h"
#import "../Identity/ICMDeviceData.h"
#import "ICMIdentityStore.h"
@interface ICMHTTPBridge (OriginalInAppDismissal)
+ (void)markConversationAsDismissed:(NSString *)identifier;
@end
@implementation ICMRootViewController
@synthesize contentModalPresented = _contentModalPresented, tearingDown = _tearingDown;
@synthesize isPresentedViewControllerDismissibleBySwipe = _isPresentedViewControllerDismissibleBySwipe;
@synthesize delegate = _delegate, hostAppState = _hostAppState, postPresentationBlock = _postPresentationBlock;
@synthesize inAppsVC = _inAppsVC, hostAppWindow = _hostAppWindow, windowScene = _windowScene;
@synthesize hostAppSnapshotView = _hostAppSnapshotView, didAttemptDismissBlock = _didAttemptDismissBlock;
@synthesize openCallService = _openCallService, savedAppLevelStatusBarStyle = _savedAppLevelStatusBarStyle;
- (instancetype)initWithWindowScene:(UIWindowScene *)scene {
    if ((self = [super init])) { _windowScene = scene; [self commonInit]; }
    return self;
}
- (instancetype)init {
    if ((self = [super init])) [self commonInit];
    return self;
}
- (void)commonInit {
    _hostAppState = [self hostAppStateFromWindow:self.hostAppWindow];
    _isPresentedViewControllerDismissibleBySwipe = YES;
    _openCallService = (id)[ICMRecoveredOpenCallService shared];
    [ICMDeviceData storeUserInterfaceStyle:self.traitCollection.userInterfaceStyle];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(conversationMarkedAsRead:) name:@"ICMConversationMarkedAsReadNotification" object:nil];
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
- (void)dismissHostAppKeyboard {
    if (![self isPresentingVC]) for (UIWindow *window in [ICMUtils fetchApplicationWindows]) [window endEditing:YES];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(dismissAllIntercomViews) name:@"kICMIntercomDeepLinkNotification" object:nil];
    self.inAppsVC = [[ICMInAppsViewController alloc] initWithConversations:@[] delegate:self hostAppState:self.hostAppState];
    [self addChildViewController:self.inAppsVC];
    CGRect bounds = self.view.bounds;
    self.inAppsVC.view.frame = bounds;
    [self.view addSubview:self.inAppsVC.view];
    [self.inAppsVC didMoveToParentViewController:self];
}
- (void)viewWillLayoutSubviews {
    [super viewWillLayoutSubviews];
    CGRect bounds = self.view.bounds;
    self.inAppsVC.view.frame = bounds;
}
- (void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator {
    [coordinator animateAlongsideTransition:nil completion:^(__unused id<UIViewControllerTransitionCoordinatorContext> context) {
        ICMHostAppState *previous = self.hostAppState;
        [self updateHostAppState];
        if (![previous isEqual:self.hostAppState]) [self animateStatusBarChange];
        if ([self isPresentingVC]) [self insertSnapshotOfHostAppWindow];
    }];
    [super viewWillTransitionToSize:size withTransitionCoordinator:coordinator];
}
- (void)animateStatusBarChange { [UIView animateWithDuration:0.3 animations:^{ [self setNeedsStatusBarAppearanceUpdate]; }]; }
- (UIStatusBarStyle)preferredStatusBarStyle {
    if ([self isPresentingVC]) return UIStatusBarStyleLightContent;
    if (self.inAppsVC && self.inAppsVC.parentViewController == self) return self.inAppsVC.preferredStatusBarStyle;
    return self.hostAppState.preferredStatusBarStyle;
}
- (BOOL)prefersStatusBarHidden {
    if ([self isPresentingVC] && self.presentedViewController.prefersStatusBarHidden) return YES;
    return self.hostAppState.prefersStatusBarHidden;
}
- (BOOL)shouldAutorotate { return self.hostAppState.shouldAutorotate; }
- (UIInterfaceOrientationMask)supportedInterfaceOrientations { return self.hostAppState.supportedInterfaceOrientations; }
- (BOOL)shouldHandleTouchOnView:(UIView *)view {
    if (self.presentedViewController) return YES;
    if (self.view == view) return NO;
    return [self.inAppsVC shouldHandleTouchOnView:view];
}
- (BOOL)isPresentingVC { return [self isIntercomPresented] || self.contentModalPresented; }
- (BOOL)isShowingMessenger { return [self.presentedViewController isKindOfClass:[ICMNavigationController class]]; }
- (BOOL)isIntercomPresented { return [self.presentedViewController isKindOfClass:[ICMNavigationController class]] || self.contentModalPresented; }
- (void)tearDownInApps { [self.inAppsVC tearDownInApps]; }
- (void)updateLauncherVisibility { [self.inAppsVC updateLauncherVisibility]; }
- (void)hideIntercomWindowIfNeeded { if ([self shouldHideIntercomWindow]) [self.delegate hideIntercomWindow]; }
- (BOOL)shouldHideIntercomWindow {
    if ([self isPresentingVC] || self.tearingDown) return NO;
    return ![self.inAppsVC isShowingLauncherOrInApp];
}
- (void)tearDown {
    _tearingDown = YES;
    [self.inAppsVC tearDown];
    [self dismissAllIntercomViews];
}
- (void)updateUnreadConversations:(NSArray *)conversations completion:(void (^)(void))completion {
    if ([ICMIdentityStore currentIdentity].userIdentity.identityExists) {
        if (conversations.count || [self isPresentingVC] || [self.delegate shouldShowLauncher]) {
            [self.inAppsVC setConversations:conversations completion:completion];
            return;
        }
        [self hideIntercomWindowIfNeeded];
    }
    if (completion) completion();
}
- (BOOL)shouldShowLauncher { return [self.delegate shouldShowLauncher]; }
- (void)didHideInApps { [self hideIntercomWindowIfNeeded]; }
- (ICMWindow *)intercomWindow { return (ICMWindow *)self.view.window; }
- (UIWindow *)hostAppWindow {
    if (!_hostAppWindow) _hostAppWindow = [self findHostAppWindow];
    return _hostAppWindow;
}
- (UIWindow *)findHostAppWindow {
    for (UIWindow *window in self.windowScene.windows) if (![window isKindOfClass:[ICMWindow class]] && !window.hidden) return window;
    return nil;
}
- (ICMHostAppState *)hostAppStateFromWindow:(UIWindow *)window {
    UIViewController *controller = window.rootViewController;
    if (!controller && [ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - could not determine rootViewController -> there might be problems with correct orientation and rotation in the SDK window");
    return [[ICMHostAppState alloc] initWithViewController:controller];
}
- (void)updateHostAppState {
    self.hostAppState = [self hostAppStateFromWindow:self.hostAppWindow];
    ICMHostAppState *state = self.hostAppState;
    self.inAppsVC.hostAppState = state;
}
- (void)conversationMarkedAsRead:(NSNotification *)notification {
    id identifier = notification.object;
    [self.inAppsVC conversationMarkedAsRead:identifier];
}
- (void)bottomPaddingUpdated { [self.inAppsVC bottomPaddingUpdated]; }
- (void)presentConversation:(id)conversation fromFull:(__unused BOOL)full {
    [conversation setPartDisplayStyle:1];
    [self presentConversation:conversation];
}
- (void)dismissConversation:(id)conversation fromFull:(__unused BOOL)full {
    [ICMHTTPBridge markConversationAsDismissed:[conversation conversationId]];
}
- (void)hideInApp:(id)controller {
    if (controller == self.inAppsVC) [self updateUnreadConversations:@[] completion:nil];
}
- (void)showInbox { [self presentMessages]; }
- (void)closeIntercomModal { [self closeIntercomModalWithCompletion:nil]; }
@end
