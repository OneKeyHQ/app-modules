#import "UIColor+HSL.h"
#import <math.h>
#import <stdint.h>
#import <string.h>
@implementation UIColor (HSL)
+ (UIColor *)colorWithHue:(CGFloat)hue saturation:(CGFloat)saturation lightness:(CGFloat)lightness alpha:(CGFloat)alpha {
    if (saturation == 0) return [UIColor colorWithRed:lightness green:lightness blue:lightness alpha:alpha];
    CGFloat q = lightness < 0.5 ? lightness * (saturation + 1) : fma(-lightness, saturation, saturation + lightness);
    CGFloat p = fma(lightness, 2, -q);
    CGFloat components[3] = {hue + 1.0 / 3.0, hue, hue + -1.0 / 3.0};
    CGFloat delta = q - p;
    CGFloat scaled = delta * 6;
    for (NSUInteger index = 0; index < 3; index++) {
        CGFloat t = components[index];
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t * 6 < 1) components[index] = fma(scaled, t, p);
        else if (t + t < 1) components[index] = q;
        else if (t * 3 < 2) components[index] = fma(delta * (2.0 / 3.0 - t), 6, p);
        else components[index] = p;
    }
    return [UIColor colorWithRed:components[0] green:components[1] blue:components[2] alpha:alpha];
}
- (BOOL)getHue:(CGFloat *)hue saturation:(CGFloat *)saturation lightness:(CGFloat *)lightness alpha:(CGFloat *)alpha {
    CGFloat red, green, blue;
    [self getRed:&red green:&green blue:&blue alpha:alpha];
    CGFloat maximum = red < green ? green : red;
    CGFloat minimum = red < green ? red : green;
    maximum = maximum < blue ? blue : maximum;
    minimum = minimum < blue ? minimum : blue;
    CGFloat sum = minimum + maximum;
    CGFloat l = sum * 0.5;
    CGFloat h, s;
    if (l <= 0) { h = 0; s = 0; }
    else {
        CGFloat delta = maximum - minimum;
        if (!(delta > 0)) { h = 0; s = delta; }
        else {
            CGFloat divisor = !(l <= 0.5) ? (2 - maximum) - minimum : sum;
            s = delta / divisor;
            CGFloat redDelta = (maximum - red) / delta;
            CGFloat greenDelta = (maximum - green) / delta;
            CGFloat blueDelta = (maximum - blue) / delta;
            if (red == maximum) h = green == minimum ? 5 + blueDelta : 1 - greenDelta;
            else if (green != maximum) h = red == minimum ? 3 + greenDelta : 5 - redDelta;
            else h = blue == minimum ? 1 + redDelta : 3 - blueDelta;
            h /= 6;
        }
    }
    if (hue) *hue = h;
    if (saturation) *saturation = s;
    if (lightness) *lightness = l;
    return YES;
}
- (CGFloat)lightness { CGFloat hue, saturation, lightness, alpha; [self getHue:&hue saturation:&saturation lightness:&lightness alpha:&alpha]; return lightness; }
- (UIColor *)offsetWithHue:(CGFloat)hue saturation:(CGFloat)saturation lightness:(CGFloat)lightness alpha:(CGFloat)alpha {
    CGFloat h, s, l, a;
    [self getHue:&h saturation:&s lightness:&l alpha:&a];
    float narrowed = (float)(h + hue);
    float remainder = narrowed - truncf(narrowed);
    uint32_t originalBits, remainderBits;
    memcpy(&originalBits, &narrowed, sizeof(originalBits));
    memcpy(&remainderBits, &remainder, sizeof(remainderBits));
    uint32_t bits = (originalBits & 0x80000000u) | (remainderBits & 0x7fffffffu);
    memcpy(&narrowed, &bits, sizeof(narrowed));
    s += saturation; l += lightness; a += alpha;
    if (s < 0) s = 0; else if (s > 1) s = 1;
    if (l < 0) l = 0; else if (l > 1) l = 1;
    if (a < 0) a = 0; else if (a > 1) a = 1;
    return [UIColor colorWithHue:(CGFloat)narrowed saturation:s lightness:l alpha:a];
}
- (UIColor *)saturate:(CGFloat)amount { return [self offsetWithHue:0 saturation:amount lightness:0 alpha:0]; }
- (UIColor *)desaturate:(CGFloat)amount { return [self offsetWithHue:0 saturation:-amount lightness:0 alpha:0]; }
- (UIColor *)lighten:(CGFloat)amount { return [self offsetWithHue:0 saturation:0 lightness:amount alpha:0]; }
- (UIColor *)darken:(CGFloat)amount { return [self offsetWithHue:0 saturation:0 lightness:-amount alpha:0]; }
- (UIColor *)spin:(CGFloat)degrees { return [self offsetWithHue:degrees / 360 saturation:0 lightness:0 alpha:0]; }
@end
