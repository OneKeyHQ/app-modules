// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
#import "ICMHTTPClient+TicketDetails.h"
#import "ICMHTTPClient+TicketRead.h"
#import "ICMConversationDependencies.h"
#import "ICMIdentityStore.h"
#import "ICMDataManager.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (TicketDetails)
+ (void)getTicketDetailsForTicketId:(NSString *)identifier success:(void (^)(ICMRecoveredTicket *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"get ticket details" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"tickets/%@", identifier];
    [engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMRecoveredTicket *ticket = [[ICMRecoveredTicket alloc] initWithDictionary:response];
        if ([NSThread isMainThread]) success(ticket);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(ticket); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"get ticket details" failureBlock:failure];
    }];
}
@end
@implementation ICMHTTPBridge (TicketDetails)
+ (void)getTicketDetailsForTicketId:(NSString *)identifier success:(void (^)(ICMRecoveredTicket *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient getTicketDetailsForTicketId:identifier success:^(ICMRecoveredTicket *ticket) {
        if (success != nil) success(ticket);
    } error:^(NSError *error) {
        if (failure != nil) failure(error);
    }];
}
+ (void)markTicketAsReadWithID:(NSString *)identifier {
    if (identifier == nil) return;
    [[ICMDataManager sharedManager].unreadManager ticketIdRead:identifier];
    [ICMHTTPClient markTicketAsRead:identifier success:^(ICMBaseResponse *response) {
        (void)response;
        [[NSNotificationCenter defaultCenter] postNotificationName:@"intercom-sdk-tickets-updated" object:nil];
    } error:nil];
}
@end
