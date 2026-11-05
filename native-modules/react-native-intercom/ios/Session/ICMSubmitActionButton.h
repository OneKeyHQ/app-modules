// Source recovery modifications: see docs/TICKETS_RECOVERY.md.
#import "ICMActionButton.h"
@interface ICMSubmitActionButton : ICMActionButton
@property (nonatomic, strong) UIImage *image;
- (void)showNormalState;
- (void)showDisabledState;
- (void)showLoadingState;
- (void)resetButtonState;
- (void)buttonReleased:(id)sender event:(UIEvent *)event;
@end
