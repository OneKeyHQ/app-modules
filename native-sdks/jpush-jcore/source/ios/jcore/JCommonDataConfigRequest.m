#import "JCommonDataConfigRequest.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCORENetChannel.h"
#import "JCOREUtilities.h"
@implementation JCommonDataConfigRequest
@synthesize sign = _sign, request, requestInfo, accountIDRequired, cacheRequired, reportFlag, controlFlag,
            addressList, completionHandler;
@dynamic hash, superclass, description, debugDescription;
- (instancetype)init {
    return [super init];
}
- (void)send {
    [JCORENetChannel send:self];
}
- (void)sendRequest:(RecoveryOpaqueCallback)completion {
    self.completionHandler = ^(id response, NSError *error) {
      if (error || !response || ![response isKindOfClass:NSData.class]) {
          if (completion) {
              completion(nil, error);
              return;
          }
      }
      NSString *text = [[NSString alloc] initWithData:response encoding:NSUTF8StringEncoding];
      id data = [JCOREDataSecurity dataFromBase64:text];
      if (!data && completion) {
          completion(nil, error);
          return;
      }
      id plain = [JCOREDataSecurity aesDecryptData:data withKey:@"I8iQemLpVcDAL0AS" iv:"RTDdJXgDDVXH8Tbv"];
      if ((!plain || ![plain isKindOfClass:NSData.class]) && completion) {
          completion(nil, error);
          return;
      }
      id content = [JCOREUtilities dataToJSON:plain];
      if (content && [content isKindOfClass:NSDictionary.class]) {
          if (completion)
              completion(content, nil);
          return;
      }
      if (completion)
          completion(nil, error);
    };
    [JCORENetChannel send:self];
}
- (NSMutableDictionary *)content {
    NSMutableDictionary *content = [[NSMutableDictionary alloc] init];
    content[@"ak"] = [JCOREClientInfo appKey];
    content[@"p"] = @"i";
    if (_sign)
        content[@"sign"] = _sign;
    return content;
}
// The setter retains its backing ivar; the original getter always generates a fresh request.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (NSMutableURLRequest *)request {
    return [self generateReportRequest];
}
#pragma clang diagnostic pop
- (NSMutableURLRequest *)generateReportRequest {
    id content = [self content];
    if (!content)
        return nil;
    id json = [JCOREUtilities jsonToData:content];
    if (!json)
        return nil;
    id encrypted = [JCOREUtilities aesEncryptData:json withKey:@"I8iQemLpVcDAL0AS" iv:"RTDdJXgDDVXH8Tbv"];
    if (!encrypted)
        return nil;
    id text = [JCOREUtilities dataBase64:encrypted];
    if (!text)
        return nil;
    id body = [text dataUsingEncoding:NSUTF8StringEncoding];
    if (!body)
        return nil;
    id addresses = [NSUserDefaults.standardUserDefaults objectForKey:@"RequestDataConfigAddress"];
    if (![addresses isKindOfClass:NSArray.class] || ![addresses count])
        self.addressList = @[ @"https://ce3e75d5.jpush.cn/wi/op8jdu" ];
    else
        self.addressList = addresses;
    NSMutableURLRequest *result = [[NSMutableURLRequest alloc] init];
    result.HTTPMethod = @"POST";
    [result setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [result setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [result setValue:@"UTF-8" forHTTPHeaderField:@"Charset"];
    result.timeoutInterval = 30;
    result.HTTPBody = body;
    return result;
}
@end
