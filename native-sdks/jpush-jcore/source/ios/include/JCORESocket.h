#pragma once
#import "RecoveredTransport.h"
@class JCOREFIFOQueue;
@protocol JCORENetworkSocketDelegate <NSObject>
- (void)recvData:(id)data socket:(id)socket;
- (void)sentData:(id)data socket:(id)socket;
- (void)sendQueueEmpty:(id)socket;
- (void)connectStateChanged:(id)state socket:(id)socket;
- (void)noRecvData:(id)data socket:(id)socket;
- (void)unsentData:(id)data socket:(id)socket;
@end
@interface JCORESocket : NSObject {
    CFSocketRef _socket;
    CFRunLoopSourceRef _rls;
    NSUInteger _ulReceivedLen;
    char *_receivingBuffer;
    NSUInteger _ulDefaultReceiveBufferSize;
    NSUInteger _ulSendingDataSentLen;
    BOOL _isSetuped;
    NSUInteger _status;
}
@property (copy, nonatomic) NSString *host;
@property (nonatomic) unsigned int port;
@property (nonatomic) NSUInteger ipType;
@property (nonatomic) BOOL careAllDataSent;
@property (nonatomic) NSUInteger status;
@property (weak, nonatomic) id<JCORENetworkSocketDelegate> delegate;
@property (strong, nonatomic) id<JCORESocketConfigProtocol> config;
@property (readonly, nonatomic) JCOREFIFOQueue *sendQueue;
@property (readonly, nonatomic) JCOREFIFOQueue *recvQueue;
- (void)changeIpAddressType;
- (void)connect;
- (void)disconect;
- (BOOL)isConnected;
- (void)sendData:(id)data;
- (void)reenableSocketCallbacks;
- (void)p_cleanSocket;
- (NSUInteger)count:(id)queue;
- (id)firstObject:(id)queue;
- (void)enqueue:(id)object in:(id)queue;
- (void)dequeue:(id)object in:(id)queue;
- (NSData *)getDataAddress;
- (NSData *)getDataAddressWithHost:(NSString *)host port:(unsigned int)port;
@end

@interface JCORENWSocket : JCORESocket
- (dispatch_data_t)dispatchDataFromNSData:(NSData *)data;
- (NSData *)nsDataFromDispatchData:(dispatch_data_t)data;
@end
