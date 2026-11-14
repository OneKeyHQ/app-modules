#import "ICMConversation.h"
#import "ICMConversationDependencies.h"
@implementation ICMConversation
@synthesize conversationId = _conversationId;
@synthesize title = _title;
@synthesize participants = _participants;
@synthesize groupConversationParticipantIds = _groupConversationParticipantIds;
@synthesize footerNotice = _footerNotice;
@synthesize lastAdmin = _lastAdmin;
@synthesize composerState = _composerState;
@synthesize conversationParts = _conversationParts;
@synthesize read = _read;
@synthesize userParticipated = _userParticipated;
@synthesize partDisplayStyle = _partDisplayStyle;
@synthesize preventEndUserReplies = _preventEndUserReplies;
@synthesize inboundConversationsDisabled = _inboundConversationsDisabled;
@synthesize notificationStatus = _notificationStatus;
@synthesize ticket = _ticket;
@synthesize composerSuggestions = _composerSuggestions;
@synthesize showTicketCardView = _showTicketCardView;
@synthesize articleMetaData = _articleMetaData;
@synthesize state = _state;
@synthesize conversationEndedButton = _conversationEndedButton;
@synthesize isInboundConversation = _isInboundConversation;
@synthesize initialMessage = _initialMessage;
@synthesize previouslyClosedConversationId = _previouslyClosedConversationId;
@synthesize eligibleForRecentConversations = _eligibleForRecentConversations;
@synthesize uiFlags = _uiFlags;
@synthesize header = _header;
@synthesize poweredBy = _poweredBy;
@synthesize teamIntro = _teamIntro;
@synthesize specialNotice = _specialNotice;
- (instancetype)init {
    if ((self = [super init])) {
        _conversationParts = @[];
        _partDisplayStyle = 0;
        _isInboundConversation = YES;
    }
    return self;
}
- (instancetype)initWithInitialMessage:(NSString *)message {
    if ((self = [self init])) _initialMessage = message;
    return self;
}
- (void)setConversationState:(id)value {
    if ([value isKindOfClass:[NSString class]]) {
        self.state = [value isEqualToString:@"state_open"] ? 0 : [value isEqualToString:@"state_closed"];
    }
}
- (void)setPartDisplayStyle:(NSInteger)style {
    if (_partDisplayStyle == style) return;
    _partDisplayStyle = style;
    for (ICMConversationPart *part in self.conversationParts) [part setDisplayStyle:style];
}
- (NSArray *)conversationParts {
    if (self.ticket != nil) {
        for (ICMConversationPart *part in _conversationParts) {
            if (part.partStyle == 6 || [part isCreateTicketPart]) [part setTicket:self.ticket];
        }
    }
    return _conversationParts;
}
- (void)setParticipantsOnConversationParts {
    for (ICMConversationPart *part in self.conversationParts) {
        part.participant = self.participants[part.participantId];
    }
}
- (BOOL)isGroupParticipantID:(NSString *)identifier {
    for (NSString *candidate in self.groupConversationParticipantIds) {
        if ([candidate isEqualToString:identifier]) return YES;
    }
    return NO;
}
- (BOOL)messageComposerShouldBeDisplayed {
    if (self.preventEndUserReplies || [self lastPartContainsReactionSet]) return NO;
    return self.composerState.displayState != 2;
}
- (BOOL)lastPartContainsReactionSet {
    return [(ICMConversationPart *)[self.conversationParts lastObject] reactionSet] != nil;
}
- (NSString *)participantsInConversation {
    NSString *name = self.lastAdmin ? self.lastAdmin.firstName : [ICMConfig sharedConfig].appName;
    if (self.groupConversationParticipantIds.count == 0) return name;
    if (self.groupConversationParticipantIds.count == 1) {
        return [[ICMTranslationManager messenger] translationForKey:@"name_and_1_other" params:@{@"name": name}];
    }
    return [[ICMTranslationManager messenger] translationForKey:@"name_and_x_others" params:@{@"name": name, @"count": [NSString stringWithFormat:@"%ld", (long)self.groupConversationParticipantIds.count]}];
}
- (ICMConversationPart *)lastPart { return [self.conversationParts lastObject]; }
- (ICMConversationPart *)lastAdminPart {
    ICMConversationPart *last = nil;
    for (ICMConversationPart *part in self.conversationParts) {
        if (part.participantIsAdmin) last = part;
    }
    return last;
}
- (BOOL)shouldDisplayStartNewConversationButton {
    if (![ICMConfig sharedConfig].inboundMessages || self.inboundConversationsDisabled) return NO;
    if ([ICMConfig sharedConfig].preventMultipleInboundConversations) return ![[ICMDataManager sharedManager] hasOpenInboundConversation];
    return YES;
}
- (BOOL)isOpenInboundConversation {
    NSDate *cutoff = [[NSDate date] dateByAddingTimeInterval:-864000.0];
    return self.state == 0 && self.isInboundConversation && self.lastPart.createdAt.timeIntervalSince1970 > cutoff.timeIntervalSince1970;
}
- (NSArray *)adminsToShow {
    NSArray *admins = self.lastAdmin ? @[self.lastAdmin] : [ICMTeamPresence sharedInstance].activeAdmins;
    return admins ?: @[];
}
- (id)activeBot { return [ICMTeamPresence sharedInstance].activeBot.participant; }
- (ICMRecoveredConversationHeader *)headerToDisplay {
    if (self == [ICMDataManager sharedManager].matchedNewConversation || self.header != nil) return self.header;
    return [ICMDataManager sharedManager].matchedNewConversation.header;
}
- (NSUInteger)hash { return self.conversationId.hash; }
- (NSString *)debugDescription {
    NSString *identifier = self.conversationId;
    NSString *state = self.state == 0 ? @"Open" : @"Closed";
    NSDate *lastUpdate = self.lastPart.createdAt;
    return [NSString stringWithFormat:@"ID: %@\n STATE: %@\n LAST_UPDATE_DATE: %@", identifier, state, lastUpdate];
}
- (id)copyWithZone:(NSZone *)zone {
    ICMConversation *result = [[[self class] allocWithZone:zone] init];
    result.conversationId = self.conversationId;
    result.title = self.title;
    result.participants = self.participants;
    result.lastAdmin = self.lastAdmin;
    result.composerState = self.composerState;
    result.conversationParts = [[NSArray alloc] initWithArray:self.conversationParts copyItems:YES];
    result.read = self.read;
    result.userParticipated = self.userParticipated;
    result.partDisplayStyle = self.partDisplayStyle;
    result.groupConversationParticipantIds = self.groupConversationParticipantIds;
    result.preventEndUserReplies = self.preventEndUserReplies;
    result.inboundConversationsDisabled = self.inboundConversationsDisabled;
    result.notificationStatus = self.notificationStatus;
    result.state = self.state;
    result.isInboundConversation = self.isInboundConversation;
    result.ticket = self.ticket;
    result.showTicketCardView = self.showTicketCardView;
    result.eligibleForRecentConversations = self.isEligibleForRecentConversations;
    result.uiFlags = self.uiFlags;
    result.header = self.header;
    result.articleMetaData = self.articleMetaData;
    result.composerSuggestions = self.composerSuggestions;
    result.teamIntro = self.teamIntro;
    result.specialNotice = self.specialNotice;
    return result;
}
@end
