#import "ICMMaxSizeQueue.h"
@interface ICMRateLimiter : NSObject
@property(nonatomic, strong) ICMMaxSizeQueue *timestampQueue;
+ (instancetype)sharedInstance;
- (void)limit:(void (^)(void))block;
- (void)limit:(void (^)(void))block dropped:(void (^)(void))dropped;
- (void)refreshLimits;
- (BOOL)canPerformAction;
- (NSTimeInterval)currentTimestamp;
@end
