// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMHTTPClient.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
#import "../Identity/ICMSheet.h"
@implementation ICMHTTPClient (Sheets)
+ (void)fetchMessengerSheetData:(NSDictionary *)input success:(void (^)(ICMSheet *))success failure:(void (^)(NSError *))failure {
    NSString *action = @"fetch messenger sheet data";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    [parameters addEntriesFromDictionary:input];
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"sheets/open" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (!success) return;
        ICMSheet *sheet = [ICMSheet instanceFromDictionary:response];
        if ([NSThread isMainThread]) success(sheet); else dispatch_async(dispatch_get_main_queue(), ^{ success(sheet); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
+ (void)submitMessengerSheet:(NSDictionary *)values uri:(NSString *)uri success:(void (^)(void))success failure:(void (^)(NSError *))failure {
    NSString *action = @"submit messenger sheet";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"sheet_values"] = values;
    parameters[@"uri"] = uri;
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"sheets/submit" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task; (void)response;
        if (!success) return;
        if ([NSThread isMainThread]) success(); else dispatch_async(dispatch_get_main_queue(), success);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
@end
