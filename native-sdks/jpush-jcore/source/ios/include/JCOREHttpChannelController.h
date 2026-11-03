#pragma once
#import "RecoveredTransport.h"
@class JCORETcpReportController;
@interface JCOREHttpChannelController : NSObject {
    BOOL _stopService;
    JCOREURLSessionController *_sessionController;
    NSArray *_requestList;
    JCORETcpReportController *_tcpReportController;
    NSString *_lastSuccessReportAddress;
}
@property (strong, nonatomic) JCOREURLSessionController *sessionController;
@property (strong, nonatomic) NSArray *requestList;
@property (strong, nonatomic) JCORETcpReportController *tcpReportController;
@property (nonatomic) BOOL stopService;
@property (copy, nonatomic) NSString *lastSuccessReportAddress;
- (void)invalidate;
- (void)stopHttpService;
- (void)resumeHttpService;
- (void)send:(id)request;
- (NSArray *)tcpReportAddressesWithTypes:(NSArray *)types;
- (void)doRequest:(id)request;
- (NSArray *)onlySupportTcpReportTypeList;
- (BOOL)isOnlySupportTcpReport:(NSArray *)types;
- (void)doReport:(id)request;
- (BOOL)requesting;
- (BOOL)isRequesting:(NSString *)identifier;
- (void)addRequest:(NSString *)identifier;
- (void)removeRequest:(NSString *)identifier;
- (NSArray *)sharedArray:(NSArray *)left and:(NSArray *)right;
- (NSArray *)adjustAddressPriorityForReport:(NSArray *)addresses;
- (void)saveSuccessfulAddress:(NSString *)address;
- (NSString *)getLastSuccessfulAddress;
@end
