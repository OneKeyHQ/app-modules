#import "JCommonServiceController.h"
#import "JCORELocationController.h"
#import "JCOREAPIRecordController.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "JCORECacheChannel.h"
#import "JCOREFCCache.h"
#import "JCORELog.h"
#import <UIKit/UIKit.h>
extern void JCommonInstallUncaughtExceptionHandler(void);
extern void JCommonInstallExceptionHandler(void);
extern NSString *const kJCORENetworkCmdCtrlNotification;
extern NSString *const kJCORENetworkDidLoginNotification;
extern NSString *const kJCORENetworkHeartbeatResponseNotification;
@interface JCommonDeviceController : NSObject
- (void)startDeviceCheckStrategy;
- (void)stopDeviceCheckStrategy;
@end
@interface JCommonBatteryController : NSObject
- (void)startBatteryCheckStrategy;
- (void)stopBatteryCheckStrategy;
@end
@interface JCommonActiveUserController : NSObject
- (void)setAppLaunchTime:(double)timestamp;
- (void)setIsColdLaunch:(BOOL)cold;
- (void)reportAppLaunch;
- (void)reportAppActivityDuration:(double)start closeTime:(double)close;
@end
@interface JCommonPasteController : NSObject
- (void)startPasteStrategy;
- (void)stopPasteStrategy;
@end
#import "JCommonIpAddressController.h"
@interface JCommonAppListController : NSObject
- (void)startCheckStrategy;
- (void)stopCheckStrategy;
@end
@interface JCommonIDFAController : NSObject
- (void)startCheckStrategy;
- (void)stopCheckStrategy;
- (void)heartbeatCheck;
@end
@interface JCommonScreenStatusController : NSObject @end
@interface JCommonDataConfigManager : NSObject
+ (instancetype)sharedManager;
- (void)loadConfig:(void (^)(void))completion;
@end
@interface JCommonApnsStateController : NSObject
+ (void)reportAPNSState:(NSUInteger)state;
@end
#import "JCommonSdkController.h"
#import "JCommonSupersetManager.h"
@implementation JCommonServiceController
- (instancetype)initWithTimetamp:(double)timestamp { self = [super init]; if (self) { _becomeActiveTime = timestamp; [self setup]; } return self; }
+ (void)setCrashLogON { JCommonInstallUncaughtExceptionHandler(); }
+ (void)setSentryCrashON { JCommonInstallExceptionHandler(); }
+ (void)setSupersetMoniterOn { [JCommonSupersetManager setSupersetMoniterOn]; }
+ (NSString *)startHttpMoniter:(NSString *)method url:(NSString *)url request_byte:(long long)bytes { return [JCommonSupersetManager startHttpMoniter:method url:url request_byte:bytes]; }
+ (NSString *)startConnectionMoniter:(NSString *)type host:(NSString *)host port:(long long)port { return [JCommonSupersetManager startConnectionMoniter:type host:host port:port]; }
+ (NSString *)startTcpEventMoniter:(NSString *)type { return [JCommonSupersetManager startTcpEventMoniter:type]; }
+ (void)endHttpMoniter:(NSString *)identifier response_byte:(long long)bytes error_code:(long long)error status_code:(long long)status { [JCommonSupersetManager endHttpMoniter:identifier response_byte:bytes error_code:error status_code:status]; }
+ (void)endConnectionMoniter:(NSString *)identifier error_code:(long long)error status_code:(long long)status { [JCommonSupersetManager endConnectionMoniter:identifier error_code:error status_code:status]; }
- (id)currentLocationInfo { if (!_locationController) _locationController = [JCORELocationController sharedInstane]; return [_locationController currentLocationInfo]; }
- (void)setLocationInfo:(NSDictionary *)information {
    if (_locationController) [JCOREThread performSel:@selector(setLocation:) target:_locationController withObject:information];
    else [JCORELog log:YES level:0x3ff flag:0x40 context:0 file:"/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/Controller/JCommonServiceController.m" function:__PRETTY_FUNCTION__ line:140 tag:nil format:@"_locationController is nil"];
}
+ (void)apiRecord:(id)identifier methodName:(id)method { [JCOREAPIRecordController apiRecord:identifier methodName:method]; }
- (void)dealloc { [NSNotificationCenter.defaultCenter removeObserver:self]; }
- (void)setup {
    [self addObserver];
    [self resumeReportData];
    [self resumeSession];
    _isSetup = YES;
    [self prepareActiveUserReport:@([JCOREUtilities currentTimestamp]) isColdLaunch:YES];
    [self reportSdkInfo];
    [[JCommonDataConfigManager sharedManager] loadConfig:^{
        if ([JCommonDataControl sharedManager].idfaSwitch) [self->_idfaController startCheckStrategy];
        else [self->_idfaController stopCheckStrategy];
    }];
}
- (void)prepareActiveUserReport:(NSNumber *)timestamp isColdLaunch:(BOOL)cold {
    double value = timestamp.doubleValue;
    if (!(_closeTimetamp > 1)) {
        static BOOL reportedLaunch;
        if (reportedLaunch) return;
        [self reportLaunch:(double)timestamp.longLongValue withColdLaunch:cold];
        reportedLaunch = YES;
        return;
    }
    double now = (double)(long)value;
    double difference = now - _closeTimetamp;
    if (now - _startTimetamp > (double)1e20f || difference > 30 || (difference < 0 && _closeTimetamp - now >= 1)) {
        [self closeSession];
        [self setupSession:(double)timestamp.longLongValue];
        [self reportLaunch:(double)timestamp.longLongValue withColdLaunch:cold];
    }
    _closeTimetamp = 0;
}
- (void)hotStartPrepareActiveUserReport { [self prepareActiveUserReport:@([JCOREUtilities currentTimestamp]) isColdLaunch:NO]; }
- (void)setupSession:(double)timestamp { if (!_isSetup) { _isSetup = YES; _startTimetamp = timestamp; } }
- (void)closeSession { if (_isSetup) { _isSetup = NO; [self reportActive]; } }
- (void)reportLaunch:(double)timestamp withColdLaunch:(BOOL)cold { [_activeUserController setAppLaunchTime:timestamp]; [_activeUserController setIsColdLaunch:cold]; [_activeUserController reportAppLaunch]; }
- (void)reportActive { [_activeUserController reportAppActivityDuration:_startTimetamp closeTime:_closeTimetamp]; }
- (void)addObserver {
    NSNotificationCenter *center = NSNotificationCenter.defaultCenter;
    [center addObserver:self selector:@selector(applicationWillTerminate) name:UIApplicationWillTerminateNotification object:nil];
    [center addObserver:self selector:@selector(applicationWillResignActive) name:UIApplicationWillResignActiveNotification object:nil];
    [center addObserver:self selector:@selector(applicationDidEnterBackground) name:UIApplicationDidEnterBackgroundNotification object:nil];
    [center addObserver:self selector:@selector(applicationWillEnterForeground) name:UIApplicationWillEnterForegroundNotification object:nil];
    [center addObserver:self selector:@selector(handleCmdControl:) name:kJCORENetworkCmdCtrlNotification object:nil];
    [center addObserver:self selector:@selector(didLogin:) name:kJCORENetworkDidLoginNotification object:nil];
    [center addObserver:self selector:@selector(receiveHeartAck:) name:kJCORENetworkHeartbeatResponseNotification object:nil];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC), dispatch_get_main_queue(), ^{
        [center addObserver:self selector:@selector(applicationDidBecomeActive) name:UIApplicationDidBecomeActiveNotification object:nil];
    });
}
- (void)receiveHeartAck:(NSNotification *)notification {
    if ([JCOREThread isMainThread]) { [self reportAPNSState:@((NSUInteger)0)]; [_idfaController heartbeatCheck]; }
    else [JCOREThread performSel:@selector(reportAPNSState:) target:self withObject:@((NSUInteger)0)];
}
- (void)didLogin:(NSNotification *)notification {
    if ([JCOREThread isMainThread]) [self reportAPNSState:@((NSUInteger)2)];
    else [JCOREThread performSel:@selector(reportAPNSState:) target:self withObject:@((NSUInteger)2)];
}
- (void)handleCmdControl:(NSNotification *)notification {
    id information = notification.userInfo;
    id command = [information isKindOfClass:NSDictionary.class] ? [information objectForKey:@"cmd_content"] : nil;
    [_locationController handleContrlCommand:command];
}
- (void)applicationWillTerminate { [JCOREThread performSel:@selector(saveActiveTime) target:self withObject:nil]; }
- (void)applicationDidBecomeActive { [JCOREThread performSel:@selector(hotStartPrepareActiveUserReport) target:self withObject:nil]; }
- (void)applicationDidEnterBackground { [JCOREThread performSel:@selector(stopReportTimer) target:self withObject:nil]; }
- (void)applicationWillEnterForeground {
    [JCOREThread performSel:@selector(resumeReportData) target:self withObject:nil];
    [JCOREThread performSel:@selector(reportAPNSState:) target:self withObject:@((NSUInteger)1)];
}
- (void)applicationWillResignActive { [JCOREThread performSel:@selector(saveActiveTime) target:self withObject:nil]; }
- (void)stopReportTimer {
    [_deviceController stopDeviceCheckStrategy];
    [_bateryyController stopBatteryCheckStrategy];
    [_locationController stopLocationStrategy];
    [_pasteController stopPasteStrategy];
    [_appListController stopCheckStrategy];
    [_idfaController startCheckStrategy];
}
- (void)resumeSession {
    id start = [JCORECacheChannel sessionStartTime];
    if ([start isKindOfClass:NSNumber.class]) { _startTimetamp = [start doubleValue]; [JCORECacheChannel setSessionStartTime:nil]; }
    else _startTimetamp = [JCOREFCCache objectForKey:@"__JCOMMONStart_time"] ? [[JCOREFCCache objectForKey:@"__JCOMMONStart_time"] doubleValue] : 0;
    if (_startTimetamp <= 0) _startTimetamp = _becomeActiveTime;
    id close = [JCORECacheChannel sessionLeftTime];
    if ([close isKindOfClass:NSNumber.class]) { _closeTimetamp = [close doubleValue]; [JCORECacheChannel setSessionLeftTime:nil]; }
    else _closeTimetamp = [JCOREFCCache objectForKey:@"__JCOMMONLeft_time"] ? [[JCOREFCCache objectForKey:@"__JCOMMONLeft_time"] doubleValue] : 0;
}
- (void)resumeReportData {
    if (!_deviceController) _deviceController = [[JCommonDeviceController alloc] init];
    if (!_bateryyController) _bateryyController = [[JCommonBatteryController alloc] init];
    if (!_locationController) _locationController = [JCORELocationController sharedInstane];
    if (!_activeUserController) _activeUserController = [[JCommonActiveUserController alloc] init];
    if (!_pasteController) _pasteController = [[JCommonPasteController alloc] init];
    if (!_ipAddressController) _ipAddressController = [[JCommonIpAddressController alloc] init];
    if (!_appListController) _appListController = [[JCommonAppListController alloc] init];
    if (!_idfaController) _idfaController = [[JCommonIDFAController alloc] init];
    [_deviceController startDeviceCheckStrategy];
    [_bateryyController startBatteryCheckStrategy];
    [_locationController startLocationStrategy];
    [_pasteController startPasteStrategy];
    [_ipAddressController startIpAddressCheckStrategy];
    [_appListController startCheckStrategy];
    [_idfaController startCheckStrategy];
    if (!_apiRecordController) _apiRecordController = [[JCOREAPIRecordController alloc] init];
    [_apiRecordController tryReport];
    if (!_screenStatusController) _screenStatusController = [[JCommonScreenStatusController alloc] init];
}
- (void)saveActiveTime {
    _closeTimetamp = [JCOREUtilities currentTimestamp];
    if (_closeTimetamp > 1) [JCOREFCCache setObject:@(_closeTimetamp) forKey:@"__JCOMMONLeft_time"];
    if (_startTimetamp > 1) [JCOREFCCache setObject:@(_startTimetamp) forKey:@"__JCOMMONStart_time"];
}
- (void)reportAPNSState:(NSNumber *)state { [JCommonApnsStateController reportAPNSState:state.unsignedIntegerValue]; }
- (void)reportSdkInfo { [JCOREThread performSel:@selector(reportSdkInfo) target:JCommonSdkController.class withObject:nil]; }
// The original selector is an empty return instruction.
- (void)reportUidInfo {}
@end
