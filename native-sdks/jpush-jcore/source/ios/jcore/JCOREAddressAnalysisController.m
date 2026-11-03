#import "JCOREAddressAnalysisController.h"
#import "JCOREAddress.h"
#import "JCOREIpvxHelper.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"
#import <CFNetwork/CFNetwork.h>
#import <UIKit/UIKit.h>
#include <arpa/inet.h>

#import "JCORESupersetManager.h"

#define JCOREAnalysisFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Analysis/JCOREAddressAnalysisController.m"
#define JCOREAnalysisLog(sourceLine, ...) [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCOREAnalysisFile function:__PRETTY_FUNCTION__ line:sourceLine tag:nil format:__VA_ARGS__]

static void JCOREParseHostCallBack(CFHostRef host, CFHostInfoType type, const CFStreamError *error, void *context) {
    JCOREAddressAnalysisController *controller = (__bridge id)context;
    if (![controller isKindOfClass:JCOREAddressAnalysisController.class]) return;
    if (error->error == 0) controller.parseHostState = 1;
    else {
        [JCORELog log:NO level:0x3ff flag:1 context:0 file:JCOREAnalysisFile function:"void JCOREParseHostCallBack(CFHostRef, CFHostInfoType, const CFStreamError *, void *)" line:60 tag:nil format:@"DNS request failed"];
        controller.parseHostState = -1;
    }
}

@implementation JCOREAddressAnalysisController
@dynamic hash, superclass, description, debugDescription;
@synthesize parseHostState = _parseHostState, dataList = _dataList, dnsResults = _dnsResults;
@synthesize srvResults = _srvResults, wifiResults = _wifiResults, currentThread = _currentThread, resolver = _resolver;
+ (instancetype)instance {
    static JCOREAddressAnalysisController *controller;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ controller = [[JCOREAddressAnalysisController alloc] init]; });
    return controller;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        self.currentThread = NSThread.currentThread;
        [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(applicationWillResignActive:) name:UIApplicationWillResignActiveNotification object:nil];
        [self restoreDataFromFile];
    }
    return self;
}
- (void)dealloc {
    [self removeResolver];
    [NSNotificationCenter.defaultCenter removeObserver:self];
}
- (void)addValue:(NSDictionary *)values {
    JCOREAddressAnalysis *analysis = [self findAnalysisObject:values];
    if (![analysis isKindOfClass:JCOREAddressAnalysis.class]) return;
    for (NSString *key in values.allKeys) {
        id value = values[key];
        if (value) [analysis setValue:value forKey:key];
    }
    [self updateObject:analysis];
}
- (void)addAddressAnalysis:(JCOREAddressAnalysis *)analysis {
    if ([[self findObjectWithAnalysis:analysis] isKindOfClass:JCOREAddressAnalysis.class]) [self updateObject:analysis];
    else [self addObject:analysis];
}
- (NSArray *)addressListForType:(NSString *)type statusCode:(long long)status within:(double)interval {
    if (![type isKindOfClass:NSString.class] || !type.length) type = JCOREAddressAnalysisDefaultType;
    [self sortList];
    NSMutableArray *addresses = [NSMutableArray array];
    for (JCOREAddressAnalysis *analysis in self.dataList) {
        if ([analysis.type isEqualToString:type] && analysis.costTime <= interval && analysis.statusCode >= status) [addresses addObject:analysis.address];
    }
    return addresses.count ? [addresses copy] : nil;
}
- (NSArray *)addressListForType:(NSString *)type within:(double)interval {
    if (![type isKindOfClass:NSString.class] || !type.length) type = JCOREAddressAnalysisDefaultType;
    [self sortList];
    NSMutableArray *addresses = [[NSMutableArray alloc] init];
    for (JCOREAddressAnalysis *analysis in self.dataList) {
        if ([analysis.type isEqualToString:type] && analysis.costTime <= interval) [addresses addObject:analysis.address];
    }
    return addresses.count ? addresses : nil;
}
- (void)reset:(JCOREAddressAnalysis *)analysis {
    analysis.costTime = 86400;
    analysis.statusCode = -1;
    analysis.netType = [JCOREUtilities ctGeneration];
}
- (JCOREAddressAnalysis *)newObjectWith:(NSString *)address {
    JCOREAddressAnalysis *analysis = [[JCOREAddressAnalysis alloc] init];
    analysis.address = address;
    [self reset:analysis];
    return analysis;
}
- (void)addObject:(JCOREAddressAnalysis *)analysis {
    if (!analysis.time_type_Dic.count) return;
    NSMutableArray *array = [NSMutableArray arrayWithArray:self.dataList];
    [array addObject:analysis];
    self.dataList = [array copy];
}
- (long long)findIndex:(NSDictionary *)values {
    __block long long found = -1;
    NSString *type = values[JCOREAddressAnalysisType];
    NSString *address = values[JCOREAddressAnalysisAddress];
    if ([type isKindOfClass:NSString.class] && [address isKindOfClass:NSString.class]) {
        [[self.dataList copy] enumerateObjectsUsingBlock:^(JCOREAddressAnalysis *analysis, NSUInteger index, BOOL *stop) {
            if ([analysis.type isEqualToString:type] && [analysis.address isEqualToString:address]) {
                found = (long long)index;
                *stop = YES;
            }
        }];
    }
    return found;
}
- (JCOREAddressAnalysis *)findObjectWithAnalysis:(JCOREAddressAnalysis *)analysis { return [self findAnalysisObject:analysis.json]; }
- (JCOREAddressAnalysis *)findAnalysisObject:(NSDictionary *)values {
    long long index = [self findIndex:values];
    return index >= 0 && (unsigned long long)index < self.dataList.count ? self.dataList[(NSUInteger)index] : nil;
}
- (void)updateObject:(JCOREAddressAnalysis *)analysis {
    NSMutableArray *array = [NSMutableArray arrayWithArray:self.dataList];
    JCOREAddressAnalysis *old = [self findObjectWithAnalysis:analysis];
    NSMutableDictionary *times = [NSMutableDictionary dictionary];
    [times addEntriesFromDictionary:analysis.time_type_Dic];
    [times addEntriesFromDictionary:old.time_type_Dic];
    analysis.time_type_Dic = [times copy];
    [array removeObject:old];
    [array addObject:analysis];
    self.dataList = [array copy];
}
- (void)sortList {
    if (![self.dataList isKindOfClass:NSArray.class]) return;
    double now = [JCOREUtilities currentTimestamp];
    NSMutableArray *status3 = [NSMutableArray array], *status2 = [NSMutableArray array];
    NSMutableArray *status1 = [NSMutableArray array], *status0 = [NSMutableArray array];
    for (JCOREAddressAnalysis *analysis in self.dataList) {
        if (now - analysis.requestTime <= analysis.time_expire) {
            switch (analysis.statusCode) {
                case 3: [status3 addObject:analysis]; break;
                case 2: [status2 addObject:analysis]; break;
                case 1: [status1 addObject:analysis]; break;
                case 0: [status0 addObject:analysis]; break;
            }
        }
    }
    NSSortDescriptor *recent = [NSSortDescriptor sortDescriptorWithKey:@"requestTime" ascending:NO];
    [status3 addObjectsFromArray:status2];
    NSMutableArray *sorted = [[status3 sortedArrayUsingDescriptors:@[recent, [NSSortDescriptor sortDescriptorWithKey:@"statusCode" ascending:NO], [NSSortDescriptor sortDescriptorWithKey:@"costTime" ascending:YES]]] mutableCopy];
    [sorted addObjectsFromArray:[status1 sortedArrayUsingDescriptors:@[recent]]];
    [sorted addObjectsFromArray:[status0 sortedArrayUsingDescriptors:@[recent]]];
    self.dataList = [sorted copy];
    // The original computes this range without assigning the returned array.
    if (self.dataList.count > 20) (void)[self.dataList subarrayWithRange:NSMakeRange(0, 20)];
}
- (NSDictionary *)getAddressFromDNS:(NSString *)host timeout:(double)timeout {
    if (![host isKindOfClass:NSString.class] || !host.length) return nil;
    if (timeout < 0) timeout = 2;
    JCOREAnalysisLog(333, @"get Address from DNS Module, domian:%@", host);
    BOOL verified = [JCOREIpvxHelper instance].isVerified;
    JCOREDNSResult *cached = [self getDNSCacheWithDomain:host];
    JCOREAnalysisLog(338, @"DNS cache:%@, domain:%@", cached.ipDic, host);
    if (cached && verified && [JCOREUtilities currentTimestamp] - cached.requestTime <= cached.time_fresh) {
        long long type = [JCOREIpvxHelper instance].ipvxType;
        if ((type == 1 || type == 3) && cached.ipDic[JCORE_IPV4_Key]) {
            JCOREAnalysisLog(349, @"use cache:%@,return", cached.ipDic);
            return cached.ipDic;
        }
        if ((type == 2 || type == 4) && cached.ipDic[JCORE_IPV6_Key]) {
            JCOREAnalysisLog(355, @"use cache:%@,return", cached.ipDic);
            return cached.ipDic;
        }
        if (type < 1 || type > 4) {
            JCOREAnalysisLog(359, @"use cache:%@,return", cached.ipDic);
            return cached.ipDic;
        }
    }
    NSArray *parsed = [[self parseWithHost:host timeout:timeout] copy];
    NSMutableArray *valid = [NSMutableArray array], *ipv4 = [NSMutableArray array], *ipv6 = [NSMutableArray array];
    for (NSString *address in parsed) if ([JCOREUtilities checkIPAddress:address]) [valid addObject:address];
    JCOREAnalysisLog(373, @"DNS Real-time result:%@, domain:%@", valid.description, host);
    for (NSString *address in [valid copy]) {
        if (![JCOREUtilities checkIPAddress:address]) continue;
        if ([JCOREAddress isIpv4:address]) [ipv4 addObject:address];
        else [ipv6 addObject:address];
    }
    if (ipv4.count && ipv6.count) [[JCOREIpvxHelper instance] configNetworkIpvxType:5];
    else if (ipv4.count) [[JCOREIpvxHelper instance] configNetworkIpvxType:1];
    else if (ipv6.count) [[JCOREIpvxHelper instance] configNetworkIpvxType:2];
    if (ipv4.count > 3) ipv4 = [[ipv4 subarrayWithRange:NSMakeRange(0, 3)] mutableCopy];
    if (ipv6.count > 3) ipv6 = [[ipv6 subarrayWithRange:NSMakeRange(0, 3)] mutableCopy];
    NSMutableDictionary *addresses = [NSMutableDictionary dictionary];
    [addresses addEntriesFromDictionary:cached.ipDic];
    if (ipv6.count) addresses[JCORE_IPV6_Key] = ipv6;
    if (ipv4.count) addresses[JCORE_IPV4_Key] = ipv4;
    if (!valid.count) {
        JCOREAnalysisLog(427, @"use DNS cache:%@, domain:%@, return", cached.ipDic, host);
        return cached.ipDic;
    }
    JCOREDNSResult *result = [[JCOREDNSResult alloc] init];
    result.ipDic = [addresses copy];
    result.domain = host;
    result.requestTime = [JCOREUtilities currentTimestamp];
    [self addDNSCache:result];
    JCOREAnalysisLog(424, @"use DNS Real-time request:%@, domain:%@, return", result.ipDic, host);
    return result.ipDic;
}
- (JCOREDNSResult *)getDNSCacheWithDomain:(NSString *)domain {
    if (!domain || !self.dnsResults) return nil;
    JCOREDNSResult *result = self.dnsResults[domain];
    if (!result) return nil;
    if ([JCOREUtilities currentTimestamp] - result.requestTime <= result.time_expire) return result;
    [self removeDNSCache:result];
    return nil;
}
- (void)addDNSCache:(JCOREDNSResult *)result {
    if (!result) return;
    NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
    if (self.dnsResults) [dictionary addEntriesFromDictionary:self.dnsResults];
    dictionary[result.domain] = result;
    self.dnsResults = [dictionary copy];
}
- (void)removeDNSCache:(JCOREDNSResult *)result {
    if (!result) return;
    // Removal deliberately starts with an empty map in the original SDK.
    NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
    [dictionary removeObjectForKey:result.domain];
    self.dnsResults = [dictionary copy];
}
- (void)removeDNSCacheWithDomain:(NSString *)domain {
    if (![domain isKindOfClass:NSString.class] || !domain.length) return;
    JCOREDNSResult *result = [self getDNSCacheWithDomain:domain];
    if (result) [self removeDNSCache:result];
}
- (NSArray *)parseWithHost:(NSString *)host timeout:(double)timeout {
    if (![host isKindOfClass:NSString.class] || !host.length) return nil;
    CFStringRef name = CFStringCreateWithCString(kCFAllocatorDefault, host.UTF8String, kCFStringEncodingUTF8);
    if (!name) return nil;
    CFHostRef reference = CFHostCreateWithName(kCFAllocatorDefault, name);
    CFRelease(name);
    if (!reference) return nil;
    NSString *monitor = [JCORESupersetManager startConnectionMoniter:@"NET_DNS" host:host port:0];
    CFHostClientContext context = {0, (__bridge void *)self, NULL, NULL, NULL};
    CFHostSetClient(reference, JCOREParseHostCallBack, &context);
    CFHostScheduleWithRunLoop(reference, NSRunLoop.currentRunLoop.getCFRunLoop, kCFRunLoopDefaultMode);
    self.parseHostState = 0;
    CFStreamError error;
    CFHostStartInfoResolution(reference, kCFHostAddresses, &error);
    NSDate *deadline = [NSDate dateWithTimeIntervalSinceNow:timeout];
    @autoreleasepool {
        do {
            [NSRunLoop.currentRunLoop runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.2]];
            if (self.parseHostState != 0) break;
        } while ([NSDate.date compare:deadline] < 0);
    }
    if (self.parseHostState == 1) {
        Boolean resolved = false;
        CFArrayRef records = CFHostGetAddressing(reference, &resolved);
        if (records && resolved && CFArrayGetCount(records) > 0) {
            [JCORESupersetManager endConnectionMoniter:monitor error_code:0 status_code:0];
            NSMutableArray *addresses = [[NSMutableArray alloc] init];
            for (CFIndex index = 0; index < CFArrayGetCount(records); index++) {
                const struct sockaddr *address = (const void *)CFDataGetBytePtr(CFArrayGetValueAtIndex(records, index));
                if (!address) continue;
                if (address->sa_family == AF_INET) {
                    const char *text = inet_ntoa(((const struct sockaddr_in *)address)->sin_addr);
                    if (text) {
                        NSString *value = [NSString stringWithCString:text encoding:NSASCIIStringEncoding];
                        if ([value rangeOfString:@"."].length && value.length > 7) [addresses addObject:value];
                    }
                }
                if (address->sa_family == AF_INET6) {
                    char buffer[INET6_ADDRSTRLEN];
                    const char *text = inet_ntop(AF_INET6, &((const struct sockaddr_in6 *)address)->sin6_addr, buffer, sizeof(buffer));
                    if (text) {
                        NSString *value = [NSString stringWithCString:text encoding:NSASCIIStringEncoding];
                        if ([value rangeOfString:@":"].length && value.length > 10) {
                            [addresses addObject:value];
                            NSArray *parts = [value componentsSeparatedByString:@":"];
                            NSString *prefix = @"";
                            for (NSUInteger part = 0; part < parts.count; part++) {
                                if (part < parts.count - 2) {
                                    prefix = [prefix stringByAppendingFormat:@"%@:", parts[part]];
                                    if (prefix.length) [JCOREIpvxHelper instance].ipv6Pre = prefix;
                                }
                            }
                        }
                    }
                }
            }
            CFRelease(reference);
            return addresses.count ? addresses : nil;
        }
        [JCORESupersetManager endConnectionMoniter:monitor error_code:-1 status_code:0];
        CFRelease(reference);
    } else {
        CFRelease(reference);
        [JCORESupersetManager endConnectionMoniter:monitor error_code:-1 status_code:0];
    }
    return nil;
}
- (void)removeResolver { if (self.resolver) { [self.resolver stop]; _resolver = nil; } }
- (NSArray *)runWithSRVName:(NSString *)name timeout:(double)timeout {
    if (!name) return nil;
    if (name) {
        [self removeResolver];
        if (!_resolver) {
            _resolver = [[JCORESRVResolver alloc] initWithSRVName:name timeout:timeout];
            _resolver.delegate = self;
        }
        [self.resolver start];
    }
    while (!self.resolver.isFinished) [NSRunLoop.currentRunLoop runMode:NSDefaultRunLoopMode beforeDate:[NSDate dateWithTimeIntervalSinceNow:0.2]];
    if (self.resolver.error) return nil;
    return self.resolver.results.count ? self.resolver.results : nil;
}
- (void)removeSrvRecode { [self removeResolver]; self.srvResults = nil; }
- (NSArray *)getAddressFromSRV:(NSString *)name timeout:(double)timeout {
    if (![name isKindOfClass:NSString.class] || !name.length) return nil;
    if (timeout < 0) timeout = 1;
    JCOREAnalysisLog(645, @"get address from srv module, domain:%@", name);
    JCORESRVResult *cached = [self getSRVCacheWithDomain:name];
    JCOREAnalysisLog(647, @"srv cache:%@, domain:%@", cached, name);
    if (cached && [JCOREUtilities currentTimestamp] - cached.requestTime <= cached.time_fresh) {
        JCOREAnalysisLog(653, @"use srv fresh cache:%@, return", cached);
        return cached.ips;
    }
    NSArray *addresses = [self runWithSRVName:name timeout:timeout];
    JCOREAnalysisLog(660, @"srv real-time result:%@, domain:%@", addresses, name);
    if (addresses.count) {
        JCORESRVResult *result = [[JCORESRVResult alloc] init];
        result.ips = addresses;
        result.domain = name;
        result.requestTime = [JCOREUtilities currentTimestamp];
        [self addSRVCache:result];
        JCOREAnalysisLog(667, @"use srv real-time result:%@, domain:%@, return", addresses, name);
        return addresses;
    }
    NSArray *fallback = cached.ips;
    JCOREAnalysisLog(670, @"use srv valid cache:%@, domain:%@, return", fallback, name);
    return fallback;
}
- (JCORESRVResult *)getSRVCacheWithDomain:(NSString *)domain {
    if (!domain || !self.srvResults) return nil;
    JCORESRVResult *result = self.srvResults[domain];
    if (!result) return nil;
    if ([JCOREUtilities currentTimestamp] - result.requestTime <= result.time_expire) return result;
    [self removeSRVCache:result];
    return nil;
}
- (void)addSRVCache:(JCORESRVResult *)result {
    if (!result) return;
    NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
    if (self.srvResults) [dictionary addEntriesFromDictionary:self.srvResults];
    dictionary[result.domain] = result;
    self.srvResults = [dictionary copy];
}
- (void)removeSRVCache:(JCORESRVResult *)result {
    if (!result) return;
    NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
    [dictionary removeObjectForKey:result.domain];
    self.srvResults = [dictionary copy];
}
- (JCOREWifiCacheModel *)getCacheWithWifiInfo:(JCOREWifiCacheModel *)info {
    if (!info || !self.wifiResults) return nil;
    JCOREAnalysisLog(725, @"get wifi cache wifiInfo:%@", info.description);
    JCOREWifiCacheModel *cached = self.wifiResults[info.mac_address];
    JCOREAnalysisLog(727, @"wifi cache:%@", cached.description);
    if (!cached) { JCOREAnalysisLog(729, @"no wifi cache, return"); return nil; }
    if (cached.time_expire < [JCOREUtilities currentTimestamp] - cached.cacheTime) {
        [self removeWifiCache:cached];
        JCOREAnalysisLog(737, @"wifi cache expire, return");
        return nil;
    }
    if ([cached.ssid isEqualToString:info.ssid]) {
        JCOREAnalysisLog(745, @"use wifi cache:%@", cached.description);
        return cached;
    }
    JCOREAnalysisLog(742, @"ssid is not equal, return");
    return nil;
}
- (void)addWifiCache:(JCOREWifiCacheModel *)info {
    if (!info) return;
    NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
    if (self.wifiResults) [dictionary addEntriesFromDictionary:self.wifiResults];
    dictionary[info.mac_address] = info;
    self.wifiResults = [dictionary copy];
}
- (void)removeWifiCache:(JCOREWifiCacheModel *)info {
    if (!info) return;
    NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
    [dictionary removeObjectForKey:info.mac_address];
    self.wifiResults = [dictionary copy];
}
- (void)applicationWillResignActive:(NSNotification *)notification {
    if (self.currentThread) [self performSelector:@selector(saveDataToFile) onThread:self.currentThread withObject:nil waitUntilDone:NO];
}
- (void)saveDataToFile {
    NSMutableDictionary *payload = [[NSMutableDictionary alloc] init];
    if ([self.dataList isKindOfClass:NSArray.class] && self.dataList.count) payload[@"jcore_analysis_data"] = self.dataList;
    if ([self.dnsResults isKindOfClass:NSDictionary.class] && self.dnsResults.count) payload[@"jcore_dns_results"] = self.dnsResults;
    if ([self.srvResults isKindOfClass:NSDictionary.class] && self.srvResults.count) payload[@"jcore_srv_results"] = self.srvResults;
    if ([self.wifiResults isKindOfClass:NSDictionary.class] && self.wifiResults.count) payload[@"jcore_wifi_cache"] = self.wifiResults;
    if (payload.count) {
        @try {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
            [NSKeyedArchiver archiveRootObject:[payload copy] toFile:self.filePath];
#pragma clang diagnostic pop
        } @catch (NSException *exception) {
            NSLog(@"[归档错误] saveDataToFile");
        }
    }
}
- (void)restoreDataFromFile {
    id payload;
    @try {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
        payload = [NSKeyedUnarchiver unarchiveObjectWithFile:self.filePath];
#pragma clang diagnostic pop
    } @catch (NSException *exception) {
        [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCOREAnalysisFile function:"-[JCOREAddressAnalysisController restoreDataFromFile]" line:807 tag:nil format:@"[解档错误] restoreDataFromFile"];
    }
    if ([payload isKindOfClass:NSDictionary.class]) {
        [self restoreDnsResults:payload[@"jcore_dns_results"]];
        [self restoreAnalysisDataList:payload[@"jcore_analysis_data"]];
        [self restoreSrvResults:payload[@"jcore_srv_results"]];
        [self restoreWifiCache:payload[@"jcore_wifi_cache"]];
    }
}
- (void)restoreDnsResults:(NSDictionary *)results {
    if (![results isKindOfClass:NSDictionary.class] || !results.count) return;
    double now = [JCOREUtilities currentTimestamp];
    NSMutableDictionary *valid = [[NSMutableDictionary alloc] init];
    for (JCOREDNSResult *result in results.allValues) if (now - result.requestTime <= result.time_expire) valid[result.domain] = result;
    if (valid.count) self.dnsResults = [valid copy];
}
- (void)restoreAnalysisDataList:(NSArray *)results {
    if (![results isKindOfClass:NSArray.class] || !results.count) return;
    double now = [JCOREUtilities currentTimestamp];
    NSMutableArray *valid = [[NSMutableArray alloc] init];
    for (JCOREAddressAnalysis *result in results) if (now - result.requestTime <= result.time_expire) [valid addObject:result];
    if (valid.count) self.dataList = [valid copy];
}
- (void)restoreSrvResults:(NSDictionary *)results {
    if (![results isKindOfClass:NSDictionary.class] || !results.count) return;
    double now = [JCOREUtilities currentTimestamp];
    NSMutableDictionary *valid = [[NSMutableDictionary alloc] init];
    for (JCORESRVResult *result in results.allValues) if (now - result.requestTime <= result.time_expire) valid[result.domain] = result;
    if (valid.count) self.srvResults = [valid copy];
}
- (void)restoreWifiCache:(NSDictionary *)results {
    if (![results isKindOfClass:NSDictionary.class] || !results.count) return;
    double now = [JCOREUtilities currentTimestamp];
    NSMutableDictionary *valid = [[NSMutableDictionary alloc] init];
    for (JCOREWifiCacheModel *result in results.allValues) if (now - result.cacheTime <= result.time_expire) valid[result.mac_address] = result;
    if (valid.count) self.wifiResults = [valid copy];
}
- (NSString *)filePath {
    NSString *directory = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES)[0] stringByAppendingPathComponent:@"JCORE"];
    if (![NSFileManager.defaultManager fileExistsAtPath:directory]) [NSFileManager.defaultManager createDirectoryAtPath:directory withIntermediateDirectories:YES attributes:nil error:NULL];
    return [directory stringByAppendingPathComponent:@"JCOREAnalysis"];
}
- (NSArray *)arrayWithType:(NSString *)type array:(NSArray *)array {
    if (!array.count) return nil;
    NSMutableArray *selected = [NSMutableArray array];
    [array enumerateObjectsUsingBlock:^(JCOREAddressAnalysis *analysis, NSUInteger index, BOOL *stop) {
        [analysis.time_type_Dic enumerateKeysAndObjectsUsingBlock:^(NSString *key, id value, BOOL *innerStop) {
            if ([key isEqualToString:type]) [selected addObject:analysis];
        }];
    }];
    return [selected copy];
}
- (NSArray *)arrayWithSisResult:(NSArray *)array {
    if (!array.count) return nil;
    NSMutableArray *selected = [NSMutableArray array];
    [array enumerateObjectsUsingBlock:^(JCOREAddressAnalysis *analysis, NSUInteger index, BOOL *stop) {
        [analysis.time_type_Dic enumerateKeysAndObjectsUsingBlock:^(NSString *key, id value, BOOL *innerStop) {
            if ([key isEqualToString:JCOREAddressAnalysisType_sis_defaultSisIp] || [key isEqualToString:JCOREAddressAnalysisType_sis_lastResult] || [key isEqualToString:JCOREAddressAnalysisType_sis_defaultDomain] || [key isEqualToString:JCOREAddressAnalysisType_sis_lastSuccess] || [key isEqualToString:JCOREAddressAnalysisType_sis_srvRecord]) [selected addObject:analysis];
        }];
    }];
    return [selected copy];
}
- (double)successRateWithArray:(NSArray *)array type:(NSString *)type {
    NSArray *selected = [type isEqualToString:JCOREAddressAnalysisType_sisResult] ? [self arrayWithSisResult:array] : [self arrayWithType:type array:array];
    if (!selected.count) return 1;
    __block float successful = 0;
    [selected enumerateObjectsUsingBlock:^(JCOREAddressAnalysis *analysis, NSUInteger index, BOOL *stop) { if (analysis.statusCode > 1) successful += 1.0f; }];
    return successful / (float)selected.count;
}
- (NSArray *)arrayInFixedTime:(double)interval {
    double now = [JCOREUtilities currentTimestamp];
    NSMutableArray *selected = [NSMutableArray array];
    [[self.dataList copy] enumerateObjectsUsingBlock:^(JCOREAddressAnalysis *analysis, NSUInteger index, BOOL *stop) {
        if (now - analysis.requestTime <= interval) {
            JCOREAddressAnalysis *copy = [analysis copy];
            NSMutableDictionary *times = [NSMutableDictionary dictionary];
            [analysis.time_type_Dic enumerateKeysAndObjectsUsingBlock:^(NSString *key, id value, BOOL *innerStop) {
                if (now - [value doubleValue] <= interval) times[key] = value;
            }];
            copy.time_type_Dic = times;
            [selected addObject:copy];
        }
    }];
    return [selected copy];
}
@end

@implementation NSObject (JCOREAddressAnalysis)
- (void)jcoreAAAddValue:(NSDictionary *)values { [[JCOREAddressAnalysisController instance] addValue:values]; }
- (void)addAddressAnalysis:(JCOREAddressAnalysis *)analysis { [[JCOREAddressAnalysisController instance] addAddressAnalysis:analysis]; }
@end
@implementation NSString (JCOREAddressAnalysisType)
- (NSArray *)jcoreAAAddressListWithin:(double)interval { return [[JCOREAddressAnalysisController instance] addressListForType:self within:interval]; }
@end
