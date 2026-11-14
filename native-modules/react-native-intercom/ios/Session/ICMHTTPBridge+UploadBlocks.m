#import "ICMHTTPBridge+UploadBlocks.h"
#import "ICMHTTPClient+ConversationUploads.h"
#import <math.h>
@implementation ICMHTTPBridge (OriginalUploadBlocks)
+ (void)createUploadWithUpload:(ITBUpload *)upload success:(void (^)(NSString *))success failure:(void (^)(NSError *))failure {
    if (!upload) { if (success) success(nil); return; }
    [upload graduallyIncreaseProgressTowardsTarget:0.1 inSeconds:1];
    [ICMHTTPClient createUploadWithUpload:upload success:^(NSString *url) {
        if (upload.cancelled) {
            if (failure) failure([NSError errorWithDomain:@"Upload cancelled" code:-999 userInfo:nil]);
        } else {
            [upload graduallyIncreaseProgressTowardsTarget:0.99 inSeconds:1];
            if (success) success(url);
        }
    } progress:^(double progress) {
        upload.uploadProgress = fma(progress, 0.8, 0.1);
    } error:^(NSError *error) {
        if (failure) failure(error);
    }];
}
+ (NSArray *)makeSendableBlocks:(NSArray *)blocks withPublicURL:(NSString *)url upload:(ITBUpload *)upload {
    BOOL image = [upload isImage];
    if (url && image) {
        return @[@{@"type": @"image", @"url": url,
                   @"width": [NSNumber numberWithDouble:upload.dimensions.width],
                   @"height": [NSNumber numberWithDouble:upload.dimensions.height]}];
    }
    BOOL video = [upload isVideo];
    if (url && video) return [self attachmentListBlockForUpload:upload withPublicURL:url];
    return blocks;
}
+ (NSArray *)attachmentListBlockForUpload:(ITBUpload *)upload withPublicURL:(NSString *)url {
    NSDictionary *attachment = @{@"id": upload.uploadId.stringValue,
                                 @"contentType": upload.mimeType,
                                 @"url": url,
                                 @"size": [NSNumber numberWithUnsignedInteger:upload.uploadData.length],
                                 @"name": upload.filename};
    return @[@{@"type": @"attachmentList", @"attachments": @[attachment]}];
}
+ (NSArray *)videoFileBlockForUpload:(ITBUpload *)upload {
    return @[@{@"type": @"videoFile", @"text": upload.filename,
               @"url": upload.fileURL.absoluteString,
               @"duration": [NSNumber numberWithDouble:upload.duration],
               @"width": [NSNumber numberWithDouble:upload.dimensions.width],
               @"height": [NSNumber numberWithDouble:upload.dimensions.height]}];
}
@end
