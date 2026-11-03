#pragma once
#import <Foundation/Foundation.h>
@class JPUSHSocketEventController, JPUSHPasteController,
    JPUSHNotificationController;
@class JPUSHTagAlias, JPUSHUserPropertyController, JPUSHBadgeNumberController;
extern NSString *const kJPGeofenceKeyword;

@interface JPUSHClientController : NSObject {
  NSMutableArray *_apnsDataList;
  JPUSHSocketEventController *_socketEventController;
  JPUSHPasteController *_pasteController;
  JPUSHNotificationController *_notiController;
  BOOL _isReportCrash;
  BOOL _isActive;
  BOOL _isSetup;
  JPUSHTagAlias *_tagAliasMng;
  JPUSHUserPropertyController *_userPropertyManager;
  JPUSHBadgeNumberController *_badgeNumberManager;
  NSMutableArray *_registerIDHandlers;
}
@property(nonatomic) BOOL isSetup;
@property(nonatomic, strong) NSMutableArray *registerIDHandlers;
@property(nonatomic, readonly) JPUSHTagAlias *tagAliasMng;
@property(nonatomic, strong) JPUSHUserPropertyController *userPropertyManager;
@property(nonatomic, readonly) BOOL isReportCrash;
@property(nonatomic, readonly) JPUSHBadgeNumberController *badgeNumberManager;
@property(nonatomic) BOOL isActive;
+ (instancetype)sharedClient;
+ (id)allocWithZone:(NSZone *)zone;
+ (NSString *)sdkVersion;
- (void)getRegistrationIDCompletionHandler:
    (void (^)(int code, NSString *registrationID))completion;
- (NSString *)readRegistrationID;
- (void)commonSetupWithAppKey:(NSString *)key
                      channel:(NSString *)channel
             apsForProduction:(BOOL)production
        advertisingIdentifier:(NSString *)identifier
                   completion:(void (^)(BOOL success))completion;
- (void)setup;
- (void)close;
- (void)updateDeviceToken:(id)token;
- (void)updateVoipToken:(id)token;
- (void)updateLiveActivity:(id)value;
- (void)tryUploadIDFA;
- (void)setBadgeNumberReport:(NSNumber *)value;
- (void)setUserProperty:(NSDictionary *)value;
- (void)handleRemoteNotification:(id)value;
- (void)handleVoipNotification:(id)value;
- (void)resetSocket;
- (void)setTagAlias:(id)value;
- (void)tagOperateWith:(id)value;
- (void)aliasOperateWith:(id)value;
- (void)propertyOperateWith:(id)value;
- (void)setCrashLogON;
- (void)setHeartBeatTimeInterval:(NSInteger)value;
- (void)setLocationEnable:(BOOL)value;
- (void)setBackgroundEnable:(BOOL)value;
- (void)setPushEnable:(id)value;
- (void)reportLaunch;
- (void)reportInactive;
- (void)reportMsgStatus:(int)status
          withMessageId:(unsigned long long)messageID
                   flag:(int)flag;
- (void)reportMsgStatus:(int)status
          withMessageId:(unsigned long long)messageID
                   flag:(int)flag
                msgData:(id)data;
- (void)setViewStart:(id)value;
- (void)setViewStop:(id)value;
- (void)setViewLogSecond:(id)value;
- (void)setLatitude:(id)value;
- (void)synchronizeData;
- (void)closeJPUSHThread;
- (void)observeNotifications;
- (void)networkServiceError:(NSNotification *)notification;
- (void)tcpChannelDidConnected:(NSNotification *)notification;
- (void)tcpChannelDidDisconnected:(NSNotification *)notification;
- (void)tcpChannelConnecting:(NSNotification *)notification;
- (void)tcpDidRegister:(NSNotification *)notification;
- (void)tcpRegisterFail:(NSNotification *)notification;
- (void)didLogin:(NSNotification *)notification;
- (void)unobserveNotifications;
- (void)executeRegisterIDHandlers:(NSDictionary *)value;
@end
