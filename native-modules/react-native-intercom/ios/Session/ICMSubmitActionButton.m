#import "ICMSubmitActionButton.h"
#import "ICMBlockAppearanceDependencies.h"
#import <QuartzCore/QuartzCore.h>
#import <math.h>
@interface ICMRecoveredTheme (OriginalSubmitButtonColors)
@property (nonatomic, readonly) UIColor *textMutedExtra;
@end
@implementation ICMSubmitActionButton
- (instancetype)init {
    self = [super initFilledButtonWithTitle:@""];
    [self addTarget:self action:@selector(buttonReleased:event:) forControlEvents:UIControlEventTouchUpInside];
    [self showNormalState];
    return self;
}
- (void)setEnabled:(BOOL)enabled {
    [super setEnabled:enabled];
    if (enabled) [self showNormalState]; else [self showDisabledState];
}
- (void)showNormalState {
    UIColor *background = [ICMRecoveredDefaultThemeProvider shared].theme.primaryContainer;
    UIColor *foreground = [ICMRecoveredDefaultThemeProvider shared].theme.primaryOnContainer;
    [self configureForStyle:0 backgroundColor:background foregroundColor:foreground];
    [self.imageView.layer removeAnimationForKey:@"spin"];
    [self setImage:self.image forState:UIControlStateNormal];
    self.userInteractionEnabled = YES;
}
- (void)showDisabledState {
    UIColor *background = [ICMRecoveredDefaultThemeProvider shared].theme.textMutedExtra;
    UIColor *foreground = [ICMRecoveredDefaultThemeProvider shared].theme.textMuted;
    [self configureForStyle:0 backgroundColor:background foregroundColor:foreground];
    self.layer.borderWidth = 0;
}
- (void)showLoadingState {
    UIImage *image = self.image;
    [self setImage:[ICMAssets buttonLoadingImage] forState:UIControlStateNormal];
    self.image = image;
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:@"transform.rotation"];
    animation.cumulative = YES;
    animation.duration = 0.4000000059604645;
    animation.repeatCount = INFINITY;
    animation.fromValue = @0.0f;
    animation.toValue = @(M_PI);
    [self.imageView.layer addAnimation:animation forKey:@"spin"];
    self.userInteractionEnabled = NO;
}
- (void)setImage:(UIImage *)image forState:(UIControlState)state {
    [super setImage:image forState:state];
    self.image = image;
}
- (void)resetButtonState { [self showNormalState]; }
- (void)buttonReleased:(id)sender event:(UIEvent *)event {
    (void)sender;
    (void)event;
    [self showLoadingState];
}
@end
