#pragma once
#import "RecoveredCache.h"

@protocol JCORECacheChannelProtocol <NSObject>
@property (copy, nonatomic) NSString *key;
@property (strong, nonatomic) id object;
@optional
@property (nonatomic) unsigned long long type;
@property (copy, nonatomic) NSString *dirName;
@property (nonatomic) BOOL encrypt;
@end

@interface JCORECacheChannel : NSObject
- (BOOL)write:(id)arg1;
- (void)write:(id<JCORECacheChannelProtocol>)arg1 completion:(RecoveryCacheWriteCompletion)arg2;
- (id)read:(id)arg1;
- (void)read:(id<JCORECacheChannelProtocol>)arg1 completion:(RecoveryCacheReadCompletion)arg2;
+ (BOOL)write:(id)arg1;
+ (void)write:(id<JCORECacheChannelProtocol>)arg1 completion:(RecoveryCacheWriteCompletion)arg2;
+ (id)read:(id)arg1;
+ (void)read:(id<JCORECacheChannelProtocol>)arg1 completion:(RecoveryCacheReadCompletion)arg2;
+ (id)locationReportDisable;
+ (void)setLocationReportDisable:(id)arg1;
+ (id)locationReportFrequency;
+ (void)setLocationReportFrequency:(id)arg1;
+ (id)locationCollectFrequency;
+ (void)setLocationCollectFrequency:(id)arg1;
+ (id)lastDeviceInfo;
+ (void)setLastDeviceInfo:(id)arg1;
+ (id)crashLog;
+ (void)setCrashLog:(id)arg1;
+ (id)locationLog;
+ (void)setLocationLog:(id)arg1;
+ (id)lastLocation;
+ (void)setLastLocation:(id)arg1;
+ (id)deviceInfoCheckTime;
+ (void)setDeviceInfoCheckTime:(id)arg1;
+ (id)crashCheckTime;
+ (void)setCrashCheckTime:(id)arg1;
+ (id)locationCheckTime;
+ (void)setLocationCheckTime:(id)arg1;
+ (id)collectLocationCheckTime;
+ (void)setCollectLocationCheckTime:(id)arg1;
+ (id)batteryInfoCheckTime;
+ (void)setBatteryInfoCheckTime:(id)arg1;
+ (id)sessionLeftTime;
+ (void)setSessionLeftTime:(id)arg1;
+ (id)sessionStartTime;
+ (void)setSessionStartTime:(id)arg1;
+ (id)deviceTokenUpdateTime;
+ (void)setDeviceTokenUpdateTime:(id)arg1;
+ (id)userProperty;
+ (void)setUserProperty:(id)arg1;
+ (id)badgeNumberUserDefault;
+ (void)setBadgeNumberUserDefault:(id)arg1;
+ (id)serverUnavailableFreeze;
+ (void)setServerUnavailableFreeze:(id)arg1;
+ (id)lastApnsState;
+ (void)setLastApnsState:(id)arg1;
+ (instancetype)sharedChannel;
+ (instancetype)allocWithZone:(NSZone *)arg1;
@end
