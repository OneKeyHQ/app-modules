#import "ICMConversationPart.h"
#import "ICMConversationPartDependencies.h"
#import "ICMIdentityStore.h"
@implementation ICMConversationPart (Classification)
- (BOOL)isTypingPart { return [(ITBBlock *)self.blocks.firstObject type] == 16; }
- (BOOL)hasAttachments {
    for (ITBBlock *block in self.blocks) if (block.type == 14 || block.type == 11) return YES;
    return NO;
}
- (BOOL)hasMessengerCard {
    for (ITBBlock *block in self.blocks) if (block.type == 18) return YES;
    return NO;
}
- (BOOL)isLinkContextCard { return self.isLinkCard && self.isInitialMessage; }
- (BOOL)isLinkCard {
    if (self.partStyle != 0) return NO;
    return self.blocks.count == 1 && [(ITBBlock *)self.blocks.firstObject type] == 12;
}
- (BOOL)isConversationRatingCard { return self.blocks.count == 1 && [(ITBBlock *)self.blocks.firstObject type] == 15; }
- (BOOL)isLinkListCard { return self.blocks.count == 1 && [(ITBBlock *)self.blocks.firstObject type] == 17; }
- (BOOL)isArticleContextCard { return self.hasArticleCard && self.isLinkContextCard; }
- (BOOL)isSingleMessengerCard {
    if (self.partStyle != 0) return NO;
    return self.blocks.count == 1 && [(ITBBlock *)self.blocks.firstObject type] == 18;
}
- (BOOL)isQuickReply { return self.partStyle == 3 && self.replyOptionsArray.count != 0; }
- (BOOL)isComposerSuggestions { return self.partStyle == 5 && self.replyOptionsArray.count != 0; }
- (BOOL)hasReplyOptions { return self.replyOptionsArray.count != 0; }
- (BOOL)isSingleImagePart {
    if (self.partMetadata != nil) return NO;
    return self.blocks.count == 1 && [(ITBBlock *)self.blocks.firstObject type] == 5;
}
- (BOOL)isSingleAttachmentPart {
    if (self.partMetadata != nil) return NO;
    return self.blocks.count == 1 && [(ITBBlock *)self.blocks.firstObject type] == 14;
}
- (BOOL)isSingleMediaPart {
    BOOL personal = self.isPersonalPart;
    BOOL image = self.isSingleImagePart;
    if (personal && !image) return self.isSingleAttachmentPart;
    return personal | image;
}
- (BOOL)isCreateTicketPart {
    ITBBlock *block = self.blocks.firstObject;
    if (!block) return NO;
    return self.blocks.count == 1 && block.type == 22;
}
- (BOOL)isPersonalPart {
    if (!self.participantId) return YES;
    return [self.participantId isEqualToString:[ICMIdentityStore currentIdentity].userIdentity.intercomId];
}
- (BOOL)isInitialMessageQuickReply {
    if (self.partStyle == 3 || self.partStyle == 5) return NO;
    return self.replyOptionsArray.count != 0;
}
- (BOOL)isAttributeCollector { return self.partStyle == 4 && self.form.formAttributes.count != 0; }
- (BOOL)isEvent { return [self.eventData isValidEvent]; }
- (BOOL)isTemporaryExpectation { return self.expectationsText != nil; }
- (BOOL)isVideoFile { return self.blocks.count == 1 && [(ITBBlock *)self.blocks.firstObject type] == 19; }
- (BOOL)isServerPart { return self.partId != nil && self.partId != self.clientAssignedUUID; }
- (BOOL)containsBlocks { return self.blocks != nil && self.blocks.count != 0; }
- (BOOL)blocksContainAParagraphBlock {
    for (ITBBlock *block in self.blocks) if (block.type == 0) return YES;
    return NO;
}
- (BOOL)shouldShowPartStatus {
    NSInteger first = self.partStyle;
    NSInteger second = self.partStyle;
    if (first == 0 || second == 7) return !self.isStreamingPart;
    return NO;
}
- (NSString *)articleId {
    if (self.partStyle != 0 || self.blocks.count != 1) return nil;
    if ([(ITBBlock *)self.blocks.firstObject type] != 12) return nil;
    return [(ITBBlock *)self.blocks.firstObject articleId];
}
@end
