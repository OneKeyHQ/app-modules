// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMConversationPart.h"
#import "ICMConversationPartDependencies.h"
@implementation ICMConversationPart (Parsing)
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if (![dictionary isKindOfClass:[NSDictionary class]]) return;
    [self setValuesForKeysWithDictionary:dictionary];
    id appearance = [ICMConversationPart blockAppearanceForPart:self];
    NSMutableArray *blocks = [NSMutableArray new];
    if ([dictionary[@"body"] isKindOfClass:[NSArray class]]) {
        NSArray *body = dictionary[@"body"];
        self.blockDictionaries = body;
        NSUInteger count = [dictionary[@"body"] count];
        BOOL summary = self.partStyle == 0 && self.deliveryOption == 0;
        for (NSDictionary *original in body) {
            id blockAppearance = appearance;
            NSDictionary *blockDictionary = [self removeFallbackURLForFullChats:original];
            if (count == 1 && [[self class] isMessengerCardBlock:blockDictionary])
                blockAppearance = [[ICMMessengerCardBlockAppearance alloc] init];
            if ([[self class] isLinkBlock:blockDictionary] || [[self class] isLinkListBlock:blockDictionary]) {
                if (summary) {
                    blockAppearance = [[self class] determineAppearanceForBlock:blockDictionary];
                    if ([[self class] isArticleCardBlock:blockDictionary]) self.hasArticleCard = YES;
                } else {
                    NSMutableDictionary *paragraph = [blockDictionary mutableCopy];
                    paragraph[@"type"] = @"paragraph";
                    blockDictionary = paragraph;
                }
            }
            if ([blockDictionary[@"type"] isKindOfClass:[NSString class]] &&
                [blockDictionary[@"type"] isEqualToString:@"attachmentList"]) {
                [self addAttachmentBlocksFromArray:blockDictionary[@"attachments"] toBlocks:blocks withAppearance:appearance];
            } else {
                ITBBlock *block = [ICMBlockLoader blockFromDictionary:blockDictionary appearance:blockAppearance];
                if (block) {
                    if ([original[@"attribution"] isKindOfClass:[NSString class]]) self.attribution = original[@"attribution"];
                    [blocks addObject:block];
                }
            }
        }
    }
    [self addAttachmentBlocksFromArray:dictionary[@"attachments"] toBlocks:blocks withAppearance:appearance];
    if (blocks.count == 1 && self.partStyle == 0) [(ITBBlock *)blocks.firstObject setDisplayMode:1];
    _blocks = blocks;
}
- (void)addAttachmentBlocksFromArray:(NSArray *)attachments toBlocks:(NSMutableArray *)blocks withAppearance:(id)appearance {
    if (![attachments isKindOfClass:[NSArray class]] || attachments.count == 0) return;
    for (id attachment in attachments) {
        NSMutableDictionary *dictionary = [attachment mutableCopy];
        dictionary[@"type"] = @"internal_attachment";
        ITBBlock *block = [ICMBlockLoader blockFromDictionary:dictionary appearance:appearance];
        if (block) [blocks addObject:block];
    }
}
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary displayStyle:(NSInteger)style {
    ICMConversationPart *part = [ICMConversationPart new];
    part.displayStyle = style;
    [part setAttributesFromDictionary:dictionary];
    [ICMConversationPart setReplyOptions:dictionary forPart:part];
    [ICMConversationPart setFormAttributes:dictionary forPart:part];
    return part;
}
+ (void)setReplyOptions:(NSDictionary *)dictionary forPart:(ICMConversationPart *)part {
    if ([dictionary[@"reply_options"] isKindOfClass:[NSArray class]]) {
        NSMutableArray *options = [NSMutableArray new];
        for (id item in dictionary[@"reply_options"]) {
            ICMRecoveredReplyOption *option = [[ICMRecoveredReplyOption alloc] initWithInstanceFrom:item];
            if (option) [options addObject:option];
        }
        part.replyOptionsArray = options;
    }
}
+ (void)setFormAttributes:(NSDictionary *)dictionary forPart:(ICMConversationPart *)part {
    if ([dictionary[@"form"] isKindOfClass:[NSDictionary class]]) {
        part.form = [[ICMRecoveredForm alloc] initWithDictionary:dictionary[@"form"]];
    }
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"created_at"]) {
        if ([value isKindOfClass:[NSNumber class]])
            [self setValue:[NSDate dateWithTimeIntervalSince1970:[value doubleValue]] forKey:@"createdAt"];
    } else if ([key isEqualToString:@"participant_is_admin"]) {
        [self setValue:value forKey:@"participantIsAdmin"];
    } else if ([key isEqualToString:@"id"]) {
        [self setValue:value forKey:@"partId"];
        [self setValue:value forKey:@"quickReplyPartId"];
    } else if ([key isEqualToString:@"delivery_option"]) {
        if ([value isKindOfClass:[NSString class]]) {
            if ([value isEqualToString:@"summary"]) self.deliveryOption = 0;
            else if ([value isEqualToString:@"full"]) self.deliveryOption = 1;
            else if ([value isEqualToString:@"badge"]) self.deliveryOption = 2;
        }
    } else if ([key isEqualToString:@"message_style"]) {
        if ([value isKindOfClass:[NSString class]]) {
            if ([value isEqualToString:@"announcement"]) self.partStyle = 2;
            else if ([value isEqualToString:@"small-announcement"]) self.partStyle = 1;
            else if ([value isEqualToString:@"quick_reply"]) self.partStyle = 3;
            else if ([value isEqualToString:@"attribute_collector"]) self.partStyle = 4;
            else if ([value isEqualToString:@"ticket_state_updated"]) self.partStyle = 6;
            else if ([value isEqualToString:@"fin_answer"]) self.partStyle = 7;
            else if ([value isEqualToString:@"merged_primary_conversation"]) self.partStyle = 9;
        }
    } else if ([key isEqualToString:@"summary"]) {
        [self setValue:value forKey:@"summary"];
    } else if ([key isEqualToString:@"participant_id"]) {
        [self setValue:value forKey:@"participantId"];
    } else if ([key isEqualToString:@"show_created_at"]) {
        [self setValue:value forKey:@"displayTimestamp"];
    } else if ([key isEqualToString:@"seen_by_admin"]) {
        if ([value isKindOfClass:[NSString class]]) {
            if ([value isEqualToString:@"seen"]) self.seenState = 0;
            else if ([value isEqualToString:@"unseen"]) self.seenState = 1;
        }
    } else if ([key isEqualToString:@"reactions_reply"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.reactionSet = [ICMReactionsSet instanceFromDictionary:value];
    } else if ([key isEqualToString:@"is_initial_message"]) {
        [self setValue:value forKey:@"isInitialMessage"];
    } else if ([key isEqualToString:@"event_data"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.eventData = [[ICMRecoveredEventData alloc] initWithInstanceFrom:value];
    } else if ([key isEqualToString:@"client_assigned_uuid"]) {
        [self setValue:value forKey:@"clientAssignedUUID"];
    } else if ([key isEqualToString:@"sources"]) {
        if ([value isKindOfClass:[NSArray class]]) {
            NSMutableArray *sources = [NSMutableArray new];
            for (id dictionary in value) {
                ICMRecoveredFinAnswerSourceWrapper *source = [[ICMRecoveredFinAnswerSourceWrapper alloc] initWithDictionary:dictionary];
                if (source) [sources addObject:source];
            }
            self.answerSources = sources;
        }
    } else if ([key isEqualToString:@"metadata"]) {
        [value isKindOfClass:[NSDictionary class]];
        self.partMetadata = [[ICMRecoveredPartMetadata alloc] initWithDictionary:value];
    }
}
@end
