#pragma once
#import <Foundation/Foundation.h>
@class JCommonServiceController;
@interface JCommonService : NSObject {
    JCommonServiceController *_client;
}
@property (strong, nonatomic) JCommonServiceController *client;
+ (instancetype)sharedService;
+ (void)setCrashLogON;
+ (void)apiRecord:(id)identifier methodName:(id)method;
+ (void)setSentryCrashON;
+ (void)setSupersetMoniterOn;
+ (NSString *)startHttpMoniter:(NSString *)method url:(NSString *)url request_byte:(long long)bytes;
+ (NSString *)startConnectionMoniter:(NSString *)type host:(NSString *)host port:(long long)port;
+ (NSString *)startTcpEventMoniter:(NSString *)type;
+ (void)endHttpMoniter:(NSString *)identifier response_byte:(long long)bytes error_code:(long long)error status_code:(long long)status;
+ (void)endConnectionMoniter:(NSString *)identifier error_code:(long long)error status_code:(long long)status;
+ (void)setupService;
+ (id)currentLocationInfo;
+ (void)setLocationInfo:(NSDictionary *)information;
+ (void)startEventTracking;
+ (void)stopEventTracking;
+ (void)trackUAIDWithCode:(long long)code;
+ (void)trackAppListWithCode:(long long)code;
+ (void)trackLocInfoWithCode:(long long)code;
+ (void)trackGPSWithCode:(long long)code;
+ (void)trackWiFiWithCode:(long long)code;
+ (void)trackCellWithCode:(long long)code;
+ (void)startTcpExceptionMonitoring;
+ (void)stopTcpExceptionMonitoring;
+ (void)recordTcpException:(long long)code message:(NSString *)message;
- (void)setup;
@end
