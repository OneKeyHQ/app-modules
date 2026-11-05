#import "ICMHTTPBridge.h"
#import "ICMHTTPClient.h"
#import "ICMUserUpdateRequest.h"
#import "ICMUserUpdateDeduper.h"
#import "ICMDataManagerDependencies.h"
#import "../Identity/ICMUtils.h"
@interface ICMPresentationManager (UserUpdateApplication)
- (BOOL)isContentCurrentlyPresented;
- (void)showLauncherOrInAppsIfNeeded;
@end
@implementation ICMHTTPBridge (UserUpdate)
+ (void)updateUser:(ICMUserUpdateRequest *)update success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    ICMUserUpdateDeduper *deduper = [ICMUserUpdateDeduper instance];
    if ([deduper isDuplicateUserUpdate:update]) {
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Not updating user. Duplicate user attributes.");
        if ([NSThread isMainThread]) { if (success) success(); }
        else dispatch_async(dispatch_get_main_queue(), ^{ if (success) success(); });
        return;
    }
    [deduper storeUserUpdate];
    [ICMHTTPClient updateUserWithUserAttributes:update.attributes newSession:update.newSession sessionId:update.sessionId sentFromBackground:update.sentFromBackgroundAppState carouselVisible:[[ICMPresentationManager sharedInstance] isContentCurrentlyPresented] success:^(ICMBaseResponse *response) {
        if (update.attributes.count && [ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] INFO - Successfully updated user with attributes [%@]", update.attributes);
        [ICMHTTPBridge handleBaseResponse:response];
        [[ICMPresentationManager sharedInstance] showLauncherOrInAppsIfNeeded];
        if ([NSThread isMainThread]) { if (success) success(); }
        else dispatch_async(dispatch_get_main_queue(), ^{ if (success) success(); });
    } error:^(NSError *error) {
        [ICMHTTPBridge submitMetrics];
        if ([NSThread isMainThread]) { if (failure) failure(error); }
        else dispatch_async(dispatch_get_main_queue(), ^{ if (failure) failure(error); });
    }];
}
@end
