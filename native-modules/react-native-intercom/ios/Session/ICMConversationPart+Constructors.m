// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMConversationPart.h"
#import "ICMConversation.h"
#import "ICMConversationPartDependencies.h"
#import "ICMIdentityStore.h"
@implementation ICMConversationPart (Constructors)
+ (void)setParticipantDetails:(IntercomNexusEvent *)event onPart:(ICMConversationPart *)part {
    NSString *name = event.adminName;
    id avatar;
    if (event.adminAvatar) avatar = @{@"image_url": event.adminAvatar};
    else avatar = [NSNull null];
    ICMRecoveredParticipatingAdmin *participant = [[ICMRecoveredParticipatingAdmin alloc] initWith:@{@"name": name, @"avatar": avatar}];
    part.participant = (ICMParticipant *)participant;
    part.participantIsAdmin = YES;
}
+ (instancetype)sendingPartWithBlocks:(NSArray *)blocks upload:(ITBUpload *)upload {
    ICMConversationPart *part = [self new];
    part.displayStyle = 1;
    part.summary = blocks.firstObject[@"text"] ?: @"";
    part.state = 1;
    part.createdAt = [NSDate date];
    part.participantIsAdmin = NO;
    part.participantId = [ICMIdentityStore currentIdentity].userIdentity.intercomId;
    NSString *uuid = [NSUUID UUID].UUIDString;
    part.partId = uuid;
    part.clientAssignedUUID = uuid;
    part.displayTimestamp = YES;
    NSUInteger count = blocks.count;
    if (upload) {
        if (count) [part setAttributesFromDictionary:@{@"body": blocks}];
        ITBBlock *block = [ITBBlockUtils blockForUpload:upload appearance:[ICMConversationPart blockAppearanceForPart:part]];
        if (block) {
            NSMutableArray *all = [NSMutableArray arrayWithArray:part.blocks ?: @[]];
            [all addObject:block];
            part.blocks = all;
        }
        part.upload = upload;
        if (part.partStyle == 0) {
            if (part.blocks.count == 1) [(ITBBlock *)part.blocks.firstObject setDisplayMode:1];
            else for (ITBBlock *item in part.blocks) item.displayMode = 0;
        }
    } else if (count) {
        [part setAttributesFromDictionary:@{@"body": blocks}];
    } else {
        part.blocks = @[];
    }
    return part;
}
+ (instancetype)botPartWithBlocks:(NSArray *)blocks participant:(ICMParticipant *)participant {
    ICMConversationPart *part = [self new];
    part.displayStyle = 1;
    part.summary = [(ITBBlock *)blocks.firstObject text] ?: @"";
    part.state = 0;
    part.createdAt = [NSDate date];
    part.participantIsAdmin = YES;
    part.partId = [NSUUID UUID].UUIDString;
    part.displayTimestamp = YES;
    part.blocks = blocks;
    part.participant = participant;
    part.participantId = participant.id;
    return part;
}
+ (instancetype)streamingPartWithBlocks:(NSArray *)blocks {
    ICMConversationPart *part = [self new];
    part.displayStyle = 1;
    part.summary = [(ITBBlock *)blocks.firstObject text] ?: @"";
    part.state = 0;
    part.createdAt = [NSDate date];
    part.participantIsAdmin = YES;
    part.partId = [NSUUID UUID].UUIDString;
    part.displayTimestamp = YES;
    part.blocks = blocks;
    ICMRecoveredActiveBot *bot = [ICMTeamPresence sharedInstance].activeBot;
    part.participant = bot.participant;
    part.participantId = [(ICMParticipant *)bot.participant id];
    part.partMetadata = bot.streamingPart.metadata;
    part.partStyle = 7;
    return part;
}
+ (instancetype)temporaryArticlePartWith:(ICMRecoveredArticleMetaData *)article helpCenterURLString:(NSString *)url {
    NSString *articleId = article.articleId;
    NSString *title = article.articleTitle;
    if (!articleId || !title) return nil;
    NSString *articleURL = [url stringByAppendingPathComponent:[NSString stringWithFormat:@"/articles/%@", articleId]];
    NSDictionary *dictionary = @{@"body": @[@{@"type": @"link", @"text": @"text", @"linkType": @"educate.article",
                                             @"articleId": articleId, @"url": articleURL, @"title": title}],
                                 @"is_initial_message": @YES, @"message_style": @"chat", @"id": @"1"};
    ICMConversationPart *part = [self instanceFromDictionary:dictionary displayStyle:1];
    part.createdAt = article.createdAt;
    NSString *uuid = [NSUUID UUID].UUIDString;
    part.partId = uuid;
    part.clientAssignedUUID = uuid;
    return part;
}
@end
