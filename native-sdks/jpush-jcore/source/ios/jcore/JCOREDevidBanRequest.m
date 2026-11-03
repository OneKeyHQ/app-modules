#import "JCOREDevidBanRequest.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCOREIntegrate.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCORENetChannel.h"
@interface JCOREIpvxHelper : NSObject
+ (instancetype)instance;
- (NSDictionary *)getIPAddresses;
@end
#define RequestFile                                                                                          \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Http Event/JCOREDevidBanRequest.m"
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
@implementation JCOREDevidBanRequest
@synthesize reportTimeStamp = _reportTimeStamp, appkey = _appkey, aesKey = _aesKey, request, requestInfo,
            accountIDRequired, cacheRequired, reportFlag, controlFlag, addressList, completionHandler;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)init {
    self = [super init];
    if (self) {
        self.reportTimeStamp = (long long)([JCOREUtilities currentTimestamp]);
        self.aesKey = [JCOREDataSecurity encryptKeyForHttp];
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
    NSMutableData *data = [NSMutableData dataWithData:[JCOREUtilities jsonToData:[self bodyData]]];
    if (!data.length)
        return nil;
    NSData *encrypted = [JCOREDataSecurity aes256EncryptData:data withKey:self.aesKey iv:"0102030405060708"];
    if (!encrypted.length) {
        [JCORELog log:YES
                level:0x3ff
                 flag:2
              context:0
                 file:RequestFile
             function:__PRETTY_FUNCTION__
                 line:74
                  tag:nil
               format:@"devid config pull canceled, body data nil"];
        return nil;
    }
    NSData *body = encrypted;
    NSString *authorization = [self authorizationString];
    if (!authorization.length) {
        return nil;
    }
    self.addressList =
        @[ @"https://config.jpush.cn/v1/version-check", @"https://config-ipv6.jpush.cn/v1/version-check" ];
    NSDictionary *addresses = [[JCOREIpvxHelper instance] getIPAddresses];
    NSString *ip = addresses.allValues.count ? addresses.allValues.firstObject : @"0";
    if (!ip.length)
        ip = @"0";
    NSMutableURLRequest *result = [NSMutableURLRequest new];
    result.HTTPMethod = @"POST";
    [result setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [result setValue:@"text/plain" forHTTPHeaderField:@"Content-Type"];
    result.HTTPBody = body;
    [result setValue:authorization ?: @"" forHTTPHeaderField:@"Authorization"];
    [result setValue:ip ?: @"" forHTTPHeaderField:@"X-Real-IP"];
    result.timeoutInterval = 30;
    return result;
}
#pragma clang diagnostic pop
- (id)bodyData {
    id info = [self devidBanRequestInfo];
    RequestLog(59, @"http devid packet data, body:%@", info);
    return [info allKeys].count ? info : nil;
}
- (id)devidBanRequestInfo {
    if (!self.appkey.length)
        return nil;
    NSMutableDictionary *info =
        [NSMutableDictionary dictionaryWithDictionary:[JCOREIntegrate isHasCustomSDK]];
    [info
        setValuesForKeysWithDictionary:@{@"appkey" : self.appkey ? self.appkey : @"", @"platform" : @"ios"}];
    return info;
}
- (id)authorizationString {
    return [JCOREDataSecurity
        rsaEncrypt:self.aesKey
         publicKey:@"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApyZkPFrWtW5vrmLGCt+wpS+K0xZsH8QWKx0p+"
                   @"g3N6QonkNfEOaHns+se3E0OTa56+7MEpQ6cDoP0Hb3bCSth1QyLjFl14WXHi2J6gb+YkLnfNvkOQtmgx9tE4C/"
                   @"gmqfwNf6Aj0NG3O8QiGy4fteIMMZmz7+"
                   @"A7L7nqcPMdHI94YTvmeYeYT3T5txvBeHNbqzKKw6FdTEYVAYAlv2iAZ8/Dgq212K48EG7TT/"
                   @"A0TPOT4AUKMTL6uTkWlMkQmwF6h3HkA1ReCkq7Ijupp6fFC5LA1A19SQM71Juc7UhZI5v9yRrKJaCrYKnQWFdVBe"
                   @"Coe/+u08OxJro/BHzNb4qfSy7MQIDAQAB"];
}
@end
