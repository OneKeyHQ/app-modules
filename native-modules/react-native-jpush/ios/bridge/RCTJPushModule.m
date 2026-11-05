#import "RCTJPushModule.h"
#if __has_include("JCOREConfiguration.h")
#import "JCOREConfiguration.h"
#else
#import "../jcore/JCOREConfiguration.h"
#endif
#if __has_include("JPUSHNotificationManager.h")
#import "JPUSHNotificationManager.h"
#elif __has_include("../jpush/JPUSHNotificationManager.h")
#import "../jpush/JPUSHNotificationManager.h"
#endif
#import <UIKit/UIKit.h>
#import <UserNotifications/UserNotifications.h>

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
static NSHashTable<RCTJPushModule *> *sActiveModules = nil;
static NSMutableArray<NSDictionary *> *sPendingEvents = nil;
static NSMutableArray<NSDictionary *> *sPendingNitroEvents = nil;
static NSMutableDictionary<NSNumber *, RCTJPushNitroEventListener> *sNitroListeners = nil;
static NSInteger sNextNitroListenerId = 1;
static RCTJPushModule *sNotificationObserverHelper = nil;

@interface NSObject (RCTJPushBridgelessCompat)
- (instancetype)initWithDisabledObservation;
- (nullable id)callableJSModules;
- (void)invokeModule:(NSString *)moduleName method:(NSString *)methodName withArgs:(NSArray *)args;
- (void)sendEventWithName:(NSString *)eventName body:(id)body;
@end

@interface RCTJPushModule () <RCTBridgeModule, JPUSHRegisterDelegate, JPUSHInAppMessageDelegate> {
    BOOL _hasListeners;
    BOOL _invalidated;
}
@property (nonatomic, weak, nullable) RCTBridge *bridge;
@end

@implementation RCTJPushModule

@synthesize bridge = _bridge;

RCT_EXPORT_MODULE(JPushModule);

+ (void)registerActiveModule:(RCTJPushModule *)module {
    if (module == nil || module->_invalidated) {
        return;
    }
    @synchronized (RCTJPushModule.class) {
        if (sActiveModules == nil) {
            sActiveModules = [NSHashTable weakObjectsHashTable];
        }
        [sActiveModules addObject:module];
        sSharedJPushModule = module;
    }
}

+ (NSArray<RCTJPushModule *> *)allActiveModules {
    @synchronized (RCTJPushModule.class) {
        NSMutableArray<RCTJPushModule *> *list = [NSMutableArray array];
        if (sActiveModules != nil) {
            for (RCTJPushModule *mod in sActiveModules) {
                if (mod != nil && ![list containsObject:mod]) {
                    [list addObject:mod];
                }
            }
        }
        RCTJPushModule *shared = sSharedJPushModule;
        if (shared != nil && ![list containsObject:shared]) {
            [list addObject:shared];
        }
        return [list copy];
    }
}

+ (void)clearActiveModules {
    @synchronized (RCTJPushModule.class) {
        [sActiveModules removeAllObjects];
        [sNitroListeners removeAllObjects];
        [sPendingEvents removeAllObjects];
        [sPendingNitroEvents removeAllObjects];
        sSharedJPushModule = nil;
    }
}

+ (BOOL)isContentEvent:(NSString *)name {
    return [@[NOTIFICATION_EVENT, LOCAL_NOTIFICATION_EVENT, CUSTOM_MESSAGE_EVENT,
               INAPP_MESSAGE_EVENT, @"NotifyButtonClickEvent"] containsObject:name];
}

+ (BOOL)canDeliverEvent:(NSString *)name generation:(NSUInteger)generation {
    return ![self isContentEvent:name] || [[JCOREConfiguration shared] allowsPolicyGeneration:generation];
}

+ (void)policyDidChange:(NSNotification *)notification {
    (void)notification;
    @synchronized (RCTJPushModule.class) {
        NSUInteger generation = [JCOREConfiguration shared].policyGeneration;
        BOOL allowed = [JCOREConfiguration shared].policyAllowsPush;
        NSIndexSet *events = [sPendingEvents indexesOfObjectsPassingTest:^BOOL(NSDictionary *item, NSUInteger index, BOOL *stop) {
            return [self isContentEvent:item[@"name"]] && (!allowed || [item[@"generation"] unsignedIntegerValue] != generation);
        }];
        [sPendingEvents removeObjectsAtIndexes:events];
        NSIndexSet *nitro = [sPendingNitroEvents indexesOfObjectsPassingTest:^BOOL(NSDictionary *item, NSUInteger index, BOOL *stop) {
            return [self isContentEvent:item[@"name"]] && (!allowed || [item[@"generation"] unsignedIntegerValue] != generation);
        }];
        [sPendingNitroEvents removeObjectsAtIndexes:nitro];
        for (NSMutableArray<NSDictionary *> *queue in @[[RCTJPushEventQueue sharedInstance]._notificationQueue ?: [NSMutableArray array],
                                                       [RCTJPushEventQueue sharedInstance]._localNotificationQueue ?: [NSMutableArray array]]) {
            NSIndexSet *stale = [queue indexesOfObjectsPassingTest:^BOOL(NSDictionary *item, NSUInteger index, BOOL *stop) {
                return !allowed || item[@"_j_InternalPolicyGeneration"] == nil ||
                    [item[@"_j_InternalPolicyGeneration"] unsignedIntegerValue] != generation;
            }];
            [queue removeObjectsAtIndexes:stale];
        }
    }
}

+ (void)ensureSharedNativeObservers {
    @synchronized (RCTJPushModule.class) {
        if (sNotificationObserverHelper == nil) {
            [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(policyDidChange:) name:JCOREPolicyDidChangeNotification object:nil];
            sNotificationObserverHelper = [[RCTJPushModule alloc] initAsSharedObserver];
        }
    }
}

+ (void)setupOrRefreshJPushNativeState {
    [self ensureSharedNativeObservers];
    [sNotificationObserverHelper setupOrRefreshJPushNativeState];
}

+ (NSInteger)registerNitroEventListener:(RCTJPushNitroEventListener)listener {
    if (listener == nil) {
        return 0;
    }
    [self ensureSharedNativeObservers];
    NSInteger listenerId = 0;
    @synchronized (RCTJPushModule.class) {
        if (sNitroListeners == nil) {
            sNitroListeners = [NSMutableDictionary dictionary];
        }
        listenerId = sNextNitroListenerId++;
        sNitroListeners[@(listenerId)] = [listener copy];
    }
    [self flushQueuedNotifications];
    return listenerId;
}

+ (void)unregisterNitroEventListener:(NSInteger)listenerId {
    if (listenerId <= 0) {
        return;
    }
    @synchronized (RCTJPushModule.class) {
        [sNitroListeners removeObjectForKey:@(listenerId)];
    }
}

+ (NSString *)dictionaryToJsonString:(NSDictionary *)dict {
    if (![dict isKindOfClass:[NSDictionary class]] || dict.count == 0) {
        return @"{}";
    }
    @try {
        if ([NSJSONSerialization isValidJSONObject:dict]) {
            NSData *data = [NSJSONSerialization dataWithJSONObject:dict options:0 error:nil];
            if (data.length > 0) {
                NSString *json = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
                if (json.length > 0) {
                    return json;
                }
            }
        }
    } @catch (NSException *exception) {
    }
    return @"{}";
}

+ (BOOL)hasReadyEventConsumer {
    @synchronized (RCTJPushModule.class) {
        if (sNitroListeners.count > 0) {
            return YES;
        }
    }
    NSArray<RCTJPushModule *> *modules = [self allActiveModules];
    for (RCTJPushModule *mod in modules) {
        if ([mod canEmitEventsNow]) {
            return YES;
        }
    }
    return NO;
}

- (void)setBridge:(RCTBridge *)bridge {
    _bridge = bridge;
    [RCTJPushModule registerActiveModule:self];
    if (bridge != nil) {
        [RCTJPushModule flushPendingBridgeEvents];
    }
}

- (void)startObserving {
    _hasListeners = YES;
    [RCTJPushModule registerActiveModule:self];
    [RCTJPushModule flushQueuedNotifications];
}

- (void)stopObserving {
    _hasListeners = NO;
}

RCT_EXPORT_METHOD(addListener:(NSString *)eventName) {
    (void)eventName;
    [self startObserving];
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

- (BOOL)canEmitEventsNow {
    if (_invalidated) { return NO; }
    if (self.bridge != nil) {
        return YES;
    }
    if ([self respondsToSelector:@selector(callableJSModules)]) {
        id callableModules = [self callableJSModules];
        if (callableModules != nil && [callableModules respondsToSelector:@selector(invokeModule:method:withArgs:)]) {
            return YES;
        }
    }
    if (_hasListeners && [self respondsToSelector:@selector(sendEventWithName:body:)]) {
        return YES;
    }
    return NO;
}

- (BOOL)emitToDeviceEventEmitter:(NSString *)eventName payload:(NSDictionary *)payload {
    if (_invalidated || eventName.length == 0) {
        return NO;
    }
    NSDictionary *safePayload = payload ?: @{};
    if (self.bridge != nil) {
        [self.bridge enqueueJSCall:@"RCTDeviceEventEmitter"
                            method:@"emit"
                              args:@[eventName, safePayload]
                        completion:NULL];
        return YES;
    }
    if ([self respondsToSelector:@selector(callableJSModules)]) {
        id callableModules = [self callableJSModules];
        if (callableModules != nil && [callableModules respondsToSelector:@selector(invokeModule:method:withArgs:)]) {
            [callableModules invokeModule:@"RCTDeviceEventEmitter"
                                   method:@"emit"
                                 withArgs:@[eventName, safePayload]];
            return YES;
        }
    }
    if (_hasListeners && [self respondsToSelector:@selector(sendEventWithName:body:)]) {
        [self sendEventWithName:eventName body:safePayload];
        return YES;
    }
    return NO;
}

+ (void)emitEventWithName:(NSString *)eventName payload:(NSDictionary *)payload {
    [self emitEventWithName:eventName payload:payload generation:[JCOREConfiguration shared].policyGeneration];
}

+ (void)emitEventWithName:(NSString *)eventName payload:(NSDictionary *)payload generation:(NSUInteger)generation {
    if (eventName.length == 0 || ![self canDeliverEvent:eventName generation:generation]) { return; }
    [self ensureSharedNativeObservers];
    NSDictionary *safePayload = payload ?: @{};
    BOOL emittedToLegacy = NO;
    for (RCTJPushModule *module in [self allActiveModules]) {
        @synchronized (RCTJPushModule.class) {
            if (![self canDeliverEvent:eventName generation:generation]) { return; }
            if ([module emitToDeviceEventEmitter:eventName payload:safePayload]) { emittedToLegacy = YES; }
        }
    }
    NSString *json = [self dictionaryToJsonString:safePayload];
    NSDictionary<NSNumber *, RCTJPushNitroEventListener> *listeners = nil;
    @synchronized (RCTJPushModule.class) {
        if (![self canDeliverEvent:eventName generation:generation]) { return; }
        if (!emittedToLegacy) {
            if (sPendingEvents == nil) { sPendingEvents = [NSMutableArray array]; }
            if (sPendingEvents.count >= 64) { [sPendingEvents removeObjectAtIndex:0]; }
            [sPendingEvents addObject:@{@"name": eventName, @"payload": safePayload, @"generation": @(generation)}];
        }
        listeners = [sNitroListeners copy];
        if (listeners.count == 0) {
            if (sPendingNitroEvents == nil) { sPendingNitroEvents = [NSMutableArray array]; }
            if (sPendingNitroEvents.count >= 64) { [sPendingNitroEvents removeObjectAtIndex:0]; }
            [sPendingNitroEvents addObject:@{@"name": eventName, @"json": json, @"generation": @(generation)}];
        }
    }
    for (NSNumber *listenerId in listeners) {
        @synchronized (RCTJPushModule.class) {
            if ([self canDeliverEvent:eventName generation:generation] && sNitroListeners[listenerId] == listeners[listenerId]) {
                listeners[listenerId](eventName, json);
            }
        }
    }
}

+ (void)flushPendingNitroEvents {
    NSDictionary<NSNumber *, RCTJPushNitroEventListener> *listeners = nil;
    NSArray<NSDictionary *> *snapshot = nil;
    @synchronized (RCTJPushModule.class) {
        if (sNitroListeners.count > 0) {
            listeners = [sNitroListeners copy];
            snapshot = [sPendingNitroEvents copy];
            [sPendingNitroEvents removeAllObjects];
        }
    }
    for (NSDictionary *item in snapshot) {
        for (NSNumber *listenerId in listeners) {
            @synchronized (RCTJPushModule.class) {
                if ([self canDeliverEvent:item[@"name"] generation:[item[@"generation"] unsignedIntegerValue]] &&
                    sNitroListeners[listenerId] == listeners[listenerId]) {
                    listeners[listenerId](item[@"name"], item[@"json"] ?: @"{}");
                }
            }
        }
    }
}

+ (void)flushPendingBridgeEvents {
    [self ensureSharedNativeObservers];
    [self flushPendingNitroEvents];
    NSArray<RCTJPushModule *> *modules = [self allActiveModules];
    BOOL ready = NO;
    for (RCTJPushModule *module in modules) { if ([module canEmitEventsNow]) { ready = YES; break; } }
    if (!ready) { return; }
    NSArray<NSDictionary *> *snapshot = nil;
    @synchronized (RCTJPushModule.class) {
        snapshot = [sPendingEvents copy];
        [sPendingEvents removeAllObjects];
    }
    for (NSDictionary *item in snapshot) {
        for (RCTJPushModule *module in modules) {
            @synchronized (RCTJPushModule.class) {
                if ([self canDeliverEvent:item[@"name"] generation:[item[@"generation"] unsignedIntegerValue]] &&
                    [[self allActiveModules] containsObject:module]) {
                    [module emitToDeviceEventEmitter:item[@"name"] payload:item[@"payload"] ?: @{}];
                }
            }
        }
    }
}

+ (void)flushQueuedNotifications {
    [self ensureSharedNativeObservers];
    [self flushPendingBridgeEvents];

    NSUInteger generation = [JCOREConfiguration shared].policyGeneration;
    if (![JCOREConfiguration shared].policyAllowsPush) { [self policyDidChange:nil]; return; }
    @synchronized (RCTJPushModule.class) {
        RCTJPushModule *converter = sNotificationObserverHelper ?: [self allActiveModules].firstObject;
        NSMutableArray *notificationList = [RCTJPushEventQueue sharedInstance]._notificationQueue;
        if (notificationList.count > 0) {
            NSArray *queuedApns = [notificationList copy];
            [notificationList removeAllObjects];
            for (id obj in queuedApns) {
                if ([[JCOREConfiguration shared] allowsPolicyGeneration:generation] && [obj isKindOfClass:[NSDictionary class]]) {
                    [converter sendApnsNotificationEventByDictionary:(NSDictionary *)obj];
                }
            }
        }
        NSMutableArray *localNotificationList = [RCTJPushEventQueue sharedInstance]._localNotificationQueue;
        if (localNotificationList.count > 0) {
            NSArray *queuedLocal = [localNotificationList copy];
            [localNotificationList removeAllObjects];
            for (id obj in queuedLocal) {
                if ([[JCOREConfiguration shared] allowsPolicyGeneration:generation] && [obj isKindOfClass:[NSDictionary class]]) {
                    [converter sendLocalNotificationEventByDictionary:(NSDictionary *)obj];
                }
            }
        }
    }
}

- (void)registerNotificationCenterObservers {
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

- (instancetype)initAsSharedObserver {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wobjc-designated-initializers"
    if ([self respondsToSelector:@selector(initWithDisabledObservation)]) {
        self = [super initWithDisabledObservation];
    } else {
        self = [super init];
    }
#pragma clang diagnostic pop
    if (self) {
        _hasListeners = NO;
        [self registerNotificationCenterObservers];
    }
    return self;
}

- (instancetype)init {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wobjc-designated-initializers"
    if ([self respondsToSelector:@selector(initWithDisabledObservation)]) {
        self = [super initWithDisabledObservation];
    } else {
        self = [super init];
    }
#pragma clang diagnostic pop
    if (self) {
        _hasListeners = NO;
        [RCTJPushModule registerActiveModule:self];
        [RCTJPushModule ensureSharedNativeObservers];

        dispatch_async(dispatch_get_main_queue(), ^{
            [RCTJPushModule flushPendingBridgeEvents];
        });
    }
    return self;
}

- (void)invalidate {
    @synchronized (RCTJPushModule.class) {
        _invalidated = YES;
        _hasListeners = NO;
        _bridge = nil;
        [sActiveModules removeObject:self];
        if (sSharedJPushModule == self) { sSharedJPushModule = sActiveModules.allObjects.lastObject; }
    }
    [[NSNotificationCenter defaultCenter] removeObserver:self];
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

- (void)setupOrRefreshJPushNativeState {
    [RCTJPushModule ensureSharedNativeObservers];
    if ([JPUSHNotificationManager shared].registerDelegate == nil) {
        JPUSHRegisterEntity *entity = [[JPUSHRegisterEntity alloc] init];
        entity.types = 0;
        [JPUSHService registerForRemoteNotificationConfig:entity delegate:(id<JPUSHRegisterDelegate>)self];
    }
    if ([JPUSHNotificationManager shared].inAppMessageDelegate == nil) {
        [JPUSHService setInAppMessageDelegate:(id<JPUSHInAppMessageDelegate>)self];
    }
}

RCT_EXPORT_METHOD(initPush) {
    [self setupOrRefreshJPushNativeState];
    [RCTJPushModule flushQueuedNotifications];
}

RCT_EXPORT_METHOD(setupWithConfig:(NSDictionary *)params) {
    [self setupOrRefreshJPushNativeState];
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
    [[JPUSHNotificationManager shared] getAuthorizationStatus:^(JPAuthorizationStatus status) {
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
            [self sendPropertiesEvent:data];
        } seq:sequence];
    }
}

RCT_EXPORT_METHOD(cleanProperties:(NSDictionary *)params) {
    NSInteger sequence = params[SEQUENCE] ? [params[SEQUENCE] integerValue] : -1;
    [JPUSHService cleanProperties:^(NSInteger iResCode, NSDictionary *props, NSInteger seq) {
        NSDictionary *data = @{CODE: @(iResCode), SEQUENCE: @(seq), PROS: props ?: @{}};
        [self sendTagAliasEvent:data];
        [self sendPropertiesEvent:data];
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

- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center
        willPresentNotification:(UNNotification *)notification
          withCompletionHandler:(void (^)(NSInteger))completionHandler {
    (void)center;
    NSUInteger generation = [JCOREConfiguration shared].policyGeneration;
    NSDictionary *userInfo = notification.request.content.userInfo ?: @{};
    NSMutableDictionary *notificationInfo = [NSMutableDictionary dictionaryWithDictionary:userInfo];
    notificationInfo[@"_j_InternalPolicyGeneration"] = @(generation);
    if ([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        [JPUSHService handleRemoteNotification:userInfo];
        [[NSNotificationCenter defaultCenter] postNotificationName:J_APNS_NOTIFICATION_ARRIVED_EVENT
                                                            object:userInfo
                                                          userInfo:notificationInfo];
    } else {
        [[NSNotificationCenter defaultCenter] postNotificationName:J_LOCAL_NOTIFICATION_ARRIVED_EVENT
                                                            object:userInfo
                                                          userInfo:notificationInfo];
    }
    if (completionHandler != nil) {
        completionHandler([[JCOREConfiguration shared] allowsPolicyGeneration:generation]
            ? UNNotificationPresentationOptionBadge | UNNotificationPresentationOptionSound | UNNotificationPresentationOptionAlert
            : UNNotificationPresentationOptionNone);
    }
}

- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center
 didReceiveNotificationResponse:(UNNotificationResponse *)response
          withCompletionHandler:(void (^)(void))completionHandler {
    (void)center;
    NSUInteger generation = [JCOREConfiguration shared].policyGeneration;
    NSDictionary *userInfo = response.notification.request.content.userInfo ?: @{};
    NSMutableDictionary *notificationInfo = [NSMutableDictionary dictionaryWithDictionary:userInfo];
    notificationInfo[@"_j_InternalPolicyGeneration"] = @(generation);
    if ([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        [JPUSHService handleRemoteNotification:userInfo];
        [[NSNotificationCenter defaultCenter] postNotificationName:J_APNS_NOTIFICATION_OPENED_EVENT
                                                            object:userInfo
                                                          userInfo:notificationInfo];
    } else {
        [[NSNotificationCenter defaultCenter] postNotificationName:J_LOCAL_NOTIFICATION_OPENED_EVENT
                                                            object:userInfo
                                                          userInfo:notificationInfo];
    }
    if (completionHandler != nil) {
        completionHandler();
    }
}

- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center
    openSettingsForNotification:(UNNotification *)notification {
    (void)center;
    (void)notification;
}

- (void)jpushNotificationAuthorization:(JPAuthorizationStatus)status withInfo:(NSDictionary *)info {
    (void)status;
    (void)info;
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
    id broadcastTimeObj = params[BROADCASTTIME];
    double rawSec = [broadcastTimeObj respondsToSelector:@selector(doubleValue)] ? [broadcastTimeObj doubleValue] : 0.0;
    JPushNotificationTrigger *trigger = [[JPushNotificationTrigger alloc] init];
    NSTimeInterval interval = 1.0;
    if (rawSec > 0.0) {
        interval = (rawSec > 1000000000000.0) ? (rawSec / 1000.0 - [[NSDate date] timeIntervalSince1970]) : rawSec;
    }
    trigger.timeInterval = MAX(1.0, interval);
    JPushNotificationRequest *request = [[JPushNotificationRequest alloc] init];
    request.requestIdentifier = messageID;
    request.content = content;
    request.trigger = trigger;
    [JPUSHService addNotification:request];
}

RCT_EXPORT_METHOD(removeNotification:(NSDictionary *)params) {
    NSString *requestIdentifier = params[MESSAGE_ID];
    if ([requestIdentifier isKindOfClass:[NSString class]]) {
        JPushNotificationIdentifier *pendingIdentifier = [[JPushNotificationIdentifier alloc] init];
        pendingIdentifier.identifiers = @[requestIdentifier];
        if (@available(iOS 10.0, *)) {
            pendingIdentifier.delivered = NO;
        }
        [JPUSHService removeNotification:pendingIdentifier];

        JPushNotificationIdentifier *deliveredIdentifier = [[JPushNotificationIdentifier alloc] init];
        deliveredIdentifier.identifiers = @[requestIdentifier];
        if (@available(iOS 10.0, *)) {
            deliveredIdentifier.delivered = YES;
        }
        [JPUSHService removeNotification:deliveredIdentifier];
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
    NSUInteger generation = [data.userInfo[@"_j_InternalPolicyGeneration"] respondsToSelector:@selector(unsignedIntegerValue)]
        ? [data.userInfo[@"_j_InternalPolicyGeneration"] unsignedIntegerValue] : [JCOREConfiguration shared].policyGeneration;
    @synchronized (RCTJPushModule.class) {
        if (![[JCOREConfiguration shared] allowsPolicyGeneration:generation]) { return; }
        if (![RCTJPushModule hasReadyEventConsumer]) {
            NSDictionary *objectData = [data.object isKindOfClass:[NSDictionary class]]
                ? data.object
                : ([data.userInfo isKindOfClass:[NSDictionary class]] ? data.userInfo : nil);
            if (objectData != nil) {
                if ([RCTJPushEventQueue sharedInstance]._notificationQueue == nil) {
                    [RCTJPushEventQueue sharedInstance]._notificationQueue = [NSMutableArray array];
                }
                NSMutableDictionary *queuedItem = [NSMutableDictionary dictionaryWithDictionary:objectData];
                queuedItem[@"_j_InternalPolicyGeneration"] = @(generation);
                queuedItem[@"_j_InternalNotificationName"] = data.name ?: J_APNS_NOTIFICATION_OPENED_EVENT;
                [[RCTJPushEventQueue sharedInstance]._notificationQueue addObject:[queuedItem copy]];
            }
            return;
        }
        NSDictionary *responseData = [self convertApnsMessage:data];
        [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onNotifyMessage type=%@ notificationType=remote msgId=%@",
                                     responseData[NOTIFICATION_EVENT_TYPE] ?: @"",
                                     responseData[MESSAGE_ID] ?: @""]];
        [RCTJPushModule emitEventWithName:NOTIFICATION_EVENT payload:responseData generation:generation];
        if ([RCTJPushEventQueue sharedInstance]._notificationQueue.count > 0) {
            [[RCTJPushEventQueue sharedInstance]._notificationQueue removeAllObjects];
        }
    }
}

- (void)sendApnsNotificationEventByDictionary:(NSDictionary *)data {
    NSUInteger generation = [data[@"_j_InternalPolicyGeneration"] respondsToSelector:@selector(unsignedIntegerValue)]
        ? [data[@"_j_InternalPolicyGeneration"] unsignedIntegerValue] : [JCOREConfiguration shared].policyGeneration;
    @synchronized (RCTJPushModule.class) {
        if (![[JCOREConfiguration shared] allowsPolicyGeneration:generation]) { return; }
        NSDictionary *responseData = [self convertApnsMessage:data];
        [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onNotifyMessage type=%@ notificationType=remote msgId=%@",
                                     responseData[NOTIFICATION_EVENT_TYPE] ?: @"",
                                     responseData[MESSAGE_ID] ?: @""]];
        [RCTJPushModule emitEventWithName:NOTIFICATION_EVENT payload:responseData generation:generation];
        if ([RCTJPushEventQueue sharedInstance]._notificationQueue.count > 0) {
            [[RCTJPushEventQueue sharedInstance]._notificationQueue removeAllObjects];
        }
    }
}

- (void)sendLocalNotificationEventByDictionary:(NSDictionary *)data {
    NSUInteger generation = [data[@"_j_InternalPolicyGeneration"] respondsToSelector:@selector(unsignedIntegerValue)]
        ? [data[@"_j_InternalPolicyGeneration"] unsignedIntegerValue] : [JCOREConfiguration shared].policyGeneration;
    @synchronized (RCTJPushModule.class) {
        if (![[JCOREConfiguration shared] allowsPolicyGeneration:generation]) { return; }
        NSDictionary *responseData = [self convertLocalMessage:data];
        [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onLocalNotification type=%@ notificationType=local msgId=%@",
                                     responseData[NOTIFICATION_EVENT_TYPE] ?: @"",
                                     responseData[MESSAGE_ID] ?: @""]];
        [RCTJPushModule emitEventWithName:LOCAL_NOTIFICATION_EVENT payload:responseData generation:generation];
        if ([RCTJPushEventQueue sharedInstance]._localNotificationQueue.count > 0) {
            [[RCTJPushEventQueue sharedInstance]._localNotificationQueue removeAllObjects];
        }
    }
}

- (void)sendCustomNotificationEvent:(NSNotification *)data {
    NSDictionary *objectData = [data.object isKindOfClass:[NSDictionary class]] ? data.object : nil;
    id originGeneration = data.userInfo[@"_j_InternalPolicyGeneration"] ?: objectData[@"_j_InternalPolicyGeneration"];
    NSUInteger generation = [originGeneration respondsToSelector:@selector(unsignedIntegerValue)]
        ? [originGeneration unsignedIntegerValue] : [JCOREConfiguration shared].policyGeneration;
    NSDictionary *responseData = [self convertCustomMessage:data];
    [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onMessage type=customMessage msgId=%@",
                                 responseData[MESSAGE_ID] ?: @""]];
    [RCTJPushModule emitEventWithName:CUSTOM_MESSAGE_EVENT payload:responseData generation:generation];
}

- (void)sendLocalNotificationEvent:(NSNotification *)data {
    NSUInteger generation = [data.userInfo[@"_j_InternalPolicyGeneration"] respondsToSelector:@selector(unsignedIntegerValue)]
        ? [data.userInfo[@"_j_InternalPolicyGeneration"] unsignedIntegerValue] : [JCOREConfiguration shared].policyGeneration;
    @synchronized (RCTJPushModule.class) {
        if (![[JCOREConfiguration shared] allowsPolicyGeneration:generation]) { return; }
        if (![RCTJPushModule hasReadyEventConsumer]) {
            NSDictionary *objectData = [data.object isKindOfClass:[NSDictionary class]]
                ? data.object
                : ([data.userInfo isKindOfClass:[NSDictionary class]] ? data.userInfo : nil);
            if (objectData != nil) {
                if ([RCTJPushEventQueue sharedInstance]._localNotificationQueue == nil) {
                    [RCTJPushEventQueue sharedInstance]._localNotificationQueue = [NSMutableArray array];
                }
                NSMutableDictionary *queuedItem = [NSMutableDictionary dictionaryWithDictionary:objectData];
                queuedItem[@"_j_InternalPolicyGeneration"] = @(generation);
                queuedItem[@"_j_InternalNotificationName"] = data.name ?: J_LOCAL_NOTIFICATION_OPENED_EVENT;
                [[RCTJPushEventQueue sharedInstance]._localNotificationQueue addObject:[queuedItem copy]];
            }
            return;
        }
        NSDictionary *responseData = [self convertLocalMessage:data];
        [JCOREConfiguration logInfo:[NSString stringWithFormat:@"onLocalNotification type=%@ notificationType=local msgId=%@",
                                     responseData[NOTIFICATION_EVENT_TYPE] ?: @"",
                                     responseData[MESSAGE_ID] ?: @""]];
        [RCTJPushModule emitEventWithName:LOCAL_NOTIFICATION_EVENT payload:responseData generation:generation];
        if ([RCTJPushEventQueue sharedInstance]._localNotificationQueue.count > 0) {
            [[RCTJPushEventQueue sharedInstance]._localNotificationQueue removeAllObjects];
        }
    }
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
        NSDictionary *dictData = (NSDictionary *)data;
        NSString *internalName = [dictData[@"_j_InternalNotificationName"] isKindOfClass:[NSString class]]
            ? dictData[@"_j_InternalNotificationName"]
            : nil;
        notificationName = internalName.length > 0 ? internalName : J_APNS_NOTIFICATION_OPENED_EVENT;
        objectData = dictData;
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
    [copyData removeObjectForKey:@"_j_InternalNotificationName"];
    [copyData removeObjectForKey:@"_j_InternalPolicyGeneration"];
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
        NSDictionary *dictData = (NSDictionary *)data;
        NSString *internalName = [dictData[@"_j_InternalNotificationName"] isKindOfClass:[NSString class]]
            ? dictData[@"_j_InternalNotificationName"]
            : nil;
        notificationName = internalName.length > 0 ? internalName : J_LOCAL_NOTIFICATION_OPENED_EVENT;
        NSMutableDictionary *cleanData = [NSMutableDictionary dictionaryWithDictionary:dictData];
        [cleanData removeObjectForKey:@"_j_InternalNotificationName"];
        [cleanData removeObjectForKey:@"_j_InternalPolicyGeneration"];
        objectData = [cleanData copy];
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
