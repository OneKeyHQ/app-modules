#import "ICMImageTextBlock.h"
#import "ICMBlockAppearance.h"
@implementation ICMImageTextBlock
@synthesize title = _title, url = _url, width = _width, height = _height;
@synthesize textColor = _textColor, titleTextColor = _titleTextColor, loadingViewColorType = _loadingViewColorType;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) {
        // The original initializer retains these inputs directly; later setters copy.
        _title = dictionary[@"title"];
        _url = dictionary[@"imageUrl"];
        _width = [dictionary[@"imageWidth"] floatValue];
        _height = [dictionary[@"imageHeight"] floatValue];
        [self updateAppearance:appearance];
    }
    return self;
}
- (NSInteger)type { return 20; }
- (void)updateAppearance:(ICMBlockAppearance *)appearance {
    self.textColor = appearance.textColor;
    self.titleTextColor = appearance.headingTextColor;
    self.loadingViewColorType = appearance.loadingViewColorType;
}
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [super validateBlockDictionary:dictionary]
        && [self isNonEmptyString:dictionary[@"text"]]
        && [self isNonEmptyString:dictionary[@"imageUrl"]]
        && [self validateNumber:dictionary[@"imageWidth"]]
        && [self validateNumber:dictionary[@"imageHeight"]];
}
+ (BOOL)validateNumber:(id)value {
    BOOL isNumber = [value isKindOfClass:[NSNumber class]];
    return value != nil && isNumber;
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    ICMImageTextBlock *other = object;
    if (!((!self.title && !other.title) || [self.title isEqual:other.title])) return NO;
    if (!((!self.text && !other.text) || [self.text isEqual:other.text])) return NO;
    if (!((!self.url && !other.url) || [self.url isEqual:other.url])) return NO;
    if (self.width != other.width) return NO;
    if (self.height != other.height) return NO;
    if (!((!self.textColor && !other.textColor) || [self.textColor isEqual:other.textColor])) return NO;
    return (!self.titleTextColor && !other.titleTextColor) || [self.titleTextColor isEqual:other.titleTextColor];
}
- (id)copyWithZone:(NSZone *)zone {
    ICMImageTextBlock *copy = [super copyWithZone:zone];
    copy.title = self.title;
    copy.text = self.text;
    copy.url = self.url;
    copy.width = self.width;
    copy.height = self.height;
    copy.textColor = self.textColor;
    copy.titleTextColor = self.titleTextColor;
    copy.loadingViewColorType = self.loadingViewColorType;
    return copy;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [super encodeWithCoder:coder];
    [coder encodeObject:self.title forKey:NSStringFromSelector(@selector(title))];
    [coder encodeObject:self.url forKey:NSStringFromSelector(@selector(url))];
    [coder encodeFloat:(float)self.width forKey:NSStringFromSelector(@selector(width))];
    [coder encodeFloat:(float)self.height forKey:NSStringFromSelector(@selector(height))];
    [coder encodeObject:self.textColor forKey:NSStringFromSelector(@selector(textColor))];
    [coder encodeObject:self.titleTextColor forKey:NSStringFromSelector(@selector(titleTextColor))];
    [coder encodeInteger:self.loadingViewColorType forKey:NSStringFromSelector(@selector(loadingViewColorType))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        self.title = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(title))];
        self.url = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(url))];
        self.width = [coder decodeFloatForKey:NSStringFromSelector(@selector(width))];
        self.height = [coder decodeFloatForKey:NSStringFromSelector(@selector(height))];
        self.textColor = [coder decodeObjectOfClass:[UIColor class] forKey:NSStringFromSelector(@selector(textColor))];
        self.titleTextColor = [coder decodeObjectOfClass:[UIColor class] forKey:NSStringFromSelector(@selector(titleTextColor))];
        self.loadingViewColorType = [coder decodeIntegerForKey:NSStringFromSelector(@selector(loadingViewColorType))];
    }
    return self;
}
@end
