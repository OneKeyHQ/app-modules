#pragma once
#import <Foundation/Foundation.h>
@class JCommonReportTimerManager;
@interface JCommonDeviceController : NSObject {
    JCommonReportTimerManager *_timerManager;
}
- (void)startDeviceCheckStrategy;
- (void)stopDeviceCheckStrategy;
- (void)startTimer;
- (void)stopTimer;
- (NSDictionary *)content;
- (void)reportDeviceInfo;
- (BOOL)isDeviceInfoChange:(id)oldContent newContent:(id)newContent;
@end
