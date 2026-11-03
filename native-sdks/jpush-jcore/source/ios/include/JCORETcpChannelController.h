#pragma once
#import "JCOREConnectManager.h"
@class JCORENetworkQueue;
typedef void (^JCORETcpChannelAck)(unsigned short status, unsigned short rid, NSData *data);
@interface JCORETcpChannelMonitor : NSObject {
    __weak NSTimer *_sentTimer;
    unsigned short _rid;
    NSData *_data;
    JCORETcpChannelAck _completion;
}
+ (void)monitor:(id)data completion:(JCORETcpChannelAck)completion;
+ (unsigned short)parseRid:(id)data;
- (void)stopSentTimer;
- (void)onAckTimeout:(id)timer;
- (unsigned short)parseStatusCode:(id)data;
- (void)receiveAckNotification:(id)notification;
@end
@interface JCORETcpChannelController : NSObject <JCOREConnectManagerDelegate> {
    BOOL _isLogined;
    BOOL _serviceUnavailable;
    unsigned short _serverVersion;
    JCORENetwork *_currentNetwork;
    JCORENetworkQueue *_tcpBlockedQueue;
    JCORENetworkQueue *_tcpSendingQueue;
}
@property (strong, nonatomic) JCORENetwork *currentNetwork;
@property (strong, nonatomic) JCORENetworkQueue *tcpBlockedQueue;
@property (strong, nonatomic) JCORENetworkQueue *tcpSendingQueue;
@property (nonatomic) BOOL isLogined;
@property (nonatomic) unsigned short serverVersion;
@property (nonatomic) BOOL serviceUnavailable;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (void)applicationDidEnterBackground;
- (void)applicationWillEnterForeground;
- (void)setupService;
- (void)send:(id)request;
- (void)stopTcpService;
- (void)resumeTcpService;
- (void)startTcpConnect;
- (void)stopTcpConnect;
- (void)sendTcpData:(id)request;
- (void)doSendTcpRequest:(id)request;
- (NSData *)requestData:(id)request;
- (NSData *)parsePacketData:(id)data;
- (void)didLogin:(id)notification;
- (void)closeSocket;
- (BOOL)loginRequired:(id)request;
- (BOOL)canBeQueuedIfNotLogin:(id)request;
- (BOOL)isExclusive:(unsigned char)flag;
- (BOOL)hasHighPriority:(unsigned char)flag;
- (BOOL)ackOrRespRequired:(id)request;
- (void)enqueueRequest:(id)request;
- (void)errorCallBack:(id)request code:(long long)code des:(id)description;
- (void)dequeueRequest:(id)request;
- (id)findBlockedRequest:(unsigned short)rid;
- (id)findSendingRequest:(unsigned short)rid;
- (BOOL)isCloseSocket;
- (void)onAckOrRespReceived:(id)response;
- (void)reenqueueSendingRequests;
@end
