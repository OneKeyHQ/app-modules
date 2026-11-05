#import <UIKit/UIKit.h>
#import <ImageIO/ImageIO.h>
@interface ITBImageFileLoader : NSObject {
    NSURL *_imageURL;
    CGSize _imageSize;
}
@property (nonatomic, strong) NSURL *imageURL;
@property (nonatomic, readonly) CGSize imageSize;
- (instancetype)initWithImageURL:(NSURL *)url;
- (NSData *)loadImage;
- (NSData *)createDataFromImage:(CGImageRef)image;
- (CGImageSourceRef)getImageSource;
- (double)getRotationAngleFromExistingProperties:(CFDictionaryRef)properties;
- (double)getImageWidthFromExistingProperties:(CFDictionaryRef)properties;
- (double)getImageHeightFromExistingProperties:(CFDictionaryRef)properties;
- (CGImageRef)createImageWithSource:(CGImageSourceRef)source width:(double)width height:(double)height;
- (double)getImageProperty:(CFStringRef)key fromExistingProperties:(CFDictionaryRef)properties;
- (double)getAngleToRotateByForOrientation:(int)orientation;
- (CGImageRef)rotateImage:(CGImageRef)image ofAngle:(double)angle;
@end
