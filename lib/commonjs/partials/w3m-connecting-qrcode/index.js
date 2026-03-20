"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectingQrCode = ConnectingQrCode;
var _react = require("react");
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function ConnectingQrCode() {
  const {
    wcUri
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectionController.state);
  const showCopy = _appkitCoreReactNative.OptionsController.isClipboardAvailable();
  const {
    maxWidth: windowSize,
    isPortrait
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const qrSize = (windowSize - _appkitUiReactNative.Spacing.xl * 2) / (isPortrait ? 1 : 1.5);
  const onCopyAddress = () => {
    if (_appkitCoreReactNative.ConnectionController.state.wcUri) {
      _appkitCoreReactNative.OptionsController.copyToClipboard(_appkitCoreReactNative.ConnectionController.state.wcUri);
      _appkitCoreReactNative.SnackController.showSuccess('Link copied');
    }
  };
  const onConnect = async () => {
    await _appkitCoreReactNative.ConnectionController.state.wcPromise;
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'CONNECT_SUCCESS',
      properties: {
        method: 'qrcode',
        name: 'WalletConnect'
      }
    });
    const connectors = _appkitCoreReactNative.ConnectorController.state.connectors;
    const connector = connectors.find(c => c.type === 'WALLET_CONNECT');
    const url = _appkitCoreReactNative.AssetUtil.getConnectorImage(connector);
    _appkitCoreReactNative.ConnectionController.setConnectedWalletImageUrl(url);
  };
  (0, _react.useEffect)(() => {
    if (wcUri) {
      onConnect();
    }
  }, [wcUri]);
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    flexDirection: isPortrait ? 'column' : 'row',
    padding: ['xl', 'xl', '2xl', 'xl']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.QrCode, {
    size: qrSize,
    uri: wcUri,
    testID: "qr-code"
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    margin: "m"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-500"
  }, "Scan this QR code with your phone"), showCopy && /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    iconLeft: "copySmall",
    color: "fg-200",
    style: _styles.default.copyButton,
    onPress: onCopyAddress,
    testID: "copy-link"
  }, "Copy link")));
}
//# sourceMappingURL=index.js.map