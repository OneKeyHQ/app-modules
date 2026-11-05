#import "ICMSubmitButton.h"
#import "ICMFormInputDependencies.h"
#import <QuartzCore/QuartzCore.h>

@implementation ICMSubmitButton
- (instancetype)init {
    self = [super init];
    if (self) {
        self.frame = CGRectMake(0, 0, 40, 40);
        [self addTarget:self action:@selector(buttonTapped) forControlEvents:UIControlEventTouchDown];
        [self addTarget:self action:@selector(buttonReleased:event:) forControlEvents:UIControlEventTouchUpInside];
        [self addLeadingBorder];
    }
    return self;
}
- (void)addLeadingBorder {
    self.leadingBorder = [UIView new];
    self.leadingBorder.translatesAutoresizingMaskIntoConstraints = NO;
    [self addSubview:self.leadingBorder];
    [self.leadingBorder.widthAnchor constraintEqualToConstant:1].active = YES;
    self.leadingBorder.backgroundColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralBorderEmphasis;
    [self.leadingBorder.leadingAnchor constraintEqualToAnchor:self.leadingAnchor].active = YES;
    [self.leadingBorder.topAnchor constraintEqualToAnchor:self.topAnchor].active = YES;
    [self.leadingBorder.bottomAnchor constraintEqualToAnchor:self.bottomAnchor].active = YES;
}
- (void)setEnabled:(BOOL)enabled {
    [super setEnabled:enabled];
    if (enabled) [self showEnabledState];
    else [self showDisabledState];
}
- (void)showEnabledState {
    [self setBackgroundImage:[[ICMAssets submitButtonBackgroundImage] imageFlippedForRightToLeftLayoutDirection] forState:UIControlStateNormal];
    [self setImage:[[ICMAssets submitImage] imageFlippedForRightToLeftLayoutDirection] forState:UIControlStateNormal];
    self.imageView.tintColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryOnContainer;
    self.tintColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryContainer;
    self.leadingBorder.hidden = NO;
    self.accessibilityTraits = UIAccessibilityTraitButton;
    self.accessibilityLabel = [[ICMTranslationManager messenger] translationForKey:@"submit"];
}
- (void)showSubmittedState {
    [self setBackgroundImage:[ICMAssets submitButtonBackgroundImageClear] forState:UIControlStateNormal];
    [self setImage:[ICMAssets greenCheckImage] forState:UIControlStateNormal];
    self.tintColor = UIColor.clearColor;
    self.imageView.tintColor = [ICMRecoveredDefaultThemeProvider shared].theme.successContainer;
    [self.imageView.layer removeAnimationForKey:@"spin"];
    self.leadingBorder.hidden = YES;
    self.accessibilityTraits = UIAccessibilityTraitNone;
    self.accessibilityLabel = [[ICMTranslationManager messenger] translationForKey:@"checklist_completed"];
}
- (void)showDisabledState {
    [self setBackgroundImage:[[ICMAssets submitButtonBackgroundImage] imageFlippedForRightToLeftLayoutDirection] forState:UIControlStateNormal];
    [self setImage:[[ICMAssets submitImage] imageFlippedForRightToLeftLayoutDirection] forState:UIControlStateNormal];
    self.imageView.tintColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryText;
    self.tintColor = [ICMRecoveredDefaultThemeProvider shared].theme.neutralContainerEmphasis;
    self.leadingBorder.hidden = NO;
    self.accessibilityTraits = UIAccessibilityTraitButton;
    self.accessibilityLabel = [[ICMTranslationManager messenger] translationForKey:@"submit"];
}
- (void)showLoadingState {
    [self setImage:[ICMAssets buttonLoadingImage] forState:UIControlStateNormal];
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:@"transform.rotation"];
    animation.cumulative = YES;
    animation.duration = 0.4000000059604645;
    animation.repeatCount = INFINITY;
    animation.fromValue = @0;
    animation.toValue = @6.283185307179586;
    [self.imageView.layer addAnimation:animation forKey:@"spin"];
    self.leadingBorder.hidden = YES;
    self.accessibilityTraits = UIAccessibilityTraitNone;
    self.accessibilityLabel = [[ICMTranslationManager messenger] translationForKey:@"loading"];
}
- (void)resetButtonState {
    [self showEnabledState];
    [self.imageView.layer removeAnimationForKey:@"spin"];
}
- (void)configureTappedAppearance {
    self.tintColor = [[ICMRecoveredDefaultThemeProvider shared].theme.primaryContainer colorWithAlphaComponent:1.2];
}
- (void)resetAppearanceWithAnimation {
    [UIView animateWithDuration:0.08 animations:^{ self.tintColor = [ICMRecoveredDefaultThemeProvider shared].theme.primaryContainer; }];
}
- (void)setHighlighted:(BOOL)highlighted {
    BOOL previous = self.highlighted;
    [super setHighlighted:highlighted];
    if (!highlighted && previous && self.tappedAnimationCompleted) [self resetAppearanceWithAnimation];
}
- (void)buttonTapped {
    self.tappedAnimationCompleted = NO;
    [UIView animateWithDuration:0.08 animations:^{ [self configureTappedAppearance]; }
        completion:^(BOOL finished) {
            (void)finished;
            if (!self.highlighted) [self resetAppearanceWithAnimation];
            self.tappedAnimationCompleted = YES;
        }];
}
- (void)buttonReleased:(UIButton *)sender event:(UIEvent *)event {
    [self provideHapticFeedback:sender event:event];
    [self showLoadingState];
}
- (void)provideHapticFeedback:(UIButton *)sender event:(UIEvent *)event {
    CGPoint point = [[event.allTouches anyObject] locationInView:sender];
    if (CGRectContainsPoint(sender.bounds, point)) [ICMFeedbackGenerator feedbackWithType:0];
}
@end
