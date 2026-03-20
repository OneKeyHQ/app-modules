"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = void 0;
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
function PaymentButton({
  disabled,
  loading,
  title,
  subtitle,
  paymentLogo,
  providerLogo,
  onPress,
  testID
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const backgroundColor = Theme['gray-glass-005'];
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.Pressable, {
    disabled: disabled || loading,
    onPress: onPress,
    style: styles.pressable,
    testID: testID
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    style: [styles.container, {
      backgroundColor
    }],
    alignItems: "center",
    justifyContent: "space-between",
    flexDirection: "row"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: [styles.iconContainer, {
      backgroundColor: Theme['bg-300']
    }]
  }, paymentLogo ? /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
    source: paymentLogo,
    style: styles.paymentLogo,
    resizeMethod: "resize",
    resizeMode: "contain"
  }) : /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "card",
    size: "lg"
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexGrow: 1,
    flexDirection: "column",
    alignItems: "flex-start",
    margin: ['0', '0', '0', 's']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-100"
  }, title), subtitle && /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    margin: ['4xs', '0', '0', '0']
  }, providerLogo && /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, "via"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
    source: providerLogo,
    style: styles.providerLogo,
    resizeMethod: "resize",
    resizeMode: "contain"
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, subtitle))), loading ? /*#__PURE__*/React.createElement(_appkitUiReactNative.LoadingSpinner, {
    size: "md",
    color: "fg-200",
    style: styles.rightIcon
  }) : disabled ? /*#__PURE__*/React.createElement(_reactNative.View, null) : /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "chevronRight",
    size: "md",
    color: "fg-200",
    style: styles.rightIcon
  })));
}
const styles = _reactNative.StyleSheet.create({
  pressable: {
    borderRadius: _appkitUiReactNative.BorderRadius.xs
  },
  container: {
    padding: _appkitUiReactNative.Spacing.s,
    borderRadius: _appkitUiReactNative.BorderRadius.xs
  },
  iconContainer: {
    height: 40,
    width: 40,
    borderRadius: _appkitUiReactNative.BorderRadius['3xs']
  },
  paymentLogo: {
    height: 24,
    width: 24
  },
  providerLogo: {
    height: 16,
    width: 16,
    marginHorizontal: _appkitUiReactNative.Spacing['4xs'],
    borderRadius: _appkitUiReactNative.BorderRadius['5xs']
  },
  rightIcon: {
    marginRight: _appkitUiReactNative.Spacing.xs
  }
});
var _default = exports.default = PaymentButton;
//# sourceMappingURL=PaymentButton.js.map