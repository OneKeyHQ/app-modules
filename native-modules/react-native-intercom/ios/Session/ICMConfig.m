// PARTIAL_CLASS_UNLINKED: nested Swift models and original image/UI methods missing.
#import "ICMConfig.h"
#import "../Identity/ICMUtils.h"
#import "../Identity/ICMPersistenceProxy.h"
static ICMConfig *sharedConfig;
// Four protocol requirements are implemented in ICMConfig+ImagesAndUI.m.
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wprotocol"
@implementation ICMConfig
@synthesize isPersistedToDisk = _isPersistedToDisk;
@synthesize baseColorString = _baseColorString;
@synthesize appName = _appName;
@synthesize teamIntro = _teamIntro;
@synthesize teamGreeting = _teamGreeting;
@synthesize launcherAlignment = _launcherAlignment;
@synthesize launcherBottomPadding = _launcherBottomPadding;
@synthesize launcherCustomLogoURLString = _launcherCustomLogoURLString;
@synthesize inboundMessages = _inboundMessages;
@synthesize audioEnabled = _audioEnabled;
@synthesize metricsEnabled = _metricsEnabled;
@synthesize backgroundRequestsEnabled = _backgroundRequestsEnabled;
@synthesize identityVerificationEnabled = _identityVerificationEnabled;
@synthesize accessToTeammateEnabled = _accessToTeammateEnabled;
@synthesize helpCenterRequireSearch = _helpCenterRequireSearch;
@synthesize preventMultipleInboundConversations = _preventMultipleInboundConversations;
@synthesize hasOpenInboundConversations = _hasOpenInboundConversations;
@synthesize finEnabled = _finEnabled;
@synthesize articleAutoReactionEnabled = _articleAutoReactionEnabled;
@synthesize crashReportingConfig = _crashReportingConfig;
@synthesize userUpdateDupCacheMaxAge = _userUpdateDupCacheMaxAge;
@synthesize newSessionThreshold = _newSessionThreshold;
@synthesize batchUserUpdatePeriod = _batchUserUpdatePeriod;
@synthesize softResetTimeout = _softResetTimeout;
@synthesize pingDelay = _pingDelay;
@synthesize rateLimitPeriod = _rateLimitPeriod;
@synthesize rateLimit = _rateLimit;
@synthesize helpCenterUrlString = _helpCenterUrlString;
@synthesize helpCenterUrlStrings = _helpCenterUrlStrings;
@synthesize intercomLocale = _intercomLocale;
@synthesize helpCenterLocale = _helpCenterLocale;
@synthesize features = _features;
@synthesize modules = _modules;
@synthesize attachmentSettings = _attachmentSettings;
@synthesize finThinkingGIFBrandedURLString = _finThinkingGIFBrandedURLString;
@synthesize finThinkingGIFUnBrandedURLString = _finThinkingGIFUnBrandedURLString;
@synthesize conversationStateSyncSettings = _conversationStateSyncSettings;
+ (BOOL)supportsSecureCoding { return YES; }
- (instancetype)init {
    if ((self = [super init])) {
        _inboundMessages = NO;
        _audioEnabled = _metricsEnabled = _backgroundRequestsEnabled = YES;
        _crashReportingConfig = nil;
        _userUpdateDupCacheMaxAge = 300;
        _rateLimit = 100; _rateLimitPeriod = 60;
        _newSessionThreshold = 20;
        _batchUserUpdatePeriod = _pingDelay = _softResetTimeout = 1;
        _launcherAlignment = [@"right" copy];
        _attachmentSettings = [ICMRecoveredAttachmentSettings new];
        _conversationStateSyncSettings = [ICMRecoveredConversationStateSyncSettings new];
        _isPersistedToDisk = NO;
        _features = [NSSet new];
    }
    return self;
}
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary {
    ICMConfig *config = [[ICMConfig alloc] init];
    [config setAttributesFromDictionary:dictionary];
    return config;
}
+ (void)updateSharedInstanceFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:NSDictionary.class] && dictionary.count) [[ICMConfig instanceFromDictionary:dictionary] saveIfNeeded];
}
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary {
    if ([dictionary isKindOfClass:NSDictionary.class]) [self setValuesForKeysWithDictionary:dictionary];
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([value isKindOfClass:NSNull.class]) return;
    if ([key isEqualToString:@"features"]) {
        if ([value isKindOfClass:NSArray.class]) self.features = [NSSet setWithArray:value];
    } else if ([key isEqualToString:@"modules"]) {
        if ([value isKindOfClass:NSDictionary.class]) {
            self.modules = [[ICMRecoveredConfigModules alloc] initWithDictionary:value];
            [self prefetchHomeHeaderImagesIfNeeded];
        }
    } else [super setValue:value forKey:key];
}
- (void)setValue:(id)value forUndefinedKey:(NSString *)key {
    if ([value isKindOfClass:NSNull.class]) return;
    if ([key isEqualToString:@"base_color"]) {
        [self setValue:value forKey:@"baseColorString"];
    }
    else if ([key isEqualToString:@"name"]) {
        [self setValue:value forKey:@"appName"];
    }
    else if ([key isEqualToString:@"inbound_messages"]) {
        [self setValue:value forKey:@"inboundMessages"];
    }
    else if ([key isEqualToString:@"user_update_dup_cache_max_age"]) {
        if ([value isKindOfClass:NSNumber.class]) self.userUpdateDupCacheMaxAge = [value doubleValue];
    }
    else if ([key isEqualToString:@"audio_enabled"]) {
        [self setValue:value forKey:@"audioEnabled"];
    }
    else if ([key isEqualToString:@"metrics_enabled"]) {
        [self setValue:value forKey:@"metricsEnabled"];
    }
    else if ([key isEqualToString:@"background_requests_enabled"]) {
        [self setValue:value forKey:@"backgroundRequestsEnabled"];
    }
    else if ([key isEqualToString:@"local_rate_limit_period"]) {
        if ([value isKindOfClass:NSNumber.class]) self.rateLimitPeriod = [value doubleValue];
    }
    else if ([key isEqualToString:@"local_rate_limit"]) {
        if ([value isKindOfClass:NSNumber.class]) self.rateLimit = [value integerValue];
    }
    else if ([key isEqualToString:@"locale"]) {
        if ([value isKindOfClass:NSString.class]) self.intercomLocale = [NSLocale localeWithLocaleIdentifier:value];
    }
    else if ([key isEqualToString:@"help_center_locale"]) {
        if ([value isKindOfClass:NSString.class]) self.helpCenterLocale = [NSLocale localeWithLocaleIdentifier:value];
    }
    else if ([key isEqualToString:@"new_session_threshold"]) {
        if ([value isKindOfClass:NSNumber.class]) self.newSessionThreshold = [value doubleValue];
    }
    else if ([key isEqualToString:@"batch_user_update_period"]) {
        if ([value isKindOfClass:NSNumber.class]) self.batchUserUpdatePeriod = [value doubleValue];
    }
    else if ([key isEqualToString:@"soft_reset_timeout"]) {
        if ([value isKindOfClass:NSNumber.class]) self.softResetTimeout = [value doubleValue];
    }
    else if ([key isEqualToString:@"ping_delay"]) {
        if ([value isKindOfClass:NSNumber.class]) self.pingDelay = [value doubleValue];
    }
    else if ([key isEqualToString:@"help_center_url"]) {
        [self setValue:value forKey:@"helpCenterUrlString"];
    }
    else if ([key isEqualToString:@"help_center_urls"]) {
        [self setValue:value forKey:@"helpCenterUrlStrings"];
    }
    else if ([key isEqualToString:@"identity_verification_enabled"]) {
        [self setValue:value forKey:@"identityVerificationEnabled"];
    }
    else if ([key isEqualToString:@"team_greeting"]) {
        self.teamGreeting = value;
    }
    else if ([key isEqualToString:@"launcher_bottom_padding"]) {
        if ([value isKindOfClass:NSNumber.class]) self.launcherBottomPadding = [value integerValue];
    }
    else if ([key isEqualToString:@"launcher_alignment"]) {
        [self setValue:value forKey:@"launcherAlignment"];
    }
    else if ([key isEqualToString:@"access_to_teammate_enabled"]) {
        [self setValue:value forKey:@"accessToTeammateEnabled"];
    }
    else if ([key isEqualToString:@"help_center_require_search"]) {
        [self setValue:value forKey:@"helpCenterRequireSearch"];
    }
    else if ([key isEqualToString:@"launcher_logo_url"]) {
        self.launcherCustomLogoURLString = value;
    }
    else if ([key isEqualToString:@"prevent_multiple_inbound_conversations_enabled"]) {
        [self setValue:value forKey:@"preventMultipleInboundConversations"];
    }
    else if ([key isEqualToString:@"has_open_conversations"]) {
        [self setValue:value forKey:@"hasOpenInboundConversations"];
    }
    else if ([key isEqualToString:@"fin_enabled"]) {
        [self setValue:value forKey:@"finEnabled"];
    }
    else if ([key isEqualToString:@"attachment_settings"]) {
        if ([value isKindOfClass:NSDictionary.class]) self.attachmentSettings = [[ICMRecoveredAttachmentSettings alloc] initWithDictionary:value];
    }
    else if ([key isEqualToString:@"conversation_state_sync_settings"]) {
        if ([value isKindOfClass:NSDictionary.class]) self.conversationStateSyncSettings = [[ICMRecoveredConversationStateSyncSettings alloc] initWithDictionary:value];
    }
    else if ([key isEqualToString:@"article_auto_reaction_enabled"]) {
        if ([value isKindOfClass:NSNumber.class]) self.articleAutoReactionEnabled = [value boolValue];
    }
    else if ([key isEqualToString:@"fin_thinking_branded_url"]) {
        self.finThinkingGIFBrandedURLString = value;
    }
    else if ([key isEqualToString:@"fin_thinking_unbranded_url"]) {
        self.finThinkingGIFUnBrandedURLString = value;
    }
    else if ([key isEqualToString:@"ios_crash_reporting_config"]) {
        if ([value isKindOfClass:NSDictionary.class]) self.crashReportingConfig = value;
    }
}
- (BOOL)isEqual:(id)object {
    if (object == self) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToConfig:object];
}
- (BOOL)isEqualToConfig:(ICMConfig *)config {
    if ((self.baseColorString || config.baseColorString) && ![self.baseColorString isEqual:config.baseColorString]) return NO;
    if ((self.appName || config.appName) && ![self.appName isEqual:config.appName]) return NO;
    if ((self.teamIntro || config.teamIntro) && ![self.teamIntro isEqual:config.teamIntro]) return NO;
    if (self.inboundMessages != config.inboundMessages) return NO;
    if (self.audioEnabled != config.audioEnabled) return NO;
    if (self.userUpdateDupCacheMaxAge != config.userUpdateDupCacheMaxAge) return NO;
    if (self.rateLimitPeriod != config.rateLimitPeriod) return NO;
    if (self.rateLimit != config.rateLimit) return NO;
    if (self.metricsEnabled != config.metricsEnabled) return NO;
    if (self.backgroundRequestsEnabled != config.backgroundRequestsEnabled) return NO;
    if ((self.intercomLocale || config.intercomLocale) && ![self.intercomLocale isEqual:config.intercomLocale]) return NO;
    if ((self.helpCenterLocale || config.helpCenterLocale) && ![self.helpCenterLocale isEqual:config.helpCenterLocale]) return NO;
    if (self.newSessionThreshold != config.newSessionThreshold) return NO;
    if (self.batchUserUpdatePeriod != config.batchUserUpdatePeriod) return NO;
    if (self.softResetTimeout != config.softResetTimeout) return NO;
    if (self.pingDelay != config.pingDelay) return NO;
    if ((self.helpCenterUrlString || config.helpCenterUrlString) && ![self.helpCenterUrlString isEqual:config.helpCenterUrlString]) return NO;
    if ((self.helpCenterUrlStrings || config.helpCenterUrlStrings) && ![self.helpCenterUrlStrings isEqual:config.helpCenterUrlStrings]) return NO;
    if ((self.features || config.features) && ![self.features isEqual:config.features]) return NO;
    if (self.identityVerificationEnabled != config.identityVerificationEnabled) return NO;
    if ((self.teamGreeting || config.teamGreeting) && ![self.teamGreeting isEqual:config.teamGreeting]) return NO;
    if (self.launcherBottomPadding != config.launcherBottomPadding) return NO;
    if (![self.launcherAlignment isEqualToString:config.launcherAlignment]) return NO;
    if (self.accessToTeammateEnabled != config.accessToTeammateEnabled) return NO;
    if (self.helpCenterRequireSearch != config.helpCenterRequireSearch) return NO;
    if ((self.launcherCustomLogoURLString || config.launcherCustomLogoURLString) && ![self.launcherCustomLogoURLString isEqual:config.launcherCustomLogoURLString]) return NO;
    if (self.preventMultipleInboundConversations != config.preventMultipleInboundConversations) return NO;
    if (self.hasOpenInboundConversations != config.hasOpenInboundConversations) return NO;
    if (self.finEnabled != config.finEnabled) return NO;
    if ((self.modules || config.modules) && ![self.modules isEqual:config.modules]) return NO;
    if ((self.attachmentSettings || config.attachmentSettings) && ![self.attachmentSettings isEqual:config.attachmentSettings]) return NO;
    if (self.articleAutoReactionEnabled != config.articleAutoReactionEnabled) return NO;
    if ((self.finThinkingGIFBrandedURLString || config.finThinkingGIFBrandedURLString) && ![self.finThinkingGIFBrandedURLString isEqual:config.finThinkingGIFBrandedURLString]) return NO;
    if ((self.finThinkingGIFUnBrandedURLString || config.finThinkingGIFUnBrandedURLString) && ![self.finThinkingGIFUnBrandedURLString isEqual:config.finThinkingGIFUnBrandedURLString]) return NO;
    if ((self.conversationStateSyncSettings || config.conversationStateSyncSettings) && ![self.conversationStateSyncSettings isEqual:config.conversationStateSyncSettings]) return NO;
    if ((self.crashReportingConfig || config.crashReportingConfig) && ![self.crashReportingConfig isEqual:config.crashReportingConfig]) return NO;
    return YES;
}
- (NSUInteger)hash { return self.baseColorString.hash + _inboundMessages; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeObject:self.baseColorString forKey:NSStringFromSelector(@selector(baseColorString))];
    [coder encodeBool:self.identityVerificationEnabled forKey:NSStringFromSelector(@selector(identityVerificationEnabled))];
    [coder encodeObject:self.appName forKey:NSStringFromSelector(@selector(appName))];
    [coder encodeObject:self.teamIntro forKey:NSStringFromSelector(@selector(teamIntro))];
    [coder encodeBool:self.inboundMessages forKey:NSStringFromSelector(@selector(inboundMessages))];
    [coder encodeBool:self.audioEnabled forKey:NSStringFromSelector(@selector(audioEnabled))];
    [coder encodeDouble:self.userUpdateDupCacheMaxAge forKey:NSStringFromSelector(@selector(userUpdateDupCacheMaxAge))];
    [coder encodeDouble:self.rateLimitPeriod forKey:NSStringFromSelector(@selector(rateLimitPeriod))];
    [coder encodeInteger:self.rateLimit forKey:NSStringFromSelector(@selector(rateLimit))];
    [coder encodeObject:self.intercomLocale forKey:NSStringFromSelector(@selector(intercomLocale))];
    [coder encodeObject:self.helpCenterLocale forKey:NSStringFromSelector(@selector(helpCenterLocale))];
    [coder encodeBool:self.metricsEnabled forKey:NSStringFromSelector(@selector(metricsEnabled))];
    [coder encodeBool:self.backgroundRequestsEnabled forKey:NSStringFromSelector(@selector(backgroundRequestsEnabled))];
    [coder encodeDouble:self.newSessionThreshold forKey:NSStringFromSelector(@selector(newSessionThreshold))];
    [coder encodeDouble:self.batchUserUpdatePeriod forKey:NSStringFromSelector(@selector(batchUserUpdatePeriod))];
    [coder encodeDouble:self.softResetTimeout forKey:NSStringFromSelector(@selector(softResetTimeout))];
    [coder encodeDouble:self.pingDelay forKey:NSStringFromSelector(@selector(pingDelay))];
    [coder encodeObject:self.helpCenterUrlString forKey:NSStringFromSelector(@selector(helpCenterUrlString))];
    [coder encodeObject:self.helpCenterUrlStrings forKey:NSStringFromSelector(@selector(helpCenterUrlStrings))];
    [coder encodeObject:self.features forKey:NSStringFromSelector(@selector(features))];
    [coder encodeObject:self.teamGreeting forKey:NSStringFromSelector(@selector(teamGreeting))];
    [coder encodeInteger:self.launcherBottomPadding forKey:NSStringFromSelector(@selector(launcherBottomPadding))];
    [coder encodeObject:self.launcherAlignment forKey:NSStringFromSelector(@selector(launcherAlignment))];
    [coder encodeBool:self.accessToTeammateEnabled forKey:NSStringFromSelector(@selector(accessToTeammateEnabled))];
    [coder encodeBool:self.helpCenterRequireSearch forKey:NSStringFromSelector(@selector(helpCenterRequireSearch))];
    [coder encodeObject:self.launcherCustomLogoURLString forKey:NSStringFromSelector(@selector(launcherCustomLogoURLString))];
    [coder encodeBool:self.preventMultipleInboundConversations forKey:NSStringFromSelector(@selector(preventMultipleInboundConversations))];
    [coder encodeBool:self.hasOpenInboundConversations forKey:NSStringFromSelector(@selector(hasOpenInboundConversations))];
    [coder encodeBool:self.finEnabled forKey:NSStringFromSelector(@selector(finEnabled))];
    [coder encodeObject:self.modules forKey:NSStringFromSelector(@selector(modules))];
    [coder encodeObject:self.attachmentSettings forKey:NSStringFromSelector(@selector(attachmentSettings))];
    [coder encodeObject:self.conversationStateSyncSettings forKey:NSStringFromSelector(@selector(conversationStateSyncSettings))];
    [coder encodeBool:self.articleAutoReactionEnabled forKey:NSStringFromSelector(@selector(articleAutoReactionEnabled))];
    [coder encodeObject:self.crashReportingConfig forKey:NSStringFromSelector(@selector(crashReportingConfig))];
    [coder encodeObject:self.finThinkingGIFBrandedURLString forKey:NSStringFromSelector(@selector(finThinkingGIFBrandedURLString))];
    [coder encodeObject:self.finThinkingGIFUnBrandedURLString forKey:NSStringFromSelector(@selector(finThinkingGIFUnBrandedURLString))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    if ((self = [self init])) {
        self.baseColorString = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(baseColorString))];
        self.identityVerificationEnabled = [coder decodeBoolForKey:NSStringFromSelector(@selector(identityVerificationEnabled))];
        self.appName = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(appName))];
        self.teamIntro = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(teamIntro))];
        self.inboundMessages = [coder decodeBoolForKey:NSStringFromSelector(@selector(inboundMessages))];
        self.audioEnabled = [coder decodeBoolForKey:NSStringFromSelector(@selector(audioEnabled))];
        self.userUpdateDupCacheMaxAge = [coder decodeDoubleForKey:NSStringFromSelector(@selector(userUpdateDupCacheMaxAge))];
        self.rateLimitPeriod = [coder decodeDoubleForKey:NSStringFromSelector(@selector(rateLimitPeriod))];
        self.rateLimit = [coder decodeIntegerForKey:NSStringFromSelector(@selector(rateLimit))];
        self.intercomLocale = [coder decodeObjectOfClass:NSLocale.class forKey:NSStringFromSelector(@selector(intercomLocale))];
        self.helpCenterLocale = [coder decodeObjectOfClass:NSLocale.class forKey:NSStringFromSelector(@selector(helpCenterLocale))];
        self.metricsEnabled = [coder decodeBoolForKey:NSStringFromSelector(@selector(metricsEnabled))];
        if ([coder containsValueForKey:NSStringFromSelector(@selector(backgroundRequestsEnabled))]) self.backgroundRequestsEnabled = [coder decodeBoolForKey:NSStringFromSelector(@selector(backgroundRequestsEnabled))];
        if ([coder containsValueForKey:NSStringFromSelector(@selector(newSessionThreshold))]) self.newSessionThreshold = [coder decodeDoubleForKey:NSStringFromSelector(@selector(newSessionThreshold))];
        if ([coder containsValueForKey:NSStringFromSelector(@selector(batchUserUpdatePeriod))]) self.batchUserUpdatePeriod = [coder decodeDoubleForKey:NSStringFromSelector(@selector(batchUserUpdatePeriod))];
        if ([coder containsValueForKey:NSStringFromSelector(@selector(softResetTimeout))]) self.softResetTimeout = [coder decodeDoubleForKey:NSStringFromSelector(@selector(softResetTimeout))];
        if ([coder containsValueForKey:NSStringFromSelector(@selector(pingDelay))]) self.pingDelay = [coder decodeDoubleForKey:NSStringFromSelector(@selector(pingDelay))];
        self.helpCenterUrlString = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(helpCenterUrlString))];
        if ([coder containsValueForKey:NSStringFromSelector(@selector(helpCenterUrlStrings))]) self.helpCenterUrlStrings = [coder decodeObjectOfClasses:[NSSet setWithObjects:NSArray.class, NSString.class, nil] forKey:NSStringFromSelector(@selector(helpCenterUrlStrings))];
        self.isPersistedToDisk = YES;
        if ([coder containsValueForKey:NSStringFromSelector(@selector(features))]) self.features = [coder decodeObjectOfClasses:[NSSet setWithObjects:NSSet.class, NSString.class, nil] forKey:NSStringFromSelector(@selector(features))];
        self.teamGreeting = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(teamGreeting))];
        self.launcherBottomPadding = [coder decodeIntegerForKey:NSStringFromSelector(@selector(launcherBottomPadding))];
        self.launcherAlignment = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(launcherAlignment))];
        self.accessToTeammateEnabled = [coder decodeBoolForKey:NSStringFromSelector(@selector(accessToTeammateEnabled))];
        self.helpCenterRequireSearch = [coder decodeBoolForKey:NSStringFromSelector(@selector(helpCenterRequireSearch))];
        self.launcherCustomLogoURLString = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(launcherCustomLogoURLString))];
        self.preventMultipleInboundConversations = [coder decodeBoolForKey:NSStringFromSelector(@selector(preventMultipleInboundConversations))];
        self.hasOpenInboundConversations = [coder decodeBoolForKey:NSStringFromSelector(@selector(hasOpenInboundConversations))];
        self.finEnabled = [coder decodeBoolForKey:NSStringFromSelector(@selector(finEnabled))];
        if ([coder containsValueForKey:NSStringFromSelector(@selector(modules))]) self.modules = [coder decodeObjectOfClass:ICMRecoveredConfigModules.class forKey:NSStringFromSelector(@selector(modules))];
        if ([coder containsValueForKey:NSStringFromSelector(@selector(attachmentSettings))]) self.attachmentSettings = [coder decodeObjectOfClass:ICMRecoveredAttachmentSettings.class forKey:NSStringFromSelector(@selector(attachmentSettings))];
        if ([coder containsValueForKey:NSStringFromSelector(@selector(conversationStateSyncSettings))]) self.conversationStateSyncSettings = [coder decodeObjectOfClass:ICMRecoveredConversationStateSyncSettings.class forKey:NSStringFromSelector(@selector(conversationStateSyncSettings))];
        self.articleAutoReactionEnabled = [coder decodeBoolForKey:NSStringFromSelector(@selector(articleAutoReactionEnabled))];
        if ([coder containsValueForKey:NSStringFromSelector(@selector(crashReportingConfig))]) self.crashReportingConfig = [coder decodeObjectOfClasses:[NSSet setWithObjects:NSDictionary.class, NSArray.class, NSString.class, NSNumber.class, nil] forKey:NSStringFromSelector(@selector(crashReportingConfig))];
        self.finThinkingGIFBrandedURLString = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(finThinkingGIFBrandedURLString))];
        self.finThinkingGIFUnBrandedURLString = [coder decodeObjectOfClass:NSString.class forKey:NSStringFromSelector(@selector(finThinkingGIFUnBrandedURLString))];
    }
    return self;
}
- (void)setLauncherCustomLogoURLString:(NSString *)value {
    if ([value isKindOfClass:NSString.class]) { _launcherCustomLogoURLString = value; [self loadCustomLauncherLogoImage]; }
    else _launcherCustomLogoURLString = nil;
}
- (void)setFinThinkingGIFBrandedURLString:(NSString *)value { _finThinkingGIFBrandedURLString = value; [self fetchAndCacheFinThinkingGIFBrandedImage]; }
- (void)setFinThinkingGIFUnBrandedURLString:(NSString *)value { _finThinkingGIFUnBrandedURLString = value; [self fetchAndCacheFinThinkingGIFUnBrandedImage]; }
+ (instancetype)sharedConfig {
    static dispatch_once_t once;
    dispatch_once(&once, ^{ sharedConfig = [self configFromDisk] ?: [ICMConfig new]; });
    return sharedConfig;
}
+ (instancetype)configFromDisk {
    @synchronized(self) { return [ICMPersistenceProxy unarchiveObject:ICMConfig.class withFile:[ICMConfig archivePath]]; }
}
+ (void)saveConfig:(ICMConfig *)config {
    @synchronized(self) {
        [ICMPersistenceProxy archiveRootObject:config toFile:[ICMConfig archivePath]];
        config.isPersistedToDisk = YES;
    }
}
+ (void)deleteSharedConfig {
    @synchronized(self) {
        [[ICMPersistenceProxy fileManager] removeItemAtPath:[ICMConfig archivePath] error:nil];
        sharedConfig = [ICMConfig new];
        [[NSNotificationCenter defaultCenter] postNotificationName:@"kICMIntercomConfigChangedNotification" object:nil];
    }
}
- (void)saveIfNeeded {
    if ([self isEqual:[ICMConfig sharedConfig]] && [ICMConfig sharedConfig].isPersistedToDisk) return;
    [self save];
}
- (void)save {
    sharedConfig = self;
    [ICMConfig saveConfig:self];
    dispatch_async(dispatch_get_main_queue(), ^{ [[NSNotificationCenter defaultCenter] postNotificationName:@"kICMIntercomConfigChangedNotification" object:nil]; });
}
+ (NSString *)archivePath { return [[ICMUtils intercomDocumentsDirectory] stringByAppendingPathComponent:@"config.icm"]; }
- (BOOL)receivedFromServer { return self.isPersistedToDisk; }
- (BOOL)useDefaultLauncherAlignment { return [self.launcherAlignment isEqualToString:[@"right" copy]]; }
- (BOOL)hasFeature:(NSString *)feature { return [self.features containsObject:feature]; }
@end
#pragma clang diagnostic pop
