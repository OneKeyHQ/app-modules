#if __has_include("JPUSHService.h")
#import "JPUSHService.h"
#else
#import "../include/JPUSHService.h"
#endif
#if __has_include("JCOREConfiguration.h")
#import "JCOREConfiguration.h"
#import "JCORETcpSession.h"
#import "JCOREWireProtocol.h"
#else
#import "../jcore/JCOREConfiguration.h"
#import "../jcore/JCORETcpSession.h"
#import "../jcore/JCOREWireProtocol.h"
#endif
#import "JPUSHNotificationManager.h"
#import <UserNotifications/UserNotifications.h>

static NSString *JPushHexFromData(NSData *data) {
    if (data.length == 0) {
        return @"";
    }
    const unsigned char *bytes = data.bytes;
    NSMutableString *hex = [NSMutableString stringWithCapacity:data.length * 2];
    for (NSUInteger i = 0; i < data.length; i++) {
        [hex appendFormat:@"%02x", bytes[i]];
    }
    return hex;
}

@implementation JPUSHService

+ (void)setupWithOption:(NSDictionary *)launchingOption
                 appKey:(NSString *)appKey
                channel:(NSString *)channel
       apsForProduction:(BOOL)isProduction {
    [self setupWithOption:launchingOption
                   appKey:appKey
                  channel:channel
         apsForProduction:isProduction
    advertisingIdentifier:nil];
}

+ (void)setupWithOption:(NSDictionary *)launchingOption
                 appKey:(NSString *)appKey
                channel:(NSString *)channel
       apsForProduction:(BOOL)isProduction
  advertisingIdentifier:(NSString *)advertisingId {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    [config configureWithAppKey:appKey ?: @""
                        channel:channel
                   isProduction:isProduction
          advertisingIdentifier:advertisingId];

#if __has_include(<UIKit/UIKit.h>)
    NSDictionary *remoteNotification = launchingOption[UIApplicationLaunchOptionsRemoteNotificationKey];
    if ([remoteNotification isKindOfClass:[NSDictionary class]]) {
        [self handleRemoteNotification:remoteNotification];
    }
#endif

    [[JCORETcpSession shared] startIfNeeded];
}

+ (void)registerForRemoteNotificationTypes:(NSUInteger)types
                                categories:(NSSet *)categories {
    [[JPUSHNotificationManager shared] registerTypes:types categories:categories];
}

+ (void)registerForRemoteNotificationConfig:(JPUSHRegisterEntity *)config
                                   delegate:(id<JPUSHRegisterDelegate>)delegate {
    [[JPUSHNotificationManager shared] registerWithConfig:config delegate:delegate];
}

+ (void)registerDeviceToken:(NSData *)deviceToken {
    NSString *tokenHex = JPushHexFromData(deviceToken);
    [JCOREConfiguration shared].deviceTokenHex = tokenHex;
    [[JCORETcpSession shared] reportDeviceTokenIfNeeded];
}

+ (void)registerVoipToken:(NSData *)voipToken {
    NSString *voipHex = JPushHexFromData(voipToken);
    [JCOREConfiguration shared].voipTokenHex = voipHex;
    [[JCORETcpSession shared] reportVoipTokenIfNeeded];
}

+ (void)registerLiveActivity:(NSString *)activityAttributes
            pushToStartToken:(NSData *)pushToStartToken
                  completion:(JPUSHLiveActivityTokenCompletion)completion
                         seq:(NSInteger)seq {
    NSString *tokenHex = JPushHexFromData(pushToStartToken);
    NSData *body = [JCOREWireProtocol buildLiveActivityPayloadWithTokenHex:tokenHex
                                                     aliasOrAttributesType:activityAttributes ?: @""
                                                               isPushToken:NO];
    [[JCORETcpSession shared] sendCommand:JCOREWireCommandLiveActivity
                                  version:1
                                 bodyData:body
                               completion:^(JCOREWireFrame *responseFrame, NSError *error) {
        if (completion != nil) {
            JCORELiveActivityResponse *parsed = [JCOREWireProtocol parseLiveActivityResponse:responseFrame];
            NSInteger resCode = error != nil
                ? error.code
                : (parsed != nil ? parsed.mappedErrorCode : (NSInteger)responseFrame.statusCode);
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(resCode, activityAttributes, pushToStartToken, seq);
            });
        }
    }];
}

+ (void)registerLiveActivity:(NSString *)liveActivityType
                   pushToken:(NSData *)pushToken
                  completion:(JPUSHLiveActivityTokenCompletion)completion
                         seq:(NSInteger)seq {
    [self registerLiveActivityId:liveActivityType
                       pushToken:pushToken
                      completion:completion
                             seq:seq];
}

+ (void)registerLiveActivityId:(NSString *)liveActivityId
                     pushToken:(NSData *)pushToken
                    completion:(JPUSHLiveActivityTokenCompletion)completion
                           seq:(NSInteger)seq {
    NSString *tokenHex = JPushHexFromData(pushToken);
    NSData *body = [JCOREWireProtocol buildLiveActivityPayloadWithTokenHex:tokenHex
                                                     aliasOrAttributesType:liveActivityId ?: @""
                                                               isPushToken:YES];
    [[JCORETcpSession shared] sendCommand:JCOREWireCommandLiveActivity
                                  version:1
                                 bodyData:body
                               completion:^(JCOREWireFrame *responseFrame, NSError *error) {
        if (completion != nil) {
            JCORELiveActivityResponse *parsed = [JCOREWireProtocol parseLiveActivityResponse:responseFrame];
            NSInteger resCode = error != nil
                ? error.code
                : (parsed != nil ? parsed.mappedErrorCode : (NSInteger)responseFrame.statusCode);
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(resCode, liveActivityId, pushToken, seq);
            });
        }
    }];
}

+ (void)handleRemoteNotification:(NSDictionary *)remoteInfo {
    if (![remoteInfo isKindOfClass:[NSDictionary class]]) {
        return;
    }
    id msgIdObj = remoteInfo[@"_j_msgid"];
    if ([msgIdObj respondsToSelector:@selector(unsignedLongLongValue)]) {
        uint64_t msgId = [msgIdObj unsignedLongLongValue];
        [[JCORETcpSession shared] sendReceiptForMessageType:4 messageId:msgId];
    }
}

+ (void)handleVoipNotification:(NSDictionary *)remoteInfo {
    [self handleRemoteNotification:remoteInfo];
}

+ (void)setNotiInMessageDelegate:(id<JPUSHNotiInMessageDelegate>)notiInMessageDelegate {
    [JPUSHNotificationManager shared].notiInMessageDelegate = notiInMessageDelegate;
}

+ (void)setInAppMessageDelegate:(id<JPUSHInAppMessageDelegate>)inAppMessageDelegate {
    [JPUSHNotificationManager shared].inAppMessageDelegate = inAppMessageDelegate;
}

+ (void)registerLbsGeofenceDelegate:(id<JPUSHGeofenceDelegate>)delegate
                  withLaunchOptions:(NSDictionary *)launchOptions {
    (void)launchOptions;
    [JPUSHNotificationManager shared].geofenceDelegate = delegate;
}

+ (void)setGeofenceMaxCount:(NSInteger)count {
    (void)count;
}

+ (void)setGeofeneceMaxCount:(NSInteger)count {
    (void)count;
}

+ (void)setGeofenecePeriodForInside:(NSInteger)seconds {
    (void)seconds;
}

+ (void)removeGeofenceWithIdentifier:(NSString *)geofenceId {
    (void)geofenceId;
}

+ (void)setLocationEanable:(BOOL)isEanble {
    (void)isEanble;
}

+ (void)setLatitude:(double)latitude longitude:(double)longitude {
    (void)latitude;
    (void)longitude;
}

+ (void)setLocation:(id)location {
    (void)location;
}

+ (void)pullInAppMessageWithPage:(NSString *)pageName
                      completion:(void (^)(NSInteger iResCode))completion {
    (void)pageName;
    if (completion != nil) {
        dispatch_async(dispatch_get_main_queue(), ^{
            completion(0);
        });
    }
}

+ (void)addNotification:(JPushNotificationRequest *)request {
    [[JPUSHNotificationManager shared] addNotification:request];
}

+ (void)removeNotification:(JPushNotificationIdentifier *)identifier {
    [[JPUSHNotificationManager shared] removeNotification:identifier];
}

+ (void)findNotification:(JPushNotificationIdentifier *)identifier {
    [[JPUSHNotificationManager shared] findNotification:identifier];
}

+ (UILocalNotification *)setLocalNotification:(NSDate *)fireDate
                                    alertBody:(NSString *)alertBody
                                        badge:(int)badge
                                  alertAction:(NSString *)alertAction
                                identifierKey:(NSString *)notificationKey
                                     userInfo:(NSDictionary *)userInfo
                                    soundName:(NSString *)soundName {
    return [self setLocalNotification:fireDate
                            alertBody:alertBody
                                badge:badge
                          alertAction:alertAction
                        identifierKey:notificationKey
                             userInfo:userInfo
                            soundName:soundName
                               region:nil
                   regionTriggersOnce:YES
                             category:nil];
}

+ (UILocalNotification *)setLocalNotification:(NSDate *)fireDate
                                    alertBody:(NSString *)alertBody
                                        badge:(int)badge
                                  alertAction:(NSString *)alertAction
                                identifierKey:(NSString *)notificationKey
                                     userInfo:(NSDictionary *)userInfo
                                    soundName:(NSString *)soundName
                                       region:(CLRegion *)region
                           regionTriggersOnce:(BOOL)regionTriggersOnce
                                     category:(NSString *)category {
    JPushNotificationRequest *request = [[JPushNotificationRequest alloc] init];
    request.requestIdentifier = notificationKey.length > 0 ? notificationKey : [NSUUID UUID].UUIDString;

    JPushNotificationContent *content = [[JPushNotificationContent alloc] init];
    content.body = alertBody;
    content.action = alertAction;
    content.userInfo = userInfo;
    content.sound = soundName;
    content.categoryIdentifier = category;
    if (badge >= 0) {
        content.badge = @(badge);
    }
    request.content = content;

    JPushNotificationTrigger *trigger = [[JPushNotificationTrigger alloc] init];
    trigger.fireDate = fireDate;
    trigger.region = region;
    trigger.repeat = !regionTriggersOnce;
    request.trigger = trigger;

    [self addNotification:request];
    return nil;
}

+ (void)showLocalNotificationAtFront:(UILocalNotification *)notification
                       identifierKey:(NSString *)notificationKey {
    (void)notification;
    (void)notificationKey;
}

+ (void)deleteLocalNotificationWithIdentifierKey:(NSString *)notificationKey {
    if (notificationKey.length == 0) {
        return;
    }
    JPushNotificationIdentifier *identifier = [[JPushNotificationIdentifier alloc] init];
    identifier.identifiers = @[ notificationKey ];
    [self removeNotification:identifier];
}

+ (void)deleteLocalNotification:(UILocalNotification *)localNotification {
    (void)localNotification;
}

+ (NSArray *)findLocalNotificationWithIdentifier:(NSString *)notificationKey {
    (void)notificationKey;
    return @[];
}

+ (void)clearAllLocalNotifications {
    [self removeNotification:nil];
}

+ (BOOL)setBadge:(NSInteger)value {
    NSInteger clamped = MAX((NSInteger)0, value);
    dispatch_async(dispatch_get_main_queue(), ^{
#if __has_include(<UIKit/UIKit.h>)
        if ([NSBundle mainBundle].bundleIdentifier.length > 0) {
            [UIApplication sharedApplication].applicationIconBadgeNumber = clamped;
        }
#else
        (void)clamped;
#endif
    });
    [[JCOREHttpReporter shared] reportBadgeValue:clamped completion:nil];
    return YES;
}

+ (void)resetBadge {
    [self setBadge:0];
}

+ (NSString *)registrationID {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    return config.registrationID.length > 0 ? config.registrationID : @"";
}

+ (void)registrationIDCompletionHandler:(void (^)(int resCode, NSString *registrationID))completionHandler {
    if (completionHandler == nil) {
        return;
    }
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSString *regId = config.registrationID.length > 0 ? config.registrationID : @"";
    int code = regId.length > 0 ? 0 : 1011;
    dispatch_async(dispatch_get_main_queue(), ^{
        completionHandler(code, regId);
    });
}

+ (void)setDebugMode {
    [JCOREConfiguration shared].debugEnabled = YES;
    [JCOREConfiguration shared].logOff = NO;
}

+ (void)setLogOFF {
    [JCOREConfiguration shared].logOff = YES;
    [JCOREConfiguration shared].debugEnabled = NO;
}

+ (void)crashLogON {
}

+ (void)setCollectControl:(JPushCollectControl *)control {
    (void)control;
}

+ (void)setUserid:(NSString *)userId {
    (void)userId;
}

+ (void)setTimedaley:(NSInteger)time {
    (void)time;
}

#pragma mark - Tags & Alias

+ (NSSet *)filterValidTags:(NSSet *)tags {
    if (![tags isKindOfClass:[NSSet class]]) {
        return [NSSet set];
    }
    NSMutableSet<NSString *> *filtered = [NSMutableSet set];
    for (id item in tags) {
        if ([item isKindOfClass:[NSString class]] && [(NSString *)item length] > 0 && [(NSString *)item length] <= 40) {
            [filtered addObject:item];
        }
    }
    return filtered;
}

+ (void)performTagOperationWithOperator:(NSString *)opString
                                   tags:(NSSet<NSString *> *)tags
                                    seq:(NSInteger)seq
                             completion:(JPUSHTagsOperationCompletion)completion {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSSet<NSString *> *validInput = [self filterValidTags:tags];
    NSMutableSet<NSString *> *updated = [NSMutableSet setWithSet:[config storedTags]];

    if ([opString isEqualToString:@"add"]) {
        [updated unionSet:validInput];
        [config saveStoredTags:updated];
    } else if ([opString isEqualToString:@"set"]) {
        updated = [NSMutableSet setWithSet:validInput];
        [config saveStoredTags:updated];
    } else if ([opString isEqualToString:@"del"]) {
        [updated minusSet:validInput];
        [config saveStoredTags:updated];
    } else if ([opString isEqualToString:@"clean"]) {
        [updated removeAllObjects];
        [config saveStoredTags:updated];
    }

    NSData *body = [JCOREWireProtocol buildTagOperationPayloadWithOperator:opString
                                                                      tags:validInput.allObjects
                                                                  currPage:1];
    [[JCORETcpSession shared] sendCommand:JCOREWireCommandTagOperation
                                  version:1
                                 bodyData:body
                               completion:^(JCOREWireFrame *responseFrame, NSError *error) {
        if (completion != nil) {
            NSDictionary *respJson = [JCOREWireProtocol parseJsonBodyFromFrame:responseFrame];
            NSInteger resCode = error != nil
                ? error.code
                : (respJson[@"code"] != nil ? [respJson[@"code"] integerValue] : (NSInteger)responseFrame.statusCode);
            NSSet<NSString *> *resultTags = [opString isEqualToString:@"get"] ? [updated copy] : [validInput copy];
            if ([opString isEqualToString:@"get"] && [respJson[@"tags"] isKindOfClass:[NSArray class]]) {
                resultTags = [NSSet setWithArray:respJson[@"tags"]];
                [config saveStoredTags:resultTags];
            }
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(resCode, resultTags, seq);
            });
        }
    }];
}

+ (void)setTags:(NSSet<NSString *> *)tags
     completion:(JPUSHTagsOperationCompletion)completion
            seq:(NSInteger)seq {
    [self performTagOperationWithOperator:@"set" tags:tags seq:seq completion:completion];
}

+ (void)addTags:(NSSet<NSString *> *)tags
     completion:(JPUSHTagsOperationCompletion)completion
            seq:(NSInteger)seq {
    [self performTagOperationWithOperator:@"add" tags:tags seq:seq completion:completion];
}

+ (void)deleteTags:(NSSet<NSString *> *)tags
        completion:(JPUSHTagsOperationCompletion)completion
               seq:(NSInteger)seq {
    [self performTagOperationWithOperator:@"del" tags:tags seq:seq completion:completion];
}

+ (void)cleanTags:(JPUSHTagsOperationCompletion)completion
              seq:(NSInteger)seq {
    [self performTagOperationWithOperator:@"clean" tags:[NSSet set] seq:seq completion:completion];
}

+ (void)getAllTags:(JPUSHTagsOperationCompletion)completion
               seq:(NSInteger)seq {
    [self performTagOperationWithOperator:@"get" tags:[NSSet set] seq:seq completion:completion];
}

+ (void)validTag:(NSString *)tag
      completion:(JPUSHTagValidOperationCompletion)completion
             seq:(NSInteger)seq {
    NSSet<NSString *> *stored = [[JCOREConfiguration shared] storedTags];
    BOOL localBind = tag.length > 0 && [stored containsObject:tag];
    NSSet<NSString *> *tagSet = tag.length > 0 ? [NSSet setWithObject:tag] : [NSSet set];
    NSData *body = [JCOREWireProtocol buildTagOperationPayloadWithOperator:@"valid"
                                                                      tags:(tag.length > 0 ? @[ tag ] : @[])
                                                                  currPage:1];
    [[JCORETcpSession shared] sendCommand:JCOREWireCommandTagOperation
                                  version:1
                                 bodyData:body
                               completion:^(JCOREWireFrame *responseFrame, NSError *error) {
        if (completion != nil) {
            NSDictionary *respJson = [JCOREWireProtocol parseJsonBodyFromFrame:responseFrame];
            NSInteger resCode = error != nil
                ? error.code
                : (respJson[@"code"] != nil ? [respJson[@"code"] integerValue] : (NSInteger)responseFrame.statusCode);
            BOOL isBind = respJson[@"bind"] != nil ? [respJson[@"bind"] boolValue] : localBind;
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(resCode, tagSet, seq, isBind);
            });
        }
    }];
}

+ (void)performAliasOperationWithOperator:(NSString *)opString
                                    alias:(NSString *)alias
                                      seq:(NSInteger)seq
                               completion:(JPUSHAliasOperationCompletion)completion {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSString *resultAlias = alias ?: @"";
    if ([opString isEqualToString:@"set"]) {
        [config saveStoredAlias:resultAlias];
    } else if ([opString isEqualToString:@"del"]) {
        [config saveStoredAlias:nil];
        resultAlias = @"";
    } else if ([opString isEqualToString:@"get"]) {
        resultAlias = [config storedAlias] ?: @"";
    }

    NSData *body = [JCOREWireProtocol buildAliasOperationPayloadWithOperator:opString
                                                                       alias:resultAlias];
    [[JCORETcpSession shared] sendCommand:JCOREWireCommandAliasOperation
                                  version:1
                                 bodyData:body
                               completion:^(JCOREWireFrame *responseFrame, NSError *error) {
        if (completion != nil) {
            NSDictionary *respJson = [JCOREWireProtocol parseJsonBodyFromFrame:responseFrame];
            NSInteger resCode = error != nil
                ? error.code
                : (respJson[@"code"] != nil ? [respJson[@"code"] integerValue] : (NSInteger)responseFrame.statusCode);
            NSString *finalAlias = resultAlias;
            if ([opString isEqualToString:@"get"] && [respJson[@"alias"] isKindOfClass:[NSString class]]) {
                finalAlias = respJson[@"alias"];
                [config saveStoredAlias:finalAlias];
            }
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(resCode, finalAlias, seq);
            });
        }
    }];
}

+ (void)setAlias:(NSString *)alias
      completion:(JPUSHAliasOperationCompletion)completion
             seq:(NSInteger)seq {
    [self performAliasOperationWithOperator:@"set" alias:alias seq:seq completion:completion];
}

+ (void)deleteAlias:(JPUSHAliasOperationCompletion)completion
                seq:(NSInteger)seq {
    [self performAliasOperationWithOperator:@"del" alias:@"" seq:seq completion:completion];
}

+ (void)getAlias:(JPUSHAliasOperationCompletion)completion
             seq:(NSInteger)seq {
    [self performAliasOperationWithOperator:@"get" alias:@"" seq:seq completion:completion];
}

+ (void)setTags:(NSSet *)tags
          alias:(NSString *)alias
callbackSelector:(SEL)cbSelector
         target:(id)theTarget {
    (void)cbSelector;
    (void)theTarget;
    if (tags != nil) {
        [self setTags:tags completion:nil seq:0];
    }
    if (alias != nil) {
        [self setAlias:alias completion:nil seq:0];
    }
}

+ (void)setTags:(NSSet *)tags
          alias:(NSString *)alias
callbackSelector:(SEL)cbSelector
         object:(id)theTarget {
    [self setTags:tags alias:alias callbackSelector:cbSelector target:theTarget];
}

+ (void)setTags:(NSSet *)tags
callbackSelector:(SEL)cbSelector
         object:(id)theTarget {
    [self setTags:tags alias:nil callbackSelector:cbSelector target:theTarget];
}

+ (void)setTags:(NSSet *)tags
          alias:(NSString *)alias
fetchCompletionHandle:(void (^)(int iResCode, NSSet *iTags, NSString *iAlias))completionHandler {
    if (tags != nil) {
        [self setTags:tags completion:nil seq:0];
    }
    if (alias != nil) {
        [self setAlias:alias completion:nil seq:0];
    }
    if (completionHandler != nil) {
        dispatch_async(dispatch_get_main_queue(), ^{
            completionHandler(0, tags, alias);
        });
    }
}

+ (void)setTags:(NSSet *)tags
aliasInbackground:(NSString *)alias {
    [self setTags:tags alias:alias fetchCompletionHandle:nil];
}

+ (void)setAlias:(NSString *)alias
callbackSelector:(SEL)cbSelector
          object:(id)theTarget {
    [self setTags:nil alias:alias callbackSelector:cbSelector target:theTarget];
}

#pragma mark - Properties & Mobile Number

+ (void)setProperties:(NSDictionary *)properties
           completion:(JPUSHPropertiesOperationCompletion)completion
                  seq:(NSInteger)seq {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSMutableDictionary *merged = [NSMutableDictionary dictionaryWithDictionary:[config storedProperties]];
    if ([properties isKindOfClass:[NSDictionary class]]) {
        [merged addEntriesFromDictionary:properties];
    }
    [config saveStoredProperties:merged];

    NSData *body = [JCOREWireProtocol buildUserPropertiesPayloadWithOperator:@"set"
                                                                  properties:(properties ?: @{})];
    [[JCORETcpSession shared] sendCommand:JCOREWireCommandPropertyOperation
                                  version:1
                                 bodyData:body
                               completion:^(JCOREWireFrame *responseFrame, NSError *error) {
        if (completion != nil) {
            NSDictionary *respJson = [JCOREWireProtocol parseJsonBodyFromFrame:responseFrame];
            NSInteger resCode = error != nil
                ? error.code
                : (respJson[@"code"] != nil ? [respJson[@"code"] integerValue] : (NSInteger)responseFrame.statusCode);
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(resCode, properties ?: @{}, seq);
            });
        }
    }];
}

+ (void)deleteProperties:(NSSet<NSString *> *)keys
              completion:(JPUSHPropertiesOperationCompletion)completion
                     seq:(NSInteger)seq {
    JCOREConfiguration *config = [JCOREConfiguration shared];
    NSMutableDictionary *merged = [NSMutableDictionary dictionaryWithDictionary:[config storedProperties]];
    NSMutableDictionary *delDict = [NSMutableDictionary dictionary];
    for (NSString *key in keys) {
        if ([key isKindOfClass:[NSString class]]) {
            [merged removeObjectForKey:key];
            delDict[key] = @"";
        }
    }
    [config saveStoredProperties:merged];

    NSData *body = [JCOREWireProtocol buildUserPropertiesPayloadWithOperator:@"del"
                                                                  properties:delDict];
    [[JCORETcpSession shared] sendCommand:JCOREWireCommandPropertyOperation
                                  version:1
                                 bodyData:body
                               completion:^(JCOREWireFrame *responseFrame, NSError *error) {
        if (completion != nil) {
            NSDictionary *respJson = [JCOREWireProtocol parseJsonBodyFromFrame:responseFrame];
            NSInteger resCode = error != nil
                ? error.code
                : (respJson[@"code"] != nil ? [respJson[@"code"] integerValue] : (NSInteger)responseFrame.statusCode);
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(resCode, [merged copy], seq);
            });
        }
    }];
}

+ (void)cleanProperties:(JPUSHPropertiesOperationCompletion)completion
                    seq:(NSInteger)seq {
    [[JCOREConfiguration shared] saveStoredProperties:@{}];
    NSData *body = [JCOREWireProtocol buildUserPropertiesPayloadWithOperator:@"clean"
                                                                  properties:@{}];
    [[JCORETcpSession shared] sendCommand:JCOREWireCommandPropertyOperation
                                  version:1
                                 bodyData:body
                               completion:^(JCOREWireFrame *responseFrame, NSError *error) {
        if (completion != nil) {
            NSDictionary *respJson = [JCOREWireProtocol parseJsonBodyFromFrame:responseFrame];
            NSInteger resCode = error != nil
                ? error.code
                : (respJson[@"code"] != nil ? [respJson[@"code"] integerValue] : (NSInteger)responseFrame.statusCode);
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(resCode, @{}, seq);
            });
        }
    }];
}

+ (void)setMobileNumber:(NSString *)mobileNumber
             completion:(void (^)(NSError *error))completion {
    [[JCOREConfiguration shared] saveStoredMobileNumber:mobileNumber];
    NSData *body = [JCOREWireProtocol buildMobileNumberPayload:mobileNumber ?: @""];
    [[JCORETcpSession shared] sendCommand:JCOREWireCommandMobileNumber
                                  version:0
                                 bodyData:body
                               completion:^(JCOREWireFrame *responseFrame, NSError *error) {
        if (completion != nil) {
            NSError *finalError = error;
            if (finalError == nil && responseFrame != nil && responseFrame.statusCode != 0) {
                finalError = [NSError errorWithDomain:@"cn.jiguang.jpush"
                                                 code:responseFrame.statusCode
                                             userInfo:nil];
            }
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(finalError);
            });
        }
    }];
}

#pragma mark - Page Analytics & Push Switch

+ (void)startLogPageView:(NSString *)pageName {
    (void)pageName;
}

+ (void)stopLogPageView:(NSString *)pageName {
    (void)pageName;
}

+ (void)beginLogPageView:(NSString *)pageName duration:(int)seconds {
    (void)pageName;
    (void)seconds;
}

+ (void)pageEnterTo:(NSString *)pageName {
    (void)pageName;
}

+ (void)pageLeave:(NSString *)pageName {
    (void)pageName;
}

+ (void)requestNotificationAuthorization:(void (^)(JPAuthorizationStatus status))completion {
    [[JPUSHNotificationManager shared] requestAuthorization:completion];
}

+ (void)openSettingsForNotification:(void (^)(BOOL success))completionHandler {
    [[JPUSHNotificationManager shared] openNotificationSettings:completionHandler];
}

+ (void)setHeartBeatTimeInterval:(NSInteger)interval {
    NSInteger clamped = MIN(MAX(interval, (NSInteger)30), (NSInteger)290);
    [JCOREConfiguration shared].heartbeatInterval = (NSTimeInterval)clamped;
}

+ (void)setPushEnable:(BOOL)isEnable completion:(void (^)(NSInteger iResCode))completion {
    if (isEnable) {
        [self turnOnPush];
    } else {
        [self turnOFFPush];
    }
    if (completion != nil) {
        dispatch_async(dispatch_get_main_queue(), ^{
            completion(0);
        });
    }
}

+ (void)setBackgroundEnable:(BOOL)isEnable {
    (void)isEnable;
}

+ (void)setSmartPushEnable:(BOOL)isEnable {
    (void)isEnable;
}

+ (void)setDataInsightsEnable:(BOOL)isEnable {
    (void)isEnable;
}

+ (void)turnOnPush {
    [JCOREConfiguration shared].pushStopped = NO;
    NSData *body = [JCOREWireProtocol buildPushControlPayloadWithStopped:NO];
    [[JCORETcpSession shared] startIfNeeded];
    [[JCORETcpSession shared] sendCommand:JCOREWireCommandPushCtrl version:1 bodyData:body completion:nil];
}

+ (void)turnOFFPush {
    [JCOREConfiguration shared].pushStopped = YES;
    NSData *body = [JCOREWireProtocol buildPushControlPayloadWithStopped:YES];
    [[JCORETcpSession shared] sendCommand:JCOREWireCommandPushCtrl version:1 bodyData:body completion:nil];
}

@end
