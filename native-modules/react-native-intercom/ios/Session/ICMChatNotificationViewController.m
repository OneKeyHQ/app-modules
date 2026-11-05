#import "ICMChatNotificationViewController.h"
#import "ICMConversation.h"
#import "ICMConversationPart.h"
#import "ICMSoundPlayer.h"
#import "ICMHTTPBridge.h"
#import "ICMHTTPClient+ReadAndReactions.h"
#import "../Identity/ICMUtils.h"
#import <math.h>
@interface ICMUtils (ChatNotificationWindows)
+ (NSArray *)fetchApplicationWindows;
@end
@implementation ICMChatNotificationViewController
@synthesize conversation = _conversation;
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate config:(ICMConfig *)config {
    return [self initWithConversation:conversation delegate:delegate config:config themeProvider:nil];
}
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate config:(ICMConfig *)config themeProvider:(id)themeProvider {
    self = [super init];
    if (self) {
        _delegate = delegate;
        _conversation = conversation;
        _config = config;
        _themeProvider = themeProvider;
        [ICMSoundPlayer playSoundForInApp];
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    [self addChatNotificationView];
    self.chatNotificationView.alpha = 0;
    if ([self.delegate respondsToSelector:@selector(conversationShown:asFull:)]) [self.delegate conversationShown:self.conversation asFull:self.conversation.lastPart.deliveryOption == 1];
}
- (void)viewDidAppear:(BOOL)animated { [super viewDidAppear:animated]; [self animateIn]; [self markChatFullAsRead]; }
- (void)addChatNotificationView {
    UIUserInterfaceSizeClass sizeClass = self.traitCollection.horizontalSizeClass;
    CGFloat width = 398;
    if (sizeClass == UIUserInterfaceSizeClassCompact) {
        UIEdgeInsets insets = self.view.safeAreaInsets;
        width = CGRectGetWidth(self.view.bounds) - insets.left - insets.right - 32;
    }
    self.chatNotificationView = [[ICMRecoveredChatNotificationView alloc] initWithConversationPart:self.conversation.lastPart ticket:self.conversation.ticket width:width delegate:self themeProvider:self.themeProvider];
    [self.chatNotificationView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(notificationTapped)]];
    [self.chatNotificationView addGestureRecognizer:[[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(pan:)]];
    self.chatNotificationView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:self.chatNotificationView];
    self.chatNotificationViewTopConstraint = [self.chatNotificationView.topAnchor constraintGreaterThanOrEqualToAnchor:self.view.topAnchor constant:[self getTopPadding]];
    NSLayoutConstraint *trailing = [self.chatNotificationView.trailingAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.trailingAnchor constant:-16];
    trailing.priority = 999;
    [NSLayoutConstraint activateConstraints:@[
        [self.chatNotificationView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor constant:-32],
        [self.chatNotificationView.heightAnchor constraintGreaterThanOrEqualToConstant:70],
        self.chatNotificationViewTopConstraint,
        [self.chatNotificationView.centerXAnchor constraintEqualToAnchor:self.view.centerXAnchor],
        [self.chatNotificationView.widthAnchor constraintLessThanOrEqualToConstant:398], trailing
    ]];
}
- (CGFloat)getTopPadding {
    CGFloat top = CGRectGetHeight(self.view.window.windowScene.statusBarManager.statusBarFrame) + 44;
    for (UIWindow *window in [ICMUtils fetchApplicationWindows]) {
        UIViewController *root = window.rootViewController;
        UINavigationController *navigation = nil;
        if ([root isKindOfClass:UINavigationController.class]) navigation = (UINavigationController *)root;
        else if ([root isKindOfClass:UITabBarController.class]) {
            if ([((UITabBarController *)root).selectedViewController isKindOfClass:UINavigationController.class]) navigation = (UINavigationController *)((UITabBarController *)root).selectedViewController;
        }
        if (navigation) {
            CGFloat candidate = CGRectGetMaxY(navigation.navigationBar.frame);
            if (top < candidate) top = candidate;
        }
    }
    return top + 32;
}
- (void)traitCollectionDidChange:(UITraitCollection *)previous {
    [super traitCollectionDidChange:previous];
    if (previous.verticalSizeClass != self.traitCollection.verticalSizeClass) self.chatNotificationViewTopConstraint.constant = [self getTopPadding];
}
- (void)viewWillLayoutSubviews { [super viewWillLayoutSubviews]; if (!self.isAnimating) [self layoutViews]; }
- (void)layoutViews { self.notificationViewCenter = self.chatNotificationView.center; }
- (void)setConversation:(ICMConversation *)conversation {
    if (![conversation isEqual:_conversation]) [ICMSoundPlayer playSoundForInApp];
    if ([_conversation.conversationId isEqualToString:conversation.conversationId]) {
        BOOL equal = [_conversation.lastPart isEqual:conversation.lastPart];
        _conversation = conversation;
        if (!equal) [self pulseForNewMessage];
    } else _conversation = conversation;
    [self.chatNotificationView loadAvatarFor:self.conversation.lastPart];
}
- (void)pulseForNewMessage { [self.view layoutIfNeeded]; self.isAnimating = YES; [self animateHideNotificationView]; }
- (void)animateIn {
    [self.view layoutIfNeeded];
    self.notificationViewCenter = self.chatNotificationView.center;
    self.isAnimating = YES;
    self.chatNotificationView.center = CGPointMake(self.notificationViewCenter.x - 50, self.notificationViewCenter.y + 5);
    self.chatNotificationView.alpha = 0;
    self.chatNotificationView.transform = CGAffineTransformMakeScale(0.8, 0.8);
    [UIView animateWithDuration:0.2 delay:0.6 options:0 animations:^{
        self.chatNotificationView.alpha = 1;
        self.chatNotificationView.center = self.notificationViewCenter;
        self.chatNotificationView.transform = CGAffineTransformIdentity;
    } completion:^(BOOL finished) {
        (void)finished;
        self.isAnimating = NO;
        [self.view layoutIfNeeded];
    }];
}
- (void)animateHideNotificationView {
    NSString *text = [self.chatNotificationView getParticipantNameAndTimeLabelTextForConversationPart:self.conversation.lastPart];
    [UIView animateWithDuration:0.15 animations:^{
        self.chatNotificationView.transform = CGAffineTransformMakeScale(1.05, 1.05);
        if (![text isEqualToString:self.chatNotificationView.participantNameAndTimeLabelText]) [self.chatNotificationView setParticipantNameAndTimeLabelAlpha:0];
        [self.chatNotificationView setNotificationContentAlpha:0];
    } completion:^(BOOL finished) {
        (void)finished;
        [self.chatNotificationView updateWithConversationPart:self.conversation.lastPart ticket:self.conversation.ticket];
        [self animateShowNotificationView];
    }];
}
- (void)animateShowNotificationView {
    [UIView animateWithDuration:0.15 animations:^{
        [self layoutViews];
        [self.chatNotificationView setParticipantNameAndTimeLabelAlpha:1];
        [self.chatNotificationView setNotificationContentAlpha:1];
        self.chatNotificationView.transform = CGAffineTransformIdentity;
        [self.view layoutIfNeeded];
    } completion:^(BOOL finished) { (void)finished; self.isAnimating = NO; }];
}
- (void)notificationTapped {
    [self.delegate presentConversation:self.conversation fromFull:self.conversation.lastPart.deliveryOption == 1];
    [self.delegate hideInApp:self];
}
- (void)markChatFullAsRead {
    if (self.conversation.lastPart.deliveryOption == 1) [ICMHTTPBridge markConversationAsRead:self.conversation];
}
- (void)pan:(UIPanGestureRecognizer *)gesture {
    CGFloat x = [gesture locationInView:self.view].x;
    CGFloat y = CGRectGetMidY(self.chatNotificationView.frame);
    UIGestureRecognizerState state = gesture.state;
    if (state == UIGestureRecognizerStateBegan) {
        self.isAnimating = YES;
        self.initialPanPoint = CGPointMake(x, y);
        [UIView animateWithDuration:0.15 animations:^{
            self.chatNotificationView.transform = CGAffineTransformMakeScale(0.9, 0.9);
            self.chatNotificationView.alpha = 0.9;
        }];
    } else if (state == UIGestureRecognizerStateChanged) {
        CGFloat origin = self.notificationViewCenter.x + self.initialPanPoint.x;
        origin -= self.notificationViewCenter.x;
        CGFloat delta = x - origin;
        CGFloat width = CGRectGetWidth(self.chatNotificationView.bounds);
        CGPoint center = self.notificationViewCenter;
        self.chatNotificationView.center = CGPointMake(center.x + (delta * width * 0.4) / fma(delta, 0.4, width), center.y);
    } else {
        CGFloat velocity = [gesture velocityInView:self.view].x;
        if (!(fabs(velocity) > 800)) {
            [UIView animateWithDuration:0.3 delay:0 usingSpringWithDamping:0.8 initialSpringVelocity:0.3 options:0 animations:^{
                self.chatNotificationView.center = self.notificationViewCenter;
                self.chatNotificationView.transform = CGAffineTransformIdentity;
                self.chatNotificationView.alpha = 1;
            } completion:^(BOOL finished) {
                (void)finished;
                self.initialPanPoint = CGPointZero;
                self.isAnimating = NO;
            }];
        } else {
            CGFloat target;
            if (velocity > 0) target = CGRectGetWidth(self.view.bounds) + CGRectGetWidth(self.chatNotificationView.bounds);
            else target = -CGRectGetWidth(self.chatNotificationView.bounds);
            CGFloat distance = self.chatNotificationView.center.x - target;
            if (distance < 0) distance = -distance;
            CGFloat duration = distance / velocity;
            if (duration < 0) duration = -duration;
            [UIView animateWithDuration:duration delay:0 options:4 animations:^{
                self.chatNotificationView.center = CGPointMake(target, self.notificationViewCenter.y);
            } completion:^(BOOL finished) {
                (void)finished;
                self.initialPanPoint = CGPointZero;
                self.isAnimating = NO;
                [self.delegate dismissConversation:self.conversation fromFull:self.conversation.lastPart.deliveryOption == 1];
                [self.delegate hideInApp:self];
                [self.chatNotificationView removeFromSuperview];
            }];
        }
    }
}
- (void)animateOutWithCompletion:(void (^)(void))completion {
    [UIView animateWithDuration:0.5 delay:0 usingSpringWithDamping:0.8 initialSpringVelocity:0.3 options:0 animations:^{
        self.isAnimating = YES;
        self.chatNotificationView.alpha = 0;
        self.chatNotificationView.transform = CGAffineTransformTranslate(CGAffineTransformMakeScale(0.9, 0.9), 0, 40);
    } completion:^(BOOL finished) {
        (void)finished;
        self.isAnimating = NO;
        completion();
    }];
}
- (void)conversationMarkedAsRead:(NSString *)identifier {
    if ([identifier isEqualToString:self.conversation.conversationId] && self.conversation.lastPart.deliveryOption != 1) [self.delegate hideInApp:self];
}
- (void)quickReplyTapped { [self notificationTapped]; }
@end
