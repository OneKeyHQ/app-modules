#pragma once
#import <Foundation/Foundation.h>
@class JCommonReportTimerManager;
@interface JCommonCrashController : NSObject {
    JCommonReportTimerManager *_timerManager;
    NSTimeInterval _lastCrashCheckTime;
    BOOL _isReporting;
}
+ (id)content;
+ (void)reportCrash;
- (void)startCrashCheckStrategy;
- (void)stopCrashCheckStrategy;
- (void)startTimer;
- (void)stopTimer;
@end
