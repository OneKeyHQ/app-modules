#import "JCommonBatteryController.h"
#import "JCORECacheChannel.h"
#import "JCORELog.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "JCommonReportManager.h"
#import "JCommonReportTimerManager.h"
#import <UIKit/UIKit.h>
#define BatteryFile                                                                                          \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/"                        \
    "JCommonBatteryController.m"
@implementation JCommonBatteryController
- (void)startBatteryCheckStrategy {
    [self startTimer];
}
- (void)stopBatteryCheckStrategy {
    [self stopTimer];
}
- (void)dealloc {
    _timerManager = nil;
}
- (void)startTimer {
    if (!_timerManager) {
        _timerManager = [JCommonReportTimerManager manager];
        __weak typeof(self) weakSelf = self;
        _timerManager.timerFuncBlock = ^{
          [weakSelf reportBatteryInfo];
        };
        id previous = [JCORECacheChannel batteryInfoCheckTime];
        if ([previous isKindOfClass:NSNumber.class]) {
            [JCORECacheChannel setBatteryInfoCheckTime:nil];
            _timerManager.lastSaveTimeStamp = (double)[previous longLongValue];
        }
        _timerManager.frequency = 3600;
        _timerManager.saveKey = @"__JCOMMONBatteryTime";
        _timerManager.firstLoadReport = YES;
    }
    [_timerManager startTimerCheck];
}
- (void)stopTimer {
    [_timerManager stopTimerCheck];
}
- (NSDictionary *)batteryInfo {
    if (!NSThread.isMainThread)
        return nil;
    UIDevice.currentDevice.batteryMonitoringEnabled = YES;
    NSInteger state = UIDevice.currentDevice.batteryState;
    float level = UIDevice.currentDevice.batteryLevel;
    double time = [JCOREUtilities currentTimestamp];
    return @{
        @"itime" : @((long)time),
        @"type" : @"battery",
        @"level" : @((short)(int)(level * 100.0)),
        @"status" : @(state)
    };
}
- (void)reportBatteryInfo {
    dispatch_async(dispatch_get_main_queue(), ^{
      id content = [self batteryInfo];
      if (content)
          [self report:content];
    });
}
- (void)report:(id)content {
    if (![JCOREThread isMainThread]) {
        [JCOREThread performSel:@selector(report:) target:self withObject:content];
        return;
    }
    [JCommonReportManager sendBatteryInfoReport:content
                              completionHandler:^(id response, NSError *error) {
                                if (!error)
                                    [JCORELog log:YES
                                            level:0x3ff
                                             flag:0x100
                                          context:0
                                             file:BatteryFile
                                         function:"-[JCommonBatteryController report:]_block_invoke"
                                             line:103
                                              tag:nil
                                           format:@"sendBatteryInfoReport success"];
                              }];
}
@end
