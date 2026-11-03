#import "JCommonIDFAController.h"
#import "JCORELog.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "JCommonReportManager.h"
#import "JCommonReportTimerManager.h"
#import "JCommonUtilities.h"
#import "RecoveredCache.h"
#define IDFAFile                                                                                             \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/JCommonIDFAController.m"
@implementation JCommonIDFAController
- (void)startCheckStrategy {
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:IDFAFile
         function:__PRETTY_FUNCTION__
             line:45
              tag:nil
           format:@"start IDFA check strategy"];
    [self startTimer];
}
- (void)stopCheckStrategy {
    [JCORELog log:YES
            level:0x3ff
             flag:0x100
          context:0
             file:IDFAFile
         function:__PRETTY_FUNCTION__
             line:51
              tag:nil
           format:@"stop IDFA check strategy"];
    [self stopTimer];
}
- (void)heartbeatCheck {
    BOOL cached = [JCommonUtilities hasIdfaCache];
    BOOL expired = [JCommonUtilities idfaCacheExpired];
    if (cached && !expired)
        return;
    [JCommonUtilities objectForKey:JCOMMON_ADVERTISING_ID];
    id sent = [JCORECacheUtilities cacheObjectForKey:@"kJCommonIdfaLastValidReportKey"];
    if (sent && [sent isKindOfClass:NSNumber.class] && [sent boolValue])
        return;
    [self reportIDFAInfo];
}
- (void)dealloc {
    _timerManager = nil;
}
- (void)startTimer {
    [self stopTimer];
    if (!_timerManager) {
        _timerManager = [JCommonReportTimerManager manager];
        __weak typeof(self) weakSelf = self;
        _timerManager.timerFuncBlock = ^{
          [weakSelf reportIDFAInfo];
        };
        _timerManager.frequency = 86400;
        _timerManager.saveKey = @"__JCOMMONIDFACheckTime";
        _timerManager.firstLoadReport = YES;
    }
    [_timerManager startTimerCheck];
}
- (void)stopTimer {
    [_timerManager stopTimerCheck];
}
- (NSDictionary *)content {
    double time = [JCOREUtilities currentTimestamp];
    id identifier = [JCommonUtilities objectForKey:JCOMMON_ADVERTISING_ID];
    NSUInteger status = [JCommonUtilities attAuthStatus];
    return @{
        @"itime" : @((long)time),
        @"type" : @"idfa_ios",
        @"idfa" : identifier,
        @"status" : @(status),
        @"model" : [JCOREUtilities model] ?: @""
    };
}
- (void)reportIDFAInfo {
    if ([JCOREUtilities isFirstLoad]) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 10000000000), dispatch_get_main_queue(), ^{
          [self reportIDFAInfoAction];
        });
        return;
    }
    [self reportIDFAInfoAction];
}
- (void)reportIDFAInfoAction {
    if (![JCOREThread isMainThread]) {
        [JCOREThread performSel:@selector(reportIDFAInfoAction) target:self withObject:nil];
        return;
    }
    id content = [self content];
    [JCommonReportManager sendAppIDFAInfoReport:content
                              completionHandler:^(id response, NSError *error) {
                                if (error) {
                                    [JCORELog log:YES
                                            level:0x3ff
                                             flag:0x100
                                          context:0
                                             file:IDFAFile
                                         function:"-[JCommonIDFAController reportIDFAInfoAction]_block_invoke"
                                             line:151
                                              tag:nil
                                           format:@"sendAppIDFAInfoReport fail"];
                                    return;
                                }
                                [JCORELog log:YES
                                        level:0x3ff
                                         flag:0x100
                                      context:0
                                         file:IDFAFile
                                     function:"-[JCommonIDFAController reportIDFAInfoAction]_block_invoke"
                                         line:143
                                          tag:nil
                                       format:@"sendAppIDFAInfoReport success"];
                                id identifier = [content objectForKey:@"idfa"];
                                if ([identifier isKindOfClass:NSString.class] && [identifier length] &&
                                    ![JCommonUtilities checkFormatOfAllZeroIDFA:identifier])
                                    [JCORECacheUtilities setCacheObject:@YES
                                                                 forKey:@"kJCommonIdfaLastValidReportKey"];
                              }];
}
@end
