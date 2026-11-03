#import "JCOREUaidTokenGetRequest.h"
#import "RecoveredTransport.h"
@implementation JCOREUaidTokenGetRequest
- (instancetype)initWithUrl:(id)url method:(id)method headers:(id)headers requestBody:(id)body {
    self = [super init];
    if (self) {
        _tokenGetUrl = url;
        _method = method;
        _headers = headers;
        _requestBody = body;
    }
    return self;
}
- (void)sendRequest:(RecoveryOpaqueCallback)completion {
    JCOREURLSessionController *controller = [JCOREURLSessionController new];
    NSMutableURLRequest *request = [self request];
    [controller sendRequest:request
                  requestId:@""
          completionHandler:^(id task, id identifier, NSData *data, NSError *error) {
            id response = [task response];
            NSDictionary *headers = [response allHeaderFields];
            NSDictionary *result = @{@"respHeader" : headers ?: @{}, @"respBody" : data ?: [NSData new]};
            if (completion)
                completion(result, error);
            [controller invalidate];
          }];
}
- (NSMutableURLRequest *)request {
    if (!_tokenGetUrl || ![_tokenGetUrl isKindOfClass:[NSString class]] || !_tokenGetUrl.length || !_method ||
        ![_method isKindOfClass:[NSString class]] || !_method.length)
        return nil;
    if (![@[ @"GET", @"POST", @"PUT" ] containsObject:_method])
        return nil;
    NSMutableURLRequest *result =
        [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:_tokenGetUrl]];
    result.HTTPMethod = _method;
    if ([_headers isKindOfClass:[NSDictionary class]])
        for (id key in _headers.allKeys)
            [result setValue:_headers[key] forHTTPHeaderField:key];
    if (![_method isEqualToString:@"GET"] && [_requestBody isKindOfClass:[NSString class]]) {
        [_requestBody length];
        result.HTTPBody = [_requestBody dataUsingEncoding:NSUTF8StringEncoding];
    }
    result.timeoutInterval = 10;
    return result;
}
@end
