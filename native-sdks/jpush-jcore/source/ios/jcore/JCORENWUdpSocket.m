#import "JCORENWUdpSocket.h"
#import "JCORENetworkModels.h"
#import "JCOREQueueManager.h"
#import "JCORELog.h"
#include <errno.h>

#define JCORENWUdpFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Network/Model/JCORENWUdpSocket.m"
#define JCORENWUdpLog(sync, flags, sourceLine, ...) [JCORELog log:sync level:0x3ff flag:flags context:0 file:JCORENWUdpFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]

@implementation JCORENWUdpSocket
- (instancetype)init { if ((self = [super init])) { _isSetuped = NO; _ulDefaultReceiveBufferSize = 0x4000; } return self; }
- (void)connect { [super connect]; if (!_connection) [self setup]; }
- (void)disconect { [super disconect]; [self close]; }
- (BOOL)setup {
    if (_isSetuped) return NO;
    if (@available(iOS 12.0, *)) {
        _connectQueue = [JCOREQueueManager syncQueue];
        nw_parameters_t parameters = nw_parameters_create_secure_udp(NW_PARAMETERS_DISABLE_PROTOCOL, NW_PARAMETERS_DEFAULT_CONFIGURATION);
        NSString *port = [NSString stringWithFormat:@"%u", self.port];
        nw_endpoint_t endpoint = nw_endpoint_create_host(self.host.UTF8String, port.UTF8String);
        if (!endpoint) return NO;
        _connection = nw_connection_create(endpoint, parameters);
        if (!_connection) return NO;
        nw_connection_set_queue(_connection, _connectQueue);
        nw_connection_set_state_changed_handler(_connection, ^(nw_connection_state_t state, nw_error_t error) {
            if (state == nw_connection_state_ready) {
                JCORENWUdpLog(YES, 0x100, 158, @"nwsocket udp:connection is ready");
                [JCORENetworkController performSel:@selector(handleReady) target:self withObject:nil];
            } else if (state == nw_connection_state_cancelled) {
                [JCORENetworkController performSel:@selector(close) target:self withObject:nil];
                JCORENWUdpLog(YES, 0x100, 163, @"nwsocket udp:connection is cancelled");
            } else if (state == nw_connection_state_failed) {
                JCORENWUdpLog(YES, 0x100, 154, @"nwsocket udp:failed");
                [JCORENetworkController performSel:@selector(close) target:self withObject:nil];
            }
        });
        [self doRead];
        nw_connection_start(_connection);
        return YES;
    }
    return NO;
}
- (void)handleReady { _isSetuped = YES; [self doSend]; }
- (void)dealloc { [self close]; }
- (void)changeIpAddressType { [super changeIpAddressType]; [self close]; }
- (void)close {
    if (!_isSetuped) return;
    JCORENWUdpLog(YES, 8, 204, @"close udp socket");
    _isSetuped = NO;
    while ([self dequeueFromSendQueue]) {}
    [self p_cleanSocket];
}
- (void)p_cleanSocket { if (@available(iOS 12.0, *)) { if (_connection) nw_connection_cancel(_connection); } }
- (void)sendData:(id)data {
    double timeout = [data timeout];
    if (timeout <= 1) timeout = 4;
    BOOL result = [self sendData:data toHost:self.host port:(unsigned short)self.port withTimeout:timeout];
    if ([data handler]) ((JCORETransportResult)[data handler])(result);
}
- (BOOL)sendData:(id)data toHost:(id)host port:(unsigned short)port withTimeout:(double)timeout {
    JCORENWUdpLog(YES, 8, 243, @"send udp data");
    JCORENWUdpLog(YES, 0x100, 244, @"try to send udp data to host %@:%u with timeout %f", host, port, timeout);
    if (!_connection) return NO;
    NSData *address = host && port ? [self getDataAddressWithHost:host port:port] : nil;
    JCORENWUdpPacket *packet = [[JCORENWUdpPacket alloc] initWithType:0 data:data address:address timeout:timeout udpSocket:self];
    [self enqueueToSendQueue:packet];
    JCORENWUdpLog(YES, 8, 259, @"send udp data succeed");
    return YES;
}
- (void)receiveDataWithTimeout:(double)timeout {
    [self stopReceiveTimeoutTimer];
    _recvTimeoutTimer = [NSTimer scheduledTimerWithTimeInterval:timeout target:self selector:@selector(receiveTimeout:) userInfo:nil repeats:NO];
}
- (void)stopReceiveTimeoutTimer { if (_recvTimeoutTimer.isValid) [_recvTimeoutTimer invalidate]; _recvTimeoutTimer = nil; }
- (NSUInteger)sendQueueCount { return [self count:self.sendQueue]; }
- (void)timeoutWithUdpPacket:(id)packet { [self delayedDidNotSendData:[packet buffer]]; [self dequeue:packet in:self.sendQueue]; }
- (void)doSend {
    if (@available(iOS 12.0, *)) {
        JCORENWUdpPacket *packet = [self dequeueFromSendQueue];
        if (!packet) return;
        NSData *data = [(id)packet.buffer data];
        JCORENWUdpLog(YES, 0x100, 301, @"nwsocket udp: doSend Udp Data %@ at udp socket %@", packet, self);
        JCORENWUdpLog(YES, 8, 302, @"udp socket begin send behavior");
        dispatch_data_t content = [self dispatchDataFromNSData:data];
        nw_connection_send(_connection, content, NW_CONNECTION_DEFAULT_MESSAGE_CONTEXT, true, ^(nw_error_t error) {
            if (!error) [JCORENetworkController performSel:@selector(handleSendResult:) target:self withObject:packet];
            else {
                NSError *result = [[NSError alloc] initWithDomain:@"domin" code:nw_error_get_error_code(error) userInfo:@{@"nw_connection_send: nw_error_get_error_domain":@((unsigned int)nw_error_get_error_domain(error))}];
                JCORENWUdpLog(YES, 0x100, 309, @"nwsocket send data error: %@", result);
                [JCORENetworkController performSel:@selector(handleSendError:) target:self withObject:packet];
            }
        });
    }
}
- (void)handleSendError:(id)packet {
    JCORENWUdpLog(NO, 1, 324, @"udp send error occured: errocode %d", errno);
    [self close];
    [self performSelector:@selector(delayedDidNotSendData:) withObject:[packet buffer] afterDelay:0];
}
- (void)handleSendResult:(id)packet { [self performSelector:@selector(delayedDidSendData:) withObject:[packet buffer] afterDelay:0]; }
- (void)doRead {
    if (@available(iOS 12.0, *)) {
        nw_connection_receive(_connection, 1, 0x4000, ^(dispatch_data_t content, nw_content_context_t context, bool complete, nw_error_t error) {
            JCORENWUdpLog(YES, 0x100, 342, @"nwsocket udp receive data：content=%@, context=%@, is_complete=%d, error=%@", content, context, complete, error);
            if (content) {
                JCORENWUdpLog(YES, 0x100, 344, @"nwsocket udp: doread data - no content");
                [JCORENetworkController performSel:@selector(handleReceivedData:) target:self withObject:content];
            }
            if (error) {
                JCORENWUdpLog(YES, 0x100, 348, @"nwsocket udp: doread data error - %d", nw_error_get_error_code(error));
                [JCORENetworkController performSel:@selector(handleReceiveDataError:) target:self withObject:content];
            } else if (complete) {
                JCORENWUdpLog(YES, 0x100, 353, @"nwsocket udp: doread data - complete");
                [JCORENetworkController performSel:@selector(close) target:self withObject:nil];
            } else [JCORENetworkController performSel:@selector(doRead) target:self withObject:nil];
        });
    }
}
- (void)handleReceivedData:(id)data { [self performSelector:@selector(delayedDidReceivedData:) withObject:[self nsDataFromDispatchData:data] afterDelay:0]; }
- (void)handleReceiveDataError:(id)data {
    JCORENWUdpLog(NO, 1, 374, @"udp read error occured: errocode");
    [self close];
    [self performSelector:@selector(delayedDidNotReceivedDataWithError:) withObject:[NSError errorWithDomain:@"udp sys error" code:0 userInfo:nil] afterDelay:0];
}
- (void)enqueueToSendQueue:(id)packet { if (packet) [self enqueue:packet in:self.sendQueue]; }
- (id)dequeueFromSendQueue {
    JCORENWUdpPacket *packet = [self firstObject:self.sendQueue];
    if (packet) { [self dequeue:packet in:self.sendQueue]; [packet invalidTimeoutTimer]; }
    return packet;
}
- (void)receiveTimeout:(id)timer {
    if (_recvTimeoutTimer != timer) return;
    [self stopReceiveTimeoutTimer];
    [self performSelector:@selector(delayedDidNotReceivedDataWithError:) withObject:[NSError errorWithDomain:@"udp time out" code:0 userInfo:nil] afterDelay:0];
}
- (void)delayedDidSendData:(id)data { if (self.delegate) [self.delegate sentData:data socket:self]; }
- (void)delayedDidNotSendData:(id)data { if (self.delegate) [self.delegate unsentData:data socket:self]; }
- (void)delayedDidReceivedData:(id)data { if (self.delegate) [self.delegate recvData:data socket:self]; }
- (void)delayedDidNotReceivedDataWithError:(id)error { if (self.delegate) [self.delegate noRecvData:error socket:self]; }
@end
