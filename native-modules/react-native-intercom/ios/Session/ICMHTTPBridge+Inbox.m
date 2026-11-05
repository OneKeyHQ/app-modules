#import "ICMHTTPBridge.h"
#import "ICMHTTPClient+Inbox.h"
#import "ICMDataManagerOpenDependencies.h"
@implementation ICMHTTPBridge (OriginalInbox)
+ (void)getUnreadConversationsWithSuccess:(void (^)(void))success {
    [ICMHTTPClient unreadConversations:0 success:^(ICMBaseResponse *response) {
        [ICMHTTPBridge handleBaseResponse:response];
        if (success) success();
    } error:nil];
}
+ (void)getConversationsBefore:(NSDate *)before pageSize:(NSInteger)pageSize success:(void (^)(ICMConversationPageResponse *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient conversationsBefore:before pageSize:pageSize success:^(ICMConversationsResponse *response) {
        [ICMHTTPBridge storeUserIdentity:response.user];
        if (success) success(response.conversationPage);
    } error:^(NSError *error) { if (failure) failure(error); }];
}
+ (void)openMessenger:(void (^)(ICMRecoveredOpenResponse *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient openMessenger:^(ICMRecoveredOpenResponse *response) { if (success) success(response); }
                         error:^(NSError *error) { if (failure) failure(error); }];
}
@end
