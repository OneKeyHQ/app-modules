#import "ICMFullScreenInAppViewController.h"
#import "ICMFullScreenInAppDependencies.h"
#import "ICMContentStackView.h"
#import "ICMConversation.h"
#import "ICMConversationPart.h"
#import "ICMSeparatorView.h"
#import "ICMAutoLayout.h"
#import "ICMReactions.h"
#import "ICMHTTPClient+ReadAndReactions.h"
#import "ICMMetric.h"
#import "ICMMetricEvent.h"
@interface OutboundContentBlockDelegate (FullScreenConstruction)
- (instancetype)initWithContext:(NSInteger)context viewController:(UIViewController *)controller;
@property (nonatomic, copy) void (^onShouldDismiss)(void);
@end
@implementation ICMFullScreenInAppViewController
- (instancetype)initWithConversation:(ICMConversation *)conversation inAppDelegate:(id<ICMInAppsDelegate>)delegate fullScreenDelegate:(id<ICMFullScreenInAppDelegate>)fullScreenDelegate isInApp:(BOOL)inApp attributes:(ICMInAppAttributes)attributes {
    self = [super init];
    if (self) {
        _fullScreenDelegate = fullScreenDelegate;
        _inAppsDelegate = delegate;
        _conversation = conversation;
        _conversationPart = conversation.conversationParts.lastObject;
        for (ICMConversationPart *part in conversation.conversationParts) if (part.partStyle == 2) { _conversationPart = part; break; }
        _isInApp = inApp;
        _attributes = attributes;
    }
    return self;
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; }
- (void)loadView {
    [super loadView];
    self.view = [UIView new];
    self.view.translatesAutoresizingMaskIntoConstraints = NO;
    UIColor *clear = UIColor.clearColor;
    self.view.backgroundColor = clear;
    self.headerController = [[ICMInAppHeaderViewController alloc] initWithAvatarDiameter:self.attributes.avatarDiameter];
    [self addChildViewController:self.headerController];
    [self.view addSubview:self.headerController.view];
    [self.headerController didMoveToParentViewController:self];
    self.headerController.delegate = self;
    [self addFooterView];
    self.blockListScrollView = [UIScrollView new];
    self.blockListScrollView.translatesAutoresizingMaskIntoConstraints = NO;
    self.blockListScrollView.alwaysBounceVertical = YES;
    [self.view addSubview:self.blockListScrollView];
    self.blockDelegate = [[OutboundContentBlockDelegate alloc] initWithContext:2 viewController:self];
    __weak typeof(self) weakSelf = self;
    self.blockDelegate.onShouldDismiss = ^{ typeof(self) owner = weakSelf; [owner.fullScreenDelegate closePressed]; };
    self.blockListStackView = [[ICMContentStackView alloc] initWithBlocks:self.conversationPart.blocks context:2 blockDelegate:self.blockDelegate];
    self.blockListStackView.translatesAutoresizingMaskIntoConstraints = NO;
    [self.blockListScrollView addSubview:self.blockListStackView];
    [self.blockListStackView renderContent];
    [ICMAutoLayout pinView:self.view edges:NSUIntegerMax toSameEdgesOfView:self.view];
    [ICMAutoLayout pinView:self.headerController.view edges:0xb toSameEdgesOfView:self.view];
    [ICMAutoLayout constrainView:self.headerController.view toHeight:self.attributes.headerHeight];
    ICMSeparatorView *separator = [ICMSeparatorView alloc];
    separator = [separator initWithColor:[self.fullScreenDelegate headerSeparatorColor]];
    [self.headerController.view addSubview:separator];
    [separator pinToEdge:4 ofView:self.headerController.view];
    [ICMAutoLayout pinView:self.blockListScrollView edges:0xa toSameEdgesOfView:self.view];
    [ICMAutoLayout pinView:self.blockListScrollView attribute:NSLayoutAttributeTop toAttribute:NSLayoutAttributeBottom ofItem:self.headerController.view];
    UIEdgeInsets insets = self.attributes.blockListInsets;
    [self.blockListStackView.topAnchor constraintEqualToAnchor:self.blockListScrollView.contentLayoutGuide.topAnchor constant:insets.top].active = YES;
    [self.blockListStackView.leadingAnchor constraintEqualToAnchor:self.blockListScrollView.contentLayoutGuide.leadingAnchor constant:insets.left].active = YES;
    [self.blockListStackView.trailingAnchor constraintEqualToAnchor:self.blockListScrollView.contentLayoutGuide.trailingAnchor constant:-insets.right].active = YES;
    [self.blockListStackView.bottomAnchor constraintEqualToAnchor:self.blockListScrollView.contentLayoutGuide.bottomAnchor constant:-insets.bottom].active = YES;
    [self.blockListStackView.widthAnchor constraintEqualToAnchor:self.blockListScrollView.frameLayoutGuide.widthAnchor constant:-(insets.left + insets.right)].active = YES;
    UIView *footer = nil;
    BOOL hasFooter = NO;
    if (self.reactionsController) {
        hasFooter = YES;
        footer = self.reactionsController.view;
        [ICMAutoLayout pinView:self.blockListScrollView attribute:NSLayoutAttributeBottom toAttribute:NSLayoutAttributeTop ofItem:self.reactionsController.view];
        [ICMAutoLayout constrainView:self.reactionsController.view toHeight:self.attributes.footerHeight];
    } else if (self.fakeComposerView) {
        hasFooter = YES;
        footer = self.fakeComposerView;
        [ICMAutoLayout pinView:self.blockListScrollView attribute:NSLayoutAttributeBottom toAttribute:NSLayoutAttributeTop ofItem:self.fakeComposerView];
    }
    if (hasFooter) {
        [ICMAutoLayout pinView:footer edges:0xe toSameEdgesOfView:self.view];
        [self.view bringSubviewToFront:footer];
    } else [ICMAutoLayout pinView:self.blockListScrollView attribute:NSLayoutAttributeBottom toAttribute:NSLayoutAttributeBottom ofItem:self.view];
}
- (void)addFooterView {
    if (!self.isInApp) return;
    ICMReactionsSet *set = self.conversationPart.reactionSet;
    if (set) {
        ICMReactionsViewController *controller = [ICMReactionsViewController alloc];
        controller = [controller initWithReactionSet:set separatorColor:[self.fullScreenDelegate footerSeparatorColor] reactionsLabel:nil delegate:self];
        self.reactionsController = controller;
        UIView *view = self.reactionsController.view;
        UIColor *clear = UIColor.clearColor;
        view.backgroundColor = clear;
        view.translatesAutoresizingMaskIntoConstraints = NO;
        view.alpha = 1;
        [self.view addSubview:view];
    } else if (self.conversation.messageComposerShouldBeDisplayed) {
        ICMFakeComposerView *composer = [ICMFakeComposerView alloc];
        composer = [composer initWithInactiveTextColor:[self.fullScreenDelegate fakeComposerInactiveTextColor] horizontalPadding:self.attributes.fakeComposerHorizontalPadding];
        self.fakeComposerView = composer;
        UIColor *clear = UIColor.clearColor;
        self.fakeComposerView.backgroundColor = clear;
        [self.fakeComposerView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(openPressed)]];
        self.fakeComposerView.translatesAutoresizingMaskIntoConstraints = NO;
        [self.view addSubview:self.fakeComposerView];
        ICMSeparatorView *separator = [ICMSeparatorView alloc];
        separator = [separator initWithColor:[self.fullScreenDelegate footerSeparatorColor]];
        [self.fakeComposerView addSubview:separator];
        [separator pinToEdge:1 ofView:self.fakeComposerView];
    }
}
- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(closePressed) name:@"kICMIntercomDeepLinkNotification" object:nil];
    if ([self.inAppsDelegate respondsToSelector:@selector(conversationShown:asFull:)]) [self.inAppsDelegate conversationShown:self.conversation asFull:YES];
}
- (void)viewDidAppear:(BOOL)animated { [super viewDidAppear:animated]; if (!self.conversation.read) [ICMHTTPBridge markConversationAsRead:self.conversation]; }
- (CGFloat)headerCloseButtonRightPadding { return self.attributes.headerCloseButtonRightPadding; }
- (void)didCalculateBlocksSize:(CGSize)size {
    if ([self.fullScreenDelegate respondsToSelector:@selector(didCalculateBlocksSize:)]) [self.fullScreenDelegate didCalculateBlocksSize:size];
}
- (void)clipBlocksList {
    CAShapeLayer *shape = [CAShapeLayer new];
    CGRect bounds = self.blockListScrollView.bounds;
    CGFloat width = CGRectGetWidth(bounds);
    CGFloat height = CGRectGetHeight(bounds);
    CGRect rect = CGRectMake(bounds.origin.x, bounds.origin.y, width, height - self.attributes.footerHeight);
    CGPathRef path = CGPathCreateWithRect(rect, NULL);
    shape.path = path;
    CGPathRelease(path);
    shape.fillColor = UIColor.blackColor.CGColor;
    shape.shouldRasterize = YES;
    self.blockListScrollView.layer.mask = shape;
    self.blockListScrollView.layer.masksToBounds = YES;
}
- (void)closePressed {
    [self.fullScreenDelegate closePressed];
    ICMMetric *metric = [ICMMetric sharedInstance];
    [metric addEvent:[ICMMetricEvent eventWithAction:@"closed" object:@"message" place:self.isInApp ? @"in_app" : @"messenger" context:@"from_full" additionalInfo:@{@"conversation":self.conversation, @"conversation_part":self.conversationPart}]];
}
- (void)openPressed { [self.fullScreenDelegate openPressed]; }
- (void)reactionsViewController:(ICMReactionsViewController *)controller reactedWithOption:(ICMReactionsOption *)option {
    (void)controller;
    [ICMHTTPBridge reactToConversation:self.conversation reactionIndex:option.index.integerValue fromInApp:YES];
}
@end
