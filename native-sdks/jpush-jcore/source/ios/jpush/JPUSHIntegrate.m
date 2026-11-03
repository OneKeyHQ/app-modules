#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHIntegrate.h"
#import "JPUSHPropertyModels.h"
@implementation JPUSHIntegrate
+ (id)info {
  JPUSHIntegrateInfo *info = [JPUSHIntegrateInfo new];
  info.index = 0;
  info.property = 1;
  info.version = @"50900";
  return info;
}
+ (void)registerIntegrateInfo:(id)info {
  [super registerIntegrateInfo:info];
}
+ (void)load {
  [self registerIntegrateInfo:[self info]];
}
+ (id)jcoreDisplayVersion {
  return [super jcoreDisplayVersion];
}
+ (short)jcoreBuildVersion {
  return [super jcoreBuildVersion];
}
@end
#endif
