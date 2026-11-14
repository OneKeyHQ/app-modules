#import "ICMHTTPBridge+DeviceTokens.h"
#import "ICMHTTPClient+DeviceTokens.h"
#import "ICMIdentityStore.h"
#import "ICMIdentityStoreDependencies.h"
#import "ICMUserJwtRefresher.h"
#import "../Identity/ICMAppEnvironmentChecker.h"
#import "../Identity/ICMDeviceTokenData.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate9JWTExpiry")))
@interface ICMRecoveredJWTExpiry : NSObject
+ (BOOL)isDefinitivelyExpired:(id)jwt;
@end
@interface ICMRecoveredErrorProcessor (OriginalTokenErrors)
+ (NSError *)generatePushNotificationNilDeviceTokenError;
+ (NSError *)generateConfigNotReceivedError;
+ (NSError *)generateExpiredIdentityVerificationTokenError;
+ (NSError *)generateInvalidIdentityVerificationError;
@end
@implementation ICMHTTPBridge (OriginalDeviceTokens)
+ (void)registerDeviceToken:(NSData *)token failure:(void (^)(NSError *))failure { [self registerDeviceToken:token success:nil failure:failure]; }
+ (void)registerDeviceToken:(NSData *)token success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    if (!token) {
        void (^deliver)(void) = ^{ if (failure) failure([ICMRecoveredErrorProcessor generatePushNotificationNilDeviceTokenError]); };
        if ([NSThread isMainThread]) deliver(); else dispatch_async(dispatch_get_main_queue(), deliver);
        return;
    }
    BOOL changed = [[ICMIdentityStore currentIdentity] setDeviceTokenData:token environment:[ICMAppEnvironmentChecker appEnvironment] failure:failure];
    [[ICMIdentityStore currentIdentity] saveToDisk];
    BOOL submitted = [ICMIdentityStore currentIdentity].deviceTokenData.deviceTokenSubmitted;
    if (!changed && submitted) {
        if ([NSThread isMainThread]) { if (success) success(); }
        else dispatch_async(dispatch_get_main_queue(), ^{ if (success) success(); });
        return;
    }
    ICMConfig *config = [ICMConfig sharedConfig];
    ICMUserIdentity *identity = [ICMIdentityStore currentIdentity].userIdentity;
    NSInteger decision = [self deviceTokenDecisionForConfig:config userIdentity:identity];
    if (decision == 0) { [ICMHTTPClient submitDeviceTokenWithSuccess:success failure:failure]; return; }
    NSError *error;
    if (decision == 1) error = [ICMRecoveredErrorProcessor generateConfigNotReceivedError];
    else if (decision == 3) error = [ICMRecoveredErrorProcessor generateExpiredIdentityVerificationTokenError];
    else error = [ICMRecoveredErrorProcessor generateInvalidIdentityVerificationError];
    if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] [DeviceToken] Not submitting: %@", error.localizedDescription);
    if ([NSThread isMainThread]) { if (failure) failure(error); }
    else dispatch_async(dispatch_get_main_queue(), ^{ if (failure) failure(error); });
}
+ (NSInteger)deviceTokenDecisionForConfig:(ICMConfig *)config userIdentity:(ICMUserIdentity *)identity {
    if (!config.receivedFromServer) return 1;
    if (!config.identityVerificationEnabled) return 0;
    id hmac = identity.secureOptions[@"hmac"];
    id jwt = identity.secureOptions[@"intercom_user_jwt"];
    if (hmac && ![hmac isKindOfClass:[NSNull class]]) return 0;
    if (!jwt || [jwt isKindOfClass:[NSNull class]]) return 2;
    if ([ICMRecoveredJWTExpiry isDefinitivelyExpired:jwt]) return [ICMRecoveredUserJwtRefresher shared].hasProvider ? 0 : 3;
    return 0;
}
+ (void)deleteDeviceToken:(NSString *)token forUserIdentity:(ICMUserIdentity *)identity completion:(void (^)(void))completion {
    [ICMHTTPClient deleteDeviceToken:token forUserIdentity:identity completion:completion];
}
+ (void)setAuthTokens:(NSDictionary *)tokens success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    [ICMHTTPClient setAuthTokens:tokens success:success failure:failure];
}
@end
