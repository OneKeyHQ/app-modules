#import "JCommonAppListController.h"
#import "JCORECacheChannel.h"
#import "JCORELog.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "JCommonReportManager.h"
#import "JCommonReportTimerManager.h"
#import "JCommonService.h"
#import "JCoreParameter.h"
#import <UIKit/UIKit.h>
#define AppListFile                                                                                          \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/"                        \
    "JCommonAppListController.m"
@implementation JCommonAppListController
- (void)startCheckStrategy {
    [self startTimer];
}
- (void)stopCheckStrategy {
    [self stopTimer];
}
- (void)dealloc {
    _timerManager = nil;
}
- (instancetype)init {
    return [super init];
}
- (void)startTimer {
    if (!_timerManager) {
        _timerManager = [JCommonReportTimerManager manager];
        __weak typeof(self) weakSelf = self;
        _timerManager.timerFuncBlock = ^{
          [weakSelf reportApps];
        };
        id previous = [JCORECacheChannel deviceInfoCheckTime];
        if ([previous isKindOfClass:NSNumber.class]) {
            [JCORECacheChannel setDeviceInfoCheckTime:nil];
            _timerManager.lastSaveTimeStamp = (double)[previous longLongValue];
        }
        _timerManager.frequency = 86400;
        _timerManager.saveKey = @"__JCOMMONAppListCheckTime";
        _timerManager.firstLoadReport = YES;
    }
    [_timerManager startTimerCheck];
}
- (void)stopTimer {
    [_timerManager stopTimerCheck];
}
- (NSDictionary *)content {
    id apps = [self appListInfo];
    if (!apps || ![apps count])
        return nil;
    double time = [JCOREUtilities currentTimestamp];
    return @{@"it" : @((long)time), @"type" : @"app_list_ios", @"pv" : @1, @"da" : apps};
}
- (void)reportApps {
    [JCommonService trackAppListWithCode:1101];
    if ([JCoreParameter shared].applistEnable) {
        dispatch_async(dispatch_get_main_queue(), ^{
          [self report:[self content]];
        });
        return;
    }
    [JCommonService trackAppListWithCode:1102];
}
- (void)report:(id)content {
    if (![JCOREThread isMainThread]) {
        [JCOREThread performSel:@selector(report:) target:self withObject:content];
        return;
    }
    if (!content || ![content isKindOfClass:NSDictionary.class]) {
        [JCommonService trackAppListWithCode:1103];
        return;
    }
    [JCommonService trackAppListWithCode:1104];
    [JCommonReportManager sendAppListReport:content
                          completionHandler:^(id response, NSError *error) {
                            if (!error) {
                                [JCORELog log:YES
                                        level:0x3ff
                                         flag:0x100
                                      context:0
                                         file:AppListFile
                                     function:"-[JCommonAppListController report:]_block_invoke"
                                         line:122
                                          tag:nil
                                       format:@"sendAppListReport success"];
                                [JCommonService trackAppListWithCode:1100];
                            } else {
                                [JCORELog log:YES
                                        level:0x3ff
                                         flag:0x100
                                      context:0
                                         file:AppListFile
                                     function:"-[JCommonAppListController report:]_block_invoke"
                                         line:126
                                          tag:nil
                                       format:@"sendAppListReport failed: %@", error];
                                [JCommonService trackAppListWithCode:1105];
                            }
                          }];
}
- (NSArray *)appListInfo {
    id schemes = NSBundle.mainBundle.infoDictionary[@"LSApplicationQueriesSchemes"];
    if (!schemes || ![schemes isKindOfClass:NSArray.class] || ![schemes count])
        return nil;
    NSMutableArray *result = [NSMutableArray array];
    for (id scheme in schemes) {
        if (scheme && [scheme isKindOfClass:NSString.class] && [scheme length]) {
            id address = scheme;
            if (![scheme hasSuffix:@"://"])
                address = [NSString stringWithFormat:@"%@://", scheme];
            BOOL available = [UIApplication.sharedApplication canOpenURL:[NSURL URLWithString:address]];
            [result addObject:@{@"sc" : scheme, @"st" : @((NSInteger)available)}];
        }
    }
    return result;
}
@end
