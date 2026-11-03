#import "JCOREConnectPool.h"
#import "JCOREAddress.h"
#import "JCOREAddressAnalysisController.h"
#import "JCOREBusinessStorage.h"
#import "JCOREIpvxHelper.h"
#import "JCORELog.h"
#import "JCORENetworkModels.h"
#import "JCOREUtilities.h"
#define PoolLog(flagValue, sourceLine, text, ...)                                                            \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:flagValue                                                                                  \
          context:0                                                                                          \
             file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Connection/"            \
                  "JCOREConnectPool.m"                                                                       \
         function:__PRETTY_FUNCTION__                                                                        \
             line:sourceLine                                                                                 \
              tag:nil                                                                                        \
           format:text, ##__VA_ARGS__]
@implementation JCOREConnectPool
+ (instancetype)instance {
    static JCOREConnectPool *pool;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
      pool = [[JCOREConnectPool alloc] init];
    });
    return pool;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        _interval = 3;
        _maxNum = 5;
        _timeoutTimerArray = [NSMutableArray array];
        _socketArray = [NSMutableArray array];
        _ipArray = [NSMutableArray array];
        _ipvxHistoryArray = [NSMutableArray array];
        _minorIpvxTimes = 0;
    }
    return self;
}
- (void)dealloc {
    [self stopConTimer];
    for (NSTimer *timer in [_timeoutTimerArray copy]) {
        if (timer.isValid)
            [timer invalidate];
    }
    [_timeoutTimerArray removeAllObjects];
}
- (void)asyConnectWithIps:(id)ips {
    [self connectWithIps:ips maxNum:5];
}
- (void)synConnectWithIps:(id)ips {
    [self connectWithIps:ips maxNum:1];
}
- (void)connectWithIps:(id)ips maxNum:(long long)maxNum {
    if (![ips count])
        return;
    id converted = [self arrayAfterTransSomeIpType:ips];
    _maxNum = maxNum;
    NSMutableArray *unused = [NSMutableArray array];
    [[_ipArray copy] enumerateObjectsUsingBlock:^(JCOREConnectIpModel *model, NSUInteger index, BOOL *stop) {
      if (!model.loading)
          [unused addObject:model];
    }];
    [_ipArray removeObjectsInArray:unused];
    NSArray *reversed = [[[[converted mutableCopy] reverseObjectEnumerator] allObjects] copy];
    NSMutableArray *models = [_ipArray mutableCopy];
    for (id ip in reversed) {
        if (![JCOREAddress isValidIpString:ip])
            continue;
        JCOREConnectIpModel *model = [[JCOREConnectIpModel alloc] initWithIp:ip];
        JCOREConnectIpModel *old = [self findModelWithIP:ip];
        if (old) {
            if (old.loading)
                continue;
            [models removeObject:old];
        }
        [models insertObject:model atIndex:0];
    }
    _ipArray = [models mutableCopy];
    [_ipvxHistoryArray removeAllObjects];
    if (_ipArray.count && !_isConnected) {
        [self startConTimer];
        [self launchASocket];
    }
}
- (void)launchASocket {
    JCOREConnectIpModel *model = [self getAValidModel];
    if (!model)
        return;
    model.loading = YES;
    model.startTime = [JCOREUtilities currentTimestamp];
    PoolLog(0x100, 180, @"Action - launchASocket");
    JCORESocketConfig *config = [[JCORESocketConfig alloc] init];
    config.host = model.host;
    config.port = model.port;
    config.ipType = model.ipType;
    PoolLog(0x100, 199, @"start a socket connect, host:%@, port:%d, ipType:%lu", config.host, config.port,
            (unsigned long)config.ipType);
    JCORENetwork *socket = [JCORENetwork network:0];
    [socket setConfig:config delegate:self.socketDelegate];
    [socket connect];
    [self socketSend:socket];
    [_socketArray addObject:socket];
}
- (void)start {
    _isConnected = NO;
    [self stop];
    [self poolExhausted];
}
- (void)stop {
    [self stopConTimer];
    [self stopOtherSocketWithout:nil];
    [_ipArray removeAllObjects];
}
- (void)stopOtherSocketWithout:(id)socket {
    while (_socketArray.count) {
        JCORENetwork *first = _socketArray.firstObject;
        [_socketArray removeObject:first];
        if (first != socket) {
            first.delegate = nil;
            [first disconect];
            [first remove];
        }
    }
}
- (void)startConTimer {
    PoolLog(0x100, 245, @"Action - %@", NSStringFromSelector(_cmd));
    [self stopConTimer];
    _conTimer = [NSTimer scheduledTimerWithTimeInterval:_interval
                                                 target:self
                                               selector:@selector(launchASocket)
                                               userInfo:nil
                                                repeats:YES];
}
- (void)stopConTimer {
    PoolLog(0x100, 255, @"Action - %@", NSStringFromSelector(_cmd));
    if (_conTimer) {
        [_conTimer invalidate];
        _conTimer = nil;
    }
}
- (id)arrayAfterTransSomeIpType:(id)ips {
    long long type = [JCOREIpvxHelper instance].ipvxType;
    if (type != 2 && type != 4)
        return ips;
    NSMutableArray *ipv4 = [NSMutableArray array];
    BOOL hasIPv6 = NO;
    for (id ip in ips) {
        if ([JCOREAddress isIpv4:[JCOREAddress host:ip]])
            [ipv4 addObject:ip];
        else
            hasIPv6 = YES;
    }
    if (!ipv4.count)
        return ips;
    PoolLog(0x100, 293, @"need trans some ip from ipv4->ipv6");
    NSUInteger count = hasIPv6 ? MAX(ipv4.count >> 2, (NSUInteger)1) : MAX(ipv4.count >> 1, (NSUInteger)1);
    NSMutableArray *result = [NSMutableArray array];
    [result addObjectsFromArray:ips];
    for (NSUInteger index = 0; index < count; index++) {
        id ip = ipv4[index];
        NSString *host = [JCOREAddress host:ip];
        NSString *port = [JCOREAddress port:ip];
        if (!host.length || !port.length)
            continue;
        NSString *converted =
            [[JCOREIpvxHelper instance] convertHostFromIPv4ToIPv6WithHost:host
                                                                      pre:[JCOREIpvxHelper instance].ipv6Pre];
        NSString *address = [NSString stringWithFormat:@"%@:%@", converted, port];
        if ([JCOREAddress isValidIpString:address])
            [result addObject:address];
        PoolLog(0x100, 317, @"convert ipv4 address:%@ to ipv6 address:%@", ip, address);
    }
    return [result copy];
}
- (NSUInteger)ipType {
    long long type = [JCOREIpvxHelper instance].ipvxType;
    if (type == 4)
        return 1;
    if (type != 1 && type != 2)
        return 0;
    NSUInteger preferred = type == 1 ? 0 : 1;
    NSUInteger alternate = type == 1 ? 1 : 0;
    NSUInteger preferredCount = type == 1 ? [self getIpv4Num] : [self getIpv6Num];
    NSUInteger alternateCount = type == 1 ? [self getIpv6Num] : [self getIpv4Num];
    if (alternateCount) {
        if (!preferredCount) {
            if (alternateCount < 3) {
                _minorIpvxTimes++;
                preferred = alternate;
            }
        } else if (self.ipvxHistoryArray.count > 1) {
            NSNumber *first = self.ipvxHistoryArray.firstObject;
            NSNumber *last = self.ipvxHistoryArray.lastObject;
            if (first.unsignedIntegerValue == last.unsignedIntegerValue &&
                first.unsignedIntegerValue != alternate) {
                if (first.unsignedIntegerValue == preferred) {
                    if (alternateCount < 3) {
                        _minorIpvxTimes++;
                        preferred = alternate;
                    }
                } else
                    preferred = 1;
            }
        }
    }
    return preferred;
}
- (NSUInteger)getIpv6Num {
    NSUInteger count = 0;
    for (JCOREConnectIpModel *model in [_ipArray copy])
        if (!model.loading)
            count += ![JCOREAddress isIpv4:model.host];
    return count;
}
- (NSUInteger)getIpv4Num {
    NSUInteger count = 0;
    for (JCOREConnectIpModel *model in [_ipArray copy])
        if (!model.loading)
            count += [JCOREAddress isIpv4:model.host];
    return count;
}
- (JCOREConnectIpModel *)getAValidModel {
    NSUInteger type = [self ipType];
    [_ipvxHistoryArray addObject:@(type)];
    if (_ipvxHistoryArray.count > 2)
        [_ipvxHistoryArray removeObjectAtIndex:0];
    for (JCOREConnectIpModel *model in [_ipArray copy])
        if (!model.loading && model.ipType == type)
            return model;
    return nil;
}
- (void)removeModelObjectWithIP:(id)ip {
    id model = [self findModelWithIP:ip];
    if (model)
        [_ipArray removeObject:model];
}
- (JCOREConnectIpModel *)findModelWithIP:(id)ip {
    for (JCOREConnectIpModel *model in [_ipArray copy])
        if ([model.ip isEqualToString:ip])
            return model;
    return nil;
}
- (void)connectionDidRegisterFailed:(id)config {
    [self connectionDidFailedWithConfig:config];
}
- (void)connectionDidLoginFailed:(id)config {
    [self connectionDidFailedWithConfig:config];
}
- (void)connectionDidAckTimeout:(id)config {
    [self connectionDidFailedWithConfig:config];
}
- (void)connectionLoginSuccess:(id)config {
    if (!config)
        return;
    NSString *ip =
        [NSString stringWithFormat:@"%@:%u", [config host], [(id<JCORESocketConfigProtocol>)config port]];
    JCOREConnectIpModel *model = [self findModelWithIP:ip];
    if (model)
        [self recodeSuccess:model.ip costTime:[JCOREUtilities currentTimestamp] - model.startTime];
    [_ipArray removeAllObjects];
}
- (void)connectionDidFailed:(id)socket {
    JCORESocketConfig *config = [socket config];
    PoolLog(0x100, 500, @"%@ , host:%@, port:%d", NSStringFromSelector(_cmd), config.host,
            [[socket config] port]);
    if (socket)
        [self connectionDidFailedWithConfig:[socket config]];
}
- (void)connectionDidSuccess:(id)socket {
    JCORESocketConfig *config = [socket config];
    PoolLog(0x100, 508, @"%@ , host:%@, port:%d", NSStringFromSelector(_cmd), config.host,
            [[socket config] port]);
    if (!socket)
        return;
    _isConnected = YES;
    [self stopOtherSocketWithout:socket];
    [self stopConTimer];
    NSUInteger type = [[socket config] ipType];
    [[JCOREIpvxHelper instance] cacheNetworkIpvxType:type ? 2 : 1];
}
- (void)connectionDidFailedWithConfig:(id)config {
    if (!config)
        return;
    _isConnected = NO;
    NSString *ip =
        [NSString stringWithFormat:@"%@:%u", [config host], [(id<JCORESocketConfigProtocol>)config port]];
    [self recodeFailure:ip];
    [JCOREBusinessStorage addFailedConIPs:@[ ip ]];
}
- (void)recodeSuccess:(id)ip costTime:(double)time {
    [[JCOREAddressAnalysisController instance] addValue:@{
        JCOREAddressAnalysisType : @"jcore_aa_sis_type",
        JCOREAddressAnalysisAddress : ip,
        JCOREAddressAnalysisCostTime : @(time),
        JCOREAddressAnalysisStatus : @((NSInteger)3)
    }];
}
- (void)recodeFailure:(id)ip {
    [[JCOREAddressAnalysisController instance] addValue:@{
        JCOREAddressAnalysisType : @"jcore_aa_sis_type",
        JCOREAddressAnalysisAddress : ip,
        JCOREAddressAnalysisStatus : @((NSInteger)0)
    }];
    [self removeModelObjectWithIP:ip];
    if (!_ipArray.count && _conTimer)
        [self didAllIpsFailed];
}
- (void)recodeTimeout:(id)ip {
    [[JCOREAddressAnalysisController instance] addValue:@{
        JCOREAddressAnalysisType : @"jcore_aa_sis_type",
        JCOREAddressAnalysisAddress : ip,
        JCOREAddressAnalysisStatus : @((NSInteger)1)
    }];
    [self removeModelObjectWithIP:ip];
    if (!_ipArray.count && _conTimer)
        [self didAllIpsFailed];
}
- (void)poolExhausted {
    if ([self.poolDelegate respondsToSelector:@selector(didPoolExhausted:)])
        [self.poolDelegate didPoolExhausted:self];
}
- (void)socketSend:(id)socket {
    if (socket && [self.poolDelegate respondsToSelector:@selector(didSocketSend:)])
        [self.poolDelegate didSocketSend:socket];
}
- (void)didAllIpsFailed {
    [self stopConTimer];
    if ([self.poolDelegate respondsToSelector:@selector(didAllIpsFailed)])
        [self.poolDelegate didAllIpsFailed];
}
@end
#undef PoolLog
