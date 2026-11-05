// PARTIAL_CLASS: four failure/admission methods; endpoint implementation missing.
#import "ICMHTTPClient.h"
#import "ICMIdentityStore.h"
#import "ICMIdentityStoreDependencies.h"
@implementation ICMHTTPClient
+ (void)failWithNetworkError:(NSError *)error whenTryingTo:(NSString *)action failureBlock:(void (^)(NSError *))failure {
    NSString *message = [NSString stringWithFormat:@"ERROR - Tried to %@ but there was a network error", action];
    NSInteger status = 0;
    if (error.userInfo[@"kIntercomHTTPStatusCode"]) {
        message = [message stringByAppendingString:[NSString stringWithFormat:@" (%@)", error.userInfo[@"kIntercomHTTPStatusCode"]]];
        status = [error.userInfo[@"kIntercomHTTPStatusCode"] integerValue];
    }
    if (error.localizedDescription) message = [message stringByAppendingString:[NSString stringWithFormat:@" - %@", error.localizedDescription]];
    NSLog(@"[Intercom] %@", message);
    if (!failure) return;
    void (^deliver)(void) = ^{ failure(status ? [ICMHTTPClient httpErrorWithCode:status fromNetworkError:error] : error); };
    if ([NSThread isMainThread]) deliver(); else dispatch_async(dispatch_get_main_queue(), deliver);
}
+ (NSError *)httpErrorWithCode:(NSInteger)code fromNetworkError:(NSError *)error {
    NSError *http = [ICMRecoveredErrorProcessor generateHTTPErrorWithCode:code message:error.localizedDescription];
    id serverCode = error.userInfo[@"kIntercomErrorCode"];
    if (!serverCode) return http;
    NSMutableDictionary *info = [http.userInfo mutableCopy] ?: [NSMutableDictionary dictionary];
    info[@"kIntercomErrorCode"] = serverCode;
    return [NSError errorWithDomain:http.domain code:http.code userInfo:info];
}
+ (void)failWithErrorMessage:(NSString *)message recoverySuggestion:(NSString *)suggestion errorCode:(NSInteger)code underlyingError:(NSError *)underlying failureBlock:(void (^)(NSError *))failure {
    NSLog(@"[Intercom] %@", message);
    if (!failure) return;
    NSError *error = [NSError errorWithDomain:@"IntercomSDKErrorDomain" code:code userInfo:@{NSLocalizedDescriptionKey:message ?: @"", NSLocalizedRecoverySuggestionErrorKey:suggestion ?: @"", NSUnderlyingErrorKey:underlying ?: [NSNull null]}];
    if ([NSThread isMainThread]) failure(error); else dispatch_async(dispatch_get_main_queue(), ^{ failure(error); });
}
+ (BOOL)bailIfMissingIdentityWhenTryingTo:(NSString *)action withFailureBlock:(void (^)(NSError *))failure {
    if (![[ICMIdentityStore currentIdentity].appIdentity identityExists]) {
        NSString *message = [NSString stringWithFormat:@"ERROR - Tried to %@, but there was a problem with the SDK's initialisation.", action];
        [self failWithErrorMessage:message recoverySuggestion:@"Double check that you have copied and pasted the API Key and App ID correctly, with the `ios_sdk-` prefix. Also check your secure mode implementation to make sure it is correct." errorCode:1000 underlyingError:nil failureBlock:failure];
        return YES;
    }
    if (![[ICMIdentityStore currentIdentity].userIdentity identityExists]) {
        NSString *message = [NSString stringWithFormat:@"ERROR - Tried to %@, but no user has been registered yet.", action];
        [self failWithErrorMessage:message recoverySuggestion:@"Double check that you have correctly registered a user in order to retrieve messages." errorCode:1001 underlyingError:nil failureBlock:failure];
        return YES;
    }
    return NO;
}
@end
