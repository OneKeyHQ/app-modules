#import "ICMBridge.h"
@interface ICMBridge (OriginalApplication)
+ (void)setNeedsStatusBarAppearanceUpdate;
+ (void)setThemeOverride:(NSInteger)mode;
+ (void)setInAppMessagesVisible:(BOOL)visible;
+ (void)setProactiveVisibleWithCarousels:(BOOL)carousels surveys:(BOOL)surveys;
+ (void)setLauncherVisible:(BOOL)visible;
+ (void)hideIntercom;
+ (NSUInteger)unreadConversationCount;
+ (NSUInteger)unreadTicketCount;
+ (void)setInAppBottomPadding:(double)padding;
+ (void)dispatchOnMainQueueIfAppInactive:(void (^)(void))block;
+ (void)setAuthTokens:(NSDictionary *)tokens success:(void (^)(void))success failure:(void (^)(NSError *))failure;
+ (void)enableLogging;
+ (void)setWrapperVersion:(NSString *)version forWrapperType:(NSInteger)type;
@end
