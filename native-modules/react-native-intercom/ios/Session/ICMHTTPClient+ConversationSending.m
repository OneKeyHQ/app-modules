#import "ICMHTTPClient+ConversationSending.h"
#import "ICMIdentityStore.h"
#import "ICMDataManager.h"
#import "ICMOreoEngine.h"
#import "../Identity/ICMDeviceData.h"

@implementation ICMHTTPClient (ConversationSending)
+ (void)createConversationWithBlocks:(NSArray *)blocks clientAssignedUUID:(NSString *)uuid articleMetaData:(ICMRecoveredArticleMetaData *)article composerSuggestions:(ICMComposerSuggestions *)suggestions success:(void (^)(ICMConversation *))success error:(void (^)(NSError *))failure {
    NSString *action = @"create a conversation";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    if (uuid != nil) parameters[@"client_assigned_uuid"] = uuid;
    if (blocks.count != 0) parameters[@"blocks"] = blocks;
    if ([ICMDataManager sharedManager].resolutionBotBehaviourId != nil) parameters[@"resolution_bot_behavior_version_id"] = [ICMDataManager sharedManager].resolutionBotBehaviourId;
    if ([ICMDataManager sharedManager].openRequestId != nil) parameters[@"messenger_open_request_id"] = [ICMDataManager sharedManager].openRequestId;
    NSArray *uuids = [suggestions suggestionsUUIDArray];
    if (uuids.count != 0) parameters[@"composer_suggestions"] = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:uuids options:0 error:NULL] encoding:NSUTF8StringEncoding];
    if (suggestions.snapshotId != nil) parameters[@"snapshot_id"] = suggestions.snapshotId;
    NSString *botIntro = [ICMDataManager sharedManager].botIntroId;
    if (botIntro != nil) parameters[@"bot_intro"] = botIntro;
    if (article != nil) {
        if (article.articleId != nil) parameters[@"article_id"] = article.articleId;
        if (article.articleSource != nil) parameters[@"article_source"] = article.articleSource;
    }
    if (parameters.count == 2) {
        [self failWithErrorMessage:@"ERROR - Tried to create a conversation with no data" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    [engine POST:@"conversations" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMConversation *conversation = [ICMConversation instanceFromDictionary:response partDisplayStyle:1];
        if ([NSThread isMainThread]) success(conversation);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(conversation); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}

+ (void)replyToConversation:(NSString *)identifier clientAssignedUUID:(NSString *)uuid withBlocks:(NSArray *)blocks nexusMetadata:(NSDictionary *)metadata lastAdminPartCreatedAt:(NSNumber *)timestamp success:(void (^)(ICMConversationPart *))success error:(void (^)(NSError *))failure {
    NSString *action = @"reply to a conversation";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"client_assigned_uuid"] = uuid;
    parameters[@"nexus_connection_status"] = metadata;
    if (timestamp != nil) parameters[@"last_admin_part_created_at"] = timestamp;
    if (blocks.count != 0) parameters[@"blocks"] = blocks;
    if (parameters.count == 2) {
        [self failWithErrorMessage:@"ERROR - Tried to reply to a conversation with no data" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"conversations/%@/reply", identifier];
    [engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMConversationPart *part = [ICMConversationPart instanceFromDictionary:response displayStyle:1];
        if ([NSThread isMainThread]) success(part);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(part); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}

+ (void)quickReplyToConversation:(NSString *)identifier clientAssignedUUID:(NSString *)uuid withPartId:(NSString *)partId withOptionUUID:(NSString *)optionId success:(void (^)(ICMConversationPart *))success error:(void (^)(NSError *))failure {
    NSString *action = @"quick reply to a conversation";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"client_assigned_uuid"] = uuid;
    if (partId != nil) parameters[@"quick_reply_part_id"] = partId;
    parameters[@"reply_option_uuid"] = optionId;
    if (parameters.count == 2 || optionId.length == 0) {
        [self failWithErrorMessage:@"ERROR - Tried to quick reply to a conversation with no data" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    NSString *path = [NSString stringWithFormat:@"conversations/%@/quick_reply", identifier];
    [engine POST:path parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMConversationPart *part = [ICMConversationPart instanceFromDictionary:response displayStyle:1];
        if ([NSThread isMainThread]) success(part);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(part); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}

+ (void)triggerInboundBotWithOptionUUID:(NSString *)optionId clientAssignedUUID:(NSString *)uuid success:(void (^)(ICMConversation *))success error:(void (^)(NSError *))failure {
    NSString *action = @"trigger inbound bot from composer suggestion";
    if ([self bailIfMissingIdentityWhenTryingTo:action withFailureBlock:failure]) return;
    NSMutableDictionary *parameters = [[ICMIdentityStore currentIdentity].userIdentity.dictionaryRepresentation mutableCopy];
    parameters[@"device_identifier"] = [ICMDeviceData deviceIdentifier];
    parameters[@"client_assigned_uuid"] = uuid;
    if (optionId != nil && optionId.length != 0) parameters[@"id"] = optionId;
    if ([ICMDataManager sharedManager].openRequestId != nil) parameters[@"messenger_open_request_id"] = [ICMDataManager sharedManager].openRequestId;
    if (parameters[@"id"] == nil) {
        [self failWithErrorMessage:@"ERROR - Tried to trigger an inbound bot without a suggestion ID" recoverySuggestion:nil errorCode:400 underlyingError:nil failureBlock:failure];
        return;
    }
    ICMOreoEngine *engine = [ICMIdentityStore currentIdentity].appIdentity.engine;
    [engine POST:@"custom_bots/trigger_inbound_conversation" parameters:parameters success:^(NSURLSessionDataTask *task, id response) {
        (void)task;
        if (success == nil) return;
        ICMConversation *conversation = [ICMConversation instanceFromDictionary:response partDisplayStyle:1];
        if ([NSThread isMainThread]) success(conversation);
        else dispatch_async(dispatch_get_main_queue(), ^{ success(conversation); });
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        (void)task;
        [ICMHTTPClient failWithNetworkError:error whenTryingTo:action failureBlock:failure];
    }];
}
@end
