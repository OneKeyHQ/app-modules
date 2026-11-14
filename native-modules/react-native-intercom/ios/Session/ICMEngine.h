#import <Foundation/Foundation.h>
#import "ICMFileUploadConfig.h"
@interface ICMEngine : NSObject
- (void)requestWithoutRedirects:(NSURLRequest *)request success:(void (^)(void))success failure:(void (^)(NSError *))failure;
- (NSURLSessionUploadTask *)uploadFileWithFileName:(NSString *)name data:(NSData *)data uploadConfig:(id<ICMFileUploadConfig>)config success:(void (^)(void))success progress:(void (^)(double))progress failure:(void (^)(NSError *))failure;
- (NSURLSessionDownloadTask *)downloadFileFrom:(NSURL *)url downloadConfig:(id)config success:(void (^)(NSURL *, NSURLResponse *))success failure:(void (^)(NSError *))failure;
- (void)requestURL:(NSString *)string success:(void (^)(NSURLResponse *))success failure:(void (^)(NSError *))failure;
@end
@interface ICMEngineSessionDelegate : NSObject <NSURLSessionDataDelegate, NSURLSessionDownloadDelegate> {
    BOOL _ignoreRedirects;
    void (^_emptySuccessBlock)(void);
    void (^_downloadContentSuccessBlock)(NSURL *, NSURLResponse *);
    void (^_failureBlock)(NSError *);
    void (^_progressBlock)(double);
}
@property (nonatomic, copy) void (^emptySuccessBlock)(void);
@property (nonatomic, copy) void (^downloadContentSuccessBlock)(NSURL *, NSURLResponse *);
@property (nonatomic, copy) void (^failureBlock)(NSError *);
@property (nonatomic, copy) void (^progressBlock)(double);
@property (nonatomic) BOOL ignoreRedirects;
@end
