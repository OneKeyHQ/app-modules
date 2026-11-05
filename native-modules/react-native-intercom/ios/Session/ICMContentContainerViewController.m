#import "ICMContentContainerViewController.h"
#import "ICMColor.h"
#import "ICMAutoLayout.h"
@interface ICMContentContainerViewController () {
    UIView *_backgroundView;
    UIViewController *_contentViewController;
    NSArray<NSLayoutConstraint *> *_contentHeightConstraints;
}
@end
@implementation ICMContentContainerViewController
@synthesize backgroundView = _backgroundView, contentViewController = _contentViewController;
@synthesize contentHeightConstraints = _contentHeightConstraints;
- (instancetype)initWithContentViewController:(UIViewController *)controller {
    if ((self = [super init])) _contentViewController = controller;
    return self;
}
- (void)loadView {
    [super loadView];
    self.view.alpha = 0;
    self.view.backgroundColor = [UIColor clearColor];
    self.backgroundView = [UIView new];
    self.backgroundView.accessibilityIdentifier = @"intercom content background view";
    self.backgroundView.translatesAutoresizingMaskIntoConstraints = NO;
    self.backgroundView.backgroundColor = [ICMColor colorWithR:42 g:48 b:55 alpha:0.699999988079071];
    [self.backgroundView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(close)]];
    [self.view addSubview:self.backgroundView];
    self.contentViewController.view.translatesAutoresizingMaskIntoConstraints = NO;
    self.contentViewController.view.layer.cornerRadius = 8;
    self.contentViewController.view.clipsToBounds = YES;
    [self addChildViewController:self.contentViewController];
    [self.view addSubview:self.contentViewController.view];
    [self.contentViewController didMoveToParentViewController:self];
    [self setUpConstraints];
}
- (void)viewDidAppear:(BOOL)animated { [super viewDidAppear:animated]; [self animateIn]; }
- (void)animateIn {
    [UIView animateWithDuration:0.2 animations:^{ [self setNeedsStatusBarAppearanceUpdate]; self.view.alpha = 1; }];
}
- (UIStatusBarStyle)preferredStatusBarStyle { return UIStatusBarStyleLightContent; }
- (void)close {
    [UIView animateWithDuration:0.2 animations:^{ self.view.alpha = 0; } completion:^(BOOL finished) { (void)finished; [self didClose]; }];
}
- (void)didClose {}
- (void)updateContentHeight:(CGFloat)height { for (NSLayoutConstraint *constraint in self.contentHeightConstraints) constraint.constant = height; }
- (void)setUpConstraints {
    [ICMAutoLayout pinView:self.backgroundView edges:NSUIntegerMax toSameEdgesOfView:self.view];
    [ICMAutoLayout centerView:self.contentViewController.view inContainerOnAxis:NSLayoutAttributeCenterX];
    [ICMAutoLayout centerView:self.contentViewController.view inContainerOnAxis:NSLayoutAttributeCenterY];
    [self pinContentViewAttribute:NSLayoutAttributeTop constant:20 relation:NSLayoutRelationGreaterThanOrEqual priority:1000];
    [self pinContentViewAttribute:NSLayoutAttributeLeft constant:12 relation:NSLayoutRelationGreaterThanOrEqual priority:1000];
    [self pinContentViewAttribute:NSLayoutAttributeBottom constant:-20 relation:NSLayoutRelationLessThanOrEqual priority:1000];
    [self pinContentViewAttribute:NSLayoutAttributeRight constant:-12 relation:NSLayoutRelationLessThanOrEqual priority:1000];
    NSLayoutConstraint *height = [self constrainContentViewAttribute:NSLayoutAttributeHeight toConstant:552 relation:NSLayoutRelationEqual priority:750];
    NSLayoutConstraint *maxHeight = [self constrainContentViewAttribute:NSLayoutAttributeHeight toConstant:552 relation:NSLayoutRelationLessThanOrEqual priority:1000];
    self.contentHeightConstraints = @[height, maxHeight];
    [self constrainContentViewAttribute:NSLayoutAttributeWidth toConstant:552 relation:NSLayoutRelationEqual priority:750];
    [self constrainContentViewAttribute:NSLayoutAttributeWidth toConstant:552 relation:NSLayoutRelationLessThanOrEqual priority:1000];
}
- (NSLayoutConstraint *)pinContentViewAttribute:(NSLayoutAttribute)attribute constant:(CGFloat)constant relation:(NSLayoutRelation)relation priority:(UILayoutPriority)priority {
    NSLayoutConstraint *constraint = [NSLayoutConstraint constraintWithItem:self.contentViewController.view attribute:attribute relatedBy:relation toItem:self.view attribute:attribute multiplier:1 constant:constant];
    constraint.priority = priority;
    constraint.active = YES;
    return constraint;
}
- (NSLayoutConstraint *)constrainContentViewAttribute:(NSLayoutAttribute)attribute toConstant:(CGFloat)constant relation:(NSLayoutRelation)relation priority:(UILayoutPriority)priority {
    NSLayoutConstraint *constraint = [NSLayoutConstraint constraintWithItem:self.contentViewController.view attribute:attribute relatedBy:relation toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:0 constant:constant];
    constraint.priority = priority;
    constraint.active = YES;
    return constraint;
}
@end
