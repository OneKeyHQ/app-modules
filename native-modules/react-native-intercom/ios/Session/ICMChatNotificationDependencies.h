#pragma once
#import <UIKit/UIKit.h>
@class ICMConversation, ICMConversationPart;
@protocol ICMChatNotificationItemActionDelegate <NSObject>
- (void)frontNotificationItemChanged:(ICMConversation *)conversation;
- (void)notificationItemTappedForConversation:(ICMConversation *)conversation;
- (void)notificationItemDismissedForConversation:(ICMConversation *)conversation remainingCount:(NSUInteger)count;
- (void)hideNotificationItems;
@end
__attribute__((swift_name("ChatNotificationViewDelegate")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@protocol _TtP18IntercomSDKPrivate28ChatNotificationViewDelegate_
- (void)quickReplyTapped;
@end
// Original concrete native views; implementations remain required link inputs.
@class ICMStackedChatNotificationsView;
#import "ICMStackedChatNotificationsPillView.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate20ChatNotificationView")))
__attribute__((swift_name("ChatNotificationView")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredChatNotificationView : UIView
- (instancetype)initWithConversationPart:(ICMConversationPart *)part ticket:(id)ticket width:(CGFloat)width delegate:(id<_TtP18IntercomSDKPrivate28ChatNotificationViewDelegate_>)delegate themeProvider:(id)themeProvider;
- (void)loadAvatarFor:(ICMConversationPart *)part;
- (NSString *)getParticipantNameAndTimeLabelTextForConversationPart:(ICMConversationPart *)part;
@property (nonatomic, readonly) NSString *participantNameAndTimeLabelText;
- (void)setParticipantNameAndTimeLabelAlpha:(CGFloat)alpha;
- (void)setNotificationContentAlpha:(CGFloat)alpha;
- (void)updateWithConversationPart:(ICMConversationPart *)part ticket:(id)ticket;
@end
