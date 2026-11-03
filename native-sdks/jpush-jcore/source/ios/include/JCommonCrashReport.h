#pragma once
#import <Foundation/Foundation.h>
@class JCommonCrashController, JCommonCrashManager;
@interface JCommonCrashReport : NSObject {
    JCommonCrashController *_crashController;
    JCommonCrashManager *_crashManager;
}
+ (instancetype)sharedService;
+ (void)setupService;
+ (void)startSentryLogReport;
+ (void)startSentryCrashCountReport;
- (void)setup;
- (void)startCrashReport;
- (void)startSentryLogReport;
- (void)startSentryCrashCountReport;
@end
