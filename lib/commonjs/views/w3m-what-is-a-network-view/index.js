"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.WhatIsANetworkView = WhatIsANetworkView;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function WhatIsANetworkView() {
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const onLearnMorePress = () => {
    _reactNative.Linking.openURL('https://ethereum.org/en/developers/docs/networks/');
  };
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    style: {
      paddingHorizontal: padding
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    padding: ['l', '4xl', '3xl', '4xl']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    padding: ['0', '0', 'xs', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "network"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "layers",
    style: _styles.default.visual
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "system"
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500",
    style: _styles.default.text
  }, "The system\u2019s nuts and bolts"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-500",
    color: "fg-200",
    center: true
  }, "A network is what brings the blockchain to life, as this technical infrastructure allows apps to access the ledger and smart contract services."), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    padding: ['3xl', '0', 'xs', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "noun"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "defiAlt",
    style: _styles.default.visual
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Visual, {
    name: "dao"
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500",
    style: _styles.default.text
  }, "Designed for different uses"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-500",
    color: "fg-200",
    center: true
  }, "Each network is designed differently, and may therefore suit certain apps and experiences."), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    size: "sm",
    iconRight: "externalLink",
    style: _styles.default.learnButton,
    onPress: onLearnMorePress
  }, "Learn more")));
}
//# sourceMappingURL=index.js.map