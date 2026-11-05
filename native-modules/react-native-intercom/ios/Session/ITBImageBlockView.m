// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBImageBlockView.h"
#import "ITBImageLoader.h"
#import <math.h>

@implementation ITBImageBlockView
@synthesize imageView = _imageView, progressView = _progressView;
@synthesize tapOnImageRecognizer = _tapOnImageRecognizer, loadingView = _loadingView;
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.clipsToBounds = YES;
        [self createImageView];
        [self createTapGestureRecognizer];
        [self createLoadingView];
        self.accessibilityIdentifier = @"intercom image";
        self.isAccessibilityElement = YES;
        self.accessibilityLabel = @"Image";
    }
    return self;
}
- (void)createImageView {
    self.imageView = [ITBAnimatedImageView new];
    self.imageView.contentMode = UIViewContentModeScaleAspectFit;
    self.imageView.backgroundColor = UIColor.clearColor;
    self.imageView.clipsToBounds = YES;
    self.imageView.translatesAutoresizingMaskIntoConstraints = NO;
    [self addSubview:self.imageView];
}
- (void)createTapGestureRecognizer {
    self.tapOnImageRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(didTapOnImage:)];
    [self.imageView addGestureRecognizer:self.tapOnImageRecognizer];
    self.imageView.userInteractionEnabled = YES;
}
- (void)createLoadingView {
    self.loadingView = [ITBLoadingView new];
    self.loadingView.translatesAutoresizingMaskIntoConstraints = NO;
    [self addSubview:self.loadingView];
}
- (void)layoutSubviews {
    [super layoutSubviews];
    self.progressView.center = self.imageView.center;
}
- (void)adjustBlockSizeForImage:(UIImage *)image {
    ITBImageBlock *block = (ITBImageBlock *)self.block;
    if (block.width != image.size.width || block.height != image.size.height) {
        block.width = image.size.width;
        block.height = image.size.height;
        if (self.delegate && [(id)self.delegate respondsToSelector:@selector(reloadRequiredForBlock:)]) {
            [(id)self.delegate reloadRequiredForBlock:self.block];
        }
    }
}
- (void)updateLayoutForView:(UIView *)view layoutConstraints:(ITBImageBlock *)block {
    if (CGSizeEqualToSize(block.imageSize, CGSizeZero)) {
        CGSize size = CGSizeMake(block.width, block.height);
        CGFloat width = [UIScreen mainScreen].bounds.size.width;
        block.imageSize = [ITBImageUtils imageDisplaySizeFromSize:size withinWidth:fma(block.padding, -2, width)];
    }
    [view.centerYAnchor constraintEqualToAnchor:self.centerYAnchor].active = YES;
    [view.centerXAnchor constraintEqualToAnchor:self.centerXAnchor].active = YES;
    [view.widthAnchor constraintLessThanOrEqualToAnchor:self.widthAnchor constant:-2 * block.padding].active = YES;
    [view.heightAnchor constraintEqualToAnchor:self.heightAnchor constant:-2 * block.padding].active = YES;
    [view setContentHuggingPriority:752 forAxis:UILayoutConstraintAxisVertical];
    [view setContentHuggingPriority:752 forAxis:UILayoutConstraintAxisHorizontal];
    NSLayoutConstraint *height = [self.imageView.heightAnchor constraintEqualToConstant:block.height];
    height.priority = 300;
    height.active = YES;
    CGFloat ratio;
    if (block.imageSize.width != 0) {
        CGFloat imageHeight = block.imageSize.height;
        ratio = imageHeight / block.imageSize.width;
    } else ratio = 1;
    NSLayoutConstraint *aspect = [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:view attribute:NSLayoutAttributeWidth multiplier:ratio constant:0];
    aspect.priority = 751;
    aspect.active = YES;
}
- (void)applyImageViewContraints:(ITBImageBlock *)block {
    [self updateLayoutForView:self.imageView layoutConstraints:block];
    [self.loadingView.centerXAnchor constraintEqualToAnchor:self.imageView.centerXAnchor].active = YES;
    [self.loadingView.centerYAnchor constraintEqualToAnchor:self.imageView.centerYAnchor].active = YES;
    [self.loadingView.heightAnchor constraintEqualToAnchor:self.imageView.heightAnchor].active = YES;
    [self.loadingView.widthAnchor constraintEqualToAnchor:self.imageView.widthAnchor].active = YES;
}
- (void)didTapOnImage:(__unused UITapGestureRecognizer *)recognizer {
    ITBImageBlock *block = (ITBImageBlock *)self.block;
    if (block.linkUrl.length) {
        [(id)self.intentOpener handleIntent:block forBlock:block fromView:self];
    } else if ((self.imageView.image || self.imageView.animatedImage) &&
               ![(ITBImageBlock *)self.block upload] &&
               [(id)self.delegate respondsToSelector:@selector(viewControllerForModalPresentation)] &&
               [(id)self.delegate viewControllerForModalPresentation]) {
        ITBImagePreviewMetadata *metadata = [ITBImagePreviewMetadata new];
        metadata.sourceView = self;
        UIImage *cover = self.imageView.animatedImage.coverImage;
        metadata.previewImage = cover ?: self.imageView.image;
        metadata.presentingViewController = [(id)self.delegate viewControllerForModalPresentation];
        [(id)self.imagePreviewHandler blockView:self showImagePreviewWithMetadata:metadata];
    }
}
- (void)uploadCancelled { [(ITBImageBlock *)self.block upload].cancelled = YES; }
- (void)loadImage:(ITBImageBlock *)block {
    __weak ITBImageBlockView *weakSelf = self;
    [self.imageView loadImageWithURLString:block.url actionsBeforeNetworkLoad:^{
        [self startImageLoadingAnimations];
    } completion:^{
        if (weakSelf.imageView.image || weakSelf.imageView.animatedImage) {
            if (weakSelf.imageView.animatedImage) {
                [weakSelf adjustBlockSizeForImage:weakSelf.imageView.animatedImage.coverImage];
            } else {
                [weakSelf adjustBlockSizeForImage:weakSelf.imageView.image];
            }
            [weakSelf invalidateIntrinsicContentSize];
            [UIView animateWithDuration:0.4 delay:0 options:0 animations:^{
                [weakSelf endImageLoadingAnimations];
            } completion:nil];
        } else [weakSelf.loadingView handleLoadingFailure];
    } animationOptions:0x20000];
}
- (void)startImageLoadingAnimations {
    self.imageView.alpha = 0;
    [self.loadingView isLoading:YES];
}
- (void)endImageLoadingAnimations {
    [self.loadingView isLoading:NO];
    self.imageView.alpha = 1;
}
- (void)showProgressView:(ITBUpload *)upload {
    upload.progressDelegate = self;
    self.progressView = [ITBUploadProgressButton new];
    self.progressView.progress = upload.uploadProgress;
    self.progressView.center = self.imageView.center;
    [self.progressView addTarget:self action:@selector(uploadCancelled) forControlEvents:UIControlEventTouchUpInside];
    [self.imageView addSubview:self.progressView];
}
- (void)setCornerRadiuses:(CGFloat)radius {
    self.layer.cornerRadius = radius;
    self.loadingView.layer.cornerRadius = radius;
}
- (void)configureWithBlock:(ITBBlock *)value {
    [super configureWithBlock:value];
    ITBImageBlock *block = (ITBImageBlock *)value;
    [self setCornerRadiuses:block.cornerRadius];
    [self.loadingView setLoadingViewColorType:block.loadingViewColorType];
    [self applyImageViewContraints:block];
    if (block.upload) [self showProgressView:block.upload];
    [self.loadingView setShimmerSpeedForImageWidth:block.imageSize.width];
    [self loadImage:block];
    if (block.text.length) self.accessibilityLabel = block.text;
}
+ (CGSize)estimateSizeForBlock:(ITBBlock *)value withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    ITBImageBlock *block = (ITBImageBlock *)value;
    id cached = [[ITBImageLoader defaultLoader] cachedImageForURL:[NSURL URLWithString:block.url]];
    if (block.width == 0 && block.height == 0 && cached) {
        CGSize size;
        if ([cached isKindOfClass:[UIImage class]] || [cached isKindOfClass:[ICMAnimatedImage class]]) size = [cached size];
        else size = CGSizeZero;
        block.width = size.width;
        block.height = size.height;
    }
    CGFloat padding = block.padding;
    CGSize size;
    if (block.width != 0 && block.height != 0) {
        CGFloat maximum = [self maxWidthForBlockContentBasedInWidth:width forBlock:block constraintWidth:constraintWidth];
        size = [ITBImageUtils imageDisplaySizeFromSize:CGSizeMake(block.width, block.height) withinWidth:fma(padding, -2, maximum)];
        block.imageSize = size;
    } else {
        block.width = 0;
        block.height = 0;
        size = CGSizeMake(width < 400 ? width : 400, 400);
    }
    return CGSizeMake(constraintWidth ? fma(padding, 2, size.width) : width, fma(padding, 2, size.height));
}
- (void)uploadProgressUpdated:(double)progress {
    self.progressView.progress = progress;
    if (progress >= 1) {
        [UIView animateWithDuration:0.2 delay:0.2 options:0 animations:^{ self.progressView.alpha = 0; } completion:nil];
    }
}
- (void)uploadFailed {
    [UIView animateWithDuration:0.2 delay:0.2 options:0 animations:^{ self.progressView.alpha = 0; } completion:nil];
}
- (void)cleanup {
    [self.imageView cancelImageLoad];
    [self.imageView removeConstraints:self.imageView.constraints];
    self.imageView.image = nil;
    self.imageView.animatedImage = nil;
    self.imageView.alpha = 1;
    self.backgroundColor = UIColor.clearColor;
    [self.loadingView cleanup];
}
- (void)prepareForReuse {
    [self.progressView removeTarget:self action:@selector(uploadCancelled) forControlEvents:UIControlEventTouchUpInside];
    [self.progressView removeFromSuperview];
    self.progressView = nil;
    if ([(ITBImageBlock *)self.block upload].progressDelegate == self) [(ITBImageBlock *)self.block upload].progressDelegate = nil;
    [super prepareForReuse];
    [self cleanup];
}
- (void)dealloc { [self cleanup]; }
@end
