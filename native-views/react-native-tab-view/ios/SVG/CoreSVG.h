#import <UIKit/UIKit.h>
typedef UIImage PlatformImage;

@interface CoreSVGWrapper : NSObject

+ (instancetype)shared;

- (PlatformImage *)imageFromSVGData:(NSData *)data;

+ (BOOL)isSVGData:(NSData *)data;
+ (BOOL)supportsVectorSVGImage;

@end
