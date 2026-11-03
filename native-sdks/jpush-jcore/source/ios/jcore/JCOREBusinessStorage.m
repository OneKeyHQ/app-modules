#import "JCOREBusinessStorage.h"
#import "RecoveredCache.h"
#import "JCORELog.h"

#define StorageFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Business/Cache/JCOREBusinessStorage.m"
@interface JCOREIntegrate : NSObject
+ (id)sdkVersionOfIndex:(NSUInteger)index;
@end
@interface JCORETestAddressCache : NSObject
+ (BOOL)isDebugMode;
+ (instancetype)shared;
- (id)defaultUserReportAddress;
- (id)defaultReportAddress;
@end

@implementation JCOREBusinessStorage
+ (void)setServerCerVesion:(id)arg1 {
    if (!arg1 || ![arg1 isKindOfClass:[NSNumber class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setServerCerVesion:]" line:47 tag:nil format:@"%@", @"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"kServerSSLCertVersion"];
}
+ (id)getSeverCerVerion { return [JCORECacheUtilities persistentObjectForKey:@"kServerSSLCertVersion"]; }
+ (void)setLocalCerVerison:(id)arg1 {
    if (!arg1 || ![arg1 isKindOfClass:[NSNumber class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setLocalCerVerison:]" line:59 tag:nil format:@"%@", @"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"kLocalSSLCertVersion"];
}
+ (id)getLocalCerVerion { return [JCORECacheUtilities persistentObjectForKey:@"kLocalSSLCertVersion"]; }
+ (void)setLocalCert:(id)arg1 {
    if (!arg1 || ![arg1 isKindOfClass:[NSString class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setLocalCert:]" line:71 tag:nil format:@"%@", @"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"kLocalSSLCert"];
}
+ (id)getLocalCert { return [JCORECacheUtilities persistentObjectForKey:@"kLocalSSLCert"]; }
+ (void)setSisReportList:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSArray class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setSisReportList:]" line:83 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"SisReportList"];
}
+ (id)getSisReportList { return [JCORECacheUtilities persistentObjectForKey:@"SisReportList"]; }
+ (void)setLastSisReportTime:(id)arg1 { [JCORECacheUtilities setPersistentObject:arg1 forKey:@"LastSisReportTime"]; }
+ (id)getLastSisReportTime { return [JCORECacheUtilities persistentObjectForKey:@"LastSisReportTime"]; }
+ (void)setLastSuccessSisIps:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSArray class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setLastSuccessSisIps:]" line:102 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"LastSuccessSisIp"];
}
+ (id)getLastSuccessSisIps { return [JCORECacheUtilities persistentObjectForKey:@"LastSuccessSisIp"]; }
+ (void)setDefaultConAddress:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSString class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setDefaultConAddress:]" line:113 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"DefaultConAddress"];
}
+ (id)getDefaultConAddress { return [JCORECacheUtilities persistentObjectForKey:@"DefaultConAddress"]; }
+ (void)setSisIpsFromServer:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSArray class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setSisIpsFromServer:]" line:124 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"SisIpsFromServer"];
}
+ (id)getSisIpsFromServer { return [JCORECacheUtilities persistentObjectForKey:@"SisIpsFromServer"]; }
+ (void)setReportSisAddress:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSString class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setReportSisAddress:]" line:136 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"ReportSisAddress"];
}
+ (id)getReportSisAddress { return [JCORECacheUtilities persistentObjectForKey:@"ReportSisAddress"]; }
+ (void)setHttpSisAddressList:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSArray class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setHttpSisAddressList:]" line:147 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"HttpSisAddressList"];
}
+ (id)getHttpSisAddressList { return [JCORECacheUtilities persistentObjectForKey:@"HttpSisAddressList"]; }
+ (void)setReportAddressDict:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSDictionary class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setReportAddressDict:]" line:158 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"ReportAddressDict"];
}
+ (id)getReportAddressDict { return [JCORECacheUtilities persistentObjectForKey:@"ReportAddressDict"]; }
+ (void)setTcpReportAddressDict:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSDictionary class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setTcpReportAddressDict:]" line:169 tag:nil format:@"JCOREBusinessStorage tcp report address error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"TcpReportAddressDict"];
}
+ (id)getTcpReportAddressDict { return [JCORECacheUtilities persistentObjectForKey:@"TcpReportAddressDict"]; }
+ (void)setNextSisRequestTime:(double)arg1 { [JCORECacheUtilities setPersistentObject:@(arg1) forKey:@"NextSisRequestTime"]; }
+ (double)getNextSisRequestTime { return [[JCORECacheUtilities persistentObjectForKey:@"NextSisRequestTime"] doubleValue]; }
+ (void)setLastSucceedConnectionAddress:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSArray class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setLastSucceedConnectionAddress:]" line:192 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"LastSucceedConnectionAddress"];
}
+ (id)getLastSucceedConnectionAddress { return [JCORECacheUtilities persistentObjectForKey:@"LastSucceedConnectionAddress"]; }
+ (void)setDefaultSRVRecord:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSString class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setDefaultSRVRecord:]" line:203 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"DefaultSRVRecord"];
}
+ (id)getDefaultSRVRecord { return [JCORECacheUtilities persistentObjectForKey:@"DefaultSRVRecord"]; }
+ (void)setDefaultConnHost:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSString class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setDefaultConnHost:]" line:214 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"DefaultConnHost"];
}
+ (id)getDefaultConnHost { return [JCORECacheUtilities persistentObjectForKey:@"DefaultConnHost"]; }
+ (void)setFailedConIPs:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSArray class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setFailedConIPs:]" line:225 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"FailedConIps"];
}
+ (id)getFailedConIps { return [JCORECacheUtilities persistentObjectForKey:@"FailedConIps"]; }
+ (void)setLocation:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSDictionary class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setLocation:]" line:252 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"Location"];
}
+ (id)getLocation { return [JCORECacheUtilities persistentObjectForKey:@"Location"]; }
+ (void)setNeedDataReport:(BOOL)arg1 { [JCORECacheUtilities setPersistentObject:@(arg1) forKey:@"NeedDataReport"]; }
+ (BOOL)getNeedDataReport { return [[JCORECacheUtilities persistentObjectForKey:@"NeedDataReport"] boolValue]; }
+ (void)setGuaranteeHttpReportAddress:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSArray class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setGuaranteeHttpReportAddress:]" line:271 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"GuaranteeHttpReportAddress"];
}
+ (id)getGuaranteeHttpReportAddress { return [JCORECacheUtilities persistentObjectForKey:@"GuaranteeHttpReportAddress"]; }
+ (void)setGuaranteeHttpsReportAddress:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSArray class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setGuaranteeHttpsReportAddress:]" line:282 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"GuaranteeHttpsReportAddress"];
}
+ (id)getGuaranteeHttpsReportAddress { return [JCORECacheUtilities persistentObjectForKey:@"GuaranteeHttpsReportAddress"]; }
+ (void)setGuaranteeUserReportAddress:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSArray class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setGuaranteeUserReportAddress:]" line:294 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"GuaranteeUserReportAddress"];
}
+ (id)getGuaranteeUserReportAddress { return [JCORECacheUtilities persistentObjectForKey:@"GuaranteeUserReportAddress"]; }
+ (void)setGuaranteeSSLUserReportAddress:(id)arg1 {
    if (arg1 && ![arg1 isKindOfClass:[NSArray class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage setGuaranteeSSLUserReportAddress:]" line:305 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    [JCORECacheUtilities setPersistentObject:arg1 forKey:@"GuaranteeSSLUserReportAddress"];
}
+ (id)getGuaranteeSSLUserReportAddress { return [JCORECacheUtilities persistentObjectForKey:@"GuaranteeSSLUserReportAddress"]; }
+ (void)addFailedConIPs:(id)value {
    if (value && ![value isKindOfClass:[NSArray class]]) { [JCORELog log:NO level:0x3ff flag:0x20 context:0 file:StorageFile function:"+[JCOREBusinessStorage addFailedConIPs:]" line:232 tag:nil format:@"JCOREBusinessStorage error"]; return; }
    if ([value count]) { NSMutableArray *addresses = [NSMutableArray array]; id previous = [self getFailedConIps]; if ([previous count]) [addresses addObjectsFromArray:previous]; [addresses addObjectsFromArray:value]; [self setFailedConIPs:[addresses copy]]; }
}
+ (id)getUserAddress {
    id version = [JCOREIntegrate sdkVersionOfIndex:0]; NSArray *addresses = (!version || [version integerValue] < 50400) ? @[@"https://user.jpush.cn/v1/user/", @"https://user-ipv6.jpush.cn/v1/user/"] : @[@"https://user.jpush.cn/secret/user/", @"https://user-ipv6.jpush.cn/secret/user/"];
    if ([JCORETestAddressCache isDebugMode]) { id custom = [[JCORETestAddressCache shared] defaultUserReportAddress]; if ([custom isKindOfClass:[NSArray class]] && [custom count]) addresses = custom; }
    return addresses;
}
+ (id)getReportAddress {
    NSArray *addresses = @[@"https://stats.jpush.cn/v3/report", @"https://stats-ipv6.jpush.cn/v3/report"];
    if ([JCORETestAddressCache isDebugMode]) { id custom = [[JCORETestAddressCache shared] defaultReportAddress]; if ([custom isKindOfClass:[NSString class]] && [custom length]) addresses = @[custom]; }
    return addresses;
}
+ (unsigned short)getConnectPort { id value = [[NSUserDefaults standardUserDefaults] objectForKey:@"kJCoreConnectPort"]; return [value isKindOfClass:[NSNumber class]] ? [value unsignedShortValue] : 7000; }
+ (id)getConnectAddress { id value = [[NSUserDefaults standardUserDefaults] stringForKey:@"kJCoreConnectAddress"]; return [value isKindOfClass:[NSString class]] ? [value copy] : @"113.31.17.106"; }
+ (void)setVerified:(BOOL)value { [[NSUserDefaults standardUserDefaults] setBool:value forKey:@"kJCoreVerified"]; }
+ (BOOL)getVerified { return YES; }
@end
