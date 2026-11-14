#import "ICMBridge+SecureOptions.h"
#import "ICMBridgeDependencies.h"
#import "ICMIdentityStore.h"
#import "ICMConfig.h"
#import "ICMSessionManager.h"
#import "ICMUserJwtRefresher.h"
#import "IntercomNexusClient.h"
@implementation ICMBridge (SecureOptions)
+ (void)setApiKey:(NSString *)apiKey appId:(NSString *)appId {
    if ([[ICMIdentityStore currentIdentity].appIdentity setApiKey:apiKey appId:appId]) {
        [ICMConfig deleteSharedConfig];
        [self clearNonIdentityData];
        [[ICMMetric sharedInstance] clearAllEvents];
        if ([ICMIdentityStore currentIdentity].userIdentity.intercomId) [[ICMIdentityStore currentIdentity].userIdentity clearIntercomId];
        [IntercomNexusClient sharedClient].configuration = nil;
        [self ping];
        [[ICMIdentityStore currentIdentity] saveToDisk];
    }
}
+ (void)setSecureOptions:(NSDictionary *)options {
    if (options[@"hmac"] && options[@"intercom_user_jwt"]) {
        NSLog(@"[Intercom] WARNING - Both HMAC and JWT identity verification methods were provided. Using JWT and ignoring HMAC.");
        NSMutableDictionary *changed = [options mutableCopy];
        [changed removeObjectForKey:@"hmac"];
        [self applySecureOptions:changed];
    } else [self applySecureOptions:options];
}
+ (void)applySecureOptions:(NSDictionary *)options {
    id previous = [ICMBridge currentUserJwt];
    if ([[ICMIdentityStore currentIdentity] updateSecureOptions:options]) {
        id current = [ICMBridge currentUserJwt];
        if ([current isKindOfClass:[NSString class]] && ![current isEqual:previous]) {
            [[ICMRecoveredUserJwtRefresher shared] userJwtWasSet:current];
            [ICMBridge postUserJwtDidChange];
        }
        [ICMBridge sendSecureOptionsIfNeeded];
    }
}
+ (void)setUserJwtProvider:(void (^)(void (^)(NSString *jwt)))provider { [ICMRecoveredUserJwtRefresher shared].provider = provider; }
+ (void)removeUserJwtProvider { [ICMRecoveredUserJwtRefresher shared].provider = nil; }
+ (void)refreshedUserJwtWasApplied {
    void (^applied)(void) = ^{
        [[NSNotificationCenter defaultCenter] postNotificationName:@"IntercomUserJwtDidChangeNotification" object:nil];
        if ([[ICMIdentityStore currentIdentity].userIdentity isIdentified] && [[ICMSessionManager sharedInstance] hasOutstandingSessionStart]) [ICMBridge ping];
    };
    if ([NSThread isMainThread]) applied(); else dispatch_async(dispatch_get_main_queue(), applied);
}
+ (id)currentUserJwt { return [ICMIdentityStore currentIdentity].userIdentity.secureOptions[@"intercom_user_jwt"]; }
+ (void)postUserJwtDidChange {
    void (^post)(void) = ^{ [[NSNotificationCenter defaultCenter] postNotificationName:@"IntercomUserJwtDidChangeNotification" object:nil]; };
    if ([NSThread isMainThread]) post(); else dispatch_async(dispatch_get_main_queue(), post);
}
+ (void)sendSecureOptionsIfNeeded {
    if ([[ICMIdentityStore currentIdentity].userIdentity isIdentified]) {
        if ([[ICMSessionManager sharedInstance] hasOutstandingSessionStart]) [self ping];
        else if (![ICMIdentityStore currentIdentity].isUserAuthenticatedWithAPI) [self registerUserWithAttributes:nil success:nil failure:nil];
    }
}
@end
