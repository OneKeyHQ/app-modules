#import "JCORETcpReportTransport.h"
#import "JCORENetworkQueue.h"
#import "JCOREAddress.h"
#import "JCORENetDataManager.h"
#import "JCORETcpAddressBlackList.h"
#import "JCORELog.h"
#include <arpa/inet.h>
#import "JCORESisReport.h"
#define JCORETcpConnectorFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Controller/Network/JCORETcpReportController.m"
#define JCORETcpConnectorLog(sync, flagValue, sourceLine, ...) [JCORELog log:sync level:0x3ff flag:flagValue context:0 file:JCORETcpConnectorFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
@implementation JCORETcpReportConnector
@dynamic hash, superclass, description, debugDescription;
@synthesize network = _network, connectStatus = _connectStatus, tcpSendingQueue = _tcpSendingQueue, tcpBlockedQueue = _tcpBlockedQueue, address = _address, completionHandler = _completionHandler;
- (instancetype)initWithAddress:(id)address {
    if ((self = [super init])) {
        _address = address;
        JCOREAddress *parsed = [[JCOREAddress alloc] initWithString:address];
        _host = [parsed.host copy]; _port = parsed.port; _ipType = parsed.ipType;
        _tcpBlockedQueue = [[JCORENetworkQueue alloc] initWithMaxItem:1000 withKeyProperyName:@"rid"];
        _tcpSendingQueue = [[JCORENetworkQueue alloc] initWithMaxItem:1000 withKeyProperyName:@"rid"];
    }
    return self;
}
- (void)send:(id)request { if (self.connectStatus == 2) [self doSendTcpRequest:request]; else { [self.tcpBlockedQueue enqueue:request]; [self startTcpConnect]; } }
- (void)resetKeepTimer { [self invalidate]; _keepTimer = [NSTimer scheduledTimerWithTimeInterval:30 target:self selector:@selector(keepTimeout:) userInfo:nil repeats:NO]; }
- (void)invalidate { if (_keepTimer && _keepTimer.isValid) { [_keepTimer invalidate]; _keepTimer = nil; } }
- (void)keepTimeout:(id)timer { if (self.completionHandler) self.completionHandler(); }
- (void)startTcpConnect {
    if (_startConnecting) return;
    _startConnecting = YES;
    if (!_network) {
        _network = [JCORENetwork network:0];
        JCORESocketConfig *config = [[JCORESocketConfig alloc] init];
        config.host = _host; config.port = _port; config.ipType = _ipType;
        [_network setConfig:config delegate:self];
    }
    [_network connect];
}
- (void)doSendTcpRequest:(id)request {
    JCORETcpConnectorLog(YES, 8, 353, @"Action - doSendTcpReportRequest");
    NSData *data = [self requestData:request];
    if (!data) { [JCORETcpReportConnector errorCallBack:request code:2001 des:@"request packet data error"]; return; }
    [JCORENetDataManager startSocketMoniter:1 host:_host port:_port request_byte:(long long)data.length];
    JCORESocketData *socketData = [[JCORESocketData alloc] init];
    socketData.data = data;
    socketData.handler = ^(BOOL sent) {
        [self.tcpSendingQueue enqueue:request];
        if (!sent) JCORETcpConnectorLog(YES, 8, 368, @"当前网络未连接，发送请求失败");
    };
    [self.network sendData:socketData];
    [self resetKeepTimer];
}
- (void)reenqueueSendingRequests {
    JCORETcpConnectorLog(YES, 8, 381, @"Action - reenqueueSendingRequests");
    __weak JCORETcpReportConnector *weakSelf = self;
    [_tcpBlockedQueue copyQueue:_tcpSendingQueue withCondition:^BOOL(id request) {
        if (![request conformsToProtocol:@protocol(JCORETcpProtocol)]) { JCORETcpConnectorLog(YES, 0x100, 387, @"invalid request type, remove it from sendingQueue"); return NO; }
        long long count = [weakSelf.tcpBlockedQueue count];
        if (count > 1000) JCORETcpConnectorLog(YES, 8, 392, @"等待请求队列过多");
        return count < 1001;
    }];
}
- (NSData *)requestData:(id)request {
    NSMutableData *data = [[NSMutableData alloc] init];
    [data appendData:[request headData]];
    [data appendData:[request bodyData]];
    NSUInteger length = data.length;
    if ((unsigned short)length < 2) { JCORETcpConnectorLog(NO, 1, 412, @"report packet too short, %lu : %@", length, data); return nil; }
    unsigned short networkLength = htons((unsigned short)length);
    [data replaceBytesInRange:NSMakeRange(0, 2) withBytes:&networkLength];
    return [data copy];
}
+ (void)errorCallBack:(id)request code:(long long)code des:(id)description {
    if ([request respondsToSelector:@selector(completionHandler)] && [request completionHandler]) {
        NSError *error = [NSError errorWithDomain:@"JIGUANGTcpReportErrorDomain" code:(NSInteger)code userInfo:@{NSLocalizedDescriptionKey:description ?: @""}];
        ((RecoveryOpaqueCallback)[request completionHandler])(request, error);
    }
}
- (void)socket:(id)socket recvData:(id)data handler:(void (^)(BOOL))handler {
    [JCORENetDataManager endSocketMoniter:1 host:_host port:_port response_byte:(long long)[data length]];
    if (!data) [NSNotificationCenter.defaultCenter postNotificationName:@"kJCORETcpReportResponseNotification" object:nil];
    else [NSNotificationCenter.defaultCenter postNotificationName:@"kJCORETcpReportResponseNotification" object:nil userInfo:@{@"data":data}];
    if (handler) handler(YES);
}
- (void)socket:(id)socket sentData:(id)data {
    id printed = [data length] > 37 ? [data subdataWithRange:NSMakeRange(0, 37)] : data;
    JCORETcpConnectorLog(YES, 0x100, 448, @"tcp report didSendData: %@, length: %lu, address: %@:%d", printed, [data length], [(JCORESocketConfig *)[socket config] host], [(JCORESocketConfig *)[socket config] port]);
    if (![data isKindOfClass:NSData.class]) return;
    [JCORETcpReportMonitor monitor:data completion:^(unsigned short code, unsigned short rid, NSData *response) {
        id request = [self.tcpSendingQueue dequeueItemWithKey:@(rid)];
        if (code == 2002) {
            JCORETcpConnectorLog(YES, 0x40, 456, @"tcp report didSendData timeout");
            [JCORETcpReportConnector errorCallBack:request code:2002 des:@"tcp report timeout"];
        } else if ((code & 0xfffe) == 2004) {
            JCORETcpConnectorLog(YES, 0x40, 459, @"tcp report didSendData response error");
            [JCORETcpReportConnector errorCallBack:request code:code des:@"tcp report response error"];
            [JCORETcpAddressBlackList.shareInstance addToBlackList:self.address time:600];
            [JCORESisReport.shared addSisReportData:3 ip:self->_host port:(unsigned short)self->_port];
            [JCORESisReport.shared updateSisReportDataWithIp:self->_host port:(unsigned short)self->_port code:code == 2004 ? 1 : 2 type:3];
            if (self.completionHandler) self.completionHandler();
        } else if (!code) {
            if ([request respondsToSelector:@selector(completionHandler)] && [request completionHandler]) ((RecoveryOpaqueCallback)[request completionHandler])(response, nil);
        } else [JCORETcpReportConnector errorCallBack:request code:code des:@"tcp report failed"];
    }];
}
- (void)sendQueueEmpty:(id)socket {
    id request = [self.tcpBlockedQueue dequeue];
    if (request && [request conformsToProtocol:@protocol(JCORETcpProtocol)]) [self doSendTcpRequest:request];
    else { self.network.careAllDataSent = NO; [self resetKeepTimer]; }
}
- (void)socket:(id)socket connectStateChanged:(NSUInteger)state {
    if (_startConnecting && !state) {
        id request;
        while ((request = [self.tcpBlockedQueue dequeue])) [JCORETcpReportConnector errorCallBack:request code:2002 des:@"connect timeout"];
        if (self.completionHandler) self.completionHandler();
    }
    if (state != 1) _startConnecting = NO;
    self.connectStatus = state;
    JCORETcpConnectorLog(YES, 0x100, 512, @"socket connect:%@:%u connectStateChanged:%d", [(JCORESocketConfig *)[socket config] host], [(JCORESocketConfig *)[socket config] port], (int)state);
    if (state == 2) [self sendQueueEmpty:nil];
}
@end
