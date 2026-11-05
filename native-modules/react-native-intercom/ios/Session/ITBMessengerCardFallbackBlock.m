// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBMessengerCardFallbackBlock.h"
#import "ITBMessengerCardFallbackView.h"
#import "ICPCardFallbackModel.h"
#import "ICMBlockAppearance.h"
@implementation ITBMessengerCardFallbackBlock
@synthesize height = _height, width = _width, fallbackURL = _fallbackURL, loadingSpinnerColor = _loadingSpinnerColor;
@synthesize loadingSpinnerImage = _loadingSpinnerImage, cardInsets = _cardInsets, showCardBorder = _showCardBorder, appPrimaryColor = _appPrimaryColor;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) {
        _fallbackURL = [NSURL URLWithString:dictionary[@"fallbackUrl"]];
        [self updateAppearance:appearance];
    }
    return self;
}
- (void)updateAppearance:(id)appearance {
    _loadingSpinnerColor = [appearance activityIndicatorColor];
    _loadingSpinnerImage = [appearance activityIndicatorImage];
    _cardInsets = [appearance messengerCardInsets];
    _showCardBorder = [appearance showMessengerAppBorder];
    _appPrimaryColor = [appearance appPrimaryColor];
}
- (Class)viewClass { return [ITBMessengerCardFallbackView class]; }
- (NSInteger)type { return 18; }
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [super validateBlockDictionary:dictionary] && [self isNonEmptyString:dictionary[@"fallbackUrl"]];
}
- (NSString *)blockUniqueIdentifier { return [ICPCardFallbackModel uniqueIdentifier:self.fallbackURL]; }
- (id)copyWithZone:(NSZone *)zone {
    ITBMessengerCardFallbackBlock *copy = [super copyWithZone:zone];
    copy.fallbackURL = self.fallbackURL;
    copy.width = self.width;
    copy.height = self.height;
    copy.loadingSpinnerColor = self.loadingSpinnerColor;
    copy.loadingSpinnerImage = self.loadingSpinnerImage;
    copy.cardInsets = self.cardInsets;
    copy.showCardBorder = self.showCardBorder;
    copy.appPrimaryColor = self.appPrimaryColor;
    return copy;
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBMessengerCardFallbackBlock *other = object;
    return (!other.blockUniqueIdentifier && !self.blockUniqueIdentifier) || [other.blockUniqueIdentifier isEqual:self.blockUniqueIdentifier];
}
- (NSUInteger)hash { return self.blockUniqueIdentifier.hash; }
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [super encodeWithCoder:coder];
    [coder encodeInteger:self.height forKey:NSStringFromSelector(@selector(height))];
    [coder encodeInteger:self.width forKey:NSStringFromSelector(@selector(width))];
    [coder encodeObject:self.fallbackURL forKey:NSStringFromSelector(@selector(fallbackURL))];
    [coder encodeObject:self.loadingSpinnerColor forKey:NSStringFromSelector(@selector(loadingSpinnerColor))];
    [coder encodeObject:self.loadingSpinnerImage forKey:NSStringFromSelector(@selector(loadingSpinnerImage))];
    [coder encodeUIEdgeInsets:self.cardInsets forKey:NSStringFromSelector(@selector(cardInsets))];
    [coder encodeBool:self.showCardBorder forKey:NSStringFromSelector(@selector(showCardBorder))];
    [coder encodeObject:self.appPrimaryColor forKey:NSStringFromSelector(@selector(appPrimaryColor))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super initWithCoder:coder];
    if (self) {
        self.height = [coder decodeIntegerForKey:NSStringFromSelector(@selector(height))];
        self.width = [coder decodeIntegerForKey:NSStringFromSelector(@selector(width))];
        self.fallbackURL = [coder decodeObjectOfClass:[NSURL class] forKey:NSStringFromSelector(@selector(fallbackURL))];
        self.loadingSpinnerColor = [coder decodeObjectOfClass:[UIColor class] forKey:NSStringFromSelector(@selector(loadingSpinnerColor))];
        self.loadingSpinnerImage = [coder decodeObjectOfClass:[UIImage class] forKey:NSStringFromSelector(@selector(loadingSpinnerImage))];
        self.cardInsets = [coder decodeUIEdgeInsetsForKey:NSStringFromSelector(@selector(cardInsets))];
        self.showCardBorder = [coder decodeBoolForKey:NSStringFromSelector(@selector(showCardBorder))];
        self.appPrimaryColor = [coder decodeObjectOfClass:[UIColor class] forKey:NSStringFromSelector(@selector(appPrimaryColor))];
    }
    return self;
}
@end
