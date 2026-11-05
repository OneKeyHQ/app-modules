#import "ICMHTTPClient.h"
#import "ICMHTTPBridge.h"
@protocol ICMFileDownloadConfig <NSObject>
@property NSTimeInterval timeoutInterval;
@end
@interface ICMRichPushMediaDownloadConfig : NSObject <ICMFileDownloadConfig> {
    NSTimeInterval timeoutInterval;
}
@property NSTimeInterval timeoutInterval;
@end
@interface ICMHTTPClient (OriginalVersionAndRichPush)
+ (void)fetchLatestiOSSDKReleaseVersion:(void (^)(NSString *))completion;
+ (void)downloadRichPushMediaAttachmentFrom:(NSURL *)url success:(void (^)(NSURL *))success failure:(void (^)(NSError *))failure;
+ (void)handleRichPushMediaDownloadFrom:(NSURL *)url location:(NSURL *)location response:(NSURLResponse *)response success:(void (^)(NSURL *))success failure:(void (^)(NSError *))failure;
@end
@interface ICMHTTPBridge (OriginalRichPush)
+ (void)downloadRichPushMediaAttachmentFrom:(NSURL *)url success:(void (^)(NSURL *))success failure:(void (^)(NSError *))failure;
@end
