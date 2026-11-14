#import <UIKit/UIKit.h>
@interface ICMButton : UIButton {
    BOOL _tappedAnimationCompleted;
    CGFloat _cornerRadius;
    UIColor *_normalStateBackgroundColour;
}
@property (nonatomic) BOOL tappedAnimationCompleted;
@property (nonatomic) CGFloat cornerRadius;
@property (nonatomic, strong) UIColor *normalStateBackgroundColour;
- (instancetype)initWithTitle:(NSString *)title;
- (void)setupTitleAttributes:(NSString *)title;
- (void)setupTargetActions;
- (void)resetAppearanceWithAnimation;
- (void)configureTappedAppearance;
- (void)touchUpInside;
- (void)buttonTapped;
- (UIPointerStyle *)pointerInteraction:(UIPointerInteraction *)interaction styleForRegion:(UIPointerRegion *)region;
@end
