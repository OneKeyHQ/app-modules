"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.UpgradeWalletButton = UpgradeWalletButton;
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
const AnimatedPressable = _reactNative.Animated.createAnimatedComponent(_reactNative.Pressable);
function UpgradeWalletButton({
  style,
  onPress
}) {
  const Theme = (0, _appkitUiReactNative.useTheme)();
  const {
    animatedValue,
    setStartValue,
    setEndValue
  } = (0, _appkitUiReactNative.useAnimatedValue)(Theme['accent-glass-010'], Theme['accent-glass-020']);
  return /*#__PURE__*/React.createElement(AnimatedPressable, {
    onPress: onPress,
    onPressIn: setEndValue,
    onPressOut: setStartValue,
    style: [styles.container, {
      backgroundColor: animatedValue
    }, style]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
    icon: "wallet",
    size: "lg",
    background: true,
    iconColor: "accent-100"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexGrow: 1,
    margin: ['0', 's', '0', 's']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    style: styles.upgradeText,
    color: "fg-100"
  }, "Upgrade your wallet"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, "Transition to a self-custodial wallet")), /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "chevronRight",
    size: "md",
    color: "fg-150",
    style: styles.chevron
  }));
}
const styles = _reactNative.StyleSheet.create({
  container: {
    height: 75,
    borderRadius: _appkitUiReactNative.BorderRadius.s,
    backgroundColor: 'red',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: _appkitUiReactNative.Spacing.s
  },
  textContainer: {
    marginHorizontal: _appkitUiReactNative.Spacing.m
  },
  upgradeText: {
    marginBottom: _appkitUiReactNative.Spacing['3xs']
  },
  chevron: {
    marginRight: _appkitUiReactNative.Spacing['2xs']
  }
});
//# sourceMappingURL=upgrade-wallet-button.js.map