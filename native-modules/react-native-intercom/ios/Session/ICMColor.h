// Original color helpers and dynamic theme accessors.
#import <UIKit/UIKit.h>
@interface ICMColor : NSObject
+ (UIColor *)colorWithHexString:(NSString *)string;
+ (UIColor *)colorWithR:(int)red g:(int)green b:(int)blue;
+ (UIColor *)colorWithR:(int)red g:(int)green b:(int)blue alpha:(CGFloat)alpha;
+ (NSString *)hexStringFromColor:(UIColor *)color;
+ (UIColor *)helpCenterSearchBarBackgroundColor;
+ (UIColor *)intercomLinkColor;
+ (UIColor *)composerSeparatorColor;
+ (UIColor *)closeButtonDarkColorWithAlpha:(CGFloat)alpha;
+ (UIColor *)closeButtonLightColor;
+ (UIColor *)closeButtonLightColorWithAlpha:(CGFloat)alpha;
+ (UIColor *)messageBadgeBackgroundColor;
+ (UIColor *)cardLoadingColor;
+ (UIColor *)black70;
+ (UIColor *)activeColour;
+ (UIColor *)awayColour;
+ (UIColor *)buttonTextColorVarient:(UIColor *)color;
+ (UIColor *)buttonBackgroundColorVarient:(UIColor *)color;
+ (UIColor *)buttonHighlightColor:(UIColor *)color;
+ (UIColor *)textColorForBackgroundColor:(UIColor *)color;
+ (UIColor *)borderColorForBackgroundColor:(UIColor *)color;
+ (BOOL)isLightColor:(UIColor *)color;
@end
