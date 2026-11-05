// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ICPLoadingView.h"
#import <math.h>
@implementation ICPLoadingView
@synthesize spinnerImageView = _spinnerImageView;
- (instancetype)initWithLoadingImage:(UIImage *)image tintColor:(UIColor *)color {
    self = [super init];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.hidden = YES;
        self.userInteractionEnabled = NO;
        self.spinnerImageView = [[UIImageView alloc] initWithImage:image];
        self.spinnerImageView.tintColor = color;
        [self addSubview:self.spinnerImageView];
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat x = CGRectGetMidX(self.bounds);
    CGFloat y = CGRectGetMidY(self.bounds);
    self.spinnerImageView.center = CGPointMake(x, y);
}
- (CGSize)intrinsicContentSize { return self.spinnerImageView.bounds.size; }
- (void)startSpinning {
    self.hidden = NO;
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:@"transform.rotation"];
    animation.cumulative = YES;
    animation.duration = 0.4000000059604645;
    animation.repeatCount = INFINITY;
    animation.fromValue = @0;
    animation.toValue = @(M_PI);
    [self.spinnerImageView.layer addAnimation:animation forKey:@"spin"];
}
- (void)stopSpinning {
    [self.spinnerImageView.layer removeAnimationForKey:@"spin"];
    self.hidden = YES;
}
@end
