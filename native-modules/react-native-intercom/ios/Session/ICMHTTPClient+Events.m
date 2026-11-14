#import "ICMHTTPClient+Events.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "ICMBaseResponse.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (OriginalEvents)
+ (void)logEventWithName:(NSString *)name metadata:(NSDictionary *)metadata sentFromBackground:(BOOL)background carouselVisible:(BOOL)visible success:(void (^)(ICMBaseResponse *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"log an event" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    NSMutableDictionary *event = [@{} mutableCopy];
    if (name && name.length) event[@"event_name"] = name;
    if (metadata) event[@"metadata"] = metadata;
    parameters[@"event"] = event;
    parameters[@"sent_from_background"] = @(background);
    parameters[@"carousel_visible"] = @(visible);
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"events" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success) {
            ICMBaseResponse *model = [ICMBaseResponse instanceFromDictionary:response];
            if ([NSThread isMainThread]) success(model);
            else dispatch_async(dispatch_get_main_queue(), ^{ success(model); });
        }
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"log an event" failureBlock:failure];
    }];
}
@end
