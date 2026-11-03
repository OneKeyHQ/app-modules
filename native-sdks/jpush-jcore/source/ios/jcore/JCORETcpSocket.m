#import "JCORETcpSocket.h"
#import "JCORELog.h"
#include <arpa/inet.h>
#include <errno.h>
#include <sys/socket.h>

#define JCORETcpFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Network/Model/JCORETcpSocket.m"
#define JCORETcpLog(sync, flags, sourceLine, ...) [JCORELog log:sync level:0x3ff flag:flags context:0 file:JCORETcpFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]

static void JCORETcpSocketCallback(CFSocketRef socket, CFSocketCallBackType type, CFDataRef address, const void *data, void *context) {
    @autoreleasepool {
        id controller = (__bridge id)context;
        const char *function = "void JCORETcpSocketCallback(CFSocketRef, CFSocketCallBackType, CFDataRef, const void *, void *)";
        if (data) {
            [JCORELog log:NO level:0x3ff flag:1 context:0 file:JCORETcpFile function:function line:33 tag:nil format:@"TCP连接失败,error:%d", *(const int *)data];
            if ([controller respondsToSelector:@selector(close)]) [controller close];
        } else if (!socket || !CFSocketIsValid(socket)) {
            [JCORELog log:YES level:0x3ff flag:2 context:0 file:JCORETcpFile function:function line:42 tag:nil format:@"tcp socket is invaild"];
        } else if ([controller isKindOfClass:JCORETcpSocket.class] && [controller respondsToSelector:@selector(doCFSocketCallback:forSocket:withData:)]) {
            [controller doCFSocketCallback:type forSocket:socket withData:NULL];
        }
    }
}

@implementation JCORETcpSocket
- (instancetype)init {
    if ((self = [super init])) {
        _ulDefaultReceiveBufferSize = 100240;
        _receivingBuffer = calloc(1, 100240);
    }
    return self;
}
- (void)setConfig:(id<JCORESocketConfigProtocol>)config {
    [super setConfig:config];
    _isSetuped = NO;
    _ulSendingDataSentLen = 0;
    _ulReceivedLen = 0;
}
- (void)dealloc { [self close]; free(_receivingBuffer); }
- (void)connect {
    [super connect];
    if (![self setup] && self.delegate) [self.delegate connectStateChanged:@((NSUInteger)0) socket:self];
}
- (void)disconect { [super disconect]; [self close]; }
- (void)sendData:(id)data {
    [super sendData:data];
    if (!data) { JCORETcpLog(NO, 0x20, 100, @"sendData object nil"); return; }
    [self enqueue:data in:self.sendQueue];
    BOOL connected = [self isConnected];
    if ([data handler]) ((JCORETransportResult)[data handler])(connected);
    if (connected) [self reenableSocketCallbacks];
}
- (BOOL)setup {
    if ([self isConnected]) return YES;
    if (_isSetuped) { JCORETcpLog(NO, 1, 125, @"tcp socket is setup up already"); return YES; }
    CFSocketContext context = {0, (__bridge void *)self, NULL, NULL, NULL};
    _socket = CFSocketCreate(kCFAllocatorDefault, self.ipType == 1 ? AF_INET6 : AF_INET, SOCK_STREAM, IPPROTO_TCP, kCFSocketReadCallBack | kCFSocketConnectCallBack | kCFSocketWriteCallBack, JCORETcpSocketCallback, &context);
    if (!_socket) { JCORETcpLog(NO, 1, 172, @"create tcp socket error: errocode %d", errno); return NO; }
    CFSocketSetSocketFlags(_socket, (CFSocketGetSocketFlags(_socket) & ~kCFSocketAutomaticallyReenableWriteCallBack) | 0x81);
    _rls = CFSocketCreateRunLoopSource(NULL, _socket, 0);
    if (!_rls) __assert_rtn("-[JCORETcpSocket setup]", "JCORETcpSocket.m", 152, "_rls != NULL");
    CFRunLoopAddSource(CFRunLoopGetCurrent(), _rls, kCFRunLoopDefaultMode);
    int native = CFSocketGetNative(_socket);
    struct timeval timeout = {1, 500000};
    setsockopt(native, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout));
    int size = 100240;
    setsockopt(native, SOL_SOCKET, SO_RCVBUF, &size, sizeof(size));
    setsockopt(native, SOL_SOCKET, SO_SNDBUF, &size, sizeof(size));
    int noSignal = 1;
    setsockopt(native, SOL_SOCKET, SO_NOSIGPIPE, &noSignal, sizeof(noSignal));
    NSData *address = [self getDataAddress];
    if (!address) {
        JCORETcpLog(NO, 1, 178, @"connect failed with get remote address");
        [self p_cleanSocket];
        JCORETcpLog(NO, 1, 180, @"get host and address data failed");
        return NO;
    }
    CFSocketError result = CFSocketConnectToAddress(_socket, (__bridge CFDataRef)address, -1);
    if (result != kCFSocketSuccess) {
        JCORETcpLog(NO, 1, 186, @"connect failed with %@", result == kCFSocketError ? @"socket error" : @"socket timeout");
        JCORETcpLog(NO, 0x20, 187, @"connect failed with %@, %d", self.host, self.port);
        [self p_cleanSocket];
        return NO;
    }
    _isSetuped = YES;
    self.status = 1;
    _connectTimer = [NSTimer scheduledTimerWithTimeInterval:5 target:self selector:@selector(outTimeAction) userInfo:nil repeats:NO];
    return YES;
}
- (void)outTimeAction { JCORETcpLog(YES, 0x100, 202, @"Socket:%@ 超时了", self.host); [self close]; }
- (void)close {
    if (!_isSetuped) return;
    if (_connectTimer && _connectTimer.isValid) { [_connectTimer invalidate]; _connectTimer = nil; }
    JCORETcpLog(YES, 8, 219, @"关闭TCP连接");
    JCORETcpLog(YES, 0x100, 220, @"关闭TCP连接: %@ 端口: %u", self.host, self.port);
    _isSetuped = NO;
    self.status = 0;
    if (_socket) { [self p_cleanSocket]; _ulReceivedLen = 0; _ulSendingDataSentLen = 0; }
}
- (void)setStatus:(NSUInteger)status {
    if (_status == status) return;
    _status = status;
    if (self.delegate) [self.delegate connectStateChanged:@(status) socket:self];
}
- (void)doCFSocketCallback:(CFSocketCallBackType)type forSocket:(CFSocketRef)socket withData:(CFDataRef)data {
    if (!_socket || _socket != socket || !CFSocketIsValid(socket)) { JCORETcpLog(YES, 2, 249, @"unkown cfsocket callback"); return; }
    if (!_isSetuped) { JCORETcpLog(YES, 2, 254, @"socket object not setup when receive server callback"); return; }
    static NSRecursiveLock *lock;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ lock = [[NSRecursiveLock alloc] init]; });
    [lock lock];
    if (type == kCFSocketReadCallBack) [self doRead];
    else if (type == kCFSocketWriteCallBack) [self doSend];
    else if (type == kCFSocketConnectCallBack) [self onConnectedWithErrorCode:data];
    else JCORETcpLog(NO, 1, 278, @"JCORETcpSocket %@ received unexpected CFSocketCallBackType %lu.", self, type);
    [lock unlock];
}
- (BOOL)isConnected { return self.status == 2; }
- (void)onConnectedWithErrorCode:(CFDataRef)error {
    if (error) {
        JCORETcpLog(NO, 1, 305, @"连接主机失败: %d", *(const int *)error);
        JCORETcpLog(NO, 0x20, 306, @"connect to host failed with error code: %d", *(const int *)error);
        [self close];
        return;
    }
    self.status = 2;
    if (_connectTimer && _connectTimer.isValid) { [_connectTimer invalidate]; _connectTimer = nil; }
    JCORETcpLog(YES, 8, 300, @"连接主机成功");
    JCORETcpLog(YES, 0x100, 301, @"连接主机成功: %@ 端口: %u", self.host, self.port);
}
- (void)doRead {
    if (!_socket) return;
    int native = CFSocketGetNative(_socket);
    void *temporary = calloc(1, 100240);
    // The original dereferences failed allocations and can overrun its fixed buffer.
    if (!temporary || !_receivingBuffer) { free(temporary); [self resetReadInfo]; [self close]; return; }
    ssize_t received = recv(native, temporary, 100240, 0);
    if (received < 0) {
        if (errno != EAGAIN) {
            JCORETcpLog(NO, 1, errno == ENOTCONN ? 333 : 338, @"tcp read error occured: errocode %d", errno);
            [self resetReadInfo];
            [self close];
        }
        free(temporary);
    } else if (!received) {
        JCORETcpLog(YES, 8, 346, @"tcp connection closed by remote");
        [self resetReadInfo];
        [self close];
        free(temporary);
        return;
    } else {
        if (_ulReceivedLen > 100240 || (NSUInteger)received > 100240 - _ulReceivedLen) {
            free(temporary); [self resetReadInfo]; [self close]; return;
        }
        memmove(_receivingBuffer + _ulReceivedLen, temporary, (size_t)received);
        _ulReceivedLen += (NSUInteger)received;
        free(temporary);
        while (_ulReceivedLen >= 2) {
            uint16_t raw;
            memcpy(&raw, _receivingBuffer, sizeof(raw));
            NSUInteger length = ntohs(raw) & 0x7fff;
            if (!length) { [self resetReadInfo]; [self close]; break; }
            if (_ulReceivedLen < length) break;
            void *packet = malloc(length);
            NSUInteger remainingLength = _ulReceivedLen - length;
            void *remaining = malloc(remainingLength);
            if (!packet || (!remaining && remainingLength)) {
                free(packet); free(remaining); [self resetReadInfo]; [self close]; return;
            }
            memmove(packet, _receivingBuffer, length);
            if (remainingLength) memmove(remaining, _receivingBuffer + length, remainingLength);
            bzero(_receivingBuffer, _ulReceivedLen);
            if (remainingLength) memmove(_receivingBuffer, remaining, remainingLength);
            free(remaining);
            _ulReceivedLen -= length;
            [self recvData:[NSData dataWithBytes:packet length:length]];
            free(packet);
            if (!_ulReceivedLen) break;
        }
    }
    [self reenableSocketCallbacks];
}
- (void)doSend {
    id object = [self firstObject:self.sendQueue];
    if (!object) {
        [self reenableSocketCallbacks];
        if (self.delegate) [self.delegate sendQueueEmpty:self];
        return;
    }
    NSUInteger length = [[object data] length];
    if (length < _ulSendingDataSentLen) {
        JCORETcpLog(NO, 1, 420, @"send data error: data len %zu < sent len %lu", length, _ulSendingDataSentLen);
        [self close];
        return;
    }
    NSData *data = [object data];
    ssize_t sent = send(CFSocketGetNative(_socket), (const char *)data.bytes + _ulSendingDataSentLen, length - _ulSendingDataSentLen, 0);
    if (sent < 0) { JCORETcpLog(YES, 8, 430, @"tcp send data error :%d", errno); [self close]; return; }
    [self reenableSocketCallbacks];
    _ulSendingDataSentLen += (NSUInteger)sent;
    if (length != _ulSendingDataSentLen) return;
    _ulSendingDataSentLen = 0;
    [self dequeue:object in:self.sendQueue];
    if (self.delegate) [self.delegate sentData:object socket:self];
}
- (void)resetReadInfo {
    if (_receivingBuffer) { memset(_receivingBuffer, 0, 8); _ulReceivedLen = 0; [self reenableSocketCallbacks]; }
}
- (void)reenableSocketCallbacks {
    CFOptionFlags callbacks = self.careAllDataSent ? 9 : ([self count:self.sendQueue] ? 9 : 1);
    if (_socket && CFSocketIsValid(_socket)) CFSocketEnableCallBacks(_socket, callbacks);
}
- (void)doCheck {
    if (_socket && CFSocketIsValid(_socket)) JCORETcpLog(YES, 0x100, 474, @"tcp socket status %lu, native socket: %d", self.status, CFSocketGetNative(_socket));
}
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
