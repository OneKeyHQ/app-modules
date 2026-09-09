#pragma once

#include <algorithm>

#include <react/renderer/components/text/BaseParagraphComponentDescriptor.h>
#include <react/renderer/components/text/ParagraphShadowNode.h>

namespace facebook::react {

class OneKeyTextShadowNode final : public ParagraphShadowNode {
 public:
  using ParagraphShadowNode::ParagraphShadowNode;

  static constexpr ComponentName Name() {
    return "OneKeyText";
  }

  static ComponentHandle Handle() {
    return ComponentHandle(Name());
  }

  Size measureContent(
      const LayoutContext &layoutContext,
      const LayoutConstraints &layoutConstraints) const override {
    auto size = ParagraphShadowNode::measureContent(
        layoutContext, layoutConstraints);
    if (size.width <= 0 || layoutContext.pointScaleFactor <= 0) {
      return size;
    }

    const auto onePhysicalPixel = 1.0f / layoutContext.pointScaleFactor;
    size.width = std::min(
        size.width + onePhysicalPixel,
        layoutConstraints.maximumSize.width);
    return size;
  }
};

class OneKeyTextComponentDescriptor final
    : public BaseParagraphComponentDescriptor<OneKeyTextShadowNode> {
 public:
  using BaseParagraphComponentDescriptor::BaseParagraphComponentDescriptor;
};

} // namespace facebook::react
