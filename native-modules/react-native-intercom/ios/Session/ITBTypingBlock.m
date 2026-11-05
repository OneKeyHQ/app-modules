#import "ITBTypingBlock.h"
#import "ICMBlockAppearance.h"
#import <math.h>
@implementation ITBTypingBlock
@synthesize dotColor = _dotColor;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) _dotColor = [appearance typingDotColor];
    return self;
}
- (Class)viewClass { return [ITBTypingBlockView class]; }
- (NSInteger)type { return 16; }
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBTypingBlock *other = object;
    return (!other.dotColor && !self.dotColor) || [other.dotColor isEqual:self.dotColor];
}
- (id)copyWithZone:(NSZone *)zone {
    ITBTypingBlock *copy = [super copyWithZone:zone];
    copy.dotColor = self.dotColor;
    return copy;
}
@end
@implementation ITBTypingBlockView
@synthesize typingView = _typingView;
- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        self.typingView = [[ITBTypingView alloc] initWithFrame:frame dotSize:8 dotPadding:5];
        [self addSubview:self.typingView];
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    self.typingView.frame = self.bounds;
}
+ (CGSize)estimateSizeForBlock:(ITBBlock *)block withWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth {
    (void)block; (void)width; (void)constraintWidth;
    return CGSizeMake(70, 44);
}
- (void)configureWithBlock:(ITBBlock *)block {
    [super configureWithBlock:block];
    ITBTypingBlock *typing = (ITBTypingBlock *)block;
    for (UIView *dot in self.typingView.typingDots) dot.backgroundColor = typing.dotColor;
}
@end
@implementation ITBTypingDotView
@synthesize animationGroup = _animationGroup;
- (instancetype)initWithAnimationDelay:(double)delay dotSize:(CGFloat)size {
    self = [super initWithFrame:CGRectMake(0, 0, size, size)];
    if (self) {
        self.layer.cornerRadius = CGRectGetWidth(self.frame) * 0.5;
        CABasicAnimation *scale = [CABasicAnimation animationWithKeyPath:@"transform.scale"];
        scale.fromValue = @(0.7f);
        NSNumber *one = @(1.0f);
        scale.toValue = one;
        CABasicAnimation *opacity = [CABasicAnimation animationWithKeyPath:@"opacity"];
        opacity.fromValue = @(0.4f);
        opacity.toValue = one;
        self.animationGroup = [CAAnimationGroup animation];
        self.animationGroup.duration = 0.4000000059604645;
        [self.animationGroup performSelector:@selector(setDelegate:) withObject:self];
        double begin = delay + CACurrentMediaTime();
        self.animationGroup.beginTime = begin;
        self.animationGroup.repeatCount = INFINITY;
        self.animationGroup.autoreverses = YES;
        self.animationGroup.animations = @[scale, opacity];
        [self startAnimating];
    }
    return self;
}
- (void)startAnimating {
    if ([self.layer animationForKey:@"typing_animation"]) return;
    CALayer *layer = self.layer;
    [layer addAnimation:self.animationGroup forKey:@"typing_animation"];
}
- (void)stopAnimating { [self.layer removeAllAnimations]; }
- (void)cleanUp {
    [self stopAnimating];
    self.animationGroup = nil;
}
- (void)animationDidStop:(CAAnimation *)animation finished:(BOOL)finished {
    (void)animation; (void)finished;
    if (self.window) [self startAnimating];
}
@end
@implementation ITBTypingView
@synthesize typingDots = _typingDots, dotPadding = _dotPadding;
- (instancetype)initWithFrame:(CGRect)frame dotSize:(CGFloat)size dotPadding:(CGFloat)padding {
    self = [super initWithFrame:frame];
    if (self) {
        _dotPadding = padding;
        self.backgroundColor = [UIColor clearColor];
        NSMutableArray *dots = [NSMutableArray new];
        for (unsigned int index = 0; index < 3; index++) {
            double delay = (double)((float)index * 0.2f);
            ITBTypingDotView *dot = [[ITBTypingDotView alloc] initWithAnimationDelay:delay dotSize:size];
            [self addSubview:dot];
            [dots addObject:dot];
        }
        self.typingDots = dots;
    }
    return self;
}
- (void)layoutSubviews {
    [super layoutSubviews];
    CGFloat midX = self.bounds.size.width * 0.5;
    CGFloat midY = self.bounds.size.height * 0.5;
    UIView *first = self.typingDots[0];
    CGFloat x = midX - self.dotPadding;
    x -= CGRectGetWidth(first.frame);
    first.center = CGPointMake(x, midY);
    UIView *second = self.typingDots[1];
    second.center = CGPointMake(midX, midY);
    UIView *third = self.typingDots[2];
    x = midX + self.dotPadding;
    x += CGRectGetWidth(third.frame);
    third.center = CGPointMake(x, midY);
}
- (void)didMoveToWindow {
    BOOL hasWindow = self.window != nil;
    [self.typingDots makeObjectsPerformSelector:hasWindow ? @selector(startAnimating) : @selector(stopAnimating)];
}
- (void)dealloc {
    [self.typingDots makeObjectsPerformSelector:@selector(cleanUp)];
    [self.typingDots makeObjectsPerformSelector:@selector(removeFromSuperview)];
    self.typingDots = nil;
}
@end
