#import "RCTJPushModule.h"
#if __has_include("JCOREConfiguration.h")
#import "JCOREConfiguration.h"
#else
#import "../jcore/JCOREConfiguration.h"
#endif
#import <UIKit/UIKit.h>

#if __has_include(<React/RCTBridge.h>)
#import <React/RCTBridge.h>
#elif __has_include("RCTBridge.h")
#import "RCTBridge.h"
#endif

#define CODE           @"code"
#define BADGE          @"badge"
#define SEQUENCE       @"sequence"
#define REGISTER_ID    @"registerID"
#define MOBILE_NUMBER  @"mobileNumber"
#define CONNECT_ENABLE @"connectEnable"

#define MESSAGE_ID     @"messageID"
#define TITLE          @"title"
#define CONTENT        @"content"
#define EXTRAS         @"extras"
#define RING           @"ring"
#define BROADCASTTIME  @"broadcastTime"
#define APP_BADGE      @"appBadge"

#define TAG            @"tag"
#define TAGS           @"tags"
#define TAG_ENABLE     @"tagEnable"
#define ALIAS          @"alias"
#define PROS           @"pros"

#define GEO_FENCE_ID         @"geoFenceID"
#define GEO_FENCE_MAX_NUMBER @"geoFenceMaxNumber"

#define NOTIFICATION_EVENT_TYPE   @"notificationEventType"
#define NOTIFICATION_ARRIVED      @"notificationArrived"
#define NOTIFICATION_OPENED       @"notificationOpened"
#define NOTIFICATION_DISMISSED    @"notificationDismissed"
#define NOTIFICATION_EVENT        @"NotificationEvent"
#define CUSTOM_MESSAGE_EVENT      @"CustomMessageEvent"
#define INAPP_MESSAGE_EVENT_TYPE  @"inappEventType"
#define INAPP_MESSAGE_SHOW        @"inappShow"
#define INAPP_MESSAGE_CLICK       @"inappClick"
#define INAPP_MESSAGE_EVENT       @"InappMessageEvent"
#define LOCAL_NOTIFICATION_EVENT  @"LocalNotificationEvent"
#define CONNECT_EVENT             @"ConnectEvent"
#define TAG_ALIAS_EVENT           @"TagAliasEvent"
#define PROPERTIES_EVENT          @"PropertiesEvent"
#define MOBILE_NUMBER_EVENT       @"MobileNumberEvent"

static __weak RCTJPushModule *sSharedJPushModule = nil;

@interface NSObject (RCTJPushBridgelessCompat)
- (instancetype)initWithDisabledObservation;
- (nullable id)callableJSModules;
- (void)invokeModule:(NSString *)moduleName method:(NSString *)methodName withArgs:(NSArray *)args;
@end

@interface RCTJPushModule () <RCTBridgeModule>
@end

@implementation RCTJPushModule

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE(JPushModule);

RCT_EXPORT_METHOD(addListener:(NSString *)eventName) {
    (void)eventName;
}

RCT_EXPORT_METHOD(removeListeners:(double)count) {
    (void)count;
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

+ (nullable instancetype)sharedModule {
    return sSharedJPushModule;
}

+ (void)emitEventWithName:(NSString *)eventName payload:(NSDictionary *)payload {
    RCTJPushModule *module = sSharedJPushModule;
    if (module == nil || eventName.length == 0 || payload == nil) {
        return;
    }
    if ([module respondsToSelector:@selector(callableJSModules)]) {
        id callableModules = [module callableJSModules];
        if (callableModules != nil && [callableModules respondsToSelector:@selector(invokeModule:method:withArgs:)]) {
            [callableModules invokeModule:@"RCTDeviceEventEmitter"
                                   method:@"emit"
                                 withArgs:@[eventName, payload]];
            return;
        }
    }
    if (module.bridge != nil) {
        [module.bridge enqueueJSCall:@"RCTDeviceEventEmitter"
                              method:@"emit"
                                args:@[eventName, payload]
                          completion:NULL];
    }
}

+ (void)flushQueuedNotifications {
    RCTJPushModule *module = sSharedJPushModule;
    if (module == nil) {
        return;
    }
    NSMutableArray *notificationList = [RCTJPushEventQueue sharedInstance]._notificationQueue;
    if (notificationList.count > 0) {
        [module sendApnsNotificationEventByDictionary:notificationList[0]];
    }
    NSMutableArray *localNotificationList = [RCTJPushEventQueue sharedInstance]._localNotificationQueue;
    if (localNotificationList.count > 0) {
        [module sendLocalNotificationEventByDictionary:localNotificationList[0]];
    }
}

- (instancetype)init {
    if ([self respondsToSelector:@selector(initWithDisabledObservation)]) {
        self = [super initWithDisabledObservation];
    } else {
        self = [super init];
    }
    if (self) {
        sSharedJPushModule = self;
        NSNotificationCenter *defaultCenter = [NSNotificationCenter defaultCenter];
        [defaultCenter removeObserver:self];

        [defaultCenter addObserver:self
                          selector:@selector(sendApnsNotificationEvent:)
                              name:J_APNS_NOTIFICATION_ARRIVED_EVENT
                            object:nil];
        [defaultCenter addObserver:self
                          selector:@selector(sendApnsNotificationEvent:)
                              name:J_APNS_NOTIFICATION_OPENED_EVENT
                            object:nil];
        [defaultCenter addObserver:self
                          selector:@selector(sendLocalNotificationEvent:)
                              name:J_LOCAL_NOTIFICATION_ARRIVED_EVENT
                            object:nil];
        [defaultCenter addObserver:self
                          selector:@selector(sendLocalNotificationEvent:)
                              name:J_LOCAL_NOTIFICATION_OPENED_EVENT
                            object:nil];
        [defaultCenter addObserver:self
                          selector:@selector(sendCustomNotificationEvent:)
                              name:J_CUSTOM_NOTIFICATION_EVENT
                            object:nil];
        [defaultCenter addObserver:self
                          selector:@selector(sendConnectEvent:)
                              name:kJPFNetworkDidCloseNotification
                            object:nil];
        [defaultCenter addObserver:self
                          selector:@selector(sendConnectEvent:)
                              name:kJPFNetworkFailedRegisterNotification
                            object:nil];
        [defaultCenter addObserver:self
                          selector:@selector(sendConnectEvent:)
                              name:kJPFNetworkDidLoginNotification
                            object:nil];
    }
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

RCT_EXPORT_METHOD(setDebugMode:(BOOL)enable) {
    if (enable) {
        [JPUSHService setDebugMode];
    } else {
        [JPUSHService setLogOFF];
    }
}

RCT_EXPORT_METHOD(setupWithConfig:(NSDictionary *)params) {
    if (params[@"appKey"] && params[@"channel"] && params[@"production"] != nil) {
        NSMutableDictionary *launchOptions = self.bridge.launchOptions
            ? [NSMutableDictionary dictionaryWithDictionary:self.bridge.launchOptions]
            : [NSMutableDictionary dictionary];
        [JPUSHService setupWithOption:launchOptions
                               appKey:params[@"appKey"]
                              channel:params[@"channel"]
                     apsForProduction:[params[@"production"] boolValue]];
    }
    [RCTJPushModule flushQueuedNotifications];
}

RCT_EXPORT_METHOD(loadJS) {
    [RCTJPushModule flushQueuedNotifications];
}

RCT_EXPORT_METHOD(getRegisterId:(RCTResponseSenderBlock)callback) {
    [JPUSHService registrationIDCompletionHandler:^(int resCode, NSString *registrationID) {
        (void)resCode;
        NSDictionary *response = @{REGISTER_ID: registrationID ?: @""};
        if (callback) {
            callback(@[response]);
        }
    }];
}

RCT_EXPORT_METHOD(isNotificationEnabled:(RCTResponseSenderBlock)callback) {
    [JPUSHService requestNotificationAuthorization:^(JPAuthorizationStatus status) {
        BOOL enabled = (status > JPAuthorizationStatusDenied);
        if (callback) {
            callback(@[@(enabled)]);
        }
    }];
}

RCT_EXPORT_METHOD(addTags:(NSDictionary *)params) {
    if ([params[TAGS] isKindOfClass:[NSArray class]]) {
        NSArray *tags = [params[TAGS] copy];
        if (tags != nil) {
            NSSet *tagSet = [NSSet setWithArray:tags];
            NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
            [JPUSHService addTags:tagSet completion:^(NSInteger iResCode, NSSet *iTags, NSInteger seq) {
                NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq), TAGS: [iTags allObjects] ?: @[]};
                [self sendTagAliasEvent:data];
            } seq:sequence];
        }
    }
}

RCT_EXPORT_METHOD(setTags:(NSDictionary *)params) {
    if ([params[TAGS] isKindOfClass:[NSArray class]]) {
        NSArray *tags = [params[TAGS] copy];
        if (tags != nil) {
            NSSet *tagSet = [NSSet setWithArray:tags];
            NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
            [JPUSHService setTags:tagSet completion:^(NSInteger iResCode, NSSet *iTags, NSInteger seq) {
                NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq), TAGS: [iTags allObjects] ?: @[]};
                [self sendTagAliasEvent:data];
            } seq:sequence];
        }
    }
}

RCT_EXPORT_METHOD(deleteTags:(NSDictionary *)params) {
    if ([params[TAGS] isKindOfClass:[NSArray class]]) {
        NSArray *tags = [params[TAGS] copy];
        if (tags != nil) {
            NSSet *tagSet = [NSSet setWithArray:tags];
            NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
            [JPUSHService deleteTags:tagSet completion:^(NSInteger iResCode, NSSet *iTags, NSInteger seq) {
                NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq), TAGS: [iTags allObjects] ?: @[]};
                [self sendTagAliasEvent:data];
            } seq:sequence];
        }
    }
}

RCT_EXPORT_METHOD(cleanTags:(NSDictionary *)params) {
    NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
    [JPUSHService cleanTags:^(NSInteger iResCode, NSSet *iTags, NSInteger seq) {
        (void)iTags;
        NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq)};
        [self sendTagAliasEvent:data];
    } seq:sequence];
}

RCT_EXPORT_METHOD(getAllTags:(NSDictionary *)params) {
    NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
    [JPUSHService getAllTags:^(NSInteger iResCode, NSSet *iTags, NSInteger seq) {
        NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq), TAGS: [iTags allObjects] ?: @[]};
        [self sendTagAliasEvent:data];
    } seq:sequence];
}

RCT_EXPORT_METHOD(validTag:(NSDictionary *)params) {
    if (params[TAG]) {
        NSString *tag = params[TAG];
        NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
        [JPUSHService validTag:tag completion:^(NSInteger iResCode, NSSet *iTags, NSInteger seq, BOOL isBind) {
            (void)iTags;
            NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq), TAG_ENABLE: @(isBind), TAG: tag};
            [self sendTagAliasEvent:data];
        } seq:sequence];
    }
}

RCT_EXPORT_METHOD(setAlias:(NSDictionary *)params) {
    if (params[ALIAS]) {
        NSString *alias = params[ALIAS];
        NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
        [JPUSHService setAlias:alias completion:^(NSInteger iResCode, NSString *iAlias, NSInteger seq) {
            NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq), ALIAS: iAlias ?: @""};
            [self sendTagAliasEvent:data];
        } seq:sequence];
    }
}

RCT_EXPORT_METHOD(deleteAlias:(NSDictionary *)params) {
    NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
    [JPUSHService deleteAlias:^(NSInteger iResCode, NSString *iAlias, NSInteger seq) {
        (void)iAlias;
        NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq)};
        [self sendTagAliasEvent:data];
    } seq:sequence];
}

RCT_EXPORT_METHOD(getAlias:(NSDictionary *)params) {
    NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
    [JPUSHService getAlias:^(NSInteger iResCode, NSString *iAlias, NSInteger seq) {
        NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq), ALIAS: iAlias ?: @""};
        [self sendTagAliasEvent:data];
    } seq:sequence];
}

RCT_EXPORT_METHOD(setProperties:(NSDictionary *)params) {
    if ([params[PROS] isKindOfClass:[NSDictionary class]]) {
        NSDictionary *properties = params[PROS];
        NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
        [JPUSHService setProperties:properties completion:^(NSInteger iResCode, NSDictionary *props, NSInteger seq) {
            NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq), PROS: props ?: @{}};
            [self sendTagAliasEvent:data];
            [self sendPropertiesEvent:data];
        } seq:sequence];
    }
}

RCT_EXPORT_METHOD(deleteProperties:(NSDictionary *)params) {
    if ([params[PROS] isKindOfClass:[NSDictionary class]]) {
        NSDictionary *properties = params[PROS];
        NSSet *set = [NSSet setWithArray:properties.allKeys];
        NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
        [JPUSHService deleteProperties:set completion:^(NSInteger iResCode, NSDictionary *props, NSInteger seq) {
            NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq), PROS: props ?: @{}};
            [self sendTagAliasEvent:data];
        } seq:sequence];
    }
}

RCT_EXPORT_METHOD(cleanProperties:(NSDictionary *)params) {
    NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
    [JPUSHService cleanProperties:^(NSInteger iResCode, NSDictionary *props, NSInteger seq) {
        NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq), PROS: props ?: @{}};
        [self sendTagAliasEvent:data];
    } seq:sequence];
}

RCT_EXPORT_METHOD(pageEnterTo:(NSString *)pageName) {
    [JPUSHService pageEnterTo:pageName];
}

RCT_EXPORT_METHOD(pageLeave:(NSString *)pageName) {
    [JPUSHService pageLeave:pageName];
}

RCT_EXPORT_METHOD(setCollectControl:(NSDictionary *)params) {
    JPushCollectControl *control = [[JPushCollectControl alloc] init];
    BOOL gps = YES;
    BOOL cell = YES;
    BOOL bssid = YES;
    BOOL ssid = YES;
    if ([params[@"gps"] isKindOfClass:[NSNumber class]]) {
        gps = [params[@"gps"] boolValue];
    }
    if ([params[@"cell"] isKindOfClass:[NSNumber class]]) {
        cell = [params[@"cell"] boolValue];
    }
    if ([params[@"bssid"] isKindOfClass:[NSNumber class]]) {
        bssid = [params[@"bssid"] boolValue];
    }
    if ([params[@"ssid"] isKindOfClass:[NSNumber class]]) {
        ssid = [params[@"ssid"] boolValue];
    }
    control.gps = gps;
    control.cell = cell;
    control.bssid = bssid;
    control.ssid = ssid;
    [JPUSHService setCollectControl:control];
}

RCT_EXPORT_METHOD(setSmartPushEnable:(BOOL)enable) {
    [JPUSHService setSmartPushEnable:enable];
}

RCT_EXPORT_METHOD(setBackgroundEnable:(BOOL)enable) {
    [JPUSHService setBackgroundEnable:enable];
}

- (void)jPushInAppMessageDidShow:(JPushInAppMessage *)inAppMessage {
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onInAppMessageShow type=%@ msgId=%@",
                                 INAPP_MESSAGE_SHOW, inAppMessage.mesageId ?: @""]];
    NSDictionary *responseData = [self convertInappMsg:inAppMessage isShow:YES];
    [RCTJPushModule emitEventWithName:INAPP_MESSAGE_EVENT payload:responseData];
}

- (void)jPushInAppMessageDidClick:(JPushInAppMessage *)inAppMessage {
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onInAppMessageClick type=%@ msgId=%@",
                                 INAPP_MESSAGE_CLICK, inAppMessage.mesageId ?: @""]];
    NSDictionary *responseData = [self convertInappMsg:inAppMessage isShow:NO];
    [RCTJPushModule emitEventWithName:INAPP_MESSAGE_EVENT payload:responseData];
}

RCT_EXPORT_METHOD(setBadge:(NSDictionary *)params) {
    if ([params[BADGE] isKindOfClass:[NSNumber class]]) {
        NSInteger badgeValue = [params[BADGE] integerValue];
        if (badgeValue >= 0) {
            [JPUSHService setBadge:badgeValue];
        }
    }
    if ([params[APP_BADGE] isKindOfClass:[NSNumber class]]) {
        NSInteger appBadgeValue = [params[APP_BADGE] integerValue];
        if (appBadgeValue >= 0) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [UIApplication sharedApplication].applicationIconBadgeNumber = appBadgeValue;
            });
        }
    }
}

- (void)sendPropertiesEvent:(NSDictionary *)data {
    [RCTJPushModule emitEventWithName:PROPERTIES_EVENT payload:data];
}

RCT_EXPORT_METHOD(setMobileNumber:(NSDictionary *)params) {
    if (params[MOBILE_NUMBER]) {
        NSString *number = params[MOBILE_NUMBER];
        NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
        [JPUSHService setMobileNumber:number completion:^(NSError *error) {
            NSInteger code = error ? error.code : 0;
            NSDictionary *data = @{CODE: @(code), SEQUENCE: @(sequence)};
            [self sendMobileNumberEvent:data];
        }];
    }
}

RCT_EXPORT_METHOD(crashLogON:(NSDictionary *)params) {
    (void)params;
    [JPUSHService crashLogON];
}

RCT_EXPORT_METHOD(addNotification:(NSDictionary *)params) {
    NSString *messageID = params[MESSAGE_ID] ?: @"";
    JPushNotificationContent *content = [[JPushNotificationContent alloc] init];
    NSString *notificationTitle = params[TITLE] ?: @"";
    NSString *notificationContent = params[CONTENT] ?: @"";
    content.title = notificationTitle;
    content.body = notificationContent;
    if (@available(iOS 15.0, *)) {
        content.interruptionLevel = 1;
    }
    if (params[EXTRAS]) {
        content.userInfo = @{
            MESSAGE_ID: messageID,
            TITLE: notificationTitle,
            CONTENT: notificationContent,
            EXTRAS: params[EXTRAS]
        };
    } else {
        content.userInfo = @{
            MESSAGE_ID: messageID,
            TITLE: notificationTitle,
            CONTENT: notificationContent
        };
    }
    NSString *broadcastTime = params[BROADCASTTIME];
    JPushNotificationTrigger *trigger = [[JPushNotificationTrigger alloc] init];
    NSDate *now = [NSDate date];
    if ([broadcastTime isKindOfClass:[NSString class]] && broadcastTime.length > 0) {
        now = [NSDate dateWithTimeIntervalSince1970:[broadcastTime doubleValue] / 1000.0];
    }
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSCalendarUnit unitFlags = NSCalendarUnitYear | NSCalendarUnitMonth | NSCalendarUnitDay |
                               NSCalendarUnitHour | NSCalendarUnitMinute | NSCalendarUnitSecond;
    NSDateComponents *components = [calendar components:unitFlags fromDate:now];
    components.second = components.second + 1;
    if (@available(iOS 10.0, *)) {
        trigger.dateComponents = components;
    } else {
        return;
    }
    JPushNotificationRequest *request = [[JPushNotificationRequest alloc] init];
    request.requestIdentifier = messageID;
    request.content = content;
    request.trigger = trigger;
    [JPUSHService addNotification:request];
}

RCT_EXPORT_METHOD(removeNotification:(NSDictionary *)params) {
    NSString *requestIdentifier = params[MESSAGE_ID];
    if ([requestIdentifier isKindOfClass:[NSString class]]) {
        JPushNotificationIdentifier *identifier = [[JPushNotificationIdentifier alloc] init];
        identifier.identifiers = @[requestIdentifier];
        if (@available(iOS 10.0, *)) {
            identifier.delivered = YES;
        }
        [JPUSHService removeNotification:identifier];
    }
}

RCT_EXPORT_METHOD(clearLocalNotifications) {
    [JPUSHService removeNotification:nil];
}

RCT_EXPORT_METHOD(removeGeofenceWithIdentifier:(NSDictionary *)params) {
    if (params[GEO_FENCE_ID]) {
        [JPUSHService removeGeofenceWithIdentifier:params[GEO_FENCE_ID]];
    }
}

RCT_EXPORT_METHOD(setGeofeneceMaxCount:(NSDictionary *)params) {
    if (params[GEO_FENCE_MAX_NUMBER]) {
        [JPUSHService setGeofeneceMaxCount:[params[GEO_FENCE_MAX_NUMBER] integerValue]];
    }
}

- (NSArray<NSString *> *)supportedEvents {
    return @[
        CONNECT_EVENT,
        NOTIFICATION_EVENT,
        CUSTOM_MESSAGE_EVENT,
        LOCAL_NOTIFICATION_EVENT,
        TAG_ALIAS_EVENT,
        PROPERTIES_EVENT,
        MOBILE_NUMBER_EVENT,
        INAPP_MESSAGE_EVENT
    ];
}

- (void)sendConnectEvent:(NSNotification *)data {
    NSDictionary *responseData = [self convertConnect:data];
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onConnected state=%@",
                                 [responseData[CONNECT_ENABLE] boolValue] ? @"true" : @"false"]];
    [RCTJPushModule emitEventWithName:CONNECT_EVENT payload:responseData];
}

- (void)sendApnsNotificationEvent:(NSNotification *)data {
    NSDictionary *responseData = [self convertApnsMessage:data];
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onNotifyMessage type=%@ notificationType=remote msgId=%@",
                                 responseData[NOTIFICATION_EVENT_TYPE] ?: @"",
                                 responseData[MESSAGE_ID] ?: @""]];
    [RCTJPushModule emitEventWithName:NOTIFICATION_EVENT payload:responseData];
    if ([RCTJPushEventQueue sharedInstance]._notificationQueue.count > 0) {
        [[RCTJPushEventQueue sharedInstance]._notificationQueue removeAllObjects];
    }
}

- (void)sendApnsNotificationEventByDictionary:(NSDictionary *)data {
    NSDictionary *responseData = [self convertApnsMessage:data];
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onNotifyMessage type=%@ notificationType=remote msgId=%@",
                                 responseData[NOTIFICATION_EVENT_TYPE] ?: @"",
                                 responseData[MESSAGE_ID] ?: @""]];
    [RCTJPushModule emitEventWithName:NOTIFICATION_EVENT payload:responseData];
    if ([RCTJPushEventQueue sharedInstance]._notificationQueue.count > 0) {
        [[RCTJPushEventQueue sharedInstance]._notificationQueue removeAllObjects];
    }
}

- (void)sendLocalNotificationEventByDictionary:(NSDictionary *)data {
    NSDictionary *responseData = [self convertLocalMessage:data];
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onLocalNotification type=%@ notificationType=local msgId=%@",
                                 responseData[NOTIFICATION_EVENT_TYPE] ?: @"",
                                 responseData[MESSAGE_ID] ?: @""]];
    [RCTJPushModule emitEventWithName:LOCAL_NOTIFICATION_EVENT payload:responseData];
    if ([RCTJPushEventQueue sharedInstance]._localNotificationQueue.count > 0) {
        [[RCTJPushEventQueue sharedInstance]._localNotificationQueue removeAllObjects];
    }
}

- (void)sendCustomNotificationEvent:(NSNotification *)data {
    NSDictionary *responseData = [self convertCustomMessage:data];
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onMessage type=customMessage msgId=%@",
                                 responseData[MESSAGE_ID] ?: @""]];
    [RCTJPushModule emitEventWithName:CUSTOM_MESSAGE_EVENT payload:responseData];
}

- (void)sendLocalNotificationEvent:(NSNotification *)data {
    NSDictionary *responseData = [self convertLocalMessage:data];
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onLocalNotification type=%@ notificationType=local msgId=%@",
                                 responseData[NOTIFICATION_EVENT_TYPE] ?: @"",
                                 responseData[MESSAGE_ID] ?: @""]];
    [RCTJPushModule emitEventWithName:LOCAL_NOTIFICATION_EVENT payload:responseData];
}

- (void)sendTagAliasEvent:(NSDictionary *)data {
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onTagAliasOperatorResult seq=%@ code=%@",
                                 data[SEQUENCE] ?: @0, data[CODE] ?: @0]];
    [RCTJPushModule emitEventWithName:TAG_ALIAS_EVENT payload:data];
}

- (void)sendMobileNumberEvent:(NSDictionary *)data {
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onMobileNumberOperatorResult seq=%@ code=%@",
                                 data[SEQUENCE] ?: @0, data[CODE] ?: @0]];
    [RCTJPushModule emitEventWithName:MOBILE_NUMBER_EVENT payload:data];
}

- (NSDictionary *)convertConnect:(NSNotification *)data {
    NSNotificationName notificationName = data.name;
    BOOL isConnect = [notificationName isEqualToString:kJPFNetworkDidLoginNotification];
    return @{CONNECT_ENABLE: @(isConnect)};
}

- (NSDictionary *)convertApnsMessage:(id)data {
    NSNotificationName notificationName = J_APNS_NOTIFICATION_ARRIVED_EVENT;
    NSDictionary *objectData = @{};
    if ([data isKindOfClass:[NSNotification class]]) {
        notificationName = [(NSNotification *)data name];
        if ([[(NSNotification *)data object] isKindOfClass:[NSDictionary class]]) {
            objectData = [(NSNotification *)data object];
        }
    } else if ([data isKindOfClass:[NSDictionary class]]) {
        notificationName = J_APNS_NOTIFICATION_OPENED_EVENT;
        objectData = data;
    }
    NSString *notificationEventType = [notificationName isEqualToString:J_APNS_NOTIFICATION_OPENED_EVENT]
        ? NOTIFICATION_OPENED
        : NOTIFICATION_ARRIVED;
    id alertData = objectData[@"aps"][@"alert"];
    NSString *badge = objectData[@"aps"][@"badge"] ? [objectData[@"aps"][@"badge"] stringValue] : @"";
    NSString *sound = objectData[@"aps"][@"sound"] ?: @"";

    NSString *title = @"";
    NSString *content = @"";
    if ([alertData isKindOfClass:[NSString class]]) {
        content = alertData;
    } else if ([alertData isKindOfClass:[NSDictionary class]]) {
        title = alertData[@"title"] ?: @"";
        content = alertData[@"body"] ?: @"";
    }
    NSMutableDictionary *copyData = [NSMutableDictionary dictionaryWithDictionary:objectData];
    [copyData removeObjectForKey:@"_j_business"];
    [copyData removeObjectForKey:@"_j_uid"];
    [copyData removeObjectForKey:@"_j_msgid"];
    [copyData removeObjectForKey:@"aps"];

    NSMutableDictionary *extrasData = [NSMutableDictionary dictionary];
    for (NSString *key in copyData.allKeys) {
        id value = copyData[key];
        if (value != nil) {
            extrasData[key] = value;
        }
    }
    NSString *messageID = objectData[@"_j_msgid"] ? [objectData[@"_j_msgid"] description] : @"";
    if (extrasData.count > 0) {
        return @{
            MESSAGE_ID: messageID,
            TITLE: title,
            CONTENT: content,
            BADGE: badge,
            RING: sound,
            EXTRAS: extrasData,
            NOTIFICATION_EVENT_TYPE: notificationEventType
        };
    }
    return @{
        MESSAGE_ID: messageID,
        TITLE: title,
        CONTENT: content,
        BADGE: badge,
        RING: sound,
        NOTIFICATION_EVENT_TYPE: notificationEventType
    };
}

- (NSDictionary *)convertLocalMessage:(id)data {
    NSNotificationName notificationName = J_LOCAL_NOTIFICATION_ARRIVED_EVENT;
    NSDictionary *objectData = @{};
    if ([data isKindOfClass:[NSNotification class]]) {
        notificationName = [(NSNotification *)data name];
        if ([[(NSNotification *)data object] isKindOfClass:[NSDictionary class]]) {
            objectData = [(NSNotification *)data object];
        }
    } else if ([data isKindOfClass:[NSDictionary class]]) {
        notificationName = J_LOCAL_NOTIFICATION_OPENED_EVENT;
        objectData = data;
    }
    NSString *notificationEventType = [notificationName isEqualToString:J_LOCAL_NOTIFICATION_OPENED_EVENT]
        ? NOTIFICATION_OPENED
        : NOTIFICATION_ARRIVED;
    NSString *messageID = objectData[MESSAGE_ID] ?: @"";
    NSString *title = objectData[TITLE] ?: @"";
    NSString *content = objectData[CONTENT] ?: @"";
    if (objectData[EXTRAS]) {
        return @{
            MESSAGE_ID: messageID,
            TITLE: title,
            CONTENT: content,
            EXTRAS: objectData[EXTRAS],
            NOTIFICATION_EVENT_TYPE: notificationEventType
        };
    }
    return @{
        MESSAGE_ID: messageID,
        TITLE: title,
        CONTENT: content,
        NOTIFICATION_EVENT_TYPE: notificationEventType
    };
}

- (NSDictionary *)convertCustomMessage:(NSNotification *)data {
    NSDictionary *objectData = [data.object isKindOfClass:[NSDictionary class]] ? data.object : @{};
    NSString *messageID = objectData[@"_j_msgid"] ? [objectData[@"_j_msgid"] description] : @"";
    NSString *title = objectData[@"title"] ?: @"";
    NSString *content = objectData[@"content"] ?: @"";
    if (objectData[@"extras"]) {
        return @{
            MESSAGE_ID: messageID,
            TITLE: title,
            CONTENT: content,
            EXTRAS: objectData[@"extras"]
        };
    }
    return @{
        MESSAGE_ID: messageID,
        TITLE: title,
        CONTENT: content
    };
}

- (NSDictionary *)convertInappMsg:(JPushInAppMessage *)inAppMessage isShow:(BOOL)isShow {
    return @{
        @"mesageId": inAppMessage.mesageId ?: @"",
        @"title": inAppMessage.title ?: @"",
        @"content": inAppMessage.content ?: @"",
        @"target": inAppMessage.target ?: @[],
        @"clickAction": inAppMessage.clickAction ?: @"",
        @"extras": inAppMessage.extras ?: @{},
        INAPP_MESSAGE_EVENT_TYPE: isShow ? INAPP_MESSAGE_SHOW : INAPP_MESSAGE_CLICK
    };
}

@end
