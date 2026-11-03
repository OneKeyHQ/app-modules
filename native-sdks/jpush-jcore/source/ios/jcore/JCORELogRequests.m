#import "JCORELogRequests.h"
#import "JCOREClientInfo.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"

#import "JCORENetChannel.h"

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
// Original request getters generate fresh values; their setters retain separate ivars.
@implementation JCORELogTokenRequest
@synthesize accountIDRequired = accountIDRequired, cacheRequired = cacheRequired;
@synthesize completionHandler = completionHandler, reportFlag = reportFlag;
@synthesize request = request, requestInfo = requestInfo, addressList = addressList;
@synthesize reportTimeStamp = _reportTimeStamp, filename = _filename;
@dynamic controlFlag, hash, superclass, description, debugDescription;
- (instancetype)init { return [super init]; }
- (void)send { [JCORENetChannel send:self]; }
- (void)sendRequest:(RecoveryOpaqueCallback)completion {
    if (completion) self.completionHandler = completion;
    [JCORENetChannel send:self];
}
- (id)requestInfo { return [self badgeBody]; }
- (NSMutableDictionary *)badgeBody {
    @try {
        _badgeBody = [[NSMutableDictionary alloc] initWithCapacity:3];
        _badgeBody[@"uid"] = @([JCOREClientInfo uid]);
        _badgeBody[@"appkey"] = [JCOREClientInfo appKey];
        _badgeBody[@"key"] = self.filename ?: @"";
    } @catch (NSException *exception) {
        [JCORELog log:YES level:0x3ff flag:0x100 context:0
            file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Http Event/JCORELogTokenRequest.m"
            function:"-[JCORELogTokenRequest badgeBody]" line:63 tag:nil format:@"[bomb] [%s] is exception", "-[JCORELogTokenRequest badgeBody]"];
    }
    return _badgeBody;
}
- (NSMutableURLRequest *)request { return [self generateReportRequest]; }
- (NSMutableURLRequest *)generateReportRequest {
    _bodyData = [JCOREUtilities jsonToData:[self badgeBody]];
    if (!_bodyData) return nil;
    NSString *authorization = [self authorizationString];
    self.addressList = @[@"https://ce3e75d5.jpush.cn/wi/lmops", @"https://ce3e75d5-ipv6.jpush.cn/wi/lmops"];
    NSMutableURLRequest *result = [[NSMutableURLRequest alloc] init];
    result.HTTPMethod = @"POST";
    [result setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [result setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [result setValue:authorization forHTTPHeaderField:@"Authorization"];
    result.timeoutInterval = 30.0;
    result.HTTPBody = _bodyData;
    return result;
}
- (NSString *)authorizationString {
    NSString *uidString = [NSString stringWithFormat:@"%llu", (unsigned long long)[JCOREClientInfo uid]];
    NSString *passwordHash = [[JCOREUtilities stringByMD5:[JCOREClientInfo password]] uppercaseString];
    NSString *digest = [[JCOREUtilities stringByMD5:[NSString stringWithFormat:@"%@%@", uidString, passwordHash]] uppercaseString];
    NSString *credentials = [NSString stringWithFormat:@"%llu:%@", (unsigned long long)[JCOREClientInfo uid], digest];
    NSString *base64 = [JCOREUtilities dataBase64:[credentials dataUsingEncoding:NSUTF8StringEncoding]];
    return [NSString stringWithFormat:@"Basic %@", base64];
}
@end

@implementation JCORELogFileUploadRequest
@synthesize accountIDRequired = accountIDRequired, cacheRequired = cacheRequired;
@synthesize completionHandler = completionHandler, reportFlag = reportFlag;
@synthesize request = request, requestInfo = requestInfo, addressList = addressList;
@dynamic controlFlag, hash, superclass, description, debugDescription;
- (instancetype)initWithUrl:(NSString *)url fileData:(NSData *)data {
    self = [super init];
    if (self) { self.addressList = @[url]; _fileData = data; }
    return self;
}
- (void)sendRequest:(RecoveryOpaqueCallback)completion {
    if (completion) self.completionHandler = completion;
    [JCORENetChannel send:self];
}
- (NSMutableURLRequest *)request {
    NSMutableURLRequest *result = [[NSMutableURLRequest alloc] init];
    result.HTTPMethod = @"PUT";
    [result setValue:@"multipart/form-data" forHTTPHeaderField:@"Content-Type"];
    result.HTTPBody = _fileData;
    return result;
}
@end
#pragma clang diagnostic pop
