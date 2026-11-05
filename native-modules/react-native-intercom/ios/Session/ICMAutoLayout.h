#import <UIKit/UIKit.h>
@interface ICMAutoLayout : NSObject
+ (NSLayoutConstraint *)centerView:(UIView *)view inContainerOnAxis:(NSLayoutAttribute)axis;
+ (NSLayoutConstraint *)centerView:(UIView *)view inContainerOnAxis:(NSLayoutAttribute)axis withConstant:(CGFloat)constant;
+ (NSLayoutConstraint *)centerView:(UIView *)view inContainerOnAxis:(NSLayoutAttribute)axis withConstant:(CGFloat)constant withPriority:(UILayoutPriority)priority;
+ (NSArray<NSLayoutConstraint *> *)constrainView:(UIView *)view toSize:(CGSize)size;
+ (NSLayoutConstraint *)constrainView:(UIView *)view toWidth:(CGFloat)width;
+ (NSLayoutConstraint *)constrainView:(UIView *)view toWidth:(CGFloat)width withPriority:(UILayoutPriority)priority;
+ (NSLayoutConstraint *)constrainView:(UIView *)view toHeight:(CGFloat)height;
+ (NSLayoutConstraint *)constrainView:(UIView *)view toHeight:(CGFloat)height withPriority:(UILayoutPriority)priority;
+ (NSLayoutConstraint *)pinView:(UIView *)view attribute:(NSLayoutAttribute)attribute toAttribute:(NSLayoutAttribute)otherAttribute ofItem:(id)item;
+ (NSLayoutConstraint *)pinView:(UIView *)view attribute:(NSLayoutAttribute)attribute toAttribute:(NSLayoutAttribute)otherAttribute ofItem:(id)item withConstant:(CGFloat)constant;
+ (NSLayoutConstraint *)pinView:(UIView *)view attribute:(NSLayoutAttribute)attribute toAttribute:(NSLayoutAttribute)otherAttribute ofItem:(id)item withConstant:(CGFloat)constant relation:(NSLayoutRelation)relation;
+ (NSArray<NSLayoutConstraint *> *)pinView:(UIView *)view edges:(NSUInteger)edges toSameEdgesOfView:(UIView *)other;
+ (NSArray<NSLayoutConstraint *> *)pinView:(UIView *)view edges:(NSUInteger)edges toSameEdgesOfView:(UIView *)other inset:(CGFloat)inset;
+ (NSArray<NSLayoutConstraint *> *)pinView:(UIView *)view edges:(NSUInteger)edges toSameEdgesOfView:(UIView *)other inset:(CGFloat)inset withPriority:(UILayoutPriority)priority;
+ (UIView *)view:(UIView *)view commonSuperviewWithView:(UIView *)other;
@end
