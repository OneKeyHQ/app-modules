#import "RNCCollapsiblePagerViewComponentView.h"

#import <react/renderer/components/pagerview/ComponentDescriptors.h>
#import <react/renderer/components/pagerview/EventEmitters.h>
#import <react/renderer/components/pagerview/Props.h>
#import <react/renderer/components/pagerview/RCTComponentViewHelpers.h>

#import "React/RCTConversions.h"

using namespace facebook::react;

static void *RNCCollapsiblePagerContentOffsetContext = &RNCCollapsiblePagerContentOffsetContext;

@interface RNCCollapsiblePagerViewComponentView () <
  RCTRNCCollapsiblePagerViewViewProtocol,
  UIPageViewControllerDataSource,
  UIPageViewControllerDelegate,
  UIScrollViewDelegate,
  UIGestureRecognizerDelegate
>
- (void)finishPagerScrollEmittingSelection:(BOOL)emitSelection;
@end

@implementation RNCCollapsiblePagerViewComponentView {
  UIView *_containerView;
  UIPageViewController *_pageViewController;
  UIScrollView *_pagerScrollView;
  NSMutableArray<UIView<RCTComponentViewProtocol> *> *_logicalChildren;
  NSMutableArray<UIViewController *> *_pageControllers;
  UIView *_headerView;
  UIView *_stickyHeaderView;
  NSInteger _currentIndex;
  NSInteger _destinationIndex;
  NSInteger _pendingInitialPage;
  NSInteger _headerHeight;
  NSInteger _stickyHeaderHeight;
  CGFloat _headerOffset;
  BOOL _scrollEnabled;
  // OneKey patch: Coordinate this inner pager with an outer horizontal pager.
  BOOL _nestedScrollEnabled;
  UIPanGestureRecognizer *_blockerGesture;
  BOOL _transitioning;
  BOOL _isPagerDragging;
  BOOL _hasReceivedPageCommand;
  NSUInteger _transitionId;
  // OneKey patch: Retain the latest tab tap while an animation is in flight.
  NSInteger _pendingGoToIndex;
  BOOL _hasAppliedInitialPage;
  BOOL _needsPropsReapply;
  NSString *_layoutDirection;
  NSArray<NSString *> *_pageKeys;
  NSString *_retainedPages;
  NSMutableDictionary<NSString *, NSNumber *> *_pageOffsets;
  NSMapTable<UIScrollView *, NSValue *> *_originalInsets;
  NSMapTable<UIScrollView *, NSNumber *> *_appliedTopInsets;
  NSMapTable<UIScrollView *, NSValue *> *_originalIndicatorInsets;
  NSMapTable<UIScrollView *, NSNumber *> *_originalAlwaysBounceVertical;
  NSMapTable<UIScrollView *, NSNumber *> *_originalInsetAdjustmentBehavior;
  __weak UIScrollView *_observedScrollView;
  BOOL _observingContentOffset;
  BOOL _isBeingRecycled;
  NSUInteger _generation;
}

+ (void)load
{
  [super load];
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const RNCCollapsiblePagerViewProps>();
    _props = defaultProps;
    _logicalChildren = [NSMutableArray new];
    _pageControllers = [NSMutableArray new];
    _pageOffsets = [NSMutableDictionary new];
    _originalInsets = [NSMapTable weakToStrongObjectsMapTable];
    _appliedTopInsets = [NSMapTable weakToStrongObjectsMapTable];
    _originalIndicatorInsets = [NSMapTable weakToStrongObjectsMapTable];
    _originalAlwaysBounceVertical = [NSMapTable weakToStrongObjectsMapTable];
    _originalInsetAdjustmentBehavior = [NSMapTable weakToStrongObjectsMapTable];
    _pageKeys = @[];
    _retainedPages = @"[]";
    _currentIndex = 0;
    _destinationIndex = 0;
    _pendingInitialPage = 0;
    _scrollEnabled = YES;
    _nestedScrollEnabled = NO;
    _pendingGoToIndex = -1;
    _hasAppliedInitialPage = NO;
    _needsPropsReapply = YES;
    _layoutDirection = @"ltr";

    _containerView = [UIView new];
    _containerView.clipsToBounds = YES;
    self.contentView = _containerView;

    [self initializePageViewController];
  }
  return self;
}

- (void)willMoveToSuperview:(UIView *)newSuperview
{
  [super willMoveToSuperview:newSuperview];
  if (newSuperview != nil && _pageViewController == nil) {
    self.contentView = _containerView;
    [self initializePageViewController];
  }
}

- (void)didMoveToWindow
{
  [super didMoveToWindow];
  // OneKey patch: removing a decelerating page from its window can suppress
  // UIKit's final scroll callback. Restore the last acknowledged page on reattach.
  if (self.window != nil && _isPagerDragging &&
      !_pagerScrollView.dragging && !_pagerScrollView.decelerating) {
    [self finishPagerScrollEmittingSelection:NO];
  }
}

- (void)initializePageViewController
{
  _pageViewController = [[UIPageViewController alloc]
    initWithTransitionStyle:UIPageViewControllerTransitionStyleScroll
    navigationOrientation:UIPageViewControllerNavigationOrientationHorizontal
    options:nil];
  _pageViewController.dataSource = self;
  _pageViewController.delegate = self;
  // Fabric may mount header slots before a recycled pager reattaches.
  // Keep the recreated page container below those existing headers.
  [_containerView insertSubview:_pageViewController.view atIndex:0];

  for (UIView *subview in _pageViewController.view.subviews) {
    if ([subview isKindOfClass:UIScrollView.class]) {
      _pagerScrollView = (UIScrollView *)subview;
      _pagerScrollView.delegate = self;
      _pagerScrollView.delaysContentTouches = NO;
      _pagerScrollView.scrollEnabled = _scrollEnabled;
      break;
    }
  }
  [self applyNestedScrollBlocker];
}

#pragma mark - React mounting

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView
                          index:(NSInteger)index
{
  if (_isBeingRecycled) return;
  NSInteger safeIndex = MIN(MAX(index, 0), _logicalChildren.count);
  [_logicalChildren insertObject:childComponentView atIndex:safeIndex];
  [self rebuildSlots];
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView
                            index:(NSInteger)index
{
  if (_isBeingRecycled) return;
  NSUInteger existingIndex = [_logicalChildren indexOfObjectIdenticalTo:childComponentView];
  if (existingIndex == NSNotFound) return;
  [childComponentView removeFromSuperview];
  [_logicalChildren removeObjectAtIndex:existingIndex];
  [self rebuildSlots];
}

- (void)rebuildSlots
{
  [self detachScrollObserver];
  // OneKey patch: Fabric reuses these views without resetting transforms
  // written by a native parent. Release our collapse translation with the slot.
  _headerView.transform = CGAffineTransformIdentity;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  [_headerView removeFromSuperview];
  [_stickyHeaderView removeFromSuperview];
  _headerView = nil;
  _stickyHeaderView = nil;
  [_pageControllers removeAllObjects];

  if (_logicalChildren.count > 0) {
    _headerView = _logicalChildren[0];
    [_containerView addSubview:_headerView];
  }
  if (_logicalChildren.count > 1) {
    _stickyHeaderView = _logicalChildren[1];
    [_containerView addSubview:_stickyHeaderView];
  }
  for (NSInteger index = 2; index < _logicalChildren.count; index++) {
    UIView *page = _logicalChildren[index];
    UIViewController *controller = [UIViewController new];
    [controller.view addSubview:page];
    [_pageControllers addObject:controller];
  }

  if (_pageControllers.count > 0) {
    if (!_hasAppliedInitialPage &&
        _pendingInitialPage >= 0 &&
        _pendingInitialPage < _pageControllers.count) {
      _currentIndex = _pendingInitialPage;
      // A recycled host may receive slots before its page controller exists.
      _hasAppliedInitialPage = _pageViewController != nil;
    } else {
      _currentIndex = MIN(MAX(_currentIndex, 0), _pageControllers.count - 1);
    }
    UIViewController *controller = _pageControllers[_currentIndex];
    [_pageViewController setViewControllers:@[controller]
                                  direction:UIPageViewControllerNavigationDirectionForward
                                   animated:NO
                                 completion:nil];
  }
  if (_headerView != nil) [_containerView bringSubviewToFront:_headerView];
  if (_stickyHeaderView != nil) [_containerView bringSubviewToFront:_stickyHeaderView];
  [self setNeedsLayout];
  dispatch_async(dispatch_get_main_queue(), ^{
    if (!self->_isBeingRecycled) {
      [self prepareAdjacentPageInsets];
      [self attachScrollObserverForCurrentPage];
    }
  });
}

- (void)layoutSubviews
{
  [super layoutSubviews];
  _containerView.frame = self.bounds;
  _pageViewController.view.frame = _containerView.bounds;
  // OneKey patch: Fabric may mount page slots before applying the host props.
  // Commit the pending initial page once both props and page controllers exist.
  if (!_hasAppliedInitialPage && _pageViewController != nil &&
      _pendingInitialPage >= 0 && _pendingInitialPage < _pageControllers.count) {
    [self detachScrollObserver];
    _currentIndex = _pendingInitialPage;
    _destinationIndex = _currentIndex;
    _hasAppliedInitialPage = YES;
    [_pageViewController setViewControllers:@[_pageControllers[_currentIndex]]
                                  direction:UIPageViewControllerNavigationDirectionForward
                                   animated:NO
                                 completion:nil];
  }
  // UIKit frame assignment is undefined while a view has a non-identity transform.
  // Lay out the original slots, then restore the shared collapse translation.
  _headerView.transform = CGAffineTransformIdentity;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  _headerView.frame = CGRectMake(0, 0, self.bounds.size.width, _headerHeight);
  _stickyHeaderView.frame = CGRectMake(
    0,
    _headerHeight,
    self.bounds.size.width,
    _stickyHeaderHeight
  );
  // Settle UIKit page containers before sizing their autoresizing child views.
  [_pageViewController.view layoutIfNeeded];
  for (UIViewController *controller in _pageControllers) {
    controller.view.frame = _pageViewController.view.bounds;
    controller.view.subviews.firstObject.frame = controller.view.bounds;
  }
  [self applyHeaderOffset];
  dispatch_async(dispatch_get_main_queue(), ^{
    if (!self->_isBeingRecycled) {
      [self prepareAdjacentPageInsets];
      [self attachScrollObserverForCurrentPage];
    }
  });
}

- (void)prepareForRecycle
{
  _isBeingRecycled = YES;
  _generation++;
  [self detachScrollObserver];
  for (UIScrollView *scrollView in _originalInsets.keyEnumerator) {
    NSValue *value = [_originalInsets objectForKey:scrollView];
    if (value != nil) {
      UIEdgeInsets original = value.UIEdgeInsetsValue;
      NSNumber *appliedTop = [_appliedTopInsets objectForKey:scrollView];
      if (appliedTop != nil && scrollView.refreshControl != nil) {
        original.top += MAX(0, scrollView.contentInset.top - appliedTop.doubleValue);
      }
      scrollView.contentInset = original;
    }
    NSValue *indicatorValue = [_originalIndicatorInsets objectForKey:scrollView];
    if (indicatorValue != nil) {
      scrollView.verticalScrollIndicatorInsets = indicatorValue.UIEdgeInsetsValue;
    }
    NSNumber *alwaysBounce = [_originalAlwaysBounceVertical objectForKey:scrollView];
    if (alwaysBounce != nil) scrollView.alwaysBounceVertical = alwaysBounce.boolValue;
    NSNumber *adjustment = [_originalInsetAdjustmentBehavior objectForKey:scrollView];
    if (adjustment != nil) {
      scrollView.contentInsetAdjustmentBehavior = (UIScrollViewContentInsetAdjustmentBehavior)adjustment.integerValue;
    }
  }
  [_originalInsets removeAllObjects];
  [_appliedTopInsets removeAllObjects];
  [_originalIndicatorInsets removeAllObjects];
  [_originalAlwaysBounceVertical removeAllObjects];
  [_originalInsetAdjustmentBehavior removeAllObjects];
  // OneKey patch: Fabric reuses these views without resetting transforms
  // written by a native parent. Release our collapse translation with the slot.
  _headerView.transform = CGAffineTransformIdentity;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  [_headerView removeFromSuperview];
  [_stickyHeaderView removeFromSuperview];
  [_logicalChildren removeAllObjects];
  [_pageControllers removeAllObjects];
  _headerView = nil;
  _stickyHeaderView = nil;
  _pageViewController.dataSource = nil;
  _pageViewController.delegate = nil;
  _pagerScrollView.delegate = nil;
  if (_blockerGesture != nil) {
    [self removeGestureRecognizer:_blockerGesture];
    _blockerGesture = nil;
  }
  [_pageViewController.view removeFromSuperview];
  _pageViewController = nil;
  _pagerScrollView = nil;
  [super prepareForRecycle];
  [_pageOffsets removeAllObjects];
  _pageKeys = @[];
  _retainedPages = @"[]";
  _currentIndex = 0;
  _destinationIndex = 0;
  _pendingInitialPage = 0;
  _headerHeight = 0;
  _stickyHeaderHeight = 0;
  _headerOffset = 0;
  _scrollEnabled = YES;
  _nestedScrollEnabled = NO;
  _transitioning = NO;
  _isPagerDragging = NO;
  _hasReceivedPageCommand = NO;
  _pendingGoToIndex = -1;
  _hasAppliedInitialPage = NO;
  // OneKey patch: Fabric retains old props when recycling this view.
  _needsPropsReapply = YES;
  _layoutDirection = @"ltr";
  _isBeingRecycled = NO;
}

#pragma mark - Props

- (void)updateProps:(const Props::Shared &)props oldProps:(const Props::Shared &)oldProps
{
  const auto &oldViewProps = *std::static_pointer_cast<const RNCCollapsiblePagerViewProps>(_props);
  const auto &newViewProps = *std::static_pointer_cast<const RNCCollapsiblePagerViewProps>(props);

  if (_needsPropsReapply || oldViewProps.initialPage != newViewProps.initialPage) {
    _pendingInitialPage = MAX(0, newViewProps.initialPage);
    // Focus commands can arrive before the recycled host receives its props.
    _hasAppliedInitialPage = _hasReceivedPageCommand;
    // OneKey patch: Prop updates can arrive after the last slot was mounted.
    [self setNeedsLayout];
  }
  if (_needsPropsReapply || oldViewProps.scrollEnabled != newViewProps.scrollEnabled) {
    _scrollEnabled = newViewProps.scrollEnabled;
    _pagerScrollView.scrollEnabled = _scrollEnabled;
  }
  if (_needsPropsReapply || oldViewProps.nestedScrollEnabled != newViewProps.nestedScrollEnabled) {
    _nestedScrollEnabled = newViewProps.nestedScrollEnabled;
    [self applyNestedScrollBlocker];
  }
  if (_needsPropsReapply || oldViewProps.layoutDirection != newViewProps.layoutDirection) {
    _layoutDirection = RCTNSStringFromString(toString(newViewProps.layoutDirection));
  }
  BOOL insetsChanged = NO;
  if (_needsPropsReapply || oldViewProps.headerHeight != newViewProps.headerHeight) {
    _headerHeight = MAX(0, newViewProps.headerHeight);
    _headerOffset = MIN(_headerOffset, _headerHeight);
    insetsChanged = YES;
  }
  if (_needsPropsReapply || oldViewProps.stickyHeaderHeight != newViewProps.stickyHeaderHeight) {
    _stickyHeaderHeight = MAX(0, newViewProps.stickyHeaderHeight);
    insetsChanged = YES;
  }
  if (_needsPropsReapply || oldViewProps.pageKeys != newViewProps.pageKeys) {
    NSString *json = RCTNSStringFromString(newViewProps.pageKeys);
    NSData *data = [json dataUsingEncoding:NSUTF8StringEncoding];
    id parsed = data == nil ? nil : [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    _pageKeys = [parsed isKindOfClass:NSArray.class] ? parsed : @[];
  }
  if (_needsPropsReapply || oldViewProps.retainedPages != newViewProps.retainedPages) {
    _retainedPages = RCTNSStringFromString(newViewProps.retainedPages);
    [self setNeedsLayout];
  }

  _needsPropsReapply = NO;
  [super updateProps:props oldProps:oldProps];
  if (insetsChanged) {
    [self reapplyInsetsToObservedScrollView];
    [self setNeedsLayout];
  }
}

#pragma mark - Collapsible scroll coordination

- (NSString *)currentPageKey
{
  return [self pageKeyForIndex:_currentIndex];
}

- (NSString *)pageKeyForIndex:(NSInteger)index
{
  if (index >= 0 && index < _pageKeys.count) return _pageKeys[index];
  return [NSString stringWithFormat:@"page-%ld", (long)index];
}

- (UIScrollView *)findVerticalScrollViewInView:(UIView *)view
{
  if ([view isKindOfClass:UICollectionView.class]) {
    UICollectionView *collectionView = (UICollectionView *)view;
    UICollectionViewLayout *layout = collectionView.collectionViewLayout;
    if (![layout isKindOfClass:UICollectionViewFlowLayout.class] ||
        ((UICollectionViewFlowLayout *)layout).scrollDirection == UICollectionViewScrollDirectionVertical) {
      return collectionView;
    }
  }
  // Empty pages may use an RN ScrollView instead of a native row list.
  if ([view isKindOfClass:UIScrollView.class] &&
      ![view isKindOfClass:UICollectionView.class]) {
    UIScrollView *scrollView = (UIScrollView *)view;
    if (!scrollView.alwaysBounceHorizontal &&
        scrollView.contentSize.width <= scrollView.bounds.size.width + 1) {
      return scrollView;
    }
  }
  for (UIView *subview in view.subviews) {
    UIScrollView *candidate = [self findVerticalScrollViewInView:subview];
    if (candidate != nil) return candidate;
  }
  return nil;
}

- (void)attachScrollObserverForCurrentPage
{
  [self restoreDetachedScrollInsets];
  if (_currentIndex < 0 || _currentIndex >= _pageControllers.count) return;
  UIScrollView *candidate = [self findVerticalScrollViewInView:_pageControllers[_currentIndex].view];
  if (candidate == nil) return;
  // A decelerating list can otherwise claim a new horizontal touch immediately.
  if (_pagerScrollView != nil) {
    [candidate.panGestureRecognizer requireGestureRecognizerToFail:_pagerScrollView.panGestureRecognizer];
  }
  if (candidate == _observedScrollView) return;

  BOOL replacingCurrentScrollView = _observingContentOffset;
  [self detachScrollObserver];
  if (replacingCurrentScrollView) {
    // A list replaced by an empty RN view keeps collapse, not the old row offset.
    _pageOffsets[self.currentPageKey] = @(_headerOffset);
  }
  _observedScrollView = candidate;
  [self applyInsetsToScrollView:candidate pageIndex:_currentIndex restore:YES];
  [candidate addObserver:self
              forKeyPath:@"contentOffset"
                 options:NSKeyValueObservingOptionNew
                 context:RNCCollapsiblePagerContentOffsetContext];
  [candidate addObserver:self
              forKeyPath:@"contentSize"
                 options:NSKeyValueObservingOptionOld | NSKeyValueObservingOptionNew
                 context:RNCCollapsiblePagerContentOffsetContext];
  _observingContentOffset = YES;
}

- (void)detachScrollObserver
{
  UIScrollView *scrollView = _observedScrollView;
  if (scrollView != nil) {
    _pageOffsets[self.currentPageKey] = @(scrollView.contentOffset.y + scrollView.contentInset.top);
    if (_observingContentOffset) {
      [scrollView removeObserver:self
                      forKeyPath:@"contentOffset"
                         context:RNCCollapsiblePagerContentOffsetContext];
      [scrollView removeObserver:self
                      forKeyPath:@"contentSize"
                         context:RNCCollapsiblePagerContentOffsetContext];
    }
  }
  _observingContentOffset = NO;
  _observedScrollView = nil;
}

- (void)restoreDetachedScrollInsets
{
  for (UIScrollView *scrollView in _originalInsets.keyEnumerator.allObjects) {
    BOOL belongsToPage = NO;
    for (UIViewController *controller in _pageControllers) {
      if ([scrollView isDescendantOfView:controller.view]) {
        belongsToPage = YES;
        break;
      }
    }
    if (belongsToPage) continue;
    if (scrollView == _observedScrollView) {
      [self detachScrollObserver];
      _pageOffsets[self.currentPageKey] = @(_headerOffset);
    }
    // Fabric can reuse an empty RN ScrollView in another screen. Release all
    // pager-owned insets before that view is used as a search or dialog list.
    CGFloat logicalOffset = scrollView.contentOffset.y + scrollView.contentInset.top;
    UIEdgeInsets original = [_originalInsets objectForKey:scrollView].UIEdgeInsetsValue;
    NSNumber *appliedTop = [_appliedTopInsets objectForKey:scrollView];
    if (appliedTop != nil && scrollView.refreshControl != nil) {
      original.top += MAX(0, scrollView.contentInset.top - appliedTop.doubleValue);
    }
    scrollView.contentInset = original;
    scrollView.verticalScrollIndicatorInsets =
      [_originalIndicatorInsets objectForKey:scrollView].UIEdgeInsetsValue;
    scrollView.alwaysBounceVertical =
      [_originalAlwaysBounceVertical objectForKey:scrollView].boolValue;
    scrollView.contentInsetAdjustmentBehavior = (UIScrollViewContentInsetAdjustmentBehavior)
      [_originalInsetAdjustmentBehavior objectForKey:scrollView].integerValue;
    [scrollView setContentOffset:CGPointMake(scrollView.contentOffset.x, logicalOffset - original.top)
                       animated:NO];
    [_originalInsets removeObjectForKey:scrollView];
    [_appliedTopInsets removeObjectForKey:scrollView];
    [_originalIndicatorInsets removeObjectForKey:scrollView];
    [_originalAlwaysBounceVertical removeObjectForKey:scrollView];
    [_originalInsetAdjustmentBehavior removeObjectForKey:scrollView];
  }
}

- (void)applyInsetsToScrollView:(UIScrollView *)scrollView
                      pageIndex:(NSInteger)pageIndex
                        restore:(BOOL)restore
{
  NSValue *storedOriginal = [_originalInsets objectForKey:scrollView];
  UIEdgeInsets original = storedOriginal == nil
    ? scrollView.contentInset
    : storedOriginal.UIEdgeInsetsValue;
  if (storedOriginal == nil) {
    [_originalInsets setObject:[NSValue valueWithUIEdgeInsets:original] forKey:scrollView];
    [_originalIndicatorInsets
      setObject:[NSValue valueWithUIEdgeInsets:scrollView.verticalScrollIndicatorInsets]
      forKey:scrollView];
    [_originalAlwaysBounceVertical setObject:@(scrollView.alwaysBounceVertical) forKey:scrollView];
    [_originalInsetAdjustmentBehavior setObject:@(scrollView.contentInsetAdjustmentBehavior) forKey:scrollView];
  }

  // This host already includes the fixed header and safe area in its frame.
  // UIKit automatic adjustment would add them a second time to short pages.
  scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
  CGFloat previousTopInset = scrollView.contentInset.top;
  CGFloat previousLogicalOffset = scrollView.contentOffset.y + previousTopInset;
  NSNumber *appliedTop = [_appliedTopInsets objectForKey:scrollView];
  // UIRefreshControl owns its temporary top inset, including while ending.
  CGFloat refreshInset = appliedTop != nil && scrollView.refreshControl != nil
    ? MAX(0, previousTopInset - appliedTop.doubleValue) : 0;
  UIEdgeInsets next = original;
  next.top += _headerHeight + _stickyHeaderHeight;
  CGFloat pagerTopInset = next.top;
  next.top += refreshInset;
  [_appliedTopInsets setObject:@(pagerTopInset) forKey:scrollView];
  // Short pages still need enough range to collapse the header; long pages
  // must end at the last row rather than leave a header-sized blank footer.
  next.bottom += MAX(0, scrollView.bounds.size.height - _stickyHeaderHeight
    - original.top - original.bottom - scrollView.contentSize.height);
  if (UIEdgeInsetsEqualToEdgeInsets(scrollView.contentInset, next) && !restore) {
    // UICollectionView may report contentSize during a bounce without changing
    // the required insets. Re-setting contentOffset here interrupts that bounce.
    if (pageIndex == _currentIndex) [self updateHeaderForScrollView:scrollView];
    return;
  }
  scrollView.contentInset = next;
  UIEdgeInsets indicatorInsets = scrollView.verticalScrollIndicatorInsets;
  indicatorInsets.top = pagerTopInset;
  scrollView.verticalScrollIndicatorInsets = indicatorInsets;
  scrollView.alwaysBounceVertical = YES;

  CGFloat targetOffset = previousLogicalOffset - next.top;
  if (restore) {
    NSNumber *saved = _pageOffsets[[self pageKeyForIndex:pageIndex]];
    // The sticky slot can change height between pages. Cache offsets relative
    // to content start so a round trip does not add that height difference.
    targetOffset = (saved == nil ? _headerOffset : saved.doubleValue) - next.top;
    targetOffset = MAX(targetOffset, -next.top + _headerOffset);
  }
  // A bottom-inset change must not cancel UIKit's refresh or bounce settlement.
  if (restore || previousTopInset != next.top) {
    [scrollView setContentOffset:CGPointMake(scrollView.contentOffset.x, targetOffset) animated:NO];
  }
  if (pageIndex == _currentIndex) [self updateHeaderForScrollView:scrollView];
}

- (void)reapplyInsetsToObservedScrollView
{
  [self restoreDetachedScrollInsets];
  UIScrollView *scrollView = _observedScrollView;
  if (scrollView != nil) {
    [self applyInsetsToScrollView:scrollView pageIndex:_currentIndex restore:NO];
  }
  [self prepareAdjacentPageInsets];
}

- (void)prepareAdjacentPageInsets
{
  [self restoreDetachedScrollInsets];
  NSInteger first = MAX(0, _currentIndex - 1);
  NSInteger last = MIN((NSInteger)_pageControllers.count - 1, _currentIndex + 1);
  if (last < first) return;
  for (NSInteger index = first; index <= last; index++) {
    UIScrollView *scrollView = [self findVerticalScrollViewInView:_pageControllers[index].view];
    if (scrollView != nil && scrollView != _observedScrollView) {
      [self applyInsetsToScrollView:scrollView pageIndex:index restore:YES];
    }
  }
}

- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary<NSKeyValueChangeKey,id> *)change
                       context:(void *)context
{
  if (context == RNCCollapsiblePagerContentOffsetContext && object == _observedScrollView) {
    if ([keyPath isEqualToString:@"contentSize"]) {
      NSValue *oldSize = change[NSKeyValueChangeOldKey];
      NSValue *newSize = change[NSKeyValueChangeNewKey];
      if (![oldSize isEqual:newSize]) {
        [self applyInsetsToScrollView:_observedScrollView pageIndex:_currentIndex restore:NO];
      }
      return;
    }
    UIScrollView *scrollView = (UIScrollView *)object;
    _pageOffsets[self.currentPageKey] = @(scrollView.contentOffset.y + scrollView.contentInset.top);
    [self updateHeaderForScrollView:scrollView];
    return;
  }
  [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
}

- (void)updateHeaderForScrollView:(UIScrollView *)scrollView
{
  CGFloat logicalOffset = scrollView.contentOffset.y + scrollView.contentInset.top;
  _headerOffset = MIN(MAX(logicalOffset, 0), _headerHeight);
  [self applyHeaderOffset];
}

- (void)applyHeaderOffset
{
  CGAffineTransform transform = CGAffineTransformMakeTranslation(0, -_headerOffset);
  _headerView.transform = transform;
  _stickyHeaderView.transform = transform;
}

#pragma mark - Pager navigation

- (BOOL)isLtrLayout
{
  return [_layoutDirection isEqualToString:@"ltr"];
}

- (void)goTo:(NSInteger)index animated:(BOOL)animated
{
  if (_isBeingRecycled || index < 0 || index >= _pageControllers.count) return;
  _hasReceivedPageCommand = YES;
  _hasAppliedInitialPage = YES;
  if (_transitioning && animated) {
    _pendingGoToIndex = index;
    return;
  }

  _pendingGoToIndex = -1;

  // Re-selecting the displayed controller removes its view briefly and cancels
  // an in-flight list touch. Focus synchronization must be idempotent.
  if (!_transitioning && index == _currentIndex &&
      _pageViewController.viewControllers.firstObject == _pageControllers[index]) {
    [self attachScrollObserverForCurrentPage];
    [self emitPageSelected:index];
    [self emitDiagnostics:@"page-selected"];
    return;
  }

  [self detachScrollObserver];
  _destinationIndex = index;
  BOOL forward = (index > _currentIndex && self.isLtrLayout) ||
    (index < _currentIndex && !self.isLtrLayout);
  UIPageViewControllerNavigationDirection direction = forward
    ? UIPageViewControllerNavigationDirectionForward
    : UIPageViewControllerNavigationDirectionReverse;
  _transitioning = YES;
  _isPagerDragging = NO;
  NSUInteger capturedTransition = ++_transitionId;
  NSUInteger capturedGeneration = _generation;
  __weak __typeof__(self) weakSelf = self;
  [_pageViewController setViewControllers:@[_pageControllers[index]]
                                direction:direction
                                 animated:animated && index != _currentIndex
                               completion:^(BOOL finished) {
    __strong __typeof__(weakSelf) self = weakSelf;
    if (self == nil || self->_generation != capturedGeneration ||
        self->_transitionId != capturedTransition || self->_isBeingRecycled) return;
    if (finished) {
      self->_currentIndex = index;
      [self attachScrollObserverForCurrentPage];
      [self emitPageSelected:index];
      [self emitDiagnostics:@"page-selected"];
    }
    // UIKit is still unwinding its transition when this completion runs.
    // Start a queued animation only after that callback has returned.
    dispatch_async(dispatch_get_main_queue(), ^{
      if (self->_generation != capturedGeneration ||
          self->_transitionId != capturedTransition || self->_isBeingRecycled) return;
      self->_transitioning = NO;
      [self drainPendingGoTo];
    });
  }];
}

- (UIViewController *)adjacentController:(UIViewController *)controller delta:(NSInteger)delta
{
  NSInteger index = [_pageControllers indexOfObjectIdenticalTo:controller];
  if (index == NSNotFound) return nil;
  NSInteger target = index + delta;
  if (target < 0 || target >= _pageControllers.count) return nil;
  UIScrollView *scrollView = [self findVerticalScrollViewInView:_pageControllers[target].view];
  if (scrollView != nil) {
    [self applyInsetsToScrollView:scrollView pageIndex:target restore:YES];
  }
  return _pageControllers[target];
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController
      viewControllerAfterViewController:(UIViewController *)viewController
{
  return [self adjacentController:viewController delta:self.isLtrLayout ? 1 : -1];
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController
     viewControllerBeforeViewController:(UIViewController *)viewController
{
  return [self adjacentController:viewController delta:self.isLtrLayout ? -1 : 1];
}

- (void)drainPendingGoTo
{
  NSInteger pending = _pendingGoToIndex;
  _pendingGoToIndex = -1;
  if (pending >= 0 && pending != _currentIndex) {
    [self goTo:pending animated:YES];
  }
}

#pragma mark - Pager UIScrollViewDelegate

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
  // A user drag owns the result even if UIKit later completes the old animation.
  ++_transitionId;
  _isPagerDragging = YES;
  _transitioning = YES;
  const auto emitter = [self eventEmitter];
  if (emitter) {
    emitter->onPageScrollStateChanged({
      .pageScrollState = RNCCollapsiblePagerViewEventEmitter::OnPageScrollStateChangedPageScrollState::Dragging
    });
  }
}

- (void)scrollViewWillEndDragging:(UIScrollView *)scrollView
                     withVelocity:(CGPoint)velocity
              targetContentOffset:(inout CGPoint *)targetContentOffset
{
  const auto emitter = [self eventEmitter];
  if (emitter) {
    emitter->onPageScrollStateChanged({
      .pageScrollState = RNCCollapsiblePagerViewEventEmitter::OnPageScrollStateChangedPageScrollState::Settling
    });
  }
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView
                    willDecelerate:(BOOL)decelerate
{
  if (!decelerate) [self scrollViewDidEndDecelerating:scrollView];
}

- (void)finishPagerScrollEmittingSelection:(BOOL)emitSelection
{
  if (_isPagerDragging) {
    UIViewController *controller = nil;
    if (!emitSelection && _currentIndex >= 0 && _currentIndex < _pageControllers.count) {
      // Detaching can make UIKit settle on a neighbouring controller without a
      // completion callback. Keep the last page acknowledged by JavaScript.
      controller = _pageControllers[_currentIndex];
    } else {
      controller = _pageViewController.viewControllers.firstObject;
      // An interrupted programmatic animation can report its target while a
      // different controller is centered. Read the settled UIKit viewport.
      for (UIViewController *candidate in _pageControllers) {
        if (candidate.view.window == nil) continue;
        CGRect frame = [candidate.view convertRect:candidate.view.bounds toView:_containerView];
        if (fabs(frame.origin.x) < 1 &&
            fabs(frame.size.width - _containerView.bounds.size.width) < 1) {
          controller = candidate;
          break;
        }
      }
    }
    NSInteger index = [_pageControllers indexOfObjectIdenticalTo:controller];
    if (index != NSNotFound) {
      if (_pageViewController.viewControllers.firstObject != controller) {
        // Correct UIKit only after its animation completion has returned.
        [_pageViewController setViewControllers:@[controller]
                                      direction:UIPageViewControllerNavigationDirectionForward
                                       animated:NO
                                     completion:nil];
      }
      [self detachScrollObserver];
      _currentIndex = index;
      _destinationIndex = index;
      [self attachScrollObserverForCurrentPage];
      // A newer tab tap remains authoritative while its queued command runs.
      if (emitSelection &&
          (_pendingGoToIndex < 0 || _pendingGoToIndex == index)) {
        [self emitPageSelected:index];
        [self emitDiagnostics:@"page-selected"];
      }
    }
    _isPagerDragging = NO;
    _transitioning = NO;
  }
  const auto emitter = [self eventEmitter];
  if (emitter) {
    emitter->onPageScrollStateChanged({
      .pageScrollState = RNCCollapsiblePagerViewEventEmitter::OnPageScrollStateChangedPageScrollState::Idle
    });
  }
  [self emitDiagnostics:@"pager-idle"];
  [self drainPendingGoTo];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
  [self finishPagerScrollEmittingSelection:YES];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
  CGFloat width = scrollView.bounds.size.width;
  if (width <= 0) return;
  CGFloat rawOffset = (scrollView.contentOffset.x - width) / width;
  BOOL backwards = self.isLtrLayout ? rawOffset < 0 : rawOffset > 0;
  NSInteger position = backwards ? _currentIndex - 1 : _currentIndex;
  CGFloat offset = backwards ? 1 - fabs(rawOffset) : fabs(rawOffset);
  position = MAX(0, MIN(position, (NSInteger)_pageControllers.count - 1));
  const auto emitter = [self eventEmitter];
  if (emitter) {
    emitter->onPageScroll({.position = (double)position, .offset = (double)offset});
  }
}

#pragma mark - Events and commands

- (std::shared_ptr<const RNCCollapsiblePagerViewEventEmitter>)eventEmitter
{
  if (!_eventEmitter) return nullptr;
  return std::static_pointer_cast<const RNCCollapsiblePagerViewEventEmitter>(_eventEmitter);
}

- (void)emitPageSelected:(NSInteger)position
{
  const auto emitter = [self eventEmitter];
  if (emitter) emitter->onPageSelected({.position = (double)position});
}

- (void)emitDiagnostics:(NSString *)reason
{
  const auto emitter = [self eventEmitter];
  if (!emitter) return;
  NSInteger attached = 0;
  for (UIViewController *controller in _pageControllers) {
    if (controller.view.superview != nil) attached++;
  }
  emitter->onCollapsibleStateChanged({
    .position = (int)_currentIndex,
    .headerOffset = (double)_headerOffset,
    .nativePageCount = (int)_pageControllers.count,
    .attachedPageCount = (int)attached,
    .observedScrollableCount = (int)_originalInsets.count,
    .retainedPages = std::string(_retainedPages.UTF8String ?: "[]"),
    .reason = std::string(reason.UTF8String ?: "unknown")
  });
}

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args
{
  RCTRNCCollapsiblePagerViewHandleCommand(self, commandName, args);
}

- (void)setPage:(NSInteger)index
{
  [self goTo:index animated:YES];
}

- (void)setPageWithoutAnimation:(NSInteger)index
{
  [self goTo:index animated:NO];
}

- (void)setScrollEnabledImperatively:(BOOL)scrollEnabled
{
  _scrollEnabled = scrollEnabled;
  _pagerScrollView.scrollEnabled = scrollEnabled;
}

#pragma mark - Nested pager gesture coordination

- (void)applyNestedScrollBlocker
{
  if (_pagerScrollView == nil) return;

  if (_nestedScrollEnabled && _blockerGesture == nil) {
    _blockerGesture = [[UIPanGestureRecognizer alloc]
      initWithTarget:self
      action:@selector(blockerGestureFired:)];
    _blockerGesture.delegate = self;
    _blockerGesture.cancelsTouchesInView = NO;
    _blockerGesture.delaysTouchesBegan = NO;
    [self addGestureRecognizer:_blockerGesture];
    [_pagerScrollView.panGestureRecognizer requireGestureRecognizerToFail:_blockerGesture];
  } else if (!_nestedScrollEnabled && _blockerGesture != nil) {
    [self removeGestureRecognizer:_blockerGesture];
    _blockerGesture = nil;
  }
}

- (void)blockerGestureFired:(UIPanGestureRecognizer *)recognizer
{
  // Reset through the public enabled API. UIGestureRecognizer.state is
  // read-only for clients; only recognizer subclasses may assign it.
  if (recognizer.state == UIGestureRecognizerStateBegan) {
    recognizer.enabled = NO;
    recognizer.enabled = YES;
  }
}

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
{
  if (gestureRecognizer != _blockerGesture) return YES;
  if (!_nestedScrollEnabled) return NO;
  if (![gestureRecognizer isKindOfClass:[UIPanGestureRecognizer class]]) return NO;

  UIPanGestureRecognizer *pan = (UIPanGestureRecognizer *)gestureRecognizer;
  CGPoint velocity = [pan velocityInView:self];
  NSInteger pageCount = _pageControllers.count;
  if (pageCount == 0 || _currentIndex < 0 || _currentIndex >= pageCount) return NO;

  CGFloat absVelocityX = fabs(velocity.x);
  CGFloat absVelocityY = fabs(velocity.y);
  if (absVelocityX <= absVelocityY || absVelocityX < 1.0f) return NO;

  BOOL isLtr = self.isLtrLayout;
  NSInteger firstPageIndex = isLtr ? 0 : pageCount - 1;
  NSInteger lastPageIndex = isLtr ? pageCount - 1 : 0;
  if (_currentIndex == firstPageIndex && velocity.x > 0) return YES;
  if (_currentIndex == lastPageIndex && velocity.x < 0) return YES;
  return NO;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch
{
  // OneKey patch: a new touch can cancel UIKit's animation before its first
  // frame without starting a drag or completing the animation. Finish the
  // commanded page before handing that touch to the nested scroll views.
  if (gestureRecognizer == _blockerGesture && _transitioning && !_isPagerDragging &&
      [touch.view isDescendantOfView:_pagerScrollView]) {
    [self goTo:_destinationIndex animated:NO];
  }
  return YES;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer
    shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer
{
  return gestureRecognizer == _blockerGesture;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<RNCCollapsiblePagerViewComponentDescriptor>();
}

@end

Class<RCTComponentViewProtocol> RNCCollapsiblePagerViewCls(void)
{
  return RNCCollapsiblePagerViewComponentView.class;
}
