#import <XCTest/XCTest.h>
#import "RNCCollapsiblePagerViewComponentView.h"
#import "RNCNativeScrollerComponentView.h"
#import <react/renderer/mounting/MountingTransaction.h>

using namespace facebook::react;

@interface RNCCollapsiblePagerViewComponentView (FirstFrameTesting)
- (void)mountingTransactionWillMount:(const MountingTransaction &)transaction
                withSurfaceTelemetry:(const SurfaceTelemetry &)telemetry;
- (void)mountingTransactionDidMount:(const MountingTransaction &)transaction
               withSurfaceTelemetry:(const SurfaceTelemetry &)telemetry;
- (UIScrollView *)findExplicitNativeScrollerInView:(UIView *)view;
- (UIScrollView *)findVerticalScrollViewInView:(UIView *)view;
- (void)attachScrollObserverForCurrentPage;
- (void)attachScrollObserverForCurrentPageAfterTransition:(BOOL)selectionSettled;
- (void)detachScrollObserver;
- (void)beginPageStateTransition;
- (void)updatePageStateTransitionProgress:(CGFloat)progress;
- (void)finishPageStateTransition;
- (void)scrollToTop:(NSInteger)target animated:(BOOL)animated;
- (void)updateHeaderSettledStateForceBusy:(BOOL)forceBusy;
- (void)preparePageForHorizontalTransitionAtIndex:(NSInteger)index;
- (void)applyInsetsToScrollView:(UIScrollView *)scrollView pageIndex:(NSInteger)index restore:(BOOL)restore;
@end

@interface RNCNativeScrollerComponentView (ScrollCommandTesting)
- (void)scrollTo:(double)x y:(double)y animated:(BOOL)animated;
- (void)scrollToEnd:(BOOL)animated;
@end

@interface RNCCollapsiblePagerFirstFrameTests : XCTestCase
@end

@implementation RNCCollapsiblePagerFirstFrameTests

- (void)testHeaderSettledAtTopRequiresCurrentQuietOwnerAndCancelsDetachedTimer
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:1];
  UIView *header = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 120)];
  [pager addSubview:header];
  [pager setValue:header forKey:@"headerView"];
  RNCNativeScrollerComponentView *scroller = [self addScrollerToPager:pager atIndex:0];
  [pager attachScrollObserverForCurrentPage];
  [pager updateHeaderSettledStateForceBusy:NO];
  XCTAssertFalse([[pager valueForKey:@"headerSettledAtTop"] boolValue]);
  [[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.25]];
  XCTAssertTrue([[pager valueForKey:@"headerSettledAtTop"] boolValue]);

  [scroller scrollTo:0 y:200 - scroller.scrollView.contentInset.top animated:NO];
  XCTAssertFalse([[pager valueForKey:@"headerSettledAtTop"] boolValue]);
  [scroller scrollTo:0 y:-scroller.scrollView.adjustedContentInset.top animated:NO];
  [pager updateHeaderSettledStateForceBusy:NO];
  XCTAssertFalse([[pager valueForKey:@"headerSettledAtTop"] boolValue]);
  [pager detachScrollObserver];
  [[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.25]];
  XCTAssertFalse([[pager valueForKey:@"headerSettledAtTop"] boolValue]);
  XCTAssertNil([pager valueForKey:@"headerSettledTimer"]);

  [pager attachScrollObserverForCurrentPage];
  [pager updateHeaderSettledStateForceBusy:NO];
  XCTAssertFalse([[pager valueForKey:@"headerSettledAtTop"] boolValue]);
  [[NSRunLoop mainRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:0.25]];
  XCTAssertTrue([[pager valueForKey:@"headerSettledAtTop"] boolValue]);
}

- (void)testReachableTopCommandBeforeLayoutMustNotReplayPendingPagePosition
{
  for (NSNumber *command in @[@0, @1, @2]) {
    RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:1];
    RNCNativeScrollerComponentView *scroller = [self addScrollerToPager:pager atIndex:0];
    NSMutableDictionary *offsets = [pager valueForKey:@"pageOffsets"];
    offsets[@"page-0"] = @660;
    [pager setValue:@120 forKey:@"headerOffset"];
    scroller.scrollView.contentSize = CGSizeMake(0, 0);
    [pager attachScrollObserverForCurrentPage];
    XCTAssertTrue([[pager valueForKey:@"pendingPageRestoreKeys"] containsObject:@"page-0"]);
    XCTAssertEqualObjects(offsets[@"page-0"], @660);
    XCTAssertEqual([pager valueForKey:@"observedScrollView"], scroller.scrollView);
    XCTAssertEqualObjects([pager valueForKey:@"observedPageKey"], @"page-0");
    XCTAssertEqualObjects([pager valueForKey:@"currentIndex"], @0);
    if (command.boolValue) {
      if (command.integerValue == 2) {
        CGFloat reachableEndQ = MAX(-scroller.scrollView.adjustedContentInset.top,
          scroller.scrollView.contentSize.height - scroller.scrollView.bounds.size.height +
          scroller.scrollView.adjustedContentInset.bottom) + scroller.scrollView.contentInset.top;
        XCTAssertEqualWithAccuracy(reachableEndQ, 120, 0.01);
        XCTAssertEqualWithAccuracy(scroller.scrollView.contentOffset.y + scroller.scrollView.contentInset.top, reachableEndQ, 0.01);
        [scroller scrollToEnd:NO];
      }
      else [scroller scrollTo:0 y:-scroller.scrollView.adjustedContentInset.top animated:NO];
      XCTAssertEqualWithAccuracy(scroller.scrollView.contentOffset.y + scroller.scrollView.contentInset.top, command.integerValue == 2 ? 120 : 0, 0.01);
      XCTAssertFalse([[pager valueForKey:@"pendingPageRestoreKeys"] containsObject:@"page-0"]);
      XCTAssertEqualWithAccuracy([offsets[@"page-0"] doubleValue], 0, 0.01);
      XCTAssertEqual([pager valueForKey:@"observedScrollView"], scroller.scrollView);
      XCTAssertEqualObjects([pager valueForKey:@"observedPageKey"], @"page-0");
    }
    // The existing observed content-size path retries the pending restoration.
    scroller.scrollView.contentSize = CGSizeMake(320, 1500);
    XCTAssertFalse([[pager valueForKey:@"pendingPageRestoreKeys"] containsObject:@"page-0"]);
    CGFloat expected = command.integerValue == 2 ? 120 : (command.boolValue ? 0 : 780);
    XCTAssertEqualWithAccuracy(scroller.scrollView.contentOffset.y + scroller.scrollView.contentInset.top,
                              expected, 0.01, @"command=%@ must not replay a cancelled reading position", command);
    XCTAssertEqualWithAccuracy([[pager valueForKey:@"headerOffset"] doubleValue], command.integerValue == 1 ? 0 : 120, 0.01);
  }
}

- (void)testScrollCommandCancellationRespectsOwnerAndValidity
{
  for (NSNumber *mode in @[@0, @1, @2]) {
    RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:2];
    RNCNativeScrollerComponentView *current = [self addScrollerToPager:pager atIndex:0];
    RNCNativeScrollerComponentView *other = [self addScrollerToPager:pager atIndex:1];
    NSMutableDictionary *offsets = [pager valueForKey:@"pageOffsets"];
    offsets[@"page-0"] = @660;
    [pager setValue:@120 forKey:@"headerOffset"];
    offsets[@"page-1"] = @360;
    current.scrollView.contentSize = CGSizeMake(0, 0);
    [pager attachScrollObserverForCurrentPage];
    XCTAssertTrue([[pager valueForKey:@"pendingPageRestoreKeys"] containsObject:@"page-0"]);
    if (mode.integerValue == 0) {
      [current scrollTo:0 y:NAN animated:NO];
    } else if (mode.integerValue == 1) {
      [other scrollTo:0 y:-other.scrollView.adjustedContentInset.top animated:NO];
    } else {
      NSUInteger generation = [[pager valueForKey:@"generation"] unsignedIntegerValue];
      [pager setValue:@(generation + 1) forKey:@"observedPageGeneration"];
      [current scrollTo:0 y:-current.scrollView.adjustedContentInset.top animated:NO];
    }
    XCTAssertTrue([[pager valueForKey:@"pendingPageRestoreKeys"] containsObject:@"page-0"]);
    XCTAssertEqualObjects(offsets[@"page-0"], @660);
    XCTAssertEqualObjects(offsets[@"page-1"], @360);
  }
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:2];
  RNCNativeScrollerComponentView *current = [self addScrollerToPager:pager atIndex:0];
  [self addScrollerToPager:pager atIndex:1];
  NSMutableDictionary *offsets = [pager valueForKey:@"pageOffsets"];
  offsets[@"page-0"] = @0;
  offsets[@"page-1"] = @360;
  [pager setValue:@120 forKey:@"headerOffset"];
  [pager attachScrollObserverForCurrentPage];
  XCTAssertFalse([[pager valueForKey:@"pendingPageRestoreKeys"] containsObject:@"page-0"]);
  [current scrollTo:0 y:200 - current.scrollView.contentInset.top animated:NO];
  XCTAssertEqualWithAccuracy(current.scrollView.contentOffset.y + current.scrollView.contentInset.top, 200, 0.01);
  XCTAssertEqualWithAccuracy([offsets[@"page-0"] doubleValue], 80, 0.01);
  XCTAssertEqualObjects(offsets[@"page-1"], @360);
}

- (void)testPagerTopCommandsClearOnlyCurrentReadingMemoryInBothModes
{
  for (NSNumber *smooth in @[@NO, @YES]) {
    for (NSNumber *target in @[@0, @1]) {
      RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:2];
      [pager setValue:smooth forKey:@"nativeSmoothHeaderScrollEnabled"];
      RNCNativeScrollerComponentView *current = [self addScrollerToPager:pager atIndex:0];
      [self addScrollerToPager:pager atIndex:1];
      NSMutableDictionary *offsets = [pager valueForKey:@"pageOffsets"];
      offsets[@"page-0"] = smooth.boolValue ? @660 : @780;
      offsets[@"page-1"] = @360;
      [pager setValue:@120 forKey:@"headerOffset"];
      [pager attachScrollObserverForCurrentPage];
      [pager scrollToTop:target.integerValue animated:NO];
      CGFloat expectedQ = target.boolValue ? 120 : 0;
      XCTAssertEqualWithAccuracy(current.scrollView.contentOffset.y + current.scrollView.contentInset.top, expectedQ, 0.01);
      XCTAssertEqualWithAccuracy([[pager valueForKey:@"headerOffset"] doubleValue], expectedQ, 0.01);
      XCTAssertEqualWithAccuracy([offsets[@"page-0"] doubleValue], smooth.boolValue ? 0 : expectedQ, 0.01);
      XCTAssertEqualObjects(offsets[@"page-1"], @360);
      XCTAssertFalse([[pager valueForKey:@"pendingPageRestoreKeys"] containsObject:@"page-0"]);
      XCTAssertNil([pager valueForKey:@"pagerTopScrollView"]);
      // A same-target call cannot resurrect the old reading position.
      [pager scrollToTop:target.integerValue animated:NO];
      XCTAssertEqualWithAccuracy(current.scrollView.contentOffset.y + current.scrollView.contentInset.top, expectedQ, 0.01);
      XCTAssertEqualObjects(offsets[@"page-1"], @360);
    }
  }
}

- (void)testTransitionLayoutCannotSaveSourceOffsetIntoDestinationCache
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:2];
  RNCNativeScrollerComponentView *source = [self addScrollerToPager:pager atIndex:0];
  RNCNativeScrollerComponentView *target = [self addScrollerToPager:pager atIndex:1];
  NSMutableDictionary *offsets = [pager valueForKey:@"pageOffsets"];
  offsets[@"page-0"] = @0;
  offsets[@"page-1"] = @660;
  [pager setValue:@120 forKey:@"headerOffset"];
  [pager attachScrollObserverForCurrentPage];
  XCTAssertEqualWithAccuracy(source.scrollView.contentOffset.y + source.scrollView.contentInset.top, 120, 0.01);
  XCTAssertEqualObjects([pager valueForKey:@"headerOffset"], @120);
  XCTAssertEqualObjects(offsets[@"page-1"], @660);
  [pager detachScrollObserver];
  [pager setValue:@YES forKey:@"transitioning"];
  // A layout pass runs between programmatic transition start and completion.
  [pager attachScrollObserverForCurrentPage];
  [pager setValue:@1 forKey:@"currentIndex"];
  [pager attachScrollObserverForCurrentPageAfterTransition:YES];
  XCTAssertEqualWithAccuracy([offsets[@"page-1"] doubleValue], 660, 0.01);
  XCTAssertEqualWithAccuracy(target.scrollView.contentOffset.y + target.scrollView.contentInset.top, 780, 0.01);
  XCTAssertNotEqual([pager valueForKey:@"observedScrollView"], source.scrollView);
}

- (void)testSmoothTransitionKeepsGlobalHeaderAndIndependentBodyMemory
{
  for (NSNumber *collapse in @[@0, @60, @120]) {
    for (NSNumber *body in @[@0, @60, @660]) {
      RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:2];
      RNCNativeScrollerComponentView *source = [self addScrollerToPager:pager atIndex:0];
      RNCNativeScrollerComponentView *target = [self addScrollerToPager:pager atIndex:1];
      NSMutableDictionary *offsets = [pager valueForKey:@"pageOffsets"];
      offsets[@"page-0"] = @0;
      offsets[@"page-1"] = body;
      [pager setValue:collapse forKey:@"headerOffset"];
      [pager attachScrollObserverForCurrentPage];
      [pager beginPageStateTransition];
      [pager setValue:@YES forKey:@"isPagerDragging"];
      [pager setValue:@YES forKey:@"transitioning"];
      [pager updatePageStateTransitionProgress:0.5];
      CGFloat targetQ = collapse.doubleValue + (collapse.doubleValue == 120 ? body.doubleValue : 0);
      XCTAssertEqualWithAccuracy([[pager valueForKey:@"headerOffset"] doubleValue], collapse.doubleValue, 0.01);
      XCTAssertEqualWithAccuracy(source.scrollView.contentOffset.y + source.scrollView.contentInset.top, collapse.doubleValue, 0.01);
      XCTAssertEqualWithAccuracy(target.scrollView.contentOffset.y + target.scrollView.contentInset.top, targetQ, 0.01);
      XCTAssertEqualObjects(offsets[@"page-0"], @0);
      XCTAssertEqualObjects(offsets[@"page-1"], body);
      // Cancellation restores the source display without consuming either reading memory.
      [pager finishPageStateTransition];
      XCTAssertEqualWithAccuracy(source.scrollView.contentOffset.y + source.scrollView.contentInset.top, collapse.doubleValue, 0.01);
      XCTAssertEqualObjects(offsets[@"page-1"], body);
      [pager setValue:@NO forKey:@"isPagerDragging"];
      [pager setValue:@NO forKey:@"transitioning"];
      [pager attachScrollObserverForCurrentPage];
      [pager beginPageStateTransition];
      [pager setValue:@1 forKey:@"destinationIndex"];
      [pager preparePageForHorizontalTransitionAtIndex:1];
      [pager updatePageStateTransitionProgress:1];
      XCTAssertEqualWithAccuracy(source.scrollView.contentOffset.y + source.scrollView.contentInset.top, collapse.doubleValue, 0.01);
      [pager setValue:@1 forKey:@"currentIndex"];
      [pager attachScrollObserverForCurrentPageAfterTransition:YES];
      XCTAssertEqualWithAccuracy(target.scrollView.contentOffset.y + target.scrollView.contentInset.top, targetQ, 0.01);
      XCTAssertEqualWithAccuracy([[pager valueForKey:@"headerOffset"] doubleValue], collapse.doubleValue, 0.01);
      XCTAssertEqualObjects(offsets[@"page-1"], body);
    }
  }
}

- (void)testNonAdjacentTransitionUsesFixedEndpointsAndRestoresIntermediatePage
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:4];
  for (NSInteger index = 0; index < 4; index++) [self addScrollerToPager:pager atIndex:index];
  NSMutableDictionary *offsets = [pager valueForKey:@"pageOffsets"];
  offsets[@"page-0"] = @0;
  offsets[@"page-1"] = @60;
  offsets[@"page-2"] = @360;
  offsets[@"page-3"] = @660;
  [pager setValue:@60 forKey:@"headerOffset"];
  [pager attachScrollObserverForCurrentPage];
  [pager beginPageStateTransition];
  [pager setValue:@3 forKey:@"destinationIndex"];
  [pager preparePageForHorizontalTransitionAtIndex:3];
  [pager updatePageStateTransitionProgress:1];
  XCTAssertEqualWithAccuracy([[pager valueForKey:@"headerOffset"] doubleValue], 60, 0.01);
  [pager setValue:@3 forKey:@"currentIndex"];
  [pager attachScrollObserverForCurrentPageAfterTransition:YES];
  XCTAssertEqualObjects(offsets[@"page-1"], @60);
  XCTAssertEqualObjects(offsets[@"page-2"], @360);
  XCTAssertEqualObjects(offsets[@"page-3"], @660);
  for (NSInteger index = 1; index < 4; index++) {
    UIViewController *page = [pager valueForKey:@"pageControllers"][index];
    RNCNativeScrollerComponentView *scroller = page.view.subviews.firstObject;
    XCTAssertEqualWithAccuracy(scroller.scrollView.contentOffset.y + scroller.scrollView.contentInset.top,
                              60, 0.01);
  }
}

- (void)testZeroLayoutPreservesPendingBodyRestore
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:1];
  RNCNativeScrollerComponentView *scroller = [self addScrollerToPager:pager atIndex:0];
  NSMutableDictionary *offsets = [pager valueForKey:@"pageOffsets"];
  offsets[@"page-0"] = @660;
  [pager setValue:@120 forKey:@"headerOffset"];
  scroller.scrollView.contentSize = CGSizeMake(0, 0);
  [pager attachScrollObserverForCurrentPage];
  XCTAssertEqualObjects(offsets[@"page-0"], @660);
  XCTAssertTrue([[pager valueForKey:@"pendingPageRestoreKeys"] containsObject:@"page-0"]);
  // Releasing an observed but unmeasured identity must retain its pending B.
  scroller.tag = 91001;
  ShadowView removedView;
  removedView.tag = (Tag)scroller.tag;
  MountingTransaction removal(1, 1, {ShadowViewMutation::RemoveMutation(1, removedView, 0)}, {});
  SurfaceTelemetry telemetry;
  [pager mountingTransactionWillMount:removal withSurfaceTelemetry:telemetry];
  XCTAssertEqualObjects(offsets[@"page-0"], @660);
  [self finishMount:pager];
  XCTAssertEqualObjects(offsets[@"page-0"], @660);
  scroller.scrollView.contentSize = CGSizeMake(320, 1500);
  XCTAssertEqualObjects(offsets[@"page-0"], @660);
  XCTAssertFalse([[pager valueForKey:@"pendingPageRestoreKeys"] containsObject:@"page-0"]);
  XCTAssertEqualWithAccuracy(scroller.scrollView.contentOffset.y + scroller.scrollView.contentInset.top, 780, 0.01);
}

- (void)testUnresolvedReplacementDoesNotCommitTemporaryClamp
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:2];
  RNCNativeScrollerComponentView *source = [self addScrollerToPager:pager atIndex:0];
  NSMutableDictionary *offsets = [pager valueForKey:@"pageOffsets"];
  offsets[@"page-0"] = @0;
  offsets[@"page-1"] = @660;
  [pager setValue:@120 forKey:@"headerOffset"];
  [pager attachScrollObserverForCurrentPage];
  [pager beginPageStateTransition];
  [pager setValue:@1 forKey:@"destinationIndex"];
  UICollectionView *replacement = [[UICollectionView alloc]
    initWithFrame:CGRectMake(0, 0, 320, 600)
    collectionViewLayout:[UICollectionViewFlowLayout new]];
  UIViewController *page = [pager valueForKey:@"pageControllers"][1];
  [page.view addSubview:replacement];
  id released = [NSClassFromString(@"RNCCollapsiblePagerReleasedScrollState") new];
  XCTAssertNotNil(released);
  [released setValue:source.scrollView forKey:@"scrollView"];
  [released setValue:source forKey:@"componentView"];
  [released setValue:@(source.tag) forKey:@"componentTag"];
  [pager valueForKey:@"releasedPageScrollStates"][@"page-1"] = released;
  [pager preparePageForHorizontalTransitionAtIndex:1];
  [pager setValue:@1 forKey:@"currentIndex"];
  [pager finishPageStateTransition];
  XCTAssertEqualObjects(offsets[@"page-1"], @660);
  XCTAssertTrue([[pager valueForKey:@"pendingPageRestoreKeys"] containsObject:@"page-1"]);
}

- (void)testRenamedPageDoesNotResurrectPrunedObserverCache
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithPageCount:1];
  RNCNativeScrollerComponentView *scroller = [self addScrollerToPager:pager atIndex:0];
  NSMutableDictionary *offsets = [pager valueForKey:@"pageOffsets"];
  offsets[@"page-0"] = @60;
  [pager attachScrollObserverForCurrentPage];
  [pager setValue:@[@"replacement-key"] forKey:@"pageKeys"];
  [offsets removeObjectForKey:@"page-0"];
  scroller.scrollView.contentOffset = CGPointMake(0, 200);
  [pager detachScrollObserver];
  XCTAssertNil(offsets[@"page-0"]);
  XCTAssertNil(offsets[@"replacement-key"]);
}

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
