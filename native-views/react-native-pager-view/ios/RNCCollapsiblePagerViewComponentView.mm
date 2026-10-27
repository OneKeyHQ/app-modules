#import "RNCCollapsiblePagerViewComponentView.h"
#import "RNCPagerViewComponentView.h"
#import "RNCNativeScrollerComponentView.h"
#import "RNCCollapsiblePagerReleasedStatePolicy.h"

#import <react/renderer/components/pagerview/ComponentDescriptors.h>
#import <react/renderer/components/pagerview/EventEmitters.h>
#import <react/renderer/components/pagerview/Props.h>
#import <react/renderer/components/pagerview/RCTComponentViewHelpers.h>

#import "React/RCTConversions.h"
#import "React/RCTMountingTransactionObserving.h"
#import "React/RCTScrollViewComponentView.h"
#import "React/RCTSurfaceTouchHandler.h"
#import "React/RCTTouchHandler.h"
#import <objc/runtime.h>
#import <UIKit/UIGestureRecognizerSubclass.h>

// OneKey patch: deduplicate structural mutation parents before scanning the page.
#include <unordered_set>

using namespace facebook::react;

static void *RNCCollapsiblePagerContentOffsetContext = &RNCCollapsiblePagerContentOffsetContext;
static void *RNCRefreshControlLayerContext = &RNCRefreshControlLayerContext;
static void *RNCScrollRefreshControlContext = &RNCScrollRefreshControlContext;
static char RNCRefreshControlOffsetKey;
static void RNCCollapsiblePagerLog(NSString *message);

@interface RNCRefreshControlOffsetState : NSObject
+ (void)setOffset:(CGFloat)offset forControl:(UIRefreshControl *)control owner:(id)owner;
+ (void)removeControl:(UIRefreshControl *)control owner:(id)owner;
@end

@implementation RNCRefreshControlOffsetState {
  __weak UIRefreshControl *_control;
  NSMapTable<id, NSNumber *> *_offsets;
  CATransform3D _baselineTransform;
  CATransform3D _appliedTransform;
  BOOL _originalMasksToBounds;
  BOOL _applying;
  BOOL _observing;
  CGFloat _lastLoggedOffset;
  NSUInteger _lastLoggedOwnerCount;
  CGFloat _lastLoggedTransformY;
  BOOL _hasLoggedState;
}

- (instancetype)initWithControl:(UIRefreshControl *)control
{
  if ((self = [super init])) {
    _control = control;
    _baselineTransform = control.layer.sublayerTransform;
    _originalMasksToBounds = control.layer.masksToBounds;
    _offsets = [NSMapTable weakToStrongObjectsMapTable];
    [control.layer addObserver:self forKeyPath:@"sublayerTransform"
                       options:NSKeyValueObservingOptionNew
                       context:RNCRefreshControlLayerContext];
    [control.layer addObserver:self forKeyPath:@"masksToBounds"
                       options:NSKeyValueObservingOptionNew
                       context:RNCRefreshControlLayerContext];
    _observing = YES;
  }
  return self;
}

- (void)apply
{
  UIRefreshControl *control = _control;
  if (control == nil) return;
  CGFloat offset = 0;
  for (NSNumber *value in _offsets.objectEnumerator) offset += value.doubleValue;
  CATransform3D transform = CATransform3DConcat(
    _baselineTransform, CATransform3DMakeTranslation(0, -offset, 0));
  _appliedTransform = transform;
  _applying = YES;
  // Visual placement must not change UIKit's refresh geometry or RN's bounds
  // origin (progressViewOffset). Allow the shifted indicator outside its host.
  [CATransaction begin];
  [CATransaction setDisableActions:YES];
  control.layer.masksToBounds = _offsets.count == 0 ? _originalMasksToBounds : NO;
  if (!CATransform3DEqualToTransform(control.layer.sublayerTransform, transform)) {
    control.layer.sublayerTransform = transform;
  }
  [CATransaction commit];
  _applying = NO;
#if DEBUG
  if (!_hasLoggedState || fabs(_lastLoggedOffset - offset) > 0.01 ||
      _lastLoggedOwnerCount != _offsets.count ||
      fabs(_lastLoggedTransformY - transform.m42) > 0.01) {
    _hasLoggedState = YES;
    _lastLoggedOffset = offset;
    _lastLoggedOwnerCount = _offsets.count;
    _lastLoggedTransformY = transform.m42;
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"[IOS-HOME-VISUAL] epochMs=%.0f monoMs=%.0f phase=refresh-transform ownerCount=%lu offsetSum=%.1f baselineY=%.1f appliedY=%.1f masksToBounds=%d",
      [NSDate.date timeIntervalSince1970] * 1000, CACurrentMediaTime() * 1000,
      (unsigned long)_offsets.count, offset, _baselineTransform.m42,
      transform.m42, control.layer.masksToBounds]);
  }
#endif
}

+ (void)setOffset:(CGFloat)offset forControl:(UIRefreshControl *)control owner:(id)owner
{
  if (control == nil) return;
  RNCRefreshControlOffsetState *state = objc_getAssociatedObject(control, &RNCRefreshControlOffsetKey);
  if (state == nil) {
    state = [[self alloc] initWithControl:control];
    objc_setAssociatedObject(control, &RNCRefreshControlOffsetKey, state, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
  }
  [state->_offsets setObject:@(offset) forKey:owner];
  [state apply];
}

+ (void)removeControl:(UIRefreshControl *)control owner:(id)owner
{
  if (control == nil) return;
  RNCRefreshControlOffsetState *state = objc_getAssociatedObject(control, &RNCRefreshControlOffsetKey);
  if (state == nil) return;
  [state->_offsets removeObjectForKey:owner];
  [state apply];
  if (state->_offsets.count == 0) {
    [control.layer removeObserver:state forKeyPath:@"sublayerTransform" context:RNCRefreshControlLayerContext];
    [control.layer removeObserver:state forKeyPath:@"masksToBounds" context:RNCRefreshControlLayerContext];
    state->_observing = NO;
    objc_setAssociatedObject(control, &RNCRefreshControlOffsetKey, nil, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
  }
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object
                      change:(NSDictionary<NSKeyValueChangeKey, id> *)change context:(void *)context
{
  if (context == RNCRefreshControlLayerContext) {
    if (_applying) return;
    if ([keyPath isEqualToString:@"sublayerTransform"]) {
      CATransform3D next = [change[NSKeyValueChangeNewKey] CATransform3DValue];
      if (CATransform3DEqualToTransform(next, _appliedTransform)) return;
      _baselineTransform = next;
    }
    [self apply];
    return;
  }
  [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
}

- (void)dealloc
{
  if (_observing && _control != nil) {
    [_control.layer removeObserver:self forKeyPath:@"sublayerTransform" context:RNCRefreshControlLayerContext];
    [_control.layer removeObserver:self forKeyPath:@"masksToBounds" context:RNCRefreshControlLayerContext];
  }
}
@end
static NSString *const RNCCollapsiblePagerNativeScrollerIDPrefix =
  @"rnc-collapsible-pager-native-scroller:";

static const CFTimeInterval RNCCollapsiblePagerFallbackDebounceInterval = 0.5;

typedef NS_ENUM(NSInteger, RNCScrollCandidateTier) {
  RNCScrollCandidateTierNone = 0,
  RNCScrollCandidateTierGenericFallback = 1,
  RNCScrollCandidateTierVerifiedNativeList = 2,
  RNCScrollCandidateTierFabricScrollView = 3,
  RNCScrollCandidateTierExplicitScroller = 4,
};

struct RNCScrollCandidateResolution {
  UIScrollView *scrollView = nil;
  RNCScrollCandidateTier tier = RNCScrollCandidateTierNone;
  BOOL hasUnreadyScrollHost = NO;
};

// OneKey patch: native scroll views can survive a move or be reused for a new
// Fabric component. Keep weak identity until their page is attached again.
@interface RNCCollapsiblePagerReleasedScrollState : NSObject
@property (nonatomic, weak) UIScrollView *scrollView;
@property (nonatomic, weak) UIView *componentView;
@property (nonatomic, assign) NSInteger componentTag;
@property (nonatomic, weak) UIScrollView *pendingFallbackScrollView;
@property (nonatomic, assign) CFTimeInterval pendingFallbackDetectedAt;
@end

@implementation RNCCollapsiblePagerReleasedScrollState
@end

static UIView *RNCScrollComponentView(UIScrollView *scrollView)
{
  for (UIView *view = scrollView; view != nil; view = view.superview) {
    // UIView's Fabric category adopts the protocol even for unregistered views.
    if ([view isKindOfClass:RCTViewComponentView.class] && view.tag > 0) return view;
  }
  return nil;
}

// NativeList owns its collection through a Fabric contentView and its delegate.
// A dropped Nitro view can remain in the hierarchy after its delegate is cleared.
static RCTViewComponentView *RNCNativeListComponentForScrollView(UIScrollView *scrollView)
{
  Class nativeListClass = NSClassFromString(@"HybridNativeListComponent");
  for (UIView *component = scrollView; component != nil; component = component.superview) {
    if (![component isKindOfClass:RCTViewComponentView.class]) continue;
    return nativeListClass != Nil && [component isKindOfClass:nativeListClass]
      ? (RCTViewComponentView *)component : nil;
  }
  return nil;
}

static BOOL RNCIsMountedNativeListScrollView(UIScrollView *scrollView)
{
  if (![scrollView isKindOfClass:UICollectionView.class]) return NO;
  RCTViewComponentView *component = RNCNativeListComponentForScrollView(scrollView);
  UIView *content = component.contentView;
  return component.tag > 0 && content != nil && [scrollView isDescendantOfView:content] &&
    ((UICollectionView *)scrollView).delegate == (id)content;
}

typedef void (^RNCCollapsiblePagerNativeTabPressHandler)(NSInteger index, NSString *key);

static CGFloat RNCClamp(CGFloat value, CGFloat minimum, CGFloat maximum)
{
  return MIN(MAX(value, minimum), maximum);
}

static BOOL RNCIsEnabledHorizontalScrollOwner(UIScrollView *scroll)
{
  return scroll.scrollEnabled && scroll.panGestureRecognizer.enabled &&
    scroll.contentSize.width > CGRectGetWidth(scroll.bounds) && !scroll.alwaysBounceVertical &&
    scroll.contentSize.height <= CGRectGetHeight(scroll.bounds) + 0.5;
}

static UIScrollView *RNCNestedOuterReactPagerScrollView(UIView *view)
{
  Class outerPagerClass = NSClassFromString(@"RNCPagerViewComponentView");
  if (outerPagerClass == Nil) return nil;
  UIScrollView *pagingAncestor = nil;
  for (UIView *ancestor = view.superview; ancestor != nil; ancestor = ancestor.superview) {
    if ([ancestor isKindOfClass:UIScrollView.class] &&
        ((UIScrollView *)ancestor).pagingEnabled) {
      pagingAncestor = (UIScrollView *)ancestor;
    }
    if ([ancestor isKindOfClass:outerPagerClass]) {
      return pagingAncestor.panGestureRecognizer.enabled ? pagingAncestor : nil;
    }
  }
  return nil;
}

static void RNCGuardNestedHorizontalPans(UIView *view, UIGestureRecognizer *guard)
{
  if ([view isKindOfClass:UIScrollView.class]) {
    UIScrollView *scroll = (UIScrollView *)view;
    if (RNCIsEnabledHorizontalScrollOwner(scroll)) {
      [scroll.panGestureRecognizer requireGestureRecognizerToFail:guard];
    }
  }
  for (UIView *child in view.subviews) RNCGuardNestedHorizontalPans(child, guard);
}

static void RNCCollapsiblePagerLog(NSString *message)
{
  Class logClass = NSClassFromString(@"ReactNativeNativeLogger.OneKeyLog");
  if (logClass == nil) {
    logClass = NSClassFromString(@"OneKeyLog");
  }
  SEL selector = NSSelectorFromString(@"debug::");
  if (logClass == nil || ![logClass respondsToSelector:selector]) return;

  typedef void (*LogFunction)(id, SEL, NSString *, NSString *);
  LogFunction logFunction = (LogFunction)[logClass methodForSelector:selector];
  logFunction(logClass, selector, @"CollapsiblePager", message);
}


static BOOL RNCGetColorComponents(UIColor *color, UITraitCollection *traits,
                                  CGFloat *red, CGFloat *green, CGFloat *blue, CGFloat *alpha)
{
  UIColor *resolved = [color resolvedColorWithTraitCollection:traits];
  if ([resolved getRed:red green:green blue:blue alpha:alpha]) return YES;
  CGFloat white = 0;
  if ([resolved getWhite:&white alpha:alpha]) {
    *red = white;
    *green = white;
    *blue = white;
    return YES;
  }
  return NO;
}

static UIColor *RNCInterpolateColor(UIColor *from, UIColor *to, CGFloat progress,
                                    UITraitCollection *traits)
{
  CGFloat fromRed = 0, fromGreen = 0, fromBlue = 0, fromAlpha = 1;
  CGFloat toRed = 0, toGreen = 0, toBlue = 0, toAlpha = 1;
  if (!RNCGetColorComponents(from, traits, &fromRed, &fromGreen, &fromBlue, &fromAlpha) ||
      !RNCGetColorComponents(to, traits, &toRed, &toGreen, &toBlue, &toAlpha)) {
    return progress >= 0.5 ? to : from;
  }
  CGFloat clamped = RNCClamp(progress, 0, 1);
  return [UIColor colorWithRed:fromRed + (toRed - fromRed) * clamped
                         green:fromGreen + (toGreen - fromGreen) * clamped
                          blue:fromBlue + (toBlue - fromBlue) * clamped
                         alpha:fromAlpha + (toAlpha - fromAlpha) * clamped];
}

@interface RNCCollapsiblePagerHorizontalScrollView : UIScrollView
@property (nonatomic, copy) NSString *axisOwner;
@end

@implementation RNCCollapsiblePagerHorizontalScrollView

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
{
  if (gestureRecognizer != self.panGestureRecognizer) {
    return [super gestureRecognizerShouldBegin:gestureRecognizer];
  }

  CGPoint translation = [self.panGestureRecognizer translationInView:self];
  CGPoint velocity = [self.panGestureRecognizer velocityInView:self];
  CGPoint intent = fabs(translation.x) + fabs(translation.y) >= 1
    ? translation
    : velocity;
  CGFloat maximumOffset = MAX(0, self.contentSize.width - CGRectGetWidth(self.bounds));
  BOOL hasHorizontalRange = maximumOffset > 0;
  BOOL horizontal = MAX(fabs(intent.x), fabs(intent.y)) >= 1 &&
    fabs(intent.x) > fabs(intent.y);
  BOOL shouldBegin = hasHorizontalRange && horizontal &&
    [super gestureRecognizerShouldBegin:gestureRecognizer];
  RNCCollapsiblePagerLog([NSString stringWithFormat:
    @"native-tab-axis owner=%@ dx=%.2f dy=%.2f max=%.2f result=%d",
    _axisOwner ?: @"unknown",
    intent.x,
    intent.y,
    maximumOffset,
    shouldBegin]);
  return shouldBegin;
}

// Tab and category items are UIButtons that start tracking on touch-down, and UIScrollView does
// not cancel UIControl touches by default, so a drag that began on an item never scrolled the bar.
// The host NativeList used to hide this by holding touch-down back for about 150 ms.
- (BOOL)touchesShouldCancelInContentView:(UIView *)view
{
  return [view isKindOfClass:UIControl.class] ? YES : [super touchesShouldCancelInContentView:view];
}

@end

static void RNCLogNativeTabScrollBoundary(NSString *owner,
                                          NSString *phase,
                                          UIScrollView *scrollView,
                                          CGFloat startOffsetX)
{
  NSInteger listState = -1;
  NSInteger pagerState = -1;
  for (UIView *ancestor = scrollView.superview;
       ancestor != nil;
       ancestor = ancestor.superview) {
    if (![ancestor isKindOfClass:UIScrollView.class]) continue;
    UIScrollView *ancestorScrollView = (UIScrollView *)ancestor;
    if (ancestorScrollView.pagingEnabled) {
      pagerState = ancestorScrollView.panGestureRecognizer.state;
    } else if (listState < 0 &&
               (ancestorScrollView.alwaysBounceVertical ||
                ancestorScrollView.contentSize.height >
                  CGRectGetHeight(ancestorScrollView.bounds))) {
      listState = ancestorScrollView.panGestureRecognizer.state;
    }
  }
  RNCCollapsiblePagerLog([NSString stringWithFormat:
    @"native-tab-scroll owner=%@ phase=%@ x-start=%.2f x-current=%.2f state=%ld list-state=%ld pager-state=%ld",
    owner,
    phase,
    startOffsetX,
    scrollView.contentOffset.x,
    (long)scrollView.panGestureRecognizer.state,
    (long)listState,
    (long)pagerState]);
}

@interface RNCCollapsiblePagerNativeTabBarView : UIView <UIScrollViewDelegate>
@property (nonatomic, copy) RNCCollapsiblePagerNativeTabPressHandler onTabPress;
- (void)updateItemsJSON:(NSString *)itemsJSON;
- (void)updateStyleWithHeight:(CGFloat)height
     contentPaddingHorizontal:(CGFloat)contentPaddingHorizontal
                  itemSpacing:(CGFloat)itemSpacing
                     fontSize:(CGFloat)fontSize
                   fontFamily:(NSString *)fontFamily
              backgroundColor:(UIColor *)backgroundColor
              activeTextColor:(UIColor *)activeTextColor
            inactiveTextColor:(UIColor *)inactiveTextColor
               indicatorColor:(UIColor *)indicatorColor
              indicatorHeight:(CGFloat)indicatorHeight
              indicatorBottom:(CGFloat)indicatorBottom;
- (void)setProgress:(CGFloat)progress;
- (void)setLeftToRight:(BOOL)leftToRight;
- (void)updateAncestorGesturePrecedence;
@end

@implementation RNCCollapsiblePagerNativeTabBarView {
  RNCCollapsiblePagerHorizontalScrollView *_scrollView;
  UIView *_indicatorView;
  NSArray<NSDictionary *> *_items;
  NSMutableArray<UIButton *> *_buttons;
  NSMutableArray<NSValue *> *_indicatorFrames;
  CGFloat _barHeight;
  CGFloat _contentPaddingHorizontal;
  CGFloat _itemSpacing;
  CGFloat _fontSize;
  NSString *_fontFamily;
  UIColor *_activeTextColor;
  UIColor *_inactiveTextColor;
  UIColor *_indicatorColor;
  CGFloat _indicatorHeight;
  CGFloat _indicatorBottom;
  CGFloat _progress;
  BOOL _leftToRight;
  BOOL _shouldCenterSelectedItem;
  CGFloat _scrollStartOffsetX;
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    _items = @[];
    _buttons = [NSMutableArray new];
    _indicatorFrames = [NSMutableArray new];
    _barHeight = 44;
    _contentPaddingHorizontal = 20;
    _itemSpacing = 8;
    _fontSize = 16;
    _activeTextColor = UIColor.labelColor;
    _inactiveTextColor = UIColor.secondaryLabelColor;
    _indicatorColor = UIColor.labelColor;
    _indicatorHeight = 2;
    _indicatorBottom = 0;
    _leftToRight = YES;
    _shouldCenterSelectedItem = YES;

    _scrollView = [RNCCollapsiblePagerHorizontalScrollView new];
    _scrollView.axisOwner = @"primary";
    _scrollView.showsHorizontalScrollIndicator = NO;
    _scrollView.showsVerticalScrollIndicator = NO;
    _scrollView.alwaysBounceHorizontal = YES;
    _scrollView.directionalLockEnabled = YES;
    _scrollView.delaysContentTouches = NO;
    _scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    _scrollView.delegate = self;
    [self addSubview:_scrollView];

    _indicatorView = [UIView new];
    _indicatorView.userInteractionEnabled = NO;
    [_scrollView addSubview:_indicatorView];
  }
  return self;
}

- (UIFont *)tabFont
{
  if (_fontFamily.length > 0) {
    UIFont *font = [UIFont fontWithName:_fontFamily size:_fontSize];
    if (font != nil) return font;
  }
  return [UIFont systemFontOfSize:_fontSize weight:UIFontWeightMedium];
}

- (void)updateItemsJSON:(NSString *)itemsJSON
{
  NSData *data = [itemsJSON dataUsingEncoding:NSUTF8StringEncoding];
  id parsed = data == nil ? nil : [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
  NSArray *items = [parsed isKindOfClass:NSArray.class] ? parsed : @[];
  if ([_items isEqualToArray:items]) return;
  _items = items;

  for (UIButton *button in _buttons) [button removeFromSuperview];
  [_buttons removeAllObjects];
  [_indicatorFrames removeAllObjects];

  [_items enumerateObjectsUsingBlock:^(NSDictionary *item, NSUInteger index, BOOL *stop) {
    UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
    button.tag = (NSInteger)index;
    button.titleLabel.font = self.tabFont;
    button.titleLabel.lineBreakMode = NSLineBreakByTruncatingTail;
    NSString *title = [item[@"title"] isKindOfClass:NSString.class] ? item[@"title"] : @"";
    [button setTitle:title forState:UIControlStateNormal];
    [button addTarget:self action:@selector(handleTabPress:) forControlEvents:UIControlEventTouchUpInside];
    NSString *accessibilityLabel = [item[@"accessibilityLabel"] isKindOfClass:NSString.class]
      ? item[@"accessibilityLabel"]
      : title;
    button.accessibilityLabel = accessibilityLabel;
    NSString *testID = [item[@"testID"] isKindOfClass:NSString.class] ? item[@"testID"] : nil;
    button.accessibilityIdentifier = testID;
    [self->_scrollView addSubview:button];
    [self->_buttons addObject:button];
  }];

  [_scrollView bringSubviewToFront:_indicatorView];
  for (UIButton *button in _buttons) [_scrollView bringSubviewToFront:button];
  _shouldCenterSelectedItem = YES;
  self.hidden = _items.count == 0;
  [self setNeedsLayout];
}

- (void)updateStyleWithHeight:(CGFloat)height
     contentPaddingHorizontal:(CGFloat)contentPaddingHorizontal
                  itemSpacing:(CGFloat)itemSpacing
                     fontSize:(CGFloat)fontSize
                   fontFamily:(NSString *)fontFamily
              backgroundColor:(UIColor *)backgroundColor
              activeTextColor:(UIColor *)activeTextColor
            inactiveTextColor:(UIColor *)inactiveTextColor
               indicatorColor:(UIColor *)indicatorColor
              indicatorHeight:(CGFloat)indicatorHeight
              indicatorBottom:(CGFloat)indicatorBottom
{
  _barHeight = MAX(1, height);
  _contentPaddingHorizontal = MAX(0, contentPaddingHorizontal);
  _itemSpacing = MAX(0, itemSpacing);
  _fontSize = MAX(1, fontSize);
  _fontFamily = [fontFamily copy];
  self.backgroundColor = backgroundColor ?: UIColor.clearColor;
  _scrollView.backgroundColor = self.backgroundColor;
  _activeTextColor = activeTextColor ?: UIColor.labelColor;
  _inactiveTextColor = inactiveTextColor ?: UIColor.secondaryLabelColor;
  _indicatorColor = indicatorColor ?: _activeTextColor;
  _indicatorHeight = MAX(0, indicatorHeight);
  _indicatorBottom = MAX(0, indicatorBottom);
  _indicatorView.backgroundColor = _indicatorColor;
  _indicatorView.layer.cornerRadius = _indicatorHeight / 2;
  for (UIButton *button in _buttons) button.titleLabel.font = self.tabFont;
  [self setNeedsLayout];
}

- (void)setLeftToRight:(BOOL)leftToRight
{
  if (_leftToRight == leftToRight) return;
  _leftToRight = leftToRight;
  _shouldCenterSelectedItem = YES;
  [self setNeedsLayout];
}

- (void)updateAncestorGesturePrecedence
{
  for (UIView *ancestor = self.superview; ancestor != nil; ancestor = ancestor.superview) {
    if ([ancestor isKindOfClass:UIScrollView.class]) {
      UIScrollView *scrollView = (UIScrollView *)ancestor;
      if (scrollView.pagingEnabled || scrollView.alwaysBounceHorizontal) {
        [scrollView.panGestureRecognizer
          requireGestureRecognizerToFail:_scrollView.panGestureRecognizer];
      }
    }
  }
}

- (void)handleTabPress:(UIButton *)button
{
  NSInteger index = button.tag;
  if (index < 0 || index >= (NSInteger)_items.count) return;
  NSDictionary *item = _items[index];
  NSString *key = [item[@"key"] isKindOfClass:NSString.class] ? item[@"key"] : @"";
  _shouldCenterSelectedItem = YES;
  if (self.onTabPress) self.onTabPress(index, key);
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
  _shouldCenterSelectedItem = NO;
  _scrollStartOffsetX = scrollView.contentOffset.x;
  RNCLogNativeTabScrollBoundary(@"primary", @"begin", scrollView, _scrollStartOffsetX);
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView
                  willDecelerate:(BOOL)decelerate
{
  if (decelerate) return;
  RNCLogNativeTabScrollBoundary(@"primary", @"end", scrollView, _scrollStartOffsetX);
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
  RNCLogNativeTabScrollBoundary(@"primary", @"end", scrollView, _scrollStartOffsetX);
}

- (void)layoutSubviews
{
  [super layoutSubviews];
  _scrollView.frame = self.bounds;
  UIFont *font = self.tabFont;
  NSMutableArray<NSNumber *> *widths = [NSMutableArray arrayWithCapacity:_buttons.count];
  CGFloat itemsWidth = 0;
  for (UIButton *button in _buttons) {
    NSString *title = [button titleForState:UIControlStateNormal] ?: @"";
    CGFloat textWidth = ceil([title sizeWithAttributes:@{NSFontAttributeName: font}].width);
    CGFloat buttonWidth = MAX(44, textWidth + 16);
    [widths addObject:@(buttonWidth)];
    itemsWidth += buttonWidth;
  }
  if (_buttons.count > 1) itemsWidth += (_buttons.count - 1) * _itemSpacing;
  CGFloat contentWidth = MAX(CGRectGetWidth(self.bounds),
                             itemsWidth + _contentPaddingHorizontal * 2);
  _scrollView.contentSize = CGSizeMake(contentWidth, MAX(_barHeight, CGRectGetHeight(self.bounds)));
  [_indicatorFrames removeAllObjects];

  __block CGFloat x = _leftToRight
    ? _contentPaddingHorizontal
    : contentWidth - _contentPaddingHorizontal;
  [_buttons enumerateObjectsUsingBlock:^(UIButton *button, NSUInteger index, BOOL *stop) {
    CGFloat buttonWidth = widths[index].doubleValue;
    if (!self->_leftToRight) x -= buttonWidth;
    button.frame = CGRectMake(x, 0, buttonWidth, self->_barHeight);
    NSString *title = [button titleForState:UIControlStateNormal] ?: @"";
    CGFloat textWidth = ceil([title sizeWithAttributes:@{NSFontAttributeName: font}].width);
    CGRect indicatorFrame = CGRectMake(
      CGRectGetMidX(button.frame) - textWidth / 2,
      self->_barHeight - self->_indicatorBottom - self->_indicatorHeight,
      textWidth,
      self->_indicatorHeight
    );
    [self->_indicatorFrames addObject:[NSValue valueWithCGRect:indicatorFrame]];
    if (self->_leftToRight) {
      x += buttonWidth + self->_itemSpacing;
    } else {
      x -= self->_itemSpacing;
    }
  }];
  [self updatePresentation];
}

- (void)setProgress:(CGFloat)progress
{
  if (_items.count == 0) return;
  if (fabs(_progress - progress) >= 0.001) _shouldCenterSelectedItem = YES;
  _progress = RNCClamp(progress, 0, _items.count - 1);
  if (_indicatorFrames.count == _items.count) [self updatePresentation];
}

- (void)updatePresentation
{
  NSInteger count = _buttons.count;
  if (count == 0 || _indicatorFrames.count != count) return;
  CGFloat progress = RNCClamp(_progress, 0, count - 1);
  NSInteger lower = (NSInteger)floor(progress);
  NSInteger upper = MIN(lower + 1, count - 1);
  CGFloat fraction = progress - lower;
  CGRect fromFrame = _indicatorFrames[lower].CGRectValue;
  CGRect toFrame = _indicatorFrames[upper].CGRectValue;
  CGRect indicatorFrame = CGRectMake(
    CGRectGetMinX(fromFrame) + (CGRectGetMinX(toFrame) - CGRectGetMinX(fromFrame)) * fraction,
    CGRectGetMinY(fromFrame),
    CGRectGetWidth(fromFrame) + (CGRectGetWidth(toFrame) - CGRectGetWidth(fromFrame)) * fraction,
    _indicatorHeight
  );
  _indicatorView.frame = indicatorFrame;
  _indicatorView.hidden = _indicatorHeight <= 0;

  NSInteger selectedIndex = (NSInteger)round(progress);
  [_buttons enumerateObjectsUsingBlock:^(UIButton *button, NSUInteger index, BOOL *stop) {
    CGFloat emphasis = 1 - MIN(1, fabs(progress - index));
    UIColor *color = RNCInterpolateColor(
      self->_inactiveTextColor,
      self->_activeTextColor,
      emphasis,
      self.traitCollection
    );
    [button setTitleColor:color forState:UIControlStateNormal];
    button.accessibilityTraits = index == selectedIndex
      ? UIAccessibilityTraitButton | UIAccessibilityTraitSelected
      : UIAccessibilityTraitButton;
  }];

  if (_shouldCenterSelectedItem && !_scrollView.tracking && !_scrollView.dragging &&
      !_scrollView.decelerating && CGRectGetWidth(_scrollView.bounds) > 0) {
    CGFloat maximumOffset = MAX(0, _scrollView.contentSize.width - CGRectGetWidth(_scrollView.bounds));
    CGFloat desiredOffset = RNCClamp(
      CGRectGetMidX(indicatorFrame) - CGRectGetWidth(_scrollView.bounds) / 2,
      0,
      maximumOffset
    );
    [_scrollView setContentOffset:CGPointMake(desiredOffset, 0) animated:NO];
    _shouldCenterSelectedItem = NO;
  }
}

@end

@interface RNCCollapsiblePagerNativeSubHeaderView : UIView <UIScrollViewDelegate>
@property (nonatomic, copy) RNCCollapsiblePagerNativeTabPressHandler onItemPress;
@property (nonatomic, readonly) CGFloat preferredHeight;
- (void)updateConfigJSON:(NSString *)configJSON;
- (void)updateColorsWithBackgroundColor:(UIColor *)backgroundColor
                        activeTextColor:(UIColor *)activeTextColor
                      inactiveTextColor:(UIColor *)inactiveTextColor
                selectedBackgroundColor:(UIColor *)selectedBackgroundColor
                              fontFamily:(NSString *)fontFamily;
- (void)setLeftToRight:(BOOL)leftToRight;
- (void)updateAncestorGesturePrecedence;
@end

@implementation RNCCollapsiblePagerNativeSubHeaderView {
  RNCCollapsiblePagerHorizontalScrollView *_tabsScrollView;
  UIView *_columnsView;
  UILabel *_leadingColumnLabel;
  UILabel *_middleColumnLabel;
  UILabel *_trailingColumnLabel;
  NSArray<NSDictionary *> *_items;
  NSMutableArray<UIButton *> *_buttons;
  NSString *_selectedKey;
  CGFloat _configuredHeight;
  CGFloat _tabsHeight;
  CGFloat _contentPaddingHorizontal;
  CGFloat _itemSpacing;
  CGFloat _fontSize;
  CGFloat _columnFontSize;
  CGFloat _trailingColumnWidth;
  CGFloat _columnGap;
  NSString *_fontFamily;
  UIColor *_activeTextColor;
  UIColor *_inactiveTextColor;
  UIColor *_selectedBackgroundColor;
  BOOL _leftToRight;
  BOOL _shouldCenterSelectedItem;
  CGFloat _scrollStartOffsetX;
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    _items = @[];
    _buttons = [NSMutableArray new];
    _selectedKey = @"";
    _configuredHeight = 74;
    _tabsHeight = 42;
    _contentPaddingHorizontal = 20;
    _itemSpacing = 8;
    _fontSize = 14;
    _columnFontSize = 12;
    _trailingColumnWidth = 80;
    _columnGap = 8;
    _activeTextColor = UIColor.labelColor;
    _inactiveTextColor = UIColor.secondaryLabelColor;
    _selectedBackgroundColor = UIColor.secondarySystemFillColor;
    _leftToRight = YES;
    _shouldCenterSelectedItem = YES;

    _tabsScrollView = [RNCCollapsiblePagerHorizontalScrollView new];
    _tabsScrollView.axisOwner = @"secondary";
    _tabsScrollView.showsHorizontalScrollIndicator = NO;
    _tabsScrollView.showsVerticalScrollIndicator = NO;
    _tabsScrollView.alwaysBounceHorizontal = YES;
    _tabsScrollView.directionalLockEnabled = YES;
    _tabsScrollView.delaysContentTouches = NO;
    _tabsScrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    _tabsScrollView.delegate = self;
    [self addSubview:_tabsScrollView];

    _columnsView = [UIView new];
    [self addSubview:_columnsView];
    _leadingColumnLabel = [UILabel new];
    _middleColumnLabel = [UILabel new];
    _trailingColumnLabel = [UILabel new];
    for (UILabel *label in @[_leadingColumnLabel, _middleColumnLabel, _trailingColumnLabel]) {
      label.numberOfLines = 1;
      label.lineBreakMode = NSLineBreakByTruncatingTail;
      [_columnsView addSubview:label];
    }
  }
  return self;
}

- (CGFloat)preferredHeight
{
  return _configuredHeight;
}

- (void)didMoveToWindow
{
  [super didMoveToWindow];
  if (self.window == nil) return;

  [self updateAncestorGesturePrecedence];
}

- (void)updateAncestorGesturePrecedence
{

  // Keep this nested horizontal scroller ahead of any ancestor pager. Without
  // this precedence, the Discovery pager can consume the category drag.
  for (UIView *ancestor = self.superview; ancestor != nil; ancestor = ancestor.superview) {
    if ([ancestor isKindOfClass:UIScrollView.class]) {
      UIScrollView *scrollView = (UIScrollView *)ancestor;
      if (scrollView.pagingEnabled || scrollView.alwaysBounceHorizontal) {
        [scrollView.panGestureRecognizer
          requireGestureRecognizerToFail:_tabsScrollView.panGestureRecognizer];
      }
    }
  }
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
  _shouldCenterSelectedItem = NO;
  _scrollStartOffsetX = scrollView.contentOffset.x;
  RNCLogNativeTabScrollBoundary(@"secondary", @"begin", scrollView, _scrollStartOffsetX);
}

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView
                  willDecelerate:(BOOL)decelerate
{
  if (decelerate) return;
  RNCLogNativeTabScrollBoundary(@"secondary", @"end", scrollView, _scrollStartOffsetX);
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
  RNCLogNativeTabScrollBoundary(@"secondary", @"end", scrollView, _scrollStartOffsetX);
}

- (UIFont *)fontWithSize:(CGFloat)size
{
  if (_fontFamily.length > 0) {
    UIFont *font = [UIFont fontWithName:_fontFamily size:size];
    if (font != nil) return font;
  }
  return [UIFont systemFontOfSize:size weight:UIFontWeightMedium];
}

- (void)updateConfigJSON:(NSString *)configJSON
{
  NSData *data = [configJSON dataUsingEncoding:NSUTF8StringEncoding];
  id parsed = data == nil ? nil : [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
  NSDictionary *config = [parsed isKindOfClass:NSDictionary.class] ? parsed : @{};
  NSArray *items = [config[@"items"] isKindOfClass:NSArray.class] ? config[@"items"] : @[];
  NSString *selectedKey = [config[@"selectedKey"] isKindOfClass:NSString.class]
    ? config[@"selectedKey"]
    : @"";
  NSDictionary *columns = [config[@"columns"] isKindOfClass:NSDictionary.class]
    ? config[@"columns"]
    : @{};
  NSDictionary *style = [config[@"style"] isKindOfClass:NSDictionary.class]
    ? config[@"style"]
    : @{};

  BOOL itemsChanged = ![_items isEqualToArray:items];
  BOOL selectedKeyChanged = ![_selectedKey isEqualToString:selectedKey];
  _items = items;
  _selectedKey = [selectedKey copy];
  if (itemsChanged || selectedKeyChanged) _shouldCenterSelectedItem = YES;
  _configuredHeight = MAX(1, [style[@"height"] doubleValue] ?: 74);
  _tabsHeight = MAX(0, [style[@"tabsHeight"] doubleValue] ?: 42);
  _contentPaddingHorizontal = MAX(
    0,
    [style[@"contentPaddingHorizontal"] doubleValue] ?: 20
  );
  _itemSpacing = MAX(0, [style[@"itemSpacing"] doubleValue] ?: 8);
  _fontSize = MAX(1, [style[@"fontSize"] doubleValue] ?: 14);
  _columnFontSize = MAX(1, [style[@"columnFontSize"] doubleValue] ?: 12);
  _trailingColumnWidth = MAX(0, [style[@"trailingColumnWidth"] doubleValue] ?: 80);
  _columnGap = MAX(0, [style[@"columnGap"] doubleValue] ?: 8);

  _leadingColumnLabel.text = [columns[@"leading"] isKindOfClass:NSString.class]
    ? columns[@"leading"]
    : @"";
  _middleColumnLabel.text = [columns[@"middle"] isKindOfClass:NSString.class]
    ? columns[@"middle"]
    : @"";
  _trailingColumnLabel.text = [columns[@"trailing"] isKindOfClass:NSString.class]
    ? columns[@"trailing"]
    : @"";

  if (itemsChanged) {
    for (UIButton *button in _buttons) [button removeFromSuperview];
    [_buttons removeAllObjects];
    [_items enumerateObjectsUsingBlock:^(NSDictionary *item, NSUInteger index, BOOL *stop) {
      UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
      button.tag = (NSInteger)index;
      button.titleLabel.lineBreakMode = NSLineBreakByTruncatingTail;
      NSString *title = [item[@"title"] isKindOfClass:NSString.class] ? item[@"title"] : @"";
      [button setTitle:title forState:UIControlStateNormal];
      [button addTarget:self action:@selector(handleItemPress:) forControlEvents:UIControlEventTouchUpInside];
      button.accessibilityLabel = [item[@"accessibilityLabel"] isKindOfClass:NSString.class]
        ? item[@"accessibilityLabel"]
        : title;
      button.accessibilityIdentifier = [item[@"testID"] isKindOfClass:NSString.class]
        ? item[@"testID"]
        : nil;
      [self->_tabsScrollView addSubview:button];
      [self->_buttons addObject:button];
    }];
  }

  self.hidden = _items.count == 0;
  [self setNeedsLayout];
}

- (void)updateColorsWithBackgroundColor:(UIColor *)backgroundColor
                        activeTextColor:(UIColor *)activeTextColor
                      inactiveTextColor:(UIColor *)inactiveTextColor
                selectedBackgroundColor:(UIColor *)selectedBackgroundColor
                              fontFamily:(NSString *)fontFamily
{
  self.backgroundColor = backgroundColor ?: UIColor.clearColor;
  _tabsScrollView.backgroundColor = self.backgroundColor;
  _columnsView.backgroundColor = self.backgroundColor;
  _activeTextColor = activeTextColor ?: UIColor.labelColor;
  _inactiveTextColor = inactiveTextColor ?: UIColor.secondaryLabelColor;
  _selectedBackgroundColor = selectedBackgroundColor ?: UIColor.secondarySystemFillColor;
  _fontFamily = [fontFamily copy];
  [self setNeedsLayout];
}

- (void)setLeftToRight:(BOOL)leftToRight
{
  if (_leftToRight == leftToRight) return;
  _leftToRight = leftToRight;
  [self setNeedsLayout];
}

- (void)handleItemPress:(UIButton *)button
{
  NSInteger index = button.tag;
  if (index < 0 || index >= (NSInteger)_items.count) return;
  NSDictionary *item = _items[index];
  NSString *key = [item[@"key"] isKindOfClass:NSString.class] ? item[@"key"] : @"";
  if (self.onItemPress) self.onItemPress(index, key);
}

- (void)layoutSubviews
{
  [super layoutSubviews];
  CGFloat width = CGRectGetWidth(self.bounds);
  CGFloat height = CGRectGetHeight(self.bounds);
  CGFloat tabsHeight = MIN(height, _tabsHeight);
  _tabsScrollView.frame = CGRectMake(0, 0, width, tabsHeight);
  _columnsView.frame = CGRectMake(0, tabsHeight, width, MAX(0, height - tabsHeight));

  UIFont *itemFont = [self fontWithSize:_fontSize];
  CGFloat itemsWidth = 0;
  NSMutableArray<NSNumber *> *widths = [NSMutableArray arrayWithCapacity:_buttons.count];
  for (UIButton *button in _buttons) {
    button.titleLabel.font = itemFont;
    NSString *title = [button titleForState:UIControlStateNormal] ?: @"";
    CGFloat itemWidth = MAX(44, ceil([title sizeWithAttributes:@{NSFontAttributeName: itemFont}].width) + 20);
    [widths addObject:@(itemWidth)];
    itemsWidth += itemWidth;
  }
  if (_buttons.count > 1) itemsWidth += (_buttons.count - 1) * _itemSpacing;
  CGFloat contentWidth = MAX(width, itemsWidth + _contentPaddingHorizontal * 2);
  _tabsScrollView.contentSize = CGSizeMake(contentWidth, tabsHeight);
  __block CGFloat x = _leftToRight
    ? _contentPaddingHorizontal
    : contentWidth - _contentPaddingHorizontal;
  __block UIButton *selectedButton = nil;
  [_buttons enumerateObjectsUsingBlock:^(UIButton *button, NSUInteger index, BOOL *stop) {
    CGFloat itemWidth = widths[index].doubleValue;
    if (!self->_leftToRight) x -= itemWidth;
    button.frame = CGRectMake(x, MAX(0, (tabsHeight - 32) / 2), itemWidth, MIN(32, tabsHeight));
    NSDictionary *item = self->_items[index];
    NSString *key = [item[@"key"] isKindOfClass:NSString.class] ? item[@"key"] : @"";
    BOOL selected = [key isEqualToString:self->_selectedKey];
    button.backgroundColor = selected ? self->_selectedBackgroundColor : UIColor.clearColor;
    // OneKey patch: match the pill shape of the React category filters.
    // button.layer.cornerRadius = 10;
    button.layer.cornerRadius = CGRectGetHeight(button.bounds) / 2;
    [button setTitleColor:selected ? self->_activeTextColor : self->_inactiveTextColor
                 forState:UIControlStateNormal];
    button.accessibilityTraits = selected
      ? UIAccessibilityTraitButton | UIAccessibilityTraitSelected
      : UIAccessibilityTraitButton;
    if (selected) selectedButton = button;
    if (self->_leftToRight) {
      x += itemWidth + self->_itemSpacing;
    } else {
      x -= self->_itemSpacing;
    }
  }];

  UIFont *columnFont = [self fontWithSize:_columnFontSize];
  for (UILabel *label in @[_leadingColumnLabel, _middleColumnLabel, _trailingColumnLabel]) {
    label.font = columnFont;
    label.textColor = _inactiveTextColor;
  }
  CGFloat columnHeight = CGRectGetHeight(_columnsView.bounds);
  CGFloat half = width / 2;
  CGFloat trailingX = width - _contentPaddingHorizontal - _trailingColumnWidth;
  if (_leftToRight) {
    _leadingColumnLabel.textAlignment = NSTextAlignmentLeft;
    _middleColumnLabel.textAlignment = NSTextAlignmentRight;
    _trailingColumnLabel.textAlignment = NSTextAlignmentRight;
    _leadingColumnLabel.frame = CGRectMake(
      _contentPaddingHorizontal,
      0,
      MAX(0, half - _contentPaddingHorizontal),
      columnHeight
    );
    _middleColumnLabel.frame = CGRectMake(
      half,
      0,
      MAX(0, trailingX - _columnGap - half),
      columnHeight
    );
    _trailingColumnLabel.frame = CGRectMake(
      trailingX,
      0,
      _trailingColumnWidth,
      columnHeight
    );
  } else {
    _leadingColumnLabel.textAlignment = NSTextAlignmentRight;
    _middleColumnLabel.textAlignment = NSTextAlignmentLeft;
    _trailingColumnLabel.textAlignment = NSTextAlignmentLeft;
    _leadingColumnLabel.frame = CGRectMake(
      half,
      0,
      MAX(0, width - _contentPaddingHorizontal - half),
      columnHeight
    );
    _middleColumnLabel.frame = CGRectMake(
      _contentPaddingHorizontal + _trailingColumnWidth + _columnGap,
      0,
      MAX(0, half - _contentPaddingHorizontal - _trailingColumnWidth - _columnGap),
      columnHeight
    );
    _trailingColumnLabel.frame = CGRectMake(
      _contentPaddingHorizontal,
      0,
      _trailingColumnWidth,
      columnHeight
    );
  }

  if (_shouldCenterSelectedItem && selectedButton != nil &&
      !_tabsScrollView.tracking && !_tabsScrollView.dragging &&
      !_tabsScrollView.decelerating) {
    CGFloat maximumOffset = MAX(0, contentWidth - width);
    CGFloat desiredOffset = RNCClamp(
      CGRectGetMidX(selectedButton.frame) - width / 2,
      0,
      maximumOffset
    );
    [_tabsScrollView setContentOffset:CGPointMake(desiredOffset, 0) animated:NO];
    _shouldCenterSelectedItem = NO;
  }
}

@end

@interface RNCCollapsiblePagerSharedHeaderView : UIView
@end

@implementation RNCCollapsiblePagerSharedHeaderView

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event
{
  if (self.hidden || !self.userInteractionEnabled || self.alpha <= 0.01) return nil;
  // A pinned sticky child can be translated below this host's original bounds.
  // Keep it interactive while the host itself scrolls above the viewport.
  for (UIView *subview in self.subviews.reverseObjectEnumerator) {
    if (subview.hidden || subview.alpha <= 0.01 || !subview.userInteractionEnabled) continue;
    CGPoint childPoint = [subview convertPoint:point fromView:self];
    UIView *hitView = [subview hitTest:childPoint withEvent:event];
    if (hitView != nil) return hitView;
  }
  return nil;
}

@end

@interface RNCCollapsiblePagerDirectScrollView : UIScrollView
@property (nonatomic, weak) UIView *excludedHeaderView;
@end

@implementation RNCCollapsiblePagerDirectScrollView

- (BOOL)gestureRecognizerShouldBegin:(UIGestureRecognizer *)gestureRecognizer
{
  if (gestureRecognizer == self.panGestureRecognizer) {
    UIPanGestureRecognizer *pan = self.panGestureRecognizer;
    CGPoint translation = [pan translationInView:self];
    CGPoint velocity = [pan velocityInView:self];
    CGPoint intent = fabs(translation.x) + fabs(translation.y) >= 1
      ? translation
      : velocity;
    BOOL hasMotion = MAX(fabs(intent.x), fabs(intent.y)) >= 1;
    if (hasMotion && fabs(intent.y) > fabs(intent.x)) {
      RNCCollapsiblePagerLog([NSString stringWithFormat:
        @"pager-axis dx=%.2f dy=%.2f result=vertical-rejected",
        intent.x,
        intent.y]);
      return NO;
    }
    UIView *target = [self hitTest:[pan locationInView:self] withEvent:nil];
    for (UIView *ancestor = target; ancestor != nil && ancestor != self; ancestor = ancestor.superview) {
      if (![ancestor isKindOfClass:UIScrollView.class]) continue;
      UIScrollView *inner = (UIScrollView *)ancestor;
      CGFloat maximum = MAX(0, inner.contentSize.width - CGRectGetWidth(inner.bounds));
      BOOL canScroll = maximum > 0 && (intent.x < 0
        ? inner.contentOffset.x < maximum - 0.5 : inner.contentOffset.x > 0.5);
      if (canScroll) {
        return NO;
      }
    }
  }
  if (gestureRecognizer == self.panGestureRecognizer && self.excludedHeaderView != nil) {
    CGPoint point = [gestureRecognizer locationInView:self.excludedHeaderView];
    if ([self.excludedHeaderView hitTest:point withEvent:nil] != nil) {
      RNCCollapsiblePagerLog(@"pager-header-excluded result=1");
      return NO;
    }
  }
  BOOL result = [super gestureRecognizerShouldBegin:gestureRecognizer];
  return result;
}

@end

@interface RNCCollapsiblePagerPressCancellationPanGestureRecognizer : UIPanGestureRecognizer
@end

@implementation RNCCollapsiblePagerPressCancellationPanGestureRecognizer

- (BOOL)canPreventGestureRecognizer:(UIGestureRecognizer *)preventedGestureRecognizer
{
  return [preventedGestureRecognizer isKindOfClass:RCTSurfaceTouchHandler.class] ||
    [preventedGestureRecognizer isKindOfClass:RCTTouchHandler.class];
}

@end

// This observer never competes for scrolling. RN presses are terminated when
// the pager pan begins or the existing guard resolves horizontal intent.
@interface RNCCollapsiblePagerContentPressCancellationGestureRecognizer : UIGestureRecognizer
@property (nonatomic, weak) UIGestureRecognizer *reactTouchHandler;
- (void)cancelPressForPan:(UIPanGestureRecognizer *)pan;
- (void)cancelPressForHorizontalTouch:(UITouch *)touch event:(UIEvent *)event;
- (void)observeIncomingTouch:(UITouch *)touch;
@end

@implementation RNCCollapsiblePagerContentPressCancellationGestureRecognizer {
  __weak UITouch *_trackedTouch;
  UIEvent *_touchEvent;
  BOOL _multipleSurfaceTouchesObserved;
  CGPoint _trackedTouchStartPoint;
  BOOL _didCancelReactPressForVerticalMovement;
  BOOL _didCancelReactPressForHorizontalMovement;
}

- (BOOL)canPreventGestureRecognizer:(UIGestureRecognizer *)other
{
  return other == self.reactTouchHandler;
}

- (void)observeIncomingTouch:(UITouch *)touch
{
  if (_trackedTouch != nil && touch != _trackedTouch &&
      [touch.view isDescendantOfView:self.reactTouchHandler.view]) {
    _multipleSurfaceTouchesObserved = YES;
  }
}

- (void)recordEvent:(UIEvent *)event
{
  _touchEvent = event;
  NSUInteger count = 0;
  for (UITouch *touch in event.allTouches) {
    if (touch.phase != UITouchPhaseEnded && touch.phase != UITouchPhaseCancelled &&
        [touch.view isDescendantOfView:self.reactTouchHandler.view]) ++count;
  }
  if (count > 1) _multipleSurfaceTouchesObserved = YES;
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  if (_trackedTouch != nil || touches.count != 1) {
    _multipleSurfaceTouchesObserved = YES;
    if (self.state == UIGestureRecognizerStatePossible) {
      self.state = UIGestureRecognizerStateFailed;
    }
    return;
  }
  _trackedTouch = touches.anyObject;
  _trackedTouchStartPoint = [_trackedTouch locationInView:self.view];
  _didCancelReactPressForVerticalMovement = NO;
  _didCancelReactPressForHorizontalMovement = NO;
  [self recordEvent:event];
}

- (void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  [self recordEvent:event];
  UITouch *touch = _trackedTouch;
  UIGestureRecognizer *handler = self.reactTouchHandler;
  if (touch == nil || ![touches containsObject:touch] ||
      self.state != UIGestureRecognizerStatePossible ||
      _didCancelReactPressForVerticalMovement || _multipleSurfaceTouchesObserved ||
      handler.view == nil || self.view != handler.view.superview ||
      handler.state == UIGestureRecognizerStateFailed ||
      handler.state == UIGestureRecognizerStateCancelled ||
      handler.state == UIGestureRecognizerStateEnded) return;
  CGPoint point = [touch locationInView:self.view];
  CGFloat dx = point.x - _trackedTouchStartPoint.x;
  CGFloat dy = point.y - _trackedTouchStartPoint.y;
  if (fabs(dy) < 10.0 || fabs(dy) <= fabs(dx)) return;
  NSUInteger activeTouches = 0;
  for (UITouch *activeTouch in event.allTouches) {
    if (activeTouch.phase == UITouchPhaseEnded ||
        activeTouch.phase == UITouchPhaseCancelled) continue;
    if ([activeTouch.view isDescendantOfView:handler.view]) ++activeTouches;
  }
  if (activeTouches != 1) return;
  _didCancelReactPressForVerticalMovement = YES;
  handler.enabled = NO;
  handler.enabled = YES;
}

- (void)cancelPressForHorizontalTouch:(UITouch *)touch event:(UIEvent *)event
{
  [self recordEvent:event];
  UIGestureRecognizer *handler = self.reactTouchHandler;
  BOOL eligible = touch != nil && touch == _trackedTouch &&
    touch.phase != UITouchPhaseEnded && touch.phase != UITouchPhaseCancelled &&
    self.state == UIGestureRecognizerStatePossible &&
    !_multipleSurfaceTouchesObserved && !_didCancelReactPressForHorizontalMovement &&
    handler.view != nil && self.view == handler.view.superview &&
    (handler.state == UIGestureRecognizerStateBegan || handler.state == UIGestureRecognizerStateChanged);
  NSUInteger activeTouches = 0;
  for (UITouch *activeTouch in event.allTouches) {
    if (activeTouch.phase == UITouchPhaseEnded || activeTouch.phase == UITouchPhaseCancelled) continue;
    if ([activeTouch.view isDescendantOfView:handler.view]) ++activeTouches;
  }
  if (!eligible || activeTouches != 1) return;
  // A coalesced move can resolve horizontal intent before the pager pan begins.
  _didCancelReactPressForHorizontalMovement = YES;
#if DEBUG
  RNCCollapsiblePagerLog(@"content-press-cancel axis=horizontal");
#endif
  handler.enabled = NO;
  handler.enabled = YES;
}

- (void)cancelPressForPan:(UIPanGestureRecognizer *)pan
{
  UIGestureRecognizer *handler = self.reactTouchHandler;
  UITouch *touch = _trackedTouch;
  if (self.state != UIGestureRecognizerStatePossible || _multipleSurfaceTouchesObserved ||
      pan.state != UIGestureRecognizerStateBegan || touch == nil ||
      touch.phase == UITouchPhaseEnded || touch.phase == UITouchPhaseCancelled ||
      handler.view == nil || self.view != handler.view.superview ||
      handler.state == UIGestureRecognizerStateFailed ||
      handler.state == UIGestureRecognizerStateCancelled ||
      handler.state == UIGestureRecognizerStateEnded) return;

  // RN cancellation belongs to a Surface, not one finger. Do not terminate an
  // unrelated, already active touch on that Surface during a multi-touch sequence.
  NSUInteger activeTouches = 0;
  for (UITouch *activeTouch in _touchEvent.allTouches) {
    if (activeTouch.phase == UITouchPhaseEnded ||
        activeTouch.phase == UITouchPhaseCancelled) continue;
    if ([activeTouch.view isDescendantOfView:handler.view]) ++activeTouches;
  }
  if (activeTouches != 1) return;
  self.state = UIGestureRecognizerStateBegan;
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  if (![touches containsObject:_trackedTouch]) return;
  self.state = self.state == UIGestureRecognizerStateBegan
    ? UIGestureRecognizerStateEnded : UIGestureRecognizerStateFailed;
  _trackedTouch = nil;
  _touchEvent = nil;
  _didCancelReactPressForVerticalMovement = NO;
  _didCancelReactPressForHorizontalMovement = NO;
}

- (void)touchesCancelled:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  if (![touches containsObject:_trackedTouch]) return;
  self.state = self.state == UIGestureRecognizerStateBegan
    ? UIGestureRecognizerStateCancelled : UIGestureRecognizerStateFailed;
  _trackedTouch = nil;
  _touchEvent = nil;
  _didCancelReactPressForVerticalMovement = NO;
  _didCancelReactPressForHorizontalMovement = NO;
}

- (void)reset
{
  [super reset];
  _trackedTouch = nil;
  _touchEvent = nil;
  _multipleSurfaceTouchesObserved = NO;
  _didCancelReactPressForVerticalMovement = NO;
  _didCancelReactPressForHorizontalMovement = NO;
}

@end

@interface RNCCollapsiblePagerOuterPagerPanGestureRecognizer : UIPanGestureRecognizer
@property (nonatomic, strong) NSHashTable<UIGestureRecognizer *> *blockedPagerGestures;
@property (nonatomic, weak) UITouch *trackedTouch;
@property (nonatomic, assign) BOOL protectsNestedBody;
@end

@implementation RNCCollapsiblePagerOuterPagerPanGestureRecognizer

- (BOOL)canPreventGestureRecognizer:(UIGestureRecognizer *)preventedGestureRecognizer
{
  return [_blockedPagerGestures containsObject:preventedGestureRecognizer];
}

- (BOOL)canBePreventedByGestureRecognizer:(UIGestureRecognizer *)preventingGestureRecognizer
{
  UIView *view = preventingGestureRecognizer.view;
  if ([view isKindOfClass:UIScrollView.class] &&
      [view isDescendantOfView:self.view]) {
    UIScrollView *scrollView = (UIScrollView *)view;
    BOOL horizontal = scrollView.alwaysBounceHorizontal ||
      scrollView.contentSize.width > CGRectGetWidth(scrollView.bounds);
    if (horizontal && preventingGestureRecognizer == scrollView.panGestureRecognizer) {
      return YES;
    }
  }
  return NO;
}

- (void)reset
{
  [super reset];
  self.trackedTouch = nil;
  self.protectsNestedBody = NO;
  self.cancelsTouchesInView = YES;
}

@end

@interface RNCCollapsiblePagerVerticalPagerGuardGestureRecognizer : UIGestureRecognizer
@property (nonatomic, weak) UIPanGestureRecognizer *pagerPan;
@property (nonatomic, weak) RNCCollapsiblePagerContentPressCancellationGestureRecognizer *contentPressCancellation;
- (CGPoint)translationInView:(UIView *)view;
- (CGPoint)velocityInView:(UIView *)view;
@end

@implementation RNCCollapsiblePagerVerticalPagerGuardGestureRecognizer {
  __weak UITouch *_trackedTouch;
  NSMapTable<UIPanGestureRecognizer *, NSNumber *> *_heldPans;
  CGPoint _startPoint;
  CGPoint _lastPoint;
  CGPoint _velocity;
  NSTimeInterval _lastTimestamp;
}

- (void)holdPan:(UIPanGestureRecognizer *)pan
{
  if (pan == nil || [_heldPans objectForKey:pan] != nil) return;
  if (_heldPans == nil) _heldPans = [NSMapTable weakToStrongObjectsMapTable];
  [_heldPans setObject:@(pan.minimumNumberOfTouches) forKey:pan];
  // A failure dependency alone lets UIKit's pager pan cancel a descendant pan
  // before intent resolves. Keep DOWN tracked while deferring one-finger begin.
  pan.minimumNumberOfTouches = MAX(2, pan.minimumNumberOfTouches);
}

- (void)restoreHeldPans
{
  for (UIPanGestureRecognizer *pan in _heldPans.keyEnumerator) {
    pan.minimumNumberOfTouches = [_heldPans objectForKey:pan].unsignedIntegerValue;
    UIScrollView *scroll = [pan.view isKindOfClass:UIScrollView.class] ? (UIScrollView *)pan.view : nil;
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"gesture-restore pan=%@ min-touches=%lu offset=%@",
      NSStringFromClass(pan.view.class), (unsigned long)pan.minimumNumberOfTouches,
      scroll != nil ? NSStringFromCGPoint(scroll.contentOffset) : @"none"]);
  }
  [_heldPans removeAllObjects];
}

- (CGPoint)translationInView:(UIView *)view
{
  CGPoint start = [view convertPoint:_startPoint fromView:self.view];
  CGPoint last = [view convertPoint:_lastPoint fromView:self.view];
  return CGPointMake(last.x - start.x, last.y - start.y);
}

- (CGPoint)velocityInView:(UIView *)view { return _velocity; }

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  if (_trackedTouch != nil || touches.count != 1) {
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"gesture-second-touch previous-state=%ld incoming=%lu held-pans=%lu",
      (long)self.state, (unsigned long)touches.count, (unsigned long)_heldPans.count]);
    [self restoreHeldPans];
    if (self.state == UIGestureRecognizerStatePossible) {
      self.state = UIGestureRecognizerStateFailed;
    } else if (self.state == UIGestureRecognizerStateBegan || self.state == UIGestureRecognizerStateChanged) {
      self.state = UIGestureRecognizerStateCancelled;
    }
    return;
  }
  _trackedTouch = touches.anyObject;
  _startPoint = _lastPoint = [touches.anyObject locationInView:self.view];
  _lastTimestamp = event.timestamp;
  _velocity = CGPointZero;
  [self holdPan:self.pagerPan];
  for (UIView *view = _trackedTouch.view; view != nil && view != self.view; view = view.superview) {
    if (![view isKindOfClass:UIScrollView.class]) continue;
    UIScrollView *scroll = (UIScrollView *)view;
    if (RNCIsEnabledHorizontalScrollOwner(scroll)) {
      RNCCollapsiblePagerLog([NSString stringWithFormat:
        @"gesture-hold inner=%@ paging=%d offset=%@ content=%@ bounds=%@",
        NSStringFromClass(scroll.class), scroll.pagingEnabled, NSStringFromCGPoint(scroll.contentOffset),
        NSStringFromCGSize(scroll.contentSize), NSStringFromCGSize(scroll.bounds.size)]);
      [self holdPan:scroll.panGestureRecognizer];
    }
  }
}

- (void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  CGPoint point = [touches.anyObject locationInView:self.view];
  NSTimeInterval elapsed = event.timestamp - _lastTimestamp;
  if (elapsed > 0) {
    _velocity = CGPointMake((point.x - _lastPoint.x) / elapsed,
                           (point.y - _lastPoint.y) / elapsed);
  }
  _lastPoint = point;
  _lastTimestamp = event.timestamp;
  if (self.state == UIGestureRecognizerStatePossible) {
    CGFloat dx = fabs(point.x - _startPoint.x), dy = fabs(point.y - _startPoint.y);
    // Keep intent pending until the explicit distance decision, instead of
    // relying on UIPanGestureRecognizer's earlier recognition.
    if ((dy >= 10 && dy > dx) || (dx >= 32 && dx > dy)) {
      RNCCollapsiblePagerLog([NSString stringWithFormat:
        @"gesture-intent axis=%@ dx=%.2f dy=%.2f held-pans=%lu",
        dy > dx ? @"vertical" : @"horizontal", dx, dy, (unsigned long)_heldPans.count]);
      if (dx >= 32 && dx > dy) {
        [self.contentPressCancellation cancelPressForHorizontalTouch:_trackedTouch event:event];
      }
      self.state = UIGestureRecognizerStateBegan;
      if (self.state == UIGestureRecognizerStateFailed) [self restoreHeldPans];
    }
  } else if (self.state == UIGestureRecognizerStateBegan || self.state == UIGestureRecognizerStateChanged) {
    self.state = UIGestureRecognizerStateChanged;
  }
}

- (void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  [self restoreHeldPans];
  self.state = self.state == UIGestureRecognizerStatePossible
    ? UIGestureRecognizerStateFailed : UIGestureRecognizerStateEnded;
}

- (void)touchesCancelled:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event
{
  [self restoreHeldPans];
  self.state = UIGestureRecognizerStateCancelled;
}

- (void)reset
{
  [self restoreHeldPans];
  [super reset];
  _trackedTouch = nil;
  _velocity = CGPointZero;
}

- (BOOL)canBePreventedByGestureRecognizer:(UIGestureRecognizer *)other { return NO; }
- (BOOL)canPreventGestureRecognizer:(UIGestureRecognizer *)other { return YES; }
@end

@interface RNCCollapsiblePagerViewComponentView () <
  RCTRNCCollapsiblePagerViewViewProtocol,
  UIPageViewControllerDataSource,
  UIPageViewControllerDelegate,
  UIScrollViewDelegate,
  UIGestureRecognizerDelegate,
  RCTMountingTransactionObserving,
  RNCNativeScrollerInsetCoordinator,
  RNCPagerViewNestedOverlayObserver
>
- (void)finishPagerScrollEmittingSelection:(BOOL)emitSelection;
- (void)completeTransitionOnNextRunLoopForGeneration:(NSUInteger)generation
                                         transition:(NSUInteger)transition;
- (void)setDirectNativePagerEnabled:(BOOL)enabled;
- (void)attachSharedHeadersToScrollView:(UIScrollView *)scrollView;
- (void)liftSharedHeadersForPagerTransition:(NSString *)reason;
- (void)restoreSharedHeadersToContainer;
- (void)layoutSharedHeaders;
- (void)restorePinnedStickyHeader;
- (void)updatePinnedStickyHeader;
- (UIScrollView *)currentVerticalScrollViewForParentPager;
- (UIScrollView *)verticalScrollViewForPageAtIndex:(NSInteger)pageIndex;
- (RNCScrollCandidateResolution)resolveScrollCandidateForPageAtIndex:(NSInteger)pageIndex;
- (RNCScrollCandidateResolution)resolveScrollCandidateInView:(UIView *)view;
- (UIScrollView *)findExplicitNativeScrollerInView:(UIView *)view;
- (UIScrollView *)findFabricScrollViewInView:(UIView *)view;
- (UIScrollView *)findVerticalScrollViewInView:(UIView *)view;
- (void)scheduleCoalescedScrollReconcile;
- (void)scheduleScrollObserverRetryForCurrentPage;
- (void)preparePageForHorizontalTransitionAtIndex:(NSInteger)pageIndex;
- (void)updateSharedHeaderPressCancellationGesture;
- (void)detachSharedHeaderPressCancellationGesture;
- (void)updateRefreshControlForScrollView:(UIScrollView *)scrollView;
- (void)releaseRefreshControlForScrollView:(UIScrollView *)scrollView;
- (void)logHomeVisualGeometry:(NSString *)phase scrollView:(UIScrollView *)scrollView;
- (void)cancelTopInsetReboundCheck;
- (void)scheduleTopInsetReboundCheck;
- (void)syncScrollEdgeAppearance;
- (void)updateHardEdgeSeparatorCovers;
- (void)updateTopOverlayThemeBackground;
- (void)updateScrollEdgeContentMasks;
- (void)restoreScrollEdgeContentMasks;
- (void)sharedHeaderPressCancellationGestureChanged:(UIPanGestureRecognizer *)recognizer;
- (void)outerPagerGestureChanged:(UIPanGestureRecognizer *)recognizer;
- (void)verticalPagerGuardGestureChanged:(UIPanGestureRecognizer *)recognizer;
- (void)connectVerticalPagerGuard;
- (void)observedListPanChanged:(UIPanGestureRecognizer *)recognizer;
- (BOOL)resolveExternalTopOverlay;
- (void)registerExternalOuterPager;
- (CGFloat)effectiveTopOverlayHeight;
- (BOOL)hasFixedTopOverlay;
@end

@implementation RNCCollapsiblePagerViewComponentView {
  UIView *_containerView;
  RNCCollapsiblePagerSharedHeaderView *_sharedHeaderHostView;
  UIView *_pinnedStickyHostView;
  BOOL _stickyHeaderIsPinned;
  UIView *_sharedHeaderMaskView;
  UIView *_originalSharedHeaderMaskView;
  NSMapTable<UIView *, id> *_originalPageMasks;
  NSMapTable<UIView *, UIView *> *_ownedPageMasks;
  CGFloat _pageMaskBoundary;
  UIPageViewController *_pageViewController;
  UIScrollView *_pageViewControllerScrollView;
  UIScrollView *_pagerScrollView;
  NSMutableArray<UIView<RCTComponentViewProtocol> *> *_logicalChildren;
  NSMutableArray<UIViewController *> *_pageControllers;
  UIView *_headerView;
  UIView *_stickyHeaderView;
  UIView *_topOverlayView;
  UIView *_topOverlayHostView;
  UIScrollEdgeElementContainerInteraction *_topEdgeInteraction;
  UIScrollEdgeElementContainerInteraction *_stickyEdgeInteraction;
  UILabel *_topEdgeMarker;
  UILabel *_stickyEdgeMarker;
  CALayer *_topEdgeSeparatorCover;
  CALayer *_stickyEdgeSeparatorCover;
  CALayer *_topOverlayThemeBackgroundLayer;
  __weak UIView *_topEdgeInteractionHost;
  __weak UIView *_stickyEdgeInteractionHost;
  __weak UIScrollView *_topEdgeScrollView;
  UIScrollEdgeEffectStyle *_originalTopEdgeStyle;
  RNCCollapsiblePagerNativeTabBarView *_nativeTabBarView;
  RNCCollapsiblePagerNativeSubHeaderView *_nativeSubHeaderView;
  NSInteger _currentIndex;
  NSInteger _destinationIndex;
  NSInteger _pendingInitialPage;
  NSInteger _headerHeight;
  NSInteger _stickyHeaderHeight;
  CGFloat _topOverlayHeight;
  NSInteger _externalTopOverlayTag;
  __weak UIView *_externalTopOverlayView;
  CGFloat _externalTopOverlayHeight;
  __weak RNCPagerViewComponentView *_externalOuterPager;
  BOOL _externalOuterPageActive;
  BOOL _externalOuterTransitioning;
  BOOL _topScrollEdgeEnabled;
  RNCCollapsiblePagerViewTopScrollEdgeEffect _topScrollEdgeEffect;
  NSString *_topOverlayColorScheme;
  BOOL _pendingTopOverlayAppearanceCommit;
  BOOL _applyingTopOverlayInsetChange;
  BOOL _correctingTopInsetRebound;
  __weak UIScrollView *_pendingTopInsetReboundScrollView;
  NSString *_pendingTopInsetReboundPageKey;
  NSUInteger _pendingTopInsetReboundGeneration;
  NSTimer *_topInsetReboundTimer;
  CGFloat _nativeTabBarHeight;
  CGFloat _nativeSubHeaderHeight;
  CGFloat _headerOffset;
  BOOL _scrollEnabled;
  // OneKey patch: Coordinate this inner pager with an outer horizontal pager.
  BOOL _nestedScrollEnabled;
  BOOL _nativeSmoothHeaderScrollEnabled;
  UIPanGestureRecognizer *_blockerGesture;
  BOOL _transitioning;
  BOOL _isPagerDragging;
  BOOL _directNativePagerEnabled;
  BOOL _pendingDirectNativePagerEnabled;
  BOOL _hasPendingDirectNativePagerChange;
  BOOL _hasReceivedPageCommand;
  NSUInteger _transitionId;
  // OneKey patch: Retain the latest tab tap while an animation is in flight.
  NSInteger _pendingGoToIndex;
  BOOL _pendingGoToAnimated;
  BOOL _needsSlotRebuild;
  BOOL _deferTopEdgeSlotRebuild;
  BOOL _hasAppliedInitialPage;
  BOOL _needsPropsReapply;
  NSString *_layoutDirection;
  NSArray<NSString *> *_pageKeys;
  NSString *_retainedPages;
  NSMutableDictionary<NSString *, NSNumber *> *_pageOffsets;
  NSMutableSet<NSString *> *_commandBodyKeys;
  __weak UIScrollView *_pagerTopScrollView;
  NSString *_pagerTopPageKey;
  NSUInteger _pagerTopGeneration;
  NSInteger _pagerTopPageIndex;
  CGFloat _pagerTopLogicalTarget;
  BOOL _dispatchingPagerTop;
  NSUInteger _pagerTopToken;
  NSMutableSet<NSString *> *_alignedBodyKeys;
  NSMutableSet<NSString *> *_suspendedBodyKeys;
  CGFloat _transitionHeaderOffset;
  CGFloat _transitionSourceDisplayBody;
  NSMutableDictionary<NSString *, NSNumber *> *_displayBodyOverrides;
  NSMapTable<UIScrollView *, NSValue *> *_originalInsets;
  NSMapTable<UIScrollView *, UIRefreshControl *> *_managedRefreshControls;
  NSHashTable<UIScrollView *> *_refreshControlObservedScrollViews;
  CFTimeInterval _homeVisualLastScrollLog;
  CFTimeInterval _homeVisualLastLayoutLog;
  CFTimeInterval _homeVisualStartMono;
  NSString *_homeVisualLastGeometrySignature;
  CGFloat _homeVisualLastThemeOpacity;
  CFTimeInterval _homeVisualLastThemeLog;
  BOOL _homeVisualThemeHolding;
  NSMapTable<UIScrollView *, NSNumber *> *_appliedTopInsets;
  NSMapTable<UIScrollView *, NSValue *> *_originalIndicatorInsets;
  NSMapTable<UIScrollView *, NSNumber *> *_originalAlwaysBounceVertical;
  NSMapTable<UIScrollView *, NSNumber *> *_originalDirectionalLockEnabled;
  NSMapTable<UIScrollView *, NSNumber *> *_originalInsetAdjustmentBehavior;
  // OneKey patch: pair pre-mount release with recovery, including deferred mounts.
  NSMutableDictionary<NSString *, RNCCollapsiblePagerReleasedScrollState *> *_releasedPageScrollStates;
  BOOL _needsScrollObserverReattachAfterMount;
  NSHashTable<UIScrollView *> *_mountingRemovedScrollViews;
  __weak UIScrollView *_observedScrollView;
  NSString *_observedPageKey;
  NSInteger _observedPageIndex;
  NSUInteger _observedPageGeneration;
  NSDictionary<NSString *, NSNumber *> *_transitionPageOffsets;
  NSMutableIndexSet *_transitionCompensatedPages;
  NSInteger _transitionSourceIndex;
  BOOL _syntheticPageRestore;
  BOOL _headerSettledAtTop;
  __weak UIScrollView *_headerSettledOwner;
  NSString *_headerSettledKey;
  NSUInteger _headerSettledGeneration;
  NSString *_headerSettledGeometry;
  CFTimeInterval _headerSettledSince;
  BOOL _headerSettledWasBusy;
  NSTimer *_headerSettledTimer;
  __weak UIScrollView *_headerCommandOwner;

  BOOL _finishingPageStateTransition;
  NSMutableSet<NSString *> *_pendingPageRestoreKeys;
  __weak UIScrollView *_sharedHeaderScrollView;
  __weak UIScrollView *_pendingFallbackScrollView;
  __weak UIView *_sharedHeaderPressCancellationGestureHost;
  __weak UIGestureRecognizer *_reactTouchHandler;
  UIPanGestureRecognizer *_sharedHeaderPressCancellationGesture;
  RNCCollapsiblePagerContentPressCancellationGestureRecognizer *_contentPressCancellationGesture;
  RNCCollapsiblePagerOuterPagerPanGestureRecognizer *_sharedHeaderOuterPagerGesture;
  RNCCollapsiblePagerVerticalPagerGuardGestureRecognizer *_verticalPagerGesture;
  BOOL _observingContentOffset;
  BOOL _awaitingScrollOwnerForCurrentPage;
  BOOL _scrollReconcileScheduled;
  BOOL _scrollResolveRetryScheduled;
  CFTimeInterval _pendingFallbackScrollViewDetectedAt;
  NSInteger _scrollResolveRetryPageIndex;
  NSInteger _scrollResolveRetryAttempt;
  NSUInteger _scrollResolveRetryToken;
  CGFloat _currentLogicalOffset;
  CGFloat _observedListPanStartLogicalOffset;
  BOOL _sharedHeadersLiftedForPagerTransition;
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
    _commandBodyKeys = [NSMutableSet new];
    _alignedBodyKeys = [NSMutableSet new];
    _suspendedBodyKeys = [NSMutableSet new];
    _displayBodyOverrides = [NSMutableDictionary new];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(nativeListScrollCommandAccepted:)
      name:@"OneKeyNativeListScrollCommandAccepted" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(nativeScrollViewDidBecomeReadyNotification:)
      name:@"OneKeyNativeScrollViewDidBecomeReady" object:nil];
    _pendingPageRestoreKeys = [NSMutableSet new];
    _releasedPageScrollStates = [NSMutableDictionary new];
    _mountingRemovedScrollViews = [NSHashTable weakObjectsHashTable];
    _originalInsets = [NSMapTable weakToStrongObjectsMapTable];
    _managedRefreshControls = [NSMapTable weakToWeakObjectsMapTable];
    _refreshControlObservedScrollViews = [NSHashTable weakObjectsHashTable];
    _appliedTopInsets = [NSMapTable weakToStrongObjectsMapTable];
    _originalIndicatorInsets = [NSMapTable weakToStrongObjectsMapTable];
    _originalAlwaysBounceVertical = [NSMapTable weakToStrongObjectsMapTable];
    _originalDirectionalLockEnabled = [NSMapTable weakToStrongObjectsMapTable];
    _originalInsetAdjustmentBehavior = [NSMapTable weakToStrongObjectsMapTable];
    _originalPageMasks = [NSMapTable strongToStrongObjectsMapTable];
    _ownedPageMasks = [NSMapTable strongToStrongObjectsMapTable];
    _pageKeys = @[];
    _retainedPages = @"[]";
    _currentIndex = 0;
    _destinationIndex = 0;
    _pendingInitialPage = 0;
    _scrollEnabled = YES;
    _nativeTabBarHeight = 44;
    _nativeSubHeaderHeight = 74;
    _nestedScrollEnabled = NO;
    _nativeSmoothHeaderScrollEnabled = NO;
    _directNativePagerEnabled = NO;
    _pendingDirectNativePagerEnabled = NO;
    _hasPendingDirectNativePagerChange = NO;
    _pendingGoToIndex = -1;
    _pendingGoToAnimated = YES;
    _scrollResolveRetryPageIndex = NSNotFound;
    _hasAppliedInitialPage = NO;
    _needsPropsReapply = YES;
    _layoutDirection = @"ltr";

    _containerView = [UIView new];
    _containerView.clipsToBounds = YES;
    self.contentView = _containerView;

    _sharedHeaderHostView = [RNCCollapsiblePagerSharedHeaderView new];
    _sharedHeaderHostView.clipsToBounds = NO;
    [_containerView addSubview:_sharedHeaderHostView];
    _pinnedStickyHostView = [UIView new];
    _pinnedStickyHostView.hidden = YES;
    [_containerView addSubview:_pinnedStickyHostView];
    _topOverlayHostView = [UIView new];
    _topOverlayHostView.hidden = YES;
    [_containerView addSubview:_topOverlayHostView];

    _verticalPagerGesture =
      [[RNCCollapsiblePagerVerticalPagerGuardGestureRecognizer alloc]
        initWithTarget:self
        action:@selector(verticalPagerGuardGestureChanged:)];
    _verticalPagerGesture.delegate = self;
    _verticalPagerGesture.cancelsTouchesInView = NO;
    _verticalPagerGesture.enabled = NO;
    [self addGestureRecognizer:_verticalPagerGesture];
    _nativeTabBarView = [RNCCollapsiblePagerNativeTabBarView new];
    _nativeTabBarView.hidden = YES;
    __weak __typeof__(self) weakSelf = self;
    _nativeTabBarView.onTabPress = ^(NSInteger index, NSString *key) {
      __strong __typeof__(weakSelf) self = weakSelf;
      if (self == nil || self->_isBeingRecycled) return;
      const auto emitter = [self eventEmitter];
      if (emitter) {
        emitter->onNativeTabPress({
          .position = (int)index,
          .key = std::string(key.UTF8String ?: "")
        });
      }
    };
    [_sharedHeaderHostView addSubview:_nativeTabBarView];

    _nativeSubHeaderView = [RNCCollapsiblePagerNativeSubHeaderView new];
    _nativeSubHeaderView.hidden = YES;
    _nativeSubHeaderView.onItemPress = ^(NSInteger index, NSString *key) {
      __strong __typeof__(weakSelf) self = weakSelf;
      if (self == nil || self->_isBeingRecycled) return;
      const auto emitter = [self eventEmitter];
      if (emitter) {
        emitter->onNativeSubHeaderPress({
          .position = (int)index,
          .key = std::string(key.UTF8String ?: "")
        });
      }
    };
    [_sharedHeaderHostView addSubview:_nativeSubHeaderView];

    [self initializePageViewController];
  }
  return self;
}

- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event
{
  // Shared headers can sit above their clipped viewport after collapsing.
  // Forward only viewport touches, so invisible headers cannot cover siblings.
  if (!self.userInteractionEnabled || self.hidden || self.alpha < 0.01 ||
      ![self pointInside:point withEvent:event]) return nil;
  const auto &viewProps =
    *std::static_pointer_cast<const RNCCollapsiblePagerViewProps>(_props);
  if (viewProps.pointerEvents == PointerEventsMode::None ||
      viewProps.pointerEvents == PointerEventsMode::BoxOnly) {
    return [super hitTest:point withEvent:event];
  }
  if (_topOverlayView != nil) {
    CGPoint overlayPoint = [_topOverlayView convertPoint:point fromView:self];
    UIView *overlayHitView = [_topOverlayView hitTest:overlayPoint withEvent:event];
    if (overlayHitView != nil) return overlayHitView;
  }
  if (_stickyHeaderIsPinned) {
    CGPoint stickyPoint = [_pinnedStickyHostView convertPoint:point fromView:self];
    UIView *stickyHitView = [_pinnedStickyHostView hitTest:stickyPoint withEvent:event];
    if (stickyHitView != nil) return stickyHitView;
  }
  CGPoint headerPoint = [_sharedHeaderHostView convertPoint:point fromView:self];
  BOOL sharedHeaderPointIsVisible =
    _sharedHeaderHostView.maskView != _sharedHeaderMaskView ||
    _sharedHeaderMaskView == nil ||
    CGRectContainsPoint(_sharedHeaderMaskView.frame, headerPoint);
  if (_nativeSmoothHeaderScrollEnabled && !_isBeingRecycled &&
      _sharedHeaderHostView.superview != _containerView &&
      sharedHeaderPointIsVisible) {
    UIView *headerHitView =
      [_sharedHeaderHostView hitTest:headerPoint withEvent:event];
    if (headerHitView != nil) return headerHitView;
  }
  UIView *hitView = [super hitTest:point withEvent:event];
  if (hitView != nil && !sharedHeaderPointIsVisible &&
      (hitView == _sharedHeaderHostView ||
       [hitView isDescendantOfView:_sharedHeaderHostView])) return nil;
  if (hitView != nil && _ownedPageMasks.count > 0 &&
      [_containerView convertPoint:point fromView:self].y < _pageMaskBoundary) {
    for (UIView *page in _ownedPageMasks.keyEnumerator.allObjects) {
      if ([hitView isDescendantOfView:page]) return nil;
    }
  }
  return hitView;
}

- (void)willMoveToSuperview:(UIView *)newSuperview
{
  [super willMoveToSuperview:newSuperview];
  if (newSuperview != nil && !_directNativePagerEnabled && _pageViewController == nil) {
    self.contentView = _containerView;
    [self initializePageViewController];
  }
}

- (void)didMoveToWindow
{
  [super didMoveToWindow];
  [self registerExternalOuterPager];
  BOOL externalHeightChanged = [self resolveExternalTopOverlay];
  if (self.window == nil) [self restoreScrollEdgeContentMasks];
  [self syncScrollEdgeAppearance];
  [self emitTopScrollEdgeCapability];
  [self updateTopScrollEdgeForScrollView:_observedScrollView];
  [self updateSharedHeaderPressCancellationGesture];
  [self updateHeaderSettledStateForceBusy:NO];
  [self logHomeVisualGeometry:@"window" scrollView:_observedScrollView];
  for (UIScrollView *scrollView in _originalInsets.keyEnumerator) {
    [self updateNativeScrollerViewport:scrollView stickyHeight:self.window == nil ? 0 : _stickyHeaderHeight + self.effectiveTopOverlayHeight];
  }
  if (externalHeightChanged) [self reapplyInsetsToObservedScrollView];
  // OneKey patch: removing a decelerating page from its window can suppress
  // UIKit's final scroll callback. Restore the last acknowledged page on reattach.
  if (self.window != nil && _isPagerDragging &&
      !_pagerScrollView.dragging && !_pagerScrollView.decelerating) {
    [self finishPagerScrollEmittingSelection:NO];
  }
  // OneKey patch: a detached pager can return without a new Fabric transaction.
  // Wait until the current mount has finished before inspecting its new lists.
  if (self.window != nil &&
      (_needsScrollObserverReattachAfterMount || _releasedPageScrollStates.count > 0)) {
    NSUInteger generation = _generation;
    dispatch_async(dispatch_get_main_queue(), ^{
      if (self->_generation == generation &&
          (self->_needsScrollObserverReattachAfterMount || self->_releasedPageScrollStates.count > 0)) {
        self->_needsScrollObserverReattachAfterMount = YES;
        [self attachScrollObserverForCurrentPage];
      }
    });
  }
}

- (CGFloat)effectiveTopOverlayHeight
{
  BOOL localOverlayPending = _topScrollEdgeEnabled && _topOverlayHeight > 0 &&
    _externalTopOverlayTag <= 0;
  return _topOverlayView != nil || localOverlayPending
    ? _topOverlayHeight : _externalTopOverlayHeight;
}

- (BOOL)hasFixedTopOverlay
{
  return _topOverlayView != nil ||
    (_externalTopOverlayTag > 0 && _externalTopOverlayHeight > 0);
}

- (void)registerExternalOuterPager
{
  RNCPagerViewComponentView *outer = nil;
  if (_externalTopOverlayTag > 0 && self.window != nil && !_isBeingRecycled) {
    for (UIView *ancestor = self.superview; ancestor != nil; ancestor = ancestor.superview) {
      if ([ancestor isKindOfClass:RNCPagerViewComponentView.class]) {
        outer = (RNCPagerViewComponentView *)ancestor;
        break;
      }
    }
  }
  if (_externalOuterPager == outer) return;
  [_externalOuterPager removeNestedOverlayObserver:self];
  _externalOuterPager = outer;
  _externalOuterPageActive = NO;
  _externalOuterTransitioning = NO;
  [outer addNestedOverlayObserver:self];
}

- (BOOL)resolveExternalTopOverlay
{
  UIView *nextView = nil;
  CGFloat nextHeight = 0;
  if (_externalTopOverlayTag > 0 && _topOverlayView == nil &&
      self.window != nil && !_isBeingRecycled) {
    for (UIView *ancestor = self.superview; ancestor != nil; ancestor = ancestor.superview) {
      UIView *candidate = [ancestor viewWithTag:_externalTopOverlayTag];
      if (candidate == nil || candidate == self ||
          [candidate isDescendantOfView:self] || candidate.window != self.window) continue;
      nextView = candidate;
      break;
    }
    if (nextView != nil && CGRectGetHeight(nextView.bounds) > 0) {
      nextHeight = MAX(0, CGRectGetMaxY([nextView convertRect:nextView.bounds
        toView:_containerView]));
    }
  }
  if (nextView == nil && _externalOuterTransitioning &&
      _externalTopOverlayTag > 0 && self.window != nil) {
    nextHeight = _externalTopOverlayHeight;
  }
  BOOL changed = _externalTopOverlayView != nextView ||
    fabs(_externalTopOverlayHeight - nextHeight) > 0.5;
  _externalTopOverlayView = nextView;
  _externalTopOverlayHeight = nextHeight;
  _topOverlayHostView.hidden = !self.hasFixedTopOverlay;
  _topOverlayHostView.userInteractionEnabled = _topOverlayView != nil;
  return changed;
}

- (void)outerPagerDidUpdateOverlayOwner:(BOOL)active transitioning:(BOOL)transitioning
{
  if (_isBeingRecycled || _externalOuterPager == nil || _externalTopOverlayTag <= 0) return;
  if (_externalOuterPageActive == active && _externalOuterTransitioning == transitioning) return;
  BOOL wasTransitioning = _externalOuterTransitioning;
  _externalOuterPageActive = active;
  _externalOuterTransitioning = transitioning;
  if (transitioning && !wasTransitioning) {
    [self updateTopScrollEdgeForScrollView:nil];
    [self liftSharedHeadersForPagerTransition:@"outer"];
  } else if (!transitioning && wasTransitioning &&
             !_transitioning && !_isPagerDragging && active) {
    [self attachSharedHeadersToScrollView:_observedScrollView];
  }
  [self updateTopScrollEdgeForScrollView:transitioning ? nil : _observedScrollView];
  [self updateScrollEdgeContentMasks];
  [self setNeedsLayout];
}

- (void)traitCollectionDidChange:(UITraitCollection *)previousTraitCollection
{
  [super traitCollectionDidChange:previousTraitCollection];
  if (previousTraitCollection.userInterfaceStyle != self.traitCollection.userInterfaceStyle) {
    [self syncScrollEdgeAppearance];
    [self updateTopOverlayThemeBackground];
    [self logHomeVisualGeometry:@"trait" scrollView:_observedScrollView];
  }
}

- (void)syncScrollEdgeAppearance
{
  BOOL active = _topScrollEdgeEnabled &&
    (self.hasFixedTopOverlay || _stickyHeaderView != nil) && self.window != nil &&
    !_isBeingRecycled;
  UIUserInterfaceStyle style = UIUserInterfaceStyleUnspecified;
  if (active) {
    if ([_topOverlayColorScheme isEqualToString:@"dark"]) {
      style = UIUserInterfaceStyleDark;
    } else if ([_topOverlayColorScheme isEqualToString:@"light"]) {
      style = UIUserInterfaceStyleLight;
    } else {
      style = self.traitCollection.userInterfaceStyle;
    }
  }
  if (_topOverlayHostView.overrideUserInterfaceStyle == style) return;
  _topOverlayHostView.overrideUserInterfaceStyle = style;
  _pinnedStickyHostView.overrideUserInterfaceStyle = style;
}

- (void)restoreScrollEdgeContentMasks
{
  if (_sharedHeaderHostView.maskView == _sharedHeaderMaskView) {
    _sharedHeaderHostView.maskView = _originalSharedHeaderMaskView;
  }
  _sharedHeaderMaskView = nil;
  _originalSharedHeaderMaskView = nil;
  for (UIView *page in _ownedPageMasks.keyEnumerator.allObjects) {
    if (page.maskView == [_ownedPageMasks objectForKey:page]) {
      id original = [_originalPageMasks objectForKey:page];
      page.maskView = original == NSNull.null ? nil : original;
    }
  }
  [_ownedPageMasks removeAllObjects];
  [_originalPageMasks removeAllObjects];
  _pageMaskBoundary = 0;
}

- (void)updateScrollEdgeContentMasks
{
  [self updateHardEdgeSeparatorCovers];
  BOOL enabled = _topScrollEdgeEnabled &&
    (self.hasFixedTopOverlay || _stickyHeaderView != nil) &&
    self.window != nil && !_isBeingRecycled;
  if (!enabled) {
    [self restoreScrollEdgeContentMasks];
    [self updateTopOverlayThemeBackground];
    return;
  }
  if (self.hasFixedTopOverlay && _sharedHeaderHostView.window == self.window &&
      CGRectGetHeight(_sharedHeaderHostView.bounds) > 0 &&
      CGRectGetHeight(_topOverlayHostView.bounds) > 0) {
    if (_sharedHeaderMaskView == nil) {
      _originalSharedHeaderMaskView = _sharedHeaderHostView.maskView;
      _sharedHeaderMaskView = [UIView new];
      _sharedHeaderMaskView.backgroundColor = UIColor.blackColor;
      _sharedHeaderHostView.maskView = _sharedHeaderMaskView;
    }
    CGFloat topBottom = CGRectGetMaxY([_topOverlayHostView
      convertRect:_topOverlayHostView.bounds toView:_containerView]);
    CGFloat localTop = [_containerView convertPoint:CGPointMake(0, topBottom)
      toView:_sharedHeaderHostView].y;
    _sharedHeaderMaskView.frame = CGRectMake(0, localTop,
      CGRectGetWidth(_sharedHeaderHostView.bounds),
      MAX(0, CGRectGetHeight(_sharedHeaderHostView.bounds) - localTop));
  } else if (_sharedHeaderMaskView != nil) {
    if (_sharedHeaderHostView.maskView == _sharedHeaderMaskView) {
      _sharedHeaderHostView.maskView = _originalSharedHeaderMaskView;
    }
    _sharedHeaderMaskView = nil;
    _originalSharedHeaderMaskView = nil;
  }

  BOOL transitioning = _transitioning || _isPagerDragging || _externalOuterTransitioning ||
    _sharedHeadersLiftedForPagerTransition;
  CGFloat boundary = CGRectGetMaxY([_topOverlayHostView
    convertRect:_topOverlayHostView.bounds toView:_containerView]);
  UIView *sticky = _stickyHeaderIsPinned ? _pinnedStickyHostView : _stickyHeaderView;
  if (sticky != nil && !sticky.hidden && sticky.window == self.window &&
      CGRectGetHeight(sticky.bounds) > 0) {
    boundary = MAX(boundary, CGRectGetMaxY([sticky
      convertRect:sticky.bounds toView:_containerView]));
  }
  _pageMaskBoundary = boundary;
  NSMutableSet<UIView *> *visiblePages = [NSMutableSet new];
  if (transitioning) {
    for (UIViewController *controller in _pageControllers) {
      UIView *page = controller.view;
      if (page.window != self.window ||
          !CGRectIntersectsRect([page convertRect:page.bounds toView:_containerView],
            _containerView.bounds)) continue;
      [visiblePages addObject:page];
      UIView *mask = [_ownedPageMasks objectForKey:page];
      if (mask == nil) {
        [_originalPageMasks setObject:page.maskView ?: NSNull.null forKey:page];
        mask = [UIView new];
        mask.backgroundColor = UIColor.blackColor;
        [_ownedPageMasks setObject:mask forKey:page];
        page.maskView = mask;
      }
      CGFloat localTop = [_containerView convertPoint:CGPointMake(0, boundary)
        toView:page].y;
      mask.frame = CGRectMake(0, localTop, CGRectGetWidth(page.bounds),
        MAX(0, CGRectGetHeight(page.bounds) - localTop));
    }
  }
  for (UIView *page in _ownedPageMasks.keyEnumerator.allObjects) {
    if ([visiblePages containsObject:page]) continue;
    UIView *mask = [_ownedPageMasks objectForKey:page];
    if (page.maskView == mask) {
      id original = [_originalPageMasks objectForKey:page];
      page.maskView = original == NSNull.null ? nil : original;
    }
    [_ownedPageMasks removeObjectForKey:page];
    [_originalPageMasks removeObjectForKey:page];
  }
  [self updateTopOverlayThemeBackground];
}

- (void)updateTopOverlayThemeBackground
{
  if (@available(iOS 27.0, *)) {
    BOOL localHardOverlay = !_isBeingRecycled && _topScrollEdgeEnabled &&
      _topScrollEdgeEffect == RNCCollapsiblePagerViewTopScrollEdgeEffect::Hard &&
      _nativeSmoothHeaderScrollEnabled && _topOverlayView != nil &&
      _externalTopOverlayTag <= 0 && _topOverlayHeight > 0 && _headerHeight > 0 &&
      _topOverlayHostView.window == self.window && self.window != nil &&
      !CGRectIsEmpty(_topOverlayHostView.bounds);
    if (!localHardOverlay) {
      [_topOverlayThemeBackgroundLayer removeFromSuperlayer];
      _topOverlayThemeBackgroundLayer = nil;
      _homeVisualThemeHolding = NO;
      return;
    }

    UIColor *color = nil;
    for (UIView *view = self; view != nil; view = view.superview) {
      UIColor *candidate = [view.backgroundColor resolvedColorWithTraitCollection:
        _topOverlayHostView.traitCollection];
      if (candidate != nil && CGColorGetAlpha(candidate.CGColor) >= 1) {
        color = candidate;
        break;
      }
    }
    if (color == nil) {
      [_topOverlayThemeBackgroundLayer removeFromSuperlayer];
      _topOverlayThemeBackgroundLayer = nil;
      return;
    }

    UIScrollView *scrollView = _observedScrollView;
    BOOL geometryReady = scrollView != nil &&
      _sharedHeaderScrollView == scrollView && _topEdgeScrollView == scrollView &&
      _topEdgeInteraction.scrollView == scrollView &&
      scrollView.topEdgeEffect.style == UIScrollEdgeEffectStyle.hardStyle &&
      _sharedHeaderMaskView != nil &&
      _sharedHeaderHostView.maskView == _sharedHeaderMaskView &&
      _headerView.window == self.window;
    if (_topOverlayThemeBackgroundLayer == nil && !geometryReady) return;
    BOOL created = _topOverlayThemeBackgroundLayer == nil;
    if (created) {
      _topOverlayThemeBackgroundLayer = [CALayer layer];
      _topOverlayThemeBackgroundLayer.name = @"onekey-top-overlay-theme-background";
      _topOverlayThemeBackgroundLayer.actions = @{
        @"position": NSNull.null, @"bounds": NSNull.null,
        @"opacity": NSNull.null, @"backgroundColor": NSNull.null
      };
      [_topOverlayHostView.layer insertSublayer:_topOverlayThemeBackgroundLayer atIndex:0];
    }
    _topOverlayThemeBackgroundLayer.frame = _topOverlayHostView.bounds;
    _topOverlayThemeBackgroundLayer.backgroundColor = color.CGColor;
    if (!geometryReady) {
      if (!_homeVisualThemeHolding) {
        _homeVisualThemeHolding = YES;
#if DEBUG
        RNCCollapsiblePagerLog([NSString stringWithFormat:
          @"[IOS-HOME-VISUAL] phase=theme-background-hold pagerTag=%ld generation=%lu pageIndex=%ld opacity=%.3f",
          (long)self.tag, (unsigned long)_generation, (long)_currentIndex,
          _topOverlayThemeBackgroundLayer.opacity]);
#endif
      }
      return;
    }
    _homeVisualThemeHolding = NO;
    CGRect overlayWindow = [_topOverlayHostView convertRect:_topOverlayHostView.bounds
                                                     toView:self.window];
    CGRect headerWindow = [_headerView convertRect:_headerView.bounds
                                           toView:self.window];
    CGFloat overlayBottom = CGRectGetMaxY(overlayWindow);
    CGFloat collapse = MIN(MAX(overlayBottom - CGRectGetMinY(headerWindow), 0),
      _headerHeight);
    CGFloat opacity = 1 - collapse / _headerHeight;
    _topOverlayThemeBackgroundLayer.opacity = opacity;
#if DEBUG
    CFTimeInterval now = CACurrentMediaTime();
    if (created || fabs(opacity - _homeVisualLastThemeOpacity) >= 0.1 ||
        now - _homeVisualLastThemeLog >= 2.0) {
      _homeVisualLastThemeOpacity = opacity;
      _homeVisualLastThemeLog = now;
      CGFloat red = 0, green = 0, blue = 0, alpha = 0;
      [color getRed:&red green:&green blue:&blue alpha:&alpha];
      RNCCollapsiblePagerLog([NSString stringWithFormat:
        @"[IOS-HOME-VISUAL] phase=theme-background epochMs=%.0f monoMs=%.0f pagerTag=%ld generation=%lu pageIndex=%ld opacity=%.3f collapse=%.1f header=%.1f overlayBottom=%.1f headerY=%.1f bodyYEstimate=%.1f hardOwner=%d rgba=%.3f,%.3f,%.3f,%.3f",
        NSDate.date.timeIntervalSince1970 * 1000, now * 1000,
        (long)self.tag, (unsigned long)_generation, (long)_currentIndex,
        opacity, collapse, (CGFloat)_headerHeight, overlayBottom,
        CGRectGetMinY(headerWindow), overlayBottom + _headerHeight +
          _stickyHeaderHeight - collapse, _topEdgeInteraction.scrollView == scrollView,
        red, green, blue, alpha]);
    }
#endif
  }
}

- (void)updateEventEmitter:(const EventEmitter::Shared &)eventEmitter
{
  [super updateEventEmitter:eventEmitter];
  [self emitTopScrollEdgeCapability];
}

- (void)emitTopScrollEdgeCapability
{
  if (self.window == nil || _isBeingRecycled) return;
  const auto emitter = [self eventEmitter];
  if (!emitter) return;
  BOOL available = NO;
  if (@available(iOS 26.0, *)) available = YES;
  emitter->onScrollEdgeCapability({.available = (bool)available});
}

- (void)updateTopScrollEdgeForScrollView:(UIScrollView *)scrollView
{
  [self syncScrollEdgeAppearance];
  if (@available(iOS 26.0, *)) {
    if (_pendingTopOverlayAppearanceCommit) scrollView = nil;
    BOOL externalOwnerReady = _externalTopOverlayTag <= 0 ||
      _externalOuterPager == nil ||
      (_externalOuterPageActive && !_externalOuterTransitioning);
    BOOL hasOverlay = _topScrollEdgeEnabled && self.hasFixedTopOverlay &&
      externalOwnerReady && self.window != nil;
    BOOL hasSticky = _topScrollEdgeEnabled && _stickyHeaderView != nil &&
      externalOwnerReady && self.window != nil;
    BOOL enabled = (hasOverlay || hasSticky) && scrollView != nil;
    BOOL topHostChanged = (_topEdgeInteraction != nil) != hasOverlay ||
      (hasOverlay && _topEdgeInteractionHost != _topOverlayHostView);
    if (_topEdgeScrollView != nil && (!enabled || _topEdgeScrollView != scrollView ||
        topHostChanged)) {
      [_topEdgeInteractionHost removeInteraction:_topEdgeInteraction];
      _topEdgeInteraction.scrollView = nil;
      [_stickyEdgeInteractionHost removeInteraction:_stickyEdgeInteraction];
      _stickyEdgeInteraction.scrollView = nil;
      [_topEdgeMarker removeFromSuperview];
      [_stickyEdgeMarker removeFromSuperview];
      _topEdgeScrollView.topEdgeEffect.style = _originalTopEdgeStyle;
      _topEdgeInteraction = nil;
      _stickyEdgeInteraction = nil;
      _topEdgeMarker = nil;
      _stickyEdgeMarker = nil;
      _topEdgeInteractionHost = nil;
      _stickyEdgeInteractionHost = nil;
      _topEdgeScrollView = nil;
      _originalTopEdgeStyle = nil;
    }
    if (enabled && _topEdgeScrollView == nil) {
      _originalTopEdgeStyle = scrollView.topEdgeEffect.style;
      _topEdgeScrollView = scrollView;
    }
    if (enabled) {
      UIScrollEdgeEffectStyle *style =
        _topScrollEdgeEffect == RNCCollapsiblePagerViewTopScrollEdgeEffect::Soft
          ? UIScrollEdgeEffectStyle.softStyle : UIScrollEdgeEffectStyle.hardStyle;
      if (scrollView.topEdgeEffect.style != style) scrollView.topEdgeEffect.style = style;
    }
    if (hasOverlay && enabled && _topEdgeInteraction == nil) {
      // UIKit needs a native element to shape the edge effect around React-drawn content.
      _topEdgeMarker = [UILabel new];
      _topEdgeMarker.frame = _topOverlayHostView.bounds;
      _topEdgeMarker.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
      _topEdgeMarker.text = @" ";
      _topEdgeMarker.textColor = UIColor.clearColor;
      _topEdgeMarker.backgroundColor = UIColor.clearColor;
      _topEdgeMarker.userInteractionEnabled = NO;
      _topEdgeMarker.isAccessibilityElement = NO;
      _topEdgeMarker.accessibilityElementsHidden = YES;
      [_topOverlayHostView addSubview:_topEdgeMarker];
      _topEdgeInteraction = [UIScrollEdgeElementContainerInteraction new];
      _topEdgeInteraction.edge = UIRectEdgeTop;
      _topEdgeInteraction.scrollView = scrollView;
      [_topOverlayHostView addInteraction:_topEdgeInteraction];
      _topEdgeInteractionHost = _topOverlayHostView;
    }
    BOOL legacyStickyPinned = !_nativeSmoothHeaderScrollEnabled &&
      _stickyHeaderView != nil && _headerOffset >= _headerHeight - 0.5;
    BOOL stickyEnabled = enabled && (_stickyHeaderIsPinned || legacyStickyPinned);
    UIView *stickyInteractionHost = _stickyHeaderIsPinned
      ? _pinnedStickyHostView : _stickyHeaderView;
    if (_stickyEdgeInteractionHost != nil &&
        (!stickyEnabled || _stickyEdgeInteractionHost != stickyInteractionHost)) {
      [_stickyEdgeInteractionHost removeInteraction:_stickyEdgeInteraction];
      _stickyEdgeInteraction.scrollView = nil;
      [_stickyEdgeMarker removeFromSuperview];
      _stickyEdgeMarker = nil;
      _stickyEdgeInteraction = nil;
      _stickyEdgeInteractionHost = nil;
    }
    if (stickyEnabled && _stickyEdgeInteraction == nil) {
      _stickyEdgeMarker = [UILabel new];
      _stickyEdgeMarker.frame = stickyInteractionHost.bounds;
      _stickyEdgeMarker.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
      _stickyEdgeMarker.text = @" ";
      _stickyEdgeMarker.textColor = UIColor.clearColor;
      _stickyEdgeMarker.backgroundColor = UIColor.clearColor;
      _stickyEdgeMarker.userInteractionEnabled = NO;
      _stickyEdgeMarker.isAccessibilityElement = NO;
      _stickyEdgeMarker.accessibilityElementsHidden = YES;
      [stickyInteractionHost addSubview:_stickyEdgeMarker];
      _stickyEdgeInteraction = [UIScrollEdgeElementContainerInteraction new];
      _stickyEdgeInteraction.edge = UIRectEdgeTop;
      _stickyEdgeInteraction.scrollView = scrollView;
      [stickyInteractionHost addInteraction:_stickyEdgeInteraction];
      _stickyEdgeInteractionHost = stickyInteractionHost;
    }
    [self updateScrollEdgeContentMasks];
  }
}

- (void)updateHardEdgeSeparatorCovers
{
  BOOL enabled = _topScrollEdgeEnabled &&
    _topScrollEdgeEffect == RNCCollapsiblePagerViewTopScrollEdgeEffect::Hard &&
    _topEdgeScrollView != nil && self.window != nil && !_isBeingRecycled;
  if (!enabled && _topEdgeSeparatorCover == nil && _stickyEdgeSeparatorCover == nil) return;
  UIColor *color = nil;
  for (UIView *view = self; view != nil; view = view.superview) {
    UIColor *candidate = [view.backgroundColor resolvedColorWithTraitCollection:
      _topOverlayHostView.traitCollection];
    if (candidate != nil && CGColorGetAlpha(candidate.CGColor) >= 1) {
      color = candidate;
      break;
    }
  }
  color = color ?: [UIColor.systemBackgroundColor resolvedColorWithTraitCollection:
    _topOverlayHostView.traitCollection];
  CGFloat scale = self.window.screen.scale ?: self.traitCollection.displayScale;
  CGFloat pixel = 1.0 / MAX(1, scale);
  UIView *hosts[] = {_topEdgeInteractionHost, _stickyEdgeInteractionHost};
  CALayer *covers[] = {_topEdgeSeparatorCover, _stickyEdgeSeparatorCover};
  for (NSInteger index = 0; index < 2; index++) {
    UIView *host = hosts[index];
    CALayer *cover = covers[index];
    if (!enabled || host == nil || host.hidden || CGRectIsEmpty(host.bounds)) {
      [cover removeFromSuperlayer];
      cover = nil;
    } else {
      if (cover == nil) {
        cover = [CALayer layer];
        cover.name = @"onekey-hard-edge-separator-cover";
        cover.zPosition = 1;
        cover.actions = @{@"position": NSNull.null, @"bounds": NSNull.null,
          @"backgroundColor": NSNull.null};
        [_containerView.layer addSublayer:cover];
      }
      // Cover only the hairline; keep the native Hard material and glass shadows.
      CGFloat boundary = CGRectGetMaxY([host convertRect:host.bounds toView:_containerView]);
      cover.frame = CGRectMake(0, round(boundary * scale) / scale - pixel,
        CGRectGetWidth(_containerView.bounds), pixel);
      cover.contentsScale = scale;
      cover.backgroundColor = color.CGColor;
      cover.hidden = index == 0 && _stickyHeaderIsPinned;
    }
    if (index == 0) _topEdgeSeparatorCover = cover;
    else _stickyEdgeSeparatorCover = cover;
  }
}

- (void)dealloc
{
  [self updateTopScrollEdgeForScrollView:nil];
  [self cancelHeaderSettledCheck];
  [[NSNotificationCenter defaultCenter] removeObserver:self name:@"OneKeyNativeListScrollCommandAccepted" object:nil];
  [[NSNotificationCenter defaultCenter] removeObserver:self name:@"OneKeyNativeScrollViewDidBecomeReady" object:nil];
  for (UIScrollView *scrollView in _originalInsets.keyEnumerator) {
    [self updateNativeScrollerViewport:scrollView stickyHeight:0];
  }
  for (UIScrollView *scrollView in _refreshControlObservedScrollViews.allObjects) {
    [self releaseRefreshControlForScrollView:scrollView];
  }
  [self detachSharedHeaderPressCancellationGesture];
}

- (void)initializePageViewController
{
  if (_directNativePagerEnabled) return;
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
      _pageViewControllerScrollView = _pagerScrollView;
      _pagerScrollView.delegate = self;
      _pagerScrollView.delaysContentTouches = NO;
      _pagerScrollView.scrollEnabled = _scrollEnabled;
      break;
    }
  }
  [self applyNestedScrollBlocker];
  [self connectVerticalPagerGuard];
}

- (void)setDirectNativePagerEnabled:(BOOL)enabled
{
  if ([_pagerScrollView isKindOfClass:RNCCollapsiblePagerDirectScrollView.class]) {
    ((RNCCollapsiblePagerDirectScrollView *)_pagerScrollView).excludedHeaderView =
      _nativeSmoothHeaderScrollEnabled ? _sharedHeaderHostView : nil;
  }
  if (_transitioning || _isPagerDragging) {
    _pendingDirectNativePagerEnabled = enabled;
    _hasPendingDirectNativePagerChange = YES;
    return;
  }
  _hasPendingDirectNativePagerChange = NO;
  if (_directNativePagerEnabled == enabled) return;

  [self restoreScrollEdgeContentMasks];
  [self detachScrollObserver];
  [self restoreSharedHeadersToContainer];
  if (_blockerGesture != nil) {
    [self removeGestureRecognizer:_blockerGesture];
    _blockerGesture = nil;
  }
  for (UIViewController *controller in _pageControllers) {
    [controller.view removeFromSuperview];
  }

  if (enabled) {
    _pageViewController.dataSource = nil;
    _pageViewController.delegate = nil;
    _pageViewControllerScrollView.delegate = nil;
    [_pageViewController.view removeFromSuperview];
    _pageViewController = nil;
    _pageViewControllerScrollView = nil;

    RNCCollapsiblePagerDirectScrollView *directPager =
      [RNCCollapsiblePagerDirectScrollView new];
    directPager.excludedHeaderView = _nativeSmoothHeaderScrollEnabled
      ? _sharedHeaderHostView
      : nil;
    directPager.pagingEnabled = YES;
    directPager.directionalLockEnabled = YES;
    directPager.delaysContentTouches = NO;
    directPager.showsHorizontalScrollIndicator = NO;
    directPager.showsVerticalScrollIndicator = NO;
    directPager.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    directPager.scrollEnabled = _scrollEnabled;
    directPager.delegate = self;
    [_containerView insertSubview:directPager atIndex:0];
    _pagerScrollView = directPager;
    _directNativePagerEnabled = YES;
  } else {
    _pagerScrollView.delegate = nil;
    [_pagerScrollView removeFromSuperview];
    _pagerScrollView = nil;
    _directNativePagerEnabled = NO;
    [self initializePageViewController];
  }

  [self applyNestedScrollBlocker];
  [self connectVerticalPagerGuard];
  [self rebuildSlots];
  [self updateSharedHeaderPressCancellationGesture];
  [self setNeedsLayout];
}

- (CGFloat)directPagerOffsetForIndex:(NSInteger)index
{
  CGFloat width = CGRectGetWidth(_pagerScrollView.bounds);
  if (width <= 0 || _pageControllers.count == 0) return 0;
  NSInteger physicalIndex = self.isLtrLayout
    ? index
    : (NSInteger)_pageControllers.count - 1 - index;
  return width * MAX(0, physicalIndex);
}

- (CGFloat)directPagerProgress
{
  CGFloat width = CGRectGetWidth(_pagerScrollView.bounds);
  if (width <= 0 || _pageControllers.count == 0) return _currentIndex;
  CGFloat physicalProgress = RNCClamp(
    _pagerScrollView.contentOffset.x / width,
    0,
    _pageControllers.count - 1
  );
  return self.isLtrLayout
    ? physicalProgress
    : _pageControllers.count - 1 - physicalProgress;
}

#pragma mark - Smooth shared header ownership

- (void)restorePinnedStickyHeader
{
  if (!_stickyHeaderIsPinned) return;
  _stickyHeaderIsPinned = NO;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  _nativeTabBarView.transform = CGAffineTransformIdentity;
  _nativeSubHeaderView.transform = CGAffineTransformIdentity;
  [_stickyHeaderView removeFromSuperview];
  [_nativeTabBarView removeFromSuperview];
  [_nativeSubHeaderView removeFromSuperview];
  if (_stickyHeaderView != nil) [_sharedHeaderHostView addSubview:_stickyHeaderView];
  [_sharedHeaderHostView addSubview:_nativeTabBarView];
  [_sharedHeaderHostView addSubview:_nativeSubHeaderView];
  _pinnedStickyHostView.hidden = YES;
  _pinnedStickyHostView.layer.zPosition = 0;
  [_containerView addSubview:_pinnedStickyHostView];
  if ([_pagerScrollView isKindOfClass:RNCCollapsiblePagerDirectScrollView.class]) {
    ((RNCCollapsiblePagerDirectScrollView *)_pagerScrollView).excludedHeaderView = _sharedHeaderHostView;
  }
  [_nativeTabBarView updateAncestorGesturePrecedence];
  [_nativeSubHeaderView updateAncestorGesturePrecedence];
}

- (void)restoreSharedHeadersToContainer
{
  [self restorePinnedStickyHeader];
  [self updateTopScrollEdgeForScrollView:_observedScrollView];
  _sharedHeaderScrollView = nil;
  _sharedHeadersLiftedForPagerTransition = NO;
  _sharedHeaderHostView.layer.zPosition = 0;
  if (_sharedHeaderHostView.superview != _containerView) {
    [_containerView addSubview:_sharedHeaderHostView];
  }
  [self layoutSharedHeaders];
  if (self.hasFixedTopOverlay) {
    [_containerView bringSubviewToFront:_topOverlayHostView];
  }
}

- (void)attachSharedHeadersToScrollView:(UIScrollView *)scrollView
{
  if (!_nativeSmoothHeaderScrollEnabled || _transitioning || _isPagerDragging ||
      _externalOuterTransitioning ||
      scrollView == nil || _isBeingRecycled) return;
  if ((UIView *)scrollView == _sharedHeaderHostView ||
      [scrollView isDescendantOfView:_sharedHeaderHostView]) {
    [self restoreSharedHeadersToContainer];
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"header-owner=pager page=%ld reason=cycle-guard",
      (long)_currentIndex]);
    return;
  }
  if (_sharedHeaderScrollView == scrollView &&
      _sharedHeaderHostView.superview == scrollView) {
    _sharedHeaderHostView.layer.zPosition =
      _topScrollEdgeEnabled && (self.hasFixedTopOverlay || _stickyHeaderView != nil) ? 0 : 1000;
    [self layoutSharedHeaders];
    return;
  }

  _sharedHeaderScrollView = scrollView;
  _sharedHeadersLiftedForPagerTransition = NO;
  _sharedHeaderHostView.layer.zPosition =
    _topScrollEdgeEnabled && (self.hasFixedTopOverlay || _stickyHeaderView != nil) ? 0 : 1000;
  if (_sharedHeaderHostView.superview != scrollView) {
    [scrollView addSubview:_sharedHeaderHostView];
  }
  [_nativeTabBarView updateAncestorGesturePrecedence];
  [_nativeSubHeaderView updateAncestorGesturePrecedence];
  _currentLogicalOffset = scrollView.contentOffset.y + scrollView.contentInset.top;
  [self layoutSharedHeaders];
  NSString *scrollKind = [scrollView isKindOfClass:UICollectionView.class]
    ? @"list"
    : @"scroller";
  RNCCollapsiblePagerLog([NSString stringWithFormat:
    @"header-owner=%@ page=%ld offset=%.2f",
    scrollKind,
    (long)_currentIndex,
    _currentLogicalOffset]);
}

- (void)liftSharedHeadersForPagerTransition:(NSString *)reason
{
  if (!_nativeSmoothHeaderScrollEnabled || _sharedHeadersLiftedForPagerTransition ||
      _isBeingRecycled) return;
  [self restoreSharedHeadersToContainer];
  _sharedHeadersLiftedForPagerTransition = YES;
  [self layoutSharedHeaders];
  RNCCollapsiblePagerLog([NSString stringWithFormat:
    @"header-owner=pager page=%ld destination=%ld reason=%@",
    (long)_currentIndex,
    (long)_destinationIndex,
    reason ?: @"unknown"]);
}

- (void)layoutSharedHeaders
{
  CGFloat width = CGRectGetWidth(self.bounds);
  CGFloat totalHeight = _headerHeight + _stickyHeaderHeight;
  BOOL attachedToList = _nativeSmoothHeaderScrollEnabled &&
    _sharedHeaderScrollView != nil && !_sharedHeadersLiftedForPagerTransition;

  _sharedHeaderHostView.transform = CGAffineTransformIdentity;
  _headerView.transform = CGAffineTransformIdentity;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  _nativeTabBarView.transform = CGAffineTransformIdentity;
  _nativeSubHeaderView.transform = CGAffineTransformIdentity;

  _sharedHeaderHostView.frame = CGRectMake(
    0,
    attachedToList ? -totalHeight : self.effectiveTopOverlayHeight,
    width,
    totalHeight
  );
  CGFloat stickyY = _headerHeight;
  _headerView.frame = CGRectMake(0, 0, width, _headerHeight);
  CGPoint pinnedOrigin = _stickyHeaderIsPinned
    ? [_sharedHeaderScrollView convertPoint:CGPointMake(0, self.effectiveTopOverlayHeight) fromView:_containerView]
    : CGPointMake(0, self.effectiveTopOverlayHeight);
  _pinnedStickyHostView.frame = CGRectMake(
    pinnedOrigin.x, pinnedOrigin.y, width, _stickyHeaderHeight);
  CGFloat stickyChildY = _stickyHeaderIsPinned ? 0 : stickyY;
  _stickyHeaderView.frame = CGRectMake(0, stickyChildY, width, _stickyHeaderHeight);
  CGFloat nativeTabBarVisibleHeight = _nativeTabBarView.hidden
    ? 0
    : MIN(_stickyHeaderHeight, _nativeTabBarHeight);
  _nativeTabBarView.frame = CGRectMake(
    0,
    stickyChildY,
    width,
    nativeTabBarVisibleHeight
  );
  _nativeSubHeaderView.frame = CGRectMake(
    0,
    stickyChildY + nativeTabBarVisibleHeight,
    width,
    MIN(
      MAX(0, _stickyHeaderHeight - nativeTabBarVisibleHeight),
      _nativeSubHeaderHeight
    )
  );
  _topOverlayHostView.frame = CGRectMake(0, 0, width, self.effectiveTopOverlayHeight);
  _topOverlayView.frame = _topOverlayHostView.bounds;
  [self applyHeaderOffset];
}

- (void)updatePinnedStickyHeader
{
  BOOL attachedToList = _nativeSmoothHeaderScrollEnabled &&
    _sharedHeaderScrollView != nil && !_sharedHeadersLiftedForPagerTransition;
  if (!attachedToList && !_stickyHeaderIsPinned) return;
  UIScrollView *scrollView = _sharedHeaderScrollView;
  NSNumber *appliedTop = [_appliedTopInsets objectForKey:scrollView];
  CGFloat refreshInset = scrollView.refreshControl != nil && appliedTop != nil
    ? MAX(0, scrollView.contentInset.top - appliedTop.doubleValue) : 0;
  CGFloat pinnedTranslation = MAX(0, _currentLogicalOffset - _headerHeight - refreshInset);
  CGFloat stickyTop = [_sharedHeaderHostView convertPoint:
    CGPointMake(0, _headerHeight + pinnedTranslation) toView:self].y;
  BOOL shouldPin = _topScrollEdgeEnabled &&
    _stickyHeaderView != nil && attachedToList && !_transitioning &&
    !_externalOuterTransitioning &&
    !_isPagerDragging && _headerOffset >= _headerHeight - 0.5 &&
    stickyTop <= self.effectiveTopOverlayHeight + 0.5;
  if (_stickyHeaderIsPinned && shouldPin && _pinnedStickyHostView.superview != scrollView) {
    [self updateTopScrollEdgeForScrollView:nil];
    [scrollView addSubview:_pinnedStickyHostView];
    [self updateTopScrollEdgeForScrollView:_observedScrollView];
  }
  if (_stickyHeaderIsPinned == shouldPin) {
    if (shouldPin) {
      CGPoint origin = [scrollView convertPoint:CGPointMake(0, self.effectiveTopOverlayHeight)
        fromView:_containerView];
      _pinnedStickyHostView.frame = CGRectMake(origin.x, origin.y,
        CGRectGetWidth(self.bounds), _stickyHeaderHeight);
    }
    return;
  }

  _stickyHeaderIsPinned = shouldPin;
  UIView *host = shouldPin ? _pinnedStickyHostView : _sharedHeaderHostView;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  _nativeTabBarView.transform = CGAffineTransformIdentity;
  _nativeSubHeaderView.transform = CGAffineTransformIdentity;
  [_stickyHeaderView removeFromSuperview];
  [_nativeTabBarView removeFromSuperview];
  [_nativeSubHeaderView removeFromSuperview];
  if (_stickyHeaderView != nil) [host addSubview:_stickyHeaderView];
  [host addSubview:_nativeTabBarView];
  [host addSubview:_nativeSubHeaderView];
  CGFloat childY = shouldPin ? 0 : _headerHeight;
  CGFloat width = CGRectGetWidth(self.bounds);
  CGFloat tabHeight = _nativeTabBarView.hidden ? 0 : MIN(_stickyHeaderHeight, _nativeTabBarHeight);
  _stickyHeaderView.frame = CGRectMake(0, childY, width, _stickyHeaderHeight);
  _nativeTabBarView.frame = CGRectMake(0, childY, width, tabHeight);
  _nativeSubHeaderView.frame = CGRectMake(0, childY + tabHeight, width,
    MIN(MAX(0, _stickyHeaderHeight - tabHeight), _nativeSubHeaderHeight));
  _pinnedStickyHostView.hidden = !shouldPin;
  if ([_pagerScrollView isKindOfClass:RNCCollapsiblePagerDirectScrollView.class]) {
    ((RNCCollapsiblePagerDirectScrollView *)_pagerScrollView).excludedHeaderView =
      shouldPin ? _pinnedStickyHostView : _sharedHeaderHostView;
  }
  if (shouldPin) {
    [scrollView addSubview:_pinnedStickyHostView];
    _pinnedStickyHostView.layer.zPosition = 1000;
    CGPoint origin = [scrollView convertPoint:CGPointMake(0, self.effectiveTopOverlayHeight)
      fromView:_containerView];
    _pinnedStickyHostView.frame = CGRectMake(origin.x, origin.y, width, _stickyHeaderHeight);
  } else {
    _pinnedStickyHostView.layer.zPosition = 0;
    [_containerView addSubview:_pinnedStickyHostView];
  }
  [_nativeTabBarView updateAncestorGesturePrecedence];
  [_nativeSubHeaderView updateAncestorGesturePrecedence];
  [self updateTopScrollEdgeForScrollView:_observedScrollView];
  [self logHomeVisualGeometry:shouldPin ? @"pin" : @"unpin"
                    scrollView:_observedScrollView];
}

- (void)preparePageForHorizontalTransitionAtIndex:(NSInteger)pageIndex
{
  if (!_nativeSmoothHeaderScrollEnabled ||
      pageIndex < 0 || pageIndex >= _pageControllers.count) return;
  UIScrollView *scrollView = [self verticalScrollViewForPageAtIndex:pageIndex];
  if (scrollView != nil && scrollView != _observedScrollView) {
    [self applyInsetsToScrollView:scrollView pageIndex:pageIndex restore:YES];
  }
}

- (void)beginPageStateTransition
{
  [self cancelPagerTop];
  if (!_nativeSmoothHeaderScrollEnabled || _headerHeight <= 0 || _transitionPageOffsets != nil) return;
  _transitionSourceDisplayBody = MAX(_observedScrollView.contentOffset.y + _observedScrollView.contentInset.top - _headerHeight, 0);
  [self detachScrollObserver];
  _transitionSourceIndex = _currentIndex;
  _transitionHeaderOffset = _headerOffset;
  _transitionPageOffsets = [_pageOffsets copy];
  _transitionCompensatedPages = [NSMutableIndexSet new];
}

- (void)updatePageStateTransitionProgress:(CGFloat)progress
{
  if (_transitionPageOffsets == nil || _finishingPageStateTransition) return;
  NSInteger first = MAX(0, MIN((NSInteger)floor(progress), (NSInteger)_pageControllers.count - 1));
  NSInteger second = MIN(first + 1, (NSInteger)_pageControllers.count - 1);
  CGFloat fraction = RNCClamp(progress - first, 0, 1);
  if (!_isPagerDragging && _destinationIndex != _transitionSourceIndex) {
    first = _transitionSourceIndex;
    second = _destinationIndex;
    fraction = RNCClamp((progress - first) / (second - first), 0, 1);
  }
  CGFloat firstQ = [_transitionPageOffsets[[self pageKeyForIndex:first]] doubleValue];
  CGFloat secondQ = [_transitionPageOffsets[[self pageKeyForIndex:second]] doubleValue];
  _headerOffset = _transitionHeaderOffset;
  _syntheticPageRestore = YES;
  NSInteger visibleFirst = MAX(0, MIN((NSInteger)floor(progress), (NSInteger)_pageControllers.count - 1));
  NSInteger visibleLast = MIN(visibleFirst + 1, (NSInteger)_pageControllers.count - 1);
  NSMutableIndexSet *displayPages = [NSMutableIndexSet indexSetWithIndexesInRange:
    NSMakeRange(visibleFirst, visibleLast - visibleFirst + 1)];
  if (!_isPagerDragging) {
    // UIView tap animation reports its endpoint once while the source exits.
    [displayPages addIndex:_transitionSourceIndex];
    [displayPages addIndex:_destinationIndex];
  }
  for (NSUInteger index = displayPages.firstIndex; index != NSNotFound;
       index = [displayPages indexGreaterThanIndex:index]) {
    UIScrollView *scroll = [self verticalScrollViewForPageAtIndex:index];
    if (scroll != nil) [self applyInsetsToScrollView:scroll pageIndex:index restore:YES];
  }
  _syntheticPageRestore = NO;
  [self layoutSharedHeaders];
}

- (void)finishPageStateTransition
{
  if (_transitionPageOffsets == nil) return;
  _finishingPageStateTransition = YES;
  _headerOffset = _transitionHeaderOffset;
  if (_currentIndex == _transitionSourceIndex) _displayBodyOverrides[self.currentPageKey] = @(_transitionSourceDisplayBody);
  else [_displayBodyOverrides removeObjectForKey:self.currentPageKey];
  if (_headerOffset >= _headerHeight) [_suspendedBodyKeys removeObject:self.currentPageKey];
  _syntheticPageRestore = YES;
  // Both completion and cancellation undo every temporary viewport offset.
  for (NSUInteger index = _transitionCompensatedPages.firstIndex; index != NSNotFound;
       index = [_transitionCompensatedPages indexGreaterThanIndex:index]) {
    UIScrollView *scroll = [self verticalScrollViewForPageAtIndex:index];
    if (scroll == nil) continue;
    [self applyInsetsToScrollView:scroll pageIndex:index restore:YES];

  }
  _syntheticPageRestore = NO;
  _finishingPageStateTransition = NO;
  _transitionPageOffsets = nil;
  _transitionCompensatedPages = nil;
  [self layoutSharedHeaders];
}

#pragma mark - React mounting

// OneKey patch: Fabric can recycle a removed list as a descendant of our header
// within the same transaction. Release native ownership before that reuse can
// form a cycle; did-mount recovery runs too late to prevent it.
- (void)mountingTransactionWillMount:(const MountingTransaction &)transaction
                withSurfaceTelemetry:(const SurfaceTelemetry &)surfaceTelemetry
{
  if (_isBeingRecycled || (_originalInsets.count == 0 && _releasedPageScrollStates.count == 0)) return;
  std::unordered_set<NSInteger> removedTags;
  std::unordered_set<NSInteger> deletedTags;
  for (const auto &mutation : transaction.getMutations()) {
    if ((mutation.type == ShadowViewMutation::Remove ||
         mutation.type == ShadowViewMutation::Delete) && mutation.oldChildShadowView.tag > 0) {
      removedTags.insert(mutation.oldChildShadowView.tag);
      if (mutation.type == ShadowViewMutation::Delete) {
        deletedTags.insert(mutation.oldChildShadowView.tag);
      }
    }
  }
  // A transition can defer reattachment across several mounting transactions.
  for (RNCCollapsiblePagerReleasedScrollState *state in _releasedPageScrollStates.allValues) {
    if (deletedTags.find(state.componentTag) != deletedTags.end()) state.componentView = nil;
  }
  if (removedTags.empty()) return;

  // Inspect only the managed scroll views' ancestor chains, including wrappers
  // above the pager. Row removals and unrelated commits keep ownership intact.
  for (UIScrollView *scrollView in _originalInsets.keyEnumerator.allObjects) {
    for (UIView *ancestor = scrollView; ancestor != nil; ancestor = ancestor.superview) {
      if (removedTags.find(ancestor.tag) == removedTags.end()) continue;
      BOOL belongsToPage = NO;
      for (NSInteger index = 0; index < _pageControllers.count; index++) {
        if (![scrollView isDescendantOfView:_pageControllers[index].view]) continue;
        belongsToPage = YES;
        NSString *pageKey = [self pageKeyForIndex:index];
        UIView *componentView = RNCScrollComponentView(scrollView);
        RNCCollapsiblePagerReleasedScrollState *state = [RNCCollapsiblePagerReleasedScrollState new];
        state.scrollView = scrollView;
        state.componentTag = componentView.tag;
        // A deleted component can reuse both the native object and its tag.
        if (deletedTags.find(componentView.tag) == deletedTags.end()) {
          state.componentView = componentView;
        }
        _releasedPageScrollStates[pageKey] = state;
        // Retained pages may be visually aligned to the expanded header while
        // their saved offsets still hold the user's independent list position.
        BOOL ownsObservedPage = scrollView == _observedScrollView &&
            (!_nativeSmoothHeaderScrollEnabled ||
             (_observedPageGeneration == _generation && _observedPageIndex == index &&
              [_observedPageKey isEqualToString:pageKey]));
        if (!_nativeSmoothHeaderScrollEnabled && _transitionPageOffsets == nil && !_syntheticPageRestore &&
            ![_pendingPageRestoreKeys containsObject:pageKey] &&
            (ownsObservedPage || _pageOffsets[pageKey] == nil)) {
          _pageOffsets[pageKey] = @(scrollView.contentOffset.y + scrollView.contentInset.top);
        }
        break;
      }
      // Quarantine only this mount transaction; moved views may be valid again.
      [_mountingRemovedScrollViews addObject:scrollView];
      _needsScrollObserverReattachAfterMount = YES;
      [self restoreScrollViewState:scrollView resetSavedOffset:!belongsToPage];
      break;
    }
  }
}

- (void)mountingTransactionDidMount:(const MountingTransaction &)transaction
               withSurfaceTelemetry:(const SurfaceTelemetry &)surfaceTelemetry
{
  if (_needsSlotRebuild && _deferTopEdgeSlotRebuild &&
      _topScrollEdgeEnabled && _nativeSmoothHeaderScrollEnabled &&
      _topScrollEdgeEffect == RNCCollapsiblePagerViewTopScrollEdgeEffect::Hard &&
      _topOverlayHeight > 0 && _pageKeys.count > 0 &&
      _externalTopOverlayTag <= 0 &&
      _logicalChildren.count == _pageKeys.count + 3 &&
      !_transitioning && !_isPagerDragging && !_isBeingRecycled) {
    _deferTopEdgeSlotRebuild = NO;
  }
  if (_needsSlotRebuild && !_deferTopEdgeSlotRebuild &&
      !_transitioning && !_isPagerDragging) {
    [self rebuildSlots];
  }
  [self registerExternalOuterPager];
  if ([self resolveExternalTopOverlay]) {
    [self reapplyInsetsToObservedScrollView];
    [self setNeedsLayout];
  }
  if (_pendingTopOverlayAppearanceCommit) {
    _pendingTopOverlayAppearanceCommit = NO;
    BOOL localOverlayReady = _topOverlayView.superview == _topOverlayHostView &&
      _topOverlayView.window == self.window;
    BOOL externalOverlayReady = _externalTopOverlayTag > 0 &&
      _externalTopOverlayView.window == self.window;
    if (!_isBeingRecycled && _topScrollEdgeEnabled && self.window != nil &&
        (localOverlayReady || externalOverlayReady)) {
      [self updateTopScrollEdgeForScrollView:_observedScrollView];
    }
  }
  [self updateScrollEdgeContentMasks];
  [_mountingRemovedScrollViews removeAllObjects];
  [self logHomeVisualGeometry:@"did-mount" scrollView:_observedScrollView];
  if (!_nativeSmoothHeaderScrollEnabled || _isBeingRecycled || self.window == nil ||
      _currentIndex < 0 || _currentIndex >= _pageControllers.count) return;
  // Prepare newly mounted owned scrollers before their first frame, even while
  // horizontal selection still owns the shared header and observer.
  NSInteger first = MAX(0, _currentIndex - 1);
  NSInteger last = MIN((NSInteger)_pageControllers.count - 1, _currentIndex + 1);
  for (NSInteger index = 0; index < _pageControllers.count; index++) {
    if ((index < first || index > last) && index != _destinationIndex) continue;
    UIScrollView *scrollView = [self verticalScrollViewForPageAtIndex:index];
    BOOL isOwnedScroller = [scrollView.superview isKindOfClass:RNCNativeScrollerComponentView.class] &&
      ((RNCNativeScrollerComponentView *)scrollView.superview).scrollView == scrollView &&
      [scrollView isDescendantOfView:_pageControllers[index].view];
    if ((RNCIsMountedNativeListScrollView(scrollView) || isOwnedScroller) &&
        [_originalInsets objectForKey:scrollView] == nil) {
      [self applyInsetsToScrollView:scrollView pageIndex:index restore:YES];
    }
  }
  UIView *page = _pageControllers[_currentIndex].view;
  if (_scrollResolveRetryAttempt >= 12 &&
      _releasedPageScrollStates[self.currentPageKey] != nil) {
    for (const auto &mutation : transaction.getMutations()) {
      if (mutation.type == ShadowViewMutation::Insert && mutation.parentTag > 0 &&
          [page viewWithTag:mutation.parentTag] != nil) {
        // A new current-page insertion starts a bounded recovery round after
        // the previous replacement search has scheduled its final retry.
        _scrollResolveRetryToken += 1;
        _scrollResolveRetryScheduled = NO;
        _scrollResolveRetryAttempt = 0;
        break;
      }
    }
  }

  // Record recovery during transitions; ownership still waits for settlement.
  if (_transitioning || _isPagerDragging) return;

  // OneKey patch: a release must recover even when the changed parent is above
  // the page subtree. Missing lists keep their identity for a later insertion.
  if (_needsScrollObserverReattachAfterMount) {
    [self attachScrollObserverForCurrentPage];
    return;
  }
  BOOL headerIsAttached = [_sharedHeaderHostView isDescendantOfView:self];
  BOOL currentListIsAttached = _observedScrollView != nil &&
    [_observedScrollView isDescendantOfView:page] && headerIsAttached;
  BOOL currentListIsFallback = currentListIsAttached &&
    [_observedScrollView isKindOfClass:UICollectionView.class];
  if (currentListIsAttached && !currentListIsFallback &&
      _releasedPageScrollStates.count == 0) return;
  if (!headerIsAttached) {
    [self attachScrollObserverForCurrentPage];
    return;
  }
  // Property-only updates cannot replace a list. Collect changed parents first
  // so a burst of unrelated mutations scans the current page at most once.
  std::unordered_set<NSInteger> changedParentTags;
  for (const auto &mutation : transaction.getMutations()) {
    if ((mutation.type == ShadowViewMutation::Insert ||
         mutation.type == ShadowViewMutation::Remove) && mutation.parentTag > 0) {
      changedParentTags.insert(mutation.parentTag);
    }
  }
  if (changedParentTags.empty()) return;

  // A missing list can return in a later transaction. Inspect only the current
  // page needing an observer and pages whose released identities are retained.
  for (NSInteger index = 0; index < _pageControllers.count; index++) {
    BOOL hasReleasedList = _releasedPageScrollStates[[self pageKeyForIndex:index]] != nil;
    BOOL currentPageNeedsResolve = index == _currentIndex &&
      (!currentListIsAttached || currentListIsFallback);
    if (!hasReleasedList && !currentPageNeedsResolve) continue;
    NSMutableArray<UIView *> *pendingViews =
      [NSMutableArray arrayWithObject:_pageControllers[index].view];
    while (pendingViews.count > 0) {
      UIView *view = pendingViews.lastObject;
      [pendingViews removeLastObject];
      if (changedParentTags.find(view.tag) != changedParentTags.end()) {
        if (hasReleasedList) _needsScrollObserverReattachAfterMount = YES;
        [self attachScrollObserverForCurrentPage];
        return;
      }
      [pendingViews addObjectsFromArray:view.subviews];
    }
  }
}

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView
                          index:(NSInteger)index
{
  if (_isBeingRecycled) return;
  NSInteger safeIndex = MIN(MAX(index, 0), _logicalChildren.count);
  [_logicalChildren insertObject:childComponentView atIndex:safeIndex];
  if (_topScrollEdgeEnabled || _topOverlayView != nil) {
    _needsSlotRebuild = YES;
  } else {
    [self rebuildSlots];
  }
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView
                            index:(NSInteger)index
{
  if (_isBeingRecycled) return;
  NSUInteger existingIndex = [_logicalChildren indexOfObjectIdenticalTo:childComponentView];
  if (existingIndex == NSNotFound) return;
  [childComponentView removeFromSuperview];
  [_logicalChildren removeObjectAtIndex:existingIndex];
  if (_topScrollEdgeEnabled || _topOverlayView != nil) {
    _needsSlotRebuild = YES;
  } else {
    [self rebuildSlots];
  }
}

- (void)rebuildSlots
{
  if (_deferTopEdgeSlotRebuild) {
    _needsSlotRebuild = YES;
    return;
  }
  if (_transitioning || _isPagerDragging) {
    _needsSlotRebuild = YES;
    return;
  }
  if (_topScrollEdgeEnabled &&
      _logicalChildren.count != _pageKeys.count + 2 &&
      _logicalChildren.count != _pageKeys.count + 3) {
    _needsSlotRebuild = YES;
    return;
  }
  _needsSlotRebuild = NO;
  _deferTopEdgeSlotRebuild = NO;
  [self restoreScrollEdgeContentMasks];
  [self detachScrollObserver];
  [self restoreSharedHeadersToContainer];
  // OneKey patch: Fabric reuses these views without resetting transforms
  // written by a native parent. Release our collapse translation with the slot.
  _headerView.transform = CGAffineTransformIdentity;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  _sharedHeaderHostView.transform = CGAffineTransformIdentity;
  _nativeTabBarView.transform = CGAffineTransformIdentity;
  _nativeSubHeaderView.transform = CGAffineTransformIdentity;
  [_headerView removeFromSuperview];
  [_stickyHeaderView removeFromSuperview];
  [self updateTopScrollEdgeForScrollView:nil];
  [_topOverlayView removeFromSuperview];
  for (UIViewController *controller in _pageControllers) {
    [controller.view removeFromSuperview];
  }
  _headerView = nil;
  _stickyHeaderView = nil;
  _topOverlayView = nil;
  [self updateTopScrollEdgeForScrollView:nil];
  [_pageControllers removeAllObjects];

  if (_logicalChildren.count > 0) {
    _headerView = _logicalChildren[0];
    [_sharedHeaderHostView addSubview:_headerView];
  }
  if (_logicalChildren.count > 1) {
    _stickyHeaderView = _logicalChildren[1];
    [_sharedHeaderHostView addSubview:_stickyHeaderView];
  }
  BOOL hasOverlaySlot = _topScrollEdgeEnabled &&
    _logicalChildren.count == _pageKeys.count + 3;
  NSInteger pageSlotStart = hasOverlaySlot ? 3 : 2;
  if (hasOverlaySlot) {
    _topOverlayView = _logicalChildren[2];
    [_topOverlayHostView addSubview:_topOverlayView];
  }
  [self resolveExternalTopOverlay];
  for (NSInteger index = pageSlotStart; index < _logicalChildren.count; index++) {
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
      _hasAppliedInitialPage = _directNativePagerEnabled || _pageViewController != nil;
    } else {
      _currentIndex = MIN(MAX(_currentIndex, 0), _pageControllers.count - 1);
    }
    _destinationIndex = _currentIndex;
    if (_directNativePagerEnabled) {
      for (UIViewController *controller in _pageControllers) {
        [_pagerScrollView addSubview:controller.view];
      }
      [_pagerScrollView setContentOffset:CGPointMake(
        [self directPagerOffsetForIndex:_currentIndex],
        0
      ) animated:NO];
    } else {
      UIViewController *controller = _pageControllers[_currentIndex];
      [_pageViewController setViewControllers:@[controller]
                                    direction:UIPageViewControllerNavigationDirectionForward
                                     animated:NO
                                   completion:nil];
    }
    [_nativeTabBarView setProgress:_currentIndex];
  }
  if (_stickyHeaderView != nil) [_sharedHeaderHostView bringSubviewToFront:_stickyHeaderView];
  if (!_nativeTabBarView.hidden) [_sharedHeaderHostView bringSubviewToFront:_nativeTabBarView];
  if (!_nativeSubHeaderView.hidden) [_sharedHeaderHostView bringSubviewToFront:_nativeSubHeaderView];
  [_containerView bringSubviewToFront:_sharedHeaderHostView];
  if (_stickyHeaderIsPinned) {
    [_pinnedStickyHostView.superview bringSubviewToFront:_pinnedStickyHostView];
  }
  if (self.hasFixedTopOverlay) [_containerView bringSubviewToFront:_topOverlayHostView];
  [self setNeedsLayout];
  [self scheduleCoalescedScrollReconcile];
}

- (void)layoutSubviews
{
  [super layoutSubviews];
  [self syncScrollEdgeAppearance];
  _containerView.frame = self.bounds;
  [self registerExternalOuterPager];
  BOOL externalHeightChanged = [self resolveExternalTopOverlay];
  if (_directNativePagerEnabled) {
    _pagerScrollView.frame = _containerView.bounds;
  } else {
    _pageViewController.view.frame = _containerView.bounds;
  }
  // OneKey patch: Fabric may mount page slots before applying the host props.
  // Commit the pending initial page once both props and page controllers exist.
  if (!_transitioning && !_isPagerDragging && !_hasAppliedInitialPage &&
      (_directNativePagerEnabled ? _pagerScrollView != nil : _pageViewController != nil) &&
      _pendingInitialPage >= 0 && _pendingInitialPage < _pageControllers.count) {
    [self detachScrollObserver];
    _currentIndex = _pendingInitialPage;
    _destinationIndex = _currentIndex;
    _hasAppliedInitialPage = YES;
    if (_directNativePagerEnabled) {
      [_pagerScrollView setContentOffset:CGPointMake(
        [self directPagerOffsetForIndex:_currentIndex],
        0
      ) animated:NO];
    } else {
      [_pageViewController setViewControllers:@[_pageControllers[_currentIndex]]
                                    direction:UIPageViewControllerNavigationDirectionForward
                                     animated:NO
                                   completion:nil];
    }
    [_nativeTabBarView setProgress:_currentIndex];
  }
  // UIKit frame assignment is undefined while a view has a non-identity transform.
  // Restore transforms before settling page and shared-header frames.
  _headerView.transform = CGAffineTransformIdentity;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  _sharedHeaderHostView.transform = CGAffineTransformIdentity;
  _nativeTabBarView.transform = CGAffineTransformIdentity;
  _nativeSubHeaderView.transform = CGAffineTransformIdentity;
  // Settle native page containers before sizing their React child views.
  if (_directNativePagerEnabled) {
    CGFloat width = CGRectGetWidth(_pagerScrollView.bounds);
    CGFloat height = CGRectGetHeight(_pagerScrollView.bounds);
    _pagerScrollView.contentSize = CGSizeMake(width * _pageControllers.count, height);
    [_pageControllers enumerateObjectsUsingBlock:^(UIViewController *controller,
                                                    NSUInteger index,
                                                    BOOL *stop) {
      NSInteger physicalIndex = self.isLtrLayout
        ? (NSInteger)index
        : (NSInteger)self->_pageControllers.count - 1 - (NSInteger)index;
      controller.view.frame = CGRectMake(width * physicalIndex, 0, width, height);
      controller.view.subviews.firstObject.frame = controller.view.bounds;
    }];
    if (!_transitioning && !_isPagerDragging) {
      [_pagerScrollView setContentOffset:CGPointMake(
        [self directPagerOffsetForIndex:_currentIndex],
        0
      ) animated:NO];
    }
  } else {
    [_pageViewController.view layoutIfNeeded];
    for (UIViewController *controller in _pageControllers) {
      controller.view.frame = _pageViewController.view.bounds;
      controller.view.subviews.firstObject.frame = controller.view.bounds;
    }
  }
  [self layoutSharedHeaders];
  if (externalHeightChanged) [self reapplyInsetsToObservedScrollView];
  [self updateTopScrollEdgeForScrollView:_observedScrollView];
  [self updateScrollEdgeContentMasks];
  [self updateHeaderSettledStateForceBusy:NO];
  [self logHomeVisualGeometry:@"layout" scrollView:_observedScrollView];
  [self scheduleCoalescedScrollReconcile];
}

- (void)prepareForRecycle
{
  [_externalOuterPager removeNestedOverlayObserver:self];
  _externalOuterPager = nil;
  [self cancelTopInsetReboundCheck];
  [self restoreScrollEdgeContentMasks];
  [self updateTopScrollEdgeForScrollView:nil];
  [self cancelHeaderSettledCheck];
  [self publishHeaderSettledAtTop:NO identityChanged:NO];
  _isBeingRecycled = YES;
  _pendingTopOverlayAppearanceCommit = NO;
  [self syncScrollEdgeAppearance];
  _generation++;
  [_mountingRemovedScrollViews removeAllObjects];
  _needsScrollObserverReattachAfterMount = NO;
  _awaitingScrollOwnerForCurrentPage = NO;
  _scrollReconcileScheduled = NO;
  [_releasedPageScrollStates removeAllObjects];
  [self detachSharedHeaderPressCancellationGesture];
  [self detachScrollObserver];
  [self restoreSharedHeadersToContainer];
  for (UIScrollView *scrollView in _refreshControlObservedScrollViews.allObjects) {
    [self releaseRefreshControlForScrollView:scrollView];
  }
  for (UIScrollView *scrollView in _originalInsets.keyEnumerator) {
    [self updateNativeScrollerViewport:scrollView stickyHeight:0];
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
    NSNumber *directionalLock = [_originalDirectionalLockEnabled objectForKey:scrollView];
    if (directionalLock != nil) scrollView.directionalLockEnabled = directionalLock.boolValue;
    NSNumber *adjustment = [_originalInsetAdjustmentBehavior objectForKey:scrollView];
    if (adjustment != nil) {
      scrollView.contentInsetAdjustmentBehavior = (UIScrollViewContentInsetAdjustmentBehavior)adjustment.integerValue;
    }
  }
  [_originalInsets removeAllObjects];
  [_appliedTopInsets removeAllObjects];
  [_originalIndicatorInsets removeAllObjects];
  [_originalAlwaysBounceVertical removeAllObjects];
  [_originalDirectionalLockEnabled removeAllObjects];
  [_originalInsetAdjustmentBehavior removeAllObjects];
  // OneKey patch: Fabric reuses these views without resetting transforms
  // written by a native parent. Release our collapse translation with the slot.
  _headerView.transform = CGAffineTransformIdentity;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  _nativeTabBarView.transform = CGAffineTransformIdentity;
  _nativeSubHeaderView.transform = CGAffineTransformIdentity;
  [_headerView removeFromSuperview];
  [_stickyHeaderView removeFromSuperview];
  [_topOverlayView removeFromSuperview];
  for (UIViewController *controller in _pageControllers) {
    [controller.view removeFromSuperview];
  }
  [_logicalChildren removeAllObjects];
  [_pageControllers removeAllObjects];
  _headerView = nil;
  _stickyHeaderView = nil;
  _topOverlayView = nil;
  _topOverlayHostView.hidden = YES;
  [self updateTopScrollEdgeForScrollView:nil];
  _pageViewController.dataSource = nil;
  _pageViewController.delegate = nil;
  _pagerScrollView.delegate = nil;
  if (_blockerGesture != nil) {
    [self removeGestureRecognizer:_blockerGesture];
    _blockerGesture = nil;
  }
  [_pageViewController.view removeFromSuperview];
  if (_directNativePagerEnabled) {
    [_pagerScrollView removeFromSuperview];
  }
  _pageViewController = nil;
  _pageViewControllerScrollView = nil;
  _pagerScrollView = nil;
  [super prepareForRecycle];
  [_pageOffsets removeAllObjects];
  [_commandBodyKeys removeAllObjects];
  [_alignedBodyKeys removeAllObjects];
  [_suspendedBodyKeys removeAllObjects];
  [_displayBodyOverrides removeAllObjects];
  [_pendingPageRestoreKeys removeAllObjects];
  _transitionPageOffsets = nil;
  _transitionCompensatedPages = nil;
  _syntheticPageRestore = NO;
  _finishingPageStateTransition = NO;
  _pageKeys = @[];
  _retainedPages = @"[]";
  _currentIndex = 0;
  _destinationIndex = 0;
  _pendingInitialPage = 0;
  _headerHeight = 0;
  _stickyHeaderHeight = 0;
  _topOverlayHeight = 0;
  _externalTopOverlayTag = 0;
  _externalTopOverlayView = nil;
  _externalTopOverlayHeight = 0;
  _externalOuterPageActive = NO;
  _externalOuterTransitioning = NO;
  _topScrollEdgeEnabled = NO;
  _topScrollEdgeEffect = RNCCollapsiblePagerViewTopScrollEdgeEffect::None;
  _homeVisualStartMono = 0;
  _homeVisualLastScrollLog = 0;
  _homeVisualLastLayoutLog = 0;
  _homeVisualLastGeometrySignature = nil;
  _nativeTabBarHeight = 44;
  _nativeSubHeaderHeight = 74;
  _headerOffset = 0;
  _currentLogicalOffset = 0;
  _observedListPanStartLogicalOffset = 0;
  _sharedHeaderHostView.transform = CGAffineTransformIdentity;
  _sharedHeaderHostView.frame = CGRectZero;
  _sharedHeaderHostView.layer.zPosition = 0;
  _scrollEnabled = YES;
  _nestedScrollEnabled = NO;
  _nativeSmoothHeaderScrollEnabled = NO;
  _directNativePagerEnabled = NO;
  _pendingDirectNativePagerEnabled = NO;
  _hasPendingDirectNativePagerChange = NO;
  _transitioning = NO;
  _isPagerDragging = NO;
  _sharedHeaderScrollView = nil;
  _sharedHeadersLiftedForPagerTransition = NO;
  _verticalPagerGesture.enabled = NO;
  _hasReceivedPageCommand = NO;
  _pendingGoToIndex = -1;
  _pendingGoToAnimated = YES;
  _needsSlotRebuild = NO;
  _deferTopEdgeSlotRebuild = NO;
  _hasAppliedInitialPage = NO;
  // OneKey patch: Fabric retains old props when recycling this view.
  _needsPropsReapply = YES;
  _layoutDirection = @"ltr";
  [_nativeTabBarView updateItemsJSON:@"[]"];
  [_nativeTabBarView setProgress:0];
  [_nativeSubHeaderView updateConfigJSON:@"{}"];
  _isBeingRecycled = NO;
}

#pragma mark - Props

- (void)updateProps:(const Props::Shared &)props oldProps:(const Props::Shared &)oldProps
{
  const auto &oldViewProps = *std::static_pointer_cast<const RNCCollapsiblePagerViewProps>(_props);
  const auto &newViewProps = *std::static_pointer_cast<const RNCCollapsiblePagerViewProps>(props);
  _applyingTopOverlayInsetChange =
    (oldViewProps.topScrollEdgeEffect == RNCCollapsiblePagerViewTopScrollEdgeEffect::None) !=
      (newViewProps.topScrollEdgeEffect == RNCCollapsiblePagerViewTopScrollEdgeEffect::None) ||
    oldViewProps.topOverlayHeight != newViewProps.topOverlayHeight;

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
  if (_needsPropsReapply ||
      oldViewProps.nativeSmoothHeaderScrollEnabled !=
        newViewProps.nativeSmoothHeaderScrollEnabled) {
    _nativeSmoothHeaderScrollEnabled = newViewProps.nativeSmoothHeaderScrollEnabled;
    _verticalPagerGesture.enabled = _nativeSmoothHeaderScrollEnabled;
    [self connectVerticalPagerGuard];
    [self setDirectNativePagerEnabled:
      _nativeSmoothHeaderScrollEnabled || !_nativeTabBarView.hidden];
    if (_nativeSmoothHeaderScrollEnabled) {
      [self attachSharedHeadersToScrollView:_observedScrollView];
    } else {
      [self restoreSharedHeadersToContainer];
    }
    [self updateSharedHeaderPressCancellationGesture];
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"smooth-header enabled=%d",
      _nativeSmoothHeaderScrollEnabled]);
  }
  if (_needsPropsReapply || oldViewProps.layoutDirection != newViewProps.layoutDirection) {
    _layoutDirection = RCTNSStringFromString(toString(newViewProps.layoutDirection));
    [_nativeTabBarView setLeftToRight:self.isLtrLayout];
    [_nativeSubHeaderView setLeftToRight:self.isLtrLayout];
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
  if (_needsPropsReapply || oldViewProps.topScrollEdgeEffect != newViewProps.topScrollEdgeEffect) {
    BOOL enabled = newViewProps.topScrollEdgeEffect != RNCCollapsiblePagerViewTopScrollEdgeEffect::None;
    BOOL enabledChanged = _topScrollEdgeEnabled != enabled;
    _topScrollEdgeEnabled = enabled;
    _topScrollEdgeEffect = newViewProps.topScrollEdgeEffect;
    if (enabledChanged) {
      _deferTopEdgeSlotRebuild = YES;
      [self rebuildSlots];
      NSUInteger generation = _generation;
      dispatch_async(dispatch_get_main_queue(), ^{
        if (self->_generation != generation || self->_isBeingRecycled ||
            !self->_deferTopEdgeSlotRebuild) return;
        self->_deferTopEdgeSlotRebuild = NO;
        [self rebuildSlots];
      });
    }
    insetsChanged |= enabledChanged;
  }
  if (_needsPropsReapply ||
      oldViewProps.topOverlayColorScheme != newViewProps.topOverlayColorScheme) {
    NSString *nextScheme = RCTNSStringFromString(newViewProps.topOverlayColorScheme);
    if (self.hasFixedTopOverlay && _topScrollEdgeEnabled &&
        _topOverlayColorScheme != nil &&
        ![_topOverlayColorScheme isEqualToString:nextScheme]) {
      _pendingTopOverlayAppearanceCommit = YES;
      [self updateTopScrollEdgeForScrollView:nil];
    }
    _topOverlayColorScheme = nextScheme;
    [self syncScrollEdgeAppearance];
  }
  if (_needsPropsReapply || oldViewProps.topOverlayHeight != newViewProps.topOverlayHeight) {
    _topOverlayHeight = MAX(0, newViewProps.topOverlayHeight);
    insetsChanged = YES;
  }
  if (_needsPropsReapply ||
      oldViewProps.externalTopOverlayTag != newViewProps.externalTopOverlayTag) {
    _externalTopOverlayTag = newViewProps.externalTopOverlayTag;
    [self registerExternalOuterPager];
    insetsChanged = [self resolveExternalTopOverlay] || insetsChanged;
  }
  if (_needsPropsReapply || oldViewProps.pageKeys != newViewProps.pageKeys) {
    NSString *json = RCTNSStringFromString(newViewProps.pageKeys);
    NSData *data = [json dataUsingEncoding:NSUTF8StringEncoding];
    id parsed = data == nil ? nil : [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
    NSArray<NSString *> *nextPageKeys = [parsed isKindOfClass:NSArray.class] ? parsed : @[];
    // Stable keys keep their released identity across reorders. A missing key
    // represents an unmounted React page and must not be reused by a later page.
    RNCPruneReleasedPageStates(_releasedPageScrollStates, nextPageKeys);
    _pageKeys = nextPageKeys;
  }
  if (_needsPropsReapply || oldViewProps.retainedPages != newViewProps.retainedPages) {
    _retainedPages = RCTNSStringFromString(newViewProps.retainedPages);
    [self setNeedsLayout];
  }
  if (_needsPropsReapply || oldViewProps.nativeTabBarItems != newViewProps.nativeTabBarItems) {
    NSString *itemsJSON = RCTNSStringFromString(newViewProps.nativeTabBarItems);
    [_nativeTabBarView updateItemsJSON:itemsJSON];
    [self setDirectNativePagerEnabled:
      _nativeSmoothHeaderScrollEnabled || !_nativeTabBarView.hidden];
    [self setNeedsLayout];
  }
  if (_needsPropsReapply || oldViewProps.nativeSubHeaderConfig != newViewProps.nativeSubHeaderConfig) {
    NSString *configJSON = RCTNSStringFromString(newViewProps.nativeSubHeaderConfig);
    [_nativeSubHeaderView updateConfigJSON:configJSON];
    _nativeSubHeaderHeight = _nativeSubHeaderView.preferredHeight;
    [self setNeedsLayout];
  }
  if (_needsPropsReapply ||
      oldViewProps.nativeTabBarHeight != newViewProps.nativeTabBarHeight ||
      oldViewProps.nativeTabBarContentPaddingHorizontal != newViewProps.nativeTabBarContentPaddingHorizontal ||
      oldViewProps.nativeTabBarItemSpacing != newViewProps.nativeTabBarItemSpacing ||
      oldViewProps.nativeTabBarFontSize != newViewProps.nativeTabBarFontSize ||
      oldViewProps.nativeTabBarFontFamily != newViewProps.nativeTabBarFontFamily ||
      oldViewProps.nativeTabBarBackgroundColor != newViewProps.nativeTabBarBackgroundColor ||
      oldViewProps.nativeTabBarActiveTextColor != newViewProps.nativeTabBarActiveTextColor ||
      oldViewProps.nativeTabBarInactiveTextColor != newViewProps.nativeTabBarInactiveTextColor ||
      oldViewProps.nativeTabBarIndicatorColor != newViewProps.nativeTabBarIndicatorColor ||
      oldViewProps.nativeTabBarIndicatorHeight != newViewProps.nativeTabBarIndicatorHeight ||
      oldViewProps.nativeTabBarIndicatorBottom != newViewProps.nativeTabBarIndicatorBottom ||
      oldViewProps.nativeSubHeaderSelectedBackgroundColor != newViewProps.nativeSubHeaderSelectedBackgroundColor) {
    _nativeTabBarHeight = newViewProps.nativeTabBarHeight > 0
      ? newViewProps.nativeTabBarHeight
      : 44;
    NSString *fontFamily = RCTNSStringFromString(newViewProps.nativeTabBarFontFamily);
    [_nativeTabBarView
      updateStyleWithHeight:_nativeTabBarHeight
      contentPaddingHorizontal:MAX(0, newViewProps.nativeTabBarContentPaddingHorizontal)
      itemSpacing:MAX(0, newViewProps.nativeTabBarItemSpacing)
      fontSize:newViewProps.nativeTabBarFontSize > 0 ? newViewProps.nativeTabBarFontSize : 16
      fontFamily:fontFamily
      backgroundColor:RCTUIColorFromSharedColor(newViewProps.nativeTabBarBackgroundColor)
      activeTextColor:RCTUIColorFromSharedColor(newViewProps.nativeTabBarActiveTextColor)
      inactiveTextColor:RCTUIColorFromSharedColor(newViewProps.nativeTabBarInactiveTextColor)
      indicatorColor:RCTUIColorFromSharedColor(newViewProps.nativeTabBarIndicatorColor)
      indicatorHeight:MAX(0, newViewProps.nativeTabBarIndicatorHeight)
      indicatorBottom:MAX(0, newViewProps.nativeTabBarIndicatorBottom)];
    [_nativeSubHeaderView
      updateColorsWithBackgroundColor:RCTUIColorFromSharedColor(newViewProps.nativeTabBarBackgroundColor)
      activeTextColor:RCTUIColorFromSharedColor(newViewProps.nativeTabBarActiveTextColor)
      inactiveTextColor:RCTUIColorFromSharedColor(newViewProps.nativeTabBarInactiveTextColor)
      selectedBackgroundColor:RCTUIColorFromSharedColor(newViewProps.nativeSubHeaderSelectedBackgroundColor)
      fontFamily:fontFamily];
    [self setNeedsLayout];
  }

  _needsPropsReapply = NO;
  [super updateProps:props oldProps:oldProps];
  [self updateTopScrollEdgeForScrollView:_observedScrollView];
  if (insetsChanged) {
    [self reapplyInsetsToObservedScrollView];
    [self setNeedsLayout];
    [self logHomeVisualGeometry:@"props-inset" scrollView:_observedScrollView];
  }
  _applyingTopOverlayInsetChange = NO;
  if (!_nativeTabBarView.hidden) {
    [_nativeTabBarView.superview bringSubviewToFront:_nativeTabBarView];
  }
  if (!_nativeSubHeaderView.hidden) {
    [_nativeSubHeaderView.superview bringSubviewToFront:_nativeSubHeaderView];
  }
  [_sharedHeaderHostView.superview bringSubviewToFront:_sharedHeaderHostView];
  if (_stickyHeaderIsPinned) {
    [_pinnedStickyHostView.superview bringSubviewToFront:_pinnedStickyHostView];
  }
  if (self.hasFixedTopOverlay) [_containerView bringSubviewToFront:_topOverlayHostView];
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

- (UIScrollView *)currentVerticalScrollViewForParentPager
{
  return [self verticalScrollViewForPageAtIndex:_currentIndex];
}

- (UIScrollView *)verticalScrollViewForPageAtIndex:(NSInteger)pageIndex
{
  return [self resolveScrollCandidateForPageAtIndex:pageIndex].scrollView;
}

- (RNCScrollCandidateResolution)resolveScrollCandidateForPageAtIndex:(NSInteger)pageIndex
{
  if (pageIndex < 0 || pageIndex >= _pageControllers.count) return {};
  return [self resolveScrollCandidateInView:_pageControllers[pageIndex].view];
}

// Single-pass tiered resolution replaces three separate subtree walks while
// distinguishing genuinely static pages from pages with unready scroll hosts.
- (RNCScrollCandidateResolution)resolveScrollCandidateInView:(UIView *)view
{
  RNCScrollCandidateResolution resolution;
  if (view == nil || view == _sharedHeaderHostView) return resolution;
  if (view != self &&
      [view isKindOfClass:RNCCollapsiblePagerViewComponentView.class]) {
    UIScrollView *nested = [(RNCCollapsiblePagerViewComponentView *)view
      currentVerticalScrollViewForParentPager];
    if (nested != nil) {
      resolution.scrollView = nested;
      resolution.tier = RNCScrollCandidateTierExplicitScroller;
    } else {
      resolution.hasUnreadyScrollHost = YES;
    }
    return resolution;
  }

  Class nativeListClass = NSClassFromString(@"HybridNativeListComponent");
  if (nativeListClass != Nil && [view isKindOfClass:nativeListClass]) {
    resolution.hasUnreadyScrollHost = YES;
  }

  if ([view isKindOfClass:RNCNativeScrollerComponentView.class]) {
    UIScrollView *scrollView = ((RNCNativeScrollerComponentView *)view).scrollView;
    if (![_mountingRemovedScrollViews containsObject:scrollView]) {
      resolution.scrollView = scrollView;
      resolution.tier = RNCScrollCandidateTierExplicitScroller;
      return resolution;
    }
  } else if ([view isKindOfClass:RCTScrollViewComponentView.class]) {
    RCTScrollViewComponentView *componentView = (RCTScrollViewComponentView *)view;
    UIScrollView *scrollView = componentView.scrollView;
    if (![_mountingRemovedScrollViews containsObject:scrollView]) {
      if ([componentView.nativeId hasPrefix:RNCCollapsiblePagerNativeScrollerIDPrefix]) {
        resolution.scrollView = scrollView;
        resolution.tier = RNCScrollCandidateTierExplicitScroller;
        return resolution;
      }
      if (!scrollView.alwaysBounceHorizontal) {
        resolution.scrollView = scrollView;
        resolution.tier = RNCScrollCandidateTierFabricScrollView;
      }
    }
  } else if ([view isKindOfClass:UICollectionView.class]) {
    UICollectionView *collectionView = (UICollectionView *)view;
    if (![_mountingRemovedScrollViews containsObject:collectionView]) {
      UICollectionViewLayout *layout = collectionView.collectionViewLayout;
      BOOL isVerticalLayout = ![layout isKindOfClass:UICollectionViewFlowLayout.class] ||
        ((UICollectionViewFlowLayout *)layout).scrollDirection == UICollectionViewScrollDirectionVertical;
      if (isVerticalLayout) {
        BOOL hasNativeListHost = RNCNativeListComponentForScrollView(collectionView) != nil;
        BOOL isMountedNativeList = hasNativeListHost && RNCIsMountedNativeListScrollView(collectionView);
        BOOL hasUsableBounds = CGRectGetWidth(collectionView.bounds) > 0 &&
          CGRectGetHeight(collectionView.bounds) > 0;
        if (hasNativeListHost && !isMountedNativeList) {
          resolution.hasUnreadyScrollHost = YES;
        } else if (!hasUsableBounds) {
          resolution.hasUnreadyScrollHost = YES;
        } else {
          resolution.scrollView = collectionView;
          resolution.tier = isMountedNativeList
            ? RNCScrollCandidateTierVerifiedNativeList
            : RNCScrollCandidateTierGenericFallback;
        }
      }
    }
  } else if ([view isKindOfClass:UIScrollView.class]) {
    UIScrollView *scrollView = (UIScrollView *)view;
    if (![_mountingRemovedScrollViews containsObject:scrollView] &&
        !scrollView.alwaysBounceHorizontal &&
        scrollView.contentSize.width <= scrollView.bounds.size.width + 1) {
      resolution.scrollView = scrollView;
      resolution.tier = RNCScrollCandidateTierGenericFallback;
    }
  }

  for (UIView *subview in view.subviews) {
    RNCScrollCandidateResolution child = [self resolveScrollCandidateInView:subview];
    resolution.hasUnreadyScrollHost |= child.hasUnreadyScrollHost;
    if (child.tier > resolution.tier) {
      resolution.scrollView = child.scrollView;
      resolution.tier = child.tier;
      if (resolution.tier == RNCScrollCandidateTierExplicitScroller) {
        return resolution;
      }
    }
  }
  return resolution;
}

- (UIScrollView *)findExplicitNativeScrollerInView:(UIView *)view
{
  if (view == _sharedHeaderHostView) return nil;
  if (view != self &&
      [view isKindOfClass:RNCCollapsiblePagerViewComponentView.class]) {
    return [(RNCCollapsiblePagerViewComponentView *)view
      currentVerticalScrollViewForParentPager];
  }
  if ([view isKindOfClass:RNCNativeScrollerComponentView.class]) {
    UIScrollView *scrollView = ((RNCNativeScrollerComponentView *)view).scrollView;
    return [_mountingRemovedScrollViews containsObject:scrollView] ? nil : scrollView;
  }
  if ([view isKindOfClass:RCTScrollViewComponentView.class]) {
    RCTScrollViewComponentView *componentView =
      (RCTScrollViewComponentView *)view;
    if ([componentView.nativeId hasPrefix:RNCCollapsiblePagerNativeScrollerIDPrefix]) {
      UIScrollView *scrollView = componentView.scrollView;
      return [_mountingRemovedScrollViews containsObject:scrollView] ? nil : scrollView;
    }
  }
  for (UIView *subview in view.subviews) {
    UIScrollView *candidate = [self findExplicitNativeScrollerInView:subview];
    if (candidate != nil) return candidate;
  }
  return nil;
}

- (UIScrollView *)findFabricScrollViewInView:(UIView *)view
{
  if (view == _sharedHeaderHostView) return nil;
  if (view != self &&
      [view isKindOfClass:RNCCollapsiblePagerViewComponentView.class]) {
    return [(RNCCollapsiblePagerViewComponentView *)view
      currentVerticalScrollViewForParentPager];
  }
  if ([view isKindOfClass:RCTScrollViewComponentView.class]) {
    UIScrollView *scrollView =
      ((RCTScrollViewComponentView *)view).scrollView;
    if (!scrollView.alwaysBounceHorizontal &&
        ![_mountingRemovedScrollViews containsObject:scrollView]) {
      return scrollView;
    }
  }
  for (UIView *subview in view.subviews) {
    UIScrollView *candidate = [self findFabricScrollViewInView:subview];
    if (candidate != nil) return candidate;
  }
  return nil;
}

- (UIScrollView *)findVerticalScrollViewInView:(UIView *)view
{
  if (view == _sharedHeaderHostView ||
      ([view isKindOfClass:UIScrollView.class] &&
       [_mountingRemovedScrollViews containsObject:(UIScrollView *)view])) return nil;
  // A parent pager must follow the nested pager's active page instead of the
  // first retained page found by recursive subview order.
  if (view != self &&
      [view isKindOfClass:RNCCollapsiblePagerViewComponentView.class]) {
    UIScrollView *nestedScrollView =
      [(RNCCollapsiblePagerViewComponentView *)view
        currentVerticalScrollViewForParentPager];
    if (nestedScrollView != nil) return nestedScrollView;
  }
  if ([view isKindOfClass:UICollectionView.class]) {
    UICollectionView *collectionView = (UICollectionView *)view;
    if (RNCNativeListComponentForScrollView(collectionView) != nil &&
        !RNCIsMountedNativeListScrollView(collectionView)) return nil;
    UICollectionViewLayout *layout = collectionView.collectionViewLayout;
    BOOL hasUsableBounds = CGRectGetWidth(collectionView.bounds) > 0 &&
      CGRectGetHeight(collectionView.bounds) > 0;
    if (hasUsableBounds &&
        (![layout isKindOfClass:UICollectionViewFlowLayout.class] ||
         ((UICollectionViewFlowLayout *)layout).scrollDirection == UICollectionViewScrollDirectionVertical)) {
      return collectionView;
    }
  }
  // Preserve compatibility with non-Fabric or custom vertical scroll views.
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

- (void)scheduleCoalescedScrollReconcile
{
  if (_isBeingRecycled || _scrollReconcileScheduled) return;
  _scrollReconcileScheduled = YES;
  NSUInteger generation = _generation;
  dispatch_async(dispatch_get_main_queue(), ^{
    self->_scrollReconcileScheduled = NO;
    if (self->_isBeingRecycled || self->_generation != generation) return;
    [self prepareAdjacentPageInsets];
    [self attachScrollObserverForCurrentPage];
  });
}

- (void)scheduleScrollObserverRetryForCurrentPage
{
  if (_scrollResolveRetryScheduled || _scrollResolveRetryAttempt >= 12 ||
      _isBeingRecycled) return;
  _scrollResolveRetryScheduled = YES;
  _scrollResolveRetryPageIndex = _currentIndex;
  _scrollResolveRetryAttempt += 1;
  NSUInteger token = _scrollResolveRetryToken;
  NSUInteger generation = _generation;
  NSInteger pageIndex = _currentIndex;
  dispatch_after(
    // Fabric can mount a recycled child's native view before attaching its
    // replacement ScrollView component. Let that transaction settle before
    // accepting an automatically discovered UICollectionView fallback.
    dispatch_time(DISPATCH_TIME_NOW, (int64_t)(100 * NSEC_PER_MSEC)),
    dispatch_get_main_queue(),
    ^{
      if (self->_scrollResolveRetryToken != token) return;
      self->_scrollResolveRetryScheduled = NO;
      if (self->_isBeingRecycled || self->_generation != generation ||
          self->_currentIndex != pageIndex) return;
      [self attachScrollObserverForCurrentPage];
    }
  );
}

- (void)attachScrollObserverForCurrentPage
{
  [self attachScrollObserverForCurrentPageAfterTransition:NO];
}

// OneKey patch: selection completion restores list state synchronously, before
// the transition flags are cleared on the next run loop. Other callers wait.
- (void)attachScrollObserverForCurrentPageAfterTransition:(BOOL)selectionSettled
{
  if (_isBeingRecycled) return;
  if (_nativeSmoothHeaderScrollEnabled &&
      (_transitioning || _isPagerDragging || _transitionPageOffsets != nil) && !selectionSettled) return;
  if (selectionSettled) [self finishPageStateTransition];
  if (_needsScrollObserverReattachAfterMount) {
    if (self.window == nil || ((_transitioning || _isPagerDragging) && !selectionSettled)) return;
    [self prepareAdjacentPageInsets];
  }
  [self restoreDetachedScrollInsets];
  if (_scrollResolveRetryPageIndex != _currentIndex) {
    _scrollResolveRetryToken += 1;
    _scrollResolveRetryScheduled = NO;
    _scrollResolveRetryAttempt = 0;
    _scrollResolveRetryPageIndex = _currentIndex;
    _pendingFallbackScrollView = nil;
    _pendingFallbackScrollViewDetectedAt = 0;
  }
  RNCScrollCandidateResolution resolution = [self resolveScrollCandidateForPageAtIndex:_currentIndex];
  UIScrollView *candidate = resolution.scrollView;
  if (_nativeSmoothHeaderScrollEnabled && _currentIndex >= 0 && _currentIndex < _pageControllers.count) {
    RNCGuardNestedHorizontalPans(_pageControllers[_currentIndex].view, _verticalPagerGesture);
  }
  if (candidate == nil) {
    [self detachScrollObserver];
    [self restoreSharedHeadersToContainer];
    _needsScrollObserverReattachAfterMount = NO;
    _pendingFallbackScrollView = nil;
    _pendingFallbackScrollViewDetectedAt = 0;
    _awaitingScrollOwnerForCurrentPage = YES;
    // Only schedule bounded retry when the page has an unready scroll host in
    // its subtree or a released scroll owner awaiting replacement. Static pages
    // without a scroll host do not spin retry timers, and late replacements on
    // any page also attach deterministically via readiness notifications.
    BOOL hasReleasedOwnerAwaitingReplacement = _releasedPageScrollStates[self.currentPageKey] != nil;
    if (resolution.hasUnreadyScrollHost || hasReleasedOwnerAwaitingReplacement) {
      [self scheduleScrollObserverRetryForCurrentPage];
    } else {
      _scrollResolveRetryToken += 1;
      _scrollResolveRetryScheduled = NO;
      _scrollResolveRetryAttempt = 0;
    }
    return;
  }
  if ([candidate isKindOfClass:UICollectionView.class] &&
      candidate != _observedScrollView && !RNCIsMountedNativeListScrollView(candidate)) {
    _awaitingScrollOwnerForCurrentPage = YES;
    if (_pendingFallbackScrollView != candidate) {
      _pendingFallbackScrollView = candidate;
      _pendingFallbackScrollViewDetectedAt = CACurrentMediaTime();
      _scrollResolveRetryAttempt = 0;
    }
    if (CACurrentMediaTime() - _pendingFallbackScrollViewDetectedAt <
        RNCCollapsiblePagerFallbackDebounceInterval) {
      [self scheduleScrollObserverRetryForCurrentPage];
      return;
    }
  }
  _awaitingScrollOwnerForCurrentPage = NO;
  _scrollResolveRetryAttempt = 0;
  _scrollResolveRetryPageIndex = _currentIndex;
  _scrollResolveRetryToken += 1;
  _scrollResolveRetryScheduled = NO;
  _pendingFallbackScrollView = nil;
  _pendingFallbackScrollViewDetectedAt = 0;
  // Legacy mode keeps the historical pager-first dependency. Smooth mode
  // arbitrates header touches by axis so the vertical list can begin directly.
  if (_pagerScrollView != nil && !_nativeSmoothHeaderScrollEnabled && !_needsPropsReapply) {
    [candidate.panGestureRecognizer requireGestureRecognizerToFail:_pagerScrollView.panGestureRecognizer];
  }
  if (candidate == _observedScrollView &&
      (!_nativeSmoothHeaderScrollEnabled ||
       ([_observedPageKey isEqualToString:self.currentPageKey] &&
        _observedPageIndex == _currentIndex && _observedPageGeneration == _generation))) {
    _needsScrollObserverReattachAfterMount = NO;
    if ([_pendingPageRestoreKeys containsObject:self.currentPageKey]) {
      [self applyInsetsToScrollView:candidate pageIndex:_currentIndex restore:YES];
    }
    [self attachSharedHeadersToScrollView:candidate];
    [self updateSharedHeaderPressCancellationGesture];
    return;
  }

  BOOL replacingCurrentScrollView = _observingContentOffset &&
    (!_nativeSmoothHeaderScrollEnabled || [_observedPageKey isEqualToString:self.currentPageKey]);
  [self detachScrollObserver];
  if (replacingCurrentScrollView) {
    // Legacy replacement discards body state; smooth replacement waits for layout.
    if (!_nativeSmoothHeaderScrollEnabled) _pageOffsets[self.currentPageKey] = @(_headerOffset);
  }
  _observedScrollView = candidate;
  _observedPageKey = [self.currentPageKey copy];
  _observedPageIndex = _currentIndex;
  _observedPageGeneration = _generation;
  [self applyInsetsToScrollView:candidate pageIndex:_currentIndex restore:YES];
  [candidate addObserver:self
              forKeyPath:@"contentOffset"
                 options:NSKeyValueObservingOptionOld | NSKeyValueObservingOptionNew
                 context:RNCCollapsiblePagerContentOffsetContext];
  [candidate addObserver:self
              forKeyPath:@"contentInset"
                 options:NSKeyValueObservingOptionNew
                 context:RNCCollapsiblePagerContentOffsetContext];
  [candidate addObserver:self
              forKeyPath:@"contentSize"
                 options:NSKeyValueObservingOptionOld | NSKeyValueObservingOptionNew
                 context:RNCCollapsiblePagerContentOffsetContext];
  [candidate.panGestureRecognizer addTarget:self action:@selector(observedListPanChanged:)];
  _observingContentOffset = YES;
  _needsScrollObserverReattachAfterMount = NO;
  _currentLogicalOffset = candidate.contentOffset.y + candidate.contentInset.top;
  [self updateTopScrollEdgeForScrollView:candidate];
  [self attachSharedHeadersToScrollView:candidate];
  [self updateSharedHeaderPressCancellationGesture];
  [self updateHeaderSettledStateForceBusy:NO];
}

- (void)detachScrollObserver
{
  [self cancelTopInsetReboundCheck];
  [self restorePinnedStickyHeader];
  [self updateTopScrollEdgeForScrollView:nil];
  [self cancelHeaderSettledCheck];
  _headerCommandOwner = nil;
  _headerSettledGeometry = nil;
  _headerSettledWasBusy = YES;
  [self publishHeaderSettledAtTop:NO identityChanged:NO];
  [self cancelPagerTop];
  UIScrollView *scrollView = _observedScrollView;
  if (scrollView != nil) {
    [scrollView.panGestureRecognizer removeTarget:self action:@selector(observedListPanChanged:)];
    NSString *ownerKey = _nativeSmoothHeaderScrollEnabled ? _observedPageKey : self.currentPageKey;
    BOOL ownerKeyExists = ownerKey != nil && ([_pageKeys containsObject:ownerKey] ||
      (_observedPageIndex >= 0 && _observedPageIndex < _pageControllers.count &&
       [[self pageKeyForIndex:_observedPageIndex] isEqualToString:ownerKey]));
    if (ownerKey != nil && !_nativeSmoothHeaderScrollEnabled) {
      _pageOffsets[ownerKey] = @(scrollView.contentOffset.y + scrollView.contentInset.top);
    }
    if (_observingContentOffset) {
      [scrollView removeObserver:self
                      forKeyPath:@"contentOffset"
                         context:RNCCollapsiblePagerContentOffsetContext];
      [scrollView removeObserver:self
                      forKeyPath:@"contentInset"
                         context:RNCCollapsiblePagerContentOffsetContext];
      [scrollView removeObserver:self
                      forKeyPath:@"contentSize"
                         context:RNCCollapsiblePagerContentOffsetContext];
    }
  }
  if (!_nativeSmoothHeaderScrollEnabled && _observedPageKey != nil) {
    [_alignedBodyKeys removeObject:_observedPageKey];
  }
  _observingContentOffset = NO;
  _observedScrollView = nil;
  _observedPageKey = nil;
  _observedPageIndex = NSNotFound;
  if ([scrollView.superview isKindOfClass:RNCNativeScrollerComponentView.class]) {
    [(RNCNativeScrollerComponentView *)scrollView.superview stopScrolling];
  }
}

- (void)detachSharedHeaderPressCancellationGesture
{
  if (_contentPressCancellationGesture.view != nil) {
    [_contentPressCancellationGesture.view removeGestureRecognizer:_contentPressCancellationGesture];
  }
  _contentPressCancellationGesture.reactTouchHandler = nil;
  _verticalPagerGesture.contentPressCancellation = nil;
  _contentPressCancellationGesture = nil;
  if (_sharedHeaderPressCancellationGesture.view != nil) {
    [_sharedHeaderPressCancellationGesture.view
      removeGestureRecognizer:_sharedHeaderPressCancellationGesture];
  }
  if (_sharedHeaderOuterPagerGesture.view != nil) {
    [_sharedHeaderOuterPagerGesture.view
      removeGestureRecognizer:_sharedHeaderOuterPagerGesture];
  }
  _sharedHeaderOuterPagerGesture.blockedPagerGestures = nil;
  _sharedHeaderOuterPagerGesture.trackedTouch = nil;
  _sharedHeaderOuterPagerGesture.protectsNestedBody = NO;
  _sharedHeaderOuterPagerGesture.cancelsTouchesInView = YES;
  _sharedHeaderPressCancellationGestureHost = nil;
  _reactTouchHandler = nil;
}

- (void)connectVerticalPagerGuard
{
  if (_pagerScrollView != nil && _verticalPagerGesture != nil) {
    _verticalPagerGesture.pagerPan = _pagerScrollView.panGestureRecognizer;
    [_pagerScrollView.panGestureRecognizer requireGestureRecognizerToFail:_verticalPagerGesture];
  }
}

- (void)updateSharedHeaderPressCancellationGesture
{
  if (!_nativeSmoothHeaderScrollEnabled || self.window == nil || _isBeingRecycled) {
    [self detachSharedHeaderPressCancellationGesture];
    return;
  }

  UIGestureRecognizer *reactTouchHandler = nil;
  for (UIView *ancestor = self; ancestor != nil; ancestor = ancestor.superview) {
    for (UIGestureRecognizer *gestureRecognizer in ancestor.gestureRecognizers) {
      if ([gestureRecognizer isKindOfClass:RCTSurfaceTouchHandler.class] ||
          [gestureRecognizer isKindOfClass:RCTTouchHandler.class]) {
        reactTouchHandler = gestureRecognizer;
        break;
      }
    }
    if (reactTouchHandler != nil) break;
  }

  UIView *gestureHost = reactTouchHandler.view.superview;
  if (reactTouchHandler == nil || gestureHost == nil) {
    [self detachSharedHeaderPressCancellationGesture];
    RNCCollapsiblePagerLog(@"press-cancel-bridge attached=0 reason=touch-handler-missing");
    return;
  }

  NSHashTable<UIGestureRecognizer *> *outerPagerGestures = [NSHashTable weakObjectsHashTable];
  for (UIView *ancestor = self.superview;
       ancestor != nil;
       ancestor = ancestor.superview) {
    if (![ancestor isKindOfClass:UIScrollView.class]) continue;
    UIScrollView *scrollView = (UIScrollView *)ancestor;
    if (scrollView.pagingEnabled) {
      [outerPagerGestures addObject:scrollView.panGestureRecognizer];
    }
  }
  if (_sharedHeaderPressCancellationGesture.view == gestureHost &&
      _sharedHeaderOuterPagerGesture.view == _containerView &&
      _reactTouchHandler == reactTouchHandler) {
    _sharedHeaderOuterPagerGesture.blockedPagerGestures = outerPagerGestures;
    return;
  }

  [self detachSharedHeaderPressCancellationGesture];
  if (_sharedHeaderPressCancellationGesture == nil) {
    _sharedHeaderPressCancellationGesture =
      [[RNCCollapsiblePagerPressCancellationPanGestureRecognizer alloc]
        initWithTarget:self
        action:@selector(sharedHeaderPressCancellationGestureChanged:)];
    _sharedHeaderPressCancellationGesture.delegate = self;
    _sharedHeaderPressCancellationGesture.cancelsTouchesInView = NO;
    _sharedHeaderPressCancellationGesture.maximumNumberOfTouches = 1;
  }
  if (_sharedHeaderOuterPagerGesture == nil) {
    _sharedHeaderOuterPagerGesture =
      [[RNCCollapsiblePagerOuterPagerPanGestureRecognizer alloc]
        initWithTarget:self
        action:@selector(outerPagerGestureChanged:)];
    _sharedHeaderOuterPagerGesture.delegate = self;
    _sharedHeaderOuterPagerGesture.cancelsTouchesInView = YES;
    _sharedHeaderOuterPagerGesture.maximumNumberOfTouches = 1;
  }
  _sharedHeaderOuterPagerGesture.blockedPagerGestures = outerPagerGestures;
  if (_contentPressCancellationGesture == nil) {
    _contentPressCancellationGesture =
      [RNCCollapsiblePagerContentPressCancellationGestureRecognizer new];
    _contentPressCancellationGesture.delegate = self;
    // RN's external-recognizer path cancels its responder immediately only
    // when this flag is set; otherwise reset can be deferred until finger-up.
    _contentPressCancellationGesture.cancelsTouchesInView = YES;
    _contentPressCancellationGesture.delaysTouchesBegan = NO;
    _contentPressCancellationGesture.delaysTouchesEnded = NO;
  }
  _contentPressCancellationGesture.reactTouchHandler = reactTouchHandler;
  _verticalPagerGesture.contentPressCancellation = _contentPressCancellationGesture;
  [gestureHost addGestureRecognizer:_contentPressCancellationGesture];
  _reactTouchHandler = reactTouchHandler;
  _sharedHeaderPressCancellationGestureHost = gestureHost;
  [gestureHost addGestureRecognizer:_sharedHeaderPressCancellationGesture];
  [_containerView addGestureRecognizer:_sharedHeaderOuterPagerGesture];
  RNCCollapsiblePagerLog([NSString stringWithFormat:
    @"press-cancel-bridge attached=1 outer-pagers=%lu",
    (unsigned long)outerPagerGestures.count]);
}

- (void)sharedHeaderPressCancellationGestureChanged:(UIPanGestureRecognizer *)recognizer
{
  if (recognizer.state == UIGestureRecognizerStateBegan) {
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"press-cancel-owner=header page=%ld touch-handler-state=%ld",
      (long)_currentIndex,
      (long)_reactTouchHandler.state]);
  } else if (recognizer.state == UIGestureRecognizerStateEnded ||
             recognizer.state == UIGestureRecognizerStateCancelled ||
             recognizer.state == UIGestureRecognizerStateFailed) {
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"press-cancel-end page=%ld state=%ld touch-handler-state=%ld",
      (long)_currentIndex,
      (long)recognizer.state,
      (long)_reactTouchHandler.state]);
  }
}

- (void)outerPagerGestureChanged:(UIPanGestureRecognizer *)recognizer
{
  if (recognizer.state == UIGestureRecognizerStateBegan) {
    NSMutableArray<NSString *> *blockedStates = [NSMutableArray new];
    for (UIGestureRecognizer *pagerGesture in
         _sharedHeaderOuterPagerGesture.blockedPagerGestures) {
      [blockedStates addObject:[NSString stringWithFormat:
        @"%@:%ld",
        NSStringFromClass(pagerGesture.view.class),
        (long)pagerGesture.state]];
    }
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"gesture-owner=header-outer-pager-block page=%ld blocked=[%@] rct=%ld list=%ld inner-pager=%ld",
      (long)_currentIndex,
      [blockedStates componentsJoinedByString:@","],
      (long)_reactTouchHandler.state,
      (long)_observedScrollView.panGestureRecognizer.state,
      (long)_pagerScrollView.panGestureRecognizer.state]);
  } else if (recognizer.state == UIGestureRecognizerStateEnded ||
             recognizer.state == UIGestureRecognizerStateCancelled ||
             recognizer.state == UIGestureRecognizerStateFailed) {
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"gesture-end=header-outer-pager-block page=%ld state=%ld",
      (long)_currentIndex,
      (long)recognizer.state]);
  }
}

- (void)verticalPagerGuardGestureChanged:(UIPanGestureRecognizer *)recognizer
{
  if (recognizer.state == UIGestureRecognizerStateBegan) {
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"gesture-owner=vertical-pager-guard page=%ld list=%ld pager=%ld",
      (long)_currentIndex,
      (long)_observedScrollView.panGestureRecognizer.state,
      (long)_pagerScrollView.panGestureRecognizer.state]);
  } else if (recognizer.state == UIGestureRecognizerStateEnded ||
             recognizer.state == UIGestureRecognizerStateCancelled ||
             recognizer.state == UIGestureRecognizerStateFailed) {
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"gesture-end=vertical-pager-guard page=%ld state=%ld",
      (long)_currentIndex,
      (long)recognizer.state]);
  }
}

- (void)observedListPanChanged:(UIPanGestureRecognizer *)recognizer
{
  if (recognizer == _pendingTopInsetReboundScrollView.panGestureRecognizer &&
      (recognizer.state == UIGestureRecognizerStateBegan ||
       recognizer.state == UIGestureRecognizerStateChanged)) {
    [self cancelTopInsetReboundCheck];
  }
  if (recognizer == _observedScrollView.panGestureRecognizer) {
    [self updateHeaderSettledStateForceBusy:
      recognizer.state == UIGestureRecognizerStateBegan || recognizer.state == UIGestureRecognizerStateChanged];
  }
  if (!_nativeSmoothHeaderScrollEnabled || recognizer != _observedScrollView.panGestureRecognizer) {
    return;
  }
  if (recognizer.state == UIGestureRecognizerStateBegan) {
    _observedListPanStartLogicalOffset =
      _observedScrollView.contentOffset.y + _observedScrollView.contentInset.top;
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"gesture-owner=list page=%ld logical=%.2f content-height=%.2f "
       "bounds-height=%.2f inset=(%.2f,%.2f) enabled=%d dragging=%d",
      (long)_currentIndex,
      _observedListPanStartLogicalOffset,
      _observedScrollView.contentSize.height,
      _observedScrollView.bounds.size.height,
      _observedScrollView.contentInset.top,
      _observedScrollView.contentInset.bottom,
      _observedScrollView.isScrollEnabled,
      _observedScrollView.isDragging]);
  } else if (recognizer.state == UIGestureRecognizerStateEnded ||
             recognizer.state == UIGestureRecognizerStateCancelled ||
             recognizer.state == UIGestureRecognizerStateFailed) {
    CGFloat minimumOffset = -_observedScrollView.contentInset.top;
    CGFloat maximumOffset = MAX(
      minimumOffset,
      _observedScrollView.contentSize.height - _observedScrollView.bounds.size.height +
        _observedScrollView.contentInset.bottom
    );
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"gesture-end=list page=%ld state=%ld logical-start=%.2f "
       "logical-end=%.2f header=%.2f range=(%.2f,%.2f) "
       "content-height=%.2f bounds-height=%.2f inset=(%.2f,%.2f)",
      (long)_currentIndex,
      (long)recognizer.state,
      _observedListPanStartLogicalOffset,
      _observedScrollView.contentOffset.y + _observedScrollView.contentInset.top,
      _headerOffset,
      minimumOffset,
      maximumOffset,
      _observedScrollView.contentSize.height,
      _observedScrollView.bounds.size.height,
      _observedScrollView.contentInset.top,
      _observedScrollView.contentInset.bottom]);
  }
}

- (void)updateRefreshControlForScrollView:(UIScrollView *)scrollView
{
  if (!_nativeSmoothHeaderScrollEnabled || _isBeingRecycled) {
    [self releaseRefreshControlForScrollView:scrollView];
    return;
  }
  if (![_refreshControlObservedScrollViews containsObject:scrollView]) {
    [_refreshControlObservedScrollViews addObject:scrollView];
    [scrollView addObserver:self forKeyPath:@"refreshControl"
                   options:NSKeyValueObservingOptionNew
                   context:RNCScrollRefreshControlContext];
  }
  UIRefreshControl *previous = [_managedRefreshControls objectForKey:scrollView];
  UIRefreshControl *current = scrollView.refreshControl;
  if (previous != current) {
    [RNCRefreshControlOffsetState removeControl:previous owner:self];
    [_managedRefreshControls removeObjectForKey:scrollView];
    if (current != nil) [_managedRefreshControls setObject:current forKey:scrollView];
  }
  [RNCRefreshControlOffsetState setOffset:_headerHeight + _stickyHeaderHeight
                             forControl:current owner:self];
  if (previous != current) [self logHomeVisualGeometry:@"refresh-bind" scrollView:scrollView];
}

- (void)logHomeVisualGeometry:(NSString *)phase scrollView:(UIScrollView *)scrollView
{
#if DEBUG
  if (self.window == nil || _isBeingRecycled) return;
  CFTimeInterval now = CACurrentMediaTime();
  if (_homeVisualStartMono == 0) _homeVisualStartMono = now;
  if ([phase isEqualToString:@"pull"] && now - _homeVisualLastScrollLog < 0.2) return;
  if ([phase isEqualToString:@"pull"]) _homeVisualLastScrollLog = now;
  UIRefreshControl *control = scrollView.refreshControl;
  CGRect controlWindow = control != nil
    ? [control convertRect:control.bounds toView:self.window] : CGRectZero;
  CGRect overlayWindow = [_topOverlayHostView convertRect:_topOverlayHostView.bounds
                                                   toView:self.window];
  CGRect stickyWindow = [_stickyHeaderView convertRect:_stickyHeaderView.bounds
                                                toView:self.window];
  CGRect maskWindow = _sharedHeaderMaskView != nil
    ? [_sharedHeaderHostView convertRect:_sharedHeaderMaskView.frame toView:self.window]
    : CGRectZero;
  CGFloat pageMaskWindowY = [_containerView convertPoint:
    CGPointMake(0, _pageMaskBoundary) toView:self.window].y;
  BOOL layoutPhase = [phase isEqualToString:@"layout"] ||
    [phase isEqualToString:@"did-mount"];
  NSString *signature = [NSString stringWithFormat:
    @"%.1f:%ld:%ld:%.1f:%d:%.1f:%.1f:%.1f:%d:%d",
    self.effectiveTopOverlayHeight, (long)_headerHeight,
    (long)_stickyHeaderHeight, scrollView.contentInset.top, control != nil,
    CGRectGetMinY(maskWindow), CGRectGetHeight(maskWindow), _pageMaskBoundary,
    _stickyHeaderIsPinned, _topScrollEdgeEnabled];
  if (layoutPhase && now - _homeVisualStartMono >= 2.0 &&
      [_homeVisualLastGeometrySignature isEqualToString:signature] &&
      now - _homeVisualLastLayoutLog < 2.0) return;
  if (layoutPhase) {
    _homeVisualLastLayoutLog = now;
    _homeVisualLastGeometrySignature = signature;
  }
  CGFloat visualYEstimate = CGRectGetMinY(controlWindow) +
    (control != nil ? control.layer.sublayerTransform.m42 : 0);
  CALayer *firstChild = control.layer.sublayers.firstObject;
  BOOL childAttached = firstChild != nil && control.window == self.window;
  CGRect firstChildWindow = childAttached
    ? [firstChild convertRect:firstChild.bounds toLayer:self.window.layer]
    : CGRectZero;
  CGFloat overscroll = scrollView != nil
    ? -(scrollView.contentOffset.y + scrollView.contentInset.top) : 0;
  double epochMs = [NSDate.date timeIntervalSince1970] * 1000;
  double monoMs = now * 1000;
  RNCCollapsiblePagerLog([NSString stringWithFormat:
    @"[IOS-HOME-VISUAL] epochMs=%.0f monoMs=%.0f phase=%@ pagerTag=%ld generation=%lu pageIndex=%ld window=%d edge=%d overlay=%.1f header=%ld sticky=%ld collapse=%.1f scrollY=%.1f insetTop=%.1f overscroll=%.1f refresh=%d refreshing=%d controlY=%.1f controlH=%.1f sublayerY=%.1f visualYEstimate=%.1f overlayY=%.1f overlayBottom=%.1f stickyY=%.1f stickyBottom=%.1f maskY=%.1f maskH=%.1f pageMaskWindowY=%.1f",
    epochMs, monoMs, phase, (long)self.tag, (unsigned long)_generation,
    (long)_currentIndex, self.window != nil, _topScrollEdgeEnabled,
    self.effectiveTopOverlayHeight, (long)_headerHeight, (long)_stickyHeaderHeight,
    _headerOffset, scrollView.contentOffset.y, scrollView.contentInset.top,
    overscroll, control != nil, control.isRefreshing,
    CGRectGetMinY(controlWindow), CGRectGetHeight(controlWindow),
    control != nil ? control.layer.sublayerTransform.m42 : 0, visualYEstimate,
    CGRectGetMinY(overlayWindow), CGRectGetMaxY(overlayWindow),
    CGRectGetMinY(stickyWindow), CGRectGetMaxY(stickyWindow),
    CGRectGetMinY(maskWindow), CGRectGetHeight(maskWindow), pageMaskWindowY]);
  RNCCollapsiblePagerLog([NSString stringWithFormat:
    @"[IOS-HOME-VISUAL] epochMs=%.0f monoMs=%.0f phase=%@-refresh-layer pagerTag=%ld generation=%lu pageIndex=%ld boundsY=%.1f boundsH=%.1f centerY=%.1f transformTY=%.1f layerPositionY=%.1f layerTransformY=%.1f directChildCount=%lu firstChildAttached=%d firstChildWindowY=%.1f firstChildWindowH=%.1f",
    epochMs, monoMs, phase, (long)self.tag, (unsigned long)_generation,
    (long)_currentIndex, control.bounds.origin.y, control.bounds.size.height,
    control.center.y, control.transform.ty, control.layer.position.y,
    control.layer.transform.m42, (unsigned long)control.layer.sublayers.count,
    childAttached, CGRectGetMinY(firstChildWindow), CGRectGetHeight(firstChildWindow)]);
  if (![phase isEqualToString:@"pull"]) {
    CALayer *topCover = _topEdgeSeparatorCover;
    CALayer *stickyCover = _stickyEdgeSeparatorCover;
    UIColor *color = topCover.backgroundColor != nil
      ? [UIColor colorWithCGColor:topCover.backgroundColor] : nil;
    CGFloat red = 0, green = 0, blue = 0, alpha = 0;
    [color getRed:&red green:&green blue:&blue alpha:&alpha];
    CGRect topCoverWindow = topCover.superlayer != nil
      ? [topCover convertRect:topCover.bounds toLayer:self.window.layer] : CGRectZero;
    CGRect stickyCoverWindow = stickyCover.superlayer != nil
      ? [stickyCover convertRect:stickyCover.bounds toLayer:self.window.layer] : CGRectZero;
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"[IOS-HOME-VISUAL] epochMs=%.0f monoMs=%.0f phase=%@-edge pagerTag=%ld generation=%lu pageIndex=%ld trait=%ld override=%ld style=%ld pinned=%d topHostY=%.1f topHostBottom=%.1f stickyHostY=%.1f stickyHostBottom=%.1f topInteractionSameScroll=%d stickyInteractionSameScroll=%d topCover=%d topCoverHidden=%d topCoverY=%.1f topCoverH=%.3f stickyCover=%d stickyCoverHidden=%d stickyCoverY=%.1f stickyCoverH=%.3f coverRGBA=%.3f,%.3f,%.3f,%.3f scale=%.1f",
      epochMs, monoMs, phase, (long)self.tag, (unsigned long)_generation,
      (long)_currentIndex, (long)self.traitCollection.userInterfaceStyle,
      (long)_topOverlayHostView.overrideUserInterfaceStyle,
      (long)_topScrollEdgeEffect, _stickyHeaderIsPinned,
      CGRectGetMinY(overlayWindow), CGRectGetMaxY(overlayWindow),
      CGRectGetMinY(stickyWindow), CGRectGetMaxY(stickyWindow),
      _topEdgeInteraction.scrollView == scrollView,
      _stickyEdgeInteraction.scrollView == scrollView,
      topCover != nil, topCover.hidden, CGRectGetMinY(topCoverWindow),
      CGRectGetHeight(topCoverWindow), stickyCover != nil,
      stickyCover.hidden,
      CGRectGetMinY(stickyCoverWindow),
      CGRectGetHeight(stickyCoverWindow), red, green, blue, alpha,
      self.window.screen.scale]);
  }
#endif
}

- (void)releaseRefreshControlForScrollView:(UIScrollView *)scrollView
{
  if ([_refreshControlObservedScrollViews containsObject:scrollView]) {
    [scrollView removeObserver:self forKeyPath:@"refreshControl" context:RNCScrollRefreshControlContext];
    [_refreshControlObservedScrollViews removeObject:scrollView];
  }
  [RNCRefreshControlOffsetState removeControl:[_managedRefreshControls objectForKey:scrollView] owner:self];
  [_managedRefreshControls removeObjectForKey:scrollView];
}

- (void)cancelTopInsetReboundCheck
{
  [_topInsetReboundTimer invalidate];
  _topInsetReboundTimer = nil;
  _pendingTopInsetReboundScrollView = nil;
  _pendingTopInsetReboundPageKey = nil;
}

- (void)scheduleTopInsetReboundCheck
{
  if (_pendingTopInsetReboundScrollView == nil ||
      _pendingTopInsetReboundScrollView != _observedScrollView) return;
  [_topInsetReboundTimer invalidate];
  __weak __typeof__(self) weakSelf = self;
  _topInsetReboundTimer = [NSTimer scheduledTimerWithTimeInterval:0.15 repeats:NO block:^(NSTimer *timer) {
    __strong __typeof__(weakSelf) self = weakSelf;
    if (self == nil || self->_topInsetReboundTimer != timer) return;
    self->_topInsetReboundTimer = nil;
    UIScrollView *scrollView = self->_pendingTopInsetReboundScrollView;
    if (scrollView == nil || scrollView != self->_observedScrollView ||
        self->_pendingTopInsetReboundGeneration != self->_generation ||
        ![self->_pendingTopInsetReboundPageKey isEqualToString:self.currentPageKey] ||
        self->_isBeingRecycled || self.window == nil ||
        self->_transitioning || self->_isPagerDragging ||
        self->_headerCommandOwner == scrollView || self->_pagerTopPageKey != nil) {
      [self cancelTopInsetReboundCheck];
      return;
    }
    UIGestureRecognizerState pan = scrollView.panGestureRecognizer.state;
    if (scrollView.refreshControl.isRefreshing || scrollView.isDragging ||
        scrollView.isDecelerating || scrollView.isTracking ||
        pan == UIGestureRecognizerStateBegan || pan == UIGestureRecognizerStateChanged ||
        self->_syntheticPageRestore || self->_transitionPageOffsets != nil) {
      [self scheduleTopInsetReboundCheck];
      return;
    }
    CGFloat minimumOffset = -scrollView.adjustedContentInset.top;
    [self cancelTopInsetReboundCheck];
    if (scrollView.contentOffset.y < minimumOffset - 0.5) {
      // UIKit can finish a refresh rebound at the inset that preceded an overlay resize.
      self->_correctingTopInsetRebound = YES;
      [scrollView setContentOffset:CGPointMake(scrollView.contentOffset.x, minimumOffset) animated:NO];
      self->_correctingTopInsetRebound = NO;
    }
  }];
}

// OneKey patch: release observers, headers and saved state while the scroll view
// still represents the old list, before Fabric applies its next owner's props.
- (void)restoreScrollViewState:(UIScrollView *)scrollView resetSavedOffset:(BOOL)resetSavedOffset
{
  [self updateNativeScrollerViewport:scrollView stickyHeight:0];
  [self releaseRefreshControlForScrollView:scrollView];
  if (scrollView == _observedScrollView) {
    [self detachScrollObserver];
    if (resetSavedOffset && !_nativeSmoothHeaderScrollEnabled) _pageOffsets[self.currentPageKey] = @(_headerOffset);
  }
  if (scrollView == _sharedHeaderScrollView) {
    [self restoreSharedHeadersToContainer];
  }
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
  scrollView.directionalLockEnabled =
    [_originalDirectionalLockEnabled objectForKey:scrollView].boolValue;
  scrollView.contentInsetAdjustmentBehavior = (UIScrollViewContentInsetAdjustmentBehavior)
    [_originalInsetAdjustmentBehavior objectForKey:scrollView].integerValue;
  [scrollView setContentOffset:CGPointMake(scrollView.contentOffset.x, logicalOffset - original.top)
                     animated:NO];
  [_originalInsets removeObjectForKey:scrollView];
  [_appliedTopInsets removeObjectForKey:scrollView];
  [_originalIndicatorInsets removeObjectForKey:scrollView];
  [_originalAlwaysBounceVertical removeObjectForKey:scrollView];
  [_originalDirectionalLockEnabled removeObjectForKey:scrollView];
  [_originalInsetAdjustmentBehavior removeObjectForKey:scrollView];
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
    // OneKey patch: share the full release path with pre-mount recycling.
    [self restoreScrollViewState:scrollView resetSavedOffset:YES];
  }
}

- (void)applyInsetsToScrollView:(UIScrollView *)scrollView
                      pageIndex:(NSInteger)pageIndex
                        restore:(BOOL)restore
{
  // Smooth pages retain their complete offset while replacement identity and
  // layout settle. Legacy pages retain the historical header-only policy.
  NSString *pageKey = [self pageKeyForIndex:pageIndex];
  RNCCollapsiblePagerReleasedScrollState *released = _releasedPageScrollStates[pageKey];
  BOOL deferredReleasedIdentity = NO;
  if (released != nil) {
    UIView *componentView = RNCScrollComponentView(scrollView);
    BOOL sameList = released.scrollView == scrollView && componentView != nil &&
      released.componentView == componentView && released.componentTag == componentView.tag;
    // Adjacent Nitro lists are visible only as generic UICollectionViews, so
    // debounce each replacement candidate independently before rejecting the
    // released identity. A surviving native list is safe to accept immediately.
    BOOL needsFallbackDebounce = !sameList &&
      [scrollView isKindOfClass:UICollectionView.class] && scrollView != _observedScrollView &&
      !RNCIsMountedNativeListScrollView(scrollView);
    if (needsFallbackDebounce) {
      CFTimeInterval now = CACurrentMediaTime();
      if (released.pendingFallbackScrollView != scrollView) {
        released.pendingFallbackScrollView = scrollView;
        released.pendingFallbackDetectedAt = now;
        __weak __typeof__(self) weakSelf = self;
        __weak RNCCollapsiblePagerReleasedScrollState *weakReleased = released;
        __weak UIScrollView *weakScrollView = scrollView;
        NSString *capturedPageKey = [pageKey copy];
        NSUInteger capturedGeneration = _generation;
        dispatch_after(
          dispatch_time(
            DISPATCH_TIME_NOW,
            (int64_t)(RNCCollapsiblePagerFallbackDebounceInterval * NSEC_PER_SEC)
          ),
          dispatch_get_main_queue(),
          ^{
            RNCCollapsiblePagerViewComponentView *strongSelf = weakSelf;
            RNCCollapsiblePagerReleasedScrollState *strongReleased = weakReleased;
            UIScrollView *strongScrollView = weakScrollView;
            if (strongSelf == nil || strongReleased == nil || strongScrollView == nil ||
                strongSelf->_isBeingRecycled || strongSelf.window == nil ||
                strongSelf->_generation != capturedGeneration ||
                strongSelf->_releasedPageScrollStates[capturedPageKey] != strongReleased ||
                strongReleased.pendingFallbackScrollView != strongScrollView) return;
            [strongSelf prepareAdjacentPageInsets];
          }
        );
      }
      deferredReleasedIdentity = now - released.pendingFallbackDetectedAt <
        RNCCollapsiblePagerFallbackDebounceInterval;
    }
    if (!deferredReleasedIdentity) {
      if (!sameList && !_nativeSmoothHeaderScrollEnabled) _pageOffsets[pageKey] = @(_headerOffset);
      [_releasedPageScrollStates removeObjectForKey:pageKey];
    }
  }
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
    [_originalDirectionalLockEnabled setObject:@(scrollView.directionalLockEnabled) forKey:scrollView];
    [_originalInsetAdjustmentBehavior setObject:@(scrollView.contentInsetAdjustmentBehavior) forKey:scrollView];
  }

  // This host already includes the fixed header and safe area in its frame.
  // UIKit automatic adjustment would add them a second time to short pages.
  scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
  // This viewport is vertical-only; UIKit's first horizontal lock must not
  // suppress its later vertical movement while the pager is still pending.
  scrollView.directionalLockEnabled = _nativeSmoothHeaderScrollEnabled
    ? NO : [_originalDirectionalLockEnabled objectForKey:scrollView].boolValue;
  [self updateNativeScrollerViewport:scrollView stickyHeight:_stickyHeaderHeight + self.effectiveTopOverlayHeight];
  [self updateRefreshControlForScrollView:scrollView];
  CGFloat previousTopInset = scrollView.contentInset.top;
  CGFloat previousLogicalOffset = scrollView.contentOffset.y + previousTopInset;
  // Preserve UIKit's rebound after refresh while the content is beyond its top inset.
  BOOL wasReturningFromPull = scrollView.contentOffset.y < -scrollView.adjustedContentInset.top - 0.5;
  NSNumber *appliedTop = [_appliedTopInsets objectForKey:scrollView];
  // UIRefreshControl owns its temporary top inset, including while ending.
  CGFloat refreshInset = appliedTop != nil && scrollView.refreshControl != nil
    ? MAX(0, previousTopInset - appliedTop.doubleValue) : 0;
  UIEdgeInsets next = original;
  next.top += self.effectiveTopOverlayHeight + _headerHeight + _stickyHeaderHeight;
  CGFloat pagerTopInset = next.top;
  next.top += refreshInset;
  [_appliedTopInsets setObject:@(pagerTopInset) forKey:scrollView];
  // Short pages still need enough range to collapse the header; long pages
  // must end at the last row rather than leave a header-sized blank footer.
  next.bottom += MAX(0, scrollView.bounds.size.height - _stickyHeaderHeight - self.effectiveTopOverlayHeight
    - original.top - original.bottom - scrollView.contentSize.height);
  if (UIEdgeInsetsEqualToEdgeInsets(scrollView.contentInset, next) && !restore) {
    // UICollectionView may report contentSize during a bounce without changing
    // the required insets. Re-setting contentOffset here interrupts that bounce.
    if (pageIndex == _currentIndex) [self updateHeaderForScrollView:scrollView];
    return;
  }
  BOOL previousSyntheticRestore = _syntheticPageRestore;
  if (_nativeSmoothHeaderScrollEnabled || [_alignedBodyKeys containsObject:pageKey]) _syntheticPageRestore = YES;
  scrollView.contentInset = next;
  UIEdgeInsets indicatorInsets = scrollView.verticalScrollIndicatorInsets;
  indicatorInsets.top = pagerTopInset;
  scrollView.verticalScrollIndicatorInsets = indicatorInsets;
  scrollView.alwaysBounceVertical = YES;

  CGFloat targetOffset = previousLogicalOffset - next.top;
  CGFloat intendedLogicalOffset = previousLogicalOffset;
  if (restore) {
    NSNumber *saved = _transitionPageOffsets != nil
      ? _transitionPageOffsets[pageKey] : _pageOffsets[pageKey];
    // The sticky slot can change height between pages. Cache offsets relative
    // to content start so a round trip does not add that height difference.
    CGFloat restoredLogicalOffset;
    if (_nativeSmoothHeaderScrollEnabled && _headerHeight > 0) {
      [_alignedBodyKeys addObject:pageKey];
      if (_headerOffset < _headerHeight && saved.doubleValue > 0) [_suspendedBodyKeys addObject:pageKey];
      CGFloat body = saved.doubleValue;
      if (_transitionPageOffsets != nil && pageIndex == _transitionSourceIndex) body = _transitionSourceDisplayBody;
      else if (_transitionPageOffsets != nil) [_displayBodyOverrides removeObjectForKey:pageKey];
      else if (_displayBodyOverrides[pageKey] != nil) body = _displayBodyOverrides[pageKey].doubleValue;
      restoredLogicalOffset = _headerOffset + (_headerOffset >= _headerHeight ? body : 0);
      if (_transitionPageOffsets != nil) [_transitionCompensatedPages addIndex:pageIndex];
      if (deferredReleasedIdentity || scrollView.bounds.size.height <= 0 || scrollView.contentSize.height <= 0) {
        [_pendingPageRestoreKeys addObject:pageKey];
        _syntheticPageRestore = previousSyntheticRestore;
        return;
      }
      [_pendingPageRestoreKeys removeObject:pageKey];
    } else {
      restoredLogicalOffset = deferredReleasedIdentity || saved == nil ? _headerOffset : saved.doubleValue;
    }
    intendedLogicalOffset = restoredLogicalOffset;
    // Cancellation restores the visible source, while retaining its unreachable reading intent.
    if (_nativeSmoothHeaderScrollEnabled && _headerOffset >= _headerHeight &&
        _displayBodyOverrides[pageKey] != nil) {
      intendedLogicalOffset = MAX(intendedLogicalOffset, _headerOffset + _pageOffsets[pageKey].doubleValue);
    }
    targetOffset = restoredLogicalOffset - next.top;
    if (!_nativeSmoothHeaderScrollEnabled || _headerHeight <= 0) {
      targetOffset = MAX(targetOffset, -next.top + _headerOffset);
    }
    // An inactive list may shrink after its offset observer was detached.
    // Do not overwrite its corrected offset with a now-unreachable cached one.
    targetOffset = RNCLimitRestoredScrollOffset(
      targetOffset,
      scrollView.contentSize.height,
      scrollView.bounds.size.height,
      next.top,
      next.bottom
    );
  }
  // A bottom-inset change must not cancel UIKit's refresh or bounce settlement.
  if (restore || (previousTopInset != next.top && !wasReturningFromPull)) {
    [scrollView setContentOffset:CGPointMake(scrollView.contentOffset.x, targetOffset) animated:NO];
  }
  if (_applyingTopOverlayInsetChange && !restore && wasReturningFromPull &&
      previousTopInset != next.top && scrollView == _observedScrollView) {
    _pendingTopInsetReboundScrollView = scrollView;
    _pendingTopInsetReboundPageKey = [pageKey copy];
    _pendingTopInsetReboundGeneration = _generation;
    [self scheduleTopInsetReboundCheck];
  }
  if (_nativeSmoothHeaderScrollEnabled && restore && targetOffset + next.top + 0.5 < intendedLogicalOffset) {
    [_pendingPageRestoreKeys addObject:pageKey];
  }
  _syntheticPageRestore = previousSyntheticRestore;
  if (pageIndex == _currentIndex) {
    _currentLogicalOffset = scrollView.contentOffset.y + scrollView.contentInset.top;
    if (!_nativeSmoothHeaderScrollEnabled) [self updateHeaderForScrollView:scrollView];
    else [self applyHeaderOffset];
  }
}

- (void)updateNativeScrollerViewport:(UIScrollView *)scrollView stickyHeight:(CGFloat)height
{
  if ([scrollView.superview isKindOfClass:RNCNativeScrollerComponentView.class]) {
    [(RNCNativeScrollerComponentView *)scrollView.superview setPagerStickyHeight:height forOwner:self];
  }
}

- (void)nativeListScrollCommandAccepted:(NSNotification *)notification
{
  UIScrollView *scrollView = notification.object;
  if (scrollView != _observedScrollView ||
      _observedPageKey == nil || ![_observedPageKey isEqualToString:self.currentPageKey] ||
      _observedPageIndex != _currentIndex || _observedPageGeneration != _generation ||
      _transitioning || _isPagerDragging || _transitionPageOffsets != nil || _syntheticPageRestore) return;
  [self cancelTopInsetReboundCheck];
  NSInteger phase = [notification.userInfo[@"phase"] integerValue];
  [self updateHeaderSettledStateForceBusy:NO];
  if (phase == 1) _headerCommandOwner = scrollView;
  else if (phase == 0 || phase == 2) _headerCommandOwner = nil;
  [self updateHeaderSettledStateForceBusy:phase != 2];
  if ([self matchesPagerTop:scrollView]) {
    if (phase == 0) [self cancelPagerTop];
    else return;
  }
  if (phase == 0) [_alignedBodyKeys removeObject:_observedPageKey];
  if (!_nativeSmoothHeaderScrollEnabled) return;
  _currentLogicalOffset = scrollView.contentOffset.y + scrollView.contentInset.top;
  if (phase == 1) { [_commandBodyKeys addObject:_observedPageKey]; return; }
  if (phase == 2) {
    if (![_commandBodyKeys containsObject:_observedPageKey]) return;
    _pageOffsets[_observedPageKey] = @(MAX(_currentLogicalOffset - _headerHeight, 0));
    [self updateHeaderForScrollView:scrollView];
    [_commandBodyKeys removeObject:_observedPageKey];
    return;
  }
  [_commandBodyKeys removeObject:_observedPageKey];
  [_pendingPageRestoreKeys removeObject:_observedPageKey];
  [_alignedBodyKeys removeObject:_observedPageKey];
  [_suspendedBodyKeys removeObject:_observedPageKey];
  [_displayBodyOverrides removeObjectForKey:_observedPageKey];
  _currentLogicalOffset = scrollView.contentOffset.y + scrollView.contentInset.top;
  BOOL explicitTop = [notification.userInfo[@"explicitTop"] boolValue];
  _pageOffsets[_observedPageKey] = @(explicitTop ? 0 : MAX(_currentLogicalOffset - _headerHeight, 0));
  if (explicitTop) {
    _headerOffset = 0;
    [self applyHeaderOffset];
  }
}

- (void)nativeScrollerWillReceiveScrollCommand:(RNCNativeScrollerComponentView *)scroller
{
  UIScrollView *scrollView = scroller.scrollView;
  NSString *ownerKey = _observedPageKey;
  if (scrollView != _observedScrollView ||
      ownerKey == nil || ![ownerKey isEqualToString:self.currentPageKey] ||
      _observedPageIndex != _currentIndex || _currentIndex < 0 || _currentIndex >= _pageControllers.count ||
      _observedPageGeneration != _generation || _transitioning || _isPagerDragging ||
      _transitionPageOffsets != nil || _syntheticPageRestore) return;
  [self cancelTopInsetReboundCheck];
  if (!_dispatchingPagerTop) {
    [self cancelPagerTop];
    [_alignedBodyKeys removeObject:ownerKey];
  }
  if (!_nativeSmoothHeaderScrollEnabled) return;
  [_pendingPageRestoreKeys removeObject:ownerKey];
  _currentLogicalOffset = scrollView.contentOffset.y + scrollView.contentInset.top;
  [_commandBodyKeys removeObject:ownerKey];
  [_alignedBodyKeys removeObject:ownerKey];
  [_suspendedBodyKeys removeObject:ownerKey];
  [_displayBodyOverrides removeObjectForKey:ownerKey];
  _pageOffsets[ownerKey] = @(MAX(_currentLogicalOffset - _headerHeight, 0));
  [self updateHeaderForScrollView:scrollView];
}


- (void)nativeScrollViewDidBecomeReadyNotification:(NSNotification *)notification
{
  if ([notification.object isKindOfClass:UIScrollView.class]) {
    [self nativeScrollViewDidBecomeReady:(UIScrollView *)notification.object];
  }
}

- (void)nativeScrollViewDidBecomeReady:(UIScrollView *)scrollView
{
  if (_isBeingRecycled || self.window == nil || scrollView == nil ||
      [_mountingRemovedScrollViews containsObject:scrollView]) return;
  for (UIView *ancestor = scrollView.superview;
       ancestor != nil && ancestor != self;
       ancestor = ancestor.superview) {
    if ([ancestor isKindOfClass:RNCCollapsiblePagerViewComponentView.class]) {
      RNCCollapsiblePagerViewComponentView *nestedPager =
        (RNCCollapsiblePagerViewComponentView *)ancestor;
      if ([nestedPager currentVerticalScrollViewForParentPager] != scrollView) return;
      break;
    }
  }
  NSInteger pageIndex = NSNotFound;
  for (NSInteger index = 0; index < _pageControllers.count; index++) {
    if ([scrollView isDescendantOfView:_pageControllers[index].view]) {
      pageIndex = index;
      break;
    }
  }
  if (pageIndex == NSNotFound) return;

  NSString *pageKey = [self pageKeyForIndex:pageIndex];
  BOOL hasReleasedStateForPage = _releasedPageScrollStates[pageKey] != nil;
  BOOL currentPageNeedsOwner = pageIndex == _currentIndex &&
    (_awaitingScrollOwnerForCurrentPage || _needsScrollObserverReattachAfterMount ||
     hasReleasedStateForPage ||
     (_observedScrollView != nil &&
      [_observedScrollView isKindOfClass:UICollectionView.class] &&
      !RNCIsMountedNativeListScrollView(_observedScrollView) &&
      _observedScrollView != scrollView));

  if (!hasReleasedStateForPage && !currentPageNeedsOwner &&
      !_needsScrollObserverReattachAfterMount) return;

  if (_nativeSmoothHeaderScrollEnabled) {
    NSInteger first = MAX(0, _currentIndex - 1);
    NSInteger last = MIN((NSInteger)_pageControllers.count - 1, _currentIndex + 1);
    BOOL isOwnedScroller = [scrollView.superview isKindOfClass:RNCNativeScrollerComponentView.class] &&
      ((RNCNativeScrollerComponentView *)scrollView.superview).scrollView == scrollView;
    if ((pageIndex >= first && pageIndex <= last || pageIndex == _destinationIndex) &&
        (RNCIsMountedNativeListScrollView(scrollView) || isOwnedScroller) &&
        [_originalInsets objectForKey:scrollView] == nil) {
      [self applyInsetsToScrollView:scrollView pageIndex:pageIndex restore:YES];
    }
  }

  if (currentPageNeedsOwner && !_transitioning && !_isPagerDragging) {
    [self attachScrollObserverForCurrentPage];
  }
}

- (void)nativeScroller:(RNCNativeScrollerComponentView *)scroller
    callerInsetChangedFrom:(UIEdgeInsets)previous to:(UIEdgeInsets)next
{
  UIScrollView *scrollView = scroller.scrollView;
  NSValue *stored = [_originalInsets objectForKey:scrollView];
  if (stored == nil) return;
  UIEdgeInsets original = stored.UIEdgeInsetsValue;
  original.top += next.top - previous.top;
  original.left += next.left - previous.left;
  original.bottom += next.bottom - previous.bottom;
  original.right += next.right - previous.right;
  [_originalInsets setObject:[NSValue valueWithUIEdgeInsets:original] forKey:scrollView];
  NSNumber *appliedTop = [_appliedTopInsets objectForKey:scrollView];
  if (appliedTop != nil) {
    [_appliedTopInsets setObject:@(appliedTop.doubleValue + next.top - previous.top) forKey:scrollView];
  }
  if (scrollView == _observedScrollView) [self updateHeaderForScrollView:scrollView];
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
    UIScrollView *scrollView = [self verticalScrollViewForPageAtIndex:index];
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
  if (context == RNCScrollRefreshControlContext) {
    UIScrollView *scrollView = (UIScrollView *)object;
    [self updateRefreshControlForScrollView:scrollView];
    [self logHomeVisualGeometry:@"refresh-control-change" scrollView:scrollView];
    if (scrollView == _observedScrollView && !_syntheticPageRestore &&
        (!_nativeSmoothHeaderScrollEnabled || (!_transitioning && _transitionPageOffsets == nil))) {
      [self updateHeaderForScrollView:scrollView];
    }
    return;
  }
  if (context == RNCCollapsiblePagerContentOffsetContext && object == _observedScrollView) {
    if ([keyPath isEqualToString:@"contentOffset"] &&
        -(((UIScrollView *)object).contentOffset.y +
          ((UIScrollView *)object).contentInset.top) > 5) {
      [self logHomeVisualGeometry:@"pull" scrollView:(UIScrollView *)object];
    }
    if (_pendingTopInsetReboundScrollView == object &&
        ([keyPath isEqualToString:@"contentOffset"] || [keyPath isEqualToString:@"contentInset"])) {
      if (((UIScrollView *)object).panGestureRecognizer.state == UIGestureRecognizerStateChanged) {
        [self cancelTopInsetReboundCheck];
      } else {
        [self scheduleTopInsetReboundCheck];
      }
    }
    if (![change[NSKeyValueChangeOldKey] isEqual:change[NSKeyValueChangeNewKey]]) {
      [self updateHeaderSettledStateForceBusy:YES];
    }
    if ((_nativeSmoothHeaderScrollEnabled || [_alignedBodyKeys containsObject:_observedPageKey]) &&
        (_observedPageGeneration != _generation || _observedPageKey == nil ||
         _observedPageIndex != _currentIndex || ![_observedPageKey isEqualToString:self.currentPageKey] ||
         _transitioning || _isPagerDragging ||
         _transitionPageOffsets != nil || _syntheticPageRestore)) return;
    UIScrollView *ownedScroll = (UIScrollView *)object;
    NSValue *oldPoint = change[NSKeyValueChangeOldKey];
    NSValue *newPoint = change[NSKeyValueChangeNewKey];
    if ([keyPath isEqualToString:@"contentOffset"] && [self matchesPagerTop:ownedScroll] && ownedScroll.panGestureRecognizer.state == UIGestureRecognizerStateChanged &&
        [oldPoint isKindOfClass:NSValue.class] && [newPoint isKindOfClass:NSValue.class] &&
        oldPoint.CGPointValue.y != newPoint.CGPointValue.y) {
      CGPoint interruptedOffset = ownedScroll.contentOffset;
      [self cancelPagerTop];
      [ownedScroll setContentOffset:interruptedOffset animated:NO];
    }
    if ([keyPath isEqualToString:@"contentOffset"] &&
        ownedScroll.panGestureRecognizer.state == UIGestureRecognizerStateChanged &&
        [oldPoint isKindOfClass:NSValue.class] && [newPoint isKindOfClass:NSValue.class] &&
        oldPoint.CGPointValue.y != newPoint.CGPointValue.y) {
      [_alignedBodyKeys removeObject:_observedPageKey];
    }
    if (_nativeSmoothHeaderScrollEnabled && [keyPath isEqualToString:@"contentOffset"] &&
        ownedScroll.panGestureRecognizer.state == UIGestureRecognizerStateChanged &&
        [oldPoint isKindOfClass:NSValue.class] && [newPoint isKindOfClass:NSValue.class] &&
        oldPoint.CGPointValue.y != newPoint.CGPointValue.y) {
      [self cancelPagerTop];
      [_commandBodyKeys removeObject:_observedPageKey];
      [_pendingPageRestoreKeys removeObject:_observedPageKey];
      [_alignedBodyKeys removeObject:_observedPageKey];
      [_suspendedBodyKeys removeObject:_observedPageKey];
      [_displayBodyOverrides removeObjectForKey:_observedPageKey];
    }
    if (_nativeSmoothHeaderScrollEnabled && [_pendingPageRestoreKeys containsObject:_observedPageKey]) {
      if ([keyPath isEqualToString:@"contentSize"] &&
          ![change[NSKeyValueChangeOldKey] isEqual:change[NSKeyValueChangeNewKey]]) {
        if (_headerOffset >= _headerHeight) [_displayBodyOverrides removeObjectForKey:_observedPageKey];
        [self applyInsetsToScrollView:_observedScrollView pageIndex:_observedPageIndex restore:YES];
      }
      return;
    }
    if ([keyPath isEqualToString:@"contentInset"]) {
      if (!_nativeSmoothHeaderScrollEnabled) [self updateHeaderForScrollView:(UIScrollView *)object];
      return;
    }
    if ([keyPath isEqualToString:@"contentSize"]) {
      NSValue *oldSize = change[NSKeyValueChangeOldKey];
      NSValue *newSize = change[NSKeyValueChangeNewKey];
      if (![oldSize isEqual:newSize]) {
        BOOL restoreAlignedBody =
          _observedPageGeneration == _generation && _observedPageIndex == _currentIndex &&
          _observedPageKey != nil && [_observedPageKey isEqualToString:self.currentPageKey] &&
          [_alignedBodyKeys containsObject:_observedPageKey] &&
          ![_commandBodyKeys containsObject:_observedPageKey] &&
          _observedScrollView.panGestureRecognizer.state != UIGestureRecognizerStateChanged &&
          !_observedScrollView.isDragging && !_observedScrollView.isDecelerating;
        [self applyInsetsToScrollView:_observedScrollView pageIndex:_currentIndex restore:restoreAlignedBody];
      }
      return;
    }
    UIScrollView *scrollView = (UIScrollView *)object;
    _currentLogicalOffset = scrollView.contentOffset.y + scrollView.contentInset.top;
    if (_correctingTopInsetRebound) return;
    if ([_alignedBodyKeys containsObject:_observedPageKey] ||
        (_nativeSmoothHeaderScrollEnabled && scrollView.panGestureRecognizer.state != UIGestureRecognizerStateChanged &&
         !scrollView.isDecelerating && ![_commandBodyKeys containsObject:_observedPageKey])) return;
    _pageOffsets[_nativeSmoothHeaderScrollEnabled ? _observedPageKey : self.currentPageKey] =
      @(_nativeSmoothHeaderScrollEnabled ? MAX(_currentLogicalOffset - _headerHeight, 0) : _currentLogicalOffset);
    [self updateHeaderForScrollView:scrollView];
    return;
  }
  [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
}

- (void)updateHeaderForScrollView:(UIScrollView *)scrollView
{
  if ([self matchesPagerTop:scrollView]) {
    _currentLogicalOffset = scrollView.contentOffset.y + scrollView.contentInset.top;
    _headerOffset = _pagerTopLogicalTarget;
    [self applyHeaderOffset];
    [self finishPagerTopIfReached];
    return;
  }
  if ([_alignedBodyKeys containsObject:self.currentPageKey] ||
      (_nativeSmoothHeaderScrollEnabled && (_syntheticPageRestore || _transitionPageOffsets != nil ||
       [_pendingPageRestoreKeys containsObject:self.currentPageKey]))) return;
  CGFloat logicalOffset = scrollView.contentOffset.y + scrollView.contentInset.top;
  _currentLogicalOffset = logicalOffset;
  _headerOffset = MIN(MAX(logicalOffset, 0), _headerHeight);
  [self applyHeaderOffset];
}

- (void)applyHeaderOffset
{
  [self updateHeaderSettledStateForceBusy:NO];
  BOOL attachedToList = _nativeSmoothHeaderScrollEnabled &&
    _sharedHeaderScrollView != nil && !_sharedHeadersLiftedForPagerTransition;
  if (attachedToList) {
    // The header and content share this scroll view. UIKit's refresh inset
    // moves both together, so translating the header would separate them.
    _sharedHeaderHostView.transform = CGAffineTransformIdentity;
    _headerView.transform = CGAffineTransformIdentity;
    UIScrollView *scrollView = _sharedHeaderScrollView;
    NSNumber *appliedTop = [_appliedTopInsets objectForKey:scrollView];
    CGFloat refreshInset = scrollView.refreshControl != nil && appliedTop != nil
      ? MAX(0, scrollView.contentInset.top - appliedTop.doubleValue) : 0;
    CGFloat pinnedTranslation = MAX(0, _currentLogicalOffset - _headerHeight - refreshInset);
    CGAffineTransform stickyTransform = CGAffineTransformMakeTranslation(0, pinnedTranslation);
    [self updatePinnedStickyHeader];
    _stickyHeaderView.transform = _stickyHeaderIsPinned ? CGAffineTransformIdentity : stickyTransform;
    _nativeTabBarView.transform = _stickyHeaderIsPinned ? CGAffineTransformIdentity : stickyTransform;
    _nativeSubHeaderView.transform = _stickyHeaderIsPinned ? CGAffineTransformIdentity : stickyTransform;
    [self updateScrollEdgeContentMasks];
    return;
  }
  [self updatePinnedStickyHeader];
  CGAffineTransform collapseTransform = CGAffineTransformMakeTranslation(0, -_headerOffset);
  _sharedHeaderHostView.transform = collapseTransform;
  _headerView.transform = CGAffineTransformIdentity;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  _nativeTabBarView.transform = CGAffineTransformIdentity;
  _nativeSubHeaderView.transform = CGAffineTransformIdentity;
  [self updateTopScrollEdgeForScrollView:_observedScrollView];
  [self updateScrollEdgeContentMasks];
}

#pragma mark - Pager navigation

- (BOOL)isLtrLayout
{
  return [_layoutDirection isEqualToString:@"ltr"];
}

- (void)goTo:(NSInteger)index animated:(BOOL)animated
{
  [self cancelTopInsetReboundCheck];
  if (_isBeingRecycled || index < 0 || index >= _pageControllers.count) return;
  _hasReceivedPageCommand = YES;
  _hasAppliedInitialPage = YES;
  if (_transitioning || _isPagerDragging) {
    _pendingGoToIndex = index;
    _pendingGoToAnimated = animated;
    return;
  }

  _pendingGoToIndex = -1;
  _pendingGoToAnimated = YES;

  // Re-selecting the displayed controller removes its view briefly and cancels
  // an in-flight list touch. Focus synchronization must be idempotent.
  BOOL currentControllerDisplayed = _directNativePagerEnabled
    ? fabs(_pagerScrollView.contentOffset.x - [self directPagerOffsetForIndex:index]) < 0.5
    : _pageViewController.viewControllers.firstObject == _pageControllers[index];
  if (!_transitioning && index == _currentIndex &&
      currentControllerDisplayed) {
    [self attachScrollObserverForCurrentPage];
    [self emitPageSelected:index];
    [self emitDiagnostics:@"page-selected"];
    return;
  }

  [self beginPageStateTransition];
  [self preparePageForHorizontalTransitionAtIndex:index];
  _destinationIndex = index;
  [self liftSharedHeadersForPagerTransition:@"programmatic"];
  [self detachScrollObserver];
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
  if (_directNativePagerEnabled) {
    CGFloat targetOffset = [self directPagerOffsetForIndex:index];
    void (^animations)(void) = ^{
      [self->_pagerScrollView setContentOffset:CGPointMake(targetOffset, 0)];
      [self->_nativeTabBarView setProgress:index];
      [self updatePageStateTransitionProgress:index];
    };
    void (^completion)(BOOL) = ^(BOOL finished) {
      __strong __typeof__(weakSelf) self = weakSelf;
      if (self == nil || self->_generation != capturedGeneration ||
          self->_transitionId != capturedTransition || self->_isBeingRecycled) return;
      self->_pagerScrollView.scrollEnabled = self->_scrollEnabled;
      if (finished) {
        [self detachScrollObserver];
        self->_currentIndex = index;
        self->_destinationIndex = index;
        [self->_nativeTabBarView setProgress:index];
        [self attachScrollObserverForCurrentPageAfterTransition:YES];
        [self emitPageSelected:index];
        [self emitDiagnostics:@"page-selected"];
      } else if (self->_pendingGoToIndex < 0) {
        self->_pendingGoToIndex = index;
        self->_pendingGoToAnimated = animated;
      }
      [self completeTransitionOnNextRunLoopForGeneration:capturedGeneration
                                              transition:capturedTransition];
    };

    if (animated && index != _currentIndex) {
      _pagerScrollView.scrollEnabled = NO;
      [UIView animateWithDuration:0.28
                            delay:0
                          options:UIViewAnimationOptionCurveEaseInOut |
                                  UIViewAnimationOptionAllowUserInteraction
                       animations:animations
                       completion:completion];
    } else {
      [UIView performWithoutAnimation:animations];
      completion(YES);
    }
    return;
  }

  [_pageViewController setViewControllers:@[_pageControllers[index]]
                                direction:direction
                                 animated:animated && index != _currentIndex
                               completion:^(BOOL finished) {
    __strong __typeof__(weakSelf) self = weakSelf;
    if (self == nil || self->_generation != capturedGeneration ||
        self->_transitionId != capturedTransition || self->_isBeingRecycled) return;
    if (finished) {
      [self detachScrollObserver];
      self->_currentIndex = index;
      self->_destinationIndex = index;
      [self->_nativeTabBarView setProgress:index];
      [self attachScrollObserverForCurrentPageAfterTransition:YES];
      [self emitPageSelected:index];
      [self emitDiagnostics:@"page-selected"];
    } else {
      [self->_nativeTabBarView setProgress:self->_currentIndex];
      if (self->_pendingGoToIndex < 0) {
        self->_pendingGoToIndex = self->_currentIndex;
        self->_pendingGoToAnimated = NO;
      }
    }
    [self completeTransitionOnNextRunLoopForGeneration:capturedGeneration
                                           transition:capturedTransition];
  }];
}

- (void)completeTransitionOnNextRunLoopForGeneration:(NSUInteger)generation
                                         transition:(NSUInteger)transition
{
  dispatch_async(dispatch_get_main_queue(), ^{
    if (self->_generation != generation || self->_transitionId != transition ||
        self->_isBeingRecycled) return;
    [self finishPageStateTransition];
    self->_transitioning = NO;
    if (self->_hasPendingDirectNativePagerChange) {
      BOOL enabled = self->_pendingDirectNativePagerEnabled;
      self->_hasPendingDirectNativePagerChange = NO;
      [self setDirectNativePagerEnabled:enabled];
    } else if (self->_needsSlotRebuild) {
      [self rebuildSlots];
    }
    if (!self->_hasAppliedInitialPage) [self setNeedsLayout];
    [self drainPendingGoTo];
    if (!self->_transitioning && !self->_needsSlotRebuild) {
      [self attachScrollObserverForCurrentPage];
    }
    [self updateScrollEdgeContentMasks];
  });
}

- (UIViewController *)adjacentController:(UIViewController *)controller delta:(NSInteger)delta
{
  NSInteger index = [_pageControllers indexOfObjectIdenticalTo:controller];
  if (index == NSNotFound) return nil;
  NSInteger target = index + delta;
  if (target < 0 || target >= _pageControllers.count) return nil;
  UIScrollView *scrollView = [self verticalScrollViewForPageAtIndex:target];
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
  BOOL animated = _pendingGoToAnimated;
  _pendingGoToIndex = -1;
  _pendingGoToAnimated = YES;
  BOOL currentControllerNeedsRestore = NO;
  if (_currentIndex >= 0 && _currentIndex < _pageControllers.count) {
    currentControllerNeedsRestore = _directNativePagerEnabled
      ? fabs(_pagerScrollView.contentOffset.x -
             [self directPagerOffsetForIndex:_currentIndex]) >= 0.5
      : _pageViewController.viewControllers.firstObject != _pageControllers[_currentIndex];
  }
  if (pending >= 0 && (pending != _currentIndex || currentControllerNeedsRestore)) {
    [self goTo:pending animated:animated];
  }
}

#pragma mark - Pager UIScrollViewDelegate

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView
{
  [self updateHeaderSettledStateForceBusy:YES];
  if (_nativeSmoothHeaderScrollEnabled && scrollView == _pagerScrollView) {
    [_contentPressCancellationGesture cancelPressForPan:scrollView.panGestureRecognizer];
  }
  [self beginPageStateTransition];
  if (_directNativePagerEnabled) {
    [self preparePageForHorizontalTransitionAtIndex:_currentIndex - 1];
    [self preparePageForHorizontalTransitionAtIndex:_currentIndex + 1];
  }
  // A user drag owns the result even if UIKit later completes the old animation.
  ++_transitionId;
  [self liftSharedHeadersForPagerTransition:@"interactive"];
  [self detachScrollObserver];
  _isPagerDragging = YES;
  _transitioning = YES;
  if (_nativeSmoothHeaderScrollEnabled) {
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"gesture-owner=pager page=%ld",
      (long)_currentIndex]);
  }
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
  if (_directNativePagerEnabled) {
    NSInteger index = emitSelection
      ? (NSInteger)llround([self directPagerProgress])
      : _currentIndex;
    index = MAX(0, MIN(index, (NSInteger)_pageControllers.count - 1));
    [self detachScrollObserver];
    _currentIndex = index;
    _destinationIndex = index;
    [_pagerScrollView setContentOffset:CGPointMake(
      [self directPagerOffsetForIndex:index],
      0
    ) animated:NO];
    [_nativeTabBarView setProgress:index];
    [self attachScrollObserverForCurrentPageAfterTransition:YES];
    if (emitSelection &&
        (_pendingGoToIndex < 0 || _pendingGoToIndex == index)) {
      [self emitPageSelected:index];
      [self emitDiagnostics:@"page-selected"];
    }
    _isPagerDragging = NO;
    if (_nativeSmoothHeaderScrollEnabled) {
      RNCCollapsiblePagerLog([NSString stringWithFormat:
        @"gesture-end=pager page=%ld",
        (long)_currentIndex]);
    }
    const auto emitter = [self eventEmitter];
    if (emitter) {
      emitter->onPageScrollStateChanged({
        .pageScrollState = RNCCollapsiblePagerViewEventEmitter::OnPageScrollStateChangedPageScrollState::Idle
      });
    }
    [self emitDiagnostics:@"pager-idle"];
    [self completeTransitionOnNextRunLoopForGeneration:_generation
                                           transition:_transitionId];
    return;
  }

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
      BOOL controllerNeedsRestore = _pageViewController.viewControllers.firstObject != controller;
      [self detachScrollObserver];
      _currentIndex = index;
      _destinationIndex = index;
      [_nativeTabBarView setProgress:index];
      [self attachScrollObserverForCurrentPageAfterTransition:YES];
      // A newer tab tap remains authoritative while its queued command runs.
      if (emitSelection &&
          (_pendingGoToIndex < 0 || _pendingGoToIndex == index)) {
        [self emitPageSelected:index];
        [self emitDiagnostics:@"page-selected"];
      }
      if (controllerNeedsRestore && _pendingGoToIndex < 0) {
        _pendingGoToIndex = index;
        _pendingGoToAnimated = NO;
      }
    }
    _isPagerDragging = NO;
  }
  if (_nativeSmoothHeaderScrollEnabled) {
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"gesture-end=pager page=%ld",
      (long)_currentIndex]);
  }
  const auto emitter = [self eventEmitter];
  if (emitter) {
    emitter->onPageScrollStateChanged({
      .pageScrollState = RNCCollapsiblePagerViewEventEmitter::OnPageScrollStateChangedPageScrollState::Idle
    });
  }
  [self emitDiagnostics:@"pager-idle"];
  [self completeTransitionOnNextRunLoopForGeneration:_generation
                                         transition:_transitionId];
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
  [self finishPagerScrollEmittingSelection:YES];
}

- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
  CGFloat width = scrollView.bounds.size.width;
  if (width <= 0) return;
  if (_directNativePagerEnabled && scrollView == _pagerScrollView) {
    CGFloat progress = [self directPagerProgress];
    NSInteger position = (NSInteger)floor(progress);
    CGFloat offset = progress - position;
    position = MAX(0, MIN(position, (NSInteger)_pageControllers.count - 1));
    [self updatePageStateTransitionProgress:progress];
    [self updateScrollEdgeContentMasks];
    [_nativeTabBarView setProgress:progress];
    const auto emitter = [self eventEmitter];
    if (emitter) {
      emitter->onPageScroll({.position = (double)position, .offset = (double)offset});
    }
    return;
  }
  CGFloat rawOffset = (scrollView.contentOffset.x - width) / width;
  BOOL backwards = self.isLtrLayout ? rawOffset < 0 : rawOffset > 0;
  NSInteger position = backwards ? _currentIndex - 1 : _currentIndex;
  CGFloat offset = backwards ? 1 - fabs(rawOffset) : fabs(rawOffset);
  position = MAX(0, MIN(position, (NSInteger)_pageControllers.count - 1));
  CGFloat tabProgress = position + offset;
  if (_transitioning && !_isPagerDragging && _destinationIndex != _currentIndex) {
    CGFloat transitionProgress = RNCClamp(fabs(rawOffset), 0, 1);
    tabProgress = _currentIndex + (_destinationIndex - _currentIndex) * transitionProgress;
  }
  [self updatePageStateTransitionProgress:tabProgress];
  [self updateScrollEdgeContentMasks];
  [_nativeTabBarView setProgress:tabProgress];
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

- (void)publishHeaderSettledAtTop:(BOOL)settled identityChanged:(BOOL)identityChanged
{
  if (_headerSettledAtTop == settled && !identityChanged) return;
  _headerSettledAtTop = settled;
  RNCCollapsiblePagerLog([NSString stringWithFormat:@"header-settled-state page=%ld generation=%lu settled=%d",
    (long)_currentIndex, (unsigned long)_generation, settled]);
  [self emitDiagnostics:@"header-settled-state"];
}

- (void)cancelHeaderSettledCheck
{
  [_headerSettledTimer invalidate];
  _headerSettledTimer = nil;
}

- (void)updateHeaderSettledStateForceBusy:(BOOL)forceBusy
{
  UIScrollView *owner = _observedScrollView;
  NSString *key = self.currentPageKey;
  BOOL valid = !_isBeingRecycled && self.window != nil && _headerView.window == self.window &&
    _headerHeight > 0 && owner.window == self.window && owner != nil &&
    CGRectGetWidth(owner.bounds) > 0 && CGRectGetHeight(owner.bounds) > 0 &&
    _observedPageGeneration == _generation && _observedPageIndex == _currentIndex &&
    _currentIndex >= 0 && _currentIndex < _pageControllers.count &&
    [_observedPageKey isEqualToString:key] && [owner isDescendantOfView:_pageControllers[_currentIndex].view];
  BOOL identityChanged = _headerSettledOwner != owner || ![_headerSettledKey isEqualToString:key] ||
    _headerSettledGeneration != _generation;
  if (identityChanged) {
    [self cancelHeaderSettledCheck];
    _headerCommandOwner = nil;
    _headerSettledOwner = owner;
    _headerSettledKey = [key copy];
    _headerSettledGeneration = _generation;
    _headerSettledGeometry = nil;
    _headerSettledWasBusy = YES;
    [self publishHeaderSettledAtTop:NO identityChanged:YES];
  }
  if (!valid || fabs(_headerOffset) > 0.5) {
    [self cancelHeaderSettledCheck];
    _headerSettledGeometry = nil;
    _headerSettledWasBusy = YES;
    [self publishHeaderSettledAtTop:NO identityChanged:NO];
    return;
  }
  CGRect headerRect = [_headerView convertRect:_headerView.bounds toView:self.window];
  CGRect ownerRect = [owner convertRect:owner.bounds toView:self.window];
  UIEdgeInsets inset = owner.contentInset;
  UIEdgeInsets adjustedInset = owner.adjustedContentInset;
  NSString *geometry = [NSString stringWithFormat:
    @"%.4f:%ld:%ld:%@:%@:%@:%@:%@:%@:%@",
    _headerOffset, (long)_headerHeight, (long)_stickyHeaderHeight,
    NSStringFromCGPoint(owner.contentOffset), NSStringFromCGSize(owner.contentSize),
    NSStringFromCGRect(owner.bounds), NSStringFromUIEdgeInsets(inset),
    NSStringFromUIEdgeInsets(adjustedInset), NSStringFromCGRect(headerRect), NSStringFromCGRect(ownerRect)];
  UIGestureRecognizerState panState = owner.panGestureRecognizer.state;
  BOOL busy = forceBusy || _transitioning || _isPagerDragging || _transitionPageOffsets != nil ||
    _syntheticPageRestore || _pagerTopPageKey != nil || _headerCommandOwner == owner ||
    owner.isDragging || owner.isDecelerating || owner.isTracking ||
    panState == UIGestureRecognizerStateBegan || panState == UIGestureRecognizerStateChanged;
  CFTimeInterval now = CACurrentMediaTime();
  if (![geometry isEqualToString:_headerSettledGeometry] || busy || _headerSettledWasBusy) {
    _headerSettledSince = now;
    _headerSettledGeometry = geometry;
    [self cancelHeaderSettledCheck];
    [self publishHeaderSettledAtTop:NO identityChanged:NO];
  }
  _headerSettledWasBusy = busy;
  if (!busy && now - _headerSettledSince >= 0.15) {
    [self cancelHeaderSettledCheck];
    [self publishHeaderSettledAtTop:YES identityChanged:NO];
    return;
  }
  if (_headerSettledTimer == nil) {
    __weak RNCCollapsiblePagerViewComponentView *weakSelf = self;
    __weak UIScrollView *weakOwner = owner;
    NSUInteger generation = _generation;
    _headerSettledTimer = [NSTimer scheduledTimerWithTimeInterval:0.15 repeats:NO block:^(NSTimer *timer) {
      RNCCollapsiblePagerViewComponentView *strongSelf = weakSelf;
      if (strongSelf == nil) return;
      if (strongSelf->_headerSettledTimer != timer) return;
      strongSelf->_headerSettledTimer = nil;
      if (strongSelf->_generation == generation && [strongSelf.currentPageKey isEqualToString:key] &&
          strongSelf->_observedScrollView == weakOwner) [strongSelf updateHeaderSettledStateForceBusy:NO];
    }];
  }
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
    .reason = std::string(reason.UTF8String ?: "unknown"),
    .pageKey = std::string(self.currentPageKey.UTF8String ?: ""),
    .headerSettledAtTop = (bool)_headerSettledAtTop
  });
}

- (void)handleCommand:(const NSString *)commandName args:(const NSArray *)args
{
  RCTRNCCollapsiblePagerViewHandleCommand(self, commandName, args);
}

- (BOOL)matchesPagerTop:(UIScrollView *)scrollView
{
  return scrollView != nil && scrollView == _pagerTopScrollView && scrollView == _observedScrollView &&
    _pagerTopPageKey != nil && [_pagerTopPageKey isEqualToString:self.currentPageKey] &&
    [_observedPageKey isEqualToString:_pagerTopPageKey] && _pagerTopPageIndex == _currentIndex &&
    _observedPageIndex == _currentIndex && _pagerTopGeneration == _generation &&
    _observedPageGeneration == _generation && scrollView.window != nil;
}

- (void)cancelPagerTop
{
  if (_pagerTopPageKey != nil) [_commandBodyKeys removeObject:_pagerTopPageKey];
  _pagerTopScrollView = nil;
  _pagerTopPageKey = nil;
}

- (void)finishPagerTopIfReached
{
  UIScrollView *scrollView = _pagerTopScrollView;
  if (_dispatchingPagerTop || ![self matchesPagerTop:scrollView]) return;
  CGFloat logical = scrollView.contentOffset.y + scrollView.contentInset.top;
  if (fabs(logical - _pagerTopLogicalTarget) > 0.5) return;
  _pageOffsets[_pagerTopPageKey] = @(_nativeSmoothHeaderScrollEnabled ? 0 : _pagerTopLogicalTarget);
  [_alignedBodyKeys addObject:_pagerTopPageKey];
  _currentLogicalOffset = logical;
  _headerOffset = _pagerTopLogicalTarget;
  [self applyHeaderOffset];
  [self cancelPagerTop];
}

- (void)scrollToTop:(NSInteger)target animated:(BOOL)animated
{
  [self cancelTopInsetReboundCheck];
  UIScrollView *scrollView = _observedScrollView;
  NSString *key = _observedPageKey;
  if ((target != 0 && target != 1) || _isBeingRecycled || _transitioning || _isPagerDragging ||
      _transitionPageOffsets != nil || _syntheticPageRestore || scrollView == nil || scrollView.window == nil ||
      key == nil || ![key isEqualToString:self.currentPageKey] || _observedPageIndex != _currentIndex ||
      _observedPageGeneration != _generation || self.window == nil) return;
  [self cancelPagerTop];
  // Old List phase2 notifications must run before the new target owns this page.
  [NSNotificationCenter.defaultCenter postNotificationName:@"OneKeyNativeListCancelScrollCommand" object:scrollView];
  if ([scrollView.superview isKindOfClass:RNCNativeScrollerComponentView.class]) {
    [(RNCNativeScrollerComponentView *)scrollView.superview stopScrolling];
  } else {
    [scrollView setContentOffset:scrollView.contentOffset animated:NO];
  }
  [_pendingPageRestoreKeys removeObject:key];
  [_alignedBodyKeys removeObject:key];
  [_suspendedBodyKeys removeObject:key];
  [_displayBodyOverrides removeObjectForKey:key];
  CGFloat logicalTarget = target == 1 ? _headerHeight : 0;
  _pageOffsets[key] = @(_nativeSmoothHeaderScrollEnabled ? 0 : logicalTarget);
  _pagerTopScrollView = scrollView;
  _pagerTopPageKey = [key copy];
  _pagerTopPageIndex = _currentIndex;
  _pagerTopGeneration = _generation;
  _pagerTopLogicalTarget = logicalTarget;
  _pagerTopToken++;
  [_commandBodyKeys addObject:key];
  _headerOffset = logicalTarget;
  [self applyHeaderOffset];
  CGFloat physicalTarget = RNCLimitRestoredScrollOffset(logicalTarget - scrollView.contentInset.top,
    scrollView.contentSize.height, scrollView.bounds.size.height, scrollView.contentInset.top, scrollView.contentInset.bottom);
  _dispatchingPagerTop = YES;
  [scrollView setContentOffset:CGPointMake(scrollView.contentOffset.x, physicalTarget) animated:animated];
  _dispatchingPagerTop = NO;
  [self finishPagerTopIfReached];
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
  if (gestureRecognizer == _sharedHeaderOuterPagerGesture) {
    UIPanGestureRecognizer *pan = (UIPanGestureRecognizer *)gestureRecognizer;
    CGPoint translation = [pan translationInView:pan.view];
    CGPoint velocity = [pan velocityInView:pan.view];
    CGPoint intent = fabs(translation.x) + fabs(translation.y) >= 1
      ? translation
      : velocity;
    BOOL hasMotion = MAX(fabs(intent.x), fabs(intent.y)) >= 1;
    BOOL horizontal = hasMotion && fabs(intent.x) > fabs(intent.y);
    if (_sharedHeaderOuterPagerGesture.protectsNestedBody) {
      NSInteger pageCount = _pageControllers.count;
      BOOL validPage = _currentIndex >= 0 && _currentIndex < pageCount;
      NSInteger firstPageIndex = self.isLtrLayout ? 0 : pageCount - 1;
      NSInteger lastPageIndex = self.isLtrLayout ? pageCount - 1 : 0;
      BOOL outward = validPage &&
        ((_currentIndex == firstPageIndex && intent.x > 0) ||
         (_currentIndex == lastPageIndex && intent.x < 0));
      UIScrollView *outer = RNCNestedOuterReactPagerScrollView(self);
      BOOL protectInnerPage = horizontal && validPage && pageCount > 1 &&
        _nestedScrollEnabled && _scrollEnabled && _pagerScrollView.scrollEnabled &&
        _pagerScrollView.panGestureRecognizer.enabled && outer != nil && outer.scrollEnabled &&
        [_sharedHeaderOuterPagerGesture.blockedPagerGestures
          containsObject:outer.panGestureRecognizer] && !outward;
      return protectInnerPage;
    }
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"header-axis role=outer-pager-block dx=%.2f dy=%.2f velocity=(%.2f,%.2f) result=%d outer-pagers=%lu",
      intent.x,
      intent.y,
      velocity.x,
      velocity.y,
      horizontal,
      (unsigned long)_sharedHeaderOuterPagerGesture.blockedPagerGestures.count]);
    return horizontal;
  }
  if (gestureRecognizer == _sharedHeaderPressCancellationGesture ||
      gestureRecognizer == _verticalPagerGesture) {
    UIPanGestureRecognizer *pan = (UIPanGestureRecognizer *)gestureRecognizer;
    CGPoint translation = [pan translationInView:pan.view];
    CGPoint velocity = [pan velocityInView:pan.view];
    CGPoint intent = fabs(translation.x) + fabs(translation.y) >= 1
      ? translation
      : velocity;
    BOOL hasMotion = MAX(fabs(intent.x), fabs(intent.y)) >= 1;
    BOOL vertical = hasMotion && fabs(intent.y) > fabs(intent.x);
    if (gestureRecognizer == _sharedHeaderPressCancellationGesture) vertical = hasMotion;
    NSString *role = gestureRecognizer == _sharedHeaderPressCancellationGesture
      ? @"vertical-cancel"
      : @"vertical-pager-guard";
    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"header-axis role=%@ dx=%.2f dy=%.2f velocity=(%.2f,%.2f) "
       "result=%d rct=%ld list=%ld pager=%ld",
      role,
      intent.x,
      intent.y,
      velocity.x,
      velocity.y,
      vertical,
      (long)_reactTouchHandler.state,
      (long)_observedScrollView.panGestureRecognizer.state,
      (long)_pagerScrollView.panGestureRecognizer.state]);
    return vertical;
  }
  if (gestureRecognizer != _blockerGesture) return YES;
  UIPanGestureRecognizer *pan = [gestureRecognizer isKindOfClass:UIPanGestureRecognizer.class]
    ? (UIPanGestureRecognizer *)gestureRecognizer : nil;
  CGPoint velocity = pan != nil ? [pan velocityInView:self] : CGPointZero;
  NSInteger pageCount = _pageControllers.count;
  CGFloat absVelocityX = fabs(velocity.x);
  CGFloat absVelocityY = fabs(velocity.y);
  NSInteger firstPageIndex = self.isLtrLayout ? 0 : pageCount - 1;
  NSInteger lastPageIndex = self.isLtrLayout ? pageCount - 1 : 0;
  BOOL validPage = pageCount > 0 && _currentIndex >= 0 && _currentIndex < pageCount;
  BOOL horizontal = absVelocityX > absVelocityY && absVelocityX >= 1.0f;
  BOOL outward = validPage &&
    ((_currentIndex == firstPageIndex && velocity.x > 0) ||
     (_currentIndex == lastPageIndex && velocity.x < 0));
  BOOL shouldBegin = _nestedScrollEnabled && pan != nil && horizontal && outward;
  return shouldBegin;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch
{
  if (gestureRecognizer == _sharedHeaderOuterPagerGesture) {
    if (!_nativeSmoothHeaderScrollEnabled || _isBeingRecycled ||
        _sharedHeaderPressCancellationGestureHost == nil) return NO;
    UIView *touchView = touch.view;
    BOOL beginsInSharedHeader = touchView == _sharedHeaderHostView ||
      [touchView isDescendantOfView:_sharedHeaderHostView] ||
      touchView == _pinnedStickyHostView ||
      [touchView isDescendantOfView:_pinnedStickyHostView];
    if (_sharedHeaderOuterPagerGesture.trackedTouch != nil &&
        _sharedHeaderOuterPagerGesture.trackedTouch != touch) return NO;
    UIScrollView *outer = RNCNestedOuterReactPagerScrollView(self);
    BOOL beginsInNestedBody = !beginsInSharedHeader && _nestedScrollEnabled &&
      _scrollEnabled && _pagerScrollView.scrollEnabled &&
      _pagerScrollView.panGestureRecognizer.enabled && _pageControllers.count > 1 &&
      outer != nil && outer.scrollEnabled &&
      [_sharedHeaderOuterPagerGesture.blockedPagerGestures containsObject:outer.panGestureRecognizer] &&
      (touchView == _pagerScrollView || [touchView isDescendantOfView:_pagerScrollView]);
    if (!beginsInSharedHeader && !beginsInNestedBody) return NO;

    _sharedHeaderOuterPagerGesture.trackedTouch = touch;
    _sharedHeaderOuterPagerGesture.protectsNestedBody = beginsInNestedBody;
    _sharedHeaderOuterPagerGesture.cancelsTouchesInView = !beginsInNestedBody;

    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"outer-pager-touch target=%@ outer-pagers=%lu nested-body=%d",
      touchView == nil ? @"none" : NSStringFromClass(touchView.class),
      (unsigned long)_sharedHeaderOuterPagerGesture.blockedPagerGestures.count,
      beginsInNestedBody]);
    return YES;
  }
  if (gestureRecognizer == _contentPressCancellationGesture) {
    [_contentPressCancellationGesture observeIncomingTouch:touch];
    UIView *touchView = touch.view;
    return _nativeSmoothHeaderScrollEnabled && !_isBeingRecycled &&
      _contentPressCancellationGesture.view != nil &&
      [touchView isDescendantOfView:_pagerScrollView] &&
      ![touchView isDescendantOfView:_sharedHeaderHostView] &&
      ![touchView isDescendantOfView:_pinnedStickyHostView];
  }
  if (gestureRecognizer == _sharedHeaderPressCancellationGesture) {
    if (!_nativeSmoothHeaderScrollEnabled || _isBeingRecycled ||
        _sharedHeaderPressCancellationGestureHost == nil) return NO;
    UIView *touchView = touch.view;
    BOOL beginsInSharedHeader = touchView == _sharedHeaderHostView ||
      [touchView isDescendantOfView:_sharedHeaderHostView] ||
      touchView == _pinnedStickyHostView ||
      [touchView isDescendantOfView:_pinnedStickyHostView];
    if (!beginsInSharedHeader) return NO;

    RNCCollapsiblePagerLog([NSString stringWithFormat:
      @"header-touch target=%@ shared=1",
      touchView == nil ? @"none" : NSStringFromClass(touchView.class)]);
    return YES;
  }
  if (gestureRecognizer == _verticalPagerGesture) {
    UIView *touchView = touch.view;
    return touchView != _sharedHeaderHostView &&
      ![touchView isDescendantOfView:_sharedHeaderHostView] &&
      touchView != _pinnedStickyHostView &&
      ![touchView isDescendantOfView:_pinnedStickyHostView];
  }
  // A new touch can cancel UIKit's animation before its first frame. Queue a
  // non-animated settle for the next safe transition boundary instead of
  // re-entering setViewControllers while UIKit is flushing the current view.
  if (gestureRecognizer == _blockerGesture && _transitioning && !_isPagerDragging &&
      [touch.view isDescendantOfView:_pagerScrollView]) {
    [self goTo:_destinationIndex animated:NO];
  }
  return YES;
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer
    shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer
{
  if (gestureRecognizer == _sharedHeaderOuterPagerGesture ||
      otherGestureRecognizer == _sharedHeaderOuterPagerGesture) {
    UIGestureRecognizer *other = gestureRecognizer == _sharedHeaderOuterPagerGesture
      ? otherGestureRecognizer
      : gestureRecognizer;
    return ![other isKindOfClass:RCTSurfaceTouchHandler.class] &&
      ![other isKindOfClass:RCTTouchHandler.class] &&
      ![_sharedHeaderOuterPagerGesture.blockedPagerGestures containsObject:other];
  }
  if (gestureRecognizer == _contentPressCancellationGesture ||
      otherGestureRecognizer == _contentPressCancellationGesture) {
    UIGestureRecognizer *other = gestureRecognizer == _contentPressCancellationGesture
      ? otherGestureRecognizer : gestureRecognizer;
    return other != _contentPressCancellationGesture.reactTouchHandler;
  }
  if (gestureRecognizer == _sharedHeaderPressCancellationGesture ||
      otherGestureRecognizer == _sharedHeaderPressCancellationGesture) {
    UIGestureRecognizer *other = gestureRecognizer == _sharedHeaderPressCancellationGesture
      ? otherGestureRecognizer
      : gestureRecognizer;
    return ![other isKindOfClass:RCTSurfaceTouchHandler.class] &&
      ![other isKindOfClass:RCTTouchHandler.class];
  }
  if (gestureRecognizer == _verticalPagerGesture ||
      otherGestureRecognizer == _verticalPagerGesture) {
    UIGestureRecognizer *other = gestureRecognizer == _verticalPagerGesture
      ? otherGestureRecognizer
      : gestureRecognizer;
    return other != _pagerScrollView.panGestureRecognizer;
  }
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
