#pragma once
#import <UIKit/UIKit.h>
@class ICMConversation, ICMConfig, ICMRecoveredChatNotificationContentView;
@interface ICMStackedChatNotificationsViewItem : UIView
@property (nonatomic, strong) ICMConversation *conversation;
@property (nonatomic, strong) ICMRecoveredChatNotificationContentView *notificationContentView;
@property (nonatomic) CGAffineTransform prevTransform;
- (instancetype)initWithConversation:(ICMConversation *)conversation;
- (instancetype)initWithConversation:(ICMConversation *)conversation config:(ICMConfig *)config;
- (void)showFullShadow:(BOOL)full;
- (void)pulseForNewMessageInSameConversation;
@end
