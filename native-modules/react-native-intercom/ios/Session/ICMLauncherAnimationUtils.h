#pragma once
#import <Foundation/Foundation.h>
@interface ICMLauncherAnimationUtils : NSObject
+ (void)animate:(void (^)(void))animations completion:(void (^)(void))completion;
@end
