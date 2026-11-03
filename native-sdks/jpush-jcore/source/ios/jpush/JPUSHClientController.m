#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JCOREClientInfo.h"
#import "JCORELog.h"
#import "JPUSHAutomatedTester.h"
#import "JPUSHBadgeNumber.h"
#import "JPUSHClientController.h"
#import "JPUSHConfigEntity.h"
#import "JPUSHHelper.h"
#import "JPUSHMessageReport.h"
#import "JPUSHPropertyManagerController.h"
#import "JPUSHPropertyVO.h"
#import "JPUSHTagAliasManagerController.h"
#import "JPUSHUserPropertyController.h"
#import "JPushLiveActivityTokenVO.h"
#import "RecoveredAPNsController.h"
#import "RecoveredCoreService.h"
#import "RecoveredModels.h"
#import <UIKit/UIKit.h>

#import "JPUSHSocketEventController.h"
#import "JPUSHPasteController.h"
#import "JPUSHNotificationController.h"
#import "JPUSHLegacyTagAlias.h"
#import "JPUSHGeofenceManager.h"
#import "JPUSHStopPush.h"
#import "JPushVoipTokenController.h"
#import "JPUSHLiveActivity.h"
@interface JCOREConnectManager : NSObject
+ (instancetype)shared;
- (void)closeConection;
- (void)connect;
@end
extern NSString *const kJCORETcpChannelConnectedNotification;
extern NSString *const kJCORETcpChannelDisconnectNotification;
extern NSString *const kJCORETcpChannelConnectingNotification;
extern NSString *const kJCORENetworkDidRegisterNotification;
extern NSString *const kJCORENetworkFailedRegisterNotification;
extern NSString *const kJCORENetworkDidLoginNotification;
extern NSString *const kJCORENetworkErrorNotification;
extern NSString *const kJPFServiceErrorNotification;
extern NSString *const kJPFNetworkDidSetupNotification;
extern NSString *const kJPFNetworkDidCloseNotification;
extern NSString *const kJPFNetworkIsConnectingNotification;
extern NSString *const kJPFNetworkDidRegisterNotification;
extern NSString *const kJPFNetworkFailedRegisterNotification;
extern NSString *const kJPFNetworkDidLoginNotification;
#define CLIENT_LOG(asyncValue, flagValue, lineValue, formatValue, ...)         \
  [JCORELog log:asyncValue                                                     \
          level:0x3ff                                                          \
           flag:flagValue                                                      \
        context:0                                                              \
           file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"         \
                "ios-jpush/JPush/JPush/Controller/JPUSHClientController.m"     \
       function:__PRETTY_FUNCTION__                                            \
           line:lineValue                                                      \
            tag:nil                                                            \
         format:formatValue, ##__VA_ARGS__]
NSString *const kJPGeofenceKeyword = @"geofence";
static JPUSHClientController *SharedClient;
@implementation JPUSHClientController
@synthesize isSetup = _isSetup, registerIDHandlers = _registerIDHandlers,
            tagAliasMng = _tagAliasMng;
@synthesize userPropertyManager = _userPropertyManager,
            isReportCrash = _isReportCrash;
@synthesize badgeNumberManager = _badgeNumberManager, isActive = _isActive;
+ (instancetype)sharedClient {
  static dispatch_once_t once;
  dispatch_once(&once, ^{
    SharedClient = [[JPUSHClientController alloc] init];
  });
  return SharedClient;
}
+ (id)allocWithZone:(NSZone *)zone {
  static dispatch_once_t once;
  dispatch_once(&once, ^{
    if (!SharedClient)
      SharedClient = [super allocWithZone:zone];
  });
  return SharedClient;
}
- (instancetype)init {
  if ((self = [super init])) {
    _badgeNumberManager = [JPUSHBadgeNumberController new];
    _isActive = YES;
    [self observeNotifications];
  }
  return self;
}
- (void)dealloc {
  [self close];
  [self unobserveNotifications];
}
- (void)getRegistrationIDCompletionHandler:
    (void (^__unsafe_unretained)(int, NSString *))completion {
  id input = (__bridge id)(__bridge void *)completion;
  if (!input || [input isKindOfClass:NSNull.class])
    return;
  NSString *registrationID = [self readRegistrationID];
  if (!registrationID || !registrationID.length) {
    if (!self.registerIDHandlers)
      self.registerIDHandlers = [NSMutableArray array];
    [self.registerIDHandlers addObject:input];
  } else {
    void (^handler)(int, NSString *) = input;
    dispatch_async(dispatch_get_main_queue(), ^{
      handler(0, registrationID);
    });
  }
}
- (NSString *)readRegistrationID {
  return [JCOREClientInfo registrationID];
}
- (void)commonSetupWithAppKey:(NSString *)key
                      channel:(NSString *)channel
             apsForProduction:(BOOL)production
        advertisingIdentifier:(NSString *)identifier
                   completion:(void (^)(BOOL))completion {
  JPUSHConfigEntity *config = [JPUSHConfigEntity new];
  config.appKey = key;
  config.advertisingId =
      [identifier isKindOfClass:NSString.class] ? identifier : @"";
  config.isProduction = production;
  config.channel = channel;
  config.index = 0;
  config.completionHandler = ^(NSError *error, id<JCOREConfigProtocol> result) {
    BOOL success = YES;
    if (error) {
      CLIENT_LOG(NO, 1, 0x92, @"error:%@", error.description);
      success = NO;
    } else if (![key isEqualToString:result.appKey]) {
      CLIENT_LOG(YES, 2, 0x96,
                 @"Error：JPush Appkey:%@ is different with the first sdk "
                 @"AppKey:%@, Please make them the same",
                 key, result.appKey);
      [JPUSHAutomatedTester PostAutomatedTestStatusNotification:2];
      [JCOREService setNewAdvertisingId:identifier];
    }
    completion(success);
  };
  [JCOREService setupWithConfig:config];
}
- (void)setup {
  if (_isSetup) {
    CLIENT_LOG(YES, 0x100, 0xa6, @"you have setup the jpush client");
    return;
  }
  _isSetup = YES;
  CLIENT_LOG(YES, 4, 0xab, @"Action - jpush setup");
  _socketEventController = [JPUSHSocketEventController new];
  _tagAliasMng = [JPUSHTagAlias new];
  [JPUSHDeviceTokenController startDeviceTokenUpdatedCheckTimer];
  [self reportLaunch];
  _pasteController = [JPUSHPasteController new];
  [_pasteController start];
  _notiController = [JPUSHNotificationController new];
  [_notiController start];
  [JPUSHTagAliasManagerController sharedInstance];
  [JPUSHPropertyManagerController sharedInstance];
  [JPUSHStopPushController shared];
  [[JPUSHGeofenceManager sharedGeoFence] setup];
}
- (void)close {
  _badgeNumberManager = nil;
}
- (void)updateDeviceToken:(id)token {
  if ([JPUSHDeviceTokenController judgeAndSetDeviceToken:token])
    [JPUSHDeviceTokenController uploadDeviceToken];
}
- (void)updateVoipToken:(id)token {
  if (!token) {
    CLIENT_LOG(YES, 0x100, 0xd6, @"voipToken is invalid");
  } else
    [[JPushVoipTokenController shared] uploadVoipToken:token];
}
- (void)updateLiveActivity:(JPushLiveActivityTokenVO *)value {
  if (value.reportType == 0) {
    NSString *alias = value.liveActivityAlias;
    NSData *token = value.pushToken;
    JPUSHLiveActivityTokenCompletion completion = value.completion;
    NSInteger seq = value.seq;
    [[JPushLiveActivityController shared] updateLiveActivity:alias
                                                   pushToken:token
                                                  completion:completion
                                                         seq:seq];
  } else if (value.reportType == 1) {
    NSString *type = value.attributesType;
    NSData *token = value.pushToStartToken;
    JPUSHLiveActivityTokenCompletion completion = value.completion;
    NSInteger seq = value.seq;
    [[JPushLiveActivityController shared] updateLiveActivity:type
                                            pushToStartToken:token
                                                  completion:completion
                                                         seq:seq];
  }
}
// The original exported entry points are intentionally empty.
- (void)tryUploadIDFA {
}
- (void)setBadgeNumberReport:(NSNumber *)value {
  [_badgeNumberManager setBadge:value];
}
- (JPUSHUserPropertyController *)userPropertyManager {
  if (!_userPropertyManager)
    _userPropertyManager = [JPUSHUserPropertyController new];
  return _userPropertyManager;
}
- (void)setUserProperty:(NSDictionary *)value {
  [self.userPropertyManager setUserProperty:value];
}
- (void)handleRemoteNotification:(id)value {
  if ([value isKindOfClass:NSArray.class] && [value count] > 1) {
    if ([JPUSHGeofenceManager existGeofence])
      [[JPUSHGeofenceManager sharedGeoFence]
          handleSilentPushGeofenceMessage:value[1]];
    if (!_apnsDataList)
      _apnsDataList = [NSMutableArray array];
    if ([_apnsDataList containsObject:value])
      return;
    if (_apnsDataList.count > 4)
      [_apnsDataList removeObjectAtIndex:0];
    [_apnsDataList addObject:value];
    int status = [value[0] integerValue];
    NSDictionary *info = value[1];
    id data = info[@"_j_data_"];
    NSString *token = info[@"deviceToken"];
    NSString *current = [JCOREClientInfo deviceToken];
    if (token.length && current.length && ![token isEqualToString:current]) {
      CLIENT_LOG(YES, 0x100, 0x128, @"there is a invaild device token！ %@",
                 token);
      [JCOREService reportToken:token invalid:YES];
    }
    unsigned long long messageID = [info[@"_j_msgid"] unsignedLongLongValue];
    id flag = info[@"_j_flag"];
    if (flag) {
      [self reportMsgStatus:status
              withMessageId:messageID
                       flag:[flag intValue]
                    msgData:data];
      return;
    }
    [self reportMsgStatus:status
            withMessageId:messageID
                     flag:-10000
                  msgData:data];
  }
  [JPUSHAutomatedTester PostAutomatedTestStatusNotification:5];
  [_notiController performSelector:@selector(checkCachedUndoNotification)
                        withObject:nil
                        afterDelay:1];
}
- (void)handleVoipNotification:(id)value {
  if (value && [value isKindOfClass:NSDictionary.class]) {
    NSUInteger messageID = [value[@"_j_msgid"] unsignedIntegerValue];
    if (messageID) {
      JPUSHVoipMessageReport *report = [JPUSHVoipMessageReport new];
      report.messageID =
          [NSString stringWithFormat:@"%llu", (unsigned long long)messageID];
      [report send];
      [self resetSocket];
    }
  }
}
- (void)resetSocket {
  __block NSInteger state = 0;
  dispatch_sync(dispatch_get_main_queue(), ^{
    state = UIApplication.sharedApplication.applicationState;
  });
  if ((NSUInteger)(state - 1) < 2) {
    [[JCOREConnectManager shared] closeConection];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC),
                   dispatch_get_main_queue(), ^{
                     [[JCOREConnectManager shared] connect];
                   });
  }
}
- (void)setTagAlias:(id)value {
  [[[JPUSHClientController sharedClient] tagAliasMng] setTagsAlias:value];
}
- (void)tagOperateWith:(JPUSHTagAliasVO *)value {
  id completion = value.tagsCompletion;
  if (value.opearteType == 5)
    completion = value.tagValidCompletion;
  [[JPUSHTagAliasManagerController sharedInstance] tagOperate:value.tags
                                                         type:value.opearteType
                                                andCompletion:completion
                                                       andSeq:value.seq];
}
- (void)aliasOperateWith:(JPUSHTagAliasVO *)value {
  [[JPUSHTagAliasManagerController sharedInstance]
       aliasOperate:value.alias
               type:value.opearteType
      andCompletion:value.aliasCompletion
             andSeq:value.seq];
}
- (void)propertyOperateWith:(JPUSHPropertyVO *)value {
  [[JPUSHPropertyManagerController sharedInstance]
      propertyOperate:value.propertiesInfo
                 type:value.opearteType
        andCompletion:value.propertyCompletion
               andSeq:value.seq];
}
- (void)setCrashLogON {
  [JPUSHAutomatedTester PostAutomatedTestStatusNotification:13];
  [JCOREService setCrashLogON];
}
- (void)setHeartBeatTimeInterval:(NSInteger)value {
  if ([JCOREService respondsToSelector:@selector(setHeartBeatTimeInterval:)])
    [JCOREService performSelector:@selector(setHeartBeatTimeInterval:)
                       withObject:@(value)];
  else
    CLIENT_LOG(NO, 1, 0x17b,
               @"[JPushService setHeartBeatTimeInterval] Action JCore must be "
               @"at least 5.0.2");
}
- (void)setLocationEnable:(BOOL)value {
  if ([JCOREService respondsToSelector:@selector(setLocationEnable:)])
    [JCOREService performSelector:@selector(setLocationEnable:)
                       withObject:@(value)];
  else
    CLIENT_LOG(NO, 1, 0x183,
               @"[JPushService setLocationEnable] Action JCore must be at "
               @"least 2.2.4");
}
- (void)setBackgroundEnable:(BOOL)value {
  if ([JCOREService respondsToSelector:@selector(setBackgroundEnable:)])
    [JCOREService performSelector:@selector(setBackgroundEnable:)
                       withObject:@(value)];
  else
    CLIENT_LOG(NO, 1, 0x18b,
               @"[JPushService setBackgroundEnable] Action JCore must be at "
               @"least 5.2.0");
}
- (void)setPushEnable:(id)value {
  id enable = [value valueForKey:@"isEnable"];
  id completion = [value valueForKey:@"completion"];
  if (enable && [enable isKindOfClass:NSNumber.class])
    [[JPUSHStopPushController shared] setPushEnable:[enable boolValue]
                                         completion:completion];
  else
    CLIENT_LOG(NO, 1, 0x196, @"setPushEnable: parameter error");
}
- (void)reportLaunch {
  if (self.isSetup)
    [_badgeNumberManager becomeActive];
}
- (void)reportInactive {
  if (self.isSetup)
    [_badgeNumberManager willInactive];
}
- (void)reportMsgStatus:(int)status
          withMessageId:(unsigned long long)messageID
                   flag:(int)flag {
  JPUSHMessageReportRequest *report = [JPUSHMessageReportRequest new];
  [report
      setActiveType:status
          messageID:messageID ? [NSString stringWithFormat:@"%llu", messageID]
                              : @""
               flag:flag];
  [report send];
}
- (void)reportMsgStatus:(int)status
          withMessageId:(unsigned long long)messageID
                   flag:(int)flag
                msgData:(id)data {
  JPUSHMessageReportRequest *report = [JPUSHMessageReportRequest new];
  if ([data isKindOfClass:NSString.class] && [data length])
    report.msgData = data;
  [report
      setActiveType:status
          messageID:messageID ? [NSString stringWithFormat:@"%llu", messageID]
                              : @""
               flag:flag];
  [report send];
}
- (void)setViewStart:(id)value {
  if (value)
    [JPUSHAutomatedTester PostAutomatedTestStatusNotification:8];
  else {
    [JPUSHAutomatedTester PostAutomatedTestStatusNotification:9];
    CLIENT_LOG(NO, 1, 0x1e3, @"参数错误，pageName不能为空");
  }
}
- (void)setViewStop:(id)value {
  if (value)
    [JPUSHAutomatedTester PostAutomatedTestStatusNotification:10];
  else {
    [JPUSHAutomatedTester PostAutomatedTestStatusNotification:9];
    CLIENT_LOG(NO, 1, 0x1f2, @"参数错误，pageName不能为空");
  }
}
- (void)setViewLogSecond:(id)value {
  if ([value count] < 2 && ![value[0] isKindOfClass:NSString.class]) {
    [JPUSHAutomatedTester PostAutomatedTestStatusNotification:12];
    CLIENT_LOG(NO, 1, 0x1fc, @"缺少页面Log时间或者页面的名称");
  } else
    [JPUSHAutomatedTester PostAutomatedTestStatusNotification:11];
}
- (void)setLatitude:(id)value {
  [JPUSHAutomatedTester PostAutomatedTestStatusNotification:14];
  [JCOREService setLocationInfo:value];
}
+ (NSString *)sdkVersion {
  return @"50900";
}
- (void)synchronizeData {
}
- (void)closeJPUSHThread {
  [NSThread exit];
}
- (void)observeNotifications {
  NSNotificationCenter *center = NSNotificationCenter.defaultCenter;
  [center addObserver:self
             selector:@selector(tcpChannelDidConnected:)
                 name:kJCORETcpChannelConnectedNotification
               object:nil];
  [center addObserver:self
             selector:@selector(tcpChannelDidDisconnected:)
                 name:kJCORETcpChannelDisconnectNotification
               object:nil];
  [center addObserver:self
             selector:@selector(tcpChannelConnecting:)
                 name:kJCORETcpChannelConnectingNotification
               object:nil];
  [center addObserver:self
             selector:@selector(tcpDidRegister:)
                 name:kJCORENetworkDidRegisterNotification
               object:nil];
  [center addObserver:self
             selector:@selector(tcpRegisterFail:)
                 name:kJCORENetworkFailedRegisterNotification
               object:nil];
  [center addObserver:self
             selector:@selector(didLogin:)
                 name:kJCORENetworkDidLoginNotification
               object:nil];
  [center addObserver:self
             selector:@selector(networkServiceError:)
                 name:kJCORENetworkErrorNotification
               object:nil];
}
- (void)networkServiceError:(NSNotification *)notification {
  [JPUSHHelper postNotification:kJPFServiceErrorNotification
                   withUserInfo:notification.userInfo];
}
- (void)tcpChannelDidConnected:(NSNotification *)notification {
  [JPUSHHelper postNotification:kJPFNetworkDidSetupNotification
                   withUserInfo:notification.userInfo];
}
- (void)tcpChannelDidDisconnected:(NSNotification *)notification {
  [JPUSHHelper postNotification:kJPFNetworkDidCloseNotification
                   withUserInfo:notification.userInfo];
}
- (void)tcpChannelConnecting:(NSNotification *)notification {
  [JPUSHHelper postNotification:kJPFNetworkIsConnectingNotification
                   withUserInfo:notification.userInfo];
}
- (void)tcpDidRegister:(NSNotification *)notification {
  [JPUSHHelper postNotification:kJPFNetworkDidRegisterNotification
                   withUserInfo:notification.userInfo];
  [self executeRegisterIDHandlers:notification.userInfo];
}
- (void)tcpRegisterFail:(NSNotification *)notification {
  [JPUSHHelper postNotification:kJPFNetworkFailedRegisterNotification
                   withUserInfo:notification.userInfo];
}
- (void)didLogin:(NSNotification *)notification {
  [[[JPUSHClientController sharedClient] tagAliasMng] sendFailedTagAlias];
  [JPUSHHelper postNotification:kJPFNetworkDidLoginNotification
                   withUserInfo:notification.userInfo];
}
- (void)unobserveNotifications {
  [NSNotificationCenter.defaultCenter removeObserver:self];
}
- (void)executeRegisterIDHandlers:(NSDictionary *)value {
  if (self.registerIDHandlers.count) {
    NSString *registrationID =
        value ? value[@"RegistrationID"] : [self readRegistrationID];
    int code = value ? [value[@"StatusCode"] intValue] : 0;
    NSArray *handlers = [self.registerIDHandlers copy];
    dispatch_async(dispatch_get_main_queue(), ^{
      for (id handler in handlers) {
        if (handler && ![handler isKindOfClass:NSNull.class])
          ((void (^)(int, NSString *))handler)(code, registrationID);
      }
    });
    [self.registerIDHandlers removeAllObjects];
  }
}
@end
#endif
