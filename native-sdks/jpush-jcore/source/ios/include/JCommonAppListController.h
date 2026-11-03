#pragma once
#import <Foundation/Foundation.h>
@class JCommonReportTimerManager;
@interface JCommonAppListController : NSObject {
    JCommonReportTimerManager *_timerManager;
}
- (instancetype)init;
- (void)startCheckStrategy;
- (void)stopCheckStrategy;
- (void)startTimer;
- (void)stopTimer;
- (NSDictionary *)content;
- (NSArray *)appListInfo;
- (void)reportApps;
- (void)report:(id)content;
@end
