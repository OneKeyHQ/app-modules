"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectingFarcasterView = ConnectingFarcasterView;
var _reactNative = require("react-native");
var _react = require("react");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function ConnectingFarcasterView() {
  const {
    maxWidth: width
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const authConnector = _appkitCoreReactNative.ConnectorController.getAuthConnector();
  const [error, setError] = (0, _react.useState)(false);
  const [processing, setProcessing] = (0, _react.useState)(false);
  const [url, setUrl] = (0, _react.useState)();
  const showCopy = _appkitCoreReactNative.OptionsController.isClipboardAvailable();
  const provider = authConnector?.provider;
  const onConnect = (0, _react.useCallback)(async () => {
    try {
      if (provider && authConnector) {
        setError(false);
        const {
          url: farcasterUrl
        } = await provider.getFarcasterUri();
        setUrl(farcasterUrl);
        _reactNative.Linking.openURL(farcasterUrl);
        await provider.connectFarcaster();
        _appkitCoreReactNative.EventsController.sendEvent({
          type: 'track',
          event: 'SOCIAL_LOGIN_REQUEST_USER_DATA',
          properties: {
            provider: 'farcaster'
          }
        });
        setProcessing(true);
        await _appkitCoreReactNative.ConnectionController.connectExternal(authConnector);
        _appkitCoreReactNative.ConnectionController.setConnectedSocialProvider('farcaster');
        _appkitCoreReactNative.EventsController.sendEvent({
          type: 'track',
          event: 'SOCIAL_LOGIN_SUCCESS',
          properties: {
            provider: 'farcaster'
          }
        });
        setProcessing(false);
        _appkitCoreReactNative.ModalController.close();
      }
    } catch (e) {
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'SOCIAL_LOGIN_ERROR',
        properties: {
          provider: 'farcaster'
        }
      });
      // TODO: remove this once Farcaster session refresh is implemented
      // @ts-expect-error
      provider?.webviewRef?.current?.reload();
      _appkitCoreReactNative.SnackController.showError('Something went wrong');
      setError(true);
      setProcessing(false);
    }
  }, [provider, authConnector]);
  const onCopyUrl = () => {
    if (url) {
      _appkitCoreReactNative.OptionsController.copyToClipboard(url);
      _appkitCoreReactNative.SnackController.showSuccess('Link copied');
    }
  };
  (0, _react.useEffect)(() => {
    return () => {
      // TODO: remove this once Farcaster session refresh is implemented
      if (!_appkitCoreReactNative.ModalController.state.open) {
        // @ts-expect-error
        provider.webviewRef?.current?.reload();
      }
    };
    // @ts-expect-error
  }, [provider.webviewRef]);
  (0, _react.useEffect)(() => {
    onConnect();
  }, [onConnect]);
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    alignSelf: "center",
    padding: ['2xl', 'l', '3xl', 'l'],
    style: {
      width
    }
  }, /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.LoadingThumbnail, {
    paused: !!error
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Logo, {
    logo: "farcasterSquare",
    height: 72,
    width: 72
  }), error && /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
    icon: 'close',
    border: true,
    background: true,
    backgroundColor: "icon-box-bg-error-100",
    size: "sm",
    iconColor: "error-100",
    style: _styles.default.errorIcon
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    style: _styles.default.continueText,
    variant: "paragraph-500"
  }, processing ? 'Loading user data' : 'Continue in Farcaster'), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200"
  }, processing ? 'Please wait a moment while we load your data' : 'Connect in the Farcaster app'), showCopy && /*#__PURE__*/React.createElement(_appkitUiReactNative.Link, {
    iconLeft: "copySmall",
    color: "fg-200",
    style: _styles.default.copyButton,
    onPress: onCopyUrl,
    testID: "copy-link"
  }, "Copy link")));
}
//# sourceMappingURL=index.js.map