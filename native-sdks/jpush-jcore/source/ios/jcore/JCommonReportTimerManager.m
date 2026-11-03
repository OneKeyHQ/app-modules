#import "JCommonReportTimerManager.h"
#import "JCOREFCCache.h"
#import "JCORELog.h"
#import "JCOREUtilities.h"
#include <math.h>
@implementation JCommonReportTimerManager
+ (instancetype)manager {
    return [[self alloc] init];
}
- (instancetype)init {
    return [super init];
}
- (void)startTimerCheck {
    [self stopTimerCheck];
    if (!self.timerFuncBlock || !self.saveKey) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x40
              context:0
                 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/"
                      "JCommonReportTimerManager.m"
             function:__PRETTY_FUNCTION__
                 line:33
                  tag:nil
               format:@"定时器未启动"];
        return;
    }
    NSTimeInterval now = [JCOREUtilities currentTimestamp];
    if (fabs(self.lastSaveTimeStamp) < 1) {
        if (![JCOREFCCache objectForKey:self.saveKey])
            self.lastSaveTimeStamp = now;
        else
            self.lastSaveTimeStamp = (double)[[JCOREFCCache objectForKey:self.saveKey] longLongValue];
    }
    NSTimeInterval elapsed = 0;
    if (fabs(self.lastSaveTimeStamp) >= 1) {
        elapsed = now - self.lastSaveTimeStamp;
        if (elapsed > 0 && !(elapsed + 1 > self.frequency))
            goto ScheduleTimer;
        self.timerFuncBlock();
    } else if (self.firstLoadReport)
        self.timerFuncBlock();
    self.lastSaveTimeStamp = now;
    elapsed = 0;
ScheduleTimer:
    if (self.saveKey)
        [JCOREFCCache setObject:@(self.lastSaveTimeStamp) forKey:self.saveKey];
    _checkTimer = [NSTimer scheduledTimerWithTimeInterval:self.frequency - elapsed
                                                   target:self
                                                 selector:@selector(p_checkTimer)
                                                 userInfo:nil
                                                  repeats:NO];
}
- (void)stopTimerCheck {
    if (_checkTimer.isValid) {
        [_checkTimer invalidate];
        _checkTimer = nil;
    }
}
- (void)p_checkTimer {
    NSTimeInterval now = [JCOREUtilities currentTimestamp], previous = self.lastSaveTimeStamp;
    NSTimeInterval delta = now - previous;
    NSTimeInterval frequency = self.frequency;
    if (!(delta > 0 && !(delta + 1 > frequency)))
        [self startTimerCheck];
}
@end
