#import "JCORETcpReport.h"
#import "JCORETcpAddressBlackList.h"
#import "JCORELog.h"
#import "JCORETcpReportTransport.h"
@interface NSObject (JCORETcpReportAddressDeclaration)
- (NSArray *)reportAddressList;
@end
#define JCORETcpReportControllerFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Controller/Network/JCORETcpReportController.m"
@implementation JCORETcpReportController
- (void)send:(id)report {
    NSString *failure;
    NSUInteger line;
    if (![report conformsToProtocol:@protocol(JCORETcpProtocol)]) { failure = @"send tcp object error, be canceled"; line = 629; }
    else if (![report respondsToSelector:@selector(reportAddressList)]) { failure = @"send tcp object no reportAddress error, be canceled"; line = 633; }
    else {
        NSArray *addresses = [report reportAddressList];
        if (![addresses isKindOfClass:NSArray.class] || !addresses.count) {
            [JCORETcpReportConnector errorCallBack:report code:2003 des:@"tcp report address error"];
            return;
        }
        NSMutableArray *filtered = [NSMutableArray arrayWithArray:addresses];
        for (id address in addresses) if ([JCORETcpAddressBlackList addressInBlackList:address]) [filtered removeObject:address];
        if (![filtered isKindOfClass:NSArray.class] || !filtered.count) {
            [JCORETcpReportConnector errorCallBack:report code:2003 des:@"all tcp report address are in blackList, return"];
            return;
        }
        [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCORETcpReportControllerFile function:__PRETTY_FUNCTION__ line:653 tag:nil format:@"tcp report address list:%@", filtered];
        JCORETcpReportManager *manager = [[JCORETcpReportManager alloc] init];
        manager.report = report;
        manager.reportAddressList = [filtered mutableCopy];
        [manager startReport];
        return;
    }
    [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:JCORETcpReportControllerFile function:__PRETTY_FUNCTION__ line:line tag:nil format:failure];
}
@end
