#import "ICMPushNotificationManager+Dispatch.h"
#import "ICMPendingPush.h"
#import "ICMIdentityStore.h"

static NSDictionary *coldLaunchPush;
static ICMPendingPush *pendingPush;

@implementation ICMPushNotificationManager (OriginalDispatch)
+ (BOOL)isIntercomPushNotification:(NSDictionary *)payload {
    return [payload isKindOfClass:[NSDictionary class]] && payload[@"intercom_push_type"] != nil;
}
+ (void)handleIntercomPushNotification:(NSDictionary *)payload { [self handleIntercomPushNotification:payload delivery:2]; }
+ (void)handleIntercomPushNotification:(NSDictionary *)payload delivery:(NSUInteger)delivery {
    if (![ICMIdentityStore currentIdentity].userIdentity.identityExists) return;
    if (delivery == 0 && coldLaunchPush) {
        BOOL duplicate = [coldLaunchPush isEqual:payload];
        coldLaunchPush = nil;
        if (duplicate) return;
    }
    UIApplicationState state = [UIApplication sharedApplication].applicationState;
    NSInteger verdict = [self verdictForDelivery:delivery applicationState:state];
    if (verdict == 0) return;
    if (state != UIApplicationStateActive) {
        if (verdict == 1 && pendingPush && pendingPush.verdict == 2) return;
        if (delivery == 1) coldLaunchPush = payload;
        [self parkPush:payload verdict:verdict];
    } else if ([NSThread isMainThread]) [self handleNotificationInForeground:payload];
    else dispatch_async(dispatch_get_main_queue(), ^{ [self handleNotificationInForeground:payload]; });
}
+ (NSUInteger)verdictForDelivery:(NSUInteger)delivery applicationState:(UIApplicationState)state {
    switch (delivery) {
        case 0: return 2;
        case 1: return state == UIApplicationStateInactive ? 2 : 0;
        case 2: return state != UIApplicationStateActive ? 1 : 0;
        case 3: return state == UIApplicationStateInactive ? 1 : 0;
        default: return 0;
    }
}
+ (void)parkPush:(NSDictionary *)payload verdict:(NSUInteger)verdict {
    [pendingPush stopWaitingForActivation];
    pendingPush = [[ICMPendingPush alloc] initWithUserInfo:payload verdict:verdict queuedAt:[self currentDate]];
    __weak Class weakSelf = self;
    id observer = [[NSNotificationCenter defaultCenter] addObserverForName:UIApplicationDidBecomeActiveNotification object:nil queue:nil usingBlock:^(NSNotification *notification) {
        (void)notification;
        [weakSelf presentPendingPush];
    }];
    pendingPush.activationObserver = observer;
}
+ (void)presentPendingPush {
    ICMPendingPush *push = pendingPush;
    pendingPush = nil;
    [push stopWaitingForActivation];
    if (!push) return;
    if (push.verdict == 1 && [[self currentDate] timeIntervalSinceDate:push.queuedAt] > 10.0) return;
    if ([NSThread isMainThread]) [self handleNotificationInForeground:push.userInfo];
    else dispatch_async(dispatch_get_main_queue(), ^{ [self handleNotificationInForeground:push.userInfo]; });
}
+ (NSDate *)currentDate { return [NSDate date]; }
+ (void)resetPushHandlingState {
    [pendingPush stopWaitingForActivation];
    pendingPush = nil;
    coldLaunchPush = nil;
}
@end
