#pragma once
#import "RecoveredProtocols.h"
@interface JCommonReportManager : NSObject
+ (void)sendReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendAppLaunchReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendAppTerminalReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendAppLocationReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendAppCrashReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendAppCrashCountReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendBatteryInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendAppDeviceInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendIPV6AddressInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendApnsStateReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendUidReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendBoardReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendSdkInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendBlueInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendNidInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendAPIReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendSupersetMoniter:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendSCStatus:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendAppListReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendAppIDFAInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendExceptionReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
+ (void)sendTcpExceptionReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion;
@end
