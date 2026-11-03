#pragma once
#import <Foundation/Foundation.h>
@interface JCORENetUtilities : NSObject
+ (unsigned short)generateRid;
+ (BOOL)isViaWifi;
+ (int)networkingStatus;
@end
@interface JCOREIpvPreferedUtilities : NSObject
+ (id)getPreferedAddresses:(id)addresses;
+ (id)getPreferedIpAddresses:(id)addresses;
@end
