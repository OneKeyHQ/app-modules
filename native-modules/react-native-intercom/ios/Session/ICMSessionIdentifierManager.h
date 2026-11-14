#import <Foundation/Foundation.h>
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate24SessionIdentifierManager")))
@interface ICMRecoveredSessionIdentifierManager : NSObject
+ (instancetype)shared;
@property (nonatomic, readonly) NSString *lastLaunchSessionId;
- (void)recordStartedSession:(NSString *)identifier;
@end
