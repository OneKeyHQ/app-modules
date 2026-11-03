#import "JCommonActiveUserController.h"
#import "JCOREClientInfo.h"
#import "JCORELog.h"
#import <math.h>
#import "JCOREUtilities.h"
#import "JCommonReportManager.h"
#define ActiveFile                                                                                           \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/"                        \
    "JCommonActiveUserController.m"
@implementation JCommonActiveUserController
- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (instancetype)init {
    return [super init];
}
- (NSString *)sessionID:(long long)timestamp {
    double time = [JCOREUtilities currentTimestamp];
    long selected = (long)time;
    if (timestamp > 0)
        selected = timestamp;
    NSString *value = [NSString
        stringWithFormat:@"%@%@%@", [JCOREClientInfo appKey], [JCOREUtilities fakeIMEI], @(selected)];
    return [JCOREUtilities stringByMD5:value];
}
- (NSDictionary *)appLauchReportInfo {
    double time = [JCOREUtilities currentTimestamp];
    if (self.appLaunchTime <= 0)
        self.appLaunchTime = (double)(long)time;
    NSString *date = nil, *clock = nil;
    [JCOREUtilities converTimestamp:(long)self.appLaunchTime toDate:&date andTime:&clock];
    return @{
        @"type" : @"active_launch",
        @"itime" : @((long)time),
        @"session_id" : [self sessionID:(long)self.appLaunchTime],
        @"date" : date ?: @"",
        @"time" : clock ?: @"",
        @"timezone" : [JCOREUtilities currentTimeZone]
    };
}
- (NSDictionary *)appTerminateReportInfo:(double)launchTime closetTime:(double)closeTime {
    if (launchTime <= 0)
        return nil;
    double time = [JCOREUtilities currentTimestamp];
    if (closeTime <= 0)
        closeTime = (double)(long)time;
    NSString *date = nil, *clock = nil;
    [JCOREUtilities converTimestamp:(long)closeTime toDate:&date andTime:&clock];
    id session = [self sessionID:(long)launchTime];
    return @{
        @"type" : @"active_terminate",
        @"itime" : @((long)time),
        @"duration" : @(round((closeTime - launchTime) * 10.0) / 10.0),
        @"session_id" : session,
        @"date" : date ?: @"",
        @"time" : clock ?: @"",
        @"timezone" : [JCOREUtilities currentTimeZone]
    };
}
- (void)appWillTerminate:(id)notification {
    self.appTerminateTime = NSDate.date.timeIntervalSince1970;
}
- (void)reportAppLaunch {
    [JCommonReportManager sendAppLaunchReport:[self appLauchReportInfo]
                            completionHandler:^(id response, NSError *error) {
                              if (!error)
                                  [JCORELog log:YES
                                          level:0x3ff
                                           flag:8
                                        context:0
                                           file:ActiveFile
                                       function:"-[JCommonActiveUserController reportAppLaunch]_block_invoke"
                                           line:99
                                            tag:nil
                                         format:@"launchReport 成功"];
                            }];
}
- (void)reportAppActivityDuration:(double)launchTime closeTime:(double)closeTime {
    [JCommonReportManager
        sendAppTerminalReport:[self appTerminateReportInfo:launchTime closetTime:closeTime]
            completionHandler:^(id response, NSError *error) {
              if (!error)
                  [JCORELog log:YES
                          level:0x3ff
                           flag:0x100
                        context:0
                           file:ActiveFile
                       function:
                           "-[JCommonActiveUserController reportAppActivityDuration:closeTime:]_block_invoke"
                           line:112
                            tag:nil
                         format:@"reportAppActivityDuration success"];
            }];
}
@end
