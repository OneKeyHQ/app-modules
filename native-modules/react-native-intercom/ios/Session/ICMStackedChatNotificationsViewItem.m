#import "ICMStackedChatNotificationsViewItem.h"
#import "ICMChatNotificationContentImports.h"
#import "ICMBlockAppearanceDependencies.h"
#import "ICMConfig.h"
#import "ICMConversation.h"
#import "ICMConversationPart.h"
@implementation ICMStackedChatNotificationsViewItem
@synthesize conversation = _conversation;
- (instancetype)initWithConversation:(ICMConversation *)conversation { return [self initWithConversation:conversation config:[ICMConfig sharedConfig]]; }
- (instancetype)initWithConversation:(ICMConversation *)conversation config:(ICMConfig *)config {
    self = [super init];
    if (self) {
        self.notificationContentView = [[ICMRecoveredChatNotificationContentView alloc] initWithPreventFullLayoutStyle:YES blocksWidth:0 config:config themeProvider:[ICMRecoveredDefaultThemeProvider shared] timestampCalculator:[ICMTimestampCalculator sharedInstance]];
        self.notificationContentView.translatesAutoresizingMaskIntoConstraints = NO;
        [self addSubview:self.notificationContentView];
        [NSLayoutConstraint activateConstraints:@[
            [self.notificationContentView.leadingAnchor constraintEqualToAnchor:self.leadingAnchor],
            [self.notificationContentView.trailingAnchor constraintEqualToAnchor:self.trailingAnchor],
            [self.notificationContentView.topAnchor constraintEqualToAnchor:self.topAnchor],
            [self.notificationContentView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor]
        ]];
        self.conversation = conversation;
        self.backgroundColor = UIColor.clearColor;
        self.notificationContentView.layer.cornerRadius = 10;
        self.notificationContentView.layer.borderWidth = 1;
        self.notificationContentView.layer.borderColor = [UIColor.blackColor colorWithAlphaComponent:0.08].CGColor;
        self.accessibilityIdentifier = @"intercom snippet";
        [self showFullShadow:NO];
    }
    return self;
}
- (void)showFullShadow:(BOOL)full {
    self.layer.shadowOffset = CGSizeMake(0, full ? 8 : 4);
    self.layer.shadowRadius = full ? 48 : 24;
    self.layer.shadowColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:full ? 0.16 : 0.06].CGColor;
    self.layer.shadowOpacity = 1;
}
- (void)setTransform:(CGAffineTransform)transform { [super setTransform:transform]; self.prevTransform = self.transform; }
- (void)setConversation:(ICMConversation *)conversation {
    if ([self.conversation isEqual:conversation]) return;
    if ([self.conversation.conversationId isEqualToString:conversation.conversationId]) {
        BOOL equal = [self.conversation.lastPart isEqual:conversation.lastPart];
        _conversation = conversation;
        if (!equal) { [self pulseForNewMessageInSameConversation]; return; }
    } else _conversation = conversation;
    [self.notificationContentView configureFor:conversation.lastPart ticket:conversation.ticket];
}
- (void)pulseForNewMessageInSameConversation {
    NSString *text = [self.notificationContentView participantNameAndTimeLabelTextFor:self.conversation.lastPart];
    [UIView animateWithDuration:0.15 animations:^{
        self.transform = CGAffineTransformMakeScale(1.05, 1.05);
        if (![text isEqualToString:self.notificationContentView.participantNameAndTimeLabel.text]) self.notificationContentView.participantNameAndTimeLabel.alpha = 0;
        [self.notificationContentView setContentAlphaTo:0];
    } completion:^(BOOL finished) {
        (void)finished;
        [self.notificationContentView configureFor:self.conversation.lastPart ticket:self.conversation.ticket];
        [UIView animateWithDuration:0.15 animations:^{
            self.notificationContentView.participantNameAndTimeLabel.alpha = 1;
            [self.notificationContentView setContentAlphaTo:1];
            self.transform = self.prevTransform;
            [self layoutSubviews];
        }];
    }];
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGRect bounds = self.bounds;
    CGFloat radius = self.layer.cornerRadius;
    UIBezierPath *path = [UIBezierPath bezierPathWithRoundedRect:bounds cornerRadius:radius];
    self.layer.shadowPath = path.CGPath;
}
@end
