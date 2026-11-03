#import "JCommonService.h"
#import "JCommonServiceController.h"
#import "JCOREThread.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"
#import "JCommonEventTrackingManager.h"
#import "JCommonTcpExpMonitor.h"
#define JCommonServiceFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/JCommonService.m"
#define JCommonServiceLog(sourceLine, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCommonServiceFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]
static JCommonService *JCommonSharedService;
@implementation JCommonService
@synthesize client = _client;
+ (instancetype)sharedService {
    static dispatch_once_t once;
    dispatch_once(&once, ^{ if (!JCommonSharedService) JCommonSharedService = [[JCommonService alloc] init]; });
    return JCommonSharedService;
}
+ (id)allocWithZone:(NSZone *)zone {
    static dispatch_once_t once;
    dispatch_once(&once, ^{ if (!JCommonSharedService) JCommonSharedService = [super allocWithZone:zone]; });
    return JCommonSharedService;
}
+ (void)setCrashLogON { JCommonServiceLog(49, @"action-start crash log on"); [JCommonServiceController setCrashLogON]; }
+ (void)apiRecord:(id)identifier methodName:(id)method { [JCommonServiceController apiRecord:identifier methodName:method]; }
+ (void)setSentryCrashON { JCommonServiceLog(59, @"action-start sentry crash log on"); [JCommonServiceController setSentryCrashON]; }
+ (void)setSupersetMoniterOn { [JCommonServiceController setSupersetMoniterOn]; }
+ (NSString *)startHttpMoniter:(NSString *)method url:(NSString *)url request_byte:(long long)bytes { return [JCommonServiceController startHttpMoniter:method url:url request_byte:bytes]; }
+ (NSString *)startConnectionMoniter:(NSString *)type host:(NSString *)host port:(long long)port { return [JCommonServiceController startConnectionMoniter:type host:host port:port]; }
+ (NSString *)startTcpEventMoniter:(NSString *)type { return [JCommonServiceController startTcpEventMoniter:type]; }
+ (void)endHttpMoniter:(NSString *)identifier response_byte:(long long)bytes error_code:(long long)error status_code:(long long)status { [JCommonServiceController endHttpMoniter:identifier response_byte:bytes error_code:error status_code:status]; }
+ (void)endConnectionMoniter:(NSString *)identifier error_code:(long long)error status_code:(long long)status { [JCommonServiceController endConnectionMoniter:identifier error_code:error status_code:status]; }
+ (void)setupService {
    JCommonServiceLog(98, @"JCommon setupService");
    [JCOREThread performSel:@selector(setup) target:[JCommonService sharedService] withObject:nil];
}
- (void)setup { if (!_client) _client = [[JCommonServiceController alloc] initWithTimetamp:[JCOREUtilities currentTimestamp]]; }
+ (id)currentLocationInfo {
    if (![JCommonService sharedService].client) [JCOREThread performSel:@selector(setup) target:[JCommonService sharedService] withObject:nil];
    return [[JCommonService sharedService].client currentLocationInfo];
}
+ (void)setLocationInfo:(NSDictionary *)information {
    if (information && information.allKeys.count) { [[JCommonService sharedService].client setLocationInfo:information]; return; }
    JCommonServiceLog(116, @"locationInfo is nil");
}
+ (void)startEventTracking { JCommonServiceLog(125, @"[JCommonService] Starting event tracking"); [JCommonEventTrackingManager setEventTrackingOn]; }
+ (void)stopEventTracking { JCommonServiceLog(130, @"[JCommonService] Stopping event tracking"); [JCommonEventTrackingManager setEventTrackingOff]; }
+ (void)trackUAIDWithCode:(long long)code { [JCommonEventTrackingManager trackUAIDWithCode:code]; }
+ (void)trackAppListWithCode:(long long)code { [JCommonEventTrackingManager trackAppListWithCode:code]; }
+ (void)trackLocInfoWithCode:(long long)code { [JCommonEventTrackingManager trackLocInfoWithCode:code]; }
+ (void)trackGPSWithCode:(long long)code { [JCommonEventTrackingManager trackGPSWithCode:code]; }
+ (void)trackWiFiWithCode:(long long)code { [JCommonEventTrackingManager trackWiFiWithCode:code]; }
+ (void)trackCellWithCode:(long long)code { [JCommonEventTrackingManager trackCellWithCode:code]; }
+ (void)startTcpExceptionMonitoring { JCommonServiceLog(161, @"[JCommonService] Starting TCP exception monitoring"); [JCommonTcpExpMonitor startMonitoring]; }
+ (void)stopTcpExceptionMonitoring { JCommonServiceLog(166, @"[JCommonService] Stopping TCP exception monitoring"); [JCommonTcpExpMonitor stopMonitoring]; }
+ (void)recordTcpException:(long long)code message:(NSString *)message { [JCommonTcpExpMonitor recordException:code message:message]; }
@end
