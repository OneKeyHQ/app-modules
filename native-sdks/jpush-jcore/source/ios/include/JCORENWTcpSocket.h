#pragma once
#import "JCORESocket.h"
#import <Network/Network.h>
@interface JCORENWTcpSocket : JCORENWSocket {
    BOOL _isDispatching;
    __weak NSTimer *_connectTimer;
    nw_connection_t _connection;
    dispatch_queue_t _connectQueue;
}
- (BOOL)setup;
- (void)handleReady;
- (void)invalidateConnectTimeOutTimer;
- (void)outTimeAction;
- (void)close;
- (void)doRead;
- (void)handleReceivedData:(id)data;
- (void)doSend;
- (void)handleSendResult:(id)data;
- (void)resetReadInfo;
- (void)recvData:(id)data;
- (void)enqueueRecvData:(id)data;
- (void)dispatchRecvData;
@end
