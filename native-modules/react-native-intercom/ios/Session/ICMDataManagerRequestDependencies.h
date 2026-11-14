// Declarations only; these original HTTP/model implementations remain required.
#import "ICMDataManagerDependencies.h"
#import "ICMUnreadTicketsResponse.h"
#import "ICMHTTPBridge.h"
#import "ICMConversationsResponse.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate6Ticket")))
__attribute__((swift_name("Ticket")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredTicket : NSObject
@property (nonatomic, readonly) NSString *id;
@property (nonatomic, readonly) NSDate *latestStatusUpdatedAt;
@property (nonatomic) BOOL read;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate19TicketsPageResponse")))
__attribute__((swift_name("TicketsPageResponse")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredTicketsPageResponse : NSObject
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@property (nonatomic, readonly) NSArray *tickets;
@property (nonatomic, readonly) NSNumber *nextPage;
@property (nonatomic, readonly) ICMUnreadTicketsResponse *unreadTickets;
@end
#import "ICMConversationPart.h"
#import "ICMAvatarParticipantImports.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate26ExpandedConversationHeader")))
__attribute__((swift_name("ExpandedConversationHeader")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredExpandedConversationHeader : NSObject @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate18ConversationHeader")))
__attribute__((swift_name("ConversationHeader")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredConversationHeader : NSObject
@property (nonatomic, strong) ICMRecoveredExpandedConversationHeader *expanded;
@property (nonatomic, readonly) BOOL displayActiveIndicator;
@end
#import "ICMComposerSuggestions.h"
#import "ICMComposerState.h"
@interface ICMHTTPBridge (DataManagerRequests)
+ (void)getConversationsBefore:(NSDate *)before pageSize:(NSInteger)pageSize success:(void (^)(ICMConversationPageResponse *))success error:(void (^)(NSError *))failure;
+ (void)getUnreadConversationsWithSuccess:(void (^)(void))success;
+ (void)getTicketsAtPage:(NSInteger)page pageSize:(NSInteger)pageSize success:(void (^)(ICMRecoveredTicketsPageResponse *))success error:(void (^)(NSError *))failure;
+ (void)getTicketDetailsForTicketId:(NSString *)identifier success:(void (^)(ICMRecoveredTicket *))success error:(void (^)(NSError *))failure;
+ (void)markTicketAsReadWithID:(NSString *)identifier;
+ (void)conversationDetail:(NSString *)identifier reason:(NSString *)reason nexusMetadata:(NSDictionary *)metadata lastAdminPartCreatedAt:(NSNumber *)timestamp lastAdminPartId:(NSString *)partId success:(void (^)(ICMConversation *))success error:(void (^)(NSError *))failure;
@end
#import "ICMCarouselService.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate13SurveyService")))
@interface ICMRecoveredSurveyService : NSObject
- (void)presentSurveyWithInstanceId:(NSString *)identifier;
@end
