"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.NetworkButton = NetworkButton;
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function NetworkButton({
  disabled,
  style
}) {
  const {
    isConnected
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    loading
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ModalController.state);
  const {
    themeMode,
    themeVariables
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ThemeController.state);
  const onNetworkPress = () => {
    _appkitCoreReactNative.ModalController.open({
      view: 'Networks'
    });
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'CLICK_NETWORKS'
    });
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.ThemeProvider, {
    themeMode: themeMode,
    themeVariables: themeVariables
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.NetworkButton, {
    imageSrc: _appkitCoreReactNative.AssetUtil.getNetworkImage(caipNetwork, networkImages),
    imageHeaders: _appkitCoreReactNative.ApiController._getApiHeaders(),
    disabled: disabled || loading,
    style: style,
    onPress: onNetworkPress,
    loading: loading,
    testID: "network-button"
  }, caipNetwork?.name ?? (isConnected ? 'Unknown Network' : 'Select Network')));
}
//# sourceMappingURL=index.js.map