#import "ICMComposerState.h"
@implementation ICMComposerState
@synthesize workflowActive = _workflowActive, displayState = _displayState, version = _version, placeholder = _placeholder;
- (instancetype)init {
    self = [super init];
    if (self) _displayState = 0;
    return self;
}
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMComposerState *state = [ICMComposerState new];
    [state setAttributesFromDictionary:dictionary];
    return state;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"visibility"]) {
        if ([value isEqualToString:@"visible_and_enabled"]) self.displayState = 0;
        else if ([value isEqualToString:@"visible_and_disabled"]) self.displayState = 1;
        else if ([value isEqualToString:@"hidden"]) self.displayState = 2;
    } else if ([key isEqualToString:@"workflow_active"]) {
        self.workflowActive = [value boolValue];
    }
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToComposerState:object];
}
- (BOOL)isEqualToComposerState:(ICMComposerState *)other {
    BOOL placeholderEqual = [self.placeholder isEqualToString:other.placeholder] || self.placeholder == other.placeholder;
    if (self.displayState != other.displayState) return NO;
    if (self.workflowActive != other.workflowActive) return NO;
    return self.version == other.version && placeholderEqual;
}
- (id)copyWithZone:(NSZone *)zone {
    ICMComposerState *copy = [[[self class] allocWithZone:zone] init];
    copy.displayState = self.displayState;
    copy.workflowActive = self.workflowActive;
    copy.version = self.version;
    copy.placeholder = self.placeholder;
    return copy;
}
- (NSString *)description {
    return [NSString stringWithFormat:@"ComposerState: [version: %ld], [displayStatus: %ld, [workflowActive: %@], [placeholder: %@]", (long)self.version, (long)self.displayState, self.workflowActive ? @"Yes" : @"No", self.placeholder];
}
@end
