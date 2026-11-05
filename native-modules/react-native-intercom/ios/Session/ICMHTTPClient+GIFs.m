#import "ICMHTTPClient+GIFs.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
#import "ICMMetric.h"
#import "ICMMetricEvent.h"
@implementation ICMHTTPClient (GIFs)
+ (void)loadGIFs:(NSString *)query success:(void (^)(ICMGIFResponse *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"load GIFs" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    if (query != nil) parameters[@"query"] = query;
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    [engine POST:@"gifs" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMGIFResponse *parsed = [ICMGIFResponse instanceFromDictionary:response];
        if ([NSThread isMainThread]) success(parsed);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(parsed); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"load GIFs" failureBlock:failure];
    }];
}
@end
@implementation ICMHTTPBridge (GIFs)
+ (void)loadGIFs:(NSString *)query success:(void (^)(ICMGIFResponse *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient loadGIFs:query success:^(ICMGIFResponse *response) {
        if (success == nil) return;
        success(response);
        ICMMetric *metric = [ICMMetric sharedInstance];
        NSDictionary *info = @{@"search_query": query};
        ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"searched" object:@"expanded_gif_input" place:@"messenger" context:@"in_conversation" additionalInfo:info];
        [metric addEvent:event];
    } error:^(NSError *error) {
        if (error != nil) failure(error);
    }];
}
@end
