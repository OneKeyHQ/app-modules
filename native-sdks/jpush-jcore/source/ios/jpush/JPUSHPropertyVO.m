#import "JPUSHPropertyVO.h"
#import <TargetConditionals.h>

#if !TARGET_OS_SIMULATOR
@implementation JPUSHPropertyVO
@synthesize opearteType = _opearteType;
@synthesize seq = _seq;
@synthesize propertiesInfo = _propertiesInfo;
@synthesize propertyCompletion = _propertyCompletion;
@end
#endif
