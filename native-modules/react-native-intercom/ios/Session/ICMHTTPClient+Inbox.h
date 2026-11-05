#import "ICMHTTPClient.h"
#import "ICMConversationsResponse.h"
@class ICMBaseResponse, ICMRecoveredOpenResponse;
@interface ICMHTTPClient (OriginalInbox)
+ (void)unreadConversations:(NSInteger)pageSize success:(void (^)(ICMBaseResponse *))success error:(void (^)(NSError *))failure;
+ (void)conversationsBefore:(NSDate *)before pageSize:(NSInteger)pageSize success:(void (^)(ICMConversationsResponse *))success error:(void (^)(NSError *))failure;
+ (void)openMessenger:(void (^)(ICMRecoveredOpenResponse *))success error:(void (^)(NSError *))failure;
@end
