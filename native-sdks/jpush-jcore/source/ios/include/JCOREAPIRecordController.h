#pragma once
#import <Foundation/Foundation.h>
@interface JCOREAPIRecordController : NSObject
- (void)tryReport;
+ (void)apiRecord:(id)type methodName:(id)method;
@end
