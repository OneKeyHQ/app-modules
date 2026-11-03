#import "JCORENWTcpSocket.h"
#import "JCOREQueueManager.h"
#import "JCORELog.h"
#include <arpa/inet.h>

#define JCORENWTcpFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Network/Model/JCORENWTcpSocket.m"
#define JCORENWTcpLog(sync, flags, sourceLine, ...) [JCORELog log:sync level:0x3ff flag:flags context:0 file:JCORENWTcpFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]

@implementation JCORENWTcpSocket
- (instancetype)init {
    if ((self = [super init])) { _ulDefaultReceiveBufferSize = 100240; _receivingBuffer = calloc(1, 100240); }
    return self;
}
- (void)setConfig:(id<JCORESocketConfigProtocol>)config { [super setConfig:config]; _isSetuped = NO; _ulReceivedLen = 0; }
- (void)dealloc { [self close]; free(_receivingBuffer); }
- (void)connect { [super connect]; [self setup]; }
- (void)disconect { [super disconect]; [self close]; }
- (void)sendData:(id)data {
    if (!data) { JCORENWTcpLog(NO, 0x20, 71, @"sendData object nil"); return; }
    JCORENWTcpLog(YES, 0x100, 74, @"nwsocket - send data%@", data);
    [self enqueue:data in:self.sendQueue];
    BOOL connected = [self isConnected];
    if ([data handler]) ((JCORETransportResult)[data handler])(connected);
    if (connected) [self doSend];
}
- (BOOL)setup {
    if ([self isConnected]) return YES;
    if (_isSetuped) { JCORENWTcpLog(NO, 1, 100, @"tcp socket is setup up already"); return NO; }
    _connectQueue = [JCOREQueueManager syncQueue];
    if (@available(iOS 12.0, *)) {
        nw_parameters_t parameters = nw_parameters_create_secure_tcp(NW_PARAMETERS_DISABLE_PROTOCOL, NW_PARAMETERS_DEFAULT_CONFIGURATION);
        NSString *port = [NSString stringWithFormat:@"%u", self.port];
        nw_endpoint_t endpoint = nw_endpoint_create_host(self.host.UTF8String, port.UTF8String);
        _connection = nw_connection_create(endpoint, parameters);
        if (!_connection) { JCORENWTcpLog(NO, 1, 113, @"nwsocket tcp: create tcp socket error"); return NO; }
        _isSetuped = YES;
        self.status = 1;
        nw_connection_set_queue(_connection, _connectQueue);
        nw_connection_set_state_changed_handler(_connection, ^(nw_connection_state_t state, nw_error_t error) {
            if (state == nw_connection_state_ready) {
                JCORENWTcpLog(YES, 0x100, 130, @"nwsocket tcp:connection is ready");
                [JCORENetworkController performSel:@selector(handleReady) target:self withObject:nil];
            } else if (state == nw_connection_state_cancelled) {
                JCORENWTcpLog(YES, 0x100, 134, @"nwsocket tcp:connection is cancelled");
                [JCORENetworkController performSel:@selector(close) target:self withObject:nil];
            } else if (state == nw_connection_state_failed) {
                JCORENWTcpLog(YES, 0x100, 126, @"nwsocket tcp:failed");
                [JCORENetworkController performSel:@selector(close) target:self withObject:nil];
            }
        });
        [self doRead];
        nw_connection_start(_connection);
        _connectTimer = [NSTimer scheduledTimerWithTimeInterval:5 target:self selector:@selector(outTimeAction) userInfo:nil repeats:NO];
        return YES;
    }
    return NO;
}
- (void)handleReady {
    self.status = 2;
    [self invalidateConnectTimeOutTimer];
    JCORENWTcpLog(YES, 8, 161, @"连接主机成功");
    JCORENWTcpLog(YES, 0x100, 162, @"nwsocket tcp:连接主机成功: %@ 端口: %u", self.host, self.port);
    [self doSend];
}
- (void)invalidateConnectTimeOutTimer { if (_connectTimer && _connectTimer.isValid) { [_connectTimer invalidate]; _connectTimer = nil; } }
- (void)outTimeAction { JCORENWTcpLog(YES, 0x100, 175, @"nwsocket tcp - Socket:%@ 超时了", self.host); [self close]; }
- (void)close {
    if (!_isSetuped) return;
    if (_connectTimer && _connectTimer.isValid) { [_connectTimer invalidate]; _connectTimer = nil; }
    JCORENWTcpLog(YES, 8, 191, @"nwsocket tcp - %@", @"关闭TCP连接");
    JCORENWTcpLog(YES, 0x100, 192, @"nwsocket tcp - 关闭TCP连接: %@ 端口: %u", self.host, self.port);
    _isSetuped = NO;
    self.status = 0;
    if (_connection) { [self p_cleanSocket]; _ulReceivedLen = 0; }
}
- (void)p_cleanSocket { if (@available(iOS 12.0, *)) { if (_connection) nw_connection_cancel(_connection); } }
- (void)setStatus:(NSUInteger)status {
    if (_status == status) return;
    _status = status;
    if (self.delegate) [self.delegate connectStateChanged:@(status) socket:self];
}
- (BOOL)isConnected { return self.status == 2; }
- (void)doRead {
    if (@available(iOS 12.0, *)) {
        nw_connection_receive(_connection, 1, 100240, ^(dispatch_data_t content, nw_content_context_t context, bool complete, nw_error_t error) {
            JCORENWTcpLog(YES, 0x100, 240, @"nwsocket tcp receive data：content=%@, context=%@, is_complete=%d, error=%@", content, context, complete, error);
            if (content) [JCORENetworkController performSel:@selector(handleReceivedData:) target:self withObject:content];
            if (error) {
                JCORENWTcpLog(YES, 0x100, 247, @"nwsocket tcp: doread data error, code: %d, domin:%@", nw_error_get_error_code(error), @((unsigned int)nw_error_get_error_domain(error)));
            } else if (!complete) { [JCORENetworkController performSel:@selector(doRead) target:self withObject:nil]; return; }
            else JCORENWTcpLog(YES, 0x100, 253, @"nwsocket tcp: doread data - is complete is yes");
            [JCORENetworkController performSel:@selector(close) target:self withObject:nil];
        });
    }
}
- (void)handleReceivedData:(id)content {
    if (!content) return;
    NSData *data = [self nsDataFromDispatchData:content];
    size_t length = dispatch_data_get_size(content);
    void *temporary = calloc(1, 100240);
    // Reject the original's allocation failure or overflowing fixed-buffer append.
    if (!temporary || !_receivingBuffer || length > 100240 || _ulReceivedLen > 100240 || length > 100240 - _ulReceivedLen) {
        free(temporary); [self resetReadInfo]; [self close]; return;
    }
    memcpy(temporary, data.bytes, length);
    memmove(_receivingBuffer + _ulReceivedLen, temporary, length);
    _ulReceivedLen += length;
    free(temporary);
    while (_ulReceivedLen >= 2) {
        uint16_t raw;
        memcpy(&raw, _receivingBuffer, sizeof(raw));
        NSUInteger packetLength = ntohs(raw) & 0x7fff;
        if (!packetLength) { [self resetReadInfo]; [self close]; return; }
        if (_ulReceivedLen < packetLength) return;
        void *packet = malloc(packetLength);
        NSUInteger remainingLength = _ulReceivedLen - packetLength;
        void *remaining = malloc(remainingLength);
        if (!packet || (!remaining && remainingLength)) { free(packet); free(remaining); [self resetReadInfo]; [self close]; return; }
        memmove(packet, _receivingBuffer, packetLength);
        if (remainingLength) memmove(remaining, _receivingBuffer + packetLength, remainingLength);
        bzero(_receivingBuffer, _ulReceivedLen);
        if (remainingLength) memmove(_receivingBuffer, remaining, remainingLength);
        free(remaining);
        _ulReceivedLen -= packetLength;
        [self recvData:[NSData dataWithBytes:packet length:packetLength]];
        free(packet);
        if (!_ulReceivedLen) break;
    }
}
- (void)doSend {
    if (@available(iOS 12.0, *)) {
        id object = [self firstObject:self.sendQueue];
        if (!object) { if (self.delegate) [self.delegate sendQueueEmpty:self]; return; }
        dispatch_data_t content = [self dispatchDataFromNSData:[object data]];
        nw_connection_send(_connection, content, NW_CONNECTION_DEFAULT_MESSAGE_CONTEXT, true, ^(nw_error_t error) {
            if (!error) [JCORENetworkController performSel:@selector(handleSendResult:) target:self withObject:object];
            else {
                NSError *result = [[NSError alloc] initWithDomain:@"domin" code:nw_error_get_error_code(error) userInfo:@{@"nw_connection_send: nw_error_get_error_domain":@((unsigned int)nw_error_get_error_domain(error))}];
                JCORENWTcpLog(YES, 0x100, 346, @"nwsocket tcp send data error: %@", result);
                [JCORENetworkController performSel:@selector(close) target:self withObject:nil];
            }
        });
    }
}
- (void)handleSendResult:(id)data { [self dequeue:data in:self.sendQueue]; if (self.delegate) [self.delegate sentData:data socket:self]; }
- (void)resetReadInfo { if (_receivingBuffer) { memset(_receivingBuffer, 0, 8); _ulReceivedLen = 0; } }
- (void)recvData:(id)data { [self performSelector:@selector(enqueueRecvData:) withObject:data afterDelay:0]; }
- (void)enqueueRecvData:(id)data {
    [self enqueue:data in:self.recvQueue];
    if (_isDispatching) return;
    _isDispatching = YES;
    [self dispatchRecvData];
}
- (void)dispatchRecvData {
    id data = [self firstObject:self.recvQueue];
    if (!data) { _isDispatching = NO; return; }
    if ([data length] && self.delegate) [self.delegate recvData:data socket:self];
    [self dequeue:data in:self.recvQueue];
    [self performSelector:@selector(dispatchRecvData) withObject:nil afterDelay:0];
}
@end
