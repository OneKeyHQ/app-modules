#pragma once
#import "JCOREIntegrate.h"
@interface JPUSHIntegrate : JCOREIntegrate
+ (id)info;
+ (void)registerIntegrateInfo:(id)info;
+ (id)jcoreDisplayVersion;
+ (short)jcoreBuildVersion;
@end
