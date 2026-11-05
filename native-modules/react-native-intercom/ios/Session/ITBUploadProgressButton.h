#import <UIKit/UIKit.h>
@interface ITBUploadProgressButton : UIButton {
    double _progress;
    CAShapeLayer *_progressLayer;
}
@property (nonatomic) double progress;
@property (nonatomic, strong) CAShapeLayer *progressLayer;
- (instancetype)initWithStrokeColor:(UIColor *)color showCancelIcon:(BOOL)showCancel size:(CGFloat)size;
+ (UIImage *)progressBackgroundWithSize:(CGFloat)size;
@end
