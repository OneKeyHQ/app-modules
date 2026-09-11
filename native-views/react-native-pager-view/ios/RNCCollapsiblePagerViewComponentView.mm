#import "RNCCollapsiblePagerViewComponentView.h"

#import <react/renderer/components/pagerview/ComponentDescriptors.h>
#import <react/renderer/components/pagerview/EventEmitters.h>
#import <react/renderer/components/pagerview/Props.h>
#import <react/renderer/components/pagerview/RCTComponentViewHelpers.h>

#import "React/RCTConversions.h"

using namespace facebook::react;

static void *RNCCollapsiblePagerContentOffsetContext = &RNCCollapsiblePagerContentOffsetContext;

typedef void (^RNCCollapsiblePagerNativeTabPressHandler)(NSInteger index, NSString *key);

static CGFloat RNCClamp(CGFloat value, CGFloat minimum, CGFloat maximum)
{
  return MIN(MAX(value, minimum), maximum);
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

@interface RNCCollapsiblePagerNativeTabBarView : UIView
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
@end

@implementation RNCCollapsiblePagerNativeTabBarView {
  UIScrollView *_scrollView;
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

    _scrollView = [UIScrollView new];
    _scrollView.showsHorizontalScrollIndicator = NO;
    _scrollView.showsVerticalScrollIndicator = NO;
    _scrollView.alwaysBounceHorizontal = YES;
    _scrollView.directionalLockEnabled = YES;
    _scrollView.delaysContentTouches = NO;
    _scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
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
  [self setNeedsLayout];
}

- (void)handleTabPress:(UIButton *)button
{
  NSInteger index = button.tag;
  if (index < 0 || index >= (NSInteger)_items.count) return;
  NSDictionary *item = _items[index];
  NSString *key = [item[@"key"] isKindOfClass:NSString.class] ? item[@"key"] : @"";
  if (self.onTabPress) self.onTabPress(index, key);
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

  if (!_scrollView.dragging && !_scrollView.tracking && CGRectGetWidth(_scrollView.bounds) > 0) {
    CGFloat maximumOffset = MAX(0, _scrollView.contentSize.width - CGRectGetWidth(_scrollView.bounds));
    CGFloat desiredOffset = RNCClamp(
      CGRectGetMidX(indicatorFrame) - CGRectGetWidth(_scrollView.bounds) / 2,
      0,
      maximumOffset
    );
    [_scrollView setContentOffset:CGPointMake(desiredOffset, 0) animated:NO];
  }
}

@end

@interface RNCCollapsiblePagerNativeSubHeaderView : UIView
@property (nonatomic, copy) RNCCollapsiblePagerNativeTabPressHandler onItemPress;
@property (nonatomic, readonly) CGFloat preferredHeight;
- (void)updateConfigJSON:(NSString *)configJSON;
- (void)updateColorsWithBackgroundColor:(UIColor *)backgroundColor
                        activeTextColor:(UIColor *)activeTextColor
                      inactiveTextColor:(UIColor *)inactiveTextColor
                selectedBackgroundColor:(UIColor *)selectedBackgroundColor
                              fontFamily:(NSString *)fontFamily;
- (void)setLeftToRight:(BOOL)leftToRight;
@end

@implementation RNCCollapsiblePagerNativeSubHeaderView {
  UIScrollView *_tabsScrollView;
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

    _tabsScrollView = [UIScrollView new];
    _tabsScrollView.showsHorizontalScrollIndicator = NO;
    _tabsScrollView.showsVerticalScrollIndicator = NO;
    _tabsScrollView.alwaysBounceHorizontal = YES;
    _tabsScrollView.directionalLockEnabled = YES;
    _tabsScrollView.delaysContentTouches = NO;
    _tabsScrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
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
  _items = items;
  _selectedKey = [selectedKey copy];
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
    button.layer.cornerRadius = 10;
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

  if (selectedButton != nil && !_tabsScrollView.dragging && !_tabsScrollView.tracking) {
    CGFloat maximumOffset = MAX(0, contentWidth - width);
    CGFloat desiredOffset = RNCClamp(
      CGRectGetMidX(selectedButton.frame) - width / 2,
      0,
      maximumOffset
    );
    [_tabsScrollView setContentOffset:CGPointMake(desiredOffset, 0) animated:NO];
  }
}

@end

@interface RNCCollapsiblePagerViewComponentView () <
  RCTRNCCollapsiblePagerViewViewProtocol,
  UIPageViewControllerDataSource,
  UIPageViewControllerDelegate,
  UIScrollViewDelegate,
  UIGestureRecognizerDelegate
>
- (void)finishPagerScrollEmittingSelection:(BOOL)emitSelection;
- (void)completeTransitionOnNextRunLoopForGeneration:(NSUInteger)generation
                                         transition:(NSUInteger)transition;
- (void)setDirectNativePagerEnabled:(BOOL)enabled;
@end

@implementation RNCCollapsiblePagerViewComponentView {
  UIView *_containerView;
  UIPageViewController *_pageViewController;
  UIScrollView *_pageViewControllerScrollView;
  UIScrollView *_pagerScrollView;
  NSMutableArray<UIView<RCTComponentViewProtocol> *> *_logicalChildren;
  NSMutableArray<UIViewController *> *_pageControllers;
  UIView *_headerView;
  UIView *_stickyHeaderView;
  RNCCollapsiblePagerNativeTabBarView *_nativeTabBarView;
  RNCCollapsiblePagerNativeSubHeaderView *_nativeSubHeaderView;
  NSInteger _currentIndex;
  NSInteger _destinationIndex;
  NSInteger _pendingInitialPage;
  NSInteger _headerHeight;
  NSInteger _stickyHeaderHeight;
  CGFloat _nativeTabBarHeight;
  CGFloat _nativeSubHeaderHeight;
  CGFloat _headerOffset;
  BOOL _scrollEnabled;
  // OneKey patch: Coordinate this inner pager with an outer horizontal pager.
  BOOL _nestedScrollEnabled;
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
    _nativeTabBarHeight = 44;
    _nativeSubHeaderHeight = 74;
    _nestedScrollEnabled = NO;
    _directNativePagerEnabled = NO;
    _pendingDirectNativePagerEnabled = NO;
    _hasPendingDirectNativePagerChange = NO;
    _pendingGoToIndex = -1;
    _pendingGoToAnimated = YES;
    _hasAppliedInitialPage = NO;
    _needsPropsReapply = YES;
    _layoutDirection = @"ltr";

    _containerView = [UIView new];
    _containerView.clipsToBounds = YES;
    self.contentView = _containerView;

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
    [_containerView addSubview:_nativeTabBarView];

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
    [_containerView addSubview:_nativeSubHeaderView];

    [self initializePageViewController];
  }
  return self;
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
  // OneKey patch: removing a decelerating page from its window can suppress
  // UIKit's final scroll callback. Restore the last acknowledged page on reattach.
  if (self.window != nil && _isPagerDragging &&
      !_pagerScrollView.dragging && !_pagerScrollView.decelerating) {
    [self finishPagerScrollEmittingSelection:NO];
  }
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
}

- (void)setDirectNativePagerEnabled:(BOOL)enabled
{
  if (_transitioning || _isPagerDragging) {
    _pendingDirectNativePagerEnabled = enabled;
    _hasPendingDirectNativePagerChange = YES;
    return;
  }
  _hasPendingDirectNativePagerChange = NO;
  if (_directNativePagerEnabled == enabled) return;

  [self detachScrollObserver];
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

    UIScrollView *directPager = [UIScrollView new];
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
  [self rebuildSlots];
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
  if (_transitioning || _isPagerDragging) {
    _needsSlotRebuild = YES;
    return;
  }
  _needsSlotRebuild = NO;
  [self detachScrollObserver];
  // OneKey patch: Fabric reuses these views without resetting transforms
  // written by a native parent. Release our collapse translation with the slot.
  _headerView.transform = CGAffineTransformIdentity;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  _nativeTabBarView.transform = CGAffineTransformIdentity;
  _nativeSubHeaderView.transform = CGAffineTransformIdentity;
  [_headerView removeFromSuperview];
  [_stickyHeaderView removeFromSuperview];
  for (UIViewController *controller in _pageControllers) {
    [controller.view removeFromSuperview];
  }
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
  if (_headerView != nil) [_containerView bringSubviewToFront:_headerView];
  if (_stickyHeaderView != nil) [_containerView bringSubviewToFront:_stickyHeaderView];
  if (!_nativeTabBarView.hidden) [_containerView bringSubviewToFront:_nativeTabBarView];
  if (!_nativeSubHeaderView.hidden) [_containerView bringSubviewToFront:_nativeSubHeaderView];
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
  // Lay out the original slots, then restore the shared collapse translation.
  _headerView.transform = CGAffineTransformIdentity;
  _stickyHeaderView.transform = CGAffineTransformIdentity;
  _nativeTabBarView.transform = CGAffineTransformIdentity;
  _nativeSubHeaderView.transform = CGAffineTransformIdentity;
  _headerView.frame = CGRectMake(0, 0, self.bounds.size.width, _headerHeight);
  _stickyHeaderView.frame = CGRectMake(
    0,
    _headerHeight,
    self.bounds.size.width,
    _stickyHeaderHeight
  );
  _nativeTabBarView.frame = CGRectMake(
    0,
    _headerHeight,
    self.bounds.size.width,
    MIN(_stickyHeaderHeight, _nativeTabBarHeight)
  );
  CGFloat nativeTabBarVisibleHeight = CGRectGetHeight(_nativeTabBarView.frame);
  _nativeSubHeaderView.frame = CGRectMake(
    0,
    _headerHeight + nativeTabBarVisibleHeight,
    self.bounds.size.width,
    MIN(
      MAX(0, _stickyHeaderHeight - nativeTabBarVisibleHeight),
      _nativeSubHeaderHeight
    )
  );
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
  _nativeTabBarView.transform = CGAffineTransformIdentity;
  _nativeSubHeaderView.transform = CGAffineTransformIdentity;
  [_headerView removeFromSuperview];
  [_stickyHeaderView removeFromSuperview];
  for (UIViewController *controller in _pageControllers) {
    [controller.view removeFromSuperview];
  }
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
  if (_directNativePagerEnabled) {
    [_pagerScrollView removeFromSuperview];
  }
  _pageViewController = nil;
  _pageViewControllerScrollView = nil;
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
  _nativeTabBarHeight = 44;
  _nativeSubHeaderHeight = 74;
  _headerOffset = 0;
  _scrollEnabled = YES;
  _nestedScrollEnabled = NO;
  _directNativePagerEnabled = NO;
  _pendingDirectNativePagerEnabled = NO;
  _hasPendingDirectNativePagerChange = NO;
  _transitioning = NO;
  _isPagerDragging = NO;
  _hasReceivedPageCommand = NO;
  _pendingGoToIndex = -1;
  _pendingGoToAnimated = YES;
  _needsSlotRebuild = NO;
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
  if (_needsPropsReapply || oldViewProps.nativeTabBarItems != newViewProps.nativeTabBarItems) {
    NSString *itemsJSON = RCTNSStringFromString(newViewProps.nativeTabBarItems);
    [_nativeTabBarView updateItemsJSON:itemsJSON];
    [self setDirectNativePagerEnabled:!_nativeTabBarView.hidden];
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
  if (insetsChanged) {
    [self reapplyInsetsToObservedScrollView];
    [self setNeedsLayout];
  }
  if (!_nativeTabBarView.hidden) {
    [_containerView bringSubviewToFront:_nativeTabBarView];
  }
  if (!_nativeSubHeaderView.hidden) {
    [_containerView bringSubviewToFront:_nativeSubHeaderView];
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
  _nativeTabBarView.transform = transform;
  _nativeSubHeaderView.transform = transform;
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
  if (_directNativePagerEnabled) {
    CGFloat targetOffset = [self directPagerOffsetForIndex:index];
    void (^animations)(void) = ^{
      [self->_pagerScrollView setContentOffset:CGPointMake(targetOffset, 0)];
      [self->_nativeTabBarView setProgress:index];
    };
    void (^completion)(BOOL) = ^(BOOL finished) {
      __strong __typeof__(weakSelf) self = weakSelf;
      if (self == nil || self->_generation != capturedGeneration ||
          self->_transitionId != capturedTransition || self->_isBeingRecycled) return;
      self->_pagerScrollView.scrollEnabled = self->_scrollEnabled;
      if (finished) {
        self->_currentIndex = index;
        self->_destinationIndex = index;
        [self->_nativeTabBarView setProgress:index];
        [self attachScrollObserverForCurrentPage];
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
      self->_currentIndex = index;
      self->_destinationIndex = index;
      [self->_nativeTabBarView setProgress:index];
      [self attachScrollObserverForCurrentPage];
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
  });
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
    [self attachScrollObserverForCurrentPage];
    if (emitSelection &&
        (_pendingGoToIndex < 0 || _pendingGoToIndex == index)) {
      [self emitPageSelected:index];
      [self emitDiagnostics:@"page-selected"];
    }
    _isPagerDragging = NO;
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
      [self attachScrollObserverForCurrentPage];
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
