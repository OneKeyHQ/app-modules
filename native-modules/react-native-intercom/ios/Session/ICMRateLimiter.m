#import "ICMRateLimiter.h"
#import "ICMConfig.h"
@implementation ICMRateLimiter
+ (instancetype)sharedInstance {
    static ICMRateLimiter *shared;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ shared = [ICMRateLimiter new]; });
    return shared;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        _timestampQueue = [[ICMMaxSizeQueue alloc] initWithMaxSize:[ICMConfig sharedConfig].rateLimit];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshLimits) name:@"kICMIntercomConfigChangedNotification" object:nil];
    }
    return self;
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
- (void)limit:(void (^)(void))block { [self limit:block dropped:nil]; }
- (void)limit:(void (^)(void))block dropped:(void (^)(void))dropped {
    @synchronized (self) {
        if (!block) return;
        if ([self canPerformAction]) {
            [self.timestampQueue enqueueObject:@([self currentTimestamp])];
            block();
        } else {
            if (dropped) dropped();
            NSUInteger limit = _timestampQueue.maxSize;
            int period = (int)[ICMConfig sharedConfig].rateLimitPeriod;
            NSLog(@"[Intercom] ERROR - More than %d user updates/events have been made in the past %d seconds", (int)limit, period);
        }
    }
}
- (void)refreshLimits { @synchronized (self) { _timestampQueue.maxSize = [ICMConfig sharedConfig].rateLimit; } }
- (BOOL)canPerformAction {
    NSTimeInterval period = [ICMConfig sharedConfig].rateLimitPeriod;
    if (period == 0 || _timestampQueue.size < _timestampQueue.maxSize) return YES;
    if ([_timestampQueue front]) {
        NSTimeInterval now = [self currentTimestamp];
        NSTimeInterval age = now - [[_timestampQueue front] doubleValue];
        return age > [ICMConfig sharedConfig].rateLimitPeriod;
    }
    return NO;
}
- (NSTimeInterval)currentTimestamp { return [[NSDate date] timeIntervalSince1970]; }
@end
