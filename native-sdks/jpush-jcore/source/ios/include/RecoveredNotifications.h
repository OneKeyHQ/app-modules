#pragma once
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@class UILocalNotification, CLRegion, JPushNotificationSound, JPushNotificationContent, JPushNotificationTrigger;
typedef void (^RecoveryFindCompletion)(NSArray * _Nullable results);
typedef void (^RecoveryNotificationCompletion)(id result);

@interface JPUSHRegisterEntity : NSObject {
    NSInteger _types;
    NSSet * _categories;
}
@property (nonatomic) NSInteger types;
@property (nonatomic, nullable, strong) NSSet * categories;
@end

@interface JPushCollectControl : NSObject {
    BOOL _ssid;
    BOOL _bssid;
    BOOL _cell;
    BOOL _gps;
    BOOL _appList;
}
@property (nonatomic) BOOL appList;
@property (nonatomic) BOOL ssid;
@property (nonatomic) BOOL bssid;
@property (nonatomic) BOOL cell;
@property (nonatomic) BOOL gps;
@end

@interface JPushInAppMessage : NSObject {
    NSString * _mesageId;
    NSString * _title;
    NSString * _content;
    NSArray * _target;
    NSString * _clickAction;
    NSDictionary * _extras;
}
@property (nonatomic, nullable, copy) NSString * mesageId;
@property (nonatomic, nullable, copy) NSString * title;
@property (nonatomic, nullable, copy) NSString * content;
@property (nonatomic, nullable, strong) NSArray * target;
@property (nonatomic, nullable, copy) NSString * clickAction;
@property (nonatomic, nullable, strong) NSDictionary * extras;
@end

@interface JPushNotificationContent : NSObject <NSCopying, NSCoding> {
    NSString * _title;
    NSString * _subtitle;
    NSString * _body;
    NSNumber * _badge;
    NSString * _action;
    NSString * _categoryIdentifier;
    NSDictionary * _userInfo;
    NSString * _sound;
    JPushNotificationSound * _soundSetting;
    NSArray * _attachments;
    NSString * _threadIdentifier;
    NSString * _launchImageName;
    NSString * _summaryArgument;
    NSUInteger _summaryArgumentCount;
    NSString * _targetContentIdentifier;
    NSUInteger _interruptionLevel;
    double _relevanceScore;
    NSString * _filterCriteria;
}
@property (nonatomic, nullable, copy) NSString * title;
@property (nonatomic, nullable, copy) NSString * subtitle;
@property (nonatomic, nullable, copy) NSString * body;
@property (nonatomic, nullable, copy) NSNumber * badge;
@property (nonatomic, nullable, copy) NSString * action;
@property (nonatomic, nullable, copy) NSString * categoryIdentifier;
@property (nonatomic, nullable, copy) NSDictionary * userInfo;
@property (nonatomic, nullable, copy) NSString * sound;
@property (nonatomic, nullable, copy) JPushNotificationSound * soundSetting;
@property (nonatomic, nullable, copy) NSArray * attachments;
@property (nonatomic, nullable, copy) NSString * threadIdentifier;
@property (nonatomic, nullable, copy) NSString * launchImageName;
@property (nonatomic, nullable, copy) NSString * summaryArgument;
@property (nonatomic) NSUInteger summaryArgumentCount;
@property (nonatomic, nullable, copy) NSString * targetContentIdentifier;
@property (nonatomic) NSUInteger interruptionLevel;
@property (nonatomic) double relevanceScore;
@property (nonatomic, nullable, copy) NSString * filterCriteria;
@end

@interface JPushNotificationIdentifier : NSObject <NSCopying, NSCoding> {
    BOOL _delivered;
    NSArray * _identifiers;
    UILocalNotification * _notificationObj;
    RecoveryFindCompletion _findCompletionHandler;
}
@property (nonatomic, nullable, copy) NSArray * identifiers;
@property (nonatomic, nullable, copy) UILocalNotification * notificationObj;
@property (nonatomic) BOOL delivered;
@property (nonatomic, nullable, copy) RecoveryFindCompletion findCompletionHandler;
@end

@interface JPushNotificationRequest : NSObject <NSCopying, NSCoding> {
    NSString * _requestIdentifier;
    JPushNotificationContent * _content;
    JPushNotificationTrigger * _trigger;
    RecoveryNotificationCompletion _completionHandler;
}
@property (nonatomic, nullable, copy) NSString * requestIdentifier;
@property (nonatomic, nullable, copy) JPushNotificationContent * content;
@property (nonatomic, nullable, copy) JPushNotificationTrigger * trigger;
@property (nonatomic, nullable, copy) RecoveryNotificationCompletion completionHandler;
@end

@interface JPushNotificationSound : NSObject <NSCopying, NSCoding> {
    float _criticalSoundVolume;
    NSString * _soundName;
    NSString * _criticalSoundName;
}
@property (nonatomic, nullable, copy) NSString * soundName;
@property (nonatomic, nullable, copy) NSString * criticalSoundName;
@property (nonatomic) float criticalSoundVolume;
@end

@interface JPushNotificationTrigger : NSObject <NSCopying, NSCoding> {
    BOOL _repeat;
    NSDate * _fireDate;
    CLRegion * _region;
    NSDateComponents * _dateComponents;
    double _timeInterval;
}
@property (nonatomic) BOOL repeat;
@property (nonatomic, nullable, copy) NSDate * fireDate;
@property (nonatomic, nullable, copy) CLRegion * region;
@property (nonatomic, nullable, copy) NSDateComponents * dateComponents;
@property (nonatomic) double timeInterval;
@end

NS_ASSUME_NONNULL_END