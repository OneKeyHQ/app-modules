#pragma once
#import <Foundation/Foundation.h>
#import <dispatch/dispatch.h>
@interface JCORETcpEventController : NSObject {
    BOOL _isLogined, _isLoginPWDError, _isRegisterError;
    dispatch_semaphore_t _intervalLock;
    BOOL _isInMessage, _isNewDevice, _heartbeatLock;
    __weak NSTimer *_heartbeatTimer;
    long long _inMessageTimerInterval, _incrementTime;
}
@property (nonatomic) BOOL heartbeatLock;
@property (weak, nonatomic) NSTimer *heartbeatTimer;
@property (nonatomic) long long inMessageTimerInterval;
@property (nonatomic) long long incrementTime;
- (void)clearRegisterAndDeviceInfo;
- (void)doRegister;
- (void)doLogin;
- (void)reportToken:(id)token;
- (void)reportWithInvalidToken:(id)token;
- (void)reportVoipToken:(id)token;
- (void)tryUploadIDFA;
- (void)tryUploadSDKVersion;
- (void)sendReceipt:(id)response;
- (void)startHeartbeat;
- (void)resetHeartbeatLock;
- (void)startHeartbeatTimer;
- (void)startHeartbeatTimerWithInMessage;
- (void)resetTimerInterval;
- (void)stopHeartbeatTimer;
- (unsigned short)parseStatusCode:(id)data;
- (void)parseTcpData:(id)data moniterID:(id)identifier;
- (void)onRespond:(id)response;
- (void)onRegisterRespond:(id)response;
- (BOOL)clientStateWithError:(unsigned short)code;
- (void)handleAppKeyRefuesReset:(id)information;
- (void)clearDefaultConnInfo;
- (void)onLoginRespond:(id)response;
- (void)reConnect;
- (void)onControlRespond:(id)response;
- (void)onIDFAReportRespond:(id)response;
- (void)onDeviceTokenReportRespond:(id)response;
- (void)onVoipTokenReportRespond:(id)response;
- (void)receiveTcpConnected:(id)notification;
- (void)receiveTcpDisconnect:(id)notification;
- (void)receiveTcpResponse:(id)notification;
- (void)applicationWillEnterForeground;
- (void)handleCmdControl:(id)information;
- (void)beginInMessageHeart;
- (void)stopInMessage;
@end
