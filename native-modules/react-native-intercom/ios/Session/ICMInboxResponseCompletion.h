#import <Foundation/Foundation.h>
@class ICMConversation, ICMRecoveredMessagesEmptyState;
typedef void (^ICMInboxConversationsCompletion)(NSArray<ICMConversation *> * _Nonnull conversations, ICMRecoveredMessagesEmptyState * _Nonnull emptyState);
