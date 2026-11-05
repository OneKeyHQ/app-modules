// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMReactions.h"
@implementation ICMReactionsOption
@synthesize index = _index, unicodeEmoticon = _unicodeEmoticon, imageUrl = _imageUrl;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMReactionsOption *option = [ICMReactionsOption new];
    [option setAttributesFromDictionary:dictionary];
    return option;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary { [self setValuesForKeysWithDictionary:dictionary]; }
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"unicode_emoticon"]) [self setValue:value forKey:@"unicodeEmoticon"];
    else if ([key isEqualToString:@"image_url"] && [value isKindOfClass:[NSString class]])
        [self setValue:[NSURL URLWithString:value] forKey:@"imageUrl"];
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToOption:object];
}
- (BOOL)isEqualToOption:(ICMReactionsOption *)other {
    if (self == other) return YES;
    if (!other) return NO;
    if (!(self.index == other.index || [self.index isEqualToNumber:other.index])) return NO;
    if (!(self.unicodeEmoticon == other.unicodeEmoticon || [self.unicodeEmoticon isEqualToString:other.unicodeEmoticon])) return NO;
    return self.imageUrl == other.imageUrl || [self.imageUrl isEqual:other.imageUrl];
}
- (NSUInteger)hash {
    NSUInteger hash = self.index.hash;
    hash = 31 * hash + self.unicodeEmoticon.hash;
    return 31 * hash + self.imageUrl.hash;
}
@end

@implementation ICMReactionsSet
@synthesize options = _options, selectedIndex = _selectedIndex;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMReactionsSet *set = [ICMReactionsSet new];
    [set setAttributesFromDictionary:dictionary];
    return set;
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary { [self setValuesForKeysWithDictionary:dictionary]; }
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([key isEqualToString:@"reaction_index"]) [self setValue:value forKey:@"selectedIndex"];
    else if ([key isEqualToString:@"reaction_set"] && [value isKindOfClass:[NSArray class]]) {
        NSMutableArray *options = [NSMutableArray arrayWithCapacity:[value count]];
        for (id dictionary in value) {
            if ([dictionary isKindOfClass:[NSDictionary class]]) [options addObject:[ICMReactionsOption instanceFromDictionary:dictionary]];
        }
        _options = options;
    }
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToReactionsSet:object];
}
- (BOOL)isEqualToReactionsSet:(ICMReactionsSet *)other {
    if (self == other) return YES;
    if (!other) return NO;
    if (!(self.options == other.options || [self.options isEqualToArray:other.options])) return NO;
    return self.selectedIndex == other.selectedIndex || [self.selectedIndex isEqualToNumber:other.selectedIndex];
}
- (NSUInteger)hash {
    NSUInteger hash = self.options.hash;
    return 31 * hash + self.selectedIndex.hash;
}
@end
