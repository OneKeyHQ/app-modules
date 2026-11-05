#import "ICMComposerSuggestions.h"
#import "ICMComposerSuggestionsDependencies.h"
@implementation ICMComposerSuggestions
@synthesize prompt = _prompt, suggestions = _suggestions, composerDisabled = _composerDisabled, snapshotId = _snapshotId;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMComposerSuggestions *result = [ICMComposerSuggestions new];
    [result setAttributesFromDictionary:dictionary];
    return result;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:[NSDictionary class]]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"prompt"]) {
        if ([value isKindOfClass:[NSArray class]] && [value count] != 0) {
            self.prompt = [value[0] objectForKey:@"text"];
        }
    } else if ([key isEqualToString:@"suggestions"]) {
        if ([value isKindOfClass:[NSArray class]]) {
            NSMutableArray *suggestions = [NSMutableArray arrayWithCapacity:[value count]];
            for (id dictionary in value) {
                [suggestions addObject:[[ICMRecoveredReplyOption alloc] initWithInstanceFrom:dictionary]];
            }
            self.suggestions = suggestions;
        }
    } else if ([key isEqualToString:@"snapshot_id"] && [value isKindOfClass:[NSNumber class]]) {
        self.snapshotId = value;
    } else {
        [super setValue:value forKey:key];
    }
}
- (NSArray *)suggestionsUUIDArray {
    NSMutableArray *result = [@[] mutableCopy];
    for (ICMRecoveredReplyOption *suggestion in self.suggestions) {
        [result addObject:@{@"uuid": suggestion.uuid}];
    }
    return result;
}
- (BOOL)isEmpty {
    if (self.prompt) return NO;
    return self.suggestions == nil;
}
- (NSArray *)conversationParts {
    if ([self isEmpty]) return @[];
    id participant = [[[ICMTeamPresence sharedInstance] activeBot] participant];
    ICMConversationPart *part = [ICMConversationPart new];
    part.replyOptionsArray = self.suggestions;
    part.partId = [NSUUID UUID].UUIDString;
    part.quickReplyPartId = [NSUUID UUID].UUIDString;
    part.partStyle = 5;
    part.createdAt = [NSDate date];
    part.participant = participant;
    part.participantId = [participant id];
    return @[part];
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToComposerSuggestions:object];
}
- (BOOL)isEqualToComposerSuggestions:(ICMComposerSuggestions *)other {
    if (self == other) return YES;
    if (!other) return NO;
    if (self.prompt != other.prompt && ![self.prompt isEqualToString:other.prompt]) return NO;
    if (![self.suggestions isEqualToArray:other.suggestions]) return NO;
    return self.snapshotId == other.snapshotId;
}
- (NSUInteger)hash { return 31 * self.suggestions.hash + self.prompt.hash; }
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.prompt forKey:NSStringFromSelector(@selector(prompt))];
    [coder encodeObject:self.suggestions forKey:NSStringFromSelector(@selector(suggestions))];
    [coder encodeObject:self.snapshotId forKey:NSStringFromSelector(@selector(snapshotId))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [self init];
    if (self) {
        self.prompt = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(prompt))];
        NSSet *classes = [NSSet setWithObjects:[NSArray class], [ICMRecoveredReplyOption class], nil];
        self.suggestions = [coder decodeObjectOfClasses:classes forKey:NSStringFromSelector(@selector(suggestions))];
        self.snapshotId = [coder decodeObjectOfClass:[NSNumber class] forKey:NSStringFromSelector(@selector(snapshotId))];
    }
    return self;
}
@end
