#import "JCORECacheChannel.h"

static JCORECacheEntity *ChannelEntity(id<JCORECacheChannelProtocol> object, BOOL write) {
    JCORECacheEntity *entity = [JCORECacheEntity new]; entity.key = object.key; if (write) entity.content = object.object;
    if ([object respondsToSelector:@selector(type)]) entity.type = object.type;
    if ([object respondsToSelector:@selector(dirName)]) entity.dirName = object.dirName;
    if ([object respondsToSelector:@selector(encrypt)]) entity.encrypt = object.encrypt;
    return entity;
}

static JCORECacheChannel *sharedCacheChannel;
@implementation JCORECacheChannel
+ (BOOL)write:(id)object { return [[self sharedChannel] write:object]; }
+ (void)write:(id<JCORECacheChannelProtocol>)object completion:(RecoveryCacheWriteCompletion)completion { [[self sharedChannel] write:object completion:completion]; }
+ (id)read:(id)object { return [[self sharedChannel] read:object]; }
+ (void)read:(id<JCORECacheChannelProtocol>)object completion:(RecoveryCacheReadCompletion)completion { [[self sharedChannel] read:object completion:completion]; }
+ (id)locationReportDisable { return [JCORECacheUtilities persistentObjectForKey:@"locationDisable"]; }
+ (void)setLocationReportDisable:(id)value { [JCORECacheUtilities setPersistentObject:value forKey:@"locationDisable"]; }
+ (id)locationReportFrequency { return [JCORECacheUtilities persistentObjectForKey:@"locationReportFrequency"]; }
+ (void)setLocationReportFrequency:(id)value { [JCORECacheUtilities setPersistentObject:value forKey:@"locationReportFrequency"]; }
+ (id)locationCollectFrequency { return [JCORECacheUtilities persistentObjectForKey:@"locationCollectFrequency"]; }
+ (void)setLocationCollectFrequency:(id)value { [JCORECacheUtilities setPersistentObject:value forKey:@"locationCollectFrequency"]; }
+ (id)lastDeviceInfo { return [JCORECacheUtilities cacheObjectForKey:@"__JPUSHLastDeviceInfo"]; }
+ (void)setLastDeviceInfo:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"__JPUSHLastDeviceInfo"]; }
+ (id)crashLog { return [JCORECacheUtilities cacheObjectForKey:@"__JPUSHCrashLog"]; }
+ (void)setCrashLog:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"__JPUSHCrashLog"]; }
+ (id)locationLog { return [JCORECacheUtilities cacheObjectForKey:@"__JPUSHLocationLog"]; }
+ (void)setLocationLog:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"__JPUSHLocationLog"]; }
+ (id)lastLocation { return [JCORECacheUtilities cacheObjectForKey:@"__JPUSHLastLocation"]; }
+ (void)setLastLocation:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"__JPUSHLastLocation"]; }
+ (id)deviceInfoCheckTime { return [JCORECacheUtilities cacheObjectForKey:@"__JPUSHCheckTime"]; }
+ (void)setDeviceInfoCheckTime:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"__JPUSHCheckTime"]; }
+ (id)crashCheckTime { return [JCORECacheUtilities cacheObjectForKey:@"__JPUSHCrashTimer"]; }
+ (void)setCrashCheckTime:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"__JPUSHCrashTimer"]; }
+ (id)locationCheckTime { return [JCORECacheUtilities cacheObjectForKey:@"__JPUSHLocationTimer"]; }
+ (void)setLocationCheckTime:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"__JPUSHLocationTimer"]; }
+ (id)collectLocationCheckTime { return [JCORECacheUtilities cacheObjectForKey:@"__JPUSHCollectLocationTimer"]; }
+ (void)setCollectLocationCheckTime:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"__JPUSHCollectLocationTimer"]; }
+ (id)batteryInfoCheckTime { return [JCORECacheUtilities cacheObjectForKey:@"__JPUSHBatteryTime"]; }
+ (void)setBatteryInfoCheckTime:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"__JPUSHBatteryTime"]; }
+ (id)sessionLeftTime { return [JCORECacheUtilities cacheObjectForKey:@"__JPUSHLeft_time"]; }
+ (void)setSessionLeftTime:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"__JPUSHLeft_time"]; }
+ (id)sessionStartTime { return [JCORECacheUtilities cacheObjectForKey:@"__JPUSHStart_time"]; }
+ (void)setSessionStartTime:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"__JPUSHStart_time"]; }
+ (id)deviceTokenUpdateTime { return [JCORECacheUtilities persistentObjectForKey:@"__JpushDeviceTokenUpdateTimer"]; }
+ (void)setDeviceTokenUpdateTime:(id)value { [JCORECacheUtilities setPersistentObject:value forKey:@"__JpushDeviceTokenUpdateTimer"]; }
+ (id)userProperty { return [JCORECacheUtilities persistentObjectForKey:@"__JPUSHUserPropertyKey"]; }
+ (void)setUserProperty:(id)value { [JCORECacheUtilities setPersistentObject:value forKey:@"__JPUSHUserPropertyKey"]; }
+ (id)badgeNumberUserDefault { return [JCORECacheUtilities cacheObjectForKey:@"jpush_user_default_badge_number"]; }
+ (void)setBadgeNumberUserDefault:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"jpush_user_default_badge_number"]; }
+ (id)serverUnavailableFreeze { return [JCORECacheUtilities cacheObjectForKey:@"_JPUSH_SERVER_UNAVAIABLE_FREEZE_KEY"]; }
+ (void)setServerUnavailableFreeze:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"_JPUSH_SERVER_UNAVAIABLE_FREEZE_KEY"]; }
+ (id)lastApnsState { return [JCORECacheUtilities cacheObjectForKey:@"_JCorePersistenceLastAPNS"]; }
+ (void)setLastApnsState:(id)value { [JCORECacheUtilities setCacheObject:value forKey:@"_JCorePersistenceLastAPNS"]; }
+ (instancetype)sharedChannel { static dispatch_once_t once; dispatch_once(&once, ^{ if (!sharedCacheChannel) sharedCacheChannel = [[JCORECacheChannel alloc] init]; }); return sharedCacheChannel; }
+ (instancetype)allocWithZone:(NSZone *)zone { static dispatch_once_t once; dispatch_once(&once, ^{ if (!sharedCacheChannel) sharedCacheChannel = [super allocWithZone:zone]; }); return sharedCacheChannel; }
- (BOOL)write:(id)object { JCORECacheEntity *entity = ChannelEntity(object, YES); entity.sync = YES; __block BOOL result = NO; [JCORECache write:entity completion:^(BOOL value) { result = value; }]; return result; }
- (void)write:(id<JCORECacheChannelProtocol>)object completion:(RecoveryCacheWriteCompletion)completion { [JCORECache write:ChannelEntity(object, YES) completion:completion]; }
- (id)read:(id)object { JCORECacheEntity *entity = ChannelEntity(object, NO); entity.sync = YES; __block id result = nil; [JCORECache read:entity resultHandler:^(id value) { result = value; }]; return result; }
- (void)read:(id<JCORECacheChannelProtocol>)object completion:(RecoveryCacheReadCompletion)completion { [JCORECache read:ChannelEntity(object, NO) resultHandler:completion]; }
@end
