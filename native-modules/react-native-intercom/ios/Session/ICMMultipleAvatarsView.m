#import "ICMMultipleAvatarsView.h"
#import <math.h>
@implementation ICMMultipleAvatarsView
- (instancetype)initWithAvatarViewModels:(NSArray<ICMAvatarViewModel *> *)models height:(CGFloat)height overlap:(CGFloat)overlap maxCount:(NSInteger)count {
    self = [super init];
    if (self) { _avatarHeight = height; _avatarOverlap = overlap; _maxAvatarCount = count; [self configureAvatarViewsForAvatarViewModels:models]; }
    return self;
}
- (void)configureAvatarViewsForAvatarViewModels:(NSArray<ICMAvatarViewModel *> *)models {
    NSUInteger maximum = (NSUInteger)self.maxAvatarCount;
    NSUInteger count = models.count;
    NSInteger total = (NSInteger)(maximum < count ? maximum : count);
    NSMutableArray *views = [NSMutableArray new];
    for (NSInteger index = 0; index < total; index++) {
        ICMAvatarViewModel *model = models[index];
        ICMAvatarView *view = nil;
        switch (model.activeIndicatorSize) {
            case 0: view = [[ICMAvatarView alloc] initWithNoTeammatePresenceWithAvatarDiameter:self.avatarHeight]; break;
            case 1:
                view = [[ICMAvatarView alloc] initWithDefaultTeammatePresenceWithAvatarDiameter:self.avatarHeight];
                [view.teammatePresenceView setActive:YES];
                [view setUserPresenceViewIsHidden:NO];
                break;
            case 2:
                view = [[ICMAvatarView alloc] initWithLargeTeammatePresenceWithAvatarDiameter:self.avatarHeight];
                [view.teammatePresenceView setActive:YES];
                [view setUserPresenceViewIsHidden:NO];
                break;
        }
        [view loadAvatar:model.avatar useActionColorAsBackgroundColor:YES];
        [self insertSubview:view atIndex:0];
        if (index > 0) [self roundCutOutView:view withCutOutWidth:self.avatarOverlap + 2 leadingEdge:YES];
        [views addObject:view];
    }
    self.avatarViewsArray = views;
}
- (void)layoutSubviews { [super layoutSubviews]; [self layoutAvatars]; }
- (void)layoutAvatars {
    CGFloat x = 0;
    for (ICMAvatarView *view in self.avatarViewsArray) {
        CGRect frame = view.frame;
        frame.origin.x = x;
        view.frame = frame;
        view.borderColor = UIColor.clearColor;
        view.borderWidth = 0;
        x += self.avatarHeight - self.avatarOverlap;
    }
}
- (CGSize)intrinsicContentSize {
    NSUInteger count = self.avatarViewsArray.count;
    CGFloat height = self.avatarHeight;
    NSUInteger previous = count - 1;
    CGFloat overlap = self.avatarOverlap;
    return CGSizeMake(fma((double)count, height, -((double)previous * overlap)), self.avatarHeight);
}
- (NSUInteger)numberOfAvatarsDisplayed { return self.avatarViewsArray.count; }
- (void)updateVisibleAvatars {
    CGFloat width = CGRectGetWidth(self.frame);
    for (ICMAvatarView *view in self.avatarViewsArray) view.hidden = CGRectGetMaxX(view.frame) > width;
}
- (void)roundCutOutView:(UIView *)view withCutOutWidth:(CGFloat)width leadingEdge:(BOOL)leading {
    CGFloat height = CGRectGetHeight(view.frame);
    CGFloat radius = height * .5;
    CGFloat x = leading ? width - radius : fma(height, 1.5, -width);
    UIBezierPath *path = [UIBezierPath new];
    [path addArcWithCenter:CGPointMake(x, radius) radius:radius startAngle:0 endAngle:6.283185307179586 clockwise:NO];
    CAShapeLayer *shape = [CAShapeLayer new];
    shape.path = path.CGPath;
    CALayer *container = [CALayer new];
    container.frame = CGRectMake(0, 0, height, height);
    container.backgroundColor = UIColor.blackColor.CGColor;
    [container addSublayer:shape];
    container.masksToBounds = YES;
    shape.compositingFilter = @"xor";
    view.layer.mask = container;
}
@end
