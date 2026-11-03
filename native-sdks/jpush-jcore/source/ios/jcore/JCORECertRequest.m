#import "JCORECertRequest.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCOREIntegrate.h"
#import "JCORELog.h"
#import "JCORENetChannel.h"
#import "JCOREUtilities.h"
#define CertRequestFile                                                                                      \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Http Event/JCORECertRequest.m"
@implementation JCORECertRequest
@synthesize aesKey = _aesKey, timeOut = _timeOut, retryCount = _retryCount, request, requestInfo,
            accountIDRequired, cacheRequired, reportFlag, completionHandler;
@dynamic hash, superclass, description, debugDescription, controlFlag, addressList;
- (instancetype)init {
    self = [super init];
    if (self) {
        _timeOut = 30;
        _retryCount = 1;
        _aesKey = [JCOREDataSecurity encryptKeyForHttp];
    }
    return self;
}
- (void)sendWithCompletion:(JCORECertRequestCompletion)completion {
    self.completionHandler = ^(id response, NSError *error) {
      if (!error && response && [response isKindOfClass:[NSData class]]) {
          id content = [JCOREUtilities dataToJSON:[JCOREUtilities unzip:response]];
          if (content && [content isKindOfClass:[NSDictionary class]]) {
              [JCORELog log:YES
                      level:0x3ff
                       flag:0x100
                    context:0
                       file:CertRequestFile
                   function:__PRETTY_FUNCTION__
                       line:55
                        tag:nil
                     format:@"cert request result: %@", content];
              id code = [content valueForKey:@"code"];
              if (code && [code isKindOfClass:[NSNumber class]] && [code intValue] == 200) {
                  id version = [content valueForKey:@"ver"], cert = [content valueForKey:@"cert"];
                  if (version && [version isKindOfClass:[NSNumber class]] && cert &&
                      [cert isKindOfClass:[NSString class]]) {
                      NSData *decoded = [JCOREDataSecurity dataFromBase64:cert];
                      NSData *plain = [JCOREDataSecurity aesDecryptData:decoded
                                                                withKey:@"DFA84B10B7ACDD25"
                                                                     iv:"DFA84B10B7ACDD25"];
                      NSString *value = [[NSString alloc] initWithData:plain encoding:NSUTF8StringEncoding];
                      if (value)
                          completion(YES, version, value);
                      else
                          completion(NO, nil, nil);
                      return;
                  }
              }
          }
      }
      completion(NO, nil, nil);
    };
    [JCORENetChannel send:self];
}
- (NSArray *)addressList {
    NSMutableArray *list = [NSMutableArray array];
    for (long long i = 0; i < _retryCount; i++)
        [list addObject:@"https://sslcert.jpush.cn"];
    return list;
}
- (id)bodyData {
    if (![JCOREClientInfo appKey] || ![[JCOREClientInfo appKey] length])
        return nil;
    NSDictionary *content = @{
        @"appkey" : [JCOREClientInfo appKey] ?: @"",
        @"sdkver" : [JCOREIntegrate jcoreDisplayVersion],
        @"platform" : @1
    };
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:CertRequestFile
         function:__PRETTY_FUNCTION__
             line:112
              tag:nil
           format:@"cert request packet data, body:%@", content];
    return [NSMutableData dataWithData:[JCOREUtilities jsonToData:content]];
}
// The original setter stores a separate ivar; this getter always builds a fresh request.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (NSMutableURLRequest *)request {
    NSData *body = [self bodyData];
    if (!body.length)
        return nil;
    body = [JCOREUtilities gzip:body];
    NSData *encrypted = [JCOREDataSecurity aes256EncryptData:body withKey:self.aesKey iv:"0102030405060708"];
    if (!encrypted.length) {
        [JCORELog log:YES
                level:0x3ff
                 flag:2
              context:0
                 file:CertRequestFile
             function:__PRETTY_FUNCTION__
                 line:127
                  tag:nil
               format:@"report canceled, body data nil"];
        return nil;
    }
    id authorization = [self authorizationString];
    NSMutableURLRequest *result = [NSMutableURLRequest new];
    result.HTTPMethod = @"POST";
    [result setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [result setValue:authorization forHTTPHeaderField:@"Authorization"];
    result.HTTPBody = encrypted;
    result.timeoutInterval = _timeOut;
    return result;
}
#pragma clang diagnostic pop
- (id)authorizationString {
    return [JCOREDataSecurity
        rsaEncrypt:self.aesKey
         publicKey:@"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCi0v4VEUhOdfIEfFCrPC72pcDsQF/"
                   @"luTmr4q34NY0EZYGKzfQuTrUAm916P52HCgF+342gjZ/Nvijts5543qYNyoLvgtu4NRcHJmuGI/"
                   @"w7qifhfsivYeoEj9wYphXOyB9HUjzwn1BtAih+1RyUrcErCi249yabUOIKQygPZ9OXXQIDAQAB"];
}
@end
