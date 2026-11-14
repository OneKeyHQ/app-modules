#import "ICMHTTPBridge.h"
#import "ICMHTTPClient.h"
#import "ICMConversation.h"
#import "ICMConversationPart.h"
#import "ITBUpload.h"
@interface ICMHTTPBridge (OriginalCompletion)
+ (void)rateConversation:(NSString *)identifier withRatingIndex:(NSUInteger)index error:(void (^)(NSError *))failure;
+ (void)addConversationRatingRemarkForConversation:(NSString *)identifier remark:(NSString *)remark error:(void (^)(NSError *))failure;
+ (void)quickReplyToConversation:(NSString *)identifier clientAssignedUUID:(NSString *)uuid withPartId:(NSString *)partId withOptionUUID:(NSString *)optionId success:(void (^)(ICMConversationPart *))success error:(void (^)(NSError *))failure;
+ (void)submitFormParams:(NSDictionary *)form forConversation:(NSString *)identifier success:(void (^)(ICMConversation *))success error:(void (^)(NSError *))failure;
+ (void)triggerInboundBotWithOptionUUID:(NSString *)optionId clientAssignedUUID:(NSString *)uuid success:(void (^)(ICMConversation *))success error:(void (^)(NSError *))failure;
+ (void)markConversationAsDismissed:(NSString *)identifier;
+ (void)messageSentMetric:(ICMConversation *)conversation upload:(ITBUpload *)upload closedConversationId:(NSString *)identifier;
+ (void)messageOpenedMetric:(ICMConversation *)conversation closedConversationId:(NSString *)identifier;
@end
@interface ICMHTTPClient (OriginalDismissal)
+ (void)markConversationAsDismissed:(NSString *)identifier success:(void (^)(void))success error:(void (^)(NSError *))failure;
@end
