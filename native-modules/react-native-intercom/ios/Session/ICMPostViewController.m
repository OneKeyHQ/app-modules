#import "ICMPostViewController.h"
#import "ICMFullScreenInAppViewController.h"
#import "ICMFullScreenInAppDependencies.h"
#import "ICMConversation.h"
#import "ICMConversationPart.h"
#import "ICMConfig.h"
#import "ICMConversationDependencies.h"
#import "ICMBlockAppearanceDependencies.h"
#import "ICMAutoLayout.h"
#import "ICMAvatarView.h"
@interface ICMRecoveredTheme (InAppInverseColor)
@property (nonatomic, readonly) UIColor *textInversed;
@end
@implementation ICMPostViewController
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate isInApp:(BOOL)inApp { return [self initWithConversation:conversation delegate:delegate isInApp:inApp config:nil]; }
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate isInApp:(BOOL)inApp config:(id<ICMConfigProtocol>)config {
    self = [super init];
    if (self) {
        _delegate = delegate;
        _conversation = conversation;
        _conversationPart = conversation.conversationParts.firstObject;
        _isInApp = inApp;
        _config = config ?: [ICMConfig sharedConfig];
    }
    return self;
}
- (ICMInAppAttributes)inAppAttributes { return (ICMInAppAttributes){88,48,32,24,24,24,{16,24,0,24}}; }
- (void)loadView {
    [super loadView];
    self.view.alpha = 0;
    UIColor *clear = UIColor.clearColor;
    self.view.backgroundColor = clear;
    self.view.accessibilityIdentifier = @"intercom post";
    self.backgroundView = [[UIVisualEffectView alloc] initWithEffect:[UIBlurEffect effectWithStyle:UIBlurEffectStyleDark]];
    self.backgroundView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:self.backgroundView];
    ICMFullScreenInAppViewController *controller = [ICMFullScreenInAppViewController alloc];
    controller = [controller initWithConversation:self.conversation inAppDelegate:self.delegate fullScreenDelegate:self isInApp:self.isInApp attributes:[self inAppAttributes]];
    self.inAppController = controller;
    self.inAppController.view.translatesAutoresizingMaskIntoConstraints = NO;
    self.inAppController.view.alpha = 0;
    [self addChildViewController:self.inAppController];
    [self.view addSubview:self.inAppController.view];
    [self.inAppController didMoveToParentViewController:self];
    self.inAppController.headerController.closeButton.accessibilityIdentifier = @"intercom post close button";
    ICMInAppHeaderViewController *header = self.inAppController.headerController;
    UIImage *image = [ICMAssets closeImage];
    [header setCloseButtonImage:image tintColor:UIColor.whiteColor];
    if (self.inAppController.fakeComposerView) self.inAppController.fakeComposerView.accessibilityIdentifier = @"intercom post open button";
    [ICMAutoLayout pinView:self.backgroundView edges:NSUIntegerMax toSameEdgesOfView:self.view];
    [NSLayoutConstraint activateConstraints:@[
        [self.inAppController.view.topAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.topAnchor],
        [self.inAppController.view.leftAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.leftAnchor],
        [self.inAppController.view.rightAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.rightAnchor],
        [self.inAppController.view.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor]
    ]];
    [ICMAutoLayout pinView:self.inAppController.headerController.avatarView attribute:NSLayoutAttributeTop toAttribute:NSLayoutAttributeTop ofItem:self.inAppController.headerController.view withConstant:36];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    NSDictionary *name = @{NSFontAttributeName:[ICMUtils fontOfSize:13 weight:2], NSForegroundColorAttributeName:UIColor.whiteColor};
    NSDictionary *status = @{NSFontAttributeName:[ICMUtils fontOfSize:13 weight:0], NSForegroundColorAttributeName:UIColor.whiteColor};
    NSArray *parts = self.conversation.conversationParts;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"self.isInitialMessage = %d", 1];
    ICMConversationPart *first = [parts filteredArrayUsingPredicate:predicate].firstObject;
    id participant = first.participant;
    if ([self.conversation.lastAdmin.id isEqual:self.conversationPart.participant.id]) participant = self.conversation.lastAdmin;
    [self.inAppController.headerController configureForParticipant:participant nameAttributes:name statusAttributes:status appName:self.config.appName];
}
- (void)viewDidAppear:(BOOL)animated { [super viewDidAppear:animated]; [self animateIn]; }
- (void)animateIn {
    [UIView animateWithDuration:0.2 animations:^{ [self setNeedsStatusBarAppearanceUpdate]; self.view.alpha = 1; }];
    self.inAppController.view.transform = CGAffineTransformMakeScale(0.98, 0.98);
    [UIView animateWithDuration:0.2 delay:0.3 usingSpringWithDamping:0.8 initialSpringVelocity:0.3 options:0 animations:^{
        self.inAppController.view.alpha = 1;
        self.inAppController.view.transform = CGAffineTransformIdentity;
    } completion:nil];
}
- (UIStatusBarStyle)preferredStatusBarStyle { return UIStatusBarStyleLightContent; }
- (UIColor *)headerSeparatorColor { return [[ICMRecoveredDefaultThemeProvider shared].theme.textInversed colorWithAlphaComponent:0.15]; }
- (UIColor *)footerSeparatorColor { return [[ICMRecoveredDefaultThemeProvider shared].theme.textInversed colorWithAlphaComponent:0.12]; }
- (UIColor *)fakeComposerInactiveTextColor { return [ICMRecoveredDefaultThemeProvider shared].theme.textInversed; }
- (void)close { [UIView animateWithDuration:0.3 animations:^{ self.view.alpha = 0; } completion:^(BOOL finished) { (void)finished; [self.delegate hideInApp:self]; }]; }
- (void)openPressed {
    [UIView animateWithDuration:0.3 animations:^{ self.view.alpha = 0; } completion:^(BOOL finished) { (void)finished; [self.delegate hideInApp:self]; }];
    [self.delegate presentConversation:self.conversation fromFull:YES];
}
- (void)closePressed { [self close]; }
@end
