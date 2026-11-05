#import "ICMDataManager.h"
@class ICMRecoveredOpenResponse;
@interface ICMDataManager (Open)
- (void)refreshOpenResponseWithSuccess:(void (^)(void))success error:(void (^)(NSError *))failure;
- (void)clearOpenResponseData;
- (ICMConversation *)conversationFromOpenResponse:(ICMRecoveredOpenResponse *)response;
@end
