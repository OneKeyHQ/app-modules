#import "JCORESupersetManager.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "JCOREFCCache.h"
#import "JCORENetChannel.h"
#import "JCORELog.h"
#import <UIKit/UIKit.h>

#import "JCOREReport.h"

#define JCORESupersetFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Superset/JCORESupersetManager.m"
#define JCORESupersetLog(sourceLine, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCORESupersetFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
static BOOL JCORESupersetEnabled;
@implementation JCORESupersetManager
@synthesize uuid = _uuid, moniterContainer = _moniterContainer, tcp_id = _tcp_id, tcpHost = _tcpHost, tcpPort = _tcpPort;
+ (instancetype)sharedManager {
    static JCORESupersetManager *manager;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        if (manager) return;
        manager = [[JCORESupersetManager alloc] init];
        manager.uuid = NSUUID.UUID.UUIDString;
        manager.moniterContainer = [[NSMutableDictionary alloc] init];
        manager.tcp_id = 0;
        manager.tcpHost = @"";
        manager.tcpPort = 0;
    });
    return manager;
}
+ (NSString *)startHttpMoniter:(NSString *)method url:(NSString *)url request_byte:(long long)bytes {
    if (!JCORESupersetEnabled) return @"";
    JCORESupersetMoniter *monitor = [self commonMoniter:@"NET_HTTP"];
    monitor.method = method ?: @"";
    monitor.url = url ?: @"";
    monitor.request_byte = bytes;
    [JCOREThread performSel:@selector(startHttpMoniter:) target:[self sharedManager] withObject:monitor];
    return monitor.moniter_id;
}
+ (NSString *)startConnectionMoniter:(NSString *)type host:(NSString *)host port:(long long)port {
    if (!JCORESupersetEnabled) return @"";
    JCORESupersetMoniter *monitor = [self commonMoniter:type];
    if ([@"NET_TCP_CONN" isEqualToString:type]) {
        JCORESupersetManager *manager = [self sharedManager];
        manager.tcp_id = (long long)((unsigned long long)manager.tcp_id + 1);
        [self sharedManager].tcpHost = host ?: @"";
        [self sharedManager].tcpPort = port;
    }
    monitor.tcp_id = [NSString stringWithFormat:@"%ld", (long)[self sharedManager].tcp_id];
    monitor.host = host ?: @"";
    monitor.port = port;
    [JCOREThread performSel:@selector(startConnectionMoniter:) target:[self sharedManager] withObject:monitor];
    return monitor.moniter_id;
}
+ (NSString *)startTcpEventMoniter:(NSString *)type {
    NSString *host = [self sharedManager].tcpHost;
    long long port = [self sharedManager].tcpPort;
    return [self startConnectionMoniter:type host:host port:port];
}
+ (void)endHttpMoniter:(NSString *)identifier response_byte:(long long)bytes error_code:(long long)error status_code:(long long)status {
    if (!JCORESupersetEnabled) return;
    JCORESupersetMoniter *monitor = [[JCORESupersetMoniter alloc] init];
    monitor.moniter_id = identifier;
    monitor.response_byte = bytes;
    monitor.error_code = error;
    monitor.status_code = status;
    [JCOREThread performSel:@selector(endHttpMoniter:) target:[self sharedManager] withObject:monitor];
}
+ (void)endConnectionMoniter:(NSString *)identifier error_code:(long long)error status_code:(long long)status {
    if (!JCORESupersetEnabled) return;
    JCORESupersetMoniter *monitor = [[JCORESupersetMoniter alloc] init];
    monitor.moniter_id = identifier;
    monitor.error_code = error;
    monitor.status_code = status;
    [JCOREThread performSel:@selector(endConnectionMoniter:) target:[self sharedManager] withObject:monitor];
}
+ (void)setSupersetMoniterOn {
    if (JCORESupersetEnabled) { JCORESupersetLog(129, @"superset moniter service has started"); return; }
    JCORESupersetEnabled = YES;
    JCORESupersetLog(133, @"superset moniter service start");
    [JCOREThread performSel:@selector(reportAllSuperset) target:[self sharedManager] withObject:nil];
}
+ (void)clearAllCachedDatas { [JCOREFCCache setObject:nil forFile:@"__JCoreNETMoniter"]; }
- (void)startHttpMoniter:(JCORESupersetMoniter *)monitor {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 0), dispatch_get_main_queue(), ^{ monitor.is_foreground = UIApplication.sharedApplication.applicationState != UIApplicationStateBackground; });
    self.moniterContainer[monitor.moniter_id] = monitor;
}
- (void)startConnectionMoniter:(JCORESupersetMoniter *)monitor {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 0), dispatch_get_main_queue(), ^{ monitor.is_foreground = UIApplication.sharedApplication.applicationState != UIApplicationStateBackground; });
    self.moniterContainer[monitor.moniter_id] = monitor;
}
- (void)endHttpMoniter:(JCORESupersetMoniter *)result {
    if (result.moniter_id && [self.moniterContainer.allKeys containsObject:result.moniter_id]) {
        JCORESupersetMoniter *monitor = self.moniterContainer[result.moniter_id];
        monitor.response_byte = result.response_byte;
        monitor.cost_time = (long)(NSDate.date.timeIntervalSince1970 - monitor.timestamp);
        monitor.error_code = result.error_code;
        monitor.status_code = result.status_code;
        [self saveMoniter:monitor];
    }
}
- (void)endConnectionMoniter:(JCORESupersetMoniter *)result {
    if (result.moniter_id && [self.moniterContainer.allKeys containsObject:result.moniter_id]) {
        JCORESupersetMoniter *monitor = self.moniterContainer[result.moniter_id];
        monitor.cost_time = (long)(NSDate.date.timeIntervalSince1970 - monitor.timestamp);
        monitor.error_code = result.error_code;
        monitor.status_code = result.status_code;
        [self saveMoniter:monitor];
    }
}
- (void)saveMoniter:(JCORESupersetMoniter *)monitor {
    JCORESupersetLog(178, @"save net moniter %@ : %@ , url:%@, host:%@", monitor.moniter_type, monitor.moniter_id, monitor.url, monitor.host);
    id information = monitor.requestInfo;
    id cached = [JCOREFCCache objectForFile:@"__JCoreNETMoniter"];
    NSMutableArray *array = cached ? [NSMutableArray arrayWithArray:cached] : nil;
    if (!array) array = [[NSMutableArray alloc] init];
    if (array.count > 1000) [array removeObjectAtIndex:0];
    [array addObject:information];
    [JCOREFCCache setObject:array forFile:@"__JCoreNETMoniter"];
}
- (void)reportAllSuperset {
    id cached = [JCOREFCCache objectForFile:@"__JCoreNETMoniter"];
    NSMutableArray *array = cached ? [NSMutableArray arrayWithArray:cached] : nil;
    if (!array) array = [[NSMutableArray alloc] init];
    NSMutableArray *batch;
    if (array.count < 101) { batch = [NSMutableArray arrayWithArray:array]; [array removeAllObjects]; }
    else { batch = [NSMutableArray arrayWithArray:[array subarrayWithRange:NSMakeRange(0, 50)]]; [array removeObjectsInRange:NSMakeRange(0, 50)]; }
    [JCOREFCCache setObject:[JCOREUtilities jsonToData:array] forFile:@"__JCoreNETMoniter"];
    NSDictionary *information = @{@"type": @"sdk_moniter", @"itime": @((long)[JCOREUtilities currentTimestamp]), @"data": batch};
    [JCORESupersetManager sendSupersetMoniter:information completionHandler:^(id response, NSError *error) {}];
}
+ (void)sendSupersetMoniter:(id)information completionHandler:(RecoveryOpaqueCallback)completion {
    JCOREReport *report = [[JCOREReport alloc] init];
    report.requestInfo = information;
    report.completionHandler = completion;
    [JCORENetChannel send:report];
}
+ (JCORESupersetMoniter *)commonMoniter:(NSString *)type {
    JCORESupersetMoniter *monitor = [[JCORESupersetMoniter alloc] init];
    if (![@"NET_HTTP" isEqualToString:type]) monitor.type = 1;
    monitor.moniter_type = type;
    monitor.uuid = [self sharedManager].uuid;
    monitor.moniter_id = NSUUID.UUID.UUIDString;
    monitor.timestamp = [JCOREUtilities currentTimestamp];
    monitor.network_type = [JCOREUtilities ctGeneration] ?: @"unknow";
    monitor.operate_type = [JCOREUtilities ctCarrierName] ?: @"unknow";
    JCORESupersetLog(249, @"generate net moniter %@ : %@", monitor.moniter_type, monitor.moniter_id);
    return monitor;
}
@end
