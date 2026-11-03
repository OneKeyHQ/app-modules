#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHSetTagAliasArg.h"

@interface JPUSHClientController : NSObject
+ (instancetype)sharedClient;
- (id)tagAliasMng;
@end
@interface NSObject (JPUSHSetTagAliasTimeoutDependency)
- (void)doTagAliasCallbackForSeq:(NSUInteger)sequence withResCode:(int)code;
@end

@implementation JPUSHSetTagAliasArg
@synthesize tags=_tags,alias=_alias,type=_type,completionHandler=_completionHandler,cbInvocation=_cbInvocation,target=_target,sequence=_sequence;

- (id)copyWithZone:(NSZone *)zone {
    JPUSHSetTagAliasArg *copy=[[[self class] allocWithZone:zone] init];
    copy->_alias=[_alias copy];
    copy->_tags=[_tags copy];
    copy->_sequence=_sequence;
    copy->_cbInvocation=_cbInvocation;
    copy->_timeoutTimer=_timeoutTimer;
    copy->_target=_target;
    copy->_completionHandler=[_completionHandler copy];
    copy->_type=_type;
    return copy;
}

- (void)dealloc {
    _target=nil;
    _alias=nil;
    _tags=nil;
    _completionHandler=nil;
    _timeoutTimer=nil;
}

- (void)beginTimeoutTimer {
    _timeoutTimer=[NSTimer scheduledTimerWithTimeInterval:20.0 target:self selector:@selector(onTimeout:) userInfo:nil repeats:NO];
}

- (void)stopTimeoutTimer {
    if([_timeoutTimer isValid])[_timeoutTimer invalidate];
    _timeoutTimer=nil;
}

- (void)onTimeout:(NSTimer *)timer {
    if(_timeoutTimer!=timer)return;
    [self stopTimeoutTimer];
    [[[JPUSHClientController sharedClient] tagAliasMng] doTagAliasCallbackForSeq:_sequence withResCode:6002];
    [self p_deQueueFromTcpBlockQueue];
}

// The device implementation deliberately leaves this hook empty.
- (void)p_deQueueFromTcpBlockQueue {}
@end
#endif
