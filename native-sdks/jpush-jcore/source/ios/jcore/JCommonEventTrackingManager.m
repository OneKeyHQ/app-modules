#import "JCommonEventTrackingManager.h"
#import "JCommonReportTimerManager.h"
#import "JCommonReportManager.h"
#import "JCOREFCCache.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"
#define JCommonTrackingFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/JCommonEventTrackingManager.m"
#define JCommonTrackingLog(sourceLine, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCommonTrackingFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
@implementation JCommonEventTrackingManager
@synthesize status = _status, hasDecide = _hasDecide, trackingData = _trackingData, timerManager = _timerManager;
+ (instancetype)sharedInstance {
    static JCommonEventTrackingManager *manager;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ manager = [[self alloc] init]; });
    return manager;
}
- (instancetype)init { self = [super init]; if (self) [self loadDataFromCache]; return self; }
+ (void)trackUAIDWithCode:(long long)code { [[JCommonEventTrackingManager sharedInstance] trackEventWithType:4 code:code]; }
+ (void)trackAppListWithCode:(long long)code { [[JCommonEventTrackingManager sharedInstance] trackEventWithType:11 code:code]; }
+ (void)trackLocInfoWithCode:(long long)code { [[JCommonEventTrackingManager sharedInstance] trackEventWithType:6 code:code]; }
+ (void)trackGPSWithCode:(long long)code { [[JCommonEventTrackingManager sharedInstance] trackEventWithType:7 code:code]; }
+ (void)trackWiFiWithCode:(long long)code { [[JCommonEventTrackingManager sharedInstance] trackEventWithType:9 code:code]; }
+ (void)trackCellWithCode:(long long)code { [[JCommonEventTrackingManager sharedInstance] trackEventWithType:8 code:code]; }
+ (void)setEventTrackingOn {
    [JCommonEventTrackingManager sharedInstance].hasDecide = YES;
    [JCommonEventTrackingManager sharedInstance].status = YES;
    [[JCommonEventTrackingManager sharedInstance] startReporting];
}
+ (void)setEventTrackingOff {
    [JCommonEventTrackingManager sharedInstance].hasDecide = YES;
    [JCommonEventTrackingManager sharedInstance].status = NO;
    [[JCommonEventTrackingManager sharedInstance] stopReporting];
}
- (void)startReporting {
    if (!_timerManager) {
        _timerManager = [JCommonReportTimerManager manager];
        __weak JCommonEventTrackingManager *weakSelf = self;
        _timerManager.timerFuncBlock = ^{ [weakSelf reportTrackingData]; };
        _timerManager.frequency = 3600;
        _timerManager.saveKey = @"__JCommonEventTrackingTimerKey";
        _timerManager.firstLoadReport = NO;
    }
    [_timerManager startTimerCheck];
    JCommonTrackingLog(110, @"[EventTracking] Started reporting with interval: %f seconds", 3600.0);
}
- (void)stopReporting {
    [_timerManager stopTimerCheck];
    [self.trackingData removeAllObjects];
    [self clearDataCache];
    JCommonTrackingLog(118, @"[EventTracking] Stopped reporting, clean cache");
}
- (void)trackEventWithType:(long long)type code:(long long)code {
    JCommonTrackingLog(125, @"[EventTracking] Tracked event - type: %ld, code: %ld, status: %ld", (long)type, (long)code, (long)self.status);
    if (self.hasDecide && !self.status) return;
    NSDictionary *dictionary = [self getExceptionTypeDictionary:type];
    if (!dictionary) {
        dictionary = @{@"result": [NSMutableDictionary dictionary], @"exception_type": [NSString stringWithFormat:@"%ld", (long)type]};
        [self.trackingData addObject:dictionary];
    }
    NSMutableDictionary *results = [dictionary[@"result"] mutableCopy];
    NSString *key = [NSString stringWithFormat:@"%ld", (long)code];
    NSInteger count = [results[key] integerValue];
    results[key] = @((long)((NSUInteger)count + 1));
    NSMutableDictionary *updated = [dictionary mutableCopy];
    updated[@"result"] = results;
    [self updateExceptionTypeDictionary:updated];
    [self saveDataToCache];
}
- (void)loadDataFromCache {
    id cached = [JCOREFCCache objectForFile:@"__JCommonEventTrackingCache"];
    self.trackingData = cached && [cached isKindOfClass:NSArray.class] ? [NSMutableArray arrayWithArray:cached] : [NSMutableArray array];
    JCommonTrackingLog(170, @"[EventTracking] Loaded %lu tracking records from cache", (unsigned long)self.trackingData.count);
}
- (void)saveDataToCache { [JCOREFCCache setObject:self.trackingData forFile:@"__JCommonEventTrackingCache"]; }
- (void)clearDataCache { [JCOREFCCache setObject:nil forFile:@"__JCommonEventTrackingCache"]; }
- (NSDictionary *)getExceptionTypeDictionary:(long long)type {
    NSString *value = [NSString stringWithFormat:@"%ld", (long)type];
    for (NSDictionary *item in self.trackingData) if ([item[@"exception_type"] isEqualToString:value]) return item;
    return nil;
}
- (void)updateExceptionTypeDictionary:(NSDictionary *)dictionary {
    id type = dictionary[@"exception_type"];
    for (NSUInteger index = 0; index < self.trackingData.count; index++) {
        if ([self.trackingData[index][@"exception_type"] isEqualToString:type]) { [self.trackingData replaceObjectAtIndex:index withObject:dictionary]; return; }
    }
    [self.trackingData addObject:dictionary];
}
- (void)reportTrackingData {
    if (!self.trackingData.count) { JCommonTrackingLog(214, @"[EventTracking] No data to report"); return; }
    NSMutableArray *data = [NSMutableArray array];
    for (NSDictionary *item in self.trackingData) {
        NSMutableDictionary *result = [NSMutableDictionary dictionary];
        result[@"exception_type"] = item[@"exception_type"];
        NSMutableString *encoded = [NSMutableString string];
        NSDictionary *counts = item[@"result"];
        BOOL first = YES;
        for (id key in counts.allKeys) {
            id count = counts[key];
            if (!first) [encoded appendString:@"&"];
            [encoded appendFormat:@"%@_%@", key, count];
            first = NO;
        }
        result[@"result"] = encoded;
        [data addObject:result];
    }
    NSString *sequence = NSUUID.UUID.UUIDString;
    NSDictionary *information = @{@"data": data, @"sequence": sequence, @"itime": @((long)[JCOREUtilities currentTimestamp]), @"type": @"exception_stats"};
    JCommonTrackingLog(251, @"[EventTracking] Reporting %lu tracking records", (unsigned long)data.count);
    [JCommonReportManager sendExceptionReport:information completionHandler:^(id response, NSError *error) {
        if (error) {
            [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCommonTrackingFile function:"-[JCommonEventTrackingManager reportTrackingData]_block_invoke" line:261 tag:nil format:@"[EventTracking] Report failed: %@", error];
            return;
        }
        [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCommonTrackingFile function:"-[JCommonEventTrackingManager reportTrackingData]_block_invoke" line:257 tag:nil format:@"[EventTracking] Report success, clearing data"];
        self.trackingData = [NSMutableArray array];
        [self saveDataToCache];
    }];
}
@end
