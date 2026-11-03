#import "JCOREUdpSis.h"
#import "JCOREAddress.h"
#import "JCOREBusinessStorage.h"
#import "JCORECertController.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCOREIntegrate.h"
#import "JCORELog.h"
#import "JCORENetDataManager.h"
#import "JCORESisReport.h"
#import "JCOREUtilities.h"
#import "RecoveredCoreService.h"
#import <arpa/inet.h>
#define UdpSisFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Sis/JCOREUdpSis.m"
@implementation JCOREUdpSis
@dynamic hash, superclass, description, debugDescription;
- (void)sendSisRequest:(id)host port:(unsigned short)port completion:(JCOREUdpSisCompletion)completion {
    if (!completion)
        return;
    _sisCompletion = [completion copy];
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:UdpSisFile
         function:__PRETTY_FUNCTION__
             line:80
              tag:nil
           format:@"Action - %@", NSStringFromSelector(_cmd)];
    if (![host length]) {
        [JCORELog log:YES
                level:0x3ff
                 flag:8
              context:0
                 file:UdpSisFile
             function:__PRETTY_FUNCTION__
                 line:82
                  tag:nil
               format:@"send sis request cancel, invalid address"];
        [self callbackSisCompletion:nil];
        return;
    }
    [self reset];
    if (!port) {
        port = 19000;
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:UdpSisFile
             function:__PRETTY_FUNCTION__
                 line:91
                  tag:nil
               format:@"port is invalid, use defaultPort:%d", port];
    }
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:UdpSisFile
         function:__PRETTY_FUNCTION__
             line:94
              tag:nil
           format:@"request sis host:%@  port:%d", host, port];
    [[JCORESisReport shared] addSisReportData:1 ip:host port:port];
    NSData *data = [self requestData];
    [JCORENetDataManager startSocketMoniter:0 host:host port:port request_byte:(long long)data.length];
    JCOREAddress *address =
        [[JCOREAddress alloc] initWithString:[NSString stringWithFormat:@"%@:%d", host, port]];
    JCORESocketConfig *config = [[JCORESocketConfig alloc] init];
    config.host = address.host;
    config.port = address.port;
    config.ipType = address.ipType;
    _host = [config.host copy];
    _port = config.port;
    __weak typeof(self) weakSelf = self;
    JCORESocketData *packet = [[JCORESocketData alloc] init];
    packet.data = data;
    packet.handler = ^(BOOL success) {
      if (success)
          return;
      [[JCORESisReport shared] updateSisReportDataWithIp:config.host
                                                    port:(unsigned short)config.port
                                                    code:2
                                                    type:1];
      [weakSelf callbackSisCompletion:nil];
      [weakSelf reset];
    };
    packet.timeout = 4;
    JCORENetwork *socket = [JCORENetwork network:1];
    [socket setConfig:config delegate:self];
    [socket connect];
    [socket sendData:packet];
    _udpSocket = socket;
    [self startSisRequestTimer:4];
}
- (void)startSisRequestTimer:(double)timeout {
    [self stopSisRequestTimer];
    _sisRequestTimer = [NSTimer scheduledTimerWithTimeInterval:timeout
                                                        target:self
                                                      selector:@selector(sisRequestTimeOut)
                                                      userInfo:nil
                                                       repeats:NO];
}
- (void)sisRequestTimeOut {
    if (_host && _port) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:UdpSisFile
             function:__PRETTY_FUNCTION__
                 line:145
                  tag:nil
               format:@"sisRequestTimeOut %@:%d", _host, _port];
        [[JCORESisReport shared] updateSisReportDataWithIp:_host port:(unsigned short)_port code:2 type:1];
        [self reset];
    }
    [self callbackSisCompletion:nil];
}
- (void)stopSisRequestTimer {
    if (_sisRequestTimer.isValid)
        [_sisRequestTimer invalidate];
    _sisRequestTimer = nil;
}
- (void)dealloc {
    self.sisCompletion = nil;
    [self stopSisRequestTimer];
}
- (void)callbackSisCompletion:(id)addresses {
    if (self.sisCompletion) {
        [self stopSisRequestTimer];
        if (![addresses count]) {
            NSError *error = [NSError errorWithDomain:@"JCORETcpAddressSis"
                                                 code:-1
                                             userInfo:@{NSLocalizedDescriptionKey : @"JCORETcpAddressSis"}];
            [JCORELog log:YES
                    level:0x3ff
                     flag:0x100
                  context:0
                     file:UdpSisFile
                 function:__PRETTY_FUNCTION__
                     line:175
                      tag:nil
                   format:@"udp sis failed, error:%@", [error description]];
            self.sisCompletion(nil, error);
        } else {
            [JCORELog log:YES
                    level:0x3ff
                     flag:0x100
                  context:0
                     file:UdpSisFile
                 function:__PRETTY_FUNCTION__
                     line:171
                      tag:nil
                   format:@"udp sis success, result:%@", [addresses description]];
            self.sisCompletion(addresses, nil);
        }
        self.sisCompletion = nil;
    }
}
- (void)resetUdpSocket {
    if (_udpSocket) {
        [_udpSocket disconect];
        [_udpSocket remove];
    }
}
- (void)reset {
    [self stopSisRequestTimer];
    [self resetUdpSocket];
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
    NSArray *failures = [[NSSet setWithArray:[JCOREBusinessStorage getFailedConIps]] allObjects];
    NSMutableDictionary *content = [NSMutableDictionary dictionaryWithDictionary:@{
        @"type" : @(network),
        @"opera" : carrier ?: @"",
        @"uid" : @([JCOREClientInfo uid]),
        @"appkey" : [JCOREClientInfo appKey] ?: @"",
        @"sdkver" : [JCOREIntegrate jcoreDisplayVersion],
        @"lat" : lat ?: @0,
        @"lng" : lng ?: @0,
        @"time" : time ?: @0,
        @"platform" : @1,
        @"fail_ips" : failures ?: @[],
        @"ips_flag" : [NSNumber numberWithInteger:0],
        @"report_flag" : [NSNumber numberWithInteger:0]
    }];
    id version = [JCOREBusinessStorage getLocalCerVerion];
    if (!version || ![version isKindOfClass:NSNumber.class])
        version = @1;
    [content setValue:version forKey:@"cert_ver"];
    return content;
}
- (NSData *)requestData {
    uint32_t key = arc4random();
    id content = [self sisRequestInfo];
    BOOL messaging = [JCOREIntegrate sdkIsExistOfIndex:3];
    return [self assemble:content magic:messaging ? 'M' : 'U' key:key];
}
- (NSMutableData *)assemble:(id)data magic:(char)magic key:(unsigned int)key {
    if (!data)
        return nil;
    NSData *json = [JCOREUtilities jsonToData:data];
    NSData *compressed = [JCOREUtilities gzip:json];
    NSString *secret = [JCOREDataSecurity encryptSisKeyWithSeed:key & 0xffffff];
    NSString *iv = [secret substringWithRange:NSMakeRange(0, 16)];
    NSData *encrypted = [JCOREDataSecurity aes256EncryptData:compressed withKey:secret iv:iv.UTF8String];
    struct {
        uint16_t length;
        char magic;
        char marker;
        uint32_t key;
    } header = {htons((uint16_t)(encrypted.length + 10)), magic, 'G', htonl((key & 0xffffff) | 0x11000000)};
    NSMutableData *result = [NSMutableData dataWithBytes:&header length:8];
    uint16_t length = htons((uint16_t)compressed.length);
    [result appendBytes:&length length:2];
    [result appendData:encrypted];
    return result;
}
- (id)didReceivedSisData:(id)data {
    if (!data) {
        [JCORELog log:NO
                level:0x3ff
                 flag:1
              context:0
                 file:UdpSisFile
             function:__PRETTY_FUNCTION__
                 line:298
                  tag:nil
               format:@"sis result null"];
        return [NSNumber numberWithInteger:3];
    }
    struct {
        uint16_t length;
        uint16_t magic;
        uint32_t key;
    } header;
    [data getBytes:&header length:8];
    uint32_t rawKey = header.key;
    uint16_t total = ntohs(header.length);
    uint32_t key = ntohl(header.key) & 0xffffff;
    if (total <= 9 || total > [data length]) {
        [JCORELog log:NO
                level:0x3ff
                 flag:1
              context:0
                 file:UdpSisFile
             function:__PRETTY_FUNCTION__
                 line:308
                  tag:nil
               format:@"sis result error, error length"];
        return [NSNumber numberWithInteger:3];
    }
    NSData *body = [data subdataWithRange:NSMakeRange(8, total - 8)];
    uint16_t declared;
    [body getBytes:&declared length:2];
    if (!declared) {
        [JCORELog log:NO
                level:0x3ff
                 flag:1
              context:0
                 file:UdpSisFile
             function:__PRETTY_FUNCTION__
                 line:316
                  tag:nil
               format:@"sis result error, body is null"];
        return [NSNumber numberWithInteger:3];
    }
    body = [body subdataWithRange:NSMakeRange(2, body.length - 2)];
    NSString *secret = [JCOREDataSecurity encryptSisKeyWithSeed:key];
    NSString *iv = [secret substringWithRange:NSMakeRange(0, 16)];
    NSData *plain = [JCOREDataSecurity aes256DecryptData:body withKey:secret iv:iv.UTF8String];
    if (!plain.length) {
        [JCORELog log:NO
                level:0x3ff
                 flag:1
              context:0
                 file:UdpSisFile
             function:__PRETTY_FUNCTION__
                 line:324
                  tag:nil
               format:@"sis result error, decrypt failed"];
        return [NSNumber numberWithInteger:4];
    }
    uint16_t expected = ntohs(declared);
    NSUInteger used = expected;
    if (plain.length < expected)
        used = plain.length & 0xffff;
    plain = [plain subdataWithRange:NSMakeRange(0, used)];
    if (rawKey & 1)
        plain = [JCOREUtilities unzip:plain];
    if (header.magic == 0x4744) {
        short code;
        [plain getBytes:&code length:2];
        if (code == 0) {
            [JCORELog log:YES
                    level:0x3ff
                     flag:0x100
                  context:0
                     file:UdpSisFile
                 function:__PRETTY_FUNCTION__
                     line:339
                      tag:nil
                   format:@"sis report success"];
            id list = [[JCOREBusinessStorage getSisReportList] mutableCopy];
            if ([list isKindOfClass:NSMutableArray.class] && [list count])
                for (id item in _sendingSisReportData)
                    [list removeObject:item];
            [JCOREBusinessStorage setSisReportList:list];
        } else
            [JCORELog log:NO
                    level:0x3ff
                     flag:0x20
                  context:0
                     file:UdpSisFile
                 function:__PRETTY_FUNCTION__
                     line:350
                      tag:nil
                   format:@"sis report failed:%d", code];
        _sendingSisReportData = nil;
        return @0;
    }
    if (!plain.length) {
        [JCORELog log:YES
                level:0x3ff
                 flag:2
              context:0
                 file:UdpSisFile
             function:__PRETTY_FUNCTION__
                 line:356
                  tag:nil
               format:@"sis result error, unpack failed"];
        return [NSNumber numberWithInteger:4];
    }
    return [plain copy];
}
- (void)parse:(id)data ip:(id)ip port:(unsigned short)port {
    [JCORELog log:YES
            level:0x3ff
             flag:8
          context:0
             file:UdpSisFile
         function:__PRETTY_FUNCTION__
             line:373
              tag:nil
           format:@"Action - parse"];
    id value = [self didReceivedSisData:data];
    if ([value isKindOfClass:NSNumber.class]) {
        if ([value intValue] != 0)
            [[JCORESisReport shared] updateSisReportDataWithIp:ip
                                                          port:port
                                                          code:(long)[value intValue]
                                                          type:1];
        return;
    }
    id content = [JCOREUtilities dataToJSON:value];
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:UdpSisFile
         function:__PRETTY_FUNCTION__
             line:382
              tag:nil
           format:@"get sis result from server:%@", content];
    NSArray *addresses = nil;
    if (![content count])
        [[JCORESisReport shared] updateSisReportDataWithIp:ip port:port code:5 type:1];
    else {
        [[JCORESisReport shared] updateSisReportDataWithIp:ip port:port code:0 type:1];
        {
            id list = content[@"ssl_user"];
            if ([list isKindOfClass:NSArray.class] && [list count]) {
                NSMutableArray *result = [NSMutableArray array];
                for (id item in list) {
                    if ([item isKindOfClass:NSString.class] && [item length] &&
                        [JCOREAddress isValidIpString:item]) {
                        id base = [JCOREBusinessStorage getUserAddress][0];
                        id merged = [JCOREAddress mergeURLStringWithIp:item localURL:base SSL:YES];
                        if (merged)
                            [result addObject:merged];
                    }
                }
                if (result.count)
                    [JCOREBusinessStorage setGuaranteeSSLUserReportAddress:[result copy]];
            }
        }
        {
            id list = content[@"user"];
            if ([list isKindOfClass:NSArray.class] && [list count]) {
                NSMutableArray *result = [NSMutableArray array];
                for (id item in list) {
                    if ([item isKindOfClass:NSString.class] && [item length] &&
                        [JCOREAddress isValidIpString:item]) {
                        id base = [JCOREBusinessStorage getUserAddress][0];
                        id merged = [JCOREAddress mergeURLStringWithIp:item localURL:base SSL:NO];
                        if (merged)
                            [result addObject:merged];
                    }
                }
                if (result.count)
                    [JCOREBusinessStorage setGuaranteeUserReportAddress:[result copy]];
            }
        }
        BOOL needReport = [content[@"data_report"] boolValue];
        [JCOREBusinessStorage setNeedDataReport:needReport];
        if (needReport)
            [[JCORESisReport shared] reportDGDataWithHost:ip port:port];
        {
            id list = content[@"sis_ips"];
            if ([list isKindOfClass:NSArray.class] && [list count]) {
                NSMutableArray *result = [NSMutableArray array];
                for (id item in list)
                    if ([item isKindOfClass:NSString.class] && [item length] &&
                        [JCOREAddress isValidIpString:item])
                        [result addObject:item];
                if (result.count)
                    [JCOREBusinessStorage setSisIpsFromServer:[result copy]];
            }
        }
        {
            id list = content[@"https_report"];
            if ([list isKindOfClass:NSArray.class] && [list count]) {
                NSMutableArray *result = [NSMutableArray array];
                for (id item in list) {
                    if ([item isKindOfClass:NSString.class] && [item length] &&
                        [JCOREAddress isValidIpString:item]) {
                        id base = [JCOREBusinessStorage getReportAddress][0];
                        id merged = [JCOREAddress mergeURLStringWithIp:item localURL:base SSL:YES];
                        if (merged)
                            [result addObject:merged];
                    }
                }
                if (result.count)
                    [JCOREBusinessStorage setGuaranteeHttpsReportAddress:[result copy]];
            }
        }
        {
            id list = content[@"http_report"];
            if ([list isKindOfClass:NSArray.class] && [list count]) {
                NSMutableArray *result = [NSMutableArray array];
                for (id item in list) {
                    if ([item isKindOfClass:NSString.class] && [item length] &&
                        [JCOREAddress isValidIpString:item]) {
                        id base = [JCOREBusinessStorage getReportAddress][0];
                        id merged = [JCOREAddress mergeURLStringWithIp:item localURL:base SSL:NO];
                        if (merged)
                            [result addObject:merged];
                    }
                }
                if (result.count)
                    [JCOREBusinessStorage setGuaranteeHttpReportAddress:[result copy]];
            }
        }
        id version = content[@"cert_ver"];
        if (version && [version isKindOfClass:NSNumber.class]) {
            [JCOREBusinessStorage setServerCerVesion:version];
            id local = [JCOREBusinessStorage getLocalCerVerion];
            if (!local || ![local isKindOfClass:NSNumber.class])
                [JCOREService setCanStartTcpSSL:NO];
            else if ([local isKindOfClass:NSNumber.class] && [local intValue] != [version intValue])
                [JCOREService setCanStartTcpSSL:NO];
            [JCORECertController pullCertWithServerVersion:version];
        }
        id ips = content[[JCOREService canStartTcpSSL] ? @"ssl_ips" : @"ips"];
        if ([ips isKindOfClass:NSArray.class] && [ips count]) {
            NSMutableArray *result = [NSMutableArray array];
            for (id item in ips)
                if ([item isKindOfClass:NSString.class] && [item length] &&
                    [JCOREAddress isValidIpString:item])
                    [result addObject:item];
            if (result.count)
                addresses = [result copy];
        }
        id tcp = content[@"tcp_report"];
        if ([tcp isKindOfClass:NSDictionary.class] && [tcp count]) {
            NSMutableDictionary *mapping = [[NSMutableDictionary alloc] init];
            for (id key in [tcp allKeys]) {
                if ([key isKindOfClass:NSString.class] && [key length]) {
                    id list = [tcp objectForKey:key];
                    if ([list isKindOfClass:NSArray.class] && [list count]) {
                        NSMutableArray *result = [[NSMutableArray alloc] init];
                        for (id item in list)
                            if ([item isKindOfClass:NSString.class] && [item length] &&
                                [JCOREAddress isValidIpString:item])
                                [result addObject:item];
                        if (result.count)
                            [mapping setObject:[result copy] forKey:key];
                    }
                }
            }
            if (mapping.count)
                [JCOREBusinessStorage setTcpReportAddressDict:[mapping copy]];
        }
        if ([ip length] && port) {
            NSString *address = [[NSString alloc] initWithFormat:@"%@:%hu", ip, port];
            if ([JCOREAddress isValidIpString:address]) {
                NSMutableArray *successful =
                    [NSMutableArray arrayWithArray:[JCOREBusinessStorage getLastSuccessSisIps]];
                if (![successful containsObject:address]) {
                    [successful addObject:address];
                    [JCOREBusinessStorage setLastSuccessSisIps:[successful copy]];
                }
            }
        }
    }
    [self callbackSisCompletion:addresses];
    [self reset];
    [JCOREBusinessStorage setFailedConIPs:nil];
}
- (void)unsentData:(id)data socket:(id)socket {
    if (_udpSocket != socket)
        return;
    [JCORELog log:NO
            level:0x3ff
             flag:0x20
          context:0
             file:UdpSisFile
         function:__PRETTY_FUNCTION__
             line:620
              tag:nil
           format:@"sis did not sent with at udp %@", socket];
    [self callbackSisCompletion:nil];
}
- (void)socket:(id)socket recvData:(id)data handler:(void (^)(BOOL))handler {
    if (_udpSocket != socket)
        return;
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:UdpSisFile
         function:__PRETTY_FUNCTION__
             line:629
              tag:nil
           format:@"sis did receive data at udp %@", socket];
    [JCORENetDataManager endSocketMoniter:0
                                     host:_udpSocket.config.host
                                     port:_udpSocket.config.port
                            response_byte:(long long)[data length]];
    [self parse:data ip:_udpSocket.config.host port:(unsigned short)_udpSocket.config.port];
}
- (void)socket:(id)socket sentData:(id)data {
    if (_udpSocket == socket)
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:UdpSisFile
             function:__PRETTY_FUNCTION__
                 line:640
                  tag:nil
               format:@"sis sent with at udp %@", socket];
}
- (void)socket:(id)socket noRecvData:(id)data {
    if (_udpSocket != socket)
        return;
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:UdpSisFile
         function:__PRETTY_FUNCTION__
             line:648
              tag:nil
           format:@"sis did not receive data at udp %@", socket];
    [[JCORESisReport shared] updateSisReportDataWithIp:_udpSocket.config.host
                                                  port:(unsigned short)_udpSocket.config.port
                                                  code:2
                                                  type:1];
    [self callbackSisCompletion:nil];
}
- (void)socket:(id)socket unsentData:(id)data {
    if (_udpSocket != socket)
        return;
    [JCORELog log:NO
            level:0x3ff
             flag:0x20
          context:0
             file:UdpSisFile
         function:__PRETTY_FUNCTION__
             line:657
              tag:nil
           format:@"sis did not sent with at udp %@", socket];
    [[JCORESisReport shared] updateSisReportDataWithIp:_udpSocket.config.host
                                                  port:(unsigned short)_udpSocket.config.port
                                                  code:2
                                                  type:1];
    [self callbackSisCompletion:nil];
}
@end
