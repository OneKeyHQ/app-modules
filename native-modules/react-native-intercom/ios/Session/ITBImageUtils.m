#import "ITBImageUtils.h"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
@implementation ITBImageUtils
+ (UIImage *)imageFromColor:(UIColor *)color {
    UIGraphicsBeginImageContext(CGSizeMake(1, 1));
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, color.CGColor);
    CGContextFillRect(context, CGRectMake(0, 0, 1, 1));
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}
+ (CGSize)imageDisplaySizeFromSize:(CGSize)size withinWidth:(CGFloat)width {
    if (CGSizeEqualToSize(size, CGSizeZero)) return CGSizeZero;
    if (size.width > width) return CGRectIntegral(CGRectMake(0, 0, width, size.height / (size.width / width))).size;
    return size;
}
+ (UIImage *)grayscaleImage:(UIImage *)image {
    CGFloat width = image.size.width;
    CGFloat height = image.size.height;
    CGColorSpaceRef space = CGColorSpaceCreateDeviceGray();
    CGContextRef context = CGBitmapContextCreate(NULL, (size_t)width, (size_t)height, 8, 0, space, 0);
    CGContextDrawImage(context, CGRectMake(0, 0, width, height), image.CGImage);
    CGImageRef gray = CGBitmapContextCreateImage(context);
    CGColorSpaceRelease(space);
    CGContextRelease(context);
    CGContextRef alphaContext = CGBitmapContextCreate(NULL, (size_t)width, (size_t)height, 8, 0, NULL, (CGBitmapInfo)kCGImageAlphaOnly);
    CGContextDrawImage(alphaContext, CGRectMake(0, 0, width, height), image.CGImage);
    CGImageRef mask = CGBitmapContextCreateImage(alphaContext);
    CGContextRelease(alphaContext);
    CGImageRef masked = CGImageCreateWithMask(gray, mask);
    CGFloat scale = image.scale;
    UIImageOrientation orientation = image.imageOrientation;
    UIImage *result = [UIImage imageWithCGImage:masked scale:scale orientation:orientation];
    CGImageRelease(gray);
    CGImageRelease(mask);
    CGImageRelease(masked);
    return result;
}
+ (BOOL)isGifImageData:(NSData *)data {
    if (data.length < 3) return NO;
    NSData *prefix = [NSData dataWithBytes:"GIF" length:3];
    return [[data subdataWithRange:NSMakeRange(0, 3)] isEqualToData:prefix];
}
@end
#pragma clang diagnostic pop
