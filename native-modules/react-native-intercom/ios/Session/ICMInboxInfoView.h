#import "ICMGradientView.h"
#import "ICMErrorView.h"
@interface ICMInboxInfoView : UIView
@property (nonatomic, strong) ICMErrorView *errorView;
@property (nonatomic, strong) ICMGradientView *gradientView;
- (instancetype)initWithTitle:(NSString *)title message:(NSString *)message showRetryButton:(BOOL)retry delegate:(id<ICMErrorViewDelegate>)delegate;
- (void)createGhostItemAtIndex:(int)index;
- (void)createConstraints;
- (void)setAlphaAnimated:(CGFloat)alpha completion:(void (^)(BOOL))completion;
@end
