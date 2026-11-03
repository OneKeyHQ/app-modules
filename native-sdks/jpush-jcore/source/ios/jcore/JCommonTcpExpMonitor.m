#import "JCommonTcpExpMonitor.h"
#import "JCommonReportManager.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"
#define JCommonTcpExpFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/JCommonTcpExpMonitor.m"
#define JCommonTcpExpLog(sourceLine, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCommonTcpExpFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
@implementation JCommonTcpExpMonitor
@synthesize monitoringEnabled = _monitoringEnabled;
+ (instancetype)sharedInstance {
    static JCommonTcpExpMonitor *monitor;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ monitor = [[self alloc] init]; });
    return monitor;
}
- (instancetype)init { self = [super init]; if (self) _monitoringEnabled = NO; return self; }
+ (void)startMonitoring { [[JCommonTcpExpMonitor sharedInstance] startMonitoring]; }
+ (void)stopMonitoring { [[JCommonTcpExpMonitor sharedInstance] stopMonitoring]; }
+ (void)recordException:(long long)code message:(NSString *)message { [[JCommonTcpExpMonitor sharedInstance] recordException:code message:message]; }
- (void)startMonitoring { self.monitoringEnabled = YES; JCommonTcpExpLog(60, @"[TcpExpMonitor] TCP exception monitoring started"); }
- (void)stopMonitoring { self.monitoringEnabled = NO; JCommonTcpExpLog(65, @"[TcpExpMonitor] TCP exception monitoring stopped"); }
- (void)recordException:(long long)code message:(NSString *)message {
    if (!self.monitoringEnabled) { JCommonTcpExpLog(70, @"[TcpExpMonitor] Exception ignored - monitoring is disabled"); return; }
    // The original wire report deliberately carries an empty message.
    NSDictionary *information = @{@"type": @"tcp_exception", @"e_code": @((NSInteger)code), @"itime": @((long)[JCOREUtilities currentTimestamp]), @"e_msg": @""};
    JCommonTcpExpLog(82, @"[TcpExpMonitor] Recording exception - code: %ld, message: %@", (long)code, message);
    [JCommonReportManager sendTcpExceptionReport:information completionHandler:^(id response, NSError *error) {
        if (error) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCommonTcpExpFile function:"-[JCommonTcpExpMonitor recordException:message:]_block_invoke" line:89 tag:nil format:@"[TcpExpMonitor] Failed to send exception report: %@", error];
        else [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCommonTcpExpFile function:"-[JCommonTcpExpMonitor recordException:message:]_block_invoke" line:87 tag:nil format:@"[TcpExpMonitor] Exception report sent successfully"];
    }];
}
@end
