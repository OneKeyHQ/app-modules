"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AuthButtons = AuthButtons;
var _reactNative = require("react-native");
var _upgradeWalletButton = require("./upgrade-wallet-button");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function AuthButtons({
  onUpgradePress,
  onPress,
  socialProvider,
  text,
  style
}) {
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_upgradeWalletButton.UpgradeWalletButton, {
    onPress: onUpgradePress,
    style: styles.upgradeButton
  }), socialProvider ? /*#__PURE__*/React.createElement(_appkitUiReactNative.ListSocial, {
    logo: socialProvider,
    logoHeight: 32,
    logoWidth: 32,
    style: [styles.socialContainer, style]
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-100",
    numberOfLines: 1,
    ellipsizeMode: "tail",
    style: styles.socialText
  }, text)) : /*#__PURE__*/React.createElement(_appkitUiReactNative.ListItem, {
    icon: "mail",
    onPress: onPress,
    chevron: true,
    testID: "button-email",
    iconColor: "fg-100",
    style: style
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    color: "fg-100",
    numberOfLines: 1,
    ellipsizeMode: "tail"
  }, text)));
}
const styles = _reactNative.StyleSheet.create({
  actionButton: {
    marginBottom: _appkitUiReactNative.Spacing.xs
  },
  upgradeButton: {
    marginBottom: _appkitUiReactNative.Spacing.s
  },
  socialContainer: {
    justifyContent: 'flex-start',
    width: '100%'
  },
  socialText: {
    flex: 1,
    marginLeft: _appkitUiReactNative.Spacing.s
  }
});
//# sourceMappingURL=auth-buttons.js.map