#import "ICMPushNotificationManager+Installation.h"
@interface ICMPushNotificationManager (OriginalDispatch)
+ (NSUInteger)verdictForDelivery:(NSUInteger)delivery applicationState:(UIApplicationState)state;
+ (void)parkPush:(NSDictionary *)payload verdict:(NSUInteger)verdict;
+ (void)presentPendingPush;
+ (NSDate *)currentDate;
+ (void)resetPushHandlingState;
@end
@interface ICMPushNotificationManager (OriginalForegroundDependency)
+ (void)handleNotificationInForeground:(NSDictionary *)payload;
@end
