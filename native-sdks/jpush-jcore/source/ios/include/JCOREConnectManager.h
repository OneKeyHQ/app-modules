#pragma once
#import "RecoveredTransport.h"
@protocol JCOREConnectManagerDelegate <NSObject>
- (void)socket:(id)socket recvData:(id)data handler:(void (^)(BOOL result))handler;
- (void)socket:(id)socket sentData:(id)data;
@optional
- (void)sendQueueEmpty:(id)socket;
- (void)socket:(id)socket noRecvData:(id)data;
- (void)socket:(id)socket unsentData:(id)data;
- (void)socket:(id)socket connectStateChanged:(NSUInteger)state;
@end
#import "JCOREConnectPool.h"
@class JCORESisManager;
typedef void (^JCOREConnectSisCompletion)(NSArray *addresses);
@interface JCOREConnectManager : NSObject <JCOREConnectPoolDelegate, JCORENetworkDataDelegate> {
    BOOL _isConnected;
    BOOL _isConnecting;
    BOOL _isSetup;
    BOOL _responseError;
    __weak id<JCOREConnectManagerDelegate> _delegate;
    JCORESisManager *_sisManager;
    JCORENetwork *_socket;
    NSUInteger _delaySetupTimeIntervalIndex;
    __weak NSTimer *_delaySetupTimer;
    NSUInteger _addressCursor;
}
@property(nonatomic) BOOL isConnected;
@property(nonatomic) BOOL isConnecting;
@property(nonatomic) BOOL isSetup;
@property(nonatomic) BOOL responseError;
@property(weak, nonatomic) id<JCOREConnectManagerDelegate> delegate;
@property(strong, nonatomic) JCORESisManager *sisManager;
@property(strong, nonatomic) JCORENetwork *socket;
@property(nonatomic) NSUInteger delaySetupTimeIntervalIndex;
@property(weak, nonatomic) NSTimer *delaySetupTimer;
@property(nonatomic) NSUInteger addressCursor;
@property(readonly) NSUInteger hash;
@property(readonly) Class superclass;
@property(readonly, copy) NSString *description;
@property(readonly, copy) NSString *debugDescription;
+ (instancetype)shared;
- (instancetype)init;
- (void)resetDelaySetupIndex;
- (void)connect;
- (void)disConnect;
- (void)reConnect;
- (void)beginConnect;
- (void)closeConection;
- (void)networkingReachabilityDidChange:(id)notification;
- (void)reachabilityChanged:(id)notification;
- (void)didRespond:(id)response;
- (void)didRegisterSucceed:(id)config;
- (void)didRegisterFailed:(id)config error:(NSError *)error;
- (void)didLoginSucceed:(id)config;
- (void)didLoginFailed:(id)config error:(NSError *)error;
- (void)getSisResult:(JCOREConnectSisCompletion)completion;
- (id)getLastSucceedConnectionAddress;
- (id)defaultDomainAddress;
- (id)getConAddress;
- (void)connectWithIps:(id)ips asy:(BOOL)async;
- (void)continueGetConAddress;
- (void)getSisResultAgain;
- (void)doDelaySetup:(id)timer;
- (void)startDelaySetupTimer:(double)interval;
- (void)startReachableDelaySetupTimer;
- (void)startUnReachableDelaySetupTimer;
- (void)stopDelaySetupTimer;
@end
