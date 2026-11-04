#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

// Keep optional coder modules out of the Swift compilation unit so SDWebImage
// Objective-C types are imported under a single Swift module identity.
@interface OneKeyImageCoderBridge : NSObject

+ (void)addCodersToManager:(id)manager NS_SWIFT_NAME(addCoders(to:));
+ (void)ensureWebPCoderRegistered;
// A zero target preserves the decoder's intrinsic/vector result.
+ (nullable UIImage *)decodeSVGData:(NSData *)data pixelSize:(CGSize)pixelSize
  NS_SWIFT_NAME(decodeSVG(_:pixelSize:));

@end

NS_ASSUME_NONNULL_END
