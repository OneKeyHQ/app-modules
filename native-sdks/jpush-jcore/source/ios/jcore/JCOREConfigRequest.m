#import "JCOREConfigRequest.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCORENetChannel.h"
@interface JCOREIpvxHelper : NSObject
+ (instancetype)instance;
- (NSDictionary *)getIPAddresses;
@end
#define RequestFile                                                                                          \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Http Event/JCOREConfigRequest.m"
@implementation JCOREConfigRequest
@synthesize req = _req, reportTimeStamp = _reportTimeStamp, appkey = _appkey, aesKey = _aesKey,
            authorizationString = _authorizationString;
@synthesize request, requestInfo, accountIDRequired, cacheRequired, reportFlag, controlFlag, addressList,
    completionHandler;
@dynamic hash, superclass, description, debugDescription;
+ (long long)getProtocolVersion {
    return 2;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        self.reportTimeStamp = (long long)[JCOREUtilities currentTimestamp];
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
// The original setters store these ivars, while getters recompute from live state.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (id)requestInfo {
    return [self bodyData];
}
- (NSString *)authorizationString {
    return [JCOREDataSecurity
        rsaEncrypt:self.aesKey
         publicKey:@"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApyZkPFrWtW5vrmLGCt+wpS+K0xZsH8QWKx0p+"
                   @"g3N6QonkNfEOaHns+se3E0OTa56+7MEpQ6cDoP0Hb3bCSth1QyLjFl14WXHi2J6gb+YkLnfNvkOQtmgx9tE4C/"
                   @"gmqfwNf6Aj0NG3O8QiGy4fteIMMZmz7+"
                   @"A7L7nqcPMdHI94YTvmeYeYT3T5txvBeHNbqzKKw6FdTEYVAYAlv2iAZ8/Dgq212K48EG7TT/"
                   @"A0TPOT4AUKMTL6uTkWlMkQmwF6h3HkA1ReCkq7Ijupp6fFC5LA1A19SQM71Juc7UhZI5v9yRrKJaCrYKnQWFdVBe"
                   @"Coe/+u08OxJro/BHzNb4qfSy7MQIDAQAB"];
}
- (NSMutableURLRequest *)request {
    if (self.req)
        return self.req;
    NSMutableData *body = [NSMutableData dataWithData:[JCOREUtilities jsonToData:[self bodyData]]];
    if (!body.length)
        return nil;
    NSData *encrypted = [JCOREDataSecurity aes256EncryptData:body withKey:self.aesKey iv:"0102030405060708"];
    if (!encrypted.length) {
        [JCORELog log:YES
                level:0x3ff
                 flag:2
              context:0
                 file:RequestFile
             function:"-[JCOREConfigRequest request]"
                 line:99
                  tag:nil
               format:@"report canceled, body data nil"];
        return nil;
    }
    NSString *authorization = [self authorizationString];
    if (!authorization.length)
        return nil;
    self.addressList = @[ @"https://config.jpush.cn/v1/status", @"https://config-ipv6.jpush.cn/v1/status" ];
    NSDictionary *addresses = [[JCOREIpvxHelper instance] getIPAddresses];
    NSString *ip = addresses.allValues.count ? addresses.allValues.firstObject : @"0";
    if (!ip.length)
        ip = @"0";
    NSMutableURLRequest *result = [NSMutableURLRequest new];
    result.HTTPMethod = @"POST";
    [result setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [result setValue:@"text/plain" forHTTPHeaderField:@"Content-Type"];
    result.HTTPBody = encrypted;
    [result setValue:authorization forHTTPHeaderField:@"Authorization"];
    [result setValue:ip forHTTPHeaderField:@"X-Real-IP"];
    result.timeoutInterval = 30;
    self.req = result;
    return result;
}
#pragma clang diagnostic pop
- (id)bodyData {
    NSDictionary *body = [self sisRequestInfo];
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:RequestFile
         function:"-[JCOREConfigRequest bodyData]"
             line:64
              tag:nil
           format:@"http sis packet data, body:%@", body];
    return body.allKeys.count ? body : nil;
}
- (id)sisRequestInfo {
    if (!self.appkey.length)
        return nil;
    NSString *token = [[[NSUUID UUID] UUIDString] stringByReplacingOccurrencesOfString:@"-" withString:@""];
    NSNumber *uid = @([JCOREClientInfo uid]);
    uid = uid ? @([JCOREClientInfo uid]) : @0;
    return @{
        @"uid" : uid,
        @"appkey" : self.appkey,
        @"stoken" : token,
        @"ver" : @([JCOREConfigRequest getProtocolVersion]),
        @"platform" : @"i"
    };
}
@end
