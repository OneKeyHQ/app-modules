// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMHTTPClient.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (IdentityRequests)
+ (void)setAuthTokens:(NSDictionary *)tokens success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    NSString *action = @"set user auth tokens";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"auth_tokens"] = tokens;
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"user_auth_tokens" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task; (void)response;
        if ([NSThread isMainThread]) success(); else dispatch_async(dispatch_get_main_queue(), ^{ success(); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        NSError *http = [ICMHTTPClient httpErrorWithCode:[error.userInfo[@"kIntercomHTTPStatusCode"] integerValue] fromNetworkError:error];
        [ICMHTTPClient failWithNetworkError:http whenTryingTo:action failureBlock:failure];
    }];
}
+ (NSMutableDictionary *)buildMultipartParametersForCurrentIdentity {
    NSMutableDictionary *parameters = [[NSMutableDictionary alloc] init];
    ICMUserIdentity *identity = [ICMIdentityStore currentIdentity].userIdentity;
    if (identity.email.length) parameters[@"email"] = identity.email;
    if (identity.userId.length) parameters[@"user_id"] = identity.userId;
    if (!identity.email && !identity.userId && identity.anonymousId.length) parameters[@"anonymous_id"] = identity.anonymousId;
    NSString *identifier = [ICMDeviceData deviceIdentifier];
    if (identifier) parameters[@"device_identifier"] = identifier;
    NSDictionary *representation = identity.dictionaryRepresentation;
    if (representation[@"app_id"]) parameters[@"app_id"] = [representation[@"app_id"] description];
    id hmac = representation[@"hmac"];
    if (hmac && ![hmac isKindOfClass:NSNull.class]) parameters[@"hmac"] = hmac;
    id jwt = representation[@"intercom_user_jwt"];
    if (jwt && ![jwt isKindOfClass:NSNull.class]) parameters[@"intercom_user_jwt"] = jwt;
    return parameters;
}
@end
