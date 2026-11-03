#pragma once
#import "RecoveredProtocols.h"
#import <dispatch/dispatch.h>
@interface JCOREUaidTokenConfig : NSObject {
    BOOL _statusSwitch;
    BOOL _isPull;
    BOOL _isFinish;
}
@property BOOL isPull;
@property BOOL isFinish;
@property(readonly, nonatomic) BOOL statusSwitch;
- (void)handleConfigContentWithResponse:(id)response completion:(void (^)(BOOL, NSDictionary *))completion;
- (void)pullUaidTokenWithAppkeyAndCompletion:(id)args;
- (void)pullUaidTokenCofigContentWithCompletion:(RecoveryOpaqueCallback)completion;
- (void)forceUpdateConfig:(id)args;
- (void)pushUaidTokenWithAppkeyAndCompletion:(id)info;
- (void)pushUaidTokenCofigContentWithAppkey:(id)appkey
                                  tokenInfo:(id)info
                                 completion:(RecoveryOpaqueCallback)completion;
- (void)getToken:(id)list completion:(RecoveryOpaqueCallback)completion;
- (void)getToken:(id)entry handler:(RecoveryOpaqueCallback)completion;
@end
