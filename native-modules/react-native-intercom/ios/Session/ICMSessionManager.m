#import "ICMSessionManager.h"
#import "ICMSessionIdentifierManager.h"
#import "ICMConfig.h"
@implementation ICMSessionManager
@synthesize sessionStartedSinceLastBackgrounded = _sessionStartedSinceLastBackgrounded, retryRequested = _retryRequested;
@synthesize sessionStartOwed = _sessionStartOwed, shouldPreserveBackgroundRecord = _shouldPreserveBackgroundRecord;
@synthesize backgroundTime = _backgroundTime, inFlightToken = _inFlightToken, lastIssuedToken = _lastIssuedToken, pendingSessionId = _pendingSessionId;
+ (instancetype)sharedInstance {
    static ICMSessionManager *manager;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ manager = [ICMSessionManager new]; });
    return manager;
}
- (void)appMovedToTheBackground {
    @synchronized (self) {
        self.backgroundTime = [self currentTimestamp];
        self.sessionStartedSinceLastBackgrounded = NO;
        self.shouldPreserveBackgroundRecord = self.inFlightToken != 0;
        self.retryRequested = NO;
    }
}
- (BOOL)shouldStartNewSession {
    @synchronized (self) {
        NSTimeInterval now = [self currentTimestamp];
        if (self.sessionStartedSinceLastBackgrounded) return NO;
        NSTimeInterval background = self.backgroundTime;
        NSTimeInterval threshold = [ICMConfig sharedConfig].newSessionThreshold;
        return now - background > threshold;
    }
}
- (BOOL)hasOutstandingSessionStart {
    @synchronized (self) {
        if (self.inFlightToken || self.sessionStartOwed) return YES;
        return [self shouldStartNewSession];
    }
}
- (ICMSessionStartAttempt *)beginSessionStart {
    @synchronized (self) {
        if (self.inFlightToken) {
            self.retryRequested = YES;
            return [ICMSessionStartAttempt attemptRequestingNoNewSession];
        }
        if (!self.sessionStartOwed && ![self shouldStartNewSession]) return [ICMSessionStartAttempt attemptRequestingNoNewSession];
        self.lastIssuedToken = self.lastIssuedToken + 1;
        self.inFlightToken = self.lastIssuedToken;
        if (!self.pendingSessionId) self.pendingSessionId = [NSUUID UUID].UUIDString;
        return [ICMSessionStartAttempt attemptWithToken:self.inFlightToken sessionId:self.pendingSessionId];
    }
}
- (BOOL)sessionStartSucceededWithToken:(NSUInteger)token {
    @synchronized (self) {
        BOOL claims = [self claimsInFlightAttempt:token];
        if (claims) {
            self.inFlightToken = 0;
            self.retryRequested = NO;
            self.sessionStartOwed = NO;
            if (!self.shouldPreserveBackgroundRecord) {
                self.backgroundTime = 0;
                self.sessionStartedSinceLastBackgrounded = YES;
            }
            self.shouldPreserveBackgroundRecord = NO;
            if (self.pendingSessionId) {
                [[ICMRecoveredSessionIdentifierManager shared] recordStartedSession:self.pendingSessionId];
                self.pendingSessionId = nil;
            }
        }
        return claims;
    }
}
- (BOOL)sessionStartFailedWithToken:(NSUInteger)token {
    @synchronized (self) {
        if (![self claimsInFlightAttempt:token]) return NO;
        self.inFlightToken = 0;
        self.sessionStartOwed = YES;
        self.shouldPreserveBackgroundRecord = NO;
        BOOL retry = self.retryRequested;
        self.retryRequested = NO;
        return retry;
    }
}
- (void)reset {
    @synchronized (self) {
        self.backgroundTime = 0;
        self.sessionStartedSinceLastBackgrounded = NO;
        self.inFlightToken = 0;
        self.retryRequested = NO;
        self.sessionStartOwed = NO;
        self.shouldPreserveBackgroundRecord = NO;
        self.pendingSessionId = nil;
    }
}
- (BOOL)claimsInFlightAttempt:(NSUInteger)token { return token != 0 && token == self.inFlightToken; }
- (NSTimeInterval)currentTimestamp { return [[NSDate date] timeIntervalSince1970]; }
@end
