#pragma once
#import <Foundation/Foundation.h>
@interface JPUSHDeviceTokenController : NSObject {
    __weak NSTimer *_deviceTokenStatusCheckTimer;
    NSInteger _deviceTokenStatusCheckTimes;
    NSString *_currentDeviceToken;
}
+ (instancetype)share;
+ (void)startDeviceTokenUpdatedCheckTimer;
+ (BOOL)judgeAndSetDeviceToken:(NSData *)data;
+ (void)uploadDeviceToken;
- (void)startDeviceTokenUpdatedCheckTimer;
- (void)checkDeviceTokenUpdated:(id)timer;
- (void)stopDeviceTokenUpdatedCheckTimer;
- (BOOL)judgeAndSetDeviceToken:(NSData *)data;
- (void)uploadDeviceToken;
- (void)report:(double)timestamp;
@end
