// Objective-C alias for the genuine PushNotificationStatus.swift class.
#import <Foundation/Foundation.h>
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate22PushNotificationStatus")))
@interface ICMRecoveredPushNotificationStatus : NSObject
@property (class, nonatomic, strong) ICMRecoveredPushNotificationStatus *shared;
@property (nonatomic, readonly) BOOL isEnabled;
- (void)refresh;
- (void)refreshWithCompletion:(void (^)(void))completion;
@end
