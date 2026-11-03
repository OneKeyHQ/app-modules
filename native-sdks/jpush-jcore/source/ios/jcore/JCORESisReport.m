#import "JCORESisReport.h"
#import "JCOREAddress.h"
#import "JCOREBusinessStorage.h"
#import "JCOREClientInfo.h"
#import "JCOREDataSecurity.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import <arpa/inet.h>
#define SisReportFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Sis/JCORESisReport.m"
@implementation JCORESisReportModel
- (id)copyWithZone:(NSZone *)zone {
    JCORESisReportModel *copy = [[[self class] allocWithZone:zone] init];
    copy->_addr = [_addr copy];
    copy->_type = _type;
    copy->_uid = _uid;
    copy->_net = _net;
    copy->_rtime = _rtime;
    copy->_interval = _interval;
    copy->_lat = _lat;
    copy->_lng = _lng;
    copy->_ltime = _ltime;
    copy->_code = _code;
    copy->_appkey = _appkey;
    return copy;
}
- (NSMutableDictionary *)info {
    NSMutableDictionary *content = [NSMutableDictionary dictionary];
    if ([self.addr isKindOfClass:NSString.class])
        [content setObject:self.addr forKey:@"addr"];
    [content setObject:@(self.type) forKey:@"type"];
    [content setObject:@(self.uid) forKey:@"uid"];
    [content setObject:@(self.net) forKey:@"net"];
    [content setObject:@(self.rtime) forKey:@"rtime"];
    [content setObject:@(self.interval) forKey:@"interval"];
    [content setObject:@(self.lat) forKey:@"lat"];
    [content setObject:@(self.ltime) forKey:@"ltime"];
    [content setObject:@(self.lng) forKey:@"lng"];
    [content setObject:@(self.code) forKey:@"code"];
    [content setObject:self.appkey forKey:@"appkey"];
    return content;
}
@end
@implementation JCORESisReport
@dynamic hash, superclass, description, debugDescription;
+ (instancetype)shared {
    static JCORESisReport *report;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
      report = [[JCORESisReport alloc] init];
    });
    return report;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        _sisReportDataDic = [NSMutableDictionary dictionary];
        _connReportDataDic = [NSMutableDictionary dictionary];
        _tcpReportDataDic = [NSMutableDictionary dictionary];
    }
    return self;
}
- (void)addSisReportData:(unsigned char)type ip:(id)ip port:(unsigned short)port {
    NSMutableDictionary *container = nil;
    switch (type) {
    case 1:
        container = _sisReportDataDic;
        break;
    case 2:
        container = _connReportDataDic;
        break;
    case 3:
        container = _tcpReportDataDic;
        break;
    default:
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:109
                  tag:nil
               format:@"unknown report type"];
        return;
    }
    JCORESisReportModel *model = [[JCORESisReportModel alloc] init];
    model.appkey = [JCOREClientInfo appKey] ?: @"";
    model.type = type;
    model.net = [JCOREUtilities ctGenerationNumber];
    model.uid = [JCOREClientInfo uid];
    model.addr = [NSString stringWithFormat:@"%@:%d", ip, port];
    model.rtime = (unsigned long long)(long)NSDate.date.timeIntervalSince1970;
    model.interval = 0;
    id location = [JCOREBusinessStorage getLocation];
    if (![location isKindOfClass:NSDictionary.class] || ![location count]) {
        model.lat = 0;
        model.lng = 0;
        model.ltime = 0;
    } else {
        model.lat = [[location valueForKey:@"lat"] doubleValue];
        model.lng = [[location valueForKey:@"lng"] doubleValue];
        model.ltime = [[location valueForKey:@"itime"] unsignedLongLongValue];
    }
    model.code = 2;
    [container setObject:model forKey:[NSString stringWithFormat:@"%@:%d", ip, port]];
}
- (void)updateSisReportDataWithIp:(id)ip
                             port:(unsigned short)port
                             code:(long long)code
                             type:(unsigned char)type {
    NSMutableDictionary *container = nil;
    switch (type) {
    case 1:
        container = _sisReportDataDic;
        break;
    case 2:
        container = _connReportDataDic;
        break;
    case 3:
        container = _tcpReportDataDic;
        break;
    default:
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:151
                  tag:nil
               format:@"unknown report type"];
        return;
    }
    id model = container[[NSString stringWithFormat:@"%@:%d", ip, port]];
    if (model) {
        NSDate *start = [NSDate dateWithTimeIntervalSince1970:(double)[(JCORESisReportModel *)model rtime]];
        [(JCORESisReportModel *)model
            setInterval:(unsigned long long)(long)[NSDate.date timeIntervalSinceDate:start]];
        [(JCORESisReportModel *)model setCode:code];
        if (code) {
            id stored = [JCOREBusinessStorage getSisReportList];
            NSMutableArray *list;
            if (![stored isKindOfClass:NSArray.class] || ![stored count])
                list = [NSMutableArray array];
            else
                list = [NSMutableArray arrayWithArray:stored];
            [list addObject:[model info]];
            if (list.count > 30)
                [list removeObjectAtIndex:0];
            [JCOREBusinessStorage setSisReportList:list];
        }
    }
}
- (void)reportDGDataWithHost:(id)host port:(unsigned short)port {
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:SisReportFile
         function:__PRETTY_FUNCTION__
             line:185
              tag:nil
           format:@"Action - %@", NSStringFromSelector(_cmd)];
    double previous = [[JCOREBusinessStorage getLastSisReportTime] doubleValue];
    double now = [JCOREUtilities currentTimestamp];
    if (!(now - previous >= 3600)) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:189
                  tag:nil
               format:@"cancel report DGData, interval is too short"];
        return;
    }
    [self resetUdpSocket];
    id data = [self reportData];
    if (![data isKindOfClass:NSData.class]) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:197
                  tag:nil
               format:@"no DG Data, return"];
        return;
    }
    JCOREAddress *address =
        [[JCOREAddress alloc] initWithString:[NSString stringWithFormat:@"%@:%d", host, port]];
    JCORESocketConfig *config = [[JCORESocketConfig alloc] init];
    config.host = address.host;
    config.port = address.port;
    config.ipType = address.ipType;
    __weak typeof(self) weakSelf = self;
    JCORESocketData *packet = [[JCORESocketData alloc] init];
    packet.data = data;
    packet.handler = ^(BOOL success) {
      if (!success)
          [weakSelf resetUdpSocket];
    };
    packet.timeout = 4;
    JCORENetwork *socket = [JCORENetwork network:1];
    [socket setConfig:config delegate:self];
    [socket connect];
    [socket sendData:packet];
    _udpSocket = socket;
}
- (NSData *)reportData {
    id list = [JCOREBusinessStorage getSisReportList];
    uint32_t key = arc4random();
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:SisReportFile
         function:__PRETTY_FUNCTION__
             line:231
              tag:nil
           format:@"DG report list:%@", list];
    if ([list isKindOfClass:NSArray.class] && [list count]) {
        _sendingSisReportData = [NSArray arrayWithArray:list];
        return [self assemble:list magic:'D' key:key];
    }
    return nil;
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
- (void)resetUdpSocket {
    if (_udpSocket) {
        [_udpSocket disconect];
        [_udpSocket remove];
    }
}
- (void)parse:(id)data ip:(id)ip port:(unsigned short)port {
    [JCORELog log:YES
            level:0x3ff
             flag:8
          context:0
             file:SisReportFile
         function:__PRETTY_FUNCTION__
             line:282
              tag:nil
           format:@"Action - parse"];
    [self didReceivedSisData:data];
    [self resetUdpSocket];
}
- (id)didReceivedSisData:(id)data {
    if (!data) {
        [JCORELog log:NO
                level:0x3ff
                 flag:1
              context:0
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:290
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
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:300
                  tag:nil
               format:@"DG response error, error length"];
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
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:308
                  tag:nil
               format:@"DG response error, body is null"];
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
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:316
                  tag:nil
               format:@"DG response error, decrypt failed"];
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
                     file:SisReportFile
                 function:__PRETTY_FUNCTION__
                     line:331
                      tag:nil
                   format:@"DG report success"];
            id list = [[JCOREBusinessStorage getSisReportList] mutableCopy];
            if ([list isKindOfClass:NSMutableArray.class] && [list count])
                for (id item in _sendingSisReportData)
                    [list removeObject:item];
            [JCOREBusinessStorage setSisReportList:list];
            [JCOREBusinessStorage setLastSisReportTime:@([JCOREUtilities currentTimestamp])];
        } else
            [JCORELog log:NO
                    level:0x3ff
                     flag:0x20
                  context:0
                     file:SisReportFile
                 function:__PRETTY_FUNCTION__
                     line:346
                      tag:nil
                   format:@"DG report failed:%d", code];
        _sendingSisReportData = nil;
        return @0;
    }
    if (!plain.length) {
        [JCORELog log:YES
                level:0x3ff
                 flag:2
              context:0
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:352
                  tag:nil
               format:@"DG report error, unpack failed"];
        return [NSNumber numberWithInteger:4];
    }
    return [plain copy];
}
- (void)socket:(id)socket recvData:(id)data handler:(void (^)(BOOL))handler {
    if (_udpSocket != socket)
        return;
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:SisReportFile
         function:__PRETTY_FUNCTION__
             line:366
              tag:nil
           format:@"DG report response did receive data at udp %@", socket];
    [self parse:data ip:_udpSocket.config.host port:(unsigned short)_udpSocket.config.port];
}
- (void)socket:(id)socket sentData:(id)data {
    if (_udpSocket == socket)
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:375
                  tag:nil
               format:@"DG report data sent with at udp %@", socket];
}
- (void)socket:(id)socket noRecvData:(id)data {
    if (_udpSocket == socket)
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:383
                  tag:nil
               format:@"DG report response did not receive data at udp %@", socket];
}
- (void)socket:(id)socket unsentData:(id)data {
    if (_udpSocket == socket)
        [JCORELog log:NO
                level:0x3ff
                 flag:0x20
              context:0
                 file:SisReportFile
             function:__PRETTY_FUNCTION__
                 line:390
                  tag:nil
               format:@"DG report data did not sent with at udp %@", socket];
}
@end
