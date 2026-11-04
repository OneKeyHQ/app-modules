#import <Foundation/Foundation.h>
#import "../include/JPUSHService.h"

NS_ASSUME_NONNULL_BEGIN

@interface JPUSHNotificationManager : NSObject

+ (instancetype)shared;

@property (nonatomic, weak, nullable) id<JPUSHRegisterDelegate> registerDelegate;
@property (nonatomic, weak, nullable) id<JPUSHNotiInMessageDelegate> notiInMessageDelegate;
@property (nonatomic, weak, nullable) id<JPUSHInAppMessageDelegate> inAppMessageDelegate;
@property (nonatomic, weak, nullable) id<JPUSHGeofenceDelegate> geofenceDelegate;

- (void)registerWithConfig:(JPUSHRegisterEntity *)config
                  delegate:(nullable id<JPUSHRegisterDelegate>)delegate;

- (void)registerTypes:(NSUInteger)types categories:(nullable NSSet *)categories;

- (void)addNotification:(JPushNotificationRequest *)request;
- (void)removeNotification:(nullable JPushNotificationIdentifier *)identifier;
- (void)findNotification:(JPushNotificationIdentifier *)identifier;

- (void)requestAuthorization:(nullable void (^)(JPAuthorizationStatus status))completion;
- (void)openNotificationSettings:(nullable void (^)(BOOL success))completion;

@end

NS_ASSUME_NONNULL_END
