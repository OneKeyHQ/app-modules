#import "UIImage+ICMImageUtils.h"
@implementation UIImage (ICMImageUtils)
- (UIImage *)icm_grayscale {
    CGFloat width = self.size.width;
    CGFloat height = self.size.height;
    CGColorSpaceRef space = CGColorSpaceCreateDeviceGray();
    size_t widthPixels = (size_t)width;
    size_t heightPixels = (size_t)height;
    CGContextRef grayContext = CGBitmapContextCreate(NULL, widthPixels, heightPixels, 8, 0, space, 0);
    CGContextDrawImage(grayContext, CGRectMake(0, 0, width, height), self.CGImage);
    CGImageRef grayImage = CGBitmapContextCreateImage(grayContext);
    CGColorSpaceRelease(space);
    CGContextRelease(grayContext);
    CGContextRef alphaContext = CGBitmapContextCreate(NULL, widthPixels, heightPixels, 8, 0, NULL, 7);
    CGContextDrawImage(alphaContext, CGRectMake(0, 0, width, height), self.CGImage);
    CGImageRef alphaImage = CGBitmapContextCreateImage(alphaContext);
    CGContextRelease(alphaContext);
    CGImageRef image = CGImageCreateWithMask(grayImage, alphaImage);
    CGFloat scale = self.scale;
    UIImageOrientation orientation = self.imageOrientation;
    UIImage *result = [UIImage imageWithCGImage:image scale:scale orientation:orientation];
    CGImageRelease(grayImage);
    CGImageRelease(alphaImage);
    CGImageRelease(image);
    return result;
}
- (UIImage *)icm_resizeImageToSize:(CGSize)size {
    UIGraphicsImageRenderer *renderer = [[UIGraphicsImageRenderer alloc] initWithSize:size];
    UIImage *image = [renderer imageWithActions:^(UIGraphicsImageRendererContext *context) {
        (void)context;
        [self drawInRect:CGRectMake(0, 0, size.width, size.height)];
    }];
    return [image imageWithRenderingMode:self.renderingMode];
}
@end
