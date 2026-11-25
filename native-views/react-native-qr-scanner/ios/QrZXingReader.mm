#import "QrZXingReader.h"

#if __has_include(<ZXingCpp/ZXIBarcodeReader.h>)
#import <ZXingCpp/ZXIBarcodeReader.h>
#elif __has_include(<zxing-cpp/ZXIBarcodeReader.h>)
#import <zxing-cpp/ZXIBarcodeReader.h>
#else
#import "ZXIBarcodeReader.h"
#endif

@implementation QrZXingReader {
  ZXIBarcodeReader *_reader;
  ZXIBarcodeReader *_multiReader;
}

- (instancetype)init {
  if (self = [super init]) {
    ZXIReaderOptions *options = [[ZXIReaderOptions alloc] init];
    options.formats = @[ @(QR_CODE) ];
    options.tryHarder = YES;
    options.tryRotate = YES;
    // tryInvert would re-run every attempt on an inverted copy. zxing-cpp
    // 2.2.0 still reads light-on-dark QR codes without it (see the XCTests),
    // and the pipeline has its own inverted pass.
    options.tryInvert = NO;
    options.tryDownscale = YES;
    options.maxNumberOfSymbols = 1;
    _reader = [[ZXIBarcodeReader alloc] initWithOptions:options];

    ZXIReaderOptions *multiOptions = [[ZXIReaderOptions alloc] init];
    multiOptions.formats = @[ @(QR_CODE) ];
    multiOptions.tryHarder = YES;
    multiOptions.tryRotate = YES;
    multiOptions.tryInvert = NO;
    multiOptions.tryDownscale = YES;
    multiOptions.maxNumberOfSymbols = 8;
    _multiReader = [[ZXIBarcodeReader alloc] initWithOptions:multiOptions];
  }
  return self;
}

- (nullable NSString *)decodeImage:(CGImageRef)image {
  NSError *error = nil;
  NSArray<ZXIResult *> *results = [_reader readCGImage:image error:&error];
  if (error != nil) {
    return nil;
  }
  for (ZXIResult *result in results) {
    if (result.text.length > 0) {
      return result.text;
    }
  }
  return nil;
}

- (NSArray<NSDictionary<NSString *, id> *> *)decodeImageSymbols:(CGImageRef)image {
  NSError *error = nil;
  NSArray<ZXIResult *> *results = [_multiReader readCGImage:image error:&error];
  if (error != nil || results.count == 0) {
    return @[];
  }
  double width = (double)CGImageGetWidth(image);
  double height = (double)CGImageGetHeight(image);
  NSMutableArray<NSDictionary<NSString *, id> *> *out = [NSMutableArray arrayWithCapacity:results.count];
  for (ZXIResult *result in results) {
    if (result.text.length == 0) continue;
    double centerX = 0.5;
    double centerY = 0.5;
    ZXIPosition *pos = result.position;
    if (pos != nil && width > 0 && height > 0) {
      double xs[4] = {
        (double)pos.topLeft.x,
        (double)pos.topRight.x,
        (double)pos.bottomRight.x,
        (double)pos.bottomLeft.x,
      };
      double ys[4] = {
        (double)pos.topLeft.y,
        (double)pos.topRight.y,
        (double)pos.bottomRight.y,
        (double)pos.bottomLeft.y,
      };
      double minX = xs[0], maxX = xs[0], minY = ys[0], maxY = ys[0];
      double sumX = 0, sumY = 0;
      for (int i = 0; i < 4; i++) {
        if (xs[i] < minX) minX = xs[i];
        if (xs[i] > maxX) maxX = xs[i];
        if (ys[i] < minY) minY = ys[i];
        if (ys[i] > maxY) maxY = ys[i];
        sumX += xs[i];
        sumY += ys[i];
      }
      centerX = sumX / (4.0 * width);
      centerY = sumY / (4.0 * height);
      [out addObject:@{
        @"text": result.text,
        @"centerX": @(centerX),
        @"centerY": @(centerY),
        @"x": @(minX / width),
        @"y": @(minY / height),
        @"width": @(MAX(0.0, (maxX - minX) / width)),
        @"height": @(MAX(0.0, (maxY - minY) / height)),
      }];
    } else {
      [out addObject:@{
        @"text": result.text,
        @"centerX": @(centerX),
        @"centerY": @(centerY),
      }];
    }
  }
  return out;
}

@end
