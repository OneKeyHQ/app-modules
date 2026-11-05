#import "ITBVideoFileBlockView.h"
#import "ITBVideoBlock.h"
#import "ITBImageBlockViewDependencies.h"
#import "ICMBlockLoader.h"
#import <math.h>
@implementation ITBVideoFileBlockView
@synthesize imageView = _imageView, player = _player, loadedVideoURLString = _loadedVideoURLString;
@synthesize playButtonIcon = _playButtonIcon, currentMetricAction = _currentMetricAction, loadingView = _loadingView;
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.clipsToBounds = YES;
        self.accessibilityLabel = @"intercom video file block";
        self.userInteractionEnabled = YES;
        [self configureView];
        [NSNotificationCenter.defaultCenter addObserver:self selector:@selector(addPlaybackCompletedMetric) name:AVPlayerItemDidPlayToEndTimeNotification object:nil];
    }
    return self;
}
- (void)configureView { [self createImageView]; [self createLoadingView]; [self createPlayButton]; }
- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat padding = [(ITBVideoFileBlock *)self.block padding];
    CGFloat width = fma(padding, -2, CGRectGetWidth(self.frame));
    CGFloat height = fma(padding, -2, CGRectGetHeight(self.frame));
    CGFloat x = ceilf((float)(CGRectGetWidth(self.bounds) * .5 - CGRectGetWidth(CGRectMake(padding, padding, width, height)) * .5));
    CGRect frame = CGRectMake(x, padding, width, height);
    self.imageView.frame = frame;
    self.playButtonIcon.frame = frame;
    self.loadingView.frame = frame;
    CGFloat centerX = CGRectGetWidth(frame) * .5;
    CGFloat centerY = CGRectGetHeight(frame) * .5;
    self.playButtonIcon.center = CGPointMake(centerX, centerY);
}
- (void)cleanup {
    [self.imageView cancelImageLoad];
    self.imageView.image = nil;
    self.imageView.animatedImage = nil;
    self.imageView.alpha = 1;
    self.backgroundColor = UIColor.clearColor;
    [self.loadingView cleanup];
}
- (void)prepareForReuse { [super prepareForReuse]; [self cleanup]; }
- (void)dealloc {
    [self cleanup];
    [NSNotificationCenter.defaultCenter removeObserver:self name:AVPlayerItemDidPlayToEndTimeNotification object:nil];
    [self.player removeObserver:self forKeyPath:@"status"];
}
- (void)createImageView {
    self.imageView = [ITBAnimatedImageView new];
    self.imageView.contentMode = UIViewContentModeScaleAspectFill;
    UIColor *clear = UIColor.clearColor;
    self.imageView.backgroundColor = clear;
    self.imageView.clipsToBounds = YES;
    self.imageView.userInteractionEnabled = YES;
    [self addSubview:self.imageView];
}
- (void)createLoadingView { self.loadingView = [ITBLoadingView new]; [self addSubview:self.loadingView]; }
- (void)createPlayButton {
    self.playButtonIcon = [ITBVideoFilePlayButton new];
    [self.playButtonIcon addTarget:self action:@selector(playVideo) forControlEvents:UIControlEventTouchUpInside];
    ITBAnimatedImageView *image = self.imageView;
    [image addSubview:self.playButtonIcon];
}
- (void)playVideo { [self configureAVPlayer]; [self presentAVPlayerViewController]; }
- (void)configureAVPlayer {
    ITBVideoFileBlock *block = (ITBVideoFileBlock *)self.block;
    if ([self shouldReplaceAVPlayerWithURL:block.url]) {
        self.player = [AVPlayer playerWithURL:[NSURL URLWithString:block.url]];
        [self.player addObserver:self forKeyPath:@"status" options:0 context:nil];
    }
    self.loadedVideoURLString = block.url;
}
- (BOOL)shouldReplaceAVPlayerWithURL:(NSString *)url {
    if (!self.player) return YES;
    if (![self.loadedVideoURLString isEqualToString:url]) return YES;
    return self.player.status == AVPlayerStatusFailed;
}
- (void)presentAVPlayerViewController {
    if (!self.player) return;
    AVPlayerViewController *controller = [AVPlayerViewController new];
    controller.player = self.player;
    controller.view.accessibilityLabel = @"intercom video player";
    controller.modalPresentationStyle = UIModalPresentationFullScreen;
    UIViewController *presenter = [(NSObject *)self.delegate viewControllerForModalPresentation];
    [presenter presentViewController:controller animated:YES completion:^{
        double current = CMTimeGetSeconds(self.player.currentTime);
        double duration = CMTimeGetSeconds(self.player.currentItem.duration);
        if (current == duration) [self.player.currentItem seekToTime:kCMTimeZero completionHandler:nil];
        [self.player play];
        [self addPlaybackStartedMetric];
    }];
}
- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSKeyValueChangeKey,id> *)change context:(void *)context {
    (void)change; (void)context;
    if (object == self.player && [keyPath isEqualToString:@"status"] && self.player.status == AVPlayerStatusFailed) [self addPlaybackFailedMetric];
}
- (void)setCornerRadiuses:(CGFloat)radius {
    self.layer.cornerRadius = radius;
    self.loadingView.layer.cornerRadius = radius;
}
- (void)loadVideoThumbnail:(ITBVideoFileBlock *)block {
    __weak typeof(self) weakSelf = self;
    [self.imageView loadImageWithURLString:block.thumbnailUrl actionsBeforeNetworkLoad:^{ [self startVideoThumbnailLoadingAnimations]; } completion:^{
        if (weakSelf.imageView.image) {
            [UIView animateWithDuration:.4 delay:0 options:0 animations:^{ [weakSelf endVideoThumbnailLoadingAnimations]; } completion:nil];
        } else [weakSelf.loadingView handleLoadingFailure];
    }];
}
- (void)startVideoThumbnailLoadingAnimations { self.imageView.alpha = 0; [self.loadingView isLoading:YES]; }
- (void)endVideoThumbnailLoadingAnimations { [self.loadingView isLoading:NO]; self.imageView.alpha = 1; }
- (void)configureWithBlock:(ITBVideoFileBlock *)block {
    [super configureWithBlock:block];
    [self setCornerRadiuses:block.cornerRadius];
    ITBLoadingView *loading = self.loadingView;
    [loading setLoadingViewColorType:block.loadingViewColorType];
    [self loadVideoThumbnail:block];
}
+ (CGSize)estimateSizeForBlock:(ITBVideoFileBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    (void)constraintWidth;
    CGFloat padding = block.padding;
    CGFloat height = (width * 9) * .0625;
    return CGSizeMake(width, fma(padding, 2, height));
}
- (NSDictionary *)metricPayload {
    if (!self.currentMetricAction.length || !self.loadedVideoURLString.length) return nil;
    return @{@"action":self.currentMetricAction, @"object":@"video_auto_message", @"place":@"in_app", @"metadata":@{@"video_url":self.loadedVideoURLString}};
}
- (ITBIntentType)intentType { return 5; }
- (void)addPlaybackFailedMetric {
    self.currentMetricAction = @"failed";
    NSObject *opener = (NSObject *)self.intentOpener;
    [opener handleIntent:self forBlock:self.block fromView:self];
}
- (void)addPlaybackStartedMetric {
    self.currentMetricAction = @"started";
    NSObject *opener = (NSObject *)self.intentOpener;
    [opener handleIntent:self forBlock:self.block fromView:self];
}
- (void)addPlaybackCompletedMetric {
    self.currentMetricAction = @"completed";
    NSObject *opener = (NSObject *)self.intentOpener;
    [opener handleIntent:self forBlock:self.block fromView:self];
}
@end
@implementation ITBVideoFilePlayButton
@synthesize tappedAnimationCompleted = _tappedAnimationCompleted;
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self addTarget:self action:@selector(buttonTapped) forControlEvents:UIControlEventTouchDown];
        UIImage *image = [[UIImage imageNamed:@"videoFilePlayIcon" inBundle:[ITBBlockUtils loadBlocksAssetsBundle] compatibleWithTraitCollection:nil] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
        [self setImage:image forState:UIControlStateNormal];
        self.imageView.backgroundColor = UIColor.whiteColor;
        self.contentMode = UIViewContentModeCenter;
        self.imageView.layer.cornerRadius = 24;
        CGColorRef color = UIColor.blackColor.CGColor;
        self.imageView.layer.shadowColor = color;
        self.imageView.layer.shadowOffset = CGSizeMake(0, 1);
        self.imageView.layer.shadowRadius = 3;
        self.imageView.layer.shadowOpacity = .3f;
    }
    return self;
}
- (void)setHighlighted:(BOOL)highlighted {
    BOOL previous = self.highlighted;
    [super setHighlighted:highlighted];
    if (!highlighted && previous && self.tappedAnimationCompleted) [self resetAppearanceWithAnimation];
}
- (void)resetAppearanceWithAnimation {
    [UIView animateWithDuration:.08 animations:^{ self.transform = CGAffineTransformIdentity; }];
}
- (void)configureTappedAppearance { self.transform = CGAffineTransformMakeScale(.9, .9); }
- (void)buttonTapped {
    self.tappedAnimationCompleted = NO;
    [UIView animateWithDuration:.08 animations:^{ [self configureTappedAppearance]; } completion:^(BOOL finished) {
        (void)finished;
        if (!self.highlighted) [self resetAppearanceWithAnimation];
        self.tappedAnimationCompleted = YES;
    }];
}
@end
