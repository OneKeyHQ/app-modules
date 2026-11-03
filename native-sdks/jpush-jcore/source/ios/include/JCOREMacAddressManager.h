#pragma once
#import <Foundation/Foundation.h>

@interface JCOREMacAddressManager : NSObject
+ (void)getDeviceMacFromMDNS:(void (^)(NSString *value))callback;
+ (NSString *)currentIPAddressOf:(NSString *)interfaceName;
+ (NSString *)IPv4Ntop:(unsigned int)address;
+ (unsigned int)IPv4Pton:(NSString *)address;
@end
