#import <Foundation/Foundation.h>
@interface ICMPendingPush : NSObject
@property(nonatomic, readonly, strong) NSDictionary *userInfo;
@property(nonatomic, readonly) NSUInteger verdict;
@property(nonatomic, readonly, strong) NSDate *queuedAt;
@property(nonatomic, strong) id activationObserver;
- (instancetype)initWithUserInfo:(NSDictionary *)userInfo verdict:(NSUInteger)verdict queuedAt:(NSDate *)queuedAt;
- (void)stopWaitingForActivation;
@end
