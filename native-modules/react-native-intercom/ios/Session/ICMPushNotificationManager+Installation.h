#import "ICMPushNotificationManager.h"
#import <UIKit/UIKit.h>
@interface ICMPushNotificationManager (OriginalInstallation)
+ (void)swizzleReceiveRemoteNotifications;
+ (void)observePushDelegateAssignment;
+ (void)warnApplicationDelegateNotFound;
+ (void)observeDelegateAssignmentOnClass:(Class)target handler:(void (^)(id))handler;
+ (void)swizzleApplicationDelegate:(id)delegate;
+ (void)swizzleNotificationCenterDelegate:(id)delegate;
+ (void)swizzleDelegate:(id)delegate selector:(SEL)selector usingBlock:(void (^)(Class))installer;
+ (Class)swizzleTargetClassForDelegate:(id)delegate selector:(SEL)selector;
+ (Class)swizzleTargetClassForDelegate:(id)delegate;
+ (void)observeDidFinishLaunchingOnce:(void (^)(void))block;
+ (void)swizzlePushNotificationServiceExtensionClasses;
+ (Class)getPushNotificationServiceExtensionClass;
+ (void)swizzleDidReceiveRemoteNotificationFetchCompletionHandlerOnClass:(Class)target;
+ (void)swizzleDidFinishLaunchingWithOptionsOnClass:(Class)target;
+ (NSMutableDictionary *)modifyLaunchOptions:(NSDictionary *)options;
+ (void)observeLaunchForManualPushHandling;
+ (void)logManualPushDelegateReminder;
+ (void)swizzleDidReceiveNotificationResponseWithCompletionHandlerOnClass:(Class)target;
+ (void)swizzleWillPresentNotificationWithCompletionHandlerOnClass:(Class)target;
+ (void)swizzleRichPush:(Class)target;
+ (NSMutableDictionary *)updateReactNativeLaunchOptionsWithDeepLinkURL:(NSMutableDictionary *)options;
+ (void)resetOnceToken;
@end
@interface ICMPushNotificationManager (OriginalPayloadDependencies)
+ (BOOL)isIntercomPushNotification:(NSDictionary *)payload;
+ (void)handleIntercomPushNotification:(NSDictionary *)payload;
+ (void)handleIntercomPushNotification:(NSDictionary *)payload delivery:(NSUInteger)delivery;
+ (void)handleIntercomRichPushNotificationContent:(UNMutableNotificationContent *)content withContentHandler:(void (^)(UNNotificationContent *))handler;
@end
