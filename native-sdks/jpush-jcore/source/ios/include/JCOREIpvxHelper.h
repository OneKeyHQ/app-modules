#pragma once
#import <Foundation/Foundation.h>
@interface JCOREIpvxHelper : NSObject {
    BOOL _isVerified;
    NSString *_ipv6Pre;
    long long _networkIPvxType;
    long long _staticIpvxType;
}
@property (nonatomic) long long networkIPvxType;
@property (nonatomic) long long staticIpvxType;
@property (readonly, nonatomic) BOOL isVerified;
@property (copy, nonatomic) NSString *ipv6Pre;
+ (instancetype)instance;
- (void)configStaticIpvxType:(long long)type;
- (void)configNetworkIpvxType:(long long)type;
- (long long)ipvxType;
- (void)checkIpvxType;
- (void)checkIpvxTypeByLocalNetwork;
- (BOOL)isIpv6Network;
- (NSMutableDictionary *)getIPAddresses;
- (void)cacheNetworkIpvxType:(long long)type;
- (NSString *)convertHostFromIPv4ToIPv6WithHost:(NSString *)host pre:(NSString *)prefix;
@end
