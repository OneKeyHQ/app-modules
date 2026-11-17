#import "RCTNativeOverlayToasterComponentView.h"

#import <react/renderer/components/RNCNativeOverlay/ComponentDescriptors.h>
#import <react/renderer/components/RNCNativeOverlay/EventEmitters.h>
#import <react/renderer/components/RNCNativeOverlay/Props.h>
#import <react/renderer/components/RNCNativeOverlay/RCTComponentViewHelpers.h>

#import <React/RCTComponent.h>
#import <React/RCTFabricComponentsPlugins.h>

#if __has_include(<NativeOverlay/NativeOverlay-Swift.h>)
#import <NativeOverlay/NativeOverlay-Swift.h>
#elif __has_include("NativeOverlay/NativeOverlay-Swift.h")
#import "NativeOverlay/NativeOverlay-Swift.h"
#else
#import "NativeOverlay-Swift.h"
#endif

using namespace facebook::react;

@implementation RCTNativeOverlayToasterComponentView {
  NativeOverlayToasterView *_toasterView;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<RNCNativeOverlayToasterComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const RNCNativeOverlayToasterProps>();
    _props = defaultProps;
    _toasterView = [NativeOverlayToasterView new];
    __weak __typeof(self) weakSelf = self;
    _toasterView.onToastEvent = ^(NSDictionary *body) {
      __typeof(self) strongSelf = weakSelf;
      if (!strongSelf || !strongSelf->_eventEmitter) {
        return;
      }
      auto emitter =
          std::static_pointer_cast<const RNCNativeOverlayToasterEventEmitter>(strongSelf->_eventEmitter);
      NSString *toastId = body[@"toastId"] ?: @"";
      NSNumber *generation = body[@"generation"];
      NSString *type = body[@"type"] ?: @"";
      NSString *value = body[@"value"] ?: @"";
      emitter->onToastEvent({
          .toastId = std::string(toastId.UTF8String),
          .generation = generation ? generation.intValue : 0,
          .type = std::string(type.UTF8String),
          .value = std::string(value.UTF8String),
      });
    };
    self.contentView = _toasterView;
  }
  return self;
}

// The host is created once and keeps the toast window alive across updates.
+ (BOOL)shouldBeRecycled
{
  return NO;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  const auto &newProps = *std::static_pointer_cast<RNCNativeOverlayToasterProps const>(props);
  [_toasterView updateWithToasts:[NSString stringWithUTF8String:newProps.toasts.c_str()]
                          config:[NSString stringWithUTF8String:newProps.config.c_str()]];
  [super updateProps:props oldProps:oldProps];
}

- (void)invalidate
{
  [_toasterView invalidate];
  [super invalidate];
}

@end

Class<RCTComponentViewProtocol> RNCNativeOverlayToasterCls(void)
{
  return RCTNativeOverlayToasterComponentView.class;
}
