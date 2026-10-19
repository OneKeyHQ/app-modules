#import <XCTest/XCTest.h>

#import "RNCCollapsiblePagerViewComponentView.h"
#import "RNCPagerViewComponentView.h"
#import <react/renderer/components/pagerview/Props.h>

using namespace facebook::react;

@interface RNCCollapsiblePagerViewComponentView (ScrollEdgeTesting)
- (void)updateTopScrollEdgeForScrollView:(UIScrollView *)scrollView;
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

@end
