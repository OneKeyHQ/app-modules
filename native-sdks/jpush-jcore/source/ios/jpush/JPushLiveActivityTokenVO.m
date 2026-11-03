#import "JPushLiveActivityTokenVO.h"
#import <TargetConditionals.h>

#if !TARGET_OS_SIMULATOR
@implementation JPushLiveActivityTokenVO
@synthesize reportType = _reportType;
@synthesize liveActivityAlias = _liveActivityAlias;
@synthesize pushToken = _pushToken;
@synthesize attributesType = _attributesType;
@synthesize pushToStartToken = _pushToStartToken;
@synthesize seq = _seq;
@synthesize completion = _completion;
@end
#endif
