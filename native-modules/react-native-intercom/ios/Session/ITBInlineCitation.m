// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBInlineCitation.h"

@implementation ITBInlineCitation
@synthesize sourceIndex = _sourceIndex, entityType = _entityType, href = _href, title = _title;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary {
    self = [super init];
    if (self) {
        [self setValue:dictionary[@"data-source-index"] forKey:@"sourceIndex"];
        [self setValue:dictionary[@"href"] forKey:@"href"];
        [self setValue:dictionary[@"title"] forKey:@"title"];
        self.entityType = [dictionary[@"data-entity-type"] isEqualToString:@"external"];
    }
    return self;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    ITBInlineCitation *other = object;
    return [self.title isEqualToString:other.title] && [self.href isEqualToString:other.href]
        && [self.sourceIndex isEqualToString:other.sourceIndex] && self.entityType == other.entityType;
}
- (NSUInteger)hash {
    return ((self.sourceIndex.hash * 31 + self.href.hash) * 31 + self.title.hash) * 31 + (NSUInteger)self.entityType;
}
@end
