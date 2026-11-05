// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICMURLSessionTaskOperation.h"
@implementation ICMURLSessionTaskOperation
@synthesize task = _task, ignoreResponse = _ignoreResponse, taskCompleted = _taskCompleted;
- (instancetype)initWithURLSessionTask:(NSURLSessionTask *)task {
    if ((self = [super init])) { _task = task; _ready = YES; }
    return self;
}
- (void)start {
    if (self.isCancelled) { [self setFinished:YES]; return; }
    if (self.isExecuting) return;
    [self setReady:NO];
    [self setExecuting:YES];
    [self setFinished:NO];
    [self.task resume];
}
- (void)finish {
    if (!self.isExecuting) return;
    [self setExecuting:NO];
    [self setFinished:YES];
}
- (void)cancel { [super cancel]; [self.task cancel]; }
- (void)setTaskCompleted:(BOOL)value {
    if (_taskCompleted == value) return;
    _taskCompleted = value;
    if (value) [self finish];
}
- (BOOL)isReady { return _ready; }
- (void)setReady:(BOOL)value {
    if (_ready == value) return;
    [self willChangeValueForKey:NSStringFromSelector(@selector(isReady))];
    _ready = value;
    [self didChangeValueForKey:NSStringFromSelector(@selector(isReady))];
}
- (BOOL)isExecuting { return _executing; }
- (void)setExecuting:(BOOL)value {
    if (_executing == value) return;
    [self willChangeValueForKey:NSStringFromSelector(@selector(isExecuting))];
    _executing = value;
    [self didChangeValueForKey:NSStringFromSelector(@selector(isExecuting))];
}
- (BOOL)isFinished { return _finished; }
- (void)setFinished:(BOOL)value {
    if (_finished == value) return;
    [self willChangeValueForKey:NSStringFromSelector(@selector(isFinished))];
    _finished = value;
    [self didChangeValueForKey:NSStringFromSelector(@selector(isFinished))];
}
- (BOOL)isAsynchronous { return YES; }
@end
