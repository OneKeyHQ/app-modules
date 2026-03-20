"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.AppKit = AppKit;
var _valtio = require("valtio");
var _react = require("react");
var _reactNative = require("react-native");
var _reactNativeModal = _interopRequireDefault(require("react-native-modal"));
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitSiweReactNative = require("@reown/appkit-siwe-react-native");
var _w3mRouter = require("../w3m-router");
var _w3mHeader = require("../../partials/w3m-header");
var _w3mSnackbar = require("../../partials/w3m-snackbar");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function AppKit() {
  const {
    open,
    loading
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ModalController.state);
  const {
    connectors,
    connectedConnector
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectorController.state);
  const {
    caipAddress,
    isConnected
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const {
    themeMode,
    themeVariables
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ThemeController.state);
  const {
    height
  } = (0, _reactNative.useWindowDimensions)();
  const {
    isLandscape
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const portraitHeight = height - 80;
  const landScapeHeight = height * 0.95 - (_reactNative.StatusBar.currentHeight ?? 0);
  const authProvider = connectors.find(c => c.type === 'AUTH')?.provider;
  const AuthView = authProvider?.AuthView;
  const SocialView = authProvider?.Webview;
  const showAuth = !connectedConnector || connectedConnector === 'AUTH';
  const onBackButtonPress = () => {
    if (_appkitCoreReactNative.RouterController.state.history.length > 1) {
      return _appkitCoreReactNative.RouterController.goBack();
    }
    return handleClose();
  };
  const prefetch = async () => {
    await _appkitCoreReactNative.ApiController.prefetch();
    _appkitCoreReactNative.EventsController.sendEvent({
      type: 'track',
      event: 'MODAL_LOADED'
    });
  };
  const handleClose = async () => {
    if (_appkitCoreReactNative.OptionsController.state.isSiweEnabled) {
      if (_appkitSiweReactNative.SIWEController.state.status !== 'success' && _appkitCoreReactNative.AccountController.state.isConnected) {
        await _appkitCoreReactNative.ConnectionController.disconnect();
      }
    }
    if (_appkitCoreReactNative.RouterController.state.view === 'OnRampLoading' && _appkitCoreReactNative.EventsController.state.data.event === 'BUY_SUBMITTED') {
      // Send event only if the onramp url was already created
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'BUY_CANCEL'
      });
    }
  };
  const onNewAddress = (0, _react.useCallback)(async address => {
    if (!isConnected || loading) {
      return;
    }
    const newAddress = _appkitCoreReactNative.CoreHelperUtil.getPlainAddress(address);
    _appkitCoreReactNative.TransactionsController.resetTransactions();
    if (_appkitCoreReactNative.OptionsController.state.isSiweEnabled) {
      const newNetworkId = _appkitCoreReactNative.CoreHelperUtil.getNetworkId(address);
      const {
        signOutOnAccountChange,
        signOutOnNetworkChange
      } = _appkitSiweReactNative.SIWEController.state._client?.options ?? {};
      const session = await _appkitSiweReactNative.SIWEController.getSession();
      if (session && newAddress && signOutOnAccountChange) {
        // If the address has changed and signOnAccountChange is enabled, sign out
        await _appkitSiweReactNative.SIWEController.signOut();
        onSiweNavigation();
      } else if (newNetworkId && session?.chainId.toString() !== newNetworkId && signOutOnNetworkChange) {
        // If the network has changed and signOnNetworkChange is enabled, sign out
        await _appkitSiweReactNative.SIWEController.signOut();
        onSiweNavigation();
      } else if (!session) {
        // If it's connected but there's no session, show sign view
        onSiweNavigation();
      }
    }
  }, [isConnected, loading]);
  const onSiweNavigation = () => {
    if (_appkitCoreReactNative.ModalController.state.open) {
      _appkitCoreReactNative.RouterController.push('ConnectingSiwe');
    } else {
      _appkitCoreReactNative.ModalController.open({
        view: 'ConnectingSiwe'
      });
    }
  };
  (0, _react.useEffect)(() => {
    prefetch();
  }, []);
  (0, _react.useEffect)(() => {
    onNewAddress(caipAddress);
  }, [caipAddress, onNewAddress]);
  return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.ThemeProvider, {
    themeMode: themeMode,
    themeVariables: themeVariables
  }, /*#__PURE__*/React.createElement(_reactNativeModal.default, {
    style: _styles.default.modal,
    coverScreen: false,
    isVisible: open,
    useNativeDriver: true,
    useNativeDriverForBackdrop: true,
    statusBarTranslucent: true,
    hideModalContentWhileAnimating: true,
    propagateSwipe: true,
    onModalHide: handleClose,
    onBackdropPress: _appkitCoreReactNative.ModalController.close,
    onBackButtonPress: onBackButtonPress,
    testID: "w3m-modal"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Card, {
    style: [_styles.default.card, {
      maxHeight: isLandscape ? landScapeHeight : portraitHeight
    }]
  }, /*#__PURE__*/React.createElement(_w3mHeader.Header, null), /*#__PURE__*/React.createElement(_w3mRouter.AppKitRouter, null), /*#__PURE__*/React.createElement(_w3mSnackbar.Snackbar, null))), !!showAuth && AuthView && /*#__PURE__*/React.createElement(AuthView, null), !!showAuth && SocialView && /*#__PURE__*/React.createElement(SocialView, null)));
}
//# sourceMappingURL=index.js.map