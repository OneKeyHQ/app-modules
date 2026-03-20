"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.ConnectingView = ConnectingView;
var _valtio = require("valtio");
var _react = require("react");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitSiweReactNative = require("@reown/appkit-siwe-react-native");
var _w3mConnectingQrcode = require("../../partials/w3m-connecting-qrcode");
var _w3mConnectingMobile = require("../../partials/w3m-connecting-mobile");
var _w3mConnectingWeb = require("../../partials/w3m-connecting-web");
var _w3mConnectingHeader = require("../../partials/w3m-connecting-header");
var _UiUtil = require("../../utils/UiUtil");
function ConnectingView() {
  const {
    installed
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ApiController.state);
  const {
    data
  } = _appkitCoreReactNative.RouterController.state;
  const [lastRetry, setLastRetry] = (0, _react.useState)(Date.now());
  const isQr = !data?.wallet;
  const isInstalled = !!installed?.find(wallet => wallet.id === data?.wallet?.id);
  const [platform, setPlatform] = (0, _react.useState)();
  const [platforms, setPlatforms] = (0, _react.useState)([]);
  const onRetry = () => {
    if (_appkitCoreReactNative.CoreHelperUtil.isAllowedRetry(lastRetry)) {
      setLastRetry(Date.now());
      _appkitCoreReactNative.ConnectionController.clearUri();
      initializeConnection(true);
    } else {
      _appkitCoreReactNative.SnackController.showError('Please wait a second before retrying');
    }
  };
  const initializeConnection = async (retry = false) => {
    try {
      const {
        wcPairingExpiry
      } = _appkitCoreReactNative.ConnectionController.state;
      const {
        data: routeData
      } = _appkitCoreReactNative.RouterController.state;
      if (retry || _appkitCoreReactNative.CoreHelperUtil.isPairingExpired(wcPairingExpiry)) {
        _appkitCoreReactNative.ConnectionController.setWcError(false);
        _appkitCoreReactNative.ConnectionController.connectWalletConnect(routeData?.wallet?.link_mode ?? undefined);
        await _appkitCoreReactNative.ConnectionController.state.wcPromise;
        await _appkitCoreReactNative.ConnectorController.setConnectedConnector('WALLET_CONNECT');
        _appkitCoreReactNative.AccountController.setIsConnected(true);
        if (_appkitCoreReactNative.OptionsController.state.isSiweEnabled) {
          if (_appkitSiweReactNative.SIWEController.state.status === 'success') {
            _appkitCoreReactNative.ModalController.close();
          } else {
            _appkitCoreReactNative.RouterController.push('ConnectingSiwe');
          }
        } else {
          _appkitCoreReactNative.ModalController.close();
        }
      }
    } catch (error) {
      _appkitCoreReactNative.ConnectionController.setWcError(true);
      _appkitCoreReactNative.ConnectionController.clearUri();
      _appkitCoreReactNative.SnackController.showError('Declined');
      if (isQr && _appkitCoreReactNative.CoreHelperUtil.isAllowedRetry(lastRetry)) {
        setLastRetry(Date.now());
        initializeConnection(true);
      }
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'CONNECT_ERROR',
        properties: {
          message: error?.message ?? 'Unknown'
        }
      });
    }
  };
  const onCopyUri = uri => {
    if (_appkitCoreReactNative.OptionsController.isClipboardAvailable() && uri) {
      _appkitCoreReactNative.OptionsController.copyToClipboard(uri);
      _appkitCoreReactNative.SnackController.showSuccess('Link copied');
    }
  };
  const onSelectPlatform = tab => {
    _UiUtil.UiUtil.createViewTransition();
    setPlatform(tab);
  };
  const headerTemplate = () => {
    if (isQr) return null;
    if (platforms.length > 1) {
      return /*#__PURE__*/React.createElement(_w3mConnectingHeader.ConnectingHeader, {
        platforms: platforms,
        onSelectPlatform: onSelectPlatform
      });
    }
    return null;
  };
  const platformTemplate = () => {
    if (isQr) {
      return /*#__PURE__*/React.createElement(_w3mConnectingQrcode.ConnectingQrCode, null);
    }
    switch (platform) {
      case 'mobile':
        return /*#__PURE__*/React.createElement(_w3mConnectingMobile.ConnectingMobile, {
          onRetry: onRetry,
          onCopyUri: onCopyUri,
          isInstalled: isInstalled
        });
      case 'web':
        return /*#__PURE__*/React.createElement(_w3mConnectingWeb.ConnectingWeb, {
          onCopyUri: onCopyUri
        });
      default:
        return undefined;
    }
  };
  (0, _react.useEffect)(() => {
    const _platforms = [];
    if (data?.wallet?.mobile_link) {
      _platforms.push('mobile');
    }
    if (data?.wallet?.webapp_link && !isInstalled) {
      _platforms.push('web');
    }
    setPlatforms(_platforms);
    setPlatform(_platforms[0]);
  }, [data, isInstalled]);
  (0, _react.useEffect)(() => {
    initializeConnection();
    let _interval;

    // Check if the pairing expired every 10 seconds. If expired, it will create a new uri.
    if (isQr) {
      _interval = setInterval(initializeConnection, _appkitCoreReactNative.ConstantsUtil.TEN_SEC_MS);
    }
    return () => clearInterval(_interval);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isQr]);
  return /*#__PURE__*/React.createElement(React.Fragment, null, headerTemplate(), platformTemplate());
}
//# sourceMappingURL=index.js.map