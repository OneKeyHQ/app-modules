#import "ICMConversationsResponse.h"
#import "ICMConfig.h"
@implementation ICMConversationsResponse
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMConversationsResponse *response = [[ICMConversationsResponse alloc] init];
    [ICMConfig updateSharedInstanceFromDictionary:dictionary[@"config"]];
    [response setAttributesFromDictionary:dictionary];
    return response;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"config"]) return;
    if ([key isEqualToString:@"user"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.user = [ICMUserIdentity instanceFromDictionary:value];
    } else [super setValue:value forKey:key];
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"conversation_page"] && [value isKindOfClass:[NSDictionary class]]) self.conversationPage = [ICMConversationPageResponse instanceFromDictionary:value];
}
- (BOOL)isEqual:(id)other {
    if (other == self) return YES;
    if (!other || ![[other class] isEqual:[self class]]) return NO;
    return [self isEqualToResponse:other];
}
- (BOOL)isEqualToResponse:(ICMConversationsResponse *)other {
    if (self == other) return YES;
    if (!other) return NO;
    if (!(self.conversationPage == other.conversationPage || [self.conversationPage isEqualToPageResponse:other.conversationPage])) return NO;
    return self.user == other.user || [self.user isEqual:other.user];
}
- (NSUInteger)hash { return 31 * self.conversationPage.hash + self.user.hash; }
@end
