#pragma once
#import "RecoveredTransport.h"
#import "RecoveredProtocols.h"
@interface JCOREHttpRequestController : NSObject {
    long long _currentIndex;
    NSArray *_addressList;
    NSMutableURLRequest *_urlRequest;
    NSString *_requestId;
    __weak JCOREURLSessionController *_sessionController;
    RecoveryOpaqueCallback _completionHandler;
}
@property (strong, nonatomic) NSArray *addressList;
@property (strong, nonatomic) NSMutableURLRequest *urlRequest;
@property (copy, nonatomic) NSString *requestId;
@property (weak, nonatomic) JCOREURLSessionController *sessionController;
@property (copy, nonatomic) RecoveryOpaqueCallback completionHandler;
- (void)send;
- (void)sendRequest;
- (void)handleRespond:(NSDictionary *)information;
- (BOOL)hasNextRequest;
- (NSString *)getHttpNetworkErrorStringWith:(long long)code;
@end
