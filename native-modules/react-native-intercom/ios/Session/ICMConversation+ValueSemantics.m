#import "ICMConversation.h"
#import "ICMConversationDependencies.h"
@implementation ICMConversation (OriginalValueSemantics)
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (object == nil || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToConversation:object];
}
- (BOOL)isEqualToConversation:(ICMConversation *)other {
    if (other == nil) return NO;
    if (!((self.conversationId == nil && other.conversationId == nil) || [self.conversationId isEqual:other.conversationId])) return NO;
    if (!((self.title == nil && other.title == nil) || [self.title isEqual:other.title])) return NO;
    if (!((self.participants == nil && other.participants == nil) || [self.participants isEqual:other.participants])) return NO;
    if (!((self.groupConversationParticipantIds == nil && other.groupConversationParticipantIds == nil) || [self.groupConversationParticipantIds isEqual:other.groupConversationParticipantIds])) return NO;
    if (!((self.conversationParts == nil && other.conversationParts == nil) || [self.conversationParts isEqual:other.conversationParts])) return NO;
    if (self.read != other.read) return NO;
    if (self.userParticipated != other.userParticipated) return NO;
    if (self.partDisplayStyle != other.partDisplayStyle) return NO;
    if (!((self.composerState == nil && other.composerState == nil) || [self.composerState isEqual:other.composerState])) return NO;
    if (self.preventEndUserReplies != other.preventEndUserReplies) return NO;
    if (self.inboundConversationsDisabled != other.inboundConversationsDisabled) return NO;
    if (!((self.notificationStatus == nil && other.notificationStatus == nil) || [self.notificationStatus isEqual:other.notificationStatus])) return NO;
    if (self.state != other.state) return NO;
    if (self.isInboundConversation != other.isInboundConversation) return NO;
    if (self.showTicketCardView != other.showTicketCardView) return NO;
    if (self.isEligibleForRecentConversations != other.isEligibleForRecentConversations) return NO;
    if (![self.uiFlags isEqual:other.uiFlags]) return NO;
    if (self.header != other.header) return NO;
    if (self.teamIntro != other.teamIntro) return NO;
    if (self.specialNotice != other.specialNotice) return NO;
    return YES;
}
@end
