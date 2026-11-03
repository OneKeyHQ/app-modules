#pragma once
#import "RecoveredProtocols.h"
#import <dispatch/dispatch.h>
@interface JCORECrashConfig : NSObject {
    BOOL _statusSwitch;
    BOOL _statusSupersetSwitch;
    BOOL _statusTcpSSL;
    BOOL _statusCrashCountSwitch;
    BOOL _statusTcpReportSwitch;
    BOOL _statusNetLengthSwitch;
    BOOL _statusExceptionSwitch;
    BOOL _statusTcpExceptionSwitch;
    BOOL _newSocketSwitch;
    BOOL _checkIpv6Switch;
    BOOL _isPull;
    BOOL _isFinish;
    double _netLengthThreshold;
    long long _netCountThreshold;
    double _netConnLengthThreshold;
    long long _netConnCountThreshold;
}
@property BOOL isPull;
@property BOOL isFinish;
@property(readonly, nonatomic) BOOL statusSwitch;
@property(readonly, nonatomic) BOOL statusSupersetSwitch;
@property(readonly, nonatomic) BOOL statusTcpSSL;
@property(readonly, nonatomic) BOOL statusCrashCountSwitch;
@property(readonly, nonatomic) BOOL statusTcpReportSwitch;
@property(readonly, nonatomic) BOOL statusNetLengthSwitch;
@property(readonly, nonatomic) BOOL statusExceptionSwitch;
@property(readonly, nonatomic) BOOL statusTcpExceptionSwitch;
@property(readonly, nonatomic) double netLengthThreshold;
@property(readonly, nonatomic) long long netCountThreshold;
@property(readonly, nonatomic) double netConnLengthThreshold;
@property(readonly, nonatomic) long long netConnCountThreshold;
@property(readonly, nonatomic) BOOL newSocketSwitch;
@property(readonly, nonatomic) BOOL checkIpv6Switch;
- (void)handleConfigContentWithResponse:(id)response
                             completion:(void (^)(BOOL crash, BOOL network))completion;
- (void)pullCrashSwitchWithAppkeyAndCompletion:(id)args;
- (void)pullCrashCofigContentWithAppkey:(id)appkey completion:(RecoveryOpaqueCallback)completion;
- (void)getTcpSSLCache:(void (^)(BOOL))completion;
- (void)sendAllReports;
@end
