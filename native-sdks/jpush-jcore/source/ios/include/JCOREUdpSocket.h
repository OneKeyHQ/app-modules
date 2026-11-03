#pragma once
#import "JCORESocket.h"
@interface JCOREUdpSocket : JCORESocket {
    __weak NSTimer *_recvTimeoutTimer;
}
- (BOOL)setup;
- (void)close;
- (BOOL)sendData:(id)data toHost:(id)host port:(unsigned short)port withTimeout:(double)timeout;
- (void)receiveDataWithTimeout:(double)timeout;
- (void)stopReceiveTimeoutTimer;
- (NSUInteger)sendQueueCount;
- (void)doCFSocketCallback:(CFSocketCallBackType)type forSocket:(CFSocketRef)socket withAddress:(id)address withData:(const void *)data;
- (void)doSend:(CFSocketRef)socket;
- (void)doRecv:(CFSocketRef)socket;
- (void)enqueueToSendQueue:(id)packet;
- (id)dequeueFromSendQueue;
- (void)timeoutWithUdpPacket:(id)packet;
- (void)receiveTimeout:(id)timer;
- (void)delayedDidSendData:(id)data;
- (void)delayedDidNotSendData:(id)data;
- (void)delayedDidReceivedData:(id)data;
- (void)delayedDidNotReceivedDataWithError:(id)error;
@end
