#import "ICMButton.h"
#import "ICMBlockAppearanceDependencies.h"
#import "ICMFeedbackGenerator.h"
#import "ICMPointerInteraction.h"
#import "UIColor+HSL.h"
@implementation ICMButton
@synthesize tappedAnimationCompleted = _tappedAnimationCompleted;
@synthesize cornerRadius = _cornerRadius;
@synthesize normalStateBackgroundColour = _normalStateBackgroundColour;
- (instancetype)init { return [self initWithTitle:@""]; }
- (instancetype)initWithTitle:(NSString *)title {
    if ((self = [super initWithFrame:CGRectZero])) {
        self.configuration = [UIButtonConfiguration plainButtonConfiguration];
        [self setupTargetActions];
        [self setupTitleAttributes:title];
        if ([self respondsToSelector:@selector(setPointerInteractionEnabled:)]) self.pointerInteractionEnabled = YES;
    }
    return self;
}
- (void)setupTitleAttributes:(NSString *)title {
    UIButtonConfiguration *config = self.configuration;
    config.attributedTitle = [[NSMutableAttributedString alloc] initWithString:title attributes:@{NSFontAttributeName: [ICMUtils fontOfSize:14 weight:1]}];
    self.configuration = config;
}
- (void)setupTargetActions {
    [self addTarget:self action:@selector(buttonTapped) forControlEvents:UIControlEventTouchDown];
    [self addTarget:self action:@selector(touchUpInside) forControlEvents:UIControlEventTouchUpInside];
}
- (void)setCornerRadius:(CGFloat)radius { _cornerRadius = radius; self.configuration.background.cornerRadius = radius; }
- (void)setHighlighted:(BOOL)highlighted {
    BOOL old = self.isHighlighted;
    [super setHighlighted:highlighted];
    if (!highlighted && old && self.tappedAnimationCompleted) [self resetAppearanceWithAnimation];
}
- (void)resetAppearanceWithAnimation {
    [UIView animateWithDuration:0.08 animations:^{
        UIButtonConfiguration *config = self.configuration;
        config.background.backgroundColor = self.normalStateBackgroundColour;
        self.transform = CGAffineTransformIdentity;
        config.background.strokeColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralBorder;
        self.configuration = config;
    }];
}
- (void)configureTappedAppearance {
    UIButtonConfiguration *config = self.configuration;
    self.normalStateBackgroundColour = [config.background.backgroundColor copy];
    self.transform = CGAffineTransformMakeScale(0.95, 0.95);
    config.background.backgroundColor = [self.normalStateBackgroundColour darken:0.05];
    config.background.strokeColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralBorder;
    self.configuration = config;
}
- (void)touchUpInside { [ICMFeedbackGenerator feedbackWithType:0]; }
- (void)buttonTapped {
    self.tappedAnimationCompleted = NO;
    [UIView animateWithDuration:0.12 animations:^{ [self configureTappedAppearance]; } completion:^(BOOL finished) {
        (void)finished;
        if (!self.isHighlighted) [self resetAppearanceWithAnimation];
        self.tappedAnimationCompleted = YES;
    }];
}
- (void)layoutSubviews { [super layoutSubviews]; CGFloat radius = self.cornerRadius; self.configuration.background.cornerRadius = radius; }
- (UIPointerStyle *)pointerInteraction:(UIPointerInteraction *)interaction styleForRegion:(UIPointerRegion *)region {
    (void)region;
    return [ICMPointerInteraction standardLargeButtonPointerStyle:interaction];
}
@end
