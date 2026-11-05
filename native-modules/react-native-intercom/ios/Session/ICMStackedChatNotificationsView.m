#import "ICMStackedChatNotificationsView.h"
#import "ICMConversation.h"
#import <math.h>
@implementation ICMStackedChatNotificationsView
@synthesize conversations = _conversations;
@synthesize frontView = _frontView;
- (instancetype)initWithConversations:(NSArray *)conversations delegate:(id<ICMChatNotificationItemActionDelegate>)delegate {
    self = [super initWithFrame:CGRectZero];
    if (self) { _delegate = delegate; _conversations = conversations; _didCreateItems = NO; }
    return self;
}
- (void)layoutSubviews { [super layoutSubviews]; if (!self.didCreateItems) [self updateNotificationViews]; }
- (void)setFrontView:(ICMStackedChatNotificationsViewItem *)frontView {
    _frontView = frontView;
    if (frontView.conversation) [self.delegate frontNotificationItemChanged:frontView.conversation];
    [_frontView showFullShadow:YES];
}
- (void)setConversations:(NSArray *)conversations {
    _conversations = conversations;
    if (self.isDismissAnimating) return;
    if (self.frontView) {
        ICMConversation *first = conversations.firstObject;
        if (![first.conversationId isEqualToString:self.frontView.conversation.conversationId]) {
            ICMStackedChatNotificationsViewItem *oldBack = self.backView;
            [UIView animateWithDuration:0.1 animations:^{ oldBack.alpha = 0; } completion:^(BOOL finished) { (void)finished; [oldBack removeFromSuperview]; }];
            self.backView = self.middleView;
            self.middleView = self.frontView;
            [self bringSubviewToFront:self.reusableView];
            self.frontView = self.reusableView;
            [self.middleView showFullShadow:NO];
        }
    } else if (self.middleView) {
        self.frontView = self.middleView;
        self.middleView = self.backView;
        [self sendSubviewToBack:self.reusableView];
        self.backView = self.reusableView;
        self.backView.transform = CGAffineTransformMakeScale(0.86, 0.86);
    }
    self.reusableView = nil;
    [self prepareReusableView];
    [self updateNotificationViews];
}
- (void)updateNotificationViews {
    [self removeGestureRecognizersFromAllItems];
    BOOL newFront = NO, newMiddle = NO, newBack = NO;
    if (self.conversations.count > 0) {
        if (self.frontView) self.frontView.conversation = self.conversations[0];
        else { self.frontView = [self addNotificationItemForConversation:self.conversations[0] shouldAddSubviewInFront:YES]; self.frontView.alpha = 0; newFront = YES; }
    } else { [self.frontView removeFromSuperview]; self.frontView = nil; }
    if (self.conversations.count > 1) {
        if (self.middleView) self.middleView.conversation = self.conversations[1];
        else { self.middleView = [self addNotificationItemForConversation:self.conversations[1] shouldAddSubviewInFront:NO]; self.middleView.alpha = 0; newMiddle = YES; }
    } else { [self.middleView removeFromSuperview]; self.middleView = nil; }
    if (self.conversations.count > 2) {
        if (self.backView) self.backView.conversation = self.conversations[2];
        else { self.backView = [self addNotificationItemForConversation:self.conversations[2] shouldAddSubviewInFront:NO]; self.backView.alpha = 0; newBack = YES; }
    } else { [self.backView removeFromSuperview]; self.backView = nil; }
    self.frontView.transform = CGAffineTransformIdentity;
    [self.frontView addGestureRecognizer:[[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(panFrontView:)]];
    [self.frontView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapFrontView)]];
    self.middleView.transform = CGAffineTransformMakeScale(0.93, 0.93);
    self.backView.transform = CGAffineTransformMakeScale(0.86, 0.86);
    self.frontViewLeadingConstraint = [self getLeadingConstraintOfItem:self.frontView];
    NSLayoutConstraint *frontTemporary = nil, *middleTemporary = nil, *backTemporary = nil;
    NSLayoutConstraint *frontFinal = nil, *middleFinal = nil, *backFinal = nil;
    if (self.frontView) {
        [self removeVerticalConstraintsOfItem:self.frontView];
        if (newFront) { frontTemporary = [self.frontView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:150]; frontTemporary.active = YES; }
        frontFinal = [self.frontView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor];
    }
    if (self.middleView) {
        [self removeVerticalConstraintsOfItem:self.middleView];
        if (newMiddle) { middleTemporary = [self.middleView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:150]; middleTemporary.active = YES; }
        middleFinal = [self.middleView.topAnchor constraintEqualToAnchor:self.frontView.topAnchor constant:-15];
    }
    if (self.backView) {
        [self removeVerticalConstraintsOfItem:self.backView];
        if (newBack) { backTemporary = [self.backView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:150]; backTemporary.active = YES; }
        backFinal = [self.backView.topAnchor constraintEqualToAnchor:self.middleView.topAnchor constant:-15];
    }
    if (!self.didCreateItems) { self.didCreateItems = YES; [self layoutIfNeeded]; }
    if (self.frontView) [UIView animateWithDuration:0.5 delay:0 usingSpringWithDamping:0.8 initialSpringVelocity:0.3 options:0 animations:^{
        frontTemporary.active = NO; frontFinal.active = YES; self.frontView.alpha = 1; [self layoutIfNeeded];
    } completion:nil];
    if (self.middleView) [UIView animateWithDuration:0.5 delay:0.05 usingSpringWithDamping:0.8 initialSpringVelocity:0.3 options:0 animations:^{
        middleTemporary.active = NO; middleFinal.active = YES; self.middleView.alpha = 1; [self layoutIfNeeded];
    } completion:nil];
    if (self.backView) [UIView animateWithDuration:0.5 delay:0.1 usingSpringWithDamping:0.8 initialSpringVelocity:0.3 options:0 animations:^{
        backTemporary.active = NO; backFinal.active = YES; self.backView.alpha = 1; [self layoutIfNeeded];
    } completion:nil];
}
- (void)removeGestureRecognizersFromAllItems {
    NSMutableArray *items = [NSMutableArray new];
    if (self.frontView) [items addObject:self.frontView];
    if (self.middleView) [items addObject:self.middleView];
    if (self.backView) [items addObject:self.backView];
    if (self.reusableView) [items addObject:self.reusableView];
    for (UIView *item in items) for (UIGestureRecognizer *gesture in item.gestureRecognizers) [item removeGestureRecognizer:gesture];
}
- (ICMStackedChatNotificationsViewItem *)addNotificationItemForConversation:(ICMConversation *)conversation shouldAddSubviewInFront:(BOOL)front {
    ICMStackedChatNotificationsViewItem *item = [[ICMStackedChatNotificationsViewItem alloc] initWithConversation:conversation];
    item.translatesAutoresizingMaskIntoConstraints = NO;
    if (front) [self addSubview:item]; else [self insertSubview:item atIndex:0];
    [NSLayoutConstraint activateConstraints:@[[item.widthAnchor constraintEqualToAnchor:self.widthAnchor], [item.leadingAnchor constraintEqualToAnchor:self.leadingAnchor]]];
    return item;
}
- (void)prepareReusableView {
    if (self.reusableView) return;
    if (self.conversations.count == 0) return;
    self.reusableView = [self addNotificationItemForConversation:self.conversations[0] shouldAddSubviewInFront:NO];
    self.reusableView.alpha = 0;
    [self.reusableView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:150].active = YES;
}
- (void)panFrontView:(UIPanGestureRecognizer *)gesture {
    CGFloat x = [gesture locationInView:self].x;
    CGFloat y = CGRectGetMidY(gesture.view.frame);
    UIGestureRecognizerState state = gesture.state;
    if (state == UIGestureRecognizerStateBegan) {
        self.initialPanPoint = CGPointMake(x, y);
        self.initialCenter = gesture.view.center;
        [UIView animateWithDuration:0.15 animations:^{ gesture.view.transform = CGAffineTransformMakeScale(0.95, 0.95); }];
    } else if (state == UIGestureRecognizerStateChanged) {
        CGFloat first = self.initialCenter.x;
        CGFloat initial = self.initialPanPoint.x;
        CGFloat second = self.initialCenter.x;
        CGFloat width = CGRectGetWidth(gesture.view.bounds);
        CGFloat third = self.initialCenter.x;
        if (self.frontViewLeadingConstraint) {
            CGFloat delta = x - ((first + initial) - second);
            CGFloat shifted = third + (delta * width * 0.4) / fma(delta, 0.4, width);
            self.frontViewLeadingConstraint.constant = shifted - self.bounds.size.width * 0.5;
            [self layoutIfNeeded];
        }
    } else {
        CGFloat velocity = [gesture velocityInView:self].x;
        if (fabs(velocity) > 800) [self dismissFrontViewWithXVelocity:velocity];
        else [self rePositionFrontViewToOriginalPosition];
    }
}
- (void)dismissFrontViewWithXVelocity:(CGFloat)velocity {
    ICMConversation *conversation = self.frontView.conversation;
    NSUInteger count = self.conversations.count;
    ICMStackedChatNotificationsViewItem *front = self.frontView;
    CGFloat target = velocity > 0 ? CGRectGetWidth(self.bounds) + CGRectGetWidth(self.frontView.bounds) : -CGRectGetWidth(self.frontView.bounds);
    NSUInteger remaining = count - 1;
    CGFloat distance = self.frontView.center.x - target;
    if (distance < 0) distance = -distance;
    CGFloat duration = distance / velocity;
    if (duration < 0) duration = -duration;
    self.frontViewLeadingConstraint.constant = target - self.bounds.size.width;
    self.isDismissAnimating = YES;
    [UIView animateWithDuration:duration delay:0 options:4 animations:^{ front.alpha = 0; [self layoutIfNeeded]; } completion:^(BOOL finished) {
        (void)finished;
        [self.delegate notificationItemDismissedForConversation:conversation remainingCount:remaining];
        [front removeFromSuperview];
        self.frontView = nil;
        self.initialPanPoint = CGPointZero;
        self.isDismissAnimating = NO;
        [self applyPendingConversationRemoval:conversation];
    }];
}
- (void)rePositionFrontViewToOriginalPosition {
    [UIView animateWithDuration:0.3 delay:0 usingSpringWithDamping:0.8 initialSpringVelocity:0.3 options:0 animations:^{
        self.frontView.transform = CGAffineTransformIdentity;
        self.frontViewLeadingConstraint.constant = 0;
        [self layoutIfNeeded];
    } completion:^(BOOL finished) { (void)finished; self.initialPanPoint = CGPointZero; }];
}
- (void)tapFrontView {
    ICMStackedChatNotificationsViewItem *front = self.frontView;
    [UIView animateWithDuration:0.1 animations:^{ front.transform = CGAffineTransformScale(CGAffineTransformIdentity, 0.95, 0.95); } completion:^(BOOL finished) {
        (void)finished;
        [self.delegate notificationItemTappedForConversation:front.conversation];
        [UIView animateWithDuration:0.1 animations:^{ front.alpha = 0; } completion:^(BOOL done) { (void)done; [front removeFromSuperview]; }];
        if (front == self.frontView) { self.frontView = nil; [self applyPendingConversationRemoval:front.conversation]; }
    }];
}
- (void)applyPendingConversationRemoval:(ICMConversation *)conversation {
    NSArray *conversations = self.conversations;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"conversationId != %@", conversation.conversationId];
    self.conversations = [conversations filteredArrayUsingPredicate:predicate];
    if (!self.frontView) [self.delegate hideNotificationItems];
}
- (NSLayoutConstraint *)getLeadingConstraintOfItem:(UIView *)item {
    for (NSLayoutConstraint *constraint in self.constraints) {
        if (constraint.firstAttribute == NSLayoutAttributeLeading && constraint.secondAttribute == NSLayoutAttributeLeading && constraint.firstItem == item && constraint.secondItem == self) return constraint;
    }
    return nil;
}
- (void)removeVerticalConstraintsOfItem:(UIView *)item {
    for (NSLayoutConstraint *constraint in self.constraints) {
        if (constraint.firstItem == item) {
            if (constraint.firstAttribute == NSLayoutAttributeTop || constraint.firstAttribute == NSLayoutAttributeBottom) constraint.active = NO;
        } else if (constraint.secondItem == item) {
            if (constraint.secondAttribute == NSLayoutAttributeTop || constraint.secondAttribute == NSLayoutAttributeBottom) constraint.active = NO;
        }
    }
}
@end
