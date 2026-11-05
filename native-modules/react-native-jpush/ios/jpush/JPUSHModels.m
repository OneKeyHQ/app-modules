#import "../include/JPUSHService.h"
#import <CoreLocation/CoreLocation.h>
#import <UserNotifications/UserNotifications.h>

@implementation JPUSHRegisterEntity
@end

@implementation JPushNotificationIdentifier

- (id)copyWithZone:(NSZone *)zone {
    JPushNotificationIdentifier *copy = [[[self class] allocWithZone:zone] init];
    copy.identifiers = [self.identifiers copyWithZone:zone];
    copy.notificationObj = [self.notificationObj copyWithZone:zone];
    copy.delivered = self.delivered;
    copy.findCompletionHandler = [self.findCompletionHandler copy];
    return copy;
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        _identifiers = [coder decodeObjectForKey:@"identifiers"];
        _delivered = [coder decodeBoolForKey:@"delivered"];
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.identifiers forKey:@"identifiers"];
    [coder encodeBool:self.delivered forKey:@"delivered"];
}

@end

@implementation JPushNotificationSound

- (instancetype)init {
    self = [super init];
    if (self) {
        _criticalSoundVolume = 1.0f;
    }
    return self;
}

- (id)copyWithZone:(NSZone *)zone {
    JPushNotificationSound *copy = [[[self class] allocWithZone:zone] init];
    copy.soundName = [self.soundName copyWithZone:zone];
    copy.criticalSoundName = [self.criticalSoundName copyWithZone:zone];
    copy.criticalSoundVolume = self.criticalSoundVolume;
    copy.soundSetting = self.soundSetting;
    return copy;
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        _soundName = [coder decodeObjectForKey:@"soundName"];
        _criticalSoundName = [coder decodeObjectForKey:@"criticalSoundName"];
        _criticalSoundVolume = [coder decodeFloatForKey:@"criticalSoundVolume"];
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.soundName forKey:@"soundName"];
    [coder encodeObject:self.criticalSoundName forKey:@"criticalSoundName"];
    [coder encodeFloat:self.criticalSoundVolume forKey:@"criticalSoundVolume"];
}

@end

@implementation JPushNotificationContent

- (instancetype)init {
    self = [super init];
    if (self) {
        _summaryArgumentCount = 1;
        _interruptionLevel = 1;
        _relevanceScore = 0.0;
    }
    return self;
}

- (id)copyWithZone:(NSZone *)zone {
    JPushNotificationContent *copy = [[[self class] allocWithZone:zone] init];
    copy.title = [self.title copyWithZone:zone];
    copy.subtitle = [self.subtitle copyWithZone:zone];
    copy.body = [self.body copyWithZone:zone];
    copy.badge = [self.badge copyWithZone:zone];
    copy.action = [self.action copyWithZone:zone];
    copy.categoryIdentifier = [self.categoryIdentifier copyWithZone:zone];
    copy.userInfo = [self.userInfo copyWithZone:zone];
    copy.sound = [self.sound copyWithZone:zone];
    copy.soundSetting = [self.soundSetting copyWithZone:zone];
    copy.attachments = [self.attachments copyWithZone:zone];
    copy.threadIdentifier = [self.threadIdentifier copyWithZone:zone];
    copy.launchImageName = [self.launchImageName copyWithZone:zone];
    copy.summaryArgument = [self.summaryArgument copyWithZone:zone];
    copy.summaryArgumentCount = self.summaryArgumentCount;
    copy.targetContentIdentifier = [self.targetContentIdentifier copyWithZone:zone];
    copy.interruptionLevel = self.interruptionLevel;
    copy.relevanceScore = self.relevanceScore;
    copy.filterCriteria = [self.filterCriteria copyWithZone:zone];
    return copy;
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        _title = [coder decodeObjectForKey:@"title"];
        _subtitle = [coder decodeObjectForKey:@"subtitle"];
        _body = [coder decodeObjectForKey:@"body"];
        _badge = [coder decodeObjectForKey:@"badge"];
        _action = [coder decodeObjectForKey:@"action"];
        _categoryIdentifier = [coder decodeObjectForKey:@"categoryIdentifier"];
        _userInfo = [coder decodeObjectForKey:@"userInfo"];
        _sound = [coder decodeObjectForKey:@"sound"];
        _threadIdentifier = [coder decodeObjectForKey:@"threadIdentifier"];
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.title forKey:@"title"];
    [coder encodeObject:self.subtitle forKey:@"subtitle"];
    [coder encodeObject:self.body forKey:@"body"];
    [coder encodeObject:self.badge forKey:@"badge"];
    [coder encodeObject:self.action forKey:@"action"];
    [coder encodeObject:self.categoryIdentifier forKey:@"categoryIdentifier"];
    [coder encodeObject:self.userInfo forKey:@"userInfo"];
    [coder encodeObject:self.sound forKey:@"sound"];
    [coder encodeObject:self.threadIdentifier forKey:@"threadIdentifier"];
}

@end

@implementation JPushNotificationTrigger

- (id)copyWithZone:(NSZone *)zone {
    JPushNotificationTrigger *copy = [[[self class] allocWithZone:zone] init];
    copy.repeat = self.repeat;
    copy.fireDate = [self.fireDate copyWithZone:zone];
    copy.region = [self.region copyWithZone:zone];
    copy.dateComponents = [self.dateComponents copyWithZone:zone];
    copy.timeInterval = self.timeInterval;
    return copy;
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        _repeat = [coder decodeBoolForKey:@"repeat"];
        _fireDate = [coder decodeObjectForKey:@"fireDate"];
        _dateComponents = [coder decodeObjectForKey:@"dateComponents"];
        _timeInterval = [coder decodeDoubleForKey:@"timeInterval"];
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeBool:self.repeat forKey:@"repeat"];
    [coder encodeObject:self.fireDate forKey:@"fireDate"];
    [coder encodeObject:self.dateComponents forKey:@"dateComponents"];
    [coder encodeDouble:self.timeInterval forKey:@"timeInterval"];
}

@end

@implementation JPushNotificationRequest

- (id)copyWithZone:(NSZone *)zone {
    JPushNotificationRequest *copy = [[[self class] allocWithZone:zone] init];
    copy.requestIdentifier = [self.requestIdentifier copyWithZone:zone];
    copy.content = [self.content copyWithZone:zone];
    copy.trigger = [self.trigger copyWithZone:zone];
    copy.completionHandler = [self.completionHandler copy];
    return copy;
}

- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        _requestIdentifier = [coder decodeObjectForKey:@"requestIdentifier"];
        _content = [coder decodeObjectForKey:@"content"];
        _trigger = [coder decodeObjectForKey:@"trigger"];
    }
    return self;
}

- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.requestIdentifier forKey:@"requestIdentifier"];
    [coder encodeObject:self.content forKey:@"content"];
    [coder encodeObject:self.trigger forKey:@"trigger"];
}

@end

@implementation JPushInAppMessage
@end

@implementation JPushCollectControl

- (instancetype)init {
    self = [super init];
    if (self) {
        _gps = NO;
        _bssid = NO;
        _ssid = NO;
        _cell = NO;
    }
    return self;
}

@end
