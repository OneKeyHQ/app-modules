#import "ICMOpsMetricEvent.h"
@implementation ICMOpsMetricEvent
+ (instancetype)eventWithType:(NSString *)type name:(NSString *)name value:(NSNumber *)value {
    if (![[self class] isNonEmptyString:type] || ![[self class] isNonEmptyString:name] || ![[self class] isNonNullNumber:value]) return nil;
    ICMOpsMetricEvent *event = [self alloc];
    return [event eventWithType:type name:name value:value];
}
- (instancetype)eventWithType:(NSString *)type name:(NSString *)name value:(NSNumber *)value {
    _type = type; _name = name; _value = value;
    return self;
}
+ (BOOL)isNonEmptyString:(id)value { return [value isKindOfClass:[NSString class]] && [value length] != 0; }
+ (BOOL)isNonNullNumber:(id)value { return [value isKindOfClass:[NSNumber class]] && value != nil; }
- (NSDictionary *)eventDictionary {
    if (![[self class] isNonEmptyString:self.type] || ![[self class] isNonEmptyString:self.name] || ![[self class] isNonNullNumber:self.value]) return nil;
    return @{ @"type": self.type, @"name": self.name, @"value": self.value };
}
+ (BOOL)supportsSecureCoding { return YES; }
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        self.type = [coder decodeObjectOfClass:[NSString class] forKey:@"type"];
        self.name = [coder decodeObjectOfClass:[NSString class] forKey:@"name"];
        self.value = [coder decodeObjectOfClass:[NSNumber class] forKey:@"value"];
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.type forKey:@"type"];
    [coder encodeObject:self.name forKey:@"name"];
    [coder encodeObject:self.value forKey:@"value"];
}
- (BOOL)isEqual:(id)other {
    if (other == self) return YES;
    if (!other || ![[other class] isEqual:[self class]]) return NO;
    return [self isEqualToMetricEvent:other];
}
- (BOOL)isEqualToMetricEvent:(ICMOpsMetricEvent *)other {
    if (!((!self.type && !other.type) || [self.type isEqual:other.type])) return NO;
    if (!((!self.name && !other.name) || [self.name isEqual:other.name])) return NO;
    return (!self.value && !other.value) || [self.value isEqual:other.value];
}
- (NSUInteger)hash { return self.type.hash ^ self.name.hash ^ self.value.hash; }
@end
