#import "JCORETcpReportTransport.h"
#import "JCORELog.h"
static NSDictionary *jcoreTcpReportConnectorDict;
#define JCORETcpManagerFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Controller/Network/JCORETcpReportController.m"
#define JCORETcpManagerLog(sourceLine, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCORETcpManagerFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
@implementation JCORETcpReportManager
@synthesize report = _report, reportAddressList = _reportAddressList;
- (void)startReport {
    RecoveryOpaqueCallback completion = [self.report.completionHandler copy];
    _reportAddressIndex = 0;
    __weak JCORETcpReportManager *weakSelf = self;
    self.report.completionHandler = ^(id response, NSError *error) {
        if (error) { JCORETcpManagerLog(540, @"tcp report error:%ld, msg:%@, try next report", (long)error.code, error.localizedDescription); if ([weakSelf tryReportWithNextAddress]) return; }
        [weakSelf performSelector:@selector(stopReportTimer) withObject:nil afterDelay:0];
        if (completion) completion(response, error);
    };
    [self tryReportWithNextAddress];
}
- (BOOL)tryReportWithNextAddress {
    NSUInteger index = (NSUInteger)_reportAddressIndex;
    NSUInteger count = self.reportAddressList.count;
    if (index < count) {
        NSArray *addresses = self.reportAddressList;
        NSUInteger currentIndex = (NSUInteger)_reportAddressIndex;
        _reportAddressIndex = (long long)((unsigned long long)_reportAddressIndex + 1);
        id address = [addresses objectAtIndex:currentIndex];
        JCORETcpReportConnector *connector = [self connectorWithAddress:address];
        [connector send:self.report];
        JCORETcpManagerLog(560, @"tcp try report address:%ld, %@", (long)((unsigned long long)_reportAddressIndex - 1), address);
        [self stopReportTimer];
        _reportTimer = [NSTimer scheduledTimerWithTimeInterval:30 target:self selector:@selector(reportTimeout:) userInfo:nil repeats:NO];
    }
    return index < count;
}
- (id)connectorWithAddress:(id)address {
    if (!address) return nil;
    JCORETcpReportConnector *connector = jcoreTcpReportConnectorDict ? [jcoreTcpReportConnectorDict objectForKey:address] : nil;
    if (!connector) {
        NSMutableDictionary *connectors = [[NSMutableDictionary alloc] initWithDictionary:jcoreTcpReportConnectorDict];
        connector = [[JCORETcpReportConnector alloc] initWithAddress:address];
        connector.completionHandler = ^{ [self removeConnector:address]; };
        [connectors setObject:connector forKey:address];
        jcoreTcpReportConnectorDict = [connectors copy];
    }
    return connector;
}
- (void)removeConnector:(id)address {
    if (!address) return;
    JCORETcpReportConnector *connector = [jcoreTcpReportConnectorDict objectForKey:address];
    if (connector) {
        NSMutableDictionary *connectors = [[NSMutableDictionary alloc] initWithDictionary:jcoreTcpReportConnectorDict];
        [connectors removeObjectForKey:address];
        jcoreTcpReportConnectorDict = [connectors copy];
        [connector.network disconect];
        [connector.network remove];
        [connector invalidate];
    }
}
- (void)reportTimeout:(id)timer {
    [self performSelector:@selector(stopReportTimer) withObject:nil afterDelay:0];
    if (self.report) [JCORETcpReportConnector errorCallBack:self.report code:2002 des:@"connect timeout"];
}
- (void)stopReportTimer { if (_reportTimer && _reportTimer.isValid) { [_reportTimer invalidate]; _reportTimer = nil; } }
@end
