#import "JCORECrashConfigRequest.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCOREIntegrate.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCORENetChannel.h"
#define RequestFile                                                                                          \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Http "                               \
    "Event/JCORECrashConfigRequest.m"
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
@implementation JCORECrashConfigRequest
@synthesize aesKey = _aesKey, reportTimeStamp = _reportTimeStamp, appkey = _appkey, request, requestInfo,
            accountIDRequired, cacheRequired, reportFlag, controlFlag, addressList, completionHandler;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)init {
    self = [super init];
    if (self) {
        self.reportTimeStamp = (long long)([JCOREUtilities currentTimestamp] * 1000);
        self.aesKey = @"";
        NSString *key = [JCOREDataSecurity encryptKeyForHttp];
        if (key.length > 15)
            self.aesKey = [key substringToIndex:16];
        self.cacheRequired = NO;
        self.accountIDRequired = NO;
    }
    return self;
}
- (void)send {
    [JCORENetChannel send:self];
}
- (void)sendRequest:(RecoveryOpaqueCallback)completion {
    __weak JCORECrashConfigRequest *weakSelf = self;
    self.completionHandler = ^(id response, NSError *error) {
      id result = nil;
      if (response && [response isKindOfClass:[NSData class]]) {
          id content = [JCOREUtilities dataToJSON:response];
          if (content && [content isKindOfClass:[NSDictionary class]]) {
              RequestLog(67, @"crash config response:%@", content);
              id encoded = [content valueForKey:@"data"];
              if (encoded && [encoded isKindOfClass:[NSString class]] && [encoded length]) {
                  NSData *data = [[NSData alloc] initWithBase64EncodedString:encoded options:0];
                  if (data) {
                      JCORECrashConfigRequest *strongSelf = weakSelf;
                      NSData *plain = [JCOREUtilities aesDecryptData:data
                                                             withKey:strongSelf.aesKey
                                                                  iv:"0000000000000000"];
                      if (plain) {
                          id decoded = [JCOREUtilities dataToJSON:plain];
                          NSMutableDictionary *values =
                              [NSMutableDictionary dictionaryWithDictionary:content];
                          [values removeObjectForKey:@"data"];
                          [values setObject:decoded ?: @{} forKey:@"data"];
                          result = values;
                      }
                  }
              }
          }
      }
      if (completion)
          completion(result, error);
    };
    [JCORENetChannel send:self];
}
// Original getters recompute; the setters still retain their separate stored ivars.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (id)requestInfo {
    return [self bodyDataDic];
}
- (NSMutableURLRequest *)request {
    NSMutableData *data = [NSMutableData dataWithData:[JCOREUtilities jsonToData:self.requestInfo]];
    if (!data.length)
        return nil;
    NSData *encrypted = [JCOREDataSecurity aesEncryptData:data withKey:self.aesKey iv:"0000000000000000"];
    if (!encrypted.length) {
        [JCORELog log:YES
                level:0x3ff
                 flag:2
              context:0
                 file:RequestFile
             function:__PRETTY_FUNCTION__
                 line:112
                  tag:nil
               format:@"report canceled, body data nil"];
        return nil;
    }
    NSString *bodyString = [JCOREUtilities dataBase64:encrypted];
    NSData *body = [bodyString dataUsingEncoding:NSUTF8StringEncoding];
    if (!body.length) {
        [JCORELog log:YES
                level:0x3ff
                 flag:2
              context:0
                 file:RequestFile
             function:__PRETTY_FUNCTION__
                 line:119
                  tag:nil
               format:@"report canceled, body data nil"];
        return nil;
    }
    NSString *authorization = [self authorizationString];
    if (!authorization.length) {
        [JCORELog log:YES
                level:0x3ff
                 flag:2
              context:0
                 file:RequestFile
             function:__PRETTY_FUNCTION__
                 line:125
                  tag:nil
               format:@"crash config: report canceled, authorizationString nil"];
        return nil;
    }
    self.addressList = @[
        @"https://ce3e75d5.jpush.cn/cz/b85fd4e568833e92",
        @"https://ce3e75d5-ipv6.jpush.cn/cz/b85fd4e568833e92"
    ];
    NSString *timestamp = [NSString stringWithFormat:@"%ld", (long)self.reportTimeStamp];
    NSString *key =
        [JCOREUtilities stringByMD5:[NSString stringWithFormat:@"%@%@", [JCOREClientInfo appKey], timestamp]];
    NSString *content = [NSString stringWithFormat:@"%@%@%@%@", authorization, timestamp, @"1.0", bodyString];
    NSString *sign = [JCOREDataSecurity hmacSHA256WithKey:key content:content];
    RequestLog(149,
               @"crash config: Authorization:%@,\n x-jg-version:%@,\n x-jg-timestamp:%@,\n x-jg-sign:%@,\n "
               @"bodystr:%@",
               authorization, @"1.0", timestamp, sign, bodyString);
    NSMutableURLRequest *result = [NSMutableURLRequest new];
    result.HTTPMethod = @"POST";
    result.HTTPBody = body;
    [result setValue:authorization forHTTPHeaderField:@"Authorization"];
    [result setValue:@"1.0" forHTTPHeaderField:@"x-jg-version"];
    [result setValue:timestamp forHTTPHeaderField:@"x-jg-timestamp"];
    [result setValue:sign forHTTPHeaderField:@"x-jg-sign"];
    result.timeoutInterval = 30;
    return result;
}
#pragma clang diagnostic pop
- (id)bodyDataDic {
    NSString *token = [[[NSUUID UUID] UUIDString] stringByReplacingOccurrencesOfString:@"-" withString:@""];
    NSNumber *uid = @([JCOREClientInfo uid]);
    uid = uid ? @([JCOREClientInfo uid]) : @0;
    NSDictionary *info = @{
        @"platform" : @"i",
        @"app_key" : _appkey ?: @"",
        @"uid" : uid,
        @"appkey" : self.appkey ?: @"",
        @"stoken" : token ?: @"",
        @"core_sdk_ver" : [JCOREIntegrate jcoreDisplayVersion] ?: @"",
        @"os_ver" : [JCOREUtilities model] ?: @"",
        @"brand" : @"iphone"
    };
    RequestLog(174, @"http crash packet data, body:%@", info);
    return info;
}
- (id)authorizationString {
    id rsa = [JCOREDataSecurity rsaEncrypt:self.aesKey publicKey:[self publicKey]];
    if (rsa && [rsa isKindOfClass:[NSString class]] && [rsa length]) {
        NSString *value = [NSString stringWithFormat:@"%@:%@", [JCOREClientInfo appKey], rsa];
        NSData *data = [value dataUsingEncoding:NSUTF8StringEncoding];
        return [NSString stringWithFormat:@"Basic %@", [JCOREUtilities dataBase64:data]];
    }
    RequestLog(181, @"crash config: authorizationString rsaEncrypt error");
    return nil;
}
- (id)publicKey {
    return @"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGHnOJjf1yD7SOWt0w4q5ZqER+VMOTJ9oPh+"
           @"9TNgW8WpXwCklSLxFDM46eHWzT2TV46gZ0A6xTNVHKX4zAW621jfb6qzljTvwzM5Cxa5n2440kU8HF/"
           @"EDu04yqUpWCsNXs+jQD4R2gjGE203LW87fBivmfq0mxPvIR94UdKf10YQIDAQAB";
}
@end
