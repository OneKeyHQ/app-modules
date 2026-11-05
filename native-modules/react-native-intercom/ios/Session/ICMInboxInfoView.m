#import "ICMInboxInfoView.h"
#import "ICMAvatarDependencies.h"
#import "ICMAutoLayout.h"
@implementation ICMInboxInfoView
- (instancetype)initWithTitle:(NSString *)title message:(NSString *)message showRetryButton:(BOOL)retry delegate:(id<ICMErrorViewDelegate>)delegate {
    self = [super init];
    if (self) {
        self.backgroundColor = ICMRecoveredDefaultThemeProvider.shared.theme.base;
        for (int index = 0; index < 6; index++) [self createGhostItemAtIndex:index];
        self.gradientView = [[ICMGradientView alloc] initWithFrame:self.bounds];
        self.gradientView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
        self.gradientView.layer.colors = @[(id)[ICMRecoveredDefaultThemeProvider.shared.theme.base colorWithAlphaComponent:0].CGColor, (id)ICMRecoveredDefaultThemeProvider.shared.theme.base.CGColor];
        self.gradientView.layer.endPoint = CGPointMake(.5, .4);
        [self addSubview:self.gradientView];
        self.errorView = [[ICMErrorView alloc] initWithTitle:title message:message showActionButton:retry actionButtonTitle:[ICMTranslationManager.messenger translationForKey:@"reload"]];
        self.errorView.translatesAutoresizingMaskIntoConstraints = NO;
        self.errorView.delegate = delegate;
        [self addSubview:self.errorView];
        [self createConstraints];
    }
    return self;
}
- (void)createGhostItemAtIndex:(int)index {
    UIColor *color = ICMRecoveredDefaultThemeProvider.shared.theme.neutralContainer;
    UIView *container = [UIView new];
    container.backgroundColor = UIColor.clearColor;
    container.alpha = .4;
    container.translatesAutoresizingMaskIntoConstraints = NO;
    UIView *avatar = [[UIView alloc] initWithFrame:CGRectMake(24, 16, 32, 32)];
    avatar.backgroundColor = color;
    avatar.layer.cornerRadius = avatar.frame.size.width * .5;
    [container addSubview:avatar];
    UIView *name = [[UIView alloc] initWithFrame:CGRectMake(80, 26, 72, 10)];
    name.backgroundColor = color;
    name.layer.cornerRadius = name.frame.size.height * .5;
    [container addSubview:name];
    UIView *body = [[UIView alloc] initWithFrame:CGRectMake(80, 49, 210, 10)];
    body.backgroundColor = color;
    body.layer.cornerRadius = body.frame.size.height * .5;
    [container addSubview:body];
    UIView *time = [[UIView alloc] initWithFrame:CGRectMake(0, 26, 30, 10)];
    time.backgroundColor = color;
    time.layer.cornerRadius = time.frame.size.height * .5;
    time.translatesAutoresizingMaskIntoConstraints = NO;
    [container addSubview:time];
    [ICMAutoLayout pinView:time attribute:NSLayoutAttributeTop toAttribute:NSLayoutAttributeTop ofItem:container withConstant:26];
    [ICMAutoLayout pinView:time attribute:NSLayoutAttributeRight toAttribute:NSLayoutAttributeRight ofItem:container withConstant:-10];
    [ICMAutoLayout constrainView:time toSize:time.frame.size];
    UIView *divider = [[UIView alloc] initWithFrame:CGRectMake(0, 26, 0, .5)];
    divider.backgroundColor = color;
    divider.translatesAutoresizingMaskIntoConstraints = NO;
    [container addSubview:divider];
    [ICMAutoLayout pinView:divider attribute:NSLayoutAttributeTop toAttribute:NSLayoutAttributeTop ofItem:container withConstant:80 - divider.frame.size.height];
    [ICMAutoLayout pinView:divider attribute:NSLayoutAttributeLeft toAttribute:NSLayoutAttributeLeft ofItem:container withConstant:16];
    [ICMAutoLayout pinView:divider attribute:NSLayoutAttributeRight toAttribute:NSLayoutAttributeRight ofItem:container withConstant:0];
    [ICMAutoLayout constrainView:divider toHeight:divider.frame.size.height];
    [self addSubview:container];
    [ICMAutoLayout constrainView:container toHeight:80];
    int32_t offset = (int32_t)((uint32_t)index * 80u);
    [ICMAutoLayout pinView:container attribute:NSLayoutAttributeTop toAttribute:NSLayoutAttributeTop ofItem:self withConstant:(double)offset];
    [ICMAutoLayout pinView:container attribute:NSLayoutAttributeLeft toAttribute:NSLayoutAttributeLeft ofItem:self withConstant:0];
    [ICMAutoLayout pinView:container attribute:NSLayoutAttributeRight toAttribute:NSLayoutAttributeRight ofItem:self withConstant:0];
}
- (void)createConstraints {
    [ICMAutoLayout centerView:self.errorView inContainerOnAxis:NSLayoutAttributeCenterY];
    [NSLayoutConstraint activateConstraints:@[[self.errorView.leftAnchor constraintEqualToAnchor:self.safeAreaLayoutGuide.leftAnchor constant:10], [self.errorView.rightAnchor constraintEqualToAnchor:self.safeAreaLayoutGuide.rightAnchor constant:-10]]];
}
- (void)setAlphaAnimated:(CGFloat)alpha completion:(void (^)(BOOL))completion {
    [UIView animateWithDuration:.3 delay:0 options:UIViewAnimationOptionBeginFromCurrentState animations:^{ self.alpha = alpha; } completion:completion];
}
@end
