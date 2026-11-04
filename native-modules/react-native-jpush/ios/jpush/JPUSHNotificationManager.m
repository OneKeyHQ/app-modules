#import "JPUSHNotificationManager.h"
#import <UserNotifications/UserNotifications.h>
#if __has_include(<UIKit/UIKit.h>)
#import <UIKit/UIKit.h>
#endif

@interface JPUSHNotificationManager () <UNUserNotificationCenterDelegate>
@end

@implementation JPUSHNotificationManager

+ (instancetype)shared {
    static JPUSHNotificationManager *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[JPUSHNotificationManager alloc] init];
    });
    return instance;
}

- (nullable UNUserNotificationCenter *)safeNotificationCenter {
    if ([NSBundle mainBundle].bundleIdentifier.length == 0) {
        return nil;
    }
    @try {
        return [UNUserNotificationCenter currentNotificationCenter];
    } @catch (NSException *exception) {
        return nil;
    }
}

- (void)registerWithConfig:(JPUSHRegisterEntity *)config
                  delegate:(id<JPUSHRegisterDelegate>)delegate {
    self.registerDelegate = delegate;
    [self registerTypes:(NSUInteger)config.types categories:config.categories];
}

- (void)registerTypes:(NSUInteger)types categories:(NSSet *)categories {
    UNUserNotificationCenter *center = [self safeNotificationCenter];
    if (center == nil) {
        return;
    }
    center.delegate = self;
    if (categories.count > 0) {
        [center setNotificationCategories:categories];
    }

    UNAuthorizationOptions options = UNAuthorizationOptionNone;
    if (types & JPAuthorizationOptionBadge) {
        options |= UNAuthorizationOptionBadge;
    }
    if (types & JPAuthorizationOptionSound) {
        options |= UNAuthorizationOptionSound;
    }
    if (types & JPAuthorizationOptionAlert) {
        options |= UNAuthorizationOptionAlert;
    }
    if (types & JPAuthorizationOptionCarPlay) {
        options |= UNAuthorizationOptionCarPlay;
    }
    if (@available(iOS 12.0, *)) {
        if (types & JPAuthorizationOptionCriticalAlert) {
            options |= UNAuthorizationOptionCriticalAlert;
        }
        if (types & JPAuthorizationOptionProvidesAppNotificationSettings) {
            options |= UNAuthorizationOptionProvidesAppNotificationSettings;
        }
        if (types & JPAuthorizationOptionProvisional) {
            options |= UNAuthorizationOptionProvisional;
        }
    }

    if (options == UNAuthorizationOptionNone) {
        return;
    }

    __weak typeof(self) weakSelf = self;
    [center requestAuthorizationWithOptions:options completionHandler:^(BOOL granted, NSError * _Nullable error) {
        dispatch_async(dispatch_get_main_queue(), ^{
#if __has_include(<UIKit/UIKit.h>)
            if (granted) {
                [[UIApplication sharedApplication] registerForRemoteNotifications];
            }
#endif
            __strong typeof(weakSelf) strongSelf = weakSelf;
            if ([strongSelf.registerDelegate respondsToSelector:@selector(jpushNotificationAuthorization:withInfo:)]) {
                JPAuthorizationStatus status = granted ? JPAuthorizationStatusAuthorized : JPAuthorizationStatusDenied;
                NSDictionary *info = error ? @{@"error": error.localizedDescription ?: @""} : @{};
                [strongSelf.registerDelegate jpushNotificationAuthorization:status withInfo:info];
            }
        });
    }];
}

- (void)addNotification:(JPushNotificationRequest *)request {
    if (request == nil || request.content == nil) {
        if (request.completionHandler != nil) {
            request.completionHandler(nil);
        }
        return;
    }

    NSString *identifier = request.requestIdentifier.length > 0
        ? request.requestIdentifier
        : [NSUUID UUID].UUIDString;

    UNMutableNotificationContent *content = [[UNMutableNotificationContent alloc] init];
    if (request.content.title != nil) {
        content.title = request.content.title;
    }
    if (request.content.subtitle != nil) {
        content.subtitle = request.content.subtitle;
    }
    if (request.content.body != nil) {
        content.body = request.content.body;
    }
    if (request.content.badge != nil) {
        content.badge = request.content.badge;
    }
    if (request.content.categoryIdentifier.length > 0) {
        content.categoryIdentifier = request.content.categoryIdentifier;
    }
    if (request.content.threadIdentifier.length > 0) {
        content.threadIdentifier = request.content.threadIdentifier;
    }
    if (request.content.userInfo != nil) {
        content.userInfo = request.content.userInfo;
    }
    if (request.content.soundSetting.soundSetting != nil) {
        content.sound = request.content.soundSetting.soundSetting;
    } else if (request.content.soundSetting.soundName.length > 0) {
        content.sound = [UNNotificationSound soundNamed:request.content.soundSetting.soundName];
    } else if (request.content.sound.length > 0) {
        content.sound = [UNNotificationSound soundNamed:request.content.sound];
    } else {
        content.sound = [UNNotificationSound defaultSound];
    }
    if (request.content.attachments.count > 0) {
        content.attachments = request.content.attachments;
    }

    UNNotificationTrigger *trigger = nil;
    if (request.trigger != nil) {
        if (request.trigger.dateComponents != nil) {
            trigger = [UNCalendarNotificationTrigger triggerWithDateMatchingComponents:request.trigger.dateComponents
                                                                               repeats:request.trigger.repeat];
        } else if (request.trigger.timeInterval > 0) {
            NSTimeInterval interval = request.trigger.repeat ? MAX(60.0, request.trigger.timeInterval) : request.trigger.timeInterval;
            trigger = [UNTimeIntervalNotificationTrigger triggerWithTimeInterval:interval
                                                                         repeats:request.trigger.repeat];
        } else if (request.trigger.fireDate != nil) {
            NSTimeInterval delta = [request.trigger.fireDate timeIntervalSinceNow];
            if (delta > 0) {
                trigger = [UNTimeIntervalNotificationTrigger triggerWithTimeInterval:delta repeats:NO];
            }
        }
    }

    UNNotificationRequest *unRequest = [UNNotificationRequest requestWithIdentifier:identifier
                                                                            content:content
                                                                            trigger:trigger];
    UNUserNotificationCenter *center = [self safeNotificationCenter];
    if (center == nil) {
        if (request.completionHandler != nil) {
            dispatch_async(dispatch_get_main_queue(), ^{
                request.completionHandler(unRequest);
            });
        }
        return;
    }
    [center addNotificationRequest:unRequest
             withCompletionHandler:^(NSError * _Nullable error) {
        if (request.completionHandler != nil) {
            dispatch_async(dispatch_get_main_queue(), ^{
                request.completionHandler(error == nil ? unRequest : nil);
            });
        }
    }];
}

- (void)removeNotification:(JPushNotificationIdentifier *)identifier {
    UNUserNotificationCenter *center = [self safeNotificationCenter];
    if (center == nil) {
        return;
    }
    if (identifier == nil || identifier.identifiers.count == 0) {
        [center removeAllPendingNotificationRequests];
        [center removeAllDeliveredNotifications];
        return;
    }
    if (identifier.delivered) {
        [center removeDeliveredNotificationsWithIdentifiers:identifier.identifiers];
    } else {
        [center removePendingNotificationRequestsWithIdentifiers:identifier.identifiers];
    }
}

- (void)findNotification:(JPushNotificationIdentifier *)identifier {
    if (identifier == nil || identifier.findCompletionHandler == nil) {
        return;
    }
    UNUserNotificationCenter *center = [self safeNotificationCenter];
    if (center == nil) {
        dispatch_async(dispatch_get_main_queue(), ^{
            identifier.findCompletionHandler(@[]);
        });
        return;
    }
    NSSet<NSString *> *filterSet = identifier.identifiers.count > 0
        ? [NSSet setWithArray:identifier.identifiers]
        : nil;

    if (identifier.delivered) {
        [center getDeliveredNotificationsWithCompletionHandler:^(NSArray<UNNotification *> * _Nonnull notifications) {
            NSMutableArray *matched = [NSMutableArray array];
            for (UNNotification *item in notifications) {
                if (filterSet == nil || [filterSet containsObject:item.request.identifier]) {
                    [matched addObject:item.request];
                }
            }
            dispatch_async(dispatch_get_main_queue(), ^{
                identifier.findCompletionHandler(matched);
            });
        }];
    } else {
        [center getPendingNotificationRequestsWithCompletionHandler:^(NSArray<UNNotificationRequest *> * _Nonnull requests) {
            NSMutableArray *matched = [NSMutableArray array];
            for (UNNotificationRequest *req in requests) {
                if (filterSet == nil || [filterSet containsObject:req.identifier]) {
                    [matched addObject:req];
                }
            }
            dispatch_async(dispatch_get_main_queue(), ^{
                identifier.findCompletionHandler(matched);
            });
        }];
    }
}

- (void)requestAuthorization:(void (^)(JPAuthorizationStatus status))completion {
    UNUserNotificationCenter *center = [self safeNotificationCenter];
    if (center == nil) {
        if (completion != nil) {
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(JPAuthorizationNotDetermined);
            });
        }
        return;
    }
    UNAuthorizationOptions options = (UNAuthorizationOptionBadge | UNAuthorizationOptionSound | UNAuthorizationOptionAlert);
    [center requestAuthorizationWithOptions:options completionHandler:^(BOOL granted, NSError * _Nullable error) {
        (void)error;
        JPAuthorizationStatus status = granted ? JPAuthorizationStatusAuthorized : JPAuthorizationStatusDenied;
        dispatch_async(dispatch_get_main_queue(), ^{
#if __has_include(<UIKit/UIKit.h>)
            if (granted) {
                [[UIApplication sharedApplication] registerForRemoteNotifications];
            }
#endif
            if (completion != nil) {
                completion(status);
            }
        });
    }];
}

- (void)getAuthorizationStatus:(void (^)(JPAuthorizationStatus status))completion {
    UNUserNotificationCenter *center = [self safeNotificationCenter];
    if (center == nil) {
        if (completion != nil) {
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(JPAuthorizationNotDetermined);
            });
        }
        return;
    }
    [center getNotificationSettingsWithCompletionHandler:^(UNNotificationSettings * _Nonnull settings) {
        JPAuthorizationStatus status = JPAuthorizationNotDetermined;
        switch (settings.authorizationStatus) {
            case UNAuthorizationStatusDenied:
                status = JPAuthorizationStatusDenied;
                break;
            case UNAuthorizationStatusAuthorized:
                status = JPAuthorizationStatusAuthorized;
                break;
            case UNAuthorizationStatusProvisional:
                status = JPAuthorizationStatusProvisional;
                break;
#if defined(__IPHONE_14_0) && __IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_14_0
            case UNAuthorizationStatusEphemeral:
                status = JPAuthorizationStatusAuthorized;
                break;
#endif
            default:
                status = JPAuthorizationNotDetermined;
                break;
        }
        if (completion != nil) {
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(status);
            });
        }
    }];
}

- (void)openNotificationSettings:(void (^)(BOOL success))completion {
    dispatch_async(dispatch_get_main_queue(), ^{
#if __has_include(<UIKit/UIKit.h>)
        NSURL *url = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
        if (url != nil && [[UIApplication sharedApplication] canOpenURL:url]) {
            [[UIApplication sharedApplication] openURL:url options:@{} completionHandler:completion];
            return;
        }
#endif
        if (completion != nil) {
            completion(NO);
        }
    });
}

#pragma mark - UNUserNotificationCenterDelegate

- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions options))completionHandler {
    if ([self.registerDelegate respondsToSelector:@selector(jpushNotificationCenter:willPresentNotification:withCompletionHandler:)]) {
        [self.registerDelegate jpushNotificationCenter:center
                               willPresentNotification:notification
                                 withCompletionHandler:^(NSInteger options) {
            completionHandler((UNNotificationPresentationOptions)options);
        }];
    } else {
        completionHandler(UNNotificationPresentationOptionBadge |
                          UNNotificationPresentationOptionSound |
                          UNNotificationPresentationOptionAlert);
    }
}

- (void)userNotificationCenter:(UNUserNotificationCenter *)center
didReceiveNotificationResponse:(UNNotificationResponse *)response
         withCompletionHandler:(void (^)(void))completionHandler {
    if ([self.registerDelegate respondsToSelector:@selector(jpushNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:)]) {
        [self.registerDelegate jpushNotificationCenter:center
                        didReceiveNotificationResponse:response
                                 withCompletionHandler:completionHandler];
    } else {
        completionHandler();
    }
}

- (void)userNotificationCenter:(UNUserNotificationCenter *)center
   openSettingsForNotification:(UNNotification *)notification {
    if ([self.registerDelegate respondsToSelector:@selector(jpushNotificationCenter:openSettingsForNotification:)]) {
        [self.registerDelegate jpushNotificationCenter:center openSettingsForNotification:notification];
    }
}

@end
