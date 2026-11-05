#import "ICMHTTPClient+TicketCreation.h"
#import "ICMConversationDependencies.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPClient (TicketCreation)
+ (void)getTicketFormWithRequest:(ICMRecoveredTicketFormRequest *)request success:(void (^)(ICMRecoveredTicketFormResponse *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"get ticket form" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"ticket_type_id"] = request.ticketTypeID;
    parameters[@"attributes"] = request.attributesDictionary;
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    [engine POST:@"tickets/ticket_form" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        ICMRecoveredTicketFormResponse *form = [[ICMRecoveredTicketFormResponse alloc] initWith:response];
        success(form);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"get ticket form" failureBlock:failure];
    }];
}
+ (void)createTicketTypeId:(NSString *)identifier conversationId:(NSString *)conversationId attributes:(NSArray<NSDictionary *> *)attributes success:(void (^)(ICMRecoveredTicket *))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"create ticket" withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"type_id"] = identifier;
    if (conversationId != nil) parameters[@"conversation_id"] = conversationId;
    parameters[@"attributes"] = attributes;
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    [engine POST:@"tickets/create" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        ICMRecoveredTicket *ticket = [[ICMRecoveredTicket alloc] initWithDictionary:response];
        success(ticket);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"create ticket" failureBlock:failure];
    }];
}
@end
