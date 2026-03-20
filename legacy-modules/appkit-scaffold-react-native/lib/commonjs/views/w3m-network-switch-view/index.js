"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.NetworkSwitchView = NetworkSwitchView;
var _valtio = require("valtio");
var _react = require("react");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
/* eslint-disable valtio/state-snapshot-rule */

const imageHeaders = _appkitCoreReactNative.ApiController._getApiHeaders();
function NetworkSwitchView() {
  const {
    data
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.RouterController.state);
  const {
    recentWallets
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.ConnectionController.state);
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    networkImages
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AssetController.state);
  const isAuthConnected = _appkitCoreReactNative.ConnectorController.state.connectedConnector === 'AUTH';
  const [error, setError] = (0, _react.useState)(false);
  const [showRetry, setShowRetry] = (0, _react.useState)(false);
  const network = data?.network;
  const wallet = recentWallets?.[0];
  const onSwitchNetwork = async () => {
    try {
      setError(false);
      await _appkitCoreReactNative.NetworkController.switchActiveNetwork(network);
      _appkitCoreReactNative.EventsController.sendEvent({
        type: 'track',
        event: 'SWITCH_NETWORK',
        properties: {
          network: network.id
        }
      });
    } catch {
      setError(true);
      setShowRetry(true);
    }
  };
  (0, _react.useEffect)(() => {
    onSwitchNetwork();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
  (0, _react.useEffect)(() => {
    // Go back if network is already switched
    if (caipNetwork?.id === network?.id) {
      _appkitCoreReactNative.RouterUtil.navigateAfterNetworkSwitch();
    }
  }, [caipNetwork?.id, network?.id]);
  const retryTemplate = () => {
    if (!showRetry) return null;
    return /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
      size: "sm",
      variant: "accent",
      iconLeft: "refresh",
      style: _styles.default.retryButton,
      iconStyle: _styles.default.retryIcon,
      onPress: onSwitchNetwork
    }, "Try again");
  };
  const textTemplate = () => {
    const walletName = wallet?.name ?? 'wallet';
    if (error) {
      return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
        variant: "paragraph-500",
        style: _styles.default.text
      }, "Switch declined"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
        center: true,
        variant: "small-400",
        color: "fg-200",
        style: _styles.default.descriptionText
      }, "Switch can be declined if chain is not supported by a wallet or previous request is still active"));
    }
    if (isAuthConnected) {
      return /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
        variant: "paragraph-500",
        style: _styles.default.text
      }, "Switching to ", network.name, " network");
    }
    return /*#__PURE__*/React.createElement(React.Fragment, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
      variant: "paragraph-500",
      style: _styles.default.text
    }, `Approve in ${walletName}`), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
      center: true,
      variant: "small-400",
      color: "fg-200",
      style: _styles.default.descriptionText
    }, "Accept switch request in your wallet"));
  };
  return /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    padding: ['2xl', 's', '4xl', 's']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.LoadingHexagon, {
    paused: error
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.NetworkImage, {
    imageSrc: _appkitCoreReactNative.AssetUtil.getNetworkImage(network, networkImages),
    imageHeaders: imageHeaders,
    size: "lg"
  }), error && /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
    icon: "close",
    border: true,
    background: true,
    backgroundColor: "icon-box-bg-error-100",
    size: "sm",
    iconColor: "error-100",
    style: _styles.default.errorIcon
  })), textTemplate(), retryTemplate());
}
//# sourceMappingURL=index.js.map