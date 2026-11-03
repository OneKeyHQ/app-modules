#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHNewRequest.h"
#import "JCORENetChannel.h"
@implementation JPUSHNewRequest
@dynamic version, protocolType, algorithm, rid, uid, seed, controlFlag, autoRetryCount, moniter_id,
    completionHandler, hash, superclass, description, debugDescription;
- (void)send {
    [JCORENetChannel send:self];
}
@end
#endif
