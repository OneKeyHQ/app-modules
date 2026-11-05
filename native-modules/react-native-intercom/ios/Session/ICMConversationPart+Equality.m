// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMConversationPart.h"
#import "ICMConversationPartDependencies.h"
@implementation ICMConversationPart (Equality)
- (NSUInteger)hash {
    NSUInteger hash = self.partId.hash;
    return 31 * hash + self.seenState;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[ICMConversationPart class]]) return NO;
    ICMConversationPart *other = object;
    if (!((!other.partId && !self.partId) || [other.partId isEqual:self.partId])) return NO;
    if (self.seenState != other.seenState || self.displayStyle != other.displayStyle ||
        self.form != other.form || self.isProfilePart != other.isProfilePart ||
        self.isStreamingPart != other.isStreamingPart) return NO;
    if (!((!other.ticket && !self.ticket) || [other.ticket isEqual:self.ticket])) return NO;
    if (self.isCreateTicketPart && !((!other.blocks && !self.blocks) || [other.blocks isEqual:self.blocks])) return NO;
    return self.clientAssignedUUID == other.clientAssignedUUID && self.groupingPosition == other.groupingPosition &&
        self.state == other.state && self.replyOptionsArray == other.replyOptionsArray &&
        self.displayPartStatus == other.displayPartStatus && self.displayTimestampTemporarily == other.displayTimestampTemporarily &&
        self.answerSources == other.answerSources && self.partMetadata == other.partMetadata;
}
@end
