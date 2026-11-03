#import "JCORELocationController.h"
#import "JCORECacheChannel.h"
#import "JCOREFCCache.h"
#import "JCOREIpvxHelper.h"
#import "JCORELog.h"
#import "JCORENetworkReachabilityManager.h"
#import "JCOREUtilities.h"
#import "JCoreParameter.h"
#import "RecoveredCoreService.h"
#import "RecoveredProtocols.h"
#import <CoreLocation/CoreLocation.h>
#import "JCORECollectLocationManager.h"
#import "JCommonReportManager.h"
#import "JCommonReportTimerManager.h"
#import "JCommonIpAddressController.h"
#import "JCommonIpAddressRequest.h"
@interface JCommonService : NSObject
+ (void)trackLocInfoWithCode:(long long)code;
@end
#define LocationFile                                                                                         \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Location/JCORELocationController.m"
#define LocationLog(flagValue, lineValue, fmt, ...)                                                          \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:flagValue                                                                                  \
          context:0                                                                                          \
             file:LocationFile                                                                               \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineValue                                                                                  \
              tag:nil                                                                                        \
           format:fmt, ##__VA_ARGS__]
static JCORELocationController *__manager;
@implementation JCORELocationController
+ (instancetype)sharedInstane {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
      __manager = [JCORELocationController new];
    });
    return __manager;
}
- (void)setLocation:(id)location {
    if (!location)
        return;
    LocationLog(0x100, 434, @"setLocation:%@", location);
    if ([[location allKeys] containsObject:@"lat"] && [[location allKeys] containsObject:@"lng"])
        [_collectionManager addLocationInfo:location];
}
- (id)currentLocationInfo {
    if (![JCoreParameter shared].locationEnable)
        return nil;
    CLLocation *location = [_collectionManager latestLocation];
    if (!location)
        return nil;
    @try {
        NSTimeInterval timestamp = [location.timestamp timeIntervalSince1970];
        if (timestamp <= 0)
            timestamp = [[NSDate date] timeIntervalSince1970];
        else
            timestamp = [location.timestamp timeIntervalSince1970];
        return @{
            @"lat" : @(location.coordinate.latitude),
            @"lng" : @(location.coordinate.longitude),
            @"altitude" : @(location.altitude),
            @"horizontalAccuracy" : @(location.horizontalAccuracy),
            @"verticalAccuracy" : @(location.verticalAccuracy),
            @"itime" : @(timestamp)
        };
    } @catch (NSException *exception) {
        LocationLog(0x100, 458, @"[%s] is exception", __PRETTY_FUNCTION__);
        return nil;
    }
}
- (id)reportContentWithIpv6Address:(id)address {
    NSString *ipv6 = @"";
    if (address && [address isKindOfClass:[NSString class]] && [@"" length])
        ipv6 = address;
    NSArray *locations = [_collectionManager locationReportArray];
    NSMutableDictionary *content = [NSMutableDictionary new];
    NSInteger now = (NSInteger)[JCOREUtilities currentTimestamp];
    NSInteger reachability = [JCORENetworkReachabilityManager sharedManager].networkReachabilityStatus;
    NSString *network = @"";
    if (reachability >= -1 && reachability <= 0)
        network = @"unknow,unknow";
    else if (reachability == 1)
        network = [NSString stringWithFormat:@"MOBILE,%@", [JCOREUtilities ctNetworkType]];
    else if (reachability == 2)
        network = @"WIFI,";
    NSArray *ips = [JCommonIpAddressController localApiIpv6];
    NSString *joined = ips.count ? [ips componentsJoinedByString:@"&"] : @"";
    [content addEntriesFromDictionary:@{
        @"type" : @"loc_info",
        @"network_type" : network,
        @"data" : locations,
        @"itime" : @(now),
        @"ipv6" : ipv6,
        @"ip2" : joined
    }];
    return content;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        id report = [JCORECacheChannel locationReportFrequency];
        if (![report isKindOfClass:[NSNumber class]])
            report = [JCOREFCCache objectForKey:@"locationReportFrequency"];
        else
            [JCORECacheChannel setLocationReportFrequency:nil];
        _needReport = YES;
        id collect = [JCORECacheChannel locationCollectFrequency];
        if (![collect isKindOfClass:[NSNumber class]])
            collect = [JCOREFCCache objectForKey:@"locationCollectFrequency"];
        else
            [JCORECacheChannel setCollectLocationCheckTime:nil];
        _reportLocationFrequency = [report isKindOfClass:[NSNumber class]] && [report doubleValue] >= 30
                                       ? [report doubleValue]
                                       : 3600;
        _collectLocationFrequency = [collect isKindOfClass:[NSNumber class]] && [collect doubleValue] >= 30
                                        ? [collect doubleValue]
                                        : 900;
        _collectionManager = [[JCORECollectLocationManager alloc] initWith:_collectLocationFrequency];
    }
    id disabled = [JCORECacheChannel locationReportDisable];
    if (!disabled)
        disabled = [JCOREFCCache objectForKey:@"locationDisable"];
    else
        [JCORECacheChannel setLocationReportDisable:nil];
    if (disabled)
        _locationDisable = [disabled boolValue];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleLocationOffChange:)
                                                 name:@"JCoreParameterLocationOff"
                                               object:nil];
    return self;
}
- (void)handleLocationOffChange:(NSNotification *)notification {
    if ([notification.object isKindOfClass:[NSNumber class]]) {
        BOOL enabled = [notification.object boolValue];
        [self stopLocationStrategy];
        if (enabled)
            [self startLocationStrategy];
    }
}
- (void)dealloc {
    _collectionManager = nil;
    _reportTimerManager = nil;
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (void)handleContrlCommand:(id)command {
    if (!command || ![command count])
        return;
    id content = command[@"content"];
    if (!content || ![content isKindOfClass:[NSDictionary class]] || ![content count])
        return;
    id number = command[@"cmd"];
    if (!number)
        return;
    char cmd = [number charValue];
    if (cmd == 45) {
        id value = content[@"interval"];
        if (value) {
            double interval = [value doubleValue];
            if (interval >= 30) {
                if (_reportLocationFrequency < interval)
                    LocationLog(0x40, 605,
                                @"collect location frequency greater than report location frequency");
                [self resetCollectLocationFrequency:interval];
            }
        }
    } else if ((unsigned char)cmd == 5) {
        id frequency = content[@"frequency"], disabled = content[@"disable"];
        if (frequency && disabled) {
            double value = [frequency doubleValue];
            BOOL off = [disabled boolValue];
            if (value >= 30)
                self.reportLocationFrequency = value;
            self.locationDisable = off;
            [self resetReportLocation];
        }
    }
}
- (void)resetReportLocation {
    [JCOREFCCache setObject:@(self.locationDisable) forKey:@"locationDisable"];
    [JCOREFCCache setObject:@(self.reportLocationFrequency) forKey:@"locationReportFrequency"];
    if (self.locationDisable || ![JCoreParameter shared].locationEnable) {
        [self stopLocationStrategy];
        return;
    }
    NSTimeInterval now = [JCOREUtilities currentTimestamp];
    _reportTimerManager.lastSaveTimeStamp = now - _reportTimerManager.lastSaveTimeStamp;
    _reportTimerManager.frequency = self.reportLocationFrequency;
    [_collectionManager resetCollectTimer];
    [_reportTimerManager startTimerCheck];
}
- (void)resetCollectLocationFrequency:(NSTimeInterval)frequency {
    self.collectLocationFrequency = frequency;
    [JCOREFCCache setObject:@(frequency) forKey:@"locationCollectFrequency"];
    [_collectionManager setCollectLocationFrequency:frequency];
    [_collectionManager resetCollectTimer];
}
- (void)startLocationStrategy {
    LocationLog(8, 642, @"star Report LocationS trategy");
    [self startReportTimer];
}
- (void)stopLocationStrategy {
    LocationLog(8, 647, @"stop LocationS trategy");
    [self stopTimer];
}
- (void)reportLocation {
    [JCommonService trackLocInfoWithCode:1201];
    if (self.locationDisable) {
        [JCommonService trackLocInfoWithCode:1202];
        return;
    }
    if (![JCoreParameter shared].locationEnable) {
        [JCommonService trackLocInfoWithCode:1203];
        return;
    }
    _needReport = YES;
    if ([_collectionManager authorized]) {
        __weak JCORELocationController *weakSelf = self;
        [_collectionManager handleCollectLocationBeforeSendReport:^(BOOL success) {
          if (self->_needReport)
              [weakSelf sendReportRequest];
        }];
    } else
        [self sendReportRequest];
}
- (void)sendReportRequest {
    [self requestIpAddress:^(NSString *ip) {
      id content = [self reportContentWithIpv6Address:ip];
      [JCommonReportManager sendAppLocationReport:content
                                completionHandler:^(id response, NSError *error) {
                                  if (!error) {
                                      LocationLog(8, 687, @"sendAppLocationReport success");
                                      [JCOREFCCache removeObjectForFile:@"JCommonLocationLogTmp"];
                                      [self->_collectionManager clearReportLoacationData];
                                      self->_needReport = NO;
                                      [JCommonService trackLocInfoWithCode:1200];
                                  } else
                                      [JCommonService trackLocInfoWithCode:1204];
                                }];
    }];
}
- (void)requestIpAddress:(void (^)(NSString *))completion {
    if ([JCOREService getCheckIpv6Switch]) {
        JCOREIpvxHelper *helper = [JCOREIpvxHelper new];
        NSInteger type = helper.ipvxType;
        if (type > 5 || ((1ULL << (type & 63)) & 0x34) == 0) {
            LocationLog(0x100, 707, @"location: ipv6 check open, current network may not ipv6, return");
            if (completion)
                completion(@"");
            return;
        }
    }
    JCommonIpAddressRequest *request = [JCommonIpAddressRequest new];
    [request sendRequest:^(NSString *ip, NSError *error) {
      NSString *result = ip && [ip isKindOfClass:[NSString class]] && ip.length ? ip : @"";
      if (completion)
          completion(result);
    }];
}
- (void)startReportTimer {
    [_collectionManager startCollectLocationTimer];
    if (!_reportTimerManager) {
        _reportTimerManager = [JCommonReportTimerManager manager];
        __weak JCORELocationController *weakSelf = self;
        _reportTimerManager.timerFuncBlock = ^{
          [weakSelf reportLocation];
        };
        id previous = [JCORECacheChannel locationCheckTime];
        if (![previous isKindOfClass:[NSNumber class]])
            previous = [JCOREFCCache objectForKey:@"__JCOMMONLocationTimer"];
        else
            [JCORECacheChannel setLocationCheckTime:nil];
        if ([previous isKindOfClass:[NSNumber class]])
            _lastLocationTimestamp = (double)[previous longLongValue];
        _reportTimerManager.lastSaveTimeStamp = _lastLocationTimestamp;
        _reportTimerManager.frequency = self.reportLocationFrequency;
        _reportTimerManager.saveKey = @"__JCOMMONLocationTimer";
        _reportTimerManager.firstLoadReport = YES;
    }
    [_reportTimerManager startTimerCheck];
}
- (void)stopTimer {
    [_reportTimerManager stopTimerCheck];
    [_collectionManager stopTimer];
}
@end
