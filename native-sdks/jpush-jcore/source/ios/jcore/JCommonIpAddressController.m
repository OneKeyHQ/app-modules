#import "JCommonIpAddressController.h"
#import "JCOREClientInfo.h"
#import "JCOREIpvxHelper.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#import "JCommonIpAddressRequest.h"
#import "JCommonReportManager.h"
#import "JCommonReportTimerManager.h"
#import "RecoveredCoreService.h"
FOUNDATION_EXPORT NSString *const kJCORENetworkDidLoginNotification;
#define IpControllerFile                                                                                     \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/"                        \
    "JCommonIpAddressController.m"
#define IpLog(lineValue, fmt, ...)                                                                           \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:IpControllerFile                                                                           \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineValue                                                                                  \
              tag:nil                                                                                        \
           format:fmt, ##__VA_ARGS__]
@implementation JCommonIpAddressController
- (void)dealloc {
    _timerManager = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (instancetype)init {
    self = [super init];
    if (self)
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(didLogin)
                                                     name:kJCORENetworkDidLoginNotification
                                                   object:nil];
    return self;
}
- (void)didLogin {
    if (_neesLoginReport) {
        [self requestAndReportIpAddress];
        _neesLoginReport = NO;
    }
}
- (void)startIpAddressCheckStrategy {
    [self startTimer];
}
- (void)stopIpAddressCheckStrategy {
    [self stopTimer];
}
- (void)startTimer {
    if (!_timerManager) {
        _timerManager = [JCommonReportTimerManager manager];
        __weak JCommonIpAddressController *weakSelf = self;
        _timerManager.timerFuncBlock = ^{
          [weakSelf reportIpv6AddressInfo];
        };
        _timerManager.frequency = 86400;
        _timerManager.saveKey = @"__JCOMMONIpAddressCheckTime";
        _timerManager.firstLoadReport = YES;
    }
    [_timerManager startTimerCheck];
}
- (void)stopTimer {
    [_timerManager stopTimerCheck];
}
+ (id)localApiIpv6 {
    id addresses = [[JCOREIpvxHelper instance] getIPAddresses];
    NSMutableArray *result = [NSMutableArray array];
    for (id key in [addresses allKeys]) {
        id address = addresses[key];
        if ([key isEqualToString:@"en0/ipv6"] ||
            [key isEqualToString:[NSString stringWithFormat:@"pdp_ip0/ipv6"]])
            [result addObject:address];
    }
    return result;
}
- (id)contentWithIpv6Address:(id)address {
    id local = [JCommonIpAddressController localApiIpv6];
    if ((!address || ![address isKindOfClass:[NSString class]] || ![address length]) && ![local count])
        return nil;
    id ip = address ?: @"";
    NSString *joined = [local componentsJoinedByString:@"&"] ?: @"";
    return @{
        @"type" : @"ip_info",
        @"itime" : @([JCOREUtilities currentTimestamp]),
        @"ipv6" : ip,
        @"ip2" : joined
    };
}
- (void)reportIpv6AddressInfo {
    if (![JCOREClientInfo uid]) {
        IpLog(122, @"not login, return %s");
        _neesLoginReport = YES;
        return;
    }
    if ([JCOREService getCheckIpv6Switch]) {
        JCOREIpvxHelper *helper = [JCOREIpvxHelper new];
        long long type = helper.ipvxType;
        if (type > 5 || ((1ULL << (type & 63)) & 0x34) == 0) {
            IpLog(131, @"ipv6 check open, current network may not ipv6, return");
            return;
        }
    }
    [self requestAndReportIpAddress];
}
- (void)requestAndReportIpAddress {
    [self requestIpAddress:^(id address) {
      id content = [self contentWithIpv6Address:address];
      if (content)
          [JCommonReportManager sendIPV6AddressInfoReport:content
                                        completionHandler:^(id response, NSError *error) {
                                          if (!error)
                                              IpLog(149, @"sendIpAddressInfoReport success");
                                        }];
    }];
}
- (void)requestIpAddress:(void (^)(id))completion {
    JCommonIpAddressRequest *request = [JCommonIpAddressRequest new];
    [request sendRequest:^(id address, NSError *error) {
      IpLog(158, @"%@ -- %@", address);
      if (completion)
          completion(address);
    }];
}
@end
