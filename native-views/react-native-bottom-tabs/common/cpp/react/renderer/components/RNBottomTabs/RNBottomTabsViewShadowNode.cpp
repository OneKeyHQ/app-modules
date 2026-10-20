#include <react/renderer/components/RNBottomTabs/RNBottomTabsViewShadowNode.h>

namespace facebook::react {

extern const char RNBottomTabsViewComponentName[] = "RNBottomTabsView";

void RNBottomTabsViewShadowNode::setImageLoader(
    std::weak_ptr<void> imageLoader) {
  getStateDataMutable().setImageLoader(imageLoader);
}

RNBottomTabsViewShadowNode::StateData &
RNBottomTabsViewShadowNode::getStateDataMutable() {
  ensureUnsealed();
  return const_cast<RNBottomTabsViewShadowNode::StateData &>(getStateData());
}

} // namespace facebook::react
