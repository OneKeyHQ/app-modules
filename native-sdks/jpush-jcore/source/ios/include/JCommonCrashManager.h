#pragma once
#import <Foundation/Foundation.h>
#include <stdint.h>
FOUNDATION_EXPORT volatile int32_t exceptionCount;
FOUNDATION_EXPORT NSException *_isException;
FOUNDATION_EXPORT void JCommonInstallExceptionHandler(void);
FOUNDATION_EXPORT void JCommonUnInstallExceptionHandler(NSException *exception);
@class JCommonReportTimerManager;
@interface JCommonCrashManager : NSObject {
    JCommonReportTimerManager *_timerManager;
}
- (id)backtrace;
- (void)setCrashCountSwitchOn:(BOOL)enabled;
- (void)startSentryLogRecord;
+ (id)reportContent;
- (void)startSentryReport;
+ (void)reportCrashCount;
+ (void)reportCrash;
- (void)startCrashCheckStrategy;
- (void)stopCrashCheckStrategy;
- (void)startTimer;
- (void)stopTimer;
@end
