#import <UIKit/UIKit.h>
#import "ICMUtils.h"
#import "ICMPersistenceProxy.h"
#import "../Session/ICMUtils+Integration.h"
@implementation ICMUtils
+ (NSString *)intercomVersion { return @"19.9.1"; }
+ (NSString *)intercomAgent { return [NSString stringWithFormat:@"%@/%@", @"intercom-ios-sdk", [ICMUtils intercomVersion]]; }
+ (NSString *)appVersion {
    id version = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"];
    id build = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"];
    return [NSString stringWithFormat:@"%@(%@)", version, build];
}
+ (NSString *)messengerEnvironmentIdentifier {
    id value = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"IntercomMessengerEnvironment"];
    if (![value isKindOfClass:[NSString class]]) return nil;
    NSString *trimmed = [value stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
    return trimmed.length ? trimmed : nil;
}
static BOOL debugLoggingEnabled;
+ (BOOL)debugLoggingEnabled {
    @synchronized(self) { return debugLoggingEnabled; }
}
+ (void)setDebugLoggingEnabled:(BOOL)enabled {
    @synchronized(self) { debugLoggingEnabled = enabled; }
}
+ (NSString *)intercomDocumentsDirectory {
    @synchronized(self) {
        if (![self migrateDocumentsIfNeeded]) return [ICMUtils intercomLegacyDocumentsPath];
        NSString *path = [ICMUtils intercomDocumentsPath];
        if (![[ICMPersistenceProxy fileManager] fileExistsAtPath:path]) {
            [[ICMPersistenceProxy fileManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:nil];
        }
        return path;
    }
}
+ (NSString *)intercomCachesDirectory {
    NSString *path = [self intercomCachesPath];
    if (![[ICMPersistenceProxy fileManager] fileExistsAtPath:path]) {
        [[ICMPersistenceProxy fileManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:nil];
    }
    return path;
}
+ (NSString *)intercomDocumentsPath {
    NSString *library = NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, YES).firstObject;
    return [[library stringByAppendingPathComponent:@"PrivateDocuments"] stringByAppendingPathComponent:@"io.intercom.ios"];
}
+ (NSString *)intercomLegacyDocumentsPath {
    NSString *documents = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).firstObject;
    return [documents stringByAppendingPathComponent:@"io.intercom.ios"];
}
+ (BOOL)migrateDocumentsIfNeeded {
    NSString *legacy = [ICMUtils intercomLegacyDocumentsPath];
    NSString *destination = [ICMUtils intercomDocumentsPath];
    if (![[ICMPersistenceProxy fileManager] fileExistsAtPath:legacy] || [[ICMPersistenceProxy fileManager] fileExistsAtPath:destination]) return YES;
    NSString *parent = [destination stringByDeletingLastPathComponent];
    if (![[ICMPersistenceProxy fileManager] fileExistsAtPath:parent]
        && ![[ICMPersistenceProxy fileManager] createDirectoryAtPath:parent withIntermediateDirectories:YES attributes:nil error:nil]) return NO;
    BOOL copied = [[ICMPersistenceProxy fileManager] copyItemAtPath:legacy toPath:destination error:nil];
    [[ICMPersistenceProxy fileManager] removeItemAtPath:copied ? legacy : destination error:nil];
    return copied;
}
+ (NSString *)intercomCachesPath {
    NSString *caches = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES).firstObject;
    return [caches stringByAppendingPathComponent:@"io.intercom.ios"];
}
+ (NSString *)applicationName { return [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleName"]; }
+ (NSString *)bundleIdentifier { return [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleIdentifier"]; }
+ (BOOL)shouldHandlePushNotificationsAutomatically {
    id value = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"IntercomAutoIntegratePushNotifications"];
    BOOL automatic = [value isKindOfClass:NSNumber.class] ? [value boolValue] : YES;
    if ([value isKindOfClass:NSString.class]) automatic = automatic && ![value isEqualToString:@"NO"];
    if ([ICMUtils debugLoggingEnabled]) NSLog(@"[Intercom] Push Notifications Handler: %@", automatic ? @"AUTO" : @"MANUAL");
    return automatic;
}
+ (BOOL)isRunningNotificationServiceExtension {
    id extension = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"NSExtension"];
    return extension ? [extension[@"NSExtensionPointIdentifier"] isEqualToString:@"com.apple.usernotifications.service"] : NO;
}
+ (NSInteger)fetchSDKType {
    Class reactNative = NSClassFromString(@"IntercomModule");
    Class cordova = NSClassFromString(@"IntercomBridge");
    Class flutter = NSClassFromString(@"IntercomFlutterPlugin");
    Class unity = NSClassFromString(@"IntercomUnity");
    if (reactNative && [reactNative conformsToProtocol:NSProtocolFromString(@"RCTBridgeModule")]) return 1;
    if (cordova && [cordova isSubclassOfClass:NSClassFromString(@"CDVPlugin")]) return 0;
    if (flutter && [flutter conformsToProtocol:NSProtocolFromString(@"FlutterPlugin")]) return 2;
    return unity ? 3 : 4;
}
+ (BOOL)sceneManifestPresent {
    return [[NSBundle mainBundle] objectForInfoDictionaryKey:@"UIApplicationSceneManifest"] != nil;
}
+ (NSString *)extractArticleIdFromSheetParams:(NSDictionary *)params {
    if (![params[@"action_id"] isKindOfClass:[NSString class]]) return nil;
    if (![params[@"action_id"] isEqualToString:@"article-link"]) return nil;
    id articleId = params[@"values"][@"article_id"];
    return articleId && [articleId length] != 0 ? articleId : nil;
}
+ (BOOL)contentSizeCategoryIsAccessibilityCategory {
    UIContentSizeCategory category = UIApplication.sharedApplication.preferredContentSizeCategory;
    return UIContentSizeCategoryIsAccessibilityCategory(category);
}
+ (NSArray *)jsonArrayFromLocalFile:(NSString *)name error:(NSError **)error {
    NSString *path = [[ICMUtils intercomBundle] pathForResource:name ofType:@"json"];
    NSData *data = [NSData dataWithContentsOfFile:path];
    if (!data) data = [NSData new];
    return [NSJSONSerialization JSONObjectWithData:data options:0 error:error];
}
+ (CGFloat)scaleToAccessibilitySizing:(CGFloat)value {
    return [[UIFontMetrics metricsForTextStyle:UIFontTextStyleTitle1] scaledValueForValue:value];
}
+ (void)enableRTLSupportForContainerClass:(Class)containerClass {
    if (UIApplication.sharedApplication.userInterfaceLayoutDirection == UIUserInterfaceLayoutDirectionRightToLeft) {
        [[UIView appearanceWhenContainedInInstancesOfClasses:@[containerClass, UIView.class]] setSemanticContentAttribute:UISemanticContentAttributeForceRightToLeft];
    }
}
@end
