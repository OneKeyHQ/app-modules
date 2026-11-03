#import "JCORENetDataManager.h"
#import "JCORENetDataMoniter.h"
#import "JCommonReportTimerManager.h"
#import "JCOREFCCache.h"
#import "JCOREUtilities.h"
#import "JCORENetChannel.h"
#import "RecoveredCoreService.h"
#import "JCORELog.h"
#import "JCOREReport.h"
#import "JCommonService.h"
#define JCORENetDataFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Superset/JCORENetDataManager.m"
#define JCORENetDataLog(sourceLine, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCORENetDataFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
static BOOL JCORENetDataEnabled;
@implementation JCORENetDataManager
@synthesize netDataMoniterContainer = _netDataMoniterContainer, uuid = _uuid, timerManager = _timerManager;
@synthesize netByte = _netByte, netCount = _netCount, netConnByte = _netConnByte, netConnCount = _netConnCount;
+ (instancetype)sharedManager {
    static JCORENetDataManager *manager;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ if (!manager) { manager = [[JCORENetDataManager alloc] init]; manager.uuid = NSUUID.UUID.UUIDString; } });
    return manager;
}
+ (void)startHttpMoniter:(NSString *)url request_byte:(long long)bytes { if (JCORENetDataEnabled) [[JCORENetDataManager sharedManager] startHttpMoniter:url request_byte:bytes]; }
+ (void)endHttpMoniter:(NSString *)url response_byte:(long long)bytes { if (JCORENetDataEnabled) [[JCORENetDataManager sharedManager] endHttpMoniter:url response_byte:bytes]; }
+ (void)startSocketMoniter:(NSUInteger)type host:(NSString *)host port:(long long)port request_byte:(long long)bytes {
    if (!JCORENetDataEnabled) return;
    if (type == 1) [[JCORENetDataManager sharedManager] startHttpMoniter:[NSString stringWithFormat:@"%@:%@", host, @((NSInteger)port)] request_byte:bytes];
    else [[JCORENetDataManager sharedManager] startConnectionMoniter:host port:port request_byte:bytes];
}
+ (void)endSocketMoniter:(NSUInteger)type host:(NSString *)host port:(long long)port response_byte:(long long)bytes {
    if (!JCORENetDataEnabled) return;
    if (type == 1) [[JCORENetDataManager sharedManager] endHttpMoniter:[NSString stringWithFormat:@"%@:%@", host, @((NSInteger)port)] response_byte:bytes];
    else [[JCORENetDataManager sharedManager] endConnectionMoniter:host port:port response_byte:bytes];
}
+ (void)setDataMoniterOn {
    JCORENetDataEnabled = YES;
    [[JCORENetDataManager sharedManager] getDataFromCache];
    [[JCORENetDataManager sharedManager] startTimer];
}
- (instancetype)init { return [super init]; }
- (void)dealloc { _timerManager = nil; }
- (void)getDataFromCache {
    id cached = [JCOREFCCache objectForFile:@"__JCoreNETDataMoniter"];
    self.netDataMoniterContainer = [NSMutableDictionary dictionary];
    if (![cached isKindOfClass:NSDictionary.class]) return;
    double now = [JCOREUtilities currentTimestamp];
    for (NSString *key in [cached allKeys]) {
        id information = cached[key];
        double timestamp = [[key componentsSeparatedByString:@"/"][0] doubleValue];
        if ([self isExpire:now otherTimeStamp:timestamp]) continue;
        JCORENetDataMoniter *monitor = [[JCORENetDataMoniter alloc] initWithDic:information];
        self.netDataMoniterContainer[key] = monitor;
        if ([self isToday:timestamp]) {
            if (monitor.sortType == 0) {
                self.netByte += (double)monitor.request_byte;
                self.netByte += (double)monitor.response_byte;
                self.netCount = (long long)((unsigned long long)self.netCount + (unsigned long long)monitor.count);
            } else {
                self.netConnByte += (double)monitor.request_byte;
                self.netConnByte += (double)monitor.response_byte;
                self.netConnCount = (long long)((unsigned long long)self.netConnCount + (unsigned long long)monitor.count);
            }
        }
    }
    [self checkTcpThreshold];
    [self checkHTTPThreshold];
}
- (void)saveData {
    NSMutableDictionary *saved = [NSMutableDictionary dictionary];
    for (NSString *key in self.netDataMoniterContainer.allKeys) [saved setObject:[self.netDataMoniterContainer[key] saveInfo] forKey:key];
    [JCOREFCCache setObject:saved forFile:@"__JCoreNETDataMoniter"];
}
- (void)startTimer {
    if (!_timerManager) {
        _timerManager = [JCommonReportTimerManager manager];
        __weak JCORENetDataManager *weakSelf = self;
        _timerManager.timerFuncBlock = ^{ [weakSelf reportData]; };
        _timerManager.frequency = 86400;
        _timerManager.saveKey = @"__JCOMMONetDataMoniterCheckTime";
        _timerManager.firstLoadReport = NO;
    }
    [_timerManager startTimerCheck];
}
- (void)stopTimer { [_timerManager stopTimerCheck]; }
- (void)startHttpMoniter:(NSString *)url request_byte:(long long)bytes {
    NSString *normalized = [self changeUrl:url];
    JCORENetDataMoniter *monitor = [self findProperMoniter:normalized];
    monitor.sortType = 0;
    monitor.request_byte = (long long)((unsigned long long)monitor.request_byte + (unsigned long long)bytes);
    monitor.request_count = (long long)((unsigned long long)monitor.request_count + 1);
    self.netByte += (double)bytes;
    self.netCount = (long long)((unsigned long long)self.netCount + 1);
    self.netDataMoniterContainer[[NSString stringWithFormat:@"%@/%@", @(monitor.timestamp), normalized]] = monitor;
    [self saveData];
    [self checkHTTPThreshold];
}
- (void)endHttpMoniter:(NSString *)url response_byte:(long long)bytes {
    NSString *normalized = [self changeUrl:url];
    JCORENetDataMoniter *monitor = [self findProperMoniter:normalized];
    monitor.sortType = 0;
    monitor.response_byte = (long long)((unsigned long long)monitor.response_byte + (unsigned long long)bytes);
    monitor.response_count = (long long)((unsigned long long)monitor.response_count + 1);
    self.netByte += (double)bytes;
    self.netCount = (long long)((unsigned long long)self.netCount + 1);
    self.netDataMoniterContainer[[NSString stringWithFormat:@"%@/%@", @(monitor.timestamp), normalized]] = monitor;
    [self saveData];
    [self checkHTTPThreshold];
}
- (void)startConnectionMoniter:(NSString *)host port:(long long)port request_byte:(long long)bytes {
    NSString *url = [self changeUrl:[NSString stringWithFormat:@"%@:%@", host, @((NSInteger)port)]];
    JCORENetDataMoniter *monitor = [self findProperMoniter:url];
    monitor.sortType = 1;
    monitor.request_byte = (long long)((unsigned long long)monitor.request_byte + (unsigned long long)bytes);
    monitor.request_count = (long long)((unsigned long long)monitor.request_count + 1);
    self.netConnByte += (double)bytes;
    self.netConnCount = (long long)((unsigned long long)self.netConnCount + 1);
    self.netDataMoniterContainer[[NSString stringWithFormat:@"%@/%@", @(monitor.timestamp), url]] = monitor;
    [self saveData];
    [self checkTcpThreshold];
}
- (void)endConnectionMoniter:(NSString *)host port:(long long)port response_byte:(long long)bytes {
    NSString *url = [self changeUrl:[NSString stringWithFormat:@"%@:%@", host, @((NSInteger)port)]];
    JCORENetDataMoniter *monitor = [self findProperMoniter:url];
    monitor.sortType = 1;
    monitor.response_byte = (long long)((unsigned long long)monitor.response_byte + (unsigned long long)bytes);
    monitor.response_count = (long long)((unsigned long long)monitor.response_count + 1);
    self.netConnByte += (double)bytes;
    self.netConnCount = (long long)((unsigned long long)self.netConnCount + 1);
    self.netDataMoniterContainer[[NSString stringWithFormat:@"%@/%@", @(monitor.timestamp), url]] = monitor;
    [self saveData];
    [self checkTcpThreshold];
}
- (JCORENetDataMoniter *)findProperMoniter:(NSString *)url {
    double now = [@((long)[JCOREUtilities currentTimestamp]) doubleValue];
    for (NSString *key in self.netDataMoniterContainer.allKeys) {
        double timestamp = [[key componentsSeparatedByString:@"/"][0] doubleValue];
        NSString *storedURL = [key componentsSeparatedByString:@"/"][1];
        if ([self isSameDay:timestamp otherDay:now] && [storedURL isEqualToString:url]) return self.netDataMoniterContainer[key];
    }
    JCORENetDataMoniter *monitor = [[JCORENetDataMoniter alloc] init];
    monitor.moniter_type = @"NET_DATA";
    monitor.uuid = [JCORENetDataManager sharedManager].uuid;
    monitor.moniter_id = NSUUID.UUID.UUIDString;
    monitor.timestamp = now;
    monitor.url = url;
    return monitor;
}
- (NSDictionary *)reportDataUrlTypeList {
    return @{@"wi/cjc4sa": @"1", @"wi/d8n3hj": @"2", @"v1/status": @"3", @"tsis": @"4", @"ali-stats": @"6", @"gd-stats": @"7", @"stats": @"5", @"v1/version-check": @"8", @"wi/plp8j0k": @"9", @"wi/spoml": @"10", @"qiniup": @"11", @"1/getEventV2": @"12", @"1/setUserInfo": @"13", @"1/setChannel": @"14", @"1/updateUserProperties": @"15", @"1/switcUserProperties": @"16", @"v1/user": @"17", @"wi/qj18op": @"18", @"wi/op8jdu": @"19", @"v1/reqtime": @"20", @"appgwc/status": @"21", @"appawake/status": @"22", @"jx/6ae71c": @"23", @"wi/swelbv": @"24", @"wi/ysdiox": @"25"};
}
- (NSString *)changeUrl:(NSString *)url {
    NSDictionary *types = [self reportDataUrlTypeList];
    for (NSString *key in types) if ([url rangeOfString:key].length) return types[key];
    return [url hasPrefix:@"http"] ? @"1000" : url;
}
- (BOOL)getServiceSwitch { return [[[JCOREService getNetMointerConfig] valueForKey:@"statusNetLengthSwitch"] boolValue]; }
- (void)checkTcpThreshold {
    id config = [JCOREService getNetMointerConfig];
    NSInteger count = [[config valueForKey:@"netConnCountThreshold"] integerValue];
    double megabytes = [[config valueForKey:@"netConnLengthThreshold"] doubleValue];
    double threshold = megabytes <= 0 ? 104857600 : megabytes * 1024 * 1024;
    if (self.netConnByte <= threshold) {
        if (self.netConnCount <= (count > 0 ? count : 3000)) return;
        JCORENetDataLog(350, @"长连接超过了数量上限");
        [JCORENetChannel stopTcpService];
        [JCommonService recordTcpException:1409 message:@"长连接超过了数量上限"];
    } else {
        JCORENetDataLog(344, @"长连接超过了流量上限");
        [JCORENetChannel stopTcpService];
        [JCommonService recordTcpException:1410 message:@"长连接超过了流量上限"];
    }
}
- (void)checkHTTPThreshold {
    id config = [JCOREService getNetMointerConfig];
    double megabytes = [[config valueForKey:@"netLengthThreshold"] doubleValue];
    NSInteger count = [[config valueForKey:@"netCountThreshold"] integerValue];
    double threshold = megabytes <= 0 ? 314572800 : megabytes * 1024 * 1024;
    if (self.netByte <= threshold && self.netCount <= (count > 0 ? count : 7000)) return;
    JCORENetDataLog(363, @"上报超过了数量和流量上线");
    [JCORENetChannel stopHttpService];
}
- (void)reportData {
    if (!JCORENetDataEnabled) { JCORENetDataLog(371, @"net moiter off, not report, return"); return; }
    NSMutableDictionary *snapshot = [NSMutableDictionary dictionaryWithDictionary:self.netDataMoniterContainer];
    NSMutableArray *reports = [NSMutableArray array];
    double now = [JCOREUtilities currentTimestamp];
    for (NSString *key in snapshot.allKeys) {
        double timestamp = [[key componentsSeparatedByString:@"/"][0] doubleValue];
        if ([self isExpire:now otherTimeStamp:timestamp]) [self.netDataMoniterContainer removeObjectForKey:key];
        else if (![self isToday:timestamp]) {
            [reports addObject:[snapshot[key] requestInfo]];
            [self.netDataMoniterContainer removeObjectForKey:key];
        }
    }
    if (!reports.count) { JCORENetDataLog(393, @"has no not today net monitor data to report, return"); return; }
    [self saveData];
    NSMutableArray *batch = [NSMutableArray arrayWithArray:reports];
    [reports removeAllObjects];
    NSDictionary *information = @{@"type": @"sdk_moniter", @"itime": @((long)[JCOREUtilities currentTimestamp]), @"data": batch};
    [JCORENetDataManager sendSupersetMoniter:information completionHandler:^(id response, NSError *error) {}];
}
+ (void)sendSupersetMoniter:(id)information completionHandler:(RecoveryOpaqueCallback)completion {
    JCOREReport *report = [[JCOREReport alloc] init];
    report.requestInfo = information;
    report.completionHandler = completion;
    [JCORENetChannel send:report];
}
- (BOOL)isToday:(double)timestamp {
    NSString *text = [JCOREUtilities converTimestampToString:(long)timestamp];
    return [NSCalendar.currentCalendar isDateInToday:[JCOREUtilities transformStrToDate:text]];
}
- (BOOL)isSameDay:(double)timestamp otherDay:(double)other {
    NSDate *first = [NSDate dateWithTimeIntervalSince1970:timestamp];
    NSDate *second = [NSDate dateWithTimeIntervalSince1970:other];
    NSCalendar *calendar = NSCalendar.currentCalendar;
    NSDateComponents *left = [calendar components:0x1c fromDate:first], *right = [calendar components:0x1c fromDate:second];
    return left.day == right.day && left.month == right.month && left.year == right.year;
}
- (BOOL)isExpire:(double)timestamp otherTimeStamp:(double)other {
    if (other < timestamp - 1296000) return YES;
    return [self isSameDay:timestamp - 1296000 otherDay:other];
}
@end
