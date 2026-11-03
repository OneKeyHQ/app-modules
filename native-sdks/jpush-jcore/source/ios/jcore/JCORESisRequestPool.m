#import "JCORESisRequestPool.h"
#import "JCOREAddress.h"
#import "JCOREAddressAnalysisController.h"
#import "JCOREIpvxHelper.h"
#import "JCORELog.h"
#import "JCORESupersetManager.h"
#import "JCOREUdpSis.h"
#define SisPoolFile                                                                                          \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Sis/JCORESisRequestPool.m"
@implementation JCORESisRequestPoolModel
- (NSString *)description {
    return [NSString
        stringWithFormat:@"isDomainString:%d, domain:%@, ip:%@", self.isDomainString, self.domain, self.ip];
}
@end
@implementation JCORESisRequestPool
- (instancetype)init {
    self = [super init];
    if (self) {
        _addressSisArray = [NSMutableArray array];
        _poolModelArray = [NSMutableArray array];
    }
    return self;
}
- (void)requestWithDomains:(id)domains completion:(JCORESisPoolCompletion)completion {
    if (completion && [domains count]) {
        [self endCurrentRequest];
        self.Completion = [completion copy];
        [_poolModelArray removeAllObjects];
        [_poolModelArray addObjectsFromArray:domains];
        _requestingNum = _poolModelArray.count;
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:SisPoolFile
             function:__PRETTY_FUNCTION__
                 line:68
                  tag:nil
               format:@"open a sis request pool:%@", [domains description]];
        [self sisRequest];
        [self startComplexTimer];
    }
}
- (void)endCurrentRequest {
    [self stopComplexTimer];
    _Completion = nil;
    _requestingNum = 0;
    while (_addressSisArray.count) {
        id sis = _addressSisArray.firstObject;
        [_addressSisArray removeObject:sis];
        [sis reset];
    }
    [_poolModelArray removeAllObjects];
}
- (void)sisRequest {
    if (!_poolModelArray.count) {
        [self stopComplexTimer];
        return;
    }
    JCORESisRequestPoolModel *model = _poolModelArray.firstObject;
    [_poolModelArray removeObject:model];
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:SisPoolFile
         function:__PRETTY_FUNCTION__
             line:102
              tag:nil
           format:@"start a request in sis request pool: %@", [model description]];
    NSArray *addresses;
    unsigned int port;
    if (!model.isDomainString) {
        JCOREAddress *address = [[JCOREAddress alloc] initWithString:model.ip];
        addresses = address.host ? [self getAddressFromDNS:address.host] : nil;
        port = address.port;
    } else {
        addresses = [self getAddressFromDNS:model.domain];
        port = 0;
    }
    if (!addresses) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:SisPoolFile
             function:__PRETTY_FUNCTION__
                 line:119
                  tag:nil
               format:@"%@ Dns failed", [model description]];
        _requestingNum--;
        [self callBackSisRequestCompletionFailure:nil poolModel:nil];
        return;
    }
    _requestingNum = _requestingNum + addresses.count - 1;
    for (id host in addresses) {
        unsigned short monitoredPort = (unsigned short)port;
        if (!monitoredPort)
            monitoredPort = 19000;
        NSString *monitor = [JCORESupersetManager startConnectionMoniter:@"NET_SIS"
                                                                    host:host
                                                                    port:monitoredPort];
        __weak typeof(self) weakSelf = self;
        JCOREUdpSis *sis = [[JCOREUdpSis alloc] init];
        [self.addressSisArray addObject:sis];
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:SisPoolFile
             function:__PRETTY_FUNCTION__
                 line:134
                  tag:nil
               format:@"send udp sis request, host:%@, port:%d", host, monitoredPort];
        [sis sendSisRequest:host
                       port:(unsigned short)port
                 completion:^(NSArray *result, NSError *error) {
                   __strong typeof(weakSelf) target = weakSelf;
                   target.requestingNum = target.requestingNum - 1;
                   if (!error && [result count]) {
                       [JCORESupersetManager endConnectionMoniter:monitor error_code:0 status_code:0];
                       [JCORELog log:YES
                               level:0x3ff
                                flag:0x100
                             context:0
                                file:SisPoolFile
                            function:"-[JCORESisRequestPool sisRequest]_block_invoke"
                                line:141
                                 tag:nil
                              format:@"send udp sis request success, host:%@, port:%d, result:%@", host,
                                     (unsigned short)port, [result description]];
                       [target callBackSisRequestCompletionSuccess:result];
                   } else {
                       [JCORESupersetManager endConnectionMoniter:monitor error_code:-1 status_code:0];
                       [JCORELog log:YES
                               level:0x3ff
                                flag:0x100
                             context:0
                                file:SisPoolFile
                            function:"-[JCORESisRequestPool sisRequest]_block_invoke"
                                line:146
                                 tag:nil
                              format:@"send udp sis request fail, host:%@, port:%d, error:%@", host,
                                     (unsigned short)port, [error description]];
                       [target removeDNSCacheWithDomain:host];
                       [target callBackSisRequestCompletionFailure:sis poolModel:model];
                   }
                 }];
    }
}
- (void)callBackSisRequestCompletionSuccess:(id)addresses {
    if ([addresses isKindOfClass:NSArray.class] && [addresses count]) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:SisPoolFile
             function:__PRETTY_FUNCTION__
                 line:159
                  tag:nil
               format:@"sis request pool success, result:%@", [addresses description]];
        if (self.Completion) {
            self.Completion(addresses, YES, nil);
            self.Completion = nil;
        }
        [self endCurrentRequest];
    }
}
- (void)callBackSisRequestCompletionFailure:(JCOREUdpSis *)sis poolModel:(id)model {
    if (sis) {
        [self.addressSisArray removeObject:sis];
        [sis reset];
    }
    if (_requestingNum == 0) {
        if (self.Completion) {
            [JCORELog log:YES
                    level:0x3ff
                     flag:0x100
                  context:0
                     file:SisPoolFile
                 function:__PRETTY_FUNCTION__
                     line:184
                      tag:nil
                   format:@"all request in sis request pool failed"];
            NSError *error =
                [NSError errorWithDomain:@"sis request"
                                    code:-1
                                userInfo:@{NSLocalizedDescriptionKey : @"JCORESisRequestPool failure"}];
            self.Completion(nil, YES, error);
            self.Completion = nil;
        }
        [self endCurrentRequest];
    } else
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:SisPoolFile
             function:__PRETTY_FUNCTION__
                 line:178
                  tag:nil
               format:@"Request not completely failed"];
}
- (void)startComplexTimer {
    [self stopComplexTimer];
    _complexTimer = [NSTimer scheduledTimerWithTimeInterval:2
                                                     target:self
                                                   selector:@selector(sisRequest)
                                                   userInfo:nil
                                                    repeats:YES];
}
- (void)stopComplexTimer {
    if (_complexTimer.isValid)
        [_complexTimer invalidate];
    _complexTimer = nil;
}
- (NSArray *)getAddressFromDNS:(id)domain {
    id result = [[JCOREAddressAnalysisController instance] getAddressFromDNS:domain timeout:2];
    if (![result isKindOfClass:NSDictionary.class] || ![result count])
        return nil;
    id ipv4 = result[JCORE_IPV4_Key];
    id ipv6 = result[JCORE_IPV6_Key];
    if (ipv4 && ![ipv4 isKindOfClass:NSArray.class])
        return nil;
    if (ipv6 && ![ipv6 isKindOfClass:NSArray.class])
        return nil;
    if (![ipv6 count] && ![ipv4 count])
        return nil;
    ipv4 = [self randamArray:ipv4];
    ipv6 = [self randamArray:ipv6];
    long long type = [JCOREIpvxHelper instance].ipvxType;
    if (type == 4)
        return [ipv6 count] ? @[ [ipv6 firstObject] ] : nil;
    if (type == 3)
        return [ipv4 count] ? @[ [ipv4 firstObject] ] : nil;
    NSMutableArray *ordered = [NSMutableArray array];
    if (type == 2) {
        if ([ipv6 count])
            [ordered addObject:[ipv6 firstObject]];
        if ([ipv4 count])
            [ordered addObject:[ipv4 firstObject]];
    } else {
        if ([ipv4 count])
            [ordered addObject:[ipv4 firstObject]];
        if ([ipv6 count])
            [ordered addObject:[ipv6 firstObject]];
    }
    return ordered.count ? [ordered copy] : nil;
}
- (void)removeDNSCacheWithDomain:(id)domain {
    [[JCOREAddressAnalysisController instance] removeDNSCacheWithDomain:domain];
}
- (NSArray *)randamArray:(id)addresses {
    if ([addresses count] > 1)
        return [addresses sortedArrayUsingComparator:^NSComparisonResult(NSString *left, NSString *right) {
          if (arc4random_uniform(2))
              return [left compare:right];
          return [right compare:left];
        }];
    return addresses;
}
@end
