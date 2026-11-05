// Original lifecycle/authentication source is partial until the remaining API closes.
#import <Foundation/Foundation.h>
@class ICMUserAttributes;
@interface ICMBridge : NSObject
+ (void)prepareForNotifications;
+ (void)ping;
+ (void)registerUserWithAttributes:(ICMUserAttributes *)attributes success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (void)completeAuthenticationWithAPI;
+ (NSArray *)identitySnapshot;
+ (BOOL)appIsBackgrounded;
+ (void)handleConfigChanged;
+ (BOOL)isUserLoggedIn;
+ (ICMUserAttributes *)fetchLoggedInUserAttributes;
@end
@interface ICMBridge (UnrestoredLifecycleDependencies)
+ (void)refreshedUserJwtWasApplied;
+ (void)deleteDeviceTokenIfPushNotificationsDisabled;
@end
