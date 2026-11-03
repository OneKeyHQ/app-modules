#import "JCORENWSSLSocket.h"
#import "JCOREQueueManager.h"
#import "JCORELog.h"
#import "JCOREBusinessStorage.h"
#import <Security/Security.h>
#include <arpa/inet.h>

#define JCORENWSSLFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Network/Model/JCORENWSSLSocket.m"
#define JCORENWSSLLog(sync, flags, sourceLine, ...) [JCORELog log:sync level:0x3ff flag:flags context:0 file:JCORENWSSLFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]

@implementation JCORENWSSLSocket
- (instancetype)init {
    if ((self = [super init])) { _ulDefaultReceiveBufferSize = 100240; _receivingBuffer = calloc(1, 100240); }
    return self;
}
- (void)setConfig:(id<JCORESocketConfigProtocol>)config { [super setConfig:config]; _isSetuped = NO; _ulSendingDataSentLen = 0; _ulReceivedLen = 0; }
- (void)dealloc { [self close]; free(_receivingBuffer); }
- (void)connect { [super connect]; [self setup]; }
- (void)disconect { [super disconect]; [self close]; }
- (void)sendData:(id)data {
    [super sendData:data];
    if (!data) { JCORENWSSLLog(NO, 0x20, 72, @"sendData object nil"); return; }
    JCORENWSSLLog(YES, 0x100, 75, @"nwsocket ssl - send data%@", data);
    [self enqueue:data in:self.sendQueue];
    BOOL connected = [self isConnected];
    if ([data handler]) ((JCORETransportResult)[data handler])(connected);
    if (connected) [self doSend];
}
- (BOOL)setup {
    if ([self isConnected]) return YES;
    if (_isSetuped) { JCORENWSSLLog(NO, 1, 100, @"tcp socket is setup up already"); return NO; }
    _connectQueue = [JCOREQueueManager syncQueue];
    if (@available(iOS 12.0, *)) {
        nw_parameters_t parameters = nw_parameters_create_secure_tcp(^(nw_protocol_options_t options) {
            sec_protocol_options_t security = nw_tls_copy_sec_protocol_options(options);
            sec_protocol_options_set_verify_block(security, ^(sec_protocol_metadata_t metadata, sec_trust_t suppliedTrust, sec_protocol_verify_complete_t complete) {
                SecTrustRef trust = sec_trust_copy_ref(suppliedTrust);
                if (!trust) { JCORENWSSLLog(YES, 0x100, 111, @"nwsocket ssl:trust is nil"); complete(false); return; }
                NSMutableArray *anchors = [NSMutableArray array];
                id certificateString = [JCOREBusinessStorage getLocalCert];
                if (!certificateString || ![certificateString isKindOfClass:NSString.class]) {
                    JCORENWSSLLog(YES, 0x100, 120, @"nwsocket ssl:serverCertStr is nil"); complete(false); CFRelease(trust); return;
                }
                NSData *certificateData = [self convertCerStringToData:certificateString];
                if (!certificateData || ![certificateData isKindOfClass:NSData.class]) {
                    JCORENWSSLLog(YES, 0x100, 127, @"nwsocket ssl:cerData is nil"); complete(false); CFRelease(trust); return;
                }
                SecCertificateRef certificate = SecCertificateCreateWithData(NULL, (__bridge CFDataRef)certificateData);
                if (!certificate) { JCORENWSSLLog(YES, 0x100, 134, @"cerRef is nil"); complete(false); CFRelease(trust); return; }
                [anchors addObject:(__bridge id)certificate];
                SecTrustSetAnchorCertificates(trust, (__bridge CFArrayRef)anchors);
                NSMutableArray *policies = [NSMutableArray array];
                SecPolicyRef policy = SecPolicyCreateBasicX509();
                [policies addObject:(__bridge id)policy];
                // Preserve the original BasicX509 policy; it does not check a hostname.
                SecTrustSetPolicies(trust, (__bridge CFArrayRef)policies);
                CFErrorRef error = NULL;
                if (!SecTrustEvaluateWithError(trust, &error)) {
                    JCORENWSSLLog(YES, 0x100, 148, @"nwsocket ssl: cert cherk error :%@", (__bridge id)error);
                    complete(false);
                    if (error) CFRelease(error);
                } else complete(true);
                CFRelease(trust);
                // The collections retain these Create-rule objects for their needed lifetime.
                CFRelease(certificate);
                CFRelease(policy);
            }, self->_connectQueue);
        }, ^(nw_protocol_options_t options) {});
        NSString *port = [NSString stringWithFormat:@"%u", self.port];
        nw_endpoint_t endpoint = nw_endpoint_create_host(self.host.UTF8String, port.UTF8String);
        _connection = nw_connection_create(endpoint, parameters);
        if (!_connection) { JCORENWSSLLog(NO, 1, 168, @"nwsocket ssl: create tcp socket error"); return NO; }
        _isSetuped = YES;
        self.status = 1;
        nw_connection_set_queue(_connection, _connectQueue);
        nw_connection_set_state_changed_handler(_connection, ^(nw_connection_state_t state, nw_error_t error) {
            if (state == nw_connection_state_ready) {
                JCORENWSSLLog(YES, 0x100, 185, @"nwsocket ssl:connection is ready");
                [JCORENetworkController performSel:@selector(handleReady) target:self withObject:nil];
            } else if (state == nw_connection_state_cancelled) {
                JCORENWSSLLog(YES, 0x100, 189, @"nwsocket ssl:connection is cancelled");
                [JCORENetworkController performSel:@selector(close) target:self withObject:nil];
            } else if (state == nw_connection_state_failed) {
                JCORENWSSLLog(YES, 0x100, 181, @"nwsocket ssl:failed");
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
    JCORENWSSLLog(YES, 8, 216, @"连接主机成功");
    JCORENWSSLLog(YES, 0x100, 217, @"nwsocket ssl:连接主机成功: %@ 端口: %u", self.host, self.port);
    [self doSend];
}
- (void)invalidateConnectTimeOutTimer { if (_connectTimer && _connectTimer.isValid) { [_connectTimer invalidate]; _connectTimer = nil; } }
- (void)outTimeAction { JCORENWSSLLog(YES, 0x100, 231, @"nwsocet ssl - Socket:%@ 超时了", self.host); [self close]; }
- (void)close {
    if (!_isSetuped) return;
    if (_connectTimer && _connectTimer.isValid) { [_connectTimer invalidate]; _connectTimer = nil; }
    JCORENWSSLLog(YES, 8, 247, @"关闭TCP连接");
    JCORENWSSLLog(YES, 0x100, 248, @"nwsocket ssl - 关闭TCP连接: %@ 端口: %u", self.host, self.port);
    _isSetuped = NO;
    self.status = 0;
    [self p_cleanSocket];
    _ulReceivedLen = 0;
    _ulSendingDataSentLen = 0;
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
            JCORENWSSLLog(YES, 0x100, 294, @"nwsocket ssl receive data：content=%@, context=%@, is_complete=%d, error=%@", content, context, complete, error);
            if (content) [JCORENetworkController performSel:@selector(handleReceivedData:) target:self withObject:content];
            if (error) {
                JCORENWSSLLog(YES, 0x100, 301, @"nwsocket ssl: doread data error - %d", nw_error_get_error_code(error));
            } else if (!complete) { [JCORENetworkController performSel:@selector(doRead) target:self withObject:nil]; return; }
            else JCORENWSSLLog(YES, 0x100, 307, @"nwsocket ssl: doread data - is complete is yes");
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
                JCORENWSSLLog(YES, 0x100, 403, @"nwsocket ssl: send data error: %@", result);
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
- (NSData *)convertCerStringToData:(id)string {
    NSString *body = [[string stringByReplacingOccurrencesOfString:@"-----BEGIN CERTIFICATE-----\n" withString:@""] stringByReplacingOccurrencesOfString:@"-----END CERTIFICATE-----" withString:@""];
    body = [body stringByReplacingOccurrencesOfString:@"\n" withString:@""];
    return [[NSData alloc] initWithBase64EncodedString:body options:0];
}
@end
