#import "JCORETestAddressCache.h"

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-property-ivar"
// Original getters consult QA defaults; corresponding setters only keep private copies.
@implementation JCORETestAddressCache
@synthesize sisHostArray = _sisHostArray, sisIpArray = _sisIpArray, reportSisAddress = _reportSisAddress, defaultConHost = _defaultConHost, defaultConnSrv = _defaultConnSrv, defaultSisSrv = _defaultSisSrv, defaultReportAddress = _defaultReportAddress, defaultUserReportAddress = _defaultUserReportAddress, defaultWhiteListArray = _defaultWhiteListArray;
+ (instancetype)shared {
    static JCORETestAddressCache *cache;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ cache = [[JCORETestAddressCache alloc] init]; });
    return cache;
}
+ (BOOL)isDebugMode { return [NSUserDefaults.standardUserDefaults boolForKey:@"kJIGUANGIsDebugMode"]; }
- (NSArray *)sisHostArray { return [NSUserDefaults.standardUserDefaults arrayForKey:@"kJIGUANGQATest_SisHostArray"]; }
- (NSArray *)sisIpArray { return [NSUserDefaults.standardUserDefaults arrayForKey:@"kJIGUANGQATest_SisIpArray"]; }
- (NSArray *)reportSisAddress { return [NSUserDefaults.standardUserDefaults arrayForKey:@"kJIGUANGQATest_ReportSisAddress"]; }
- (NSString *)defaultConHost { return [NSUserDefaults.standardUserDefaults stringForKey:@"kJIGUANGQATest_DefaultConnHost"]; }
- (NSString *)defaultConnSrv { return [NSUserDefaults.standardUserDefaults stringForKey:@"kJIGUANGQATest_DefaultConnSrv"]; }
- (NSString *)defaultSisSrv { return [NSUserDefaults.standardUserDefaults stringForKey:@"kJIGUANGQATest_DefaultSisSrv"]; }
- (NSArray *)defaultReportAddress { return [NSUserDefaults.standardUserDefaults arrayForKey:@"kJIGUANGQATest_DefaultReportAddress"]; }
- (NSArray *)defaultUserReportAddress { return [NSUserDefaults.standardUserDefaults arrayForKey:@"kJIGUANGQATest_DefaultUserReportAddress"]; }
- (NSArray *)defaultWhiteListArray { return [NSUserDefaults.standardUserDefaults arrayForKey:@"kJIGUANGQATest_DefaultWhiteListArray"]; }
- (NSString *)defaultLastSucceedConnAddress { return [NSUserDefaults.standardUserDefaults stringForKey:@"kJIGUANGQATest_DefaultLastSucceedConnAddress"]; }
- (void)setDefaultLastSucceedConnAddress:(NSString *)address {
    if (address) [NSUserDefaults.standardUserDefaults setObject:address forKey:@"kJIGUANGQATest_DefaultLastSucceedConnAddress"];
}
@end
#pragma clang diagnostic pop
