#import "RCTBottomAccessoryComponentView.h"

#import <react/renderer/components/RNBottomTabs/ComponentDescriptors.h>
#import <react/renderer/components/RNBottomTabs/EventEmitters.h>
#import <react/renderer/components/RNBottomTabs/Props.h>
#import <react/renderer/components/RNBottomTabs/RCTComponentViewHelpers.h>

#import <React/RCTFabricComponentsPlugins.h>

#if __has_include(<BottomTabsModule/BottomTabsModule-Swift.h>)
#import <BottomTabsModule/BottomTabsModule-Swift.h>
#elif __has_include("BottomTabsModule/BottomTabsModule-Swift.h")
#import "BottomTabsModule/BottomTabsModule-Swift.h"
#elif __has_include("BottomTabsModule-Swift.h")
#import "BottomTabsModule-Swift.h"
#else
#import "react_native_bottom_tabs-Swift.h"
#endif

using namespace facebook::react;

@implementation RCTBottomAccessoryComponentView {
  UIView *_accessoryView;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<RNBottomTabsAccessoryViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const RNBottomTabsAccessoryViewProps>();
    _props = defaultProps;

    Class containerClass = NSClassFromString(@"BottomTabsModule.RCTBottomAccessoryContainerView");
    if (!containerClass) {
      containerClass = NSClassFromString(@"RCTBottomAccessoryContainerView");
    }
    _accessoryView = [[containerClass alloc] init];
    self.contentView = _accessoryView;
  }

  return self;
}

- (void)setFrame:(CGRect)frame
{
  [super setFrame:frame];
  if (frame.size.width > 0 && frame.size.height > 0) {
    auto eventEmitter = std::static_pointer_cast<const RNBottomTabsAccessoryViewEventEmitter>(_eventEmitter);
    if (eventEmitter) {
      eventEmitter->onNativeLayout(RNBottomTabsAccessoryViewEventEmitter::OnNativeLayout {
        .width = frame.size.width,
        .height = frame.size.height
      });
    }
  }
}

@end

Class<RCTComponentViewProtocol> RNBottomTabsAccessoryViewCls(void)
{
  return RCTBottomAccessoryComponentView.class;
}
