#import "ICMWindowDependencies.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate11ColorHelper")))
__attribute__((swift_name("ColorHelper")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredColorHelper : NSObject
+ (BOOL)isLightColor:(UIColor *)color;
+ (UIColor *)suitableForegroundColorFor:(UIColor *)color theme:(ICMRecoveredTheme *)theme;
@end
