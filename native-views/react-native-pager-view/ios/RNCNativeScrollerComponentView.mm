#import "RNCNativeScrollerComponentView.h"

#import <React/RCTConversions.h>
#import <react/renderer/components/pagerview/RNCNativeScrollerComponentDescriptor.h>
#import <react/renderer/components/pagerview/EventEmitters.h>
#import <react/renderer/components/pagerview/Props.h>
#import <react/renderer/components/pagerview/RCTComponentViewHelpers.h>

using namespace facebook::react;

@interface RNCNativeVerticalScrollView : UIScrollView
@end
@implementation RNCNativeVerticalScrollView
- (BOOL)touchesShouldCancelInContentView:(UIView *)view { return YES; }
@end

template <typename Event>
static Event RNCScrollerMetrics(UIScrollView *scroll, CGPoint velocity, CGPoint target)
{
  UIEdgeInsets inset = scroll.contentInset;
  return {
    .contentInset = {.top = inset.top, .right = inset.right, .bottom = inset.bottom, .left = inset.left},
    .contentOffset = {.x = scroll.contentOffset.x, .y = scroll.contentOffset.y},
    .contentSize = {.width = scroll.contentSize.width, .height = scroll.contentSize.height},
    .layoutMeasurement = {.width = scroll.bounds.size.width, .height = scroll.bounds.size.height},
    .velocity = {.x = velocity.x, .y = velocity.y},
    .targetContentOffset = {.x = target.x, .y = target.y},
    .zoomScale = 1,
  };
}

@interface RNCNativeScrollerComponentView () <UIScrollViewDelegate, RCTRNCNativeScrollerViewProtocol>
@end

@implementation RNCNativeScrollerComponentView {
  RNCNativeScrollerShadowNode::ConcreteState::Shared _state;
  RNCNativeVerticalScrollView *_scrollView;
  UIView<RCTComponentViewProtocol> *_reactContent;
  UIRefreshControl *_refreshControl;
  UIEdgeInsets _callerInset;
  BOOL _dragActive;
  BOOL _momentumActive;
  BOOL _refreshEventSentForDrag;
  BOOL _needsContentClamp;
  BOOL _recycled;
  BOOL _stopping;
  NSUInteger _generation;
  CFTimeInterval _lastScrollEvent;
  NSTimeInterval _scrollEventThrottle;
  NSMapTable<id, NSNumber *> *_pagerStickyHeights;
  NSNumber *_lastContentViewportHeight;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<RNCNativeScrollerComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if ((self = [super initWithFrame:frame])) {
    static const auto defaultProps = std::make_shared<const RNCNativeScrollerProps>();
    _props = defaultProps;
    _pagerStickyHeights = [NSMapTable weakToStrongObjectsMapTable];
    _scrollView = [[RNCNativeVerticalScrollView alloc] initWithFrame:self.bounds];
    _scrollView.delegate = self;
    _scrollView.alwaysBounceVertical = YES;
    _scrollView.alwaysBounceHorizontal = NO;
    _scrollView.showsHorizontalScrollIndicator = NO;
    _scrollView.directionalLockEnabled = YES;
    _scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    self.contentView = _scrollView;
  }
  return self;
}

- (UIScrollView *)scrollView { return _scrollView; }

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)child index:(NSInteger)index
{
  // The wrapper supplies one Yoga-owned content view. Pager-owned shared headers
  // are separate UIScrollView children and must never be removed or repositioned.
  if (_reactContent != nil && _reactContent != child) {
    [self stopScrolling];
    [_reactContent removeFromSuperview];
  }
  _reactContent = child;
  [_scrollView insertSubview:child atIndex:0];
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)child index:(NSInteger)index
{
  if (child != _reactContent) return;
  [self stopScrolling];
  [child removeFromSuperview];
  _reactContent = nil;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  const auto &next = *std::static_pointer_cast<const RNCNativeScrollerProps>(props);
  const auto &previous = *std::static_pointer_cast<const RNCNativeScrollerProps>(_props);
  BOOL wasRecycled = _recycled;
  _recycled = NO;
  _scrollView.delegate = self;
  if (!next.scrollEnabled && _scrollView.scrollEnabled) [self stopScrolling];
  _scrollView.scrollEnabled = next.scrollEnabled;
  _scrollView.showsVerticalScrollIndicator = next.showsVerticalScrollIndicator;
  _scrollView.showsHorizontalScrollIndicator = next.showsHorizontalScrollIndicator;
  _scrollView.bounces = next.bounces;
  if (wasRecycled || previous.alwaysBounceVertical != next.alwaysBounceVertical) {
    _scrollView.alwaysBounceVertical = next.alwaysBounceVertical;
  }
  _scrollView.decelerationRate = isfinite(next.decelerationRate)
    ? MIN(1, MAX(0, next.decelerationRate)) : UIScrollViewDecelerationRateNormal;
  switch (next.keyboardDismissMode) {
    case RNCNativeScrollerKeyboardDismissMode::OnDrag:
      _scrollView.keyboardDismissMode = UIScrollViewKeyboardDismissModeOnDrag; break;
    case RNCNativeScrollerKeyboardDismissMode::Interactive:
      _scrollView.keyboardDismissMode = UIScrollViewKeyboardDismissModeInteractive; break;
    default: _scrollView.keyboardDismissMode = UIScrollViewKeyboardDismissModeNone; break;
  }
  _scrollEventThrottle = isfinite(next.scrollEventThrottle)
    ? MAX(0, next.scrollEventThrottle) / 1000.0 : 0;
  if (_scrollEventThrottle <= 0.016) _scrollEventThrottle = 0;

  UIEdgeInsets inset = UIEdgeInsetsMake(
    isfinite(next.contentInsetTop) ? next.contentInsetTop : 0,
    isfinite(next.contentInsetLeft) ? next.contentInsetLeft : 0,
    isfinite(next.contentInsetBottom) ? next.contentInsetBottom : 0,
    isfinite(next.contentInsetRight) ? next.contentInsetRight : 0);
  if (!UIEdgeInsetsEqualToEdgeInsets(inset, _callerInset)) {
    UIEdgeInsets oldInset = _callerInset;
    UIEdgeInsets current = _scrollView.contentInset;
    _callerInset = inset;
    _scrollView.contentInset = UIEdgeInsetsMake(
      current.top + inset.top - oldInset.top, current.left + inset.left - oldInset.left,
      current.bottom + inset.bottom - oldInset.bottom, current.right + inset.right - oldInset.right);
    for (UIView *owner = self.superview; owner != nil; owner = owner.superview) {
      if ([owner conformsToProtocol:@protocol(RNCNativeScrollerInsetCoordinator)]) {
        [(id<RNCNativeScrollerInsetCoordinator>)owner nativeScroller:self
          callerInsetChangedFrom:oldInset to:inset];
      }
    }
  }

  BOOL createdRefreshControl = next.refreshEnabled && _refreshControl == nil;
  if (createdRefreshControl) {
    _refreshControl = [UIRefreshControl new];
    [_refreshControl addTarget:self action:@selector(refreshTriggered:)
             forControlEvents:UIControlEventValueChanged];
    _scrollView.refreshControl = _refreshControl;
  } else if (!next.refreshEnabled && _refreshControl != nil) {
    [_refreshControl endRefreshing];
    _scrollView.refreshControl = nil;
    _refreshControl = nil;
  }
  if (_refreshControl != nil) {
    _refreshControl.tintColor = next.refreshTintColor ? RCTUIColorFromSharedColor(next.refreshTintColor) : nil;
    NSString *title = RCTNSStringFromString(next.refreshTitle);
    UIColor *titleColor = next.refreshTitleColor ? RCTUIColorFromSharedColor(next.refreshTitleColor) : nil;
    _refreshControl.attributedTitle = title.length == 0 ? nil : [[NSAttributedString alloc]
      initWithString:title attributes:titleColor == nil ? @{} : @{NSForegroundColorAttributeName: titleColor}];
    if (previous.refreshProgressViewOffset != next.refreshProgressViewOffset || createdRefreshControl) {
      CGRect bounds = _refreshControl.bounds;
      bounds.origin.y = isfinite(next.refreshProgressViewOffset) ? -next.refreshProgressViewOffset : 0;
      _refreshControl.bounds = bounds;
    }
    if (next.refreshing && !_refreshControl.refreshing) {
      [_refreshControl beginRefreshing];
    } else if (!next.refreshing && (previous.refreshing || createdRefreshControl)) {
      [_refreshControl endRefreshing];
    }
  }

  CGSize size = CGSizeMake(isfinite(next.contentWidth) ? MAX(0, next.contentWidth) : 0,
                           isfinite(next.contentHeight) ? MAX(0, next.contentHeight) : 0);
  if (!CGSizeEqualToSize(size, _scrollView.contentSize)) {
    _needsContentClamp |= size.height < _scrollView.contentSize.height;
    CGPoint offset = _scrollView.contentOffset;
    BOOL wasPulling = offset.y < -_scrollView.adjustedContentInset.top;
    _scrollView.contentSize = size;
    // UIKit can synchronously discard a pull when content becomes empty.
    // Upper-range shrink correction remains owned by the deferred clamp.
    if (wasPulling && _scrollView.contentOffset.y != offset.y) {
      [_scrollView setContentOffset:CGPointMake(_scrollView.contentOffset.x, offset.y) animated:NO];
    }
    [self scheduleContentClamp];
  }
  BOOL refreshEnded = previous.refreshing && !next.refreshing;
  [super updateProps:props oldProps:oldProps];
  if (refreshEnded) [self scheduleContentClamp];
  [self updateFabricContentOrigin];
  [self emitContentViewportChangeIfNeeded];
}

- (void)updateLayoutMetrics:(const LayoutMetrics &)layoutMetrics
           oldLayoutMetrics:(const LayoutMetrics &)oldLayoutMetrics
{
  CGSize previous = _scrollView.bounds.size;
  [super updateLayoutMetrics:layoutMetrics oldLayoutMetrics:oldLayoutMetrics];
  [self updateFabricContentOrigin];
  [self emitContentViewportChangeIfNeeded];
  if (!CGSizeEqualToSize(previous, _scrollView.bounds.size)) {
    _needsContentClamp = YES;
    [self scheduleContentClamp];
  }
}

- (CGFloat)contentViewportHeight
{
  CGFloat stickyHeight = 0;
  for (NSNumber *height in _pagerStickyHeights.objectEnumerator) stickyHeight += height.doubleValue;
  return MAX(0, _scrollView.bounds.size.height - _callerInset.top - _callerInset.bottom - stickyHeight);
}

- (void)setPagerStickyHeight:(CGFloat)height forOwner:(id)owner
{
  if (owner == nil) return;
  CGFloat next = isfinite(height) ? MAX(0, height) : 0;
  if (next == 0) [_pagerStickyHeights removeObjectForKey:owner];
  else [_pagerStickyHeights setObject:@(next) forKey:owner];
  [self emitContentViewportChangeIfNeeded];
}

- (void)emitContentViewportChangeIfNeeded
{
  if (_recycled || !_eventEmitter) return;
  CGFloat height = [self contentViewportHeight];
  if (_lastContentViewportHeight != nil && _lastContentViewportHeight.doubleValue == height) return;
  _lastContentViewportHeight = @(height);
  std::static_pointer_cast<const RNCNativeScrollerEventEmitter>(_eventEmitter)
    ->onContentViewportChange({.height = height});
}

- (void)updateEventEmitter:(const EventEmitter::Shared &)eventEmitter
{
  if (_eventEmitter != eventEmitter) _lastContentViewportHeight = nil;
  [super updateEventEmitter:eventEmitter];
  [self emitContentViewportChangeIfNeeded];
}

- (void)updateState:(const State::Shared &)state oldState:(const State::Shared &)oldState
{
  _state = std::static_pointer_cast<const RNCNativeScrollerShadowNode::ConcreteState>(state);
  [self updateFabricContentOrigin];
}

- (void)updateFabricContentOrigin
{
  if (!_state || _recycled) return;
  facebook::react::Point origin = {-static_cast<Float>(_scrollView.contentOffset.x),
                  -static_cast<Float>(_scrollView.contentOffset.y)};
  _state->updateState([origin](const RNCNativeScrollerShadowNode::ConcreteState::Data &oldData)
      -> RNCNativeScrollerShadowNode::ConcreteState::SharedData {
    if (oldData.contentOrigin == origin) return nullptr;
    auto next = oldData;
    next.contentOrigin = origin;
    return std::make_shared<const RNCNativeScrollerShadowNode::ConcreteState::Data>(next);
  });
}

- (void)scheduleContentClamp
{
  if (!_needsContentClamp) return;
  NSUInteger generation = _generation;
  __weak __typeof__(self) weakSelf = self;
  dispatch_async(dispatch_get_main_queue(), ^{
    __strong __typeof__(self) self = weakSelf;
    if (self == nil || self->_generation != generation || self->_recycled) return;
    [self clampContentOffsetIfNeeded];
  });
}

- (void)clampContentOffsetIfNeeded
{
  if (!_needsContentClamp || _scrollView.dragging || _scrollView.decelerating ||
      _refreshControl.refreshing || _scrollView.bounds.size.height <= 0) return;
  _needsContentClamp = NO;
  CGFloat maximum = MAX(-_scrollView.adjustedContentInset.top,
    _scrollView.contentSize.height - _scrollView.bounds.size.height + _scrollView.adjustedContentInset.bottom);
  if (_scrollView.contentOffset.y > maximum) {
    [_scrollView setContentOffset:CGPointMake(_scrollView.contentOffset.x, maximum) animated:NO];
  }
}

- (void)refreshTriggered:(UIRefreshControl *)control
{
  if (_recycled || _refreshEventSentForDrag || control != _refreshControl) return;
  const auto &props = *std::static_pointer_cast<const RNCNativeScrollerProps>(_props);
  if (!props.refreshEnabled || props.refreshing) return;
  _refreshEventSentForDrag = YES;
  if (_eventEmitter) {
    std::static_pointer_cast<const RNCNativeScrollerEventEmitter>(_eventEmitter)->onRefresh({});
  }
}

// Every delegate path and imperative interruption uses the same terminal owner.
- (void)emitScrollEvent:(NSInteger)kind velocity:(CGPoint)velocity target:(CGPoint)target
{
  if (_recycled || !_eventEmitter) return;
  const auto emitter = std::static_pointer_cast<const RNCNativeScrollerEventEmitter>(_eventEmitter);
  switch (kind) {
    case 0: emitter->onScroll(RNCScrollerMetrics<RNCNativeScrollerEventEmitter::OnScroll>(_scrollView, velocity, target)); break;
    case 1: emitter->onScrollBeginDrag(RNCScrollerMetrics<RNCNativeScrollerEventEmitter::OnScrollBeginDrag>(_scrollView, velocity, target)); break;
    case 2: emitter->onScrollEndDrag(RNCScrollerMetrics<RNCNativeScrollerEventEmitter::OnScrollEndDrag>(_scrollView, velocity, target)); break;
    case 3: emitter->onMomentumScrollBegin(RNCScrollerMetrics<RNCNativeScrollerEventEmitter::OnMomentumScrollBegin>(_scrollView, velocity, target)); break;
    case 4: emitter->onMomentumScrollEnd(RNCScrollerMetrics<RNCNativeScrollerEventEmitter::OnMomentumScrollEnd>(_scrollView, velocity, target)); break;
  }
}

- (void)finishMomentum
{
  if (!_momentumActive) return;
  _momentumActive = NO;
  [self emitScrollEvent:4 velocity:CGPointZero target:_scrollView.contentOffset];
}

- (void)stopScrolling
{
  if (_stopping) return;
  _stopping = YES;
  [_scrollView setContentOffset:_scrollView.contentOffset animated:NO];
  if (_dragActive) {
    _dragActive = NO;
    [self emitScrollEvent:2 velocity:CGPointZero target:_scrollView.contentOffset];
    BOOL enabled = _scrollView.panGestureRecognizer.enabled;
    _scrollView.panGestureRecognizer.enabled = NO;
    _scrollView.panGestureRecognizer.enabled = enabled;
  }
  [self finishMomentum];
  _stopping = NO;
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
  if (_recycled) return;
  [self finishMomentum];
  _dragActive = YES;
  _refreshEventSentForDrag = NO;
  _lastScrollEvent = 0;
  [self emitScrollEvent:1 velocity:CGPointZero target:scrollView.contentOffset];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
  if (_recycled) return;
  [self updateFabricContentOrigin];
  CFTimeInterval now = CACurrentMediaTime();
  if (_lastScrollEvent != 0 && now - _lastScrollEvent < _scrollEventThrottle) return;
  _lastScrollEvent = now;
  [self emitScrollEvent:0 velocity:CGPointZero target:scrollView.contentOffset];
}

- (void)scrollViewWillEndDragging:(UIScrollView *)scrollView
                    withVelocity:(CGPoint)velocity targetContentOffset:(inout CGPoint *)target
{
  if (_recycled || !_dragActive) return;
  _dragActive = NO;
  [self emitScrollEvent:2 velocity:velocity target:*target];
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
  if (_recycled) return;
  if (_dragActive) {
    _dragActive = NO;
    [self emitScrollEvent:2 velocity:CGPointZero target:scrollView.contentOffset];
  }
  if (!decelerate) [self scheduleContentClamp];
}

- (void)scrollViewWillBeginDecelerating:(UIScrollView *)scrollView
{
  if (_recycled || _stopping || _momentumActive) return;
  _momentumActive = YES;
  [self emitScrollEvent:3 velocity:CGPointZero target:scrollView.contentOffset];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
  if (_recycled) return;
  [self finishMomentum];
  _lastScrollEvent = 0;
  [self scrollViewDidScroll:scrollView];
  [self scheduleContentClamp];
}

- (void)scrollViewDidEndScrollingAnimation:(UIScrollView *)scrollView
{
  [self scrollViewDidEndDecelerating:scrollView];
}

- (void)scrollTo:(double)x y:(double)y animated:(BOOL)animated
{
  if (_recycled || !isfinite(x) || !isfinite(y)) return;
  [self stopScrolling];
  UIEdgeInsets inset = _scrollView.adjustedContentInset;
  CGPoint target = CGPointMake(MAX(-inset.left, MIN(x,
    MAX(-inset.left, _scrollView.contentSize.width - _scrollView.bounds.size.width + inset.right))),
    MAX(-inset.top, MIN(y,
    MAX(-inset.top, _scrollView.contentSize.height - _scrollView.bounds.size.height + inset.bottom))));
  if (CGPointEqualToPoint(target, _scrollView.contentOffset)) return;
  if (animated) {
    _momentumActive = YES;
    [self emitScrollEvent:3 velocity:CGPointZero target:target];
  }
  [_scrollView setContentOffset:target animated:animated];
}

- (void)scrollToEnd:(BOOL)animated
{
  [self scrollTo:_scrollView.contentOffset.x
               y:_scrollView.contentSize.height - _scrollView.bounds.size.height + _scrollView.adjustedContentInset.bottom
        animated:animated];
}

- (void)setRefreshState:(BOOL)refreshing
{
  [self setRefreshing:refreshing];
}

- (void)setRefreshing:(BOOL)refreshing
{
  if (_recycled || _refreshControl == nil) return;
  if (refreshing) {
    if (!_refreshControl.refreshing) [_refreshControl beginRefreshing];
  } else {
    [_refreshControl endRefreshing];
    [self scheduleContentClamp];
  }
}

- (void)flashScrollIndicators { [_scrollView flashScrollIndicators]; }

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args
{
  RCTRNCNativeScrollerHandleCommand(self, commandName, args);
}

- (void)didMoveToWindow
{
  [super didMoveToWindow];
  if (self.window == nil) {
    [self stopScrolling];
    [_pagerStickyHeights removeAllObjects];
    [self emitContentViewportChangeIfNeeded];
  } else {
    const auto &props = *std::static_pointer_cast<const RNCNativeScrollerProps>(_props);
    if (props.refreshing) [_refreshControl beginRefreshing];
    [self scheduleContentClamp];
  }
}

- (void)prepareForRecycle
{
  [self stopScrolling];
  _recycled = YES;
  _scrollView.delegate = nil;
  ++_generation;
  _state.reset();
  [_pagerStickyHeights removeAllObjects];
  _lastContentViewportHeight = nil;
  [_refreshControl endRefreshing];
  _scrollView.refreshControl = nil;
  _refreshControl = nil;
  [_reactContent removeFromSuperview];
  _reactContent = nil;
  _scrollView.contentInset = UIEdgeInsetsZero;
  _scrollView.contentSize = CGSizeZero;
  _scrollView.contentOffset = CGPointZero;
  _callerInset = UIEdgeInsetsZero;
  _needsContentClamp = NO;
  _refreshEventSentForDrag = NO;
  _lastScrollEvent = 0;
  [super prepareForRecycle];
}

@end
