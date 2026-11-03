#import "JCOREUdpSocket.h"
#import "JCORENetworkModels.h"
#import "JCORELog.h"
#include <netinet/in.h>
#include <errno.h>
#include <sys/socket.h>

#define JCOREUdpFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Network/Model/JCOREUdpSocket.m"
#define JCOREUdpLog(sync, flags, sourceLine, ...) [JCORELog log:sync level:0x3ff flag:flags context:0 file:JCOREUdpFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]

static void JCOREUdpSocketCallback(CFSocketRef socket, CFSocketCallBackType type, CFDataRef address, const void *data, void *context) {
    @autoreleasepool {
        id controller = (__bridge id)context;
        const char *function = "void JCOREUdpSocketCallback(CFSocketRef, CFSocketCallBackType, CFDataRef, const void *, void *)";
        if (data) {
            [JCORELog log:NO level:0x3ff flag:1 context:0 file:JCOREUdpFile function:function line:101 tag:nil format:@"UDP连接失败,error:%d", *(const int *)data];
        } else if (!socket || !CFSocketIsValid(socket)) {
            [JCORELog log:YES level:0x3ff flag:2 context:0 file:JCOREUdpFile function:function line:106 tag:nil format:@"udp socket is invaild"];
        } else if ([controller isKindOfClass:JCOREUdpSocket.class] && [controller respondsToSelector:@selector(doCFSocketCallback:forSocket:withAddress:withData:)]) {
            [controller doCFSocketCallback:type forSocket:socket withAddress:(__bridge id)address withData:NULL];
        }
    }
}

@implementation JCOREUdpSocket
- (instancetype)init {
    if ((self = [super init])) {
        _isSetuped = NO;
        _ulDefaultReceiveBufferSize = 0x4000;
        _receivingBuffer = calloc(1, 0x4000);
        [self setup];
    }
    return self;
}
- (void)connect { [super connect]; if (!_socket) [self setup]; }
- (void)disconect { [super disconect]; }
- (BOOL)setup {
    if (_isSetuped) return NO;
    CFSocketContext context = {0, (__bridge void *)self, NULL, NULL, NULL};
    _socket = CFSocketCreate(kCFAllocatorDefault, self.ipType == 1 ? AF_INET6 : AF_INET, SOCK_DGRAM, IPPROTO_UDP, 9, JCOREUdpSocketCallback, &context);
    if (!_socket) { JCOREUdpLog(NO, 1, 184, @"create udp socket error: errocode %d", errno); return NO; }
    CFSocketSetSocketFlags(_socket, (CFSocketGetSocketFlags(_socket) & ~kCFSocketAutomaticallyReenableWriteCallBack) | 0x81);
    _rls = CFSocketCreateRunLoopSource(NULL, _socket, 0);
    CFRunLoopAddSource(CFRunLoopGetCurrent(), _rls, kCFRunLoopDefaultMode);
    int noSignal = 1;
    setsockopt(CFSocketGetNative(_socket), SOL_SOCKET, SO_NOSIGPIPE, &noSignal, sizeof(noSignal));
    _isSetuped = YES;
    return YES;
}
- (void)dealloc { [self close]; free(_receivingBuffer); }
- (void)changeIpAddressType { [super changeIpAddressType]; [self close]; }
- (void)close {
    if (!_isSetuped) return;
    JCOREUdpLog(YES, 8, 205, @"close udp socket");
    _isSetuped = NO;
    while ([self dequeueFromSendQueue]) {}
    [self p_cleanSocket];
}
- (void)sendData:(id)data {
    double timeout = [data timeout];
    if (timeout <= 1) timeout = 4;
    BOOL result = [self sendData:data toHost:self.host port:(unsigned short)self.port withTimeout:timeout];
    if ([data handler]) ((JCORETransportResult)[data handler])(result);
}
- (BOOL)sendData:(id)data toHost:(id)host port:(unsigned short)port withTimeout:(double)timeout {
    JCOREUdpLog(YES, 8, 231, @"send udp data");
    JCOREUdpLog(YES, 0x100, 232, @"try to send udp data to host %@:%u with timeout %f", host, port, timeout);
    if (!_socket || !CFSocketIsValid(_socket)) return NO;
    NSData *address = host && port ? [self getDataAddressWithHost:host port:port] : nil;
    JCOREUdpPacket *packet = [[JCOREUdpPacket alloc] initWithType:0 data:data address:address timeout:timeout udpSocket:self];
    [self enqueueToSendQueue:packet];
    [self reenableSocketCallbacks];
    JCOREUdpLog(YES, 8, 249, @"send udp data succeed");
    return YES;
}
- (void)receiveDataWithTimeout:(double)timeout {
    [self stopReceiveTimeoutTimer];
    _recvTimeoutTimer = [NSTimer scheduledTimerWithTimeInterval:timeout target:self selector:@selector(receiveTimeout:) userInfo:nil repeats:NO];
    [self reenableSocketCallbacks];
}
- (void)stopReceiveTimeoutTimer { if (_recvTimeoutTimer.isValid) [_recvTimeoutTimer invalidate]; _recvTimeoutTimer = nil; }
- (NSUInteger)sendQueueCount { return [self count:self.sendQueue]; }
- (void)doCFSocketCallback:(CFSocketCallBackType)type forSocket:(CFSocketRef)socket withAddress:(id)address withData:(const void *)data {
    if (_socket != socket || !CFSocketIsValid(socket)) { JCOREUdpLog(NO, 1, 282, @"udp socket invalid:%lu", type); return; }
    if (type == kCFSocketWriteCallBack) [self doSend:socket];
    else if (type == kCFSocketReadCallBack) [self doRecv:socket];
    else {
        JCOREUdpLog(YES, 2, 293, @"receive unexcepted udp package");
        JCOREUdpLog(YES, 0x100, 294, @"USHAsyncUdpSocket %@ received unexpected CFSocketCallBackType %lu.", self, type);
    }
}
- (void)doSend:(CFSocketRef)socket {
    int native = CFSocketGetNative(socket);
    JCOREUdpPacket *packet = [self dequeueFromSendQueue];
    if (!packet) return;
    NSData *data = [(id)packet.buffer data];
    const void *bytes = data.bytes;
    NSUInteger length = data.length;
    JCOREUdpLog(YES, 0x100, 308, @"doSend Udp Data %@ at udp socket %@", packet, self);
    JCOREUdpLog(YES, 8, 309, @"udp socket begin send behavior");
    ssize_t sent;
    if (self.status == 2) sent = send(native, bytes, length, 0);
    else {
        NSData *address = packet.address;
        sent = sendto(native, bytes, length, 0, address.bytes, (socklen_t)address.length);
    }
    SEL selector = @selector(delayedDidSendData:);
    if ((NSUInteger)sent != length) {
        if (sent < 0) { JCOREUdpLog(NO, 1, 326, @"udp send error occured: errocode %d", errno); [self close]; }
        else {
            JCOREUdpLog(YES, 2, 332, @"upd send bytes length not expected");
            JCOREUdpLog(YES, 0x40, 333, @"udp send warning : send bytes %zu != packet bytes %lu", (size_t)sent, length);
        }
        selector = @selector(delayedDidNotSendData:);
    }
    [self performSelector:selector withObject:packet.buffer afterDelay:0];
    [self reenableSocketCallbacks];
}
- (void)doRecv:(CFSocketRef)socket {
    int native = CFSocketGetNative(socket);
    struct sockaddr_storage address;
    socklen_t addressLength = self.ipType == 1 ? sizeof(struct sockaddr_in6) : sizeof(struct sockaddr_in);
    ssize_t received = recvfrom(native, _receivingBuffer, _ulDefaultReceiveBufferSize, 0, (struct sockaddr *)&address, &addressLength);
    SEL selector = @selector(delayedDidReceivedData:);
    id result;
    if (received < 0) {
        int error = errno;
        JCOREUdpLog(NO, 1, 363, @"udp read error occured: errocode %d", error);
        [self close];
        selector = @selector(delayedDidNotReceivedDataWithError:);
        result = [NSError errorWithDomain:@"udp sys error" code:error userInfo:nil];
    } else result = [NSData dataWithBytes:_receivingBuffer length:(NSUInteger)received];
    [self performSelector:selector withObject:result afterDelay:0];
    [self reenableSocketCallbacks];
}
- (void)reenableSocketCallbacks {
    CFOptionFlags callbacks = [self sendQueueCount] ? 9 : 1;
    if (_socket && CFSocketIsValid(_socket)) CFSocketEnableCallBacks(_socket, callbacks);
}
- (void)enqueueToSendQueue:(id)packet { if (packet) [self enqueue:packet in:self.sendQueue]; }
- (id)dequeueFromSendQueue {
    JCOREUdpPacket *packet = [self firstObject:self.sendQueue];
    if (packet) { [self dequeue:packet in:self.sendQueue]; [packet invalidTimeoutTimer]; }
    return packet;
}
- (void)timeoutWithUdpPacket:(id)packet {
    [self delayedDidNotSendData:[packet buffer]];
    [self dequeue:packet in:self.sendQueue];
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
