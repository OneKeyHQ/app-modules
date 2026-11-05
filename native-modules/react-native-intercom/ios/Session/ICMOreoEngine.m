// Source recovery modifications: see docs/IOS_PROVENANCE.md.
// SOURCE_RESTORED_UNLINKED: full identity/Bridge/session closure is still incomplete.
#import "ICMOreoEngine.h"
#import "ICMMultipartTaskBuilder.h"
#import "ICMOreoShutdownState.h"
#import <math.h>
#import <stdlib.h>

@implementation ICMOreoEngine
@synthesize shutdownState = _shutdownState, baseURL = _baseURL, session = _session, appFingerprint = _appFingerprint, oreoOperationQueue = _oreoOperationQueue, httpHeaderFields = _httpHeaderFields, userJwtRefresher = _userJwtRefresher, ignoredRequestsGeneration = _ignoredRequestsGeneration;
- (ICMOreoShutdownState *)shutdownState {
    @synchronized (self) {
        if (!_shutdownState) _shutdownState = [ICMOreoShutdownState loadFromDisk];
        return _shutdownState;
    }
}
- (void)setShutdownState:(ICMOreoShutdownState *)state {
    @synchronized (self) { _shutdownState = state; }
}
+ (NSError *)ignoredResponseError {
    return [NSError errorWithDomain:@"Network request succeeded but the response was ignored" code:0 userInfo:nil];
}
+ (NSString *)userJwtInParameters:(id)parameters {
    if (![parameters isKindOfClass:[NSDictionary class]]) return nil;
    id value = parameters[@"intercom_user_jwt"];
    return [value isKindOfClass:[NSString class]] && [value length] ? value : nil;
}
+ (NSDictionary *)parameters:(NSDictionary *)parameters replacingUserJwt:(NSString *)jwt {
    NSMutableDictionary *result = [parameters mutableCopy];
    result[@"intercom_user_jwt"] = jwt;
    return result;
}
+ (BOOL)shouldRetryRequestURL:(NSString *)url {
    return [url isEqualToString:@"/messenger/mobile/users"] || [url isEqualToString:@"/messenger/mobile/events"] || [url isEqualToString:@"/messenger/mobile/device_tokens"];
}
+ (NSTimeInterval)backOffTimeForReplyNumber:(NSInteger)number { return exp2((double)number); }
- (void)setAuthorisationHeaderForAppID:(NSString *)appId apiKey:(NSString *)apiKey {
    NSData *data = [[NSString stringWithFormat:@"%@:%@", appId, apiKey] dataUsingEncoding:NSUTF8StringEncoding];
    NSString *encoded = [data base64EncodedStringWithOptions:0];
    [self.httpHeaderFields setValue:[NSString stringWithFormat:@"Basic %@", encoded] forKey:@"Authorization"];
}
- (NSMutableURLRequest *)createRequest:(NSString *)method urlString:(NSString *)url parameters:(NSDictionary *)parameters failure:(void (^)(NSURLResponse *, NSError *))failure {
    NSURL *resolved = [NSURL URLWithString:url relativeToURL:self.baseURL];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:resolved];
    request.HTTPMethod = method;
    request.allHTTPHeaderFields = self.httpHeaderFields;
    [request addValue:[self random16CharacterHexString] forHTTPHeaderField:@"Idempotency-Key"];
    if (parameters) {
        NSError *error = nil;
        request.HTTPBody = [NSJSONSerialization dataWithJSONObject:parameters options:0 error:&error];
        if (error) {
            if (failure) failure(nil, error);
            return nil;
        }
    }
    return request;
}
- (NSString *)random16CharacterHexString {
    return [[self random8CharacterHexString] stringByAppendingString:[self random8CharacterHexString]];
}
- (NSString *)random8CharacterHexString { return [NSString stringWithFormat:@"%02x", arc4random_uniform(UINT32_MAX)]; }
- (NSMutableURLRequest *)multipartRequest:(NSString *)url parameters:(NSDictionary *)parameters fileData:(NSData *)data fileName:(NSString *)fileName mimeType:(NSString *)mimeType fieldName:(NSString *)fieldName {
    NSURL *resolved = [NSURL URLWithString:url relativeToURL:self.baseURL];
    NSMutableDictionary *strings = [NSMutableDictionary dictionary];
    for (id key in parameters) strings[key] = [parameters[key] description];
    NSURLRequest *request = [ICMMultipartTaskBuilder createMultipartRequestWithUrl:resolved parameters:strings fileData:data fileName:fileName mimeType:mimeType fieldName:fieldName existingHeaders:self.httpHeaderFields];
    NSMutableURLRequest *mutable = [request mutableCopy];
    [mutable addValue:[self random16CharacterHexString] forHTTPHeaderField:@"Idempotency-Key"];
    return mutable;
}
@end
