#ifdef __cplusplus

#pragma once

#include <react/renderer/components/RNBottomTabs/RNBottomTabsViewShadowNode.h>
#include <react/renderer/core/ConcreteComponentDescriptor.h>
#include <react/renderer/componentregistry/ComponentDescriptorProviderRegistry.h>

namespace facebook::react {

class RNBottomTabsViewComponentDescriptor final
    : public ConcreteComponentDescriptor<RNBottomTabsViewShadowNode> {
 public:
    using ConcreteComponentDescriptor::ConcreteComponentDescriptor;

  void adopt(ShadowNode& shadowNode) const override {
    ConcreteComponentDescriptor::adopt(shadowNode);

#if !defined(ANDROID)
    auto &bottomTabsShadowNode =
        static_cast<RNBottomTabsViewShadowNode&>(shadowNode);

    std::weak_ptr<void> imageLoader =
        contextContainer_->at<std::shared_ptr<void>>("RCTImageLoader");
    bottomTabsShadowNode.setImageLoader(imageLoader);
#endif
  }
};

} // namespace facebook::react

#endif
