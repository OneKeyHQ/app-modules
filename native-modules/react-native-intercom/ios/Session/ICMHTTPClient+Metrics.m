#import "ICMHTTPClient+Metrics.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (OriginalMetrics)
+ (void)submitMetrics:(NSArray *)metrics opsMetrics:(NSArray *)ops success:(void (^)(void))success error:(void (^)(NSError *))failure {
    if (![metrics isKindOfClass:[NSArray class]] || ![NSJSONSerialization isValidJSONObject:metrics] || ![ops isKindOfClass:[NSArray class]] || ![NSJSONSerialization isValidJSONObject:ops]) {
        [self failWithErrorMessage:@"ERROR - Tried to submit invalid metricss" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    if (!metrics.count && !ops.count) { if (success) success(); return; }
    if ([self bailIfMissingIdentityWhenTryingTo:@"send metrics" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [NSMutableDictionary new];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"metrics"] = metrics;
    parameters[@"op_metrics"] = ops;
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"metrics" parameters:parameters success:^(NSURLSessionTask *task, id response) {
        (void)task; (void)response;
        if (success) success();
    } failure:^(NSURLSessionTask *task, NSError *error) {
        (void)task;
        if (failure) failure(error);
    }];
}
+ (void)submitDiagnostics:(NSArray *)diagnostics success:(void (^)(void))success error:(void (^)(NSError *))failure {
    if (![diagnostics isKindOfClass:[NSArray class]] || ![NSJSONSerialization isValidJSONObject:diagnostics]) {
        [self failWithErrorMessage:@"ERROR - Tried to submit invalid diagnostics" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    if (!diagnostics.count) { if (success) success(); return; }
    if ([self bailIfMissingIdentityWhenTryingTo:@"send diagnostics" withFailureBlock:failure]) return;
    ICMUserIdentity *identity = [ICMIdentityStore currentIdentity].userIdentity;
    NSMutableDictionary *parameters = [identity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"diagnostics"] = diagnostics;
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"error_reports" parameters:parameters success:^(NSURLSessionTask *task, id response) {
        (void)task; (void)response;
        if (success) {
            if ([NSThread isMainThread]) success();
            else dispatch_async(dispatch_get_main_queue(), success);
        }
    } failure:^(NSURLSessionTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"send diagnostics" failureBlock:failure];
    }];
}
@end
