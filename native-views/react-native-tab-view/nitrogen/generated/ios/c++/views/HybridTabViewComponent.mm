///
/// HybridTabViewComponent.mm
/// Custom component view that adds child view management to the Nitro-generated TabView.
/// This file replaces the nitrogen-generated HybridTabViewComponent.mm after codegen.
///

#import "HybridTabViewComponent.hpp"
#import <memory>
#import <react/renderer/componentregistry/ComponentDescriptorProvider.h>
#import <React/RCTViewComponentView.h>
#import <React/RCTComponentViewFactory.h>
#import <React/UIView+ComponentViewProtocol.h>
#import <NitroModules/NitroDefines.hpp>
#import <UIKit/UIKit.h>

#import "HybridTabViewSpecSwift.hpp"
#import "TabViewModule-Swift-Cxx-Umbrella.hpp"

using namespace facebook;
using namespace margelo::nitro::tabview;
using namespace margelo::nitro::tabview::views;

/**
 * Custom React Native View holder for the Nitro "TabView" HybridView.
 * Extends the generated component to support React child views.
 */
@interface HybridTabViewComponent: RCTViewComponentView
@end

@implementation HybridTabViewComponent {
  std::shared_ptr<HybridTabViewSpecSwift> _hybridView;
}

+ (void) load {
  [super load];
  [RCTComponentViewFactory.currentComponentViewFactory registerComponentViewClass:[HybridTabViewComponent class]];
}

+ (react::ComponentDescriptorProvider) componentDescriptorProvider {
  return react::concreteComponentDescriptorProvider<HybridTabViewComponentDescriptor>();
}

- (instancetype) init {
  if (self = [super init]) {
    std::shared_ptr<HybridTabViewSpec> hybridView = TabViewModule::TabViewModuleAutolinking::createTabView();
    _hybridView = std::dynamic_pointer_cast<HybridTabViewSpecSwift>(hybridView);
    [self updateView];
  }
  return self;
}

- (void) updateView {
  // 1. Get Swift part
  TabViewModule::HybridTabViewSpec_cxx& swiftPart = _hybridView->getSwiftPart();

  // 2. Get UIView*
  void* viewUnsafe = swiftPart.getView();
  UIView* view = (__bridge_transfer UIView*) viewUnsafe;

  // 3. Update RCTViewComponentView's [contentView]
  [self setContentView:view];
}

#pragma mark - Child view management

static BOOL isBottomAccessoryView(UIView *view) {
  NSString *className = NSStringFromClass([view class]);
  return [className containsString:@"BottomAccessory"];
}

- (void)mountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index {
  // Add the child view to our content view
  [self.contentView insertSubview:childComponentView atIndex:index];

  TabViewModule::HybridTabViewSpec_cxx& swiftPart = _hybridView->getSwiftPart();

  if (isBottomAccessoryView(childComponentView)) {
    // Bottom accessory view - pass index -1 to signal special handling
    try {
      swiftPart.insertChild(static_cast<double>(childComponentView.tag), -1.0);
    } catch (...) {}
  } else {
    // Regular tab content child
    try {
      swiftPart.insertChild(static_cast<double>(childComponentView.tag), static_cast<double>(index));
    } catch (...) {}
  }
}

- (void)unmountChildComponentView:(UIView<RCTComponentViewProtocol> *)childComponentView index:(NSInteger)index {
  TabViewModule::HybridTabViewSpec_cxx& swiftPart = _hybridView->getSwiftPart();

  if (isBottomAccessoryView(childComponentView)) {
    try {
      swiftPart.removeChild(static_cast<double>(childComponentView.tag), -1.0);
    } catch (...) {}
  } else {
    try {
      swiftPart.removeChild(static_cast<double>(childComponentView.tag), static_cast<double>(index));
    } catch (...) {}
  }

  [childComponentView removeFromSuperview];
}

#pragma mark - Props

- (void) updateProps:(const std::shared_ptr<const react::Props>&)props
            oldProps:(const std::shared_ptr<const react::Props>&)oldProps {
  // 1. Downcast props
  const auto& newViewPropsConst = *std::static_pointer_cast<HybridTabViewProps const>(props);
  auto& newViewProps = const_cast<HybridTabViewProps&>(newViewPropsConst);
  TabViewModule::HybridTabViewSpec_cxx& swiftPart = _hybridView->getSwiftPart();

  // 2. Update each prop individually
  swiftPart.beforeUpdate();

  // items: optional
  if (newViewProps.items.isDirty) {
    swiftPart.setItems(newViewProps.items.value);
    newViewProps.items.isDirty = false;
  }
  // selectedPage: optional
  if (newViewProps.selectedPage.isDirty) {
    swiftPart.setSelectedPage(newViewProps.selectedPage.value);
    newViewProps.selectedPage.isDirty = false;
  }
  // icons: optional
  if (newViewProps.icons.isDirty) {
    swiftPart.setIcons(newViewProps.icons.value);
    newViewProps.icons.isDirty = false;
  }
  // labeled: optional
  if (newViewProps.labeled.isDirty) {
    swiftPart.setLabeled(newViewProps.labeled.value);
    newViewProps.labeled.isDirty = false;
  }
  // sidebarAdaptable: optional
  if (newViewProps.sidebarAdaptable.isDirty) {
    swiftPart.setSidebarAdaptable(newViewProps.sidebarAdaptable.value);
    newViewProps.sidebarAdaptable.isDirty = false;
  }
  // disablePageAnimations: optional
  if (newViewProps.disablePageAnimations.isDirty) {
    swiftPart.setDisablePageAnimations(newViewProps.disablePageAnimations.value);
    newViewProps.disablePageAnimations.isDirty = false;
  }
  // hapticFeedbackEnabled: optional
  if (newViewProps.hapticFeedbackEnabled.isDirty) {
    swiftPart.setHapticFeedbackEnabled(newViewProps.hapticFeedbackEnabled.value);
    newViewProps.hapticFeedbackEnabled.isDirty = false;
  }
  // scrollEdgeAppearance: optional
  if (newViewProps.scrollEdgeAppearance.isDirty) {
    swiftPart.setScrollEdgeAppearance(newViewProps.scrollEdgeAppearance.value);
    newViewProps.scrollEdgeAppearance.isDirty = false;
  }
  // minimizeBehavior: optional
  if (newViewProps.minimizeBehavior.isDirty) {
    swiftPart.setMinimizeBehavior(newViewProps.minimizeBehavior.value);
    newViewProps.minimizeBehavior.isDirty = false;
  }
  // tabBarHidden: optional
  if (newViewProps.tabBarHidden.isDirty) {
    swiftPart.setTabBarHidden(newViewProps.tabBarHidden.value);
    newViewProps.tabBarHidden.isDirty = false;
  }
  // translucent: optional
  if (newViewProps.translucent.isDirty) {
    swiftPart.setTranslucent(newViewProps.translucent.value);
    newViewProps.translucent.isDirty = false;
  }
  // barTintColor: optional
  if (newViewProps.barTintColor.isDirty) {
    swiftPart.setBarTintColor(newViewProps.barTintColor.value);
    newViewProps.barTintColor.isDirty = false;
  }
  // activeTintColor: optional
  if (newViewProps.activeTintColor.isDirty) {
    swiftPart.setActiveTintColor(newViewProps.activeTintColor.value);
    newViewProps.activeTintColor.isDirty = false;
  }
  // inactiveTintColor: optional
  if (newViewProps.inactiveTintColor.isDirty) {
    swiftPart.setInactiveTintColor(newViewProps.inactiveTintColor.value);
    newViewProps.inactiveTintColor.isDirty = false;
  }
  // rippleColor: optional
  if (newViewProps.rippleColor.isDirty) {
    swiftPart.setRippleColor(newViewProps.rippleColor.value);
    newViewProps.rippleColor.isDirty = false;
  }
  // activeIndicatorColor: optional
  if (newViewProps.activeIndicatorColor.isDirty) {
    swiftPart.setActiveIndicatorColor(newViewProps.activeIndicatorColor.value);
    newViewProps.activeIndicatorColor.isDirty = false;
  }
  // fontFamily: optional
  if (newViewProps.fontFamily.isDirty) {
    swiftPart.setFontFamily(newViewProps.fontFamily.value);
    newViewProps.fontFamily.isDirty = false;
  }
  // fontWeight: optional
  if (newViewProps.fontWeight.isDirty) {
    swiftPart.setFontWeight(newViewProps.fontWeight.value);
    newViewProps.fontWeight.isDirty = false;
  }
  // fontSize: optional
  if (newViewProps.fontSize.isDirty) {
    swiftPart.setFontSize(newViewProps.fontSize.value);
    newViewProps.fontSize.isDirty = false;
  }
  // onPageSelected: optional
  if (newViewProps.onPageSelected.isDirty) {
    swiftPart.setOnPageSelected(newViewProps.onPageSelected.value);
    newViewProps.onPageSelected.isDirty = false;
  }
  // onTabLongPress: optional
  if (newViewProps.onTabLongPress.isDirty) {
    swiftPart.setOnTabLongPress(newViewProps.onTabLongPress.value);
    newViewProps.onTabLongPress.isDirty = false;
  }
  // onTabBarMeasured: optional
  if (newViewProps.onTabBarMeasured.isDirty) {
    swiftPart.setOnTabBarMeasured(newViewProps.onTabBarMeasured.value);
    newViewProps.onTabBarMeasured.isDirty = false;
  }
  // onNativeLayout: optional
  if (newViewProps.onNativeLayout.isDirty) {
    swiftPart.setOnNativeLayout(newViewProps.onNativeLayout.value);
    newViewProps.onNativeLayout.isDirty = false;
  }

  swiftPart.afterUpdate();

  // 3. Update hybridRef if it changed
  if (newViewProps.hybridRef.isDirty) {
    const auto& maybeFunc = newViewProps.hybridRef.value;
    if (maybeFunc.has_value()) {
      maybeFunc.value()(_hybridView);
    }
    newViewProps.hybridRef.isDirty = false;
  }

  // 4. Continue in base class
  [super updateProps:props oldProps:oldProps];
}

@end
