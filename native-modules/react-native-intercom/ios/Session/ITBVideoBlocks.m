#import "ITBVideoBlock.h"
#import "ITBVideoViewDependencies.h"
#import "ITBBlockViewsReuseManager.h"
#import "ICMBlockAppearance.h"
@implementation ITBVideoBlock
@synthesize provider = _provider, videoId = _videoId, loadingViewColorType = _loadingViewColorType, videoSize = _videoSize;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) [self updateAppearance:appearance];
    return self;
}
- (void)prepare:(void (^)(void))completion {
    dispatch_async(dispatch_get_main_queue(), ^{
        ITBBlockView *view = [[ITBBlockViewsReuseManager sharedInstance] dequeueReusableViewForBlock:self];
        [[ITBBlockViewsReuseManager sharedInstance] enqueueReusableViewForReuse:view];
        if (completion) completion();
    });
}
- (Class)viewClass { return [ITBVideoBlockView class]; }
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([key isEqualToString:@"id"]) [self setValue:value forKey:@"videoId"];
    else [super setValue:value forKey:key];
}
+ (BOOL)isProviderSupported:(NSString *)provider {
    return [@"youtube" isEqualToString:provider] || [@"vimeo" isEqualToString:provider]
        || [@"wistia" isEqualToString:provider] || [@"streamio" isEqualToString:provider]
        || [@"loom" isEqualToString:provider] || [@"vidyard" isEqualToString:provider];
}
- (NSString *)blockUniqueIdentifier {
    return [NSString stringWithFormat:@"%@_%@", self.provider, self.videoId];
}
- (NSInteger)type { return 10; }
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [super validateBlockDictionary:dictionary] && [self isNonEmptyString:dictionary[@"provider"]]
        && [self isProviderSupported:dictionary[@"provider"]] && [self isNonEmptyString:dictionary[@"id"]];
}
- (void)updateAppearance:(ICMBlockAppearance *)appearance { _loadingViewColorType = appearance.loadingViewColorType; }
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    BOOL baseEqual = [super isEqual:object];
    ITBVideoBlock *other = object;
    if (!baseEqual) return NO;
    if (!((!other.provider && !self.provider) || [other.provider isEqualToString:self.provider])) return NO;
    if (!((!other.videoId && !self.videoId) || [other.videoId isEqualToString:self.videoId])) return NO;
    CGSize otherSize = other.videoSize;
    CGSize size = self.videoSize;
    return otherSize.width == size.width && otherSize.height == size.height;
}
- (id)copyWithZone:(NSZone *)zone {
    ITBVideoBlock *copy = [super copyWithZone:zone];
    copy.provider = self.provider;
    copy.videoId = self.videoId;
    copy.videoSize = self.videoSize;
    copy.loadingViewColorType = self.loadingViewColorType;
    return copy;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [super encodeWithCoder:coder];
    [coder encodeObject:self.provider forKey:NSStringFromSelector(@selector(provider))];
    [coder encodeObject:self.videoId forKey:NSStringFromSelector(@selector(videoId))];
    [coder encodeCGSize:self.videoSize forKey:NSStringFromSelector(@selector(videoSize))];
    [coder encodeInteger:self.loadingViewColorType forKey:NSStringFromSelector(@selector(loadingViewColorType))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        self.provider = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(provider))];
        self.videoId = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(videoId))];
        self.videoSize = [coder decodeCGSizeForKey:NSStringFromSelector(@selector(videoSize))];
        self.loadingViewColorType = [coder decodeIntegerForKey:NSStringFromSelector(@selector(loadingViewColorType))];
    }
    return self;
}
@end
@implementation ITBVideoFileBlock
@synthesize url = _url, thumbnailUrl = _thumbnailUrl, duration = _duration, width = _width, height = _height;
@synthesize padding = _padding, cornerRadius = _cornerRadius, loadingViewColorType = _loadingViewColorType;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) [self updateAppearance:appearance];
    return self;
}
- (NSInteger)type { return 19; }
- (Class)viewClass { return [ITBVideoFileBlockView class]; }
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [super validateBlockDictionary:dictionary] && [self isNonEmptyString:dictionary[@"url"]];
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[ITBVideoFileBlock class]]) return NO;
    BOOL baseEqual = [super isEqual:object];
    ITBVideoFileBlock *other = object;
    if (!baseEqual) return NO;
    if (!((!other.url && !self.url) || [other.url isEqualToString:self.url])) return NO;
    return (!other.thumbnailUrl && !self.thumbnailUrl) || [other.thumbnailUrl isEqualToString:self.thumbnailUrl];
}
- (id)copyWithZone:(NSZone *)zone {
    ITBVideoFileBlock *copy = [super copyWithZone:zone];
    copy.url = self.url;
    copy.thumbnailUrl = self.thumbnailUrl;
    copy.cornerRadius = self.cornerRadius;
    copy.padding = self.padding;
    copy.loadingViewColorType = self.loadingViewColorType;
    copy.duration = self.duration;
    copy.width = self.width;
    copy.height = self.height;
    return copy;
}
- (void)updateAppearance:(ICMBlockAppearance *)appearance {
    _padding = self.displayMode ? appearance.singleImagePadding : appearance.inlineImagePadding;
    _cornerRadius = self.displayMode ? appearance.singleImageCornerRadius : appearance.inlineImageCornerRadius;
    _loadingViewColorType = appearance.loadingViewColorType;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [super encodeWithCoder:coder];
    [coder encodeObject:self.url forKey:NSStringFromSelector(@selector(url))];
    [coder encodeObject:self.thumbnailUrl forKey:NSStringFromSelector(@selector(thumbnailUrl))];
    [coder encodeFloat:(float)self.padding forKey:NSStringFromSelector(@selector(padding))];
    [coder encodeFloat:(float)self.cornerRadius forKey:NSStringFromSelector(@selector(cornerRadius))];
    [coder encodeInteger:self.loadingViewColorType forKey:NSStringFromSelector(@selector(loadingViewColorType))];
    [coder encodeFloat:(float)self.duration forKey:NSStringFromSelector(@selector(duration))];
    [coder encodeFloat:(float)self.width forKey:NSStringFromSelector(@selector(width))];
    [coder encodeFloat:(float)self.height forKey:NSStringFromSelector(@selector(height))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        self.url = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(url))];
        self.thumbnailUrl = [coder decodeObjectOfClass:[NSString class] forKey:NSStringFromSelector(@selector(thumbnailUrl))];
        self.padding = [coder decodeFloatForKey:NSStringFromSelector(@selector(padding))];
        self.cornerRadius = [coder decodeFloatForKey:NSStringFromSelector(@selector(cornerRadius))];
        self.loadingViewColorType = [coder decodeIntegerForKey:NSStringFromSelector(@selector(loadingViewColorType))];
        self.duration = [coder decodeFloatForKey:NSStringFromSelector(@selector(duration))];
        self.width = [coder decodeFloatForKey:NSStringFromSelector(@selector(width))];
        self.height = [coder decodeFloatForKey:NSStringFromSelector(@selector(height))];
    }
    return self;
}
@end
