#import "ICMInAppsViewController.h"
#import "ICMInAppsDependencies.h"
#import "ICMInAppPreloader.h"
#import "ICMConversation.h"
#import "ICMConversationPart.h"
#import "ICMConfig.h"
#import "ICMPresentationManager.h"
#import "ICMMetric.h"
#import "ICMMetricEvent.h"
#import "ICMSoundPlayer.h"
@implementation ICMInAppsViewController
@synthesize conversations = _conversations, hostAppState = _hostAppState, delegate = _delegate;
@synthesize snippetsInAppViewController = _snippetsInAppViewController, fullScreenInAppViewController = _fullScreenInAppViewController;
- (instancetype)initWithConversations:(NSArray *)conversations delegate:(id<ICMInAppsDelegate>)delegate hostAppState:(ICMHostAppState *)state {
    if ((self = [super init])) { _delegate = delegate; [self setConversations:conversations]; self.hostAppState = state; }
    return self;
}
- (UIViewController<ICMInAppUpdateProtocol, ICMInAppsAnimationProtocol> *)snippetsViewControllerForConversations:(NSArray *)conversations {
    if ([self.snippetsInAppViewController isKindOfClass:[ICMStackedChatNotificationsViewController class]] && conversations.count != 0) {
        ICMStackedChatNotificationsViewController *current = (id)self.snippetsInAppViewController;
        current.conversations = conversations;
        return current;
    }
    if (conversations.count >= 2) return [[ICMStackedChatNotificationsViewController alloc] initWithConversations:conversations delegate:self];
    if ([self.snippetsInAppViewController isKindOfClass:[ICMChatNotificationViewController class]]) {
        ICMChatNotificationViewController *current = (id)self.snippetsInAppViewController;
        if ([current.conversation.conversationId isEqualToString:[conversations.firstObject conversationId]]) {
            current.conversation = conversations.firstObject;
            return current;
        }
    }
    if (conversations.count != 0) {
        NSInteger style = [[conversations.firstObject lastPart] partStyle];
        id ticket = [conversations.firstObject ticket];
        if ((ticket || style == 3 || style == 0) && [[conversations.firstObject conversationParts] count] != 0) {
            return [[ICMChatNotificationViewController alloc] initWithConversation:conversations.firstObject delegate:self config:[ICMConfig sharedConfig]];
        }
        if ([self.delegate respondsToSelector:@selector(isIntercomPresented)] && [self.delegate isIntercomPresented]) {
            return [[ICMStackedChatNotificationsViewController alloc] initWithConversations:conversations delegate:self];
        }
        if ([[conversations.firstObject lastPart] deliveryOption] != 1) return [[ICMStackedChatNotificationsViewController alloc] initWithConversations:conversations delegate:self];
        return nil;
    }
    if (![self.delegate respondsToSelector:@selector(shouldShowLauncher)] || ![self.delegate shouldShowLauncher]) return nil;
    if (self.fullScreenInAppViewController) return nil;
    if ([self.snippetsInAppViewController isKindOfClass:[ICMLauncherViewController class]]) return self.snippetsInAppViewController;
    return [[ICMLauncherViewController alloc] initWithDelegate:self];
}
- (UIViewController<ICMFullScreenInAppProtocol> *)fullScreenViewControllerForConversations:(NSArray *)conversations {
    if (conversations.count != 1) return nil;
    ICMConversation *conversation = conversations.firstObject;
    if ([self.delegate respondsToSelector:@selector(isIntercomPresented)] && [self.delegate isIntercomPresented]) return nil;
    if (conversation.lastPart.deliveryOption != 1) return nil;
    if (conversation.lastPart.partStyle == 2) return [[ICMPostViewController alloc] initWithConversation:conversation delegate:self isInApp:YES];
    if (conversation.lastPart.partStyle == 1) return [[ICMNoteViewController alloc] initWithConversation:conversation delegate:self];
    return nil;
}
- (BOOL)shouldHandleTouchOnView:(UIView *)view { return view != self.view && view != self.snippetsInAppViewController.view; }
- (void)tearDownInApps {
    [self updateFullScreenInApp:nil fromSnippet:NO];
    self.conversations = @[];
    if ([self.snippetsInAppViewController isKindOfClass:[ICMLauncherViewController class]]) return;
    [self updateSnippetsInApp:nil];
}
- (void)tearDown { [self updateFullScreenInApp:nil fromSnippet:NO]; _conversations = nil; [self updateSnippetsInApp:nil]; }
- (UIStatusBarStyle)preferredStatusBarStyle {
    if (self.fullScreenInAppViewController) return self.fullScreenInAppViewController.preferredStatusBarStyle;
    return self.hostAppState.preferredStatusBarStyle;
}
- (void)updateLauncherVisibility {
    if (self.fullScreenInAppViewController) return;
    if (!self.snippetsInAppViewController || [self.snippetsInAppViewController isKindOfClass:[ICMLauncherViewController class]]) {
        [self updateSnippetsInApp:[self snippetsViewControllerForConversations:@[]]];
    }
}
- (BOOL)isShowingLauncherOrInApp { return self.snippetsInAppViewController != nil || self.fullScreenInAppViewController != nil; }
- (void)setConversations:(NSArray *)conversations { [self setConversations:conversations completion:nil]; }
- (void)setConversations:(NSArray *)conversations completion:(void (^)(void))completion {
    if ([conversations isEqualToArray:_conversations]) { if (completion) completion(); return; }
    _conversations = conversations;
    NSArray *snapshot = _conversations;
    [[self class] prepareConversationsForLayout:conversations completion:^(BOOL ready) {
        (void)ready;
        if (snapshot != self.conversations) return;
        [self updateSnippetsInApp:[self snippetsViewControllerForConversations:conversations]];
        if (!self.fullScreenInAppViewController) [self updateFullScreenInApp:[self fullScreenViewControllerForConversations:conversations] fromSnippet:NO];
        if (completion) completion();
    }];
}
+ (void)prepareConversationsForLayout:(NSArray *)conversations completion:(void (^)(BOOL))completion {
    if (conversations.count != 0) [ICMInAppPreloader prepareConversationForInApp:conversations.firstObject completion:completion];
    else if (completion) completion(YES);
}
- (void)updateSnippetsInApp:(UIViewController<ICMInAppUpdateProtocol, ICMInAppsAnimationProtocol> *)controller {
    if (controller == self.snippetsInAppViewController) return;
    UIViewController<ICMInAppUpdateProtocol, ICMInAppsAnimationProtocol> *old = self.snippetsInAppViewController;
    self.snippetsInAppViewController = controller;
    if (old) {
        [old animateOutWithCompletion:^{
            [old willMoveToParentViewController:nil];
            [old.view removeFromSuperview];
            [old removeFromParentViewController];
            if (!self.snippetsInAppViewController && !self.fullScreenInAppViewController && self->_conversations.count == 0) {
                if ([self.delegate respondsToSelector:@selector(didHideInApps)]) [self.delegate didHideInApps];
            }
        }];
    }
    self.snippetsInAppViewController = controller;
    if (self.snippetsInAppViewController) {
        [self addChildViewController:self.snippetsInAppViewController];
        CGRect bounds = self.view.bounds;
        self.snippetsInAppViewController.view.frame = bounds;
        [self.view insertSubview:self.snippetsInAppViewController.view atIndex:0];
        [self.snippetsInAppViewController didMoveToParentViewController:self];
    }
}
- (void)updateFullScreenInApp:(UIViewController<ICMFullScreenInAppProtocol> *)controller fromSnippet:(BOOL)snippet {
    if (controller == self.fullScreenInAppViewController) return;
    if (self.fullScreenInAppViewController) {
        [self.fullScreenInAppViewController willMoveToParentViewController:nil];
        [self.fullScreenInAppViewController.view removeFromSuperview];
        [self.fullScreenInAppViewController removeFromParentViewController];
    }
    self.fullScreenInAppViewController = controller;
    if (self.fullScreenInAppViewController) {
        [self addChildViewController:self.fullScreenInAppViewController];
        CGRect bounds = self.view.bounds;
        self.fullScreenInAppViewController.view.frame = bounds;
        [self.view addSubview:self.fullScreenInAppViewController.view];
        [self.fullScreenInAppViewController didMoveToParentViewController:self];
        if (!snippet) {
            [self addReceivedEventForConversation:controller.conversation asFull:YES];
            [self addViewedEventForConversation:controller.conversation asFull:YES];
        }
        if ([self.delegate respondsToSelector:@selector(didShowFullScreenInApp)]) [self.delegate didShowFullScreenInApp];
    } else if ([self.delegate respondsToSelector:@selector(didHideFullScreenInApp)]) [self.delegate didHideFullScreenInApp];
}
- (void)viewWillLayoutSubviews {
    [super viewWillLayoutSubviews];
    CGRect bounds = self.view.bounds;
    self.fullScreenInAppViewController.view.frame = bounds;
    CGFloat width = CGRectGetWidth(self.view.bounds);
    CGFloat height = CGRectGetHeight(self.view.bounds);
    CGFloat padding = [ICMPresentationManager sharedInstance].inAppBottomPadding;
    CGFloat bottom = self.view.safeAreaInsets.bottom;
    self.snippetsInAppViewController.view.frame = CGRectMake(0, 0, width, height - (padding + bottom));
}
- (void)bottomPaddingUpdated {
    [UIView animateWithDuration:0.3 animations:^{ [self.view setNeedsLayout]; [self.view layoutIfNeeded]; }];
}
+ (NSDictionary *)inAppMetricInfoForConversation:(ICMConversation *)conversation {
    if (!conversation.lastPart) return @{};
    ICMConversationPart *part = conversation.lastPart;
    NSDictionary *metadata = @{@"conversation":conversation, @"conversation_part":part};
    if (part.isInitialMessage) {
        NSString *type;
        if (part.partStyle == 1) type = @"note";
        else if (part.partStyle == 2) type = @"post";
        else type = @"chat";
        NSMutableDictionary *result = [metadata mutableCopy];
        result[@"message_type"] = type;
        return result;
    }
    return metadata;
}
- (void)addReceivedEventForConversation:(ICMConversation *)conversation asFull:(BOOL)full {
    ICMConversationPart *part = conversation.conversationParts.lastObject;
    if (!part) return;
    NSMutableDictionary *metadata = [[[self class] inAppMetricInfoForConversation:conversation] mutableCopy];
    if (conversation.lastPart && !conversation.lastPart.isInitialMessage) {
        NSDictionary *details = @{@"is_attachment":@(conversation.lastPart.hasAttachments), @"is_sticker":@NO,
                                  @"has_article_card":@(conversation.lastPart.hasArticleCard)};
        [metadata addEntriesFromDictionary:details];
    }
    ICMMetric *metric = [ICMMetric sharedInstance];
    NSString *object = part.isInitialMessage ? @"message" : @"reply";
    NSString *place = [self.delegate isIntercomPresented] ? @"messenger" : @"in_app";
    NSString *context = full ? @"from_full" : @"from_snippet";
    [metric addEvent:[ICMMetricEvent eventWithAction:@"received" object:object place:place context:context additionalInfo:metadata]];
}
- (void)addViewedEventForConversation:(ICMConversation *)conversation asFull:(BOOL)full {
    ICMConversationPart *part = conversation.conversationParts.lastObject;
    if (!part) return;
    ICMMetric *metric = [ICMMetric sharedInstance];
    NSString *object = part.isInitialMessage ? @"message" : @"reply";
    NSString *place = [self.delegate isIntercomPresented] ? @"messenger" : @"in_app";
    NSString *context = full ? @"from_full" : @"from_snippet";
    NSDictionary *metadata = [[self class] inAppMetricInfoForConversation:conversation];
    [metric addEvent:[ICMMetricEvent eventWithAction:@"viewed" object:object place:place context:context additionalInfo:metadata]];
}
- (void)conversationShown:(ICMConversation *)conversation asFull:(BOOL)full {
    ICMConversationPart *part = conversation.conversationParts.lastObject;
    if (!full) [self addReceivedEventForConversation:conversation asFull:NO];
    else if (part.partStyle == 0) { [self addReceivedEventForConversation:conversation asFull:YES]; [self addViewedEventForConversation:conversation asFull:YES]; }
}
- (void)presentConversation:(ICMConversation *)conversation fromFull:(BOOL)full {
    if (!full) [self addViewedEventForConversation:conversation asFull:NO];
    ICMConversationPart *part = conversation.conversationParts.lastObject;
    if (!full && (part.partStyle == 2 || part.partStyle == 1)) {
        part.deliveryOption = 1;
        [self updateFullScreenInApp:[self fullScreenViewControllerForConversations:@[conversation]] fromSnippet:YES];
        [ICMSoundPlayer playSoundForInApp];
        return;
    }
    [self.delegate presentConversation:conversation fromFull:full];
    ICMMetric *metric = [ICMMetric sharedInstance];
    [metric addEvent:[ICMMetricEvent eventWithAction:@"opened" object:@"conversation" place:@"messenger" context:full ? @"from_full" : @"from_snippet" additionalInfo:@{@"conversation":conversation, @"conversation_part":part}]];
}
- (void)dismissConversation:(ICMConversation *)conversation fromFull:(BOOL)full {
    [self.delegate dismissConversation:conversation fromFull:full];
    ICMConversationPart *part = conversation.lastPart;
    NSString *action = full ? @"closed" : @"dismissed";
    NSString *context = full ? @"from_full" : @"from_snippet";
    NSString *object = part.isInitialMessage ? @"message" : @"reply";
    ICMMetric *metric = [ICMMetric sharedInstance];
    [metric addEvent:[ICMMetricEvent eventWithAction:action object:object place:@"in_app" context:context additionalInfo:@{@"conversation":conversation, @"conversation_part":part}]];
}
- (void)hideInApp:(id)controller {
    if (controller == self.snippetsInAppViewController) [self updateSnippetsInApp:[self snippetsViewControllerForConversations:@[]]];
    if (controller == self.fullScreenInAppViewController) {
        [self updateFullScreenInApp:nil fromSnippet:NO];
        [UIView animateWithDuration:0.3 animations:^{ [self setNeedsStatusBarAppearanceUpdate]; }];
    }
    if (!self.snippetsInAppViewController && !self.fullScreenInAppViewController) [self.delegate hideInApp:self];
}
- (void)showInbox { [self.delegate showInbox]; }
- (void)launcherTapped { [self.delegate launcherTapped]; }
- (NSUInteger)state {
    if (self.fullScreenInAppViewController) return 3;
    if (!self.snippetsInAppViewController) return 0;
    if ([self.snippetsInAppViewController isKindOfClass:[ICMStackedChatNotificationsViewController class]] && [(ICMStackedChatNotificationsViewController *)self.snippetsInAppViewController conversations].count > 1) return 2;
    if ([self.snippetsInAppViewController isKindOfClass:[ICMChatNotificationViewController class]]) {
        if ([(ICMChatNotificationViewController *)self.snippetsInAppViewController conversation].lastPart.deliveryOption == 1) return 3;
    }
    return 1;
}
- (void)conversationMarkedAsRead:(NSString *)identifier { [self.snippetsInAppViewController conversationMarkedAsRead:identifier]; }
- (void)hideLauncherWithAnimation {
    if ([self.snippetsInAppViewController isKindOfClass:[ICMLauncherViewController class]]) [self hideInApp:self.snippetsInAppViewController];
}
@end
