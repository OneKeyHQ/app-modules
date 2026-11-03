#import "JCOREHeartbeat.h"
#import "JCOREIntegrate.h"
#import "JCORELog.h"
#import <UIKit/UIKit.h>
#define JCOREHeartbeatFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Tcp Event/JCOREHeartbeat.m"
#define JCOREHeartbeatLog(sourceLine, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCOREHeartbeatFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
@implementation JCOREHeartbeat
@synthesize heartBeatInterval = _heartBeatInterval;
- (instancetype)init { if ((self = [super init])) { self.controlFlag = 41; self.protocolType = 2; self.version = 8; } return self; }
- (NSData *)bodyData {
    NSMutableData *data = [[NSMutableData alloc] init];
    [data jcore_appendInt32:[JCOREIntegrate integrateBusinessForLogin]];
    unsigned char state = [self getAppState];
    [data jcore_appendInt8:state];
    unsigned short interval = self.heartBeatInterval;
    [data jcore_appendInt16:interval];
    NSNumber *version = [JCOREIntegrate sdkVersionOfIndex:0];
    BOOL type = version && version.integerValue >= 50900 ? [JCOREIntegrate isSdkInit:0] : [JCOREIntegrate sdkIsExistOfIndex:0];
    [data jcore_appendInt8:type];
    JCOREHeartbeatLog(64, @"heartbeat appState is [%hhu], interval is [%hu], sdkType is [%hu], version is [%hu]", state, interval, type, self.version);
    return [data copy];
}
- (unsigned char)getAppState {
    __block unsigned char state = 1;
    @try {
        if (NSThread.isMainThread) {
            UIApplicationState value = UIApplication.sharedApplication.applicationState;
            state = value == 1 ? 0 : value == 0 ? 1 : 2;
        } else dispatch_sync(dispatch_get_main_queue(), ^{
            @try {
                UIApplicationState value = UIApplication.sharedApplication.applicationState;
                state = value == 1 ? 0 : value == 0 ? 1 : 2;
            } @catch (NSException *exception) { JCOREHeartbeatLog(95, @"[bomb][%s] is exception", __PRETTY_FUNCTION__); }
        });
    } @catch (NSException *exception) { JCOREHeartbeatLog(81, @"[bomb] [%s] is exception", __PRETTY_FUNCTION__); }
    return state;
}
@end
