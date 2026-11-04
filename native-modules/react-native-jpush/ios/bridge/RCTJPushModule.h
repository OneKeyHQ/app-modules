#import <Foundation/Foundation.h>

#if __has_include("JPUSHService.h")
#import "JPUSHService.h"
#elif __has_include("../include/JPUSHService.h")
#import "../include/JPUSHService.h"
#endif
#import "RCTJPushEventQueue.h"

#define J_APNS_NOTIFICATION_ARRIVED_EVENT  @"J_APNS_NOTIFICATION_ARRIVED_EVENT"
#define J_APNS_NOTIFICATION_OPENED_EVENT   @"J_APNS_NOTIFICATION_OPENED_EVENT"
#define J_LOCAL_NOTIFICATION_ARRIVED_EVENT @"J_LOCAL_NOTIFICATION_ARRIVED_EVENT"
#define J_LOCAL_NOTIFICATION_OPENED_EVENT  @"J_LOCAL_NOTIFICATION_OPENED_EVENT"
#define J_CUSTOM_NOTIFICATION_EVENT        @"J_CUSTOM_NOTIFICATION_EVENT"

NS_ASSUME_NONNULL_BEGIN

@interface RCTJPushModule : NSObject

+ (nullable instancetype)sharedModule;
+ (void)emitEventWithName:(NSString *)eventName payload:(NSDictionary *)payload;
+ (void)flushQueuedNotifications;

@end

NS_ASSUME_NONNULL_END
