#ifdef RCT_NEW_ARCH_ENABLED
#import "RCTTabViewComponentView.h"

#import <react/renderer/components/RNCTabView/RNCTabViewComponentDescriptor.h>
#import <react/renderer/components/RNCTabView/EventEmitters.h>
#import <react/renderer/components/RNCTabView/Props.h>
#import <react/renderer/components/RNCTabView/RCTComponentViewHelpers.h>

#import <React/RCTFabricComponentsPlugins.h>
#import <React/RCTConversions.h>
#import <React/RCTImageLoader.h>
#import <React/RCTImageSource.h>
#import <React/RCTBridge+Private.h>
#import <react/utils/ManagedObjectWrapper.h>

#if __has_include("TabViewModule/TabViewModule-Swift.h")
#import "TabViewModule/TabViewModule-Swift.h"
#else
#import "TabViewModule-Swift.h"
#endif

using namespace facebook::react;

static inline NSString* _Nullable RCTNSStringFromStringNilIfEmpty(const std::string &string) {
  return string.empty() ? nil : [NSString stringWithUTF8String:string.c_str()];
}

// Overload `==` and `!=` operators for `RNCTabViewItemsStruct`
namespace facebook::react {

bool operator==(const RNCTabViewItemsStruct& lhs, const RNCTabViewItemsStruct& rhs) {
  return lhs.key == rhs.key &&
  lhs.title == rhs.title &&
  lhs.sfSymbol == rhs.sfSymbol &&
  lhs.badge == rhs.badge &&
  lhs.activeTintColor == rhs.activeTintColor &&
  lhs.hidden == rhs.hidden &&
  lhs.testID == rhs.testID &&
  lhs.role == rhs.role &&
  lhs.preventsDefault == rhs.preventsDefault;
}

bool operator!=(const RNCTabViewItemsStruct& lhs, const RNCTabViewItemsStruct& rhs) {
  return !(lhs == rhs);
}

}

static NSNumber* _Nullable colorToProcessedInt(const SharedColor &color) {
  UIColor *uiColor = RCTUIColorFromSharedColor(color);
  if (!uiColor) return nil;
  CGFloat r, g, b, a;
  [uiColor getRed:&r green:&g blue:&b alpha:&a];
  int32_t colorInt = ((int)(a * 255) << 24) | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
  return @(colorInt);
}

static NSArray<NSDictionary *>* convertItemsToArray(const std::vector<RNCTabViewItemsStruct>& items) {
  NSMutableArray *result = [NSMutableArray arrayWithCapacity:items.size()];

  for (const auto& item : items) {
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    dict[@"key"] = [NSString stringWithUTF8String:item.key.c_str()];
    dict[@"title"] = [NSString stringWithUTF8String:item.title.c_str()];

    if (!item.sfSymbol.empty()) {
      dict[@"sfSymbol"] = [NSString stringWithUTF8String:item.sfSymbol.c_str()];
    }
    if (!item.badge.empty()) {
      dict[@"badge"] = [NSString stringWithUTF8String:item.badge.c_str()];
    }

    NSNumber *activeTint = colorToProcessedInt(item.activeTintColor);
    if (activeTint) dict[@"activeTintColor"] = activeTint;

    NSNumber *badgeBg = colorToProcessedInt(item.badgeBackgroundColor);
    if (badgeBg) dict[@"badgeBackgroundColor"] = badgeBg;

    NSNumber *badgeTxt = colorToProcessedInt(item.badgeTextColor);
    if (badgeTxt) dict[@"badgeTextColor"] = badgeTxt;

    dict[@"hidden"] = @(item.hidden);
    dict[@"preventsDefault"] = @(item.preventsDefault);

    if (!item.testID.empty()) {
      dict[@"testID"] = [NSString stringWithUTF8String:item.testID.c_str()];
    }
    if (!item.role.empty()) {
      dict[@"role"] = [NSString stringWithUTF8String:item.role.c_str()];
    }

    [result addObject:dict];
  }

  return result;
}

@interface RCTTabViewComponentView () <RCTRNCTabViewViewProtocol>
@end

@implementation RCTTabViewComponentView {
  RCTTabViewContainerView *_containerView;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<RNCTabViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const RNCTabViewProps>();
    _props = defaultProps;

    _containerView = [[RCTTabViewContainerView alloc] init];
    self.contentView = _containerView;
  }

  return self;
}

+ (BOOL)shouldBeRecycled
{
  return NO;
}

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index {
  [_containerView insertChild:childComponentView atIndex:index];
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index {
  [_containerView removeChildAtIndex:index];
  [childComponentView removeFromSuperview];
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  const auto &oldViewProps = *std::static_pointer_cast<RNCTabViewProps const>(_props);
  const auto &newViewProps = *std::static_pointer_cast<RNCTabViewProps const>(props);

  if (oldViewProps.items != newViewProps.items) {
    _containerView.items = convertItemsToArray(newViewProps.items);
  }

  if (oldViewProps.translucent != newViewProps.translucent) {
    _containerView.translucent = @(newViewProps.translucent);
  }

  if (oldViewProps.icons != newViewProps.icons) {
    auto iconsArray = [[NSMutableArray alloc] init];
    for (auto &source: newViewProps.icons) {
      NSMutableDictionary *iconDict = [NSMutableDictionary dictionary];
      iconDict[@"uri"] = [NSString stringWithUTF8String:source.uri.c_str()];
      iconDict[@"width"] = @(source.size.width);
      iconDict[@"height"] = @(source.size.height);
      iconDict[@"scale"] = @(source.scale);
      [iconsArray addObject:iconDict];
    }
    _containerView.icons = iconsArray;
  }

  if (oldViewProps.sidebarAdaptable != newViewProps.sidebarAdaptable) {
    _containerView.sidebarAdaptable = newViewProps.sidebarAdaptable;
  }

  if (oldViewProps.minimizeBehavior != newViewProps.minimizeBehavior) {
    _containerView.minimizeBehavior = RCTNSStringFromStringNilIfEmpty(newViewProps.minimizeBehavior);
  }

  if (oldViewProps.disablePageAnimations != newViewProps.disablePageAnimations) {
    _containerView.disablePageAnimations = newViewProps.disablePageAnimations;
  }

  if (oldViewProps.labeled != newViewProps.labeled) {
    _containerView.labeled = @(newViewProps.labeled);
  }

  if (oldViewProps.selectedPage != newViewProps.selectedPage) {
    _containerView.selectedPage = [NSString stringWithUTF8String:newViewProps.selectedPage.c_str()];
  }

  if (oldViewProps.scrollEdgeAppearance != newViewProps.scrollEdgeAppearance) {
    _containerView.scrollEdgeAppearance = RCTNSStringFromStringNilIfEmpty(newViewProps.scrollEdgeAppearance);
  }

  if (oldViewProps.barTintColor != newViewProps.barTintColor) {
    _containerView.barTintColor = RCTUIColorFromSharedColor(newViewProps.barTintColor);
  }

  if (oldViewProps.activeTintColor != newViewProps.activeTintColor) {
    _containerView.activeTintColor = RCTUIColorFromSharedColor(newViewProps.activeTintColor);
  }

  if (oldViewProps.inactiveTintColor != newViewProps.inactiveTintColor) {
    _containerView.inactiveTintColor = RCTUIColorFromSharedColor(newViewProps.inactiveTintColor);
  }

  if (oldViewProps.hapticFeedbackEnabled != newViewProps.hapticFeedbackEnabled) {
    _containerView.hapticFeedbackEnabled = newViewProps.hapticFeedbackEnabled;
  }

  if (oldViewProps.fontSize != newViewProps.fontSize) {
    _containerView.fontSize = @(newViewProps.fontSize);
  }

  if (oldViewProps.fontWeight != newViewProps.fontWeight) {
    _containerView.fontWeight = RCTNSStringFromStringNilIfEmpty(newViewProps.fontWeight);
  }

  if (oldViewProps.fontFamily != newViewProps.fontFamily) {
    _containerView.fontFamily = RCTNSStringFromStringNilIfEmpty(newViewProps.fontFamily);
  }

  if (oldViewProps.tabBarHidden != newViewProps.tabBarHidden) {
    _containerView.tabBarHidden = newViewProps.tabBarHidden;
  }

  [super updateProps:props oldProps:oldProps];
}

- (void)updateState:(const facebook::react::State::Shared &)state oldState:(const facebook::react::State::Shared &)oldState
{
  auto _state = std::static_pointer_cast<RNCTabViewShadowNode::ConcreteState const>(state);
  auto data = _state->getData();
  if (auto imgLoaderPtr = _state.get()->getData().getImageLoader().lock()) {
    [_containerView setImageLoader:unwrapManagedObject(imgLoaderPtr)];
  }
}

- (void)prepareForRecycle
{
  [super prepareForRecycle];
}

@end

Class<RCTComponentViewProtocol> RNCTabViewCls(void)
{
  return RCTTabViewComponentView.class;
}

#endif // RCT_NEW_ARCH_ENABLED
