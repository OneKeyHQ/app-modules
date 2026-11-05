// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMHTTPClient.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "ICMBaseResponse.h"
#import "ICMProactiveContentVisibility.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (UserUpdate)
+ (void)updateUserWithUserAttributes:(NSDictionary *)attributes newSession:(BOOL)newSession sessionId:(NSString *)sessionId sentFromBackground:(BOOL)background carouselVisible:(BOOL)visible success:(void (^)(ICMBaseResponse *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"update user" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"device_data"] = [[ICMDeviceData new] dictionaryRepresentation];
    parameters[@"new_session"] = @(newSession);
    parameters[@"sent_from_background"] = @(background);
    parameters[@"carousel_visible"] = @(visible);
    parameters[@"carousels_enabled"] = @([ICMRecoveredProactiveContentVisibility shared].carouselsEnabled);
    parameters[@"surveys_enabled"] = @([ICMRecoveredProactiveContentVisibility shared].surveysEnabled);
    if (newSession && sessionId) parameters[@"session_id"] = sessionId;
    if (attributes) parameters[@"user_attributes"] = attributes;
    void (^onSuccess)(NSURLSessionDataTask *, id) = ^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success) {
            ICMBaseResponse *model = [ICMBaseResponse instanceFromDictionary:response];
            if ([NSThread isMainThread]) success(model);
            else dispatch_async(dispatch_get_main_queue(), ^{ success(model); });
        }
        if (parameters[@"device_data"][@"device_token"]) [ICMHTTPClient setDeviceTokenSubmitted:YES];
    };
    void (^terminalFailure)(NSURLSessionDataTask *, NSError *) = ^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"update user" failureBlock:failure];
    };
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"users" parameters:parameters success:onSuccess failure:^(NSURLSessionDataTask *task, NSError *error) {
        if (((NSHTTPURLResponse *)task.response).statusCode == 404 && parameters[@"user"][@"intercom_id"] && [parameters[@"user"] count] >= 3) {
            NSMutableDictionary *user = [parameters[@"user"] mutableCopy];
            [user removeObjectForKey:@"intercom_id"];
            parameters[@"user"] = user;
            [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"users" parameters:parameters success:onSuccess failure:^(NSURLSessionDataTask *retryTask, NSError *retryError) {
                if ([NSThread isMainThread]) terminalFailure(retryTask, retryError);
                else dispatch_async(dispatch_get_main_queue(), ^{ terminalFailure(retryTask, retryError); });
            }];
        } else {
            terminalFailure(task, error);
        }
    }];
}
+ (void)setDeviceTokenSubmitted:(BOOL)submitted { [[ICMIdentityStore currentIdentity] updateDeviceTokenSubmitted:submitted]; }
@end
