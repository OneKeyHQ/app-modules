#import <Foundation/Foundation.h>
@interface ICMSessionStartAttempt : NSObject {
    NSUInteger _token;
    NSString *_sessionId;
}
@property (nonatomic) NSUInteger token;
@property (nonatomic, copy) NSString *sessionId;
@property (nonatomic, readonly) BOOL requestsNewSession;
+ (instancetype)attemptWithToken:(NSUInteger)token sessionId:(NSString *)sessionId;
+ (instancetype)attemptRequestingNoNewSession;
@end
