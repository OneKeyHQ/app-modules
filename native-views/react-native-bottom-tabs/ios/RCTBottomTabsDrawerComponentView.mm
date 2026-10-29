#import "RCTBottomTabsDrawerComponentView.h"
#import <react/renderer/components/RNBottomTabs/ComponentDescriptors.h>
#import <react/renderer/components/RNBottomTabs/EventEmitters.h>
#import <react/renderer/components/RNBottomTabs/Props.h>
#import <React/RCTFabricComponentsPlugins.h>

// Swift keeps the host internal to this module, so its framework compatibility
// header does not export the class. Its explicit @objc name and selectors form
// this private boundary, matching the existing tab container's runtime lookup.
@protocol RNBottomTabsDrawerContainer <NSObject>
@property(nonatomic) BOOL enabled;
@property(nonatomic) BOOL open;
@property(nonatomic) BOOL swipeEnabled;
@property(nonatomic) BOOL hapticFeedbackEnabled;
@property(nonatomic) double bottomBarHeight;
@property(nonatomic, copy) void (^onDrawerOpenChange)(BOOL open);
- (void)insertChild:(UIView *)child atIndex:(NSInteger)index;
- (void)removeChildAtIndex:(NSInteger)index;
@end

using namespace facebook::react;

@implementation RCTBottomTabsDrawerComponentView {
  UIView<RNBottomTabsDrawerContainer> *_drawer;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<RNBottomTabsDrawerComponentDescriptor>();
}

+ (BOOL)shouldBeRecycled { return NO; }

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    _props = std::make_shared<const RNBottomTabsDrawerProps>();
    Class containerClass = NSClassFromString(@"RCTBottomTabsDrawerContainerView");
    _drawer = (UIView<RNBottomTabsDrawerContainer> *)[[containerClass alloc] initWithFrame:frame];
    self.contentView = _drawer;
    __weak RCTBottomTabsDrawerComponentView *weakSelf = self;
    _drawer.onDrawerOpenChange = ^(BOOL open) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const RNBottomTabsDrawerEventEmitter>(strongSelf->_eventEmitter);
      if (emitter) emitter->onDrawerOpenChange({.open = (bool)open});
    };
  }
  return self;
}

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)child index:(NSInteger)index
{
  [_drawer insertChild:child atIndex:index];
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)child index:(NSInteger)index
{
  [_drawer removeChildAtIndex:index];
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  const auto &next = *std::static_pointer_cast<const RNBottomTabsDrawerProps>(props);
  const auto &previous = *std::static_pointer_cast<const RNBottomTabsDrawerProps>(_props);
  if (next.enabled != previous.enabled) _drawer.enabled = next.enabled;
  if (next.open != previous.open) _drawer.open = next.open;
  if (next.swipeEnabled != previous.swipeEnabled) _drawer.swipeEnabled = next.swipeEnabled;
  if (next.hapticFeedbackEnabled != previous.hapticFeedbackEnabled)
    _drawer.hapticFeedbackEnabled = next.hapticFeedbackEnabled;
  if (next.bottomBarHeight != previous.bottomBarHeight) _drawer.bottomBarHeight = next.bottomBarHeight;
  [super updateProps:props oldProps:oldProps];
}
@end

Class<RCTComponentViewProtocol> RNBottomTabsDrawerCls(void)
{
  return RCTBottomTabsDrawerComponentView.class;
}
