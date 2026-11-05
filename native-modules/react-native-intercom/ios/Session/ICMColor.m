#import "ICMColor.h"
#import <math.h>
#import "ICMColorHelperImports.h"
#import "ICMBlockAppearanceDependencies.h"
#import "UIColor+HSL.h"
@interface ICMRecoveredTheme (OriginalRemainingColorFields)
@property (nonatomic, readonly) UIColor *textInversed;
@property (nonatomic, readonly) UIColor *statusNotificationFill;
@property (nonatomic, readonly) UIColor *textMutedExtra;
@property (nonatomic, readonly) UIColor *successFill;
@property (nonatomic, readonly) UIColor *warningFill;
@end

@implementation ICMColor
+ (UIColor *)colorWithHexString:(NSString *)string {
    if (!string) return UIColor.systemGrayColor;
    unsigned int rgb = 0;
    NSScanner *scanner = [NSScanner scannerWithString:string];
    scanner.charactersToBeSkipped = [NSCharacterSet characterSetWithCharactersInString:@"#"];
    [scanner scanHexInt:&rgb];
    return [self colorWithR:(rgb >> 16) & 0xff g:(rgb >> 8) & 0xff b:rgb & 0xff];
}
+ (UIColor *)colorWithR:(int)red g:(int)green b:(int)blue {
    return [self colorWithR:red g:green b:blue alpha:1];
}
+ (UIColor *)colorWithR:(int)red g:(int)green b:(int)blue alpha:(CGFloat)alpha {
    return [UIColor colorWithRed:(float)red / 255.0f green:(float)green / 255.0f blue:(float)blue / 255.0f alpha:alpha];
}
+ (NSString *)hexStringFromColor:(UIColor *)color {
    CGColorRef cgColor = color.CGColor;
    const CGFloat *components = CGColorGetComponents(cgColor);
    size_t count = CGColorGetNumberOfComponents(cgColor);
    CGFloat red = components && count > 0 ? components[0] : 0;
    CGFloat green = components && count > 1 ? components[1] : 0;
    CGFloat blue = components && count > 2 ? components[2] : 0;
    return [NSString stringWithFormat:@"#%02lX%02lX%02lX", lroundf((float)(red * 255)), lroundf((float)(green * 255)), lroundf((float)(blue * 255))];
}
+ (UIColor *)helpCenterSearchBarBackgroundColor { return [ICMRecoveredDefaultThemeProvider shared].theme.neutralContainer; }
+ (UIColor *)intercomLinkColor { return [ICMRecoveredDefaultThemeProvider shared].theme.textMuted; }
+ (UIColor *)composerSeparatorColor { return [ICMRecoveredDefaultThemeProvider shared].theme.neutralBorder; }
+ (UIColor *)closeButtonDarkColorWithAlpha:(CGFloat)alpha { return [[ICMRecoveredDefaultThemeProvider shared].theme.textDefault colorWithAlphaComponent:alpha]; }
+ (UIColor *)closeButtonLightColor { return [self closeButtonLightColorWithAlpha:1]; }
+ (UIColor *)closeButtonLightColorWithAlpha:(CGFloat)alpha { return [[ICMRecoveredDefaultThemeProvider shared].theme.textInversed colorWithAlphaComponent:alpha]; }
+ (UIColor *)messageBadgeBackgroundColor { return [ICMRecoveredDefaultThemeProvider shared].theme.statusNotificationFill; }
+ (UIColor *)cardLoadingColor { return [ICMRecoveredDefaultThemeProvider shared].theme.neutralContainerEmphasis; }
+ (UIColor *)black70 { return [ICMRecoveredDefaultThemeProvider shared].theme.textMutedExtra; }
+ (UIColor *)activeColour { return [ICMRecoveredDefaultThemeProvider shared].theme.successFill; }
+ (UIColor *)awayColour { return [ICMRecoveredDefaultThemeProvider shared].theme.warningFill; }
+ (UIColor *)buttonTextColorVarient:(UIColor *)color {
    CGFloat lightness = color.lightness;
    return lightness > 0.9 || !(lightness < 0.35) ? [color darken:lightness - 0.3] : color;
}
+ (UIColor *)buttonBackgroundColorVarient:(UIColor *)color {
    CGFloat lightness = color.lightness;
    return lightness <= 0.9 ? [color lighten:0.95 - lightness] : [color darken:lightness - 0.85];
}
+ (UIColor *)buttonHighlightColor:(UIColor *)color {
    CGFloat lightness = color.lightness;
    return lightness <= 0.3 ? [color lighten:0.2] : [color darken:0.1];
}
+ (UIColor *)textColorForBackgroundColor:(UIColor *)color { return [ICMRecoveredColorHelper suitableForegroundColorFor:color theme:[ICMRecoveredDefaultThemeProvider shared].theme]; }
+ (UIColor *)borderColorForBackgroundColor:(UIColor *)color {
    return [self isLightColor:color] ? [ICMColor colorWithR:0 g:0 b:0 alpha:0.1] : [ICMColor colorWithR:255 g:255 b:255 alpha:0.2];
}
+ (BOOL)isLightColor:(UIColor *)color { return color.lightness > 0.5; }
@end
