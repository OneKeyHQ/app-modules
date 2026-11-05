#pragma once
#import <UIKit/UIKit.h>
@interface ICMContentContainerViewController : UIViewController
@property (nonatomic, strong) UIView *backgroundView;
@property (nonatomic, strong) UIViewController *contentViewController;
@property (nonatomic, strong) NSArray<NSLayoutConstraint *> *contentHeightConstraints;
- (instancetype)initWithContentViewController:(UIViewController *)controller;
- (void)animateIn;
- (void)close;
- (void)didClose;
- (void)updateContentHeight:(CGFloat)height;
- (void)setUpConstraints;
- (NSLayoutConstraint *)pinContentViewAttribute:(NSLayoutAttribute)attribute constant:(CGFloat)constant relation:(NSLayoutRelation)relation priority:(UILayoutPriority)priority;
- (NSLayoutConstraint *)constrainContentViewAttribute:(NSLayoutAttribute)attribute toConstant:(CGFloat)constant relation:(NSLayoutRelation)relation priority:(UILayoutPriority)priority;
@end
