"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AccountButton = AccountButton;
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function AccountButton({
  balance,
  disabled,
  style,
  testID
}) {
  const {
    address,
    balance: balanceVal,
    balanceSymbol,
    profileImage,
    profileName
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const {
    themeMode,
    themeVariables
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ThemeController.state);
  const networkImage = _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages);
  const showBalance = balance === 'show';
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.ThemeProvider, {
    themeMode: themeMode,
    themeVariables: themeVariables
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.AccountButton, {
    onPress: () => _appkitCoreReactNative.ModalController.open(),
    address: address,
    profileName: profileName,
    networkSrc: networkImage,
    imageHeaders: _appkitCoreReactNative.ApiController._getApiHeaders(),
    avatarSrc: profileImage,
    disabled: disabled,
    style: style,
    balance: showBalance ? _appkitCoreReactNative.CoreHelperUtil.formatBalance(balanceVal, balanceSymbol) : '',
    testID: testID
  }));
}
//# sourceMappingURL=index.js.map