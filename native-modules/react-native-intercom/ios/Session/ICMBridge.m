// Original 19.9.1 lifecycle and registration; dependencies remain UNLINKED.
#import "ICMBridge.h"
#import "ICMBridgeDependencies.h"
#import "ICMDataManager.h"
#import "ICMDataManager+Conversations.h"
#import "ICMPresentationManager.h"
#import "ICMSessionManager.h"
#import "ICMIdentityStore.h"
#import "ICMConfig.h"
#import "ICMUserUpdateRequest.h"
#import "../Models/ICMUserAttributes.h"
#import "../Identity/ICMLogger.h"
@implementation ICMBridge
+ (void)load {
    if (![ICMUtils swizzlingDisabled]) [ICMPushNotificationManager swizzleReceiveRemoteNotifications];
    [ICMPresentationManager observeSceneWillEnterForeground];
}
+ (void)initialize {
    [ICMUtils checkIntegration];
    static dispatch_once_t once;
    dispatch_once(&once, ^{ dispatch_async(dispatch_get_main_queue(), ^{ [self prepareForNotifications]; }); });
    [ICMDataManager sharedManager];
}
+ (void)prepareForNotifications {
    if ([UIApplication sharedApplication].applicationState == UIApplicationStateActive) {
        [ICMBridge ping];
        [ICMHTTPBridge submitDiagnostics];
    }
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(ping) name:UIApplicationDidBecomeActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:[ICMHTTPBridge class] selector:@selector(submitMetrics) name:UIApplicationDidEnterBackgroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:[ICMHTTPBridge class] selector:@selector(submitDiagnostics) name:UIApplicationDidEnterBackgroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:[ICMHTTPBridge class] selector:@selector(submitDiagnostics) name:UIApplicationDidBecomeActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:[ICMSessionManager sharedInstance] selector:@selector(appMovedToTheBackground) name:UIApplicationDidEnterBackgroundNotification object:nil];
    if ([[ICMConfig sharedConfig].crashReportingConfig[@"enabled"] boolValue]) [[ICMRecoveredDiagnosticSubscriber shared] startReceiving];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleConfigChanged) name:@"kICMIntercomConfigChangedNotification" object:nil];
}
+ (void)ping {
    if (![ICMIdentityStore currentIdentity].userIdentity.identityExists) return;
    ICMSessionStartAttempt *attempt = [[ICMSessionManager sharedInstance] beginSessionStart];
    if (attempt.requestsNewSession) {
        NSArray *snapshot = [ICMBridge identitySnapshot];
        [[ICMPresentationManager sharedInstance] showLauncherOrInAppsIfNeeded];
        ICMUserUpdateRequest *update = [[ICMUserUpdateRequest alloc] initWithNewSession:YES sessionId:attempt.sessionId internalUpdate:YES sentFromBackgroundAppState:[self appIsBackgrounded]];
        [ICMHTTPBridge updateUser:update success:^{
            if ([[ICMSessionManager sharedInstance] sessionStartSucceededWithToken:attempt.token] && [[ICMBridge identitySnapshot] isEqualToArray:snapshot] && ![ICMIdentityStore currentIdentity].isUserAuthenticatedWithAPI) [ICMBridge completeAuthenticationWithAPI];
        } failure:^(NSError *error) {
            if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Session start was rejected. %@", error.localizedDescription);
            if ([[ICMSessionManager sharedInstance] sessionStartFailedWithToken:attempt.token]) [ICMBridge ping];
        }];
    } else [[ICMDataManager sharedManager] getUnreadConversationsIfNeeded];
    [ICMBridge deleteDeviceTokenIfPushNotificationsDisabled];
}
+ (void)registerUserWithAttributes:(ICMUserAttributes *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    NSArray *snapshot = [ICMBridge identitySnapshot];
    ICMSessionStartAttempt *attempt = [[ICMSessionManager sharedInstance] beginSessionStart];
    ICMUserUpdateRequest *update = [[ICMUserUpdateRequest alloc] initWithNewSession:attempt.requestsNewSession sessionId:attempt.sessionId internalUpdate:YES sentFromBackgroundAppState:[self appIsBackgrounded]];
    [update addUserAttributes:attributes.attributes];
    [ICMHTTPBridge updateUser:update success:^{
        [[ICMSessionManager sharedInstance] sessionStartSucceededWithToken:attempt.token];
        if ([[ICMBridge identitySnapshot] isEqualToArray:snapshot]) [ICMBridge completeAuthenticationWithAPI];
        if (success) success();
    } failure:^(NSError *error) {
        BOOL retry = [[ICMSessionManager sharedInstance] sessionStartFailedWithToken:attempt.token];
        if (failure) failure(error);
        if (retry) [ICMBridge ping];
    }];
}
+ (void)completeAuthenticationWithAPI {
    if ([ICMPushNotificationManager isPushNotificationsEnabled]) {
        [ICMLogger debugLog:@"Registering for push when logging in."];
        [[UIApplication sharedApplication] registerForRemoteNotifications];
    }
    [ICMIdentityStore currentIdentity].isUserAuthenticatedWithAPI = YES;
    [[ICMIdentityStore currentIdentity] saveToDisk];
}
+ (NSArray *)identitySnapshot {
    ICMUserIdentity *user = [ICMIdentityStore currentIdentity].userIdentity;
    id anonymousId = user.anonymousId ?: [NSNull null];
    id userId = user.userId ?: [NSNull null];
    id email = user.email ?: [NSNull null];
    return @[anonymousId, userId, email];
}
+ (BOOL)appIsBackgrounded { return [UIApplication sharedApplication].applicationState != UIApplicationStateActive; }
+ (void)handleConfigChanged {
    BOOL enabled = [[ICMConfig sharedConfig].crashReportingConfig[@"enabled"] boolValue];
    ICMRecoveredDiagnosticSubscriber *subscriber = [ICMRecoveredDiagnosticSubscriber shared];
    if (enabled) [subscriber startReceivingWithCompletion:^{ [ICMHTTPBridge submitDiagnostics]; }];
    else [subscriber stopReceiving];
}
+ (BOOL)isUserLoggedIn { return [ICMIdentityStore currentIdentity].isUserAuthenticatedWithAPI; }
+ (ICMUserAttributes *)fetchLoggedInUserAttributes {
    if (![ICMIdentityStore currentIdentity].userIdentity.identityExists) return nil;
    ICMUserAttributes *attributes = [ICMUserAttributes new];
    attributes.email = [ICMIdentityStore currentIdentity].userIdentity.email;
    NSString *userId = [ICMIdentityStore currentIdentity].userIdentity.userId;
    if (userId) attributes.userId = userId;
    else attributes.userId = [ICMIdentityStore currentIdentity].userIdentity.anonymousId;
    return attributes;
}
@end
