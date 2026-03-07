#import "RCTBottomAccessoryComponentView.h"

#import <react/renderer/components/RNCTabView/ComponentDescriptors.h>
#import <react/renderer/components/RNCTabView/EventEmitters.h>
#import <react/renderer/components/RNCTabView/Props.h>
#import <react/renderer/components/RNCTabView/RCTComponentViewHelpers.h>

#import <React/RCTFabricComponentsPlugins.h>

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

@implementation RCTBottomAccessoryComponentView {
  UIView *_accessoryView;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<BottomAccessoryViewComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const BottomAccessoryViewProps>();
    _props = defaultProps;

    Class containerClass = NSClassFromString(@"TabViewModule.RCTBottomAccessoryContainerView");
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
    auto eventEmitter = std::static_pointer_cast<const BottomAccessoryViewEventEmitter>(_eventEmitter);
    if (eventEmitter) {
      eventEmitter->onNativeLayout(BottomAccessoryViewEventEmitter::OnNativeLayout {
        .width = frame.size.width,
        .height = frame.size.height
      });
    }
  }
}

@end

Class<RCTComponentViewProtocol> BottomAccessoryViewCls(void)
{
  return RCTBottomAccessoryComponentView.class;
}
