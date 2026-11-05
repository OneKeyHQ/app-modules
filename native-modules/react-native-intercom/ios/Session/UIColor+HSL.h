#import <UIKit/UIKit.h>
@interface UIColor (HSL)
+ (UIColor *)colorWithHue:(CGFloat)hue saturation:(CGFloat)saturation lightness:(CGFloat)lightness alpha:(CGFloat)alpha;
- (BOOL)getHue:(CGFloat *)hue saturation:(CGFloat *)saturation lightness:(CGFloat *)lightness alpha:(CGFloat *)alpha;
- (CGFloat)lightness;
- (UIColor *)offsetWithHue:(CGFloat)hue saturation:(CGFloat)saturation lightness:(CGFloat)lightness alpha:(CGFloat)alpha;
- (UIColor *)saturate:(CGFloat)amount;
- (UIColor *)desaturate:(CGFloat)amount;
- (UIColor *)lighten:(CGFloat)amount;
- (UIColor *)darken:(CGFloat)amount;
- (UIColor *)spin:(CGFloat)degrees;
@end
