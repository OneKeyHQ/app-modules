// Genuine unrecovered theme dependency declarations, never replacement bodies.
#import <UIKit/UIKit.h>
#import "../Identity/ICMUtils.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate5Theme")))
@interface ICMRecoveredTheme : NSObject
@property (nonatomic, readonly) UIColor *primaryIcon;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate20DefaultThemeProvider")))
@interface ICMRecoveredDefaultThemeProvider : NSObject
+ (instancetype)shared;
@property (nonatomic, strong) ICMRecoveredTheme *theme;
@end
@interface ICMUtils (ApplicationWindows)
+ (NSArray *)fetchApplicationWindows;
@end
