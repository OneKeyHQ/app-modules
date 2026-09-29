#pragma once

#include <react/renderer/graphics/Point.h>
#ifdef RN_SERIALIZABLE_STATE
#include <folly/dynamic.h>
#endif

namespace facebook::react {

// Local content displacement and ancestor translation have different transform spaces.
class RNCNativeScrollerState final {
 public:
  Point contentOrigin{};
  Point ancestorOffset{};
  RNCNativeScrollerState() = default;
  explicit RNCNativeScrollerState(Point origin) : contentOrigin(origin) {}

#ifdef RN_SERIALIZABLE_STATE
  RNCNativeScrollerState(const RNCNativeScrollerState &, folly::dynamic data)
      : contentOrigin({static_cast<Float>(data["contentOriginX"].getDouble()),
                       static_cast<Float>(data["contentOriginY"].getDouble())}),
        ancestorOffset({static_cast<Float>(data.getDefault("ancestorOffsetX", 0.0).getDouble()),
                        static_cast<Float>(data.getDefault("ancestorOffsetY", 0.0).getDouble())}) {}

  folly::dynamic getDynamic() const {
    return folly::dynamic::object("contentOriginX", contentOrigin.x)
        ("contentOriginY", contentOrigin.y)
        ("ancestorOffsetX", ancestorOffset.x)("ancestorOffsetY", ancestorOffset.y);
  }
#endif
};

} // namespace facebook::react
