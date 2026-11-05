// Source recovery modifications: see docs/IOS_PROVENANCE.md.
// Product UNLINKED: actual Config/HTTPBridge and dependent models remain missing.
#import "ICMBaseResponse.h"
#import "ICMIdentityStore.h"
#import "ICMIdentityStoreDependencies.h"
@implementation ICMBaseResponse
@synthesize unreadConversations = _unreadConversations, unreadTickets = _unreadTickets, user = _user, realtimeConfig = _realtimeConfig, isFirstRequest = _isFirstRequest, carousel = _carousel, survey = _survey;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMBaseResponse *response = [[ICMBaseResponse alloc] init];
    [ICMConfig updateSharedInstanceFromDictionary:dictionary[@"config"]];
    [response setAttributesFromDictionary:dictionary];
    if ([dictionary[@"has_conversations"] isKindOfClass:NSNumber.class]) {
        BOOL hasConversations = [dictionary[@"has_conversations"] boolValue];
        [ICMIdentityStore currentIdentity].userIdentity.hasConversations = hasConversations;
    }
    return response;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:NSDictionary.class]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"config"]) {
        if ([value isKindOfClass:NSDictionary.class]) {
            if ([value[@"real_time_config"] isKindOfClass:NSDictionary.class]) {
                self.realtimeConfig = [IntercomNexusConfiguration instanceFromDictionary:value[@"real_time_config"]];
                NSNumber *timeout = @([ICMConfig sharedConfig].newSessionThreshold);
                self.realtimeConfig.backgroundConnectionTimeout = timeout;
            }
            if ([value[@"is_first_request"] isKindOfClass:NSNumber.class]) self.isFirstRequest = [value[@"is_first_request"] boolValue];
        }
    } else if ([key isEqualToString:@"unread_conversations"]) {
        if ([value isKindOfClass:NSDictionary.class]) self.unreadConversations = [ICMUnreadConversationsResponse instanceFromDictionary:value];
    } else if ([key isEqualToString:@"unread_tickets"]) {
        if ([value isKindOfClass:NSDictionary.class]) self.unreadTickets = [ICMUnreadTicketsResponse instanceFromDictionary:value];
    } else if ([key isEqualToString:@"user"]) {
        if ([value isKindOfClass:NSDictionary.class]) self.user = [ICMUserIdentity instanceFromDictionary:value];
    } else if ([key isEqualToString:@"carousel"]) {
        if ([value isKindOfClass:NSDictionary.class]) self.carousel = [ICMCarousel instanceFromDictionary:value];
    } else if ([key isEqualToString:@"survey"]) {
        if ([value isKindOfClass:NSDictionary.class]) {
            self.survey = [[ICMRecoveredSurvey alloc] initWithDictionary:value];
            if (!self.survey) {
                id identifier = value[@"id"];
                id progress = value[@"survey_progress_id"];
                if (identifier && progress && [progress integerValue]) {
                    [ICMHTTPBridge failSurvey:identifier progressId:[progress integerValue] success:^{} failure:^(NSError *error) { (void)error; }];
                }
            }
        }
    } else {
        [super setValue:value forKey:key];
    }
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"unread_conversations"]) [self setValue:value forKey:@"unreadConversations"];
    else if ([key isEqualToString:@"unread_tickets"]) [self setValue:value forKey:@"unreadTickets"];
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToResponse:object];
}
- (BOOL)isEqualToResponse:(ICMBaseResponse *)response {
    if ((self.unreadConversations || response.unreadConversations) && ![self.unreadConversations isEqual:response.unreadConversations]) return NO;
    if ((self.user || response.user) && ![self.user isEqual:response.user]) return NO;
    if ((self.carousel || response.carousel) && ![self.carousel isEqual:response.carousel]) return NO;
    return self.isFirstRequest == response.isFirstRequest;
}
- (NSUInteger)hash { return self.unreadConversations.hash * 31 + self.user.hash; }
@end
