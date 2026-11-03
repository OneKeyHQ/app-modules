#import "JCORETcpEventController.h"
#import "JCORETcpReports.h"
#import "JCOREHeartbeat.h"
#import "JCORELogin.h"
#import "JCORERegister.h"
#import "JCORESDKControl.h"
#import "JCOREDecoder.h"
#import "JCOREClientInfo.h"
#import "JCOREIntegrate.h"
#import "JCORENetChannel.h"
#import "JCOREBusinessStorage.h"
#import "JCOREUtilities.h"
#import "JCOREThread.h"
#import "JCoreParameter.h"
#import "JCORESupersetManager.h"
#import "JCOREConnectManager.h"
#import "JCORELog.h"
#import <UIKit/UIKit.h>
#include <arpa/inet.h>
#import "JCORESisManager.h"
extern NSString *const kJCORETcpChannelConnectedNotification;
extern NSString *const kJCORETcpChannelDisconnectNotification;
extern NSString *const kJCORETcpChannelResponseNotification;
extern NSString *const kJCORETcpEventResponseNotification;
extern NSString *const kJCORENetworkDidRegisterNotification;
extern NSString *const kJCORENetworkFailedRegisterNotification;
extern NSString *const kJCORENetworkDidLoginNotification;
extern NSString *const kJCORENetworkHeartbeatResponseNotification;
extern NSString *const kJCORENetworkDeviceTokenResponseNotification;
extern NSString *const kJCORENetworkCmdCtrlNotification;
extern NSString *const kJCOREHttpReportHistoryNotification;
#define JCOREEventFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Controller/Network/JCORETcpEventController.m"
#define JCOREEventLog(sync, flagValue, sourceLine, ...) [JCORELog log:sync level:0x3ff flag:flagValue context:0 file:JCOREEventFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
@implementation JCORETcpEventController
@synthesize heartbeatLock = _heartbeatLock, heartbeatTimer = _heartbeatTimer, inMessageTimerInterval = _inMessageTimerInterval, incrementTime = _incrementTime;
- (void)dealloc { [NSNotificationCenter.defaultCenter removeObserver:self]; }
- (instancetype)init {
    if ((self = [super init])) {
        [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(receiveTcpConnected:) name:kJCORETcpChannelConnectedNotification object:nil];
        [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(receiveTcpDisconnect:) name:kJCORETcpChannelDisconnectNotification object:nil];
        [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(receiveTcpResponse:) name:kJCORETcpChannelResponseNotification object:nil];
        [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(applicationWillEnterForeground) name:UIApplicationWillEnterForegroundNotification object:nil];
        self.inMessageTimerInterval = 0;
        self.incrementTime = 15;
        _isInMessage = NO;
        _intervalLock = dispatch_semaphore_create(1);
    }
    return self;
}
- (void)clearRegisterAndDeviceInfo {
    [JCOREClientInfo setUid:0]; [JCOREClientInfo setPassword:@""]; [JCOREClientInfo setRegistrationID:@""];
    [JCOREUtilities removeFakeIMEI]; [JCOREUtilities resetDeviceID]; _isNewDevice = YES;
}
- (void)doRegister { JCORERegister *request = [[JCORERegister alloc] init]; request.isNewDevice = _isNewDevice; [JCORENetChannel send:request]; }
- (void)doLogin { [JCORENetChannel send:[[JCORELogin alloc] init]]; }
- (void)reportToken:(id)token { if (token) [JCOREClientInfo setDeviceToken:token]; [self reportWithInvalidToken:nil]; }
- (void)reportWithInvalidToken:(id)token {
    JCOREDeviceTokenReport *request = [[JCOREDeviceTokenReport alloc] init];
    if ([token isKindOfClass:NSString.class] && [token length]) request.invalidDeviceToken = token;
    [JCORENetChannel send:request];
}
- (void)reportVoipToken:(id)token {
    if (token) { [JCOREClientInfo setVoipToken:token]; [JCORENetChannel send:[[JCOREVoipTokenReport alloc] init]]; }
    else JCOREEventLog(YES, 0x100, 168, @"reportVoipToken Action Token is nil");
}
- (void)tryUploadIDFA { if (JCOREClientInfo.shouldUploadAdvertisingId) [JCORENetChannel send:[[JCOREIDFAReport alloc] init]]; }
- (void)tryUploadSDKVersion {
    if (!JCOREClientInfo.uid) return;
    for (id<JCOREIntegrateProtocol> info in JCOREIntegrate.newIntegrateList) {
        if (info.property && [info.version isKindOfClass:NSString.class]) {
            JCORESDKVersion *request = [[JCORESDKVersion alloc] init];
            request.sdkProperty = info.property;
            request.sdkVersion = [JCOREIntegrate toDisplaySDKVersion:info.version];
            unsigned char property = request.sdkProperty;
            request.completionHandler = ^(id data, NSError *error) {
                if (!error && [data isKindOfClass:NSData.class] && [self parseStatusCode:data] == 0) {
                    [JCOREIntegrate updateIntegrateMarkWith:property];
                    [JCORELog log:YES level:0x3ff flag:0x80 context:0 file:JCOREEventFile function:"-[JCORETcpEventController tryUploadSDKVersion]_block_invoke" line:201 tag:nil format:@"sdk version upload success property:%d", property];
                }
            };
            [JCORENetChannel send:request];
        }
    }
    [JCOREIntegrate cacheOriginIntegrateVersion];
}
- (void)sendReceipt:(id)response {
    if (![response conformsToProtocol:@protocol(JCORETcpReceiptProtocol)]) { JCOREEventLog(NO, 0x20, 216, @"protocol error, sendReceipt cancel"); return; }
    id<JCORETcpReceiptProtocol> model = response;
    unsigned short offset = 0;
    JCOREMessageReceipt *request = [[JCOREMessageReceipt alloc] init];
    request.messageType = [JCOREDecoder uInt8FromBuffer:model.bodyData offset:&offset];
    request.messageID = [JCOREDecoder uInt64FromBuffer:model.bodyData offset:&offset];
    request.statusCode = 0; request.version = model.version; request.protocolType = model.protocolType + 1; request.rid = model.rid;
    if (model.version > 2) request.msgData = model.msgData;
    [JCORENetChannel send:request];
}
- (void)startHeartbeat {
    JCOREEventLog(YES, 0x100, 240, @"[%s] send heartBeat ,time is %@  next timerInterval is %ld", __PRETTY_FUNCTION__, NSDate.date, (long)(_inMessageTimerInterval / 2));
    if (!JCOREThread.isMainThread) JCOREEventLog(YES, 0x100, 242, @"[%s] thread is not JCoreThread", __PRETTY_FUNCTION__);
    if (_heartbeatLock) return;
    if (_isLogined) {
        JCOREHeartbeat *request = [[JCOREHeartbeat alloc] init];
        request.heartBeatInterval = JCoreParameter.shared.heartBeatTimeInterval > 0 ? (unsigned short)JCoreParameter.shared.heartBeatTimeInterval : 290;
        request.completionHandler = ^(id response, NSError *error) { [NSNotificationCenter.defaultCenter postNotificationName:kJCORENetworkHeartbeatResponseNotification object:nil]; };
        [JCORENetChannel send:request];
        [NSNotificationCenter.defaultCenter postNotificationName:kJCOREHttpReportHistoryNotification object:nil];
        [NSTimer scheduledTimerWithTimeInterval:5 target:self selector:@selector(resetHeartbeatLock) userInfo:nil repeats:NO];
        _heartbeatLock = YES;
    } else [self stopHeartbeatTimer];
}
- (void)resetHeartbeatLock { _heartbeatLock = NO; }
- (void)startHeartbeatTimer {
    if (!JCOREThread.isMainThread) JCOREEventLog(YES, 0x100, 276, @"[%s] thread is not JCoreThread", __PRETTY_FUNCTION__);
    if (!_isInMessage) {
        [self stopHeartbeatTimer];
        double interval = JCoreParameter.shared.heartBeatTimeInterval > 0 ? (double)JCoreParameter.shared.heartBeatTimeInterval : 290;
        _heartbeatTimer = [NSTimer scheduledTimerWithTimeInterval:interval target:self selector:@selector(startHeartbeat) userInfo:nil repeats:YES];
    }
}
- (void)startHeartbeatTimerWithInMessage {
    if (!JCOREThread.isMainThread) { JCOREEventLog(YES, 0x100, 295, @"[%s] thread is not JCoreThread", __PRETTY_FUNCTION__); return; }
    [self stopHeartbeatTimer];
    if (!_isInMessage) return;
    double interval;
    if (_inMessageTimerInterval < 290) interval = (double)_inMessageTimerInterval;
    else { _inMessageTimerInterval = 290; interval = 290; }
    _heartbeatTimer = [NSTimer scheduledTimerWithTimeInterval:interval target:self selector:@selector(startHeartbeatTimerWithInMessage) userInfo:nil repeats:YES];
    if (_inMessageTimerInterval < 291) {
        dispatch_semaphore_wait(_intervalLock, DISPATCH_TIME_FOREVER);
        _inMessageTimerInterval = (long long)((unsigned long long)_inMessageTimerInterval * 2);
        dispatch_semaphore_signal(_intervalLock);
    }
    if (_inMessageTimerInterval != (long long)((unsigned long long)self.incrementTime * 2)) [self startHeartbeat];
}
- (void)resetTimerInterval {
    if (!JCOREThread.isMainThread) { JCOREEventLog(YES, 0x100, 332, @"[%s] thread is not JCOREThread", __PRETTY_FUNCTION__); return; }
    if (_isInMessage && _inMessageTimerInterval > 239) {
        dispatch_semaphore_wait(_intervalLock, DISPATCH_TIME_FOREVER);
        _inMessageTimerInterval = self.incrementTime < 1 ? 15 : self.incrementTime;
        dispatch_semaphore_signal(_intervalLock);
        JCOREEventLog(YES, 0x100, 344, @"resetTimerInterval is %ld", (long)_inMessageTimerInterval);
        [self startHeartbeatTimerWithInMessage];
    }
}
- (void)stopHeartbeatTimer {
    if (!JCOREThread.isMainThread) JCOREEventLog(YES, 0x100, 352, @"[%s] thread is not JCoreThread", __PRETTY_FUNCTION__);
    if (_heartbeatTimer.isValid) [_heartbeatTimer invalidate];
    _heartbeatTimer = nil;
}
- (unsigned short)parseStatusCode:(id)data {
    if (![data isKindOfClass:NSData.class] || ((unsigned short)[data length] & 0xfffc) <= 19 || [data length] < 22) return 0xffff;
    // The original reads two bytes at 20 even for lengths 20/21; avoid that overread.
    unsigned short value; memcpy(&value, (const uint8_t *)[data bytes] + 20, 2); return ntohs(value);
}
- (void)parseTcpData:(id)data moniterID:(id)identifier {
    if (![data isKindOfClass:NSData.class]) return;
    unsigned short offset = 2;
    JCORETcpEventModel *model = [[JCORETcpEventModel alloc] init];
    model.version = [JCOREDecoder uInt8FromBuffer:data offset:&offset];
    model.protocolType = [JCOREDecoder uInt8FromBuffer:data offset:&offset];
    model.algorithm = [JCOREDecoder uInt8FromBuffer:data offset:&offset];
    offset += 5;
    model.rid = [JCOREDecoder uInt16FromBuffer:data offset:&offset];
    model.uid = [JCOREDecoder uInt64FromBuffer:data offset:&offset];
    unsigned short bodyStart = offset;
    model.statusCode = [JCOREDecoder uInt16FromBuffer:data offset:&offset];
    if (bodyStart < [data length]) model.bodyData = [data subdataWithRange:NSMakeRange(bodyStart, [data length] - bodyStart)];
    model.moniter_id = identifier;
    [self onRespond:model];
    [NSNotificationCenter.defaultCenter postNotificationName:kJCORETcpEventResponseNotification object:nil userInfo:@{@"data":model}];
}
- (void)onRespond:(id)response {
    if (!response || ![response conformsToProtocol:@protocol(JCORETcpResponseProtocol)]) return;
    id<JCORETcpResponseProtocol> model = response;
    unsigned char type = model.protocolType;
    if (type && model.statusCode == 0) [self startHeartbeatTimer];
    if (![model.bodyData isKindOfClass:NSData.class] || !model.bodyData.length) return;
    switch (type) {
        case 0: [self onRegisterRespond:response]; break;
        case 1: [self onLoginRespond:response]; break;
        case 13: [self onDeviceTokenReportRespond:response]; break;
        case 22: [self onIDFAReportRespond:response]; break;
        case 25: [self onControlRespond:response]; break;
        case 33: [self onVoipTokenReportRespond:response]; break;
        default: break;
    }
}
- (void)onRegisterRespond:(id)response {
    id<JCORETcpResponseProtocol> model = response;
    unsigned short offset = 2;
    _isRegisterError = [self clientStateWithError:model.statusCode];
    if (_isRegisterError) [NSNotificationCenter.defaultCenter postNotificationName:kJCORENetworkFailedRegisterNotification object:nil userInfo:@{@"StatusCode":@(model.statusCode)}];
    if (model.statusCode == 0) {
        unsigned long long uid = [JCOREDecoder uInt64FromBuffer:model.bodyData offset:&offset];
        NSString *password = [JCOREDecoder stringFromBuffer:model.bodyData offset:&offset];
        NSString *registrationID = [JCOREDecoder stringFromBuffer:model.bodyData offset:&offset];
        if (!password || !registrationID) {
            [self reConnect];
            JCOREEventLog(YES, 8, 453, @"JCoreService endConnectionMoniter !!");
            [JCORESupersetManager endConnectionMoniter:model.moniter_id error_code:-1 status_code:model.statusCode];
        } else {
            JCOREEventLog(YES, 4, 459, @"%@", [NSString stringWithFormat:@"\n----- register result -----\nuid: %lld\nregistrationID:%@ \n", uid, registrationID]);
            [JCOREClientInfo setUid:uid]; [JCOREClientInfo setRegistrationID:registrationID]; [JCOREClientInfo setPassword:password]; _isNewDevice = NO;
            [NSNotificationCenter.defaultCenter postNotificationName:kJCORENetworkDidRegisterNotification object:nil userInfo:@{@"RegistrationID":registrationID, @"StatusCode":@(model.statusCode)}];
            [self doLogin];
            [JCORESupersetManager endConnectionMoniter:model.moniter_id error_code:0 status_code:0];
        }
    } else {
        JCOREEventLog(NO, 0x20, 469, @"error code: %u, register failed", model.statusCode);
        if (model.statusCode == 1012) {
            JCOREEventLog(YES, 0x100, 471, @"register appkey refuse reset");
            [self handleAppKeyRefuesReset:[JCOREUtilities dataToJSON:[JCOREDecoder dataFromBuffer:model.bodyData offset:&offset]]];
        }
        [self reConnect];
        JCOREEventLog(YES, 8, 478, @"JCoreService endConnectionMoniter !!");
        [JCORESupersetManager endConnectionMoniter:model.moniter_id error_code:-1 status_code:model.statusCode];
    }
}
- (BOOL)clientStateWithError:(unsigned short)code {
    switch (code) {
        case 1002: JCOREEventLog(NO, 1, 491, @"error code: %u，注册缺少必要参数", code); break;
        case 1003: JCOREEventLog(NO, 1, 494, @"error code: %u，注册参数错误", code); break;
        case 1004: JCOREEventLog(NO, 1, 497, @"error code: %u，注册参数校验失败", code); break;
        case 1005: JCOREEventLog(NO, 1, 503, @"error code：%u，AppKey不存在或AppKey和BundleId不匹配", code); break;
        case 1009: JCOREEventLog(NO, 1, 506, @"error code：%u，你配置的AppKey尚未配置iOS平台", code); break;
        case 1008: JCOREEventLog(NO, 1, 510, @"error code：%u，你配置的AppKey格式不正确", code); break;
        case 1010: JCOREEventLog(NO, 1, 513, @"error code：%u，你配置的advertisingIdentifier(IDFA)格式不正确", code); break;
        default: return NO;
    }
    return YES;
}
- (void)handleAppKeyRefuesReset:(id)information {
    [JCORESisManager.shared resetSisCache]; [JCORESisManager.shared removeResolver]; [self clearDefaultConnInfo];
    [JCOREClientInfo setUid:0]; [JCOREClientInfo setPassword:@""]; [JCOREClientInfo setRegistrationID:@""];
    if ([information isKindOfClass:NSDictionary.class]) {
        id srv = [information objectForKey:@"srv"], conn = [information objectForKey:@"conn"];
        if ([srv isKindOfClass:NSString.class] && [srv length]) [JCOREBusinessStorage setDefaultSRVRecord:srv];
        if ([conn isKindOfClass:NSString.class] && [conn length]) [JCOREBusinessStorage setDefaultConnHost:conn];
    }
}
- (void)clearDefaultConnInfo {
    [JCOREBusinessStorage setDefaultSRVRecord:@""]; [JCOREBusinessStorage setDefaultConnHost:@""];
    [JCOREBusinessStorage setLastSuccessSisIps:nil]; [JCOREBusinessStorage setSisReportList:@[]]; [JCOREBusinessStorage setSisIpsFromServer:@[]];
}
- (void)onLoginRespond:(id)response {
    id<JCORETcpResponseProtocol> model = response;
    unsigned short offset = 2;
    if (model.statusCode == 0) {
        unsigned int session = [JCOREDecoder uInt32FromBuffer:model.bodyData offset:&offset];
        unsigned short version = [JCOREDecoder uInt16FromBuffer:model.bodyData offset:&offset];
        [JCOREDecoder stringFromBuffer:model.bodyData offset:&offset];
        unsigned int time = [JCOREDecoder uInt32FromBuffer:model.bodyData offset:&offset];
        char idc = [JCOREDecoder sInt8FromBuffer:model.bodyData offset:&offset];
        JCOREEventLog(YES, 4, 562, @"\n----- login result -----\nuid:%lld \nregistrationID:%@ \nidc:%d", JCOREClientInfo.uid, JCOREClientInfo.registrationID, idc);
        [JCOREClientInfo setSessionID:session]; _isLogined = YES;
        [NSNotificationCenter.defaultCenter postNotificationName:kJCORENetworkDidLoginNotification object:nil userInfo:@{@"version":@(version), @"time":@(time)}];
        [self tryUploadSDKVersion]; [self tryUploadIDFA];
        [NSNotificationCenter.defaultCenter postNotificationName:kJCOREHttpReportHistoryNotification object:nil];
        JCOREEventLog(YES, 8, 573, @"JCoreService endConnectionMoniter !!");
        [JCORESupersetManager endConnectionMoniter:model.moniter_id error_code:0 status_code:0];
    } else {
        JCOREEventLog(NO, 1, 577, @"error code: %u, login failed", model.statusCode);
        if (model.statusCode == 1012) {
            JCOREEventLog(YES, 0x100, 579, @"login appkey refuse reset");
            [self handleAppKeyRefuesReset:[JCOREUtilities dataToJSON:[JCOREDecoder dataFromBuffer:model.bodyData offset:&offset]]];
        } else if (model.statusCode == 108) {
            [JCOREClientInfo setUid:0]; [JCOREClientInfo setPassword:@""]; [JCOREClientInfo setRegistrationID:@""];
            [JCOREUtilities removeFakeIMEI]; [JCOREUtilities resetDeviceID];
        } else if (model.statusCode == 102) _isLoginPWDError = YES;
        JCOREEventLog(YES, 8, 596, @"JCoreService endConnectionMoniter !!");
        [JCORESupersetManager endConnectionMoniter:model.moniter_id error_code:-1 status_code:model.statusCode];
        [self reConnect];
    }
}
- (void)reConnect {
    if (_isRegisterError) { JCOREEventLog(YES, 8, 607, @"Give up reconect,  register error"); [JCOREConnectManager.shared disConnect]; }
    else if (_isLoginPWDError) { [JCOREConnectManager.shared disConnect]; JCOREEventLog(YES, 8, 613, @"Give up reconect,  login  error"); }
    else [JCOREConnectManager.shared closeConection];
}
- (void)onControlRespond:(id)response {
    id<JCORETcpResponseProtocol> model = response;
    unsigned short offset = 0;
    unsigned long long requestID = [JCOREDecoder uInt64FromBuffer:model.bodyData offset:&offset];
    id content = [[JCOREUtilities dataToJSON:[JCOREDecoder dataFromBuffer:model.bodyData offset:&offset]] copy];
    unsigned char cmd = [content isKindOfClass:NSDictionary.class] ? [[content objectForKey:@"cmd"] intValue] : 0;
    [NSNotificationCenter.defaultCenter postNotificationName:kJCORENetworkCmdCtrlNotification object:nil userInfo:@{@"cmd_content":content ?: @{}}];
    JCOREEventLog(YES, 0x100, 633, @"ctrl content:%@", content);
    [self handleCmdControl:@{@"cmd_content":content ?: @{}}];
    JCORESDKControl *request = [[JCORESDKControl alloc] init];
    request.statusCode = 0; request.requestID = requestID; request.cmdCode = cmd;
    [JCORENetChannel send:request];
}
- (void)onIDFAReportRespond:(id)response {
    if ([(id<JCORETcpResponseProtocol>)response statusCode] == 0) { JCOREEventLog(YES, 4, 647, @"upload advertisingIdentifier  success"); [JCOREClientInfo setShouldUploadAdvertisingId:NO]; }
    else JCOREEventLog(YES, 4, 652, @"upload advertisingIdentifier  failed");
}
- (void)onDeviceTokenReportRespond:(id)response {
    if ([(id<JCORETcpResponseProtocol>)response statusCode] == 0) {
        [JCOREClientInfo setUploadedDeviceToken:JCOREClientInfo.deviceToken];
        JCOREEventLog(YES, 4, 659, @"upload device token success, current token is : %@", JCOREClientInfo.deviceToken);
        [JCOREClientInfo setDeviceTokenUpdated:YES];
    }
    NSMutableDictionary *information = [NSMutableDictionary dictionary];
    [information setObject:JCOREClientInfo.deviceToken ?: @"" forKey:@"token"];
    if (response) [information setObject:response forKey:@"data"];
    [NSNotificationCenter.defaultCenter postNotificationName:kJCORENetworkDeviceTokenResponseNotification object:nil userInfo:[information copy]];
}
- (void)onVoipTokenReportRespond:(id)response {
    [JCOREClientInfo setVoipTokenUpdated:YES];
    id<JCORETcpResponseProtocol> model = response;
    if (model.statusCode == 0) {
        [JCOREClientInfo setUploadedVoipToken:JCOREClientInfo.voipToken];
        JCOREEventLog(YES, 4, 679, @"upload voip token success, current token is : %@", JCOREClientInfo.voipToken);
    } else if (model.statusCode == 255) JCOREEventLog(YES, 0x100, 681, @"upload voiptoken failed, voip token is : %@", JCOREClientInfo.voipToken);
    else JCOREEventLog(YES, 0x100, 683, @"upload voip token Code %hu", model.statusCode);
}
- (void)receiveTcpConnected:(id)notification {
    if (JCOREClientInfo.uid && JCOREClientInfo.password && JCOREClientInfo.registrationID && ![JCOREClientInfo.registrationID isEqualToString:@""]) [self doLogin];
    else [self doRegister];
}
- (void)receiveTcpDisconnect:(id)notification { _isLogined = NO; [JCOREClientInfo setSessionID:0]; [self stopInMessage]; }
- (void)receiveTcpResponse:(id)notification {
    id information = [notification userInfo];
    if ([information isKindOfClass:NSDictionary.class] && [[information objectForKey:@"data"] isKindOfClass:NSData.class]) [self parseTcpData:[information objectForKey:@"data"] moniterID:[information objectForKey:@"moniter_id"]];
}
- (void)applicationWillEnterForeground {
    [JCOREThread performSel:@selector(resetTimerInterval) target:self withObject:nil];
    JCOREEventLog(YES, 0x100, 724, @"[%s] applicationWillEnterForeground startHeartbeat", __PRETTY_FUNCTION__);
    [JCOREThread performSel:@selector(startHeartbeat) target:self withObject:nil];
}
- (void)handleCmdControl:(id)information {
    if (!JCOREThread.isMainThread) { JCOREEventLog(YES, 0x100, 734, @"[%s] thread is not JCOREThread", __PRETTY_FUNCTION__); return; }
    id command = [information isKindOfClass:NSDictionary.class] ? [information objectForKey:@"cmd_content"] : nil;
    if (![command isKindOfClass:NSDictionary.class]) { JCOREEventLog(YES, 0x100, 742, @"handle cmd control cancel, cmd content nil"); return; }
    id content = [command objectForKey:@"content"];
    if (![content isKindOfClass:NSDictionary.class]) { JCOREEventLog(YES, 0x40, 747, @"handle cmd control cancel, content error"); return; }
    if (![content count]) { JCOREEventLog(YES, 0x100, 752, @"handle cmd control cancel, content nil"); return; }
    NSInteger interval = [[content objectForKey:@"hb_interval"] integerValue];
    self.incrementTime = interval < 1 ? 15 : interval;
    id cmd = [command objectForKey:@"cmd"];
    if (!cmd) { JCOREEventLog(YES, 0x100, 765, @"handle cmd control cancel, cmd nil"); return; }
    if ([cmd charValue] == 75) [self beginInMessageHeart];
}
- (void)beginInMessageHeart {
    if (!JCOREThread.isMainThread) { JCOREEventLog(YES, 0x100, 784, @"[%s] thread is not mainThread", __PRETTY_FUNCTION__); return; }
    if (_isInMessage) { JCOREEventLog(YES, 0x100, 789, @"[%s] is already inMessage", __PRETTY_FUNCTION__); return; }
    [self stopHeartbeatTimer]; _isInMessage = YES;
    dispatch_semaphore_wait(_intervalLock, DISPATCH_TIME_FOREVER);
    _inMessageTimerInterval = self.incrementTime;
    dispatch_semaphore_signal(_intervalLock);
    [self startHeartbeatTimerWithInMessage];
    JCOREEventLog(YES, 0x100, 802, @"will begin InMessage heartBeat ,time is %@", NSDate.date);
}
- (void)stopInMessage {
    JCOREEventLog(YES, 0x100, 806, @"[%s] stopInMessage", __PRETTY_FUNCTION__);
    if (!JCOREThread.isMainThread) { JCOREEventLog(YES, 0x100, 808, @"[%s] thread is not JCoreThread", __PRETTY_FUNCTION__); return; }
    _isInMessage = NO; self.incrementTime = 15; self.inMessageTimerInterval = 0;
}
@end
