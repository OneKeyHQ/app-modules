#import "../Identity/ICMUtils.h"
#import <UIKit/UIKit.h>
@interface ICMUtils (OriginalIntegration)
+ (NSArray<UIWindowScene *> *)foregroundOrderedWindowScenes;
+ (NSString *)minimumOSVersion;
+ (void)checkIntegration;
+ (NSBundle *)intercomBundle;
+ (NSBundle *)translationsBundle;
+ (NSBundle *)loadTranslationsBundle;
+ (NSBundle *)loadIntercomBundle;
+ (NSBundle *)loadBundle:(NSString *)name;
+ (BOOL)photoUsageDescriptionPresent;
+ (BOOL)shouldShowPhotoUsagePListError;
+ (BOOL)swizzlingDisabled;
@end
