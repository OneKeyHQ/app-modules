#pragma once
#import <Foundation/Foundation.h>
#import "JCoreParameter.h"
typedef void (^JCORESetupCompletion)(NSError *error,NSDictionary *info);
#import "JGInforCollectionAuthItems.h"
#import "JCOREConfig.h"
#import "JCOREIntegrate.h"
#import "JCORECrashConfig.h"
#import "JCOREUaidTokenConfig.h"
#import "JCOREDevidBanConfig.h"
@interface JCOREIpvxHelper:NSObject
+ (instancetype)instance;
- (void)configStaticIpvxType:(NSInteger)value;
@end
#import "JCOREDataControlManager.h"
@interface JCOREBusinessStorage:NSObject
+ (void)setLocation:(id)value;
@end
#import "JCORECertController.h"
@interface NSObject (JCORESetupDependencySelectors)
- (id)appKey;
- (NSInteger)index;
- (JCORESetupCompletion)completionHandler;
@end
