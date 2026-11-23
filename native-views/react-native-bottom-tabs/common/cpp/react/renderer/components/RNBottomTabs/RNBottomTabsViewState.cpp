#include <react/renderer/components/RNBottomTabs/RNBottomTabsViewState.h>

namespace facebook::react {

void RNBottomTabsViewState::setImageLoader(
    std::weak_ptr<void> imageLoader) {
  imageLoader_ = imageLoader;
}

std::weak_ptr<void> RNBottomTabsViewState::getImageLoader()
    const noexcept {
  return imageLoader_;
}

} // namespace facebook::react
