#import <CoreGraphics/CoreGraphics.h>
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// QR-only ZXing-C++ reader. The header exposes CoreGraphics types only, so
/// ZXing headers stay out of this module's public interface.
@interface QrZXingReader : NSObject

/// Text of the first QR symbol in `image`, or nil when none is found.
- (nullable NSString *)decodeImage:(CGImageRef)image;

@end

NS_ASSUME_NONNULL_END
