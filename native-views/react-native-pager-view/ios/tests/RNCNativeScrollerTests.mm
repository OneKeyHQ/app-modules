#import <XCTest/XCTest.h>
#import "RNCNativeScrollerComponentView.h"
#import "RNCCollapsiblePagerViewComponentView.h"
#import <react/renderer/components/pagerview/Props.h>

using namespace facebook::react;

@interface RNCNativeScrollerComponentView (Tests)
- (void)emitScrollEvent:(NSInteger)kind velocity:(CGPoint)velocity target:(CGPoint)target;
- (void)clampContentOffsetIfNeeded;
- (void)scrollTo:(double)x y:(double)y animated:(BOOL)animated;
- (void)setRefreshing:(BOOL)refreshing;
- (CGFloat)contentViewportHeight;
@end

@interface RNCCollapsiblePagerViewComponentView (ScrollerTests)
- (void)applyInsetsToScrollView:(UIScrollView *)scrollView pageIndex:(NSInteger)index restore:(BOOL)restore;
- (void)restoreScrollViewState:(UIScrollView *)scrollView resetSavedOffset:(BOOL)reset;
@end

@interface RNCObservedScroller : RNCNativeScrollerComponentView
@property (nonatomic, strong) NSMutableArray<NSNumber *> *events;
@end
@implementation RNCObservedScroller
- (void)emitScrollEvent:(NSInteger)kind velocity:(CGPoint)velocity target:(CGPoint)target
{
  if (self.events == nil) self.events = [NSMutableArray new];
  [self.events addObject:@(kind)];
  [super emitScrollEvent:kind velocity:velocity target:target];
}
@end

@interface RNCNativeScrollerTests : XCTestCase
@property (nonatomic, strong) RNCObservedScroller *scroller;
@end
@implementation RNCNativeScrollerTests

- (void)setUp
{
  [super setUp];
  self.scroller = [RNCObservedScroller new];
  LayoutMetrics metrics;
  metrics.frame.size = {320, 600};
  [self.scroller updateLayoutMetrics:metrics oldLayoutMetrics:{}];
}

- (void)tearDown
{
  [self.scroller prepareForRecycle];
  self.scroller = nil;
  [super tearDown];
}

- (void)testContentSizeDoesNotRelayoutReactContentOrSharedHeader
{
  RCTViewComponentView *content = [RCTViewComponentView new];
  content.frame = CGRectMake(0, 0, 320, 900);
  [self.scroller mountChildComponentView:content index:0];
  UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, -200, 320, 200)];
  [self.scroller.scrollView addSubview:header];
  auto props = std::make_shared<RNCNativeScrollerProps>();
  props->contentWidth = 320;
  props->contentHeight = 900;
  [self.scroller updateProps:props oldProps:nullptr];
  XCTAssertEqualWithAccuracy(self.scroller.scrollView.contentSize.height, 900, 0.01);
  XCTAssertTrue(CGRectEqualToRect(content.frame, CGRectMake(0, 0, 320, 900)));
  XCTAssertTrue(CGRectEqualToRect(header.frame, CGRectMake(0, -200, 320, 200)));
  XCTAssertEqual(header.superview, self.scroller.scrollView);
  XCTAssertFalse([self.scroller.scrollView isKindOfClass:NSClassFromString(@"RCTEnhancedScrollView")]);
}

- (void)testShrinkClampsOnlyUnreachableUpperOffset
{
  auto props = std::make_shared<RNCNativeScrollerProps>();
  props->contentWidth = 320;
  props->contentHeight = 1500;
  [self.scroller updateProps:props oldProps:nullptr];
  self.scroller.scrollView.contentInset = UIEdgeInsetsMake(200, 0, 468, 0);
  self.scroller.scrollView.contentOffset = CGPointMake(0, 800);
  auto shorter = std::make_shared<RNCNativeScrollerProps>();
  shorter->contentWidth = 320;
  shorter->contentHeight = 132;
  [self.scroller updateProps:shorter oldProps:props];
  [self.scroller clampContentOffsetIfNeeded];
  XCTAssertEqualWithAccuracy(self.scroller.scrollView.contentOffset.y, 0, 0.01);
  self.scroller.scrollView.contentOffset = CGPointMake(0, -240);
  auto empty = std::make_shared<RNCNativeScrollerProps>();
  empty->contentWidth = 320;
  empty->contentHeight = 0;
  [self.scroller updateProps:empty oldProps:shorter];
  [self.scroller clampContentOffsetIfNeeded];
  XCTAssertEqualWithAccuracy(self.scroller.scrollView.contentOffset.y, -240, 0.01);
}

- (void)testNativeInterruptionEndsEachDragAndMomentumOnlyOnce
{
  UIScrollView *scroll = self.scroller.scrollView;
  id<UIScrollViewDelegate> delegate = scroll.delegate;
  [delegate scrollViewWillBeginDragging:scroll];
  [self.scroller stopScrolling];
  [delegate scrollViewDidEndDragging:scroll willDecelerate:NO];
  XCTAssertEqualObjects(self.scroller.events, (@[@1, @2]));
  [self.scroller.events removeAllObjects];
  [delegate scrollViewWillBeginDecelerating:scroll];
  [self.scroller stopScrolling];
  [delegate scrollViewDidEndDecelerating:scroll];
  NSPredicate *terminal = [NSPredicate predicateWithFormat:@"SELF == 4"];
  XCTAssertEqual([self.scroller.events filteredArrayUsingPredicate:terminal].count, 1u);
  XCTAssertEqualObjects(self.scroller.events.firstObject, @3);
}

- (void)testRefreshReplacementAndRecycleDoNotKeepOldControl
{
  auto props = std::make_shared<RNCNativeScrollerProps>();
  props->refreshEnabled = true;
  props->refreshProgressViewOffset = 24;
  [self.scroller updateProps:props oldProps:nullptr];
  UIRefreshControl *original = self.scroller.scrollView.refreshControl;
  XCTAssertNotNil(original);
  XCTAssertEqualWithAccuracy(original.bounds.origin.y, -24, 0.01);
  [self.scroller prepareForRecycle];
  XCTAssertNil(self.scroller.scrollView.refreshControl);
  [self.scroller updateProps:props oldProps:props];
  XCTAssertNotNil(self.scroller.scrollView.refreshControl);
  XCTAssertNotEqual(self.scroller.scrollView.refreshControl, original);
  XCTAssertEqualWithAccuracy(self.scroller.scrollView.refreshControl.bounds.origin.y, -24, 0.01);
  [self.scroller setRefreshing:NO];
  XCTAssertFalse(self.scroller.scrollView.refreshControl.refreshing);
}

- (void)testCallerInsetUpdatesSurvivePagerReapplyAndRelease
{
  RNCCollapsiblePagerViewComponentView *pager = [RNCCollapsiblePagerViewComponentView new];
  auto pagerProps = std::make_shared<RNCCollapsiblePagerViewProps>();
  pagerProps->headerHeight = 120;
  pagerProps->stickyHeaderHeight = 40;
  [pager updateProps:pagerProps oldProps:nullptr];
  [pager addSubview:self.scroller];
  auto props = std::make_shared<RNCNativeScrollerProps>();
  props->contentWidth = 320;
  props->contentHeight = 1500;
  props->contentInsetTop = 10;
  [self.scroller updateProps:props oldProps:nullptr];
  [pager applyInsetsToScrollView:self.scroller.scrollView pageIndex:0 restore:NO];
  XCTAssertEqualWithAccuracy(self.scroller.scrollView.contentInset.top, 170, 0.01);
  auto updated = std::make_shared<RNCNativeScrollerProps>();
  updated->contentWidth = 320;
  updated->contentHeight = 1500;
  updated->contentInsetTop = 30;
  updated->contentInsetBottom = 20;
  [self.scroller updateProps:updated oldProps:props];
  [pager applyInsetsToScrollView:self.scroller.scrollView pageIndex:0 restore:NO];
  XCTAssertEqualWithAccuracy(self.scroller.scrollView.contentInset.top, 190, 0.01);
  [pager restoreScrollViewState:self.scroller.scrollView resetSavedOffset:YES];
  XCTAssertEqualWithAccuracy(self.scroller.scrollView.contentInset.top, 30, 0.01);
  XCTAssertEqualWithAccuracy(self.scroller.scrollView.contentInset.bottom, 20, 0.01);
  [self.scroller removeFromSuperview];
}

- (void)testRecycledDelegateCannotStartASequenceForTheNextOwner
{
  UIScrollView *scroll = self.scroller.scrollView;
  id<UIScrollViewDelegate> oldDelegate = scroll.delegate;
  [self.scroller prepareForRecycle];
  XCTAssertNil(scroll.delegate);
  [oldDelegate scrollViewWillBeginDragging:scroll];
  [oldDelegate scrollViewWillBeginDecelerating:scroll];
  auto props = std::make_shared<RNCNativeScrollerProps>();
  [self.scroller updateProps:props oldProps:nullptr];
  XCTAssertEqual(scroll.delegate, self.scroller);
  [self.scroller stopScrolling];
  NSPredicate *terminal = [NSPredicate predicateWithFormat:@"SELF == 2 OR SELF == 4"];
  XCTAssertEqual([self.scroller.events filteredArrayUsingPredicate:terminal].count, 0u);
}

- (void)testPagerExplicitlyFindsIndependentScrollView
{
  RNCCollapsiblePagerViewComponentView *pager = [RNCCollapsiblePagerViewComponentView new];
  UIView *page = [UIView new];
  [page addSubview:self.scroller];
  SEL finder = NSSelectorFromString(@"findExplicitNativeScrollerInView:");
  UIScrollView *(*find)(id, SEL, UIView *) =
    (UIScrollView *(*)(id, SEL, UIView *))[pager methodForSelector:finder];
  XCTAssertEqual(find(pager, finder, page), self.scroller.scrollView);
}

- (void)testContentViewportUsesOnlyCallerInsetsAndStaticStickyHeight
{
  RNCCollapsiblePagerViewComponentView *pager = [RNCCollapsiblePagerViewComponentView new];
  auto pagerProps = std::make_shared<RNCCollapsiblePagerViewProps>();
  pagerProps->headerHeight = 120;
  pagerProps->stickyHeaderHeight = 40;
  [pager updateProps:pagerProps oldProps:nullptr];
  [pager addSubview:self.scroller];
  auto props = std::make_shared<RNCNativeScrollerProps>();
  props->contentWidth = 320;
  props->contentHeight = 20;
  props->contentInsetTop = 10;
  props->contentInsetBottom = 20;
  [self.scroller updateProps:props oldProps:nullptr];
  [pager applyInsetsToScrollView:self.scroller.scrollView pageIndex:0 restore:NO];
  XCTAssertEqualWithAccuracy([self.scroller contentViewportHeight], 530, 0.01);
  // Synthetic bottom fill and temporary refresh insets are not caller geometry.
  self.scroller.scrollView.contentInset = UIEdgeInsetsMake(230, 0, 510, 0);
  XCTAssertEqualWithAccuracy([self.scroller contentViewportHeight], 530, 0.01);
  auto filled = std::make_shared<RNCNativeScrollerProps>();
  filled->contentWidth = 320;
  filled->contentHeight = 530;
  filled->contentInsetTop = 10;
  filled->contentInsetBottom = 20;
  [self.scroller updateProps:filled oldProps:props];
  XCTAssertEqualWithAccuracy([self.scroller contentViewportHeight], 530, 0.01);
  LayoutMetrics metrics;
  metrics.frame.size = {320, 700};
  [self.scroller updateLayoutMetrics:metrics oldLayoutMetrics:{}];
  XCTAssertEqualWithAccuracy([self.scroller contentViewportHeight], 630, 0.01);
  auto updated = std::make_shared<RNCNativeScrollerProps>();
  updated->contentWidth = 320;
  updated->contentHeight = 530;
  updated->contentInsetTop = 10;
  updated->contentInsetBottom = 30;
  [self.scroller updateProps:updated oldProps:filled];
  XCTAssertEqualWithAccuracy([self.scroller contentViewportHeight], 620, 0.01);
  auto tallerSticky = std::make_shared<RNCCollapsiblePagerViewProps>();
  tallerSticky->headerHeight = 250;
  tallerSticky->stickyHeaderHeight = 60;
  [pager updateProps:tallerSticky oldProps:pagerProps];
  [pager applyInsetsToScrollView:self.scroller.scrollView pageIndex:0 restore:NO];
  XCTAssertEqualWithAccuracy([self.scroller contentViewportHeight], 600, 0.01);
  [pager restoreScrollViewState:self.scroller.scrollView resetSavedOffset:YES];
  XCTAssertEqualWithAccuracy([self.scroller contentViewportHeight], 660, 0.01);
  [self.scroller removeFromSuperview];
}

- (void)testContentViewportReleasesIndependentPagerContributions
{
  NSObject *outer = [NSObject new];
  NSObject *inner = [NSObject new];
  [self.scroller setPagerStickyHeight:40 forOwner:outer];
  [self.scroller setPagerStickyHeight:20 forOwner:inner];
  XCTAssertEqualWithAccuracy([self.scroller contentViewportHeight], 540, 0.01);
  [self.scroller setPagerStickyHeight:40 forOwner:outer];
  XCTAssertEqualWithAccuracy([self.scroller contentViewportHeight], 540, 0.01);
  [self.scroller setPagerStickyHeight:0 forOwner:outer];
  XCTAssertEqualWithAccuracy([self.scroller contentViewportHeight], 580, 0.01);
  [self.scroller prepareForRecycle];
  auto props = std::make_shared<RNCNativeScrollerProps>();
  [self.scroller updateProps:props oldProps:nullptr];
  XCTAssertEqualWithAccuracy([self.scroller contentViewportHeight], 600, 0.01);
}

@end
