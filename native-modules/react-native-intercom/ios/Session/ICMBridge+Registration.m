#import "ICMBridge+Registration.h"
#import "ICMBridge+SecureOptions.h"
#import "ICMBridgeDependencies.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "ICMPresentationManager.h"
#import "IntercomNexusClient.h"
#import "../Models/ICMUserAttributes.h"
@implementation ICMBridge (Registration)
+ (void)registerUnidentifiedUserWithSuccess:(void (^)(void))success failure:(void (^)(NSError *))failure {
    ICMIdentityStore *store = [ICMIdentityStore currentIdentity];
    ICMUserIdentity *user = store.userIdentity;
    if ([user isIdentified]) {
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Cannot register unidentified user when an identified user exists");
        if (failure) failure([ICMRecoveredErrorProcessor generateIdentifiedUserExists]);
    } else if (user.anonymousId) {
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Anonymous user already exists.");
        store.isUserAuthenticatedWithAPI = YES;
        [store saveToDisk];
        if (success) success();
    } else {
        if ([store registerUnidentifiedUser]) [self clearNonIdentityData];
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Registering an unidentified user.");
        [self registerUserWithAttributes:nil success:success failure:failure];
        [[ICMPresentationManager sharedInstance] showLauncherOrInAppsIfNeeded];
        [self updateNexusConfigurationWithIdentityStore:store];
    }
}
+ (void)registerIdentifiedUserWithProperties:(ICMUserAttributes *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    ICMIdentityStore *store = [ICMIdentityStore currentIdentity];
    ICMUserIdentity *user = store.userIdentity;
    if (user.identityExists) [self handleExistingIdentity:user userAttributes:attributes identityStore:store success:success failure:failure];
    else [self handleNewIdentity:store userAttributes:attributes success:success failure:failure];
}
+ (void)handleExistingIdentity:(ICMUserIdentity *)identity userAttributes:(ICMUserAttributes *)attributes identityStore:(ICMIdentityStore *)store success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Checking if user identity matches existing identity.");
    if ([identity matchesIdentity:attributes.registrationAttributes]) [self handleMatchingExistingIdentity:attributes success:success failure:failure];
    else {
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Different user identity detected.");
        [self handleIdentifiedUserRegistration:store userAttributes:attributes success:success failure:failure];
    }
}
+ (void)handleMatchingExistingIdentity:(ICMUserAttributes *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - User identity matches existing identity.");
    [ICMIdentityStore currentIdentity].isUserAuthenticatedWithAPI = YES;
    [[ICMIdentityStore currentIdentity] saveToDisk];
    BOOL hasAttributes = [attributes hasNonIdentifyingAttributes];
    BOOL logging = [ICMUtils debugLoggingEnabled];
    if (hasAttributes) {
        if (logging) NSLog(@"[Intercom] INFO - Updating user attributes.");
        [self updateUserWithAttributes:attributes.attributes success:success failure:failure];
    } else {
        if (logging) NSLog(@"[Intercom] INFO - No new user attributes to update.");
        if (success) success();
    }
}
+ (void)handleNewIdentity:(ICMIdentityStore *)store userAttributes:(ICMUserAttributes *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - No existing identity found, checking for previous identity.");
    ICMUserIdentity *previous = store.previousUserIdentity;
    if (!previous || ![self isPreviousIdentityMatch:previous userAttributes:attributes identityStore:store success:success]) [self handleIdentifiedUserRegistration:store userAttributes:attributes success:success failure:failure];
}
+ (BOOL)isPreviousIdentityMatch:(ICMUserIdentity *)previous userAttributes:(ICMUserAttributes *)attributes identityStore:(ICMIdentityStore *)store success:(void (^)(void))success {
    if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Found a previous identity, checking if it matches user identity.");
    BOOL matches = [previous matchesIdentity:attributes.registrationAttributes];
    BOOL logging = [ICMUtils debugLoggingEnabled];
    if (matches) {
        if (logging) NSLog(@"[Intercom] INFO - User identity matches previous identity.");
        [store usePreviousIdentity];
        store.isUserAuthenticatedWithAPI = YES;
        [store saveToDisk];
        if (success) success();
    } else if (logging) NSLog(@"[Intercom] INFO - User identity does not match previous identity.");
    return matches;
}
+ (void)handleIdentifiedUserRegistration:(ICMIdentityStore *)store userAttributes:(ICMUserAttributes *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    BOOL wasUnidentified = [store.userIdentity isUnidentified];
    if ([store registerIdentifiedUserWithProperties:attributes]) {
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Registering an identified user.");
        if (wasUnidentified) {
            if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - User was previously unidentified, clearing pending requests.");
            [store.appIdentity.engine ignoreAllPendingRequests];
            store.userIdentity.nexusConfiguration = nil;
        }
    }
    [self registerUserWithAttributes:attributes success:success failure:failure];
    [[ICMPresentationManager sharedInstance] showLauncherOrInAppsIfNeeded];
    [self updateNexusConfigurationWithIdentityStore:store];
}
+ (void)updateNexusConfigurationWithIdentityStore:(ICMIdentityStore *)store {
    dispatch_async(dispatch_get_global_queue(2, 0), ^{
        IntercomNexusConfiguration *configuration = store.userIdentity.nexusConfiguration;
        [IntercomNexusClient sharedClient].configuration = configuration;
    });
}
@end
