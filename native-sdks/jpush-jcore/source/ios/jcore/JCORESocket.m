#import "JCORESocket.h"
#import "JCOREFIFOQueue.h"
#include <arpa/inet.h>
@interface JCORESocket () {
    JCOREFIFOQueue *_sendQueue;
    JCOREFIFOQueue *_recvQueue;
    BOOL _careAllDataSent;
    unsigned int _port;
    __weak id<JCORENetworkSocketDelegate> _delegate;
    id<JCORESocketConfigProtocol> _config;
    NSString *_host;
    NSUInteger _ipType;
}
@end
@implementation JCORESocket
@synthesize host = _host, port = _port, ipType = _ipType, careAllDataSent = _careAllDataSent;
@synthesize status = _status, delegate = _delegate, config = _config;
- (void)setConfig:(id<JCORESocketConfigProtocol>)config {
    _config = config;
    self.host = config.host;
    self.port = config.port;
    self.ipType = config.ipType;
}
- (void)setDelegate:(id<JCORENetworkSocketDelegate>)delegate { _delegate = delegate; }
- (void)setIpType:(NSUInteger)type { if (_ipType != type) { _ipType = type; [self changeIpAddressType]; } }
// These base selectors are empty in the original; concrete sockets override them.
- (void)changeIpAddressType {}
- (void)connect {}
- (void)disconect {}
- (BOOL)isConnected { return self.status == 2; }
- (void)sendData:(id)data {}
- (void)reenableSocketCallbacks {}
- (void)setCareAllDataSent:(BOOL)value { _careAllDataSent = value; [self reenableSocketCallbacks]; }
- (void)p_cleanSocket {
    if (_socket) CFSocketDisableCallBacks(_socket, CFSocketGetSocketFlags(_socket));
    if (_rls) {
        CFRunLoopRemoveSource(CFRunLoopGetCurrent(), _rls, kCFRunLoopDefaultMode);
        CFRelease(_rls);
        _rls = NULL;
    }
    if (_socket) {
        if (CFSocketIsValid(_socket)) CFSocketInvalidate(_socket);
        CFRelease(_socket);
        _socket = NULL;
    }
}
- (NSUInteger)count:(id)queue { return [queue isKindOfClass:JCOREFIFOQueue.class] ? [queue count] : 0; }
- (id)firstObject:(id)queue { return [queue isKindOfClass:JCOREFIFOQueue.class] ? [queue firstObject] : nil; }
- (void)enqueue:(id)object in:(id)queue { if ([queue isKindOfClass:JCOREFIFOQueue.class]) [queue enqueue:object]; }
- (void)dequeue:(id)object in:(id)queue { if ([queue isKindOfClass:JCOREFIFOQueue.class]) [queue dequeue:object]; }
- (JCOREFIFOQueue *)sendQueue { if (!_sendQueue) _sendQueue = [[JCOREFIFOQueue alloc] initWithMaxItem:0]; return _sendQueue; }
- (JCOREFIFOQueue *)recvQueue { if (!_recvQueue) _recvQueue = [[JCOREFIFOQueue alloc] initWithMaxItem:0]; return _recvQueue; }
- (NSData *)getDataAddress { NSString *host = self.host; unsigned int port = self.port; return [self getDataAddressWithHost:host port:port]; }
- (NSData *)getDataAddressWithHost:(NSString *)host port:(unsigned int)port {
    if (!host || !host.length) return nil;
    if (self.ipType == 1) {
        struct sockaddr_in6 address = {0};
        address.sin6_len = sizeof(address);
        address.sin6_family = AF_INET6;
        address.sin6_port = htons((uint16_t)port);
        (void)inet_pton(AF_INET6, host.UTF8String, &address.sin6_addr);
        return [NSData dataWithBytes:&address length:sizeof(address)];
    }
    struct sockaddr_in address = {0};
    address.sin_len = sizeof(address);
    address.sin_family = AF_INET;
    address.sin_port = htons((uint16_t)port);
    if ([host isEqualToString:@"localhost"] || [host isEqualToString:@"loopback"]) address.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    else {
        address.sin_addr.s_addr = inet_addr(host.UTF8String);
        if (address.sin_addr.s_addr == INADDR_NONE) return nil;
    }
    return [NSData dataWithBytes:&address length:sizeof(address)];
}
@end
