#import "ICMDataManager.h"
#import "ICMInboxResponseCompletion.h"
@class ICMConversationPart, ICMComposerState, ICMRecoveredMessagesEmptyState;
@interface ICMDataManager (Conversations)
- (void)updateConversationsBefore:(NSDate *)before success:(void (^)(NSArray *, BOOL))success failure:(void (^)(NSError *))failure;
- (void)updateConversationsWithSuccess:(ICMInboxConversationsCompletion)success failure:(void (^)(NSError *))failure;
- (NSArray *)mergeExistingConversations:(NSArray *)existing withNewConversations:(NSArray *)incoming;
- (ICMComposerState *)mergeCachedComposerState:(ICMComposerState *)existing withNewComposerState:(ICMComposerState *)incoming;
- (NSArray *)mergeExistingConversationParts:(NSArray *)existing withNewConversationParts:(NSArray *)incoming;
- (NSUInteger)getIndexOfConversation:(ICMConversation *)conversation inConversations:(NSArray *)conversations;
- (NSUInteger)getIndexOfConversationPart:(ICMConversationPart *)part inConversationParts:(NSArray *)parts;
- (NSArray *)sortConversations:(NSArray *)conversations;
- (NSArray *)sortConversationParts:(NSArray *)parts;
- (ICMConversation *)findExistingConversationWithId:(NSString *)identifier;
- (void)getUnreadConversationsIfNeeded;
- (void)getUnreadConversations;
- (void)updateInbox;
- (void)triggerPoll;
- (BOOL)isConversationCachedLocally:(NSString *)identifier;
- (NSMutableArray *)sortAllConversationsByCreatedAtDate;
- (void)updateWithConversation:(ICMConversation *)conversation;
- (void)removePartsWithNoTimestampFromConversation:(ICMConversation *)conversation;
- (void)clearUndeliveredConversations;
- (ICMConversation *)matchedNewConversation;
- (void)addParticipantsToParts:(NSArray *)parts from:(NSArray *)participants;
- (void)updateConversation:(ICMConversation *)conversation fromComposerSuggestions:(id)suggestions;
- (id)fallbackHeader;
- (NSArray *)getRecentConversations:(NSInteger)limit;
@end
