// PARTIAL_CLASS: Engine initialization, scheduling and retries remain unrestored.
#import "ICMOreoEngine.h"
#import "ICMOreoErrorResponse.h"

@implementation ICMOreoEngine (Transport)
- (NSURLSessionDataTask *)dataTaskWithRequest:(NSURLRequest *)request completionHandler:(void (^)(NSURLResponse *, id, NSError *))completion {
    if (![self.shutdownState canSendOreoNetworkRequestsWithFingerprint:self.appFingerprint]) {
        if (completion) completion(nil, nil, self.shutdownState.shutdownError);
        return nil;
    }
    __weak ICMOreoEngine *weakSelf = self;
    return [self.session dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *transportError) {
        ICMOreoEngine *engine = weakSelf;
        NSHTTPURLResponse *http = (NSHTTPURLResponse *)response;
        NSError *httpError = nil;
        if (http.statusCode < 200 || http.statusCode >= 300) {
            NSString *description = [NSString stringWithFormat:@"HTTP Error: %ld %@", (long)http.statusCode, [NSHTTPURLResponse localizedStringForStatusCode:http.statusCode]];
            httpError = [NSError errorWithDomain:@"io.intercom.ios.error.network" code:http.statusCode userInfo:@{NSLocalizedDescriptionKey:description}];
        }
        NSError *jsonError = nil;
        id object = nil;
        if (data.length) object = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
        NSError *error = transportError ?: jsonError ?: httpError;
        if (error) {
            ICMOreoErrorResponse *parsed = [ICMOreoErrorResponse instanceFromDictionary:object networkError:error];
            if (parsed.oreoShutdownState) {
                engine.shutdownState = parsed.oreoShutdownState;
                [engine.shutdownState saveToDisk];
            }
            if (completion) {
                NSError *reported = error;
                if (parsed.code.length) {
                    NSMutableDictionary *info = [reported.userInfo mutableCopy] ?: [NSMutableDictionary dictionary];
                    info[@"kIntercomErrorCode"] = parsed.code;
                    reported = [NSError errorWithDomain:reported.domain code:reported.code userInfo:info];
                }
                completion(response, nil, reported);
            }
        } else if (completion) {
            completion(response, object, nil);
        }
    }];
}
@end
