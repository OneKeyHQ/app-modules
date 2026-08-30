/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

#pragma once

#include <fbjni/fbjni.h>

#include <react/renderer/components/androidtextinput/AndroidTextInputShadowNode.h>
#include <react/renderer/components/androidtextinput/AndroidTextInputState.h>
#include <react/renderer/componentregistry/ComponentDescriptorProviderRegistry.h>
#include <react/renderer/core/ConcreteComponentDescriptor.h>

#include <yoga/YGEnums.h>
#include <yoga/YGValue.h>

#include <unordered_map>

namespace facebook::react {

class OneKeyTextInputComponentDescriptor final
    : public ConcreteComponentDescriptor<AndroidTextInputShadowNode> {
  using Base = ConcreteComponentDescriptor<AndroidTextInputShadowNode>;

 public:
  struct ConcreteShadowNode {
    inline static constexpr char NameValue[] = "OneKeyTextInput";

    static ComponentHandle Handle() {
      return ComponentHandle(NameValue);
    }

    static ComponentName Name() {
      return NameValue;
    }
  };

  explicit OneKeyTextInputComponentDescriptor(
      const ComponentDescriptorParameters &parameters)
      : Base(parameters),
        textLayoutManager_(
            std::make_shared<TextLayoutManager>(contextContainer_)) {}

  ComponentHandle getComponentHandle() const override {
    return ConcreteShadowNode::Handle();
  }

  ComponentName getComponentName() const override {
    return ConcreteShadowNode::Name();
  }

  State::Shared createInitialState(
      const Props::Shared &props,
      const ShadowNodeFamily::Shared &family) const override {
    const auto surfaceId = family->getSurfaceId();

    ThemePadding theme;
    if (surfaceIdToThemePaddingMap_.find(surfaceId) !=
        surfaceIdToThemePaddingMap_.end()) {
      theme = surfaceIdToThemePaddingMap_[surfaceId];
    } else {
      const jni::global_ref<jobject> &fabricUIManager =
          contextContainer_->at<jni::global_ref<jobject>>("FabricUIManager");

      auto env = jni::Environment::current();
      auto defaultTextInputPaddingArray = env->NewFloatArray(4);
      static auto getThemeData = jni::findClassStatic(UIManagerJavaDescriptor)
                                     ->getMethod<jboolean(jint, jfloatArray)>(
                                         "getThemeData");

      if (getThemeData(
              fabricUIManager, surfaceId, defaultTextInputPaddingArray) != 0u) {
        auto defaultTextInputPadding = env->GetFloatArrayElements(
            defaultTextInputPaddingArray, nullptr);
        theme.start = defaultTextInputPadding[0];
        theme.end = defaultTextInputPadding[1];
        theme.top = defaultTextInputPadding[2];
        theme.bottom = defaultTextInputPadding[3];
        surfaceIdToThemePaddingMap_.emplace(surfaceId, theme);
        env->ReleaseFloatArrayElements(
            defaultTextInputPaddingArray,
            defaultTextInputPadding,
            JNI_ABORT);
      }
      env->DeleteLocalRef(defaultTextInputPaddingArray);
    }

    return std::make_shared<AndroidTextInputShadowNode::ConcreteState>(
        std::make_shared<const AndroidTextInputState>(
            AndroidTextInputState({}, {}, {}, 0)),
        family);
  }

 protected:
  void adopt(ShadowNode &shadowNode) const override {
    auto &textInputShadowNode =
        static_cast<AndroidTextInputShadowNode &>(shadowNode);
    textInputShadowNode.setTextLayoutManager(textLayoutManager_);

    const auto surfaceId = textInputShadowNode.getSurfaceId();
    if (surfaceIdToThemePaddingMap_.find(surfaceId) !=
        surfaceIdToThemePaddingMap_.end()) {
      const auto &theme = surfaceIdToThemePaddingMap_[surfaceId];
      const auto &textInputProps = textInputShadowNode.getConcreteProps();
      auto &style = const_cast<yoga::Style &>(textInputProps.yogaStyle);
      auto changedPadding = false;

      if (!textInputProps.hasPadding && !textInputProps.hasPaddingStart &&
          !textInputProps.hasPaddingLeft &&
          !textInputProps.hasPaddingHorizontal) {
        changedPadding = true;
        style.setPadding(
            yoga::Edge::Start, yoga::StyleLength::points(theme.start));
      }
      if (!textInputProps.hasPadding && !textInputProps.hasPaddingEnd &&
          !textInputProps.hasPaddingRight &&
          !textInputProps.hasPaddingHorizontal) {
        changedPadding = true;
        style.setPadding(
            yoga::Edge::End, yoga::StyleLength::points(theme.end));
      }
      if (!textInputProps.hasPadding && !textInputProps.hasPaddingTop &&
          !textInputProps.hasPaddingVertical) {
        changedPadding = true;
        style.setPadding(
            yoga::Edge::Top, yoga::StyleLength::points(theme.top));
      }
      if (!textInputProps.hasPadding && !textInputProps.hasPaddingBottom &&
          !textInputProps.hasPaddingVertical) {
        changedPadding = true;
        style.setPadding(
            yoga::Edge::Bottom, yoga::StyleLength::points(theme.bottom));
      }

      if ((textInputProps.hasPadding || textInputProps.hasPaddingLeft ||
           textInputProps.hasPaddingHorizontal) &&
          !textInputProps.hasPaddingStart) {
        style.setPadding(yoga::Edge::Start, yoga::StyleLength::undefined());
      }
      if ((textInputProps.hasPadding || textInputProps.hasPaddingRight ||
           textInputProps.hasPaddingHorizontal) &&
          !textInputProps.hasPaddingEnd) {
        style.setPadding(yoga::Edge::End, yoga::StyleLength::undefined());
      }

      if (changedPadding) {
        textInputShadowNode.updateYogaProps();
      }
    }

    textInputShadowNode.dirtyLayout();
    Base::adopt(shadowNode);
  }

 private:
  struct ThemePadding {
    float start{};
    float end{};
    float top{};
    float bottom{};
  };

  inline static constexpr auto UIManagerJavaDescriptor =
      "com/facebook/react/fabric/FabricUIManager";

  const std::shared_ptr<TextLayoutManager> textLayoutManager_;
  mutable std::unordered_map<int, ThemePadding>
      surfaceIdToThemePaddingMap_;
};

} // namespace facebook::react
