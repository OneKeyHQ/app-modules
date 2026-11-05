// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBTextBlock.h"
#import "ICMBlockAppearance.h"

@implementation ITBHeadingBlock
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    UIFont *font = [appearance headingFont];
    UIColor *color = [appearance headingTextColor];
    return [super initWithDictionary:dictionary appearance:appearance font:font textColor:color];
}
- (void)updateAppearance:(id)appearance font:(UIFont *)font textColor:(UIColor *)color {
    (void)font;
    (void)color;
    UIFont *headingFont = [appearance headingFont];
    UIColor *headingColor = [appearance headingTextColor];
    [super updateAppearance:appearance font:headingFont textColor:headingColor];
}
- (NSInteger)type { return 1; }
@end

@implementation ITBSubHeadingBlock
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    UIFont *font = [appearance subheadingFont];
    UIColor *color = [appearance subHeadingTextColor];
    return [super initWithDictionary:dictionary appearance:appearance font:font textColor:color];
}
- (void)updateAppearance:(id)appearance font:(UIFont *)font textColor:(UIColor *)color {
    (void)font;
    (void)color;
    UIFont *headingFont = [appearance subheadingFont];
    UIColor *headingColor = [appearance subHeadingTextColor];
    [super updateAppearance:appearance font:headingFont textColor:headingColor];
}
- (NSInteger)type { return 2; }
@end
