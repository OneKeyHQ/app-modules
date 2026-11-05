// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
#import "ICMHTTPClient.h"
#import "ICMDataManagerRequestDependencies.h"
NS_ASSUME_NONNULL_BEGIN
@interface ICMHTTPClient (TicketList)
+ (void)getTicketsAtPage:(NSInteger)page pageSize:(NSInteger)pageSize success:(void (^ _Nullable)(ICMRecoveredTicketsPageResponse * _Nullable))success error:(void (^ _Nullable)(NSError * _Null_unspecified))failure;
@end
NS_ASSUME_NONNULL_END
