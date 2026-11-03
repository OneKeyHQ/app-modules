#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
#import "JPUSHService.h"
#import "RecoveredPushServiceDependencies.h"
#import "RecoveredPushServiceLiterals.h"
#import <string.h>
#import <objc/message.h>
#pragma clang diagnostic ignored "-Wdeprecated-declarations"

@interface JPUSHService () {
    JPUSHClientController *_client;
    BOOL _isFromBackground;
    BOOL _isFromUserAction;
    BOOL _finishLaunchReceived;
    __unsafe_unretained id<JPUSHRegisterDelegate> _delegate;
    __weak NSDictionary *_launchOptions;
    NSInteger _launchReportCode;
    JPushCollectControl *_collectControl;
}
@property(nonatomic,assign) id<JPUSHRegisterDelegate> delegate;
@property(nonatomic,weak) NSDictionary *launchOptions;
@property(nonatomic) BOOL finishLaunchReceived;
@property(nonatomic) NSInteger launchReportCode;
@property(nonatomic,strong) JPushCollectControl *collectControl;
+ (instancetype)sharedService;
+ (void)commonSetupWithOption:(NSDictionary *)option appKey:(NSString *)key channel:(NSString *)channel apsForProduction:(BOOL)production advertisingIdentifier:(NSString *)identifier;
+ (void)handleDataCollectControl;
- (instancetype)initPushServer;
- (void)setupClient;
- (void)addAppStateNotifications;
- (void)reportLaunch;
- (void)reportInactive;
- (void)setLatitude:(double)latitude longitude:(double)longitude;
- (void)setLocation:(CLLocation *)location;
- (void)handleRemoteNotification:(NSDictionary *)info AtLaunch:(BOOL)launch;
- (NSArray *)getRemoteNotificationReportCode:(NSDictionary *)info AtLaunch:(BOOL)launch;
- (void)handleRemoteNotificationAction:(NSArray *)action;
- (void)updateDeviceToken:(NSData *)token;
- (void)updateVoipToken:(NSData *)token;
- (void)updateLiveActivity:(NSString *)activity pushToStartToken:(NSData *)token completion:(JPUSHLiveActivityTokenCompletion)completion seq:(NSInteger)seq;
- (void)updateLiveActivity:(NSString *)activity pushToken:(NSData *)token completion:(JPUSHLiveActivityTokenCompletion)completion seq:(NSInteger)seq;
- (void)handleVoipNotification:(NSDictionary *)info;
- (void)setTags:(NSSet *)tags alias:(NSString *)alias parameters:(NSArray *)parameters type:(NSUInteger)type;
- (void)tagOperate:(NSSet *)tags type:(long long)type andCompletion:(id)completion andSeq:(long long)seq;
- (void)aliasOperate:(NSString *)alias type:(long long)type andCompletion:(JPUSHAliasOperationCompletion)completion andSeq:(long long)seq;
- (void)propertiesOperate:(NSDictionary *)properties type:(long long)type andCompletion:(JPUSHPropertiesOperationCompletion)completion andSeq:(long long)seq;
- (void)startLogPageView:(NSString *)page;
- (void)stopLogPageView:(NSString *)page;
- (void)beginLogPageView:(NSString *)page duration:(int)duration;
- (void)crashLogON;
- (void)setHeartBeatTimeInterval:(NSInteger)interval;
- (void)locationEnable:(BOOL)enable;
- (void)pushEnable:(BOOL)enable completion:(void (^)(NSInteger))completion;
- (void)setBackgroundEnable:(BOOL)enable;
- (void)setSmartPushEnable:(BOOL)enable;
- (void)setDataInsightsEnable:(BOOL)enable;
- (void)addNotification:(JPushNotificationRequest *)request;
- (void)removeNotification:(JPushNotificationIdentifier *)identifier;
- (void)findNotification:(JPushNotificationIdentifier *)identifier;
- (UILocalNotification *)setLocalNotification:(NSDate *)fireDate alertBody:(NSString *)body badge:(int)badge alertAction:(NSString *)action identifierKey:(NSString *)key userInfo:(NSDictionary *)info soundName:(NSString *)sound region:(CLRegion *)region regionTriggersOnce:(BOOL)once category:(NSString *)category;
- (void)showLocalNotificationAtFront:(UILocalNotification *)notification identifierKey:(NSString *)key;
- (NSArray *)findLocalNotificationWithIdentifier:(NSString *)key;
- (void)deleteLocalNotificationWithIdentifierKey:(NSString *)key;
- (void)deleteLocalNotification:(UILocalNotification *)notification;
- (void)clearAllLocalNotifications;
- (NSString *)getRegistrationID;
- (void)getRegistrationIDCompletionHandler:(void (^)(int,NSString *))completion;
- (void)setBadge:(NSInteger)badge;
- (void)resetBadge;
- (void)setMobileNumber:(NSString *)number completion:(void (^)(NSError *))completion;
- (NSInvocation *)getTagAliasInvocationWithCallback:(SEL)selector target:(id)target;
- (void)standardCallback:(int)code originalTags:(NSSet *)tags alias:(NSString *)alias;
- (BOOL)validateCallbackMethod:(NSMethodSignature *)signature;
- (void)applicationWillTerminate;
- (void)applicationDidEnterBackground;
- (void)applicationWillEnterForeground;
- (void)applicationDidBecomeActive;
- (void)applicationWillResignActive;
- (void)applicationDidFinishLaunch:(NSNotification *)notification;
- (void)checkNotiAuth:(void (^)(JPAuthorizationStatus))completion;
- (void)receiveCheckNotiAuth:(NSNotification *)notification;
- (void)notificationAuthorization:(JPAuthorizationStatus)status withInfo:(NSDictionary *)info;
@end

static JPUSHService *sharedPushService;
static BOOL smartPushEnable=YES;
static BOOL dataInsightsEnable=YES;
static BOOL checkedLaunchNotification;
static unsigned long long launchNotificationMessageID;

#define RP_RECORD() [JCOREService apiRecord:NSStringFromClass(self) methodName:NSStringFromSelector(_cmd)]
#define RP_SCHEDULE(selector,object) [JCOREThread performSel:NSSelectorFromString(selector) target:_client withObject:(object)]

@implementation JPUSHService
@synthesize delegate=_delegate,launchOptions=_launchOptions,finishLaunchReceived=_finishLaunchReceived,launchReportCode=_launchReportCode,collectControl=_collectControl;
+ (instancetype)sharedService {
    static dispatch_once_t once;
    dispatch_once(&once,^{sharedPushService=[[JPUSHService alloc] initPushServer];});
    return sharedPushService;
}
+ (id)allocWithZone:(NSZone *)zone {
    static dispatch_once_t once;
    dispatch_once(&once,^{if(!sharedPushService)sharedPushService=[super allocWithZone:zone];});
    return sharedPushService;
}
- (id)copyWithZone:(NSZone *)zone { return self; }
- (instancetype)init { NSLog(REC_PUSH_cf_warning_JPUSHServiceshouldnotuseinitdirectly);return sharedPushService; }
- (instancetype)initPushServer {
    self=[super init];
    if(self){_collectControl=[[JPushCollectControl alloc] init];_client=[JPUSHClientController sharedClient];[self addAppStateNotifications];}
    return self;
}
- (void)setupClient { RP_SCHEDULE(@"setup",nil); }
- (void)addAppStateNotifications {
    NSNotificationCenter *center=[NSNotificationCenter defaultCenter];
    [center addObserver:self selector:@selector(applicationWillTerminate) name:UIApplicationWillTerminateNotification object:nil];
    [center addObserver:self selector:@selector(applicationWillResignActive) name:UIApplicationWillResignActiveNotification object:nil];
    [center addObserver:self selector:@selector(applicationDidBecomeActive) name:UIApplicationDidBecomeActiveNotification object:nil];
    [center addObserver:self selector:@selector(applicationDidEnterBackground) name:UIApplicationDidEnterBackgroundNotification object:nil];
    [center addObserver:self selector:@selector(applicationWillEnterForeground) name:UIApplicationWillEnterForegroundNotification object:nil];
    [center addObserver:self selector:@selector(applicationDidFinishLaunch:) name:UIApplicationDidFinishLaunchingNotification object:nil];
    [center addObserver:self selector:@selector(receiveCheckNotiAuth:) name:kJPUSHCheckNotiAuthNotification object:nil];
}
- (void)dealloc {
    NSNotificationCenter *center=[NSNotificationCenter defaultCenter];
    for(NSString *name in @[UIApplicationWillTerminateNotification,UIApplicationWillResignActiveNotification,UIApplicationDidBecomeActiveNotification,UIApplicationDidEnterBackgroundNotification,UIApplicationWillEnterForegroundNotification,UIApplicationDidFinishLaunchingNotification,kJPUSHCheckNotiAuthNotification]) [center removeObserver:self name:name object:nil];
}
- (void)reportLaunch { RP_SCHEDULE(@"reportLaunch",nil); }
- (void)reportInactive { RP_SCHEDULE(@"reportInactive",nil); }
- (void)setLatitude:(double)latitude longitude:(double)longitude {
    RP_SCHEDULE(@"setLatitude:",(@{@"lat":@(latitude),@"lng":@(longitude),@"altitude":@"",@"horizontalAccuracy":@"",@"verticalAccuracy":@""}));
}
- (void)setLocation:(CLLocation *)location {
    if(!location){RP_ACTION_00009b64();[JPUSHAutomatedTester PostAutomatedTestStatusNotification:15];return;}
    RP_SCHEDULE(@"setLatitude:",(@{@"lat":@(location.coordinate.latitude),@"lng":@(location.coordinate.longitude),@"altitude":@(location.altitude),@"horizontalAccuracy":@(location.horizontalAccuracy),@"verticalAccuracy":@(location.verticalAccuracy),@"itime":@((long)location.timestamp.timeIntervalSince1970)}));
}
- (void)handleRemoteNotification:(NSDictionary *)info AtLaunch:(BOOL)launch { [self handleRemoteNotificationAction:[self getRemoteNotificationReportCode:info AtLaunch:launch]]; }
- (NSArray *)getRemoteNotificationReportCode:(NSDictionary *)info AtLaunch:(BOOL)launch {
    RP_ACTION_00009ec4((int)launch,[UIApplication sharedApplication].applicationState);
    if(!info.count){[JPUSHAutomatedTester PostAutomatedTestStatusNotification:6];return nil;}
    if(![info[@"_j_msgid"] isKindOfClass:NSNumber.class]){REC_PUSH_LOG(NO,1,0x1f1,REC_PUSH_cf_invalidmessageIdtype_messageId___,info[@"_j_msgid"]);return nil;}
    UIApplicationState state=[UIApplication sharedApplication].applicationState;
    unsigned int code;
    if(launch){
        launchNotificationMessageID=[info[@"_j_msgid"] unsignedLongLongValue];
        if(!launchNotificationMessageID)checkedLaunchNotification=YES;
        _launchReportCode=state==UIApplicationStateBackground?2006:2003;
        code=(unsigned int)_launchReportCode;
    }else{
        if(!checkedLaunchNotification&&_launchReportCode==2003){checkedLaunchNotification=YES;if([info[@"_j_msgid"] unsignedLongLongValue]==launchNotificationMessageID){[JPUSHAutomatedTester PostAutomatedTestStatusNotification:7];return nil;}}
        if(state==UIApplicationStateBackground){
            code=_isFromUserAction?2005:2004;
            if(_isFromUserAction&&[info[@"_j_msgid"] unsignedLongLongValue]==launchNotificationMessageID)code=_launchReportCode==2006?2003:2005;
        }else if(state==UIApplicationStateInactive){
            if(_isFromBackground)code=2002;
            else{code=_isFromUserAction?2005:2001;if(_isFromUserAction&&[info[@"_j_msgid"] unsignedLongLongValue]==launchNotificationMessageID)code=_launchReportCode==2006?2002:2005;}
        }else if(state==UIApplicationStateActive)code=_isFromUserAction?2005:2001;
        else code=2000;
    }
    REC_PUSH_LOG(YES,8,0x233,REC_PUSH_cf_remoteinfo___reportresult__d_appState__d,info,code,state);
    return @[@(code),info];
}
- (void)handleRemoteNotificationAction:(NSArray *)action {
    if(!action||![action isKindOfClass:NSArray.class]||action.count!=2)return;
    int code=[action[0] doubleValue];NSDictionary *info=action[1];
    RP_SCHEDULE(@"handleRemoteNotification:",action);
    if(code==2005||code==2003||(code==2002&&_isFromUserAction)){
        RP_ACTION_0000a3d8();[[NSNotificationCenter defaultCenter] postNotificationName:@"kJiguangPushClick" object:nil userInfo:info];
    }
}
- (void)updateDeviceToken:(NSData *)token { RP_SCHEDULE(@"updateDeviceToken:",[token copy]); }
- (void)updateVoipToken:(NSData *)token { RP_SCHEDULE(@"updateVoipToken:",[token copy]); }
- (void)updateLiveActivity:(NSString *)activity pushToStartToken:(NSData *)token completion:(JPUSHLiveActivityTokenCompletion)completion seq:(NSInteger)seq {
    (void)[token copy];JPushLiveActivityTokenVO *value=[[JPushLiveActivityTokenVO alloc] init];value.reportType=1;value.attributesType=activity;value.pushToStartToken=token;value.seq=seq;value.completion=completion;RP_SCHEDULE(@"updateLiveActivity:",value);
}
- (void)updateLiveActivity:(NSString *)activity pushToken:(NSData *)token completion:(JPUSHLiveActivityTokenCompletion)completion seq:(NSInteger)seq {
    NSData *copied=[token copy];JPushLiveActivityTokenVO *value=[[JPushLiveActivityTokenVO alloc] init];value.reportType=0;value.liveActivityAlias=activity;value.pushToken=copied;value.seq=seq;value.completion=completion;RP_SCHEDULE(@"updateLiveActivity:",value);
}
- (void)handleVoipNotification:(NSDictionary *)info {
    if(!info){RP_ACTION_0000a8c0();return;}
    if(![info[@"_j_msgid"] isKindOfClass:NSNumber.class]){REC_PUSH_LOG(NO,1,0x275,REC_PUSH_cf_invalidmessageIdtype_messageId___,info[@"_j_msgid"]);return;}
    RP_SCHEDULE(@"handleVoipNotification:",info);
}
- (void)setTags:(NSSet *)tags alias:(NSString *)alias parameters:(NSArray *)parameters type:(NSUInteger)type {
    if(![JCOREClientInfo uid]){RP_ACTION_0000aa50(tags,alias);return;}
    NSSet *copiedTags=[tags copy];NSString *copiedAlias=[alias copy];JPUSHSetTagAliasArg *argument=[[JPUSHSetTagAliasArg alloc] init];argument.tags=copiedTags;argument.alias=copiedAlias;argument.type=type;
    if(type==1&&parameters.count)argument.completionHandler=parameters[0];
    else if(type==2&&parameters.count>1){SEL selector=NSSelectorFromString(parameters[0]);if(selector){NSInvocation *invocation=[self getTagAliasInvocationWithCallback:selector target:parameters[1]];if(invocation){argument.cbInvocation=invocation;argument.target=parameters[1];}}}
    RP_SCHEDULE(@"setTagAlias:",argument);
}
- (void)tagOperate:(NSSet *)tags type:(long long)type andCompletion:(id)completion andSeq:(long long)seq {
    JPUSHTagAliasVO *value=[[JPUSHTagAliasVO alloc] init];value.tags=tags;value.opearteType=type;value.seq=seq;
    if(type==5)value.tagValidCompletion=completion;else value.tagsCompletion=completion;
    RP_SCHEDULE(@"tagOperateWith:",value);
}
- (void)aliasOperate:(NSString *)alias type:(long long)type andCompletion:(JPUSHAliasOperationCompletion)completion andSeq:(long long)seq {
    JPUSHTagAliasVO *value=[[JPUSHTagAliasVO alloc] init];value.alias=alias;value.opearteType=type;value.seq=seq;value.aliasCompletion=completion;RP_SCHEDULE(@"aliasOperateWith:",value);
}
- (void)propertiesOperate:(NSDictionary *)properties type:(long long)type andCompletion:(JPUSHPropertiesOperationCompletion)completion andSeq:(long long)seq {
    JPUSHPropertyVO *value=[[JPUSHPropertyVO alloc] init];value.propertiesInfo=properties;value.opearteType=type;value.seq=seq;value.propertyCompletion=completion;RP_SCHEDULE(@"propertyOperateWith:",value);
}
- (void)startLogPageView:(NSString *)page { RP_ACTION_0000b084(); }
- (void)stopLogPageView:(NSString *)page { RP_ACTION_0000b0e4(); }
- (void)beginLogPageView:(NSString *)page duration:(int)duration { RP_ACTION_0000b144();(void)@[page,@(duration)]; }
- (void)crashLogON { [_client setCrashLogON]; }
- (void)setHeartBeatTimeInterval:(NSInteger)interval { [_client setHeartBeatTimeInterval:interval]; }
- (void)locationEnable:(BOOL)enable { _collectControl.gps=enable||smartPushEnable||dataInsightsEnable;[JPUSHService handleDataCollectControl]; }
- (void)pushEnable:(BOOL)enable completion:(void (^)(NSInteger))completion { RP_SCHEDULE(@"setPushEnable:",(completion?@{@"isEnable":@(enable),@"completion":completion}:@{@"isEnable":@(enable)})); }
- (void)setBackgroundEnable:(BOOL)enable { [_client setBackgroundEnable:enable]; }
- (void)setSmartPushEnable:(BOOL)enable { smartPushEnable=enable;[JPUSHService handleDataCollectControl]; }
- (void)setDataInsightsEnable:(BOOL)enable { dataInsightsEnable=enable;[JPUSHService handleDataCollectControl]; }
- (void)addNotification:(JPushNotificationRequest *)request { [JPUSHLocalNotificationController addNotification:request]; }
- (void)removeNotification:(JPushNotificationIdentifier *)identifier { [JPUSHLocalNotificationController removeNotification:identifier]; }
- (void)findNotification:(JPushNotificationIdentifier *)identifier { [JPUSHLocalNotificationController findNotification:identifier]; }
- (UILocalNotification *)setLocalNotification:(NSDate *)fireDate alertBody:(NSString *)body badge:(int)badge alertAction:(NSString *)action identifierKey:(NSString *)key userInfo:(NSDictionary *)info soundName:(NSString *)sound region:(CLRegion *)region regionTriggersOnce:(BOOL)once category:(NSString *)category {
    return [JPUSHLocalNotificationController setLocalNotification:fireDate alertBody:body badge:badge alertAction:action identifierKey:key userInfo:info soundName:sound region:region regionTriggersOnce:once category:category];
}
- (void)showLocalNotificationAtFront:(UILocalNotification *)notification identifierKey:(NSString *)key { [JPUSHLocalNotificationController showLocalNotificationAtFront:notification identifierKey:key]; }
- (NSArray *)findLocalNotificationWithIdentifier:(NSString *)key { return [JPUSHLocalNotificationController findLocalNotificationWithIdentifier:key]; }
- (void)deleteLocalNotificationWithIdentifierKey:(NSString *)key { [JPUSHLocalNotificationController deleteLocalNotificationWithIdentifierKey:key]; }
- (void)deleteLocalNotification:(UILocalNotification *)notification { [JPUSHLocalNotificationController deleteLocalNotification:notification]; }
- (void)clearAllLocalNotifications { [JPUSHLocalNotificationController clearAllLocalNotifications]; }
- (NSString *)getRegistrationID { return [JCOREClientInfo registrationID]?:@""; }
- (void)getRegistrationIDCompletionHandler:(void (^)(int,NSString *))completion { RP_SCHEDULE(@"getRegistrationIDCompletionHandler:",completion); }
- (void)setBadge:(NSInteger)badge { RP_SCHEDULE(@"setBadgeNumberReport:",@(badge)); }
- (void)resetBadge { RP_SCHEDULE(@"setBadgeNumberReport:",@0); }
- (void)setMobileNumber:(NSString *)number completion:(void (^ __unsafe_unretained)(NSError *))completion { id retainedCompletion=(__bridge id)(__bridge void *)completion;
    if(!number||([number isKindOfClass:NSString.class]&&!number.length))number=@"";
    else{
        NSInteger errorCode=0;NSString *message;
        if(![JPUSHHelper checkFormatOfMobileNumber:number]){errorCode=6023;message=REC_PUSH_cf_mobilenumberformaterror;REC_PUSH_LOG(NO,1,0x37b,message);}
        else if(number.length>20){errorCode=6025;message=REC_PUSH_cf_mobilenumberlengthtoolong;REC_PUSH_LOG(NO,1,899,message);}
        if(errorCode){if(retainedCompletion&&![(id)retainedCompletion isKindOfClass:NSNull.class])((void (^)(NSError *))retainedCompletion)([NSError errorWithDomain:@"JPUSHErrorDomain" code:errorCode userInfo:@{NSLocalizedDescriptionKey:message}]);return;}
    }
    NSDictionary *value=retainedCompletion&&![(id)retainedCompletion isKindOfClass:NSNull.class]?@{@"type":@7,@"content":number,@"completion":retainedCompletion}:@{@"type":@7,@"content":number};
    RP_SCHEDULE(@"setUserProperty:",value);
}
- (NSInvocation *)getTagAliasInvocationWithCallback:(SEL)selector target:(id)target {
    if(!selector&&!target)return nil;
    NSString *message;NSUInteger line;
    if(!selector){message=REC_PUSH_cf_JPushWarning_setTagsAliascallbackwillNOTbeinvokedduetonilcallbackselector;line=0x39b;}
    else if(!target){message=REC_PUSH_cf_JPushWarning_setTagsAliascallbackwillNOTbeinvokedduetoniltarget;line=0x39e;}
    else{
        NSMethodSignature *signature=[target methodSignatureForSelector:selector];
        if(!signature){message=REC_PUSH_cf_JPushWarning_setTagsAliascallbackselectorNOTfound_callbackselectornotimplementedfortargetobject_;line=0x3a5;}
        else if(![self validateCallbackMethod:signature]){message=REC_PUSH_cf_JPushWarning_setTagsAliascallbackselectorinvalid_referstoexampleinsdk_ziptogetstandardcallbackselectorimplementation_;line=0x3aa;}
        else{NSInvocation *invocation=[NSInvocation invocationWithMethodSignature:signature];if(invocation){invocation.selector=selector;invocation.target=target;return invocation;}message=REC_PUSH_cf_JPushWarning_setTagsAliascallbackselectorNOTfound_callbackselectornotimplementedfortargetobject_;line=0x3b1;}
    }
    REC_PUSH_LOG(YES,2,line,message);return nil;
}
// The device SDK uses this signature as a validation template; it performs no callback work.
- (void)standardCallback:(int)code originalTags:(NSSet *)tags alias:(NSString *)alias {}
- (BOOL)validateCallbackMethod:(NSMethodSignature *)signature {
    static NSMethodSignature *standard;
    if(!standard)standard=[self methodSignatureForSelector:@selector(standardCallback:originalTags:alias:)];
    if(signature.numberOfArguments!=standard.numberOfArguments)return NO;
    for(NSUInteger i=2;i<standard.numberOfArguments;i++)if(strcmp([signature getArgumentTypeAtIndex:i],[standard getArgumentTypeAtIndex:i]))return NO;
    return YES;
}
- (void)applicationWillTerminate { [self reportInactive]; }
- (void)applicationDidEnterBackground { _isFromBackground=YES;[self reportInactive]; }
- (void)applicationWillEnterForeground { [self reportLaunch]; }
- (void)applicationDidBecomeActive { _isFromBackground=NO; }
// The original lifecycle observer intentionally performs no work for this event.
- (void)applicationWillResignActive {}
- (void)applicationDidFinishLaunch:(NSNotification *)notification {
    self.finishLaunchReceived=YES;
    if(notification.userInfo&&![notification.userInfo isEqualToDictionary:self.launchOptions]){RP_ACTION_0000beec();}
}
+ (void)setupWithOption:(NSDictionary *)option appKey:(NSString *)key channel:(NSString *)channel apsForProduction:(BOOL)production {
    [JPUSHService commonSetupWithOption:option appKey:key channel:channel apsForProduction:production advertisingIdentifier:nil];RP_RECORD();
}
+ (void)setupWithOption:(NSDictionary *)option appKey:(NSString *)key channel:(NSString *)channel apsForProduction:(BOOL)production advertisingIdentifier:(NSString *)identifier {
    [JPUSHService commonSetupWithOption:option appKey:key channel:channel apsForProduction:production advertisingIdentifier:identifier];RP_RECORD();
}
+ (void)setupWithOption:(NSDictionary *)option { RP_ACTION_0000c13c(); }
+ (void)commonSetupWithOption:(NSDictionary *)option appKey:(NSString *)key channel:(NSString *)channel apsForProduction:(BOOL)production advertisingIdentifier:(NSString *)identifier {
    static dispatch_once_t once;
    dispatch_once(&once,^{
        [JCORELogger setupJCoreLogger];
        NSString *version=[NSString stringWithFormat:REC_PUSH_cf_______________________JPushSDKVersion_____build__d______________________________JCoreLibVersion_____build__d___________________________AppKey___________________________________________________________________________________,REC_PUSH_LOG_PREFIX,[JPUSHHelper JPUSHDisplayVersion],413,[JPUSHIntegrate jcoreDisplayVersion],[JPUSHIntegrate jcoreBuildVersion],key];
        REC_PUSH_LOG(YES,4,0x466,REC_PUSH_cf___,version);
        NSDictionary *remote=[option valueForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
        [JPUSHService sharedService].launchOptions=option;
        NSArray *action=[[JPUSHService sharedService] getRemoteNotificationReportCode:remote AtLaunch:YES];
        [[JPUSHClientController sharedClient] commonSetupWithAppKey:key channel:channel apsForProduction:production advertisingIdentifier:identifier completion:^(BOOL success){
            if(success){
                dispatch_async(dispatch_get_main_queue(),^{if(![JPUSHService sharedService].finishLaunchReceived){REC_PUSH_LOG(YES,2,0x472,REC_PUSH_cf__J);}});
                [[JPUSHService sharedService] setupClient];[[JPUSHService sharedService] handleRemoteNotificationAction:action];
            }
        }];
    });
}
+ (void)registerDeviceToken:(NSData *)token { RP_ACTION_0000c714(token);[[JPUSHService sharedService] updateDeviceToken:token];RP_RECORD(); }
+ (void)registerVoipToken:(NSData *)token { RP_ACTION_0000c83c(token);[[JPUSHService sharedService] updateVoipToken:token];RP_RECORD(); }
+ (void)registerLiveActivity:(NSString *)activity pushToStartToken:(NSData *)token completion:(JPUSHLiveActivityTokenCompletion)completion seq:(NSInteger)seq {
    RP_ACTION_0000c964(activity,token);[[JPUSHService sharedService] updateLiveActivity:activity pushToStartToken:token completion:completion seq:seq];
}
+ (void)registerLiveActivity:(NSString *)activity pushToken:(NSData *)token completion:(JPUSHLiveActivityTokenCompletion)completion seq:(NSInteger)seq {
    RP_ACTION_0000ca70(activity,token);[[JPUSHService sharedService] updateLiveActivity:activity pushToken:token completion:completion seq:seq];RP_RECORD();
}
+ (void)registerForRemoteNotificationTypes:(NSUInteger)types categories:(NSSet *)categories {
    if([[[UIDevice currentDevice] systemVersion] floatValue]>=8.0){
        UIApplication *application=[UIApplication sharedApplication];NSMethodSignature *signature=[application methodSignatureForSelector:@selector(registerForRemoteNotifications)];if(!signature)return;
        NSInvocation *invocation=[NSInvocation invocationWithMethodSignature:signature];invocation.target=application;invocation.selector=@selector(registerForRemoteNotifications);[invocation invoke];
        Class settingsClass=NSClassFromString(@"UIUserNotificationSettings");SEL settingsSelector=NSSelectorFromString(@"settingsForTypes:categories:");signature=[settingsClass methodSignatureForSelector:settingsSelector];if(!signature)return;
        invocation=[NSInvocation invocationWithMethodSignature:signature];invocation.target=settingsClass;invocation.selector=settingsSelector;[invocation setArgument:&types atIndex:2];
        NSMutableSet *filtered=[[NSMutableSet alloc] init];for(id category in categories)if([category isKindOfClass:UIUserNotificationCategory.class])[filtered addObject:category];
        [invocation setArgument:&filtered atIndex:3];[invocation retainArguments];[invocation invoke];__unsafe_unretained id settings=nil;[invocation getReturnValue:&settings];
        signature=[[UIApplication sharedApplication] methodSignatureForSelector:@selector(registerUserNotificationSettings:)];if(!signature)return;
        invocation=[NSInvocation invocationWithMethodSignature:signature];invocation.target=[UIApplication sharedApplication];invocation.selector=@selector(registerUserNotificationSettings:);[invocation setArgument:&settings atIndex:2];[invocation retainArguments];[invocation invoke];
    }else [[UIApplication sharedApplication] registerForRemoteNotificationTypes:types];
    [JPUSHAutomatedTester PostAutomatedTestStatusNotification:3];RP_RECORD();
}
+ (void)registerForRemoteNotificationConfig:(JPUSHRegisterEntity *)config delegate:(id<JPUSHRegisterDelegate>)delegate {
    if([[[UIDevice currentDevice] systemVersion] floatValue]>=10.0){
        Class centerClass=NSClassFromString(@"UNUserNotificationCenter");SEL current=NSSelectorFromString(@"currentNotificationCenter");if(!centerClass||![centerClass respondsToSelector:current])return;
        NSMethodSignature *signature=[centerClass methodSignatureForSelector:current];if(!signature)return;
        NSInvocation *invocation=[NSInvocation invocationWithMethodSignature:signature];invocation.target=centerClass;invocation.selector=current;[invocation invoke];__unsafe_unretained id center=nil;[invocation getReturnValue:&center];
        SEL request=NSSelectorFromString(@"requestAuthorizationWithOptions:completionHandler:");SEL setCategories=NSSelectorFromString(@"setNotificationCategories:");
        if(![center respondsToSelector:request]||![center respondsToSelector:setCategories])return;
        signature=[center methodSignatureForSelector:request];if(!signature)return;
        invocation=[NSInvocation invocationWithMethodSignature:signature];invocation.target=center;invocation.selector=request;NSInteger types=config.types;[invocation setArgument:&types atIndex:2];
        void (^completion)(BOOL,NSError *)=^(BOOL granted,NSError *error){[[NSNotificationCenter defaultCenter] postNotificationName:kJPUSHCheckNotiAuthNotification object:nil userInfo:@{@"type":@0}];};
        [invocation setArgument:&completion atIndex:3];[invocation invoke];
        if(config.categories&&config.categories.count){
            NSMutableSet *filtered=[[NSMutableSet alloc] init];Class categoryClass=NSClassFromString(@"UNNotificationCategory");for(id category in config.categories)if([category isKindOfClass:categoryClass])[filtered addObject:category];
            signature=[center methodSignatureForSelector:setCategories];if(!signature)return;
            invocation=[NSInvocation invocationWithMethodSignature:signature];invocation.target=center;invocation.selector=setCategories;[invocation setArgument:&filtered atIndex:2];[invocation invoke];
        }
        UIApplication *application=[UIApplication sharedApplication];if([application respondsToSelector:@selector(registerForRemoteNotifications)])[application performSelector:@selector(registerForRemoteNotifications)];
        if([center respondsToSelector:@selector(setDelegate:)])[center performSelector:@selector(setDelegate:) withObject:[JPUSHService sharedService]];
        [JPUSHAutomatedTester PostAutomatedTestStatusNotification:3];
    }else [self registerForRemoteNotificationTypes:config.types categories:config.categories];
    [JPUSHService sharedService].delegate=delegate;RP_RECORD();
}
+ (void)handleRemoteNotification:(NSDictionary *)info { RP_ACTION_0000da40(info);[[JPUSHService sharedService] handleRemoteNotification:info AtLaunch:NO];RP_RECORD(); }
+ (void)handleVoipNotification:(NSDictionary *)info { RP_ACTION_0000db6c(info);[[JPUSHService sharedService] handleVoipNotification:[info copy]];RP_RECORD(); }
+ (void)requestNotificationAuthorization:(void (^ __unsafe_unretained)(JPAuthorizationStatus))completion { id retainedCompletion=(__bridge id)(__bridge void *)completion; RP_ACTION_0000dcb4();[[JPUSHService sharedService] checkNotiAuth:retainedCompletion];RP_RECORD(); }
+ (void)openSettingsForNotification:(void (^)(BOOL))completion {
    RP_ACTION_0000dddc();
    if(@available(iOS 8.0,*)){
        dispatch_async(dispatch_get_main_queue(),^{NSURL *url=[NSURL URLWithString:UIApplicationOpenSettingsURLString];if(@available(iOS 10.0,*))[[UIApplication sharedApplication] openURL:url options:@{} completionHandler:completion];else{BOOL success=[[UIApplication sharedApplication] openURL:url];if(completion)completion(success);}});
    }else{REC_PUSH_LOG(YES,2,0x56f,REC_PUSH_cf_opensettingsonlysupportiOS8andlater);if(completion)completion(NO);}
    RP_RECORD();
}
+ (void)setTags:(NSSet *)tags alias:(NSString *)alias callbackSelector:(SEL)selector object:(id)target { RP_ACTION_0000e0d4(tags,alias);[JPUSHService setTags:tags alias:alias callbackSelector:selector target:target]; }
+ (void)setTags:(NSSet *)tags callbackSelector:(SEL)selector object:(id)target { RP_ACTION_0000e1c0(tags);NSString *alias=nil;[JPUSHService setTags:tags alias:alias callbackSelector:selector object:target]; }
+ (void)setAlias:(NSString *)alias callbackSelector:(SEL)selector object:(id)target { RP_ACTION_0000e290(alias);NSSet *tags=nil;[JPUSHService setTags:tags alias:alias callbackSelector:selector object:target]; }
+ (void)setTags:(NSSet *)tags alias:(NSString *)alias callbackSelector:(SEL)selector target:(id)target {
    RP_ACTION_0000e360(tags,alias);NSArray *parameters=nil;if(selector){NSString *name=NSStringFromSelector(selector);if(name&&target)parameters=@[name,target];}
    [[JPUSHService sharedService] setTags:tags alias:alias parameters:parameters type:2];
}
+ (void)setTags:(NSSet *)tags alias:(NSString *)alias fetchCompletionHandle:(void (^)(int,NSSet *,NSString *))completion {
    RP_ACTION_0000e504(tags,alias);[[JPUSHService sharedService] setTags:tags alias:alias parameters:completion?@[completion]:nil type:1];
}
+ (void)setTags:(NSSet *)tags aliasInbackground:(NSString *)alias { RP_ACTION_0000e690(tags);[[JPUSHService sharedService] setTags:tags alias:alias parameters:nil type:0]; }
+ (void)addTags:(NSSet *)tags completion:(JPUSHTagsOperationCompletion)completion seq:(NSInteger)seq { [[JPUSHService sharedService] tagOperate:tags type:0 andCompletion:completion andSeq:seq];RP_RECORD(); }
+ (void)setTags:(NSSet *)tags completion:(JPUSHTagsOperationCompletion)completion seq:(NSInteger)seq { [[JPUSHService sharedService] tagOperate:tags type:4 andCompletion:completion andSeq:seq];RP_RECORD(); }
+ (void)deleteTags:(NSSet *)tags completion:(JPUSHTagsOperationCompletion)completion seq:(NSInteger)seq { [[JPUSHService sharedService] tagOperate:tags type:1 andCompletion:completion andSeq:seq];RP_RECORD(); }
+ (void)cleanTags:(JPUSHTagsOperationCompletion)completion seq:(NSInteger)seq { [[JPUSHService sharedService] tagOperate:nil type:2 andCompletion:completion andSeq:seq];RP_RECORD(); }
+ (void)getAllTags:(JPUSHTagsOperationCompletion)completion seq:(NSInteger)seq { [[JPUSHService sharedService] tagOperate:nil type:3 andCompletion:completion andSeq:seq];RP_RECORD(); }
+ (void)validTag:(NSString *)tag completion:(__unsafe_unretained JPUSHTagValidOperationCompletion)completion seq:(NSInteger)seq { id retainedCompletion=(__bridge id)(__bridge void *)completion;
    if(tag&&![tag isKindOfClass:NSNull.class]){[[JPUSHService sharedService] tagOperate:[NSSet setWithObject:tag] type:5 andCompletion:retainedCompletion andSeq:seq];RP_RECORD();return;}
    RP_ACTION_0000ecd4();if(retainedCompletion&&![(id)retainedCompletion isKindOfClass:NSNull.class])((JPUSHTagValidOperationCompletion)retainedCompletion)(6001,nil,seq,NO);
}
+ (void)setAlias:(NSString *)alias completion:(JPUSHAliasOperationCompletion)completion seq:(NSInteger)seq { [[JPUSHService sharedService] aliasOperate:alias type:4 andCompletion:completion andSeq:seq];RP_RECORD(); }
+ (void)deleteAlias:(JPUSHAliasOperationCompletion)completion seq:(NSInteger)seq { [[JPUSHService sharedService] aliasOperate:nil type:1 andCompletion:completion andSeq:seq];RP_RECORD(); }
+ (void)getAlias:(JPUSHAliasOperationCompletion)completion seq:(NSInteger)seq { [[JPUSHService sharedService] aliasOperate:nil type:3 andCompletion:completion andSeq:seq];RP_RECORD(); }
+ (void)startLogPageView:(NSString *)page { RP_ACTION_0000f1b4(page);[[JPUSHService sharedService] startLogPageView:page]; }
+ (void)stopLogPageView:(NSString *)page { RP_ACTION_0000f274(page);[[JPUSHService sharedService] stopLogPageView:page]; }
+ (void)beginLogPageView:(NSString *)page duration:(int)duration { RP_ACTION_0000f334(page,duration);[[JPUSHService sharedService] beginLogPageView:page duration:duration]; }
+ (NSSet *)filterValidTags:(NSSet *)tags { RP_ACTION_0000f400(tags);RP_RECORD();return [JPUSHTagAliasValidation filterValidTags:tags]; }
+ (void)setProperties:(NSDictionary *)properties completion:(__unsafe_unretained JPUSHPropertiesOperationCompletion)completion seq:(NSInteger)seq { id retainedCompletion=(__bridge id)(__bridge void *)completion;
    if(properties&&![properties isKindOfClass:NSNull.class]&&(![properties isKindOfClass:NSDictionary.class]||properties.allKeys.count)){
        [[JPUSHService sharedService] propertiesOperate:properties type:0 andCompletion:retainedCompletion andSeq:seq];RP_RECORD();return;
    }
    RP_ACTION_0000f51c();if(retainedCompletion&&![(id)retainedCompletion isKindOfClass:NSNull.class])((JPUSHPropertiesOperationCompletion)retainedCompletion)(6001,properties,seq);
}
+ (void)deleteProperties:(NSSet *)keys completion:(__unsafe_unretained JPUSHPropertiesOperationCompletion)completion seq:(NSInteger)seq { id retainedCompletion=(__bridge id)(__bridge void *)completion;
    NSInteger code;
    if(!keys||[keys isKindOfClass:NSNull.class]){RP_ACTION_0000f748();code=6001;}
    else if([keys isKindOfClass:NSSet.class]){
        NSMutableDictionary *properties=[NSMutableDictionary dictionary];for(NSString *key in keys.allObjects)[properties setValue:@"" forKey:key];
        [[JPUSHService sharedService] propertiesOperate:properties type:1 andCompletion:retainedCompletion andSeq:seq];RP_RECORD();return;
    }else{REC_PUSH_LOG(NO,1,0x668,REC_PUSH_cf_Invalidkeystype_pleasecheck);code=6037;}
    if(retainedCompletion&&![(id)retainedCompletion isKindOfClass:NSNull.class])((JPUSHPropertiesOperationCompletion)retainedCompletion)(code,nil,seq);
}
+ (void)cleanProperties:(JPUSHPropertiesOperationCompletion)completion seq:(NSInteger)seq { [[JPUSHService sharedService] propertiesOperate:@{} type:2 andCompletion:completion andSeq:seq];RP_RECORD(); }
+ (void)setNotiInMessageDelegate:(id<JPUSHNotiInMessageDelegate>)delegate { [[JPUSHInMessageManager shareManager] setNotiInMessageDelegate:delegate]; }
+ (void)setInAppMessageDelegate:(id<JPUSHInAppMessageDelegate>)delegate { [[JPUSHInMessageManager shareManager] setInAppMessageDelegate:delegate]; }
+ (void)pageEnterTo:(NSString *)page { if(page&&[page isKindOfClass:NSString.class]&&page.length){[JPUSHInMessageManager pageEnterTo:page];return;}RP_ACTION_0000fcfc(); }
+ (void)pageLeave:(NSString *)page { if(page&&[page isKindOfClass:NSString.class]&&page.length){[JPUSHInMessageManager pageLeave:page];return;}RP_ACTION_0000fdd4(); }
+ (void)crashLogON { RP_ACTION_0000feac();[[JPUSHService sharedService] crashLogON];RP_RECORD(); }
+ (void)setDebugMode { RP_ACTION_0000ffac();[JCORELogger setDebugModel];[JPUSHAutomatedTester PostAutomatedTestStatusNotification:32];RP_RECORD(); }
+ (void)setLogOFF { RP_ACTION_000100a4();[JCORELogger setLogOff];[JPUSHAutomatedTester PostAutomatedTestStatusNotification:33];RP_RECORD(); }
+ (void)setCollectControl:(JPushCollectControl *)control {
    RP_ACTION_0001019c();[JPUSHService sharedService].collectControl.bssid=control.bssid;[JPUSHService sharedService].collectControl.ssid=control.ssid;[JPUSHService sharedService].collectControl.cell=control.cell;[JPUSHService sharedService].collectControl.gps=control.gps;[self handleDataCollectControl];
}
+ (void)handleDataCollectControl {
    BOOL gps=[JPUSHService sharedService].collectControl.gps||smartPushEnable||dataInsightsEnable;
    if([JCOREService respondsToSelector:@selector(setDataControl:)]){
        BOOL appList=smartPushEnable||dataInsightsEnable;
        NSDictionary *control=@{@"ssid":@([JPUSHService sharedService].collectControl.ssid),@"bssid":@([JPUSHService sharedService].collectControl.bssid),@"gps":@(gps),@"cell":@([JPUSHService sharedService].collectControl.cell),@"appList":@(appList),@"sdkIndex":@0};
        [JCOREService performSelector:@selector(setDataControl:) withObject:control];
    }else{RP_ACTION_000103dc();}
    [[JPUSHGeofenceManager sharedGeoFence] setLocationEnable:gps];
}
+ (void)setHeartBeatTimeInterval:(NSInteger)interval {
    RP_ACTION_00010818();if(((NSUInteger)interval-30)>260){REC_PUSH_LOG(NO,1,0x6ed,REC_PUSH_cf_heartbeattimeintervalmustbein_30_290_);return;}[[JPUSHService sharedService] setHeartBeatTimeInterval:interval];RP_RECORD();
}
+ (void)setLocationEanable:(BOOL)enable { RP_ACTION_00010990();[[JPUSHService sharedService] locationEnable:enable];RP_RECORD(); }
+ (void)setPushEnable:(BOOL)enable completion:(void (^)(NSInteger))completion { RP_ACTION_00010a98();[[JPUSHService sharedService] pushEnable:enable completion:completion];RP_RECORD(); }
+ (void)setBackgroundEnable:(BOOL)enable { RP_ACTION_00010bc8();[[JPUSHService sharedService] setBackgroundEnable:enable];RP_RECORD(); }
+ (void)setSmartPushEnable:(BOOL)enable { RP_ACTION_00010cd0();[[JPUSHService sharedService] setSmartPushEnable:enable];RP_RECORD(); }
+ (void)setDataInsightsEnable:(BOOL)enable { RP_ACTION_00010dd8();[[JPUSHService sharedService] setDataInsightsEnable:enable];RP_RECORD(); }
+ (void)setLatitude:(double)latitude longitude:(double)longitude { RP_ACTION_00010ee0(latitude,longitude);[[JPUSHService sharedService] setLatitude:latitude longitude:longitude];RP_RECORD(); }
+ (void)setLocation:(CLLocation *)location { RP_ACTION_00010ffc(location);[[JPUSHService sharedService] setLocation:location];RP_RECORD(); }
+ (NSString *)registrationID { RP_ACTION_00011124();RP_RECORD();return [[JPUSHService sharedService] getRegistrationID]; }
+ (void)registrationIDCompletionHandler:(void (^)(int,NSString *))completion { [[JPUSHService sharedService] getRegistrationIDCompletionHandler:completion];RP_RECORD(); }
+ (void)addNotification:(JPushNotificationRequest *)request { [[JPUSHService sharedService] addNotification:request];RP_RECORD(); }
+ (void)removeNotification:(JPushNotificationIdentifier *)identifier { [[JPUSHService sharedService] removeNotification:identifier];RP_RECORD(); }
+ (void)findNotification:(JPushNotificationIdentifier *)identifier { [[JPUSHService sharedService] findNotification:identifier];RP_RECORD(); }
+ (UILocalNotification *)setLocalNotification:(NSDate *)fireDate alertBody:(NSString *)body badge:(int)badge alertAction:(NSString *)action identifierKey:(NSString *)key userInfo:(NSDictionary *)info soundName:(NSString *)sound {
    REC_PUSH_LOG(YES,8,0x774,REC_PUSH_cf_Action_setLocalNotification___alertBody___badge__dalertAction___identifierKey___userInfo___soundName___,fireDate,body,badge,action,key,info,sound);
    return [[JPUSHService sharedService] setLocalNotification:fireDate alertBody:body badge:badge alertAction:action identifierKey:key userInfo:info soundName:sound region:nil regionTriggersOnce:NO category:nil];
}
+ (UILocalNotification *)setLocalNotification:(NSDate *)fireDate alertBody:(NSString *)body badge:(int)badge alertAction:(NSString *)action identifierKey:(NSString *)key userInfo:(NSDictionary *)info soundName:(NSString *)sound region:(CLRegion *)region regionTriggersOnce:(BOOL)once category:(NSString *)category {
    REC_PUSH_LOG(YES,8,0x78f,REC_PUSH_cf_Action_setLocalNotification___alertBody___badge__dalertAction___identifierKey___userInfo___soundName___region___regionTriggersOnce__dcategory___,fireDate,body,badge,action,key,info,sound,region,once,category);
    return [[JPUSHService sharedService] setLocalNotification:fireDate alertBody:body badge:badge alertAction:action identifierKey:key userInfo:info soundName:sound region:region regionTriggersOnce:once category:category];
}
+ (void)showLocalNotificationAtFront:(UILocalNotification *)notification identifierKey:(NSString *)key { RP_ACTION_00011908(notification,key);[[JPUSHService sharedService] showLocalNotificationAtFront:notification identifierKey:key]; }
+ (NSArray *)findLocalNotificationWithIdentifier:(NSString *)key { RP_ACTION_000119e8(key);return [[JPUSHService sharedService] findLocalNotificationWithIdentifier:key]; }
+ (void)deleteLocalNotificationWithIdentifierKey:(NSString *)key { RP_ACTION_00011abc(key);[[JPUSHService sharedService] deleteLocalNotificationWithIdentifierKey:key]; }
+ (void)deleteLocalNotification:(UILocalNotification *)notification { RP_ACTION_00011b7c(notification);[[JPUSHService sharedService] deleteLocalNotification:notification]; }
+ (void)clearAllLocalNotifications { RP_ACTION_00011c3c();[[JPUSHService sharedService] clearAllLocalNotifications]; }
+ (BOOL)setBadge:(NSInteger)badge { RP_ACTION_00011cd4(badge);BOOL valid=((NSUInteger)badge>>5)<3125;if(valid){[[JPUSHService sharedService] setBadge:badge];RP_RECORD();}return valid; }
+ (void)resetBadge { RP_ACTION_00011dfc();[[JPUSHService sharedService] resetBadge];RP_RECORD(); }
+ (void)setMobileNumber:(NSString *)number completion:(void (^ __unsafe_unretained)(NSError *))completion { id retainedCompletion=(__bridge id)(__bridge void *)completion; RP_ACTION_00011efc();[[JPUSHService sharedService] setMobileNumber:number completion:retainedCompletion];RP_RECORD(); }
+ (void)setGeofeneceMaxCount:(NSInteger)count { [[JPUSHGeofenceManager sharedGeoFence] setMonitorCount:count];RP_RECORD(); }
+ (void)setGeofenecePeriodForInside:(NSInteger)period { [[JPUSHGeofenceManager sharedGeoFence] setGenfenceInsideTimerPeriod:period];RP_RECORD(); }
+ (void)registerLbsGeofenceDelegate:(id<JPUSHGeofenceDelegate>)delegate withLaunchOptions:(NSDictionary *)option { [[JPUSHGeofenceManager sharedGeoFence] setDelegate:delegate];if([option objectForKey:UIApplicationLaunchOptionsLocationKey])[[JPUSHGeofenceManager sharedGeoFence] startMonitor];RP_RECORD(); }
+ (void)removeGeofenceWithIdentifier:(NSString *)identifier { [[JPUSHGeofenceManager sharedGeoFence] removeGeofenceWithIdentifier:identifier];RP_RECORD(); }
- (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completion {
    if([self.delegate respondsToSelector:@selector(jpushNotificationCenter:willPresentNotification:withCompletionHandler:)])[self.delegate jpushNotificationCenter:center willPresentNotification:notification withCompletionHandler:^(NSInteger options){completion(options);}];
}
- (void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)(void))completion {
    _isFromUserAction=YES;
    if([self.delegate respondsToSelector:@selector(jpushNotificationCenter:didReceiveNotificationResponse:withCompletionHandler:)])[self.delegate jpushNotificationCenter:center didReceiveNotificationResponse:response withCompletionHandler:completion];
    _isFromUserAction=NO;
}
- (void)userNotificationCenter:(UNUserNotificationCenter *)center openSettingsForNotification:(UNNotification *)notification {
    if([self.delegate respondsToSelector:@selector(jpushNotificationCenter:openSettingsForNotification:)])if(@available(iOS 12.0,*))[self.delegate jpushNotificationCenter:center openSettingsForNotification:notification];
}
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
- (void)receiveCheckNotiAuth:(NSNotification *)notification {
    dispatch_async(dispatch_get_main_queue(),^{__weak JPUSHService *weakSelf=self;[self checkNotiAuth:^(JPAuthorizationStatus status){[weakSelf notificationAuthorization:status withInfo:notification.userInfo];}];});
}
- (void)notificationAuthorization:(JPAuthorizationStatus)status withInfo:(NSDictionary *)info {
    if(self.delegate&&[self.delegate respondsToSelector:@selector(jpushNotificationAuthorization:withInfo:)])[self.delegate jpushNotificationAuthorization:status withInfo:info];
}
@end

#endif
