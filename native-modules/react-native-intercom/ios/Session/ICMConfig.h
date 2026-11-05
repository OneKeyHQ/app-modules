// Partial original Config source; image/UI implementations remain unrestored.
#import "../Identity/ICMSafeNetworkModel.h"
#import "ICMConfigDependencies.h"
#import "ICMConfigProtocol.h"
#import <CoreGraphics/CoreGraphics.h>
@class UIImage;
@interface ICMConfig : ICMSafeNetworkModel <ICMConfigProtocol, NSSecureCoding> {
@protected
    BOOL _inboundMessages;
    BOOL _audioEnabled;
    BOOL _metricsEnabled;
    BOOL _backgroundRequestsEnabled;
    BOOL _identityVerificationEnabled;
    BOOL _accessToTeammateEnabled;
    BOOL _helpCenterRequireSearch;
    BOOL _preventMultipleInboundConversations;
    BOOL _hasOpenInboundConversations;
    BOOL _finEnabled;
    BOOL _articleAutoReactionEnabled;
    BOOL _isPersistedToDisk;
    NSString * _appName;
    NSString * _teamIntro;
    NSString * _teamGreeting;
    NSString * _launcherAlignment;
    NSInteger _launcherBottomPadding;
    NSString * _launcherCustomLogoURLString;
    NSDictionary * _crashReportingConfig;
    NSTimeInterval _userUpdateDupCacheMaxAge;
    NSTimeInterval _newSessionThreshold;
    NSTimeInterval _batchUserUpdatePeriod;
    NSTimeInterval _softResetTimeout;
    NSTimeInterval _pingDelay;
    NSTimeInterval _rateLimitPeriod;
    NSUInteger _rateLimit;
    NSString * _helpCenterUrlString;
    NSArray * _helpCenterUrlStrings;
    NSLocale * _intercomLocale;
    NSLocale * _helpCenterLocale;
    NSSet * _features;
    ICMRecoveredConfigModules * _modules;
    ICMRecoveredAttachmentSettings * _attachmentSettings;
    NSString * _finThinkingGIFBrandedURLString;
    NSString * _finThinkingGIFUnBrandedURLString;
    ICMRecoveredConversationStateSyncSettings * _conversationStateSyncSettings;
    NSString * _baseColorString;
}
@property (nonatomic) BOOL isPersistedToDisk;
@property (nonatomic, copy) NSString * baseColorString;
@property (nonatomic, copy) NSString * appName;
@property (nonatomic, copy) NSString * teamIntro;
@property (nonatomic, copy) NSString * teamGreeting;
@property (nonatomic, copy) NSString * launcherAlignment;
@property (nonatomic) NSInteger launcherBottomPadding;
@property (nonatomic, copy) NSString * launcherCustomLogoURLString;
@property (nonatomic) BOOL inboundMessages;
@property (nonatomic) BOOL audioEnabled;
@property (nonatomic) BOOL metricsEnabled;
@property (nonatomic) BOOL backgroundRequestsEnabled;
@property (nonatomic) BOOL identityVerificationEnabled;
@property (nonatomic) BOOL accessToTeammateEnabled;
@property (nonatomic) BOOL helpCenterRequireSearch;
@property (nonatomic) BOOL preventMultipleInboundConversations;
@property (nonatomic) BOOL hasOpenInboundConversations;
@property (nonatomic) BOOL finEnabled;
@property (nonatomic) BOOL articleAutoReactionEnabled;
@property (nonatomic, strong) NSDictionary * crashReportingConfig;
@property (nonatomic) NSTimeInterval userUpdateDupCacheMaxAge;
@property (nonatomic) NSTimeInterval newSessionThreshold;
@property (nonatomic) NSTimeInterval batchUserUpdatePeriod;
@property (nonatomic) NSTimeInterval softResetTimeout;
@property (nonatomic) NSTimeInterval pingDelay;
@property (nonatomic) NSTimeInterval rateLimitPeriod;
@property (nonatomic) NSUInteger rateLimit;
@property (nonatomic, copy) NSString * helpCenterUrlString;
@property (nonatomic, copy) NSArray * helpCenterUrlStrings;
@property (nonatomic, strong) NSLocale * intercomLocale;
@property (nonatomic, strong) NSLocale * helpCenterLocale;
@property (nonatomic, strong) NSSet * features;
@property (nonatomic, strong) ICMRecoveredConfigModules * modules;
@property (nonatomic, strong) ICMRecoveredAttachmentSettings * attachmentSettings;
@property (nonatomic, strong) NSString * finThinkingGIFBrandedURLString;
@property (nonatomic, strong) NSString * finThinkingGIFUnBrandedURLString;
@property (nonatomic, strong) ICMRecoveredConversationStateSyncSettings * conversationStateSyncSettings;
+ (instancetype)sharedConfig;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
+ (void)updateSharedInstanceFromDictionary:(NSDictionary *)dictionary;
+ (instancetype)configFromDisk;
+ (void)saveConfig:(ICMConfig *)config;
+ (void)deleteSharedConfig;
+ (NSString *)archivePath;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (BOOL)isEqualToConfig:(ICMConfig *)config;
- (void)saveIfNeeded;
- (void)save;
- (BOOL)receivedFromServer;
- (BOOL)useDefaultLauncherAlignment;
- (BOOL)hasFeature:(NSString *)feature;
@end
@interface ICMConfig (ImagesAndUI)
- (NSString *)resizedCustomLauncherImageCacheKey;
- (void)loadCustomLauncherLogoImage;
- (UIImage *)customLauncherImage;
- (UIImage *)resizeImage:(UIImage *)image toSize:(CGSize)size;
- (void)fetchAndCacheFinThinkingGIFBrandedImage;
- (void)fetchAndCacheFinThinkingGIFUnBrandedImage;
- (void)prefetchHomeHeaderImagesIfNeeded;
- (BOOL)canStartConversation;
- (NSString *)titleForSpace:(NSInteger)space;
- (BOOL)isTicketsSpaceEnabled;
@end
