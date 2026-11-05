#import "ICMDeviceData.h"
#import "ICMUtils.h"
#import <sys/utsname.h>
#import <TargetConditionals.h>
#if TARGET_OS_IOS
#import <UIKit/UIKit.h>
#import "ICMAppEnvironmentChecker.h"
#import "../Session/ICMIdentityStore.h"
#import "../Session/ICMPushNotificationManager.h"
#endif
@implementation ICMDeviceData
@synthesize platform = _platform;
@synthesize platformVersion = _platformVersion;
@synthesize version = _version;
@synthesize application = _application;
@synthesize applicationId = _applicationId;
@synthesize browser = _browser;
@synthesize deviceType = _deviceType;
@synthesize language = _language;
@synthesize deviceTokenEnvironment = _deviceTokenEnvironment;
@synthesize deviceToken = _deviceToken;
@synthesize environment = _environment;
@synthesize supportsMultipleWindows = _supportsMultipleWindows;
@synthesize userInterfaceStyle = _userInterfaceStyle;
@synthesize sdkType = _sdkType;
@synthesize languageDirection = _languageDirection;
@synthesize autoIntegratesPushNotifications = _autoIntegratesPushNotifications;
@synthesize supportsRichPush = _supportsRichPush;
@synthesize reducedMotionEnabled = _reducedMotionEnabled;
@synthesize invertColorsEnabled = _invertColorsEnabled;
@synthesize isVoiceOverRunning = _isVoiceOverRunning;
@synthesize contentSizeCategory = _contentSizeCategory;

+ (NSString *)deviceIdentifier {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    NSString *identifier = [defaults stringForKey:@"intercom-deviceIdentifier"];
    if (!identifier) {
        identifier = [NSUUID UUID].UUIDString;
        [defaults setObject:identifier forKey:@"intercom-deviceIdentifier"];
    }
    return identifier;
}
+ (NSString *)machineName {
    struct utsname systemInfo;
    uname(&systemInfo);
    return [NSString stringWithCString:systemInfo.machine encoding:NSUTF8StringEncoding];
}
+ (NSString *)sdkType {
    switch ([ICMUtils fetchSDKType]) {
        case 0: return @"intercom-sdk-cordova";
        case 1: return @"intercom-sdk-react-native";
        case 2: return @"intercom-sdk-flutter";
        case 3: return @"intercom-sdk-unity";
        default: return @"intercom-sdk-native";
    }
}
+ (void)storeUserInterfaceStyle:(NSInteger)style { [[NSUserDefaults standardUserDefaults] setInteger:style forKey:@"userInterfaceStyle"]; }
- (NSString *)fetchUserInterfaceStyle {
    NSInteger style = [[NSUserDefaults standardUserDefaults] integerForKey:@"userInterfaceStyle"];
    return style == 2 ? @"Dark" : (style == 1 ? @"Light" : @"Unspecified");
}
#if TARGET_OS_IOS
- (instancetype)init {
    if ((self = [super init])) {
        _platform = [[self class] machineName];
        _platformVersion = [UIDevice currentDevice].systemVersion;
        _version = [ICMUtils appVersion];
        _application = [ICMUtils applicationName];
        _applicationId = [ICMUtils bundleIdentifier];
        _browser = @"Intercom-iOS-SDK";
        _deviceType = [UIDevice currentDevice].model;
        _language = [NSLocale preferredLanguages].firstObject;
        _deviceToken = [ICMIdentityStore currentIdentity].deviceTokenData.deviceToken;
        _deviceTokenEnvironment = [ICMAppEnvironmentChecker stringForDeviceTokenEnvironment:[ICMIdentityStore currentIdentity].deviceTokenData.deviceTokenEnvironment];
        _environment = [ICMAppEnvironmentChecker stringForAppEnvironment:[ICMAppEnvironmentChecker appEnvironment]];
        _supportsMultipleWindows = [UIApplication sharedApplication].supportsMultipleScenes;
        _userInterfaceStyle = [self fetchUserInterfaceStyle];
        _sdkType = [[self class] sdkType];
        _languageDirection = [UIApplication sharedApplication].userInterfaceLayoutDirection == 0 ? @"LTR" : @"RTL";
        _autoIntegratesPushNotifications = [ICMUtils shouldHandlePushNotificationsAutomatically];
        _supportsRichPush = [ICMUtils isRunningNotificationServiceExtension];
        _reducedMotionEnabled = UIAccessibilityIsReduceMotionEnabled();
        _invertColorsEnabled = UIAccessibilityIsInvertColorsEnabled();
        _isVoiceOverRunning = UIAccessibilityIsVoiceOverRunning();
        _contentSizeCategory = [UIApplication sharedApplication].preferredContentSizeCategory;
    }
    return self;
}
- (NSMutableDictionary *)dictionaryRepresentation {
    NSMutableDictionary *parameters = [NSMutableDictionary dictionary];
    if (self.platform) parameters[@"platform"] = self.platform;
    if (self.platformVersion) parameters[@"platform_version"] = self.platformVersion;
    if (self.deviceType) parameters[@"device"] = self.deviceType;
    if (self.version) parameters[@"version"] = self.version;
    if (self.application) parameters[@"application"] = self.application;
    if (self.applicationId) parameters[@"application_id"] = self.applicationId;
    if (self.browser) parameters[@"browser"] = self.browser;
    if (self.language) parameters[@"language"] = self.language;
    if ([ICMPushNotificationManager isPushNotificationsEnabled]) {
        if (self.deviceToken) parameters[@"device_token"] = self.deviceToken;
        if (self.deviceTokenEnvironment) parameters[@"device_token_environment"] = self.deviceTokenEnvironment;
    }
    if (self.environment) parameters[@"ios_environment"] = self.environment;
    if ([_deviceType hasPrefix:@"iPad"]) parameters[@"supports_multiple_windows"] = self.supportsMultipleWindows ? @"YES" : @"NO";
    if (self.userInterfaceStyle) parameters[@"user_interface_style"] = self.userInterfaceStyle;
    if (self.sdkType) parameters[@"sdk_type"] = self.sdkType;
    if (self.languageDirection) parameters[@"language_direction"] = self.languageDirection;
    parameters[@"auto_integrates_push_notifications"] = self.autoIntegratesPushNotifications ? @"YES" : @"NO";
    parameters[@"supports_rich_push"] = self.supportsRichPush ? @"YES" : @"NO";
    parameters[@"reduced_motion_enabled"] = self.reducedMotionEnabled ? @"YES" : @"NO";
    parameters[@"invert_colors_enabled"] = self.invertColorsEnabled ? @"YES" : @"NO";
    parameters[@"is_voice_over_running"] = self.isVoiceOverRunning ? @"YES" : @"NO";
    if (self.contentSizeCategory) parameters[@"content_size_category"] = self.contentSizeCategory;
    return parameters;
}
#endif
@end
