#pragma once
#import <Foundation/Foundation.h>
#import "JCOREAddressAnalysis.h"
#import "JCORENetworkModels.h"
#import "JCORESRVResolver.h"

@interface JCOREAddressAnalysisController : NSObject <JCORESRVResolverDelegate> {
    long long _parseHostState;
    NSArray *_dataList;
    NSDictionary *_dnsResults;
    NSDictionary *_srvResults;
    NSDictionary *_wifiResults;
    __weak NSThread *_currentThread;
    JCORESRVResolver *_resolver;
}
@property (nonatomic) long long parseHostState;
@property (strong, nonatomic) NSArray *dataList;
@property (strong, nonatomic) NSDictionary *dnsResults;
@property (strong, nonatomic) NSDictionary *srvResults;
@property (strong, nonatomic) NSDictionary *wifiResults;
@property (weak, nonatomic) NSThread *currentThread;
@property (readonly, nonatomic) JCORESRVResolver *resolver;
@property (readonly) NSUInteger hash;
@property (readonly) Class superclass;
@property (readonly, copy) NSString *description;
@property (readonly, copy) NSString *debugDescription;
+ (instancetype)instance;
- (void)addValue:(NSDictionary *)values;
- (void)addAddressAnalysis:(JCOREAddressAnalysis *)analysis;
- (NSArray *)addressListForType:(NSString *)type statusCode:(long long)status within:(double)interval;
- (NSArray *)addressListForType:(NSString *)type within:(double)interval;
- (void)reset:(JCOREAddressAnalysis *)analysis;
- (JCOREAddressAnalysis *)newObjectWith:(NSString *)address;
- (void)addObject:(JCOREAddressAnalysis *)analysis;
- (long long)findIndex:(NSDictionary *)values;
- (JCOREAddressAnalysis *)findObjectWithAnalysis:(JCOREAddressAnalysis *)analysis;
- (JCOREAddressAnalysis *)findAnalysisObject:(NSDictionary *)values;
- (void)updateObject:(JCOREAddressAnalysis *)analysis;
- (void)sortList;
- (NSDictionary *)getAddressFromDNS:(NSString *)host timeout:(double)timeout;
- (JCOREDNSResult *)getDNSCacheWithDomain:(NSString *)domain;
- (void)addDNSCache:(JCOREDNSResult *)result;
- (void)removeDNSCache:(JCOREDNSResult *)result;
- (void)removeDNSCacheWithDomain:(NSString *)domain;
- (NSArray *)parseWithHost:(NSString *)host timeout:(double)timeout;
- (void)removeResolver;
- (NSArray *)runWithSRVName:(NSString *)name timeout:(double)timeout;
- (void)removeSrvRecode;
- (NSArray *)getAddressFromSRV:(NSString *)name timeout:(double)timeout;
- (JCORESRVResult *)getSRVCacheWithDomain:(NSString *)domain;
- (void)addSRVCache:(JCORESRVResult *)result;
- (void)removeSRVCache:(JCORESRVResult *)result;
- (JCOREWifiCacheModel *)getCacheWithWifiInfo:(JCOREWifiCacheModel *)info;
- (void)addWifiCache:(JCOREWifiCacheModel *)info;
- (void)removeWifiCache:(JCOREWifiCacheModel *)info;
- (void)applicationWillResignActive:(NSNotification *)notification;
- (void)saveDataToFile;
- (void)restoreDataFromFile;
- (void)restoreDnsResults:(NSDictionary *)results;
- (void)restoreAnalysisDataList:(NSArray *)results;
- (void)restoreSrvResults:(NSDictionary *)results;
- (void)restoreWifiCache:(NSDictionary *)results;
- (NSString *)filePath;
- (NSArray *)arrayWithType:(NSString *)type array:(NSArray *)array;
- (NSArray *)arrayWithSisResult:(NSArray *)array;
- (double)successRateWithArray:(NSArray *)array type:(NSString *)type;
- (NSArray *)arrayInFixedTime:(double)interval;
@end

@interface NSObject (JCOREAddressAnalysis)
- (void)jcoreAAAddValue:(NSDictionary *)values;
- (void)addAddressAnalysis:(JCOREAddressAnalysis *)analysis;
@end
@interface NSString (JCOREAddressAnalysisType)
- (NSArray *)jcoreAAAddressListWithin:(double)interval;
@end
