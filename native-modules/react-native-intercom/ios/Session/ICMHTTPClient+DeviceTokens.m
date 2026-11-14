#import "ICMHTTPClient+DeviceTokens.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (OriginalDeviceTokens)
+ (void)submitDeviceTokenWithFailure:(void (^)(NSError *))failure { [self submitDeviceTokenWithSuccess:nil failure:failure]; }
+ (void)submitDeviceTokenWithSuccess:(void (^)(void))success failure:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"submit a device token" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"device_data"] = [[ICMDeviceData new] dictionaryRepresentation];
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"device_tokens" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task; (void)response;
        [ICMHTTPClient setDeviceTokenSubmitted:YES];
        if (success) {
            if ([NSThread isMainThread]) success();
            else dispatch_async(dispatch_get_main_queue(), ^{ success(); });
        }
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        NSError *http = [ICMHTTPClient httpErrorWithCode:[error.userInfo[@"kIntercomHTTPStatusCode"] integerValue] fromNetworkError:error];
        [ICMHTTPClient failWithNetworkError:http whenTryingTo:@"submit a device token" failureBlock:failure];
    }];
}
+ (void)deleteDeviceToken:(NSString *)token forUserIdentity:(ICMUserIdentity *)identity completion:(void (^)(void))completion {
    if (identity.identityExists && [ICMIdentityStore currentIdentity].appIdentity.identityExists) {
        NSMutableDictionary *parameters = [identity.dictionaryRepresentation mutableCopy];
        parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
        parameters[@"device_token"] = token ?: @"";
        [[ICMIdentityStore currentIdentity].appIdentity.engine DELETE:@"device_tokens" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
            (void)task; (void)response;
            if (completion) {
                if ([NSThread isMainThread]) completion();
                else dispatch_async(dispatch_get_main_queue(), completion);
            }
            if ([ICMIdentityStore currentIdentity].userIdentity == identity) [ICMHTTPClient setDeviceTokenSubmitted:NO];
        } failure:^(NSURLSessionDataTask *task, NSError *error) {
            (void)task;
            [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"send a device token for deletion" failureBlock:^(NSError *failure) {
                (void)failure;
                if (completion) {
                    if ([NSThread isMainThread]) completion();
                    else dispatch_async(dispatch_get_main_queue(), completion);
                }
            }];
        }];
    } else {
        NSString *message = [NSString stringWithFormat:@"ERROR - Tried to send a device token for deletion but there was an identification error"];
        [self failWithErrorMessage:message recoverySuggestion:nil errorCode:0 underlyingError:nil failureBlock:nil];
        if (completion) completion();
    }
}
@end
