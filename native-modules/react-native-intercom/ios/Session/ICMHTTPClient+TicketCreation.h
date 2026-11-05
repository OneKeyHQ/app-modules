#import "ICMHTTPClient.h"
#import "ICMDataManagerRequestDependencies.h"
NS_ASSUME_NONNULL_BEGIN
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate17TicketFormRequest")))
__attribute__((swift_name("TicketFormRequest")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredTicketFormRequest : NSObject
@property (nonatomic, readonly) NSString *ticketTypeID;
@property (nonatomic, readonly) NSArray<NSDictionary *> *attributesDictionary;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate18TicketFormResponse")))
__attribute__((swift_name("TicketFormResponse")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredTicketFormResponse : NSObject
- (nullable instancetype)initWith:(NSDictionary *)dictionary;
@property (nonatomic, readonly) NSInteger ticketTypeId;
@property (nonatomic, readonly) NSString *name;
@end
@interface ICMHTTPClient (TicketCreation)
+ (void)getTicketFormWithRequest:(ICMRecoveredTicketFormRequest *)request success:(void (^)(ICMRecoveredTicketFormResponse *))success error:(void (^ _Nullable)(NSError * _Nullable))failure;
+ (void)createTicketTypeId:(NSString *)identifier conversationId:(NSString * _Nullable)conversationId attributes:(NSArray<NSDictionary *> *)attributes success:(void (^)(ICMRecoveredTicket *))success error:(void (^ _Nullable)(NSError * _Nullable))failure;
@end
NS_ASSUME_NONNULL_END
