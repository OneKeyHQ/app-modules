#import "ICMBridge+Application.h"
#import "ICMPresentationManager.h"
#import "ICMProactiveContentVisibility.h"
#import "ICMConfig.h"
#import "ICMConfigUIModels.h"
#import "ICMDataManagerDependencies.h"
#import "ICMIdentityStore.h"
#import "ICMOreoEngine.h"
#import "ICMHTTPBridge+DeviceTokens.h"
#import "../Identity/ICMUtils.h"
@interface ICMRecoveredCustomizationConfig (OriginalThemeOverride)
- (void)clearThemeOverride;
- (void)setThemeOverrideWithThemeMode:(NSInteger)mode;
@end
@implementation ICMBridge (OriginalApplication)
+ (void)setNeedsStatusBarAppearanceUpdate { [[ICMPresentationManager sharedInstance] setNeedsHostAppStatusBarAppearanceUpdate]; }
+ (void)setThemeOverride:(NSInteger)mode {
    ICMConfig *config = [ICMConfig sharedConfig];
    if (!config || !config.modules || !config.modules.customizationConfig) return;
    if (mode == 0) [config.modules.customizationConfig clearThemeOverride];
    else [config.modules.customizationConfig setThemeOverrideWithThemeMode:mode == 1 ? 0 : mode == 2 ? 1 : 2];
    [[ICMPresentationManager sharedInstance] updateWindowThemeMode];
}
+ (void)setInAppMessagesVisible:(BOOL)visible { [ICMPresentationManager sharedInstance].inAppMessagesVisible = visible; }
+ (void)setProactiveVisibleWithCarousels:(BOOL)carousels surveys:(BOOL)surveys { [[ICMRecoveredProactiveContentVisibility shared] setProactiveVisibleWithCarousels:carousels surveys:surveys]; }
+ (void)setLauncherVisible:(BOOL)visible { [ICMPresentationManager sharedInstance].launcherVisible = visible; }
+ (void)hideIntercom { [[ICMPresentationManager sharedInstance] dismissAllIntercomViews]; }
+ (NSUInteger)unreadConversationCount { return [ICMDataManager sharedManager].unreadManager.unreadConversationIds.count; }
+ (NSUInteger)unreadTicketCount { return [ICMDataManager sharedManager].unreadManager.unreadTicketIds.count; }
+ (void)setInAppBottomPadding:(double)padding {
    ICMPresentationManager *manager = [ICMPresentationManager sharedInstance];
    manager.manualInAppBottomPaddingSet = YES;
    manager.inAppBottomPadding = padding;
}
+ (void)dispatchOnMainQueueIfAppInactive:(void (^)(void))block {
    if (!block) return;
    if ([UIApplication sharedApplication].applicationState == UIApplicationStateInactive) dispatch_async(dispatch_get_main_queue(), ^{ block(); });
    else block();
}
+ (void)setAuthTokens:(NSDictionary *)tokens success:(void (^)(void))success failure:(void (^)(NSError *))failure { [ICMHTTPBridge setAuthTokens:tokens success:success failure:failure]; }
+ (void)enableLogging { [ICMUtils setDebugLoggingEnabled:YES]; }
+ (void)setWrapperVersion:(NSString *)version forWrapperType:(NSInteger)type { [[ICMIdentityStore currentIdentity].appIdentity.engine setWrapperVersion:version forWrapper:type]; }
@end
