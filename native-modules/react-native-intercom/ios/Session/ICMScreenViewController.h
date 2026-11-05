#pragma once
#import <UIKit/UIKit.h>
#import "ICMScreen.h"
#import "ICMScreenDelegate.h"
#import "ICMContentStackView.h"
@interface ICMScreenViewController : UIViewController <UIScrollViewDelegate>
@property (nonatomic, strong) UIColor *backgroundColor;
@property (nonatomic, strong) ICMContentStackView *contentStackView;
@property (nonatomic, strong) UIView *actionsContainerView;
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, strong) NSLayoutConstraint *containerViewHeightConstraint;
@property (nonatomic, strong) ICMScreen *screen;
@property (nonatomic, strong) UIView *fadeView;
@property (nonatomic, strong) CAGradientLayer *gradientLayer;
@property (nonatomic, weak) id<ICMScreenDelegate> screenDelegate;
@property (nonatomic, strong) OutboundContentBlockDelegate *blockDelegate;
- (instancetype)initWithScreen:(ICMScreen *)screen screenDelegate:(id<ICMScreenDelegate>)delegate;
- (void)configureBackgroundColor;
- (void)createScrollView;
- (void)createScrollViewHelperViews;
- (void)setupContentView;
- (void)layoutContentView;
- (void)topLayout;
- (void)centerLayout;
- (void)bottomLayout;
- (void)createFadeView;
- (void)updateFadeGradient;
- (void)setupActionContainerView;
- (void)configureActions;
- (void)addViewControllerAsChildViewController:(UIViewController *)controller;
@end
