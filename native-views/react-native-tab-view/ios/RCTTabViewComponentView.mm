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

#if __has_include(<TabViewModule/TabViewModule-Swift.h>)
#import <TabViewModule/TabViewModule-Swift.h>
#elif __has_include("TabViewModule/TabViewModule-Swift.h")
#import "TabViewModule/TabViewModule-Swift.h"
#elif __has_include("TabViewModule-Swift.h")
#import "TabViewModule-Swift.h"
#else
#import "react_native_tab_view-Swift.h"
#endif

using namespace facebook::react;

static inline NSString* _Nullable NSStringFromStdStringNilIfEmpty(const std::string &string) {
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
  UIView *_containerView;
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

    Class containerClass = NSClassFromString(@"TabViewModule.RCTTabViewContainerView");
    if (!containerClass) {
      containerClass = NSClassFromString(@"RCTTabViewContainerView");
    }
    _containerView = [[containerClass alloc] init];
    self.contentView = _containerView;

    // Set up event forwarding from Swift container to Fabric EventEmitter.
    // Swift container uses RCTDirectEventBlock (void (^)(NSDictionary *)) for events,
    // but in Fabric mode we need to forward them through the C++ EventEmitter.
    typedef void (^EventBlock)(NSDictionary *);
    __weak auto weakSelf = self;

    EventBlock onPageSelected = ^(NSDictionary *body) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const RNCTabViewEventEmitter>(strongSelf->_eventEmitter);
      if (emitter) {
        emitter->onPageSelected({.key = std::string([[body objectForKey:@"key"] UTF8String] ?: "")});
      }
    };
    [_containerView setValue:onPageSelected forKey:@"onPageSelected"];

    EventBlock onTabLongPress = ^(NSDictionary *body) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const RNCTabViewEventEmitter>(strongSelf->_eventEmitter);
      if (emitter) {
        emitter->onTabLongPress({.key = std::string([[body objectForKey:@"key"] UTF8String] ?: "")});
      }
    };
    [_containerView setValue:onTabLongPress forKey:@"onTabLongPress"];

    EventBlock onTabBarMeasured = ^(NSDictionary *body) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const RNCTabViewEventEmitter>(strongSelf->_eventEmitter);
      if (emitter) {
        emitter->onTabBarMeasured({.height = [[body objectForKey:@"height"] intValue]});
      }
    };
    [_containerView setValue:onTabBarMeasured forKey:@"onTabBarMeasured"];

    EventBlock onNativeLayout = ^(NSDictionary *body) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const RNCTabViewEventEmitter>(strongSelf->_eventEmitter);
      if (emitter) {
        emitter->onNativeLayout({
          .width = [[body objectForKey:@"width"] doubleValue],
          .height = [[body objectForKey:@"height"] doubleValue]
        });
      }
    };
    [_containerView setValue:onNativeLayout forKey:@"onNativeLayout"];
  }

  return self;
}

+ (BOOL)shouldBeRecycled
{
  return NO;
}

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index {
  SEL sel = @selector(insertChild:atIndex:);
  if ([_containerView respondsToSelector:sel]) {
    NSMethodSignature *sig = [_containerView methodSignatureForSelector:sel];
    NSInvocation *inv = [NSInvocation invocationWithMethodSignature:sig];
    [inv setSelector:sel];
    [inv setTarget:_containerView];
    UIView *child = childComponentView;
    [inv setArgument:&child atIndex:2];
    [inv setArgument:&index atIndex:3];
    [inv invoke];
  }
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index {
  SEL sel = @selector(removeChildAtIndex:);
  if ([_containerView respondsToSelector:sel]) {
    NSMethodSignature *sig = [_containerView methodSignatureForSelector:sel];
    NSInvocation *inv = [NSInvocation invocationWithMethodSignature:sig];
    [inv setSelector:sel];
    [inv setTarget:_containerView];
    [inv setArgument:&index atIndex:2];
    [inv invoke];
  }
  [childComponentView removeFromSuperview];
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  const auto &oldViewProps = *std::static_pointer_cast<RNCTabViewProps const>(_props);
  const auto &newViewProps = *std::static_pointer_cast<RNCTabViewProps const>(props);

#define SET_PROP(prop, value) \
  if ([_containerView respondsToSelector:@selector(setProp:)]) { \
    [_containerView setValue:value forKey:@#prop]; \
  }

  if (oldViewProps.items != newViewProps.items) {
    [_containerView setValue:convertItemsToArray(newViewProps.items) forKey:@"items"];
  }

  if (oldViewProps.translucent != newViewProps.translucent) {
    [_containerView setValue:@(newViewProps.translucent) forKey:@"translucent"];
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
    [_containerView setValue:iconsArray forKey:@"icons"];
  }

  if (oldViewProps.sidebarAdaptable != newViewProps.sidebarAdaptable) {
    [_containerView setValue:@(newViewProps.sidebarAdaptable) forKey:@"sidebarAdaptable"];
  }

  if (oldViewProps.minimizeBehavior != newViewProps.minimizeBehavior) {
    [_containerView setValue:NSStringFromStdStringNilIfEmpty(newViewProps.minimizeBehavior) forKey:@"minimizeBehavior"];
  }

  if (oldViewProps.disablePageAnimations != newViewProps.disablePageAnimations) {
    [_containerView setValue:@(newViewProps.disablePageAnimations) forKey:@"disablePageAnimations"];
  }

  if (oldViewProps.labeled != newViewProps.labeled) {
    [_containerView setValue:@(newViewProps.labeled) forKey:@"labeled"];
  }

  if (oldViewProps.selectedPage != newViewProps.selectedPage) {
    [_containerView setValue:[NSString stringWithUTF8String:newViewProps.selectedPage.c_str()] forKey:@"selectedPage"];
  }

  if (oldViewProps.scrollEdgeAppearance != newViewProps.scrollEdgeAppearance) {
    [_containerView setValue:NSStringFromStdStringNilIfEmpty(newViewProps.scrollEdgeAppearance) forKey:@"scrollEdgeAppearance"];
  }

  if (oldViewProps.barTintColor != newViewProps.barTintColor) {
    [_containerView setValue:RCTUIColorFromSharedColor(newViewProps.barTintColor) forKey:@"barTintColor"];
  }

  if (oldViewProps.activeTintColor != newViewProps.activeTintColor) {
    [_containerView setValue:RCTUIColorFromSharedColor(newViewProps.activeTintColor) forKey:@"activeTintColor"];
  }

  if (oldViewProps.inactiveTintColor != newViewProps.inactiveTintColor) {
    [_containerView setValue:RCTUIColorFromSharedColor(newViewProps.inactiveTintColor) forKey:@"inactiveTintColor"];
  }

  if (oldViewProps.hapticFeedbackEnabled != newViewProps.hapticFeedbackEnabled) {
    [_containerView setValue:@(newViewProps.hapticFeedbackEnabled) forKey:@"hapticFeedbackEnabled"];
  }

  if (oldViewProps.fontSize != newViewProps.fontSize) {
    [_containerView setValue:@(newViewProps.fontSize) forKey:@"fontSize"];
  }

  if (oldViewProps.fontWeight != newViewProps.fontWeight) {
    [_containerView setValue:NSStringFromStdStringNilIfEmpty(newViewProps.fontWeight) forKey:@"fontWeight"];
  }

  if (oldViewProps.fontFamily != newViewProps.fontFamily) {
    [_containerView setValue:NSStringFromStdStringNilIfEmpty(newViewProps.fontFamily) forKey:@"fontFamily"];
  }

  if (oldViewProps.tabBarHidden != newViewProps.tabBarHidden) {
    [_containerView setValue:@(newViewProps.tabBarHidden) forKey:@"tabBarHidden"];
  }

#undef SET_PROP

  [super updateProps:props oldProps:oldProps];
}

- (void)updateState:(const facebook::react::State::Shared &)state oldState:(const facebook::react::State::Shared &)oldState
{
  auto _state = std::static_pointer_cast<RNCTabViewShadowNode::ConcreteState const>(state);
  auto data = _state->getData();
  if (auto imgLoaderPtr = _state.get()->getData().getImageLoader().lock()) {
    id imageLoader = unwrapManagedObject(imgLoaderPtr);
    if ([_containerView respondsToSelector:@selector(setImageLoader:)]) {
      [_containerView performSelector:@selector(setImageLoader:) withObject:imageLoader];
    }
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
