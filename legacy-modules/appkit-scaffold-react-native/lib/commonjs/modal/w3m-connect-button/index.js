"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectButton = ConnectButton;
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
function ConnectButton({
  label,
  loadingLabel,
  size = 'md',
  style,
  disabled,
  testID
}) {
  const {
    open,
    loading
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ModalController.state);
  const {
    themeMode,
    themeVariables
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ThemeController.state);
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.ThemeProvider, {
    themeMode: themeMode,
    themeVariables: themeVariables
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.ConnectButton, {
    onPress: () => _appkitCoreReactNative.ModalController.open(),
    size: size,
    loading: loading || open,
    style: style,
    testID: testID,
    disabled: disabled
  }, loading || open ? loadingLabel : label));
}
//# sourceMappingURL=index.js.map