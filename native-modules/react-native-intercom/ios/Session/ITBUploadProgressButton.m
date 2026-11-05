// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBUploadProgressButton.h"
#import "ITBImageLoader.h"
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
@implementation ITBUploadProgressButton
@synthesize progress = _progress, progressLayer = _progressLayer;
- (instancetype)init { return [self initWithStrokeColor:UIColor.whiteColor showCancelIcon:YES size:68]; }
- (instancetype)initWithStrokeColor:(UIColor *)color showCancelIcon:(BOOL)showCancel size:(CGFloat)size {
    self = [super initWithFrame:CGRectMake(0, 0, size, size)];
    if (self) {
        if (showCancel) [self setImage:[ITBUploadProgressButton progressBackgroundWithSize:size] forState:UIControlStateNormal];
        self.backgroundColor = UIColor.clearColor;
        CGFloat half = size * 0.5;
        self.progressLayer = [CAShapeLayer layer];
        self.progressLayer.lineWidth = 2;
        UIBezierPath *path = [UIBezierPath bezierPathWithArcCenter:CGPointMake(half, half) radius:half - self.progressLayer.lineWidth * 0.5 startAngle:-1.5707963267948966 endAngle:4.71238898038469 clockwise:YES];
        self.progressLayer.path = path.CGPath;
        self.progressLayer.fillColor = UIColor.clearColor.CGColor;
        self.progressLayer.strokeColor = color.CGColor;
        self.progressLayer.strokeStart = 0;
        self.progressLayer.strokeEnd = 0;
        [self.layer addSublayer:self.progressLayer];
    }
    return self;
}
- (void)setProgress:(double)progress {
    _progress = progress;
    self.progressLayer.strokeEnd = self.progress;
    [self.progressLayer didChangeValueForKey:@"endValue"];
}
+ (UIImage *)progressBackgroundWithSize:(CGFloat)size {
    NSString *key = [NSString stringWithFormat:@"ITB_progressBackgroundImage_%f", size];
    return [[ITBImageLoader defaultLoader] imageForKey:key withGenerationBlock:^{
        UIGraphicsBeginImageContextWithOptions(CGSizeMake(size, size), NO, 0);
        CGContextRef context = UIGraphicsGetCurrentContext();
        UIColor *black = [UIColor colorWithRed:0 green:0 blue:0 alpha:1];
        UIColor *white = [UIColor colorWithRed:1 green:1 blue:1 alpha:1];
        CGContextSaveGState(context);
        CGContextSetAlpha(context, 0.4);
        CGContextBeginTransparencyLayer(context, NULL);
        UIBezierPath *oval = [UIBezierPath bezierPathWithOvalInRect:CGRectMake(0, 0, size, size)];
        oval.usesEvenOddFillRule = YES;
        [black setFill];
        [oval fill];
        CGContextEndTransparencyLayer(context);
        CGContextRestoreGState(context);
        UIBezierPath *first = [UIBezierPath bezierPath];
        [first moveToPoint:CGPointMake(41.96, 42.19)];
        [first addLineToPoint:CGPointMake(25.76, 25.99)];
        first.lineCapStyle = kCGLineCapRound;
        [white setStroke];
        first.lineWidth = 2;
        [first stroke];
        UIBezierPath *second = [UIBezierPath bezierPath];
        [second moveToPoint:CGPointMake(42.14, 25.99)];
        [second addLineToPoint:CGPointMake(25.94, 42.19)];
        second.lineCapStyle = kCGLineCapRound;
        [white setStroke];
        second.lineWidth = 2;
        [second stroke];
        UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        return image;
    }];
}
@end
#pragma clang diagnostic pop
