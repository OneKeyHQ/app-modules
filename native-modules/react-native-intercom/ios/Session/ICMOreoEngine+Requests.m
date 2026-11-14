// SOURCE_RESTORED_UNLINKED: complete identity/refresher/Bridge closure is missing.
#import "ICMOreoEngine.h"
#import "ICMOreoUserJwtReplay.h"

@implementation ICMOreoEngine (Requests)
+ (NSURLSessionDataTask *)placeholderTask {
    return [[NSURLSession sharedSession] dataTaskWithURL:[NSURL URLWithString:@"intercom-placeholder://"] completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        (void)data; (void)response; (void)error;
    }];
}
+ (BOOL)isCancelledPlaceholderTask:(NSURLSessionDataTask *)task {
    return task.state == NSURLSessionTaskStateCanceling || task.error.code == NSURLErrorCancelled;
}
- (NSURLSessionDataTask *)sendRequest:(NSURLRequest *)request parameters:(NSDictionary *)parameters buildRequest:(NSURLRequest *(^)(NSString *))builder success:(void (^)(NSURLSessionDataTask *, id))success failure:(void (^)(NSURLSessionDataTask *, NSError *))failure {
    NSString *jwt = [ICMOreoEngine userJwtInParameters:parameters];
    if (!jwt) return [self dataTaskWithRequest:request retries:0 success:success failure:failure];
    ICMRecoveredUserJwtRefresher *refresher = self.userJwtRefresher;
    NSArray *fingerprint = [refresher fingerprintForUserJwt:jwt];
    if (!fingerprint) return [self dataTaskWithRequest:request retries:0 success:success failure:failure];
    ICMOreoUserJwtReplay *replay = [[ICMOreoUserJwtReplay alloc] initWithUserJwt:jwt fingerprint:fingerprint buildRequest:builder];
    if (![refresher shouldRefreshBeforeSending:jwt]) return [self dataTaskWithRequest:request retries:0 replay:replay success:success failure:failure];
    NSURLSessionDataTask *placeholder = [ICMOreoEngine placeholderTask];
    NSUInteger generation = self.ignoredRequestsGeneration;
    __weak ICMOreoEngine *weakSelf = self;
    [refresher refreshWithStaleJwt:jwt fingerprint:fingerprint completion:^(NSString *refreshedJwt) {
        ICMOreoEngine *engine = weakSelf;
        if (!engine || engine.ignoredRequestsGeneration != generation) {
            if (failure) failure(nil, [ICMOreoEngine ignoredResponseError]);
            return;
        }
        if ([ICMOreoEngine isCancelledPlaceholderTask:placeholder]) {
            if (failure) failure(placeholder, [NSError errorWithDomain:NSURLErrorDomain code:NSURLErrorCancelled userInfo:nil]);
            return;
        }
        NSURLRequest *rebuilt = refreshedJwt ? builder(refreshedJwt) : nil;
        ICMOreoUserJwtReplay *context = replay;
        if (rebuilt) context = [[ICMOreoUserJwtReplay alloc] initWithUserJwt:refreshedJwt fingerprint:fingerprint buildRequest:builder];
        [engine dataTaskWithRequest:rebuilt ?: request retries:0 replay:context success:success failure:failure];
    }];
    return placeholder;
}
- (NSURLSessionDataTask *)POST:(NSString *)url parameters:(NSDictionary *)parameters success:(void (^)(NSURLSessionDataTask *, id))success failure:(void (^)(NSURLSessionDataTask *, NSError *))failure {
    NSURLRequest *request = [self createRequest:@"POST" urlString:url parameters:parameters failure:(void (^)(NSURLResponse *, NSError *))failure];
    __weak ICMOreoEngine *weakSelf = self;
    return [self sendRequest:request parameters:parameters buildRequest:^NSURLRequest *(NSString *jwt) {
        ICMOreoEngine *engine = weakSelf;
        NSDictionary *updated = [ICMOreoEngine parameters:parameters replacingUserJwt:jwt];
        return [engine createRequest:@"POST" urlString:url parameters:updated failure:nil];
    } success:success failure:failure];
}
- (NSURLSessionDataTask *)DELETE:(NSString *)url parameters:(NSDictionary *)parameters success:(void (^)(NSURLSessionDataTask *, id))success failure:(void (^)(NSURLSessionDataTask *, NSError *))failure {
    NSURLRequest *request = [self createRequest:@"DELETE" urlString:url parameters:parameters failure:(void (^)(NSURLResponse *, NSError *))failure];
    __weak ICMOreoEngine *weakSelf = self;
    return [self sendRequest:request parameters:parameters buildRequest:^NSURLRequest *(NSString *jwt) {
        ICMOreoEngine *engine = weakSelf;
        NSDictionary *updated = [ICMOreoEngine parameters:parameters replacingUserJwt:jwt];
        return [engine createRequest:@"DELETE" urlString:url parameters:updated failure:nil];
    } success:success failure:failure];
}
- (NSURLSessionDataTask *)POSTMultipart:(NSString *)url parameters:(NSDictionary *)parameters fileData:(NSData *)data fileName:(NSString *)fileName mimeType:(NSString *)mimeType fieldName:(NSString *)fieldName success:(void (^)(NSURLSessionDataTask *, id))success failure:(void (^)(NSURLSessionDataTask *, NSError *))failure {
    __weak ICMOreoEngine *weakSelf = self;
    NSURLRequest *(^buildParameters)(NSDictionary *) = ^NSURLRequest *(NSDictionary *values) {
        ICMOreoEngine *engine = weakSelf;
        return [engine multipartRequest:url parameters:values fileData:data fileName:fileName mimeType:mimeType fieldName:fieldName];
    };
    NSURLRequest *request = buildParameters(parameters);
    return [self sendRequest:request parameters:parameters buildRequest:^NSURLRequest *(NSString *jwt) {
        return buildParameters([ICMOreoEngine parameters:parameters replacingUserJwt:jwt]);
    } success:success failure:failure];
}
- (void)setWrapperVersion:(NSString *)version forWrapper:(NSInteger)wrapper {
    if (!version.length) return;
    NSString *name = nil;
    if ((NSUInteger)wrapper <= 3) name = @[@"intercom-sdk-cordova", @"intercom-sdk-react-native", @"intercom-sdk-flutter", @"intercom-sdk-unity"][(NSUInteger)wrapper];
    [self.httpHeaderFields setValue:[NSString stringWithFormat:@"%@/%@", name, version] forKey:@"X-INTERCOM-AGENT-WRAPPER"];
}
@end
