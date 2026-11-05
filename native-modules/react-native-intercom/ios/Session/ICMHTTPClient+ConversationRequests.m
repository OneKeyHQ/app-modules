// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMHTTPClient+ConversationRequests.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"

@implementation ICMHTTPClient (ConversationRequests)
+ (void)recordInteractions:(NSString *)identifier interactions:(NSArray<NSString *> *)interactions {
    if ([self bailIfMissingIdentityWhenTryingTo:@"record interactions" withFailureBlock:nil]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"interactions"] = interactions;
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"conversations/%@/record_interactions", identifier];
    [engine POST:path parameters:parameters success:nil failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"record interactions" failureBlock:nil];
    }];
}

+ (void)conversationDetail:(NSString *)identifier reason:(NSString *)reason nexusMetadata:(NSDictionary *)metadata lastAdminPartCreatedAt:(NSNumber *)timestamp lastAdminPartId:(NSString *)lastId success:(void (^)(ICMConversation *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"get conversation detail" withFailureBlock:failure]) return;
    if (identifier == nil || identifier.length == 0) {
        [self failWithErrorMessage:@"ERROR - Tried to get conversation's details with an empty conversation id" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"request_origin"] = reason;
    parameters[@"nexus_connection_status"] = metadata;
    if (timestamp != nil) parameters[@"last_admin_part_created_at"] = timestamp;
    if (lastId != nil) parameters[@"last_admin_part_id"] = lastId;
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"conversations/%@", identifier];
    [engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMConversation *conversation = [ICMConversation instanceFromDictionary:response partDisplayStyle:1];
        if ([NSThread isMainThread]) success(conversation);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(conversation); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"get conversation detail" failureBlock:failure];
    }];
}
@end

@implementation ICMHTTPBridge (ConversationRequests)
+ (void)recordInteractions:(NSString *)identifier interactions:(NSArray<NSString *> *)interactions {
    [ICMHTTPClient recordInteractions:identifier interactions:interactions];
}
+ (void)conversationDetail:(NSString *)identifier reason:(NSString *)reason nexusMetadata:(NSDictionary *)metadata lastAdminPartCreatedAt:(NSNumber *)timestamp lastAdminPartId:(NSString *)lastId success:(void (^)(ICMConversation *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient conversationDetail:identifier reason:reason nexusMetadata:metadata lastAdminPartCreatedAt:timestamp lastAdminPartId:lastId success:^(ICMConversation *conversation) {
        if (success != nil) success(conversation);
    } error:^(NSError *error) {
        if (failure != nil) failure(error);
    }];
}
@end
