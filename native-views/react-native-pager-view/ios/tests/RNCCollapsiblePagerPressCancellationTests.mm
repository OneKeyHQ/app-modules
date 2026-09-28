#import <XCTest/XCTest.h>
#import <UIKit/UIGestureRecognizerSubclass.h>
#import <React/RCTSurfaceTouchHandler.h>

// Private declarations access the production class compiled into the pod.
@interface RNCCollapsiblePagerContentPressCancellationGestureRecognizer : UIGestureRecognizer
@property (nonatomic, weak) UIGestureRecognizer *reactTouchHandler;
- (void)cancelPressForPan:(UIPanGestureRecognizer *)pan;
- (void)observeIncomingTouch:(UITouch *)touch;
@end

// UIKit can reset terminal states immediately outside real touch delivery.
// Observe requests while retaining the production implementation and UIKit setter.
@interface RNCPressCancellationObservedBridge : RNCCollapsiblePagerContentPressCancellationGestureRecognizer
@property (nonatomic, strong) NSMutableArray<NSNumber *> *requestedStates;
@end
@implementation RNCPressCancellationObservedBridge
- (void)setState:(UIGestureRecognizerState)state
{
  if (self.requestedStates == nil) self.requestedStates = [NSMutableArray new];
  [self.requestedStates addObject:@(state)];
  [super setState:state];
}
@end

// These doubles supply input facts only. The production bridge owns its state
// transitions; this suite does not claim to synthesize UIKit touch delivery.
@interface RNCPressCancellationTestTouch : UITouch
@property (nonatomic, strong) UIView *testView;
@property (nonatomic, assign) UITouchPhase testPhase;
@end
@implementation RNCPressCancellationTestTouch
- (UIView *)view { return self.testView; }
- (UITouchPhase)phase { return self.testPhase; }
@end

@interface RNCPressCancellationTestEvent : UIEvent
@property (nonatomic, copy) NSSet<UITouch *> *testTouches;
@end
@implementation RNCPressCancellationTestEvent
- (NSSet<UITouch *> *)allTouches { return self.testTouches; }
@end

@interface RNCPressCancellationTestPan : UIPanGestureRecognizer
@property (nonatomic, assign) UIGestureRecognizerState testState;
@end
@implementation RNCPressCancellationTestPan
- (UIGestureRecognizerState)state { return self.testState; }
@end

@interface RNCCollapsiblePagerPressCancellationTests : XCTestCase
@property (nonatomic, strong) UIView *host;
@property (nonatomic, strong) UIView *surface;
@property (nonatomic, strong) UIView *content;
@property (nonatomic, strong) RCTSurfaceTouchHandler *handler;
@property (nonatomic, strong) RNCPressCancellationObservedBridge *bridge;
@property (nonatomic, strong) RNCPressCancellationTestPan *pan;
@property (nonatomic, strong) RNCPressCancellationTestTouch *touch;
@end

@implementation RNCCollapsiblePagerPressCancellationTests

- (void)setUp
{
  [super setUp];
  self.host = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 600)];
  self.surface = [[UIView alloc] initWithFrame:self.host.bounds];
  self.content = [[UIView alloc] initWithFrame:self.surface.bounds];
  [self.host addSubview:self.surface];
  [self.surface addSubview:self.content];
  self.handler = [RCTSurfaceTouchHandler new];
  [self.handler attachToView:self.surface];
  Class bridgeClass = NSClassFromString(@"RNCCollapsiblePagerContentPressCancellationGestureRecognizer");
  XCTAssertNotNil(bridgeClass);
  self.bridge = [RNCPressCancellationObservedBridge new];
  self.bridge.reactTouchHandler = self.handler;
  [self.host addGestureRecognizer:self.bridge];
  self.pan = [RNCPressCancellationTestPan new];
  self.pan.testState = UIGestureRecognizerStateBegan;
  self.touch = [self touchInView:self.content phase:UITouchPhaseBegan];
}

- (void)tearDown
{
  [self.host removeGestureRecognizer:self.bridge];
  [self.handler detachFromView:self.surface];
  self.bridge = nil;
  self.handler = nil;
  self.touch = nil;
  self.pan = nil;
  self.content = nil;
  self.surface = nil;
  self.host = nil;
  [super tearDown];
}

- (RNCPressCancellationTestTouch *)touchInView:(UIView *)view phase:(UITouchPhase)phase
{
  RNCPressCancellationTestTouch *touch = [RNCPressCancellationTestTouch new];
  touch.testView = view;
  touch.testPhase = phase;
  return touch;
}

- (RNCPressCancellationTestEvent *)eventWithTouches:(NSSet<UITouch *> *)touches
{
  RNCPressCancellationTestEvent *event = [RNCPressCancellationTestEvent new];
  event.testTouches = touches;
  return event;
}

- (void)beginSingleTouch
{
  [self.bridge touchesBegan:[NSSet setWithObject:self.touch]
                 withEvent:[self eventWithTouches:[NSSet setWithObject:self.touch]]];
}

- (void)testPreventionTargetsExactHandlerAndUsesExternalHost
{
  RCTSurfaceTouchHandler *otherHandler = [RCTSurfaceTouchHandler new];
  UIPanGestureRecognizer *nativePan = [UIPanGestureRecognizer new];
  XCTAssertTrue([self.bridge canPreventGestureRecognizer:self.handler]);
  XCTAssertFalse([self.bridge canPreventGestureRecognizer:otherHandler]);
  XCTAssertFalse([self.bridge canPreventGestureRecognizer:nativePan]);
  XCTAssertTrue([self.handler canBePreventedByGestureRecognizer:self.bridge]);

  [self.content addGestureRecognizer:nativePan];
  XCTAssertFalse([self.handler canBePreventedByGestureRecognizer:nativePan]);
}

- (void)testOnlyBeganPanWithTrackedLiveTouchActivates
{
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStatePossible);
  [self beginSingleTouch];
  for (NSNumber *state in @[@(UIGestureRecognizerStatePossible),
                            @(UIGestureRecognizerStateChanged),
                            @(UIGestureRecognizerStateFailed),
                            @(UIGestureRecognizerStateEnded)]) {
    self.pan.testState = (UIGestureRecognizerState)state.integerValue;
    [self.bridge cancelPressForPan:self.pan];
    XCTAssertEqual(self.bridge.state, UIGestureRecognizerStatePossible);
  }
  self.pan.testState = UIGestureRecognizerStateBegan;
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStateBegan);
}

- (void)testEndedAndCancelledOriginTouchesCannotActivate
{
  [self beginSingleTouch];
  for (NSNumber *phase in @[@(UITouchPhaseEnded), @(UITouchPhaseCancelled)]) {
    self.touch.testPhase = (UITouchPhase)phase.integerValue;
    [self.bridge cancelPressForPan:self.pan];
    XCTAssertEqual(self.bridge.state, UIGestureRecognizerStatePossible);
  }
}

- (void)testSecondActiveTouchOutsidePagerButOnSameSurfaceBlocksCancellation
{
  UIView *sibling = [UIView new];
  [self.surface addSubview:sibling];
  RNCPressCancellationTestTouch *second = [self touchInView:sibling phase:UITouchPhaseStationary];
  [self beginSingleTouch];
  [self.bridge touchesMoved:[NSSet setWithObject:self.touch]
                 withEvent:[self eventWithTouches:[NSSet setWithObjects:self.touch, second, nil]]];
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStatePossible);
}

- (void)testOtherSurfaceAndFinishedTouchesDoNotCountAsActiveSurfaceTouches
{
  UIView *otherSurface = [UIView new];
  [self.host addSubview:otherSurface];
  RNCPressCancellationTestTouch *other = [self touchInView:otherSurface phase:UITouchPhaseMoved];
  RNCPressCancellationTestTouch *ended = [self touchInView:self.surface phase:UITouchPhaseEnded];
  RNCPressCancellationTestTouch *cancelled = [self touchInView:self.content phase:UITouchPhaseCancelled];
  [self.bridge touchesBegan:[NSSet setWithObject:self.touch]
                 withEvent:[self eventWithTouches:[NSSet setWithObjects:self.touch, other, ended, cancelled, nil]]];
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStateBegan);
}

- (void)testMultipleOriginTouchesFailWithoutRecognizing
{
  RNCPressCancellationTestTouch *second = [self touchInView:self.content phase:UITouchPhaseBegan];
  NSSet<UITouch *> *touches = [NSSet setWithObjects:self.touch, second, nil];
  [self.bridge touchesBegan:touches withEvent:[self eventWithTouches:touches]];
  XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateFailed)]);
  XCTAssertNotEqual(self.bridge.state, UIGestureRecognizerStateBegan);
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertNotEqual(self.bridge.state, UIGestureRecognizerStateBegan);
}

- (void)testSecondTouchAfterRecognitionDoesNotFailOrEndTheTrackedGesture
{
  [self beginSingleTouch];
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStateBegan);
  RNCPressCancellationTestTouch *second = [self touchInView:self.content phase:UITouchPhaseBegan];
  RNCPressCancellationTestEvent *event =
    [self eventWithTouches:[NSSet setWithObjects:self.touch, second, nil]];
  NSUInteger requestCount = self.bridge.requestedStates.count;
  XCTAssertNoThrow([self.bridge touchesBegan:[NSSet setWithObject:second] withEvent:event]);
  XCTAssertEqual(self.bridge.requestedStates.count, requestCount);
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStateBegan);
  second.testPhase = UITouchPhaseEnded;
  [self.bridge touchesEnded:[NSSet setWithObject:second] withEvent:event];
  XCTAssertEqual(self.bridge.requestedStates.count, requestCount);
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStateBegan);
  self.touch.testPhase = UITouchPhaseEnded;
  [self.bridge touchesEnded:[NSSet setWithObject:self.touch] withEvent:event];
  XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateEnded)]);
  XCTAssertNotEqual(self.bridge.state, UIGestureRecognizerStateBegan);
}

- (void)testSecondTouchCancellationDoesNotCancelTheTrackedGesture
{
  [self beginSingleTouch];
  [self.bridge cancelPressForPan:self.pan];
  RNCPressCancellationTestTouch *second = [self touchInView:self.content phase:UITouchPhaseBegan];
  RNCPressCancellationTestEvent *event =
    [self eventWithTouches:[NSSet setWithObjects:self.touch, second, nil]];
  [self.bridge touchesBegan:[NSSet setWithObject:second] withEvent:event];
  second.testPhase = UITouchPhaseCancelled;
  [self.bridge touchesCancelled:[NSSet setWithObject:second] withEvent:event];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStateBegan);
  self.touch.testPhase = UITouchPhaseCancelled;
  [self.bridge touchesCancelled:[NSSet setWithObject:self.touch] withEvent:event];
  XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateCancelled)]);
  XCTAssertNotEqual(self.bridge.state, UIGestureRecognizerStateBegan);
}

- (void)testRejectedSameSurfaceTouchRemainsLatchedUntilReset
{
  UIView *sibling = [UIView new];
  [self.surface addSubview:sibling];
  RNCPressCancellationTestTouch *second = [self touchInView:sibling phase:UITouchPhaseBegan];
  [self beginSingleTouch];
  // The pager delegate observes this touch even when it rejects delivery to the bridge.
  [self.bridge observeIncomingTouch:second];
  second.testPhase = UITouchPhaseEnded;
  [self.bridge touchesMoved:[NSSet setWithObject:self.touch]
                 withEvent:[self eventWithTouches:[NSSet setWithObject:self.touch]]];
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStatePossible);
  [self.bridge reset];
  [self beginSingleTouch];
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStateBegan);
}

- (void)testObservedOtherSurfaceTouchDoesNotLatchCurrentSurface
{
  UIView *otherSurface = [UIView new];
  [self.host addSubview:otherSurface];
  RNCPressCancellationTestTouch *second = [self touchInView:otherSurface phase:UITouchPhaseBegan];
  [self beginSingleTouch];
  [self.bridge observeIncomingTouch:second];
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStateBegan);
}

- (void)testHostMismatchDoesNotCancel
{
  [self.host removeGestureRecognizer:self.bridge];
  [self.surface addGestureRecognizer:self.bridge];
  [self beginSingleTouch];
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertNotEqual(self.bridge.state, UIGestureRecognizerStateBegan);
  [self.surface removeGestureRecognizer:self.bridge];
}

- (void)testResetDropsPendingTouchAndAllowsANewSequence
{
  [self beginSingleTouch];
  [self.bridge reset];
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStatePossible);
  [self beginSingleTouch];
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStateBegan);
}

- (void)testRecognizedSequenceEndsAndReleasesEvent
{
  __weak UIEvent *weakEvent;
  @autoreleasepool {
    RNCPressCancellationTestEvent *event = [self eventWithTouches:[NSSet setWithObject:self.touch]];
    weakEvent = event;
    [self.bridge touchesBegan:[NSSet setWithObject:self.touch] withEvent:event];
    [self.bridge cancelPressForPan:self.pan];
    XCTAssertEqual(self.bridge.state, UIGestureRecognizerStateBegan);
    self.touch.testPhase = UITouchPhaseEnded;
    [self.bridge touchesEnded:[NSSet setWithObject:self.touch] withEvent:event];
    XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateEnded)]);
    XCTAssertNotEqual(self.bridge.state, UIGestureRecognizerStateBegan);
  }
  XCTAssertNil(weakEvent);
}

- (void)testUnrecognizedCancellationFailsAndReleasesEvent
{
  __weak UIEvent *weakEvent;
  @autoreleasepool {
    RNCPressCancellationTestEvent *event = [self eventWithTouches:[NSSet setWithObject:self.touch]];
    weakEvent = event;
    [self.bridge touchesBegan:[NSSet setWithObject:self.touch] withEvent:event];
    self.touch.testPhase = UITouchPhaseCancelled;
    [self.bridge touchesCancelled:[NSSet setWithObject:self.touch] withEvent:event];
    XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateFailed)]);
    XCTAssertNotEqual(self.bridge.state, UIGestureRecognizerStateBegan);
  }
  XCTAssertNil(weakEvent);
}

- (void)testRecognizedCancellationTerminatesTheBridge
{
  [self beginSingleTouch];
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStateBegan);
  self.touch.testPhase = UITouchPhaseCancelled;
  [self.bridge touchesCancelled:[NSSet setWithObject:self.touch]
                     withEvent:[self eventWithTouches:[NSSet setWithObject:self.touch]]];
  XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateCancelled)]);
  XCTAssertNotEqual(self.bridge.state, UIGestureRecognizerStateBegan);
}

- (void)testHandlerBindingIsWeak
{
  __weak UIGestureRecognizer *weakHandler;
  @autoreleasepool {
    UIGestureRecognizer *handler = [UIGestureRecognizer new];
    weakHandler = handler;
    self.bridge.reactTouchHandler = handler;
    XCTAssertTrue([self.bridge canPreventGestureRecognizer:handler]);
    XCTAssertFalse([self.bridge canPreventGestureRecognizer:self.handler]);
  }
  XCTAssertNil(weakHandler);
  XCTAssertNil(self.bridge.reactTouchHandler);
  [self beginSingleTouch];
  [self.bridge cancelPressForPan:self.pan];
  XCTAssertEqual(self.bridge.state, UIGestureRecognizerStatePossible);
}

@end
