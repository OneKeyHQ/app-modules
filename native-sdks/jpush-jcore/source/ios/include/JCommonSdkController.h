#pragma once
#import <Foundation/Foundation.h>
@interface JCommonSdkController : NSObject
+ (void)reportSdkInfo;
+ (NSDictionary *)content;
+ (BOOL)isSdkInfoChange:(NSDictionary *)oldContent newContent:(NSDictionary *)newContent;
@end
