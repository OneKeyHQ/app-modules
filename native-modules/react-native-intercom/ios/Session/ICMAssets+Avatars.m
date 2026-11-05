#import "ICMAssets+Avatars.h"
#import "ICMBlockAppearanceDependencies.h"
#import "ITBImageLoader.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate11ColorHelper")))
@interface ICMRecoveredColorHelper : NSObject
+ (UIColor *)suitableForegroundColorFor:(UIColor *)background theme:(ICMRecoveredTheme *)theme;
@end
@implementation ICMAssets (OriginalAvatars)
+ (UIImage *)defaultAvatarImageWithInitials:(NSString *)initials diameter:(CGFloat)diameter useActionColorAsBackgroundColor:(BOOL)actionColor {
    if ([initials length] != 0) return [self initialsImage:initials diameter:diameter useActionColorAsBackgroundColor:actionColor];
    return [self defaultAvatarImageWithActionColorAsBackgroundColor:actionColor];
}
+ (UIImage *)initialsImage:(NSString *)initials diameter:(CGFloat)diameter useActionColorAsBackgroundColor:(BOOL)actionColor {
    NSString *key = [NSString stringWithFormat:@"ICM_userInitials_%@_%.0f_%d", initials ?: @"", diameter, actionColor];
    return [[ITBImageLoader defaultLoader] imageForKey:key withGenerationBlock:^UIImage *{
        UIColor *background;
        UIColor *foreground;
        if (actionColor == YES) {
            background = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
            foreground = [ICMRecoveredColorHelper suitableForegroundColorFor:background theme:[ICMRecoveredDefaultThemeProvider shared].theme];
        } else {
            background = [ICMRecoveredDefaultThemeProvider shared].theme.neutralContainerEmphasis;
            foreground = [ICMRecoveredDefaultThemeProvider shared].theme.textDefault;
        }
        UIFont *font = [ICMUtils fontOfSize:13 weight:0];
        NSMutableParagraphStyle *paragraph = [NSMutableParagraphStyle new];
        paragraph.alignment = NSTextAlignmentCenter;
        NSDictionary *attributes = @{NSParagraphStyleAttributeName: paragraph, NSForegroundColorAttributeName: foreground, NSFontAttributeName: font};
        CGSize textSize = [initials sizeWithAttributes:attributes];
        CGRect rect = CGRectMake(0, 0, diameter, diameter);
        CGFloat y = (CGRectGetHeight(rect) - textSize.height) * 0.5;
        CGRect textRect = CGRectMake(0, y, CGRectGetWidth(rect), CGRectGetHeight(rect));
        UIGraphicsImageRenderer *renderer = [[UIGraphicsImageRenderer alloc] initWithSize:CGSizeMake(diameter, diameter)];
        return [renderer imageWithActions:^(UIGraphicsImageRendererContext *context) {
            (void)context;
            UIBezierPath *path = [UIBezierPath bezierPathWithRect:rect];
            [background setFill];
            [path fill];
            [initials drawInRect:textRect withAttributes:attributes];
            if (actionColor == YES) {
                UIColor *overlay = [[UIColor blackColor] colorWithAlphaComponent:0.2];
                UIBezierPath *overlayPath = [UIBezierPath bezierPathWithRect:rect];
                [overlay setFill];
                [overlayPath fill];
            }
        }];
    }];
}
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
+ (UIImage *)defaultAvatarImageWithActionColorAsBackgroundColor:(BOOL)actionColor {
    return [[ITBImageLoader defaultLoader] imageForKey:@"ICM_defaultAvatarImage" withGenerationBlock:^UIImage *{
        UIGraphicsBeginImageContextWithOptions(CGSizeMake(100, 100), YES, 0);
        UIColor *background;
        UIColor *foreground;
        if (actionColor == YES) {
            foreground = [ICMRecoveredDefaultThemeProvider shared].theme.primaryOnContainer;
            background = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
        } else {
            background = [ICMRecoveredDefaultThemeProvider shared].theme.neutralContainerEmphasis;
            foreground = [ICMRecoveredColorHelper suitableForegroundColorFor:background theme:[ICMRecoveredDefaultThemeProvider shared].theme];
        }
        UIBezierPath *backdrop = [UIBezierPath bezierPathWithRect:CGRectMake(-100, -100, 200, 200)];
        [background setFill];
        [backdrop fill];
        UIBezierPath *path = [UIBezierPath bezierPath];
        [path moveToPoint:CGPointMake(64.24000000000001, 57.47)];
        [path addCurveToPoint:CGPointMake(53.5, 52.81) controlPoint1:CGPointMake(61.95, 56.43) controlPoint2:CGPointMake(58.54, 53.739999999999995)];
        [path addCurveToPoint:CGPointMake(56.769999999999996, 46.57) controlPoint1:CGPointMake(54.79, 51.4) controlPoint2:CGPointMake(55.760000000000005, 49.19)];
        [path addCurveToPoint:CGPointMake(57.25, 41.92) controlPoint1:CGPointMake(57.35, 45.05) controlPoint2:CGPointMake(57.25, 43.76)];
        [path addCurveToPoint:CGPointMake(57.17, 37.17) controlPoint1:CGPointMake(57.25, 40.56) controlPoint2:CGPointMake(57.5, 38.37)];
        [path addCurveToPoint:CGPointMake(49.96, 32.0) controlPoint1:CGPointMake(56.06, 33.12) controlPoint2:CGPointMake(53.25, 32.0)];
        [path addCurveToPoint:CGPointMake(42.74, 37.19) controlPoint1:CGPointMake(46.66, 32.0) controlPoint2:CGPointMake(43.85, 33.12)];
        [path addCurveToPoint:CGPointMake(42.66, 41.92) controlPoint1:CGPointMake(42.41, 38.38) controlPoint2:CGPointMake(42.66, 40.56)];
        [path addCurveToPoint:CGPointMake(43.15, 46.58) controlPoint1:CGPointMake(42.66, 43.76) controlPoint2:CGPointMake(42.56, 45.06)];
        [path addCurveToPoint:CGPointMake(46.4, 52.83) controlPoint1:CGPointMake(44.16, 49.21) controlPoint2:CGPointMake(45.12, 51.42)];
        [path addCurveToPoint:CGPointMake(35.74, 57.480000000000004) controlPoint1:CGPointMake(41.4, 53.769999999999996) controlPoint2:CGPointMake(38.019999999999996, 56.44)];
        [path addCurveToPoint:CGPointMake(31.0, 61.989999999999995) controlPoint1:CGPointMake(31.02, 59.629999999999995) controlPoint2:CGPointMake(31.0, 61.989999999999995)];
        [path addLineToPoint:CGPointMake(31.0, 66.0)];
        [path addLineToPoint:CGPointMake(69.0, 66.0)];
        [path addLineToPoint:CGPointMake(69.0, 61.989999999999995)];
        [path addCurveToPoint:CGPointMake(64.24000000000001, 57.47) controlPoint1:CGPointMake(69.0, 61.989999999999995) controlPoint2:CGPointMake(68.97999999999999, 59.629999999999995)];
        [path closePath];
        [path setMiterLimit:4];
        [path setUsesEvenOddFillRule:YES];
        [foreground setFill];
        [path fill];
        UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        return image;
    }];
}
#pragma clang diagnostic pop
@end
