#import "ICMConversation.h"
#import "ICMConversationDependencies.h"
@implementation ICMConversation (OriginalParsing)
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary partDisplayStyle:(NSInteger)style {
    ICMConversation *conversation = [ICMConversation new];
    [conversation setPartDisplayStyle:style];
    [conversation setAttributesFromDictionary:dictionary];
    [conversation setParticipantsFromDictionary:dictionary];
    [conversation setConversationPartsFromDictionary:dictionary];
    return conversation;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setParticipantsFromDictionary:(NSDictionary *)dictionary {
    if (![dictionary isKindOfClass:[NSDictionary class]]) return;
    id items = dictionary[@"participants"];
    if (items == nil || ![items isKindOfClass:[NSArray class]]) return;
    NSMutableDictionary *participants = [NSMutableDictionary dictionaryWithCapacity:[items count]];
    for (id item in items) {
        BOOL group = [self isGroupParticipantID:item[@"id"]];
        ICMParticipant *participant = [[ICMParticipant alloc] initWith:item isGroupParticipant:group];
        NSString *identifier = participant.id;
        if (identifier != nil) participants[identifier] = participant;
    }
    self.participants = participants;
}
- (void)setConversationPartsFromDictionary:(NSDictionary *)dictionary {
    if (![dictionary isKindOfClass:[NSDictionary class]]) return;
    id items = dictionary[@"conversation_parts"];
    if (items == nil || ![items isKindOfClass:[NSArray class]]) return;
    NSMutableArray *parts = [NSMutableArray arrayWithCapacity:[items count]];
    for (id item in items) {
        ICMConversationPart *part = [ICMConversationPart instanceFromDictionary:item displayStyle:self.partDisplayStyle];
        NSString *participantID = part.eventData.participant.participantId;
        if (participantID != nil) {
            BOOL group = [self isGroupParticipantID:participantID];
            [part.eventData.participant setIsGroupParticipant:group];
        }
        [parts addObject:part];
        if (part.displayStyle == 1 && [part isInitialMessageQuickReply]) {
            part.replyOptionsArray = @[];
            if ([items count] == 1) {
                ICMConversationPart *reply = [ICMConversationPart instanceFromDictionary:item displayStyle:self.partDisplayStyle];
                reply.partStyle = 3;
                reply.blocks = @[];
                reply.partId = [part.partId stringByAppendingString:@"-quick-replies"];
                [parts addObject:reply];
            }
        }
    }
    self.conversationParts = parts;
    [self setParticipantsOnConversationParts];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"state"]) {
        [self setConversationState:value];
    } else if ([key isEqualToString:@"ticket"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.ticket = [[ICMRecoveredTicket alloc] initWithDictionary:value];
    } else if ([key isEqualToString:@"ui_flags"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.uiFlags = [[ICMRecoveredUIFlags alloc] initWithDictionary:value];
    } else if ([key isEqualToString:@"header"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.header = [[ICMRecoveredConversationHeader alloc] initWithDictionary:value];
        else if ([value isKindOfClass:[ICMRecoveredConversationHeader class]]) self.header = value;
    } else if ([key isEqualToString:@"powered_by"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.poweredBy = [[ICMRecoveredPoweredBy alloc] initWithInstanceFrom:value];
    } else {
        [super setValue:value forKey:key];
    }
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"id"]) [self setValue:value forKey:@"conversationId"];
    else if ([key isEqualToString:@"user_participated"]) [self setValue:value forKey:@"userParticipated"];
    else if ([key isEqualToString:@"footer_notice"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.footerNotice = [[ICMFooterNotice alloc] initWithDictionary:value];
    } else if ([key isEqualToString:@"last_participating_admin"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.lastAdmin = [[ICMRecoveredParticipatingAdmin alloc] initWith:value];
    } else if ([key isEqualToString:@"composer_state"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.composerState = [ICMComposerState instanceFromDictionary:value];
    } else if ([key isEqualToString:@"group_conversation_participant_ids"]) {
        if ([value isKindOfClass:[NSArray class]]) {
            NSMutableArray *identifiers = [NSMutableArray array];
            for (id identifier in value) if ([identifier isKindOfClass:[NSString class]]) [identifiers addObject:identifier];
            self.groupConversationParticipantIds = identifiers;
        }
    } else if ([key isEqualToString:@"prevent_end_user_replies"]) [self setValue:value forKey:@"preventEndUserReplies"];
    else if ([key isEqualToString:@"inbound_conversations_disabled"]) [self setValue:value forKey:@"inboundConversationsDisabled"];
    else if ([key isEqualToString:@"notification_status"]) [self setValue:value forKey:@"notificationStatus"];
    else if ([key isEqualToString:@"is_inbound"]) [self setValue:value forKey:@"isInboundConversation"];
    else if ([key isEqualToString:@"show_ticket_card_view"]) [self setValue:value forKey:@"showTicketCardView"];
    else if ([key isEqualToString:@"eligible_for_recent_conversations"]) [self setValue:value forKey:@"eligibleForRecentConversations"];
    else if ([key isEqualToString:@"team_intro"]) [self setValue:value forKey:@"teamIntro"];
    else if ([key isEqualToString:@"special_notice"]) [self setValue:value forKey:@"specialNotice"];
    else if ([key isEqualToString:@"conversation_ended_button"]) {
        if ([value isKindOfClass:[NSDictionary class]]) {
            ICMRecoveredConversationEndedButton *button = [[ICMRecoveredConversationEndedButton alloc] initWithDictionary:value];
            [self setValue:button forKey:@"conversationEndedButton"];
        }
    }
}
@end
