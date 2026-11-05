#import <UIKit/UIKit.h>
// Objective-C selectors implemented by the recovered ShimmeringView.swift.
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate14ShimmeringView")))
@interface ICMRecoveredShimmeringView : UIView
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic) BOOL isShimmering;
@property (nonatomic) double shimmeringBeginFadeDuration;
@property (nonatomic) double shimmeringPauseDuration;
@property (nonatomic) double shimmeringOpacity;
@property (nonatomic) double shimmeringAnimationOpacity;
@property (nonatomic) double shimmeringHighlightLength;
@property (nonatomic) double shimmeringSpeed;
@end
@interface ITBLoadingView : UIView {
    BOOL _loading;
    ICMRecoveredShimmeringView *_shimmeringView;
    UIView *_contentView;
    UIImageView *_failureImageView;
}
@property (nonatomic, readonly) BOOL loading;
@property (nonatomic, strong) ICMRecoveredShimmeringView *shimmeringView;
@property (nonatomic, strong) UIView *contentView;
@property (nonatomic, strong) UIImageView *failureImageView;
- (void)isLoading:(BOOL)loading;
- (void)setLoadingViewColorType:(NSUInteger)type;
- (void)setShimmerSpeedForImageWidth:(CGFloat)width;
- (void)handleLoadingFailure;
- (void)cleanup;
+ (UIColor *)darkLoadingViewColor;
+ (UIColor *)lightLoadingViewColor;
@end
