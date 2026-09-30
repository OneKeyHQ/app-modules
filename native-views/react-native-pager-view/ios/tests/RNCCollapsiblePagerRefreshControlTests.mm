#import <XCTest/XCTest.h>
#import "RNCCollapsiblePagerViewComponentView.h"

@interface RNCCollapsiblePagerViewComponentView (RefreshControlTesting)
- (void)updateRefreshControlForScrollView:(UIScrollView *)scrollView;
- (void)releaseRefreshControlForScrollView:(UIScrollView *)scrollView;
- (void)attachScrollObserverForCurrentPage;
@end

@interface RNCCollapsiblePagerRefreshControlTests : XCTestCase
@end

@implementation RNCCollapsiblePagerRefreshControlTests

- (RNCCollapsiblePagerViewComponentView *)pagerWithHeader:(CGFloat)height
{
  RNCCollapsiblePagerViewComponentView *pager = [RNCCollapsiblePagerViewComponentView new];
  [pager setValue:@YES forKey:@"nativeSmoothHeaderScrollEnabled"];
  [pager setValue:@(height) forKey:@"headerHeight"];
  [pager setValue:@44 forKey:@"stickyHeaderHeight"];
  return pager;
}

- (void)testLateControlAndCallerOffsetUpdatesPreserveBaseline
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithHeader:64];
  UIScrollView *scroll = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, 600)];
  [pager updateRefreshControlForScrollView:scroll];
  UIRefreshControl *control = [UIRefreshControl new];
  control.bounds = CGRectMake(3, -12, 320, 60);
  scroll.refreshControl = control;
  XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, -108, 0.001);
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, -12, 0.001);
  [pager updateRefreshControlForScrollView:scroll];
  XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, -108, 0.001);
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, -12, 0.001);
  control.layer.sublayerTransform = control.layer.sublayerTransform;
  // Also cover an explicit no-op KVO notification, even if CALayer's setter
  // suppresses notifications for equal transforms on the current OS.
  [control.layer willChangeValueForKey:@"sublayerTransform"];
  [control.layer didChangeValueForKey:@"sublayerTransform"];
  XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, -108, 0.001);

  // Fabric's progressViewOffset setter replaces bounds.origin.y directly.
  control.bounds = CGRectMake(3, -20, 320, 60);
  XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, -108, 0.001);
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, -20, 0.001);
  [pager setValue:@100 forKey:@"headerHeight"];
  [pager updateRefreshControlForScrollView:scroll];
  XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, -144, 0.001);
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, -20, 0.001);
  [pager releaseRefreshControlForScrollView:scroll];
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, -20, 0.001);
  XCTAssertEqualWithAccuracy(control.bounds.origin.x, 3, 0.001);
}

- (void)testUIKitSizeChangesAndRefreshCycleDoNotAccumulateOffset
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithHeader:64];
  UIScrollView *scroll = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, 600)];
  // UIKit ignores offscreen beginRefreshing calls. Exercise the actual refresh
  // lifecycle in a visible window rather than weakening the state assertion.
  UIWindow *window = [[UIWindow alloc] initWithFrame:scroll.frame];
  UIViewController *controller = [UIViewController new];
  window.rootViewController = controller;
  scroll.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
  [controller.view addSubview:scroll];
  window.hidden = NO;
  [self addTeardownBlock:^{ window.hidden = YES; }];
  UIRefreshControl *control = [UIRefreshControl new];
  scroll.refreshControl = control;
  [window layoutIfNeeded];
  [scroll layoutIfNeeded];
  XCTAssertEqual(control.window, window);
  [pager updateRefreshControlForScrollView:scroll];
  CGRect bounds = control.bounds;
  bounds.size = CGSizeMake(320, 60);
  control.bounds = bounds;
  XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, -108, 0.001);
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, 0, 0.001);
  [control beginRefreshing];
  [control layoutIfNeeded];
  [pager updateRefreshControlForScrollView:scroll];
  XCTAssertTrue(control.isRefreshing);
  XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, -108, 0.001);
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, 0, 0.001);
  [control endRefreshing];
  [control layoutIfNeeded];
  XCTAssertFalse(control.isRefreshing);
  XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, -108, 0.001);
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, 0, 0.001);
  [pager releaseRefreshControlForScrollView:scroll];
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, 0, 0.001);
  XCTAssertTrue(CATransform3DIsIdentity(control.layer.sublayerTransform));
}

- (void)testReplacementAndNestedOwnerReleaseRestoreOnlyTheirContributions
{
  RNCCollapsiblePagerViewComponentView *first = [self pagerWithHeader:64];
  RNCCollapsiblePagerViewComponentView *second = [self pagerWithHeader:20];
  UICollectionView *scroll = [[UICollectionView alloc] initWithFrame:CGRectMake(0, 0, 320, 600)
                                                        collectionViewLayout:[UICollectionViewFlowLayout new]];
  UIRefreshControl *original = [UIRefreshControl new];
  scroll.refreshControl = original;
  [first updateRefreshControlForScrollView:scroll];
  [second updateRefreshControlForScrollView:scroll];
  XCTAssertEqualWithAccuracy(original.layer.sublayerTransform.m42, -172, 0.001);
  [first releaseRefreshControlForScrollView:scroll];
  XCTAssertEqualWithAccuracy(original.layer.sublayerTransform.m42, -64, 0.001);

  UIRefreshControl *replacement = [UIRefreshControl new];
  replacement.bounds = CGRectMake(0, -8, 320, 60);
  scroll.refreshControl = replacement;
  XCTAssertTrue(CATransform3DIsIdentity(original.layer.sublayerTransform));
  XCTAssertEqualWithAccuracy(replacement.layer.sublayerTransform.m42, -64, 0.001);
  XCTAssertEqualWithAccuracy(replacement.bounds.origin.y, -8, 0.001);
  [second prepareForRecycle];
  XCTAssertEqualWithAccuracy(replacement.bounds.origin.y, -8, 0.001);
  XCTAssertTrue(CATransform3DIsIdentity(replacement.layer.sublayerTransform));
  scroll.refreshControl = nil;
}

- (void)testPagerDestructionRestoresControl
{
  UIScrollView *scroll = [UIScrollView new];
  UIRefreshControl *control = [UIRefreshControl new];
  scroll.refreshControl = control;
  @autoreleasepool {
    RNCCollapsiblePagerViewComponentView *pager = [self pagerWithHeader:64];
    [pager updateRefreshControlForScrollView:scroll];
    XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, -108, 0.001);
    XCTAssertEqualWithAccuracy(control.bounds.origin.y, 0, 0.001);
  }
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, 0, 0.001);
  XCTAssertTrue(CATransform3DIsIdentity(control.layer.sublayerTransform));
}

- (void)testCallerOffsetCollisionDoesNotAffectVisualContribution
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithHeader:56];
  UIScrollView *scroll = [UIScrollView new];
  UIRefreshControl *control = [UIRefreshControl new];
  scroll.refreshControl = control;
  control.bounds = CGRectMake(0, -200, 320, 60);
  [pager updateRefreshControlForScrollView:scroll];
  control.bounds = CGRectMake(0, -100, 320, 60);
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, -100, 0.001);
  XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, -100, 0.001);
  [pager releaseRefreshControlForScrollView:scroll];
  XCTAssertEqualWithAccuracy(control.bounds.origin.y, -100, 0.001);
  XCTAssertTrue(CATransform3DIsIdentity(control.layer.sublayerTransform));
}

- (void)testNestedOwnerDestructionPreservesBaselineAndRestoresClipping
{
  RNCCollapsiblePagerViewComponentView *second = [self pagerWithHeader:20];
  UIScrollView *scroll = [UIScrollView new];
  UIRefreshControl *control = [UIRefreshControl new];
  scroll.refreshControl = control;
  CATransform3D baseline = CATransform3DMakeTranslation(7, 13, 0);
  control.layer.sublayerTransform = baseline;
  control.layer.masksToBounds = YES;
  @autoreleasepool {
    RNCCollapsiblePagerViewComponentView *first = [self pagerWithHeader:64];
    [first updateRefreshControlForScrollView:scroll];
    [second updateRefreshControlForScrollView:scroll];
    XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, 13 - 172, 0.001);
    XCTAssertFalse(control.layer.masksToBounds);
  }
  XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, 13 - 64, 0.001);
  // A UIKit layer reset must not discard the remaining visual contribution.
  control.layer.sublayerTransform = baseline;
  control.layer.masksToBounds = YES;
  XCTAssertEqualWithAccuracy(control.layer.sublayerTransform.m42, 13 - 64, 0.001);
  XCTAssertFalse(control.layer.masksToBounds);
  [second releaseRefreshControlForScrollView:scroll];
  XCTAssertTrue(CATransform3DEqualToTransform(control.layer.sublayerTransform, baseline));
  XCTAssertTrue(control.layer.masksToBounds);
}

- (void)testRefreshTopInsetDoesNotDisplaceAttachedSharedHeader
{
  RNCCollapsiblePagerViewComponentView *pager = [self pagerWithHeader:64];
  UIScrollView *scroll = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, 320, 600)];
  scroll.refreshControl = [UIRefreshControl new];
  scroll.contentSize = CGSizeMake(320, 2000);
  scroll.contentInset = UIEdgeInsetsMake(232, 0, 0, 0);
  UIView *content = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 2000)];
  [scroll addSubview:content];
  UIViewController *page = [UIViewController new];
  [page.view addSubview:scroll];
  NSMutableArray<UIViewController *> *pages = [pager valueForKey:@"pageControllers"];
  [pages addObject:page];
  [pager attachScrollObserverForCurrentPage];
  UIView *headerHost = [pager valueForKey:@"sharedHeaderHostView"];
  UIView *stickyHeader = [pager valueForKey:@"stickyHeaderView"];
  UIView *tabBar = [pager valueForKey:@"nativeTabBarView"];
  XCTAssertEqual(headerHost.superview, scroll);
  XCTAssertEqualWithAccuracy(scroll.contentInset.top, 340, 0.001);
  XCTAssertEqualWithAccuracy(headerHost.transform.ty, 0, 0.001);

  scroll.contentOffset = CGPointMake(0, 500);
  CGFloat baselineStickyTranslation = stickyHeader.transform.ty;
  CGFloat baselineTabBarTranslation = tabBar.transform.ty;
  CGPoint offset = scroll.contentOffset;
  scroll.contentInset = UIEdgeInsetsMake(400, 0, 0, 0);
  XCTAssertTrue(CGPointEqualToPoint(scroll.contentOffset, offset));
  XCTAssertEqualWithAccuracy(headerHost.transform.ty, 0, 0.001);
  XCTAssertEqualWithAccuracy(stickyHeader.transform.ty, baselineStickyTranslation, 0.001);
  XCTAssertEqualWithAccuracy(tabBar.transform.ty, baselineTabBarTranslation, 0.001);
  XCTAssertEqualWithAccuracy(CGRectGetMaxY(headerHost.frame), CGRectGetMinY(content.frame), 0.001);

  scroll.contentInset = UIEdgeInsetsMake(340, 0, 0, 0);
  XCTAssertEqualWithAccuracy(headerHost.transform.ty, 0, 0.001);
  XCTAssertEqualWithAccuracy(stickyHeader.transform.ty, baselineStickyTranslation, 0.001);
  XCTAssertEqualWithAccuracy(tabBar.transform.ty, baselineTabBarTranslation, 0.001);

  scroll.contentInset = UIEdgeInsetsMake(400, 0, 0, 0);
  XCTAssertEqualWithAccuracy(headerHost.transform.ty, 0, 0.001);
  XCTAssertEqualWithAccuracy(stickyHeader.transform.ty, baselineStickyTranslation, 0.001);
  XCTAssertEqualWithAccuracy(tabBar.transform.ty, baselineTabBarTranslation, 0.001);
  XCTAssertEqualWithAccuracy(CGRectGetMaxY(headerHost.frame), CGRectGetMinY(content.frame), 0.001);
  scroll.refreshControl = nil;
  XCTAssertEqualWithAccuracy(headerHost.transform.ty, 0, 0.001);
  [pager prepareForRecycle];
}

@end
