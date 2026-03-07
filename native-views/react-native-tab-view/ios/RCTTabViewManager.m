#import <React/RCTViewManager.h>
#import <React/RCTBridge.h>
#import <React/RCTImageLoader.h>

@interface RCTTabViewManager : RCTViewManager
@end

@implementation RCTTabViewManager

RCT_EXPORT_MODULE(RNCTabView)

- (UIView *)view
{
  // Import the Swift class via the module's generated header
  Class containerClass = NSClassFromString(@"TabViewModule.RCTTabViewContainerView");
  if (!containerClass) {
    containerClass = NSClassFromString(@"RCTTabViewContainerView");
  }
  UIView *view = [[containerClass alloc] init];

  // Set image loader
  RCTImageLoader *imageLoader = [self.bridge moduleForClass:[RCTImageLoader class]];
  if (imageLoader && [view respondsToSelector:@selector(setImageLoader:)]) {
    [view performSelector:@selector(setImageLoader:) withObject:imageLoader];
  }

  return view;
}

// Tab data
RCT_EXPORT_VIEW_PROPERTY(items, NSArray)
RCT_EXPORT_VIEW_PROPERTY(selectedPage, NSString)
RCT_EXPORT_VIEW_PROPERTY(icons, NSArray)

// Display settings
RCT_EXPORT_VIEW_PROPERTY(labeled, NSNumber)
RCT_EXPORT_VIEW_PROPERTY(sidebarAdaptable, BOOL)
RCT_EXPORT_VIEW_PROPERTY(disablePageAnimations, BOOL)
RCT_EXPORT_VIEW_PROPERTY(hapticFeedbackEnabled, BOOL)
RCT_EXPORT_VIEW_PROPERTY(scrollEdgeAppearance, NSString)
RCT_EXPORT_VIEW_PROPERTY(minimizeBehavior, NSString)
RCT_EXPORT_VIEW_PROPERTY(tabBarHidden, BOOL)
RCT_EXPORT_VIEW_PROPERTY(translucent, NSNumber)

// Colors (UIColor for Paper compatibility with processColor)
RCT_EXPORT_VIEW_PROPERTY(barTintColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(activeTintColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(inactiveTintColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(rippleColor, UIColor)
RCT_EXPORT_VIEW_PROPERTY(activeIndicatorColor, UIColor)

// Font
RCT_EXPORT_VIEW_PROPERTY(fontFamily, NSString)
RCT_EXPORT_VIEW_PROPERTY(fontWeight, NSString)
RCT_EXPORT_VIEW_PROPERTY(fontSize, NSNumber)

// Events
RCT_EXPORT_VIEW_PROPERTY(onPageSelected, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onTabLongPress, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onTabBarMeasured, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onNativeLayout, RCTDirectEventBlock)

@end
