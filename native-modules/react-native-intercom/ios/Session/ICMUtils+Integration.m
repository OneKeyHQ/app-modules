#import "ICMUtils+Integration.h"
#import "../Identity/ICMAppEnvironmentChecker.h"
@implementation ICMUtils (OriginalIntegration)
+ (NSArray<UIWindowScene *> *)foregroundOrderedWindowScenes {
    NSMutableArray *foreground = [NSMutableArray array];
    NSMutableArray *other = [NSMutableArray array];
    for (UIScene *scene in UIApplication.sharedApplication.connectedScenes) {
        if ([scene isKindOfClass:[UIWindowScene class]]) {
            [(scene.activationState == 0 ? foreground : other) addObject:scene];
        }
    }
    return [foreground arrayByAddingObjectsFromArray:other];
}
+ (NSString *)minimumOSVersion { return [[NSBundle mainBundle] objectForInfoDictionaryKey:@"MinimumOSVersion"]; }
+ (void)checkIntegration {
    if (![self intercomBundle]) NSLog(@"[Intercom] ERROR - 💥👎 Intercom.bundle hasn't been added to your app correctly. This will result in images not appearing in the SDK's messaging UI. More information on how to correctly integrate Intercom is available here: https://developers.intercom.com/docs/ios-installation.");
    if ([self shouldShowPhotoUsagePListError]) NSLog(@"[Intercom] ERROR - 📸 Photos usage description not present 📸. Your app will be rejected from the AppStore unless you add NSPhotoLibraryUsageDescription to your Info.plist. This is needed for your customers who are running iOS 13 and below. More information is available here: https://developers.intercom.com/docs/ios-installation#section-step-2-update-infoplist");
}
+ (NSBundle *)intercomBundle {
    static NSBundle *bundle;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ bundle = [self loadIntercomBundle]; });
    return bundle;
}
+ (NSBundle *)translationsBundle {
    static NSBundle *bundle;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ bundle = [self loadTranslationsBundle]; });
    return bundle;
}
+ (NSBundle *)loadTranslationsBundle { return [self loadBundle:@"IntercomTranslations"]; }
+ (NSBundle *)loadIntercomBundle { return [self loadBundle:@"Intercom"]; }
+ (NSBundle *)loadBundle:(NSString *)name {
    NSURL *url = [[NSBundle bundleWithIdentifier:@"io.intercom.com.Intercom"] URLForResource:name withExtension:@"bundle"];
    if (!url) url = [[NSBundle bundleForClass:[ICMUtils class]] URLForResource:name withExtension:@"bundle"];
    if (!url) url = [[NSBundle mainBundle] URLForResource:name withExtension:@"bundle"];
    return url ? [NSBundle bundleWithURL:url] : nil;
}
+ (BOOL)photoUsageDescriptionPresent { return [[NSBundle mainBundle] objectForInfoDictionaryKey:@"NSPhotoLibraryUsageDescription"] != nil; }
+ (BOOL)shouldShowPhotoUsagePListError {
    if ([[self minimumOSVersion] integerValue] > 13 || [self photoUsageDescriptionPresent]) return NO;
    return [ICMAppEnvironmentChecker isDevelopmentEnvironment:[ICMAppEnvironmentChecker appEnvironment]];
}
+ (BOOL)swizzlingDisabled {
    id value = [NSBundle mainBundle].infoDictionary[@"IntercomDisableSwizzling"];
    if ([value isKindOfClass:[NSNumber class]]) return [value boolValue];
    if (value) NSLog(@"[Intercom] ⚠️ WARNING: Value for key `IntercomDisableSwizzling` in `Info.plist` was set incorrectly. This value must be of type BOOL.");
    return NO;
}
@end
