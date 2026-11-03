#pragma once
#import <Foundation/Foundation.h>
@interface JCommonTcpExpMonitor : NSObject {
    BOOL _monitoringEnabled;
}
@property (nonatomic) BOOL monitoringEnabled;
+ (instancetype)sharedInstance;
+ (void)startMonitoring;
+ (void)stopMonitoring;
+ (void)recordException:(long long)code message:(NSString *)message;
- (void)startMonitoring;
- (void)stopMonitoring;
- (void)recordException:(long long)code message:(NSString *)message;
@end
