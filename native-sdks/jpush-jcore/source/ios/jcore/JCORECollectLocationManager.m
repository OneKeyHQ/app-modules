#import "JCORECollectLocationManager.h"
#import "JCORECacheChannel.h"
#import "JCOREFCCache.h"
#import "JCORELocationController.h"
#import "JCORELog.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "JCommonReportTimerManager.h"
#import "JCoreParameter.h"
#include <math.h>
@interface JCommonService : NSObject
+ (void)trackGPSWithCode:(long long)code;
+ (void)trackWiFiWithCode:(long long)code;
+ (void)trackCellWithCode:(long long)code;
+ (void)trackLocInfoWithCode:(long long)code;
@end
#define CollectFile                                                                                          \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Location/JCORELocationController.m"
#define CollectLog(asyncValue, flagValue, lineValue, fmt, ...)                                               \
    [JCORELog log:asyncValue                                                                                 \
            level:0x3ff                                                                                      \
             flag:flagValue                                                                                  \
          context:0                                                                                          \
             file:CollectFile                                                                                \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineValue                                                                                  \
              tag:nil                                                                                        \
           format:fmt, ##__VA_ARGS__]
@implementation JCORECollectLocationManager
@dynamic hash, superclass, description, debugDescription;
- (instancetype)initWith:(NSTimeInterval)frequency {
    self = [super init];
    if (self) {
        _locationInfoArray = [NSMutableArray arrayWithCapacity:7];
        id cached = [JCOREFCCache objectForFile:@"JCommonLocationLogTmp"];
        if ([cached isKindOfClass:[NSArray class]])
            [_locationInfoArray addObjectsFromArray:cached];
        [self setupLocationManager];
        [self setLBSCollectFrequency:frequency];
    }
    return self;
}
- (void)dealloc {
    _locationInfoArray = nil;
    _locationManager = nil;
    _latestLocation = nil;
    _locationUpdateTimer = nil;
    _collectTimerManager = nil;
}
- (CLLocation *)latestLocation {
    if (!_latestLocation && [JCoreParameter shared].locationEnable)
        _latestLocation = _locationManager.location;
    return _latestLocation;
}
- (BOOL)authorized {
    return _locationAuthorizationStatus > 2;
}
// Preserve the original class authorizationStatus API and its dispatch order.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
- (void)setupLocationManager {
    if ([CLLocationManager locationServicesEnabled])
        dispatch_async(dispatch_get_main_queue(), ^{
          CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
          CLLocationManager *manager = [CLLocationManager new];
          NSMutableDictionary *info = [NSMutableDictionary new];
          [info setValue:@(status) forKey:@"status"];
          if (manager)
              [info setValue:manager forKey:@"locationManager"];
          [JCOREThread performSel:@selector(setupLocationManagerWith:) target:self withObject:[info copy]];
        });
}
#pragma clang diagnostic pop
- (void)setupLocationManagerWith:(id)info {
    if (![info isKindOfClass:[NSDictionary class]])
        return;
    id status = info[@"status"];
    if ([status isKindOfClass:[NSNumber class]])
        _locationAuthorizationStatus = [status intValue];
    _collectLocationFlag = 0;
    _locationManager = info[@"locationManager"];
    _locationManager.delegate = self;
    _locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    _locationManager.distanceFilter = kCLDistanceFilterNone;
}
- (id)locationReportArray {
    if (!_locationInfoArray.count)
        [self addLocationInfo:nil];
    return _locationInfoArray;
}
- (void)clearReportLoacationData {
    if (_locationInfoArray)
        [_locationInfoArray removeAllObjects];
}
- (void)setLBSCollectFrequency:(NSTimeInterval)frequency {
    self.collectLocationFrequency = frequency;
    id previous = [JCORECacheChannel collectLocationCheckTime];
    if (previous)
        [JCORECacheChannel setCollectLocationCheckTime:nil];
    else
        previous = [JCOREFCCache objectForKey:@"__JCOMMONCollectLocationTimer"];
    _lastCollectLocationTimestamp = previous ? (double)[previous longValue] : 0;
    if ([CLLocationManager locationServicesEnabled] && [self authorized])
        [self startCollectLocationTimer];
}
- (void)startCollectLocationTimer {
    if (!_collectTimerManager) {
        _collectTimerManager = [JCommonReportTimerManager manager];
        __weak JCORECollectLocationManager *weakSelf = self;
        _collectTimerManager.timerFuncBlock = ^{
          [weakSelf collectLocation];
        };
        _collectTimerManager.frequency = _collectLocationFrequency;
        _collectTimerManager.lastSaveTimeStamp = _lastCollectLocationTimestamp;
        _collectTimerManager.saveKey = @"__JCOMMONCollectLocationTimer";
        _collectTimerManager.firstLoadReport = YES;
    }
    [_collectTimerManager startTimerCheck];
}
- (void)resetCollectTimer {
    [self stopTimer];
    _collectTimerManager.frequency = _collectLocationFrequency;
    [_collectTimerManager startTimerCheck];
}
- (void)stopTimer {
    [_collectTimerManager stopTimerCheck];
}
- (void)handleCollectLocationBeforeSendReport:(void (^)(BOOL))completion {
    if (![self authorized])
        return;
    NSTimeInterval now = [JCOREUtilities currentTimestamp], age = now - _lastCollectLocationTimestamp;
    if (age >= 30 || age < 0) {
        NSTimeInterval offset = now - (_lastCollectLocationTimestamp + _collectLocationFrequency);
        [self collectLocation];
        if (offset > -30 && offset <= 0)
            _alreadyCollect = YES;
        self.collectLBSLoactionHandle = completion;
    } else if (_collectLocationFlag == 2)
        completion(YES);
}
- (void)collectLocation {
    if (_alreadyCollect) {
        _alreadyCollect = NO;
        return;
    }
    [JCommonService trackGPSWithCode:1301];
    if ([JCORELocationController sharedInstane].locationDisable) {
        [JCommonService trackGPSWithCode:1302];
        return;
    }
    if (![JCoreParameter shared].locationEnable) {
        [JCommonService trackGPSWithCode:1303];
        return;
    }
    if (_locationAuthorizationStatus > 2 && _locationManager) {
        [_locationManager startUpdatingLocation];
        _collectLocationFlag = 1;
        _locationUpdateTimer = [NSTimer scheduledTimerWithTimeInterval:10
                                                                target:self
                                                              selector:@selector(saveLatestLocation)
                                                              userInfo:nil
                                                               repeats:NO];
        return;
    }
    CollectLog(YES, 0x100, 212, @"collectLocation locationAuthorization:%d");
    if (_locationAuthorizationStatus < 3)
        [JCommonService trackGPSWithCode:1304];
    else if (!_locationManager) {
        CollectLog(YES, 0x100, 216, @"_locationManager is nil");
        [JCommonService trackGPSWithCode:1305];
    }
    if (self.collectLBSLoactionHandle)
        self.collectLBSLoactionHandle(YES);
}
- (void)saveLatestLocation {
    [_locationManager stopUpdatingLocation];
    if (_locationUpdateTimer.isValid) {
        [_locationUpdateTimer invalidate];
        _locationUpdateTimer = nil;
    }
    if (!_latestLocation) {
        [JCommonService trackGPSWithCode:1306];
        CollectLog(NO, 0x20, 232, @"collect locationInfo error, location is nil");
        [self addLocationInfo:@{}];
    } else {
        @try {
            [JCommonService trackGPSWithCode:1300];
            NSTimeInterval timestamp = [_latestLocation.timestamp timeIntervalSince1970];
            if (timestamp <= 0)
                timestamp = [[NSDate date] timeIntervalSince1970];
            else
                timestamp = [_latestLocation.timestamp timeIntervalSince1970];
            NSDictionary *location = @{
                @"lat" : @(_latestLocation.coordinate.latitude),
                @"lng" : @(_latestLocation.coordinate.longitude),
                @"altitude" : @(_latestLocation.altitude),
                @"horizontalAccuracy" : @(_latestLocation.horizontalAccuracy),
                @"verticalAccuracy" : @(_latestLocation.verticalAccuracy),
                @"itime" : @(timestamp)
            };
            _lastCollectLocationTimestamp = [JCOREUtilities currentTimestamp];
            _collectLocationFlag = 2;
            [self addLocationInfo:location];
        } @catch (NSException *exception) {
            CollectLog(YES, 0x100, 250, @"[%s] is exception", __PRETTY_FUNCTION__);
        }
    }
    if (self.collectLBSLoactionHandle)
        self.collectLBSLoactionHandle(YES);
}
- (void)addLocationInfo:(id)info {
    NSMutableDictionary *content = [NSMutableDictionary new];
    NSInteger now = (NSInteger)[JCOREUtilities currentTimestamp];
    if (![info count])
        content[@"itime"] = @(now);
    else {
        content[@"itime"] = info[@"itime"] ? info[@"itime"] : @(now);
        content[@"gps"] = @[ info ];
    }
    [JCommonService trackWiFiWithCode:1401];
    [JCommonService trackCellWithCode:1501];
    id wifi = [self wifiInfo], cell = [self cellInfo];
    if ([wifi count]) {
        [JCommonService trackWiFiWithCode:1400];
        content[@"wifi"] = @[ wifi ];
    } else
        [JCommonService trackWiFiWithCode:1402];
    if ([cell count]) {
        [JCommonService trackCellWithCode:1500];
        content[@"cell"] = @[ cell ];
    } else
        [JCommonService trackCellWithCode:1502];
    [_locationInfoArray addObject:content];
    if (_locationInfoArray.count == 7)
        [_locationInfoArray removeObjectAtIndex:0];
    [JCOREFCCache setObject:_locationInfoArray forFile:@"JCommonLocationLogTmp"];
    [JCommonService trackLocInfoWithCode:1210];
}
- (id)wifiInfo {
    id mac = [JCOREUtilities macAddress], ssid = [JCOREUtilities ssid];
    if (!mac && !ssid)
        return @{};
    NSDictionary *content = @{
        @"itime" : @((NSInteger)[JCOREUtilities currentTimestamp]),
        @"mac_address" : mac ?: @"UNKNOWN",
        @"ssid" : ssid ?: @"UNKNOWN"
    };
    CollectLog(YES, 8, 307, @"wifi 信息:%@", content);
    return content;
}
- (id)cellInfo {
    id mnc = [JCOREUtilities ctMobileNetworkCode], mcc = [JCOREUtilities ctCountryCode],
       radio = [JCOREUtilities ctRadioType], generation = [JCOREUtilities ctGeneration];
    NSInteger now = (NSInteger)[JCOREUtilities currentTimestamp];
    if (!mnc && !mcc && !radio && !generation)
        return @{};
    NSDictionary *content = @{
        @"itime" : @(now),
        @"mobile_network_code" : mnc ?: @"-1",
        @"mobile_country_code" : mcc ?: @"-1",
        @"radio_type" : radio ?: @"UNKNOWN",
        @"generation" : generation ?: @"UNKNOWN"
    };
    CollectLog(YES, 8, 328, @"WWAN 信息:%@", content);
    return content;
}
- (void)locationManager:(CLLocationManager *)manager
    didChangeAuthorizationStatus:(CLAuthorizationStatus)status {
    [JCOREThread performSel:@selector(handleChangeAuthorizationStatus:) target:self withObject:@(status)];
}
- (void)handleChangeAuthorizationStatus:(id)status {
    static BOOL change;
    if (![status isKindOfClass:[NSNumber class]])
        return;
    int value = [status intValue];
    if (_locationAuthorizationStatus < 3) {
        if (value > 2 && !change) {
            change = YES;
            _locationAuthorizationStatus = value;
            [self startCollectLocationTimer];
            return;
        }
    } else
        change = YES;
    _locationAuthorizationStatus = value;
}
- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray<CLLocation *> *)locations {
    NSMutableArray *copied = [NSMutableArray array];
    for (CLLocation *location in locations) {
        CLLocation *copy = [[CLLocation alloc] initWithCoordinate:location.coordinate
                                                         altitude:location.altitude
                                               horizontalAccuracy:location.horizontalAccuracy
                                                 verticalAccuracy:location.verticalAccuracy
                                                           course:location.course
                                                            speed:location.speed
                                                        timestamp:location.timestamp];
        [copied addObject:copy];
    }
    [JCOREThread performSel:@selector(handleUpdateLocations:) target:self withObject:copied];
}
- (void)handleUpdateLocations:(id)locations {
    if (![JCoreParameter shared].locationEnable)
        return;
    CollectLog(YES, 0x100, 380, @"locationManager didUpdateLocations:%@", locations);
    if (_collectLocationFlag == 2)
        return;
    if ([locations isKindOfClass:[NSArray class]] && [locations count]) {
        CLLocation *latest = [locations lastObject];
        if (latest && latest.horizontalAccuracy >= 0 && latest.verticalAccuracy >= 0) {
            _latestLocation = latest;
            if (fabs([latest.timestamp timeIntervalSinceNow]) < 60)
                [self saveLatestLocation];
        }
    }
}
- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error {
    CollectLog(NO, 0x20, 398, @"locationManager didFailWithError: %@", error);
}
@end
