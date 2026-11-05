#import "ICMBridge+IdentityLifecycle.h"
#import "ICMBridgeDependencies.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "ICMConfig.h"
#import "ICMDataManagerDependencies.h"
#import "ICMDataManagerRequestDependencies.h"
#import "ICMDataManager+Tickets.h"
#import "ICMDataManager+Open.h"
#import "ICMUserUpdateDeduper.h"
#import "ICMUserUpdateRequest.h"
#import "ICMSessionManager.h"
@interface ICMCarouselService (OriginalLogout)
+ (void)cleanupCarouselCache;
+ (void)clearPresentedCarouselInstanceId;
@end
@interface ICMRecoveredSurveyService (OriginalLogout)
+ (void)setCurrentlyDisplayingSurvey:(BOOL)displaying;
@end
@implementation ICMBridge (IdentityLifecycle)
+ (void)logout {
    [[ICMIdentityStore currentIdentity].appIdentity.engine ignoreAllPendingRequests];
    ICMUserUpdateDeduper *deduper = [ICMUserUpdateDeduper instance];
    [deduper clearStoredUserUpdates];
    [ICMIdentityStore currentIdentity].isUserAuthenticatedWithAPI = NO;
    [[ICMIdentityStore currentIdentity] resetUserIdentity];
    [IntercomNexusClient sharedClient].configuration = nil;
    [ICMConfig sharedConfig].teamGreeting = nil;
    [ICMCarouselService cleanupCarouselCache];
    [ICMCarouselService clearPresentedCarouselInstanceId];
    [ICMRecoveredSurveyService setCurrentlyDisplayingSurvey:NO];
    [[ICMPresentationManager sharedInstance] hideIntercomUI];
    [self clearNonIdentityData];
    if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Intercom has reset all local user data. To resume communicating with Intercom, you must register a user.");
}
+ (void)clearNonIdentityData {
    {
        ICMLocalComposerPersistManager *composer = [ICMLocalComposerPersistManager new];
        [composer clearPersistedData];
    }
    [[ICMPresentationManager sharedInstance] userChanged];
    [[ICMDataManager sharedManager] clearConversations];
    [ICMDataManager sharedManager].lastViewedConversation = nil;
    NSOrderedSet *conversationIds = [NSOrderedSet orderedSet];
    [ICMDataManager sharedManager].unreadManager.unreadConversationIds = conversationIds;
    NSOrderedSet *ticketIds = [NSOrderedSet orderedSet];
    [ICMDataManager sharedManager].unreadManager.unreadTicketIds = ticketIds;
    [[ICMDataManager sharedManager] clearTickets];
    [[ICMSessionManager sharedInstance] reset];
    [[ICMDataManager sharedManager] clearOpenResponseData];
}
+ (void)updateUserWithAttributes:(NSDictionary *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    NSDictionary *values = attributes;
    if ([[ICMIdentityStore currentIdentity].userIdentity isUnidentified]) {
        NSMutableDictionary *changed = [NSMutableDictionary dictionaryWithDictionary:values];
        if (changed[@"user_id"]) {
            NSLog(@"[Intercom] INFO - You cannot update the user_id of an unidentified user. Please call registerUserWithUserId: instead. The user_id: %@, was NOT applied.", changed[@"user_id"]);
            [changed removeObjectForKey:@"user_id"];
        }
        if (changed[@"email"]) {
            NSLog(@"[Intercom] INFO - You cannot update the email address of an anonymous user. Please call registerUserWithEmail: instead. The email: %@, was NOT applied.", changed[@"email"]);
            [changed removeObjectForKey:@"email"];
        }
        values = changed;
    }
    if (values.count) {
        ICMUserUpdateRequest *update = [[ICMUserUpdateRequest alloc] initWithAttributes:values sentFromBackgroundAppState:[self appIsBackgrounded]];
        [ICMHTTPBridge updateUser:update success:success failure:failure];
    }
}
+ (NSError *)generateInvalidUserIdOrEmailError { return [ICMRecoveredErrorProcessor generateInvalidUserIdOrEmailError]; }
+ (NSError *)generateInvalidUpdateObjectError { return [ICMRecoveredErrorProcessor generateInvalidUpdateObjectError]; }
+ (NSError *)generateInvalidAuthTokenError { return [ICMRecoveredErrorProcessor generateInvalidAuthTokenError]; }
@end
