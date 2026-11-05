// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBAnimatedImageView.h"
#import "ITBImageLoader.h"

@implementation ITBAnimatedImageViewDisplayLinkProxy
- (instancetype)initWithTarget:(ITBAnimatedImageView *)target {
    self = [super init];
    if (self) _target = target;
    return self;
}
- (void)displayLinkFired:(CADisplayLink *)link { [_target displayLinkFired:link]; }
@end

@implementation ITBAnimatedImageView
@synthesize animatedImage = _animatedImage, imageLoader = _imageLoader;
@synthesize taskUUID = _taskUUID, currentLoadToken = _currentLoadToken;
- (instancetype)init { return [self initWithImageLoader:[ITBImageLoader defaultLoader]]; }
- (instancetype)initWithImageLoader:(ITBImageLoader *)loader {
    self = [super init];
    if (self) {
        _imageLoader = loader;
        _lastFrameIndex = -1;
    }
    return self;
}
- (void)dealloc { [_displayLink invalidate]; }
- (void)loadImageWithURLString:(NSString *)url actionsBeforeNetworkLoad:(void (^)(void))before completion:(void (^)(void))completion {
    [self loadImageWithURLString:url actionsBeforeNetworkLoad:before completion:completion animationOptions:0];
}
- (void)loadImageWithURLString:(NSString *)url actionsBeforeNetworkLoad:(void (^)(void))before completion:(void (^)(void))completion animationOptions:(UIViewAnimationOptions)options {
    id cached = [self.imageLoader cachedImageForURL:[NSURL URLWithString:url]];
    if (cached) {
        [self cancelImageLoad];
        [self performSelectorOnMainThread:@selector(assignImage:) withObject:cached waitUntilDone:YES];
        if (completion) completion();
    } else {
        if (before) before();
        [self cancelImageLoad];
        __weak ITBAnimatedImageView *weakSelf = self;
        NSUUID *token = [NSUUID UUID];
        self.currentLoadToken = token;
        self.taskUUID = [self.imageLoader loadImageWithURLString:url completion:^(id image) {
            ITBAnimatedImageView *view = weakSelf;
            if (!view || view.currentLoadToken != token) return;
            if (image) [view assignImageWithAnimation:image completion:completion animationOptions:options];
            else if (completion) completion();
        }];
    }
}
- (void)cancelImageLoad {
    self.currentLoadToken = nil;
    if (self.taskUUID) {
        [self.imageLoader cancelImageLoadWithUUID:self.taskUUID];
        self.taskUUID = nil;
    }
}
- (void)setAnimatedImage:(ICMAnimatedImage *)image {
    if (_animatedImage == image) return;
    [self invalidateDisplayLink];
    _animatedImage = image;
    _playHead = 0;
    _lastFireTime = 0;
    _playedLoops = 0;
    _lastFrameIndex = -1;
    if (image) {
        [super setImage:image.coverImage];
        [self checkIfShouldAnimate];
    }
}
- (void)setImage:(UIImage *)image {
    self.animatedImage = nil;
    [super setImage:image];
}
- (void)didMoveToWindow { [super didMoveToWindow]; [self checkIfShouldAnimate]; }
- (void)didMoveToSuperview { [super didMoveToSuperview]; [self checkIfShouldAnimate]; }
- (void)setHidden:(BOOL)hidden { [super setHidden:hidden]; [self checkIfShouldAnimate]; }
- (void)setAlpha:(CGFloat)alpha { [super setAlpha:alpha]; [self checkIfShouldAnimate]; }
- (void)checkIfShouldAnimate {
    if (_animatedImage.frameCount >= 2 && [self canBeVisible]) [self startPlayback];
    else [self stopPlayback];
}
- (BOOL)canBeVisible { return self.window && self.superview && !self.hidden && self.alpha > 0; }
- (void)startPlayback {
    [_animatedImage prepareForPlayback];
    if (_displayLink) { _displayLink.paused = NO; return; }
    ITBAnimatedImageViewDisplayLinkProxy *proxy = [[ITBAnimatedImageViewDisplayLinkProxy alloc] initWithTarget:self];
    _displayLink = [CADisplayLink displayLinkWithTarget:proxy selector:@selector(displayLinkFired:)];
    _displayLink.preferredFramesPerSecond = _animatedImage.preferredFrameRate;
    [_displayLink addToRunLoop:[NSRunLoop mainRunLoop] forMode:NSRunLoopCommonModes];
}
- (void)stopPlayback { _displayLink.paused = YES; _lastFireTime = 0; }
- (void)invalidateDisplayLink {
    [_displayLink invalidate];
    _displayLink = nil;
    _lastFireTime = 0;
}
- (void)displayLinkFired:(CADisplayLink *)link {
    if (!_animatedImage) return;
    double now = CACurrentMediaTime();
    double elapsed = _lastFireTime == 0 ? 0 : now - _lastFireTime;
    _lastFireTime = now;
    _playHead += elapsed;
    double duration = _animatedImage.duration;
    if (duration > 0 && _playHead > duration) {
        do {
            _playHead -= duration;
            _playedLoops = (NSInteger)((NSUInteger)_playedLoops + 1);
        } while (_playHead > duration);
    }
    if (_animatedImage.loopCount >= 1 && _playedLoops >= _animatedImage.loopCount) {
        [self stopPlayback];
        return;
    }
    NSInteger index = [_animatedImage frameIndexAtPlayHead:_playHead];
    if (index == _lastFrameIndex) return;
    UIImage *frame = [_animatedImage frameAtIndex:index];
    if (frame) {
        _lastFrameIndex = index;
        [super setImage:frame];
    } else {
        link.paused = YES;
        _lastFireTime = 0;
    }
}
- (void)assignImageWithAnimation:(id)image completion:(void (^)(void))completion animationOptions:(UIViewAnimationOptions)options {
    if (options) {
        [UIView transitionWithView:self duration:0.30000001192092896 options:options animations:^{
            [self assignImage:image];
        } completion:^(__unused BOOL finished) {
            if (completion) completion();
        }];
    } else {
        [self assignImage:image];
        if (completion) completion();
    }
}
- (void)assignImage:(id)image {
    if ([image isKindOfClass:[UIImage class]]) self.image = image;
    else if ([image isKindOfClass:[ICMAnimatedImage class]]) self.animatedImage = image;
}
@end
