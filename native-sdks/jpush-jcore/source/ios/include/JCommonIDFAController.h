#pragma once
#import <Foundation/Foundation.h>
@class JCommonReportTimerManager;
@interface JCommonIDFAController : NSObject {
    JCommonReportTimerManager *_timerManager;
}
- (void)startCheckStrategy;
- (void)stopCheckStrategy;
- (void)heartbeatCheck;
- (void)startTimer;
- (void)stopTimer;
- (NSDictionary *)content;
- (void)reportIDFAInfo;
- (void)reportIDFAInfoAction;
@end
