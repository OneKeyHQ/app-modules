#pragma once
#import "JCORESocket.h"
#import <Security/Security.h>
@interface JCORESSLSocket : JCORESocket {
@public
    BOOL _isDispatching;
    NSMutableArray *tmpArray;
    __weak NSTimer *_connectTimer;
    CFStreamClientContext streamContext;
    CFReadStreamRef readStream;
    CFWriteStreamRef writeStream;
    unsigned int flags;
    int clientSocket;
}
- (BOOL)setup;
- (void)outTimeAction;
- (void)close;
- (void)doRead;
- (void)doSend;
- (void)resetReadInfo;
- (void)recvData:(id)data;
- (void)enqueueRecvData:(id)data;
- (void)dispatchRecvData;
- (void)startSSL;
- (BOOL)createReadAndWriteStream;
- (BOOL)registerForStreamCallbacksIncludingReadWrite:(BOOL)includeReadWrite;
- (BOOL)addStreamsToRunLoop;
- (void)removeStreamsFromRunLoop;
- (BOOL)openStreams;
- (void)cf_abortSSLHandshake;
- (void)cf_finishSSLHandshake;
- (NSData *)convertCerStringToData:(id)string;
- (BOOL)checkCert:(SecTrustRef)trust;
@end
