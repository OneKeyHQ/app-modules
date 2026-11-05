#import "ICMPendingPush.h"
@implementation ICMPendingPush
- (instancetype)initWithUserInfo:(NSDictionary *)userInfo verdict:(NSUInteger)verdict queuedAt:(NSDate *)queuedAt {
    self = [super init];
    if (self) { _userInfo = userInfo; _verdict = verdict; _queuedAt = queuedAt; }
    return self;
}
- (void)stopWaitingForActivation {
    if (self.activationObserver) {
        [[NSNotificationCenter defaultCenter] removeObserver:self.activationObserver];
        self.activationObserver = nil;
    }
}
@end
