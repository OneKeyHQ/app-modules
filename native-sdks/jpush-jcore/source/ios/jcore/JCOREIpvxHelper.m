#import "JCOREIpvxHelper.h"
#import "JCORENetworkReachabilityManager.h"
#import "JCORENetworkModels.h"
#import "JCOREUtilities.h"
#import "JCORELog.h"
#import "JCOREAddressAnalysisController.h"
#include <ifaddrs.h>
#include <net/if.h>
#include <arpa/inet.h>

#define JCOREIpvxFile "/Users/jiguang/.jenkins/workspace/ios-reconsitution/JCore/Library/Analysis/JCOREIpvxHelper.m"
@implementation JCOREIpvxHelper
@synthesize networkIPvxType = _networkIPvxType, staticIpvxType = _staticIpvxType, isVerified = _isVerified, ipv6Pre = _ipv6Pre;
+ (instancetype)instance {
    static JCOREIpvxHelper *helper;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ helper = [[JCOREIpvxHelper alloc] init]; });
    return helper;
}
- (instancetype)init { self = [super init]; if (self) { _isVerified = NO; [self checkIpvxType]; } return self; }
- (void)configStaticIpvxType:(long long)type {
    if ((unsigned long long)type - 1 < 4) { _staticIpvxType = type; if ((unsigned long long)type - 3 < 2) _isVerified = YES; }
}
- (void)configNetworkIpvxType:(long long)type {
    if ((unsigned long long)type - 1 < 5) { _networkIPvxType = type; _isVerified = YES; }
}
- (long long)ipvxType {
    if ((unsigned long long)_staticIpvxType - 3 < 2) return _staticIpvxType;
    long long result = _staticIpvxType ? _staticIpvxType : _networkIPvxType;
    if (_networkIPvxType == 3) result = 1;
    if (_networkIPvxType == 4) result = 2;
    return result;
}
- (void)checkIpvxType {
    if (_isVerified) return;
    long long status = [JCORENetworkReachabilityManager sharedManager].networkReachabilityStatus;
    if (status <= 0) return;
    if (status != 2) { [self checkIpvxTypeByLocalNetwork]; return; }
    NSString *mac = [JCOREUtilities macAddress], *ssid = [JCOREUtilities ssid];
    if (mac.length && ssid.length) {
        JCOREWifiCacheModel *query = [[JCOREWifiCacheModel alloc] initWithMacAddress:mac ssid:ssid ipvxType:1];
        JCOREWifiCacheModel *cached = [[JCOREAddressAnalysisController instance] getCacheWithWifiInfo:query];
        if (!cached) [self checkIpvxTypeByLocalNetwork];
        else { _isVerified = YES; _networkIPvxType = cached.ipvxType; }
    }
}
- (void)checkIpvxTypeByLocalNetwork { _networkIPvxType = [self isIpv6Network] ? 2 : 1; _isVerified = NO; }
- (BOOL)isIpv6Network {
    NSArray *keys = @[@"utun0/ipv6",@"utun0/ipv4",@"en0/ipv6",@"en0/ipv4",@"pdp_ip0/ipv6",@"pdp_ip0/ipv4"];
    NSDictionary *addresses = [self getIPAddresses];
    __block BOOL found = NO;
    [keys enumerateObjectsUsingBlock:^(NSString *key, NSUInteger index, BOOL *stop) {
        if ([key rangeOfString:@"ipv6"].length) {
            NSString *rendered = [NSString stringWithFormat:@"%@", addresses[key]];
            if (![rendered hasPrefix:@"(null)"] && ![addresses[key] hasPrefix:@"fe80"]) found = YES;
        }
    }];
    return found;
}
- (NSMutableDictionary *)getIPAddresses {
    NSMutableDictionary *addresses = [NSMutableDictionary dictionaryWithCapacity:8];
    struct ifaddrs *interfaces = NULL;
    if (getifaddrs(&interfaces) == 0) {
        [JCORELog log:YES level:0x3ff flag:0x100 context:0 file:JCOREIpvxFile function:"-[JCOREIpvxHelper getIPAddresses]" line:178 tag:nil format:@"getifaddrs call"];
        for (struct ifaddrs *entry = interfaces; entry; entry = entry->ifa_next) {
            if (!(entry->ifa_flags & IFF_UP) || !entry->ifa_addr) continue;
            sa_family_t family = entry->ifa_addr->sa_family;
            if (family != AF_INET && family != AF_INET6) continue;
            NSString *name = [NSString stringWithUTF8String:entry->ifa_name];
            char rendered[INET6_ADDRSTRLEN];
            const void *address = family == AF_INET ? (const void *)&((struct sockaddr_in *)entry->ifa_addr)->sin_addr : (const void *)&((struct sockaddr_in6 *)entry->ifa_addr)->sin6_addr;
            if (inet_ntop(family, address, rendered, family == AF_INET ? INET_ADDRSTRLEN : INET6_ADDRSTRLEN)) {
                NSString *key = [NSString stringWithFormat:@"%@/%@", name, family == AF_INET ? @"ipv4" : @"ipv6"];
                addresses[key] = [NSString stringWithUTF8String:rendered];
            }
        }
        freeifaddrs(interfaces);
    }
    return addresses.count ? addresses : nil;
}
- (void)cacheNetworkIpvxType:(long long)type {
    if ((unsigned long long)type - 1 >= 5 || [JCORENetworkReachabilityManager sharedManager].networkReachabilityStatus != 2) return;
    NSString *mac = [JCOREUtilities macAddress], *ssid = [JCOREUtilities ssid];
    if (mac.length && ssid.length) {
        JCOREWifiCacheModel *info = [[JCOREWifiCacheModel alloc] initWithMacAddress:mac ssid:ssid ipvxType:type];
        info.cacheTime = [JCOREUtilities currentTimestamp];
        [[JCOREAddressAnalysisController instance] addWifiCache:info];
    }
}
- (NSString *)convertHostFromIPv4ToIPv6WithHost:(NSString *)host pre:(NSString *)prefix {
    NSArray *parts = [host componentsSeparatedByString:@"."];
    const char *function = "-[JCOREIpvxHelper convertHostFromIPv4ToIPv6WithHost:pre:]";
    if (parts.count < 4) {
        [JCORELog log:YES level:0x3ff flag:0x40 context:0 file:JCOREIpvxFile function:function line:250 tag:nil format:@"ipv4 to ipv6 canceled, need not or param error"];
        return nil;
    }
    if (![prefix isKindOfClass:NSString.class] || !prefix.length) {
        [JCORELog log:YES level:0x3ff flag:0x40 context:0 file:JCOREIpvxFile function:function line:256 tag:nil format:@"ipv4 to ipv6 canceled, prefix invalid"];
        return nil;
    }
    for (NSUInteger i = 0; i < parts.count; i++) {
        NSString *part = [NSString stringWithFormat:@"%02x", [parts[i] intValue]];
        if (i && !(i & 1)) prefix = [prefix stringByAppendingString:@":"];
        prefix = [prefix stringByAppendingString:part];
    }
    return prefix;
}
@end
