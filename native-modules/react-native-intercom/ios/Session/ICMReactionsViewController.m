#import "ICMReactionsViewController.h"
#import "ITBAnimatedImageView.h"
#import "ITBImageLoader.h"
#import "UIImage+ICMImageUtils.h"
#import <math.h>
@implementation ICMReactionsViewController
- (instancetype)initWithReactionSet:(ICMReactionsSet *)set separatorColor:(UIColor *)color reactionsLabel:(UILabel *)label delegate:(id<ICMReactionsViewControllerDelegate>)delegate {
    self = [super init];
    if (self) { _reactionSet = set; _separatorColor = color; _reactionsLabel = label; _delegate = delegate; }
    return self;
}
- (void)loadView {
    [super loadView];
    self.view.accessibilityIdentifier = @"intercom reactions view";
    UILongPressGestureRecognizer *gesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(didPan:)];
    gesture.minimumPressDuration = 0.001;
    gesture.delegate = self;
    [self.view addGestureRecognizer:gesture];
    self.topSeparator = [UIView new];
    self.topSeparator.translatesAutoresizingMaskIntoConstraints = NO;
    self.topSeparator.backgroundColor = self.separatorColor;
    [self.view addSubview:self.topSeparator];
    if (self.reactionsLabel) [self.view addSubview:self.reactionsLabel];
    NSArray *options = self.reactionSet.options;
    NSMutableArray *views = [NSMutableArray arrayWithCapacity:options.count];
    for (ICMReactionsOption *option in options) {
        ITBAnimatedImageView *view = [[ITBAnimatedImageView alloc] initWithImageLoader:[ITBImageLoader longTermLoader]];
        view.contentMode = UIViewContentModeScaleAspectFit;
        view.transform = CGAffineTransformMakeScale(1, 1);
        view.accessibilityLabel = option.unicodeEmoticon;
        [views addObject:view];
        [self.view addSubview:view];
    }
    self.optionViews = views;
    [self determineSelectedView];
    [self updateReactionViews];
}
- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];
    CGFloat centerY = CGRectGetMidY(self.view.bounds);
    if (self.reactionsLabel) {
        CGFloat width = CGRectGetWidth(self.view.bounds);
        self.reactionsLabel.frame = CGRectMake(0, 5, width, 20);
        centerY = (CGRectGetMaxY(self.reactionsLabel.frame) + 2) + 14;
    }
    CGFloat width = CGRectGetWidth(self.view.bounds);
    NSUInteger count = self.optionViews.count;
    if (self.optionViews.count != 0) {
        CGFloat spacing = width / (double)(count + 1);
        NSUInteger index = 0;
        do {
            ITBAnimatedImageView *view = self.optionViews[index];
            view.bounds = CGRectMake(0, 0, 28, 28);
            index++;
            view.center = CGPointMake((double)floorf((float)(spacing * (double)index)), centerY);
        } while (index < self.optionViews.count);
    }
    CGFloat separatorWidth = CGRectGetWidth(self.view.bounds);
    self.topSeparator.frame = CGRectMake(0, 0, separatorWidth, 0.5);
}
- (void)displayOption:(ICMReactionsOption *)option inView:(ITBAnimatedImageView *)view {
    __block BOOL grayscale = NO;
    if (self.selectedView && ![view isEqual:self.selectedView]) grayscale = YES;
    __block ITBAnimatedImageView *imageView = view;
    [view loadImageWithURLString:option.imageUrl.absoluteString actionsBeforeNetworkLoad:nil completion:^{
        imageView.isAccessibilityElement = YES;
        imageView.accessibilityTraits = UIAccessibilityTraitButton;
        if (grayscale) {
            imageView.image = [self grayscaleEmoji:option inView:imageView];
            imageView.alpha = 0.6;
            imageView.accessibilityLabel = [NSString stringWithFormat:@"%@-%@", option.unicodeEmoticon, @"grayscale"];
        } else {
            if (self.reactionSet.selectedIndex) imageView.accessibilityTraits = UIAccessibilityTraitButton | UIAccessibilityTraitSelected;
            imageView.alpha = 1;
            imageView.accessibilityLabel = option.unicodeEmoticon;
        }
        dispatch_async(dispatch_get_main_queue(), ^{ imageView.image = [imageView.image icm_resizeImageToSize:CGSizeMake(56, 56)]; });
    }];
}
- (void)updateReactionViews {
    for (NSUInteger index = 0; index < self.optionViews.count; index++) {
        ITBAnimatedImageView *view = self.optionViews[index];
        ICMReactionsOption *option = self.reactionSet.options[index];
        [self displayOption:option inView:view];
    }
}
- (UIImage *)grayscaleEmoji:(ICMReactionsOption *)option inView:(ITBAnimatedImageView *)view {
    ITBImageLoader *loader = [ITBImageLoader defaultLoader];
    NSString *key = [self grayscaleImageCacheKey:option.imageUrl.absoluteString];
    UIImage *image = [loader cachedImageForKey:key];
    if (image) return image;
    view.alpha = 1;
    UIImage *gray = [view.image icm_grayscale];
    ITBImageLoader *currentLoader = [ITBImageLoader defaultLoader];
    NSString *currentKey = [self grayscaleImageCacheKey:option.imageUrl.absoluteString];
    [currentLoader cacheImage:gray ForKey:currentKey];
    return gray;
}
- (NSString *)grayscaleImageCacheKey:(NSString *)key { return [NSString stringWithFormat:@"%@-%@", key, @"grayscale"]; }
- (void)didPan:(UILongPressGestureRecognizer *)gesture {
    CGPoint point = [gesture locationInView:self.view];
    UIGestureRecognizerState state = gesture.state;
    if (state == UIGestureRecognizerStateBegan) {
        for (ITBAnimatedImageView *view in self.optionViews) {
            CGFloat center = view.center.x;
            if (!self.selectedView || fabs(center - point.x) < fabs(self.selectedView.center.x - point.x)) self.selectedView = view;
        }
        [self updateViewsWithBigSelectedView:YES];
    } else if (state == UIGestureRecognizerStateChanged) {
        if ([self touchPointShouldCancelSelection:point]) { [self resetSelectedView]; [self updateViewsWithBigSelectedView:NO]; }
        else {
            for (ITBAnimatedImageView *view in self.optionViews) {
                CGFloat center = view.center.x;
                if (fabs(center - point.x) < fabs(self.selectedView.center.x - point.x)) self.selectedView = view;
            }
            [self updateViewsWithBigSelectedView:YES];
        }
    } else if (state == UIGestureRecognizerStateEnded) {
        if ([self touchPointShouldCancelSelection:point]) { [self resetSelectedView]; [self updateViewsWithBigSelectedView:NO]; }
        else {
            NSUInteger index = [self.optionViews indexOfObject:self.selectedView];
            ICMReactionsOption *option = self.reactionSet.options[index];
            if (option.index) {
                self.reactionSet.selectedIndex = @(index);
                [self.delegate reactionsViewController:self reactedWithOption:option];
            }
            [self updateViewsWithBigSelectedView:NO];
        }
    } else if (state == UIGestureRecognizerStateCancelled || state == UIGestureRecognizerStateFailed) {
        [self resetSelectedView];
        [self updateViewsWithBigSelectedView:NO];
    }
}
- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gesture shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)other { (void)gesture; (void)other; return YES; }
- (BOOL)touchPointShouldCancelSelection:(CGPoint)point {
    if (point.y < -50) return YES;
    if (point.y >= CGRectGetHeight(self.view.bounds) + 50 || point.x <= 5) return YES;
    return point.x >= CGRectGetWidth(self.view.bounds) - 5;
}
- (void)determineSelectedView {
    if (!self.reactionSet.selectedIndex) { self.selectedView = nil; return; }
    NSArray *options = self.reactionSet.options;
    NSUInteger index = 0;
    for (ICMReactionsOption *option in options) {
        if ([option.index isEqualToNumber:self.reactionSet.selectedIndex]) { self.selectedView = self.optionViews[index]; break; }
        index++;
    }
}
- (void)resetSelectedView { [self determineSelectedView]; [self updateReactionViews]; }
- (void)updateViewsWithBigSelectedView:(BOOL)big {
    [self performSpringAnimation:^{
        self.selectedView.transform = big ? CGAffineTransformTranslate(CGAffineTransformMakeScale(2.5, 2.5), 0, -20) : CGAffineTransformMakeScale(1, 1);
        for (ITBAnimatedImageView *view in self.optionViews) if (view != self.selectedView) view.transform = CGAffineTransformMakeScale(1, 1);
        [self updateReactionViews];
    }];
}
- (void)performSpringAnimation:(void (^)(void))animation { [UIView animateWithDuration:0.5 delay:0 usingSpringWithDamping:0.6 initialSpringVelocity:0 options:4 animations:animation completion:nil]; }
- (void)prepareForAnimatingIn {
    [self.view layoutIfNeeded];
    for (ITBAnimatedImageView *view in self.optionViews) { view.alpha = 0; view.transform = CGAffineTransformMakeScale(0.800000011920929, 0.800000011920929); }
}
- (void)animateInOptions {
    for (NSUInteger index = 0; index < self.optionViews.count; index++) {
        [UIView animateWithDuration:0.5 delay:(double)index * 0.05 usingSpringWithDamping:0.5 initialSpringVelocity:0 options:4 animations:^{
            ITBAnimatedImageView *view = self.optionViews[index];
            if (self.selectedView) view.alpha = view == self.selectedView ? 1 : 0.6;
            else view.alpha = 1;
            view.transform = CGAffineTransformMakeScale(1, 1);
        } completion:nil];
    }
}
@end
