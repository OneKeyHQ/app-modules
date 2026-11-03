#import "JCORECertController.h"
#import "JCOREBusinessStorage.h"
#import "JCORECertRequest.h"
#import "JCORELog.h"
#import "RecoveredCoreService.h"
static BOOL _isPulling;
#define CertFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Cert/JCORECertController.m"
#define CertLog(lineValue, fmt, ...)                                                                         \
    [JCORELog log:YES                                                                                        \
            level:0x3ff                                                                                      \
             flag:0x100                                                                                      \
          context:0                                                                                          \
             file:CertFile                                                                                   \
         function:__PRETTY_FUNCTION__                                                                        \
             line:lineValue                                                                                  \
              tag:nil                                                                                        \
           format:fmt, ##__VA_ARGS__]
@implementation JCORECertController
+ (void)pullCertWithServerVersion:(id)version {
    if (_isPulling == YES) {
        CertLog(19, @"is updating cert, return");
        return;
    }
    _isPulling = YES;
    if (![JCOREService getStatusTcpSSL]) {
        CertLog(24, @"server not open tcp ssl, return");
        _isPulling = NO;
        return;
    }
    if (![version isKindOfClass:[NSNumber class]]) {
        CertLog(30, @"server version bad, return");
        _isPulling = NO;
        return;
    }
    id localVersion = [JCOREBusinessStorage getLocalCerVerion], cert = [JCOREBusinessStorage getLocalCert];
    if (cert && [cert isKindOfClass:[NSString class]] && localVersion &&
        [localVersion isKindOfClass:[NSNumber class]] && [localVersion isKindOfClass:[NSNumber class]] &&
        [localVersion intValue] == [version intValue]) {
        CertLog(44, @"sdk cert cersion same to server, return");
        _isPulling = NO;
        return;
    }
    CertLog(39, @"start update local cert");
    [self pullCert:version
           timeout:6
        retryCount:3
        completion:^(BOOL success) {
          _isPulling = NO;
        }];
}
+ (void)pullCert:(id)version
         timeout:(double)timeout
      retryCount:(long long)retryCount
      completion:(void (^)(BOOL))completion {
    if (!version && completion)
        completion(NO);
    JCORECertRequest *request = [JCORECertRequest new];
    request.timeOut = timeout;
    request.retryCount = retryCount;
    [request sendWithCompletion:^(BOOL success, NSNumber *newVersion, NSString *cert) {
      CertLog(59, @"cert request result: %d", success);
      if (success && newVersion && cert) {
          [JCOREBusinessStorage setLocalCert:cert];
          [JCOREBusinessStorage setLocalCerVerison:newVersion];
          if (completion)
              completion(YES);
      } else if (completion)
          completion(NO);
    }];
}
+ (id)getCert {
    return [JCOREBusinessStorage getLocalCert];
}
+ (id)getCertVersion {
    return [JCOREBusinessStorage getLocalCerVerion];
}
+ (BOOL)certReady {
    id cert = [self getCert];
    id version = [self getCertVersion];
    return cert != nil && version != nil;
}
@end
