#import <XCTest/XCTest.h>
#import "RNCCollapsiblePagerViewComponentView.h"
#import "RNCNativeScrollerComponentView.h"
#import <react/renderer/mounting/MountingTransaction.h>

using namespace facebook::react;

@interface RNCCollapsiblePagerViewComponentView (FirstFrameTesting)
- (void)mountingTransactionDidMount:(const MountingTransaction &)transaction
               withSurfaceTelemetry:(const SurfaceTelemetry &)telemetry;
- (UIScrollView *)findExplicitNativeScrollerInView:(UIView *)view;
- (UIScrollView *)findVerticalScrollViewInView:(UIView *)view;
- (void)attachScrollObserverForCurrentPage;
@end

@interface RNCCollapsiblePagerFirstFrameTests : XCTestCase
@end

@implementation RNCCollapsiblePagerFirstFrameTests

- (RNCCollapsiblePagerViewComponentView *)pagerWithPageCount:(NSInteger)count
{
  RNCCollapsiblePagerViewComponentView *pager =
    [[RNCCollapsiblePagerViewComponentView alloc] initWithFrame:CGRectMake(0, 0, 320, 844)];
  [pager setValue:@YES forKey:@"nativeSmoothHeaderScrollEnabled"];
  [pager setValue:@120 forKey:@"headerHeight"];
  [pager setValue:@44 forKey:@"stickyHeaderHeight"];
  UIWindow *window = [[UIWindow alloc] initWithFrame:pager.frame];
  UIViewController *root = [UIViewController new];
  window.rootViewController = root;
  [root.view addSubview:pager];
  window.hidden = NO;
  [self addTeardownBlock:^{
    [pager prepareForRecycle];
    window.hidden = YES;
  }];
  NSMutableArray *pages = [pager valueForKey:@"pageControllers"];
  NSMutableArray<NSString *> *keys = [NSMutableArray new];
  for (NSInteger index = 0; index < count; index++) {
    UIViewController *page = [UIViewController new];
    page.view = [[UIView alloc] initWithFrame:CGRectMake(index * 320, 0, 320, 600)];
    [pager addSubview:page.view];
    [pages addObject:page];
    [keys addObject:[NSString stringWithFormat:@"page-%ld", (long)index]];
  }
  [pager setValue:keys forKey:@"pageKeys"];
  return pager;
}

- (RNCNativeScrollerComponentView *)addScrollerToPager:(RNCCollapsiblePagerViewComponentView *)pager
                                            atIndex:(NSInteger)index
{
  RNCNativeScrollerComponentView *scroller = [RNCNativeScrollerComponentView new];
  scroller.frame = CGRectMake(0, 0, 320, 600);
  scroller.scrollView.frame = scroller.bounds;
  scroller.scrollView.contentSize = CGSizeMake(320, 1500);
  UIViewController *page = [pager valueForKey:@"pageControllers"][index];
  [page.view addSubview:scroller];
  return scroller;
}

- (void)finishMount:(RNCCollapsiblePagerViewComponentView *)pager
{
  MountingTransaction transaction(1, 1, {}, {});
  SurfaceTelemetry telemetry;
  [pager mountingTransactionDidMount:transaction withSurfaceTelemetry:telemetry];
}

- (void)testInteractiveTargetGetsInsetsBeforeSelectionWithoutTakingObserver
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:2];
  RNCNativeScrollerComponentView *target = [self addScrollerToPager:pager atIndex:1];
  [pager setValue:@YES forKey:@"transitioning"];
  [pager setValue:@YES forKey:@"isPagerDragging"];
  UIView *header = [pager valueForKey:@"sharedHeaderHostView"];
  UIView *headerOwner = header.superview;
  [self finishMount:pager];
  XCTAssertEqualWithAccuracy(target.scrollView.contentInset.top, 164, 0.01);
  XCTAssertEqualWithAccuracy(target.scrollView.contentOffset.y, -164, 0.01);
  XCTAssertEqualObjects([pager valueForKey:@"currentIndex"], @0);
  XCTAssertNil([pager valueForKey:@"observedScrollView"]);
  XCTAssertEqual(header.superview, headerOwner);
}

- (void)testRepeatedMountDoesNotRestoreAnAlreadyPreparedTarget
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:2];
  RNCNativeScrollerComponentView *target = [self addScrollerToPager:pager atIndex:1];
  [pager setValue:@YES forKey:@"transitioning"];
  [self finishMount:pager];
  target.scrollView.contentOffset = CGPointMake(0, 230);
  [self finishMount:pager];
  XCTAssertEqualWithAccuracy(target.scrollView.contentInset.top, 164, 0.01);
  XCTAssertEqualWithAccuracy(target.scrollView.contentOffset.y, 230, 0.01);
}

- (void)testProgrammaticDestinationOutsideAdjacentRangeIsPrepared
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:4];
  RNCNativeScrollerComponentView *unrelated = [self addScrollerToPager:pager atIndex:2];
  RNCNativeScrollerComponentView *target = [self addScrollerToPager:pager atIndex:3];
  [pager setValue:@3 forKey:@"destinationIndex"];
  [pager setValue:@YES forKey:@"transitioning"];
  [self finishMount:pager];
  XCTAssertEqualWithAccuracy(target.scrollView.contentInset.top, 164, 0.01);
  XCTAssertEqualWithAccuracy(target.scrollView.contentOffset.y, -164, 0.01);
  XCTAssertEqualWithAccuracy(unrelated.scrollView.contentInset.top, 0, 0.01);
  XCTAssertEqualObjects([pager valueForKey:@"currentIndex"], @0);
}

- (void)testRemovedScrollerIsExcludedUntilDidMount
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:2];
  RNCNativeScrollerComponentView *target = [self addScrollerToPager:pager atIndex:1];
  NSHashTable *removed = [pager valueForKey:@"mountingRemovedScrollViews"];
  [removed addObject:target.scrollView];
  XCTAssertNil([pager findExplicitNativeScrollerInView:target]);
  XCTAssertNil([pager findVerticalScrollViewInView:target.scrollView]);
  [pager setValue:@YES forKey:@"transitioning"];
  [self finishMount:pager];
  XCTAssertEqual(removed.count, 0u);
  XCTAssertEqual([pager findExplicitNativeScrollerInView:target], target.scrollView);
  XCTAssertEqualWithAccuracy(target.scrollView.contentInset.top, 164, 0.01);
}

- (void)testGenericCollectionStillRequiresFallbackDebounce
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:1];
  UICollectionView *collection = [[UICollectionView alloc]
    initWithFrame:CGRectMake(0, 0, 320, 600)
    collectionViewLayout:[UICollectionViewFlowLayout new]];
  UIViewController *page = [pager valueForKey:@"pageControllers"][0];
  [page.view addSubview:collection];
  [pager attachScrollObserverForCurrentPage];
  XCTAssertNil([pager valueForKey:@"observedScrollView"]);
  XCTAssertEqualObjects([pager valueForKey:@"pendingFallbackScrollView"], collection);
  XCTAssertEqualWithAccuracy(collection.contentInset.top, 0, 0.01);
}

- (void)testRecycleClearsTransactionExclusion
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:1];
  RNCNativeScrollerComponentView *scroller = [self addScrollerToPager:pager atIndex:0];
  NSHashTable *removed = [pager valueForKey:@"mountingRemovedScrollViews"];
  [removed addObject:scroller.scrollView];
  [pager prepareForRecycle];
  XCTAssertEqual(removed.count, 0u);
}

@end
