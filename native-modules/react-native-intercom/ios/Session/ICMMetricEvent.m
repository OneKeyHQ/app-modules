#import "ICMMetricEvent.h"
#import "ICMDataManagerRequestDependencies.h"
#import "ICMIdentityStore.h"
#import "../Identity/ICMAppEnvironmentChecker.h"
#import "../Identity/ICMDeviceData.h"
#import "ICMUtils+Integration.h"
#import <math.h>
@interface ICMConversationPart (OriginalMetricState)
- (BOOL)isInitialMessage;
@end
@implementation ICMMetricEvent
+ (instancetype)eventWithAction:(NSString *)action object:(NSString *)object place:(NSString *)place context:(NSString *)context {
    return [self eventWithAction:action object:object place:place context:context additionalInfo:@{}];
}
+ (instancetype)eventWithAction:(NSString *)action object:(NSString *)object place:(NSString *)place context:(NSString *)context additionalInfo:(NSDictionary *)info {
    if (info && ![info isKindOfClass:[NSDictionary class]]) return nil;
    NSMutableDictionary *metadata = [NSMutableDictionary new];
    ICMConversation *conversation = info[@"conversation"];
    if ([conversation isKindOfClass:[ICMConversation class]] && conversation.conversationId) metadata[@"conversation_id"] = conversation.conversationId;
    ICMConversationPart *part = info[@"conversation_part"];
    if ([part isKindOfClass:[ICMConversationPart class]] && part.partId) {
        BOOL initial = [part isInitialMessage];
        metadata[initial ? @"message_id" : @"comment_id"] = part.partId;
    }
    if ([info[@"conversation_id"] isKindOfClass:[NSString class]]) metadata[@"conversation_id"] = info[@"conversation_id"];
    if ([info[@"closed_conversation_id"] isKindOfClass:[NSString class]]) metadata[@"closed_conversation_id"] = info[@"closed_conversation_id"];
    if ([info[@"instance_id"] isKindOfClass:[NSString class]]) metadata[@"instance_id"] = info[@"instance_id"];
    if ([info[@"message_text"] isKindOfClass:[NSString class]]) metadata[@"message_text"] = info[@"message_text"];
    if ([info[@"is_attachment"] isKindOfClass:[NSNumber class]]) metadata[@"is_attachment"] = @([info[@"is_attachment"] boolValue]);
    if ([info[@"is_sticker"] isKindOfClass:[NSNumber class]]) metadata[@"is_sticker"] = @([info[@"is_sticker"] boolValue]);
    if ([info[@"is_annotated"] isKindOfClass:[NSNumber class]]) metadata[@"is_annotated"] = @([info[@"is_annotated"] boolValue]);
    if ([info[@"has_article_card"] isKindOfClass:[NSNumber class]]) metadata[@"has_article_card"] = @([info[@"has_article_card"] boolValue]);
    if ([info[@"state"] isKindOfClass:[NSString class]]) metadata[@"state"] = info[@"state"];
    if ([info[@"badge_value"] isKindOfClass:[NSString class]]) metadata[@"badge_value"] = info[@"badge_value"];
    if ([info[@"push_type"] isKindOfClass:[NSString class]]) metadata[@"push_type"] = info[@"push_type"];
    if ([info[@"article_id"] isKindOfClass:[NSString class]]) metadata[@"article_id"] = info[@"article_id"];
    if ([info[@"is_gif"] isKindOfClass:[NSNumber class]]) metadata[@"is_gif"] = info[@"is_gif"];
    if ([info[@"search_query"] isKindOfClass:[NSString class]]) metadata[@"search_query"] = info[@"search_query"];
    if ([info[@"url"] isKindOfClass:[NSString class]]) metadata[@"url"] = info[@"url"];
    if ([info[@"teammate_status"] isKindOfClass:[NSString class]]) metadata[@"teammate_status"] = info[@"teammate_status"];
    if ([info[@"time_since_last_active"] isKindOfClass:[NSString class]]) metadata[@"time_since_last_active"] = info[@"time_since_last_active"];
    if ([info[@"quick_reply_id"] isKindOfClass:[NSString class]]) metadata[@"quick_reply_id"] = info[@"quick_reply_id"];
    if ([info[@"screens_in_message"] isKindOfClass:[NSNumber class]]) metadata[@"screens_in_message"] = info[@"screens_in_message"];
    if ([info[@"screens_shown"] isKindOfClass:[NSNumber class]]) metadata[@"screens_shown"] = info[@"screens_shown"];
    if ([info[@"screens_seen"] isKindOfClass:[NSNumber class]]) metadata[@"screens_seen"] = info[@"screens_seen"];
    if ([info[@"permission_granted"] isKindOfClass:[NSNumber class]]) metadata[@"permission_granted"] = @([info[@"permission_granted"] boolValue]);
    if ([info[@"requested_instance_id"] isKindOfClass:[NSString class]]) metadata[@"requested_instance_id"] = info[@"requested_instance_id"];
    if ([info[@"instance_id"] isKindOfClass:[NSString class]]) metadata[@"instance_id"] = info[@"instance_id"];
    if ([info[@"requested_content_id"] isKindOfClass:[NSString class]]) metadata[@"requested_content_id"] = info[@"requested_content_id"];
    if ([info[@"source"] isKindOfClass:[NSString class]]) metadata[@"source"] = info[@"source"];
    if ([info[@"error_code"] isKindOfClass:[NSNumber class]]) metadata[@"error_code"] = info[@"error_code"];
    if ([info[@"articles_shown"] isKindOfClass:[NSArray class]]) metadata[@"articles_shown"] = info[@"articles_shown"];
    if ([info[@"articles_count"] isKindOfClass:[NSNumber class]]) metadata[@"articles_count"] = info[@"articles_count"];
    if ([info[@"article_id"] isKindOfClass:[NSString class]]) metadata[@"article_id"] = info[@"article_id"];
    if ([info[@"conversation_shown"] isKindOfClass:[NSNumber class]]) metadata[@"conversation_shown"] = info[@"conversation_shown"];
    if ([info[@"filtered"] isKindOfClass:[NSNumber class]]) metadata[@"filtered"] = info[@"filtered"];
    if ([info[@"home_screen_index"] isKindOfClass:[NSNumber class]]) metadata[@"home_screen_index"] = info[@"home_screen_index"];
    if ([info[@"survey_id"] isKindOfClass:[NSString class]]) metadata[@"survey_id"] = info[@"survey_id"];
    if ([info[@"steps_complete"] isKindOfClass:[NSNumber class]]) metadata[@"steps_complete"] = info[@"steps_complete"];
    if ([info[@"questions_seen"] isKindOfClass:[NSNumber class]]) metadata[@"questions_seen"] = info[@"questions_seen"];
    if ([info[@"format"] isKindOfClass:[NSString class]]) metadata[@"format"] = info[@"format"];
    if ([info[@"max_steps"] isKindOfClass:[NSNumber class]]) metadata[@"max_steps"] = info[@"max_steps"];
    if ([info[@"ticket_type_id"] isKindOfClass:[NSNumber class]]) metadata[@"ticket_type_id"] = info[@"ticket_type_id"];
    if ([info[@"ticket_status"] isKindOfClass:[NSString class]]) metadata[@"ticket_status"] = info[@"ticket_status"];
    if ([info[@"content_type"] isKindOfClass:[NSString class]]) metadata[@"content_type"] = info[@"content_type"];
    if ([info[@"space"] isKindOfClass:[NSString class]]) metadata[@"space"] = info[@"space"];
    if ([context isKindOfClass:[NSString class]] && [context isEqualToString:@"from_help_center_webview"]) [metadata addEntriesFromDictionary:info];
    if ([context isKindOfClass:[NSString class]] && [context isEqualToString:@"from_article_on_button"]) [metadata addEntriesFromDictionary:info];
    return [[self alloc] initWithAction:action object:object place:place context:context metaData:metadata];
}
- (instancetype)initWithAction:(NSString *)action object:(NSString *)object place:(NSString *)place context:(NSString *)context metaData:(NSDictionary *)metadata {
    if (![[self class] isNonEmptyString:action] || ![[self class] isNonEmptyString:object] || ![[self class] isNonEmptyString:place] || ![[self class] isNonEmptyString:context] || ![metadata isKindOfClass:[NSDictionary class]] || ![NSJSONSerialization isValidJSONObject:metadata]) return nil;
    self = [super init];
    if (self) {
        NSMutableDictionary *data = [NSMutableDictionary new];
        data[@"action"] = action;
        data[@"object"] = object;
        data[@"place"] = place;
        data[@"context"] = context;
        data[@"ios_environment"] = [ICMAppEnvironmentChecker stringForAppEnvironment:[ICMAppEnvironmentChecker appEnvironment]];
        NSString *intercomId = [ICMIdentityStore currentIdentity].userIdentity.intercomId;
        if (intercomId.length) data[@"user_id"] = intercomId;
        if (metadata.count) [data addEntriesFromDictionary:metadata];
        data[@"sdk_version"] = [ICMUtils intercomVersion];
        data[@"app_version"] = [ICMUtils appVersion];
        data[@"application_identifier"] = [ICMUtils bundleIdentifier];
        data[@"app_name"] = [ICMUtils applicationName];
        data[@"minimum_os_version_number"] = [ICMUtils minimumOSVersion];
        ICMDeviceData *device = [ICMDeviceData new];
        data[@"supports_multiple_scenes"] = @(device.supportsMultipleWindows);
        data[@"user_interface_style"] = device.userInterfaceStyle;
        data[@"device_type"] = device.deviceType;
        data[@"device_model"] = device.platform;
        data[@"os_version"] = device.platformVersion;
        data[@"language_direction"] = device.languageDirection;
        data[@"auto_integrates_push_notifications"] = @(device.autoIntegratesPushNotifications);
        data[@"supports_rich_push"] = @(device.supportsRichPush);
        data[@"reduced_motion_enabled"] = @(device.reducedMotionEnabled);
        data[@"invert_colors_enabled"] = @(device.invertColorsEnabled);
        data[@"voice_over_running"] = @(device.isVoiceOverRunning);
        data[@"content_size_category"] = device.contentSizeCategory;
        _eventType = 0;
        _owner = @"messenger";
        _name = @"m5_metric";
        _uuid = [[[self class] UUID] UUIDString];
        _createdAt = @(floor([[self class] currentTimestamp]));
        _metaData = data;
    }
    return self;
}
- (void)setEventType:(NSUInteger)type {
    _eventType = type;
    switch (type) {
        case 0: self.owner = @"messenger"; self.name = @"m5_metric"; break;
        case 1: self.owner = @"educate"; self.name = @"educate_event"; break;
        case 2: self.owner = @"messenger"; self.name = @"carousel_metric"; break;
        case 3: self.owner = @"educate"; self.name = @"search_browse_event"; break;
        case 4: self.owner = @"messenger"; self.name = @"survey_metric"; break;
        default: break;
    }
}
+ (BOOL)isNonEmptyString:(id)value { return [value isKindOfClass:[NSString class]] && [value length] != 0; }
- (NSDictionary *)eventDictionary {
    if (![[self class] isNonEmptyString:self.uuid] || !self.createdAt || !self.metaData || !self.owner || !self.name) return nil;
    return @{ @"owner": self.owner, @"name": self.name, @"id": self.uuid, @"created_at": self.createdAt, @"metadata": self.metaData };
}
+ (NSTimeInterval)currentTimestamp { return [[NSDate date] timeIntervalSince1970]; }
+ (NSUUID *)UUID { return [NSUUID UUID]; }
+ (BOOL)supportsSecureCoding { return YES; }
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [super init];
    if (self) {
        self.uuid = [coder decodeObjectOfClass:[NSString class] forKey:@"id"];
        self.createdAt = [coder decodeObjectOfClass:[NSNumber class] forKey:@"created_at"];
        self.metaData = [coder decodeObjectOfClasses:[NSSet setWithObjects:[NSDictionary class], [NSArray class], [NSString class], [NSNumber class], nil] forKey:@"metadata"];
        self.owner = [coder decodeObjectOfClass:[NSString class] forKey:@"owner"];
        self.name = [coder decodeObjectOfClass:[NSString class] forKey:@"name"];
    }
    return self;
}
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.uuid forKey:@"id"];
    [coder encodeObject:self.createdAt forKey:@"created_at"];
    [coder encodeObject:self.metaData forKey:@"metadata"];
    [coder encodeObject:self.owner forKey:@"owner"];
    [coder encodeObject:self.name forKey:@"name"];
}
- (BOOL)isEqual:(id)other {
    if (other == self) return YES;
    if (!other || ![[other class] isEqual:[self class]]) return NO;
    return [self isEqualToMetricEvent:other];
}
- (BOOL)isEqualToMetricEvent:(ICMMetricEvent *)other {
    if (!((!self.uuid && !other.uuid) || [self.uuid isEqual:other.uuid])) return NO;
    if (!((!self.createdAt && !other.createdAt) || [self.createdAt isEqual:other.createdAt])) return NO;
    if (!((!self.metaData && !other.metaData) || [self.metaData isEqual:other.metaData])) return NO;
    return self.eventType == other.eventType;
}
- (NSUInteger)hash { return self.uuid.hash ^ self.createdAt.hash ^ self.metaData.hash; }
@end
