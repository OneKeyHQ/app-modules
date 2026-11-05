#import "ICMHTTPClient+Carousels.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "ICMBaseResponse.h"
#import "../Identity/ICMDeviceData.h"
#import "../Identity/ICMUtils.h"
@implementation ICMHTTPClient (Carousels)
+ (void)getCarouselWithId:(NSString *)identifier success:(void (^)(ICMCarousel *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"get a carousel" withFailureBlock:failure]) return;
    NSString *path = [NSString stringWithFormat:@"carousels/%@/fetch", identifier];
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    [engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        ICMCarousel *carousel = [ICMCarousel instanceFromDictionary:response[@"carousel"]];
        success(carousel);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"get a carousel" failureBlock:failure];
    }];
}
+ (void)getCarouselWithInstanceId:(NSString *)identifier success:(void (^)(ICMCarousel *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"get a carousel" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"entity_id"] = identifier;
    parameters[@"entity_type"] = @44;
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    [engine POST:@"content/fetch_carousel" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        ICMCarousel *carousel = [ICMCarousel instanceFromDictionary:response[@"carousel"]];
        success(carousel);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"get a carousel" failureBlock:failure];
    }];
}
+ (void)recordStatistic:(NSString *)statistic withInstanceId:(NSString *)identifier statsMetadata:(NSDictionary *)metadata success:(void (^)(ICMBaseResponse *))success error:(void (^)(NSError *))failure {
    NSString *action = [NSString stringWithFormat:@"record stat for %@", statistic];
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    if (identifier == nil || identifier.length == 0) {
        [self failWithErrorMessage:[NSString stringWithFormat:@"ERROR - Tried to record %@ with an empty instanceId", statistic] recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    [parameters setObject:identifier forKey:@"instance_id"];
    if (metadata != nil && metadata.count > 0) [parameters addEntriesFromDictionary:metadata];
    void (^successBlock)(NSURLSessionDataTask *, id) = ^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMBaseResponse *parsed = [ICMBaseResponse instanceFromDictionary:response];
        if ([NSThread isMainThread]) success(parsed);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(parsed); });
    };
    if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] Recording Stat : [%@] with params : [%@]", statistic, parameters);
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"stats_system/%@", statistic];
    [engine POST:path parameters:parameters success:successBlock failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
@end
@implementation ICMHTTPBridge (Carousels)
+ (void)getCarouselWithId:(NSString *)identifier success:(void (^)(ICMCarousel *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient getCarouselWithId:identifier success:success error:failure];
}
+ (void)getCarouselWithInstanceId:(NSString *)identifier success:(void (^)(ICMCarousel *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient getCarouselWithInstanceId:identifier success:success error:failure];
}
+ (void)recordStatistic:(NSString *)statistic withInstanceId:(NSString *)identifier statsMetadata:(NSDictionary *)metadata {
    [ICMHTTPClient recordStatistic:statistic withInstanceId:identifier statsMetadata:metadata success:nil error:nil];
}
@end
