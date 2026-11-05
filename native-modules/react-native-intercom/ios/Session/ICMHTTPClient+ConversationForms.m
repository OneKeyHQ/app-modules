// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMHTTPClient+ConversationForms.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"

@implementation ICMHTTPClient (ConversationForms)
+ (void)rateConversation:(NSString *)identifier withRatingIndex:(NSUInteger)index error:(void (^)(NSError *))failure {
    NSString *action = @"rate conversation";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    if (identifier == nil || identifier.length == 0) {
        [self failWithErrorMessage:@"ERROR - Tried to rate conversation with an empty conversation id" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"rating_index"] = [NSNumber numberWithUnsignedInteger:index];
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"conversations/%@/rate", identifier];
    [engine POST:path parameters:parameters success:nil failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}

+ (void)addConversationRatingRemarkForConversation:(NSString *)identifier remark:(NSString *)remark error:(void (^)(NSError *))failure {
    NSString *action = @"add a remark to the conversation";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    if (identifier == nil || identifier.length == 0) {
        [self failWithErrorMessage:@"ERROR - Tried to add a remark to the conversation with an empty conversation id" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"remark"] = remark;
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"conversations/%@/remark", identifier];
    [engine POST:path parameters:parameters success:nil failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}

+ (void)submitFormParams:(NSDictionary *)form forConversation:(NSString *)identifier success:(void (^)(ICMConversation *))success error:(void (^)(NSError *))failure {
    NSString *action = @"submit form params to the conversation";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    if (identifier == nil || identifier.length == 0) {
        [self failWithErrorMessage:@"ERROR - Tried to submit form params to the conversation with an empty conversation id" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:nil];
        return;
    }
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"form_params"] = form;
    if (parameters.count == 2) {
        [self failWithErrorMessage:@"ERROR - Tried to submit a form to a conversation with no data" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"conversations/%@/form", identifier];
    [engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMConversation *conversation = [ICMConversation instanceFromDictionary:response partDisplayStyle:1];
        if ([NSThread isMainThread]) success(conversation);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(conversation); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
@end
