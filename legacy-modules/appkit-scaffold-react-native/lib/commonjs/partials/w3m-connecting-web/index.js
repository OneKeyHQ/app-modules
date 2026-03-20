"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectingWeb = ConnectingWeb;
var _valtio = require("valtio");
var _react = require("react");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _UiUtil = require("../../utils/UiUtil");
var _w3mConnectingBody = require("../w3m-connecting-body");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function ConnectingWeb({
  onCopyUri
}) {
  const {
    data
  } = _appkitCoreReactNative.RouterController.state;
  const {
    wcUri,
    wcError
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectionController.state);
  const showCopy = _appkitCoreReactNative.OptionsController.isClipboardAvailable();
  const bodyMessage = (0, _w3mConnectingBody.getMessage)({
    walletName: data?.wallet?.name,
    declined: wcError,
    isWeb: true
  });
  const onConnect = (0, _react.useCallback)(async () => {
    try {
      const {
        name,
        webapp_link
      } = data?.wallet ?? {};
      if (name && webapp_link && wcUri) {
        _appkitCoreReactNative.ConnectionController.setWcError(false);
        const {
          redirect,
          href
        } = _appkitCoreReactNative.CoreHelperUtil.formatUniversalUrl(webapp_link, wcUri);
        const wcLinking = {
          name,
          href
        };
        _appkitCoreReactNative.ConnectionController.setWcLinking(wcLinking);
        _appkitCoreReactNative.ConnectionController.setPressedWallet(data?.wallet);
        await _reactNative.Linking.openURL(redirect);
        await _appkitCoreReactNative.ConnectionController.state.wcPromise;
        _UiUtil.UiUtil.storeConnectedWallet(wcLinking, data?.wallet);
        _appkitCoreReactNative.EventsController.sendEvent({
          type: 'track',
          event: 'CONNECT_SUCCESS',
          properties: {
            method: 'web',
            name: data?.wallet?.name ?? 'Unknown',
            explorer_id: data?.wallet?.id
          }
        });
      }
    } catch {}
  }, [data?.wallet, wcUri]);
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    fadingEdgeLength: 20
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    padding: ['2xl', 'm', '3xl', 'm']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.LoadingThumbnail, {
    paused: wcError
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.WalletImage, {
    size: "xl",
    imageSrc: _appkitCoreReactNative.AssetUtil.getWalletImage(data?.wallet),
    imageHeaders: _appkitCoreReactNative.ApiController._getApiHeaders()
  }), wcError && /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
    icon: 'close',
    border: true,
    background: true,
    backgroundColor: "icon-box-bg-error-100",
    size: "sm",
    iconColor: "error-100",
    style: _styles.default.errorIcon
  })), /*#__PURE__*/React.createElement(_w3mConnectingBody.ConnectingBody, {
    title: bodyMessage.title,
    description: bodyMessage.description
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    size: "sm",
    variant: "accent",
    iconRight: "externalLink",
    style: _styles.default.openButton,
    onPress: onConnect
  }, "Open"), showCopy && /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    iconLeft: "copySmall",
    color: "fg-200",
    style: _styles.default.copyButton,
    onPress: () => onCopyUri(wcUri)
  }, "Copy link")));
}
//# sourceMappingURL=index.js.map