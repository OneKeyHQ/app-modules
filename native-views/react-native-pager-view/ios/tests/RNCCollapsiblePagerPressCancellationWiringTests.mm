#import <XCTest/XCTest.h>
#import <React/RCTSurfaceTouchHandler.h>

#import "RNCCollapsiblePagerViewComponentView.h"
#import <react/renderer/components/pagerview/Props.h>

using namespace facebook::react;

@interface RNCPressWiringTestTouch : UITouch
@property (nonatomic, strong) UIView *testView;
@end
@implementation RNCPressWiringTestTouch
- (UIView *)view { return self.testView; }
@end

@interface RNCCollapsiblePagerPressCancellationWiringTests : XCTestCase
@property (nonatomic, strong) UIWindow *window;
@property (nonatomic, strong) UIView *host;
@property (nonatomic, strong) UIView *surface;
@property (nonatomic, strong) RCTSurfaceTouchHandler *handler;
@property (nonatomic, strong) RNCCollapsiblePagerViewComponentView *pager;
@end

@implementation RNCCollapsiblePagerPressCancellationWiringTests

- (void)setUp
{
  [super setUp];
  // Window membership installs the bridge without presenting or taking focus.
  self.window = [[UIWindow alloc] initWithFrame:CGRectMake(0, 0, 320, 600)];
  self.host = [[UIView alloc] initWithFrame:self.window.bounds];
  self.surface = [[UIView alloc] initWithFrame:self.host.bounds];
  [self.window addSubview:self.host];
  [self.host addSubview:self.surface];
  self.handler = [RCTSurfaceTouchHandler new];
  [self.handler attachToView:self.surface];
  self.pager = [[RNCCollapsiblePagerViewComponentView alloc] init];
  auto props = std::make_shared<RNCCollapsiblePagerViewProps>();
  props->nativeSmoothHeaderScrollEnabled = true;
  [self.pager updateProps:props oldProps:nullptr];
  self.pager.frame = self.surface.bounds;
  [self.surface addSubview:self.pager];
}

- (void)tearDown
{
  [self.pager removeFromSuperview];
  [self.handler detachFromView:self.surface];
  self.pager = nil;
  self.handler = nil;
  self.surface = nil;
  self.host = nil;
  self.window = nil;
  [super tearDown];
}

- (void)testInstalledBridgeCancelsTouchesAndFiltersItsContentScope
{
  UIGestureRecognizer *bridge = [self.pager valueForKey:@"contentPressCancellationGesture"];
  XCTAssertNotNil(bridge);
  XCTAssertTrue([bridge isKindOfClass:NSClassFromString(@"RNCCollapsiblePagerContentPressCancellationGestureRecognizer")]);
  XCTAssertEqual(bridge.view, self.host);
  XCTAssertEqual([bridge valueForKey:@"reactTouchHandler"], self.handler);
  XCTAssertTrue(bridge.cancelsTouchesInView);
  XCTAssertFalse(bridge.delaysTouchesBegan);
  XCTAssertFalse(bridge.delaysTouchesEnded);
  XCTAssertTrue([self.handler canBePreventedByGestureRecognizer:bridge]);

  UIScrollView *scrollView = [self.pager valueForKey:@"pagerScrollView"];
  XCTAssertNotNil(scrollView);
  UIView *content = [UIView new];
  [scrollView addSubview:content];
  UIView *header = [self.pager valueForKey:@"sharedHeaderHostView"];
  // Smooth mode reparents the shared header into the same content subtree.
  [scrollView addSubview:header];
  UIView *headerButton = [UIView new];
  [header addSubview:headerButton];
  RNCPressWiringTestTouch *touch = [RNCPressWiringTestTouch new];
  id<UIGestureRecognizerDelegate> delegate = bridge.delegate;
  XCTAssertEqual((id)delegate, self.pager);
  touch.testView = content;
  XCTAssertTrue([delegate gestureRecognizer:bridge shouldReceiveTouch:touch]);
  touch.testView = headerButton;
  XCTAssertFalse([delegate gestureRecognizer:bridge shouldReceiveTouch:touch]);
  touch.testView = self.surface;
  XCTAssertFalse([delegate gestureRecognizer:bridge shouldReceiveTouch:touch]);
}

- (void)testWindowDetachUnbindsInstalledBridge
{
  UIGestureRecognizer *bridge = [self.pager valueForKey:@"contentPressCancellationGesture"];
  XCTAssertNotNil(bridge);
  [self.pager removeFromSuperview];
  XCTAssertNil(bridge.view);
  XCTAssertNil([bridge valueForKey:@"reactTouchHandler"]);
  XCTAssertNil([self.pager valueForKey:@"contentPressCancellationGesture"]);
}

- (void)testRecycleUnbindsInstalledBridge
{
  UIGestureRecognizer *bridge = [self.pager valueForKey:@"contentPressCancellationGesture"];
  XCTAssertNotNil(bridge);
  [self.pager prepareForRecycle];
  XCTAssertNil(bridge.view);
  XCTAssertNil([bridge valueForKey:@"reactTouchHandler"]);
  XCTAssertNil([self.pager valueForKey:@"contentPressCancellationGesture"]);
}

@end
