#import "JCORETcpAddressBlackList.h"
#import "RecoveredCache.h"
#import "JCOREUtilities.h"

@implementation JCORETcpAddressBlackList
+ (instancetype)shareInstance {
    static JCORETcpAddressBlackList *instance;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ instance = [[JCORETcpAddressBlackList alloc] init]; });
    return instance;
}
- (instancetype)init {
    self = [super init];
    if (self) { _blackListDic = [NSMutableDictionary dictionary]; [self getBlackListFromCache]; }
    return self;
}
+ (void)addToBlackList:(id)address time:(double)time { [[self shareInstance] addToBlackList:address time:time]; }
+ (BOOL)addressInBlackList:(id)address { return [[self shareInstance] addressInBlackList:address]; }
- (void)addToBlackList:(id)address time:(double)time {
    if (address && [address isKindOfClass:NSString.class] && [address length]) {
        [_blackListDic setValue:@([JCOREUtilities currentTimestamp] + time) forKey:address];
        [self cacheBlackList];
    }
}
- (BOOL)addressInBlackList:(id)address {
    double now = [JCOREUtilities currentTimestamp];
    BOOL changed = NO;
    for (NSString *key in [_blackListDic.allKeys mutableCopy]) {
        if ([_blackListDic[key] doubleValue] <= now) { [_blackListDic removeObjectForKey:key]; changed = YES; }
    }
    if (changed) [self cacheBlackList];
    return [_blackListDic.allKeys containsObject:address];
}
- (void)getBlackListFromCache {
    id cache = [JCORECacheUtilities cacheObjectForKey:@"kJCOREAddressBlackListKey"];
    if (cache && [cache isKindOfClass:NSDictionary.class]) [_blackListDic addEntriesFromDictionary:cache];
}
- (void)cacheBlackList { [JCORECacheUtilities setCacheObject:_blackListDic forKey:@"kJCOREAddressBlackListKey"]; }
@end
