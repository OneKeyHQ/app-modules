#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "RecoveredAPNsController.h"
#import "RecoveredTransportDependencies.h"
#import "RecoveredAPNsLiterals.h"
@interface JCOREClientInfo : NSObject
+ (NSString *)deviceToken;
+ (BOOL)deviceTokenUpdated;
@end
@interface JPUSHHelper : NSObject
+ (NSString *)convertTokenDataToString:(NSData *)data;
@end
@interface JPUSHCache : NSObject
+ (id)objectForKey:(id)key;
+ (void)setObject:(id)object forKey:(id)key;
@end
@interface JPUSHAutomatedTester : NSObject
+ (void)PostAutomatedTestStatusNotification:(NSUInteger)status;
@end
@implementation JPUSHDeviceTokenController
+ (instancetype)share { static JPUSHDeviceTokenController *controller;static dispatch_once_t once;dispatch_once(&once,^{controller=[[JPUSHDeviceTokenController alloc] init];});return controller; }
+ (void)startDeviceTokenUpdatedCheckTimer { [[JPUSHDeviceTokenController share] startDeviceTokenUpdatedCheckTimer]; }
+ (BOOL)judgeAndSetDeviceToken:(NSData *)data { return [[JPUSHDeviceTokenController share] judgeAndSetDeviceToken:data]; }
+ (void)uploadDeviceToken { [[JPUSHDeviceTokenController share] uploadDeviceToken]; }
- (void)startDeviceTokenUpdatedCheckTimer {
    _deviceTokenStatusCheckTimes=5;[self stopDeviceTokenUpdatedCheckTimer];
    _deviceTokenStatusCheckTimer=[NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(checkDeviceTokenUpdated:) userInfo:nil repeats:YES];
}
- (void)checkDeviceTokenUpdated:(id)timer {
    if([JCOREClientInfo deviceToken].length==0&&_deviceTokenStatusCheckTimes==5)REC_APNS_LOG(2,"-[JPUSHDeviceTokenController checkDeviceTokenUpdated:]",0x49,@"%@\n",REC_APNS_NOT_READY);
    if(![JCOREClientInfo deviceTokenUpdated]&&_deviceTokenStatusCheckTimes>0&&[JCOREClientInfo deviceToken].length!=0){
        _deviceTokenStatusCheckTimes--;
        REC_APNS_LOG(2,"-[JPUSHDeviceTokenController checkDeviceTokenUpdated:]",0x4d,@"%@\n",REC_APNS_CERTIFICATE);
        if(_deviceTokenStatusCheckTimes==0)REC_APNS_LOG(2,"-[JPUSHDeviceTokenController checkDeviceTokenUpdated:]",0x4f,@"%@\n",REC_APNS_NOT_READY);
        return;
    }
    [self stopDeviceTokenUpdatedCheckTimer];
}
- (void)stopDeviceTokenUpdatedCheckTimer { [_deviceTokenStatusCheckTimer invalidate];_deviceTokenStatusCheckTimer=nil; }
- (BOOL)judgeAndSetDeviceToken:(NSData *)data {
    _currentDeviceToken=[JPUSHHelper convertTokenDataToString:data];[self stopDeviceTokenUpdatedCheckTimer];
    BOOL same=[[JCOREClientInfo deviceToken] isEqualToString:_currentDeviceToken];
    if(same){REC_APNS_LOG(8,"-[JPUSHDeviceTokenController judgeAndSetDeviceToken:]",0x60,REC_APNS_SAME);[JPUSHAutomatedTester PostAutomatedTestStatusNotification:4];}
    return !same;
}
- (void)uploadDeviceToken {
    double now=[[NSDate date] timeIntervalSince1970];
    if(![_currentDeviceToken isEqualToString:[JCOREClientInfo deviceToken]])[self report:now];
    else {
        id last=[JPUSHCache objectForKey:REC_APNS_TIME_KEY];
        if(last&&[last isKindOfClass:NSNumber.class]&&[last doubleValue]>0&&now-[last doubleValue]<=43200.0){
            if(now<[last doubleValue])[JPUSHCache setObject:@(now) forKey:REC_APNS_TIME_KEY];
        } else {
            REC_APNS_LOG(8,"-[JPUSHDeviceTokenController uploadDeviceToken]",0x74,REC_APNS_EXPIRED);[self report:now];
        }
    }
    [JPUSHAutomatedTester PostAutomatedTestStatusNotification:4];
}
- (void)report:(double)timestamp {
    [JCOREService reportToken:_currentDeviceToken invalid:NO];
    [JPUSHCache setObject:@(timestamp) forKey:REC_APNS_TIME_KEY];
}
@end

#endif
