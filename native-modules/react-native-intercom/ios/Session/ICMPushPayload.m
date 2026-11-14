#import "ICMPushPayload.h"
@implementation ICMPushPayload
- (instancetype)init { self = [super init]; if (self) _type = -1; return self; }
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMPushPayload *payload = [[ICMPushPayload alloc] init];
    [payload setAttributesFromDictionary:dictionary];
    return payload;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"uri"]) {
        if ([value isKindOfClass:[NSString class]]) self.uri = value;
    } else [super setValue:value forKey:key];
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"aps"]) [self setValue:value forKey:@"content"];
    else if ([key isEqualToString:@"instance_id"]) {
        if ([value isKindOfClass:[NSString class]]) [self setValue:value forKey:@"instanceId"];
    } else if ([key isEqualToString:@"push_only_conv_id"]) [self setValue:[ICMPushPayload stringFromValue:value] forKey:@"pushOnlyConversationId"];
    else if ([key isEqualToString:@"icm_cid"]) [self setValue:[ICMPushPayload stringFromValue:value] forKey:@"conversationId"];
    else if ([key isEqualToString:@"intercom_push_type"]) {
        self.type = [value isKindOfClass:[NSString class]] ? [ICMPushPayload determineNotificationType:value] : -1;
    } else if ([key isEqualToString:@"content_image_url"]) {
        if ([value isKindOfClass:[NSString class]]) [self setValue:[NSURL URLWithString:[ICMPushPayload stringFromValue:value]] forKey:@"contentImageURL"];
    } else if ([key isEqualToString:@"author_name"]) {
        if ([value isKindOfClass:[NSString class]] && [value length]) [self setValue:value forKey:@"authorName"];
    }
}
+ (NSInteger)determineNotificationType:(NSString *)type {
    if (!type) return -1;
    if ([type isEqualToString:@"push_only"]) return 1;
    return [type isEqualToString:@"notification"] ? 0 : 2;
}
- (NSInteger)pushOnlyIdType {
    if (self.instanceId && ![self.instanceId isEqualToString:@""]) return 1;
    if (self.pushOnlyConversationId) return [self.pushOnlyConversationId isEqualToString:@""] ? -1 : 0;
    return -1;
}
+ (NSString *)stringFromValue:(id)value {
    if ([value isKindOfClass:[NSString class]]) return value;
    if ([value isKindOfClass:[NSNumber class]]) return [value stringValue];
    return nil;
}
@end
