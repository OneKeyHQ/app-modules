#import "ICMActionButton.h"
#import "ICMBlockAppearanceDependencies.h"
#import "UIFont+ICMWeight.h"
@implementation ICMActionButton
- (instancetype)init {
    _theme = [ICMRecoveredDefaultThemeProvider shared].theme;
    return [self initWithTitle:@"" style:0 backgroundColor:self.theme.neutralContainer foregroundColor:self.theme.textDefault];
}
- (instancetype)initFilledButtonWithTitle:(NSString *)title {
    _theme = [ICMRecoveredDefaultThemeProvider shared].theme;
    return [self initWithTitle:title style:0 backgroundColor:self.theme.primaryContainer foregroundColor:self.theme.primaryOnContainer];
}
- (instancetype)initBorderedButtonWithTitle:(NSString *)title {
    _theme = [ICMRecoveredDefaultThemeProvider shared].theme;
    return [self initWithTitle:title style:1 backgroundColor:self.theme.neutralContainer foregroundColor:self.theme.textDefault];
}
- (instancetype)initWithTitle:(NSString *)title style:(NSInteger)style backgroundColor:(UIColor *)background foregroundColor:(UIColor *)foreground {
    _theme = [ICMRecoveredDefaultThemeProvider shared].theme;
    if ((self = [super init])) {
        UIButtonConfiguration *config = [UIButtonConfiguration plainButtonConfiguration];
        config.background.backgroundColor = background;
        config.background.cornerRadius = 10;
        config.cornerStyle = UIButtonConfigurationCornerStyleFixed;
        if (title) config.attributedTitle = [[NSMutableAttributedString alloc] initWithString:title attributes:@{NSFontAttributeName: [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline weight:UIFontWeightBold]}];
        self.configuration = config;
        [self setupButtonConfiguration];
        [self configureForStyle:style backgroundColor:background foregroundColor:foreground];
        [self.heightAnchor constraintGreaterThanOrEqualToConstant:44].active = YES;
    }
    return self;
}
- (void)setupButtonConfiguration {
    UIButtonConfiguration *config = self.configuration;
    config.imagePlacement = NSDirectionalRectEdgeTrailing;
    config.imagePadding = 6;
    config.contentInsets = NSDirectionalEdgeInsetsMake(12, 16, 12, 16);
    self.configuration = config;
}
- (void)configureForStyle:(NSInteger)style backgroundColor:(UIColor *)background foregroundColor:(UIColor *)foreground {
    UIButtonConfiguration *config = self.configuration;
    if (style == 0) {
        config.titleTextAttributesTransformer = ^NSDictionary<NSAttributedStringKey,id> *(NSDictionary<NSAttributedStringKey,id> *incoming) {
            NSMutableDictionary *attributes = [incoming mutableCopy];
            attributes[NSForegroundColorAttributeName] = foreground;
            attributes[NSFontAttributeName] = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline weight:UIFontWeightSemibold];
            return attributes;
        };
        config.background.backgroundColor = background ?: self.theme.primaryContainer;
        config.background.strokeWidth = 2;
        config.background.strokeColor = [self.theme.textDefault colorWithAlphaComponent:0.2];
        self.tintColor = foreground ?: self.theme.primaryOnContainer;
        self.configuration = config;
    } else if (style == 1) {
        config.titleTextAttributesTransformer = ^NSDictionary<NSAttributedStringKey,id> *(NSDictionary<NSAttributedStringKey,id> *incoming) {
            NSMutableDictionary *attributes = [incoming mutableCopy];
            attributes[NSForegroundColorAttributeName] = self.theme.textDefault;
            attributes[NSFontAttributeName] = [UIFont preferredFontForTextStyle:UIFontTextStyleSubheadline weight:UIFontWeightSemibold];
            return attributes;
        };
        config.background.backgroundColor = self.theme.base;
        config.background.strokeWidth = 2;
        config.background.strokeColor = [self.theme.textDefault colorWithAlphaComponent:0.2];
        self.tintColor = self.theme.iconDefault;
        self.configuration = config;
    }
}
- (void)traitCollectionDidChange:(UITraitCollection *)previousTraitCollection {
    [super traitCollectionDidChange:previousTraitCollection];
    if (UIContentSizeCategoryIsAccessibilityCategory(self.traitCollection.preferredContentSizeCategory)) [self setupButtonFontConfigurationForAXSizes];
    else [self setupButtonConfiguration];
}
- (void)setupButtonFontConfigurationForAXSizes {
    UIButtonConfiguration *config = self.configuration;
    config.imagePlacement = NSDirectionalRectEdgeTrailing;
    config.imagePadding = 6;
    config.contentInsets = NSDirectionalEdgeInsetsMake(12, 16, 12, 16);
    config.titleTextAttributesTransformer = ^NSDictionary<NSAttributedStringKey,id> *(NSDictionary<NSAttributedStringKey,id> *incoming) {
        NSMutableDictionary *attributes = [incoming mutableCopy];
        attributes[NSFontAttributeName] = [UIFont preferredFontForTextStyle:UIFontTextStyleCaption1 weight:UIFontWeightSemibold];
        return attributes;
    };
    self.configuration = config;
}
@end
