"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.WalletSendPreviewView = WalletSendPreviewView;
var _valtio = require("valtio");
var _reactNative = require("react-native");
var _appkitUiReactNative = require("@reown/appkit-ui-react-native");
var _appkitCommonReactNative = require("@reown/appkit-common-react-native");
var _appkitCoreReactNative = require("@reown/appkit-core-react-native");
var _useCustomDimensions = require("../../hooks/useCustomDimensions");
var _previewSendPill = require("./components/preview-send-pill");
var _styles = _interopRequireDefault(require("./styles"));
var _previewSendDetails = require("./components/preview-send-details");
function _interopRequireDefault(e) { return e && e.__esModule ? e : { default: e }; }
function WalletSendPreviewView() {
  const {
    padding
  } = (0, _useCustomDimensions.useCustomDimensions)();
  const {
    caipNetwork
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.NetworkController.state);
  const {
    token,
    receiverAddress,
    receiverProfileName,
    receiverProfileImageUrl,
    gasPriceInUSD,
    loading
  } = (0, _valtio.useSnapshot)(_appkitCoreReactNative.SendController.state);
  const getSendValue = () => {
    if (_appkitCoreReactNative.SendController.state.token && _appkitCoreReactNative.SendController.state.sendTokenAmount) {
      const price = _appkitCoreReactNative.SendController.state.token.price;
      const totalValue = price * _appkitCoreReactNative.SendController.state.sendTokenAmount;
      return totalValue.toFixed(2);
    }
    return null;
  };
  const getTokenAmount = () => {
    const value = _appkitCoreReactNative.SendController.state.sendTokenAmount ? _appkitCommonReactNative.NumberUtil.roundNumber(_appkitCoreReactNative.SendController.state.sendTokenAmount, 6, 5) : 'unknown';
    return `${value} ${_appkitCoreReactNative.SendController.state.token?.symbol}`;
  };
  const formattedAddress = receiverProfileName ? _appkitUiReactNative.UiUtil.getTruncateString({
    string: receiverProfileName,
    charsStart: 20,
    charsEnd: 0,
    truncate: 'end'
  }) : _appkitUiReactNative.UiUtil.getTruncateString({
    string: receiverAddress || '',
    charsStart: 4,
    charsEnd: 4,
    truncate: 'middle'
  });
  const onSend = () => {
    _appkitCoreReactNative.SendController.sendToken();
  };
  const onCancel = () => {
    _appkitCoreReactNative.RouterController.goBack();
    _appkitCoreReactNative.SendController.setLoading(false);
  };
  return /*#__PURE__*/React.createElement(_reactNative.ScrollView, {
    style: {
      paddingHorizontal: padding
    }
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    padding: ['l', 'xl', '3xl', 'xl']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, null, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, "Send"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "paragraph-400",
    color: "fg-100"
  }, "$", getSendValue())), /*#__PURE__*/React.createElement(_previewSendPill.PreviewSendPill, {
    text: getTokenAmount()
  }, token?.iconUrl ? /*#__PURE__*/React.createElement(_appkitUiReactNative.Image, {
    source: token?.iconUrl,
    style: _styles.default.tokenLogo
  }) : /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "coinPlaceholder",
    height: 32,
    width: 32,
    style: _styles.default.tokenLogo,
    color: "fg-200"
  }))), /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "arrowBottom",
    height: 14,
    width: 14,
    color: "fg-200",
    style: _styles.default.arrow
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-150"
  }, "To"), /*#__PURE__*/React.createElement(_previewSendPill.PreviewSendPill, {
    text: formattedAddress
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Avatar, {
    address: receiverAddress,
    imageSrc: receiverProfileImageUrl,
    size: 32,
    borderWidth: 0,
    style: _styles.default.avatar
  }))), /*#__PURE__*/React.createElement(_previewSendDetails.PreviewSendDetails, {
    style: _styles.default.details,
    networkFee: gasPriceInUSD,
    address: receiverAddress,
    name: receiverProfileName,
    caipNetwork: caipNetwork
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center"
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Icon, {
    name: "warningCircle",
    size: "sm",
    color: "fg-200",
    style: _styles.default.reviewIcon
  }), /*#__PURE__*/React.createElement(_appkitUiReactNative.Text, {
    variant: "small-400",
    color: "fg-200"
  }, "Review transaction carefully")), /*#__PURE__*/React.createElement(_appkitUiReactNative.FlexView, {
    flexDirection: "row",
    margin: ['l', '0', '0', '0']
  }, /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    variant: "shade",
    style: _styles.default.cancelButton,
    onPress: onCancel
  }, "Cancel"), /*#__PURE__*/React.createElement(_appkitUiReactNative.Button, {
    variant: "fill",
    style: _styles.default.sendButton,
    onPress: onSend,
    loading: loading
  }, "Send"))));
}
//# sourceMappingURL=index.js.map