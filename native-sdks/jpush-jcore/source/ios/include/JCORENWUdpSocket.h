#pragma once
#import "JCORESocket.h"
#import <Network/Network.h>
@interface JCORENWUdpSocket : JCORENWSocket {
    __weak NSTimer *_recvTimeoutTimer;
    nw_connection_t _connection;
    dispatch_queue_t _connectQueue;
}
- (BOOL)setup;
- (void)handleReady;
- (void)close;
- (BOOL)sendData:(id)data toHost:(id)host port:(unsigned short)port withTimeout:(double)timeout;
- (void)receiveDataWithTimeout:(double)timeout;
- (void)stopReceiveTimeoutTimer;
- (NSUInteger)sendQueueCount;
- (void)timeoutWithUdpPacket:(id)packet;
- (void)doSend;
- (void)handleSendError:(id)packet;
- (void)handleSendResult:(id)packet;
- (void)doRead;
- (void)handleReceivedData:(id)data;
- (void)handleReceiveDataError:(id)data;
- (void)enqueueToSendQueue:(id)packet;
- (id)dequeueFromSendQueue;
- (void)receiveTimeout:(id)timer;
- (void)delayedDidSendData:(id)data;
- (void)delayedDidNotSendData:(id)data;
- (void)delayedDidReceivedData:(id)data;
- (void)delayedDidNotReceivedDataWithError:(id)error;
@end
