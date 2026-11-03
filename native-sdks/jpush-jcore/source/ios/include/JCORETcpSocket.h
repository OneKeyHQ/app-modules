#pragma once
#import "JCORESocket.h"
@interface JCORETcpSocket : JCORESocket {
    BOOL _isDispatching;
    NSMutableArray *tmpArray;
    __weak NSTimer *_connectTimer;
}
- (BOOL)setup;
- (void)outTimeAction;
- (void)close;
- (void)doCFSocketCallback:(CFSocketCallBackType)type forSocket:(CFSocketRef)socket withData:(CFDataRef)data;
- (void)onConnectedWithErrorCode:(CFDataRef)error;
- (void)doRead;
- (void)doSend;
- (void)resetReadInfo;
- (void)doCheck;
- (void)recvData:(id)data;
- (void)enqueueRecvData:(id)data;
- (void)dispatchRecvData;
@end
