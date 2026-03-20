"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectingExternalView = ConnectingExternalView;
var _react = require("react");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _w3mConnectingBody = require("../../partials/w3m-connecting-body");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function ConnectingExternalView() {
  const {
    data
  } = _appkitCoreReactNative.RouterController.state;
  const connector = data?.connector;
  const {
    maxWidth: width
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const [errorType, setErrorType] = (0, _react.useState)();
  const bodyMessage = (0, _w3mConnectingBody.getMessage)({
    walletName: data?.wallet?.name,
    errorType
  });
  const onRetryPress = () => {
    setErrorType(undefined);
    onConnect();
  };
  const storeConnectedWallet = (0, _react.useCallback)(async wallet => {
    if (wallet) {
      const recentWallets = await _appkitCoreReactNative.StorageUtil.addRecentWallet(wallet);
      if (recentWallets) {
        _appkitCoreReactNative.ConnectionController.setRecentWallets(recentWallets);
      }
    }
    if (connector) {
      const url = _appkitCoreReactNative.AssetUtil.getConnectorImage(connector);
      _appkitCoreReactNative.ConnectionController.setConnectedWalletImageUrl(url);
    }
  }, [connector]);
  const onConnect = (0, _react.useCallback)(async () => {
    try {
      if (connector) {
        await _appkitCoreReactNative.ConnectionController.connectExternal(connector);
        storeConnectedWallet(data?.wallet);
        _appkitCoreReactNative.ModalController.close();
        _appkitCoreReactNative.EventsController.sendEvent({
          type: 'track',
          event: 'CONNECT_SUCCESS',
          properties: {
            name: data.wallet?.name ?? 'Unknown',
            method: 'mobile',
            explorer_id: data.wallet?.id
          }
        });
      }
    } catch (error) {
      if (/(Wallet not found)/i.test(error.message)) {
        setErrorType('not_installed');
      } else if (/(rejected)/i.test(error.message)) {
        setErrorType('declined');
      } else {
        setErrorType('default');
      }
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'CONNECT_ERROR',
        properties: {
          message: error?.message ?? 'Unknown'
        }
      });
    }
  }, [connector, storeConnectedWallet, data?.wallet]);
  (0, _react.useEffect)(() => {
    onConnect();
  }, [onConnect]);
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    bounces: false,
    fadingEdgeLength: 20,
    contentContainerStyle: _styles.default.container
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    alignSelf: "center",
    padding: ['2xl', 'l', '0', 'l'],
    style: {
      width
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.LoadingThumbnail, {
    paused: !!errorType
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.WalletImage, {
    size: "xl",
    imageSrc: _appkitCoreReactNative.AssetUtil.getConnectorImage(connector),
    imageHeaders: _appkitCoreReactNative.ApiController._getApiHeaders()
  }), errorType && /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
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
  }), errorType !== 'not_installed' && /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    size: "sm",
    variant: "accent",
    iconLeft: "refresh",
    style: _styles.default.retryButton,
    iconStyle: _styles.default.retryIcon,
    onPress: onRetryPress
  }, "Try again")));
}
//# sourceMappingURL=index.js.map