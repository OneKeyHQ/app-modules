#import "JCORENetUtilities.h"
#import "RecoveredCache.h"
#import "JCORENetworkReachabilityManager.h"
#import "RecoveredCoreService.h"
#import "JCOREAddress.h"

@implementation JCORENetUtilities
+ (unsigned short)generateRid {
    static unsigned short requestID;
    if (!requestID) {
        id cached = [JCORECacheUtilities cacheObjectForKey:@"__JPUSHSessionID"];
        if (cached) requestID = [cached unsignedShortValue];
    }
    unsigned int candidate = requestID;
    if (!(candidate & 1)) candidate++;
    if ((candidate >> 1) > 0xffc) candidate = 1;
    unsigned short result = (unsigned short)candidate;
    requestID = (unsigned short)(candidate + 1);
    [JCORECacheUtilities setCacheObject:@(requestID) forKey:@"__JPUSHSessionID"];
    return result;
}
+ (BOOL)isViaWifi { return [JCORENetworkReachabilityManager sharedManager].networkReachabilityStatus == 2; }
+ (int)networkingStatus { return (int)[JCORENetworkReachabilityManager sharedManager].networkReachabilityStatus; }
@end

@implementation JCOREIpvPreferedUtilities
+ (id)getPreferedAddresses:(id)addresses {
    if (!addresses || ![addresses isKindOfClass:NSArray.class] || [addresses count] < 2) return addresses;
    NSMutableArray *ipv6 = [NSMutableArray array], *other = [NSMutableArray array];
    for (NSString *address in addresses) {
        if ([address rangeOfString:@"ipv6"].location != NSNotFound) [ipv6 addObject:address];
        else [other addObject:address];
    }
    NSMutableArray *result = [NSMutableArray array];
    if ([JCOREService getIpv6Prefered]) { [result addObjectsFromArray:ipv6]; [result addObjectsFromArray:other]; }
    else { [result addObjectsFromArray:other]; [result addObjectsFromArray:ipv6]; }
    return [[NSArray arrayWithArray:result] copy];
}
+ (id)getPreferedIpAddresses:(id)addresses {
    if (!addresses || ![addresses isKindOfClass:NSArray.class] || [addresses count] < 2) return addresses;
    NSMutableArray *ipv6 = [NSMutableArray array], *other = [NSMutableArray array];
    for (NSString *address in addresses) {
        JCOREAddress *parsed = [[JCOREAddress alloc] initWithString:address];
        if (parsed.host && parsed.ipType == 1) [ipv6 addObject:address];
        else [other addObject:address];
    }
    NSMutableArray *result = [NSMutableArray array];
    if ([JCOREService getIpv6Prefered]) { [result addObjectsFromArray:ipv6]; [result addObjectsFromArray:other]; }
    else { [result addObjectsFromArray:other]; [result addObjectsFromArray:ipv6]; }
    return [[NSArray arrayWithArray:result] copy];
}
@end
