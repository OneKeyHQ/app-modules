#import "JCOREHttpEventController.h"
#import "JCORENetChannel.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"
#import "RecoveredCache.h"
#import "RecoveredProtocols.h"

#import "JCOREReport.h"

#define JCOREHttpEventFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Controller/Network/JCOREHttpEventController.m"
#define JCOREHttpEventLog(sourceLine, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCOREHttpEventFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
@implementation JCOREHttpEventController
@synthesize pendingHistoryList = _pendingHistoryList, isReporting = _isReporting;
- (instancetype)init {
    self = [super init];
    if (self) [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(reportHistoryList) name:@"kJCOREHttpReportHistoryNotification" object:nil];
    return self;
}
- (void)dealloc { [NSNotificationCenter.defaultCenter removeObserver:self]; }
- (NSMutableArray *)pendingHistoryList { if (!_pendingHistoryList) _pendingHistoryList = [NSMutableArray array]; return _pendingHistoryList; }
- (void)reportHistoryList {
    if (![JCOREThread isMainThread]) {
        [JCOREThread performSel:@selector(reportHistoryList) target:self withObject:nil];
        return;
    }
    if (self.isReporting) { JCOREHttpEventLog(53, @"history data: history data is reporting, return"); return; }
    if ([JCORENetChannel httpRequesting]) return;
    self.isReporting = YES;
    JCOREHttpEventLog(60, @"history data:begin reporting history data");
    [self.pendingHistoryList removeAllObjects];
    [JCORECacheUtilities reportObjectsWithBlock:^(id result) {
        if ([result isKindOfClass:NSArray.class]) [self.pendingHistoryList addObjectsFromArray:result];
        else [self beginReport];
    }];
}
- (void)beginReport {
    JCOREHttpEventLog(72, @"%@", self.pendingHistoryList.count ? @"has history data" : @"history data: (null)");
    if (self.pendingHistoryList.count) [self testConnectionWithHistoryData];
    else [self endReportHistoryList];
}
- (void)testConnectionWithHistoryData {
    if (!self.pendingHistoryList.count) { [self endReportHistoryList]; return; }
    id item = self.pendingHistoryList.firstObject;
    if (![item isKindOfClass:NSDictionary.class]) {
        [self.pendingHistoryList removeObjectAtIndex:0];
        [self testConnectionWithHistoryData];
        return;
    }
    id object = item[@"object"], identifier = item[@"id"];
    JCOREReport *report = [[JCOREReport alloc] init];
    report.requestInfo = [JCOREUtilities dataToJSON:object];
    report.reportFlag = identifier;
    report.cacheRequired = NO;
    report.completionHandler = ^(id response, NSError *error) {
        if (!error) {
            [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCOREHttpEventFile function:"-[JCOREHttpEventController testConnectionWithHistoryData]_block_invoke" line:104 tag:nil format:@"history data: Connection test successful, starting to report remaining history data"];
            if (self.pendingHistoryList.count) [self.pendingHistoryList removeObjectAtIndex:0];
            [self reportRemainingHistoryData];
        } else {
            [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCOREHttpEventFile function:"-[JCOREHttpEventController testConnectionWithHistoryData]_block_invoke" line:112 tag:nil format:@"history data: Connection test failed, stopping history data report: %@", error.localizedDescription];
            [self endReportHistoryList];
        }
    };
    JCOREHttpEventLog(117, @"history data: Testing connection with history data:%@", report.requestInfo);
    [JCORENetChannel send:report];
}
- (void)reportRemainingHistoryData {
    if (!self.pendingHistoryList.count) { [self endReportHistoryList]; return; }
    __block NSInteger remaining = (NSInteger)self.pendingHistoryList.count;
    for (id item in self.pendingHistoryList) {
        if (![item isKindOfClass:NSDictionary.class]) { remaining--; continue; }
        id object = item[@"object"], identifier = item[@"id"];
        JCOREReport *report = [[JCOREReport alloc] init];
        report.requestInfo = [JCOREUtilities dataToJSON:object];
        report.reportFlag = identifier;
        report.cacheRequired = NO;
        report.completionHandler = ^(id response, NSError *error) {
            remaining--;
            if (remaining <= 0) [self endReportHistoryList];
        };
        JCOREHttpEventLog(145, @"history data: send history data:%@", report.requestInfo);
        [JCORENetChannel send:report];
    }
}
- (void)endReportHistoryList {
    JCOREHttpEventLog(152, @"history data: endReportHistoryList");
    self.isReporting = NO;
    [self.pendingHistoryList removeAllObjects];
}
@end
