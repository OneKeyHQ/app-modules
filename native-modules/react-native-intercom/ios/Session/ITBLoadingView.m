#import "ITBLoadingView.h"
#import "ICMBlockLoader.h"
@implementation ITBLoadingView
@synthesize loading = _loading, shimmeringView = _shimmeringView, contentView = _contentView, failureImageView = _failureImageView;
- (instancetype)init {
    self = [super init];
    if (self) { [self createLoadingContentView]; [self createShimmeringView]; }
    return self;
}
- (void)createLoadingContentView {
    self.contentView = [UIView new];
    self.contentView.translatesAutoresizingMaskIntoConstraints = NO;
    self.contentView.accessibilityIdentifier = @"contentView";
    self.contentView.backgroundColor = [ITBLoadingView darkLoadingViewColor];
}
- (void)createShimmeringView {
    self.shimmeringView = [[ICMRecoveredShimmeringView alloc] init];
    self.shimmeringView.accessibilityIdentifier = @"shimmerView";
    self.shimmeringView.translatesAutoresizingMaskIntoConstraints = NO;
    self.shimmeringView.shimmeringBeginFadeDuration = 0.1;
    self.shimmeringView.shimmeringPauseDuration = 0.1;
    self.shimmeringView.shimmeringOpacity = 1;
    self.shimmeringView.shimmeringAnimationOpacity = 0.2;
    self.shimmeringView.shimmeringHighlightLength = 0.99;
    UIView *content = self.contentView;
    self.shimmeringView.contentView = content;
    self.shimmeringView.alpha = 1;
    [self addSubview:self.shimmeringView];
    [self layoutShimmeringView:self.shimmeringView];
}
- (void)layoutShimmeringView:(UIView *)view {
    [view.leadingAnchor constraintEqualToAnchor:self.leadingAnchor].active = YES;
    [view.trailingAnchor constraintEqualToAnchor:self.trailingAnchor].active = YES;
    [view.topAnchor constraintEqualToAnchor:self.topAnchor].active = YES;
    [view.bottomAnchor constraintEqualToAnchor:self.bottomAnchor].active = YES;
}
- (void)layoutFailureImageView {
    [self.failureImageView.centerXAnchor constraintEqualToAnchor:self.centerXAnchor].active = YES;
    [self.failureImageView.centerYAnchor constraintEqualToAnchor:self.centerYAnchor].active = YES;
    [self.failureImageView.heightAnchor constraintEqualToConstant:32].active = YES;
    [self.failureImageView.widthAnchor constraintEqualToConstant:32].active = YES;
}
- (void)isLoading:(BOOL)loading {
    _loading = loading;
    self.shimmeringView.isShimmering = loading;
    self.alpha = loading ? 1 : 0;
}
- (void)setShimmerSpeedForImageWidth:(CGFloat)width { self.shimmeringView.shimmeringSpeed = (width / 100) * 60; }
- (void)handleLoadingFailure {
    self.shimmeringView.isShimmering = NO;
    [UIView animateWithDuration:0.4 delay:0 options:0 animations:^{
        UIImage *image = [[UIImage imageNamed:@"image-loading-fail" inBundle:[ITBBlockUtils loadBlocksAssetsBundle] compatibleWithTraitCollection:nil] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal];
        self.failureImageView = [[UIImageView alloc] initWithImage:image];
        self.failureImageView.translatesAutoresizingMaskIntoConstraints = NO;
        [self.contentView addSubview:self.failureImageView];
        [self layoutFailureImageView];
    } completion:nil];
}
- (void)setLoadingViewColorType:(NSUInteger)type {
    UIColor *color = type == 1 ? [ITBLoadingView lightLoadingViewColor] : [ITBLoadingView darkLoadingViewColor];
    self.contentView.backgroundColor = color;
}
+ (UIColor *)darkLoadingViewColor { return [UIColor colorWithRed:0.4 green:0.4 blue:0.4 alpha:0.15]; }
+ (UIColor *)lightLoadingViewColor { return [UIColor colorWithRed:1 green:1 blue:1 alpha:0.15]; }
- (void)cleanup {
    [self isLoading:NO];
    [self.failureImageView removeFromSuperview];
    self.failureImageView = nil;
}
@end
