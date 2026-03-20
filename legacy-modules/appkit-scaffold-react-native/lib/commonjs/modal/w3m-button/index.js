"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AppKitButton = AppKitButton;
var _valtio = require("valtio");
var _w3mAccountButton = require("../w3m-account-button");
var _w3mConnectButton = require("../w3m-connect-button");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
function AppKitButton({
  balance,
  disabled,
  size,
  label = 'Connect',
  loadingLabel = 'Connecting',
  accountStyle,
  connectStyle
}) {
  const {
    isConnected
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const {
    loading
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ModalController.state);
  return !loading && isConnected ? /*#__PURE__*/React.createElement(_w3mAccountButton.AccountButton, {
    style: accountStyle,
    balance: balance,
    disabled: disabled,
    testID: "account-button"
  }) : /*#__PURE__*/React.createElement(_w3mConnectButton.ConnectButton, {
    style: connectStyle,
    size: size,
    label: label,
    loadingLabel: loadingLabel,
    disabled: disabled,
    testID: "connect-button"
  });
}
//# sourceMappingURL=index.js.map