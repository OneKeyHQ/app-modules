#import <Foundation/Foundation.h>
@protocol ICMRootViewControllerDelegate <NSObject>
- (void)hideIntercomWindow;
- (void)setWindowIsFullScreen:(BOOL)fullscreen;
- (BOOL)shouldShowInApps;
- (BOOL)shouldShowLauncher;
@end
