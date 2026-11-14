#import "ICMConversationPageResponse.h"
#import "ICMDataManagerDependencies.h"
@implementation ICMConversationPageResponse
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMConversationPageResponse *response = [[ICMConversationPageResponse alloc] init];
    [response setAttributesFromDictionary:dictionary];
    return response;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"empty_state"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.messagesEmptyState = [[ICMRecoveredMessagesEmptyState alloc] initWithDictionary:value];
    } else if ([key isEqualToString:@"conversations"]) {
        if ([value isKindOfClass:[NSArray class]]) {
            NSMutableArray *conversations = [NSMutableArray arrayWithCapacity:[value count]];
            for (id data in value) {
                ICMConversation *conversation = [ICMConversation instanceFromDictionary:data partDisplayStyle:1];
                if ([ICMDataManager sharedManager].conversationToMark && [conversation.conversationId isEqualToString:[ICMDataManager sharedManager].conversationToMark]) conversation.read = YES;
                [conversations addObject:conversation];
            }
            self.conversations = conversations;
        }
    } else [super setValue:value forKey:key];
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"more_pages_available"]) [self setValue:value forKey:@"morePagesAvailable"];
}
- (BOOL)isEqual:(id)other {
    if (other == self) return YES;
    if (!other || ![[other class] isEqual:[self class]]) return NO;
    return [self isEqualToPageResponse:other];
}
- (BOOL)isEqualToPageResponse:(ICMConversationPageResponse *)other {
    if (!((!self.conversations && !other.conversations) || [self.conversations isEqual:other.conversations])) return NO;
    return self.morePagesAvailable == other.morePagesAvailable;
}
- (NSUInteger)hash { return self.conversations.hash + self.morePagesAvailable; }
@end
