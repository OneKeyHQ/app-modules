// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import <UIKit/UIKit.h>

@interface ICMSubmitButton : UIButton
@property (nonatomic) BOOL tappedAnimationCompleted;
@property (nonatomic) BOOL fullWidth;
@property (nonatomic, strong) UIView *leadingBorder;
- (void)addLeadingBorder;
- (void)showEnabledState;
- (void)showSubmittedState;
- (void)showDisabledState;
- (void)showLoadingState;
- (void)resetButtonState;
- (void)configureTappedAppearance;
- (void)resetAppearanceWithAnimation;
- (void)buttonTapped;
- (void)buttonReleased:(UIButton *)sender event:(UIEvent *)event;
- (void)provideHapticFeedback:(UIButton *)sender event:(UIEvent *)event;
@end
