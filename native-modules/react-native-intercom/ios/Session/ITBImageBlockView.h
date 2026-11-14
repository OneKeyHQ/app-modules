#import "ITBBlockView.h"
#import "ITBImageBlockViewDependencies.h"
@interface ITBImageBlockView : ITBBlockView <ITBUploadProgressDelegate> {
    ITBAnimatedImageView *_imageView;
    ITBUploadProgressButton *_progressView;
    UITapGestureRecognizer *_tapOnImageRecognizer;
    ITBLoadingView *_loadingView;
}
@property (nonatomic, strong) ITBAnimatedImageView *imageView;
@property (nonatomic, strong) ITBUploadProgressButton *progressView;
@property (nonatomic, strong) UITapGestureRecognizer *tapOnImageRecognizer;
@property (nonatomic, strong) ITBLoadingView *loadingView;
@end
