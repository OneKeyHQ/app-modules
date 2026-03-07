#import <React/RCTViewManager.h>

@interface RCTBottomAccessoryViewManager : RCTViewManager
@end

@implementation RCTBottomAccessoryViewManager

RCT_EXPORT_MODULE(BottomAccessoryView)

- (UIView *)view
{
  Class containerClass = NSClassFromString(@"TabViewModule.RCTBottomAccessoryContainerView");
  if (!containerClass) {
    containerClass = NSClassFromString(@"RCTBottomAccessoryContainerView");
  }
  return [[containerClass alloc] init];
}

RCT_EXPORT_VIEW_PROPERTY(onNativeLayout, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onPlacementChanged, RCTDirectEventBlock)

@end
