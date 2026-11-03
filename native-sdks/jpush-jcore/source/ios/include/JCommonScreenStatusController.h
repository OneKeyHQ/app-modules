#pragma once
#import <Foundation/Foundation.h>
@interface JCommonScreenStatusController : NSObject
- (instancetype)init;
- (void)protectedDataWillBecomeUnavailable:(id)notification;
- (void)protectedDataDidBecomeAvailable:(id)notification;
- (NSDictionary *)statusInfo:(BOOL)available;
- (void)reportSCInfo:(id)available;
@end
