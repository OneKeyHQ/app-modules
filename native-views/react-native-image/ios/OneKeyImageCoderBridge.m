#import "OneKeyImageCoderBridge.h"

#import <SDWebImage/SDWebImage.h>
#import <SDWebImageSVGCoder/SDImageSVGCoder.h>
#import <SDWebImageWebPCoder/SDImageWebPCoder.h>

@implementation OneKeyImageCoderBridge

+ (UIImage *)decodeSVGData:(NSData *)data pixelSize:(CGSize)pixelSize {
  NSDictionary *options = nil;
  if (isfinite(pixelSize.width) && isfinite(pixelSize.height) && pixelSize.width > 0 && pixelSize.height > 0) {
    options = @{SDImageCoderDecodeThumbnailPixelSize: [NSValue valueWithCGSize:pixelSize],
                SDImageCoderDecodePreserveAspectRatio: @YES};
  }
  return [SDImageSVGCoder.sharedCoder decodedImageWithData:data options:options];
}

+ (void)addCodersToManager:(id)manager {
  SDImageCodersManager *coderManager = (SDImageCodersManager *)manager;
  [coderManager addCoder:SDImageSVGCoder.sharedCoder];
  [coderManager addCoder:SDImageWebPCoder.sharedCoder];
}

+ (void)ensureWebPCoderRegistered {
  SDImageCodersManager *manager = SDImageCodersManager.sharedManager;
  for (id<SDImageCoder> coder in manager.coders) {
    if (coder == SDImageWebPCoder.sharedCoder) {
      return;
    }
  }
  [manager addCoder:SDImageWebPCoder.sharedCoder];
}

@end
