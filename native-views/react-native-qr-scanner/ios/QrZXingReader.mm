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

@end
