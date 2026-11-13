#import <XCTest/XCTest.h>

#import "RNCCollapsiblePagerViewComponentView.h"
#import "RNCPagerViewComponentView.h"
#import <React/RCTViewComponentView.h>
#import <react/renderer/components/pagerview/Props.h>
#import <react/renderer/components/view/ViewProps.h>

using namespace facebook::react;

@interface RNCCollapsiblePagerViewComponentView (ScrollEdgeTesting)
- (void)updateTopScrollEdgeForScrollView:(UIScrollView *)scrollView;
- (void)updateScrollEdgeContentMasks;
- (void)layoutSharedHeaders;
- (void)adoptCurrentScrollEdgeForProgrammaticTransition:(BOOL)allowExternalOverlay;
- (BOOL)resolveExternalTopOverlay;
- (void)beginProgrammaticScrollEdgesToIndex:(NSInteger)index allowExternalOverlay:(BOOL)allowExternalOverlay;
- (void)outerPagerDidUpdateOverlayOwner:(BOOL)active transitioning:(BOOL)transitioning;
- (void)restorePinnedStickyHeader;
- (void)finishProgrammaticScrollEdges;
- (void)clearProgrammaticScrollEdges;
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView;
- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate;
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView;
- (void)goTo:(NSInteger)index animated:(BOOL)animated;
- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView;
- (void)emitPageSelected:(NSInteger)position;
- (void)attachScrollObserverForCurrentPage;
- (void)syncScrollEdgeAppearance;
@end

@interface UIView (NativeTabReservationTesting)
- (void)updateItemsJSON:(NSString *)itemsJSON;
- (void)updateStyleWithHeight:(CGFloat)height
                      variant:(NSString *)variant
     contentPaddingHorizontal:(CGFloat)contentPaddingHorizontal
       contentPaddingVertical:(CGFloat)contentPaddingVertical
                  itemSpacing:(CGFloat)itemSpacing
                     fontSize:(CGFloat)fontSize
                   fontFamily:(NSString *)fontFamily
              backgroundColor:(UIColor *)backgroundColor
              activeTextColor:(UIColor *)activeTextColor
            inactiveTextColor:(UIColor *)inactiveTextColor
               indicatorColor:(UIColor *)indicatorColor
              indicatorHeight:(CGFloat)indicatorHeight
              indicatorBottom:(CGFloat)indicatorBottom;
- (void)setProgress:(CGFloat)progress;
- (void)setLeftToRight:(BOOL)leftToRight;
@end

@interface RNCCollapsiblePagerResizeSelectionSpy : RNCCollapsiblePagerViewComponentView
@property (nonatomic, strong) NSMutableArray<NSNumber *> *selectedIndexes;
@end

@implementation RNCCollapsiblePagerResizeSelectionSpy
- (void)emitPageSelected:(NSInteger)position
{
  if (_selectedIndexes == nil) _selectedIndexes = [NSMutableArray new];
  [_selectedIndexes addObject:@(position)];
  [super emitPageSelected:position];
}
@end

// Model the equivalent fresh UIColor instances observed on NativeListCollectionView.
@interface RNCEquivalentBackgroundScrollView : UIScrollView
@end

@implementation RNCEquivalentBackgroundScrollView
- (UIColor *)backgroundColor
{
  UIColor *color = super.backgroundColor;
  return color == nil ? nil : [[UIColor alloc] initWithCGColor:color.CGColor];
}
@end

@interface RNCAlignedTakeoverPan : UIPanGestureRecognizer
@property (nonatomic) UIGestureRecognizerState observedState;
@end

@implementation RNCAlignedTakeoverPan
- (UIGestureRecognizerState)state { return _observedState; }
@end

@interface RNCAlignedTakeoverScrollView : UIScrollView
@property (nonatomic) BOOL observedDragging;
@property (nonatomic, strong) RNCAlignedTakeoverPan *observedPan;
@end

@implementation RNCAlignedTakeoverScrollView
- (BOOL)isDragging { return _observedDragging; }
- (UIPanGestureRecognizer *)panGestureRecognizer
{
  return _observedPan ?: super.panGestureRecognizer;
}
@end

@interface RNCCollapsiblePagerHitTestTests : XCTestCase
@property (nonatomic, strong) UIView *root;
@property (nonatomic, strong) UIButton *toolbar;
@property (nonatomic, strong) RNCCollapsiblePagerViewComponentView *pager;
@property (nonatomic, strong) UIButton *sticky;
@end

@implementation RNCCollapsiblePagerHitTestTests

- (void)setPointerEvents:(PointerEventsMode)mode
{
  auto props = std::make_shared<RNCCollapsiblePagerViewProps>();
  props->nativeSmoothHeaderScrollEnabled = true;
  props->pointerEvents = mode;
  [self.pager updateProps:props oldProps:nullptr];
}

- (void)setUp
{
  [super setUp];
  self.root = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 600)];
  self.toolbar = [[UIButton alloc] initWithFrame:CGRectMake(0, 36, 320, 64)];
  [self.root addSubview:self.toolbar];
  self.pager = [[RNCCollapsiblePagerViewComponentView alloc] init];
  [self setPointerEvents:PointerEventsMode::Auto];
  self.pager.frame = CGRectMake(0, 100, 320, 500);
  [self.root addSubview:self.pager];

  // Model smooth mode's header reparenting and its collapsed, clipped content.
  UIView *scroll = [[UIView alloc] initWithFrame:self.pager.bounds];
  [self.pager addSubview:scroll];
  UIView *header = [self.pager valueForKey:@"sharedHeaderHostView"];
  for (UIView *child in header.subviews.copy) [child removeFromSuperview];
  [scroll addSubview:header];
  header.frame = CGRectMake(0, -64, 320, 108);
  [header addSubview:[[UIButton alloc] initWithFrame:CGRectMake(0, 0, 320, 64)]];
  self.sticky = [[UIButton alloc] initWithFrame:CGRectMake(0, 140, 320, 44)];
  [header addSubview:self.sticky];
}

- (void)testCollapsedHeaderDoesNotInterceptSiblingToolbar
{
  XCTAssertEqual([self.root hitTest:CGPointMake(20, 80) withEvent:nil], self.toolbar);
}

- (void)testNativeTabReservationKeepsReactToolbarAboveTabsAndResetsInRTL
{
  UIView *sharedHost = [self.pager valueForKey:@"sharedHeaderHostView"];
  UIView *tabBar = [self.pager valueForKey:@"nativeTabBarView"];
  [tabBar updateItemsJSON:@"[{\"key\":\"a\",\"title\":\"Overview\"},"
    @"{\"key\":\"b\",\"title\":\"Ethereum\"},"
    @"{\"key\":\"c\",\"title\":\"BNB Chain\"},"
    @"{\"key\":\"d\",\"title\":\"Polygon\"}]"];
  RCTViewComponentView *sticky = [RCTViewComponentView new];
  auto stickyProps = std::make_shared<ViewProps>();
  stickyProps->pointerEvents = PointerEventsMode::BoxNone;
  [sticky updateProps:stickyProps oldProps:nullptr];
  UIButton *trailingControl = [[UIButton alloc] initWithFrame:CGRectMake(250, 0, 70, 44)];
  [sticky addSubview:trailingControl];
  [sharedHost addSubview:sticky];
  [sharedHost addSubview:tabBar];
  [self.pager setValue:sticky forKey:@"stickyHeaderView"];
  [self.pager setValue:@0 forKey:@"headerHeight"];
  [self.pager setValue:@44 forKey:@"stickyHeaderHeight"];
  tabBar.hidden = NO;

  [self.pager setValue:@64 forKey:@"nativeTabBarTrailingReservedWidth"];
  [self.pager layoutSharedHeaders];
  XCTAssertEqualWithAccuracy(CGRectGetWidth(sticky.frame), 320, 0.001);
  XCTAssertEqualWithAccuracy(CGRectGetWidth(tabBar.frame), 256, 0.001);
  XCTAssertEqualWithAccuracy(CGRectGetMinX(tabBar.frame), 0, 0.001);
  XCTAssertEqual(sharedHost.subviews.lastObject, sticky);
  XCTAssertEqual([sharedHost hitTest:CGPointMake(300, 22) withEvent:nil], trailingControl);
  XCTAssertTrue([tabBar pointInside:CGPointMake(250, 22) withEvent:nil]);
  [tabBar layoutIfNeeded];
  [tabBar setProgress:3];
  UIScrollView *tabScroll = (UIScrollView *)tabBar.subviews.firstObject;
  NSArray<UIButton *> *buttons = [tabScroll.subviews filteredArrayUsingPredicate:
    [NSPredicate predicateWithBlock:^BOOL(UIView *view, NSDictionary *bindings) {
      return [view isKindOfClass:UIButton.class];
    }]];
  UIButton *lastTab = buttons.lastObject;
  XCTAssertNotNil(lastTab);
  XCTAssertGreaterThanOrEqual(CGRectGetMinX(lastTab.frame), tabScroll.contentOffset.x - 1);
  XCTAssertLessThanOrEqual(CGRectGetMaxX(lastTab.frame),
    tabScroll.contentOffset.x + CGRectGetWidth(tabScroll.bounds) + 1);

  [self.pager setValue:@"rtl" forKey:@"layoutDirection"];
  [tabBar setLeftToRight:NO];
  trailingControl.frame = CGRectMake(0, 0, 70, 44);
  [self.pager layoutSharedHeaders];
  XCTAssertEqualWithAccuracy(CGRectGetMinX(tabBar.frame), 64, 0.001);
  XCTAssertEqualWithAccuracy(CGRectGetMaxX(tabBar.frame), 320, 0.001);
  XCTAssertEqual([sharedHost hitTest:CGPointMake(20, 22) withEvent:nil], trailingControl);

  [self.pager setValue:@500 forKey:@"nativeTabBarTrailingReservedWidth"];
  [self.pager layoutSharedHeaders];
  XCTAssertEqualWithAccuracy(CGRectGetMinX(tabBar.frame), 320, 0.001);
  XCTAssertEqualWithAccuracy(CGRectGetWidth(tabBar.frame), 0, 0.001);

  [self.pager setValue:@0 forKey:@"nativeTabBarTrailingReservedWidth"];
  [self.pager layoutSharedHeaders];
  XCTAssertEqualWithAccuracy(CGRectGetMinX(tabBar.frame), 0, 0.001);
  XCTAssertEqualWithAccuracy(CGRectGetWidth(tabBar.frame), 320, 0.001);
  XCTAssertEqual(sharedHost.subviews.lastObject, tabBar);
}

- (void)testNativeTabReservationPropsRestoreDefaultOnRemoval
{
  auto reservedProps = std::make_shared<RNCCollapsiblePagerViewProps>();
  reservedProps->nativeSmoothHeaderScrollEnabled = true;
  reservedProps->nativeTabBarTrailingReservedWidth = 64;
  [self.pager updateProps:reservedProps oldProps:nullptr];
  XCTAssertEqualWithAccuracy(
    [[self.pager valueForKey:@"nativeTabBarTrailingReservedWidth"] doubleValue], 64, 0.001);

  auto defaultProps = std::make_shared<RNCCollapsiblePagerViewProps>();
  defaultProps->nativeSmoothHeaderScrollEnabled = true;
  [self.pager updateProps:defaultProps oldProps:reservedProps];
  XCTAssertEqualWithAccuracy(
    [[self.pager valueForKey:@"nativeTabBarTrailingReservedWidth"] doubleValue], 0, 0.001);
}

- (void)testSegmentedNativeTabsFollowFractionalProgressAndResetToUnderline
{
  UIView *tabBar = [self.pager valueForKey:@"nativeTabBarView"];
  [tabBar updateItemsJSON:@"[{\"key\":\"recent\",\"title\":\"Recents\"},"
    @"{\"key\":\"account\",\"title\":\"Accounts\"},"
    @"{\"key\":\"book\",\"title\":\"Address Book\"}]"];
  [tabBar updateStyleWithHeight:56 variant:@"segmented"
    contentPaddingHorizontal:20 contentPaddingVertical:12 itemSpacing:0
    fontSize:14 fontFamily:nil backgroundColor:UIColor.lightGrayColor
    activeTextColor:UIColor.blackColor inactiveTextColor:UIColor.grayColor
    indicatorColor:UIColor.whiteColor indicatorHeight:2 indicatorBottom:0];
  tabBar.frame = CGRectMake(0, 0, 320, 56);
  [tabBar layoutIfNeeded];
  UIScrollView *scroll = (UIScrollView *)tabBar.subviews.firstObject;
  UIView *track = scroll.subviews.firstObject;
  UIView *thumb = [tabBar valueForKey:@"indicatorView"];
  NSArray<UIButton *> *buttons = [scroll.subviews filteredArrayUsingPredicate:
    [NSPredicate predicateWithBlock:^BOOL(UIView *view, NSDictionary *bindings) {
      return [view isKindOfClass:UIButton.class];
    }]];
  XCTAssertEqual(buttons.count, 3);
  XCTAssertFalse(scroll.scrollEnabled);
  XCTAssertEqualWithAccuracy(CGRectGetMinX(track.frame), 20, 0.01);
  XCTAssertEqualWithAccuracy(CGRectGetHeight(track.frame), 32, 0.01);
  XCTAssertEqualWithAccuracy(CGRectGetWidth(buttons[0].frame),
    CGRectGetWidth(buttons[1].frame), 0.01);
  [tabBar setProgress:0];
  CGFloat firstX = CGRectGetMinX(thumb.frame);
  [tabBar setProgress:1];
  CGFloat secondX = CGRectGetMinX(thumb.frame);
  [tabBar setProgress:0.25];
  XCTAssertEqualWithAccuracy(CGRectGetMinX(thumb.frame), firstX + (secondX - firstX) * 0.25, 0.01);
  [tabBar setProgress:0.5];
  XCTAssertEqualWithAccuracy(CGRectGetMinX(thumb.frame), (firstX + secondX) / 2, 0.01);
  [tabBar setProgress:0];
  XCTAssertEqualWithAccuracy(CGRectGetMinX(thumb.frame), firstX, 0.01);

  [tabBar setLeftToRight:NO];
  [tabBar layoutIfNeeded];
  XCTAssertGreaterThan(CGRectGetMinX(buttons[0].frame), CGRectGetMinX(buttons[1].frame));
  [tabBar updateItemsJSON:@"[{\"key\":\"recent\",\"title\":\"Recents (8)\"},"
    @"{\"key\":\"account\",\"title\":\"Accounts\"}]"];
  [tabBar layoutIfNeeded];
  NSArray<UIButton *> *twoButtons = [scroll.subviews filteredArrayUsingPredicate:
    [NSPredicate predicateWithBlock:^BOOL(UIView *view, NSDictionary *bindings) {
      return [view isKindOfClass:UIButton.class];
    }]];
  XCTAssertEqual(twoButtons.count, 2);
  XCTAssertEqualWithAccuracy(CGRectGetWidth(twoButtons[0].frame),
    CGRectGetWidth(track.frame) / 2, 0.01);
  [tabBar updateStyleWithHeight:56 variant:@"segmented"
    contentPaddingHorizontal:20 contentPaddingVertical:40 itemSpacing:0
    fontSize:14 fontFamily:nil backgroundColor:UIColor.lightGrayColor
    activeTextColor:UIColor.blackColor inactiveTextColor:UIColor.grayColor
    indicatorColor:UIColor.whiteColor indicatorHeight:2 indicatorBottom:0];
  tabBar.frame = CGRectMake(0, 0, 30, 56);
  [tabBar layoutIfNeeded];
  XCTAssertEqualWithAccuracy(CGRectGetMinX(track.frame), 15, 0.01);
  XCTAssertEqualWithAccuracy(CGRectGetWidth(track.frame), 0, 0.01);
  XCTAssertEqualWithAccuracy(CGRectGetMinY(track.frame), 28, 0.01);
  XCTAssertEqualWithAccuracy(CGRectGetHeight(track.frame), 0, 0.01);
  XCTAssertTrue(thumb.hidden);
  [tabBar updateItemsJSON:@"[]"];
  XCTAssertTrue(tabBar.hidden);

  [tabBar updateItemsJSON:@"[{\"key\":\"recent\",\"title\":\"Recents\"}]"];
  [tabBar updateStyleWithHeight:44 variant:@"underline"
    contentPaddingHorizontal:20 contentPaddingVertical:0 itemSpacing:8
    fontSize:16 fontFamily:nil backgroundColor:UIColor.clearColor
    activeTextColor:UIColor.blackColor inactiveTextColor:UIColor.grayColor
    indicatorColor:UIColor.blackColor indicatorHeight:2 indicatorBottom:0];
  tabBar.frame = CGRectMake(0, 0, 320, 44);
  [tabBar layoutIfNeeded];
  XCTAssertTrue(scroll.scrollEnabled);
  XCTAssertTrue(track.hidden);
  XCTAssertEqualWithAccuracy(CGRectGetHeight(thumb.frame), 2, 0.01);
}

- (void)testPinnedChildOutsideHeaderHostStillReceivesViewportTouches
{
  XCTAssertEqual([self.pager hitTest:CGPointMake(20, 90) withEvent:nil], self.sticky);
  [self setPointerEvents:PointerEventsMode::BoxNone];
  XCTAssertEqual([self.pager hitTest:CGPointMake(20, 90) withEvent:nil], self.sticky);
}

- (void)testFabricPointerEventsAreNotBypassed
{
  [self setPointerEvents:PointerEventsMode::None];
  XCTAssertNil([self.pager hitTest:CGPointMake(20, 90) withEvent:nil]);
  [self setPointerEvents:PointerEventsMode::BoxOnly];
  XCTAssertEqual([self.pager hitTest:CGPointMake(20, 90) withEvent:nil], self.pager);
}

- (void)testNoninteractivePagerDoesNotForwardHeaderTouches
{
  self.pager.hidden = YES;
  XCTAssertNil([self.pager hitTest:CGPointMake(20, 90) withEvent:nil]);
  self.pager.hidden = NO;
  self.pager.userInteractionEnabled = NO;
  XCTAssertNil([self.pager hitTest:CGPointMake(20, 90) withEvent:nil]);
  self.pager.userInteractionEnabled = YES;
  self.pager.alpha = 0.005;
  XCTAssertNil([self.pager hitTest:CGPointMake(20, 90) withEvent:nil]);
}

- (void)testExternalOverlayWithoutOuterPagerOwnsItsEdgeAndRegisteredOuterStillGates
{
  if (@available(iOS 26.0, *)) {
    UIWindow *window = [[UIWindow alloc] initWithFrame:self.root.bounds];
    [window addSubview:self.root];
    UIScrollView *scrollView = [[UIScrollView alloc] initWithFrame:self.pager.bounds];
    [self.pager addSubview:scrollView];
    UIScrollEdgeEffectStyle *originalStyle = scrollView.topEdgeEffect.style;
    [self.pager setValue:@YES forKey:@"topScrollEdgeEnabled"];
    [self.pager setValue:@(static_cast<NSInteger>(RNCCollapsiblePagerViewTopScrollEdgeEffect::Hard))
                 forKey:@"topScrollEdgeEffect"];
    [self.pager setValue:@123 forKey:@"externalTopOverlayTag"];
    [self.pager setValue:@64 forKey:@"externalTopOverlayHeight"];
    UIView *container = [self.pager valueForKey:@"containerView"];
    container.frame = self.pager.bounds;
    UIView *overlayHost = [self.pager valueForKey:@"topOverlayHostView"];
    overlayHost.frame = CGRectMake(0, 0, 320, 64);
    overlayHost.hidden = NO;
    self.pager.backgroundColor = UIColor.redColor;
    [self.pager updateTopScrollEdgeForScrollView:scrollView];
    XCTAssertEqual([self.pager valueForKey:@"topEdgeScrollView"], scrollView);
    UIScrollEdgeElementContainerInteraction *interaction =
      [self.pager valueForKey:@"topEdgeInteraction"];
    XCTAssertEqual(interaction.scrollView, scrollView);
    UIView *host = [self.pager valueForKey:@"topEdgeInteractionHost"];
    XCTAssertTrue([host.interactions containsObject:interaction]);
    XCTAssertEqual(scrollView.topEdgeEffect.style, UIScrollEdgeEffectStyle.hardStyle);
    CALayer *cover = [self.pager valueForKey:@"topEdgeSeparatorCover"];
    XCTAssertEqual(cover.superlayer, container.layer);
    XCTAssertEqualWithAccuracy(CGRectGetMaxY(cover.frame), 64, 0.001);
    XCTAssertEqualWithAccuracy(CGRectGetHeight(cover.frame), 1.0 / window.screen.scale, 0.001);
    XCTAssertTrue(CGColorEqualToColor(cover.backgroundColor, UIColor.redColor.CGColor));
    UIEdgeInsets inset = scrollView.contentInset;
    CGPoint offset = scrollView.contentOffset;
    [self.pager setValue:@(static_cast<NSInteger>(RNCCollapsiblePagerViewTopScrollEdgeEffect::Soft))
                 forKey:@"topScrollEdgeEffect"];
    [self.pager updateTopScrollEdgeForScrollView:scrollView];
    XCTAssertEqual(scrollView.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
    XCTAssertNil([self.pager valueForKey:@"topEdgeSeparatorCover"]);
    XCTAssertNil(cover.superlayer);
    XCTAssertEqual([self.pager valueForKey:@"topEdgeInteraction"], interaction);
    XCTAssertTrue(UIEdgeInsetsEqualToEdgeInsets(scrollView.contentInset, inset));
    XCTAssertTrue(CGPointEqualToPoint(scrollView.contentOffset, offset));

    RNCPagerViewComponentView *outer = [RNCPagerViewComponentView new];
    [self.pager setValue:outer forKey:@"externalOuterPager"];
    [self.pager setValue:@NO forKey:@"externalOuterPageActive"];
    [self.pager updateTopScrollEdgeForScrollView:scrollView];
    XCTAssertNil([self.pager valueForKey:@"topEdgeInteraction"]);
    [self.pager setValue:@YES forKey:@"externalOuterPageActive"];
    [self.pager updateTopScrollEdgeForScrollView:scrollView];
    XCTAssertNotNil([self.pager valueForKey:@"topEdgeInteraction"]);
    [self.pager setValue:@YES forKey:@"externalOuterTransitioning"];
    [self.pager updateTopScrollEdgeForScrollView:scrollView];
    XCTAssertNil([self.pager valueForKey:@"topEdgeInteraction"]);

    [self.pager setValue:nil forKey:@"externalOuterPager"];
    [self.pager updateTopScrollEdgeForScrollView:scrollView];
    XCTAssertNotNil([self.pager valueForKey:@"topEdgeInteraction"]);
    [self.pager removeFromSuperview];
    XCTAssertNil([self.pager valueForKey:@"topEdgeInteraction"]);
    XCTAssertNil([self.pager valueForKey:@"topEdgeScrollView"]);
    XCTAssertEqual(scrollView.topEdgeEffect.style, originalStyle);
    [self.root removeFromSuperview];
  } else {
    XCTSkip(@"Scroll-edge interactions require iOS 26 or newer");
  }
}

- (void)testPinnedHardEdgeHidesOnlyTopSeparatorCoverUntilUnpinned
{
  if (@available(iOS 26.0, *)) {
    UIWindow *window = [[UIWindow alloc] initWithFrame:self.root.bounds];
    [window addSubview:self.root];
    [self addTeardownBlock:^{ window.hidden = YES; }];
    window.hidden = NO;
    UIScrollView *scrollView = [[UIScrollView alloc] initWithFrame:self.pager.bounds];
    [self.pager addSubview:scrollView];
    [self.pager setValue:@YES forKey:@"topScrollEdgeEnabled"];
    [self.pager setValue:@(static_cast<NSInteger>(RNCCollapsiblePagerViewTopScrollEdgeEffect::Hard))
                 forKey:@"topScrollEdgeEffect"];
    [self.pager setValue:@123 forKey:@"externalTopOverlayTag"];
    [self.pager setValue:@64 forKey:@"externalTopOverlayHeight"];
    [self.pager setValue:self.sticky forKey:@"stickyHeaderView"];
    UIView *container = [self.pager valueForKey:@"containerView"];
    container.frame = self.pager.bounds;
    UIView *overlayHost = [self.pager valueForKey:@"topOverlayHostView"];
    overlayHost.frame = CGRectMake(0, 0, 320, 64);
    overlayHost.hidden = NO;
    UIView *pinnedHost = [self.pager valueForKey:@"pinnedStickyHostView"];
    pinnedHost.frame = CGRectMake(0, 64, 320, 44);
    pinnedHost.hidden = NO;
    [scrollView addSubview:pinnedHost];

    [self.pager updateTopScrollEdgeForScrollView:scrollView];
    UIScrollEdgeElementContainerInteraction *topInteraction =
      [self.pager valueForKey:@"topEdgeInteraction"];
    CALayer *topCover = [self.pager valueForKey:@"topEdgeSeparatorCover"];
    XCTAssertNotNil(topInteraction);
    XCTAssertNotNil(topCover);
    XCTAssertFalse(topCover.hidden);
    XCTAssertEqual(scrollView.topEdgeEffect.style, UIScrollEdgeEffectStyle.hardStyle);

    [self.pager setValue:@YES forKey:@"stickyHeaderIsPinned"];
    [self.pager updateTopScrollEdgeForScrollView:scrollView];
    UIScrollEdgeElementContainerInteraction *stickyInteraction =
      [self.pager valueForKey:@"stickyEdgeInteraction"];
    CALayer *stickyCover = [self.pager valueForKey:@"stickyEdgeSeparatorCover"];
    XCTAssertEqual([self.pager valueForKey:@"topEdgeInteraction"], topInteraction);
    XCTAssertEqual(topInteraction.scrollView, scrollView);
    XCTAssertEqual(stickyInteraction.scrollView, scrollView);
    XCTAssertEqual([self.pager valueForKey:@"topEdgeSeparatorCover"], topCover);
    XCTAssertTrue(topCover.hidden);
    XCTAssertNotNil(stickyCover);
    XCTAssertFalse(stickyCover.hidden);
    XCTAssertEqual(scrollView.topEdgeEffect.style, UIScrollEdgeEffectStyle.hardStyle);

    [self.pager setValue:@NO forKey:@"stickyHeaderIsPinned"];
    [self.pager updateTopScrollEdgeForScrollView:scrollView];
    XCTAssertEqual([self.pager valueForKey:@"topEdgeInteraction"], topInteraction);
    XCTAssertEqual([self.pager valueForKey:@"topEdgeSeparatorCover"], topCover);
    XCTAssertFalse(topCover.hidden);
    XCTAssertEqual(scrollView.topEdgeEffect.style, UIScrollEdgeEffectStyle.hardStyle);
    [self.root removeFromSuperview];
  } else {
    XCTSkip(@"Scroll-edge interactions require iOS 26 or newer");
  }
}

- (UIScrollView *)preparePinnedLocalHardSource
{
  UIWindow *window = [[UIWindow alloc] initWithFrame:self.root.bounds];
  [window addSubview:self.root];
  window.hidden = NO;
  [self addTeardownBlock:^{
    [self.pager prepareForRecycle];
    window.hidden = YES;
  }];
  UIView *container = [self.pager valueForKey:@"containerView"];
  container.frame = self.pager.bounds;
  UIViewController *page = [UIViewController new];
  page.view = [[UIView alloc] initWithFrame:container.bounds];
  UIScrollView *source = [[UIScrollView alloc] initWithFrame:page.view.bounds];
  source.contentSize = CGSizeMake(320, 1200);
  source.topEdgeEffect.style = UIScrollEdgeEffectStyle.softStyle;
  [page.view addSubview:source];
  [container insertSubview:page.view atIndex:0];
  [[self.pager valueForKey:@"pageControllers"] addObject:page];
  [self.pager setValue:source forKey:@"observedScrollView"];
  [self.pager setValue:source forKey:@"sharedHeaderScrollView"];
  [self.pager setValue:@YES forKey:@"directNativePagerEnabled"];
  [self.pager setValue:@YES forKey:@"topScrollEdgeEnabled"];
  [self.pager setValue:@(static_cast<NSInteger>(RNCCollapsiblePagerViewTopScrollEdgeEffect::Hard))
               forKey:@"topScrollEdgeEffect"];
  UIView *overlayHost = [self.pager valueForKey:@"topOverlayHostView"];
  overlayHost.frame = CGRectMake(0, 0, 320, 50);
  overlayHost.hidden = NO;
  UIView *overlay = [[UIView alloc] initWithFrame:overlayHost.bounds];
  [overlayHost addSubview:overlay];
  [self.pager setValue:overlay forKey:@"topOverlayView"];
  [self.pager setValue:@50 forKey:@"topOverlayHeight"];
  [self.pager setValue:@100 forKey:@"headerHeight"];
  [self.pager setValue:@44 forKey:@"stickyHeaderHeight"];
  [self.pager setValue:@100 forKey:@"headerOffset"];
  UIView *pinnedHost = [self.pager valueForKey:@"pinnedStickyHostView"];
  pinnedHost.frame = CGRectMake(0, 50, 320, 44);
  pinnedHost.hidden = NO;
  [source addSubview:pinnedHost];
  [pinnedHost addSubview:self.sticky];
  [self.pager setValue:self.sticky forKey:@"stickyHeaderView"];
  [self.pager setValue:@YES forKey:@"stickyHeaderIsPinned"];
  [self.pager updateTopScrollEdgeForScrollView:source];
  return source;
}

- (UIScrollView *)prepareDirectDragWithSource:(UIScrollView **)sourceOut
{
  UIScrollView *source = [self preparePinnedLocalHardSource];
  UIView *container = [self.pager valueForKey:@"containerView"];
  UIScrollView *horizontal = [[UIScrollView alloc] initWithFrame:container.bounds];
  horizontal.contentSize = CGSizeMake(960, CGRectGetHeight(container.bounds));
  horizontal.pagingEnabled = YES;
  horizontal.delegate = (id<UIScrollViewDelegate>)self.pager;
  [container insertSubview:horizontal atIndex:0];
  NSMutableArray<UIViewController *> *pages = [self.pager valueForKey:@"pageControllers"];
  UIViewController *sourcePage = pages.firstObject;
  for (NSInteger index = 0; index < 3; index++) {
    UIViewController *page = index == 1 ? sourcePage : [UIViewController new];
    if (index != 1) {
      page.view = [[UIView alloc] initWithFrame:container.bounds];
      UIScrollView *list = [[UIScrollView alloc] initWithFrame:page.view.bounds];
      list.contentSize = CGSizeMake(320, 1200);
      list.topEdgeEffect.style = UIScrollEdgeEffectStyle.softStyle;
      [page.view addSubview:list];
    }
    page.view.frame = CGRectMake(index * 320, 0, 320, CGRectGetHeight(container.bounds));
    [horizontal addSubview:page.view];
    if (index == 0) [pages insertObject:page atIndex:0];
    if (index == 2) [pages addObject:page];
  }
  horizontal.contentOffset = CGPointMake(320, 0);
  [self.pager setValue:horizontal forKey:@"pagerScrollView"];
  [self.pager setValue:@1 forKey:@"currentIndex"];
  [self.pager setValue:@1 forKey:@"destinationIndex"];
  [self.pager setValue:@"ltr" forKey:@"layoutDirection"];
  [self.pager setValue:@YES forKey:@"hasAppliedInitialPage"];
  if (sourceOut != nil) *sourceOut = source;
  return horizontal;
}

- (void)prepareIncomingListsScrolledBeyondBars
{
  NSMutableDictionary *offsets = [self.pager valueForKey:@"pageOffsets"];
  NSArray<UIViewController *> *pages = [self.pager valueForKey:@"pageControllers"];
  for (NSNumber *index in @[@0, @2]) {
    offsets[[NSString stringWithFormat:@"page-%ld", (long)index.integerValue]] = @200;
    UIScrollView *list = (UIScrollView *)pages[index.integerValue].view.subviews.firstObject;
    list.contentOffset = CGPointMake(0, 200);
  }
}

- (UIScrollView *)prepareExternalDirectDragWithSource:(UIScrollView **)sourceOut
                                           overlay:(UIView **)overlayOut
{
  UIScrollView *source = nil;
  UIScrollView *horizontal = [self prepareDirectDragWithSource:&source];
  [self.pager updateTopScrollEdgeForScrollView:nil];
  UIView *local = [self.pager valueForKey:@"topOverlayView"];
  [local removeFromSuperview];
  [self.pager setValue:nil forKey:@"topOverlayView"];
  [self.pager setValue:@0 forKey:@"topOverlayHeight"];
  UIView *overlay = [[UIView alloc] initWithFrame:CGRectMake(0, 100, 320, 50)];
  overlay.tag = 8675309;
  [self.root addSubview:overlay];
  auto props = std::make_shared<RNCCollapsiblePagerViewProps>();
  props->nativeSmoothHeaderScrollEnabled = true;
  props->topScrollEdgeEffect = RNCCollapsiblePagerViewTopScrollEdgeEffect::Hard;
  props->externalTopOverlayTag = overlay.tag;
  [self.pager updateProps:props oldProps:nullptr];
  [self.pager resolveExternalTopOverlay];
  XCTAssertEqual([self.pager valueForKey:@"externalTopOverlayView"], overlay);
  XCTAssertEqualWithAccuracy([[self.pager valueForKey:@"externalTopOverlayHeight"] doubleValue], 50, 0.001);
  XCTAssertEqual(overlay.window, self.pager.window);
  XCTAssertFalse([overlay isDescendantOfView:self.pager]);
  UIView *proxy = [self.pager valueForKey:@"topOverlayHostView"];
  XCTAssertEqual(proxy.window, self.pager.window);
  XCTAssertFalse(proxy.hidden);
  XCTAssertFalse(proxy.userInteractionEnabled);
  [self.pager updateTopScrollEdgeForScrollView:source];
  XCTAssertEqual([[self.pager valueForKey:@"pageControllers"] count], 3);
  XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 1);
  XCTAssertEqualWithAccuracy(horizontal.contentOffset.x, 320, 0.001);
  XCTAssertTrue([[self.pager valueForKey:@"hasAppliedInitialPage"] boolValue]);
  XCTAssertTrue([[self.pager valueForKey:@"directNativePagerEnabled"] boolValue]);
  XCTAssertEqualWithAccuracy([[self.pager valueForKey:@"headerOffset"] doubleValue], 100, 0.001);
  XCTAssertTrue([[self.pager valueForKey:@"stickyHeaderIsPinned"] boolValue]);
  XCTAssertNotNil([self.pager valueForKey:@"stickyEdgeInteraction"]);
  if (sourceOut != nil) *sourceOut = source;
  if (overlayOut != nil) *overlayOut = overlay;
  return horizontal;
}

- (UIScrollView *)prepareNativeResizeCommandPager
{
  [self.pager removeFromSuperview];
  self.pager = [RNCCollapsiblePagerResizeSelectionSpy new];
  [self setPointerEvents:PointerEventsMode::Auto];
  self.pager.frame = CGRectMake(0, 100, 320, 500);
  [self.root addSubview:self.pager];
  UIScrollView *horizontal = [self prepareDirectDragWithSource:NULL];
  XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 1);
  XCTAssertEqualWithAccuracy(CGRectGetWidth(horizontal.bounds), 320, 0.001);
  XCTAssertEqualWithAccuracy(horizontal.contentOffset.x, 320, 0.001);
  XCTAssertTrue(horizontal.scrollEnabled);
  return horizontal;
}

- (void)waitForNativeResizeCommandsToSettle
{
  NSPredicate *settled = [NSPredicate predicateWithBlock:^BOOL(id object, NSDictionary *bindings) {
    return ![[self.pager valueForKey:@"transitioning"] boolValue] &&
      [self.pager valueForKey:@"pendingNativePagerScrollCompletion"] == nil &&
      [[self.pager valueForKey:@"pendingGoToIndex"] integerValue] == -1;
  }];
  XCTNSPredicateExpectation *expectation = [[XCTNSPredicateExpectation alloc]
    initWithPredicate:settled object:self.pager];
  [self waitForExpectations:@[expectation] timeout:3];
}

- (void)testNativeAnimatedCommandResizeRebasesAndDrainsQueueIgnoringLateEnd
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *horizontal = [self prepareNativeResizeCommandPager];
    RNCCollapsiblePagerResizeSelectionSpy *spy = (id)self.pager;
    [self.pager goTo:2 animated:YES];
    id originalPending = [self.pager valueForKey:@"pendingNativePagerScrollCompletion"];
    XCTAssertNotNil(originalPending);
    XCTAssertFalse(horizontal.scrollEnabled);
    XCTAssertEqualWithAccuracy([[self.pager valueForKey:@"pendingNativePagerScrollTarget"] doubleValue], 640, 0.001);
    self.pager.frame = CGRectMake(0, 100, 320, 480);
    [self.pager layoutSubviews];
    XCTAssertEqual([self.pager valueForKey:@"pendingNativePagerScrollCompletion"], originalPending);
    XCTAssertEqual(spy.selectedIndexes.count, 0);
    [self.pager goTo:0 animated:YES];
    XCTAssertEqual([[self.pager valueForKey:@"pendingGoToIndex"] integerValue], 0);
    self.pager.frame = CGRectMake(0, 100, 320.4, 480);
    [self.pager layoutSubviews];
    XCTAssertNil([self.pager valueForKey:@"pendingNativePagerScrollCompletion"]);
    XCTAssertEqualWithAccuracy(horizontal.contentOffset.x, 640.8, 1.0 / horizontal.window.screen.scale);
    XCTAssertGreaterThan(horizontal.contentOffset.x, 640.5);
    XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 2);
    XCTAssertTrue(horizontal.scrollEnabled);
    XCTAssertEqualObjects(spy.selectedIndexes, (@[@2]));
    [self.pager scrollViewDidEndScrollingAnimation:horizontal];
    XCTAssertEqualObjects(spy.selectedIndexes, (@[@2]));
    XCTestExpectation *queued = [self expectationWithDescription:@"queued native command starts"];
    dispatch_async(dispatch_get_main_queue(), ^{
      id pending = [self.pager valueForKey:@"pendingNativePagerScrollCompletion"];
      XCTAssertNotNil(pending);
      XCTAssertNotEqual(pending, originalPending);
      XCTAssertEqualWithAccuracy([[self.pager valueForKey:@"pendingNativePagerScrollTarget"] doubleValue], 0, 0.001);
      XCTAssertGreaterThan(fabs(horizontal.contentOffset.x), 0.5);
      [self.pager scrollViewDidEndScrollingAnimation:horizontal];
      XCTAssertEqual([self.pager valueForKey:@"pendingNativePagerScrollCompletion"], pending);
      XCTAssertEqualObjects(spy.selectedIndexes, (@[@2]));
      [queued fulfill];
    });
    [self waitForExpectations:@[queued] timeout:2];
    // This hostless fixture does not drive the natural scroll animation.
    // Exercise the controlled UIKit end boundary after checking stale-end rejection.
    XCTAssertNotNil([self.pager valueForKey:@"pendingNativePagerScrollCompletion"]);
    [horizontal setContentOffset:CGPointZero animated:NO];
    [self.pager scrollViewDidEndScrollingAnimation:horizontal];
    [self waitForNativeResizeCommandsToSettle];
    XCTAssertEqualWithAccuracy(horizontal.contentOffset.x, 0, 0.001);
    XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 0);
    XCTAssertTrue(horizontal.scrollEnabled);
    XCTAssertEqualObjects(spy.selectedIndexes, (@[@2, @0]));
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
  } else {
    XCTSkip(@"Native local Hard animation requires iOS 27 or newer");
  }
}

- (void)testNativeAnimatedCommandResizeRebasesNonzeroRTLTarget
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *horizontal = [self prepareNativeResizeCommandPager];
    RNCCollapsiblePagerResizeSelectionSpy *spy = (id)self.pager;
    [self.pager setValue:@"rtl" forKey:@"layoutDirection"];
    [self.pager goTo:0 animated:YES];
    XCTAssertNotNil([self.pager valueForKey:@"pendingNativePagerScrollCompletion"]);
    XCTAssertEqualWithAccuracy([[self.pager valueForKey:@"pendingNativePagerScrollTarget"] doubleValue], 640, 0.001);
    self.pager.frame = CGRectMake(0, 100, 400, 500);
    [self.pager layoutSubviews];
    XCTAssertNil([self.pager valueForKey:@"pendingNativePagerScrollCompletion"]);
    XCTAssertEqualWithAccuracy(horizontal.contentOffset.x, 800, 0.001);
    XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 0);
    [self.pager scrollViewDidEndScrollingAnimation:horizontal];
    [self waitForNativeResizeCommandsToSettle];
    XCTAssertEqualObjects(spy.selectedIndexes, (@[@0]));
    XCTAssertTrue(horizontal.scrollEnabled);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
  } else {
    XCTSkip(@"Native local Hard animation requires iOS 27 or newer");
  }
}

- (void)testNativeAnimatedCommandZeroWidthCompletesQueueAndRealignsOnRestore
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *horizontal = [self prepareNativeResizeCommandPager];
    RNCCollapsiblePagerResizeSelectionSpy *spy = (id)self.pager;
    [self.pager goTo:0 animated:YES];
    XCTAssertNotNil([self.pager valueForKey:@"pendingNativePagerScrollCompletion"]);
    [self.pager goTo:2 animated:YES];
    self.pager.frame = CGRectMake(0, 100, 0, 500);
    [self.pager layoutSubviews];
    XCTAssertNil([self.pager valueForKey:@"pendingNativePagerScrollCompletion"]);
    XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 0);
    XCTAssertTrue(horizontal.scrollEnabled);
    [self.pager scrollViewDidEndScrollingAnimation:horizontal];
    [self waitForNativeResizeCommandsToSettle];
    XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 2);
    XCTAssertTrue(horizontal.scrollEnabled);
    XCTAssertEqualObjects(spy.selectedIndexes, (@[@0, @2]));
    self.pager.frame = CGRectMake(0, 100, 400, 500);
    [self.pager layoutSubviews];
    XCTAssertEqualWithAccuracy(horizontal.contentOffset.x, 800, 0.001);
    [self.pager scrollViewDidEndScrollingAnimation:horizontal];
    XCTAssertEqualObjects(spy.selectedIndexes, (@[@0, @2]));
    XCTAssertNil([self.pager valueForKey:@"pendingNativePagerScrollCompletion"]);
    XCTAssertFalse([[self.pager valueForKey:@"transitioning"] boolValue]);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
  } else {
    XCTSkip(@"Native local Hard animation requires iOS 27 or newer");
  }
}

- (void)testExternalDirectDragPreservesProxyIdentityAndSettles
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *source = nil;
    UIView *overlay = nil;
    UIScrollView *horizontal = [self prepareExternalDirectDragWithSource:&source overlay:&overlay];
    [self prepareIncomingListsScrolledBeyondBars];
    UIView *proxy = [self.pager valueForKey:@"topOverlayHostView"];
    UIView *pinned = [self.pager valueForKey:@"pinnedStickyHostView"];
    UIScrollEdgeElementContainerInteraction *top = [self.pager valueForKey:@"topEdgeInteraction"];
    UIScrollEdgeElementContainerInteraction *sticky = [self.pager valueForKey:@"stickyEdgeInteraction"];
    UIView *marker = [self.pager valueForKey:@"topEdgeMarker"];
    UIView *stickyMarker = [self.pager valueForKey:@"stickyEdgeMarker"];
    NSArray<UIViewController *> *pages = [self.pager valueForKey:@"pageControllers"];
    UIScrollView *target = (UIScrollView *)pages[2].view.subviews.firstObject;
    XCTAssertNotNil(top);
    XCTAssertNotNil(sticky);
    XCTAssertTrue([proxy.interactions containsObject:top]);
    XCTAssertEqual(overlay.interactions.count, 0);
    [self.pager adoptCurrentScrollEdgeForProgrammaticTransition:NO];
    [self.pager beginProgrammaticScrollEdgesToIndex:2 allowExternalOverlay:NO];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
    XCTAssertEqual([self.pager valueForKey:@"topEdgeInteraction"], top);

    [self.pager scrollViewWillBeginDragging:horizontal];
    NSArray *owners = [self.pager valueForKey:@"programmaticScrollEdgeOwners"];
    XCTAssertEqual(owners.count, 3);
    id sourceOwner = owners.firstObject;
    XCTAssertEqual([sourceOwner valueForKey:@"topInteraction"], top);
    XCTAssertEqual([sourceOwner valueForKey:@"stickyInteraction"], sticky);
    XCTAssertEqual([sourceOwner valueForKey:@"topMarker"], marker);
    XCTAssertEqual([sourceOwner valueForKey:@"topHost"], proxy);
    XCTAssertEqual([sourceOwner valueForKey:@"originalStyle"], UIScrollEdgeEffectStyle.softStyle);
    XCTAssertTrue([[sourceOwner valueForKey:@"borrowsPinnedStickyHost"] boolValue]);
    XCTAssertFalse([[sourceOwner valueForKey:@"ownsTransitionHosts"] boolValue]);
    XCTAssertEqual(top.scrollView, source);
    XCTAssertEqual(sticky.scrollView, source);
    XCTAssertEqual(marker.superview, proxy);
    XCTAssertEqual(pinned.superview, source);
    XCTAssertFalse([self.pager resolveExternalTopOverlay]);
    [self.pager updateTopScrollEdgeForScrollView:source];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 3);
    XCTAssertEqual(overlay.superview, self.root);
    XCTAssertEqual(overlay.interactions.count, 0);

    NSMutableArray *incomingOwners = [NSMutableArray new];
    NSMutableArray<UIView *> *ownedHosts = [NSMutableArray new];
    NSMutableArray<UIView *> *ownedMarkers = [NSMutableArray new];
    NSMutableArray<UIScrollEdgeElementContainerInteraction *> *ownedEdges = [NSMutableArray new];
    for (id owner in owners) {
      if (owner == sourceOwner) continue;
      [incomingOwners addObject:owner];
      XCTAssertTrue([[owner valueForKey:@"ownsTransitionHosts"] boolValue]);
      UIView *page = [owner valueForKey:@"page"];
      UIScrollView *list = [owner valueForKey:@"scrollView"];
      XCTAssertNil([owner valueForKey:@"standInScrollView"]);
      XCTAssertEqual(list, page.subviews.firstObject);
      UIView *ownedTop = [owner valueForKey:@"ownedTopHost"];
      UIView *ownedSticky = [owner valueForKey:@"ownedStickyHost"];
      XCTAssertNotNil(ownedTop);
      XCTAssertNotNil(ownedSticky);
      XCTAssertEqual([owner valueForKey:@"topHost"], ownedTop);
      XCTAssertEqual([owner valueForKey:@"stickyHost"], ownedSticky);
      XCTAssertNotEqual(ownedTop, ownedSticky);
      for (NSString *edgeName in @[@"top", @"sticky"]) {
        UIView *host = [owner valueForKey:[edgeName stringByAppendingString:@"Host"]];
        UIView *edgeMarker = [owner valueForKey:[edgeName stringByAppendingString:@"Marker"]];
        UIScrollEdgeElementContainerInteraction *edge =
          [owner valueForKey:[edgeName stringByAppendingString:@"Interaction"]];
        XCTAssertFalse([ownedHosts containsObject:host]);
        [ownedHosts addObject:host];
        [ownedMarkers addObject:edgeMarker];
        [ownedEdges addObject:edge];
        XCTAssertEqual(host.superview, page);
        XCTAssertNotEqual(host, proxy);
        XCTAssertNotEqual(host, self.sticky);
        XCTAssertNotEqual(host, overlay);
        XCTAssertFalse(host.userInteractionEnabled);
        XCTAssertTrue(host.accessibilityElementsHidden);
        if (host.backgroundColor != nil) {
          XCTAssertEqualWithAccuracy(CGColorGetAlpha(host.backgroundColor.CGColor), 0, 0.001);
        }
        XCTAssertEqual(edge.view, host);
        XCTAssertEqual(edge.scrollView, list);
        XCTAssertEqual(edgeMarker.superview, host);
        XCTAssertEqualWithAccuracy(host.frame.origin.x,
          CGRectGetMinX([list convertRect:list.bounds toView:page]), 0.001);
      }
      CGRect pageFrame = page.frame;
      CGRect listFrame = list.frame;
      CGRect topFrame = ownedTop.frame;
      CGRect stickyFrame = ownedSticky.frame;
      page.frame = CGRectMake(pageFrame.origin.x, pageFrame.origin.y, 400, pageFrame.size.height);
      list.frame = CGRectMake(listFrame.origin.x, listFrame.origin.y, 400, listFrame.size.height);
      XCTAssertEqualWithAccuracy(ownedTop.frame.size.width, 400, 0.001);
      XCTAssertEqualWithAccuracy(ownedSticky.frame.size.width, 400, 0.001);
      XCTAssertEqualWithAccuracy(((UIView *)[owner valueForKey:@"topMarker"]).frame.size.width, 400, 0.001);
      XCTAssertEqualWithAccuracy(((UIView *)[owner valueForKey:@"stickyMarker"]).frame.size.width, 400, 0.001);
      XCTAssertEqualWithAccuracy(ownedTop.frame.origin.y, topFrame.origin.y, 0.001);
      XCTAssertEqualWithAccuracy(ownedSticky.frame.origin.y, stickyFrame.origin.y, 0.001);
      XCTAssertEqualWithAccuracy(ownedTop.frame.size.height, topFrame.size.height, 0.001);
      XCTAssertEqualWithAccuracy(ownedSticky.frame.size.height, stickyFrame.size.height, 0.001);
      page.frame = pageFrame;
      list.frame = listFrame;
    }
    XCTAssertEqual(incomingOwners.count, 2);
    XCTAssertEqual(ownedHosts.count, 4);
    horizontal.contentOffset = CGPointMake(480, 0);
    for (id owner in incomingOwners) {
      UIScrollView *list = [owner valueForKey:@"scrollView"];
      CGRect viewport = [list convertRect:list.bounds toView:nil];
      UIView *ownedTop = [owner valueForKey:@"ownedTopHost"];
      UIView *ownedSticky = [owner valueForKey:@"ownedStickyHost"];
      CGRect topWindow = [ownedTop convertRect:ownedTop.bounds toView:nil];
      CGRect stickyWindow = [ownedSticky convertRect:ownedSticky.bounds toView:nil];
      XCTAssertNotEqualWithAccuracy(viewport.origin.x, 0, 0.001);
      XCTAssertEqualWithAccuracy(topWindow.origin.x, viewport.origin.x, 0.001);
      XCTAssertEqualWithAccuracy(stickyWindow.origin.x, viewport.origin.x, 0.001);
      XCTAssertEqualWithAccuracy(topWindow.origin.y, viewport.origin.y, 0.001);
      XCTAssertEqualWithAccuracy(stickyWindow.origin.y, viewport.origin.y + 50, 0.001);
    }

    horizontal.contentOffset = CGPointMake(640, 0);
    [self.pager scrollViewDidEndDragging:horizontal willDecelerate:NO];
    NSPredicate *released = [NSPredicate predicateWithBlock:^BOOL(id object, NSDictionary *bindings) {
      for (UIView *host in ownedHosts) {
        if (host.superview != nil || host.interactions.count != 0) return NO;
      }
      for (UIView *edgeMarker in ownedMarkers) {
        if (edgeMarker.superview != nil) return NO;
      }
      for (UIScrollEdgeElementContainerInteraction *edge in ownedEdges) {
        if (edge.scrollView != nil) return NO;
      }
      return YES;
    }];
    XCTNSPredicateExpectation *settled = [[XCTNSPredicateExpectation alloc]
      initWithPredicate:released object:self.pager];
    [self waitForExpectations:@[settled] timeout:2];
    XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 2);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
    for (UIView *host in ownedHosts) {
      XCTAssertNil(host.superview);
      XCTAssertEqual(host.interactions.count, 0);
    }
    for (UIView *edgeMarker in ownedMarkers) XCTAssertNil(edgeMarker.superview);
    for (UIScrollEdgeElementContainerInteraction *edge in ownedEdges) XCTAssertNil(edge.scrollView);
    XCTAssertEqual(proxy.superview, [self.pager valueForKey:@"containerView"]);
    XCTAssertNotNil(self.sticky.superview);
    XCTAssertNil(top.scrollView);
    XCTAssertNil(sticky.scrollView);
    XCTAssertNil(marker.superview);
    XCTAssertEqual(source.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
    UIScrollEdgeElementContainerInteraction *normal = [self.pager valueForKey:@"topEdgeInteraction"];
    XCTAssertNotNil(normal);
    XCTAssertEqual(normal.scrollView, target);
    XCTAssertEqual([self.pager valueForKey:@"topEdgeScrollView"], target);
    XCTAssertEqual([self.pager valueForKey:@"originalTopEdgeStyle"], UIScrollEdgeEffectStyle.softStyle);
    XCTAssertEqual(target.topEdgeEffect.style, UIScrollEdgeEffectStyle.hardStyle);
    UIScrollView *otherNeighbor = (UIScrollView *)pages[0].view.subviews.firstObject;
    XCTAssertEqual(otherNeighbor.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
    XCTAssertNil(stickyMarker.superview);
    XCTAssertEqual([self.pager valueForKey:@"topEdgeInteractionHost"], proxy);
    XCTAssertEqual(overlay.superview, self.root);
    XCTAssertEqual(overlay.interactions.count, 0);
  } else {
    XCTSkip(@"External direct Hard drag handoff requires iOS 27 or newer");
  }
}

- (void)testExternalOverlayGeometryChangeReleasesBorrowedEdges
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *source = nil;
    UIView *overlay = nil;
    UIScrollView *horizontal = [self prepareExternalDirectDragWithSource:&source overlay:&overlay];
    UIScrollEdgeElementContainerInteraction *top = [self.pager valueForKey:@"topEdgeInteraction"];
    UIView *pinned = [self.pager valueForKey:@"pinnedStickyHostView"];
    [self.pager scrollViewWillBeginDragging:horizontal];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 3);
    id incoming = [[self.pager valueForKey:@"programmaticScrollEdgeOwners"] lastObject];
    UIView *ownedTop = [incoming valueForKey:@"ownedTopHost"];
    UIView *ownedSticky = [incoming valueForKey:@"ownedStickyHost"];
    UIView *ownedMarker = [incoming valueForKey:@"topMarker"];
    UIScrollEdgeElementContainerInteraction *ownedEdge = [incoming valueForKey:@"topInteraction"];
    UIView *proxy = [self.pager valueForKey:@"topOverlayHostView"];
    UIView *stickyParent = self.sticky.superview;
    XCTAssertNotNil(ownedTop);
    XCTAssertNotNil(ownedSticky);
    overlay.frame = CGRectMake(0, 100, 320, 60);
    XCTAssertTrue([self.pager resolveExternalTopOverlay]);
    XCTAssertEqualWithAccuracy([[self.pager valueForKey:@"externalTopOverlayHeight"] doubleValue], 60, 0.001);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
    XCTAssertNil(top.scrollView);
    XCTAssertNil(ownedTop.superview);
    XCTAssertNil(ownedSticky.superview);
    XCTAssertEqual(ownedTop.interactions.count, 0);
    XCTAssertEqual(ownedSticky.interactions.count, 0);
    XCTAssertNil(ownedMarker.superview);
    XCTAssertNil(ownedEdge.scrollView);
    XCTAssertNil([incoming valueForKey:@"ownedTopHost"]);
    XCTAssertNil([incoming valueForKey:@"ownedStickyHost"]);
    XCTAssertEqual(proxy.superview, [self.pager valueForKey:@"containerView"]);
    XCTAssertEqual(self.sticky.superview, stickyParent);
    XCTAssertEqual(overlay.superview, self.root);
    XCTAssertEqual(source.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
    XCTAssertEqual(pinned.superview, [self.pager valueForKey:@"containerView"]);
    XCTAssertTrue(pinned.hidden);
  } else {
    XCTSkip(@"External direct Hard drag handoff requires iOS 27 or newer");
  }
}

- (void)testExternalOverlayReplacementReleasesBorrowedEdges
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *source = nil;
    UIView *overlay = nil;
    UIScrollView *horizontal = [self prepareExternalDirectDragWithSource:&source overlay:&overlay];
    UIScrollEdgeElementContainerInteraction *top = [self.pager valueForKey:@"topEdgeInteraction"];
    XCTAssertNotNil(top);
    [self.pager scrollViewWillBeginDragging:horizontal];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 3);
    UIView *replacement = [[UIView alloc] initWithFrame:overlay.frame];
    replacement.tag = overlay.tag;
    [overlay removeFromSuperview];
    [self.root addSubview:replacement];
    XCTAssertTrue([self.pager resolveExternalTopOverlay]);
    XCTAssertEqual([self.pager valueForKey:@"externalTopOverlayView"], replacement);
    XCTAssertEqualWithAccuracy([[self.pager valueForKey:@"externalTopOverlayHeight"] doubleValue], 50, 0.001);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
    XCTAssertNil(top.scrollView);
    XCTAssertEqual(source.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
    XCTAssertEqual(replacement.interactions.count, 0);
    XCTAssertEqual(replacement.superview, self.root);
  } else {
    XCTSkip(@"External direct Hard drag handoff requires iOS 27 or newer");
  }
}

- (void)testExternalOverlayTagChangeReleasesBorrowedEdges
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *source = nil;
    UIScrollView *horizontal = [self prepareExternalDirectDragWithSource:&source overlay:nil];
    UIScrollEdgeElementContainerInteraction *top = [self.pager valueForKey:@"topEdgeInteraction"];
    XCTAssertNotNil(top);
    [self.pager scrollViewWillBeginDragging:horizontal];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 3);
    auto props = std::make_shared<RNCCollapsiblePagerViewProps>();
    props->nativeSmoothHeaderScrollEnabled = true;
    props->topScrollEdgeEffect = RNCCollapsiblePagerViewTopScrollEdgeEffect::Hard;
    props->externalTopOverlayTag = 0;
    [self.pager updateProps:props oldProps:nullptr];
    XCTAssertEqual([[self.pager valueForKey:@"externalTopOverlayTag"] integerValue], 0);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
    XCTAssertNil(top.scrollView);
    XCTAssertEqual(source.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
  } else {
    XCTSkip(@"External direct Hard drag handoff requires iOS 27 or newer");
  }
}

- (void)testExternalOuterInactivityReleasesBorrowedEdges
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *source = nil;
    UIScrollView *horizontal = [self prepareExternalDirectDragWithSource:&source overlay:nil];
    RNCPagerViewComponentView *outer = [RNCPagerViewComponentView new];
    [self.pager setValue:outer forKey:@"externalOuterPager"];
    [self.pager setValue:@YES forKey:@"externalOuterPageActive"];
    [self.pager setValue:@NO forKey:@"externalOuterTransitioning"];
    UIScrollEdgeElementContainerInteraction *top = [self.pager valueForKey:@"topEdgeInteraction"];
    XCTAssertNotNil(top);
    [self.pager scrollViewWillBeginDragging:horizontal];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 3);
    [self.pager outerPagerDidUpdateOverlayOwner:NO transitioning:NO];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
    XCTAssertNil(top.scrollView);
    XCTAssertNil([self.pager valueForKey:@"topEdgeInteraction"]);
    XCTAssertEqual(source.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
    [self.pager beginProgrammaticScrollEdgesToIndex:2 allowExternalOverlay:YES];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
  } else {
    XCTSkip(@"External direct Hard drag handoff requires iOS 27 or newer");
  }
}

- (void)testExternalOuterTransitionReleasesBorrowedEdges
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *source = nil;
    UIScrollView *horizontal = [self prepareExternalDirectDragWithSource:&source overlay:nil];
    RNCPagerViewComponentView *outer = [RNCPagerViewComponentView new];
    [self.pager setValue:outer forKey:@"externalOuterPager"];
    [self.pager setValue:@YES forKey:@"externalOuterPageActive"];
    [self.pager setValue:@NO forKey:@"externalOuterTransitioning"];
    UIScrollEdgeElementContainerInteraction *top = [self.pager valueForKey:@"topEdgeInteraction"];
    XCTAssertNotNil(top);
    [self.pager scrollViewWillBeginDragging:horizontal];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 3);
    [self.pager outerPagerDidUpdateOverlayOwner:YES transitioning:YES];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
    XCTAssertNil(top.scrollView);
    XCTAssertNil([self.pager valueForKey:@"topEdgeInteraction"]);
    XCTAssertEqual(source.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
    [self.pager beginProgrammaticScrollEdgesToIndex:2 allowExternalOverlay:YES];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
  } else {
    XCTSkip(@"External direct Hard drag handoff requires iOS 27 or newer");
  }
}

- (void)testDirectDragAdoptsSourceBeforeDetachAndOwnsBothNeighbours
{
  if (@available(iOS 26.0, *)) {
    UIScrollView *source = nil;
    UIScrollView *horizontal = [self prepareDirectDragWithSource:&source];
    [self prepareIncomingListsScrolledBeyondBars];
    UIView *topHost = [self.pager valueForKey:@"topEdgeInteractionHost"];
    UIView *pinnedHost = [self.pager valueForKey:@"pinnedStickyHostView"];
    UIScrollEdgeElementContainerInteraction *top = [self.pager valueForKey:@"topEdgeInteraction"];
    UIScrollEdgeElementContainerInteraction *sticky = [self.pager valueForKey:@"stickyEdgeInteraction"];
    UIView *topMarker = [self.pager valueForKey:@"topEdgeMarker"];
    UIView *stickyMarker = [self.pager valueForKey:@"stickyEdgeMarker"];
    XCTAssertNotNil(top);
    XCTAssertNotNil(sticky);

    [self.pager scrollViewWillBeginDragging:horizontal];

    NSArray *owners = [self.pager valueForKey:@"programmaticScrollEdgeOwners"];
    XCTAssertEqual(owners.count, 3);
    id sourceOwner = [owners firstObject];
    XCTAssertEqual([sourceOwner valueForKey:@"topInteraction"], top);
    XCTAssertEqual([sourceOwner valueForKey:@"stickyInteraction"], sticky);
    XCTAssertEqual([sourceOwner valueForKey:@"topMarker"], topMarker);
    XCTAssertEqual([sourceOwner valueForKey:@"stickyMarker"], stickyMarker);
    XCTAssertEqual([sourceOwner valueForKey:@"originalStyle"], UIScrollEdgeEffectStyle.softStyle);
    XCTAssertTrue([[sourceOwner valueForKey:@"borrowsPinnedStickyHost"] boolValue]);
    XCTAssertTrue([topHost.interactions containsObject:top]);
    XCTAssertTrue([pinnedHost.interactions containsObject:sticky]);
    XCTAssertEqual(top.scrollView, source);
    XCTAssertEqual(sticky.scrollView, source);
    XCTAssertEqual(topMarker.superview, topHost);
    XCTAssertEqual(stickyMarker.superview, pinnedHost);
    XCTAssertEqual(pinnedHost.superview, source);
    XCTAssertFalse(pinnedHost.hidden);
    for (id owner in owners) {
      XCTAssertNotNil([owner valueForKey:@"topInteraction"]);
      XCTAssertNotNil([[owner valueForKey:@"topInteraction"] scrollView]);
      if (owner == sourceOwner) continue;
      XCTAssertTrue([[owner valueForKey:@"ownsTransitionHosts"] boolValue]);
      XCTAssertNil([owner valueForKey:@"standInScrollView"]);
      UIView *page = [owner valueForKey:@"page"];
      UIScrollView *list = [owner valueForKey:@"scrollView"];
      XCTAssertEqual(list, page.subviews.firstObject);
      UIView *incomingTop = [owner valueForKey:@"ownedTopHost"];
      UIView *incomingSticky = [owner valueForKey:@"ownedStickyHost"];
      XCTAssertEqual(incomingTop.superview, page);
      XCTAssertEqual(incomingSticky.superview, page);
      XCTAssertNotEqual(incomingTop, topHost);
      XCTAssertNotEqual(incomingSticky, pinnedHost);
      XCTAssertFalse(incomingTop.userInteractionEnabled);
      XCTAssertFalse(incomingSticky.userInteractionEnabled);
      XCTAssertEqualWithAccuracy(incomingTop.frame.origin.x,
        CGRectGetMinX([list convertRect:list.bounds toView:page]), 0.001);
    }
  } else {
    XCTSkip(@"Direct local Hard drag handoff requires iOS 26 or newer");
  }
}

- (void)testDirectDragTakeoverIgnoresOldSettlementAndReleasesOnReturn
{
  if (@available(iOS 26.0, *)) {
    UIScrollView *source = nil;
    UIScrollView *horizontal = [self prepareDirectDragWithSource:&source];
    [self prepareIncomingListsScrolledBeyondBars];
    UIScrollEdgeElementContainerInteraction *top = [self.pager valueForKey:@"topEdgeInteraction"];
    UIView *topHost = [self.pager valueForKey:@"topEdgeInteractionHost"];
    NSUInteger originalTransition = [[self.pager valueForKey:@"transitionId"] unsignedIntegerValue];
    XCTAssertEqual([[self.pager valueForKey:@"pageControllers"] count], 3);
    XCTAssertEqualWithAccuracy(horizontal.contentOffset.x, 320, 0.001);
    XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 1);
    [self.pager scrollViewWillBeginDragging:horizontal];
    [self.pager scrollViewDidEndDragging:horizontal willDecelerate:NO];
    [self.pager scrollViewWillBeginDragging:horizontal];
    XCTAssertGreaterThan([[self.pager valueForKey:@"transitionId"] unsignedIntegerValue], originalTransition + 1);
    XCTAssertTrue([topHost.interactions containsObject:top]);
    XCTAssertEqual(top.scrollView, source);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 3);

    XCTestExpectation *oldSettlement = [self expectationWithDescription:@"old settlement skipped"];
    dispatch_async(dispatch_get_main_queue(), ^{ [oldSettlement fulfill]; });
    [self waitForExpectations:@[oldSettlement] timeout:2];
    XCTAssertEqualWithAccuracy(horizontal.contentOffset.x, 320, 0.001);
    XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 1);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 3);
    [self.pager scrollViewDidEndDragging:horizontal willDecelerate:YES];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 3);
    NSMutableArray<UIView *> *incomingHosts = [NSMutableArray new];
    for (id owner in [self.pager valueForKey:@"programmaticScrollEdgeOwners"]) {
      if (![[owner valueForKey:@"ownsTransitionHosts"] boolValue]) continue;
      [incomingHosts addObject:[owner valueForKey:@"ownedTopHost"]];
      [incomingHosts addObject:[owner valueForKey:@"ownedStickyHost"]];
    }
    [self.pager scrollViewDidEndDecelerating:horizontal];
    XCTestExpectation *settled = [self expectationWithDescription:@"drag settled"];
    dispatch_async(dispatch_get_main_queue(), ^{ [settled fulfill]; });
    [self waitForExpectations:@[settled] timeout:2];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
    XCTAssertEqual(incomingHosts.count, 4);
    for (UIView *host in incomingHosts) XCTAssertNil(host.superview);
    XCTAssertTrue([topHost.interactions containsObject:top]);
    XCTAssertEqual(top.scrollView, source);
    XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 1);
    XCTAssertEqual([self.pager valueForKey:@"topEdgeScrollView"], source);
    XCTAssertEqual([self.pager valueForKey:@"originalTopEdgeStyle"], UIScrollEdgeEffectStyle.softStyle);
    UIScrollEdgeElementContainerInteraction *normal = [self.pager valueForKey:@"topEdgeInteraction"];
    XCTAssertEqual(normal, top);
    XCTAssertEqual(normal.scrollView, source);
    XCTAssertEqual(source.topEdgeEffect.style, UIScrollEdgeEffectStyle.hardStyle);
    NSArray<UIViewController *> *pages = [self.pager valueForKey:@"pageControllers"];
    for (NSNumber *index in @[@0, @2]) {
      UIScrollView *neighbor = (UIScrollView *)pages[index.integerValue].view.subviews.firstObject;
      XCTAssertEqual(neighbor.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
    }
  } else {
    XCTSkip(@"Direct local Hard drag handoff requires iOS 26 or newer");
  }
}

- (void)testLocalDragMissingIncomingListReleasesStandInOnRecycle
{
  if (@available(iOS 26.0, *)) {
    self.root.backgroundColor = UIColor.blackColor;
    UIScrollView *horizontal = [self prepareDirectDragWithSource:NULL];
    NSArray<UIViewController *> *pages = [self.pager valueForKey:@"pageControllers"];
    UIView *incomingPage = pages[2].view;
    for (UIView *child in incomingPage.subviews.copy) [child removeFromSuperview];
    [self.pager scrollViewWillBeginDragging:horizontal];
    id incomingOwner = nil;
    for (id owner in [self.pager valueForKey:@"programmaticScrollEdgeOwners"]) {
      if ([owner valueForKey:@"page"] == incomingPage) incomingOwner = owner;
    }
    XCTAssertNotNil(incomingOwner);
    UIScrollView *standIn = [incomingOwner valueForKey:@"standInScrollView"];
    UIView *topHost = [incomingOwner valueForKey:@"ownedTopHost"];
    UIView *stickyHost = [incomingOwner valueForKey:@"ownedStickyHost"];
    UIScrollEdgeElementContainerInteraction *top = [incomingOwner valueForKey:@"topInteraction"];
    UIScrollEdgeElementContainerInteraction *sticky = [incomingOwner valueForKey:@"stickyInteraction"];
    XCTAssertNotNil(standIn);
    XCTAssertEqual(standIn.superview, incomingPage.superview);
    XCTAssertEqual(topHost.superview, incomingPage.superview);
    XCTAssertEqual(stickyHost.superview, incomingPage.superview);
    XCTAssertEqualWithAccuracy(standIn.frame.origin.x,
      [incomingPage convertPoint:CGPointZero toView:incomingPage.superview].x, 0.001);
    XCTAssertEqualWithAccuracy(topHost.frame.origin.x, standIn.frame.origin.x, 0.001);
    XCTAssertEqualWithAccuracy(stickyHost.frame.origin.x, standIn.frame.origin.x, 0.001);
    if (@available(iOS 27.0, *)) {
      XCTAssertEqualObjects(standIn.backgroundColor, UIColor.clearColor);
    } else {
      XCTAssertEqualObjects(standIn.backgroundColor, UIColor.blackColor);
    }
    XCTAssertEqual(top.scrollView, standIn);
    XCTAssertEqual(sticky.scrollView, standIn);
    XCTAssertFalse(standIn.userInteractionEnabled);
    [self.pager prepareForRecycle];
    XCTAssertNil(standIn.superview);
    XCTAssertEqualObjects(standIn.backgroundColor, UIColor.clearColor);
    XCTAssertNil(topHost.superview);
    XCTAssertNil(stickyHost.superview);
    XCTAssertNil(top.scrollView);
    XCTAssertNil(sticky.scrollView);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
  } else {
    XCTSkip(@"Local Hard stand-in edges require iOS 26 or newer");
  }
}

- (void)testIOS26TransparentBackingRestoresExactOriginalAndUpdatesTheme
{
  if (@available(iOS 27.0, *)) {
    XCTSkip(@"Opaque backing is restricted to iOS 26");
  } else if (@available(iOS 26.0, *)) {
    UIScrollView *source = [self preparePinnedLocalHardSource];
    UIColor *dark = [UIColor colorWithWhite:0.06 alpha:1];
    UIColor *light = [UIColor colorWithWhite:0.98 alpha:1];
    for (id value in @[NSNull.null, UIColor.clearColor]) {
      UIColor *original = value == NSNull.null ? nil : value;
      source.backgroundColor = original;
      self.root.backgroundColor = dark;
      [self.pager updateTopScrollEdgeForScrollView:source];
      UIColor *applied = source.backgroundColor;
      XCTAssertEqualObjects(applied, dark);
      XCTAssertEqualObjects(source.backgroundColor, applied);
      self.root.backgroundColor = light;
      [self.pager syncScrollEdgeAppearance];
      XCTAssertEqualObjects(source.backgroundColor, light);
      self.root.backgroundColor = nil;
      [self.pager syncScrollEdgeAppearance];
      XCTAssertEqualObjects(source.backgroundColor, original);
      self.root.backgroundColor = dark;
      [self.pager syncScrollEdgeAppearance];
      XCTAssertEqualObjects(source.backgroundColor, dark);
      [self.pager setValue:@NO forKey:@"topScrollEdgeEnabled"];
      [self.pager updateTopScrollEdgeForScrollView:nil];
      XCTAssertEqualObjects(source.backgroundColor, original);
      [self.pager setValue:@YES forKey:@"topScrollEdgeEnabled"];
      [self.pager updateTopScrollEdgeForScrollView:source];
      XCTAssertEqualObjects(source.backgroundColor, dark);
      [self.pager updateTopScrollEdgeForScrollView:nil];
      XCTAssertEqualObjects(source.backgroundColor, original);
    }
  } else {
    XCTSkip(@"Local Hard scroll edges require iOS 26 or newer");
  }
}

- (void)testIOS26BackingPreservesCallerColorsAndReleasesOnWindowExit
{
  if (@available(iOS 27.0, *)) {
    XCTSkip(@"Opaque backing is restricted to iOS 26");
  } else if (@available(iOS 26.0, *)) {
    UIScrollView *source = [self preparePinnedLocalHardSource];
    self.root.backgroundColor = UIColor.blackColor;
    for (UIColor *custom in @[UIColor.redColor, [UIColor colorWithWhite:0.5 alpha:0.5]]) {
      source.backgroundColor = custom;
      [self.pager updateTopScrollEdgeForScrollView:source];
      [self.pager syncScrollEdgeAppearance];
      XCTAssertEqualObjects(source.backgroundColor, custom);
      [self.pager updateTopScrollEdgeForScrollView:nil];
      XCTAssertEqualObjects(source.backgroundColor, custom);
    }
    source.backgroundColor = nil;
    [self.pager updateTopScrollEdgeForScrollView:source];
    XCTAssertEqualObjects(source.backgroundColor, UIColor.blackColor);
    UIColor *replacement = UIColor.blueColor;
    source.backgroundColor = replacement;
    self.root.backgroundColor = UIColor.whiteColor;
    [self.pager syncScrollEdgeAppearance];
    XCTAssertEqualObjects(source.backgroundColor, replacement);
    [self.pager updateTopScrollEdgeForScrollView:nil];
    XCTAssertEqualObjects(source.backgroundColor, replacement);
    source.backgroundColor = UIColor.clearColor;
    [self.pager updateTopScrollEdgeForScrollView:source];
    XCTAssertEqualObjects(source.backgroundColor, UIColor.whiteColor);
    [self.pager setValue:@YES forKey:@"pendingTopOverlayAppearanceCommit"];
    [self.pager updateTopScrollEdgeForScrollView:nil];
    XCTAssertNil([self.pager valueForKey:@"topEdgeScrollView"]);
    XCTAssertEqualObjects(source.backgroundColor, UIColor.whiteColor);
    [self.root removeFromSuperview];
    XCTAssertEqualObjects(source.backgroundColor, UIColor.clearColor);
  } else {
    XCTSkip(@"Local Hard scroll edges require iOS 26 or newer");
  }
}

- (void)testIOS26BackingSurvivesBorrowedOwnerAndRestoresOnRecycle
{
  if (@available(iOS 27.0, *)) {
    XCTSkip(@"Opaque backing is restricted to iOS 26");
  } else if (@available(iOS 26.0, *)) {
    UIScrollView *source = nil;
    UIScrollView *horizontal = [self prepareDirectDragWithSource:&source];
    self.root.backgroundColor = UIColor.blackColor;
    [self.pager updateTopScrollEdgeForScrollView:source];
    UIColor *applied = source.backgroundColor;
    XCTAssertEqualObjects(applied, UIColor.blackColor);
    [self.pager scrollViewWillBeginDragging:horizontal];
    XCTAssertEqualObjects(source.backgroundColor, applied);
    NSArray<UIViewController *> *pages = [self.pager valueForKey:@"pageControllers"];
    NSArray *owners = [[self.pager valueForKey:@"programmaticScrollEdgeOwners"] copy];
    XCTAssertEqual(owners.count, 3);
    NSMutableArray<UIScrollView *> *standIns = [NSMutableArray new];
    for (id owner in owners) {
      UIScrollView *list = [owner valueForKey:@"scrollView"];
      XCTAssertEqualObjects(list.backgroundColor, UIColor.blackColor);
      if (list != source) {
        XCTAssertEqual([owner valueForKey:@"standInScrollView"], list);
        [standIns addObject:list];
      }
    }
    XCTAssertEqual(standIns.count, 2);
    for (UIViewController *page in pages) {
      UIScrollView *list = (UIScrollView *)page.view.subviews.firstObject;
      if (list != source) XCTAssertNil(list.backgroundColor);
    }
    [self.pager scrollViewDidEndDragging:horizontal willDecelerate:NO];
    XCTestExpectation *settled = [self expectationWithDescription:@"background handoff cleanup"];
    dispatch_async(dispatch_get_main_queue(), ^{ [settled fulfill]; });
    [self waitForExpectations:@[settled] timeout:2];
    XCTAssertEqualObjects(source.backgroundColor, UIColor.blackColor);
    [self.pager prepareForRecycle];
    for (UIScrollView *standIn in standIns) {
      XCTAssertEqualObjects(standIn.backgroundColor, UIColor.clearColor);
      XCTAssertNil(standIn.superview);
    }
    for (UIViewController *page in pages) {
      UIScrollView *list = (UIScrollView *)page.view.subviews.firstObject;
      XCTAssertNil(list.backgroundColor);
    }
  } else {
    XCTSkip(@"Local Hard scroll edges require iOS 26 or newer");
  }
}

- (void)testIOS26EquivalentGetterColorsKeepLeaseThroughThemeAndReacquisition
{
  if (@available(iOS 27.0, *)) {
    XCTSkip(@"Opaque backing is restricted to iOS 26");
  } else if (@available(iOS 26.0, *)) {
    UIScrollView *previous = [self preparePinnedLocalHardSource];
    [self.pager updateTopScrollEdgeForScrollView:nil];
    RNCEquivalentBackgroundScrollView *source = [[RNCEquivalentBackgroundScrollView alloc]
      initWithFrame:previous.frame];
    source.contentSize = previous.contentSize;
    [previous.superview insertSubview:source belowSubview:previous];
    for (UIView *child in previous.subviews.copy) [source addSubview:child];
    [previous removeFromSuperview];
    [self.pager setValue:source forKey:@"observedScrollView"];
    [self.pager setValue:source forKey:@"sharedHeaderScrollView"];
    self.root.backgroundColor = [UIColor colorWithRed:0.06 green:0.06 blue:0.06 alpha:1];
    [self.pager updateTopScrollEdgeForScrollView:source];
    UIColor *first = source.backgroundColor;
    UIColor *second = source.backgroundColor;
    XCTAssertNotEqual(first, second);
    XCTAssertEqualObjects(first, second);
    XCTAssertEqualObjects(first, self.root.backgroundColor);
    self.root.backgroundColor = [UIColor colorWithRed:1 green:1 blue:1 alpha:1];
    [self.pager syncScrollEdgeAppearance];
    XCTAssertEqualObjects(source.backgroundColor, self.root.backgroundColor);
    [self.pager updateTopScrollEdgeForScrollView:nil];
    XCTAssertNil(source.backgroundColor);
    [self.pager updateTopScrollEdgeForScrollView:source];
    XCTAssertEqualObjects(source.backgroundColor, self.root.backgroundColor);
    UIColor *replacement = [UIColor colorWithRed:0.8 green:0.1 blue:0.2 alpha:1];
    source.backgroundColor = replacement;
    [self.pager syncScrollEdgeAppearance];
    XCTAssertEqualObjects(source.backgroundColor, replacement);
    [self.pager updateTopScrollEdgeForScrollView:nil];
    XCTAssertEqualObjects(source.backgroundColor, replacement);
  } else {
    XCTSkip(@"Local Hard scroll edges require iOS 26 or newer");
  }
}

- (void)testIOS27HardEdgesKeepTransparentCallerBacking
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *source = nil;
    UIScrollView *horizontal = [self prepareDirectDragWithSource:&source];
    self.root.backgroundColor = UIColor.blackColor;
    [self.pager updateTopScrollEdgeForScrollView:source];
    XCTAssertNil(source.backgroundColor);
    [self.pager scrollViewWillBeginDragging:horizontal];
    XCTAssertNil(source.backgroundColor);
    [self.pager prepareForRecycle];
    XCTAssertNil(source.backgroundColor);
  } else {
    XCTSkip(@"The unchanged transparent-backing control requires iOS 27");
  }
}

- (RNCAlignedTakeoverScrollView *)prepareAlignedLocalListForDragTakeover
{
  UIScrollView *previous = [self preparePinnedLocalHardSource];
  RNCAlignedTakeoverScrollView *scroll = [[RNCAlignedTakeoverScrollView alloc] initWithFrame:previous.frame];
  scroll.observedPan = [RNCAlignedTakeoverPan new];
  scroll.contentSize = previous.contentSize;
  [previous.superview insertSubview:scroll belowSubview:previous];
  [previous removeFromSuperview];
  [self.pager setValue:nil forKey:@"observedScrollView"];
  [self.pager setValue:nil forKey:@"sharedHeaderScrollView"];
  [self.pager setValue:@[@"portfolio"] forKey:@"pageKeys"];
  [self.pager setValue:@0 forKey:@"headerOffset"];
  [self.pager attachScrollObserverForCurrentPage];
  XCTAssertEqual([self.pager valueForKey:@"observedScrollView"], scroll);
  XCTAssertTrue([[self.pager valueForKey:@"observingContentOffset"] boolValue]);
  [[self.pager valueForKey:@"pendingPageRestoreKeys"] removeAllObjects];
  return scroll;
}

- (void)testIOS26DraggingAfterPanResetTakesOverAlignedBody
{
  if (@available(iOS 27.0, *)) {
    XCTSkip(@"The isDragging fallback is restricted to iOS 26");
  } else if (@available(iOS 26.0, *)) {
    RNCAlignedTakeoverScrollView *scroll = [self prepareAlignedLocalListForDragTakeover];
    NSMutableSet *aligned = [self.pager valueForKey:@"alignedBodyKeys"];
    NSMutableDictionary *display = [self.pager valueForKey:@"displayBodyOverrides"];
    NSInteger step = 0;
    for (NSNumber *state in @[@(UIGestureRecognizerStatePossible), @(UIGestureRecognizerStateEnded)]) {
      scroll.observedDragging = NO;
      scroll.contentOffset = CGPointMake(0, -scroll.contentInset.top);
      [self.pager setValue:@0 forKey:@"headerOffset"];
      [aligned addObject:@"portfolio"];
      display[@"portfolio"] = @94;
      scroll.observedPan.observedState = static_cast<UIGestureRecognizerState>(state.integerValue);
      scroll.observedDragging = YES;
      scroll.contentOffset = CGPointMake(0, 140 + step++ * 20 - scroll.contentInset.top);
      XCTAssertFalse([aligned containsObject:@"portfolio"]);
      XCTAssertNil(display[@"portfolio"]);
      XCTAssertEqualWithAccuracy([[self.pager valueForKey:@"headerOffset"] doubleValue], 100, 0.001);
    }
    scroll.observedDragging = NO;
  } else {
    XCTSkip(@"Local Hard scroll edges require iOS 26 or newer");
  }
}

- (void)testIOS26NonDraggingSyntheticAndCommandOffsetsKeepAlignedBody
{
  if (@available(iOS 27.0, *)) {
    XCTSkip(@"The isDragging fallback is restricted to iOS 26");
  } else if (@available(iOS 26.0, *)) {
    RNCAlignedTakeoverScrollView *scroll = [self prepareAlignedLocalListForDragTakeover];
    NSMutableSet *aligned = [self.pager valueForKey:@"alignedBodyKeys"];
    NSMutableSet *commands = [self.pager valueForKey:@"commandBodyKeys"];
    NSMutableDictionary *display = [self.pager valueForKey:@"displayBodyOverrides"];
    NSArray<NSString *> *guards = @[@"not-dragging", @"syntheticPageRestore", @"transitioning",
      @"commandBodyKeys", @"headerCommandOwner", @"correctingTopInsetRebound"];
    NSInteger step = 0;
    for (NSString *guard in guards) {
      scroll.observedDragging = NO;
      [self.pager setValue:@NO forKey:@"syntheticPageRestore"];
      [self.pager setValue:@NO forKey:@"transitioning"];
      [self.pager setValue:@NO forKey:@"correctingTopInsetRebound"];
      [self.pager setValue:nil forKey:@"headerCommandOwner"];
      [commands removeAllObjects];
      scroll.contentOffset = CGPointMake(0, -scroll.contentInset.top);
      [self.pager setValue:@0 forKey:@"headerOffset"];
      [aligned addObject:@"portfolio"];
      display[@"portfolio"] = @94;
      if ([guard isEqualToString:@"commandBodyKeys"]) [commands addObject:@"portfolio"];
      else if ([guard isEqualToString:@"headerCommandOwner"]) [self.pager setValue:scroll forKey:guard];
      else if (![guard isEqualToString:@"not-dragging"]) [self.pager setValue:@YES forKey:guard];
      scroll.observedDragging = ![guard isEqualToString:@"not-dragging"];
      scroll.contentOffset = CGPointMake(0, 140 + step++ * 20 - scroll.contentInset.top);
      XCTAssertTrue([aligned containsObject:@"portfolio"], @"%@", guard);
      XCTAssertEqualObjects(display[@"portfolio"], @94, @"%@", guard);
      XCTAssertEqualWithAccuracy([[self.pager valueForKey:@"headerOffset"] doubleValue], 0, 0.001, @"%@", guard);
    }
    scroll.observedDragging = NO;
  } else {
    XCTSkip(@"Local Hard scroll edges require iOS 26 or newer");
  }
}

- (void)testIOS26ExternalDragDoesNotBorrowTransitionEdges
{
  if (@available(iOS 27.0, *)) {
    XCTSkip(@"iOS 27 external edge ownership is covered separately");
  } else if (@available(iOS 26.0, *)) {
    UIScrollView *horizontal = [self prepareExternalDirectDragWithSource:NULL overlay:NULL];
    [self.pager scrollViewWillBeginDragging:horizontal];
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
  } else {
    XCTSkip(@"External scroll edges require iOS 26 or newer");
  }
}

- (void)testIOS26AnimatedLocalHardPageCommandSettlesWithBorrowedEdges
{
  if (@available(iOS 27.0, *)) {
    XCTSkip(@"iOS 27 native animated-scroll ownership is covered separately");
  } else if (@available(iOS 26.0, *)) {
    UIScrollView *source = nil;
    UIScrollView *horizontal = [self prepareDirectDragWithSource:&source];
    UIScrollEdgeElementContainerInteraction *top = [self.pager valueForKey:@"topEdgeInteraction"];
    [self.pager goTo:2 animated:YES];
    XCTAssertNil([self.pager valueForKey:@"pendingNativePagerScrollCompletion"]);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 2);
    XCTAssertEqual(top.scrollView, source);
    [self waitForNativeResizeCommandsToSettle];
    XCTAssertEqual([[self.pager valueForKey:@"currentIndex"] integerValue], 2);
    XCTAssertEqualWithAccuracy(horizontal.contentOffset.x, 640, 0.001);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
    XCTAssertNil(top.scrollView);
    XCTAssertEqual(source.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
    UIScrollEdgeElementContainerInteraction *settled = [self.pager valueForKey:@"topEdgeInteraction"];
    XCTAssertNotNil(settled);
    XCTAssertEqual(settled.scrollView, [self.pager valueForKey:@"observedScrollView"]);
  } else {
    XCTSkip(@"Local Hard scroll edges require iOS 26 or newer");
  }
}

- (void)testAnimatedDirectPageCommandBorrowsRealSourceEdgeUntilCompletion
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *source = [self preparePinnedLocalHardSource];
    UIView *topHost = [self.pager valueForKey:@"topEdgeInteractionHost"];
    UIView *pinnedHost = [self.pager valueForKey:@"pinnedStickyHostView"];
    UIScrollEdgeElementContainerInteraction *top = [self.pager valueForKey:@"topEdgeInteraction"];
    UIScrollEdgeElementContainerInteraction *sticky = [self.pager valueForKey:@"stickyEdgeInteraction"];
    UIView *topMarker = [self.pager valueForKey:@"topEdgeMarker"];
    UIView *stickyMarker = [self.pager valueForKey:@"stickyEdgeMarker"];
    XCTAssertNotNil(top);
    XCTAssertNotNil(sticky);
    XCTAssertEqual([self.pager valueForKey:@"originalTopEdgeStyle"], UIScrollEdgeEffectStyle.softStyle);

    [self.pager adoptCurrentScrollEdgeForProgrammaticTransition:NO];
    id owner = [[self.pager valueForKey:@"programmaticScrollEdgeOwners"] firstObject];
    XCTAssertNotNil(owner);
    XCTAssertEqual([owner valueForKey:@"topInteraction"], top);
    XCTAssertEqual([owner valueForKey:@"stickyInteraction"], sticky);
    XCTAssertEqual([owner valueForKey:@"topMarker"], topMarker);
    XCTAssertEqual([owner valueForKey:@"stickyMarker"], stickyMarker);
    XCTAssertEqual([owner valueForKey:@"originalStyle"], UIScrollEdgeEffectStyle.softStyle);
    XCTAssertTrue([[owner valueForKey:@"borrowsPinnedStickyHost"] boolValue]);
    XCTAssertTrue([topHost.interactions containsObject:top]);
    XCTAssertTrue([pinnedHost.interactions containsObject:sticky]);
    XCTAssertEqual(top.scrollView, source);
    XCTAssertEqual(sticky.scrollView, source);
    XCTAssertEqual(topMarker.superview, topHost);
    XCTAssertEqual(stickyMarker.superview, pinnedHost);
    XCTAssertEqual(source.topEdgeEffect.style, UIScrollEdgeEffectStyle.hardStyle);

    [self.pager restorePinnedStickyHeader];
    XCTAssertEqual(pinnedHost.superview, source);
    XCTAssertFalse(pinnedHost.hidden);
    [self.pager setValue:nil forKey:@"observedScrollView"];
    [self.pager finishProgrammaticScrollEdges];
    XCTAssertFalse([topHost.interactions containsObject:top]);
    XCTAssertFalse([pinnedHost.interactions containsObject:sticky]);
    XCTAssertNil(top.scrollView);
    XCTAssertNil(sticky.scrollView);
    XCTAssertNil(topMarker.superview);
    XCTAssertNil(stickyMarker.superview);
    XCTAssertEqual(source.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
    XCTAssertEqual(pinnedHost.superview, [self.pager valueForKey:@"containerView"]);
    XCTAssertTrue(pinnedHost.hidden);
    XCTAssertEqual([[self.pager valueForKey:@"programmaticScrollEdgeOwners"] count], 0);
  } else {
    XCTSkip(@"Animated local Hard edge handoff requires iOS 27 or newer");
  }
}

- (void)testCancelledDirectPageCommandReleasesBorrowedSourceEdge
{
  if (@available(iOS 27.0, *)) {
    UIScrollView *source = [self preparePinnedLocalHardSource];
    UIView *topHost = [self.pager valueForKey:@"topEdgeInteractionHost"];
    UIView *pinnedHost = [self.pager valueForKey:@"pinnedStickyHostView"];
    UIScrollEdgeElementContainerInteraction *top = [self.pager valueForKey:@"topEdgeInteraction"];
    UIScrollEdgeElementContainerInteraction *sticky = [self.pager valueForKey:@"stickyEdgeInteraction"];
    UIView *topMarker = [self.pager valueForKey:@"topEdgeMarker"];
    UIView *stickyMarker = [self.pager valueForKey:@"stickyEdgeMarker"];
    [self.pager adoptCurrentScrollEdgeForProgrammaticTransition:NO];
    [self.pager restorePinnedStickyHeader];
    [self.pager clearProgrammaticScrollEdges];
    XCTAssertFalse([topHost.interactions containsObject:top]);
    XCTAssertFalse([pinnedHost.interactions containsObject:sticky]);
    XCTAssertNil(topMarker.superview);
    XCTAssertNil(stickyMarker.superview);
    XCTAssertEqual(source.topEdgeEffect.style, UIScrollEdgeEffectStyle.softStyle);
    XCTAssertEqual(pinnedHost.superview, [self.pager valueForKey:@"containerView"]);
    XCTAssertTrue(pinnedHost.hidden);
  } else {
    XCTSkip(@"Animated local Hard edge handoff requires iOS 27 or newer");
  }
}

- (void)testIOS27LocalHardThemeBackgroundTracksMaskedHeaderGeometry
{
  if (@available(iOS 27.0, *)) {
    UIWindowScene *scene = nil;
    for (UIScene *connectedScene in UIApplication.sharedApplication.connectedScenes) {
      if ([connectedScene isKindOfClass:UIWindowScene.class]) {
        scene = (UIWindowScene *)connectedScene;
        break;
      }
    }
    XCTAssertNotNil(scene);
    UIWindow *window = [[UIWindow alloc] initWithWindowScene:scene];
    window.frame = self.root.bounds;
    [window addSubview:self.root];
    window.hidden = NO;
    [self addTeardownBlock:^{ window.hidden = YES; }];
    [self.pager layoutIfNeeded];
    self.pager.backgroundColor = [UIColor colorWithWhite:0.2 alpha:1];
    UIView *container = [self.pager valueForKey:@"containerView"];
    container.frame = self.pager.bounds;
    UIScrollView *scroll = [[UIScrollView alloc] initWithFrame:container.bounds];
    scroll.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    scroll.alwaysBounceVertical = YES;
    scroll.contentSize = CGSizeMake(320, 1200);
    scroll.contentInset = UIEdgeInsetsMake(50 + 100 + 20, 0, 0, 0);
    [container insertSubview:scroll atIndex:0];
    UIView *sharedHost = [self.pager valueForKey:@"sharedHeaderHostView"];
    for (UIView *child in sharedHost.subviews.copy) [child removeFromSuperview];
    [scroll addSubview:sharedHost];
    sharedHost.frame = CGRectMake(0, -120, 320, 120);
    UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 100)];
    [sharedHost addSubview:header];
    [self.pager setValue:header forKey:@"headerView"];
    UIView *overlayHost = [self.pager valueForKey:@"topOverlayHostView"];
    overlayHost.frame = CGRectMake(0, 0, 320, 50);
    overlayHost.hidden = NO;
    UIView *overlay = [[UIView alloc] initWithFrame:overlayHost.bounds];
    [overlayHost addSubview:overlay];
    [self.pager setValue:overlay forKey:@"topOverlayView"];
    [self.pager setValue:@50 forKey:@"topOverlayHeight"];
    [self.pager setValue:@100 forKey:@"headerHeight"];
    [self.pager setValue:@20 forKey:@"stickyHeaderHeight"];
    [self.pager setValue:@YES forKey:@"nativeSmoothHeaderScrollEnabled"];
    [self.pager setValue:@YES forKey:@"topScrollEdgeEnabled"];
    [self.pager setValue:@(static_cast<NSInteger>(RNCCollapsiblePagerViewTopScrollEdgeEffect::Hard))
                 forKey:@"topScrollEdgeEffect"];
    [self.pager setValue:scroll forKey:@"observedScrollView"];
    [self.pager setValue:scroll forKey:@"sharedHeaderScrollView"];
    scroll.contentOffset = CGPointMake(0, -170);
    [self.pager updateTopScrollEdgeForScrollView:scroll];
    CALayer *layer = [self.pager valueForKey:@"topOverlayThemeBackgroundLayer"];
    XCTAssertNotNil(layer);
    XCTAssertEqual(layer.superlayer, overlayHost.layer);
    XCTAssertEqualWithAccuracy(layer.opacity, 1, 0.01);
    XCTAssertEqual(scroll.topEdgeEffect.style, UIScrollEdgeEffectStyle.hardStyle);
    XCTAssertEqual([self.pager valueForKey:@"topEdgeScrollView"], scroll);

    scroll.contentOffset = CGPointMake(0, -120);
    [self.pager updateScrollEdgeContentMasks];
    XCTAssertEqualWithAccuracy(layer.opacity, 0.5, 0.01);
    scroll.contentOffset = CGPointMake(0, -70);
    [self.pager updateScrollEdgeContentMasks];
    XCTAssertEqualWithAccuracy(layer.opacity, 0, 0.01);
    scroll.contentOffset = CGPointMake(0, -210);
    [self.pager updateScrollEdgeContentMasks];
    XCTAssertEqualWithAccuracy(layer.opacity, 1, 0.01);

    scroll.contentInset = UIEdgeInsetsMake(230, 0, 0, 0);
    scroll.contentOffset = CGPointMake(0, -230);
    [self.pager updateScrollEdgeContentMasks];
    XCTAssertEqualWithAccuracy(layer.opacity, 1, 0.01);
    self.pager.backgroundColor = UIColor.whiteColor;
    [self.pager updateScrollEdgeContentMasks];
    XCTAssertTrue(CGColorEqualToColor(layer.backgroundColor, UIColor.whiteColor.CGColor));

    [self.pager updateTopScrollEdgeForScrollView:nil];
    XCTAssertEqual([self.pager valueForKey:@"topOverlayThemeBackgroundLayer"], layer);
    XCTAssertEqualWithAccuracy(layer.opacity, 1, 0.01);
    [self.pager setValue:@YES forKey:@"topScrollEdgeEnabled"];
    [self.pager updateTopScrollEdgeForScrollView:scroll];
    [self.pager setValue:@123 forKey:@"externalTopOverlayTag"];
    [self.pager updateScrollEdgeContentMasks];
    XCTAssertNil([self.pager valueForKey:@"topOverlayThemeBackgroundLayer"]);
    [self.pager setValue:@0 forKey:@"externalTopOverlayTag"];
    [self.pager updateTopScrollEdgeForScrollView:scroll];
    XCTAssertEqual(scroll.topEdgeEffect.style, UIScrollEdgeEffectStyle.hardStyle);
    [self.pager setValue:@NO forKey:@"topScrollEdgeEnabled"];
    [self.pager updateTopScrollEdgeForScrollView:scroll];
    XCTAssertNil([self.pager valueForKey:@"topOverlayThemeBackgroundLayer"]);
    [self.pager setValue:@YES forKey:@"topScrollEdgeEnabled"];
    [self.pager updateTopScrollEdgeForScrollView:scroll];
    XCTAssertNotNil([self.pager valueForKey:@"topOverlayThemeBackgroundLayer"]);
    [self.pager prepareForRecycle];
    XCTAssertNil([self.pager valueForKey:@"topOverlayThemeBackgroundLayer"]);
  } else {
    XCTSkip(@"The theme background applies only on iOS 27 or newer");
  }
}

@end
