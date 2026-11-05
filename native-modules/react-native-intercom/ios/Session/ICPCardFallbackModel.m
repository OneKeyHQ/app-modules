// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICPCardFallbackModel.h"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
@implementation ICPCardFallbackModel
@synthesize fallbackURL = _fallbackURL, loadingSpinnerColor = _loadingSpinnerColor;
@synthesize loadingSpinnerImage = _loadingSpinnerImage, cardInsets = _cardInsets, cardSize = _cardSize;
@synthesize webviewLoaded = _webviewLoaded, showCardBorder = _showCardBorder, appPrimaryColor = _appPrimaryColor, cardId = _cardId;
- (instancetype)initWithInsets:(UIEdgeInsets)insets cardSize:(CGSize)size loadingImage:(UIImage *)image loadingColor:(UIColor *)color fallbackURL:(NSURL *)url showCardBorder:(BOOL)showBorder appPrimaryColor:(UIColor *)primaryColor {
    self = [super init];
    if (self) {
        _cardInsets = insets;
        _cardSize = size;
        _loadingSpinnerImage = image;
        _loadingSpinnerColor = color;
        _fallbackURL = url;
        _showCardBorder = showBorder;
        _appPrimaryColor = primaryColor;
    }
    return self;
}
- (NSString *)cardId { return [ICPCardFallbackModel uniqueIdentifier:self.fallbackURL]; }
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToCardFallbackModel:object];
}
- (BOOL)isEqualToCardFallbackModel:(ICPCardFallbackModel *)model {
    if (self == model) return YES;
    if (!model) return NO;
    return [self.cardId isEqual:model.cardId];
}
- (NSUInteger)hash { return self.cardId.hash; }
+ (NSString *)uniqueIdentifier:(NSURL *)url {
    if (!url) return nil;
    NSURLComponents *components = [NSURLComponents componentsWithURL:url resolvingAgainstBaseURL:YES];
    NSArray *queryItems = components.queryItems;
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"name=%@", @"card_id"];
    NSArray *filtered = [queryItems filteredArrayUsingPredicate:predicate];
    NSURLQueryItem *first = [filtered firstObject];
    return first.value;
}
@end
#pragma clang diagnostic pop
