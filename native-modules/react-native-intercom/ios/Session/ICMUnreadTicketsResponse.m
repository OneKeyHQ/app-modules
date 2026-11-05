#import "ICMUnreadTicketsResponse.h"
#import "ICMUnreadResponseDependencies.h"
#import <objc/runtime.h>
@implementation ICMUnreadTicketsResponse
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMUnreadTicketsResponse *response = [[ICMUnreadTicketsResponse alloc] init];
    [response setAttributesFromDictionary:dictionary];
    return response;
}
- (instancetype)init {
    self = [super init];
    if (self) _unreadTicketIds = [NSOrderedSet orderedSet];
    return self;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if (![dictionary isKindOfClass:[NSDictionary class]]) return;
    id identifiers = dictionary[@"unread_ticket_ids"];
    if ([identifiers isKindOfClass:[NSArray class]] && [[identifiers firstObject] isKindOfClass:[NSString class]] && [[identifiers firstObject] length] > 0) {
        self.unreadTicketIds = [NSOrderedSet orderedSetWithArray:identifiers];
    }
    [self updateReadState];
}
- (void)updateReadState {
    if ([[ICMDataManager sharedManager] ticketToMark]) {
        self.unreadTicketIds = [self.unreadTicketIds filteredOrderedSetUsingPredicate:[NSPredicate predicateWithFormat:@"SELF != %@", [[ICMDataManager sharedManager] ticketToMark]]];
    }
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (!object || object_getClass(self) != object_getClass(object)) return NO;
    return [self isEqualToUnreadTicketsResponse:object];
}
- (BOOL)isEqualToUnreadTicketsResponse:(ICMUnreadTicketsResponse *)other {
    return (!self.unreadTicketIds && !other.unreadTicketIds) || [self.unreadTicketIds isEqual:other.unreadTicketIds];
}
- (NSUInteger)hash { return self.unreadTicketIds.hash; }
@end
