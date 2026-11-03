/*
 *	| |    | |  \ \  / /  | |    | |   / _______|
 *	| |____| |   \ \/ /   | |____| |  / /
 *	| |____| |    \  /    | |____| |  | |   _____
 * 	| |    | |    /  \    | |    | |  | |  |____ |
 *  | |    | |   / /\ \   | |    | |  \ \______| |
 *  | |    | |  /_/  \_\  | |    | |   \_________|
 *
 * Copyright (c) 2011 ~ 2017 Shenzhen HXHG. All rights reserved.
 */

#pragma once
#define JPUSH_VERSION_NUMBER 5.9.0

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class CLRegion;
@class UILocalNotification;
@class CLLocation;
@class UNNotificationCategory;
@class UNNotificationSettings;
@class UNNotificationRequest;
@class UNNotification;
@class UIView;
@protocol JPUSHRegisterDelegate;
@protocol JPUSHGeofenceDelegate;
@protocol JPUSHNotiInMessageDelegate;
@protocol JPUSHInAppMessageDelegate;

typedef void (^JPUSHTagsOperationCompletion)(NSInteger iResCode, NSSet *_Nullable iTags, NSInteger seq);
typedef void (^JPUSHTagValidOperationCompletion)(NSInteger iResCode, NSSet *_Nullable iTags, NSInteger seq, BOOL isBind);
typedef void (^JPUSHAliasOperationCompletion)(NSInteger iResCode, NSString *_Nullable iAlias, NSInteger seq);
typedef void (^JPUSHPropertiesOperationCompletion)(NSInteger iResCode, NSDictionary *_Nullable properties, NSInteger seq);
typedef void (^JPUSHLiveActivityTokenCompletion)(NSInteger iResCode, NSString *_Nullable iLiveActivityId, NSData *  _Nullable pushToken, NSInteger seq);

extern NSString *const kJPFNetworkIsConnectingNotification; 
extern NSString *const kJPFNetworkDidSetupNotification;     
extern NSString *const kJPFNetworkDidCloseNotification;     
extern NSString *const kJPFNetworkDidRegisterNotification;  
extern NSString *const kJPFNetworkFailedRegisterNotification; 
extern NSString *const kJPFNetworkDidLoginNotification;     
extern NSString *const kJPFNetworkDidReceiveMessageNotification;         
extern NSString *const kJPFServiceErrorNotification;  

typedef NS_OPTIONS(NSUInteger, JPAuthorizationOptions) {
    JPAuthorizationOptionNone    = 0,   
    JPAuthorizationOptionBadge   = (1 << 0),    
    JPAuthorizationOptionSound   = (1 << 1),    
    JPAuthorizationOptionAlert   = (1 << 2),    
    JPAuthorizationOptionCarPlay = (1 << 3),    
    JPAuthorizationOptionCriticalAlert NS_AVAILABLE_IOS(12.0) = (1 << 4) ,   
    JPAuthorizationOptionProvidesAppNotificationSettings NS_AVAILABLE_IOS(12.0) = (1 << 5) ,      
    JPAuthorizationOptionProvisional NS_AVAILABLE_IOS(12.0) = (1 << 6) ,     
    JPAuthorizationOptionAnnouncement NS_AVAILABLE_IOS(13.0) = (1 << 7) , 
};

typedef NS_ENUM(NSUInteger, JPAuthorizationStatus) {
    JPAuthorizationNotDetermined    = 0,   
    JPAuthorizationStatusDenied,    
    JPAuthorizationStatusAuthorized,    
    JPAuthorizationStatusProvisional NS_AVAILABLE_IOS(12.0),    
};

@interface JPUSHRegisterEntity : NSObject

@property (nonatomic, assign) NSInteger types;

@property (nonatomic, strong, nullable) NSSet *categories;
@end

@interface JPushNotificationIdentifier : NSObject<NSCopying, NSCoding>

@property (nonatomic, copy, nullable) NSArray<NSString *> * identifiers; 
@property (nonatomic, copy, nullable) UILocalNotification * notificationObj NS_DEPRECATED_IOS(4_0, 10_0);  
@property (nonatomic, assign) BOOL delivered NS_AVAILABLE_IOS(10_0); 
@property (nonatomic, copy, nullable) void (^findCompletionHandler)(NSArray * _Nullable results); 

@end

@interface JPushNotificationSound : NSObject <NSCopying, NSCoding>
@property (nonatomic, copy, nullable) NSString *soundName; 
@property (nonatomic, copy, nullable) NSString *criticalSoundName NS_AVAILABLE_IOS(12.0); 
@property (nonatomic, assign) float criticalSoundVolume NS_AVAILABLE_IOS(12.0); 
@end

@interface JPushNotificationContent : NSObject<NSCopying, NSCoding>

@property (nonatomic, copy) NSString *title;                
@property (nonatomic, copy) NSString *subtitle;             
@property (nonatomic, copy) NSString *body;                 
@property (nonatomic, copy, nullable) NSNumber *badge;                
@property (nonatomic, copy) NSString *action NS_DEPRECATED_IOS(8_0, 10_0); 
@property (nonatomic, copy) NSString *categoryIdentifier;   
@property (nonatomic, copy) NSDictionary *userInfo;         
@property (nonatomic, copy, nullable) NSString *sound;                
@property (nonatomic, copy, nullable) JPushNotificationSound *soundSetting NS_AVAILABLE_IOS(10.0);   
@property (nonatomic, copy) NSArray *attachments NS_AVAILABLE_IOS(10_0);                 
@property (nonatomic, copy) NSString *threadIdentifier NS_AVAILABLE_IOS(10_0); 
@property (nonatomic, copy) NSString *launchImageName NS_AVAILABLE_IOS(10_0);  
@property (nonatomic, copy) NSString *summaryArgument NS_AVAILABLE_IOS(12.0);  
@property (nonatomic, assign) NSUInteger summaryArgumentCount NS_AVAILABLE_IOS(12.0); 
@property (nonatomic, copy, nullable) NSString *targetContentIdentifier NS_AVAILABLE_IOS(13.0);  

@property (nonatomic, assign) NSUInteger interruptionLevel NS_AVAILABLE_IOS(15.0);

@property (nonatomic, assign) double relevanceScore NS_AVAILABLE_IOS(15.0);

@property (nonatomic, copy, nullable) NSString * filterCriteria NS_AVAILABLE_IOS(16.0); 

@end

@interface JPushNotificationTrigger : NSObject<NSCopying, NSCoding>

@property (nonatomic, assign) BOOL repeat;                  
@property (nonatomic, copy) NSDate *fireDate NS_DEPRECATED_IOS(2_0, 10_0);           
@property (nonatomic, copy) CLRegion *region NS_AVAILABLE_IOS(8_0);                  
@property (nonatomic, copy) NSDateComponents *dateComponents NS_AVAILABLE_IOS(10_0); 
@property (nonatomic, assign) NSTimeInterval timeInterval NS_AVAILABLE_IOS(10_0);    

@end

@interface JPushNotificationRequest : NSObject<NSCopying, NSCoding>

@property (nonatomic, copy) NSString *requestIdentifier;    
@property (nonatomic, copy) JPushNotificationContent *content; 
@property (nonatomic, copy) JPushNotificationTrigger *trigger; 
@property (nonatomic, copy, nullable) void (^completionHandler)(id result); 

@end

@interface JPushInAppMessage : NSObject

@property (nonatomic, copy)   NSString *mesageId;    
@property (nonatomic, copy)   NSString *title;       
@property (nonatomic, copy)   NSString *content;     
@property (nonatomic, strong) NSArray  *target;      
@property (nonatomic, copy)   NSString *clickAction; 
@property (nonatomic, strong) NSDictionary *extras;  

@end

@interface JPushCollectControl : NSObject

@property (nonatomic, assign) BOOL ssid;

@property (nonatomic, assign) BOOL bssid;

@property (nonatomic, assign) BOOL cell;

@property (nonatomic, assign) BOOL gps;

@end

@interface JPUSHService : NSObject

+ (void)setupWithOption:(nullable NSDictionary *)launchingOption
                 appKey:(NSString *)appKey
                channel:(nullable NSString *)channel
       apsForProduction:(BOOL)isProduction;

+ (void)setupWithOption:(nullable NSDictionary *)launchingOption
                 appKey:(NSString *)appKey
                channel:(nullable NSString *)channel
       apsForProduction:(BOOL)isProduction
  advertisingIdentifier:(nullable NSString *)advertisingId;

+ (void)registerForRemoteNotificationTypes:(NSUInteger)types
                                categories:(nullable NSSet *)categories;

+ (void)registerForRemoteNotificationConfig:(JPUSHRegisterEntity *)config delegate:(nullable id<JPUSHRegisterDelegate>)delegate;

+ (void)registerDeviceToken:(NSData *)deviceToken;

+ (void)registerLiveActivity:(NSString *)activityAttributes
            pushToStartToken:(nullable NSData *)pushToStartToken
                  completion:(nullable JPUSHLiveActivityTokenCompletion)completion
                        seq:(NSInteger)seq;

+ (void)registerLiveActivity:(NSString *)liveActivityId
                   pushToken:(nullable NSData *)pushToken
                  completion:(nullable JPUSHLiveActivityTokenCompletion)completion
                         seq:(NSInteger)seq;

+ (void)handleRemoteNotification:(NSDictionary *)remoteInfo;

+ (void)registerVoipToken:(NSData *)voipToken;

+ (void)handleVoipNotification:(NSDictionary *)remoteInfo;

+ (void)requestNotificationAuthorization:(nullable void (^)(JPAuthorizationStatus status))completion;

+ (void)openSettingsForNotification:(nullable void (^)(BOOL success))completionHandler NS_AVAILABLE_IOS(8_0);

+ (void)addTags:(NSSet<NSString *> *)tags
     completion:(nullable JPUSHTagsOperationCompletion)completion
            seq:(NSInteger)seq;

+ (void)setTags:(NSSet<NSString *> *)tags
     completion:(nullable JPUSHTagsOperationCompletion)completion
            seq:(NSInteger)seq;

+ (void)deleteTags:(NSSet<NSString *> *)tags
        completion:(nullable JPUSHTagsOperationCompletion)completion
               seq:(NSInteger)seq;

+ (void)cleanTags:(nullable JPUSHTagsOperationCompletion)completion
              seq:(NSInteger)seq;

+ (void)getAllTags:(nullable JPUSHTagsOperationCompletion)completion
               seq:(NSInteger)seq;

+ (void)validTag:(NSString *)tag
      completion:(nullable JPUSHTagValidOperationCompletion)completion
             seq:(NSInteger)seq;

+ (void)setAlias:(NSString *)alias
      completion:(nullable JPUSHAliasOperationCompletion)completion
             seq:(NSInteger)seq;

+ (void)deleteAlias:(nullable JPUSHAliasOperationCompletion)completion
                seq:(NSInteger)seq;

+ (void)getAlias:(nullable JPUSHAliasOperationCompletion)completion
             seq:(NSInteger)seq;

+ (NSSet *)filterValidTags:(NSSet *)tags;

+ (void)setProperties:(NSDictionary *)properties
           completion:(nullable JPUSHPropertiesOperationCompletion)completion
                  seq:(NSInteger)seq;

+ (void)deleteProperties:(NSSet<NSString *> *)keys
              completion:(nullable JPUSHPropertiesOperationCompletion)completion
                     seq:(NSInteger)seq;

+ (void)cleanProperties:(nullable JPUSHPropertiesOperationCompletion)completion
                    seq:(NSInteger)seq;

+ (void)pageEnterTo:(NSString *)pageName;

+ (void)pageLeave:(NSString *)pageName;

+ (void)setInAppMessageDelegate:(id<JPUSHInAppMessageDelegate>)inAppMessageDelegate;

+ (void)startLogPageView:(NSString *)pageName __attribute__((deprecated("JCore 1.1.8 版本已过期")));

+ (void)stopLogPageView:(NSString *)pageName __attribute__((deprecated("JCore 1.1.8 版本已过期")));

+ (void)beginLogPageView:(NSString *)pageName duration:(int)seconds __attribute__((deprecated("JCore 1.1.8 版本已过期")));

+ (void)crashLogON;

+ (void)setLatitude:(double)latitude longitude:(double)longitude;

+ (void)setLocation:(CLLocation *)location;

+ (void)setGeofeneceMaxCount:(NSInteger)count;

+ (void)setGeofenecePeriodForInside:(NSInteger)seconds;

+ (void)registerLbsGeofenceDelegate:(id<JPUSHGeofenceDelegate>)delegate withLaunchOptions:(nullable NSDictionary *)launchOptions;

+ (void)removeGeofenceWithIdentifier:(NSString *)geofenceId;

+ (void)addNotification:(JPushNotificationRequest *)request;

+ (void)removeNotification:(nullable JPushNotificationIdentifier *)identifier;

+ (void)findNotification:(JPushNotificationIdentifier *)identifier;

+ (UILocalNotification *)setLocalNotification:(NSDate *)fireDate
                                    alertBody:(NSString *)alertBody
                                        badge:(int)badge
                                  alertAction:(NSString *)alertAction
                                identifierKey:(NSString *)notificationKey
                                     userInfo:(NSDictionary *)userInfo
                                    soundName:(NSString *)soundName __attribute__((deprecated("JPush 2.1.9 版本已过期")));

+ (UILocalNotification *)setLocalNotification:(NSDate *)fireDate
                                    alertBody:(NSString *)alertBody
                                        badge:(int)badge
                                  alertAction:(NSString *)alertAction
                                identifierKey:(NSString *)notificationKey
                                     userInfo:(NSDictionary *)userInfo
                                    soundName:(NSString *)soundName
                                       region:(CLRegion *)region
                           regionTriggersOnce:(BOOL)regionTriggersOnce
                                     category:(NSString *)category NS_AVAILABLE_IOS(8_0) __attribute__((deprecated("JPush 2.1.9 版本已过期")));

+ (void)showLocalNotificationAtFront:(UILocalNotification *)notification
                       identifierKey:(NSString *)notificationKey __attribute__((deprecated("JPush 2.1.9 版本已过期")));

+ (void)deleteLocalNotificationWithIdentifierKey:(NSString *)notificationKey __attribute__((deprecated("JPush 2.1.9 版本已过期")));

+ (void)deleteLocalNotification:(UILocalNotification *)localNotification __attribute__((deprecated("JPush 2.1.9 版本已过期")));

+ (NSArray *)findLocalNotificationWithIdentifier:(NSString *)notificationKey __attribute__((deprecated("JPush 2.1.9 版本已过期")));

+ (void)clearAllLocalNotifications __attribute__((deprecated("JPush 2.1.9 版本已过期")));

+ (BOOL)setBadge:(NSInteger)value;

+ (void)resetBadge;

+ (void)setMobileNumber:(NSString *)mobileNumber completion:(nullable void (^)(NSError *error))completion;

+ (NSString *)registrationID;

+ (void)registrationIDCompletionHandler:(void(^)(int resCode,NSString * _Nullable registrationID))completionHandler;

+ (void)setDebugMode;

+ (void)setLogOFF;

+ (void)setCollectControl:(JPushCollectControl *)control;

+ (void)setHeartBeatTimeInterval:(NSInteger)interval;

+ (void)setLocationEanable:(BOOL)isEanble;

+ (void)setPushEnable:(BOOL)isEnable completion:(nullable void (^)(NSInteger iResCode))completion;

+ (void)setBackgroundEnable:(BOOL)isEnable;

+ (void)setSmartPushEnable:(BOOL)isEnable;

+ (void)setDataInsightsEnable:(BOOL)isEnable;

+ (void)setNotiInMessageDelegate:(id<JPUSHNotiInMessageDelegate>)notiInMessageDelegate;

+ (void) setTags:(NSSet *)tags
           alias:(NSString *)alias
callbackSelector:(SEL)cbSelector
          target:(id)theTarget __attribute__((deprecated("JPush 2.1.1 版本已过期")));
+ (void) setTags:(NSSet *)tags
           alias:(NSString *)alias
callbackSelector:(SEL)cbSelector
          object:(id)theTarget __attribute__((deprecated("JPush 3.0.6 版本已过期")));
+ (void) setTags:(NSSet *)tags
callbackSelector:(SEL)cbSelector
          object:(id)theTarget __attribute__((deprecated("JPush 3.0.6 版本已过期")));
+ (void)setTags:(NSSet *)tags
          alias:(NSString *)alias
fetchCompletionHandle:(void (^)(int iResCode, NSSet *iTags, NSString *iAlias))completionHandler __attribute__((deprecated("JPush 3.0.6 版本已过期")));
+ (void)  setTags:(NSSet *)tags
aliasInbackground:(NSString *)alias __attribute__((deprecated("JPush 3.0.6 版本已过期")));
+ (void)setAlias:(NSString *)alias
callbackSelector:(SEL)cbSelector
          object:(id)theTarget __attribute__((deprecated("JPush 3.0.6 版本已过期")));

@end

@class UNUserNotificationCenter;
@class UNNotificationResponse;

@protocol JPUSHRegisterDelegate <NSObject>

- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(NSInteger options))completionHandler;

- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void(^)(void))completionHandler;

- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center openSettingsForNotification:(UNNotification *)notification NS_AVAILABLE_IOS(12.0);

- (void)jpushNotificationAuthorization:(JPAuthorizationStatus)status withInfo:(nullable NSDictionary *)info;

@end

@protocol JPUSHGeofenceDelegate <NSObject>

- (void)jpushGeofenceRegion:(nullable NSDictionary *)geofence
                      error:(nullable NSError *)error;

- (void)jpushCallbackGeofenceReceived:(nullable NSArray<NSDictionary*> *)geofenceList;

- (void)jpushGeofenceIdentifer:(NSString *)geofenceId didEnterRegion:(NSDictionary *_Nullable)userInfo error:(NSError *_Nullable)error __attribute__((deprecated("JPush 3.6.0 版本已过期")));

- (void)jpushGeofenceIdentifer:(NSString *)geofenceId didExitRegion:(NSDictionary *_Nullable)userInfo error:(NSError *_Nullable)error __attribute__((deprecated("JPush 3.6.0 版本已过期")));

@end

@protocol JPUSHNotiInMessageDelegate <NSObject>

- (void)jPushNotiInMessageDidShowWithContent:(NSDictionary *)content;

- (void)jPushNotiInMessageDidClickWithContent:(NSDictionary *)content;

@end

@protocol JPUSHInAppMessageDelegate <NSObject>

- (void)jPushInAppMessageDidShow:(JPushInAppMessage *)inAppMessage;

- (void)jPushInAppMessageDidClick:(JPushInAppMessage *)inAppMessage;

@end

NS_ASSUME_NONNULL_END
