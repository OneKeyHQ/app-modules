#pragma once
#import <Foundation/Foundation.h>
@class JCORELocationController, JCommonDeviceController, JCommonBatteryController, JCommonActiveUserController;
@class JCommonPasteController, JCommonIpAddressController, JCommonAppListController, JCommonIDFAController;
@class JCOREAPIRecordController, JCommonScreenStatusController;
@interface JCommonServiceController : NSObject {
    double _becomeActiveTime;
    JCORELocationController *_locationController;
    JCommonDeviceController *_deviceController;
    JCommonBatteryController *_bateryyController;
    JCommonActiveUserController *_activeUserController;
    JCommonPasteController *_pasteController;
    JCommonIpAddressController *_ipAddressController;
    JCommonAppListController *_appListController;
    JCommonIDFAController *_idfaController;
    JCOREAPIRecordController *_apiRecordController;
    JCommonScreenStatusController *_screenStatusController;
    double _startTimetamp;
    double _closeTimetamp;
    BOOL _isSetup;
}
+ (void)setCrashLogON;
+ (void)setSentryCrashON;
+ (void)setSupersetMoniterOn;
+ (NSString *)startHttpMoniter:(NSString *)method url:(NSString *)url request_byte:(long long)bytes;
+ (NSString *)startConnectionMoniter:(NSString *)type host:(NSString *)host port:(long long)port;
+ (NSString *)startTcpEventMoniter:(NSString *)type;
+ (void)endHttpMoniter:(NSString *)identifier response_byte:(long long)bytes error_code:(long long)error status_code:(long long)status;
+ (void)endConnectionMoniter:(NSString *)identifier error_code:(long long)error status_code:(long long)status;
+ (void)apiRecord:(id)identifier methodName:(id)method;
- (instancetype)initWithTimetamp:(double)timestamp;
- (id)currentLocationInfo;
- (void)setLocationInfo:(NSDictionary *)information;
- (void)setup;
- (void)prepareActiveUserReport:(NSNumber *)timestamp isColdLaunch:(BOOL)cold;
- (void)hotStartPrepareActiveUserReport;
- (void)setupSession:(double)timestamp;
- (void)closeSession;
- (void)reportLaunch:(double)timestamp withColdLaunch:(BOOL)cold;
- (void)reportActive;
- (void)addObserver;
- (void)receiveHeartAck:(NSNotification *)notification;
- (void)didLogin:(NSNotification *)notification;
- (void)handleCmdControl:(NSNotification *)notification;
- (void)applicationWillTerminate;
- (void)applicationDidBecomeActive;
- (void)applicationDidEnterBackground;
- (void)applicationWillEnterForeground;
- (void)applicationWillResignActive;
- (void)stopReportTimer;
- (void)resumeSession;
- (void)resumeReportData;
- (void)saveActiveTime;
- (void)reportAPNSState:(NSNumber *)state;
- (void)reportSdkInfo;
- (void)reportUidInfo;
@end
