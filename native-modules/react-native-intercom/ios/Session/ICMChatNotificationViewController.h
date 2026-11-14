#pragma once
#import "ICMInAppsViewController.h"
#import "ICMChatNotificationDependencies.h"
@class ICMConfig;
@interface ICMChatNotificationViewController : UIViewController <_TtP18IntercomSDKPrivate28ChatNotificationViewDelegate_, ICMInAppUpdateProtocol, ICMInAppsAnimationProtocol> {
    BOOL _isAnimating;
    ICMConversation *_conversation;
    __weak id<ICMInAppsDelegate> _delegate;
    ICMConfig *_config;
    id _themeProvider;
    ICMRecoveredChatNotificationView *_chatNotificationView;
    NSLayoutConstraint *_chatNotificationViewTopConstraint;
    CGPoint _initialPanPoint;
    CGPoint _notificationViewCenter;
}
@property (nonatomic) BOOL isAnimating;
@property (nonatomic, strong) ICMConversation *conversation;
@property (nonatomic, weak) id<ICMInAppsDelegate> delegate;
@property (nonatomic, strong) ICMConfig *config;
@property (nonatomic, strong) id themeProvider;
@property (nonatomic, strong) ICMRecoveredChatNotificationView *chatNotificationView;
@property (nonatomic, strong) NSLayoutConstraint *chatNotificationViewTopConstraint;
@property (nonatomic) CGPoint initialPanPoint;
@property (nonatomic) CGPoint notificationViewCenter;
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate config:(ICMConfig *)config;
- (instancetype)initWithConversation:(ICMConversation *)conversation delegate:(id<ICMInAppsDelegate>)delegate config:(ICMConfig *)config themeProvider:(id)themeProvider;
- (void)addChatNotificationView;
- (CGFloat)getTopPadding;
- (void)layoutViews;
- (void)pulseForNewMessage;
- (void)animateIn;
- (void)animateHideNotificationView;
- (void)animateShowNotificationView;
- (void)notificationTapped;
- (void)markChatFullAsRead;
- (void)pan:(UIPanGestureRecognizer *)gesture;
@end
