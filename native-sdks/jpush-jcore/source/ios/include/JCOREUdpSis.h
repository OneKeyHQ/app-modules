#pragma once
#import "RecoveredTransport.h"
typedef void (^JCOREUdpSisCompletion)(NSArray *addresses, NSError *error);
@interface JCOREUdpSis : NSObject <JCORENetworkDataDelegate> {
    JCORENetwork *_udpSocket;
    NSArray *_sendingSisReportData;
    unsigned int _port;
    __weak NSTimer *_sisRequestTimer;
    JCOREUdpSisCompletion _sisCompletion;
    NSString *_host;
}
@property(weak, nonatomic) NSTimer *sisRequestTimer;
@property(copy, nonatomic) JCOREUdpSisCompletion sisCompletion;
@property(copy, nonatomic) NSString *host;
@property(nonatomic) unsigned int port;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
- (void)sendSisRequest:(id)host port:(unsigned short)port completion:(JCOREUdpSisCompletion)completion;
- (void)startSisRequestTimer:(double)timeout;
- (void)sisRequestTimeOut;
- (void)stopSisRequestTimer;
- (void)callbackSisCompletion:(id)addresses;
- (void)resetUdpSocket;
- (void)reset;
- (NSDictionary *)sisRequestInfo;
- (NSData *)requestData;
- (NSMutableData *)assemble:(id)data magic:(char)magic key:(unsigned int)key;
- (id)didReceivedSisData:(id)data;
- (void)parse:(id)data ip:(id)ip port:(unsigned short)port;
- (void)unsentData:(id)data socket:(id)socket;
- (void)socket:(id)socket recvData:(id)data handler:(void (^)(BOOL))handler;
- (void)socket:(id)socket sentData:(id)data;
- (void)socket:(id)socket noRecvData:(id)data;
- (void)socket:(id)socket unsentData:(id)data;
@end
