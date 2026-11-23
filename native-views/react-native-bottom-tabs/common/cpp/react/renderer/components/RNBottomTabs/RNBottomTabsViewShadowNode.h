#ifdef __cplusplus

#pragma once

#include <react/renderer/components/RNBottomTabs/EventEmitters.h>
#include <react/renderer/components/RNBottomTabs/Props.h>
#include <react/renderer/components/RNBottomTabs/RNBottomTabsViewState.h>
#include <react/renderer/components/view/ConcreteViewShadowNode.h>
#include <jsi/jsi.h>

namespace facebook::react {

JSI_EXPORT extern const char RNBottomTabsViewComponentName[];

/*
 * `ShadowNode` for <RNBottomTabsView> component.
 */
class JSI_EXPORT RNBottomTabsViewShadowNode final: public ConcreteViewShadowNode<
RNBottomTabsViewComponentName,
RNBottomTabsViewProps,
RNBottomTabsViewEventEmitter,
RNBottomTabsViewState> {

public:
  using ConcreteViewShadowNode::ConcreteViewShadowNode;
  using StateData = ConcreteViewShadowNode::ConcreteStateData;

  void setImageLoader(std::weak_ptr<void> imageLoader);

  StateData &getStateDataMutable();
};

}

#endif
