"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.WalletGuide = WalletGuide;
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _reactNative = require("react-native");
function WalletGuide({
  guide
}) {
  const onExplorerPress = () => {
    _reactNative.Linking.openURL('https://explorer.walletconnect.com');
  };
  const onGetStartedPress = () => {
    _appkitCoreReactNative.RouterController.push('Create');
  };
  return guide === 'explore' ? /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Separator, {
    text: "or",
    style: styles.socialSeparator
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    style: styles.text
  }, "Looking for a self-custody wallet?"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Chip, {
    label: "Visit our explorer",
    variant: "transparent",
    rightIcon: "externalLink",
    leftIcon: "walletConnectLightBrown",
    size: "sm",
    onPress: onExplorerPress
  })) : /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    margin: "m",
    flexDirection: "row"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400"
  }, "Haven't got a wallet?"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    onPress: onGetStartedPress,
    size: "sm"
  }, "Get started"));
}
const styles = _reactNative.StyleSheet.create({
  text: {
    marginBottom: _appkitUiReactNative.Spacing.xs
  },
  socialSeparator: {
    marginVertical: _appkitUiReactNative.Spacing.l
  }
});
//# sourceMappingURL=wallet-guide.js.map