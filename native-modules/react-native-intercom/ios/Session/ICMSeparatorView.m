#import "ICMSeparatorView.h"
#import "ICMAutoLayout.h"
@interface UIView (OriginalSeparatorDisplayScale)
- (CGFloat)icmDisplayScale;
@end
@implementation ICMSeparatorView
- (instancetype)initWithColor:(UIColor *)color {
    self = [super init];
    if (self) { self.translatesAutoresizingMaskIntoConstraints = NO; self.backgroundColor = color; }
    return self;
}
+ (BOOL)requiresConstraintBasedLayout { return YES; }
- (void)pinToEdge:(NSUInteger)edge ofView:(UIView *)view {
    CGFloat height = 1 / self.icmDisplayScale;
    self.heightConstraint = [ICMAutoLayout constrainView:self toHeight:height];
    [ICMAutoLayout pinView:self edges:[self adjacentEdgesWithEdge:edge] toSameEdgesOfView:view];
}
- (NSUInteger)adjacentEdgesWithEdge:(NSUInteger)edge {
    static const NSUInteger values[] = {11,7,1,14,1,1,1,13};
    return edge >= 1 && edge <= 8 ? values[edge - 1] : 1;
}
- (void)didMoveToWindow { [super didMoveToWindow]; if (self.traitCollection.displayScale > 0) [self updateHeight]; }
- (void)traitCollectionDidChange:(UITraitCollection *)previous {
    [super traitCollectionDidChange:previous];
    if (self.traitCollection.displayScale > 0) {
        CGFloat old = previous.displayScale;
        if (old != self.traitCollection.displayScale) [self updateHeight];
    }
}
- (void)updateHeight {
    CGFloat height = 1 / self.icmDisplayScale;
    if (self.heightConstraint.constant != height) self.heightConstraint.constant = height;
}
@end
