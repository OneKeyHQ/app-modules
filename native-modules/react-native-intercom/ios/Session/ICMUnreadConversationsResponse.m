// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMUnreadConversationsResponse.h"
#import "ICMUnreadResponseDependencies.h"
#import <objc/runtime.h>
@implementation ICMUnreadConversationsResponse
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMUnreadConversationsResponse *response = [[ICMUnreadConversationsResponse alloc] init];
    [response setAttributesFromDictionary:dictionary];
    return response;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        _conversations = @[];
        _unreadConversationIds = [NSOrderedSet orderedSet];
    }
    return self;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if (![dictionary isKindOfClass:[NSDictionary class]]) return;
    id conversations = dictionary[@"conversations"];
    if ([conversations isKindOfClass:[NSArray class]]) {
        NSMutableArray *parsed = [NSMutableArray arrayWithCapacity:[conversations count]];
        for (id entry in conversations) {
            if ([entry isKindOfClass:[NSDictionary class]]) {
                [parsed addObject:[ICMConversation instanceFromDictionary:entry partDisplayStyle:0]];
            }
        }
        self.conversations = parsed;
    }
    id identifiers = dictionary[@"unread_conversation_ids"];
    if ([identifiers isKindOfClass:[NSArray class]] && [[identifiers firstObject] isKindOfClass:[NSString class]] && [[identifiers firstObject] length] > 0) {
        self.unreadConversationIds = [NSOrderedSet orderedSetWithArray:identifiers];
    }
    [self updateReadState];
}
- (void)updateReadState {
    if ([[ICMDataManager sharedManager] conversationToMark]) {
        self.conversations = [self.conversations filteredArrayUsingPredicate:[NSPredicate predicateWithFormat:@"conversationId != %@", [[ICMDataManager sharedManager] conversationToMark]]];
        self.unreadConversationIds = [self.unreadConversationIds filteredOrderedSetUsingPredicate:[NSPredicate predicateWithFormat:@"SELF != %@", [[ICMDataManager sharedManager] conversationToMark]]];
    }
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (!object || object_getClass(self) != object_getClass(object)) return NO;
    return [self isEqualToUnreadConversationsResponse:object];
}
- (BOOL)isEqualToUnreadConversationsResponse:(ICMUnreadConversationsResponse *)other {
    if ((self.conversations || other.conversations) && ![self.conversations isEqual:other.conversations]) return NO;
    if ((self.unreadConversationIds || other.unreadConversationIds) && ![self.unreadConversationIds isEqual:other.unreadConversationIds]) return NO;
    return YES;
}
- (NSUInteger)hash { return self.conversations.hash ^ self.unreadConversationIds.hash; }
@end
