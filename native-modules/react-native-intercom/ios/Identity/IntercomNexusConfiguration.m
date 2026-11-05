#import "IntercomNexusConfiguration.h"
@implementation IntercomNexusConfiguration
@synthesize endpoints = _endpoints, connectionTimeout = _connectionTimeout, connectionHeartbeatInterval = _connectionHeartbeatInterval, backgroundConnectionTimeout = _backgroundConnectionTimeout;
+ (instancetype)instanceFromDictionary:(id)dictionary {
    IntercomNexusConfiguration *configuration = [IntercomNexusConfiguration new];
    [configuration setAttributesFromDictionary:dictionary];
    return configuration;
}
- (instancetype)init {
    if ((self = [super init])) {
        _connectionHeartbeatInterval = @0;
        _endpoints = @[];
        _connectionTimeout = @0;
        _backgroundConnectionTimeout = @0;
    }
    return self;
}
- (void)setAttributesFromDictionary:(id)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"endpoints"]) {
        if (![value isKindOfClass:[NSArray class]]) return;
        NSMutableArray *endpoints = [NSMutableArray array];
        for (id endpoint in value) {
            if (![endpoint isKindOfClass:[NSString class]]) continue;
            NSString *url = [endpoint stringByAppendingString:[NSString stringWithFormat:@"?X-Nexus-Version=ios.%s", "5.5.0"]];
            if ([url rangeOfString:@"http"].location == 0) url = [url stringByReplacingCharactersInRange:NSMakeRange(0, 4) withString:@"ws"];
            [endpoints addObject:[NSURL URLWithString:url]];
        }
        self.endpoints = endpoints;
    } else [super setValue:value forKey:key];
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"connection_timeout"]) {
        if ([value isKindOfClass:[NSNumber class]]) self.connectionTimeout = value;
    } else if ([key isEqualToString:@"presence_heartbeat_interval"]) {
        if ([value isKindOfClass:[NSNumber class]]) self.connectionHeartbeatInterval = value;
    }
}
- (BOOL)isEqual:(IntercomNexusConfiguration *)other {
    return ((!self.endpoints && !other.endpoints) || [self.endpoints isEqualToArray:other.endpoints]) &&
        ((!self.connectionHeartbeatInterval && !other.connectionHeartbeatInterval) || [self.connectionHeartbeatInterval isEqual:other.connectionHeartbeatInterval]) &&
        ((!self.connectionTimeout && !other.connectionTimeout) || [self.connectionTimeout isEqual:other.connectionTimeout]) &&
        ((!self.backgroundConnectionTimeout && !other.backgroundConnectionTimeout) || [self.backgroundConnectionTimeout isEqual:other.backgroundConnectionTimeout]);
}
@end
