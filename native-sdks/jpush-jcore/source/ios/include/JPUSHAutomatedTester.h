#pragma once
#import <Foundation/Foundation.h>

@interface JPUSHAutomatedTester : NSObject
+ (void)PostAutomatedTestStatusNotification:(NSInteger)status;
@end
