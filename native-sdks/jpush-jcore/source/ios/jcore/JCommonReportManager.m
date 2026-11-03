#import "JCommonReportManager.h"
#import "JCORENetChannel.h"
#import "RecoveredModels.h"
@implementation JCommonReportManager
+ (void)sendReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    JCommonReport *request = [JCommonReport new];
    request.requestInfo = content;
    request.completionHandler = completion;
    [JCORENetChannel send:request];
}
+ (void)sendAppLaunchReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendAppTerminalReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendAppLocationReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendAppCrashReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendAppCrashCountReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendBatteryInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendAppDeviceInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendIPV6AddressInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendApnsStateReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendUidReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendBoardReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendSdkInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendBlueInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendNidInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendAPIReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendSupersetMoniter:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendSCStatus:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    JCommonReport *request = [JCommonReport new];
    request.requestInfo = content;
    request.completionHandler = completion;
    request.cacheRequired = NO;
    [JCORENetChannel send:request];
}
+ (void)sendAppListReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendAppIDFAInfoReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendExceptionReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
+ (void)sendTcpExceptionReport:(id)content completionHandler:(RecoveryOpaqueCallback)completion {
    [self sendReport:content completionHandler:completion];
}
@end
