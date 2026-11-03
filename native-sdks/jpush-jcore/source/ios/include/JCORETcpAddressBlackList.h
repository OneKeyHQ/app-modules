#pragma once
#import <Foundation/Foundation.h>
@interface JCORETcpAddressBlackList : NSObject {
    NSMutableDictionary *_blackListDic;
}
+ (instancetype)shareInstance;
+ (void)addToBlackList:(id)address time:(double)time;
+ (BOOL)addressInBlackList:(id)address;
- (void)addToBlackList:(id)address time:(double)time;
- (BOOL)addressInBlackList:(id)address;
- (void)getBlackListFromCache;
- (void)cacheBlackList;
@end
