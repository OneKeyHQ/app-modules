#pragma once
#import "JCOREPropertyModels.h"
#import <dispatch/dispatch.h>
typedef void (^JCOREConfigStatusCompletion)(BOOL status);
typedef void (^JCOREConfigSetupCompletion)(NSError *error, id<JCOREConfigProtocol> config);
@interface JCOREConfig : NSObject {
    dispatch_semaphore_t _statusLock;
    BOOL _statusSwitch;
    BOOL _regFingerSwitch;
    BOOL _isFinish;
    NSMutableArray *_blockArray;
}
@property BOOL isFinish;
@property(strong, nonatomic) NSMutableArray *blockArray;
@property(readonly, nonatomic) BOOL statusSwitch;
@property(nonatomic) BOOL regFingerSwitch;
- (void)setStatusSwitch:(BOOL)value;
- (void)getFingerSwitchFromCache;
- (void)pullStatusSwitchWithCompletion:(JCOREConfigStatusCompletion)completion;
- (void)addBlockArrar:(JCOREConfigStatusCompletion)completion;
- (void)obtainSwitchStatusWithAppkey:(id)appkey;
- (void)forceUpdateConfig:(id)args;
- (void)obtainSwitchStatusWithArgs:(id)args;
- (void)pullCofigContentWithAppkey:(id)appkey;
- (void)handleConfigContentWithResponse:(id)response;
- (void)traverseBlockArr;
@end
@interface JCOREConfigController : NSObject {
    BOOL _hasConfig;
    JCOREConfigModel *_configModel;
}
@property(nonatomic) BOOL hasConfig;
@property(strong, nonatomic) JCOREConfigModel *configModel;
+ (instancetype)sharedController;
+ (BOOL)setConfig:(id)config;
+ (id)config;
+ (void)setNewAdvertisingId:(id)identifier;
- (void)setAdvertisingId:(id)identifier;
- (BOOL)setConfig:(id)config;
- (void)doResult:(unsigned short)code message:(id)message withBlock:(JCOREConfigSetupCompletion)block;
- (BOOL)isValidAppKey:(id)key;
- (BOOL)isVerifyIDFAString:(id)identifier;
- (BOOL)confirmFormatOfIDFA:(id)identifier;
- (BOOL)checkFormatOfAllZeroIDFA:(id)identifier;
@end
@interface JCOREConfigUpdateController : NSObject {
    void (^_completionHandler)(NSArray *configs);
}
@property(copy, nonatomic) void (^completionHandler)(NSArray *configs);
- (void)startMonitoringWithCompletion:(void (^)(NSArray *configs))completion;
- (void)stopMonitoring;
- (void)handleCmdControlNotification:(NSNotification *)notification;
- (BOOL)shouldUpdateWithConfig:(id)config protocolVersion:(long long)version;
- (BOOL)isVersion:(long long)version inRange:(id)range;
@end
