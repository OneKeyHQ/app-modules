// Diagnostic dependencies only. These classes never enter the product build.
#import <Foundation/Foundation.h>
#import "../ios/Session/ICMSessionManager.h"
#import <math.h>
static double fixtureThreshold = 20;
static NSMutableArray *recorded;
__attribute__((objc_runtime_name("ICMConfig")))
@interface FixtureSessionConfig : NSObject
+ (instancetype)sharedConfig;
- (double)newSessionThreshold;
@end
@implementation FixtureSessionConfig
+ (instancetype)sharedConfig { static id value; if (!value) value = [self new]; return value; }
- (double)newSessionThreshold { return fixtureThreshold; }
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate24SessionIdentifierManager")))
@interface FixtureSessionRecorder : NSObject
+ (instancetype)shared;
- (void)recordStartedSession:(NSString *)identifier;
@end
@implementation FixtureSessionRecorder
+ (instancetype)shared { static id value; if (!value) value = [self new]; return value; }
- (void)recordStartedSession:(NSString *)identifier { [recorded addObject:identifier]; }
@end
@interface FixtureSessionClock : ICMSessionManager
@property double now;
@property NSUInteger reads;
@end
@implementation FixtureSessionClock
- (double)currentTimestamp { self.reads++; return self.now; }
@end
static NSUInteger assertions;
#define CHECK(condition) do { assertions++; if (!(condition)) { NSLog(@"FAIL line %d", __LINE__); return 1; } } while (0)
int main(void) { @autoreleasepool {
    recorded = [NSMutableArray new];
    FixtureSessionClock *manager = [FixtureSessionClock new];
    manager.now = 100;
    CHECK(manager.inFlightToken == 0 && manager.lastIssuedToken == 0 && manager.pendingSessionId == nil);
    CHECK([manager shouldStartNewSession]);
    manager.backgroundTime = 80;
    CHECK(![manager shouldStartNewSession]);
    manager.now = 100.01;
    CHECK([manager shouldStartNewSession]);
    fixtureThreshold = NAN;
    CHECK(![manager shouldStartNewSession]);
    fixtureThreshold = 20;
    manager.sessionStartedSinceLastBackgrounded = YES;
    NSUInteger reads = manager.reads;
    CHECK(![manager shouldStartNewSession] && manager.reads == reads + 1);
    CHECK(![manager claimsInFlightAttempt:0]);
    ICMSessionStartAttempt *none = [manager beginSessionStart];
    CHECK(none.token == 0 && !none.requestsNewSession && !manager.retryRequested);
    manager.sessionStartOwed = YES;
    NSMutableString *identifier = [NSMutableString stringWithString:@"synthetic-id"];
    manager.pendingSessionId = identifier;
    [identifier appendString:@"-mutated"];
    CHECK([manager.pendingSessionId isEqualToString:@"synthetic-id"]);
    ICMSessionStartAttempt *first = [manager beginSessionStart];
    CHECK(first.token == 1 && first.requestsNewSession && [first.sessionId isEqualToString:@"synthetic-id"]);
    CHECK([manager hasOutstandingSessionStart]);
    CHECK([manager beginSessionStart].token == 0 && manager.retryRequested);
    CHECK(![manager sessionStartSucceededWithToken:99] && manager.inFlightToken == 1);
    CHECK([manager sessionStartFailedWithToken:1] && manager.inFlightToken == 0 && manager.sessionStartOwed && !manager.retryRequested);
    CHECK([manager.pendingSessionId isEqualToString:@"synthetic-id"]);
    ICMSessionStartAttempt *second = [manager beginSessionStart];
    CHECK(second.token == 2 && [second.sessionId isEqualToString:first.sessionId]);
    manager.now = 110;
    [manager appMovedToTheBackground];
    CHECK(manager.backgroundTime == 110 && manager.shouldPreserveBackgroundRecord && !manager.sessionStartedSinceLastBackgrounded);
    CHECK([manager sessionStartSucceededWithToken:2]);
    CHECK(manager.backgroundTime == 110 && !manager.sessionStartedSinceLastBackgrounded && !manager.shouldPreserveBackgroundRecord);
    CHECK(manager.pendingSessionId == nil && recorded.count == 1 && [recorded[0] isEqualToString:@"synthetic-id"]);
    [manager reset];
    CHECK(manager.lastIssuedToken == 2 && manager.backgroundTime == 0 && !manager.sessionStartOwed);
    CHECK(![manager sessionStartFailedWithToken:2] && ![manager sessionStartSucceededWithToken:0]);
    manager.now = 150;
    ICMSessionStartAttempt *third = [manager beginSessionStart];
    CHECK(third.token == 3 && third.sessionId.length == 36);
    CHECK([manager sessionStartSucceededWithToken:3] && manager.backgroundTime == 0 && manager.sessionStartedSinceLastBackgrounded);
    CHECK(recorded.count == 2 && [recorded[1] isEqualToString:third.sessionId]);
    [manager reset];
    manager.lastIssuedToken = NSUIntegerMax;
    manager.sessionStartOwed = YES;
    ICMSessionStartAttempt *wrapped = [manager beginSessionStart];
    CHECK(wrapped.token == 0 && !wrapped.requestsNewSession && manager.inFlightToken == 0 && manager.pendingSessionId != nil);
    CHECK(![manager sessionStartSucceededWithToken:0] && manager.sessionStartOwed);
    [manager appMovedToTheBackground];
    CHECK(!manager.shouldPreserveBackgroundRecord && manager.backgroundTime == manager.now);
    NSMutableString *copyInput = [NSMutableString stringWithString:@"copy"];
    ICMSessionStartAttempt *attempt = [ICMSessionStartAttempt attemptWithToken:9 sessionId:copyInput];
    [copyInput appendString:@" changed"];
    CHECK([attempt.sessionId isEqualToString:@"copy"] && attempt.requestsNewSession);
    printf("PASS %lu session-start assertions; Config/recorder diagnostic only; no persistence/network\n", (unsigned long)assertions);
    return 0;
} }
