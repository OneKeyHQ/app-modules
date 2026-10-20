#include <react/renderer/components/pagerview/RNCNativeScrollerShadowNode.h>

namespace facebook::react {

const char RNCNativeScrollerComponentName[] = "RNCNativeScroller";

Point RNCNativeScrollerShadowNode::getContentOriginOffset(bool includeTransform) const {
  const auto state = getStateData();
  const auto origin = state.contentOrigin;
  const auto transform = includeTransform ? getTransform() : Transform::Identity();
  const auto result = transform * Vector{origin.x, origin.y, 0.0f, 1.0f};
  return {result.x + state.ancestorOffset.x, result.y + state.ancestorOffset.y};
}

} // namespace facebook::react
