#import "JCORETcpChannelController.h"
#import "JCORENetworkQueue.h"
#import "JCORETcpObject.h"
#import "JCORELogin.h"
#import "JCORERegister.h"
#import "JCOREClientInfo.h"
#import "JCOREIntegrate.h"
#import "JCOREDataSecurity.h"
#import "JCORENetDataManager.h"
#import "JCORESupersetManager.h"
#import "JCommonService.h"
#import "JCoreParameter.h"
#import "JCOREPropertyModels.h"
#import "JCOREThread.h"
#import "JCORELog.h"
#import <UIKit/UIKit.h>
#include <arpa/inet.h>
extern NSString *const kJCORENetworkDidLoginNotification;
#define JCOREChannelFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Controller/Network/JCORETcpChannelController.m"
#define JCOREChannelLog(sync, flagValue, sourceLine, ...) [JCORELog log:sync level:0x3ff flag:flagValue context:0 file:JCOREChannelFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
static void endConnectionMoniter(NSString *identifier, NSInteger error, NSInteger status) {
    [JCORELog log:YES level:0x3ff flag:8 context:0 file:JCOREChannelFile function:"void endConnectionMoniter(NSString *__strong, NSInteger, NSInteger)" line:477 tag:nil format:@"JCoreService endConnectionMoniter !!"];
    [JCORESupersetManager endConnectionMoniter:identifier error_code:error status_code:status];
}
static void startTcpEventMoniter(id<JCORETcpProtocol> request) {
    [JCORELog log:YES level:0x3ff flag:8 context:0 file:JCOREChannelFile function:"void startTcpEventMoniter(__strong id<JCORETcpProtocol>)" line:275 tag:nil format:@"JCoreService startTcpEventMoniter !!"];
    if ([request isKindOfClass:JCORELogin.class]) request.moniter_id = [JCORESupersetManager startTcpEventMoniter:@"NET_TCP_LOGIN"];
    else if ([request isKindOfClass:JCORERegister.class]) request.moniter_id = [JCORESupersetManager startTcpEventMoniter:@"NET_TCP_REGISTER"];
}
@implementation JCORETcpChannelController
@dynamic hash, superclass, description, debugDescription;
@synthesize currentNetwork = _currentNetwork, tcpBlockedQueue = _tcpBlockedQueue, tcpSendingQueue = _tcpSendingQueue, isLogined = _isLogined, serverVersion = _serverVersion, serviceUnavailable = _serviceUnavailable;
- (void)dealloc { [NSNotificationCenter.defaultCenter removeObserver:self]; }
- (instancetype)init {
    if ((self = [super init])) {
        _tcpBlockedQueue = [[JCORENetworkQueue alloc] initWithMaxItem:100 withKeyProperyName:@"rid"];
        _tcpSendingQueue = [[JCORENetworkQueue alloc] initWithMaxItem:100 withKeyProperyName:@"rid"];
        [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(didLogin:) name:kJCORENetworkDidLoginNotification object:nil];
        [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(applicationDidEnterBackground) name:UIApplicationDidEnterBackgroundNotification object:nil];
        [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(applicationWillEnterForeground) name:UIApplicationWillEnterForegroundNotification object:nil];
    }
    return self;
}
- (void)applicationDidEnterBackground { if (!JCoreParameter.shared.backgroundEnable) [JCOREThread performSel:@selector(stopTcpConnect) target:self withObject:nil]; }
- (void)applicationWillEnterForeground { if (!JCoreParameter.shared.backgroundEnable) [JCOREThread performSel:@selector(startTcpConnect) target:self withObject:nil]; }
- (void)setupService { [JCORENetwork startThread]; [self startTcpConnect]; }
- (void)send:(id)request { [self sendTcpData:request]; }
- (void)stopTcpService { JCOREChannelLog(YES, 0x100, 219, @"stopTcpService"); self.serviceUnavailable = YES; [self stopTcpConnect]; }
- (void)resumeTcpService { JCOREChannelLog(YES, 0x100, 225, @"resumeTcpService"); self.serviceUnavailable = NO; [self startTcpConnect]; }
- (void)startTcpConnect {
    if (self.serviceUnavailable) { JCOREChannelLog(YES, 0x100, 237, @"tcp service is stoped, can not start connect"); return; }
    JCOREConnectManager.shared.delegate = self;
    [JCOREConnectManager.shared connect];
}
- (void)stopTcpConnect { [JCOREConnectManager.shared disConnect]; }
- (void)sendTcpData:(id)request {
    if (![request conformsToProtocol:@protocol(JCORETcpProtocol)]) { JCOREChannelLog(NO, 0x20, 253, @"send tcp object error, be canceled"); return; }
    if (![self loginRequired:request] || _isLogined) { [self doSendTcpRequest:request]; return; }
    if ([self canBeQueuedIfNotLogin:request]) { [self enqueueRequest:request]; return; }
    if ([request respondsToSelector:@selector(completionHandler)] && [request completionHandler]) {
        NSError *error = [NSError errorWithDomain:@"JIGUANGTcpErrorDomain" code:2002 userInfo:@{NSLocalizedDescriptionKey:@"未登录状态，请求被丢弃"}];
        ((RecoveryOpaqueCallback)[request completionHandler])(request, error);
    }
    JCOREChannelLog(YES, 8, 267, @"未登录状态，请求被丢弃");
    JCOREChannelLog(YES, 0x100, 268, @"未登录状态请求rid: %d", [request rid]);
}
- (void)doSendTcpRequest:(id)request {
    JCOREChannelLog(YES, 8, 289, @"Action - doSendTcpRequest");
    if (![request conformsToProtocol:@protocol(JCORETcpProtocol)]) { JCOREChannelLog(NO, 0x20, 291, @"send tcp object error, be canceled"); return; }
    NSData *data = [self requestData:request];
    if (!data) { [self errorCallBack:request code:2003 des:@"request packet data error"]; return; }
    startTcpEventMoniter(request);
    [JCORENetDataManager startSocketMoniter:2 host:self.currentNetwork.config.host port:self.currentNetwork.config.port request_byte:(long long)data.length];
    JCORESocketData *socketData = [[JCORESocketData alloc] init];
    socketData.data = data;
    socketData.handler = ^(BOOL sent) {
        [self.tcpSendingQueue enqueue:request];
        if (!sent) {
            if ([request isKindOfClass:JCORELogin.class] || [request isKindOfClass:JCORERegister.class]) endConnectionMoniter([request moniter_id], -1, 0);
            JCOREChannelLog(YES, 8, 319, @"当前网络未连接，发送请求失败");
            [JCOREConnectManager.shared connect];
        }
    };
    [self.currentNetwork sendData:socketData];
}
- (NSData *)requestData:(id)request {
    NSData *head = [request headData];
    NSUInteger headerLength = head.length;
    NSMutableData *data = [[NSMutableData alloc] initWithData:head];
    [data appendData:[request bodyData]];
    unsigned short bodyLength = (unsigned short)((unsigned int)data.length - (unsigned int)headerLength);
    if (bodyLength) {
        void *body = calloc(1, bodyLength);
        if (!body) return nil;
        [data getBytes:body range:NSMakeRange((unsigned short)headerLength, bodyLength)];
        NSData *plain = [NSData dataWithBytes:body length:bodyLength];
        NSString *key = [JCOREDataSecurity encryptKeyWithSeed:[(id<JCORETcpProtocol>)request seed]];
        NSString *iv = [key substringWithRange:NSMakeRange(0, 16)];
        NSInteger type = [NSUserDefaults.standardUserDefaults integerForKey:@"kJIGUANGEncryptType"];
        NSData *encrypted = type == 1 ? [JCOREDataSecurity sm4CBCEncryptData:plain withKey:key iv:iv.UTF8String] : [JCOREDataSecurity aes256EncryptData:plain withKey:key iv:iv.UTF8String];
        if (encrypted) {
            unsigned short length = (unsigned short)encrypted.length;
            void *bytes = calloc(1, length);
            if (!bytes && length) { free(body); return nil; }
            [encrypted getBytes:bytes length:length];
            [data replaceBytesInRange:NSMakeRange((unsigned short)headerLength, length) withBytes:bytes];
            free(bytes);
        }
        free(body);
    }
    if (![data jcore_setPacketLen]) {
        if ([request respondsToSelector:@selector(completionHandler)] && [request completionHandler]) {
            NSError *error = [NSError errorWithDomain:@"JIGUANGTcpErrorDomain" code:6026 userInfo:@{NSLocalizedDescriptionKey:@"tcp packet length too long"}];
            ((RecoveryOpaqueCallback)[request completionHandler])(data, error);
        }
        return nil;
    }
    unsigned char flag;
    [data getBytes:&flag range:NSMakeRange(0, 1)];
    flag |= 0x80;
    [data replaceBytesInRange:NSMakeRange(0, 1) withBytes:&flag];
    return [data copy];
}
- (NSData *)parsePacketData:(id)data {
    if (![data isKindOfClass:NSData.class]) { [JCommonService recordTcpException:1301 message:@""]; return nil; }
    const unsigned char *bytes = [data bytes];
    unsigned short inputLength = (unsigned short)[data length];
    if (inputLength <= 5) { [JCommonService recordTcpException:1301 message:@""]; return nil; }
    unsigned short raw;
    memcpy(&raw, bytes, 2);
    unsigned short header = ntohs(raw);
    NSUInteger length = header & 0x7fff;
    if (length > inputLength) { [JCommonService recordTcpException:1300 message:@""]; return nil; }
    unsigned char *packet = calloc(1, length);
    if (!packet && length) return nil;
    if (length) memmove(packet, bytes, length);
    if (header & 0x8000) {
        // Reject the original's truncated encrypted header before accessing byte 3.
        if (length < 4) { free(packet); [JCommonService recordTcpException:1301 message:@""]; return nil; }
        unsigned char version = packet[3];
        packet[0] = 0;
        if (length > 20) {
            NSData *encrypted = [NSData dataWithBytes:packet + 20 length:length - 20];
            uint64_t seed = 0;
            if (version) { memcpy(&seed, packet + 12, sizeof(seed)); seed = CFSwapInt64BigToHost(seed); }
            NSString *key = [JCOREDataSecurity encryptKeyWithSeed:seed];
            NSString *iv = [key substringWithRange:NSMakeRange(0, 16)];
            NSData *decrypted = packet[4] == 2 ? [JCOREDataSecurity sm4CBCDecryptData:encrypted withKey:key iv:iv.UTF8String] : [JCOREDataSecurity aes256DecryptData:encrypted withKey:key iv:iv.UTF8String];
            if (!decrypted.length) { free(packet); [JCommonService recordTcpException:1302 message:@""]; return nil; }
            // The original copies the encrypted length beyond the decoded NSData's length.
            // Keep the packet length and decoded prefix, with a deterministic zero tail.
            memset(packet + 20, 0, length - 20);
            memmove(packet + 20, decrypted.bytes, MIN(decrypted.length, length - 20));
        }
    }
    return [NSData dataWithBytesNoCopy:packet length:length];
}
- (void)socket:(id)socket recvData:(id)data handler:(void (^)(BOOL))handler {
    [JCORENetDataManager endSocketMoniter:2 host:self.currentNetwork.config.host port:self.currentNetwork.config.port response_byte:(long long)[data length]];
    NSData *parsed = [self parsePacketData:data];
    if (!parsed) [NSNotificationCenter.defaultCenter postNotificationName:@"kJCORETcpChannelResponseNotification" object:nil];
    else {
        unsigned short rid = [JCORETcpChannelMonitor parseRid:data];
        id request = [self.tcpSendingQueue getObjectFromQueueWithKey:@(rid)];
        NSString *identifier = request ? [request moniter_id] : nil;
        [NSNotificationCenter.defaultCenter postNotificationName:@"kJCORETcpChannelResponseNotification" object:nil userInfo:@{@"data":parsed, @"moniter_id":identifier ?: @""}];
    }
    if (handler) handler(YES);
}
- (void)socket:(id)socket sentData:(id)data {
    JCOREChannelLog(YES, 0x100, 484, @"tcpDidSendData: %lu", [data length]);
    if (![data isKindOfClass:NSData.class]) return;
    [JCORETcpChannelMonitor monitor:data completion:^(unsigned short code, unsigned short rid, NSData *response) {
        id request = [self.tcpSendingQueue dequeueItemWithKey:@(rid)];
        if (!request) request = [self.tcpBlockedQueue dequeueItemWithKey:@(rid)];
        if (!request) { JCOREChannelLog(YES, 8, 493, @"ack can't find request rid:%d, data:%@", rid, response); return; }
        if (code && [request respondsToSelector:@selector(autoRetryCount)] && [request autoRetryCount] > 0) {
            if ([request respondsToSelector:@selector(setAutoRetryCount:)]) [request setAutoRetryCount:[request autoRetryCount] - 1];
            endConnectionMoniter([request moniter_id], -1, 0);
            [self doSendTcpRequest:request];
            return;
        }
        if (code == 6001) {
            endConnectionMoniter([request moniter_id], -1, 0);
            if (![self ackOrRespRequired:request]) return;
            JCOREChannelLog(YES, 0x40, 518, @"tcpDidSendData timeout");
            [self errorCallBack:request code:6001 des:@"send data ack timeout"];
            JCOREChannelLog(NO, 1, 522, @"ack timeout for request rid: %d", [request rid]);
            [JCOREConnectManager.shared connect];
        } else if ([request respondsToSelector:@selector(completionHandler)] && [request completionHandler]) ((RecoveryOpaqueCallback)[request completionHandler])(response, nil);
        JCORETcpConnectModel *model = [[JCORETcpConnectModel alloc] init];
        model.protocolType = [request protocolType]; model.statusCode = code; model.socketConfig = self.currentNetwork.config;
        [JCOREConnectManager.shared didRespond:model];
    }];
}
- (void)sendQueueEmpty:(id)socket {
    if (!_isLogined) { self.currentNetwork.careAllDataSent = NO; return; }
    id request = [_tcpBlockedQueue dequeue];
    if (request && [request conformsToProtocol:@protocol(JCORETcpProtocol)]) [self doSendTcpRequest:request];
    else self.currentNetwork.careAllDataSent = NO;
}
// Both selectors are empty in the original channel implementation.
- (void)socket:(id)socket noRecvData:(id)data {}
- (void)socket:(id)socket unsentData:(id)data {}
- (void)socket:(id)socket connectStateChanged:(NSUInteger)state {
    NSString *notification;
    if (state == 0) {
        JCOREChannelLog(YES, 0x100, 596, @"连接断开");
        if (![(JCORENetwork *)socket config].hasMonitored) endConnectionMoniter([(JCORENetwork *)socket config].moniterID, -1, 0);
        else endConnectionMoniter([JCORESupersetManager startConnectionMoniter:@"NET_TCP_DISCONNECT" host:[(JCORENetwork *)socket config].host port:[(JCORENetwork *)socket config].port], 0, 0);
        _isLogined = NO;
        [self reenqueueSendingRequests];
        if (self.currentNetwork) self.currentNetwork = nil;
        notification = @"kJCORETcpChannelDisconnectNotification";
    } else if (state == 1) { JCOREChannelLog(YES, 0x100, 577, @"连接中"); notification = @"kJCORETcpChannelConnectingNotification"; }
    else if (state == 2) {
        JCOREChannelLog(YES, 0x100, 584, @"连接成功");
        endConnectionMoniter([(JCORENetwork *)socket config].moniterID, 0, 0);
        [(JCORENetwork *)socket config].hasMonitored = YES;
        if (!socket) return;
        self.currentNetwork = socket;
        notification = @"kJCORETcpChannelConnectedNotification";
    } else { if (state == 3) JCOREChannelLog(YES, 0x100, 618, @"一轮连接失败"); return; }
    [NSNotificationCenter.defaultCenter postNotificationName:notification object:nil];
}
- (void)didLogin:(id)notification {
    _isLogined = YES;
    if (notification && [notification userInfo]) { id version = [notification userInfo][@"version"]; if ([version isKindOfClass:NSNumber.class]) _serverVersion = (unsigned short)[version unsignedIntValue]; }
    [self sendQueueEmpty:nil];
    if (![JCOREIntegrate tcpKeepAlive]) {
        if (![JCOREClientInfo shouldUploadAdvertisingId] && ![JCOREIntegrate needUploadSDKVersion]) [self performSelector:@selector(closeSocket)];
        else [self performSelector:@selector(closeSocket) withObject:nil afterDelay:2];
    }
}
- (void)closeSocket { [JCOREConnectManager.shared disConnect]; }
- (BOOL)loginRequired:(id)request {
    if (![request respondsToSelector:@selector(controlFlag)]) return YES;
    if (![request controlFlag]) { if ([request protocolType] == 1) return NO; return [request protocolType] != 0; }
    return [request controlFlag] & 1;
}
- (BOOL)canBeQueuedIfNotLogin:(id)request { return [request respondsToSelector:@selector(controlFlag)] && (([request controlFlag] >> 4) & 1); }
- (BOOL)isExclusive:(unsigned char)flag { return (flag >> 2) & 1; }
- (BOOL)hasHighPriority:(unsigned char)flag { return (flag >> 3) & 1; }
- (BOOL)ackOrRespRequired:(id)request {
    if (![request respondsToSelector:@selector(controlFlag)]) return NO;
    if (([request controlFlag] >> 1) & 1) return YES;
    return (([request controlFlag] >> 5) & 1) && _serverVersion != 0;
}
- (void)enqueueRequest:(id)request {
    JCOREChannelLog(YES, 0x100, 700, @"Action - enqueueRequest - rid:%d, request:%@", [request rid], request);
    if (![request respondsToSelector:@selector(controlFlag)] || ![self isExclusive:[request controlFlag]]) return;
    BOOL allowed = YES;
    Class tagAliasClass = NSClassFromString(@"JPUSHSetTagAlias");
    if ([request isKindOfClass:tagAliasClass.class]) {
        __block NSMutableArray *tags = [@[] mutableCopy];
        [_tcpBlockedQueue enumerateObjectsUsingBlock:^(id item, NSUInteger index, BOOL *stop) { if ([item isKindOfClass:tagAliasClass.class]) [tags addObject:item]; }];
        if (tags.count == 10) {
            id oldest = tags[0];
            allowed = [[request valueForKey:@"createTime"] doubleValue] - [[oldest valueForKey:@"createTime"] doubleValue] >= 10;
            if ([[request valueForKey:@"createTime"] doubleValue] - [[oldest valueForKey:@"createTime"] doubleValue] >= 10) [_tcpBlockedQueue dequeue:oldest];
        }
    }
    __block BOOL duplicate = NO;
    JCORENetworkQueue *snapshot = [_tcpBlockedQueue copy];
    [snapshot enumerateObjectsUsingBlock:^(id<JCORETcpProtocol> item, NSUInteger index, BOOL *stop) { if ([request rid] == item.rid) duplicate = YES; }];
    if (duplicate) {
        JCOREChannelLog(YES, 8, 741, @"remove queued tcp request seq rid %d", [request rid]);
        JCOREChannelLog(YES, 0x100, 742, @"remove queued tcp request seq rid %d", [request rid]);
        [_tcpBlockedQueue dequeueItemWithKey:@((unsigned short)[request rid])];
    }
    if ([_tcpBlockedQueue count] > 100) [_tcpBlockedQueue dequeue];
    if ([self hasHighPriority:[request controlFlag]]) [_tcpBlockedQueue enqueueWithHighPriority:request];
    else if (allowed) [_tcpBlockedQueue enqueue:request];
    self.currentNetwork.careAllDataSent = YES;
}
- (void)errorCallBack:(id)request code:(long long)code des:(id)description {
    if ([request respondsToSelector:@selector(completionHandler)] && [request completionHandler]) {
        NSError *error = [NSError errorWithDomain:@"JIGUANGTcpErrorDomain" code:(NSInteger)code userInfo:@{NSLocalizedDescriptionKey:description ?: @""}];
        ((RecoveryOpaqueCallback)[request completionHandler])(request, error);
    }
    if ([request isKindOfClass:JCORERegister.class]) { if (code) [JCommonService recordTcpException:code == 2003 ? 1000 : 1001 message:@""]; }
    else if ([request isKindOfClass:JCORELogin.class] && code) [JCommonService recordTcpException:code == 2003 ? 1100 : 1101 message:@""];
}
- (void)dequeueRequest:(id)request { if ([request conformsToProtocol:@protocol(JCORETcpProtocol)] && [request respondsToSelector:@selector(rid)]) [_tcpBlockedQueue dequeueItemWithKey:@((unsigned short)[request rid])]; }
- (id)findBlockedRequest:(unsigned short)rid { return [_tcpBlockedQueue getObjectFromQueueWithKey:@(rid)]; }
- (id)findSendingRequest:(unsigned short)rid { return [_tcpSendingQueue getObjectFromQueueWithKey:@(rid)]; }
- (BOOL)isCloseSocket { return self.currentNetwork == nil; }
- (void)onAckOrRespReceived:(id)response {
    JCOREChannelLog(YES, 8, 821, @"Action - onAckOrRespReceived:");
    id request = [_tcpSendingQueue dequeueItemWithKey:[response valueForKey:@"rid"]];
    if (request && [request conformsToProtocol:@protocol(JCORETcpProtocol)]) JCOREChannelLog(YES, 0x100, 825, @"Event - onAckOrRespReceived - rid:%d", [request rid]);
}
- (void)reenqueueSendingRequests {
    JCOREChannelLog(YES, 8, 833, @"Action - reenqueueSendingRequests");
    __weak JCORETcpChannelController *weakSelf = self;
    [_tcpBlockedQueue copyQueue:_tcpSendingQueue withCondition:^BOOL(id request) {
        if (![request conformsToProtocol:@protocol(JCORETcpProtocol)]) { JCOREChannelLog(YES, 0x100, 839, @"invalid request type, remove it from sendingQueue"); return NO; }
        if ([weakSelf canBeQueuedIfNotLogin:request]) {
            if ([weakSelf.tcpBlockedQueue count] < 101) return YES;
            JCOREChannelLog(YES, 8, 845, @"等待请求队列过多");
        }
        return NO;
    }];
}
@end
