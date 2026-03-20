"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.WalletSendView = WalletSendView;
var _react = require("react");
var _reactNative = require("react-native");
var _valtio = require("valtio");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _w3mSendInputToken = require("../../partials/w3m-send-input-token");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _useKeyboard = require("../../hooks/useKeyboard");
var _w3mSendInputAddress = require("../../partials/w3m-send-input-address");
var _styles = _interopRequireDefault(require("./styles"));
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function WalletSendView() {
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    keyboardShown,
    keyboardHeight
  } = (0, _useKeyboard.useKeyboard)();
  const {
    token,
    sendTokenAmount,
    receiverAddress,
    receiverProfileName,
    loading,
    gasPrice
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.SendController.state);
  const {
    tokenBalance
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.AccountController.state);
  const paddingBottom = _reactNative.Platform.select({
    android: keyboardShown ? keyboardHeight + _appkitUiReactNative.Spacing['2xl'] : _appkitUiReactNative.Spacing['2xl'],
    default: _appkitUiReactNative.Spacing['2xl']
  });
  const fetchNetworkPrice = (0, _react.useCallback)(async () => {
    await _appkitCoreReactNative.SwapController.getNetworkTokenPrice();
    const gas = await _appkitCoreReactNative.SwapController.getInitialGasPrice();
    if (gas?.gasPrice && gas?.gasPriceInUSD) {
      _appkitCoreReactNative.SendController.setGasPrice(gas.gasPrice);
      _appkitCoreReactNative.SendController.setGasPriceInUsd(gas.gasPriceInUSD);
    }
  }, []);
  const onSendPress = () => {
    _appkitCoreReactNative.RouterController.push('WalletSendPreview');
  };
  const getActionText = () => {
    if (!_appkitCoreReactNative.SendController.state.token) {
      return 'Select token';
    }
    if (_appkitCoreReactNative.SendController.state.sendTokenAmount && _appkitCoreReactNative.SendController.state.token && _appkitCoreReactNative.SendController.state.sendTokenAmount > Number(_appkitCoreReactNative.SendController.state.token.quantity.numeric)) {
      return 'Insufficient funds';
    }
    if (!_appkitCoreReactNative.SendController.state.sendTokenAmount) {
      return 'Add amount';
    }
    if (_appkitCoreReactNative.SendController.state.sendTokenAmount && _appkitCoreReactNative.SendController.state.token?.price) {
      const value = _appkitCoreReactNative.SendController.state.sendTokenAmount * _appkitCoreReactNative.SendController.state.token.price;
      if (!value) {
        return 'Incorrect value';
      }
    }
    if (_appkitCoreReactNative.SendController.state.receiverAddress && !_appkitCoreReactNative.CoreHelperUtil.isAddress(_appkitCoreReactNative.SendController.state.receiverAddress)) {
      return 'Invalid address';
    }
    if (!_appkitCoreReactNative.SendController.state.receiverAddress) {
      return 'Add address';
    }
    return 'Preview send';
  };
  (0, _react.useEffect)(() => {
    if (!token) {
      _appkitCoreReactNative.SendController.setToken(tokenBalance?.[0]);
    }
    fetchNetworkPrice();
  }, [token, tokenBalance, fetchNetworkPrice]);
  const actionText = getActionText();
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    style: {
      paddingHorizontal: padding
    },
    bounces: false,
    keyboardShouldPersistTaps: "always"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: "l",
    alignItems: "center",
    justifyContent: "center",
    style: {
      paddingBottom
    }
  }, /*#__PURE__*/React.createElement(_w3mSendInputToken.SendInputToken, {
    token: token,
    sendTokenAmount: sendTokenAmount,
    gasPrice: Number(gasPrice),
    style: _styles.default.tokenInput,
    onTokenPress: () => _appkitCoreReactNative.RouterController.push('WalletSendSelectToken')
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    alignItems: "center",
    justifyContent: "center",
    style: _styles.default.addressContainer
  }, /*#__PURE__*/React.createElement(_w3mSendInputAddress.SendInputAddress, {
    value: receiverProfileName || receiverAddress
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.IconBox, {
    icon: "arrowBottom",
    size: "lg",
    iconColor: "fg-275",
    background: true,
    backgroundColor: "bg-175",
    border: true,
    borderColor: "bg-100",
    borderSize: 10,
    style: _styles.default.arrowIcon
  })), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    style: _styles.default.sendButton,
    onPress: onSendPress,
    disabled: !actionText.includes('Preview send'),
    loading: loading
  }, actionText)));
}
//# sourceMappingURL=index.js.map