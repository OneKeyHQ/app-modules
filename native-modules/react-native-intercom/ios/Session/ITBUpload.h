#import <UIKit/UIKit.h>
#import <UniformTypeIdentifiers/UniformTypeIdentifiers.h>
@class ITBUpload, AVAsset;
@protocol ITBUploadProgressDelegate <NSObject>
- (void)uploadProgressUpdated:(CGFloat)progress;
- (void)uploadFailed;
@end
@protocol ITBUploadCancellationDelegate <NSObject>
- (void)uploadCancelled:(ITBUpload *)upload;
@end
@interface ITBUpload : NSObject {
    BOOL _cancelled;
    BOOL _isAnnotated;
    NSString *_cacheKey;
    NSNumber *_uploadId;
    double _uploadProgress;
    NSUInteger _uploadState;
    NSString *_publicUrl;
    __weak id<ITBUploadProgressDelegate> _progressDelegate;
    __weak id<ITBUploadCancellationDelegate> _cancellationDelegate;
    __weak NSURLSessionDataTask *_uploadDataTask;
    NSString *_localId;
    NSString *_filename;
    NSData *_uploadData;
    id _previewImage;
    NSURL *_fileURL;
    double _duration;
    NSDictionary *_exifMetadata;
    UTType *_fileType;
    CGSize _dimensions;
}
@property (nonatomic, strong) NSString *cacheKey;
@property (nonatomic, strong) NSNumber *uploadId;
@property (nonatomic) double uploadProgress;
@property (nonatomic) NSUInteger uploadState;
@property (nonatomic, strong) NSString *publicUrl;
@property (nonatomic, weak) id<ITBUploadProgressDelegate> progressDelegate;
@property (nonatomic) BOOL cancelled;
@property (nonatomic, weak) id<ITBUploadCancellationDelegate> cancellationDelegate;
@property (nonatomic, weak) NSURLSessionDataTask *uploadDataTask;
@property (nonatomic) BOOL isAnnotated;
@property (nonatomic, strong) NSString *localId;
@property (nonatomic, strong) NSString *filename;
@property (nonatomic) CGSize dimensions;
@property (nonatomic, strong) NSData *uploadData;
@property (nonatomic, strong) id previewImage;
@property (nonatomic, strong) NSURL *fileURL;
@property (nonatomic) double duration;
@property (nonatomic, strong) NSDictionary *exifMetadata;
@property (nonatomic, strong) UTType *fileType;
@property (nonatomic, readonly) NSString *mimeType;
+ (instancetype)createUploadForFileURL:(NSURL *)url;
+ (BOOL)isValidFile:(NSURL *)url;
+ (instancetype)createImageUpload:(ITBUpload *)upload;
+ (instancetype)createVideoUpload:(ITBUpload *)upload;
+ (CGSize)getDimensionForVideoAsset:(AVAsset *)asset;
+ (NSDictionary *)extractEXIFMetadata:(NSURL *)url;
- (void)graduallyIncreaseProgressTowardsTarget:(double)target inSeconds:(double)seconds;
- (NSDictionary *)dictionaryRepresentation;
- (BOOL)isImage;
- (BOOL)isVideo;
@end
