#import <Foundation/Foundation.h>
@class ICMConversation, ICMUnreadManager, ICMRecoveredTicket, ICMRecoveredConversationHeader;
@class ICMRecoveredNewConversationCTAResponse, ICMRecoveredNewConversationCard, ICMRecoveredPoweredBy, ICMRecoveredPrivacyPolicyNotice;
@protocol ICMDataManagerProtocol
- (void)updateWithConversation:(ICMConversation *)conversation;
- (NSArray *)getRecentConversations:(NSInteger)count;
- (ICMConversation *)findExistingConversationWithId:(NSString *)identifier;
- (void)fetchTicketsFromPage:(NSInteger)page success:(void (^)(NSArray *))success failure:(void (^)(NSError *))failure;
- (void)fetchTicketDetailsForTicketWithId:(NSString *)identifier success:(void (^)(ICMRecoveredTicket *))success failure:(void (^)(NSError *))failure;
- (void)markTicketAsRead:(NSString *)identifier;
- (void)clearTickets;
- (void)updateWithTicket:(ICMRecoveredTicket *)ticket;
- (NSArray *)getRecentTickets;
- (ICMConversation *)matchedNewConversation;
- (void)refreshOpenResponseWithSuccess:(void (^)(void))success error:(void (^)(NSError *))failure;
- (void)clearOpenResponseData;
- (void)clearUndeliveredConversations;
- (ICMRecoveredConversationHeader *)fallbackHeader;
@property (nonatomic, readonly) NSMutableArray *tickets;
@property (nonatomic, strong) ICMUnreadManager *unreadManager;
@property (nonatomic) BOOL helpCenterArticleViewed;
@property (nonatomic, strong) NSString *conversationToMark;
@property (nonatomic, strong) NSString *ticketToMark;
@property (nonatomic, readonly) NSNumber *nextTicketsPageToFetch;
@property (nonatomic, readonly) ICMRecoveredNewConversationCTAResponse *conversationCTA;
@property (nonatomic, readonly) ICMRecoveredNewConversationCard *conversationCard;
@property (nonatomic, strong) NSString *openRequestId;
@property (nonatomic, strong) ICMRecoveredPoweredBy *poweredBy;
@property (nonatomic, strong) ICMRecoveredPrivacyPolicyNotice *privacyPolicyNotice;
@property (nonatomic, strong) ICMConversation *recentOpenInboundConversation;
@end
