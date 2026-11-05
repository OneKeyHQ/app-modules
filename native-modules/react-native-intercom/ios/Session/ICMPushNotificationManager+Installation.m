#import "ICMPushNotificationManager+Installation.h"
#import "ICMUtils+Integration.h"
#import "ICMIdentityStore.h"
#import "../Identity/ICMDeviceTokenData.h"
#import <objc/runtime.h>

static dispatch_once_t pushInstallationOnce;
static NSMutableDictionary<NSString *, NSMutableSet<Class> *> *installedDelegateClasses;

@implementation ICMPushNotificationManager (OriginalInstallation)
+ (void)swizzleReceiveRemoteNotifications {
    dispatch_once(&pushInstallationOnce, ^{
        if (![ICMUtils shouldHandlePushNotificationsAutomatically]) [self observeLaunchForManualPushHandling];
        else if ([ICMUtils isRunningNotificationServiceExtension]) [self swizzlePushNotificationServiceExtensionClasses];
        else if ([ICMIdentityStore currentIdentity].deviceTokenData.deviceToken.length) [self observePushDelegateAssignment];
    });
}
+ (void)observePushDelegateAssignment {
    [self observeDelegateAssignmentOnClass:[UIApplication class] handler:^(id delegate) { [self swizzleApplicationDelegate:delegate]; }];
    [self observeDelegateAssignmentOnClass:[UNUserNotificationCenter class] handler:^(id delegate) { [self swizzleNotificationCenterDelegate:delegate]; }];
    [self observeDidFinishLaunchingOnce:^{
        [self swizzleApplicationDelegate:[UIApplication sharedApplication].delegate];
        [self swizzleNotificationCenterDelegate:[UNUserNotificationCenter currentNotificationCenter].delegate];
        @synchronized (self) {
            if (installedDelegateClasses[NSStringFromSelector(@selector(application:didFinishLaunchingWithOptions:))].count) return;
            if (installedDelegateClasses[NSStringFromSelector(@selector(application:didReceiveRemoteNotification:fetchCompletionHandler:))].count) return;
        }
        [self warnApplicationDelegateNotFound];
    }];
}
+ (void)warnApplicationDelegateNotFound { NSLog(@"[Intercom] ERROR - Could not find the application delegate's class. Push notifications may not work correctly."); }
+ (void)observeDelegateAssignmentOnClass:(Class)target handler:(void (^)(id))handler {
    SEL selector = @selector(setDelegate:);
    Method method = class_getInstanceMethod(target, selector);
    if (!method) return;
    IMP original = method_getImplementation(method);
    IMP replacement = imp_implementationWithBlock(^(id receiver, id delegate) {
        if (delegate) handler(delegate);
        if (original) ((void (*)(id, SEL, id))original)(receiver, selector, delegate);
    });
    class_replaceMethod(target, selector, replacement, method_getTypeEncoding(method));
}
+ (void)swizzleApplicationDelegate:(id)delegate {
    [self swizzleDelegate:delegate selector:@selector(application:didFinishLaunchingWithOptions:) usingBlock:^(Class target) { [self swizzleDidFinishLaunchingWithOptionsOnClass:target]; }];
    [self swizzleDelegate:delegate selector:@selector(application:didReceiveRemoteNotification:fetchCompletionHandler:) usingBlock:^(Class target) { [self swizzleDidReceiveRemoteNotificationFetchCompletionHandlerOnClass:target]; }];
}
+ (void)swizzleNotificationCenterDelegate:(id)delegate {
    [self swizzleDelegate:delegate selector:@selector(userNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:) usingBlock:^(Class target) { [self swizzleDidReceiveNotificationResponseWithCompletionHandlerOnClass:target]; }];
    [self swizzleDelegate:delegate selector:@selector(userNotificationCenter:willPresentNotification:withCompletionHandler:) usingBlock:^(Class target) { [self swizzleWillPresentNotificationWithCompletionHandlerOnClass:target]; }];
}
+ (void)swizzleDelegate:(id)delegate selector:(SEL)selector usingBlock:(void (^)(Class))installer {
    Class target = [self swizzleTargetClassForDelegate:delegate selector:selector];
    if (!target) return;
    @synchronized (self) {
        if (!installedDelegateClasses) installedDelegateClasses = [NSMutableDictionary dictionary];
        NSString *key = NSStringFromSelector(selector);
        NSMutableSet *classes = installedDelegateClasses[key];
        if (!classes) { classes = [NSMutableSet set]; installedDelegateClasses[key] = classes; }
        if (![classes containsObject:target]) { installer(target); [classes addObject:target]; }
    }
}
+ (Class)swizzleTargetClassForDelegate:(id)delegate selector:(SEL)selector {
    NSHashTable *visited = [NSHashTable hashTableWithOptions:0x200];
    id current = delegate;
    while (current) {
        if ([visited containsObject:current]) return Nil;
        [visited addObject:current];
        Class target = [self swizzleTargetClassForDelegate:current];
        if (target && class_getInstanceMethod(target, selector)) return target;
        if (class_getInstanceMethod(object_getClass(current), @selector(forwardingTargetForSelector:))) {
            id forwarded = [current forwardingTargetForSelector:selector];
            if (forwarded) { current = forwarded; continue; }
        }
        return [current respondsToSelector:selector] ? Nil : target;
    }
    return Nil;
}
+ (Class)swizzleTargetClassForDelegate:(id)delegate {
    Class target = object_getClass(delegate);
    while (target) {
        if (target == [NSObject class] || target == [NSProxy class] || target == [UIResponder class]) return Nil;
        if (class_getImageName(target)) return target;
        target = class_getSuperclass(target);
    }
    return Nil;
}
+ (void)observeDidFinishLaunchingOnce:(void (^)(void))block {
    __block id token;
    token = [[NSNotificationCenter defaultCenter] addObserverForName:UIApplicationDidFinishLaunchingNotification object:nil queue:nil usingBlock:^(NSNotification *notification) {
        (void)notification;
        [[NSNotificationCenter defaultCenter] removeObserver:token];
        token = nil;
        block();
    }];
}
+ (void)swizzlePushNotificationServiceExtensionClasses { [self swizzleRichPush:[self getPushNotificationServiceExtensionClass]]; }
+ (Class)getPushNotificationServiceExtensionClass {
    id name = [[[NSBundle mainBundle] objectForInfoDictionaryKey:@"NSExtension"] valueForKey:@"NSExtensionPrincipalClass"];
    return [name isKindOfClass:[NSString class]] ? NSClassFromString(name) : Nil;
}
+ (void)swizzleDidReceiveRemoteNotificationFetchCompletionHandlerOnClass:(Class)target {
    SEL selector = @selector(application:didReceiveRemoteNotification:fetchCompletionHandler:);
    Method method = class_getInstanceMethod(target, selector);
    IMP original = method ? method_getImplementation(method) : NULL;
    IMP replacement = imp_implementationWithBlock(^(id receiver, UIApplication *application, NSDictionary *payload, void (^completion)(UIBackgroundFetchResult)) {
        if ([ICMPushNotificationManager isIntercomPushNotification:payload]) {
            [ICMPushNotificationManager handleIntercomPushNotification:payload delivery:3];
        } else if (original) {
            ((void (*)(id, SEL, UIApplication *, NSDictionary *, void (^)(UIBackgroundFetchResult)))original)(receiver, selector, application, payload, completion);
            return;
        }
        if (completion) completion(UIBackgroundFetchResultNoData);
    });
    if (method) class_replaceMethod(target, selector, replacement, method_getTypeEncoding(method));
    else {
        id modes = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"UIBackgroundModes"];
        if ([modes isKindOfClass:[NSArray class]] && [modes containsObject:@"remote-notification"] && ![target instancesRespondToSelector:@selector(application:didReceiveRemoteNotification:)]) {
            class_addMethod(target, selector, replacement, "v@:@@@");
        }
    }
}
+ (void)swizzleDidFinishLaunchingWithOptionsOnClass:(Class)target {
    SEL selector = @selector(application:didFinishLaunchingWithOptions:);
    Method method = class_getInstanceMethod(target, selector);
    if (!method) return;
    IMP original = method_getImplementation(method);
    IMP replacement = imp_implementationWithBlock(^BOOL(id receiver, UIApplication *application, NSDictionary *options) {
        NSDictionary *payload = options[UIApplicationLaunchOptionsRemoteNotificationKey];
        BOOL intercom = [ICMPushNotificationManager isIntercomPushNotification:payload];
        NSDictionary *forwarded = intercom ? [ICMPushNotificationManager modifyLaunchOptions:options] : options;
        BOOL result = original ? ((BOOL (*)(id, SEL, UIApplication *, NSDictionary *))original)(receiver, selector, application, forwarded) : YES;
        if (intercom) [ICMPushNotificationManager handleIntercomPushNotification:payload delivery:1];
        return result;
    });
    class_replaceMethod(target, selector, replacement, method_getTypeEncoding(method));
}
+ (NSMutableDictionary *)modifyLaunchOptions:(NSDictionary *)options {
    NSMutableDictionary *modified = [options mutableCopy];
    if ([ICMUtils fetchSDKType] == 1) [self updateReactNativeLaunchOptionsWithDeepLinkURL:modified];
    [modified removeObjectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
    if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] Modified Launch Options: %@", modified);
    return modified;
}
+ (void)observeLaunchForManualPushHandling {
    [self observeDidFinishLaunchingOnce:^{
        if ([ICMIdentityStore currentIdentity].deviceTokenData.deviceToken.length && ![UNUserNotificationCenter currentNotificationCenter].delegate) [self logManualPushDelegateReminder];
    }];
}
+ (void)logManualPushDelegateReminder {
    NSLog(@"[Intercom] If you use UNUserNotificationCenterDelegate to handle Intercom notification taps manually, set [UNUserNotificationCenter currentNotificationCenter].delegate before application:didFinishLaunchingWithOptions: returns so it can receive cold-launch callbacks. See https://t.ly/xoklw for more info.");
}
+ (void)swizzleDidReceiveNotificationResponseWithCompletionHandlerOnClass:(Class)target {
    SEL selector = @selector(userNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:);
    Method method = class_getInstanceMethod(target, selector);
    if (!method) return;
    IMP original = method_getImplementation(method);
    IMP replacement = imp_implementationWithBlock(^(id receiver, UNUserNotificationCenter *center, UNNotificationResponse *response, void (^completion)(void)) {
        NSDictionary *payload = response.notification.request.content.userInfo;
        if ([ICMPushNotificationManager isIntercomPushNotification:payload]) {
            if (![response.actionIdentifier isEqualToString:UNNotificationDismissActionIdentifier]) [ICMPushNotificationManager handleIntercomPushNotification:payload delivery:0];
        } else if (original) {
            ((void (*)(id, SEL, UNUserNotificationCenter *, UNNotificationResponse *, void (^)(void)))original)(receiver, selector, center, response, completion);
            return;
        }
        if (completion) completion();
    });
    class_replaceMethod(target, selector, replacement, method_getTypeEncoding(method));
}
+ (void)swizzleWillPresentNotificationWithCompletionHandlerOnClass:(Class)target {
    SEL selector = @selector(userNotificationCenter:willPresentNotification:withCompletionHandler:);
    Method method = class_getInstanceMethod(target, selector);
    if (!method) return;
    IMP original = method_getImplementation(method);
    IMP replacement = imp_implementationWithBlock(^(id receiver, UNUserNotificationCenter *center, UNNotification *notification, void (^completion)(UNNotificationPresentationOptions)) {
        if (![ICMPushNotificationManager isIntercomPushNotification:notification.request.content.userInfo] && original) {
            ((void (*)(id, SEL, UNUserNotificationCenter *, UNNotification *, void (^)(UNNotificationPresentationOptions)))original)(receiver, selector, center, notification, completion);
            return;
        }
        if (completion) completion(0);
    });
    class_replaceMethod(target, selector, replacement, method_getTypeEncoding(method));
}
+ (void)swizzleRichPush:(Class)target {
    SEL selector = @selector(didReceiveNotificationRequest:withContentHandler:);
    Method method = class_getInstanceMethod(target, selector);
    if (!method) return;
    IMP original = method_getImplementation(method);
    IMP replacement = imp_implementationWithBlock(^(id receiver, UNNotificationRequest *request, void (^handler)(UNNotificationContent *)) {
        if ([ICMPushNotificationManager isIntercomPushNotification:request.content.userInfo]) {
            [ICMPushNotificationManager handleIntercomRichPushNotificationContent:[request.content mutableCopy] withContentHandler:handler];
        } else if (original) {
            ((void (*)(id, SEL, UNNotificationRequest *, void (^)(UNNotificationContent *)))original)(receiver, selector, request, handler);
        } else if (handler) handler(nil);
    });
    class_replaceMethod(target, selector, replacement, method_getTypeEncoding(method));
}
+ (NSMutableDictionary *)updateReactNativeLaunchOptionsWithDeepLinkURL:(NSMutableDictionary *)options {
    if (options[UIApplicationLaunchOptionsRemoteNotificationKey]) {
        id payload = options[UIApplicationLaunchOptionsRemoteNotificationKey];
        if (payload[@"uri"] && ![payload[@"uri"] isKindOfClass:[NSNull class]]) {
            id uri = payload[@"uri"];
            if (!options[UIApplicationLaunchOptionsURLKey]) {
                if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] Setting launch options UIApplicationLaunchOptionsURLKey with URL: %@", uri);
                [options setObject:[NSURL URLWithString:uri] forKey:UIApplicationLaunchOptionsURLKey];
            }
        }
    }
    return options;
}
+ (void)resetOnceToken {
    pushInstallationOnce = 0;
    @synchronized (self) { [installedDelegateClasses removeAllObjects]; }
}
@end
