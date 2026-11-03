#import "RecoveredNotifications.h"

@implementation JPUSHRegisterEntity
@synthesize types = _types;
@synthesize categories = _categories;
@end

@implementation JPushCollectControl
@synthesize appList = _appList;
@synthesize ssid = _ssid;
@synthesize bssid = _bssid;
@synthesize cell = _cell;
@synthesize gps = _gps;
- (instancetype)init {
    self = [super init];
    if (self) { _ssid = YES; _bssid = YES; _cell = YES; _gps = YES; _appList = YES; }
    return self;
}
@end

@implementation JPushInAppMessage
@synthesize mesageId = _mesageId;
@synthesize title = _title;
@synthesize content = _content;
@synthesize target = _target;
@synthesize clickAction = _clickAction;
@synthesize extras = _extras;
@end

@implementation JPushNotificationContent
@synthesize title = _title;
@synthesize subtitle = _subtitle;
@synthesize body = _body;
@synthesize badge = _badge;
@synthesize action = _action;
@synthesize categoryIdentifier = _categoryIdentifier;
@synthesize userInfo = _userInfo;
@synthesize sound = _sound;
@synthesize soundSetting = _soundSetting;
@synthesize attachments = _attachments;
@synthesize threadIdentifier = _threadIdentifier;
@synthesize launchImageName = _launchImageName;
@synthesize summaryArgument = _summaryArgument;
@synthesize summaryArgumentCount = _summaryArgumentCount;
@synthesize targetContentIdentifier = _targetContentIdentifier;
@synthesize interruptionLevel = _interruptionLevel;
@synthesize relevanceScore = _relevanceScore;
@synthesize filterCriteria = _filterCriteria;
- (id)copyWithZone:(NSZone *)zone {
    // The original allocates without calling init and reads ivars directly.
    JPushNotificationContent *result = [[self class] allocWithZone:zone];
    result.title = _title;
    result.subtitle = _subtitle;
    result.body = _body;
    result.badge = _badge;
    result.action = _action;
    result.categoryIdentifier = _categoryIdentifier;
    result.userInfo = _userInfo;
    result.sound = _sound;
    result.attachments = _attachments;
    result.threadIdentifier = _threadIdentifier;
    result.launchImageName = _launchImageName;
    result.soundSetting = _soundSetting;
    if (@available(iOS 12.0, *)) { result.summaryArgument = _summaryArgument; }
    if (@available(iOS 12.0, *)) { result.summaryArgumentCount = _summaryArgumentCount; }
    if (@available(iOS 13.0, *)) { result.targetContentIdentifier = _targetContentIdentifier; }
    if (@available(iOS 15.0, *)) { result.interruptionLevel = _interruptionLevel; }
    if (@available(iOS 15.0, *)) { result.relevanceScore = _relevanceScore; }
    if (@available(iOS 16.0, *)) { result.filterCriteria = _filterCriteria; }
    return result;
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (!self) return nil;
    _title = [coder decodeObjectForKey:@"title"];
    _subtitle = [coder decodeObjectForKey:@"subtitle"];
    _body = [coder decodeObjectForKey:@"body"];
    _badge = [coder decodeObjectForKey:@"badge"];
    _action = [coder decodeObjectForKey:@"action"];
    _categoryIdentifier = [coder decodeObjectForKey:@"categoryIdentifier"];
    _userInfo = [coder decodeObjectForKey:@"userInfo"];
    _sound = [coder decodeObjectForKey:@"sound"];
    _soundSetting = [coder decodeObjectForKey:@"soundSetting"];
    _attachments = [coder decodeObjectForKey:@"attachments"];
    _threadIdentifier = [coder decodeObjectForKey:@"threadIdentifier"];
    _launchImageName = [coder decodeObjectForKey:@"launchImageName"];
    if (@available(iOS 12.0, *)) { _summaryArgument = [coder decodeObjectForKey:@"summaryArgument"]; }
    if (@available(iOS 12.0, *)) { _summaryArgumentCount = [coder decodeIntegerForKey:@"summaryArgumentCount"]; }
    if (@available(iOS 13.0, *)) { _targetContentIdentifier = [coder decodeObjectForKey:@"targetContentIdentifier"]; }
    if (@available(iOS 15.0, *)) { _interruptionLevel = [coder decodeIntegerForKey:@"interruptionLevel"]; }
    if (@available(iOS 15.0, *)) { _relevanceScore = [coder decodeIntegerForKey:@"relevanceScore"]; }
    if (@available(iOS 16.0, *)) { _filterCriteria = [coder decodeObjectForKey:@"filterCriteria"]; }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:_title forKey:@"title"];
    [coder encodeObject:_subtitle forKey:@"subtitle"];
    [coder encodeObject:_body forKey:@"body"];
    [coder encodeObject:_badge forKey:@"badge"];
    [coder encodeObject:_action forKey:@"action"];
    [coder encodeObject:_categoryIdentifier forKey:@"categoryIdentifier"];
    [coder encodeObject:_userInfo forKey:@"userInfo"];
    [coder encodeObject:_sound forKey:@"sound"];
    [coder encodeObject:_soundSetting forKey:@"soundSetting"];
    [coder encodeObject:_attachments forKey:@"attachments"];
    [coder encodeObject:_threadIdentifier forKey:@"threadIdentifier"];
    [coder encodeObject:_launchImageName forKey:@"launchImageName"];
    if (@available(iOS 12.0, *)) { [coder encodeObject:_summaryArgument forKey:@"summaryArgument"]; }
    if (@available(iOS 12.0, *)) { [coder encodeInteger:_summaryArgumentCount forKey:@"summaryArgumentCount"]; }
    if (@available(iOS 13.0, *)) { [coder encodeObject:_targetContentIdentifier forKey:@"targetContentIdentifier"]; }
    // Original archive format truncates this double to NSInteger.
    if (@available(iOS 15.0, *)) { [coder encodeInteger:(NSInteger)_relevanceScore forKey:@"relevanceScore"]; }
    if (@available(iOS 15.0, *)) { [coder encodeInteger:_interruptionLevel forKey:@"interruptionLevel"]; }
    if (@available(iOS 16.0, *)) { [coder encodeObject:_filterCriteria forKey:@"filterCriteria"]; }
}
@end

@implementation JPushNotificationIdentifier
@synthesize identifiers = _identifiers;
@synthesize notificationObj = _notificationObj;
@synthesize delivered = _delivered;
@synthesize findCompletionHandler = _findCompletionHandler;
- (id)copyWithZone:(NSZone *)zone {
    // The original allocates without calling init and reads ivars directly.
    JPushNotificationIdentifier *result = [[self class] allocWithZone:zone];
    result.identifiers = _identifiers;
    result.notificationObj = _notificationObj;
    result.delivered = _delivered;
    result.findCompletionHandler = _findCompletionHandler;
    return result;
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (!self) return nil;
    _identifiers = [coder decodeObjectForKey:@"identifiers"];
    _notificationObj = [coder decodeObjectForKey:@"notificationObj"];
    _delivered = [coder decodeBoolForKey:@"delivered"];
    *(id __strong *)&_findCompletionHandler = [coder decodeObjectForKey:@"findCompletionHandler"];
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:_identifiers forKey:@"identifiers"];
    [coder encodeObject:_notificationObj forKey:@"notificationObj"];
    [coder encodeBool:_delivered forKey:@"delivered"];
    [coder encodeObject:_findCompletionHandler forKey:@"findCompletionHandler"];
}
@end

@implementation JPushNotificationRequest
@synthesize requestIdentifier = _requestIdentifier;
@synthesize content = _content;
@synthesize trigger = _trigger;
@synthesize completionHandler = _completionHandler;
- (id)copyWithZone:(NSZone *)zone {
    // The original allocates without calling init and reads ivars directly.
    JPushNotificationRequest *result = [[self class] allocWithZone:zone];
    result.requestIdentifier = _requestIdentifier;
    result.content = _content;
    result.trigger = _trigger;
    result.completionHandler = _completionHandler;
    return result;
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (!self) return nil;
    _requestIdentifier = [coder decodeObjectForKey:@"requestIdentifier"];
    _content = [coder decodeObjectForKey:@"content"];
    _trigger = [coder decodeObjectForKey:@"trigger"];
    *(id __strong *)&_completionHandler = [coder decodeObjectForKey:@"completionHandler"];
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:_requestIdentifier forKey:@"requestIdentifier"];
    [coder encodeObject:_content forKey:@"content"];
    [coder encodeObject:_trigger forKey:@"trigger"];
    [coder encodeObject:_completionHandler forKey:@"completionHandler"];
}
@end

@implementation JPushNotificationSound
@synthesize soundName = _soundName;
@synthesize criticalSoundName = _criticalSoundName;
@synthesize criticalSoundVolume = _criticalSoundVolume;
- (instancetype)init {
    self = [super init];
    if (self) { if (@available(iOS 12.0, *)) { self.criticalSoundVolume = 1.0f; } }
    return self;
}
- (id)copyWithZone:(NSZone *)zone {
    // The original allocates without calling init and reads ivars directly.
    JPushNotificationSound *result = [[self class] allocWithZone:zone];
    result.soundName = _soundName;
    if (@available(iOS 12.0, *)) { result.criticalSoundName = _criticalSoundName; }
    if (@available(iOS 12.0, *)) { result.criticalSoundVolume = _criticalSoundVolume; }
    return result;
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (!self) return nil;
    _soundName = [coder decodeObjectForKey:@"soundName"];
    if (@available(iOS 12.0, *)) { _criticalSoundName = [coder decodeObjectForKey:@"criticalSoundName"]; }
    if (@available(iOS 12.0, *)) { _criticalSoundVolume = [coder decodeBoolForKey:@"criticalSoundVolume"]; }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:_soundName forKey:@"soundName"];
    if (@available(iOS 12.0, *)) { [coder encodeObject:_criticalSoundName forKey:@"criticalSoundName"]; }
    // Original archive format intentionally stores this float as BOOL.
    if (@available(iOS 12.0, *)) { [coder encodeBool:(_criticalSoundVolume != 0.0f) forKey:@"criticalSoundVolume"]; }
}
@end

@implementation JPushNotificationTrigger
@synthesize repeat = _repeat;
@synthesize fireDate = _fireDate;
@synthesize region = _region;
@synthesize dateComponents = _dateComponents;
@synthesize timeInterval = _timeInterval;
- (id)copyWithZone:(NSZone *)zone {
    // The original allocates without calling init and reads ivars directly.
    JPushNotificationTrigger *result = [[self class] allocWithZone:zone];
    result.repeat = _repeat;
    result.dateComponents = _dateComponents;
    result.fireDate = _fireDate;
    result.timeInterval = _timeInterval;
    result.region = _region;
    return result;
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (!self) return nil;
    _repeat = [coder decodeBoolForKey:@"repeat"];
    _dateComponents = [coder decodeObjectForKey:@"dateComponents"];
    _fireDate = [coder decodeObjectForKey:@"fireDate"];
    _timeInterval = [coder decodeDoubleForKey:@"timeInterval"];
    _region = [coder decodeObjectForKey:@"region"];
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeBool:_repeat forKey:@"repeat"];
    [coder encodeObject:_dateComponents forKey:@"dateComponents"];
    [coder encodeObject:_fireDate forKey:@"fireDate"];
    [coder encodeDouble:_timeInterval forKey:@"timeInterval"];
    [coder encodeObject:_region forKey:@"region"];
}
@end
