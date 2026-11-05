#import "ICMTeammatePresenceView.h"
#import "ICMAvatarDependencies.h"
@implementation ICMTeammatePresenceView
- (instancetype)initWithDiameter:(CGFloat)diameter border:(CGFloat)border {
    self = [super initWithFrame:CGRectMake(0, 0, diameter, diameter)];
    if (self) {
        self.userInteractionEnabled = NO;
        self.layer.cornerRadius = diameter * .5;
        self.layer.borderWidth = border;
        self.layer.borderColor = UIColor.whiteColor.CGColor;
    }
    return self;
}
- (void)setActive:(BOOL)active {
    _active = active;
    if (active) {
        self.accessibilityIdentifier = @"intercom active admin";
        self.backgroundColor = ICMRecoveredDefaultThemeProvider.shared.theme.successFill;
    } else {
        self.accessibilityIdentifier = @"intercom away admin";
        self.backgroundColor = ICMRecoveredDefaultThemeProvider.shared.theme.warningFill;
    }
}
- (void)setBorderColor:(UIColor *)color { _borderColor = color; self.layer.borderColor = _borderColor.CGColor; }
@end
