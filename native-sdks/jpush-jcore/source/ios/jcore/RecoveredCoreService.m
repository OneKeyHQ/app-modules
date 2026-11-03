#import "RecoveredCoreService.h"
#import "RecoveredCoreServiceDependencies.h"
#import "RecoveredServiceController.h"
#import "RecoveredTransportDependencies.h"
#import "RecoveredCoreServiceLiterals.h"
#import "JCORENetworkReachabilityManager.h"
@interface JCOREUtilities:NSObject
+ (id)ctGeneration;
+ (double)currentTimestamp;
@end
@interface JCOREClientInfo:NSObject
+ (NSUInteger)uid;
@end
#import "JCommonService.h"
#import "JCORENetChannel.h"
@interface JCOREThread (CoreServiceDependencies)
+ (void)performSel:(SEL)selector target:(id)target withObject:(id)object;
@end
extern NSString *const kJCORENetworkDidLoginNotification;
static JCOREService *sharedCoreService;
static id coreAppKey;
static JGInforCollectionAuthItems *coreCollectionAuthorization;
@implementation JCOREService
@synthesize serviceController=_serviceController,config=_config,crashConfig=_crashConfig,uaidTokenConfig=_uaidTokenConfig,devidBanConfig=_devidBanConfig,configUpdateController=_configUpdateController,loadTimes=_loadTimes,canStartTcpSSLStatus=_canStartTcpSSLStatus,ipv6Prefered=_ipv6Prefered;
+ (id)sharedService { static dispatch_once_t once;dispatch_once(&once,^{if(!sharedCoreService)sharedCoreService=[[JCOREService alloc] init];});return sharedCoreService; }
+ (id)allocWithZone:(NSZone *)zone { static dispatch_once_t once;dispatch_once(&once,^{if(!sharedCoreService)sharedCoreService=[super allocWithZone:zone];});return sharedCoreService; }
- (id)init { self=[super init];if(self){_loadTimes=[JCOREUtilities currentTimestamp];_serviceController=[[JCOREServiceController alloc] init];}return self; }
- (JCOREConfig *)config { if(!_config)_config=[[JCOREConfig alloc] init];return _config; }
- (JCOREDevidBanConfig *)devidBanConfig { if(!_devidBanConfig)_devidBanConfig=[[JCOREDevidBanConfig alloc] init];return _devidBanConfig; }
- (JCORECrashConfig *)crashConfig { if(!_crashConfig)_crashConfig=[[JCORECrashConfig alloc] init];return _crashConfig; }
- (JCOREUaidTokenConfig *)uaidTokenConfig { if(!_uaidTokenConfig)_uaidTokenConfig=[[JCOREUaidTokenConfig alloc] init];return _uaidTokenConfig; }
- (JCOREConfigUpdateController *)configUpdateController { if(!_configUpdateController)_configUpdateController=[[JCOREConfigUpdateController alloc] init];return _configUpdateController; }
- (void)addJCoreInitObserver { [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(pullUaidTokenConfig) name:kJCORENetworkDidLoginNotification object:nil]; }
- (void)pullUaidTokenConfig {
    [JCommonService trackUAIDWithCode:1001];
    if([[JCOREUtilities ctGeneration] isEqualToString:@"wifi"]){REC_CORE_LOG(YES,0x100,"-[JCOREService pullUaidTokenConfig]",0x90,REC_CORE_e4118,"-[JCOREService pullUaidTokenConfig]");[JCommonService trackUAIDWithCode:1003];return;}
    if(!coreAppKey){REC_CORE_LOG(YES,0x100,"-[JCOREService pullUaidTokenConfig]",0x96,REC_CORE_e4138,"-[JCOREService pullUaidTokenConfig]");[JCommonService trackUAIDWithCode:1004];return;}
    if(![JCOREClientInfo uid]){REC_CORE_LOG(YES,0x100,"-[JCOREService pullUaidTokenConfig]",0x9c,REC_CORE_e4158,"-[JCOREService pullUaidTokenConfig]");[JCommonService trackUAIDWithCode:1005];return;}
    [JCOREThread performSel:NSSelectorFromString(@"pullUaidTokenWithAppkeyAndCompletion:") target:[[JCOREService sharedService] uaidTokenConfig] withObject:nil];
}
+ (id)authInfo { return coreCollectionAuthorization?:[[JGInforCollectionAuthItems alloc] init]; }
+ (void)resetDeviceInforCollectionAuth:(JCOREAuthorizationReset)reset { if(!coreCollectionAuthorization)coreCollectionAuthorization=[[JGInforCollectionAuthItems alloc] init];if(reset)reset(coreCollectionAuthorization); }
+ (void)setupWithConfig:(id)config {
    if(![[JCOREService authInfo] isAuth]){
        REC_CORE_LOG(YES,0x100,"+[JCOREService setupWithConfig:]",0xb0,REC_CORE_e4178,"+[JCOREService setupWithConfig:]");
        NSError *error=[NSError errorWithDomain:@"JIGUANGTcpErrorDomain" code:257 userInfo:@{NSLocalizedDescriptionKey:REC_CORE_AUTH_ERROR}];
        NSDictionary *info=@{@"isAuth":@([coreCollectionAuthorization isAuth])};
        dispatch_async(dispatch_get_main_queue(),^{if([config completionHandler])((JCORESetupCompletion)[config completionHandler])(error,info);});return;
    }
    [[JCoreParameter shared] setJcoreHasSetUp:YES];[[JCORENetworkReachabilityManager sharedManager] startMonitoring];
    if([config appKey]){[[NSUserDefaults standardUserDefaults] setObject:[config appKey] forKey:@"JIGUANG_APPKEY"];[[NSUserDefaults standardUserDefaults] synchronize];}
    if([config respondsToSelector:@selector(index)])[JCOREIntegrate initSdk:[config index]];
    if([[JCOREIntegrate isHasCustomSDK] allKeys].count){REC_CORE_LOG(YES,0x100,"+[JCOREService setupWithConfig:]",0xcc,REC_CORE_e41d8,"+[JCOREService setupWithConfig:]");[JCOREThread performSel:NSSelectorFromString(@"obtainDevidBanSwitchStatusWithAppkey:") target:[[self sharedService] devidBanConfig] withObject:[config appKey]];}
    [JCOREThread performSel:NSSelectorFromString(@"obtainSwitchStatusWithAppkey:") target:[[self sharedService] config] withObject:[config appKey]];
    JCOREServiceSwitchCompletion statusCompletion=^(BOOL enabled){if(!enabled){REC_CORE_LOG(NO,1,"+[JCOREService setupWithConfig:]_block_invoke",0xd7,REC_CORE_e41f8);[JCORENetChannel stopTcpService];[JCORENetChannel stopHttpService];}};
    [JCOREThread performSel:@selector(pullStatusSwitchWithCompletion:) target:[[self sharedService] config] withObject:statusCompletion];
    JCOREServiceSwitchCompletion sslCompletion=^(BOOL enabled){
        REC_CORE_LOG(YES,0x100,"+[JCOREService setupWithConfig:]_block_invoke_2",0xe2,REC_CORE_e4218,(int)enabled);
        static dispatch_once_t once;dispatch_once(&once,^{BOOL cert=[JCORECertController certReady];REC_CORE_LOG(YES,0x100,"+[JCOREService setupWithConfig:]_block_invoke_3",0xe8,REC_CORE_e4238,(int)enabled,(int)cert);[JCOREService setCanStartTcpSSL:enabled&cert];});
    };
    [JCOREThread performSel:NSSelectorFromString(@"getTcpSSLCache:") target:[[self sharedService] crashConfig] withObject:sslCompletion];
    void (^crashCompletion)(int,int)=^(int logEnabled,int monitorEnabled){
        BOOL count=[[[JCOREService sharedService] crashConfig] statusCrashCountSwitch];BOOL network=[[[JCOREService sharedService] crashConfig] statusNetLengthSwitch];BOOL exception=[[[JCOREService sharedService] crashConfig] statusExceptionSwitch];BOOL tcp=[[[JCOREService sharedService] crashConfig] statusTcpExceptionSwitch];
        REC_CORE_LOG(YES,0x100,"+[JCOREService setupWithConfig:]_block_invoke_4",0xf7,REC_CORE_e4258,logEnabled,monitorEnabled,(int)count,(int)network,(int)exception);
        if(count)[JCOREService setSentryCrashCountON];if(logEnabled)[JCOREService setSentryCrashLogON];if(monitorEnabled)[JCOREService setSupersetMoniterOn];else [JCOREService clearSupersetMoniterCache];if(network)[JCOREService setNetDataMoniterOn];
        if(exception)[JCommonService startEventTracking];else [JCommonService stopEventTracking];if(tcp)[JCommonService startTcpExceptionMonitoring];else [JCommonService stopTcpExceptionMonitoring];
    };
    id appkey=[config appKey]?:@"";
    [JCOREThread performSel:NSSelectorFromString(@"pullCrashSwitchWithAppkeyAndCompletion:") target:[[self sharedService] crashConfig] withObject:@[appkey,crashCompletion]];
    void (^updates)(NSArray *)=^(NSArray *events){for(id event in events){id type=event[@"type"];if(![type isKindOfClass:NSString.class])continue;
        if([type isEqualToString:@"banned"])[JCOREThread performSel:NSSelectorFromString(@"forceUpdateConfig:") target:[[self sharedService] config] withObject:@[appkey,statusCompletion]];
        else if([type isEqualToString:@"sentry"])[JCOREThread performSel:NSSelectorFromString(@"pullCrashSwitchWithAppkeyAndCompletion:") target:[[self sharedService] crashConfig] withObject:@[appkey,crashCompletion,@YES]];
        else if([type isEqualToString:@"limit"]){if([[JCOREIntegrate isHasCustomSDK] allKeys].count){REC_CORE_LOG(YES,0x100,"+[JCOREService setupWithConfig:]_block_invoke_5",300,REC_CORE_e41d8,"+[JCOREService setupWithConfig:]_block_invoke_5");[JCOREThread performSel:NSSelectorFromString(@"obtainDevidBanSwitchStatusWithArgs:") target:[[self sharedService] devidBanConfig] withObject:@[appkey,@YES]];}}
        else if([type isEqualToString:@"sis"])[JCOREThread performSel:@selector(forceRequestReportAddress) target:[[self sharedService] serviceController] withObject:nil];
        else if([type isEqualToString:@"uaid"])[JCOREThread performSel:NSSelectorFromString(@"forceUpdateConfig:") target:[[self sharedService] uaidTokenConfig] withObject:@[appkey]];
    }};
    [JCOREThread performSel:NSSelectorFromString(@"startMonitoringWithCompletion:") target:[[self sharedService] configUpdateController] withObject:updates];
    coreAppKey=[config appKey];[[JCOREService sharedService] addJCoreInitObserver];[JCOREThread performSel:@selector(setupService:) target:[[self sharedService] serviceController] withObject:config];
}
+ (void)setNewAdvertisingId:(id)value { [JCOREThread performSel:_cmd target:[[self sharedService] serviceController] withObject:value]; }
+ (void)setCrashLogON { [JCOREThread performSel:_cmd target:[[self sharedService] serviceController] withObject:nil]; }
+ (void)setSentryCrashCountON { [JCOREThread performSel:_cmd target:[[self sharedService] serviceController] withObject:nil]; }
+ (void)setSentryCrashLogON { [JCOREThread performSel:_cmd target:[[self sharedService] serviceController] withObject:nil]; }
+ (void)setSupersetMoniterOn { [[[self sharedService] serviceController] setSupersetMoniterOn]; }
+ (void)clearSupersetMoniterCache { [[[self sharedService] serviceController] clearSupersetMoniterCache]; }
+ (void)setNetDataMoniterOn { [[[self sharedService] serviceController] setNetDataMoniterOn]; }
+ (void)setHeartBeatTimeInterval:(id)value { [[JCoreParameter shared] setHeartBeatTimeInterval:[value integerValue]]; }
+ (void)setBackgroundEnable:(id)value { [[JCoreParameter shared] setBackgroundEnable:[value integerValue]!=0]; }
+ (void)setLocationEnable:(id)value { [JCOREThread performSel:@selector(parameterLocationEnable:) target:self withObject:value]; }
+ (void)parameterLocationEnable:(id)value { [[JCoreParameter shared] setLocationEnable:[value boolValue]]; }
+ (void)setIPv6Preferred:(id)value { [JCOREThread performSel:@selector(parameterIPv6Preferred:) target:self withObject:value]; }
+ (void)parameterIPv6Preferred:(id)value { BOOL enabled=[value boolValue];[[JCOREService sharedService] setIpv6Prefered:enabled];[[JCOREIpvxHelper instance] configStaticIpvxType:enabled?2:1]; }
+ (BOOL)getIpv6Prefered { return [[JCOREService sharedService] ipv6Prefered]; }
+ (void)setDataControl:(id)value { if(value&&[value isKindOfClass:NSDictionary.class])[JCOREThread performSel:_cmd target:[JCOREDataControlManager shared] withObject:value]; }
+ (void)apiRecord:(id)type methodName:(id)name { if(type&&name)[JCOREThread performSel:@selector(apiRecord:) target:[[self sharedService] serviceController] withObject:@[type,name]]; }
+ (void)setLocationInfo:(id)value { [JCOREThread performSel:_cmd target:[[self sharedService] serviceController] withObject:value];[JCOREBusinessStorage setLocation:value]; }
+ (id)locationInfo { return [[[self sharedService] serviceController] locationInfo]; }
+ (BOOL)getStatusSwitch { return [[self sharedService] getStatusSwitch]; }
- (BOOL)getStatusSwitch { return [self.config statusSwitch]; }
+ (BOOL)getRegFingerSwitch { return [[self sharedService] getRegFingerSwitch]; }
- (BOOL)getRegFingerSwitch { return [self.config regFingerSwitch]; }
+ (id)getNetMointerConfig { return [[self sharedService] getNetMointerConfig]; }
- (id)getNetMointerConfig { return @{@"statusNetLengthSwitch":@([self.crashConfig statusNetLengthSwitch]),@"netLengthThreshold":@([self.crashConfig netLengthThreshold]),@"netCountThreshold":@([self.crashConfig netCountThreshold]),@"netConnCountThreshold":@([self.crashConfig netConnCountThreshold]),@"netConnLengthThreshold":@([self.crashConfig netConnLengthThreshold])}; }
+ (void)pullStatusSwitchWithCompletion:(JCOREServiceSwitchCompletion)completion { if(completion)[[self sharedService] pullStatusSwitchWithCompletion:completion];else REC_CORE_LOG(YES,0x100,"+[JCOREService pullStatusSwitchWithCompletion:]",0x1eb,REC_CORE_dfa58,"+[JCOREService pullStatusSwitchWithCompletion:]"); }
- (void)pullStatusSwitchWithCompletion:(JCOREServiceSwitchCompletion)completion { [self.config pullStatusSwitchWithCompletion:completion]; }
+ (BOOL)appkeyIsNotApplyToThisSDK { return [[self sharedService] getDevidBanSwitch]; }
- (BOOL)getDevidBanSwitch { return [self.devidBanConfig isBan]; }
+ (void)pullDevidBanSwitchWithAppkey:(id)appkey completion:(JCOREServiceSwitchCompletion)completion {
    if(completion&&appkey){[[self sharedService] pullDevidBanSwitchWithAppkey:appkey completion:completion];return;}
    REC_CORE_LOG(YES,0x100,"+[JCOREService pullDevidBanSwitchWithAppkey:completion:]",completion?0x206:0x202,completion?REC_CORE_BAD_APPKEY:REC_CORE_BAD_COMPLETION,"+[JCOREService pullDevidBanSwitchWithAppkey:completion:]");
}
- (void)pullDevidBanSwitchWithAppkey:(id)appkey completion:(JCOREServiceSwitchCompletion)completion { [self.devidBanConfig pullStatusSwitchWithCompletionWithAppkey:appkey andHandle:completion]; }
+ (BOOL)getStatusTcpSSL { return [[self sharedService] getStatusTcpSSL]; }
- (BOOL)getStatusTcpSSL { return [self.crashConfig statusTcpSSL]; }
+ (BOOL)canStartTcpSSL { return [[self sharedService] canStartTcpSSL]; }
- (BOOL)canStartTcpSSL { return self.canStartTcpSSLStatus; }
+ (void)setCanStartTcpSSL:(BOOL)value { [[self sharedService] setCanStartTcpSSL:value]; }
- (void)setCanStartTcpSSL:(BOOL)value { self.canStartTcpSSLStatus=value; }
+ (BOOL)getTcpReportStatus { return [[[self sharedService] crashConfig] statusTcpReportSwitch]; }
+ (BOOL)getNetSocketSwitch { return [[[self sharedService] crashConfig] newSocketSwitch]; }
+ (BOOL)getCheckIpv6Switch { return [[[self sharedService] crashConfig] checkIpv6Switch]; }
+ (void)pullCrashSwitc:(id)appkey completion:(JCOREServiceSwitchCompletion)completion { if(completion)[[self sharedService] pullCrashSwitchWithCompletion:completion];else REC_CORE_LOG(YES,0x100,"+[JCOREService pullCrashSwitc:completion:]",0x23a,REC_CORE_dfa58,"+[JCOREService pullCrashSwitc:completion:]"); }
- (void)pullCrashSwitchWithCompletion:(JCOREServiceSwitchCompletion)completion { if(completion)completion([self.crashConfig statusSwitch]); }
+ (void)pullUaidTokenSwitch:(id)appkey completion:(JCOREServiceSwitchCompletion)completion { if(completion)[[self sharedService] pullUaidTokenSwitchWithCompletion:completion];else REC_CORE_LOG(YES,0x100,"+[JCOREService pullUaidTokenSwitch:completion:]",0x249,REC_CORE_dfa58,"+[JCOREService pullUaidTokenSwitch:completion:]"); }
- (void)pullUaidTokenSwitchWithCompletion:(JCOREServiceSwitchCompletion)completion { if(completion)completion([self.uaidTokenConfig statusSwitch]); }
+ (void)reportToken:(id)token invalid:(BOOL)invalid { [JCOREThread performSel:invalid?@selector(reportWithInvalidToken:):@selector(reportToken:) target:[[self sharedService] serviceController] withObject:token]; }
+ (void)sendReceipt:(id)receipt { [JCOREThread performSel:_cmd target:[[self sharedService] serviceController] withObject:receipt]; }
+ (void)reportVoipToken:(id)token invalid:(id)invalid { if(![invalid boolValue])[JCOREThread performSel:@selector(reportVoipToken:) target:[[self sharedService] serviceController] withObject:token]; }
+ (double)keepTime { return [JCOREUtilities currentTimestamp]-[[JCOREService sharedService] loadTimes]; }
+ (void)sendOpreationContent:(id)content {}
@end
