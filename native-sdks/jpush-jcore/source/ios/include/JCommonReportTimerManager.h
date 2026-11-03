#pragma once
#import <Foundation/Foundation.h>
@interface JCommonReportTimerManager : NSObject {
    NSTimer *_checkTimer;
    BOOL _firstLoadReport;
    void (^_timerFuncBlock)(void);
    NSTimeInterval _frequency;
    NSTimeInterval _lastSaveTimeStamp;
    NSString *_saveKey;
}
@property(copy, nonatomic) void (^timerFuncBlock)(void);
@property(nonatomic) NSTimeInterval frequency;
@property(nonatomic) NSTimeInterval lastSaveTimeStamp;
@property(copy, nonatomic) NSString *saveKey;
@property(nonatomic) BOOL firstLoadReport;
+ (instancetype)manager;
- (void)startTimerCheck;
- (void)stopTimerCheck;
- (void)p_checkTimer;
@end
