#import <Foundation/Foundation.h>
#import "ICMSessionStartAttempt.h"
@interface ICMSessionManager : NSObject {
    BOOL _sessionStartedSinceLastBackgrounded;
    BOOL _retryRequested;
    BOOL _sessionStartOwed;
    BOOL _shouldPreserveBackgroundRecord;
    NSTimeInterval _backgroundTime;
    NSUInteger _inFlightToken;
    NSUInteger _lastIssuedToken;
    NSString *_pendingSessionId;
}
@property (nonatomic) NSTimeInterval backgroundTime;
@property (nonatomic) BOOL sessionStartedSinceLastBackgrounded;
@property (nonatomic) NSUInteger inFlightToken;
@property (nonatomic) NSUInteger lastIssuedToken;
@property (nonatomic) BOOL retryRequested;
@property (nonatomic) BOOL sessionStartOwed;
@property (nonatomic) BOOL shouldPreserveBackgroundRecord;
@property (nonatomic, copy) NSString *pendingSessionId;
+ (instancetype)sharedInstance;
- (void)appMovedToTheBackground;
- (BOOL)shouldStartNewSession;
- (BOOL)hasOutstandingSessionStart;
- (ICMSessionStartAttempt *)beginSessionStart;
- (BOOL)sessionStartSucceededWithToken:(NSUInteger)token;
- (BOOL)sessionStartFailedWithToken:(NSUInteger)token;
- (void)reset;
- (BOOL)claimsInFlightAttempt:(NSUInteger)token;
- (NSTimeInterval)currentTimestamp;
@end
