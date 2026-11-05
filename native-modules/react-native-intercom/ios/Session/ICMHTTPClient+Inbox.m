#import "ICMHTTPClient+Inbox.h"
#import "ICMBaseResponse.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "ICMDataManagerOpenDependencies.h"
#import "../Identity/ICMDeviceData.h"
@interface ICMRecoveredOpenResponse (OriginalDictionaryInitializer)
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end
@implementation ICMHTTPClient (OriginalInbox)
+ (void)unreadConversations:(NSInteger)pageSize success:(void (^)(ICMBaseResponse *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"get unread conversations" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"per_page"] = pageSize > 0 ? @(pageSize) : @10;
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"conversations/unread" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success) {
            ICMBaseResponse *model = [ICMBaseResponse instanceFromDictionary:response];
            if ([NSThread isMainThread]) success(model);
            else dispatch_async(dispatch_get_main_queue(), ^{ success(model); });
        }
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"get unread conversations" failureBlock:failure];
    }];
}
+ (void)conversationsBefore:(NSDate *)before pageSize:(NSInteger)pageSize success:(void (^)(ICMConversationsResponse *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"get conversations for display in the conversations list" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"per_page"] = pageSize > 0 ? @(pageSize) : @10;
    if (before) parameters[@"before"] = @([before timeIntervalSince1970]);
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"conversations/inbox" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success) {
            ICMConversationsResponse *model = [ICMConversationsResponse instanceFromDictionary:response];
            if ([NSThread isMainThread]) success(model);
            else dispatch_async(dispatch_get_main_queue(), ^{ success(model); });
        }
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"get conversations for display in the conversations list" failureBlock:failure];
    }];
}
+ (void)openMessenger:(void (^)(ICMRecoveredOpenResponse *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"fetch composer suggestions" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"open" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success) {
            ICMRecoveredOpenResponse *model = [[ICMRecoveredOpenResponse alloc] initWithDictionary:response];
            if ([NSThread isMainThread]) success(model);
            else dispatch_async(dispatch_get_main_queue(), ^{ success(model); });
        }
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"fetch composer suggestions" failureBlock:failure];
    }];
}
@end
