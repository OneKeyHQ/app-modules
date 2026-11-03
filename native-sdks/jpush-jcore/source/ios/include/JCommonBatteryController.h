#pragma once
#import <Foundation/Foundation.h>
@class JCommonReportTimerManager;
@interface JCommonBatteryController : NSObject {
    JCommonReportTimerManager *_timerManager;
}
- (void)startBatteryCheckStrategy;
- (void)stopBatteryCheckStrategy;
- (void)startTimer;
- (void)stopTimer;
- (NSDictionary *)batteryInfo;
- (void)reportBatteryInfo;
- (void)report:(id)content;
@end
