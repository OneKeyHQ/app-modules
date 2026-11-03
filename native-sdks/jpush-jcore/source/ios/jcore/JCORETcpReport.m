#import "JCORETcpReport.h"
#import "JCOREIntegrate.h"
#import "JCOREDataSecurity.h"
#import "JCOREClientInfo.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"
#define JCORETcpReportFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Tcp Event/JCORETcpReport.m"
@implementation JCORETcpReport
@synthesize sid = _sid, bodyData = _bodyData, reportAddressList = _reportAddressList;
- (instancetype)init { self = [super init]; if (self) self.version = 1; return self; }
- (NSData *)headData {
    NSMutableData *data = [[NSMutableData alloc] init];
    [data jcore_appendInt16:0];
    [data jcore_appendInt8:self.version];
    [data jcore_appendInt32:[JCOREIntegrate integrateBusiness]];
    [data jcore_appendInt64:self.uid];
    [data appendData:self.token];
    [data jcore_appendInt32:self.sid];
    [data jcore_appendInt16:self.rid];
    return [data copy];
}
- (NSData *)token {
    NSString *bodyHash = [JCOREDataSecurity sha1:self.bodyData];
    if (!bodyHash) [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:JCORETcpReportFile function:__PRETTY_FUNCTION__ line:40 tag:nil format:@"report data sha1 error"];
    NSString *password = [JCOREClientInfo password];
    if (![password isKindOfClass:NSString.class] || !password.length) [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:JCORETcpReportFile function:__PRETTY_FUNCTION__ line:44 tag:nil format:@"report data pwd error"];
    NSString *md5 = [JCOREUtilities stringByMD5:password];
    if (![JCOREClientInfo uid]) [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:JCORETcpReportFile function:__PRETTY_FUNCTION__ line:48 tag:nil format:@"report data uid error"];
    NSString *input = [NSString stringWithFormat:@"%llu%@%@", (unsigned long long)[JCOREClientInfo uid], md5, bodyHash];
    NSData *token = [JCOREUtilities stringByMD5Data:input];
    [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCORETcpReportFile function:__PRETTY_FUNCTION__ line:52 tag:nil format:@"tcp report token:%@", token];
    return token;
}
@end
