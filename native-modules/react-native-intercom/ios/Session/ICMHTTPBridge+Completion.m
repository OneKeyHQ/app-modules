#import "ICMHTTPBridge+Completion.h"
#import "ICMHTTPClient+ConversationForms.h"
#import "ICMHTTPClient+ConversationSending.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "ICMMetric.h"
#import "ICMMetricEvent.h"
#import "../Identity/ICMDeviceData.h"
@implementation ICMHTTPBridge (OriginalCompletion)
+ (void)rateConversation:(NSString *)identifier withRatingIndex:(NSUInteger)index error:(void (^)(NSError *))failure {
    [ICMHTTPClient rateConversation:identifier withRatingIndex:index error:failure];
}
+ (void)addConversationRatingRemarkForConversation:(NSString *)identifier remark:(NSString *)remark error:(void (^)(NSError *))failure {
    [ICMHTTPClient addConversationRatingRemarkForConversation:identifier remark:remark error:failure];
}
+ (void)quickReplyToConversation:(NSString *)identifier clientAssignedUUID:(NSString *)uuid withPartId:(NSString *)partId withOptionUUID:(NSString *)optionId success:(void (^)(ICMConversationPart *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient quickReplyToConversation:identifier clientAssignedUUID:uuid withPartId:partId withOptionUUID:optionId success:success error:failure];
}
+ (void)submitFormParams:(NSDictionary *)form forConversation:(NSString *)identifier success:(void (^)(ICMConversation *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient submitFormParams:form forConversation:identifier success:success error:failure];
}
+ (void)triggerInboundBotWithOptionUUID:(NSString *)optionId clientAssignedUUID:(NSString *)uuid success:(void (^)(ICMConversation *))success error:(void (^)(NSError *))failure {
    [ICMHTTPClient triggerInboundBotWithOptionUUID:optionId clientAssignedUUID:uuid success:success error:failure];
}
+ (void)markConversationAsDismissed:(NSString *)identifier { [ICMHTTPClient markConversationAsDismissed:identifier success:nil error:nil]; }
+ (void)messageSentMetric:(ICMConversation *)conversation upload:(ITBUpload *)upload closedConversationId:(NSString *)identifier {
    NSMutableDictionary *info = [@{@"conversation": conversation,
                                  @"conversation_part": conversation.conversationParts.lastObject,
                                  @"is_attachment": [NSNumber numberWithInt:upload != nil],
                                  @"is_annotated": [NSNumber numberWithBool:upload.isAnnotated],
                                  @"is_sticker": @NO,
                                  @"is_gif": [NSNumber numberWithInt:((ICMConversationPart *)conversation.conversationParts.lastObject).attribution != nil]} mutableCopy];
    NSString *context;
    if (identifier) { info[@"closed_conversation_id"] = identifier; context = @"in_followup_conversation"; }
    else context = @"in_new_conversation";
    ICMMetric *metric = [ICMMetric sharedInstance];
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"sent" object:@"message" place:@"messenger" context:context additionalInfo:info];
    [metric addEvent:event];
}
+ (void)messageOpenedMetric:(ICMConversation *)conversation closedConversationId:(NSString *)identifier {
    NSMutableDictionary *info = [@{@"conversation": conversation} mutableCopy];
    NSString *context;
    if (identifier) { info[@"closed_conversation_id"] = identifier; context = @"from_closed_conversation"; }
    else context = @"from_new_conversation";
    ICMMetric *metric = [ICMMetric sharedInstance];
    ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"opened" object:@"conversation" place:@"messenger" context:context additionalInfo:info];
    [metric addEvent:event];
}
@end
@implementation ICMHTTPClient (OriginalDismissal)
+ (void)markConversationAsDismissed:(NSString *)identifier success:(void (^)(void))success error:(void (^)(NSError *))failure {
    if ([self bailIfMissingIdentityWhenTryingTo:@"dismiss a conversation" withFailureBlock:failure]) return;
    if (!identifier || identifier.length == 0) {
        [self failWithErrorMessage:@"ERROR - Tried to dismiss a conversation with an empty conversation id" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"conversation_ids"] = @[identifier];
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    [engine POST:@"conversations/dismiss" parameters:parameters success:^(NSURLSessionDataTask *task, id payload) {
        __attribute__((objc_precise_lifetime)) NSURLSessionDataTask *retainedTask = task;
        __attribute__((objc_precise_lifetime)) id retainedPayload = payload;
        (void)retainedTask; (void)retainedPayload;
        if (!success) return;
        if ([NSThread isMainThread]) success();
        else dispatch_async(dispatch_get_main_queue(), success);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:@"dismiss a conversation" failureBlock:failure];
    }];
}
@end
