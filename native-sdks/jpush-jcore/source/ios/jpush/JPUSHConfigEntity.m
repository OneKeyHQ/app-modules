#import "JPUSHConfigEntity.h"
#import <TargetConditionals.h>

#if !TARGET_OS_SIMULATOR
@implementation JPUSHConfigEntity
@dynamic hash, superclass, description, debugDescription;
@synthesize appKey;
@synthesize channel;
@synthesize advertisingId;
@synthesize isProduction;
@synthesize completionHandler;
@synthesize index;
@end
#endif
