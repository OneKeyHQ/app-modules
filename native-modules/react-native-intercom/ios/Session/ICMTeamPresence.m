// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMTeamPresence.h"

@implementation ICMTeamPresence
@synthesize activeAdmins = _activeAdmins, lastActive = _lastActive;
@synthesize responseDelayShort = _responseDelayShort, responseDelayLong = _responseDelayLong, activeBot = _activeBot;
- (instancetype)init { return [super init]; }
+ (instancetype)sharedInstance {
    static ICMTeamPresence *shared;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ shared = [ICMTeamPresence new]; });
    return shared;
}
+ (void)updateSharedInstanceWithDictionary:(NSDictionary *)dictionary {
    [[ICMTeamPresence sharedInstance] setAttributesFromDictionary:dictionary];
    [[NSNotificationCenter defaultCenter] postNotificationName:@"ICMTeamPresenceUpdated" object:nil];
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)reset {
    self.activeAdmins = nil;
    self.lastActive = nil;
    self.responseDelayShort = @"";
    self.responseDelayLong = @"";
    self.activeBot = nil;
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"active_admins"]) {
        if ([value isKindOfClass:[NSArray class]]) {
            NSMutableArray *admins = [NSMutableArray arrayWithCapacity:[value count]];
            for (id dictionary in value) [admins addObject:[[ICMRecoveredParticipatingAdmin alloc] initWith:dictionary]];
            self.activeAdmins = admins;
        }
    } else if ([key isEqualToString:@"active_bot"]) {
        if ([value isKindOfClass:[NSDictionary class]]) self.activeBot = [[ICMRecoveredActiveBot alloc] initWithDictionary:value];
    } else {
        [super setValue:value forKey:key];
    }
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"active_admins"]) {
        [self setValue:value forKey:@"activeAdmins"];
    } else if ([key isEqualToString:@"last_active"]) {
        if ([value isKindOfClass:[NSNumber class]]) self.lastActive = [NSDate dateWithTimeIntervalSince1970:[value doubleValue]];
    } else if ([key isEqualToString:@"expected_response_delay_long_text"]) {
        if ([value isKindOfClass:[NSString class]]) self.responseDelayLong = value;
    } else if ([key isEqualToString:@"expected_response_delay_short_text"]) {
        if ([value isKindOfClass:[NSString class]]) self.responseDelayShort = value;
    }
}
- (id)copyWithZone:(NSZone *)zone {
    ICMTeamPresence *copy = [[[self class] allocWithZone:zone] init];
    copy.activeAdmins = self.activeAdmins;
    copy.lastActive = self.lastActive;
    copy.responseDelayShort = self.responseDelayShort;
    copy.responseDelayLong = self.responseDelayLong;
    copy.activeBot = self.activeBot;
    return copy;
}
@end
