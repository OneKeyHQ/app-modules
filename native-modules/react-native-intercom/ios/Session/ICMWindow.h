// Original UIKit dependency; see IOS_RECOVERY for implementation status.
#import <UIKit/UIKit.h>
#import "ICMRootViewController.h"
@interface ICMWindow : UIWindow {
    BOOL _fullscreen;
}
@property (nonatomic) BOOL fullscreen;
@property (nonatomic, strong) ICMRootViewController *rootViewController;
- (void)commonInit;
- (void)tearDown;
- (void)updateThemeMode;
@end
