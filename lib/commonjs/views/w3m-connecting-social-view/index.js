"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectingSocialView = ConnectingSocialView;
var _valtio = require("valtio");
var _react = require("react");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function ConnectingSocialView() {
  const {
    maxWidth: width
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    processingAuth
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.WebviewController.state);
  const {
    selectedSocialProvider
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectionController.state);
  const authConnector = _appkitCoreReactNative.ConnectorController.getAuthConnector();
  const [error, setError] = (0, _react.useState)(false);
  const provider = authConnector?.provider;
  const onConnect = (0, _react.useCallback)(async () => {
    try {
      if (!_appkitCoreReactNative.WebviewController.state.connecting && provider && _appkitCoreReactNative.ConnectionController.state.selectedSocialProvider) {
        const {
          uri
        } = await provider.getSocialRedirectUri({
          provider: _appkitCoreReactNative.ConnectionController.state.selectedSocialProvider
        });
        _appkitCoreReactNative.WebviewController.setWebviewUrl(uri);
        _appkitCoreReactNative.WebviewController.setWebviewVisible(true);
        _appkitCoreReactNative.WebviewController.setConnecting(true);
        _appkitCoreReactNative.WebviewController.setConnectingProvider(_appkitCoreReactNative.ConnectionController.state.selectedSocialProvider);
      }
    } catch (e) {
      _appkitCoreReactNative.WebviewController.setWebviewVisible(false);
      _appkitCoreReactNative.WebviewController.setWebviewUrl(undefined);
      _appkitCoreReactNative.WebviewController.setConnecting(false);
      _appkitCoreReactNative.WebviewController.setConnectingProvider(undefined);
      _appkitCoreReactNative.SnackController.showError('Something went wrong');
      setError(true);
    }
  }, [provider]);
  const socialMessageHandler = (0, _react.useCallback)(async url => {
    try {
      if (url.includes('/sdk/oauth') && _appkitCoreReactNative.ConnectionController.state.selectedSocialProvider && authConnector && !_appkitCoreReactNative.WebviewController.state.processingAuth) {
        _appkitCoreReactNative.WebviewController.setProcessingAuth(true);
        _appkitCoreReactNative.WebviewController.setWebviewVisible(false);
        const parsedUrl = new URL(url);
        _appkitCoreReactNative.EventsController.sendEvent({
          type: 'track',
          event: 'SOCIAL_LOGIN_REQUEST_USER_DATA',
          properties: {
            provider: _appkitCoreReactNative.ConnectionController.state.selectedSocialProvider
          }
        });
        await provider?.connectSocial(parsedUrl.search);
        await _appkitCoreReactNative.ConnectionController.connectExternal(authConnector);
        _appkitCoreReactNative.ConnectionController.setConnectedSocialProvider(_appkitCoreReactNative.ConnectionController.state.selectedSocialProvider);
        _appkitCoreReactNative.WebviewController.setConnecting(false);
        _appkitCoreReactNative.EventsController.sendEvent({
          type: 'track',
          event: 'SOCIAL_LOGIN_SUCCESS',
          properties: {
            provider: _appkitCoreReactNative.ConnectionController.state.selectedSocialProvider
          }
        });
        _appkitCoreReactNative.ModalController.close();
        _appkitCoreReactNative.WebviewController.reset();
      }
    } catch (e) {
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'SOCIAL_LOGIN_ERROR',
        properties: {
          provider: _appkitCoreReactNative.ConnectionController.state.selectedSocialProvider
        }
      });
      _appkitCoreReactNative.WebviewController.reset();
      _appkitCoreReactNative.RouterController.goBack();
      _appkitCoreReactNative.SnackController.showError('Something went wrong');
    }
  }, [authConnector, provider]);
  (0, _react.useEffect)(() => {
    onConnect();
  }, [onConnect]);
  (0, _react.useEffect)(() => {
    if (!provider) return;
    const unsubscribe = provider?.getEventEmitter().addListener('social', socialMessageHandler);
    return () => {
      unsubscribe.removeListener('social', socialMessageHandler);
    };
  }, [socialMessageHandler, provider]);
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    alignSelf: "center",
    padding: ['2xl', 'l', '3xl', 'l'],
    style: {
      width
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.LoadingThumbnail, {
    paused: !!error
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Logo, {
    logo: selectedSocialProvider ?? 'more',
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
  }, processingAuth ? 'Loading user data' : `Continue with ${_appkitCommonReactNative.StringUtil.capitalize(selectedSocialProvider)}`), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200"
  }, processingAuth ? 'Please wait a moment while we load your data' : 'Connect in the provider window'));
}
//# sourceMappingURL=index.js.map