// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
#import "ICMHTTPClient+ReadAndReactions.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "ICMDataManager.h"
#import "ICMDataManagerDependencies.h"
#import "ICMDataManager+Conversations.h"
#import "ICMConversationPart.h"
#import "ICMReactions.h"
#import "ICMMetric.h"
#import "ICMMetricEvent.h"
#import "../Identity/ICMDeviceData.h"

@implementation ICMHTTPBridge (ReadAndReactions)
+ (void)markConversationAsRead:(ICMConversation *)conversation {
    if (conversation.conversationId != nil) [[ICMDataManager sharedManager].unreadManager conversationIdRead:conversation.conversationId];
    conversation.read = YES;
    if (conversation.conversationId != nil) [ICMHTTPClient markConversationAsRead:conversation.conversationId success:nil error:nil];
}
+ (void)reactToConversation:(ICMConversation *)conversation reactionIndex:(NSUInteger)index fromInApp:(BOOL)fromInApp {
    NSNumber *selected = [NSNumber numberWithUnsignedInteger:index];
    ICMConversationPart *first = conversation.conversationParts.firstObject;
    first.reactionSet.selectedIndex = selected;
    [ICMHTTPClient reactToConversation:conversation.conversationId withReactionIndex:index];
    ICMConversation *copy = [conversation copy];
    [[ICMDataManager sharedManager] updateWithConversation:conversation];
    if ([copy lastPart] != nil) {
        NSString *context = fromInApp ? @"from_full" : @"from_conversation";
        NSString *place = fromInApp ? @"in_app" : @"messenger";
        ICMMetric *metric = [ICMMetric sharedInstance];
        ICMMetricEvent *event = [ICMMetricEvent eventWithAction:@"sent" object:@"reaction" place:place context:context additionalInfo:@{@"conversation": copy, @"conversation_part": [copy lastPart]}];
        [metric addEvent:event];
    }
}
@end

@implementation ICMHTTPClient (ReadAndReactions)
+ (void)markConversationAsRead:(NSString *)identifier success:(void (^)(ICMBaseResponse *))success error:(void (^)(NSError *))failure {
    NSString *action = @"mark a conversation as read";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    if (identifier == nil || identifier.length == 0) {
        [self failWithErrorMessage:@"ERROR - Tried to mark a conversation as read with an empty conversation id" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    void (^onSuccess)(NSURLSessionDataTask *, id) = ^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMBaseResponse *model = [ICMBaseResponse instanceFromDictionary:response];
        if ([NSThread isMainThread]) success(model);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(model); });
    };
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"conversations/%@/read", identifier];
    [engine POST:path parameters:parameters success:onSuccess failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
+ (void)reactToConversation:(NSString *)identifier withReactionIndex:(NSUInteger)index {
    NSString *action = @"react to a conversation";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:nil]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"reaction_index"] = [NSNumber numberWithUnsignedInteger:index];
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"conversations/%@/react", identifier];
    [engine POST:path parameters:parameters success:nil failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:nil];
    }];
}
@end
