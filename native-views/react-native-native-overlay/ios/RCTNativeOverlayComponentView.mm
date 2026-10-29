#import "RCTNativeOverlayComponentView.h"

#include <cmath>

#import <react/renderer/components/RNCNativeOverlay/NativeOverlayComponentDescriptor.h>
#import <react/renderer/components/RNCNativeOverlay/EventEmitters.h>
#import <react/renderer/components/RNCNativeOverlay/Props.h>
#import <react/renderer/components/RNCNativeOverlay/RCTComponentViewHelpers.h>

#import <React/RCTComponent.h>
#import <React/RCTConversions.h>
#import <React/RCTFabricComponentsPlugins.h>
#import <React/RCTSurfaceTouchHandler.h>

#if __has_include(<NativeOverlay/NativeOverlay-Swift.h>)
#import <NativeOverlay/NativeOverlay-Swift.h>
#elif __has_include("NativeOverlay/NativeOverlay-Swift.h")
#import "NativeOverlay/NativeOverlay-Swift.h"
#else
#import "NativeOverlay-Swift.h"
#endif

using namespace facebook::react;

static NSString *RNCNativeOverlayLevelString(RNCNativeOverlayLevel level)
{
  switch (level) {
    case RNCNativeOverlayLevel::Hardware:
      return @"hardware";
    case RNCNativeOverlayLevel::Secure:
      return @"secure";
    case RNCNativeOverlayLevel::Toast:
      return @"toast";
    case RNCNativeOverlayLevel::Lock:
      return @"lock";
    case RNCNativeOverlayLevel::Debug:
      return @"debug";
    case RNCNativeOverlayLevel::Modal:
    default:
      return @"modal";
  }
}

static NSString *RNCNativeOverlayPresentationString(RNCNativeOverlayPresentation presentation)
{
  switch (presentation) {
    case RNCNativeOverlayPresentation::Toast:
      return @"toast";
    case RNCNativeOverlayPresentation::Fullscreen:
      return @"fullscreen";
    case RNCNativeOverlayPresentation::Sheet:
      return @"sheet";
    case RNCNativeOverlayPresentation::Anchored:
      return @"anchored";
    case RNCNativeOverlayPresentation::Center:
    default:
      return @"center";
  }
}

@implementation RCTNativeOverlayComponentView {
  NativeOverlayContainerView *_containerView;
  RCTSurfaceTouchHandler *_touchHandler;
  RNCNativeOverlayShadowNode::ConcreteState::Shared _state;
  CGPoint _contentOffset;
  int32_t _hostOriginRequest;
  int32_t _reportedHostOriginRequest;
  BOOL _hostOriginReportScheduled;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<RNCNativeOverlayComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const RNCNativeOverlayProps>();
    _props = defaultProps;

    _containerView = [NativeOverlayContainerView new];
    _touchHandler = [RCTSurfaceTouchHandler new];
    _containerView.touchHandler = _touchHandler;
    self.contentView = _containerView;

    __weak auto weakSelf = self;
    _containerView.onContentOffsetChanged = ^(CGPoint offset) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      strongSelf->_contentOffset = offset;
      [strongSelf pushContentOffset];
    };
    _containerView.onPresented = ^(NSDictionary *body) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const RNCNativeOverlayEventEmitter>(strongSelf->_eventEmitter);
      if (!emitter) return;
      NSNumber *stackOrder = body[@"stackOrder"] ?: @0;
      emitter->onPresented({.stackOrder = stackOrder.intValue});
    };
    _containerView.onDismissed = ^(NSDictionary *body) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const RNCNativeOverlayEventEmitter>(strongSelf->_eventEmitter);
      if (!emitter) return;
      NSString *reason = body[@"reason"] ?: @"system";
      emitter->onDismissed({.reason = std::string(reason.UTF8String)});
    };
    _containerView.onSheetLayout = ^(CGFloat width) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const RNCNativeOverlayEventEmitter>(strongSelf->_eventEmitter);
      if (!emitter) return;
      emitter->onSheetLayout({.width = width});
    };
    _containerView.onRequestDismiss = ^(NSDictionary *body) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const RNCNativeOverlayEventEmitter>(strongSelf->_eventEmitter);
      if (!emitter) return;
      NSString *reason = body[@"reason"] ?: @"back";
      emitter->onRequestDismiss({.reason = std::string(reason.UTF8String)});
    };
  }
  return self;
}

+ (BOOL)shouldBeRecycled
{
  return NO;
}

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView
                          index:(NSInteger)index
{
  [_containerView insertChild:childComponentView atIndex:index];
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView
                            index:(NSInteger)index
{
  [_containerView removeChild:childComponentView];
  [childComponentView removeFromSuperview];
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  const auto &newProps = *std::static_pointer_cast<RNCNativeOverlayProps const>(props);

  _containerView.level = RNCNativeOverlayLevelString(newProps.level);
  _containerView.presentation = RNCNativeOverlayPresentationString(newProps.presentation);
  _containerView.scope = newProps.scope == RNCNativeOverlayScope::Page ? @"page" : @"global";
  _containerView.hostKey = [NSString stringWithUTF8String:newProps.hostKey.c_str()];
  _containerView.ownerKey = [NSString stringWithUTF8String:newProps.ownerKey.c_str()];
  _containerView.stackOrder = newProps.stackOrder;
  _containerView.blocking = newProps.blocking;
  _containerView.dismissOnBackPress = newProps.dismissOnBackPress;
  _containerView.dismissOnBackdropPress = newProps.dismissOnBackdropPress;
  _containerView.backdropColor = RCTUIColorFromSharedColor(newProps.backdropColor);
  _containerView.sheetHeight = newProps.sheetHeight;
  _containerView.sheetCornerRadius = newProps.sheetCornerRadius;
  _containerView.showHandle = newProps.showHandle;
  _containerView.sheetBackgroundColor = RCTUIColorFromSharedColor(newProps.sheetBackgroundColor);
  _containerView.dismissOnPanDown = newProps.dismissOnPanDown;
  _containerView.animationConfig = [NSString stringWithUTF8String:newProps.animationConfig.c_str()];
  _containerView.visible = newProps.visible;
  [_containerView commitConfiguration];

  _hostOriginRequest = newProps.hostOriginRequest;
  if (_hostOriginRequest == 0) _reportedHostOriginRequest = 0;
  [self scheduleHostWindowOrigin];

  [super updateProps:props oldProps:oldProps];
}

- (void)didMoveToWindow
{
  [super didMoveToWindow];
  [self scheduleHostWindowOrigin];
}

- (void)updateEventEmitter:(EventEmitter::Shared const &)eventEmitter
{
  [super updateEventEmitter:eventEmitter];
  [self scheduleHostWindowOrigin];
}

- (void)scheduleHostWindowOrigin
{
  if (_hostOriginRequest == 0 || _hostOriginRequest == _reportedHostOriginRequest || _hostOriginReportScheduled) return;
  _hostOriginReportScheduled = YES;
  __weak auto weakSelf = self;
  dispatch_async(dispatch_get_main_queue(), ^{
    auto strongSelf = weakSelf;
    if (!strongSelf) return;
    strongSelf->_hostOriginReportScheduled = NO;
    const auto request = strongSelf->_hostOriginRequest;
    // Mounting can deliver props before the window or event emitter. Keep
    // the request pending until both are ready; attachment retries it.
    if (request == 0 || request == strongSelf->_reportedHostOriginRequest || !strongSelf.window) return;
    auto emitter = std::static_pointer_cast<const RNCNativeOverlayEventEmitter>(strongSelf->_eventEmitter);
    if (!emitter) return;
    CGPoint origin = [strongSelf convertPoint:CGPointZero toView:nil];
    origin.x += strongSelf.window.frame.origin.x;
    origin.y += strongSelf.window.frame.origin.y;
    strongSelf->_reportedHostOriginRequest = request;
    emitter->onHostWindowOrigin({.x = origin.x, .y = origin.y});
  });
}

- (void)updateState:(State::Shared const &)state oldState:(State::Shared const &)oldState
{
  _state = std::static_pointer_cast<RNCNativeOverlayShadowNode::ConcreteState const>(state);
  [self pushContentOffset];
}

// Keeps the shadow node's measurement origin at the content's window origin.
- (void)pushContentOffset
{
  if (!_state) {
    return;
  }
  const auto current = _state->getData().contentOffset;
  if (std::abs(current.x - _contentOffset.x) < 0.5 && std::abs(current.y - _contentOffset.y) < 0.5) {
    return;
  }
  _state->updateState(NativeOverlayState{facebook::react::Point{.x = _contentOffset.x, .y = _contentOffset.y}});
}

- (void)invalidate
{
  _hostOriginRequest = 0;
  _reportedHostOriginRequest = 0;
  [_containerView invalidate];
  [super invalidate];
}

@end

Class<RCTComponentViewProtocol> RNCNativeOverlayCls(void)
{
  return RCTNativeOverlayComponentView.class;
}
