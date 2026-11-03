#import <TargetConditionals.h>
#if TARGET_OS_SIMULATOR
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
#import "RecoveredPushServiceDependencies.h"
#import "RecoveredPushServiceLiterals.h"
#import <objc/message.h>

#import "JCOREIntegrate.h"
@interface JPUSHService ()
+ (void)commonSetupWithOption:(NSDictionary *)option appKey:(NSString *)key channel:(NSString *)channel apsForProduction:(BOOL)production advertisingIdentifier:(NSString *)identifier;
@end
#define RP_RECORD() [JCOREService apiRecord:NSStringFromClass(self) methodName:NSStringFromSelector(_cmd)]

@implementation JPUSHService
// The vendor simulator slices intentionally leave the APNs-facing methods inactive.
// Their method surface and callback behavior differ from the device implementation.

- (void)userNotificationCenter:(UNUserNotificationCenter * __unsafe_unretained)center willPresentNotification:(UNNotification * __unsafe_unretained)notification withCompletionHandler:(void (^ __unsafe_unretained)(UNNotificationPresentationOptions))completion {}

- (void)userNotificationCenter:(UNUserNotificationCenter * __unsafe_unretained)center didReceiveNotificationResponse:(UNNotificationResponse * __unsafe_unretained)response withCompletionHandler:(void (^ __unsafe_unretained)(void))completion {}

- (void)userNotificationCenter:(UNUserNotificationCenter * __unsafe_unretained)center openSettingsForNotification:(UNNotification * __unsafe_unretained)notification {}

- (void)checkNotiAuth:(void (^ __unsafe_unretained)(JPAuthorizationStatus))completion {
    id retainedCompletion=(__bridge id)(__bridge void *)completion;if(!retainedCompletion||[retainedCompletion isKindOfClass:NSNull.class])return;
    void (^validCompletion)(JPAuthorizationStatus)=retainedCompletion;
    if(@available(iOS 10.0,*)){
        Class centerClass=NSClassFromString(@"UNUserNotificationCenter");SEL current=NSSelectorFromString(@"currentNotificationCenter");
        if(!centerClass||![centerClass respondsToSelector:current]){validCompletion(0);return;}
        NSMethodSignature *signature=[centerClass methodSignatureForSelector:current];if(!signature){validCompletion(0);return;}
        NSInvocation *invocation=[NSInvocation invocationWithMethodSignature:signature];invocation.target=centerClass;invocation.selector=current;[invocation invoke];__unsafe_unretained id center=nil;[invocation getReturnValue:&center];
        SEL get=NSSelectorFromString(@"getNotificationSettingsWithCompletionHandler:");if(![center respondsToSelector:get]){validCompletion(0);return;}signature=[center methodSignatureForSelector:get];if(!signature){validCompletion(0);return;}
        invocation=[NSInvocation invocationWithMethodSignature:signature];invocation.target=center;invocation.selector=get;
        void (^settingsCompletion)(id)=^(id settings){dispatch_async(dispatch_get_main_queue(),^{SEL statusSelector=NSSelectorFromString(@"authorizationStatus");JPAuthorizationStatus status=0;if([settings respondsToSelector:statusSelector]){NSUInteger (*send)(id,SEL,SEL)=(NSUInteger (*)(id,SEL,SEL))objc_msgSend;status=send(settings,@selector(performSelector:),statusSelector);}validCompletion(status);});};
        [invocation setArgument:&settingsCompletion atIndex:2];[invocation invoke];
    }else if(@available(iOS 8.0,*))validCompletion([UIApplication sharedApplication].currentUserNotificationSettings.types?2:1);
    else validCompletion([UIApplication sharedApplication].enabledRemoteNotificationTypes?2:1);
}

- (void)receiveCheckNotiAuth:(NSNotification * __unsafe_unretained)notification {}

- (void)notificationAuthorization:(JPAuthorizationStatus)status withInfo:(NSDictionary * __unsafe_unretained)info {}

+ (void)setupWithOption:(NSDictionary *)option appKey:(NSString *)key channel:(NSString *)channel apsForProduction:(BOOL)production {
    [JPUSHService commonSetupWithOption:option appKey:key channel:channel apsForProduction:production advertisingIdentifier:nil];RP_RECORD();
}

+ (void)setupWithOption:(NSDictionary *)option appKey:(NSString *)key channel:(NSString *)channel apsForProduction:(BOOL)production advertisingIdentifier:(NSString *)identifier {
    [JPUSHService commonSetupWithOption:option appKey:key channel:channel apsForProduction:production advertisingIdentifier:identifier];RP_RECORD();
}

+ (void)setupWithOption:(NSDictionary *)option { RP_ACTION_0000c13c(); }

+ (void)commonSetupWithOption:(NSDictionary *)option appKey:(NSString *)key channel:(NSString *)channel apsForProduction:(BOOL)production advertisingIdentifier:(NSString *)identifier {
    NSString *version=[NSString stringWithFormat:REC_PUSH_cf_______________________JPushSDKVersion_____build__d______________________________JCoreLibVersion_____build__d___________________________AppKey___________________________________________________________________________________,REC_PUSH_LOG_PREFIX,[JPUSHHelper JPUSHDisplayVersion],413,[JCOREIntegrate jcoreDisplayVersion],[JCOREIntegrate jcoreBuildVersion],key];
    REC_PUSH_LOG(YES,2,0x487,@"%@",version);
    REC_PUSH_LOG(YES,4,0x488,@"JPush WARNING! APNs is NOT supported on simulator");
}

+ (void)registerDeviceToken:(NSData * __unsafe_unretained)token {}

+ (void)registerVoipToken:(NSData * __unsafe_unretained)token {}

+ (void)registerLiveActivity:(NSString * __unsafe_unretained)activity pushToStartToken:(NSData * __unsafe_unretained)token completion:(__unsafe_unretained JPUSHLiveActivityTokenCompletion)completion seq:(NSInteger)seq {}

+ (void)registerLiveActivity:(NSString * __unsafe_unretained)activity pushToken:(NSData * __unsafe_unretained)token completion:(__unsafe_unretained JPUSHLiveActivityTokenCompletion)completion seq:(NSInteger)seq {}

+ (void)registerForRemoteNotificationTypes:(NSUInteger)types categories:(NSSet * __unsafe_unretained)categories {}

+ (void)registerForRemoteNotificationConfig:(JPUSHRegisterEntity * __unsafe_unretained)config delegate:(__unsafe_unretained id<JPUSHRegisterDelegate>)delegate {}

+ (void)handleRemoteNotification:(NSDictionary * __unsafe_unretained)info {}

+ (void)handleVoipNotification:(NSDictionary * __unsafe_unretained)info {}

+ (void)requestNotificationAuthorization:(void (^ __unsafe_unretained)(JPAuthorizationStatus))completion {}

+ (void)openSettingsForNotification:(void (^ __unsafe_unretained)(BOOL))completion {}

+ (void)setTags:(NSSet * __unsafe_unretained)tags alias:(NSString * __unsafe_unretained)alias callbackSelector:(SEL)selector object:(__unsafe_unretained id)target {}

+ (void)setTags:(NSSet * __unsafe_unretained)tags callbackSelector:(SEL)selector object:(__unsafe_unretained id)target {}

+ (void)setAlias:(NSString * __unsafe_unretained)alias callbackSelector:(SEL)selector object:(__unsafe_unretained id)target {}

+ (void)setTags:(NSSet * __unsafe_unretained)tags alias:(NSString * __unsafe_unretained)alias callbackSelector:(SEL)selector target:(__unsafe_unretained id)target {}

+ (void)setTags:(NSSet * __unsafe_unretained)tags alias:(NSString * __unsafe_unretained)alias fetchCompletionHandle:(void (^ __unsafe_unretained)(int,NSSet *,NSString * __unsafe_unretained))completion {}

+ (void)setTags:(NSSet * __unsafe_unretained)tags aliasInbackground:(NSString * __unsafe_unretained)alias {}

+ (void)addTags:(NSSet * __unsafe_unretained)tags completion:(__unsafe_unretained JPUSHTagsOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)setTags:(NSSet * __unsafe_unretained)tags completion:(__unsafe_unretained JPUSHTagsOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)deleteTags:(NSSet * __unsafe_unretained)tags completion:(__unsafe_unretained JPUSHTagsOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)cleanTags:(__unsafe_unretained JPUSHTagsOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)getAllTags:(__unsafe_unretained JPUSHTagsOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)validTag:(NSString * __unsafe_unretained)tag completion:(__unsafe_unretained JPUSHTagValidOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)setAlias:(NSString * __unsafe_unretained)alias completion:(__unsafe_unretained JPUSHAliasOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)deleteAlias:(__unsafe_unretained JPUSHAliasOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)getAlias:(__unsafe_unretained JPUSHAliasOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)startLogPageView:(NSString * __unsafe_unretained)page {}

+ (void)stopLogPageView:(NSString * __unsafe_unretained)page {}

+ (void)beginLogPageView:(NSString * __unsafe_unretained)page duration:(int)duration {}

+ (NSSet * __unsafe_unretained)filterValidTags:(NSSet * __unsafe_unretained)tags { return nil; }

+ (void)setProperties:(NSDictionary * __unsafe_unretained)properties completion:(__unsafe_unretained JPUSHPropertiesOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)deleteProperties:(NSSet * __unsafe_unretained)keys completion:(__unsafe_unretained JPUSHPropertiesOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)cleanProperties:(__unsafe_unretained JPUSHPropertiesOperationCompletion)completion seq:(NSInteger)seq {}

+ (void)setNotiInMessageDelegate:(__unsafe_unretained id<JPUSHNotiInMessageDelegate>)delegate {}

+ (void)setInAppMessageDelegate:(__unsafe_unretained id<JPUSHInAppMessageDelegate>)delegate {}

+ (void)pageEnterTo:(NSString * __unsafe_unretained)page {}

+ (void)pageLeave:(NSString * __unsafe_unretained)page {}

+ (void)crashLogON {}

+ (void)setDebugMode {}

+ (void)setLogOFF {}

+ (void)setCollectControl:(JPushCollectControl * __unsafe_unretained)control {}

+ (void)handleDataCollectControl {}

+ (void)setHeartBeatTimeInterval:(NSInteger)interval {}

+ (void)setLocationEanable:(BOOL)enable {}

+ (void)setPushEnable:(BOOL)enable completion:(void (^ __unsafe_unretained)(NSInteger))completion {}

+ (void)setBackgroundEnable:(BOOL)enable {}

+ (void)setSmartPushEnable:(BOOL)enable {}

+ (void)setDataInsightsEnable:(BOOL)enable {}

+ (void)setLatitude:(double)latitude longitude:(double)longitude {}

+ (void)setLocation:(CLLocation * __unsafe_unretained)location {}

+ (NSString * __unsafe_unretained)registrationID { return nil; }

+ (void)registrationIDCompletionHandler:(void (^ __unsafe_unretained)(int,NSString *))completion { id retainedCompletion=(__bridge id)(__bridge void *)completion;if(retainedCompletion&&![retainedCompletion isKindOfClass:NSNull.class])((void (^)(int,NSString *))retainedCompletion)(1011,nil); }

+ (void)addNotification:(JPushNotificationRequest * __unsafe_unretained)request {}

+ (void)removeNotification:(JPushNotificationIdentifier * __unsafe_unretained)identifier {}

+ (void)findNotification:(JPushNotificationIdentifier * __unsafe_unretained)identifier {}

+ (UILocalNotification * __unsafe_unretained)setLocalNotification:(NSDate * __unsafe_unretained)fireDate alertBody:(NSString * __unsafe_unretained)body badge:(int)badge alertAction:(NSString * __unsafe_unretained)action identifierKey:(NSString * __unsafe_unretained)key userInfo:(NSDictionary * __unsafe_unretained)info soundName:(NSString * __unsafe_unretained)sound { return nil; }

+ (UILocalNotification * __unsafe_unretained)setLocalNotification:(NSDate * __unsafe_unretained)fireDate alertBody:(NSString * __unsafe_unretained)body badge:(int)badge alertAction:(NSString * __unsafe_unretained)action identifierKey:(NSString * __unsafe_unretained)key userInfo:(NSDictionary * __unsafe_unretained)info soundName:(NSString * __unsafe_unretained)sound region:(CLRegion * __unsafe_unretained)region regionTriggersOnce:(BOOL)once category:(NSString * __unsafe_unretained)category { return nil; }

+ (void)showLocalNotificationAtFront:(UILocalNotification * __unsafe_unretained)notification identifierKey:(NSString * __unsafe_unretained)key {}

+ (NSArray * __unsafe_unretained)findLocalNotificationWithIdentifier:(NSString * __unsafe_unretained)key { return nil; }

+ (void)deleteLocalNotificationWithIdentifierKey:(NSString * __unsafe_unretained)key {}

+ (void)deleteLocalNotification:(UILocalNotification * __unsafe_unretained)notification {}

+ (void)clearAllLocalNotifications {}

+ (BOOL)setBadge:(NSInteger)badge { return NO; }

+ (void)resetBadge {}

+ (void)setMobileNumber:(NSString *)number completion:(void (^ __unsafe_unretained)(NSError *))completion { id retainedCompletion=(__bridge id)(__bridge void *)completion;if(retainedCompletion&&![retainedCompletion isKindOfClass:NSNull.class])((void (^)(NSError *))retainedCompletion)([NSError errorWithDomain:@"JPUSHErrorDomain" code:-1 userInfo:@{NSLocalizedDescriptionKey:@"Setting mobile number is not supported on the simulator"}]); }

+ (void)setGeofeneceMaxCount:(NSInteger)count {}

+ (void)setGeofenecePeriodForInside:(NSInteger)period {}

+ (void)registerLbsGeofenceDelegate:(__unsafe_unretained id<JPUSHGeofenceDelegate>)delegate withLaunchOptions:(NSDictionary * __unsafe_unretained)option {}

+ (void)removeGeofenceWithIdentifier:(NSString * __unsafe_unretained)identifier {}

@end
#endif
