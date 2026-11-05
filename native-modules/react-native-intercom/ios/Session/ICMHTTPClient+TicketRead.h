// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMHTTPClient.h"
#import "ICMBaseResponse.h"
NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (TicketRead)
+ (void)markTicketAsRead:(NSString * _Nullable)identifier success:(void (^ _Nullable)(ICMBaseResponse *))success error:(void (^ _Nullable)(NSError * _Nullable))failure;
@end
NS_ASSUME_NONNULL_END
