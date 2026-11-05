// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBUpload.h"
#import "ITBImageLoader.h"
#import <AVFoundation/AVFoundation.h>
#import <ImageIO/ImageIO.h>
#import <math.h>
#import "ITBImageFileLoader.h"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
@implementation ITBUpload
@synthesize cacheKey = _cacheKey, uploadId = _uploadId, uploadProgress = _uploadProgress, uploadState = _uploadState;
@synthesize publicUrl = _publicUrl, progressDelegate = _progressDelegate, cancelled = _cancelled;
@synthesize cancellationDelegate = _cancellationDelegate, uploadDataTask = _uploadDataTask, isAnnotated = _isAnnotated;
@synthesize localId = _localId, filename = _filename, dimensions = _dimensions, uploadData = _uploadData;
@synthesize previewImage = _previewImage, fileURL = _fileURL, duration = _duration, exifMetadata = _exifMetadata, fileType = _fileType;
- (instancetype)init {
    self = [super init];
    if (self) { _localId = [NSUUID UUID].UUIDString; _isAnnotated = NO; _duration = 0; }
    return self;
}
- (NSString *)mimeType { return self.fileType.preferredMIMEType; }
+ (instancetype)createUploadForFileURL:(NSURL *)url {
    if (![self isValidFile:url]) return nil;
    ITBUpload *upload = [ITBUpload new];
    UTType *type = nil;
    [url getResourceValue:&type forKey:NSURLContentTypeKey error:nil];
    if (!type) type = [UTType typeWithFilenameExtension:url.pathExtension];
    upload.filename = url.lastPathComponent;
    upload.fileURL = url;
    upload.fileType = type;
    if ([type conformsToType:UTTypeImage]) upload.exifMetadata = [self extractEXIFMetadata:url];
    if ([type conformsToType:UTTypeImage]) upload = [self createImageUpload:upload];
    else if ([type conformsToType:UTTypeMovie]) upload = [self createVideoUpload:upload];
    else upload.uploadData = [NSData dataWithContentsOfURL:url];
    return upload;
}
+ (BOOL)isValidFile:(NSURL *)url {
    NSFileManager *manager = [NSFileManager defaultManager];
    if (![manager fileExistsAtPath:url.path]) return NO;
    NSError *error = nil;
    NSDictionary *attributes = [manager attributesOfItemAtPath:url.path error:&error];
    if (attributes && attributes.fileSize == 0) return NO;
    return error == nil;
}
+ (instancetype)createImageUpload:(ITBUpload *)upload {
    ITBImageFileLoader *loader = [[ITBImageFileLoader alloc] initWithImageURL:upload.fileURL];
    upload.uploadData = [loader loadImage];
    upload.dimensions = loader.imageSize;
    upload.cacheKey = [NSUUID UUID].UUIDString;
    return upload;
}
+ (instancetype)createVideoUpload:(ITBUpload *)upload {
    NSData *data = [NSData dataWithContentsOfURL:upload.fileURL];
    upload.uploadData = data;
    AVURLAsset *asset = [AVURLAsset URLAssetWithURL:upload.fileURL options:nil];
    CMTime time = asset ? asset.duration : (CMTime){0};
    upload.duration = CMTimeGetSeconds(time);
    upload.dimensions = [self getDimensionForVideoAsset:asset];
    return upload;
}
+ (CGSize)getDimensionForVideoAsset:(AVAsset *)asset {
    AVAssetTrack *track = [[asset tracksWithMediaType:AVMediaTypeVideo] firstObject];
    if (!track) return CGSizeZero;
    CGSize size = track.naturalSize;
    CGAffineTransform transform = track.preferredTransform;
    if (transform.a == 0 && transform.d == 0 &&
        (transform.b == 1 || transform.b == -1) && (transform.c == 1 || transform.c == -1)) {
        size = CGSizeMake(size.height, size.width);
    }
    return size;
}
- (void)setCacheKey:(NSString *)key {
    NSString *previous = _cacheKey;
    _cacheKey = key;
    if (!self.previewImage) {
        id cached = [[ITBImageLoader defaultLoader] cachedImageForKey:_cacheKey];
        if (cached) self.previewImage = cached;
        else {
            id image = _previewImage;
            if (!image) image = [[ITBImageLoader defaultLoader] cachedImageForKey:previous];
            if (image) self.previewImage = image;
            else if (self.fileURL) self.previewImage = [UIImage imageWithContentsOfFile:self.fileURL.path];
        }
    }
    [[ITBImageLoader defaultLoader] cacheImage:self.previewImage ForKey:_cacheKey];
}
- (void)setCancelled:(BOOL)cancelled {
    _cancelled = cancelled;
    if (cancelled) {
        [self.uploadDataTask cancel];
        [self.cancellationDelegate uploadCancelled:self];
    }
}
- (void)setUploadProgress:(double)progress {
    _uploadProgress = fmax(fmin(progress, 1), 0);
    [self.progressDelegate uploadProgressUpdated:_uploadProgress];
}
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (void)setUploadState:(NSUInteger)state {
    // Original 0x91aa4 does not store _uploadState; zero falls through to failure.
    if (state == 1) { self.uploadProgress = 0; return; }
    if (state == 0) self.uploadProgress = 1;
    if (state == 0 || state == 2) [self.progressDelegate uploadFailed];
}
#pragma clang diagnostic pop
- (void)graduallyIncreaseProgressTowardsTarget:(double)target inSeconds:(double)seconds {
    int32_t steps = (int32_t)(seconds / 0.2);
    __block double start = self.uploadProgress;
    __block double increment = (target - start) / steps;
    __weak ITBUpload *weakSelf = self;
    double timeStep = 1;
    for (int32_t remaining = steps, index = 1; remaining >= 1; --remaining, index = (int32_t)((uint32_t)index + 1)) {
        int32_t step = index;
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)((timeStep * 0.2) * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            double next = fma(step, increment, start);
            if (next > weakSelf.uploadProgress) weakSelf.uploadProgress = next;
        });
        timeStep += 1;
    }
}
- (NSDictionary *)dictionaryRepresentation {
    NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
    if (self.filename) dictionary[@"original_filename"] = self.filename;
    if (self.mimeType) dictionary[@"content_type"] = self.mimeType;
    if (self.uploadData) dictionary[@"size_in_bytes"] = @(self.uploadData.length);
    if (self.dimensions.width > 0 && self.dimensions.height > 0) {
        dictionary[@"width"] = @(self.dimensions.width);
        dictionary[@"height"] = @(self.dimensions.height);
    }
    if (self.exifMetadata && self.exifMetadata.count) dictionary[@"image_exif"] = self.exifMetadata;
    return dictionary;
}
+ (NSDictionary *)extractEXIFMetadata:(NSURL *)url {
    CGImageSourceRef source = CGImageSourceCreateWithURL((__bridge CFURLRef)url, NULL);
    if (!source) return nil;
    CFDictionaryRef properties = CGImageSourceCopyPropertiesAtIndex(source, 0, NULL);
    CFRelease(source);
    if (!properties) return nil;
    NSDictionary *all = (__bridge NSDictionary *)properties;
    NSDictionary *exif = all[(__bridge NSString *)kCGImagePropertyExifDictionary];
    NSDictionary *tiff = all[(__bridge NSString *)kCGImagePropertyTIFFDictionary];
    NSMutableDictionary *metadata = [NSMutableDictionary dictionary];
    if (tiff[@"DateTime"]) metadata[@"DateTime"] = tiff[@"DateTime"];
    if (exif[@"DateTimeOriginal"]) metadata[@"DateTimeOriginal"] = exif[@"DateTimeOriginal"];
    if (exif[@"DateTimeDigitized"]) metadata[@"DateTimeDigitized"] = exif[@"DateTimeDigitized"];
    CFRelease(properties);
    return metadata.count ? metadata : nil;
}
- (BOOL)isImage { return [self.fileType conformsToType:UTTypeImage]; }
- (BOOL)isVideo { return [self.fileType conformsToType:UTTypeMovie]; }
@end
#pragma clang diagnostic pop
