"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.PreviewSendPill = PreviewSendPill;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
function PreviewSendPill({
  text,
  children
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    padding: ['xs', 's', 'xs', 's'],
    style: [styles.pill, {
      backgroundColor: Theme['gray-glass-002'],
      borderColor: Theme['gray-glass-010']
    }]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "large-500",
    color: "fg-100"
  }, text), children);
}
const styles = _reactNative.StyleSheet.create({
  pill: {
    borderRadius: _appkitUiReactNative.BorderRadius.full,
    borderWidth: _reactNative.StyleSheet.hairlineWidth
  }
});
//# sourceMappingURL=preview-send-pill.js.map