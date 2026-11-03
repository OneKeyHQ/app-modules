#pragma once
#import <Foundation/Foundation.h>
@interface JCommonDataConfigManager : NSObject {
    BOOL _isPull;
    NSDictionary *_cacheConfig;
}
@property(strong, nonatomic) NSDictionary *cacheConfig;
@property(nonatomic) BOOL isPull;
+ (instancetype)sharedManager;
- (instancetype)init;
- (void)loadCache;
- (void)processConfig:(id)configuration;
- (void)loadConfig:(void (^)(void))completion;
@end
