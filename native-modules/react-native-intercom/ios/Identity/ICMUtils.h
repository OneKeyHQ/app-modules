// Dependency subset only. Other ICMUtils methods remain unrestored.
#import <Foundation/Foundation.h>
@interface ICMUtils : NSObject
+ (BOOL)debugLoggingEnabled;
+ (void)setDebugLoggingEnabled:(BOOL)enabled;
+ (NSString *)intercomDocumentsDirectory;
+ (NSString *)intercomCachesDirectory;
+ (NSString *)intercomDocumentsPath;
+ (NSString *)intercomLegacyDocumentsPath;
+ (NSString *)intercomCachesPath;
+ (BOOL)migrateDocumentsIfNeeded;
+ (NSString *)intercomVersion;
+ (NSString *)intercomAgent;
+ (NSString *)appVersion;
+ (NSString *)messengerEnvironmentIdentifier;
+ (NSString *)applicationName;
+ (NSString *)bundleIdentifier;
+ (BOOL)shouldHandlePushNotificationsAutomatically;
+ (BOOL)isRunningNotificationServiceExtension;
+ (NSInteger)fetchSDKType;
+ (BOOL)sceneManifestPresent;
+ (BOOL)contentSizeCategoryIsAccessibilityCategory;
+ (CGFloat)scaleToAccessibilitySizing:(CGFloat)value;
+ (void)enableRTLSupportForContainerClass:(Class)containerClass;
+ (NSArray *)jsonArrayFromLocalFile:(NSString *)name error:(NSError **)error;
+ (NSString *)extractArticleIdFromSheetParams:(NSDictionary *)params;
@end
