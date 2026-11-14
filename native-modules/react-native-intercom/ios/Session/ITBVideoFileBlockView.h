#import "ITBBlockView.h"
#import "ITBMessengerCardIntents.h"
#import "ITBAnimatedImageView.h"
#import "ITBLoadingView.h"
#import <AVKit/AVKit.h>
@interface ITBVideoFilePlayButton : UIButton {
    BOOL _tappedAnimationCompleted;
}
@property (nonatomic) BOOL tappedAnimationCompleted;
- (void)resetAppearanceWithAnimation;
- (void)configureTappedAppearance;
- (void)buttonTapped;
@end
@interface ITBVideoFileBlockView : ITBBlockView <ITBMetricEventIntent> {
    ITBAnimatedImageView *_imageView;
    AVPlayer *_player;
    NSString *_loadedVideoURLString;
    ITBVideoFilePlayButton *_playButtonIcon;
    NSString *_currentMetricAction;
    ITBLoadingView *_loadingView;
}
@property (nonatomic, strong) ITBAnimatedImageView *imageView;
@property (nonatomic, strong) AVPlayer *player;
@property (nonatomic, copy) NSString *loadedVideoURLString;
@property (nonatomic, strong) ITBVideoFilePlayButton *playButtonIcon;
@property (nonatomic, copy) NSString *currentMetricAction;
@property (nonatomic, strong) ITBLoadingView *loadingView;
- (void)configureView;
- (void)cleanup;
- (void)createImageView;
- (void)createLoadingView;
- (void)createPlayButton;
- (void)playVideo;
- (void)configureAVPlayer;
- (BOOL)shouldReplaceAVPlayerWithURL:(NSString *)url;
- (void)presentAVPlayerViewController;
- (void)setCornerRadiuses:(CGFloat)radius;
- (void)loadVideoThumbnail:(id)block;
- (void)startVideoThumbnailLoadingAnimations;
- (void)endVideoThumbnailLoadingAnimations;
- (void)addPlaybackFailedMetric;
- (void)addPlaybackStartedMetric;
- (void)addPlaybackCompletedMetric;
@end
