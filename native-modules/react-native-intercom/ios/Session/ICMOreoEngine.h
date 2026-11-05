// Recovered Engine interface; full product session dependencies remain unlinked.
#import <Foundation/Foundation.h>
#import "ICMUserJwtRefresher.h"
NS_ASSUME_NONNULL_BEGIN
@class ICMOreoShutdownState, ICMAppInstallationFingerprint, ICMRecoveredUserJwtRefresher, ICMURLSessionTaskOperation, ICMOreoUserJwtReplay;
@interface ICMOreoEngine : NSObject <NSURLSessionDelegate> {
@protected
    ICMOreoShutdownState *_shutdownState;
    NSOperationQueue *_oreoOperationQueue;
    NSURLSession *_session;
    NSURL *_baseURL;
    ICMAppInstallationFingerprint *_appFingerprint;
    NSMutableDictionary *_httpHeaderFields;
    ICMRecoveredUserJwtRefresher *_userJwtRefresher;
    NSUInteger _ignoredRequestsGeneration;
}
@property (nonatomic, readonly) NSURL *baseURL;
@property (nonatomic, readonly) NSURLSession *session;
@property (nonatomic, strong) ICMAppInstallationFingerprint *appFingerprint;
@property (nonatomic, strong) NSOperationQueue *oreoOperationQueue;
@property (atomic) NSUInteger ignoredRequestsGeneration;
@property (nonatomic, strong) ICMRecoveredUserJwtRefresher *userJwtRefresher;
@property (nonatomic, strong) ICMOreoShutdownState *shutdownState;
@property (nonatomic, strong) NSMutableDictionary<NSString *, NSString *> *httpHeaderFields;
+ (NSError *)ignoredResponseError;
+ (nullable NSString *)userJwtInParameters:(nullable id)parameters;
+ (nullable NSDictionary *)parameters:(nullable NSDictionary *)parameters replacingUserJwt:(nullable NSString *)jwt;
+ (BOOL)shouldRetryRequestURL:(nullable NSString *)url;
+ (NSTimeInterval)backOffTimeForReplyNumber:(NSInteger)number;
- (void)setAuthorisationHeaderForAppID:(nullable NSString *)appId apiKey:(nullable NSString *)apiKey;
- (nullable NSMutableURLRequest *)createRequest:(NSString *)method urlString:(NSString *)url parameters:(nullable NSDictionary *)parameters failure:(nullable void (^)(NSURLResponse * _Nullable, NSError *))failure;
- (NSString *)random16CharacterHexString;
- (NSString *)random8CharacterHexString;
- (NSMutableURLRequest *)multipartRequest:(NSString *)url parameters:(nullable NSDictionary *)parameters fileData:(NSData *)data fileName:(NSString *)fileName mimeType:(NSString *)mimeType fieldName:(NSString *)fieldName;
@end

@interface ICMOreoEngine (Requests)
+ (NSURLSessionDataTask *)placeholderTask;
+ (BOOL)isCancelledPlaceholderTask:(nullable NSURLSessionDataTask *)task;
- (nullable NSURLSessionDataTask *)sendRequest:(NSURLRequest *)request parameters:(nullable NSDictionary *)parameters buildRequest:(NSURLRequest * _Nullable (^)(NSString *jwt))builder success:(nullable void (^)(NSURLSessionDataTask *, id))success failure:(nullable void (^)(NSURLSessionDataTask *, NSError *))failure;
- (nullable NSURLSessionDataTask *)POST:(NSString *)url parameters:(nullable NSDictionary *)parameters success:(nullable void (^)(NSURLSessionDataTask *, id))success failure:(nullable void (^)(NSURLSessionDataTask *, NSError *))failure;
- (nullable NSURLSessionDataTask *)DELETE:(NSString *)url parameters:(nullable NSDictionary *)parameters success:(nullable void (^)(NSURLSessionDataTask *, id))success failure:(nullable void (^)(NSURLSessionDataTask *, NSError *))failure;
- (nullable NSURLSessionDataTask *)POSTMultipart:(NSString *)url parameters:(nullable NSDictionary *)parameters fileData:(NSData *)data fileName:(NSString *)fileName mimeType:(NSString *)mimeType fieldName:(NSString *)fieldName success:(nullable void (^)(NSURLSessionDataTask *, id))success failure:(nullable void (^)(NSURLSessionDataTask *, NSError *))failure;
- (void)setWrapperVersion:(NSString *)version forWrapper:(NSInteger)wrapper;
@end

@interface ICMOreoEngine (Operations)
- (void)ignoreAllPendingRequests;
- (ICMURLSessionTaskOperation *)operationWithRequest:(NSURLRequest *)request retries:(NSInteger)retries success:(nullable void (^)(NSURLSessionDataTask *, id))success failure:(nullable void (^)(NSURLSessionDataTask *, NSError *))failure;
- (ICMURLSessionTaskOperation *)operationWithRequest:(NSURLRequest *)request retries:(NSInteger)retries replay:(nullable ICMOreoUserJwtReplay *)replay success:(nullable void (^)(NSURLSessionDataTask *, id))success failure:(nullable void (^)(NSURLSessionDataTask *, NSError *))failure;
- (nullable NSURLSessionDataTask *)dataTaskWithRequest:(NSURLRequest *)request retries:(NSInteger)retries success:(nullable void (^)(NSURLSessionDataTask *, id))success failure:(nullable void (^)(NSURLSessionDataTask *, NSError *))failure;
- (nullable NSURLSessionDataTask *)dataTaskWithRequest:(NSURLRequest *)request retries:(NSInteger)retries replay:(nullable ICMOreoUserJwtReplay *)replay success:(nullable void (^)(NSURLSessionDataTask *, id))success failure:(nullable void (^)(NSURLSessionDataTask *, NSError *))failure;
- (void)retryDataTask:(NSURLSessionDataTask *)task numberOfRetries:(NSInteger)retries replay:(nullable ICMOreoUserJwtReplay *)replay success:(nullable void (^)(NSURLSessionDataTask *, id))success failure:(nullable void (^)(NSURLSessionDataTask *, NSError *))failure;
- (void)replayRequestTo:(NSString *)path replay:(ICMOreoUserJwtReplay *)replay success:(nullable void (^)(NSURLSessionDataTask *, id))success failure:(nullable void (^)(NSURLSessionDataTask *, NSError *))failure otherwise:(void (^)(void))otherwise ignored:(void (^)(void))ignored;
@end

// These original methods are implemented in the transport source unit.
@interface ICMOreoEngine (Transport)
- (nullable NSURLSessionDataTask *)dataTaskWithRequest:(NSURLRequest *)request completionHandler:(nullable void (^)(NSURLResponse * _Nullable, id _Nullable, NSError * _Nullable))completion;
@end

// Initialization source uses the real refresher; full session/Bridge closure is unlinked.
@interface ICMOreoEngine (Initialization)
- (instancetype)initWithApiKey:(nullable NSString *)apiKey appId:(nullable NSString *)appId;
- (instancetype)initWithBaseURL:(NSURL *)url;
- (void)configureHTTPHeaders;
@end
NS_ASSUME_NONNULL_END
