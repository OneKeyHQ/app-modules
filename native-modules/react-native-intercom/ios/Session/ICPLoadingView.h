#import <UIKit/UIKit.h>
@interface ICPLoadingView : UIView {
    UIImageView *_spinnerImageView;
}
@property (nonatomic, strong) UIImageView *spinnerImageView;
- (instancetype)initWithLoadingImage:(UIImage *)image tintColor:(UIColor *)color;
- (void)startSpinning;
- (void)stopSpinning;
@end
