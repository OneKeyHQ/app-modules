#import "ICMHTTPClient+RichPush.h"
#import "ICMEngine.h"
#import "ICMUtils+RichPush.h"
@implementation ICMRichPushMediaDownloadConfig
@synthesize timeoutInterval = timeoutInterval;
// The original getter ignores the stored value; synthesis preserves its property metadata.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (NSTimeInterval)timeoutInterval { return 30.0; }
#pragma clang diagnostic pop
- (void)setTimeoutInterval:(NSTimeInterval)value { timeoutInterval = value; }
@end
@implementation ICMHTTPClient (OriginalVersionAndRichPush)
+ (void)fetchLatestiOSSDKReleaseVersion:(void (^)(NSString *))completion {
    [[ICMEngine new] requestURL:@"https://github.com/intercom/intercom-ios/releases/latest" success:^(NSURLResponse *response) {
        completion(response.URL.lastPathComponent);
    } failure:^(NSError *error) { (void)error; }];
}
+ (void)downloadRichPushMediaAttachmentFrom:(NSURL *)url success:(void (^)(NSURL *))success failure:(void (^)(NSError *))failure {
    [[ICMEngine new] downloadFileFrom:url downloadConfig:[ICMRichPushMediaDownloadConfig new] success:^(NSURL *location, NSURLResponse *response) {
        [self handleRichPushMediaDownloadFrom:url location:location response:response success:success failure:failure];
    } failure:^(NSError *error) {
        NSLog(@"[Intercom] ERROR - Could not download rich push notification media. Error: %@", error.localizedDescription);
        if (failure) failure(error);
    }];
}
+ (void)handleRichPushMediaDownloadFrom:(NSURL *)url location:(NSURL *)location response:(NSURLResponse *)response success:(void (^)(NSURL *))success failure:(void (^)(NSError *))failure {
    NSString *extension = [ICMUtils getFileExtensionFrom:url httpResponse:response];
    if (!extension) extension = @"jpeg";
    NSURL *destination = [NSURL fileURLWithPath:[location.path.stringByDeletingPathExtension stringByAppendingPathExtension:extension]];
    NSError *error = nil;
    BOOL moved = [[NSFileManager defaultManager] moveItemAtURL:location toURL:destination error:&error];
    if (moved && !error) {
        if (success) success(destination);
    } else {
        NSLog(@"[Intercom] ERROR - Could not move rich push notification media to a temporary location. Error: %@", error.localizedDescription);
        if (failure) failure(error);
    }
}
@end
@implementation ICMHTTPBridge (OriginalRichPush)
+ (void)downloadRichPushMediaAttachmentFrom:(NSURL *)url success:(void (^)(NSURL *))success failure:(void (^)(NSError *))failure {
    [ICMHTTPClient downloadRichPushMediaAttachmentFrom:url success:success failure:failure];
}
@end
