"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectingMobile = ConnectingMobile;
var _valtio = require("valtio");
var _react = require("react");
var _reactNative = require("react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _UiUtil = require("../../utils/UiUtil");
var _StoreLink = require("./components/StoreLink");
var _w3mConnectingBody = require("../w3m-connecting-body");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function ConnectingMobile({
  onRetry,
  onCopyUri,
  isInstalled
}) {
  const {
    data
  } = _appkitCoreReactNative.RouterController.state;
  const {
    maxWidth: width
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    wcUri,
    wcError
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectionController.state);
  const [errorType, setErrorType] = (0, _react.useState)();
  const showCopy = _appkitCoreReactNative.OptionsController.isClipboardAvailable() && errorType !== 'not_installed' && !_appkitCoreReactNative.CoreHelperUtil.isLinkModeURL(wcUri);
  const showRetry = errorType !== 'not_installed';
  const bodyMessage = (0, _w3mConnectingBody.getMessage)({
    walletName: data?.wallet?.name,
    errorType,
    declined: wcError
  });
  const storeUrl = _reactNative.Platform.select({
    ios: data?.wallet?.app_store,
    android: data?.wallet?.play_store
  });
  const onRetryPress = () => {
    setErrorType(undefined);
    _appkitCoreReactNative.ConnectionController.setWcError(false);
    onRetry?.();
  };
  const onStorePress = () => {
    if (storeUrl) {
      _appkitCoreReactNative.CoreHelperUtil.openLink(storeUrl);
    }
  };
  const onConnect = (0, _react.useCallback)(async () => {
    try {
      const {
        name,
        mobile_link
      } = data?.wallet ?? {};
      if (name && mobile_link && wcUri) {
        const {
          redirect,
          href
        } = _appkitCoreReactNative.CoreHelperUtil.formatNativeUrl(mobile_link, wcUri);
        const wcLinking = {
          name,
          href
        };
        _appkitCoreReactNative.ConnectionController.setWcLinking(wcLinking);
        _appkitCoreReactNative.ConnectionController.setPressedWallet(data?.wallet);
        await _appkitCoreReactNative.CoreHelperUtil.openLink(redirect);
        await _appkitCoreReactNative.ConnectionController.state.wcPromise;
        _UiUtil.UiUtil.storeConnectedWallet(wcLinking, data?.wallet);
        _appkitCoreReactNative.EventsController.sendEvent({
          type: 'track',
          event: 'CONNECT_SUCCESS',
          properties: {
            method: 'mobile',
            name: data?.wallet?.name ?? 'Unknown',
            explorer_id: data?.wallet?.id
          }
        });
      }
    } catch (error) {
      if (error.message.includes(_appkitCoreReactNative.ConstantsUtil.LINKING_ERROR)) {
        setErrorType('not_installed');
      } else {
        setErrorType('default');
      }
    }
  }, [wcUri, data]);
  (0, _react.useEffect)(() => {
    if (wcUri) {
      onConnect();
    }
  }, [wcUri, onConnect]);
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
    paused: !!errorType || wcError
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.WalletImage, {
    size: "xl",
    imageSrc: _appkitCoreReactNative.AssetUtil.getWalletImage(_appkitCoreReactNative.RouterController.state.data?.wallet),
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
  }), showRetry && /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    size: "sm",
    variant: "accent",
    iconLeft: "refresh",
    style: _styles.default.retryButton,
    iconStyle: _styles.default.retryIcon,
    onPress: onRetryPress
  }, "Try again")), showCopy && /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    iconLeft: "copySmall",
    color: "fg-200",
    style: _styles.default.copyButton,
    onPress: () => onCopyUri(wcUri)
  }, "Copy link"), /*#__PURE__*/React.createElement(_StoreLink.StoreLink, {
    visible: !isInstalled && !!storeUrl,
    walletName: data?.wallet?.name,
    onPress: onStorePress
  }));
}
//# sourceMappingURL=index.js.map