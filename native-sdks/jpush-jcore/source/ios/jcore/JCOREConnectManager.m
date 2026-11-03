#import "JCOREConnectManager.h"
#import "JCOREBusinessStorage.h"
#import "JCORELog.h"
#import "JCORENetUtilities.h"
#import "JCORENetworkReachabilityManager.h"
#import "JCORESisManager.h"
#import "JCORESisReport.h"
#import "JCOREThread.h"
#import "RecoveredCoreService.h"
#import "RecoveredProtocols.h"
#define ConnectLog(flagValue, sourceLine, text, ...)                                                         \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:flagValue                                                                                  \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Connection/"            \
                  "JCOREConnectManager.m"                                                                    \
         function:__PRETTY_FUNCTION__                                                                        \
             line:sourceLine                                                                                 \
              tag:nil                                                                                        \
           format:text, ##__VA_ARGS__]
@implementation JCOREConnectManager
@dynamic hash, superclass, description, debugDescription;
+ (instancetype)shared {
    static JCOREConnectManager *manager;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
      manager = [[JCOREConnectManager alloc] init];
    });
    return manager;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        _sisManager = [JCORESisManager shared];
        _isSetup = NO;
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(networkingReachabilityDidChange:)
                                                     name:JCORENetworkingReachabilityDidChangeNotification
                                                   object:nil];
        [JCOREConnectPool instance].socketDelegate = self;
        [JCOREConnectPool instance].poolDelegate = self;
    }
    return self;
}
- (void)dealloc {
    [self closeConection];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (void)resetDelaySetupIndex {
    _delaySetupTimeIntervalIndex = 0;
}
- (void)connect {
    ConnectLog(0x100, 97, @"Action - %@", NSStringFromSelector(_cmd));
    _isSetup = YES;
    [self resetDelaySetupIndex];
    [self closeConection];
    if (![JCORENetworkReachabilityManager sharedManager].isReachable) {
        ConnectLog(0x100, 106, @"not reachable, give up");
        [self startUnReachableDelaySetupTimer];
    } else
        [self beginConnect];
}
- (void)disConnect {
    ConnectLog(4, 112, @"Action - %@", NSStringFromSelector(_cmd));
    _isSetup = NO;
    [self closeConection];
}
- (void)reConnect {
    ConnectLog(0x100, 122, @"Action - %@", NSStringFromSelector(_cmd));
    if (!_isSetup) {
        ConnectLog(0x100, 125, @"is not setup, return");
        return;
    }
    if (_isConnecting) {
        ConnectLog(0x100, 130, @"is connecting, return");
        return;
    }
    if (_isConnected) {
        ConnectLog(0x100, 135, @"is connected, return");
        return;
    }
    [self closeConection];
    if ([JCORENetworkReachabilityManager sharedManager].isReachable)
        [self startReachableDelaySetupTimer];
    else
        [self startUnReachableDelaySetupTimer];
}
- (void)beginConnect {
    ConnectLog(0x100, 153, @"Action - %@", NSStringFromSelector(_cmd));
    if (![JCORENetworkReachabilityManager sharedManager].isReachable) {
        ConnectLog(0x100, 156, @"is not Reachable, return");
        return;
    }
    if (!_isSetup) {
        ConnectLog(0x100, 161, @"is not setup, return");
        return;
    }
    if (_isConnecting) {
        ConnectLog(0x100, 166, @"is connecting, return");
        return;
    }
    if (_isConnected) {
        ConnectLog(0x100, 171, @"is connected, return");
        return;
    }
    if (self.delegate && [self.delegate respondsToSelector:@selector(socket:connectStateChanged:)])
        [self.delegate socket:nil connectStateChanged:1];
    _isConnecting = YES;
    _addressCursor = 0;
    [[JCOREConnectPool instance] start];
}
- (void)closeConection {
    ConnectLog(4, 193, @"Action - %@", NSStringFromSelector(_cmd));
    [self stopDelaySetupTimer];
    if (_socket) {
        [_socket disconect];
        [_socket remove];
    }
    [[JCORESisManager shared] cancelSisRequest];
    [[JCOREConnectPool instance] stop];
    _isConnected = NO;
    _isConnecting = NO;
}
- (void)networkingReachabilityDidChange:(id)notification {
    [JCOREThread performSel:@selector(reachabilityChanged:) target:self withObject:notification];
}
- (void)reachabilityChanged:(id)notification {
    ConnectLog(0x100, 215, @"Action - reachabilityChanged");
    if (!_isSetup) {
        ConnectLog(0x100, 217, @"is not setup, return");
        return;
    }
    [self resetDelaySetupIndex];
    [self closeConection];
    if ([JCORENetworkReachabilityManager sharedManager].isReachable)
        [self beginConnect];
    else
        [self startUnReachableDelaySetupTimer];
}
- (void)didRespond:(id)response {
    id<JCORETcpConnectProtocol> result = response;
    if (!result.socketConfig || !result.socketConfig.host.length || !result.socketConfig.port ||
        result.socketConfig.port > 65534)
        return;
    if (result.statusCode && result.statusCode != 6002)
        return;
    if (result.protocolType == 1) {
        self.responseError = result.statusCode != 0;
        if (!result.statusCode)
            [self didLoginSucceed:result.socketConfig];
        else {
            JCORESocketConfig *config = result.socketConfig;
            NSError *error = [NSError errorWithDomain:@"loginEvent" code:-994 userInfo:nil];
            [self didLoginFailed:config error:error];
        }
    } else if (!result.protocolType) {
        self.responseError = result.statusCode != 0;
        if (!result.statusCode)
            [self didRegisterSucceed:result.socketConfig];
        else {
            JCORESocketConfig *config = result.socketConfig;
            NSError *error = [NSError errorWithDomain:@"registerEvent" code:-994 userInfo:nil];
            [self didRegisterFailed:config error:error];
        }
    }
}
- (void)didRegisterSucceed:(id)config {
    // The original registration-success hook has an empty body.
}
- (void)didRegisterFailed:(id)config error:(NSError *)error {
    [[JCORESisReport shared]
        updateSisReportDataWithIp:[config host]
                             port:(unsigned short)[(id<JCORESocketConfigProtocol>)config port]
                             code:error.code
                             type:2];
    [[JCOREConnectPool instance] connectionDidRegisterFailed:config];
}
- (void)didLoginSucceed:(id)config {
    NSString *address =
        [NSString stringWithFormat:@"%@:%u", [config host], [(id<JCORESocketConfigProtocol>)config port]];
    [JCOREBusinessStorage setLastSucceedConnectionAddress:@[ address ]];
    [[JCOREConnectPool instance] connectionLoginSuccess:config];
}
- (void)didLoginFailed:(id)config error:(NSError *)error {
    [[JCORESisReport shared]
        updateSisReportDataWithIp:[config host]
                             port:(unsigned short)[(id<JCORESocketConfigProtocol>)config port]
                             code:error.code
                             type:2];
    [[JCOREConnectPool instance] connectionDidLoginFailed:config];
}
- (void)getSisResult:(JCOREConnectSisCompletion)completion {
    if (!_isSetup) {
        ConnectLog(0x100, 295, @"isSetup is no, return");
        return;
    }
    ConnectLog(0x100, 299, @"Action - get address from sis");
    __block BOOL secondCallback = NO;
    __weak typeof(self) weakSelf = self;
    SEL selector = _cmd;
    [_sisManager
        fetchConAddressWithTimeout:6
                        completion:^(NSArray *addresses, BOOL isCache, NSError *error) {
                          __strong typeof(weakSelf) target = weakSelf;
                          if (error || !addresses.count) {
                              [JCORELog log:YES
                                      level:0x3ff
                                       flag:0x100
                                    context:0
                                       file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/"
                                            "Library/Connection/JCOREConnectManager.m"
                                   function:"-[JCOREConnectManager getSisResult:]_block_invoke"
                                       line:332
                                        tag:nil
                                     format:@"%@ fail ", NSStringFromSelector(selector)];
                              completion(nil);
                              return;
                          }
                          [JCORELog log:YES
                                  level:0x3ff
                                   flag:0x100
                                context:0
                                   file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/"
                                        "Connection/JCOREConnectManager.m"
                               function:"-[JCOREConnectManager getSisResult:]_block_invoke"
                                   line:305
                                    tag:nil
                                 format:@"get con address from sis success,result:%@\n ",
                                        [addresses description]];
                          if (!isCache) {
                              [JCORELog log:YES
                                      level:0x3ff
                                       flag:0x100
                                    context:0
                                       file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/"
                                            "Library/Connection/JCOREConnectManager.m"
                                   function:"-[JCOREConnectManager getSisResult:]_block_invoke"
                                       line:307
                                        tag:nil
                                     format:@"is not Cache"];
                              if (secondCallback) {
                                  [JCORELog log:YES
                                          level:0x3ff
                                           flag:0x100
                                        context:0
                                           file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/"
                                                "Library/Connection/JCOREConnectManager.m"
                                       function:"-[JCOREConnectManager getSisResult:]_block_invoke"
                                           line:315
                                            tag:nil
                                         format:@"is second Callback"];
                                  if (!target.isConnected)
                                      [target connectWithIps:addresses asy:YES];
                                  else
                                      [JCORELog log:YES
                                              level:0x3ff
                                               flag:0x100
                                            context:0
                                               file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/"
                                                    "JCore/Library/Connection/JCOREConnectManager.m"
                                           function:"-[JCOREConnectManager getSisResult:]_block_invoke"
                                               line:319
                                                tag:nil
                                             format:@"isConnected, give up connect"];
                              } else {
                                  [JCORELog log:YES
                                          level:0x3ff
                                           flag:0x100
                                        context:0
                                           file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/"
                                                "Library/Connection/JCOREConnectManager.m"
                                       function:"-[JCOREConnectManager getSisResult:]_block_invoke"
                                           line:311
                                            tag:nil
                                         format:@"is not second Callback"];
                                  completion(addresses);
                              }
                          } else {
                              [JCORELog log:YES
                                      level:0x3ff
                                       flag:0x100
                                    context:0
                                       file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/"
                                            "Library/Connection/JCOREConnectManager.m"
                                   function:"-[JCOREConnectManager getSisResult:]_block_invoke"
                                       line:325
                                        tag:nil
                                     format:@"is Cache"];
                              completion(addresses);
                          }
                          secondCallback = YES;
                        }];
}
- (id)getLastSucceedConnectionAddress {
    ConnectLog(0x100, 342, @"Action - get con address from last succed con address");
    id result = [JCOREBusinessStorage getLastSucceedConnectionAddress];
    ConnectLog(0x100, 352, @"last succed con address, result:%@", [result description]);
    return result;
}
- (id)defaultDomainAddress {
    ConnectLog(0x100, 360, @"Action - get con adress from default domain");
    id result = [_sisManager defaultConnectDomainAddress];
    ConnectLog(0x100, 362, @"get con adress from default domain, result:%@", result);
    return result;
}
- (id)getConAddress {
    if (_addressCursor == 1)
        return [self getLastSucceedConnectionAddress];
    if (_addressCursor == 2)
        return [self defaultDomainAddress];
    return nil;
}
- (void)connectWithIps:(id)ips asy:(BOOL)async {
    if (!_isSetup) {
        ConnectLog(0x100, 396, @"isSetup is false, return");
        return;
    }
    if (![ips count])
        return;
    if (self.isConnected) {
        ConnectLog(0x100, 403, @"isConnected , return");
        return;
    }
    id preferred = [JCOREIpvPreferedUtilities getPreferedIpAddresses:ips];
    ConnectLog(0x100, 407, @"Action - %@", NSStringFromSelector(_cmd));
    ConnectLog(0x100, 408, @"ip array :%@", [preferred description]);
    if (async) {
        ConnectLog(0x100, 410, @"asy = yes");
        [[JCOREConnectPool instance] asyConnectWithIps:preferred];
    } else {
        ConnectLog(0x100, 413, @"asy = no");
        [[JCOREConnectPool instance] synConnectWithIps:preferred];
    }
}
- (void)continueGetConAddress {
    ConnectLog(0x100, 419, @"Action - %@", NSStringFromSelector(_cmd));
    if (!_isSetup) {
        ConnectLog(0x100, 421, @"isSetup is no, return");
        return;
    }
    id addresses = nil;
    while (![addresses count] && self.addressCursor < 3) {
        addresses = [self getConAddress];
        self.addressCursor = self.addressCursor + 1;
    }
    if (![addresses count]) {
        ConnectLog(0x100, 433, @"tryConnectFailed, wait reconnect");
        [[JCOREConnectPool instance] stop];
        if (self.delegate && [self.delegate respondsToSelector:@selector(socket:connectStateChanged:)])
            [self.delegate socket:nil connectStateChanged:3];
        _isConnecting = NO;
        [self reConnect];
    } else
        [self connectWithIps:addresses asy:NO];
}
- (void)didPoolExhausted:(id)pool {
    if (!_isSetup) {
        ConnectLog(0x100, 448, @"isSetup is no, return");
        return;
    }
    ConnectLog(0x100, 451, @"Action - %@", NSStringFromSelector(_cmd));
    _addressCursor = 0;
    __weak typeof(self) weakSelf = self;
    [self getSisResult:^(NSArray *addresses) {
      if (!addresses.count)
          [weakSelf getSisResultAgain];
      else
          [weakSelf connectWithIps:addresses asy:YES];
    }];
}
- (void)getSisResultAgain {
    __weak typeof(self) weakSelf = self;
    [self getSisResult:^(NSArray *addresses) {
      if (!addresses.count)
          [weakSelf continueGetConAddress];
      else
          [weakSelf connectWithIps:addresses asy:YES];
    }];
}
- (void)didAllIpsFailed {
    if (!_isSetup) {
        ConnectLog(0x100, 481, @"isSetup is no, return");
        return;
    }
    ConnectLog(0x100, 484, @"Action - %@", NSStringFromSelector(_cmd));
    if (_addressCursor) {
        [self continueGetConAddress];
        return;
    }
    [self getSisResult:^(NSArray *addresses) {
      __weak typeof(self) weakSelf = self;
      if (!addresses.count)
          [weakSelf continueGetConAddress];
      else {
          weakSelf.addressCursor = weakSelf.addressCursor + 1;
          [weakSelf connectWithIps:addresses asy:YES];
      }
    }];
}
- (void)didSocketSend:(id)socket {
    if (socket)
        [[JCORESisReport shared] addSisReportData:2
                                               ip:[[socket config] host]
                                             port:(unsigned short)[[socket config] port]];
}
- (void)doDelaySetup:(id)timer {
    [self beginConnect];
}
- (void)startDelaySetupTimer:(double)interval {
    [self stopDelaySetupTimer];
    _delaySetupTimer = [NSTimer scheduledTimerWithTimeInterval:interval
                                                        target:self
                                                      selector:@selector(doDelaySetup:)
                                                      userInfo:nil
                                                       repeats:NO];
    ConnectLog(8, 526, @"session will setup delayed with time interval: %f", interval);
}
- (void)startReachableDelaySetupTimer {
    ConnectLog(8, 531, @"Action - startReachableDelaySetupTimer");
    static const double reconnectTimeIntervals[] = {0.1, 2, 5, 20, 60, 120};
    NSUInteger index = _delaySetupTimeIntervalIndex;
    double delay = 120;
    if (index < 6) {
        delay = reconnectTimeIntervals[index];
        index++;
        _delaySetupTimeIntervalIndex = index;
    }
    if (index == 6 && [JCOREService canStartTcpSSL])
        [JCOREService setCanStartTcpSSL:NO];
    [self startDelaySetupTimer:delay];
}
- (void)startUnReachableDelaySetupTimer {
    ConnectLog(8, 555, @"Action - startUnReachableDelaySetupTimer");
    [self startDelaySetupTimer:10];
}
- (void)stopDelaySetupTimer {
    if (_delaySetupTimer.isValid)
        [_delaySetupTimer invalidate];
    _delaySetupTimer = nil;
}
- (void)socket:(id)socket recvData:(id)data handler:(void (^)(BOOL))handler {
    if (self.delegate && [self.delegate respondsToSelector:_cmd])
        [self.delegate socket:socket recvData:data handler:handler];
}
- (void)socket:(id)socket sentData:(id)data {
    if (self.delegate && [self.delegate respondsToSelector:_cmd])
        [self.delegate socket:socket sentData:data];
}
- (void)sendQueueEmpty:(id)socket {
    if (self.delegate && [self.delegate respondsToSelector:_cmd])
        [self.delegate sendQueueEmpty:socket];
}
- (void)socket:(id)socket noRecvData:(id)data {
    if (self.delegate && [self.delegate respondsToSelector:_cmd])
        [self.delegate socket:socket noRecvData:data];
}
- (void)socket:(id)socket unsentData:(id)data {
    if (self.delegate && [self.delegate respondsToSelector:_cmd])
        [self.delegate socket:socket unsentData:data];
}
- (void)socket:(id)socket connectStateChanged:(NSUInteger)state {
    if (![socket isKindOfClass:JCORENetwork.class]) {
        [JCORELog log:NO
                level:0x3ff
                 flag:0x20
              context:0
                 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Connection/"
                      "JCOREConnectManager.m"
             function:__PRETTY_FUNCTION__
                 line:608
                  tag:nil
               format:@"is not a socket object, return"];
        return;
    }
    if (state == 0) {
        ConnectLog(0x100, 635, @"连接断开, host:%@, port:%d, ipType:%lu", [[socket config] host],
                   [[socket config] port], (unsigned long)[[socket config] ipType]);
        [[JCOREConnectPool instance] connectionDidFailed:socket];
        if (_socket == socket) {
            if (self.delegate && [self.delegate respondsToSelector:_cmd])
                [self.delegate socket:socket connectStateChanged:0];
            _socket = nil;
            _isConnected = NO;
            [self reConnect];
        }
    } else if (state == 2) {
        ConnectLog(0x100, 616, @"连接成功, host:%@, port:%d, ipType:%lu", [[socket config] host],
                   [[socket config] port], (unsigned long)[[socket config] ipType]);
        [[JCOREConnectPool instance] connectionDidSuccess:socket];
        _isConnected = YES;
        _isConnecting = NO;
        if (!self.responseError)
            [self resetDelaySetupIndex];
        _socket = socket;
        _socket.delegate = self;
        if (self.delegate && [self.delegate respondsToSelector:_cmd])
            [self.delegate socket:socket connectStateChanged:2];
    }
}
@end
#undef ConnectLog
