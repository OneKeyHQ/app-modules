// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBLinkBlockModels.h"
#import "ITBLinkBlockDependencies.h"
#import "ICMBlockAppearance.h"
@implementation ITBLinkBlockAuthor
@synthesize initialsImage = _initialsImage, writtenByAttributedString = _writtenByAttributedString;
@synthesize linkUpdatedAtAttributedString = _linkUpdatedAtAttributedString, name = _name;
@synthesize firstName = _firstName, lastName = _lastName, avatar = _avatar, avatarRadius = _avatarRadius;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) [self updateAppearance:appearance];
    return self;
}
- (void)updateAppearance:(id)appearance {
    if ([appearance respondsToSelector:@selector(linkAuthorInitialsImageForName:diameter:)])
        self.initialsImage = [appearance linkAuthorInitialsImageForName:self.name diameter:[appearance avatarRadius]];
    self.writtenByAttributedString = [NSAttributedString new];
    if ([appearance respondsToSelector:@selector(linkWrittenByAttributedStringForName:)]) {
        NSString *first = self.firstName;
        NSString *name = first ? first : self.name;
        NSAttributedString *written = [appearance linkWrittenByAttributedStringForName:name];
        if (written.length) {
            self.writtenByAttributedString = written;
            self.avatarRadius = [appearance avatarRadius];
        }
    }
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([[self class] isNonEmptyString:value] &&
        ([key isEqualToString:@"name"] || [key isEqualToString:@"firstName"] ||
         [key isEqualToString:@"lastName"] || [key isEqualToString:@"avatar"]))
        [super setValue:value forKey:key];
}
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [super validateBlockDictionary:dictionary] && [self isNonEmptyString:dictionary[@"name"]];
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBLinkBlockAuthor *other = object;
    if (!((!other.name && !self.name) || [other.name isEqualToString:self.name])) return NO;
    if (!((!other.firstName && !self.firstName) || [other.firstName isEqualToString:self.firstName])) return NO;
    if (!((!other.lastName && !self.lastName) || [other.lastName isEqualToString:self.lastName])) return NO;
    if (!((!other.avatar && !self.avatar) || [other.avatar isEqualToString:self.avatar])) return NO;
    if (other.avatarRadius != self.avatarRadius) return NO;
    return (!other.writtenByAttributedString && !self.writtenByAttributedString) ||
           [other.writtenByAttributedString isEqualToAttributedString:self.writtenByAttributedString];
}
- (id)copyWithZone:(NSZone *)zone {
    ITBLinkBlockAuthor *copy = [super copyWithZone:zone];
    copy.name = self.name;
    copy.firstName = self.firstName;
    copy.lastName = self.lastName;
    copy.avatar = self.avatar;
    copy.avatarRadius = self.avatarRadius;
    copy.initialsImage = self.initialsImage;
    copy.writtenByAttributedString = self.writtenByAttributedString;
    return copy;
}
@end
@implementation ITBLinkBlockImage
@synthesize url = _url, alt = _alt, size = _size;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self && [dictionary[@"width"] isKindOfClass:[NSNumber class]] &&
        [dictionary[@"height"] isKindOfClass:[NSNumber class]]) {
        NSNumber *width = dictionary[@"width"];
        NSNumber *height = dictionary[@"height"];
        if (width.integerValue >= 1 && height.integerValue >= 1)
            self.size = CGSizeMake(width.integerValue, height.integerValue);
    }
    return self;
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([[self class] isNonEmptyString:value] &&
        ([key isEqualToString:@"url"] || [key isEqualToString:@"alt"]))
        [super setValue:value forKey:key];
}
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [super validateBlockDictionary:dictionary] && [self isNonEmptyString:dictionary[@"url"]];
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBLinkBlockImage *other = object;
    if (!((!other.url && !self.url) || [other.url isEqualToString:self.url])) return NO;
    if (!((!other.alt && !self.alt) || [other.alt isEqualToString:self.alt])) return NO;
    CGSize otherSize = other.size;
    CGSize size = self.size;
    return otherSize.width == size.width && otherSize.height == size.height;
}
- (id)copyWithZone:(NSZone *)zone {
    ITBLinkBlockImage *copy = [super copyWithZone:zone];
    copy.url = self.url;
    copy.alt = self.alt;
    copy.size = self.size;
    return copy;
}
@end
