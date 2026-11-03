#pragma once
#import <Foundation/Foundation.h>
@class JCommonReportTimerManager;
@interface JCommonIpAddressController : NSObject {
    JCommonReportTimerManager *_timerManager;
    BOOL _neesLoginReport;
}
- (void)didLogin;
- (void)startIpAddressCheckStrategy;
- (void)stopIpAddressCheckStrategy;
- (void)startTimer;
- (void)stopTimer;
+ (id)localApiIpv6;
- (id)contentWithIpv6Address:(id)address;
- (void)reportIpv6AddressInfo;
- (void)requestAndReportIpAddress;
- (void)requestIpAddress:(void (^)(id))completion;
@end
