#pragma once
#import "RecoveredProtocols.h"
#import <dispatch/dispatch.h>
@interface JCOREDevidBanConfig : NSObject {
    dispatch_semaphore_t _statusLock;
    BOOL _isBan;
    BOOL _isFinish;
    NSMutableArray *_completionArray;
}
@property(copy, nonatomic) NSMutableArray *completionArray;
@property(readonly, nonatomic) BOOL isBan;
@property(readonly, nonatomic) BOOL isFinish;
- (void)pullStatusSwitchWithCompletionWithAppkey:(id)appkey andHandle:(void (^)(BOOL))completion;
- (void)obtainDevidBanSwitchStatusWithAppkey:(id)appkey;
- (void)obtainDevidBanSwitchStatusWithArgs:(id)args;
- (void)pullCofigContentWithAppkey:(id)appkey;
- (void)handleConfigContentWithResponse:(id)response;
- (void)traverseBlockArr;
- (void)addBlockArrar:(void (^)(BOOL))completion;
@end
