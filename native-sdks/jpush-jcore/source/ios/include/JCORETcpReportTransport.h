#pragma once
#import "JCORETcpReport.h"
#import "RecoveredTransport.h"
@class JCORENetworkQueue;
typedef void (^JCORETcpReportAck)(unsigned short status, unsigned short rid, NSData *data);
@interface JCORETcpReportMonitor : NSObject {
    __weak NSTimer *_sentTimer;
    unsigned short _rid;
    NSData *_data;
    JCORETcpReportAck _completion;
}
+ (void)monitor:(id)data completion:(JCORETcpReportAck)completion;
- (void)stopSentTimer;
- (void)onAckTimeout:(id)timer;
- (unsigned short)parseRequestRid:(id)data;
- (unsigned short)parseResponseRid:(id)data;
- (unsigned short)parseStatusCode:(id)data;
- (unsigned char)parseResponseVer:(id)data;
- (void)receiveAckNotification:(id)notification;
@end

@interface JCORETcpReportConnector : NSObject <JCORENetworkDataDelegate> {
    __weak NSTimer *_keepTimer;
    BOOL _startConnecting;
    NSString *_host;
    unsigned int _port;
    NSUInteger _ipType;
    JCORENetwork *_network;
    NSUInteger _connectStatus;
    JCORENetworkQueue *_tcpSendingQueue;
    JCORENetworkQueue *_tcpBlockedQueue;
    NSString *_address;
    void (^_completionHandler)(void);
}
@property (strong, nonatomic) JCORENetwork *network;
@property (nonatomic) NSUInteger connectStatus;
@property (strong, nonatomic) JCORENetworkQueue *tcpSendingQueue;
@property (strong, nonatomic) JCORENetworkQueue *tcpBlockedQueue;
@property (copy, nonatomic) NSString *address;
@property (copy, nonatomic) void (^completionHandler)(void);
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
- (instancetype)initWithAddress:(id)address;
- (void)send:(id)request;
- (void)resetKeepTimer;
- (void)invalidate;
- (void)keepTimeout:(id)timer;
- (void)startTcpConnect;
- (void)doSendTcpRequest:(id)request;
- (void)reenqueueSendingRequests;
- (NSData *)requestData:(id)request;
+ (void)errorCallBack:(id)request code:(long long)code des:(id)description;
@end

@interface JCORETcpReportManager : NSObject {
    long long _reportAddressIndex;
    __weak NSTimer *_reportTimer;
    JCORETcpReport *_report;
    NSArray *_reportAddressList;
}
@property (strong, nonatomic) JCORETcpReport *report;
@property (strong, nonatomic) NSArray *reportAddressList;
- (void)startReport;
- (BOOL)tryReportWithNextAddress;
- (id)connectorWithAddress:(id)address;
- (void)removeConnector:(id)address;
- (void)reportTimeout:(id)timer;
- (void)stopReportTimer;
@end
