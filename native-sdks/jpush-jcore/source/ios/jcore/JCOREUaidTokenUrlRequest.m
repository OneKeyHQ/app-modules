#import "JCOREUaidTokenUrlRequest.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCOREIntegrate.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCORENetChannel.h"
#define RequestFile                                                                                          \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Http "                               \
    "Event/JCOREUaidTokenUrlRequest.m"
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
@implementation JCOREUaidTokenUrlRequest
@synthesize aesKey = _aesKey, appkey = _appkey, request, requestInfo, accountIDRequired, cacheRequired,
            reportFlag, controlFlag, addressList, completionHandler;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)init {
    self = [super init];
    if (self) {
        self.aesKey = @"P8aQemLpVcPRCsl2";
        self.cacheRequired = NO;
        self.accountIDRequired = NO;
        self.appkey = [JCOREClientInfo appKey];
    }
    return self;
}
- (void)send {
    [JCORENetChannel send:self];
}
- (void)sendRequest:(RecoveryOpaqueCallback)completion {
    self.completionHandler = ^(id response, NSError *error) {
      if (error || !response || ![response isKindOfClass:[NSData class]]) {
          if (completion)
              completion(nil, error);
      }
      NSString *text = [[NSString alloc] initWithData:response encoding:NSUTF8StringEncoding];
      NSData *decoded = [JCOREDataSecurity dataFromBase64:text];
      NSData *plain = [JCOREDataSecurity aesDecryptData:decoded
                                                withKey:@"P8aQemLpVcPRCsl2"
                                                     iv:"IS78SPks2hwCZBLa"];
      id content = [JCOREUtilities dataToJSON:plain];
      RequestLog(62, @"[%s] config content is %@", __PRETTY_FUNCTION__, content);
      if (!content || ![content isKindOfClass:[NSDictionary class]]) {
          RequestLog(64, @"[%s] content is bad", __PRETTY_FUNCTION__);
          if (completion)
              completion(nil, error);
          return;
      }
      if (completion)
          completion(content, error);
    };
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
                 line:89
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
        @[ @"https://ce3e75d5.jpush.cn/wi/bd0r1q", @"https://ce3e75d5-ipv6.jpush.cn/wi/bd0r1q" ];
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
    id carrier = [JCOREUtilities ctCurCellularCarrierName];
    if (carrier && [carrier isKindOfClass:[NSString class]])
        [carrier isEqualToString:@""];
    NSString *channel = @"";
    if ([carrier rangeOfString:@"移动"].length)
        channel = @"CM";
    else if ([carrier rangeOfString:@"联通"].length)
        channel = @"CU";
    else if ([carrier rangeOfString:@"电信"].length)
        channel = @"CT";
    NSNumber *uid = @([JCOREClientInfo uid]);
    uid = uid ? @([JCOREClientInfo uid]) : @0;
    NSMutableDictionary *info = [NSMutableDictionary dictionaryWithDictionary:@{
        @"appkey" : _appkey ?: @"",
        @"core_sdk_ver" : [JCOREIntegrate jcoreDisplayVersion] ?: @"",
        @"os_ver" : [JCOREUtilities model] ?: @"",
        @"platform" : @"i",
        @"uid" : uid
    }];
    if (channel.length)
        [info setValue:channel forKey:@"channelType"];
    RequestLog(146, @"http uaid url pull, body:%@", info);
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
