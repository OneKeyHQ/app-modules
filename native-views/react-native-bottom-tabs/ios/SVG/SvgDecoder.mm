#import "SvgDecoder.h"
#import <OneKeyImage/OneKeyImageCoderBridge.h>

@implementation SvgDecoder

RCT_EXPORT_MODULE()

- (BOOL)canDecodeImageData:(NSData *)imageData
{
  if (!imageData) return NO;
  // Preserve the existing global React image-decoder selection boundary.
  return [imageData rangeOfData:[@"</svg>" dataUsingEncoding:NSUTF8StringEncoding]
                       options:NSDataSearchBackwards
                         range:NSMakeRange(imageData.length - MIN(100, imageData.length), MIN(100, imageData.length))].location != NSNotFound;
}

- (RCTImageLoaderCancellationBlock)decodeImageData:(NSData *)imageData
                                              size:(CGSize)size
                                             scale:(CGFloat)scale
                                        resizeMode:(RCTResizeMode)resizeMode
                                 completionHandler:(RCTImageLoaderCompletionBlock)completionHandler
{
  UIImage *image = [OneKeyImageCoderBridge decodeSVGData:imageData pixelSize:CGSizeZero];

  if (image) {
    completionHandler(nil, image);
  } else {
    NSError *error = [NSError errorWithDomain:@"SVGDecoderErrorDomain"
                                         code:2
                                     userInfo:@{NSLocalizedDescriptionKey: @"Failed to render SVG to image"}];
    completionHandler(error, nil);
  }
  return ^{};
}

@end
