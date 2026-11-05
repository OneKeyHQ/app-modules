#import "ICMHTTPClient.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (HelpCenter)
+ (void)getHelpCenterCollections:(void (^)(id))success error:(void (^)(NSError *))failure {
    NSString *action = @"get help center collections";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"help_center/collections" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        success(response);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
+ (void)getHelpCenterCollection:(NSString *)identifier success:(void (^)(id))success error:(void (^)(NSError *))failure {
    NSString *action = @"get help center collections";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSString *path = [NSString stringWithFormat:@"help_center/collections/%@", identifier];
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        success(response);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
+ (void)getHelpCenterSearchResults:(NSString *)phrase source:(NSString *)source success:(void (^)(id))success error:(void (^)(NSError *))failure {
    NSString *action = @"get help center collections for search term";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"phrase"] = phrase;
    parameters[@"article_source"] = source;
    [[ICMIdentityStore currentIdentity].appIdentity.engine POST:@"help_center/search" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        success(response);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
@end
