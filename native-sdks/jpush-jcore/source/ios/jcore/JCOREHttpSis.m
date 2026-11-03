#import "JCOREHttpSis.h"
#import "JCOREBusinessStorage.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCOREIntegrate.h"
#import "JCORELog.h"
#import "JCORENetDataManager.h"
#import "JCORENetUtilities.h"
#import "JCORENetworkReachabilityManager.h"
#import "JCOREUtilities.h"
#import "RecoveredTransport.h"
#define HttpSisFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Sis/JCOREHttpSis.m"
@implementation JCOREHttpSisRequest
- (instancetype)init {
    self = [super init];
    if (self)
        self.aesKey = [JCOREDataSecurity encryptKeyForHttp];
    return self;
}
- (NSMutableData *)bodyData {
    if ([JCOREClientInfo appKey] && [[JCOREClientInfo appKey] length] && [JCOREClientInfo uid]) {
        NSDictionary *content = [self sisRequestInfo];
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:HttpSisFile
             function:__PRETTY_FUNCTION__
                 line:71
                  tag:nil
               format:@"http sis packet data, body:%@", content];
        if (content.allKeys.count)
            return [NSMutableData dataWithData:[JCOREUtilities jsonToData:content]];
    }
    return nil;
}
- (NSDictionary *)sisRequestInfo {
    id carrier = [JCOREUtilities ctCarrierName];
    unsigned char network = [JCOREUtilities ctGenerationNumber];
    id location = [JCOREBusinessStorage getLocation];
    id lat = @0, lng = @0, time = @0;
    if ([location isKindOfClass:NSDictionary.class] && [location count]) {
        lat = [location valueForKey:@"lat"];
        lng = [location valueForKey:@"lng"];
        time = [location valueForKey:@"itime"];
    }
    return @{
        @"type" : @(network),
        @"opera" : carrier ?: @"",
        @"uid" : @([JCOREClientInfo uid]),
        @"appkey" : [JCOREClientInfo appKey] ?: @"",
        @"sdkver" : [JCOREIntegrate jcoreDisplayVersion],
        @"lat" : lat ?: @0,
        @"lng" : lng ?: @0,
        @"time" : time ?: @0,
        @"platform" : @1
    };
}
- (NSMutableURLRequest *)httpRequest {
    NSData *body = [self bodyData];
    if (!body.length)
        return nil;
    NSData *compressed = [JCOREUtilities gzip:body];
    NSData *encrypted = [JCOREDataSecurity aes256EncryptData:compressed
                                                     withKey:self.aesKey
                                                          iv:"0102030405060708"];
    if (!encrypted.length) {
        [JCORELog log:YES
                level:0x3ff
                 flag:2
              context:0
                 file:HttpSisFile
             function:__PRETTY_FUNCTION__
                 line:117
                  tag:nil
               format:@"report canceled, body data nil"];
        return nil;
    }
    NSString *auth = [self authorizationString];
    NSString *address = self.reportAddress;
    if (!auth.length || !address.length)
        return nil;
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    request.URL = [NSURL URLWithString:address];
    request.HTTPMethod = @"POST";
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    request.HTTPBody = encrypted;
    [request setValue:auth forHTTPHeaderField:@"Authorization"];
    request.timeoutInterval = 30;
    return request;
}
- (NSString *)authorizationString {
    return [JCOREDataSecurity
        rsaEncrypt:self.aesKey
         publicKey:@"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCi0v4VEUhOdfIEfFCrPC72pcDsQF/"
                   @"luTmr4q34NY0EZYGKzfQuTrUAm916P52HCgF+342gjZ/Nvijts5543qYNyoLvgtu4NRcHJmuGI/"
                   @"w7qifhfsivYeoEj9wYphXOyB9HUjzwn1BtAih+1RyUrcErCi249yabUOIKQygPZ9OXXQIDAQAB"];
}
@end
@implementation JCOREHttpSis
@synthesize normalReportAddressList = _normalReportAddressList, expired = _expired;

+ (NSArray *)httpSisDefaultAddress {
    return [JCOREIpvPreferedUtilities
        getPreferedAddresses:@[ @"https://tsis.jpush.cn", @"https://tsis-ipv6.jpush.cn" ]];
}
- (NSArray *)sisIpsFromServer {
    return [JCOREBusinessStorage getHttpSisAddressList];
}
- (void)setSisIpsFromServer:(NSArray *)list {
    [JCOREBusinessStorage setHttpSisAddressList:list];
}
- (NSDictionary *)reportAddressDict {
    return [JCOREBusinessStorage getReportAddressDict];
}
- (void)setReportAddressDict:(NSDictionary *)information {
    [JCOREBusinessStorage setReportAddressDict:information];
}
// The original setter stores a distinct backing ivar; this getter reads persisted report addresses.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
- (NSArray *)normalReportAddressList {
    return self.reportAddressDict[@"normal"];
}
- (BOOL)expired {
    double now = [JCOREUtilities currentTimestamp];
    if (self.nextRequestTime > 0) {
        NSString *next = [JCOREUtilities converTimestampToString:(long)self.nextRequestTime];
        NSString *current = [JCOREUtilities converTimestampToString:(long)now];
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:HttpSisFile
             function:__PRETTY_FUNCTION__
                 line:209
                  tag:nil
               format:@"nextUpdateTime:%@, now:%@", next, current];
    }
    return self.nextRequestTime <= now;
}
#pragma clang diagnostic pop
- (double)nextRequestTime {
    return [JCOREBusinessStorage getNextSisRequestTime];
}
- (void)setNextRequestTime:(double)time {
    [JCOREBusinessStorage setNextSisRequestTime:time];
}
- (void)requst:(id)address result:(JCOREHttpSisCompletionHandler)completion {
    if (![address isKindOfClass:NSString.class] || ![address length] || self.requesting)
        return;
    JCOREHttpSisRequest *request = [[JCOREHttpSisRequest alloc] init];
    request.reportAddress = address;
    if (![request httpRequest]) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:HttpSisFile
             function:__PRETTY_FUNCTION__
                 line:224
                  tag:nil
               format:@"invalid httpSis request,cancel"];
        return;
    }
    __weak typeof(self) weakSelf = self;
    JCOREHttpSisResult result = ^(BOOL success, NSData *data) {
      __strong typeof(weakSelf) target = weakSelf;
      if (success) {
          id content = [JCOREUtilities dataToJSON:[JCOREUtilities unzip:data]];
          [JCORELog log:YES
                  level:0x3ff
                   flag:0x100
                context:0
                   file:HttpSisFile
               function:"-[JCOREHttpSis requst:result:]_block_invoke"
                   line:232
                    tag:nil
                 format:@"http sis success, result:%@", content];
          if ([content isKindOfClass:NSDictionary.class]) {
              id response = content[@"ret"];
              if ([response isKindOfClass:NSDictionary.class]) {
                  id ips = response[@"ips"];
                  if (!ips || [ips isKindOfClass:NSDictionary.class])
                      target.reportAddressDict = [ips copy];
                  id sisIps = response[@"sis_ips"];
                  if (!sisIps || [sisIps isKindOfClass:NSArray.class])
                      target.sisIpsFromServer = [sisIps copy];
                  double previous = target.nextRequestTime;
                  double now = [JCOREUtilities currentTimestamp];
                  id ttl = response[@"ttl"];
                  long expiry = 3600;
                  if ([ttl isKindOfClass:NSNumber.class]) {
                      if ([ttl longValue] < 0)
                          ttl = @3600;
                      else if ([ttl longValue] < 60)
                          ttl = @60;
                      else if ([ttl longValue] > 604800)
                          ttl = @604800;
                      expiry = [ttl integerValue];
                  }
                  if (ips || sisIps || ttl) {
                      target.nextRequestTime = now + (double)expiry;
                      NSString *old = [JCOREUtilities converTimestampToString:(long)previous];
                      NSString *next = [JCOREUtilities converTimestampToString:(long)self.nextRequestTime];
                      [JCORELog log:YES
                              level:0x3ff
                               flag:0x100
                            context:0
                               file:HttpSisFile
                           function:"-[JCOREHttpSis requst:result:]_block_invoke"
                               line:262
                                tag:nil
                             format:@"lastUpdateTime:%@, nextUpdateTime:%@, expire:%ld", old, next, expiry];
                  }
              }
          }
      }
      if (completion && ![(id)completion isKindOfClass:NSNull.class])
          completion(success);
      target.requesting = NO;
    };
    request.resultHandler = result;
    double now = [JCOREUtilities currentTimestamp];
    unsigned short rid = [JCORENetUtilities generateRid];
    NSString *identifier =
        [NSString stringWithFormat:@"%d:%llu", (int)rid, (unsigned long long)now];
    NSURLRequest *http = [request httpRequest];
    NSArray *parts = [http.URL.absoluteString componentsSeparatedByString:@"?"];
    NSString *base = parts[0];
    NSString *method = http.HTTPMethod ?: @"GET";
    NSUInteger bytes =
        [method isEqualToString:@"POST"]
            ? http.HTTPBody.length
            : (parts.count >= 2 ? [parts[1] lengthOfBytesUsingEncoding:NSUTF8StringEncoding] : 0);
    if (![JCORENetworkReachabilityManager sharedManager].isReachable) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:HttpSisFile
             function:__PRETTY_FUNCTION__
                 line:292
                  tag:nil
               format:@"http sis: network is not Reachable, return"];
        if (result)
            result(NO, nil);
        return;
    }
    [JCORENetDataManager startHttpMoniter:base request_byte:(long long)bytes];
    JCOREURLSessionController *session = [[JCOREURLSessionController alloc] init];
    [session sendRequest:[request httpRequest]
                requestId:identifier
        completionHandler:^(id task, id requestId, NSData *data, NSError *error) {
          [JCORENetDataManager endHttpMoniter:base response_byte:(long long)data.length];
          id response = [task response];
          if ([response isKindOfClass:NSHTTPURLResponse.class] &&
              [(NSHTTPURLResponse *)[(NSURLSessionTask *)task response] statusCode] == 200) {
              if (result)
                  result(YES, data);
          } else if (result)
              result(NO, nil);
          [session invalidate];
        }];
    self.requesting = YES;
}
@end
