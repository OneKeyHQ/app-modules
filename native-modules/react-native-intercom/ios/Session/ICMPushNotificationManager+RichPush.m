#import "ICMPushNotificationManager+RichPush.h"
#import "ICMPushPayload.h"
#import "ICMDataManager.h"
#import "ICMConversation.h"
#import "ICMPresentationManager.h"
#import "ICMHTTPClient+ReadAndReactions.h"
#import "ICMHTTPClient+Carousels.h"
#import "ICMHTTPClient+RichPush.h"
#import "ICMLinkOpenerImports.h"
#import "ICMRootDestinationDependencies.h"
#import "ICMMetric.h"
#import "ICMMetricEvent.h"
#import "../Identity/ICMUtils.h"
#import <UniformTypeIdentifiers/UniformTypeIdentifiers.h>
@interface ICMLinkOpener (OriginalPushScheme)
- (BOOL)isCustomSchemeURL:(NSURL *)url;
@end
@interface ICMRecoveredHelpCenterMetricService (OriginalPushMetric)
+ (void)addOpenedFromPushNotification;
@end
@interface ICMPushNotificationManager (OriginalSwiftCommunication)
+ (UNNotificationContent *)communicationNotificationFrom:(UNNotificationContent *)content withAuthorName:(NSString *)authorName avatarLocationURL:(NSURL *)url;
@end
@implementation ICMPushNotificationManager (OriginalRichPush)
+ (void)handleNotificationInForeground:(NSDictionary *)dictionary {
    ICMPushPayload *payload = [ICMPushPayload instanceFromDictionary:dictionary];
    NSInteger type = payload.type;
    NSString *action = nil;
    NSDictionary *info = nil;
    if (type == 0) {
        NSString *identifier = payload.conversationId;
        if (!identifier) return;
        NSString *marked = [ICMDataManager sharedManager].conversationToMark;
        if (marked && [marked isEqualToString:identifier]) return;
        [ICMDataManager sharedManager].conversationToMark = identifier;
        ICMConversation *conversation = [ICMConversation new];
        conversation.conversationId = identifier;
        [[ICMPresentationManager sharedInstance] showConversation:conversation];
        action = @"viewed";
        info = @{@"conversation": conversation, @"push_type": @"notification"};
    } else if (type == 1) {
        NSInteger idType = payload.pushOnlyIdType;
        if (idType == 0) {
            ICMConversation *conversation = [ICMConversation new];
            conversation.conversationId = payload.pushOnlyConversationId;
            [ICMHTTPBridge markConversationAsRead:conversation];
        } else if (idType == 1) {
            [ICMHTTPBridge recordStatistic:@"push_opened" withInstanceId:payload.instanceId statsMetadata:nil];
        }
        info = @{@"instance_id": payload.instanceId ?: @"", @"conversation_id": payload.pushOnlyConversationId ?: @"", @"push_type": @"message"};
        NSURL *url = [NSURL URLWithString:payload.uri];
        if (url && ([[ICMLinkOpener shared] isCustomSchemeURL:url] || [[ICMLinkOpener shared] isUniversalLink:url] || [[ICMLinkOpener shared] isHelpCenterLink:url])) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [[ICMLinkOpener shared] openLink:url];
                if ([[ICMLinkOpener shared] isHelpCenterLink:url]) [ICMRecoveredHelpCenterMetricService addOpenedFromPushNotification];
            });
        }
        action = @"opened";
    } else return;
    if (action && info) {
        ICMMetric *metric = [ICMMetric sharedInstance];
        ICMMetricEvent *event = [ICMMetricEvent eventWithAction:action object:@"message" place:@"in_app" context:@"from_push" additionalInfo:info];
        [metric addEvent:event];
    }
}
+ (void)handleIntercomRichPushNotificationContent:(UNMutableNotificationContent *)content withContentHandler:(void (^)(UNNotificationContent *))handler {
    ICMPushPayload *payload = [ICMPushPayload instanceFromDictionary:content.userInfo];
    NSUInteger authorLength = payload.authorName.length;
    BOOL hasImage = payload.contentImageURL != nil;
    if (authorLength > 0 && hasImage) {
        NSURL *imageURL = payload.contentImageURL;
        NSString *author = payload.authorName;
        [self downloadAvatarAndDeliverCommunicationNotification:content imageURL:imageURL authorName:author contentHandler:handler];
    } else if (authorLength > 0) {
        [self deliverCommunicationNotification:content authorName:payload.authorName avatarLocationURL:nil contentHandler:handler];
    } else if (hasImage) {
        [self downloadAndAttachMedia:payload.contentImageURL toContent:content contentHandler:handler];
    } else handler([content mutableCopy]);
}
+ (void)downloadAvatarAndDeliverCommunicationNotification:(UNNotificationContent *)content imageURL:(NSURL *)imageURL authorName:(NSString *)authorName contentHandler:(void (^)(UNNotificationContent *))handler {
    [ICMHTTPBridge downloadRichPushMediaAttachmentFrom:imageURL success:^(NSURL *url) {
        [self deliverCommunicationNotification:content authorName:authorName avatarLocationURL:url contentHandler:handler];
    } failure:^(NSError *error) {
        if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] Avatar download failed, attempting communication notification without avatar. Error: %@", error.localizedDescription);
        [self deliverCommunicationNotification:content authorName:authorName avatarLocationURL:nil contentHandler:handler];
    }];
}
+ (void)downloadAndAttachMedia:(NSURL *)imageURL toContent:(UNNotificationContent *)content contentHandler:(void (^)(UNNotificationContent *))handler {
    [ICMHTTPBridge downloadRichPushMediaAttachmentFrom:imageURL success:^(NSURL *url) {
        UNMutableNotificationContent *copy = [content mutableCopy];
        UNNotificationAttachment *attachment = [self attachmentFromFileURL:url];
        if (attachment) {
            copy.attachments = @[attachment];
            if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] Downloaded rich push notification attachment.");
        }
        handler(copy);
    } failure:^(NSError *error) {
        NSLog(@"[Intercom] ERROR - Failed to download rich push notification attachment. Error: %@", error.localizedDescription);
        handler([content mutableCopy]);
    }];
}
+ (void)deliverCommunicationNotification:(UNNotificationContent *)content authorName:(NSString *)authorName avatarLocationURL:(NSURL *)avatarURL contentHandler:(void (^)(UNNotificationContent *))handler {
    UNNotificationContent *communication = [self communicationNotificationFrom:content withAuthorName:authorName avatarLocationURL:avatarURL];
    if (communication) { handler(communication); return; }
    UNMutableNotificationContent *copy = [content mutableCopy];
    if (avatarURL) {
        UNNotificationAttachment *attachment = [self attachmentFromFileURL:avatarURL];
        if (attachment) copy.attachments = @[attachment];
    }
    handler(copy);
}
+ (UNNotificationAttachment *)attachmentFromFileURL:(NSURL *)url {
    NSString *extension = url.pathExtension;
    NSDictionary *options = nil;
    if (extension.length > 0) {
        UTType *type = [UTType typeWithFilenameExtension:extension];
        if (type) options = @{UNNotificationAttachmentOptionsTypeHintKey: type.identifier};
    }
    NSError *error = nil;
    UNNotificationAttachment *attachment = [UNNotificationAttachment attachmentWithIdentifier:@"intercom-attachment" URL:url options:options error:&error];
    if (error) {
        NSLog(@"[Intercom] ERROR - Could not create notification attachment. Error: %@", error.localizedDescription);
        return nil;
    }
    return attachment;
}
@end
