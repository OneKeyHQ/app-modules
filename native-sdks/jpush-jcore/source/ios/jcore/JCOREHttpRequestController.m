#import "JCOREHttpRequestController.h"
#import "JCORESupersetManager.h"
#import "JCORENetDataManager.h"
#import "JCORELog.h"
#define JCOREHttpControllerFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Controller/Network/JCOREHttpChannelController.m"
@implementation JCOREHttpRequestController
@synthesize addressList = _addressList, urlRequest = _urlRequest, requestId = _requestId;
@synthesize sessionController = _sessionController, completionHandler = _completionHandler;
- (void)send {
    NSString *message;
    NSUInteger line;
    if (!self.addressList || !self.addressList.count) { message = @"report address nil"; line = 67; }
    else if (![self.urlRequest isKindOfClass:NSMutableURLRequest.class]) { message = @"url request nil"; line = 71; }
    else if (!self.sessionController) { message = @"url session controller nil"; line = 75; }
    else { _currentIndex = 0; [self sendRequest]; return; }
    [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:JCOREHttpControllerFile function:__PRETTY_FUNCTION__ line:line tag:nil format:message];
}
- (void)sendRequest {
    if ((unsigned long long)_currentIndex >= self.addressList.count) return;
    id address = self.addressList[(NSUInteger)_currentIndex];
    if ([address isKindOfClass:NSString.class]) self.urlRequest.URL = [NSURL URLWithString:address];
    __weak NSThread *thread = NSThread.currentThread;
    NSArray *parts = [self.urlRequest.URL.absoluteString componentsSeparatedByString:@"?"];
    NSString *base = parts[0];
    NSString *method = self.urlRequest.HTTPMethod ?: @"GET";
    NSUInteger bytes;
    if ([method isEqualToString:@"POST"]) bytes = self.urlRequest.HTTPBody.length;
    else bytes = parts.count < 2 ? 0 : [parts[1] lengthOfBytesUsingEncoding:NSUTF8StringEncoding];
    NSString *monitor = [JCORESupersetManager startHttpMoniter:method url:base request_byte:(long long)bytes];
    [JCORENetDataManager startHttpMoniter:base request_byte:(long long)bytes];
    [self.sessionController sendRequest:self.urlRequest requestId:self.requestId completionHandler:^(id task, id identifier, NSData *data, NSError *error) {
        [JCORENetDataManager endHttpMoniter:base response_byte:(long long)data.length];
        NSMutableDictionary *information = [[NSMutableDictionary alloc] init];
        if (task) [information setObject:task forKey:@"task"];
        if (identifier) [information setObject:identifier forKey:@"identifier"];
        if (data) [information setObject:data forKey:@"result"];
        if (error) [information setObject:error forKey:@"error"];
        [information setObject:monitor forKey:@"moniterID"];
        if ([NSThread.currentThread isEqual:thread]) [self handleRespond:[information copy]];
        else [self performSelector:@selector(handleRespond:) onThread:thread withObject:[information copy] waitUntilDone:NO];
    }];
}
- (void)handleRespond:(NSDictionary *)information {
    if (information && ![information isKindOfClass:NSDictionary.class]) return;
    NSString *monitor = information[@"moniterID"];
    id task = information[@"task"];
    if (task && ![task respondsToSelector:@selector(response)]) {
        [JCORESupersetManager endHttpMoniter:monitor response_byte:0 error_code:-1 status_code:0];
        return;
    }
    NSData *data = information[@"result"];
    NSError *error = information[@"error"];
    long long status = [(NSHTTPURLResponse *)[task response] statusCode];
    if (status != 200 && [self hasNextRequest]) {
        [JCORESupersetManager endHttpMoniter:monitor response_byte:0 error_code:0 status_code:status];
        return;
    }
    if (!self.completionHandler) return;
    [JCORELog log:YES level:0x3ff flag:8 context:0 file:JCOREHttpControllerFile function:__PRETTY_FUNCTION__ line:164 tag:nil format:@"JCoreService endHttpMoniter !!"];
    [JCORESupersetManager endHttpMoniter:monitor response_byte:(long long)data.length error_code:status > 99 ? 0 : status status_code:status];
    if (status == 200) { self.completionHandler(data, nil); return; }
    NSString *description = [self getHttpNetworkErrorStringWith:status];
    if (error && status == 0) { status = error.code; description = error.localizedDescription; }
    NSError *reported = [NSError errorWithDomain:@"JIGUANGHttpErrorDomain" code:(NSInteger)status userInfo:@{NSLocalizedDescriptionKey: description ?: @""}];
    self.completionHandler(data, reported);
}
- (BOOL)hasNextRequest {
    unsigned long long next = (unsigned long long)_currentIndex + 1;
    _currentIndex = (long long)next;
    NSUInteger count = self.addressList.count;
    if (next < count) [self sendRequest];
    return next < count;
}
- (NSString *)getHttpNetworkErrorStringWith:(long long)code {
    switch (code) {
        case -1014: return @"请求资源0字节";
        case -1013: return @"需要用户验证信息";
        case -1012: return @"用户取消验证";
        case -1011: return @"错误的服务器响应";
        case -1010: return @"跳转到不存在的地址";
        case -1009: return @"设备没有连接到网络";
        case -1008: return @"请求资源不存在";
        case -1007: return @"连接地址跳转过多";
        case -1006: return @"DNS解析失败";
        case -1005: return @"网络连接丢失";
        case -1004: return @"无法连接到主机";
        case -1003: return @"找不到主机";
        case -1002: return @"不支持的URL";
        case -1001: return @"连接超时";
        case -1000: return @"错误的URL地址";
        case -999: return @"请求被取消";
        case -998: return @"未知错误";
        case 1: return @"找不到服务器主机";
        case 2: return @"主机未知错误";
        default: return [NSString stringWithFormat:@"%ld", (long)code];
    }
}
@end
