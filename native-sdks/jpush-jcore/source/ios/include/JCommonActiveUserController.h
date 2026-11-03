#pragma once
#import <Foundation/Foundation.h>
@interface JCommonActiveUserController : NSObject {
    BOOL _isColdLaunch;
    double _appLaunchTime;
    double _appTerminateTime;
}
@property(nonatomic) double appLaunchTime;
@property(nonatomic) double appTerminateTime;
@property(nonatomic) BOOL isColdLaunch;
- (instancetype)init;
- (NSString *)sessionID:(long long)timestamp;
- (NSDictionary *)appLauchReportInfo;
- (NSDictionary *)appTerminateReportInfo:(double)launchTime closetTime:(double)closeTime;
- (void)appWillTerminate:(id)notification;
- (void)reportAppLaunch;
- (void)reportAppActivityDuration:(double)launchTime closeTime:(double)closeTime;
@end
