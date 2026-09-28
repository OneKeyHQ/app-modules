#pragma once

#include <jsi/jsi.h>
#include <react/renderer/components/pagerview/EventEmitters.h>
#include <react/renderer/components/pagerview/Props.h>
#include <react/renderer/components/pagerview/RNCNativeScrollerState.h>
#include <react/renderer/components/view/ConcreteViewShadowNode.h>

namespace facebook::react {

JSI_EXPORT extern const char RNCNativeScrollerComponentName[];

class JSI_EXPORT RNCNativeScrollerShadowNode final
    : public ConcreteViewShadowNode<RNCNativeScrollerComponentName,
                                    RNCNativeScrollerProps,
                                    RNCNativeScrollerEventEmitter,
                                    RNCNativeScrollerState> {
 public:
  using ConcreteViewShadowNode::ConcreteViewShadowNode;
  Point getContentOriginOffset(bool includeTransform) const override;
};

} // namespace facebook::react
