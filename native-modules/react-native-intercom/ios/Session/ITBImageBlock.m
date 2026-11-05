#import "ITBImageBlock.h"
#import "ITBImageBlockView.h"
#import "ITBImageLoader.h"
#import "ICMBlockAppearance.h"
#import "ICMConversationPartDependencies.h"

@implementation ITBImageBlock
@synthesize url = _url, linkUrl = _linkUrl, width = _width, height = _height;
@synthesize imageSize = _imageSize, padding = _padding, cornerRadius = _cornerRadius;
@synthesize upload = _upload, loadingViewColorType = _loadingViewColorType;
@synthesize inlineImagePadding = _inlineImagePadding, singleImagePadding = _singleImagePadding;
@synthesize inlineImageCornerRadius = _inlineImageCornerRadius, singleImageCornerRadius = _singleImageCornerRadius;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) {
        _imageSize = CGSizeZero;
        [self updateAppearance:appearance];
    }
    return self;
}
- (void)prepare:(void (^)(void))completion {
    [[ITBImageLoader defaultLoader] loadImageWithURLString:self.url completion:^(__unused id image) {
        if (completion) completion();
    }];
}
- (NSInteger)type { return 5; }
- (NSString *)plainTextRepresentation { return self.linkUrl.length ? self.linkUrl : nil; }
- (void)setDisplayMode:(NSInteger)displayMode {
    [super setDisplayMode:displayMode];
    _padding = self.displayMode == 0 ? _inlineImagePadding : _singleImagePadding;
    _cornerRadius = self.displayMode == 0 ? _inlineImageCornerRadius : _singleImageCornerRadius;
}
- (Class)viewClass { return [ITBImageBlockView class]; }
- (void)setWidth:(CGFloat)width {
    _width = width == 0 ? 400 : width;
    CGFloat scaledWidth = _width / [UIScreen mainScreen].scale;
    CGSize size = self.imageSize;
    self.imageSize = CGSizeMake(scaledWidth, size.height);
}
- (void)setHeight:(CGFloat)height {
    _height = height == 0 ? 600 : height;
    CGSize size = self.imageSize;
    CGFloat scaledHeight = _height / [UIScreen mainScreen].scale;
    self.imageSize = CGSizeMake(size.width, scaledHeight);
}
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    if (![super validateBlockDictionary:dictionary]) return NO;
    return [self isNonEmptyString:dictionary[@"url"]];
}
- (ITBIntentType)intentType { return ITBIntentTypeUrl; }
- (NSURL *)intentUrl { return [NSURL URLWithString:self.linkUrl]; }
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[ITBImageBlock class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBImageBlock *other = object;
    if (!((!other.url && !self.url) || [other.url isEqualToString:self.url])) return NO;
    if (!((!other.linkUrl && !self.linkUrl) || [other.linkUrl isEqualToString:self.linkUrl])) return NO;
    if (other.width != self.width) return NO;
    return other.height == self.height;
}
- (void)updateAppearance:(id)appearance {
    _singleImagePadding = [appearance singleImagePadding];
    _inlineImagePadding = [appearance inlineImagePadding];
    _singleImageCornerRadius = [appearance singleImageCornerRadius];
    _inlineImageCornerRadius = [appearance inlineImageCornerRadius];
    _padding = self.displayMode == 0 ? _inlineImagePadding : _singleImagePadding;
    _cornerRadius = self.displayMode == 0 ? _inlineImageCornerRadius : _singleImageCornerRadius;
    _loadingViewColorType = [appearance loadingViewColorType];
}
- (id)copyWithZone:(NSZone *)zone {
    ITBImageBlock *copy = [super copyWithZone:zone];
    copy.url = self.url;
    copy.linkUrl = self.linkUrl;
    copy.width = self.width;
    copy.height = self.height;
    copy.imageSize = self.imageSize;
    copy.padding = self.padding;
    copy.cornerRadius = self.cornerRadius;
    copy.upload = self.upload;
    copy.loadingViewColorType = self.loadingViewColorType;
    return copy;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [super encodeWithCoder:coder];
    [coder encodeObject:self.url forKey:NSStringFromSelector(@selector(url))];
    [coder encodeObject:self.linkUrl forKey:NSStringFromSelector(@selector(linkUrl))];
    [coder encodeFloat:(float)self.width forKey:NSStringFromSelector(@selector(width))];
    [coder encodeFloat:(float)self.height forKey:NSStringFromSelector(@selector(height))];
    [coder encodeCGSize:self.imageSize forKey:NSStringFromSelector(@selector(imageSize))];
    [coder encodeFloat:(float)self.padding forKey:NSStringFromSelector(@selector(padding))];
    [coder encodeFloat:(float)self.cornerRadius forKey:NSStringFromSelector(@selector(cornerRadius))];
    [coder encodeObject:self.upload forKey:NSStringFromSelector(@selector(upload))];
    [coder encodeInteger:self.loadingViewColorType forKey:NSStringFromSelector(@selector(loadingViewColorType))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        self.url = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(url))];
        self.linkUrl = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(linkUrl))];
        self.width = [coder decodeFloatForKey:NSStringFromSelector(@selector(width))];
        self.height = [coder decodeFloatForKey:NSStringFromSelector(@selector(height))];
        self.imageSize = [coder decodeCGSizeForKey:NSStringFromSelector(@selector(imageSize))];
        self.padding = [coder decodeFloatForKey:NSStringFromSelector(@selector(padding))];
        self.cornerRadius = [coder decodeFloatForKey:NSStringFromSelector(@selector(cornerRadius))];
        self.upload = [coder decodeObjectOfClass:[ITBUpload class] forKey:NSStringFromSelector(@selector(upload))];
        self.loadingViewColorType = [coder decodeIntegerForKey:NSStringFromSelector(@selector(loadingViewColorType))];
    }
    return self;
}
@end
