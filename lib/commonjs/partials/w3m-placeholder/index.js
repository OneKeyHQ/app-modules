"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.Placeholder = Placeholder;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function Placeholder({
  icon,
  iconColor = 'fg-175',
  title,
  description,
  style,
  actionPress,
  actionTitle,
  actionIcon
}) {
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: [styles.container, style]
  }, icon && /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
    icon: icon,
    size: "xl",
    iconColor: iconColor,
    background: true,
    style: styles.icon
  }), title && /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500",
    style: styles.title
  }, title), description && /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200",
    center: true
  }, description), actionPress && /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    style: styles.button,
    iconLeft: actionIcon,
    size: "sm",
    variant: "accent",
    onPress: actionPress
  }, actionTitle ?? ''));
}
const styles = _reactNative.StyleSheet.create({
  container: {
    flex: 1,
    minHeight: 200
  },
  icon: {
    marginBottom: _appkitUiReactNative.Spacing.l
  },
  title: {
    marginBottom: _appkitUiReactNative.Spacing['2xs']
  },
  button: {
    marginTop: _appkitUiReactNative.Spacing.m
  }
});
//# sourceMappingURL=index.js.map