#import "ICMScreenViewController.h"
#import "ICMScreenActionViewController.h"
#import "ICMAutoLayout.h"
#import "ICMColor.h"
#import "ICMCarouselReportingService.h"
@interface OutboundContentBlockDelegate (CarouselScreen)
- (instancetype)initWithContext:(NSInteger)context;
@end
@implementation ICMScreenViewController
- (instancetype)initWithScreen:(ICMScreen *)screen screenDelegate:(id<ICMScreenDelegate>)delegate {
    self = [super init];
    if (self) { _screen = screen; _screenDelegate = delegate; _blockDelegate = [[OutboundContentBlockDelegate alloc] initWithContext:0]; }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    [self configureBackgroundColor];
    [self setupActionContainerView];
    [self setupContentView];
    [self.contentStackView renderContent];
    [self createFadeView];
    [[ICMCarouselReportingService sharedInstance] recordScreenViewedStat:self.screen.screenId];
}
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated]; [self updateFadeGradient];
    self.view.accessibilityIdentifier = @"intercom current carousel screen view";
}
- (void)viewDidDisappear:(BOOL)animated { [super viewDidDisappear:animated]; self.view.accessibilityIdentifier = nil; }
- (void)viewDidLayoutSubviews { self.gradientLayer.frame = self.fadeView.bounds; [self updateFadeGradient]; }
- (void)configureBackgroundColor {
    UIColor *color = [ICMColor colorWithHexString:self.screen.backgroundColor];
    self.backgroundColor = color; self.view.backgroundColor = color;
}
- (void)createScrollView {
    self.scrollView = [UIScrollView new];
    self.scrollView.accessibilityIdentifier = @"content scroll view";
    self.scrollView.delegate = self;
    self.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    [self.view addSubview:self.scrollView];
    [ICMAutoLayout pinView:self.scrollView edges:11 toSameEdgesOfView:self.view];
    [ICMAutoLayout pinView:self.scrollView attribute:NSLayoutAttributeBottom toAttribute:NSLayoutAttributeTop ofItem:self.actionsContainerView];
}
- (void)createScrollViewHelperViews {
    UIView *external = [UIView new];
    external.accessibilityIdentifier = @"external scrollview helper";
    [self.view insertSubview:external atIndex:0];
    [ICMAutoLayout pinView:external edges:NSUIntegerMax toSameEdgesOfView:self.scrollView];
    UIView *internal = [UIView new];
    internal.translatesAutoresizingMaskIntoConstraints = NO;
    internal.accessibilityIdentifier = @"internal scrollview helper";
    [self.scrollView addSubview:internal];
    [ICMAutoLayout pinView:internal edges:NSUIntegerMax toSameEdgesOfView:self.scrollView];
    [internal.heightAnchor constraintGreaterThanOrEqualToAnchor:external.heightAnchor].active = YES;
    [internal.widthAnchor constraintEqualToAnchor:self.view.widthAnchor].active = YES;
    NSLayoutConstraint *height = [internal.heightAnchor constraintEqualToAnchor:external.heightAnchor];
    height.priority = 200; height.active = YES;
}
- (void)setupContentView {
    [self createScrollView]; [self createScrollViewHelperViews];
    self.contentStackView = [[ICMContentStackView alloc] initWithBlocks:self.screen.blocksList context:0 blockDelegate:self.blockDelegate];
    self.contentStackView.accessibilityIdentifier = @"intercom content view";
    [self.scrollView addSubview:self.contentStackView];
    [self layoutContentView];
}
- (void)layoutContentView {
    [ICMAutoLayout constrainView:self.contentStackView toWidth:400 withPriority:998];
    [ICMAutoLayout pinView:self.contentStackView edges:10 toSameEdgesOfView:self.scrollView inset:24];
    [self.contentStackView.centerXAnchor constraintEqualToAnchor:self.scrollView.centerXAnchor].active = YES;
    switch (self.screen.alignment) {
        case 2: [self bottomLayout]; break;
        case 1: [self centerLayout]; break;
        default: [self topLayout]; break;
    }
}
- (void)topLayout {
    CGFloat height = self.navigationController.navigationBar.frame.size.height;
    [ICMAutoLayout pinView:self.contentStackView edges:1 toSameEdgesOfView:self.scrollView inset:height];
    [self.contentStackView.bottomAnchor constraintLessThanOrEqualToAnchor:self.scrollView.bottomAnchor].active = YES;
}
- (void)centerLayout {
    NSLayoutConstraint *center = [self.contentStackView.centerYAnchor constraintEqualToAnchor:self.scrollView.centerYAnchor];
    center.priority = 260; center.active = YES;
    [self.contentStackView.topAnchor constraintGreaterThanOrEqualToAnchor:self.scrollView.topAnchor constant:self.navigationController.navigationBar.frame.size.height].active = YES;
    [self.contentStackView.bottomAnchor constraintLessThanOrEqualToAnchor:self.scrollView.bottomAnchor].active = YES;
}
- (void)bottomLayout {
    [ICMAutoLayout pinView:self.contentStackView edges:4 toSameEdgesOfView:self.scrollView inset:0];
    [self.contentStackView.topAnchor constraintGreaterThanOrEqualToAnchor:self.scrollView.topAnchor constant:self.navigationController.navigationBar.frame.size.height].active = YES;
}
- (void)createFadeView {
    self.fadeView = [UIView new];
    self.fadeView.userInteractionEnabled = NO;
    [self.view addSubview:self.fadeView];
    [ICMAutoLayout pinView:self.fadeView edges:14 toSameEdgesOfView:self.scrollView];
    [ICMAutoLayout constrainView:self.fadeView toHeight:64];
    UIColor *color = [ICMColor colorWithHexString:self.screen.backgroundColor];
    self.gradientLayer = [CAGradientLayer layer];
    self.gradientLayer.locations = @[@0.0, @1.0];
    self.gradientLayer.colors = @[(id)[color colorWithAlphaComponent:0].CGColor, (id)[color colorWithAlphaComponent:1].CGColor];
    [self.fadeView.layer addSublayer:self.gradientLayer];
}
- (void)updateFadeGradient {
    CGFloat contentHeight = self.scrollView.contentSize.height;
    CGFloat frameHeight = self.scrollView.frame.size.height;
    CGFloat threshold = contentHeight - frameHeight - 4;
    self.fadeView.alpha = self.scrollView.contentOffset.y < threshold ? 1 : 0;
}
- (void)setupActionContainerView {
    self.actionsContainerView = [UIView new];
    self.actionsContainerView.accessibilityIdentifier = @"Screen Actions Container";
    self.actionsContainerView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.view addSubview:self.actionsContainerView];
    [self.actionsContainerView.leadingAnchor constraintEqualToAnchor:self.view.leadingAnchor].active = YES;
    [self.actionsContainerView.trailingAnchor constraintEqualToAnchor:self.view.trailingAnchor].active = YES;
    [self.actionsContainerView.bottomAnchor constraintEqualToAnchor:self.view.safeAreaLayoutGuide.bottomAnchor constant:-32].active = YES;
    self.containerViewHeightConstraint = [self.actionsContainerView.heightAnchor constraintEqualToConstant:100];
    self.containerViewHeightConstraint.active = YES;
    [self configureActions];
}
- (void)configureActions {
    ICMScreenActionViewController *controller = [[ICMScreenActionViewController alloc] initWithScreenActions:self.screen.actions screenDelegate:self.screenDelegate];
    [self addViewControllerAsChildViewController:controller];
}
- (void)addViewControllerAsChildViewController:(UIViewController *)controller {
    [self addChildViewController:controller];
    [self.actionsContainerView addSubview:controller.view];
    controller.view.frame = self.actionsContainerView.bounds;
    controller.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    [controller didMoveToParentViewController:self];
}
- (void)preferredContentSizeDidChangeForChildContentContainer:(id<UIContentContainer>)container {
    [super preferredContentSizeDidChangeForChildContentContainer:container];
    CGFloat height = container.preferredContentSize.height;
    self.containerViewHeightConstraint.constant = height;
}
- (void)scrollViewDidScroll:(UIScrollView *)scrollView {
    (void)scrollView;
    [UIView animateWithDuration:0.3 animations:^{ [self updateFadeGradient]; }];
}
@end
