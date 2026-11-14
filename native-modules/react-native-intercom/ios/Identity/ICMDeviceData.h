// Original iOS class layout; product push/identity dependencies remain unlinked.
#import <Foundation/Foundation.h>
#import <TargetConditionals.h>
@interface ICMDeviceData : NSObject {
@protected
    BOOL _supportsMultipleWindows;
    BOOL _autoIntegratesPushNotifications;
    BOOL _supportsRichPush;
    BOOL _reducedMotionEnabled;
    BOOL _invertColorsEnabled;
    BOOL _isVoiceOverRunning;
    NSString *_platform;
    NSString *_platformVersion;
    NSString *_version;
    NSString *_application;
    NSString *_applicationId;
    NSString *_browser;
    NSString *_deviceType;
    NSString *_language;
    NSString *_deviceTokenEnvironment;
    NSString *_deviceToken;
    NSString *_environment;
    NSString *_userInterfaceStyle;
    NSString *_sdkType;
    NSString *_languageDirection;
    NSString *_contentSizeCategory;
}
@property (nonatomic, readonly, copy) NSString *platform;
@property (nonatomic, readonly, copy) NSString *platformVersion;
@property (nonatomic, readonly, copy) NSString *version;
@property (nonatomic, readonly, copy) NSString *application;
@property (nonatomic, readonly, copy) NSString *applicationId;
@property (nonatomic, readonly, copy) NSString *browser;
@property (nonatomic, readonly, copy) NSString *deviceType;
@property (nonatomic, readonly, copy) NSString *language;
@property (nonatomic, readonly, copy) NSString *deviceTokenEnvironment;
@property (nonatomic, readonly, copy) NSString *deviceToken;
@property (nonatomic, readonly, copy) NSString *environment;
@property (nonatomic, readonly) BOOL supportsMultipleWindows;
@property (nonatomic, readonly, copy) NSString *userInterfaceStyle;
@property (nonatomic, readonly, copy) NSString *sdkType;
@property (nonatomic, readonly, copy) NSString *languageDirection;
@property (nonatomic, readonly) BOOL autoIntegratesPushNotifications;
@property (nonatomic, readonly) BOOL supportsRichPush;
@property (nonatomic, readonly) BOOL reducedMotionEnabled;
@property (nonatomic, readonly) BOOL invertColorsEnabled;
@property (nonatomic, readonly) BOOL isVoiceOverRunning;
@property (nonatomic, readonly, copy) NSString *contentSizeCategory;
+ (NSString *)deviceIdentifier;
+ (NSString *)machineName;
+ (NSString *)sdkType;
+ (void)storeUserInterfaceStyle:(NSInteger)style;
- (NSString *)fetchUserInterfaceStyle;
#if TARGET_OS_IOS
- (NSMutableDictionary *)dictionaryRepresentation;
#endif
@end
