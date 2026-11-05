#import "ICMScreenActionView.h"
#import "ICMAutoLayout.h"
#import "ICMUtils+Fonts.h"
#import "ICMConfig.h"
#import "ICMPresentationManager.h"
#import "ICMConversation.h"
#import "ICMLinkOpenerImports.h"
#import "ICMRootDestinationDependencies.h"
#import "../Intercom.h"
@interface ICMRecoveredHelpCenterMetricService (CarouselActions)
+ (void)addOpenedFromCarousel;

@end
@implementation ICMScreenActionView
- (instancetype)initWithScreenActions:(NSArray<ICMScreenAction *> *)actions screenDelegate:(id<ICMScreenDelegate>)delegate {
    self = [super init];
    if (self) { _screenActions = actions; _screenDelegate = delegate; [self configureView]; }
    return self;
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
- (void)configureView { [self setupStackView]; [self setUpScreenActionButtons]; }
- (void)setupStackView {
    self.stackView = [UIStackView new];
    self.stackView.axis = UILayoutConstraintAxisVertical;
    self.stackView.spacing = 10;
    [self addSubview:self.stackView];
    [ICMAutoLayout pinView:self.stackView edges:5 toSameEdgesOfView:self inset:0 withPriority:750];
    [ICMAutoLayout constrainView:self.stackView toWidth:400 withPriority:998];
    NSLayoutConstraint *leading = [NSLayoutConstraint constraintWithItem:self.stackView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:self attribute:NSLayoutAttributeLeading multiplier:1 constant:24];
    leading.priority = 999; leading.active = YES;
    [ICMAutoLayout centerView:self.stackView inContainerOnAxis:NSLayoutAttributeCenterX];
    [ICMAutoLayout centerView:self.stackView inContainerOnAxis:NSLayoutAttributeCenterY];
}
- (void)setUpScreenActionButtons {
    for (ICMScreenAction *action in self.screenActions) [self addButtonForScreenAction:action];
}
- (ICMScreenActionButton *)addButtonForScreenAction:(ICMScreenAction *)action {
    ICMScreenActionButton *button = [[ICMScreenActionButton alloc] initWithTitle:action.title style:0 backgroundColor:action.backgroundColor foregroundColor:action.textColor];
    button.titleLabel.font = [ICMUtils fontOfSize:17 weight:2];
    button.screenAction = action;
    [button addTarget:self action:@selector(carouselButtonTapped:) forControlEvents:UIControlEventTouchUpInside];
    button.accessibilityIdentifier = [self accessibilityIdentifierForScreenActionType:action.type];
    [ICMAutoLayout constrainView:button toHeight:48 withPriority:750];
    [self.stackView addArrangedSubview:button];
    return button;
}
- (NSString *)accessibilityIdentifierForScreenActionType:(NSUInteger)type {
    static NSString *const names[] = {@"unknown permission", @"continue", @"skip", @"start chat", @"dismiss", @"link", @"push permission", @"location permission", @"photos permission", @"camera permission", @"microphone permission"};
    NSUInteger index = (NSUInteger)type - 1;
    NSString *name = index < 11 ? names[index] : @"unknown";
    return [NSString stringWithFormat:@"intercom %@ screen action button", name];
}
- (void)carouselButtonTapped:(ICMScreenActionButton *)sender {
    NSUInteger type = sender.screenAction.type;
    id<ICMScreenDelegate> delegate = self.screenDelegate;
    if (type == 3) [delegate goToNextScreenFromContext:2];
    else if (type == 4 || type == 5 || type == 6) {
        [delegate actionButtonWasTappedForScreen:sender.screenAction.screenId];
        delegate = nil;
        if (type == 4) [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(intercomWindowDidHide) name:IntercomWindowDidHideNotification object:nil];
        [self.screenDelegate completeCarouselFromContext:0];
        if (type == 6) {
            if ([[ICMLinkOpener shared] isHelpCenterLink:sender.screenAction.uri]) {
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 500000000), dispatch_get_main_queue(), ^{
                    [[ICMLinkOpener shared] openLink:sender.screenAction.uri];
                    [ICMRecoveredHelpCenterMetricService addOpenedFromCarousel];
                });
            } else [[ICMLinkOpener shared] openLink:sender.screenAction.uri];
        }
    } else [delegate goToNextScreenFromContext:0];
}
- (void)intercomWindowDidHide {
    if ([ICMConfig sharedConfig].inboundMessages) [[ICMPresentationManager sharedInstance] showConversation:[ICMConversation new]];
}
@end
