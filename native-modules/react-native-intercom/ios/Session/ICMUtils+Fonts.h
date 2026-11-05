#import "../Identity/ICMUtils.h"
#import <UIKit/UIKit.h>
@interface ICMUtils (OriginalBlockFonts)
+ (UIFont *)fontOfSize:(CGFloat)size weight:(NSUInteger)weight;
+ (UIFontWeight)convertToUIFontWeight:(NSUInteger)weight;
+ (NSString *)convertToFontName:(NSUInteger)weight;
@end
