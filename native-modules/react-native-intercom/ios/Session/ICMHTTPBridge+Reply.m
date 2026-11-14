#import "ICMHTTPBridge+Reply.h"
#import "ICMHTTPBridge+UploadBlocks.h"
#import "ICMHTTPClient+ConversationSending.h"
#import "IntercomNexusClient.h"
#import "ICMTeamPresence.h"
#import "ICMChatNotificationContentImports.h"
#import "ICMMetric.h"
#import "ICMMetricEvent.h"
@interface ICMRecoveredParticipatingAdmin (OriginalReplyStatus)
@property (nonatomic, readonly) BOOL isActive;
@property (nonatomic, readonly) NSDate *lastActiveAt;
@end
@interface ICMTimestampCalculator (OriginalReplyTimestamp)
- (NSString *)lastActiveMinutesAgo:(NSDate *)date;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate17NexusEventFactory")))
__attribute__((swift_name("NexusEventFactory")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredNexusEventFactory : NSObject
+ (IntercomNexusEvent *)createNexusEventWith:(NSUInteger)type conversationId:(NSString *)conversationId;
@end
@implementation ICMHTTPBridge (OriginalReply)
+ (void)replyToConversation:(ICMConversation *)conversation clientAssignedUUID:(NSString *)uuid withBlocks:(NSArray *)blocks upload:(ITBUpload *)upload lastAdminPartCreatedAt:(NSNumber *)timestamp success:(void (^)(ICMConversationPart *))success error:(void (^)(NSError *))failure {
    if (conversation.conversationId.length == 0) {
        NSString *message = @"ERROR - Tried to reply a conversation with an empty conversation id";
        NSLog(@"[Intercom] %@", message);
        if (failure) failure([NSError errorWithDomain:message code:400 userInfo:nil]);
        return;
    }
    [ICMHTTPBridge createUploadWithUpload:upload success:^(NSString *url) {
        NSArray *sendable = [self makeSendableBlocks:blocks withPublicURL:url upload:upload];
        [ICMHTTPClient replyToConversation:conversation.conversationId clientAssignedUUID:uuid withBlocks:sendable nexusMetadata:[[IntercomNexusClient sharedClient] getConnectionStatusMetadata] lastAdminPartCreatedAt:timestamp success:^(ICMConversationPart *part) {
            if (upload.cancelled) {
                if (failure) failure([NSError errorWithDomain:@"Upload cancelled" code:-999 userInfo:nil]);
                return;
            }
            upload.uploadProgress = 1;
            IntercomNexusEvent *event = [ICMRecoveredNexusEventFactory createNexusEventWith:6 conversationId:conversation.conversationId];
            [[IntercomNexusClient sharedClient] sendEvent:event];
            if (success) success(part);
            NSString *identifier = conversation.conversationId;
            if (!part || !identifier) return;
            NSString *status = conversation.lastAdmin.isActive ? @"active" : @"away";
            NSString *lastActive = [[ICMTimestampCalculator sharedInstance] lastActiveMinutesAgo:conversation.lastAdmin.lastActiveAt];
            ICMMetricEvent *metricEvent = [ICMMetricEvent eventWithAction:@"sent" object:@"reply" place:@"messenger" context:@"in_conversation" additionalInfo:@{
                @"conversation": conversation, @"conversation_part": part,
                @"is_attachment": [NSNumber numberWithInt:upload != nil],
                @"is_annotated": [NSNumber numberWithBool:upload.isAnnotated],
                @"is_sticker": @NO,
                @"is_gif": [NSNumber numberWithInt:part.attribution != nil],
                @"teammate_status": status, @"time_since_last_active": lastActive}];
            [[ICMMetric sharedInstance] addEvent:metricEvent];
        } error:^(NSError *error) {
            if (failure) failure(error);
        }];
    } failure:^(NSError *error) {
        if (failure) failure(error);
    }];
}
@end
