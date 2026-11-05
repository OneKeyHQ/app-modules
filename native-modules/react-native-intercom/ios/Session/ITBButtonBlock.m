// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBButtonBlock.h"
#import "ITBButtonBlockView.h"
#import "ICMBlockAppearance.h"

@implementation ITBButtonBlock
@synthesize url = _url, font = _font, textColor = _textColor, backgroundColor = _backgroundColor;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) [self updateAppearance:appearance];
    return self;
}
- (void)updateAppearance:(id)appearance {
    [super updateAppearance:appearance];
    _font = [appearance buttonFont];
    _textColor = [appearance buttonTextColor];
    _backgroundColor = [appearance buttonBackgroundColor];
}
- (Class)viewClass { return [ITBButtonBlockView class]; }
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"linkUrl"]) [super setValue:value forKey:@"url"];
    else [super setValue:value forKey:key];
}
- (NSInteger)type { return 6; }
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    if (![super validateBlockDictionary:dictionary]) return NO;
    if (![self isNonEmptyString:dictionary[@"linkUrl"]]) return NO;
    // Original 0x7772c loads the literal key, not dictionary["text"].
    return [self isNonEmptyString:@"text"];
}
- (ITBIntentType)intentType { return ITBIntentTypeUrl; }
- (NSURL *)intentUrl { return [NSURL URLWithString:self.url]; }
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBButtonBlock *other = object;
    if (!((!other.url && !self.url) || [other.url isEqualToString:self.url])) return NO;
    return (!other.text && !self.text) || [other.text isEqualToString:self.text];
}
- (id)copyWithZone:(NSZone *)zone {
    ITBButtonBlock *copy = [super copyWithZone:zone];
    copy.url = self.url;
    copy.font = self.font;
    copy.textColor = self.textColor;
    copy.backgroundColor = self.backgroundColor;
    return copy;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [super encodeWithCoder:coder];
    [coder encodeObject:self.url forKey:NSStringFromSelector(@selector(url))];
    [coder encodeObject:self.font forKey:NSStringFromSelector(@selector(font))];
    [coder encodeObject:self.textColor forKey:NSStringFromSelector(@selector(textColor))];
    [coder encodeObject:self.backgroundColor forKey:NSStringFromSelector(@selector(backgroundColor))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        self.url = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(url))];
        self.font = [coder decodeObjectOfClass:[UIFont class] forKey:NSStringFromSelector(@selector(font))];
        self.textColor = [coder decodeObjectOfClass:[UIColor class] forKey:NSStringFromSelector(@selector(textColor))];
        self.backgroundColor = [coder decodeObjectOfClass:[UIColor class] forKey:NSStringFromSelector(@selector(backgroundColor))];
    }
    return self;
}
@end
