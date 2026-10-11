#import <XCTest/XCTest.h>
#import <UIKit/UIGestureRecognizerSubclass.h>
#import <React/RCTSurfaceTouchHandler.h>
#import <React/RCTViewComponentView.h>
#import <dlfcn.h>
#import <mach/mach_time.h>
#import <objc/message.h>

#import "RNCCollapsiblePagerViewComponentView.h"
#import <react/renderer/components/pagerview/Props.h>

using namespace facebook::react;

#pragma mark - UIKit gesture environment and touch delivery

// Pod unit tests run in xctest without an application. UIKit then has no
// gesture environment: every programmatic recognizer state change, including
// Began on a plain UIGestureRecognizer, is dropped. Create the application
// singleton (what UIApplicationMain does first) so recognizers run under the
// real UIGestureEnvironment. Touches below are delivered through
// -[UIWindow sendEvent:], the path UIApplication uses for touch events, using
// the same private UITouch/UIEvent/IOHIDEvent calls as KIF. These are
// test-only; a missing symbol fails the test instead of skipping it.
@interface RNCPressCancellationTestApplication : UIApplication
@end

@implementation RNCPressCancellationTestApplication
// UIApplicationMain never ran, so there is no event run loop. UIScrollView
// pushes a tracking run-loop mode when its real pan begins; scheduling is
// outside the recognizer semantics under test.
- (void)_pushRunLoopMode:(id)mode requester:(id)requester reason:(id)reason {}
- (void)_popRunLoopMode:(id)mode requester:(id)requester reason:(id)reason {}
@end

static BOOL RNCEnsureUIKitGestureEnvironment(void)
{
  if (UIApplication.sharedApplication != nil) return YES;
  void (*instantiate)(Class) =
    (void (*)(Class))dlsym(RTLD_DEFAULT, "UIApplicationInstantiateSingleton");
  if (instantiate == NULL) return NO;
  instantiate(RNCPressCancellationTestApplication.class);
  return UIApplication.sharedApplication != nil;
}

typedef struct __IOHIDEvent *RNCTestHIDEventRef;

@interface UITouch (RNCPressCancellationSynthesis)
- (void)setWindow:(UIWindow *)window;
- (void)setView:(UIView *)view;
- (void)setGestureView:(UIView *)view;
- (void)setPhase:(UITouchPhase)phase;
- (void)setTimestamp:(NSTimeInterval)timestamp;
- (void)setTapCount:(NSUInteger)tapCount;
- (void)_setLocationInWindow:(CGPoint)location resetPrevious:(BOOL)resetPrevious;
- (void)_setIsTapToClick:(BOOL)isTapToClick;
- (void)_setHidEvent:(RNCTestHIDEventRef)event;
@end

@interface UIEvent (RNCPressCancellationSynthesis)
- (instancetype)_init;
- (void)_clearTouches;
- (void)_addTouch:(UITouch *)touch forDelayedDelivery:(BOOL)delayed;
- (void)_setHIDEvent:(RNCTestHIDEventRef)event;
@end

// Delivers one finger or several simultaneous fingers through a window.
@interface RNCSynthesizedTouchDriver : NSObject
- (instancetype)initWithWindow:(UIWindow *)window;
@property (nonatomic, readonly) NSString *unavailableReason;
- (UITouch *)beginAt:(CGPoint)windowPoint;
- (void)move:(UITouch *)touch to:(CGPoint)windowPoint;
- (void)end:(UITouch *)touch;
@end

@implementation RNCSynthesizedTouchDriver {
  UIWindow *_window;
  UIEvent *_event;
  NSMutableArray<UITouch *> *_activeTouches;
  NSTimeInterval _timestamp;
  RNCTestHIDEventRef (*_createHand)(CFAllocatorRef, uint64_t, uint32_t, uint32_t, uint32_t,
                                    uint32_t, uint32_t, double, double, double, double, double,
                                    Boolean, Boolean, uint32_t);
  RNCTestHIDEventRef (*_createFinger)(CFAllocatorRef, uint64_t, uint32_t, uint32_t, uint32_t,
                                      double, double, double, double, double, double, double,
                                      double, double, double, Boolean, Boolean, uint32_t);
  void (*_setIntegerValue)(RNCTestHIDEventRef, uint32_t, long, uint32_t);
  void (*_appendEvent)(RNCTestHIDEventRef, RNCTestHIDEventRef, uint32_t);
  void (*_setSenderID)(RNCTestHIDEventRef, uint64_t);
}

- (instancetype)initWithWindow:(UIWindow *)window
{
  if (self = [super init]) {
    _window = window;
    _activeTouches = [NSMutableArray new];
    _timestamp = NSProcessInfo.processInfo.systemUptime;
    void *iokit = dlopen("/System/Library/Frameworks/IOKit.framework/IOKit", RTLD_NOW);
    _createHand = (decltype(_createHand))dlsym(iokit, "IOHIDEventCreateDigitizerEvent");
    _createFinger =
      (decltype(_createFinger))dlsym(iokit, "IOHIDEventCreateDigitizerFingerEventWithQuality");
    _setIntegerValue =
      (decltype(_setIntegerValue))dlsym(iokit, "IOHIDEventSetIntegerValueWithOptions");
    _appendEvent = (decltype(_appendEvent))dlsym(iokit, "IOHIDEventAppendEvent");
    _setSenderID = (decltype(_setSenderID))dlsym(iokit, "IOHIDEventSetSenderID");
    Class eventClass = NSClassFromString(@"UITouchesEvent");
    if (!RNCEnsureUIKitGestureEnvironment()) {
      _unavailableReason = @"UIApplication singleton unavailable";
    } else if (_createHand == NULL || _createFinger == NULL || _setIntegerValue == NULL ||
               _appendEvent == NULL || _setSenderID == NULL) {
      _unavailableReason = @"IOHIDEvent symbols unavailable";
    } else if (eventClass == Nil || ![eventClass instancesRespondToSelector:@selector(_init)] ||
               ![eventClass instancesRespondToSelector:@selector(_addTouch:forDelayedDelivery:)] ||
               ![UITouch instancesRespondToSelector:@selector(_setLocationInWindow:resetPrevious:)] ||
               ![UITouch instancesRespondToSelector:@selector(_setHidEvent:)]) {
      _unavailableReason = @"UITouch/UITouchesEvent synthesis selectors unavailable";
    } else {
      _event = [[eventClass alloc] _init];
    }
  }
  return self;
}

- (RNCTestHIDEventRef)newHIDEvent
{
  static const uint32_t kDigitizerDisplayIntegrated = (11 << 16) | 25;
  static const uint32_t kIsBuiltIn = 4;
  static const uint32_t kOptions = (uint32_t)-268435456;
  uint64_t machTime = mach_absolute_time();
  // Hand transducer, touch mask.
  RNCTestHIDEventRef hand =
    _createHand(kCFAllocatorDefault, machTime, 3, 0, 0, 1 << 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
  _setIntegerValue(hand, kDigitizerDisplayIntegrated, 1, kOptions);
  _setIntegerValue(hand, kIsBuiltIn, 1, kOptions);
  _setSenderID(hand, 0x000000010000027F);
  uint32_t index = 0;
  for (UITouch *touch in _activeTouches) {
    UITouchPhase phase = touch.phase;
    // Position for moves; range and touch for contact changes.
    uint32_t mask = phase == UITouchPhaseMoved ? (1 << 2) : ((1 << 0) | (1 << 1));
    Boolean down = phase != UITouchPhaseEnded && phase != UITouchPhaseCancelled;
    CGPoint point = [touch locationInView:_window];
    RNCTestHIDEventRef finger = _createFinger(kCFAllocatorDefault, machTime, ++index, 2, mask,
                                              point.x, point.y, 0, 0, 0, 5, 5, 1, 1, 1,
                                              down, down, 0);
    _setIntegerValue(finger, kDigitizerDisplayIntegrated, 1, kOptions);
    _appendEvent(hand, finger, 0);
    CFRelease(finger);
  }
  return hand;
}

- (void)sendChangingTouch:(UITouch *)changed phase:(UITouchPhase)phase
{
  NSAssert(_event != nil, @"%@", _unavailableReason);
  _timestamp += 1.0 / 60.0;
  for (UITouch *touch in _activeTouches) {
    [touch setPhase:touch == changed ? phase : UITouchPhaseStationary];
    [touch setTimestamp:_timestamp];
  }
  RNCTestHIDEventRef hid = [self newHIDEvent];
  for (UITouch *touch in _activeTouches) [touch _setHidEvent:hid];
  [_event _setHIDEvent:hid];
  if (phase == UITouchPhaseBegan) [_event _addTouch:changed forDelayedDelivery:NO];
  CFRelease(hid);
  [_window sendEvent:_event];
  if (phase == UITouchPhaseEnded || phase == UITouchPhaseCancelled) {
    [_activeTouches removeObjectIdenticalTo:changed];
    // A finished finger must not be delivered again when another finger ends.
    [_event _clearTouches];
    for (UITouch *touch in _activeTouches) [_event _addTouch:touch forDelayedDelivery:NO];
  }
}

- (UITouch *)beginAt:(CGPoint)windowPoint
{
  if (_activeTouches.count == 0) [_event _clearTouches];
  UITouch *touch = [UITouch new];
  UIView *view = [_window hitTest:windowPoint withEvent:nil];
  [touch setWindow:_window];
  [touch _setLocationInWindow:windowPoint resetPrevious:YES];
  [touch setView:view];
  if ([touch respondsToSelector:@selector(setGestureView:)]) [touch setGestureView:view];
  [touch _setIsTapToClick:NO];
  [touch setTapCount:1];
  [_activeTouches addObject:touch];
  [self sendChangingTouch:touch phase:UITouchPhaseBegan];
  return touch;
}

- (void)move:(UITouch *)touch to:(CGPoint)windowPoint
{
  [touch _setLocationInWindow:windowPoint resetPrevious:NO];
  [self sendChangingTouch:touch phase:UITouchPhaseMoved];
}

- (void)end:(UITouch *)touch
{
  [self sendChangingTouch:touch phase:UITouchPhaseEnded];
}

@end

// Private declarations access the production class compiled into the pod.
@interface RNCCollapsiblePagerContentPressCancellationGestureRecognizer : UIGestureRecognizer
@property (nonatomic, weak) UIGestureRecognizer *reactTouchHandler;
- (void)cancelPressForPan:(UIPanGestureRecognizer *)pan;
- (void)observeIncomingTouch:(UITouch *)touch;
@end

// Terminal states are reset by UIKit once it processes them. Observe requests
// while retaining the production implementation and UIKit setter.
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

// These doubles supply input facts to the bridge's decision logic directly.
// The production bridge owns its state transitions. End-to-end UIKit delivery
// through the real pager is covered by the delivery suite below.
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
@property (nonatomic, strong) UIWindow *window;
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
  XCTAssertTrue(NSThread.isMainThread);
  // Without an application UIKit drops every programmatic recognizer state.
  XCTAssertTrue(RNCEnsureUIKitGestureEnvironment());
  // Register the recognizers with a UIKit gesture environment without taking focus.
  self.window = [[UIWindow alloc] initWithFrame:CGRectMake(0, 0, 320, 600)];
  self.window.hidden = NO;
  self.host = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 600)];
  self.surface = [[UIView alloc] initWithFrame:self.host.bounds];
  self.content = [[UIView alloc] initWithFrame:self.surface.bounds];
  [self.window addSubview:self.host];
  [self.host addSubview:self.surface];
  [self.surface addSubview:self.content];
  self.handler = [RCTSurfaceTouchHandler new];
  [self.handler attachToView:self.surface];
  Class bridgeClass = NSClassFromString(@"RNCCollapsiblePagerContentPressCancellationGestureRecognizer");
  XCTAssertNotNil(bridgeClass);
  self.bridge = [RNCPressCancellationObservedBridge new];
  self.bridge.reactTouchHandler = self.handler;
  [self.host addGestureRecognizer:self.bridge];
  XCTAssertEqual(self.bridge.view.window, self.window);
  XCTAssertEqual(self.handler.view.window, self.window);
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
  [self.host removeFromSuperview];
  self.host = nil;
  self.window.hidden = YES;
  self.window = nil;
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
  XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateBegan)],
                @"The production bridge must request recognition before UIKit retains Began");
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
  XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateBegan)],
                @"The production bridge must request recognition before UIKit retains Began");
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
  XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateBegan)],
                @"The production bridge must request recognition before UIKit retains Began");
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
  XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateBegan)],
                @"The production bridge must request recognition before UIKit retains Began");
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
  XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateBegan)],
                @"The production bridge must request recognition before UIKit retains Began");
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
  XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateBegan)],
                @"The production bridge must request recognition before UIKit retains Began");
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
    XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateBegan)],
                  @"The production bridge must request recognition before UIKit retains Began");
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
  XCTAssertTrue([self.bridge.requestedStates containsObject:@(UIGestureRecognizerStateBegan)],
                @"The production bridge must request recognition before UIKit retains Began");
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

#pragma mark - Real UIKit delivery through the production pager

// Records what RN's Fabric touch handler observes. Its reset dispatches
// touchCancel for every still-registered touch, which is how a Pressable loses
// its press when an external recognizer prevents the handler.
@interface RNCRecordingSurfaceTouchHandler : RCTSurfaceTouchHandler
@property (nonatomic, strong) NSMutableArray<NSString *> *events;
@property (nonatomic, assign) NSInteger registeredTouches;
@end

@implementation RNCRecordingSurfaceTouchHandler
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  [super touchesBegan:touches withEvent:event];
  self.registeredTouches += touches.count;
  [self.events addObject:@"began"];
}
- (void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  [super touchesMoved:touches withEvent:event];
  if (![self.events.lastObject isEqualToString:@"moved"]) [self.events addObject:@"moved"];
}
- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  [super touchesEnded:touches withEvent:event];
  self.registeredTouches -= touches.count;
  [self.events addObject:@"ended"];
}
- (void)touchesCancelled:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  [super touchesCancelled:touches withEvent:event];
  self.registeredTouches -= touches.count;
  [self.events addObject:@"cancelled"];
}
- (void)reset
{
  NSInteger pending = self.registeredTouches;
  [super reset];
  self.registeredTouches = 0;
  if (pending > 0) [self.events addObject:@"reset-cancelled"];
}
@end

@interface RNCCollapsiblePagerPressCancellationDeliveryTests : XCTestCase
@property (nonatomic, strong) UIWindow *window;
@property (nonatomic, strong) UIView *surface;
@property (nonatomic, strong) RNCRecordingSurfaceTouchHandler *handler;
@property (nonatomic, strong) RNCCollapsiblePagerViewComponentView *pager;
@property (nonatomic, strong) RNCSynthesizedTouchDriver *driver;
@property (nonatomic, strong) UIView *firstPage;
@property (nonatomic) NSUInteger longPressBegins;
@end

@implementation RNCCollapsiblePagerPressCancellationDeliveryTests

- (void)setUp
{
  [super setUp];
  self.window = [[UIWindow alloc] initWithFrame:CGRectMake(0, 0, 390, 800)];
  self.window.hidden = NO;
  UIView *host = [[UIView alloc] initWithFrame:self.window.bounds];
  self.surface = [[UIView alloc] initWithFrame:host.bounds];
  [self.window addSubview:host];
  [host addSubview:self.surface];
  self.handler = [RNCRecordingSurfaceTouchHandler new];
  self.handler.events = [NSMutableArray new];
  [self.handler attachToView:self.surface];

  self.pager = [RNCCollapsiblePagerViewComponentView new];
  auto props = std::make_shared<RNCCollapsiblePagerViewProps>();
  props->nativeSmoothHeaderScrollEnabled = true;
  props->headerHeight = 120;
  props->stickyHeaderHeight = 44;
  [self.pager updateProps:props oldProps:nullptr];
  self.pager.frame = self.surface.bounds;
  [self.surface addSubview:self.pager];
  NSArray<UIView<RCTComponentViewProtocol> *> *children = @[
    [RCTViewComponentView new],  // header
    [RCTViewComponentView new],  // sticky header
    [RCTViewComponentView new],  // page 0
    [RCTViewComponentView new],  // page 1
  ];
  children[0].frame = CGRectMake(0, 0, 390, 120);
  children[1].frame = CGRectMake(0, 0, 390, 44);
  [children enumerateObjectsUsingBlock:^(UIView<RCTComponentViewProtocol> *child,
                                         NSUInteger index,
                                         BOOL *stop) {
    [self.pager mountChildComponentView:child index:(NSInteger)index];
  }];
  self.firstPage = children[2];
  [self.window layoutIfNeeded];
  [self drainMainQueue];
  self.firstPage.frame = self.firstPage.superview.bounds;
  self.driver = [[RNCSynthesizedTouchDriver alloc] initWithWindow:self.window];
}

- (void)tearDown
{
  [self.pager removeFromSuperview];
  [self.handler detachFromView:self.surface];
  self.window.hidden = YES;
  self.driver = nil;
  self.firstPage = nil;
  self.pager = nil;
  self.handler = nil;
  self.surface = nil;
  self.window = nil;
  [super tearDown];
}

- (void)drainMainQueue
{
  XCTestExpectation *drained = [self expectationWithDescription:@"main queue drained"];
  dispatch_async(dispatch_get_main_queue(), ^{ [drained fulfill]; });
  [self waitForExpectations:@[drained] timeout:2];
}

- (void)assertFixtureIsReadyForDelivery
{
  XCTAssertNil(self.driver.unavailableReason);
  UIGestureRecognizer *bridge = [self.pager valueForKey:@"contentPressCancellationGesture"];
  UIScrollView *pagerScrollView = [self.pager valueForKey:@"pagerScrollView"];
  XCTAssertNotNil(bridge);
  XCTAssertEqual([bridge valueForKey:@"reactTouchHandler"], self.handler);
  XCTAssertEqual((id)pagerScrollView.delegate, self.pager);
  XCTAssertGreaterThan(pagerScrollView.contentSize.width, CGRectGetWidth(pagerScrollView.bounds));
  // The touch must start on React page content, outside the shared header.
  CGPoint point = [self pressPoint];
  UIView *hit = [self.window hitTest:point withEvent:nil];
  XCTAssertTrue(hit == self.firstPage || [hit isDescendantOfView:self.firstPage],
                @"hit view %@", hit);
}

- (CGPoint)pressPoint
{
  return CGPointMake(200, 600);
}

- (void)testHorizontalPagerDragCancelsReactPressBeforeFingerLifts
{
  [self assertFixtureIsReadyForDelivery];
  UIScrollView *pagerScrollView = [self.pager valueForKey:@"pagerScrollView"];
  CGPoint point = [self pressPoint];
  UITouch *touch = [self.driver beginAt:point];
  XCTAssertEqualObjects(self.handler.events, @[@"began"]);
  XCTAssertEqual(self.handler.registeredTouches, 1);

  for (NSInteger step = 1; step <= 8 && !pagerScrollView.isDragging; ++step) {
    [self.driver move:touch to:CGPointMake(point.x - 12 * step, point.y)];
  }
  XCTAssertTrue(pagerScrollView.isDragging, @"the real pager pan must begin");
  // Finger is still down: RN must already have cancelled its responder.
  XCTAssertEqual(self.handler.registeredTouches, 0);
  XCTAssertTrue([self.handler.events containsObject:@"reset-cancelled"] ||
                  [self.handler.events containsObject:@"cancelled"],
                @"handler events %@", self.handler.events);
  XCTAssertFalse([self.handler.events containsObject:@"ended"]);
  NSUInteger eventsBeforeLift = self.handler.events.count;

  [self.driver end:touch];
  // UIKit must not deliver the lift to the prevented handler as a press end.
  XCTAssertFalse([self.handler.events containsObject:@"ended"], @"events %@", self.handler.events);
  XCTAssertEqual(self.handler.events.count, eventsBeforeLift);
}

- (void)testStationaryTapKeepsReactPress
{
  [self assertFixtureIsReadyForDelivery];
  UITouch *touch = [self.driver beginAt:[self pressPoint]];
  [self.driver end:touch];
  XCTAssertEqualObjects(self.handler.events, (@[@"began", @"ended"]));
  XCTAssertEqual(self.handler.registeredTouches, 0);
}

- (void)testVerticalPageDragCancelsReactPressBeforeFingerLifts
{
  [self assertFixtureIsReadyForDelivery];
  CGPoint point = [self pressPoint];
  UITouch *touch = [self.driver beginAt:point];
  XCTAssertEqual(self.handler.registeredTouches, 1);

  [self.driver move:touch to:CGPointMake(point.x, point.y - 12)];
  XCTAssertEqual(self.handler.registeredTouches, 0);
  XCTAssertTrue([self.handler.events containsObject:@"reset-cancelled"] ||
                  [self.handler.events containsObject:@"cancelled"],
                @"handler events %@", self.handler.events);
  XCTAssertFalse([self.handler.events containsObject:@"ended"]);

  [self.driver end:touch];
  XCTAssertFalse([self.handler.events containsObject:@"ended"], @"events %@", self.handler.events);
}

- (void)testVerticalSiblingControlDragKeepsReactPress
{
  [self assertFixtureIsReadyForDelivery];
  UIView *sibling = [RCTViewComponentView new];
  sibling.frame = CGRectMake(160, 540, 80, 100);
  [self.surface addSubview:sibling];
  CGPoint point = [self pressPoint];
  UIScrollView *pagerScrollView = [self.pager valueForKey:@"pagerScrollView"];
  XCTAssertEqual(sibling.superview, self.pager.superview);
  XCTAssertFalse([sibling isDescendantOfView:pagerScrollView]);
  XCTAssertEqual([self.window hitTest:point withEvent:nil], sibling);

  UITouch *touch = [self.driver beginAt:point];
  XCTAssertEqual(self.handler.registeredTouches, 1);
  [self.driver move:touch to:CGPointMake(point.x, point.y - 12)];
  XCTAssertEqual(self.handler.registeredTouches, 1);
  XCTAssertFalse([self.handler.events containsObject:@"reset-cancelled"]);
  XCTAssertFalse([self.handler.events containsObject:@"cancelled"]);

  [self.driver end:touch];
  XCTAssertEqualObjects(self.handler.events, (@[@"began", @"moved", @"ended"]));
  XCTAssertEqual(self.handler.registeredTouches, 0);
}

- (void)testSmallVerticalMovementKeepsReactPress
{
  [self assertFixtureIsReadyForDelivery];
  CGPoint point = [self pressPoint];
  UITouch *touch = [self.driver beginAt:point];
  [self.driver move:touch to:CGPointMake(point.x, point.y - 8)];
  XCTAssertEqual(self.handler.registeredTouches, 1);
  [self.driver end:touch];
  XCTAssertEqualObjects(self.handler.events, (@[@"began", @"moved", @"ended"]));
}

- (void)testSmallMovementBelowPanThresholdKeepsReactPress
{
  [self assertFixtureIsReadyForDelivery];
  UIScrollView *pagerScrollView = [self.pager valueForKey:@"pagerScrollView"];
  CGPoint point = [self pressPoint];
  UITouch *touch = [self.driver beginAt:point];
  [self.driver move:touch to:CGPointMake(point.x - 2, point.y)];
  XCTAssertFalse(pagerScrollView.isDragging);
  [self.driver end:touch];
  XCTAssertEqualObjects(self.handler.events, (@[@"began", @"moved", @"ended"]));
}

- (UIScrollView *)mountNestedHorizontalScrollView
{
  UIScrollView *vertical = [[UIScrollView alloc] initWithFrame:self.firstPage.bounds];
  vertical.contentSize = CGSizeMake(CGRectGetWidth(vertical.bounds), 1800);
  [self.firstPage addSubview:vertical];
  UIScrollView *horizontal = [[UIScrollView alloc] initWithFrame:CGRectMake(40, 350, 300, 120)];
  horizontal.contentSize = CGSizeMake(900, 120);
  horizontal.contentOffset = CGPointMake(200, 0);
  [vertical addSubview:horizontal];
  UILongPressGestureRecognizer *longPress = [[UILongPressGestureRecognizer alloc]
    initWithTarget:self action:@selector(nestedLongPress:)];
  longPress.minimumPressDuration = 0.1;
  [horizontal addGestureRecognizer:longPress];
  // Re-run the production attachment path after the descendant mounts.
  ((void (*)(id, SEL))objc_msgSend)(self.pager, NSSelectorFromString(@"attachScrollObserverForCurrentPage"));
  [self.window layoutIfNeeded];
  [self drainMainQueue];
  XCTAssertEqual([self.pager valueForKey:@"observedScrollView"], vertical);
  return horizontal;
}

- (void)nestedLongPress:(UILongPressGestureRecognizer *)recognizer
{
  if (recognizer.state == UIGestureRecognizerStateBegan) self.longPressBegins += 1;
}

- (CGPoint)pressPointInNestedScrollView:(UIScrollView *)scroll
{
  CGPoint point = [scroll convertPoint:CGPointMake(scroll.contentOffset.x + 150, 60) toView:self.window];
  XCTAssertEqual([self.window hitTest:point withEvent:nil], scroll);
  return point;
}

- (void)testNestedStationaryNativeLongPressRecognizesBeforeFingerUp
{
  UIScrollView *scroll = [self mountNestedHorizontalScrollView];
  UITouch *touch = [self.driver beginAt:[self pressPointInNestedScrollView:scroll]];
  UIGestureRecognizer *guard = [self.pager valueForKey:@"verticalPagerGesture"];
  XCTAssertEqual(guard.state, UIGestureRecognizerStatePossible);
  XCTAssertEqual(scroll.panGestureRecognizer.minimumNumberOfTouches, 2);
  XCTestExpectation *hold = [self expectationWithDescription:@"stationary finger held"];
  dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.25 * NSEC_PER_SEC)),
                 dispatch_get_main_queue(), ^{ [hold fulfill]; });
  [self waitForExpectations:@[hold] timeout:2];
  // The real UIKit target action must arrive while the finger is still down.
  XCTAssertEqual(self.longPressBegins, 1);
  XCTAssertEqual(touch.phase, UITouchPhaseBegan);
  XCTAssertEqual(guard.state, UIGestureRecognizerStatePossible);
  [self.driver end:touch];
  XCTAssertEqual(scroll.panGestureRecognizer.minimumNumberOfTouches, 1);
}

- (void)testNestedHorizontalPanConsumesConfirmedHorizontalDrag
{
  UIScrollView *scroll = [self mountNestedHorizontalScrollView];
  CGPoint point = [self pressPointInNestedScrollView:scroll];
  CGFloat originalOffset = scroll.contentOffset.x;
  UITouch *touch = [self.driver beginAt:point];
  for (NSInteger step = 1; step <= 10; ++step) {
    [self.driver move:touch to:CGPointMake(point.x - 12 * step, point.y)];
  }
  XCTAssertTrue(scroll.isDragging);
  XCTAssertGreaterThan(scroll.contentOffset.x, originalOffset);
  UIScrollView *pager = [self.pager valueForKey:@"pagerScrollView"];
  XCTAssertFalse(pager.isDragging);
  XCTAssertEqual(self.longPressBegins, 0);
  [self.driver end:touch];
  XCTAssertEqual(scroll.panGestureRecognizer.minimumNumberOfTouches, 1);
}

- (void)testNestedPendingHorizontalStartCanBecomeVertical
{
  UIScrollView *scroll = [self mountNestedHorizontalScrollView];
  CGPoint point = [self pressPointInNestedScrollView:scroll];
  UITouch *touch = [self.driver beginAt:point];
  [self.driver move:touch to:CGPointMake(point.x - 8, point.y)];
  UIGestureRecognizer *guard = [self.pager valueForKey:@"verticalPagerGesture"];
  XCTAssertEqual(guard.state, UIGestureRecognizerStatePossible);
  for (NSInteger step = 1; step <= 8; ++step) {
    [self.driver move:touch to:CGPointMake(point.x - 8, point.y - 12 * step)];
  }
  UIScrollView *vertical = [self.pager valueForKey:@"observedScrollView"];
  UIScrollView *pager = [self.pager valueForKey:@"pagerScrollView"];
  XCTAssertTrue(vertical.isDragging);
  XCTAssertFalse(scroll.isDragging);
  XCTAssertFalse(pager.isDragging);
  XCTAssertEqual(self.longPressBegins, 0);
  [self.driver end:touch];
  XCTAssertEqual(scroll.panGestureRecognizer.minimumNumberOfTouches, 1);
  XCTAssertEqual(pager.panGestureRecognizer.minimumNumberOfTouches, 1);
}

- (void)testNestedHeldPanTouchCountRestoresOnSecondTouch
{
  UIScrollView *scroll = [self mountNestedHorizontalScrollView];
  CGPoint point = [self pressPointInNestedScrollView:scroll];
  UITouch *first = [self.driver beginAt:point];
  XCTAssertEqual(scroll.panGestureRecognizer.minimumNumberOfTouches, 2);
  UITouch *second = [self.driver beginAt:CGPointMake(point.x + 20, point.y)];
  UIScrollView *pager = [self.pager valueForKey:@"pagerScrollView"];
  XCTAssertEqual(scroll.panGestureRecognizer.minimumNumberOfTouches, 1);
  XCTAssertEqual(pager.panGestureRecognizer.minimumNumberOfTouches, 1);
  [self.driver end:second];
  [self.driver end:first];
  XCTAssertEqual(scroll.panGestureRecognizer.minimumNumberOfTouches, 1);
}

@end
