#import "ICMHTTPClient+Home.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (Home)
+ (void)fetchHomeWithSuccess:(void (^)(NSDictionary *))success failure:(void (^)(NSError *))failure {
    NSString *action = @"fetch home";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    [engine POST:@"home" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (!success) return;
        if ([NSThread isMainThread]) success(response);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(response); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
@end
