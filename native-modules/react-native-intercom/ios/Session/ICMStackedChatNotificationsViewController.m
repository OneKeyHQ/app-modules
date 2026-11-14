#import "ICMStackedChatNotificationsViewController.h"
#import "ICMSoundPlayer.h"
#import "ICMStackedChatNotificationsView.h"
@implementation ICMStackedChatNotificationsViewController
- (instancetype)initWithConversations:(NSArray *)conversations delegate:(id<ICMInAppsDelegate>)delegate {
    self = [super init];
    if (self) {
        _delegate = delegate;
        [ICMSoundPlayer playSoundForInApp];
        [self addSubviewsForConversations:conversations];
    }
    return self;
}
- (void)addSubviewsForConversations:(NSArray *)conversations {
    self.stackView = [[ICMStackedChatNotificationsView alloc] initWithConversations:conversations delegate:self];
    self.stackView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:self.stackView];
    NSLayoutConstraint *trailing = [self.stackView.trailingAnchor constraintEqualToAnchor:self.view.layoutMarginsGuide.trailingAnchor];
    trailing.priority = 999;
    [NSLayoutConstraint activateConstraints:@[
        [self.stackView.heightAnchor constraintEqualToConstant:100],
        [self.stackView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor constant:-28],
        [self.stackView.centerXAnchor constraintEqualToAnchor:self.view.centerXAnchor],
        [self.stackView.widthAnchor constraintLessThanOrEqualToConstant:398], trailing
    ]];
    if (conversations.count < 2) return;
    self.pillView = [ICMStackedChatNotificationsPillView new];
    [self.pillView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(pillTapped)]];
    self.pillView.alpha = 0;
    self.pillView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:self.pillView];
    [self.pillView.centerXAnchor constraintEqualToAnchor:self.stackView.centerXAnchor].active = YES;
    [self.pillView.bottomAnchor constraintEqualToAnchor:self.stackView.topAnchor constant:-11].active = YES;
}
- (NSArray *)conversations { return self.stackView.conversations; }
- (void)setConversations:(NSArray *)conversations {
    if (![self.conversations containsObject:conversations.firstObject]) [ICMSoundPlayer playSoundForInApp];
    self.stackView.conversations = conversations;
    [self updatePillCount];
}
- (void)viewDidAppear:(BOOL)animated { [super viewDidAppear:animated]; [self updatePillCount]; }
- (void)pillTapped { [self.delegate showInbox]; }
- (void)updatePillCount { [self setPillCount:(NSInteger)(self.stackView.conversations.count - 3)]; }
- (void)setPillCount:(NSInteger)count {
    if (count >= 1) self.pillView.count = count;
    [UIView animateWithDuration:0.3 animations:^{ self.pillView.alpha = count > 0 ? 1 : 0; } completion:^(BOOL finished) {
        (void)finished;
        self.pillView.count = count;
    }];
}
- (void)frontNotificationItemChanged:(ICMConversation *)conversation {
    if ([self.delegate respondsToSelector:@selector(conversationShown:asFull:)]) [self.delegate conversationShown:conversation asFull:NO];
}
- (void)notificationItemTappedForConversation:(ICMConversation *)conversation {
    [self.delegate presentConversation:conversation fromFull:NO];
    [self setPillCount:(NSInteger)((NSUInteger)self.pillView.count - 1)];
}
- (void)notificationItemDismissedForConversation:(ICMConversation *)conversation remainingCount:(NSUInteger)count {
    [self.delegate dismissConversation:conversation fromFull:NO];
    if (count == 0) [self.delegate hideInApp:self];
    [self updatePillCount];
}
- (void)hideNotificationItems { [self.delegate hideInApp:self]; }
- (void)animateOutWithCompletion:(void (^)(void))completion { if (completion) completion(); }
- (void)conversationMarkedAsRead:(NSString *)identifier {
    NSArray *conversations = self.conversations;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"conversationId != %@", identifier];
    self.conversations = [conversations filteredArrayUsingPredicate:predicate];
}
@end
