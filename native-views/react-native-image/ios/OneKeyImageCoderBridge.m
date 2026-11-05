#import "OneKeyImageCoderBridge.h"

#import <SDWebImage/SDWebImage.h>
#import <SDWebImageSVGCoder/SDImageSVGCoder.h>
#import <SDWebImageWebPCoder/SDImageWebPCoder.h>
#import <ImageIO/ImageIO.h>

@interface OneKeySVGNode : NSObject
@property(nonatomic, copy) NSString *name;
@property(nonatomic, copy) NSDictionary<NSString *, NSString *> *attributes;
@property(nonatomic, strong) NSMutableArray *children;
@end
@implementation OneKeySVGNode
@end

// Only the static, full-viewport PNG subset is converted; HTML layout is never inferred.
@interface OneKeySVGNormalizer : NSObject <NSXMLParserDelegate>
@property(nonatomic, strong) OneKeySVGNode *root;
@property(nonatomic, strong) NSMutableArray<OneKeySVGNode *> *stack;
@property(nonatomic) NSUInteger nodeCount;
@property(nonatomic) NSUInteger foreignCount;
@property(nonatomic) NSUInteger imageBytes;
@property(nonatomic) uint64_t imagePixels;
@property(nonatomic) BOOL hasForeignObject;
@property(nonatomic) BOOL hasImageRendering;
@property(nonatomic) BOOL nearestNeighbor;
@property(nonatomic) NSUInteger pixelRuns;
@property(nonatomic) NSUInteger pathCharacters;
+ (nullable NSData *)normalize:(NSData *)data;
@end

@implementation OneKeySVGNormalizer

static NSString *OneKeySVGLocalName(NSString *name) {
  return [name componentsSeparatedByString:@":"].lastObject;
}

static NSString *OneKeySVGEscape(NSString *value) {
  return [[[value stringByReplacingOccurrencesOfString:@"&" withString:@"&amp;"]
    stringByReplacingOccurrencesOfString:@"<" withString:@"&lt;"]
    stringByReplacingOccurrencesOfString:@"\"" withString:@"&quot;"];
}

static NSString *OneKeySVGSerialize(OneKeySVGNode *node) {
  NSMutableString *xml = [NSMutableString stringWithFormat:@"<%@", node.name];
  for (NSString *key in [node.attributes.allKeys sortedArrayUsingSelector:@selector(compare:)]) {
    [xml appendFormat:@" %@=\"%@\"", key, OneKeySVGEscape(node.attributes[key])];
  }
  [xml appendString:@">"];
  for (id child in node.children) {
    [xml appendString:[child isKindOfClass:NSString.class] ? OneKeySVGEscape(child) : OneKeySVGSerialize(child)];
  }
  [xml appendFormat:@"</%@>", node.name];
  return xml;
}

+ (NSData *)normalize:(NSData *)data {
  NSString *xml = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
  if (!xml) xml = [[NSString alloc] initWithData:data encoding:NSUTF16StringEncoding];
  if (!xml) return nil;
  if (![xml containsString:@"foreignObject"]) return data;
  if (data.length > 8 * 1024 * 1024 ||
      [xml rangeOfString:@"<!DOCTYPE" options:NSCaseInsensitiveSearch].location != NSNotFound ||
      [xml rangeOfString:@"<!ENTITY" options:NSCaseInsensitiveSearch].location != NSNotFound) return nil;
  OneKeySVGNormalizer *normalizer = [OneKeySVGNormalizer new];
  normalizer.stack = [NSMutableArray new];
  NSXMLParser *parser = [[NSXMLParser alloc] initWithData:data];
  parser.shouldResolveExternalEntities = NO;
  parser.delegate = normalizer;
  if (![parser parse] || !normalizer.root ||
      ![OneKeySVGLocalName(normalizer.root.name) isEqualToString:@"svg"]) return nil;
  if (!normalizer.hasForeignObject) return data;
  if (![normalizer collectStyles:normalizer.root] || ![normalizer convert:normalizer.root]) return nil;
  return [OneKeySVGSerialize(normalizer.root) dataUsingEncoding:NSUTF8StringEncoding];
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName
  namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName
  attributes:(NSDictionary<NSString *, NSString *> *)attributes {
  if (++self.nodeCount > 10000 || self.stack.count >= 64) { [parser abortParsing]; return; }
  if ([OneKeySVGLocalName(elementName) isEqualToString:@"foreignObject"]) self.hasForeignObject = YES;
  OneKeySVGNode *node = [OneKeySVGNode new];
  node.name = elementName;
  node.attributes = attributes;
  node.children = [NSMutableArray new];
  if (self.stack.count) [self.stack.lastObject.children addObject:node];
  else self.root = node;
  [self.stack addObject:node];
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName
  namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName {
  [self.stack removeLastObject];
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string {
  if (self.stack.count) [self.stack.lastObject.children addObject:string];
}

- (void)parser:(NSXMLParser *)parser foundCDATA:(NSData *)CDATABlock {
  NSString *text = [[NSString alloc] initWithData:CDATABlock encoding:NSUTF8StringEncoding];
  if (!text) [parser abortParsing];
  else [self parser:parser foundCharacters:text];
}

- (void)parser:(NSXMLParser *)parser foundProcessingInstructionWithTarget:(NSString *)target data:(NSString *)data {
  [parser abortParsing];
}

- (BOOL)validStylesheet:(OneKeySVGNode *)node {
  NSMutableString *css = [NSMutableString new];
  for (id child in node.children) {
    if (![child isKindOfClass:NSString.class]) return NO;
    [css appendString:child];
  }
  NSRegularExpression *rule = [NSRegularExpression regularExpressionWithPattern:@"^\\s*img\\s*\\{([^{}]*)\\}\\s*$" options:0 error:nil];
  NSTextCheckingResult *match = [rule firstMatchInString:css options:0 range:NSMakeRange(0, css.length)];
  if (!match) return NO;
  for (NSString *declaration in [[css substringWithRange:[match rangeAtIndex:1]] componentsSeparatedByString:@";"]) {
    NSString *trimmed = [declaration stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceAndNewlineCharacterSet];
    if (!trimmed.length) continue;
    NSArray *parts = [trimmed componentsSeparatedByString:@":"];
    if (parts.count != 2) return NO;
    NSString *key = [parts[0] stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceAndNewlineCharacterSet];
    NSString *value = [parts[1] stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceAndNewlineCharacterSet];
    if (([key isEqualToString:@"width"] || [key isEqualToString:@"height"]) && [value isEqualToString:@"100%"]) continue;
    if ([key isEqualToString:@"image-rendering"]) {
      BOOL smooth = [@[@"auto", @"smooth", @"optimizeQuality"] containsObject:value];
      BOOL nearest = [@[@"optimizeSpeed", @"-moz-crisp-edges", @"-o-crisp-edges", @"-webkit-optimize-contrast", @"optimize-contrast", @"crisp-edges", @"pixelated"] containsObject:value];
      if (!smooth && !nearest) return NO;
      self.hasImageRendering = YES;
      self.nearestNeighbor = nearest;
      continue;
    }
    if ([key isEqualToString:@"-ms-interpolation-mode"] && [value isEqualToString:@"nearest-neighbor"]) {
      if (!self.hasImageRendering) self.nearestNeighbor = YES;
      continue;
    }
    return NO;
  }
  return YES;
}

- (BOOL)collectStyles:(OneKeySVGNode *)node {
  if ([OneKeySVGLocalName(node.name) isEqualToString:@"style"]) return [self validStylesheet:node];
  for (id child in node.children) {
    if ([child isKindOfClass:OneKeySVGNode.class] && ![self collectStyles:child]) return NO;
  }
  return YES;
}

// CoreSVG smooths embedded bitmaps regardless of image-rendering. Pixel runs
// retain the small raster's edges without replacing the surrounding SVG scene.
- (BOOL)convertPixels:(CGImageRef)image node:(OneKeySVGNode *)node {
  size_t width = CGImageGetWidth(image), height = CGImageGetHeight(image);
  NSMutableData *rgba = [NSMutableData dataWithLength:width * height * 4];
  CGColorSpaceRef colorSpace = CGColorSpaceCreateWithName(kCGColorSpaceSRGB);
  if (!colorSpace) return NO;
  if (!rgba) { CGColorSpaceRelease(colorSpace); return NO; }
  CGContextRef context = CGBitmapContextCreate(rgba.mutableBytes, width, height, 8, width * 4,
    colorSpace, kCGBitmapByteOrder32Big | kCGImageAlphaPremultipliedLast);
  CGColorSpaceRelease(colorSpace);
  if (!context) return NO;
  CGContextSetInterpolationQuality(context, kCGInterpolationNone);
  CGContextSetBlendMode(context, kCGBlendModeCopy);
  CGContextDrawImage(context, CGRectMake(0, 0, width, height), image);
  CGContextRelease(context);
  NSMutableDictionary<NSNumber *, NSMutableString *> *paths = [NSMutableDictionary new];
  const uint32_t *pixels = rgba.bytes;
  for (size_t y = 0; y < height; y++) {
    for (size_t x = 0; x < width;) {
      uint32_t color = pixels[y * width + x];
      size_t end = x + 1;
      while (end < width && pixels[y * width + end] == color) end++;
      const uint8_t *channels = (const uint8_t *)&color;
      if (channels[3]) {
        if (++self.pixelRuns > 16384) return NO;
        NSNumber *key = @(color);
        NSMutableString *path = paths[key];
        if (!path) {
          if (++self.nodeCount > 10000) return NO;
          paths[key] = path = [NSMutableString new];
        }
        NSString *run = [NSString stringWithFormat:@"M%zu %zuh%zuv1H%zuz", x, y, end - x, x];
        self.pathCharacters += run.length;
        if (self.pathCharacters > 512 * 1024) return NO;
        [path appendString:run];
      }
      x = end;
    }
  }
  NSMutableDictionary *attributes = [node.attributes mutableCopy];
  attributes[@"xmlns"] = @"http://www.w3.org/2000/svg";
  attributes[@"viewBox"] = [NSString stringWithFormat:@"0 0 %zu %zu", width, height];
  attributes[@"preserveAspectRatio"] = @"none";
  attributes[@"overflow"] = @"hidden";
  NSRange colon = [node.name rangeOfString:@":"];
  node.name = colon.location == NSNotFound ? @"svg" : [[node.name substringToIndex:colon.location + 1] stringByAppendingString:@"svg"];
  node.attributes = attributes;
  [node.children removeAllObjects];
  for (NSNumber *key in [paths.allKeys sortedArrayUsingSelector:@selector(compare:)]) {
    uint32_t color = key.unsignedIntValue;
    const uint8_t *channels = (const uint8_t *)&color;
    unsigned alpha = channels[3];
    OneKeySVGNode *path = [OneKeySVGNode new];
    path.name = @"path";
    path.attributes = @{@"d": paths[key],
      @"fill": [NSString stringWithFormat:@"rgb(%u,%u,%u)",
        MIN(255u, (channels[0] * 255u + alpha / 2) / alpha),
        MIN(255u, (channels[1] * 255u + alpha / 2) / alpha),
        MIN(255u, (channels[2] * 255u + alpha / 2) / alpha)],
      @"fill-opacity": [NSString stringWithFormat:@"%.8f", alpha / 255.0]};
    path.children = [NSMutableArray new];
    [node.children addObject:path];
  }
  return YES;
}

- (BOOL)convert:(OneKeySVGNode *)node {
  NSMutableDictionary *colors = [node.attributes mutableCopy];
  for (NSString *key in @[@"fill", @"stroke", @"stop-color", @"flood-color", @"lighting-color", @"color"]) {
    NSString *value = colors[key];
    if (value.length != 9 || ![value hasPrefix:@"#"]) continue;
    NSScanner *scanner = [NSScanner scannerWithString:[value substringFromIndex:1]];
    unsigned long long rgba = 0;
    if ([scanner scanHexLongLong:&rgba] && scanner.isAtEnd) {
      colors[key] = [NSString stringWithFormat:@"rgba(%llu,%llu,%llu,%.8f)",
        (rgba >> 24) & 255, (rgba >> 16) & 255, (rgba >> 8) & 255, (rgba & 255) / 255.0];
    }
  }
  node.attributes = colors;
  NSString *localName = OneKeySVGLocalName(node.name);
  if ([localName isEqualToString:@"style"]) return YES;
  if (![localName isEqualToString:@"foreignObject"]) {
    for (id child in node.children) {
      if ([child isKindOfClass:OneKeySVGNode.class] && ![self convert:child]) return NO;
    }
    return YES;
  }
  if (++self.foreignCount > 16) return NO;
  NSSet *allowed = [NSSet setWithArray:@[@"x", @"y", @"width", @"height", @"transform", @"opacity", @"clip-path", @"mask", @"id"]];
  for (NSString *key in node.attributes) if (![allowed containsObject:key]) return NO;
  for (NSString *key in @[@"width", @"height"]) {
    NSString *value = node.attributes[key];
    NSScanner *scanner = [NSScanner scannerWithString:value ?: @""];
    double number = 0;
    if (![scanner scanDouble:&number] || !scanner.isAtEnd || !isfinite(number) || number <= 0 || number > 32768) return NO;
  }
  OneKeySVGNode *image = nil;
  for (id child in node.children) {
    if ([child isKindOfClass:NSString.class]) {
      if ([[child stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceAndNewlineCharacterSet] length]) return NO;
    } else if (image) return NO;
    else image = child;
  }
  if (!image || ![image.name isEqualToString:@"img"] ||
      ![image.attributes[@"xmlns"] isEqualToString:@"http://www.w3.org/1999/xhtml"]) return NO;
  NSSet *imageAllowed = [NSSet setWithArray:@[@"xmlns", @"src", @"width", @"height", @"alt"]];
  for (NSString *key in image.attributes) if (![imageAllowed containsObject:key]) return NO;
  for (NSString *key in @[@"width", @"height"]) {
    if (image.attributes[key] && ![image.attributes[key] isEqualToString:@"100%"]) return NO;
  }
  for (id child in image.children) {
    if (![child isKindOfClass:NSString.class] || [[child stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceAndNewlineCharacterSet] length]) return NO;
  }
  NSString *source = image.attributes[@"src"];
  NSString *prefix = @"data:image/png;base64,";
  if (![source.lowercaseString hasPrefix:prefix] || source.length - prefix.length > 11184812) return NO;
  NSData *png = [[NSData alloc] initWithBase64EncodedString:[source substringFromIndex:prefix.length] options:0];
  if (!png || self.imageBytes + png.length > 8 * 1024 * 1024) return NO;
  const unsigned char signature[] = {137, 80, 78, 71, 13, 10, 26, 10};
  if (png.length < sizeof(signature) || memcmp(png.bytes, signature, sizeof(signature))) return NO;
  CGImageSourceRef metadata = CGImageSourceCreateWithData((__bridge CFDataRef)png,
    (__bridge CFDictionaryRef)@{(__bridge NSString *)kCGImageSourceShouldCache: @NO});
  if (!metadata) return NO;
  NSDictionary *properties = CFBridgingRelease(CGImageSourceCopyPropertiesAtIndex(metadata, 0, NULL));
  BOOL isStatic = CGImageSourceGetCount(metadata) == 1;
  uint64_t width = [properties[(__bridge NSString *)kCGImagePropertyPixelWidth] unsignedLongLongValue];
  uint64_t height = [properties[(__bridge NSString *)kCGImagePropertyPixelHeight] unsignedLongLongValue];
  if (!isStatic || width == 0 || height == 0 || width > 8192 || height > 8192 ||
      self.imagePixels + width * height > 4194304) { CFRelease(metadata); return NO; }
  // A valid IHDR alone is not evidence that CoreSVG can paint the embedded raster.
  CGImageRef decoded = CGImageSourceCreateImageAtIndex(metadata, 0,
    (__bridge CFDictionaryRef)@{(__bridge NSString *)kCGImageSourceShouldCacheImmediately: @YES});
  CFRelease(metadata);
  if (!decoded) return NO;
  BOOL convertedPixels = !self.nearestNeighbor || [self convertPixels:decoded node:node];
  CGImageRelease(decoded);
  if (!convertedPixels) return NO;
  self.imageBytes += png.length;
  self.imagePixels += width * height;
  if (self.nearestNeighbor) return YES;
  NSMutableDictionary *attributes = [node.attributes mutableCopy];
  attributes[@"xmlns:xlink"] = @"http://www.w3.org/1999/xlink";
  attributes[@"xlink:href"] = [prefix stringByAppendingString:[source substringFromIndex:prefix.length]];
  attributes[@"preserveAspectRatio"] = @"none";
  attributes[@"image-rendering"] = @"optimizeQuality";
  NSRange colon = [node.name rangeOfString:@":"];
  node.name = colon.location == NSNotFound ? @"image" : [[node.name substringToIndex:colon.location + 1] stringByAppendingString:@"image"];
  node.attributes = attributes;
  [node.children removeAllObjects];
  return YES;
}
@end

@interface OneKeySVGCoder : SDImageSVGCoder
@end
@implementation OneKeySVGCoder
+ (SDImageSVGCoder *)sharedCoder {
  static OneKeySVGCoder *coder;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{ coder = [OneKeySVGCoder new]; });
  return coder;
}
- (UIImage *)decodedImageWithData:(NSData *)data options:(SDImageCoderOptions *)options {
  NSData *normalized = [OneKeySVGNormalizer normalize:data];
  if (!normalized) return nil;
  if (normalized != data && options[SDImageCoderDecodeThumbnailPixelSize]) {
    // SVGCoder rasterizes point sizes with the screen scale; callers supply pixels.
    CGSize pixels = [options[SDImageCoderDecodeThumbnailPixelSize] CGSizeValue];
    if (isfinite(pixels.width) && isfinite(pixels.height) && pixels.width > 0 && pixels.height > 0) {
      CGFloat ratio = MIN(1, sqrt(4194304.0 / pixels.width / pixels.height));
      CGFloat scale = MAX(1, UIScreen.mainScreen.scale);
      NSMutableDictionary *bounded = [options mutableCopy];
      // Keep point-to-pixel roundoff from making UIGraphics ceil an extra pixel.
      bounded[SDImageCoderDecodeThumbnailPixelSize] = [NSValue valueWithCGSize:
        CGSizeMake(nextafter(pixels.width * ratio / scale, 0), nextafter(pixels.height * ratio / scale, 0))];
      options = bounded;
    }
  }
  return [super decodedImageWithData:normalized options:options];
}
@end

@implementation OneKeyImageCoderBridge

+ (UIImage *)decodeSVGData:(NSData *)data pixelSize:(CGSize)pixelSize {
  NSDictionary *options = nil;
  if (isfinite(pixelSize.width) && isfinite(pixelSize.height) && pixelSize.width > 0 && pixelSize.height > 0) {
    options = @{SDImageCoderDecodeThumbnailPixelSize: [NSValue valueWithCGSize:pixelSize],
                SDImageCoderDecodePreserveAspectRatio: @YES};
  }
  return [OneKeySVGCoder.sharedCoder decodedImageWithData:data options:options];
}

+ (void)addCodersToManager:(id)manager {
  SDImageCodersManager *coderManager = (SDImageCodersManager *)manager;
  [coderManager addCoder:OneKeySVGCoder.sharedCoder];
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
