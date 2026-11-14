#import "ICMHTTPClient+TicketList.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (TicketList)
+ (void)getTicketsAtPage:(NSInteger)page pageSize:(NSInteger)pageSize success:(void (^)(ICMRecoveredTicketsPageResponse *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"get tickets for display in the tickets list" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    if (page != 0) parameters[@"page"] = @(page);
    parameters[@"per_page"] = pageSize > 0 ? @(pageSize) : @10;
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    [engine POST:@"tickets" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMRecoveredTicketsPageResponse *value = [[ICMRecoveredTicketsPageResponse alloc] initWithDictionary:response];
        if ([NSThread isMainThread]) success(value);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(value); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"get tickets for display in the tickets list" failureBlock:failure];
    }];
}
@end
@implementation ICMHTTPBridge (TicketList)
+ (void)getTicketsAtPage:(NSInteger)page pageSize:(NSInteger)pageSize success:(void (^)(ICMRecoveredTicketsPageResponse *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient getTicketsAtPage:page pageSize:pageSize success:^(ICMRecoveredTicketsPageResponse *value) {
        if (success != nil) success(value);
    } error:^(NSError *error) {
        if (failure != nil) failure(error);
    }];
}
@end
