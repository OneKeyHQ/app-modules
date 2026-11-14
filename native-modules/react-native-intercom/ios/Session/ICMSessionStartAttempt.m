#import "ICMSessionStartAttempt.h"
@implementation ICMSessionStartAttempt
@synthesize token = _token, sessionId = _sessionId;
+ (instancetype)attemptWithToken:(NSUInteger)token sessionId:(NSString *)sessionId {
    ICMSessionStartAttempt *attempt = [self new];
    attempt.token = token;
    attempt.sessionId = sessionId;
    return attempt;
}
+ (instancetype)attemptRequestingNoNewSession { return [self attemptWithToken:0 sessionId:nil]; }
- (BOOL)requestsNewSession { return self.token != 0; }
@end
