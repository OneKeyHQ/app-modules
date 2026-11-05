#import <UIKit/UIKit.h>
@interface ITBImageUtils : NSObject
+ (UIImage *)imageFromColor:(UIColor *)color;
+ (CGSize)imageDisplaySizeFromSize:(CGSize)size withinWidth:(CGFloat)width;
+ (UIImage *)grayscaleImage:(UIImage *)image;
+ (BOOL)isGifImageData:(NSData *)data;
@end
