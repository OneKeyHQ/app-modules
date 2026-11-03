#import "RecoveredServiceController.h"
#import "RecoveredTransportDependencies.h"
#import "RecoveredServiceLiterals.h"
#import "JCORENetworkReachabilityManager.h"
@interface JCOREConfigController:NSObject
+ (BOOL)setConfig:(id)config;
+ (void)setNewAdvertisingId:(id)identifier;
@end
@interface JCORETcpEventController:NSObject
- (void)clearRegisterAndDeviceInfo;
- (void)reportToken:(id)token;
- (void)reportWithInvalidToken:(id)token;
- (void)sendReceipt:(id)receipt;
- (void)reportVoipToken:(id)token;
@end
#import "JCOREHttpEventController.h"
#import "JCORELocationController.h"
#import "JCommonService.h"
#import "JCommonCrashReport.h"
@interface JCOREUtilities:NSObject
+ (id)model;
+ (id)stringByMD5:(id)value;
+ (void)resetDeviceInfoCache;
+ (id)deviceMacAddress;
+ (void)cacheDeviceMacAddress:(id)address;
@end
@interface JCORECacheUtilities:NSObject
+ (id)persistentObjectForKey:(id)key;
+ (void)setPersistentObject:(id)object forKey:(id)key;
@end
#import "JCOREAPIRecordController.h"
#import "JCORENetChannel.h"
#import "JCORENetDataManager.h"
#import "JCOREMacAddressManager.h"
@interface JCOREService (ControllerDependencies)
+ (BOOL)getRegFingerSwitch;
@end
@interface JCOREThread (ControllerDependencies)
+ (void)performSel:(SEL)selector target:(id)target withObject:(id)object;
@end
@implementation JCOREServiceController
@synthesize hasSetup=_hasSetup,getMacSuccess=_getMacSuccess,sentryCrashHasStart=_sentryCrashHasStart,sentryCrashLogHasStart=_sentryCrashLogHasStart,crashHasStart=_crashHasStart,tcpEventController=_tcpEventController,httpEventController=_httpEventController;
@dynamic locationInfo;
- (void)setupService:(id)config {
    if(![JCOREConfigController setConfig:config])return;
    if(self.hasSetup){REC_SERVICE_LOG(0x40,"-[JCOREServiceController setupService:]",0x37,REC_SERVICE_e6ff8);return;}
    self.hasSetup=YES;_tcpEventController=[[JCORETcpEventController alloc] init];_httpEventController=[[JCOREHttpEventController alloc] init];
    [self getDeviceMacAddress];[self checkNeedFingerReRegister];[JCORENetChannel setupNetService];
    Class portrait=NSClassFromString(@"JPortraitService");
    if(portrait){SEL selector=NSSelectorFromString(@"setupService");NSInvocation *invocation=[NSInvocation invocationWithMethodSignature:[portrait methodSignatureForSelector:selector]];invocation.target=portrait;invocation.selector=selector;[invocation invoke];}
    else [JCommonService setupService];
    [JCommonCrashReport setupService];
}
- (void)checkNeedFingerReRegister {
    REC_SERVICE_LOG(0x100,"-[JCOREServiceController checkNeedFingerReRegister]",99,REC_SERVICE_e7058);
    id fingerprint=[JCOREUtilities stringByMD5:[JCOREUtilities model]];
    id cached=[JCORECacheUtilities persistentObjectForKey:REC_SERVICE_e7078];
    if(!cached&&[fingerprint isKindOfClass:NSString.class]){[fingerprint length];[JCORECacheUtilities setPersistentObject:fingerprint forKey:REC_SERVICE_e7078];}
    if(![JCOREService getRegFingerSwitch]){REC_SERVICE_LOG(0x100,"-[JCOREServiceController checkNeedFingerReRegister]",0x6d,REC_SERVICE_e7098);return;}
    if([fingerprint isKindOfClass:NSString.class]){[fingerprint length];if([cached isKindOfClass:NSString.class]){[cached length];if(![fingerprint isEqualToString:cached]){
        REC_SERVICE_LOG(0x100,"-[JCOREServiceController checkNeedFingerReRegister]",0x72,REC_SERVICE_e70b8);
        [JCOREUtilities resetDeviceInfoCache];[self.tcpEventController clearRegisterAndDeviceInfo];[JCORECacheUtilities setPersistentObject:fingerprint forKey:REC_SERVICE_e7078];
    }}}
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
- (void)setNewAdvertisingId:(id)identifier { [JCOREConfigController setNewAdvertisingId:identifier]; }
- (void)setLocationInfo:(NSDictionary *)location { [[JCORELocationController sharedInstane] setLocation:location]; }
- (NSDictionary *)locationInfo { return [[JCORELocationController sharedInstane] currentLocationInfo]; }
- (void)setCrashLogON { if(_crashHasStart)return;_crashHasStart=YES;[JCommonService setCrashLogON]; }
- (void)setSentryCrashCountON { [self setSentryCrashON];[JCommonCrashReport startSentryCrashCountReport]; }
- (void)setSentryCrashLogON { [self setSentryCrashON];[JCommonCrashReport startSentryLogReport]; }
- (void)setSentryCrashON { if(_sentryCrashHasStart)return;_sentryCrashHasStart=YES;[JCommonService setSentryCrashON]; }
- (void)setSupersetMoniterOn { [JCORESupersetManager setSupersetMoniterOn]; }
- (void)clearSupersetMoniterCache { [JCORESupersetManager clearAllCachedDatas]; }
- (void)setNetDataMoniterOn { [JCORENetDataManager setDataMoniterOn]; }
- (void)forceRequestReportAddress { [JCORENetChannel forceRequestReportAddress]; }
- (void)apiRecord:(id)arguments { [JCOREAPIRecordController apiRecord:arguments[0] methodName:arguments[1]]; }
- (void)getDeviceMacAddress {
    if(self.getMacSuccess)return;
    if(@available(iOS 14.0,*)){REC_SERVICE_LOG(0x100,"-[JCOREServiceController getDeviceMacAddress]",199,REC_SERVICE_e70d8,"-[JCOREServiceController getDeviceMacAddress]");}
    else if([[JCORENetworkReachabilityManager sharedManager] networkReachabilityStatus]==2){
        REC_SERVICE_LOG(0x100,"-[JCOREServiceController getDeviceMacAddress]",0xcd,REC_SERVICE_e70f8);
        id cached=[JCOREUtilities deviceMacAddress];REC_SERVICE_LOG(0x100,"-[JCOREServiceController getDeviceMacAddress]",0xcf,REC_SERVICE_e7118,cached);
        dispatch_queue_t queue=dispatch_queue_create("io.jiguang.hxhg.mac",DISPATCH_QUEUE_SERIAL);
        dispatch_async(queue,^{[JCOREMacAddressManager getDeviceMacFromMDNS:^(NSString *value){
            if([value isKindOfClass:NSString.class]&&value.length){self.getMacSuccess=YES;if(![value isEqualToString:cached]){
                REC_SERVICE_LOG(0x80,"-[JCOREServiceController getDeviceMacAddress]_block_invoke_2",0xd7,REC_SERVICE_e7138,value);
                [JCOREThread performSel:@selector(cacheDeviceMacAddress:) target:self withObject:value];
            }}
        }];});
    }
}
- (void)cacheDeviceMacAddress:(id)address { [JCOREUtilities cacheDeviceMacAddress:address]; }
- (void)reportToken:(id)token { [self.tcpEventController reportToken:token]; }
- (void)reportWithInvalidToken:(id)token { [self.tcpEventController reportWithInvalidToken:token]; }
- (void)sendReceipt:(id)receipt { [self.tcpEventController sendReceipt:receipt]; }
- (void)reportVoipToken:(id)token { [self.tcpEventController reportVoipToken:token]; }
@end
