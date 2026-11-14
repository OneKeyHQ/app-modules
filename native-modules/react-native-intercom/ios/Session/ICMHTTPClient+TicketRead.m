#import "ICMHTTPClient+TicketRead.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (TicketRead)
+ (void)markTicketAsRead:(NSString *)identifier success:(void (^)(ICMBaseResponse *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"mark a ticket as read" withFailureBlock:failure]) return;
    if (identifier == nil || identifier.length == 0) {
        [self failWithErrorMessage:@"ERROR - Tried to mark a ticket as read with an empty ticket id" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    void (^received)(NSURLSessionDataTask *, id) = ^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMBaseResponse *parsed = [ICMBaseResponse instanceFromDictionary:response];
        if ([NSThread isMainThread]) success(parsed);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(parsed); });
    };
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"tickets/%@/read", identifier];
    [engine POST:path parameters:parameters success:received failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"mark a ticket as read" failureBlock:failure];
    }];
}
@end
