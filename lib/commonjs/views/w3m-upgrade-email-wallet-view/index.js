"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.UpgradeEmailWalletView = UpgradeEmailWalletView;
var _valtio = require("valtio");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
function UpgradeEmailWalletView() {
  const {
    connectors
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectorController.state);
  const authProvider = connectors.find(c => c.type === 'AUTH')?.provider;
  const onLinkPress = () => {
    const link = authProvider.getSecureSiteDashboardURL();
    _reactNative.Linking.canOpenURL(link).then(supported => {
      if (supported) _reactNative.Linking.openURL(link);
    });
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['l', 'l', '3xl', 'l'],
    alignItems: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400"
  }, "Follow the instructions on"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Chip, {
    label: "secure.reown.com",
    rightIcon: "externalLink",
    imageSrc: authProvider.getSecureSiteIconURL(),
    style: styles.chip,
    onPress: onLinkPress
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200"
  }, "You will have to reconnect for security reasons"));
}
const styles = _reactNative.StyleSheet.create({
  chip: {
    marginVertical: _appkitUiReactNative.Spacing.m
  }
});
//# sourceMappingURL=index.js.map