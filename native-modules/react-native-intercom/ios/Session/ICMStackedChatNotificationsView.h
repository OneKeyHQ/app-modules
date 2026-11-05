#pragma once
#import "ICMChatNotificationDependencies.h"
#import "ICMStackedChatNotificationsViewItem.h"
@interface ICMStackedChatNotificationsView : UIView {
    BOOL _didCreateItems;
    BOOL _isDismissAnimating;
    NSArray *_conversations;
    ICMStackedChatNotificationsViewItem *_frontView;
    ICMStackedChatNotificationsViewItem *_middleView;
    ICMStackedChatNotificationsViewItem *_backView;
    ICMStackedChatNotificationsViewItem *_reusableView;
    NSLayoutConstraint *_frontViewLeadingConstraint;
    __weak id<ICMChatNotificationItemActionDelegate> _delegate;
    CGPoint _initialPanPoint;
    CGPoint _initialCenter;
}
@property (nonatomic) BOOL didCreateItems;
@property (nonatomic) BOOL isDismissAnimating;
@property (nonatomic, strong) NSArray *conversations;
@property (nonatomic, strong) ICMStackedChatNotificationsViewItem *frontView;
@property (nonatomic, strong) ICMStackedChatNotificationsViewItem *middleView;
@property (nonatomic, strong) ICMStackedChatNotificationsViewItem *backView;
@property (nonatomic, strong) ICMStackedChatNotificationsViewItem *reusableView;
@property (nonatomic, strong) NSLayoutConstraint *frontViewLeadingConstraint;
@property (nonatomic, weak) id<ICMChatNotificationItemActionDelegate> delegate;
@property (nonatomic) CGPoint initialPanPoint;
@property (nonatomic) CGPoint initialCenter;
- (instancetype)initWithConversations:(NSArray *)conversations delegate:(id<ICMChatNotificationItemActionDelegate>)delegate;
- (void)updateNotificationViews;
- (void)removeGestureRecognizersFromAllItems;
- (ICMStackedChatNotificationsViewItem *)addNotificationItemForConversation:(ICMConversation *)conversation shouldAddSubviewInFront:(BOOL)front;
- (void)prepareReusableView;
- (void)panFrontView:(UIPanGestureRecognizer *)gesture;
- (void)dismissFrontViewWithXVelocity:(CGFloat)velocity;
- (void)rePositionFrontViewToOriginalPosition;
- (void)tapFrontView;
- (void)applyPendingConversationRemoval:(ICMConversation *)conversation;
- (NSLayoutConstraint *)getLeadingConstraintOfItem:(UIView *)item;
- (void)removeVerticalConstraintsOfItem:(UIView *)item;
@end
