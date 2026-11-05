#import "ICMNoteViewController.h"
#import "ICMFullScreenInAppViewController.h"
#import "ICMFullScreenInAppDependencies.h"
#import "ICMConversation.h"
#import "ICMConversationPart.h"
#import "ICMConfig.h"
#import "ICMConversationDependencies.h"
#import "ICMBlockAppearanceDependencies.h"
#import "ICMAutoLayout.h"
#import "ICMAvatarView.h"
@implementation ICMNoteViewController
@synthesize backgroundView = _backgroundView;
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate { return [self initWithConversation:conversation delegate:delegate config:nil]; }
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate config:(id<ICMConfigProtocol>)config {
    ICMFullScreenInAppViewController *controller = [ICMFullScreenInAppViewController alloc];
    ICMInAppAttributes attributes = [[self class] inAppAttributes];
    controller = [controller initWithConversation:conversation inAppDelegate:delegate fullScreenDelegate:self isInApp:YES attributes:attributes];
    self = [super initWithContentViewController:controller];
    if (self) {
        _inAppController = controller;
        _delegate = delegate;
        _conversation = conversation;
        _conversationPart = conversation.conversationParts.firstObject;
        _config = config ?: [ICMConfig sharedConfig];
    }
    return self;
}
+ (ICMInAppAttributes)inAppAttributes { return (ICMInAppAttributes){52,52,28,20,20,24,{16,24,16,24}}; }
- (CGFloat)fakeComposerHeight {
    if (self.conversation.messageComposerShouldBeDisplayed) return 52;
    return self.conversationPart.reactionSet ? 52 : 0;
}
- (void)loadView {
    [super loadView];
    self.inAppController.view.accessibilityIdentifier = @"intercom note";
    self.inAppController.view.translatesAutoresizingMaskIntoConstraints = NO;
    UIColor *base = [ICMRecoveredDefaultThemeProvider shared].theme.base;
    self.inAppController.view.backgroundColor = base;
    self.inAppController.headerController.closeButton.accessibilityLabel = @"intercom note close button";
    ICMInAppHeaderViewController *header = self.inAppController.headerController;
    UIImage *image = [ICMAssets closeImage];
    UIColor *muted = [ICMRecoveredDefaultThemeProvider shared].theme.textMuted;
    [header setCloseButtonImage:image tintColor:muted];
    if (self.inAppController.reactionsController) {
        self.inAppController.reactionsController.view.accessibilityLabel = @"intercom note open button";
        UIColor *background = [ICMRecoveredDefaultThemeProvider shared].theme.base;
        self.inAppController.reactionsController.view.backgroundColor = background;
    } else if (self.inAppController.fakeComposerView) self.inAppController.fakeComposerView.accessibilityLabel = @"intercom note open button";
    [ICMAutoLayout centerView:self.inAppController.headerController.avatarView inContainerOnAxis:NSLayoutAttributeCenterY];
    [NSLayoutConstraint activateConstraints:@[[self.inAppController.view.bottomAnchor constraintLessThanOrEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor]]];
}
- (void)viewDidLoad {
    [super viewDidLoad];
    NSDictionary *name = @{NSFontAttributeName:[ICMUtils fontOfSize:14 weight:0], NSForegroundColorAttributeName:[ICMRecoveredDefaultThemeProvider shared].theme.textDefault};
    NSDictionary *status = @{NSFontAttributeName:[ICMUtils fontOfSize:13 weight:0], NSForegroundColorAttributeName:[ICMRecoveredDefaultThemeProvider shared].theme.textMuted};
    id participant = self.conversationPart.participant;
    if ([self.conversation.lastAdmin.id isEqual:self.conversationPart.participant.id]) participant = self.conversation.lastAdmin;
    [self.inAppController.headerController configureForParticipant:participant nameAttributes:name statusAttributes:status appName:self.config.appName];
}
- (void)didCalculateBlocksSize:(CGSize)size {
    CGFloat height = (size.height + 32) + 52;
    height += [self fakeComposerHeight];
    if (height > 552) height = 552;
    [self updateContentHeight:height];
}
- (UIColor *)headerSeparatorColor { return [UIColor colorWithWhite:0 alpha:0.08]; }
- (UIColor *)footerSeparatorColor { return [UIColor colorWithWhite:0 alpha:0.08]; }
- (UIColor *)fakeComposerInactiveTextColor { return [ICMRecoveredDefaultThemeProvider shared].theme.textMuted; }
- (void)closePressed { [self close]; }
- (void)didClose { [self.delegate hideInApp:self]; }
- (void)openPressed {
    [UIView animateWithDuration:0.3 animations:^{ self.view.alpha = 0; } completion:^(BOOL finished) { (void)finished; [self.delegate hideInApp:self]; }];
    [self.delegate presentConversation:self.conversation fromFull:YES];
}
@end
