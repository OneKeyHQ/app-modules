#import "JCommonIpAddressRequest.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCORELog.h"
#import "JCORENetChannel.h"
#import "JCORETestAddressCache.h"
#import "JCOREUtilities.h"
#define IpRequestFile                                                                                        \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Network/JCommonIpAddressRequest.m"
@implementation JCommonIpAddressRequest
@synthesize request, requestInfo, accountIDRequired, cacheRequired, reportFlag, controlFlag, addressList,
    completionHandler;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)init {
    return [super init];
}
- (void)send {
    [JCORENetChannel send:self];
}
- (void)sendRequest:(RecoveryOpaqueCallback)completion {
    self.completionHandler = ^(id response, NSError *error) {
      if (error || !response || ![response isKindOfClass:[NSData class]]) {
          if (completion)
              completion(nil, error);
          return;
      }
      NSString *text = [[NSString alloc] initWithData:response encoding:NSUTF8StringEncoding];
      NSData *decoded = [JCOREDataSecurity dataFromBase64:text];
      decoded = [JCOREUtilities unzip:decoded];
      NSData *plain = [JCOREDataSecurity aesDecryptData:decoded
                                                withKey:@"DFA84B10B7ACDD25"
                                                     iv:"DFA84B10B7ACDD25"];
      if (!plain || ![plain isKindOfClass:[NSData class]]) {
          if (completion)
              completion(nil, nil);
          return;
      }
      id content = [NSJSONSerialization JSONObjectWithData:plain options:0 error:NULL];
      [JCORELog log:YES
              level:0x3ff
               flag:0x100
            context:0
               file:IpRequestFile
           function:__PRETTY_FUNCTION__
               line:59
                tag:nil
             format:@"ipv6 address request result:%@", content];
      if (!content || ![content isKindOfClass:[NSDictionary class]] ||
          ![[content allKeys] containsObject:@"ipv6"]) {
          if (completion)
              completion(nil, nil);
          return;
      }
      id ipv6 = [content valueForKey:@"ipv6"];
      if (completion)
          completion(ipv6 && [ipv6 isKindOfClass:[NSString class]] ? ipv6 : nil, nil);
    };
    [JCORENetChannel send:self];
}
// Both getters recompute, while the corresponding original setters retain separate ivars.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (id)requestInfo {
    return [self bodyData];
}
- (id)bodyData {
    return @{
        @"appKey" : [JCOREClientInfo appKey] ?: @"",
        @"uid" : @([JCOREClientInfo uid]),
        @"appName" : [JCOREUtilities appName] ?: @"",
        @"appVersion" : [JCOREUtilities appVersion] ?: @"",
        @"appChannel" : [JCOREClientInfo channel] ?: @"",
        @"appPackage" : [JCOREUtilities bundleID] ?: @""
    };
}
- (NSMutableURLRequest *)request {
    NSMutableData *data = [NSMutableData dataWithData:[JCOREUtilities jsonToData:[self bodyData]]];
    if (!data.length)
        return nil;
    NSData *zipped = [JCOREUtilities gzip:data];
    if (!zipped.length)
        return nil;
    NSData *encrypted = [JCOREDataSecurity aesEncryptData:zipped
                                                  withKey:@"DFA84B10B7ACDD25"
                                                       iv:"DFA84B10B7ACDD25"];
    NSString *base64 = [JCOREUtilities dataBase64:encrypted];
    if (!base64.length)
        return nil;
    NSData *body = [base64 dataUsingEncoding:NSUTF8StringEncoding];
    if (!body.length)
        return nil;
    id hash = [JCOREDataSecurity dataHashingMD5:body];
    if (![hash length])
        return nil;
    id authorization = [self authorizationString:hash];
    if (![authorization length])
        return nil;
    self.addressList = @[ @"https://status-ipv6.jpush.cn/wi/jx/6ae71c" ];
    if ([JCORETestAddressCache isDebugMode]) {
        id address = [[NSUserDefaults standardUserDefaults]
            objectForKey:@"kJIGUANGQATest_IPV6AddressRequestConfigAddress"];
        if ([address isKindOfClass:[NSString class]] && [address length])
            self.addressList = @[ address ];
    }
    NSMutableURLRequest *result = [NSMutableURLRequest new];
    result.HTTPMethod = @"POST";
    [result setValue:@"text/plain" forHTTPHeaderField:@"Content-Type"];
    result.HTTPBody = body;
    [result setValue:authorization forHTTPHeaderField:@"Authorization"];
    result.timeoutInterval = 30;
    return result;
}
#pragma clang diagnostic pop
- (id)authorizationString:(id)bodyHash {
    NSString *uid = [NSString stringWithFormat:@"%llu", [JCOREClientInfo uid]];
    id password = [JCOREUtilities stringByMD5:[JCOREClientInfo password]];
    NSString *plain = [NSString stringWithFormat:@"%@%@%@", uid, password, bodyHash];
    id digest = [JCOREUtilities stringByMD5:plain];
    plain = [NSString stringWithFormat:@"%llu:%@", [JCOREClientInfo uid], digest];
    return [NSString
        stringWithFormat:@"%@", [JCOREUtilities dataBase64:[plain dataUsingEncoding:NSUTF8StringEncoding]]];
}
@end
