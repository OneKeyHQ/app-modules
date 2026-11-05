#import "ICMButton.h"
#import "ICMWindowDependencies.h"
@interface ICMActionButton : ICMButton
@property (nonatomic, strong) ICMRecoveredTheme *theme;
- (instancetype)initFilledButtonWithTitle:(NSString *)title;
- (instancetype)initBorderedButtonWithTitle:(NSString *)title;
- (instancetype)initWithTitle:(NSString *)title style:(NSInteger)style backgroundColor:(UIColor *)background foregroundColor:(UIColor *)foreground;
- (void)setupButtonConfiguration;
- (void)configureForStyle:(NSInteger)style backgroundColor:(UIColor *)background foregroundColor:(UIColor *)foreground;
- (void)setupButtonFontConfigurationForAXSizes;
@end
