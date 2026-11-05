// Original 19.9.1 UIWindow behavior; true Root/theme dependencies remain required.
#import "ICMWindow.h"
#import "ICMWindowDependencies.h"
#import "ICMConfig.h"
#import "ICMConfigUIModels.h"
#import "../Identity/ICMLogger.h"
@implementation ICMWindow
@dynamic rootViewController;
@synthesize fullscreen = _fullscreen;
- (instancetype)initWithWindowScene:(UIWindowScene *)scene {
    if ((self = [super initWithWindowScene:scene])) [self commonInit];
    return self;
}
- (instancetype)init {
    if ((self = [super init])) [self commonInit];
    return self;
}
- (void)commonInit {
    if (CGSizeEqualToSize(self.frame.size, CGSizeZero)) {
        if (self.windowScene) {
            if (@available(iOS 26.0, *)) self.frame = self.windowScene.effectiveGeometry.coordinateSpace.bounds;
            else self.frame = self.windowScene.coordinateSpace.bounds;
        } else self.frame = [(UIWindow *)[[ICMUtils fetchApplicationWindows] firstObject] bounds];
        NSLog(@"[");
    }
    self.windowLevel = UIWindowLevelNormal + 0.1;
    self.backgroundColor = [UIColor clearColor];
    self.tintColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryIcon;
    self.clipsToBounds = YES;
    self.accessibilityIdentifier = @"intercom window";
    [self updateThemeMode];
    if ([[NSUserDefaults standardUserDefaults] boolForKey:@"isDarkModeAllowed"]) self.overrideUserInterfaceStyle = UIUserInterfaceStyleUnspecified;
}
- (void)tearDown {
    self.hidden = YES;
    [self.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
    [super setRootViewController:nil];
    [self removeFromSuperview];
}
- (void)setRootViewController:(ICMRootViewController *)controller {
    if ([controller isKindOfClass:[ICMRootViewController class]]) [super setRootViewController:controller];
    else [ICMLogger log:@"Tried to set rootViewController on Intercom's window. You probably meant to do this on your app's window.                             Try using UIApplication.shared.delegate?.window or [UIApplication sharedApplication].delegate.window instead." type:OS_LOG_TYPE_ERROR];
}
- (void)setFullscreen:(BOOL)fullscreen {
    _fullscreen = fullscreen;
    if (fullscreen) {
        if (!self.isKeyWindow) [self makeKeyWindow];
        self.windowLevel = UIWindowLevelNormal;
        self.accessibilityViewIsModal = YES;
    } else {
        UIWindow *host = self.rootViewController.hostAppWindow;
        if (!host.isKeyWindow) [host makeKeyWindow];
        self.windowLevel = UIWindowLevelNormal + 0.10000000149011612;
        self.accessibilityViewIsModal = NO;
    }
}
- (void)updateThemeMode {
    if ([ICMConfig sharedConfig].modules.customizationConfig.effectiveThemeMode == 1) self.overrideUserInterfaceStyle = UIUserInterfaceStyleDark;
    else if ([ICMConfig sharedConfig].modules.customizationConfig.effectiveThemeMode == 0) self.overrideUserInterfaceStyle = UIUserInterfaceStyleLight;
    else if ([ICMConfig sharedConfig].modules.customizationConfig.effectiveThemeMode == 2) self.overrideUserInterfaceStyle = UIUserInterfaceStyleUnspecified;
}
- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
    UIView *view = [super hitTest:point withEvent:event];
    return [self.rootViewController shouldHandleTouchOnView:view] ? view : nil;
}
@end
