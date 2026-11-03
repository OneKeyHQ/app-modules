#import "JCommonDataConfigManager.h"
#import "JCORELog.h"
#import "JCommonDataConfigRequest.h"
#import "RecoveredCache.h"
#import "RecoveredModels.h"
#define DataConfigFile                                                                                       \
    "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCommon/JCommon/DataConfig/"                        \
    "JCommonDataConfigManager.m"
@implementation JCommonDataConfigManager
+ (instancetype)sharedManager {
    static JCommonDataConfigManager *manager;
    static dispatch_once_t once;
    dispatch_once(&once, ^{
      manager = [[JCommonDataConfigManager alloc] init];
    });
    return manager;
}
- (instancetype)init {
    self = [super init];
    if (self)
        [self loadCache];
    return self;
}
- (void)loadCache {
    id cached = [JCORECacheUtilities cacheObjectForKey:@"JCommonDataConfigCacheKey"];
    if (cached && [cached isKindOfClass:NSDictionary.class]) {
        self.cacheConfig = cached;
        [self processConfig:cached];
    }
}
- (void)processConfig:(id)configuration {
    id status = [configuration valueForKey:@"status"];
    if (status && [status isKindOfClass:NSNumber.class]) {
        if ([status integerValue] == 0) {
            id list = [configuration valueForKey:@"pl"];
            if ([list isKindOfClass:NSArray.class])
                for (id item in list) {
                    id code = [item valueForKey:@"ci"];
                    if (code && [code isKindOfClass:NSNumber.class] && [code integerValue] == 10000) {
                        JCommonDataConfig *config = [[JCommonDataConfig alloc] init];
                        config.type = 10000;
                        [[JCommonDataControl sharedManager] addConfig:config];
                        [JCORELog log:YES
                                level:0x3ff
                                 flag:0x100
                              context:0
                                 file:DataConfigFile
                             function:__PRETTY_FUNCTION__
                                 line:74
                                  tag:nil
                               format:@"data config: idfa switch open"];
                    }
                }
        } else
            [[JCommonDataControl sharedManager] clearConfig];
    }
}
- (void)loadConfig:(void (^)(void))completion {
    if (self.isPull) {
        [JCORELog log:YES
                level:0x3ff
                 flag:0x100
              context:0
                 file:DataConfigFile
             function:__PRETTY_FUNCTION__
                 line:83
                  tag:nil
               format:@"data config is requesting, return"];
        return;
    }
    id sign = [self.cacheConfig valueForKey:@"sign"];
    if (![sign isKindOfClass:NSString.class] || ![sign length])
        sign = nil;
    __weak typeof(self) weakSelf = self;
    JCommonDataConfigRequest *configRequest = [[JCommonDataConfigRequest alloc] init];
    if (sign)
        configRequest.sign = sign;
    [configRequest sendRequest:^(id response, NSError *error) {
      __strong typeof(weakSelf) strongSelf = weakSelf;
      [JCORELog log:YES
              level:0x3ff
               flag:0x100
            context:0
               file:DataConfigFile
           function:"-[JCommonDataConfigManager loadConfig:]_block_invoke"
               line:100
                tag:nil
             format:@"data config: %@ --- %@", response, error];
      strongSelf.isPull = NO;
      if (!error && [response isKindOfClass:NSDictionary.class]) {
          id code = [response valueForKey:@"code"];
          if (code && [code isKindOfClass:NSNumber.class] && [code integerValue] == 2000) {
              id content = [response valueForKey:@"data"];
              if (content && [content isKindOfClass:NSDictionary.class]) {
                  id newSign = [content valueForKey:@"sign"];
                  if (newSign && [newSign isKindOfClass:NSString.class] && [newSign length]) {
                      id previous = [strongSelf.cacheConfig valueForKey:@"sign"];
                      if (previous && [previous isKindOfClass:NSString.class] &&
                          [previous isEqualToString:newSign]) {
                          [JCORELog log:YES
                                  level:0x3ff
                                   flag:0x100
                                context:0
                                   file:DataConfigFile
                               function:"-[JCommonDataConfigManager loadConfig:]_block_invoke"
                                   line:143
                                    tag:nil
                                 format:@"data config: sign is same, not update"];
                          if (completion)
                              completion();
                          return;
                      }
                      [strongSelf processConfig:content];
                      if (completion)
                          completion();
                      [JCORECacheUtilities setCacheObject:content forKey:@"JCommonDataConfigCacheKey"];
                      return;
                  }
              }
          }
      }
      if (completion)
          completion();
    }];
}
@end
