#import "ITBImageFileLoader.h"
#import <UniformTypeIdentifiers/UniformTypeIdentifiers.h>
@implementation ITBImageFileLoader
@synthesize imageURL = _imageURL, imageSize = _imageSize;
- (instancetype)initWithImageURL:(NSURL *)url {
    self = [super init];
    if (self) _imageURL = url;
    return self;
}
- (NSData *)loadImage {
    CGImageSourceRef source = [self getImageSource];
    CFDictionaryRef properties = CGImageSourceCopyPropertiesAtIndex(source, 0, NULL);
    double angle = [self getRotationAngleFromExistingProperties:properties];
    double width = [self getImageWidthFromExistingProperties:properties];
    double height = [self getImageHeightFromExistingProperties:properties];
    _imageSize = angle == 90 || angle == -90 ? CGSizeMake(height, width) : CGSizeMake(width, height);
    CGImageRef image = [self createImageWithSource:source width:width height:height];
    if (angle != 0) image = [self rotateImage:image ofAngle:angle];
    NSData *data = [self createDataFromImage:image];
    CFRelease(source);
    CFRelease(properties);
    CFRelease(image);
    return data;
}
- (NSData *)createDataFromImage:(CGImageRef)image {
    CFMutableDataRef data = CFDataCreateMutable(NULL, 0);
    NSString *identifier = UTTypeJPEG.identifier;
    CGImageDestinationRef destination = CGImageDestinationCreateWithData(data, (__bridge CFStringRef)identifier, 1, NULL);
    CGImageDestinationAddImage(destination, image, NULL);
    if (!CGImageDestinationFinalize(destination)) NSLog(@"Failed to write Image");
    NSData *result = (__bridge NSData *)data;
    CFRelease(data);
    CFRelease(destination);
    return result;
}
- (CGImageSourceRef)getImageSource {
    const void *keys[] = { kCGImageSourceShouldCache };
    const void *values[] = { kCFBooleanFalse };
    CFDictionaryRef options = CFDictionaryCreate(NULL, keys, values, 1, NULL, NULL);
    CFURLRef url = CFBridgingRetain(self.imageURL);
    CGImageSourceRef source = CGImageSourceCreateWithURL(url, options);
    CFRelease(url);
    CFRelease(options);
    return source;
}
- (double)getRotationAngleFromExistingProperties:(CFDictionaryRef)properties {
    const void *value;
    BOOL present = CFDictionaryGetValueIfPresent(properties, kCGImagePropertyOrientation, &value);
    int orientation = 1;
    if (present) {
        CFNumberGetValue((CFNumberRef)value, kCFNumberIntType, &orientation);
        return [self getAngleToRotateByForOrientation:orientation];
    }
    return 0;
}
- (double)getImageWidthFromExistingProperties:(CFDictionaryRef)properties {
    double width = [self getImageProperty:kCGImagePropertyPixelWidth fromExistingProperties:properties];
    return width > 0 ? width : [UIScreen mainScreen].bounds.size.width;
}
- (double)getImageHeightFromExistingProperties:(CFDictionaryRef)properties {
    double height = [self getImageProperty:kCGImagePropertyPixelHeight fromExistingProperties:properties];
    // Original 0x90de0 also uses screen width for the height fallback.
    return height > 0 ? height : [UIScreen mainScreen].bounds.size.width;
}
- (CGImageRef)createImageWithSource:(CGImageSourceRef)source width:(double)width height:(double)height {
    const void *keys[] = { kCGImageSourceShouldCacheImmediately, kCGImageSourceCreateThumbnailFromImageAlways,
                          kCGImageSourceCreateThumbnailWithTransform, kCGImageSourceThumbnailMaxPixelSize };
    double maximum = width < height ? height : width;
    CFNumberRef number = CFNumberCreate(NULL, kCFNumberDoubleType, &maximum);
    const void *values[] = { kCFBooleanTrue, kCFBooleanTrue, kCFBooleanTrue, number };
    CFDictionaryRef options = CFDictionaryCreate(NULL, keys, values, 4, NULL, NULL);
    // Original 0x90ec0 calls CreateImageAtIndex despite the thumbnail options.
    CGImageRef image = CGImageSourceCreateImageAtIndex(source, 0, options);
    CFRelease(number);
    CFRelease(options);
    return image;
}
- (double)getImageProperty:(CFStringRef)key fromExistingProperties:(CFDictionaryRef)properties {
    const void *value;
    if (CFDictionaryGetValueIfPresent(properties, key, &value)) {
        int number;
        if (CFNumberGetValue((CFNumberRef)value, kCFNumberIntType, &number)) return number;
    }
    return 0;
}
- (double)getAngleToRotateByForOrientation:(int)orientation {
    static const double angles[] = { 180, 180, 90, -90, -90, 90 };
    uint32_t index = (uint32_t)orientation - 3;
    return index <= 5 ? angles[index] : 0;
}
- (CGImageRef)rotateImage:(CGImageRef)image ofAngle:(double)angle {
    double radians = angle * 0.017453292519943295;
    double width = CGImageGetWidth(image);
    double height = CGImageGetHeight(image);
    CGRect rotated = CGRectApplyAffineTransform(CGRectMake(0, 0, width, height), CGAffineTransformMakeRotation(radians));
    CGColorSpaceRef space = CGColorSpaceCreateDeviceRGB();
    CGContextRef context = CGBitmapContextCreate(NULL, (size_t)rotated.size.width, (size_t)rotated.size.height, CGImageGetBitsPerComponent(image), 0, space, (CGBitmapInfo)kCGImageAlphaPremultipliedFirst);
    CGContextSetAllowsAntialiasing(context, NO);
    CGContextSetInterpolationQuality(context, kCGInterpolationNone);
    CGColorSpaceRelease(space);
    CGContextTranslateCTM(context, rotated.size.width * 0.5, rotated.size.height * 0.5);
    CGContextRotateCTM(context, radians);
    @autoreleasepool { CGContextDrawImage(context, CGRectMake(width * -0.5, height * -0.5, width, height), image); }
    CGImageRef result = CGBitmapContextCreateImage(context);
    // Preserve the second original space release at 0x910b8.
    CFRelease(space);
    CFRelease(context);
    return result;
}
@end
