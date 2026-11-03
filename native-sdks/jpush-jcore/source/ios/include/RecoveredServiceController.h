#pragma once
#import <Foundation/Foundation.h>
@class JCORETcpEventController,JCOREHttpEventController;
@interface JCOREServiceController : NSObject {
    BOOL _hasSetup,_getMacSuccess,_sentryCrashHasStart,_sentryCrashLogHasStart,_crashHasStart;
    JCORETcpEventController *_tcpEventController;
    JCOREHttpEventController *_httpEventController;
}
@property(nonatomic) BOOL hasSetup,getMacSuccess,sentryCrashHasStart,sentryCrashLogHasStart,crashHasStart;
@property(nonatomic,strong) JCORETcpEventController *tcpEventController;
@property(nonatomic,strong) JCOREHttpEventController *httpEventController;
@property(nonatomic,copy) NSDictionary *locationInfo;
- (void)setupService:(id)config;
- (void)checkNeedFingerReRegister;
- (void)setNewAdvertisingId:(id)identifier;
- (void)setCrashLogON;
- (void)setSentryCrashCountON;
- (void)setSentryCrashLogON;
- (void)setSentryCrashON;
- (void)setSupersetMoniterOn;
- (void)clearSupersetMoniterCache;
- (void)setNetDataMoniterOn;
- (void)forceRequestReportAddress;
- (void)apiRecord:(id)arguments;
- (void)getDeviceMacAddress;
- (void)cacheDeviceMacAddress:(id)address;
- (void)reportToken:(id)token;
- (void)reportWithInvalidToken:(id)token;
- (void)sendReceipt:(id)receipt;
- (void)reportVoipToken:(id)token;
@end
