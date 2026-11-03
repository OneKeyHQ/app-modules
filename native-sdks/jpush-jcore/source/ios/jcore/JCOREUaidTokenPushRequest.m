#import "JCOREUaidTokenPushRequest.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCOREIntegrate.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCORENetChannel.h"
#define RequestFile                                                                                          \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Http "                               \
    "Event/JCOREUaidTokenPushRequest.m"
#define RequestLog(lineValue, fmt, ...)                                                                      \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:RequestFile                                                                                \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineValue                                                                                  \
              tag:nil                                                                                        \
           format:fmt, ##__VA_ARGS__]
@implementation JCOREUaidTokenPushRequest
@synthesize aesKey = _aesKey, reportTimeStamp = _reportTimeStamp, appkey = _appkey, channelId = _channelId,
            respBody = _respBody, respHeader = _respHeader, request, requestInfo, accountIDRequired,
            cacheRequired, reportFlag, controlFlag, addressList, completionHandler;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)init {
    self = [super init];
    if (self) {
        self.reportTimeStamp = (long long)([JCOREUtilities currentTimestamp]);
        self.aesKey = @"P8aQemLpVcPRCsl2";
        self.cacheRequired = NO;
        self.accountIDRequired = NO;
    }
    return self;
}
- (void)send {
    [JCORENetChannel send:self];
}
- (void)sendRequest:(RecoveryOpaqueCallback)completion {
    if (completion)
        self.completionHandler = completion;
    [JCORENetChannel send:self];
}
// Original getters recompute; the setters still retain their separate stored ivars.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (id)requestInfo {
    return [self bodyData];
}
- (NSMutableURLRequest *)request {
    NSMutableData *data = [NSMutableData dataWithData:[JCOREUtilities jsonToData:self.requestInfo]];
    if (!data.length)
        return nil;
    NSData *encrypted = [JCOREDataSecurity aesEncryptData:data
                                                  withKey:@"P8aQemLpVcPRCsl2"
                                                       iv:"IS78SPks2hwCZBLa"];
    if (!encrypted.length) {
        [JCORELog log:YES
                level:0x3ff
                 flag:2
              context:0
                 file:RequestFile
             function:__PRETTY_FUNCTION__
                 line:68
                  tag:nil
               format:@"report canceled, body data nil"];
        return nil;
    }
    NSString *bodyString = [JCOREUtilities dataBase64:encrypted];
    NSData *body = [bodyString dataUsingEncoding:NSUTF8StringEncoding];
    NSString *authorization = [self authorizationString];
    if (!authorization.length) {
        return nil;
    }
    self.addressList =
        @[ @"https://ce3e75d5.jpush.cn/wi/ra0cde", @"https://ce3e75d5-ipv6.jpush.cn/wi/ra0cde" ];
    NSMutableURLRequest *result = [NSMutableURLRequest new];
    result.HTTPMethod = @"POST";
    [result setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    result.HTTPBody = body;
    [result setValue:authorization forHTTPHeaderField:@"Authorization"];
    [result setValue:@"ios" forHTTPHeaderField:@"X-Http-Platform"];
    [result setValue:_appkey ?: @"appkey is null" forHTTPHeaderField:@"X-Http-Appkey"];
    [result setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [result setValue:@"UTF-8" forHTTPHeaderField:@"Charset"];
    result.timeoutInterval = 30;
    return result;
}
#pragma clang diagnostic pop
- (id)bodyData {
    id appkey = self.appkey;
    NSString *version = [JCOREIntegrate jcoreDisplayVersion], *model = [JCOREUtilities model];
    NSNumber *uid = @([JCOREClientInfo uid]);
    uid = uid ? @([JCOREClientInfo uid]) : @0;
    NSMutableDictionary *info = [NSMutableDictionary dictionaryWithDictionary:@{
        @"appkey" : appkey ?: @"",
        @"channelId" : _channelId ?: @"",
        @"core_sdk_ver" : version ?: @"",
        @"os_ver" : model ?: @"",
        @"platform" : @"i",
        @"respBody" : _respBody ?: @"",
        @"respHeader" : _respHeader ?: @{},
        @"uid" : uid
    }];
    RequestLog(114, @"http uaid token push packet data, body:%@", info);
    return info;
}
- (id)authorizationString {
    NSString *uid = [NSString stringWithFormat:@"%llu", [JCOREClientInfo uid]];
    NSString *password = [[JCOREUtilities stringByMD5:[JCOREClientInfo password]] uppercaseString];
    NSString *digest =
        [[JCOREUtilities stringByMD5:[NSString stringWithFormat:@"%@%@", uid, password]] uppercaseString];
    NSString *value = [NSString stringWithFormat:@"%llu:%@", [JCOREClientInfo uid], digest];
    return [NSString
        stringWithFormat:@"Basic %@",
                         [JCOREUtilities dataBase64:[value dataUsingEncoding:NSUTF8StringEncoding]]];
}
@end
