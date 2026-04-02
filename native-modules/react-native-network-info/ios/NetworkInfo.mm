#import "NetworkInfo.h"
#import "getgateway.h"

#import <ifaddrs.h>
#import <arpa/inet.h>
#include <net/if.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <TargetConditionals.h>
#import <NetworkExtension/NetworkExtension.h>

#define IOS_CELLULAR    @"pdp_ip0"
#define IOS_WIFI        @"en0"
#define IP_ADDR_IPv4    @"ipv4"
#define IP_ADDR_IPv6    @"ipv6"

@import SystemConfiguration.CaptiveNetwork;

@implementation NetworkInfo

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeRNNetworkInfoSpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"RNNetworkInfo";
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

// MARK: - getSSID

- (void)getSSID:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject
{
#if TARGET_OS_IOS
    if (@available(iOS 14.0, *)) {
        [NEHotspotNetwork fetchCurrentWithCompletionHandler:^(NEHotspotNetwork * _Nullable currentNetwork) {
            resolve(currentNetwork.SSID);
        }];
        return;
    }

    @try {
        NSArray *interfaceNames = CFBridgingRelease(CNCopySupportedInterfaces());
        NSDictionary *SSIDInfo;
        NSString *SSID = nil;

        for (NSString *interfaceName in interfaceNames) {
            SSIDInfo = CFBridgingRelease(CNCopyCurrentNetworkInfo((__bridge CFStringRef)interfaceName));
            if (SSIDInfo.count > 0) {
                SSID = SSIDInfo[@"SSID"];
                break;
            }
        }
        resolve(SSID);
    } @catch (NSException *exception) {
        resolve(nil);
    }
#else
    resolve(nil);
#endif
}

// MARK: - getBSSID

- (void)getBSSID:(RCTPromiseResolveBlock)resolve
          reject:(RCTPromiseRejectBlock)reject
{
#if TARGET_OS_IOS
    if (@available(iOS 14.0, *)) {
        [NEHotspotNetwork fetchCurrentWithCompletionHandler:^(NEHotspotNetwork * _Nullable currentNetwork) {
            resolve(currentNetwork.BSSID);
        }];
        return;
    }

    @try {
        NSArray *interfaceNames = CFBridgingRelease(CNCopySupportedInterfaces());
        NSString *BSSID = nil;

        for (NSString *interface in interfaceNames) {
            CFDictionaryRef networkDetails = CNCopyCurrentNetworkInfo((CFStringRef)interface);
            if (networkDetails) {
                BSSID = (NSString *)CFDictionaryGetValue(networkDetails, kCNNetworkInfoKeyBSSID);
                CFRelease(networkDetails);
            }
        }
        resolve(BSSID);
    } @catch (NSException *exception) {
        resolve(nil);
    }
#else
    resolve(nil);
#endif
}

// MARK: - getBroadcast

- (void)getBroadcast:(RCTPromiseResolveBlock)resolve
              reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSString *address = nil;

            struct ifaddrs *interfaces = NULL;
            struct ifaddrs *temp_addr = NULL;
            int success = getifaddrs(&interfaces);

            if (success == 0) {
                temp_addr = interfaces;
                while (temp_addr != NULL) {
                    if (temp_addr->ifa_addr->sa_family == AF_INET) {
                        if ([[NSString stringWithUTF8String:temp_addr->ifa_name] isEqualToString:@"en0"]) {
                            NSString *localAddr = [NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr)];
                            NSString *netmask = [NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_netmask)->sin_addr)];

                            struct in_addr local_addr;
                            struct in_addr netmask_addr;
                            inet_aton([localAddr UTF8String], &local_addr);
                            inet_aton([netmask UTF8String], &netmask_addr);

                            local_addr.s_addr |= ~(netmask_addr.s_addr);
                            address = [NSString stringWithUTF8String:inet_ntoa(local_addr)];
                        }
                    }
                    temp_addr = temp_addr->ifa_next;
                }
            }
            freeifaddrs(interfaces);
            resolve(address);
        } @catch (NSException *exception) {
            resolve(nil);
        }
    });
}

// MARK: - getIPAddress

- (void)getIPAddress:(RCTPromiseResolveBlock)resolve
              reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSString *address = nil;

            struct ifaddrs *interfaces = NULL;
            struct ifaddrs *temp_addr = NULL;
            int success = getifaddrs(&interfaces);

            if (success == 0) {
                temp_addr = interfaces;
                while (temp_addr != NULL) {
                    if (temp_addr->ifa_addr->sa_family == AF_INET) {
                        if ([[NSString stringWithUTF8String:temp_addr->ifa_name] isEqualToString:@"en0"]) {
                            address = [NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_addr)->sin_addr)];
                        }
                    }
                    temp_addr = temp_addr->ifa_next;
                }
            }
            freeifaddrs(interfaces);
            resolve(address ?: @"");
        } @catch (NSException *exception) {
            resolve(@"");
        }
    });
}

// MARK: - getIPV6Address

- (void)getIPV6Address:(RCTPromiseResolveBlock)resolve
                reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSString *address = nil;

            struct ifaddrs *interfaces = NULL;
            struct ifaddrs *temp_addr = NULL;
            int success = getifaddrs(&interfaces);

            if (success == 0) {
                temp_addr = interfaces;
                while (temp_addr != NULL) {
                    if (temp_addr->ifa_addr->sa_family == AF_INET6) {
                        if ([[NSString stringWithUTF8String:temp_addr->ifa_name] isEqualToString:@"en0"]) {
                            char ipAddress[INET6_ADDRSTRLEN];
                            struct sockaddr_in6 *addr6 = (struct sockaddr_in6 *)temp_addr->ifa_addr;
                            if (inet_ntop(AF_INET6, &addr6->sin6_addr, ipAddress, INET6_ADDRSTRLEN)) {
                                address = [NSString stringWithUTF8String:ipAddress];
                            }
                        }
                    }
                    temp_addr = temp_addr->ifa_next;
                }
            }
            freeifaddrs(interfaces);
            resolve(address ?: @"");
        } @catch (NSException *exception) {
            resolve(@"");
        }
    });
}

// MARK: - getGatewayIPAddress

- (void)getGatewayIPAddress:(RCTPromiseResolveBlock)resolve
                     reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSString *ipString = nil;
            struct in_addr gatewayaddr;
            int r = getdefaultgateway(&(gatewayaddr.s_addr));
            if (r >= 0) {
                ipString = [NSString stringWithFormat:@"%s", inet_ntoa(gatewayaddr)];
            }
            resolve(ipString ?: @"");
        } @catch (NSException *exception) {
            resolve(@"");
        }
    });
}

// MARK: - getIPV4Address

- (void)getIPV4Address:(RCTPromiseResolveBlock)resolve
                reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSArray *searchArray = @[IOS_WIFI @"/" IP_ADDR_IPv4, IOS_CELLULAR @"/" IP_ADDR_IPv4];
            NSDictionary *addresses = [self getAllIPAddresses];

            __block NSString *address = nil;
            [searchArray enumerateObjectsUsingBlock:^(NSString *key, NSUInteger idx, BOOL *stop) {
                address = addresses[key];
                if (address) *stop = YES;
            }];
            resolve(address ?: @"0.0.0.0");
        } @catch (NSException *exception) {
            resolve(@"0.0.0.0");
        }
    });
}

// MARK: - getWIFIIPV4Address

- (void)getWIFIIPV4Address:(RCTPromiseResolveBlock)resolve
                    reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSArray *searchArray = @[IOS_WIFI @"/" IP_ADDR_IPv4];
            NSDictionary *addresses = [self getAllIPAddresses];

            __block NSString *address = nil;
            [searchArray enumerateObjectsUsingBlock:^(NSString *key, NSUInteger idx, BOOL *stop) {
                address = addresses[key];
                if (address) *stop = YES;
            }];
            resolve(address ?: @"0.0.0.0");
        } @catch (NSException *exception) {
            resolve(@"0.0.0.0");
        }
    });
}

// MARK: - getSubnet

- (void)getSubnet:(RCTPromiseResolveBlock)resolve
           reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        @try {
            NSString *netmask = nil;
            struct ifaddrs *interfaces = NULL;
            struct ifaddrs *temp_addr = NULL;

            int success = getifaddrs(&interfaces);
            if (success == 0) {
                temp_addr = interfaces;
                while (temp_addr != NULL) {
                    if (temp_addr->ifa_addr->sa_family == AF_INET) {
                        if ([@(temp_addr->ifa_name) isEqualToString:@"en0"]) {
                            netmask = @(inet_ntoa(((struct sockaddr_in *)temp_addr->ifa_netmask)->sin_addr));
                        }
                    }
                    temp_addr = temp_addr->ifa_next;
                }
            }
            freeifaddrs(interfaces);
            resolve(netmask ?: @"0.0.0.0");
        } @catch (NSException *exception) {
            resolve(@"0.0.0.0");
        }
    });
}

// MARK: - getAllIPAddresses helper

- (NSDictionary *)getAllIPAddresses
{
    NSMutableDictionary *addresses = [NSMutableDictionary dictionaryWithCapacity:8];

    struct ifaddrs *interfaces;
    if (!getifaddrs(&interfaces)) {
        struct ifaddrs *interface;
        for (interface = interfaces; interface; interface = interface->ifa_next) {
            if (!(interface->ifa_flags & IFF_UP)) {
                continue;
            }
            const struct sockaddr_in *addr = (const struct sockaddr_in *)interface->ifa_addr;
            char addrBuf[MAX(INET_ADDRSTRLEN, INET6_ADDRSTRLEN)];
            if (addr && (addr->sin_family == AF_INET || addr->sin_family == AF_INET6)) {
                NSString *name = [NSString stringWithUTF8String:interface->ifa_name];
                NSString *type = nil;
                if (addr->sin_family == AF_INET) {
                    if (inet_ntop(AF_INET, &addr->sin_addr, addrBuf, INET_ADDRSTRLEN)) {
                        type = IP_ADDR_IPv4;
                    }
                } else {
                    const struct sockaddr_in6 *addr6 = (const struct sockaddr_in6 *)interface->ifa_addr;
                    if (inet_ntop(AF_INET6, &addr6->sin6_addr, addrBuf, INET6_ADDRSTRLEN)) {
                        type = IP_ADDR_IPv6;
                    }
                }
                if (type) {
                    NSString *key = [NSString stringWithFormat:@"%@/%@", name, type];
                    addresses[key] = [NSString stringWithUTF8String:addrBuf];
                }
            }
        }
        freeifaddrs(interfaces);
    }
    return [addresses count] ? addresses : @{};
}

@end
