#import <Foundation/Foundation.h>
#if __has_include(<UIKit/UIKit.h>)
#import <UIKit/UIKit.h>
#else
@class UILocalNotification;
#endif

@class CLRegion;
@class CLLocation;
@class UNUserNotificationCenter;
@class UNNotification;
@class UNNotificationResponse;
@class UNNotificationSound;
@class UNNotificationAttachment;

NS_ASSUME_NONNULL_BEGIN

extern NSString *const kJPFNetworkIsConnectingNotification;
extern NSString *const kJPFNetworkDidSetupNotification;
extern NSString *const kJPFNetworkDidCloseNotification;
extern NSString *const kJPFNetworkDidRegisterNotification;
extern NSString *const kJPFNetworkFailedRegisterNotification;
extern NSString *const kJPFNetworkDidLoginNotification;
extern NSString *const kJPFNetworkDidReceiveMessageNotification;
extern NSString *const kJPFServiceErrorNotification;

typedef NS_OPTIONS(NSUInteger, JPAuthorizationOptions) {
    JPAuthorizationOptionNone = 0,
    JPAuthorizationOptionBadge = (1 << 0),
    JPAuthorizationOptionSound = (1 << 1),
    JPAuthorizationOptionAlert = (1 << 2),
    JPAuthorizationOptionCarPlay = (1 << 3),
    JPAuthorizationOptionCriticalAlert = (1 << 4),
    JPAuthorizationOptionProvidesAppNotificationSettings = (1 << 5),
    JPAuthorizationOptionProvisional = (1 << 6),
    JPAuthorizationOptionAnnouncement = (1 << 7),
};

typedef NS_ENUM(NSUInteger, JPAuthorizationStatus) {
    JPAuthorizationNotDetermined = 0,
    JPAuthorizationStatusDenied,
    JPAuthorizationStatusAuthorized,
    JPAuthorizationStatusProvisional,
};

typedef void (^JPUSHTagsOperationCompletion)(NSInteger iResCode, NSSet *_Nullable iTags, NSInteger seq);
typedef void (^JPUSHTagValidOperationCompletion)(NSInteger iResCode, NSSet *_Nullable iTags, NSInteger seq, BOOL isBind);
typedef void (^JPUSHAliasOperationCompletion)(NSInteger iResCode, NSString *_Nullable iAlias, NSInteger seq);
typedef void (^JPUSHPropertiesOperationCompletion)(NSInteger iResCode, NSDictionary *_Nullable properties, NSInteger seq);
typedef void (^JPUSHLiveActivityTokenCompletion)(NSInteger iResCode, NSString *_Nullable iLiveActivityId, NSData *_Nullable token, NSInteger seq);

@interface JPUSHRegisterEntity : NSObject

@property (nonatomic, assign) NSInteger types;
@property (nonatomic, strong, nullable) NSSet *categories;

@end

@interface JPushNotificationIdentifier : NSObject <NSCopying, NSCoding>

@property (nonatomic, copy, nullable) NSArray<NSString *> *identifiers;
@property (nonatomic, copy, nullable) UILocalNotification *notificationObj;
@property (nonatomic, assign) BOOL delivered;
@property (nonatomic, copy, nullable) void (^findCompletionHandler)(NSArray *_Nullable results);

@end

@interface JPushNotificationSound : NSObject <NSCopying, NSCoding>

@property (nonatomic, copy, nullable) NSString *soundName;
@property (nonatomic, copy, nullable) NSString *criticalSoundName;
@property (nonatomic, assign) float criticalSoundVolume;
@property (nonatomic, strong, nullable) UNNotificationSound *soundSetting;

@end

@interface JPushNotificationContent : NSObject <NSCopying, NSCoding>

@property (nonatomic, copy, nullable) NSString *title;
@property (nonatomic, copy, nullable) NSString *subtitle;
@property (nonatomic, copy, nullable) NSString *body;
@property (nonatomic, copy, nullable) NSNumber *badge;
@property (nonatomic, copy, nullable) NSString *action;
@property (nonatomic, copy, nullable) NSString *categoryIdentifier;
@property (nonatomic, copy, nullable) NSDictionary *userInfo;
@property (nonatomic, copy, nullable) NSString *sound;
@property (nonatomic, copy, nullable) JPushNotificationSound *soundSetting;
@property (nonatomic, copy, nullable) NSArray *attachments;
@property (nonatomic, copy, nullable) NSString *threadIdentifier;
@property (nonatomic, copy, nullable) NSString *launchImageName;
@property (nonatomic, copy, nullable) NSString *summaryArgument;
@property (nonatomic, assign) NSUInteger summaryArgumentCount;
@property (nonatomic, copy, nullable) NSString *targetContentIdentifier;
@property (nonatomic, assign) NSUInteger interruptionLevel;
@property (nonatomic, assign) double relevanceScore;
@property (nonatomic, copy, nullable) NSString *filterCriteria;

@end

@interface JPushNotificationTrigger : NSObject <NSCopying, NSCoding>

@property (nonatomic, assign) BOOL repeat;
@property (nonatomic, copy, nullable) NSDate *fireDate;
@property (nonatomic, copy, nullable) CLRegion *region;
@property (nonatomic, copy, nullable) NSDateComponents *dateComponents;
@property (nonatomic, assign) NSTimeInterval timeInterval;

@end

@interface JPushNotificationRequest : NSObject <NSCopying, NSCoding>

@property (nonatomic, copy, nullable) NSString *requestIdentifier;
@property (nonatomic, copy, nullable) JPushNotificationContent *content;
@property (nonatomic, copy, nullable) JPushNotificationTrigger *trigger;
@property (nonatomic, copy, nullable) void (^completionHandler)(id _Nullable result);

@end

@interface JPushInAppMessage : NSObject

@property (nonatomic, copy, nullable) NSString *mesageId;
@property (nonatomic, copy, nullable) NSString *title;
@property (nonatomic, copy, nullable) NSString *content;
@property (nonatomic, copy, nullable) NSArray *target;
@property (nonatomic, copy, nullable) NSString *clickAction;
@property (nonatomic, copy, nullable) NSDictionary *extras;

@end

@interface JPushCollectControl : NSObject

@property (nonatomic, assign) BOOL gps;
@property (nonatomic, assign) BOOL bssid;
@property (nonatomic, assign) BOOL ssid;
@property (nonatomic, assign) BOOL cell;

@end

@protocol JPUSHRegisterDelegate <NSObject>
@optional
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center
        willPresentNotification:(UNNotification *)notification
          withCompletionHandler:(void (^)(NSInteger options))completionHandler;
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center
 didReceiveNotificationResponse:(UNNotificationResponse *)response
          withCompletionHandler:(void (^)(void))completionHandler;
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center
    openSettingsForNotification:(nullable UNNotification *)notification;
- (void)jpushNotificationAuthorization:(JPAuthorizationStatus)status
                              withInfo:(nullable NSDictionary *)info;
@end

@protocol JPUSHGeofenceDelegate <NSObject>
@optional
- (void)jpushGeofenceIdentifer:(NSString *)geofenceId
         didEnterRegionWithInfo:(nullable NSDictionary *)userInfo
                          error:(nullable NSError *)error;
- (void)jpushGeofenceIdentifer:(NSString *)geofenceId
          didExitRegionWithInfo:(nullable NSDictionary *)userInfo
                          error:(nullable NSError *)error;
- (void)jpushGeofenceIdentifer:(NSString *)geofenceId
                didEnterRegion:(nullable NSDictionary *)userInfo
                         error:(nullable NSError *)error;
- (void)jpushGeofenceIdentifer:(NSString *)geofenceId
                 didExitRegion:(nullable NSDictionary *)userInfo
                         error:(nullable NSError *)error;
- (void)jpushCallbackGeofenceReceived:(nullable NSArray<NSDictionary *> *)geofenceList;
- (void)jpushGeofenceRegion:(nullable id)region error:(nullable NSError *)error;
@end

@protocol JPUSHNotiInMessageDelegate <NSObject>
@optional
- (void)jPushNotiInMessageDidShowWithContent:(NSDictionary *)content;
- (void)jPushNotiInMessageDidClickWithContent:(NSDictionary *)content;
@end

@protocol JPUSHInAppMessageDelegate <NSObject>
@optional
- (void)jPushInAppMessageDidShow:(JPushInAppMessage *)inAppMessage;
- (void)jPushInAppMessageDidClick:(JPushInAppMessage *)inAppMessage;
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

+ (void)registerForRemoteNotificationConfig:(JPUSHRegisterEntity *)config
                                   delegate:(nullable id<JPUSHRegisterDelegate>)delegate;

+ (void)registerDeviceToken:(nullable NSData *)deviceToken;
+ (void)registerVoipToken:(nullable NSData *)voipToken;
+ (void)registerLiveActivity:(NSString *)activityAttributes
            pushToStartToken:(nullable NSData *)pushToStartToken
                  completion:(nullable JPUSHLiveActivityTokenCompletion)completion
                         seq:(NSInteger)seq;
+ (void)registerLiveActivity:(NSString *)liveActivityType
                   pushToken:(nullable NSData *)pushToken
                  completion:(nullable JPUSHLiveActivityTokenCompletion)completion
                         seq:(NSInteger)seq;
+ (void)registerLiveActivityId:(NSString *)liveActivityId
                     pushToken:(nullable NSData *)pushToken
                    completion:(nullable JPUSHLiveActivityTokenCompletion)completion
                           seq:(NSInteger)seq;

+ (void)handleRemoteNotification:(nullable NSDictionary *)remoteInfo;
+ (void)handleVoipNotification:(nullable NSDictionary *)remoteInfo;

+ (void)setNotiInMessageDelegate:(nullable id<JPUSHNotiInMessageDelegate>)notiInMessageDelegate;
+ (void)setInAppMessageDelegate:(nullable id<JPUSHInAppMessageDelegate>)inAppMessageDelegate;
+ (void)registerLbsGeofenceDelegate:(nullable id<JPUSHGeofenceDelegate>)delegate
                  withLaunchOptions:(nullable NSDictionary *)launchOptions;

+ (void)setGeofenceMaxCount:(NSInteger)count;
+ (void)setGeofeneceMaxCount:(NSInteger)count;
+ (void)setGeofenecePeriodForInside:(NSInteger)seconds;
+ (void)removeGeofenceWithIdentifier:(NSString *)geofenceId;
+ (void)setLocationEanable:(BOOL)isEanble;
+ (void)setLatitude:(double)latitude longitude:(double)longitude;
+ (void)setLocation:(nullable id)location;

+ (void)pullInAppMessageWithPage:(NSString *)pageName
                      completion:(nullable void (^)(NSInteger iResCode))completion;

+ (void)addNotification:(JPushNotificationRequest *)request;
+ (void)removeNotification:(nullable JPushNotificationIdentifier *)identifier;
+ (void)findNotification:(JPushNotificationIdentifier *)identifier;

+ (nullable UILocalNotification *)setLocalNotification:(NSDate *)fireDate
                                             alertBody:(nullable NSString *)alertBody
                                                 badge:(int)badge
                                           alertAction:(nullable NSString *)alertAction
                                         identifierKey:(nullable NSString *)notificationKey
                                              userInfo:(nullable NSDictionary *)userInfo
                                             soundName:(nullable NSString *)soundName;

+ (nullable UILocalNotification *)setLocalNotification:(NSDate *)fireDate
                                             alertBody:(nullable NSString *)alertBody
                                                 badge:(int)badge
                                           alertAction:(nullable NSString *)alertAction
                                         identifierKey:(nullable NSString *)notificationKey
                                              userInfo:(nullable NSDictionary *)userInfo
                                             soundName:(nullable NSString *)soundName
                                                region:(nullable CLRegion *)region
                                    regionTriggersOnce:(BOOL)regionTriggersOnce
                                              category:(nullable NSString *)category;

+ (void)showLocalNotificationAtFront:(UILocalNotification *)notification
                       identifierKey:(nullable NSString *)notificationKey;
+ (void)deleteLocalNotificationWithIdentifierKey:(NSString *)notificationKey;
+ (void)deleteLocalNotification:(UILocalNotification *)localNotification;
+ (nullable NSArray *)findLocalNotificationWithIdentifier:(NSString *)notificationKey;
+ (void)clearAllLocalNotifications;

+ (BOOL)setBadge:(NSInteger)value;
+ (void)resetBadge;

+ (nullable NSString *)registrationID;
+ (void)registrationIDCompletionHandler:(void (^)(int resCode, NSString *_Nullable registrationID))completionHandler;

+ (void)setDebugMode;
+ (void)setLogOFF;
+ (void)crashLogON;

+ (void)setCollectControl:(JPushCollectControl *)control;
+ (void)setUserid:(nullable NSString *)userId;
+ (void)setTimedaley:(NSInteger)time;

+ (void)setTags:(nullable NSSet<NSString *> *)tags
     completion:(nullable JPUSHTagsOperationCompletion)completion
            seq:(NSInteger)seq;
+ (void)addTags:(nullable NSSet<NSString *> *)tags
     completion:(nullable JPUSHTagsOperationCompletion)completion
            seq:(NSInteger)seq;
+ (void)deleteTags:(nullable NSSet<NSString *> *)tags
        completion:(nullable JPUSHTagsOperationCompletion)completion
               seq:(NSInteger)seq;
+ (void)cleanTags:(nullable JPUSHTagsOperationCompletion)completion
              seq:(NSInteger)seq;
+ (void)getAllTags:(nullable JPUSHTagsOperationCompletion)completion
               seq:(NSInteger)seq;
+ (void)validTag:(NSString *)tag
      completion:(nullable JPUSHTagValidOperationCompletion)completion
             seq:(NSInteger)seq;
+ (nullable NSSet *)filterValidTags:(nullable NSSet *)tags;

+ (void)setAlias:(nullable NSString *)alias
      completion:(nullable JPUSHAliasOperationCompletion)completion
             seq:(NSInteger)seq;
+ (void)deleteAlias:(nullable JPUSHAliasOperationCompletion)completion
                seq:(NSInteger)seq;
+ (void)getAlias:(nullable JPUSHAliasOperationCompletion)completion
             seq:(NSInteger)seq;

+ (void)setProperties:(nullable NSDictionary *)properties
           completion:(nullable JPUSHPropertiesOperationCompletion)completion
                  seq:(NSInteger)seq;
+ (void)deleteProperties:(nullable NSSet<NSString *> *)keys
              completion:(nullable JPUSHPropertiesOperationCompletion)completion
                     seq:(NSInteger)seq;
+ (void)cleanProperties:(nullable JPUSHPropertiesOperationCompletion)completion
                    seq:(NSInteger)seq;

+ (void)setMobileNumber:(NSString *)mobileNumber
             completion:(nullable void (^)(NSError *_Nullable error))completion;

+ (void)startLogPageView:(NSString *)pageName;
+ (void)stopLogPageView:(NSString *)pageName;
+ (void)beginLogPageView:(NSString *)pageName duration:(int)seconds;
+ (void)pageEnterTo:(NSString *)pageName;
+ (void)pageLeave:(NSString *)pageName;

+ (void)requestNotificationAuthorization:(nullable void (^)(JPAuthorizationStatus status))completion;
+ (void)openSettingsForNotification:(nullable void (^)(BOOL success))completionHandler;

+ (void)setHeartBeatTimeInterval:(NSInteger)interval;
+ (void)setPushEnable:(BOOL)isEnable completion:(nullable void (^)(NSInteger iResCode))completion;
+ (void)setBackgroundEnable:(BOOL)isEnable;
+ (void)setSmartPushEnable:(BOOL)isEnable;
+ (void)setDataInsightsEnable:(BOOL)isEnable;

+ (void)turnOnPush;
+ (void)turnONPush;
+ (void)turnOFFPush;

+ (void)setTags:(nullable NSSet *)tags
          alias:(nullable NSString *)alias
callbackSelector:(nullable SEL)cbSelector
         target:(nullable id)theTarget;
+ (void)setTags:(nullable NSSet *)tags
          alias:(nullable NSString *)alias
callbackSelector:(nullable SEL)cbSelector
         object:(nullable id)theTarget;
+ (void)setTags:(nullable NSSet *)tags
callbackSelector:(nullable SEL)cbSelector
         object:(nullable id)theTarget;
+ (void)setTags:(nullable NSSet *)tags
          alias:(nullable NSString *)alias
fetchCompletionHandle:(nullable void (^)(int iResCode, NSSet *_Nullable iTags, NSString *_Nullable iAlias))completionHandler;
+ (void)setTags:(nullable NSSet *)tags
aliasInbackground:(nullable NSString *)alias;
+ (void)setAlias:(nullable NSString *)alias
callbackSelector:(nullable SEL)cbSelector
          object:(nullable id)theTarget;

@end

NS_ASSUME_NONNULL_END
