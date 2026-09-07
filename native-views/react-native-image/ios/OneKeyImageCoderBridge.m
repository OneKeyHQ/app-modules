#import "OneKeyImageCoderBridge.h"

#import <SDWebImage/SDWebImage.h>
#import <SDWebImageSVGCoder/SDImageSVGCoder.h>
#import <SDWebImageWebPCoder/SDImageWebPCoder.h>

@implementation OneKeyImageCoderBridge

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
