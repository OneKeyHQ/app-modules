#import "ICMAutoLayout.h"
@implementation ICMAutoLayout
+ (NSLayoutConstraint *)centerView:(UIView *)view inContainerOnAxis:(NSLayoutAttribute)axis {
    return [self centerView:view inContainerOnAxis:axis withConstant:0];
}
+ (NSLayoutConstraint *)centerView:(UIView *)view inContainerOnAxis:(NSLayoutAttribute)axis withConstant:(CGFloat)constant {
    (void)constant;
    return [self centerView:view inContainerOnAxis:axis withConstant:0 withPriority:1000];
}
+ (NSLayoutConstraint *)centerView:(UIView *)view inContainerOnAxis:(NSLayoutAttribute)axis withConstant:(CGFloat)constant withPriority:(UILayoutPriority)priority {
    if (!view) return nil;
    view.translatesAutoresizingMaskIntoConstraints = NO;
    UIView *superview = view.superview;
    NSLayoutConstraint *constraint = [NSLayoutConstraint constraintWithItem:view attribute:axis relatedBy:NSLayoutRelationEqual toItem:superview attribute:axis multiplier:1 constant:constant];
    constraint.priority = priority;
    constraint.active = YES;
    return constraint;
}
+ (NSArray<NSLayoutConstraint *> *)constrainView:(UIView *)view toSize:(CGSize)size {
    if (!view) return nil;
    view.translatesAutoresizingMaskIntoConstraints = NO;
    NSMutableArray *constraints = [NSMutableArray new];
    if (size.width != 0) [constraints addObject:[self constrainView:view toWidth:size.width]];
    if (size.height != 0) [constraints addObject:[self constrainView:view toHeight:size.height]];
    return [constraints copy];
}
+ (NSLayoutConstraint *)constrainView:(UIView *)view toWidth:(CGFloat)width { return [self constrainView:view toWidth:width withPriority:1000]; }
+ (NSLayoutConstraint *)constrainView:(UIView *)view toWidth:(CGFloat)width withPriority:(UILayoutPriority)priority {
    if (!view) return nil;
    view.translatesAutoresizingMaskIntoConstraints = NO;
    NSLayoutConstraint *constraint = [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeWidth relatedBy:0 toItem:nil attribute:0 multiplier:0 constant:width];
    constraint.priority = priority;
    constraint.active = YES;
    return constraint;
}
+ (NSLayoutConstraint *)constrainView:(UIView *)view toHeight:(CGFloat)height { return [self constrainView:view toHeight:height withPriority:1000]; }
+ (NSLayoutConstraint *)constrainView:(UIView *)view toHeight:(CGFloat)height withPriority:(UILayoutPriority)priority {
    if (!view) return nil;
    view.translatesAutoresizingMaskIntoConstraints = NO;
    NSLayoutConstraint *constraint = [NSLayoutConstraint constraintWithItem:view attribute:NSLayoutAttributeHeight relatedBy:0 toItem:nil attribute:0 multiplier:0 constant:height];
    constraint.priority = priority;
    constraint.active = YES;
    return constraint;
}
+ (NSLayoutConstraint *)pinView:(UIView *)view attribute:(NSLayoutAttribute)attribute toAttribute:(NSLayoutAttribute)otherAttribute ofItem:(id)item {
    return [self pinView:view attribute:attribute toAttribute:otherAttribute ofItem:item withConstant:0];
}
+ (NSLayoutConstraint *)pinView:(UIView *)view attribute:(NSLayoutAttribute)attribute toAttribute:(NSLayoutAttribute)otherAttribute ofItem:(id)item withConstant:(CGFloat)constant {
    return [self pinView:view attribute:attribute toAttribute:otherAttribute ofItem:item withConstant:constant relation:0];
}
+ (NSLayoutConstraint *)pinView:(UIView *)view attribute:(NSLayoutAttribute)attribute toAttribute:(NSLayoutAttribute)otherAttribute ofItem:(id)item withConstant:(CGFloat)constant relation:(NSLayoutRelation)relation {
    if (!view) return nil;
    view.translatesAutoresizingMaskIntoConstraints = NO;
    __attribute__((objc_precise_lifetime)) UIView *common = [item isKindOfClass:[UIView class]] ? [self view:view commonSuperviewWithView:item] : view.superview;
    (void)common;
    NSLayoutConstraint *constraint = [NSLayoutConstraint constraintWithItem:view attribute:attribute relatedBy:relation toItem:item attribute:otherAttribute multiplier:1 constant:constant];
    constraint.active = YES;
    return constraint;
}
+ (NSArray<NSLayoutConstraint *> *)pinView:(UIView *)view edges:(NSUInteger)edges toSameEdgesOfView:(UIView *)other { return [self pinView:view edges:edges toSameEdgesOfView:other inset:0]; }
+ (NSArray<NSLayoutConstraint *> *)pinView:(UIView *)view edges:(NSUInteger)edges toSameEdgesOfView:(UIView *)other inset:(CGFloat)inset { return [self pinView:view edges:edges toSameEdgesOfView:other inset:inset withPriority:1000]; }
+ (NSArray<NSLayoutConstraint *> *)pinView:(UIView *)view edges:(NSUInteger)edges toSameEdgesOfView:(UIView *)other inset:(CGFloat)inset withPriority:(UILayoutPriority)priority {
    if (!view) return nil;
    view.translatesAutoresizingMaskIntoConstraints = NO;
    NSMutableArray *constraints = [NSMutableArray new];
    if (edges & 1) [constraints addObject:[self pinView:view attribute:NSLayoutAttributeTop toAttribute:NSLayoutAttributeTop ofItem:other withConstant:inset]];
    if (edges & 0x18) [constraints addObject:[self pinView:view attribute:NSLayoutAttributeLeading toAttribute:NSLayoutAttributeLeading ofItem:other withConstant:inset]];
    if (edges & 0x22) [constraints addObject:[self pinView:view attribute:NSLayoutAttributeTrailing toAttribute:NSLayoutAttributeTrailing ofItem:other withConstant:-inset]];
    if (edges & 4) [constraints addObject:[self pinView:view attribute:NSLayoutAttributeBottom toAttribute:NSLayoutAttributeBottom ofItem:other withConstant:-inset]];
    for (NSLayoutConstraint *constraint in constraints) { constraint.priority = priority; constraint.active = YES; }
    return [constraints copy];
}
+ (UIView *)view:(UIView *)view commonSuperviewWithView:(UIView *)other {
    UIView *candidate = view;
    do {
        BOOL found = [other isDescendantOfView:candidate];
        UIView *next = candidate.superview;
        if (found) return candidate;
        candidate = next;
    } while (candidate);
    return nil;
}
@end
