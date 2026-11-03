#import "JCORESocketController.h"
#import "RecoveredCoreService.h"

// Declarations for the actual socket implementations selected below.
#import "JCORETcpSocket.h"
#import "JCORESSLSocket.h"
#import "JCOREUdpSocket.h"
#import "JCORENWTcpSocket.h"
#import "JCORENWSSLSocket.h"
#import "JCORENWUdpSocket.h"

#pragma clang diagnostic push
// These two original getters intentionally retain separate, unwritten ivars.
#pragma clang diagnostic ignored "-Wunused-property-ivar"
@implementation JCORESocketController
@dynamic hash, superclass, description, debugDescription;
@synthesize type = _type, delegate = _delegate, config = _config;
@synthesize careAllDataSent = _careAllDataSent, reqThread = _reqThread, socket = _socket;
+ (instancetype)socket:(NSUInteger)type { return [[JCORESocketController alloc] initWithType:type]; }
- (instancetype)initWithType:(NSUInteger)type { self = [super init]; if (self) _type = type; return self; }
- (void)dealloc { _socket = nil; }
- (JCORESocket *)socket {
    if (!_socket) {
        Class socketClass;
        if (@available(iOS 12.0, *)) {
            if (self.type == 0) {
                BOOL ssl = [JCOREService canStartTcpSSL];
                BOOL network = [JCOREService getNetSocketSwitch];
                socketClass = ssl ? (network ? JCORENWSSLSocket.class : JCORESSLSocket.class) : (network ? JCORENWTcpSocket.class : JCORETcpSocket.class);
            } else socketClass = [JCOREService getNetSocketSwitch] ? JCORENWUdpSocket.class : JCOREUdpSocket.class;
        } else {
            socketClass = self.type == 0 ? ([JCOREService canStartTcpSSL] ? JCORESSLSocket.class : JCORETcpSocket.class) : JCOREUdpSocket.class;
        }
        _socket = [[socketClass alloc] init];
    }
    return _socket;
}
- (void)setReqThread:(NSThread *)thread { _reqThread = thread; }
- (void)setConfig:(id<JCORESocketConfigProtocol>)config { [self.socket setConfig:config]; }
- (void)setDelegate:(id<JCORENetworkDelegate>)delegate { _delegate = delegate; [self.socket setDelegate:self]; }
- (void)remove { _socket = nil; }
- (void)connect { [self.socket connect]; }
- (void)disconect { if (_socket) [_socket disconect]; }
- (void)sendData:(id)data { if (_socket) [_socket sendData:data]; }
- (void)setCareAllDataSent:(NSNumber *)value { if (_socket) [_socket setCareAllDataSent:value.boolValue]; }
- (void)recvData:(id)data socket:(id)socket { if ([socket isEqual:_socket] && self.delegate) [self.delegate recvData:data]; }
- (void)sentData:(id)data socket:(id)socket { if ([socket isEqual:_socket] && self.delegate) [self.delegate sentData:data]; }
- (void)sendQueueEmpty:(id)socket { if ([socket isEqual:_socket] && self.delegate) [self.delegate sendQueueEmpty]; }
- (void)noRecvData:(id)data socket:(id)socket { if ([socket isEqual:_socket] && self.delegate) [self.delegate noRecvData:data]; }
- (void)unsentData:(id)data socket:(id)socket { if ([socket isEqual:_socket] && self.delegate) [self.delegate unsentData:data]; }
- (void)connectStateChanged:(id)state socket:(id)socket { if ([socket isEqual:_socket] && self.delegate) [self.delegate connectStateChanged:state]; }
@end
#pragma clang diagnostic pop
