#import "RCTNativeSheetComponentView.h"

#import <react/renderer/components/RNCNativeSheet/ComponentDescriptors.h>
#import <react/renderer/components/RNCNativeSheet/EventEmitters.h>
#import <react/renderer/components/RNCNativeSheet/Props.h>
#import <react/renderer/components/RNCNativeSheet/RCTComponentViewHelpers.h>

#import <React/RCTConversions.h>
#import <React/RCTFabricComponentsPlugins.h>
#import <React/RCTSurfaceTouchHandler.h>

#if __has_include(<NativeSheet/NativeSheet-Swift.h>)
#import <NativeSheet/NativeSheet-Swift.h>
#elif __has_include("NativeSheet/NativeSheet-Swift.h")
#import "NativeSheet/NativeSheet-Swift.h"
#elif __has_include("NativeSheet-Swift.h")
#import "NativeSheet-Swift.h"
#else
#import "react_native_native_sheet-Swift.h"
#endif

using namespace facebook::react;

static void RNCNativeSheetInvokeVoidSelector(id target, SEL selector)
{
  if (![target respondsToSelector:selector]) return;
  auto implementation = (void (*)(id, SEL))[target methodForSelector:selector];
  implementation(target, selector);
}

@implementation RCTNativeSheetComponentView {
  UIView *_nativeSheetView;
  RCTSurfaceTouchHandler *_touchHandler;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<RNCNativeSheetComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const RNCNativeSheetProps>();
    _props = defaultProps;

    Class containerClass = NSClassFromString(@"NativeSheet.NativeSheetContainerView");
    if (!containerClass) {
      containerClass = NSClassFromString(@"NativeSheetContainerView");
    }
    _nativeSheetView = [[containerClass alloc] init];
    _touchHandler = [RCTSurfaceTouchHandler new];
    self.contentView = _nativeSheetView;
    [_nativeSheetView setValue:_touchHandler forKey:@"touchHandler"];

    typedef void (^EventBlock)(NSDictionary *);
    __weak auto weakSelf = self;
    EventBlock onDismiss = ^(NSDictionary *body) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const RNCNativeSheetEventEmitter>(strongSelf->_eventEmitter);
      if (!emitter) return;
      NSString *reason = [body objectForKey:@"reason"] ?: @"system";
      emitter->onDismiss({.reason = std::string(reason.UTF8String)});
    };
    EventBlock onPresented = ^(NSDictionary *body) {
      auto strongSelf = weakSelf;
      if (!strongSelf) return;
      auto emitter = std::static_pointer_cast<const RNCNativeSheetEventEmitter>(strongSelf->_eventEmitter);
      if (!emitter) return;
      NSNumber *height = [body objectForKey:@"height"] ?: @0;
      emitter->onPresented({.height = height.doubleValue});
    };
    [_nativeSheetView setValue:onDismiss forKey:@"onDismiss"];
    [_nativeSheetView setValue:onPresented forKey:@"onPresented"];
  }
  return self;
}

+ (BOOL)shouldBeRecycled
{
  return NO;
}

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView
                          index:(NSInteger)index
{
  SEL selector = NSSelectorFromString(@"insertChild:atIndex:");
  if (![_nativeSheetView respondsToSelector:selector]) return;
  NSMethodSignature *signature = [_nativeSheetView methodSignatureForSelector:selector];
  NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:signature];
  UIView *child = childComponentView;
  [invocation setSelector:selector];
  [invocation setTarget:_nativeSheetView];
  [invocation setArgument:&child atIndex:2];
  [invocation setArgument:&index atIndex:3];
  [invocation invoke];
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView
                            index:(NSInteger)index
{
  SEL selector = NSSelectorFromString(@"removeChild:");
  if ([_nativeSheetView respondsToSelector:selector]) {
    NSMethodSignature *signature = [_nativeSheetView methodSignatureForSelector:selector];
    NSInvocation *invocation = [NSInvocation invocationWithMethodSignature:signature];
    UIView *child = childComponentView;
    [invocation setSelector:selector];
    [invocation setTarget:_nativeSheetView];
    [invocation setArgument:&child atIndex:2];
    [invocation invoke];
  }
  [childComponentView removeFromSuperview];
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
  const auto &newProps = *std::static_pointer_cast<RNCNativeSheetProps const>(props);

  [_nativeSheetView setValue:@(newProps.sheetHeight) forKey:@"sheetHeight"];
  [_nativeSheetView setValue:@(newProps.securityBlocked) forKey:@"securityBlocked"];
  [_nativeSheetView setValue:@(newProps.dismissOnPanDown) forKey:@"dismissOnPanDown"];
  [_nativeSheetView setValue:@(newProps.dismissOnBackdropPress) forKey:@"dismissOnBackdropPress"];
  [_nativeSheetView setValue:@(newProps.dismissOnBackPress) forKey:@"dismissOnBackPress"];
  [_nativeSheetView setValue:@(newProps.showHandle) forKey:@"showHandle"];
  [_nativeSheetView setValue:@(newProps.cornerRadius) forKey:@"cornerRadius"];
  [_nativeSheetView setValue:@(newProps.dimAmount) forKey:@"dimAmount"];
  [_nativeSheetView setValue:RCTUIColorFromSharedColor(newProps.sheetBackgroundColor)
                      forKey:@"sheetBackgroundColor"];
  [_nativeSheetView setValue:@(newProps.open) forKey:@"open"];
  SEL commitSelector = NSSelectorFromString(@"commitConfiguration");
  RNCNativeSheetInvokeVoidSelector(_nativeSheetView, commitSelector);

  [super updateProps:props oldProps:oldProps];
}

- (void)prepareForRecycle
{
  SEL invalidateSelector = NSSelectorFromString(@"invalidate");
  RNCNativeSheetInvokeVoidSelector(_nativeSheetView, invalidateSelector);
  [super prepareForRecycle];
}

- (void)invalidate
{
  SEL invalidateSelector = NSSelectorFromString(@"invalidate");
  RNCNativeSheetInvokeVoidSelector(_nativeSheetView, invalidateSelector);
  [super invalidate];
}

@end

Class<RCTComponentViewProtocol> RNCNativeSheetCls(void)
{
  return RCTNativeSheetComponentView.class;
}
